/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package org.obiz.sdtd.tool.rgwmap;

import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGException;
import com.kitfox.svg.SVGUniverse;

import javax.imageio.ImageIO;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

public class MapBuilder {

    private String path;
    private int downScale = 2; //2 - better definition
    private float gamma = 5;
    private final boolean DRAW_ICON_AXIS = false;

    private boolean applyGammaCorrection = true;
    private int mapSize;
    private int scaledSize;
    private long totalPixels;
    private BufferedImage iHeigths;
    private BufferedImage iBiomes;
    private BufferedImage iRad;
    private int waterLine;
    private boolean doBlureBiomes = true;
    private int bloorK = 256; //part of image size used as blure radius
    private Map<String, Path> icons;
    private Map<String, BufferedImage> iconsCache = new HashMap<>();

    //fixed object sized (autoscaled)
    int i10 = 10 / (downScale);
    int i1 = i10 / 10;
    int i2 = i1 * 2;
    int i5 = i10 / 2;
    int i15 = (i10 * 3) / 2;
    int i20 = 2 * i10;
    int i25 = (i10 * 5) / 2;
    int i30 = 3 * i10;
    int i35 = (7 * i10) / 2;
    int i40 = 4 * i10;
    int i45 = (9 * i10) / 2;
    int i50 = 5 * i10;
    int i60 = 6 * i10;
    int i70 = 7 * i10;
    int i7 = i70 / 70;
    int i80 = 8 * i10;
    int i160 = 16 * i10;
    int i200 = 20 * i10;
    int i250 = 25 * i10;
    int i500 = 50 * i10;

    int fileNum = 1;
    private BufferedImage iWaterZones;
    
    private long prevLogTime;

    public MapBuilder(String path) {
        this.path = path;
        prevLogTime = System.currentTimeMillis();
        try {
            icons = loadIcons();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //TODO command line options
        String path = ".";
        if(args.length==1) {
            path = args[0];
        }
        new MapBuilder(path).build();
    }

    private void build() {
        try {
            //testGetSprite("bank");
//            System.exit(0);
            readWorldHeights();
            readWatersPoint();
            autoAjustImage();
            applyHeightsToBiomes();
            drawRoads();
            drawPrefabs();
            log("All work done!\nResult map image: 9_mapWithObjects.png");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (SVGException e) {
            e.printStackTrace();
        }
    }

    private void testGetSprite(String iconName) {
        BufferedImage map = new BufferedImage(1024, 1024, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gMap = map.createGraphics();
        gMap.setColor(Color.GRAY);
        gMap.drawRect(0, 0, 1023, 1023);
        for (int i = 0; i < 1024; i+=32) {
            gMap.drawLine(0, i,1023, i);
            gMap.drawLine(i,0, i, 1023);
        }
        int iconSize = 64;
        int x = 512, y = 512;

        try {
            ImageIO.write(map, "PNG", new File("_tst_map.png"));

            drawIcon(gMap, iconName, iconSize, x, y, true);

            ImageIO.write(map, "PNG", new File("_tst_map2.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void drawIcon(Graphics2D gMap, String iconName, int targetSize, int x, int y, boolean showAxis) {
        BufferedImage sprite;
        sprite = iconsCache.get(iconName);
        if(sprite == null) {
            sprite = createSprite(iconName, targetSize, showAxis);
            iconsCache.put(iconName, sprite);
        }
        gMap.drawImage(sprite, x - targetSize *4, y - targetSize *4, null);
    }

    private BufferedImage createSprite(String name, int width, boolean showAxis) {
        BufferedImage img = new BufferedImage(width*8, width*8, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        try {
            Path path = icons.get(name);
            SVGUniverse svgUniverse = new SVGUniverse();

            URI uri = svgUniverse.loadSVG(Files.newInputStream(path), path.getFileName().toString());
            SVGDiagram diagram = svgUniverse.getDiagram(uri);
            diagram.setDeviceViewport(new Rectangle(width, width));
            Graphics graphics = g.create(0, 0, width*8, width*8);
            if(showAxis) {
                g.setColor(Color.ORANGE);
                g.drawRect(0, 0, width*8-1, width*8-1);
                g.setColor(Color.GREEN);
                g.drawLine(0, width*4, width*8-1, width*4);
                g.drawLine(width*4,0, width*4, width*8-1);
            }
            graphics.translate(width*4, width*4);
            diagram.render((Graphics2D) graphics);
//            log(diagram.getViewRect());
            svgUniverse.clear();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SVGException e) {
            e.printStackTrace();
        }
        return img;
    }

    private Map<String, Path> loadIcons() throws IOException, URISyntaxException {
        Map<String, Path> result = new HashMap<>();
        URI uri = getClass().getResource("/icons").toURI();
        Path myPath;
        if (uri.getScheme().equals("jar")) {
            FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
            myPath = fileSystem.getPath("/icons");
        } else {
            myPath = Paths.get(uri);
        }
        Stream<Path> walk = Files.walk(myPath, 1);

        walk.forEach(
                next -> {
                    String nextFile = next.getFileName().toString();
                    if(Files.isRegularFile(next) && nextFile.endsWith(".svg")) {
                        nextFile = nextFile.substring(0, nextFile.lastIndexOf("."));
                        result.put(nextFile, next);
                        log(nextFile + ": " + Files.isReadable(next));
                    }
                }
        );
        return result;
    }

    private void readWatersPoint() throws IOException, XMLStreamException {
        String prefabs = "\\water_info.xml";
        XMLInputFactory xmlif = XMLInputFactory.newInstance();
        XMLStreamReader xmlr = xmlif.createXMLStreamReader(prefabs, new FileInputStream(path + prefabs));

        iWaterZones = new BufferedImage(scaledSize, scaledSize, BufferedImage.TYPE_BYTE_BINARY);

        Graphics graphics = iWaterZones.getGraphics();

        int eventType;
        while (xmlr.hasNext()) {
            eventType = xmlr.next();
            if (eventType == XMLEvent.START_ELEMENT) {
                if (xmlr.getAttributeCount() == 5) {
                    String attributeValue = xmlr.getAttributeValue(0);
                    String[] split = attributeValue.split(",");
                    int x = (mapSize / 2 + Integer.parseInt(split[0].trim())) / downScale;
                    int y = (mapSize / 2 - Integer.parseInt(split[2].trim())) / downScale;

                    graphics.setColor(Color.WHITE);
                    graphics.fillRect((int) (x - i250 / downScale * 0.75), (int) (y - i250 / downScale * 1.25), i160, i200);

                }
            }
        }

        if (!checkFileExists("_waterZones")) {
            File waterZones = new File(path + "\\" + fileNum + "_waterZones.png");
            ImageIO.write(iWaterZones, "PNG", waterZones);
        }

    }

    private void drawPrefabs() throws IOException, XMLStreamException, SVGException {
        String prefabs = "\\prefabs.xml";
        XMLInputFactory xmlif = XMLInputFactory.newInstance();
        XMLStreamReader xmlr = xmlif.createXMLStreamReader(prefabs, new FileInputStream(path + prefabs));

        Graphics2D g = iBiomes.createGraphics();

        int eventType;

        //fixed buildings colors
        HashMap<String, Color> buildColors = new HashMap();
        buildColors.put("black", new Color(51, 49, 51));
        buildColors.put("red", new Color(139, 52, 48));
        buildColors.put("other", new Color(58, 50, 39));

        Set<String> prefabsGroups = icons.keySet();
        int prefabsSVGCounter = 0;
        int prefabsCounter = 0;
        System.out.print("Processing prefabs: ");

        Timer.startTimer("Draw prefabs");

        while (xmlr.hasNext()) {
            eventType = xmlr.next();
            if (eventType == XMLEvent.START_ELEMENT) {
                if (xmlr.getAttributeCount() == 4) {
                    String attributeValue = xmlr.getAttributeValue(2);
                    String[] split = attributeValue.split(",");
                    int x = (mapSize / 2 + Integer.parseInt(split[0])) / downScale;
                    int y = (mapSize / 2 - Integer.parseInt(split[2])) / downScale;

                    int rot = Integer.parseInt(xmlr.getAttributeValue(3));
                    int xShift = x + i15;
                    int yShift = y - i50;

                    String prefabName = xmlr.getAttributeValue(1);
                    String foundPrefabGroup = null;

                    for (String prefabsGroup : prefabsGroups) {
                        if(prefabName.contains(prefabsGroup)) {
                            foundPrefabGroup = prefabsGroup;
                            prefabsSVGCounter++;
                        }
                    }

                    prefabsCounter++;

                    if(foundPrefabGroup!=null) {
                        drawIcon(g, foundPrefabGroup, i40, x, yShift, DRAW_ICON_AXIS);
                        if (prefabsCounter % 200 == 0) System.out.print("\u25AF");
                    } else if (prefabName.contains("garage")) {
                        g.setColor(buildColors.get("black"));
                        g.fill3DRect(x + i5, y - i30, i20, i20, true);
                    } else if (prefabName.contains("trailer")) {
                        g.setColor(buildColors.get("black"));
                        g.fill3DRect(x + i5, yShift + i20, i10, i20, true);
                    } else if (prefabName.contains("fire")) {
                        g.setColor(Color.GRAY);
                        g.fillOval(x + i10, yShift - i10, i45, i45);
                        g.setColor(buildColors.get("red"));
                        g.drawOval(x + i10, yShift - i10, i45, i45);
                        g.fillArc(x + i20, yShift - i40, i30, i70, 225, 70);
                    } else {
                        g.setColor(buildColors.get("other"));
                        if (rot == 0 || rot == 2)
                            g.fill3DRect(x, yShift, i30, i25, true);
                        else
                            g.fill3DRect(x, yShift, i25, i30, true);
                    }
                }
            }
        }


        System.out.print( " | " + prefabsCounter + " prefabs added!\n");
        log( prefabsSVGCounter + " prefabs added as SVG.");
        Timer.stopTimer("Draw prefabs");

        File mapWithObjects = new File(path + "\\" + "9_mapWithObjects.png");
        ImageIO.write(iBiomes, "PNG", mapWithObjects);
    }

    private void drawRoads() throws IOException {
        BufferedImage roads = ImageIO.read(new File(path + "\\splat3.png"));
        log("Roads loaded.");
        Color roadColor;

        for (int xi = roads.getMinX(); xi < roads.getWidth(); xi++) {
            for (int yi = roads.getMinY(); yi < roads.getHeight(); yi++) {
                int p = roads.getRGB(xi, yi);
                if (p != 0) {
                    if (p == 65280)
                        roadColor = new Color(141, 129, 106);
                    else
                        roadColor = new Color(52, 59, 65);

                    iBiomes.setRGB(xi / downScale, yi / downScale, roadColor.getRGB());
                }
            }
        }

        fileNum++;
        if (!checkFileExists("_map_with_roads")) {
            File map_with_roads = new File(path + "\\" + fileNum + "_map_with_roads.png");
            ImageIO.write(iBiomes, "PNG", map_with_roads);
        }
    }

    private void applyHeightsToBiomes() throws IOException {
        long start, end;
        BufferedImage inputImage = ImageIO.read(new File(path + "\\biomes.png"));

        iBiomes = new BufferedImage(scaledSize, scaledSize, inputImage.getType());

        // scale the input biomes image to the output image size
        Graphics2D g2d = iBiomes.createGraphics();
        g2d.drawImage(inputImage, 0, 0, scaledSize, scaledSize, null);
        g2d.dispose();

        //free mem
        inputImage.flush();

        //fix Original RGB
        Map<Integer, Color> mapColor = new HashMap<>();
        mapColor.put(-16760832, new Color(55, 95, 68));//forest
        mapColor.put(-1, new Color(203, 197, 194));//snow
        mapColor.put(-7049, new Color(175, 154, 107));//desert
        mapColor.put(-22528, new Color(124, 116, 94));//wasteland
        mapColor.put(-4587265, new Color(68, 70, 67));//burned

        MapBiomeColor:
        for (int x = 0; x < scaledSize; x++) {
            for (int y = 0; y < scaledSize; y++) {
                int rgb = iBiomes.getRGB(x, y);
                if (mapColor.containsKey(rgb))
                    iBiomes.setRGB(x, y, mapColor.get(rgb).getRGB());
                else {
                    System.err.println("Unknown biome color: " + rgb);
                    break MapBiomeColor;
                }
            }
        }

        //mark radiation zones
        drawRadiation();

        if (doBlureBiomes) {
            BufferedImage iBiomesBlured = new BufferedImage(scaledSize, scaledSize, inputImage.getType());
            new BoxBlurFilter(scaledSize / bloorK, scaledSize / bloorK, 1).filter(iBiomes, iBiomesBlured);
            iBiomes.flush();
            iBiomes = iBiomesBlured;
        }

        //Draw lakes
        WritableRaster iHeigthsRaster = iHeigths.getRaster();
        for (int x = 0; x < scaledSize; x++) {
            for (int y = 0; y < scaledSize; y++) {
                if (iHeigthsRaster.getSample(x, y, 0) < waterLine
                        && iWaterZones.getRaster().getSample(x, y, 0) > 0) {
                    iBiomes.setRGB(x, y, new Color(49, 87, 145).getRGB());
                }
            }
        }

        start = System.nanoTime();
        fileNum++;
        if (!checkFileExists("_bump")) {
            //write heights image to file
            File bump = new File(path + "\\" + fileNum + "_bump.png");
            ImageIO.write(iHeigths, "PNG", bump);
        }
        fileNum++;
        if (!checkFileExists("_biomes")) {
            //write scaled biomes to file
            File biomes = new File(path + "\\" + fileNum + "_biomes.png");
            ImageIO.write(iBiomes, "PNG", biomes);
        }
        end = System.nanoTime();
        log("File saving time:  = " + (end - start) / 1000000000 + "s");

        // normal vectors array
        float[][] normalVectors = new float[scaledSize * scaledSize][3];
        // precalculate normal vectors
        BumpMappingUtils.FindNormalVectors(iHeigths, normalVectors);
        //free mem
        iHeigths.flush();
        //apply bump-mapping using normal vectors
        BumpMappingUtils.paint(iBiomes, scaledSize, scaledSize, normalVectors);
        fileNum++;
        if (!checkFileExists("_biomesShadow")) {
            //Write bump-mapped biomes
            File biomesShadow = new File(path + "\\" + fileNum + "_biomesShadow.png");
            ImageIO.write(iBiomes, "PNG", biomesShadow);
        }
    }

    private void autoAjustImage() throws IOException {
        WritableRaster raster = iHeigths.getRaster();
        // initialisation of image histogram array
        long hist[] = new long[256];
        for (int i = 0; i < hist.length; i++) {
            hist[i] = 0;
        }
        //time measurement vars
        long start, end;
        //init other stats
        int min = raster.getSample(raster.getMinX(), raster.getMinY(), 0);
        int max = min;
        long rms = 0;
        double mean = 0;
        int tcount = 0;

        start = System.nanoTime();
        for (int x = raster.getMinX(); x < raster.getMinX() + raster.getWidth(); x++) {
            for (int y = raster.getMinY(); y < raster.getMinY() + raster.getHeight(); y++) {

                //get integer height value from a current pixel
                int color = raster.getSample(x, y, 0);

                //find min and max heights
                if (color < min) {
                    min = color;
                } else if (color > max) {
                    max = color;
                }

                //build histogram
                hist[color / 256]++;

                //calulate MEAN
                mean += color * 1. / totalPixels;
                long lColor = (long) color;
                lColor *= lColor;
                lColor /= totalPixels;
                rms += lColor;

                //just check pixels count
                tcount++;
            }
        }
        assert tcount == totalPixels;
        end = System.nanoTime();
        long t1 = end - start;
        log("Time to solve stats: " + t1 / 1000000 + "ms");
//        log("tcount = " + tcount);

        rms = Math.round(Math.sqrt(rms));
        int intrms = Math.toIntExact(rms);

        log("mean = " + Math.round(mean));
        log("rms = " + rms);
        log("min = " + min);
        log("max = " + max);

        StringBuilder sb = new StringBuilder();
        float D = 0;
        for (int i = 0; i < hist.length; i++) {
            sb.append(i * 256 + "\t").append(hist[i]).append('\n');
            long a = i * 256 - rms;
            double tmp = Math.pow(a, 2);
            tmp /= tcount;
            tmp *= hist[i];
            D += tmp;
        }

        Files.write(Paths.get(path + "\\heigthsHistogram.txt"), Collections.singleton(sb));

        D = Math.round(Math.sqrt(D));
        log("D2 = " + D);

        int startHist = Math.round(intrms - gamma * D);
        log("startHist = " + startHist);
        float k = 256 * 256 / (max - min);
        log("k = " + k);

        waterLine = intrms - Math.round(1.7f * D);
        log("waterLine = " + waterLine);
        if (applyGammaCorrection) {
            waterLine = Math.round((waterLine - min) * k);
            log("after gamma waterLine = " + waterLine);
            for (int x = raster.getMinX(); x < raster.getMinX() + raster.getWidth(); x++) {
                for (int y = raster.getMinY(); y < raster.getMinY() + raster.getHeight(); y++) {
                    int grayColor = raster.getSample(x, y, 0);
                    int imageColor = Math.round((grayColor - min) * k);
                    raster.setSample(x, y, 0, imageColor);
                }
            }
        }
    }

    public void readWorldHeights() throws IOException {
        String dtmFileName = path + "\\dtm.raw";
        File heightsFile = new File(dtmFileName);
        if (!heightsFile.exists() || !heightsFile.isFile() || !heightsFile.canRead()) {
            System.err.println("File not found: " + dtmFileName);
            System.exit(1);
        }
        long fileLength = heightsFile.length();
        log("fileLength = " + fileLength);
        mapSize = (int) Math.round(Math.sqrt(fileLength / 2.));
        log("Detected mapSize: " + mapSize);
        scaledSize = mapSize / downScale;
        log("Resulting image side size will be: " + scaledSize + "px");
        //TODO rename to totalScaledPixels
        totalPixels = scaledSize;
        totalPixels *= totalPixels;
        //Result processed heights image
        iHeigths = new BufferedImage(scaledSize, scaledSize, BufferedImage.TYPE_USHORT_GRAY);
        WritableRaster raster = iHeigths.getRaster();

        try (FileInputStream hmis = new FileInputStream(heightsFile)) {
            byte buf[] = new byte[mapSize * 4];

            int readedBytes;
            int curPixelNum = 0;
            System.out.print("File load:\n|----------------|\n|");
            while ((readedBytes = hmis.read(buf)) > -1) {
                //TODO here potential problem if readedBytes%2 != 0
                //convert every 2 bytes to new gray pixel
                for (int i = 0; i < readedBytes / 2; i++) {
                    //TODO use avg of pixel color with same coordinate in scaled image.
                    //calculate pixel position
                    int x = (curPixelNum % mapSize) / downScale;
                    int y = (mapSize - 1 - curPixelNum / mapSize) / downScale;
                    //write pixel to resulting image
                    int grayColor = (buf[i * 2 + 1] << 8) | (((int) buf[i * 2]) & 0xff);
                    raster.setSample(x, y, 0, grayColor);
                    curPixelNum++;
                    //Draw progress bar
                    if (curPixelNum % (mapSize * 512) == 0) {
                        System.out.print("-");
                    }
                }
            }
            log("|\nDone.");
        }
    }

    private void drawRadiation() throws IOException {
        int newR;
        int oldR, oldG, oldB;

        BufferedImage inputImage = ImageIO.read(new File(path + "\\radiation.png"));
        log("Beware of radiation!");

        iRad = new BufferedImage(scaledSize, scaledSize, inputImage.getType());

        // scale the input radiation zone image to the output image size
        Graphics2D g2d = iRad.createGraphics();
        g2d.drawImage(inputImage, 0, 0, scaledSize, scaledSize, null);
        g2d.dispose();

        //free mem
        inputImage.flush();

        for (int x = 0; x < scaledSize; x++) {
            for (int y = 0; y < scaledSize; y++) {
                int rgb = iBiomes.getRGB(x, y);
                int rgbRad = iRad.getRGB(x, y);
                if (rgbRad == -65536) {
                    oldR = rgb >> 16 & 0xff;
                    oldG = rgb >> 8 & 0xff;
                    oldB = rgb & 0xff;

                    newR = (int) (oldR * 1.5);
                    if (newR > 255) newR = 255;
                    if (newR < 0) newR = 0;

                    iBiomes.setRGB(x, y, new Color(newR, oldG, oldB).getRGB());
                }
            }
        }

    }

    public boolean checkFileExists(String fileName) {
        String filePath = path + "\\" + fileNum + fileName + ".png";
        File f = new File(filePath);
        if (!f.exists() || !f.isFile() || !f.canRead()) {
            return false;
        } else {
            log("File already exists: " + fileNum + fileName + ".png");
        }

        return true;
    }

    private void log(String message) {
        long now = System.currentTimeMillis();
        System.out.println("[+" + (now-prevLogTime)/1000f + "s]: " + message);
        prevLogTime = now;
    }

}
