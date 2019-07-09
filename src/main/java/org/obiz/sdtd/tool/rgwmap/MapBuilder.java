/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package org.obiz.sdtd.tool.rgwmap;

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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class MapBuilder {

    private String path = ".";
    private int downScale = 4; //2 - better definition
    private float gamma = 5;
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
    int i500 = 50 * i10;

    int fileNum = 1;
    private BufferedImage iWaterZones;

    public static void main(String[] args) {
        //TODO command line options
        new MapBuilder().build();
    }

    private void build() {
        try {
            readWorldHeights();
            readWatersPoint();
            autoAjustImage();
            applyHeightsToBiomes();
            drawRoads();
            drawPrefabs();
            System.out.println("All work done!\nResulting map image: 9_mapWithObjects.png");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
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
                if(xmlr.getAttributeCount()==5) {
                    String attributeValue = xmlr.getAttributeValue(0);
                    String[] split = attributeValue.split(",");
                    int x = (mapSize/2 + Integer.parseInt(split[0].trim()))/downScale;
                    int y = (mapSize/2 - Integer.parseInt(split[2].trim()))/downScale;

                    graphics.setColor(Color.WHITE);
                    graphics.fillOval(x - i500 /downScale, y - i500 /downScale, i500, i500);

                }
            }
        }

        if (!checkFileExists("_waterZones")) {
            File waterZones = new File(path + "\\" + fileNum + "_waterZones.png");
            ImageIO.write(iWaterZones, "PNG", waterZones);
        }

    }

    private void drawPrefabs() throws IOException, XMLStreamException {
        String prefabs = "\\prefabs.xml";
        XMLInputFactory xmlif = XMLInputFactory.newInstance();
        XMLStreamReader xmlr = xmlif.createXMLStreamReader(prefabs,new FileInputStream(path + prefabs));

        Graphics g = iBiomes.getGraphics();

        int eventType;

        //fixed buildings colors
        HashMap<String, Color> buildColors = new HashMap();
        buildColors.put("cabin", new Color(77, 72, 59));
        buildColors.put("apartment", new Color(80, 81, 75));
        buildColors.put("house", new Color(90, 92, 91));
        buildColors.put("field", new Color(79, 109, 77));
        buildColors.put("army", new Color(82, 83, 50));
        buildColors.put("gas", new Color(134, 78, 74));
        buildColors.put("garage", new Color(51, 49, 51));
        buildColors.put("site", new Color(61, 71, 55));
        buildColors.put("trader", new Color(180, 108, 5));
        buildColors.put("sky", new Color(76, 121, 126));
        buildColors.put("hotel", new Color(83, 47, 61));
        buildColors.put("pharmacy", new Color(33, 126, 46));
        buildColors.put("red", new Color(181, 48, 42));
        buildColors.put("gun", new Color(175, 147, 49));
        buildColors.put("yellow", new Color(183, 183, 0));
        buildColors.put("black", new Color(33, 33, 33));
        buildColors.put("water", new Color(22, 116, 168));
        buildColors.put("other", new Color(69, 72, 72));
        //red_mesa

        while (xmlr.hasNext()) {
            eventType = xmlr.next();
            if (eventType == XMLEvent.START_ELEMENT) {
                if (xmlr.getAttributeCount() == 4) {
                    String attributeValue = xmlr.getAttributeValue(2);
                    String[] split = attributeValue.split(",");
                    int x = (mapSize / 2 + Integer.parseInt(split[0])) / downScale;
                    int y = (mapSize / 2 - Integer.parseInt(split[2])) / downScale;

                    int rot = Integer.parseInt(xmlr.getAttributeValue(3));

                    //int rgb = Color.RED.getRGB();
                    // iBiomes.setRGB(x, y, rgb);
                    int xShift = x + i15;
                    int yShift = y - i50;


                    if (xmlr.getAttributeValue(1).startsWith("cave")) {
                        g.setColor(new Color(180, 151, 0));
                        g.fillArc(xShift, yShift, i40, i70, 0, 180);
                        g.setColor(Color.DARK_GRAY);
                        g.fillArc(xShift + i10, yShift + i10, i20, i50, 0, 180);
                    } else if (xmlr.getAttributeValue(1).startsWith("water")) {
                        g.setColor(Color.LIGHT_GRAY);
                        g.fillOval(x, yShift, i30, i30);
                        g.setColor(buildColors.get("water"));
                        g.drawOval(x, yShift, i30, i30);
                        g.fillArc(x, yShift - i5, i30, i30, 225, 90);
                        g.fillOval(x + i5, yShift + i15, i20, i20);
                    } else if (xmlr.getAttributeValue(1).contains("electric")) {
                        g.setColor(Color.DARK_GRAY);
                        g.fillOval(x + i10, yShift - i10, i35, i35);
                        g.setColor(buildColors.get("yellow"));
                        g.drawOval(x + i10, yShift - i10, i35, i35);
                        g.fillArc(x + i20, yShift - i40, i30, i60, 180, 80);
                    } else if (xmlr.getAttributeValue(1).contains("church")) {
                        g.setColor(buildColors.get("black"));
                        g.fillRect(x, yShift + i10, i30, i10);
                        g.fillRect(x + i10, yShift, i10, i40);
                    } else if (xmlr.getAttributeValue(1).contains("cemetery")) {
                        g.setColor(buildColors.get("black"));
                        g.fillArc(x, yShift, i25, i30, 0, 180);
                        g.fillRect(x, yShift + i15, i25, i20);
                    } else if (xmlr.getAttributeValue(1).contains("snowy_ski_lodge")) {
                        g.setColor(Color.LIGHT_GRAY);
                        g.fillOval(x + i15, y - i30, i45, i45);
                        g.setColor(buildColors.get("black"));
                        g.drawOval(x + i15, y - i30, i45, i45);
                        g.fillOval(x + i20, y - i20, i30, i20);
                        g.fillRect(x + i30, y - i10, i15, i20);
                        g.setColor(Color.LIGHT_GRAY);
                        g.fillOval(x + i25, y - i15, i10, i10);
                        g.fillOval(x + i35, y - i15, i10, i10);
                    } else if (xmlr.getAttributeValue(1).contains("bombshelter")) {
                        g.setColor(buildColors.get("black"));
                        g.fillOval(x + i20, y - i20, i30, i20);
                        g.fillRect(x + i20, y - i10, i35, i10);
                        g.setColor(Color.GRAY);
                        g.fillRect(x + i25, y - i10, i25, i5);
                    } else if (xmlr.getAttributeValue(1).contains("house")) {
                        g.setColor(buildColors.get("house"));
                        if (rot == 0 || rot == 2)
                            g.fill3DRect(x, yShift + i10, i35, i30, true);
                        else
                            g.fill3DRect(x, yShift + i10, i30, i35, true);
                    } else if (xmlr.getAttributeValue(1).contains("business")) {
                        g.setColor(buildColors.get("sky"));
                        if (rot == 0 || rot == 2)
                            g.fill3DRect(x, yShift + i10, i25, i30, true);
                        else
                            g.fill3DRect(x, yShift + i10, i30, i25, true);
                    } else if (xmlr.getAttributeValue(1).contains("hotel")) {
                        g.setColor(buildColors.get("hotel"));
                        if (rot == 0 || rot == 2)
                            g.fill3DRect(x + i5, y - i50, i30, i25, true);
                        else
                            g.fill3DRect(x + i5, y - i50, i25, i30, true);
                    } else if (xmlr.getAttributeValue(1).contains("sky")) {
                        g.setColor(buildColors.get("sky"));
                        g.fill3DRect(x, yShift - i10, i35, i50, true);
                    } else if (xmlr.getAttributeValue(1).contains("army")) {
                        g.setColor(buildColors.get("army"));
                        g.fill3DRect(xShift, yShift + i10, i30, i30, true);
                    } else if (xmlr.getAttributeValue(1).contains("bank")) {
                        g.setColor(Color.DARK_GRAY);
                        g.fillOval(x + i2, yShift + i5, i35, i35);
                        g.setColor(buildColors.get("yellow"));
                        g.drawOval(x + i2, yShift + i5, i35, i35);
                        g.fillArc(x, yShift - i15, i40, i40, 225, 90);
                        g.fillArc(x, yShift + i15, i40, i40, 45, 90);
                    } else if (xmlr.getAttributeValue(1).contains("red_mesa")) {
                        g.setColor(buildColors.get("red"));
                        g.fillArc(x, yShift - i15, i45, i45, 225, 90);
                        g.fillArc(x, yShift + i15, i45, i45, 45, 90);
                        g.setColor(Color.DARK_GRAY);
                        g.drawArc(x, yShift - i15, i45, i45, 225, 90);
                        g.drawArc(x, yShift + i15, i45, i45, 45, 90);
                        g.drawArc(x + i15, yShift, i45, i45, 135, 90);
                        g.drawArc(x - i15, yShift, i45, i45, 315, 90);
                    } else if (xmlr.getAttributeValue(1).contains("cabin")) {
                        g.setColor(buildColors.get("cabin"));
                        g.fill3DRect(xShift, yShift + i10, i30, i30, true);
                    } else if (xmlr.getAttributeValue(1).contains("pharmacy")) {
                        g.setColor(Color.LIGHT_GRAY);
                        g.fillOval(x + i10, yShift - i10, i45, i45);
                        g.setColor(buildColors.get("pharmacy"));
                        g.drawOval(x + i10, yShift - i10, i45, i45);
                        g.fillRect(x + i20, yShift + i10, i30, i10);
                        g.fillRect(x + i30, yShift, i10, i30);
                    } else if (xmlr.getAttributeValue(1).contains("post_office")) {
                        g.setColor(Color.DARK_GRAY);
                        g.drawRect(x + i5, y, i35, i25);
                        g.drawLine(x + i5, y, x + i25, y + i10);
                        g.drawLine(x + i40, y, x + i20, y + i10);
                        g.drawLine(x + i5, y + i25, x + i10, y + i10);
                        g.drawLine(x + i40, y + i25, x + i35, y + i10);
                    } else if (xmlr.getAttributeValue(1).contains("gun")) {
                        g.setColor(Color.DARK_GRAY);
                        g.fillOval(x + i10, yShift - i10, i45, i45);
                        g.setColor(buildColors.get("gun"));
                        g.drawOval(x + i10, yShift - i10, i45, i45);
                        g.fillRect(x + i20, yShift, i10, i30);
                        g.fillRect(x + i40, yShift, i10, i30);
                    } else if (xmlr.getAttributeValue(1).contains("hospital")) {
                        g.setColor(Color.LIGHT_GRAY);
                        g.fillOval(x + i10, yShift - i10, i45, i45);
                        g.setColor(buildColors.get("red"));
                        g.drawOval(x + i10, yShift - i10, i45, i45);
                        g.fillRect(x + i20, yShift + i10, i30, i10);
                        g.fillRect(x + i30, yShift, i10, i30);
                    } else if (xmlr.getAttributeValue(1).contains("garage")) {
                        g.setColor(buildColors.get("garage"));
                        g.fill3DRect(x + i5, y - i30, i20, i20, true);
                    } else if (xmlr.getAttributeValue(1).contains("parking")) {
                        g.setColor(buildColors.get("garage"));
                        g.fill3DRect(x + i5, yShift + i20, i40, i40, true);
                    } else if (xmlr.getAttributeValue(1).contains("trailer")) {
                        g.setColor(buildColors.get("garage"));
                        g.fill3DRect(x + i5, yShift + i20, i10, i20, true);
                    } else if (xmlr.getAttributeValue(1).contains("apartment")) {
                        g.setColor(buildColors.get("apartment"));
                        if (rot == 0 || rot == 2)
                            g.fill3DRect(x + i5, yShift + i10, i40, i30, true);
                        else
                            g.fill3DRect(x + i5, yShift + i10, i30, i40, true);
                    } else if (xmlr.getAttributeValue(1).contains("gas")) {
                        g.setColor(buildColors.get("gas"));
                        if (rot == 0 || rot == 2)
                            g.fill3DRect(xShift, yShift + i10, i30, i25, true);
                        else
                            g.fill3DRect(xShift, yShift + i10, i25, i30, true);
                    } else if (xmlr.getAttributeValue(1).contains("fire")) {
                        g.setColor(Color.lightGray);
                        g.fillOval(x + i10, yShift - i10, i45, i45);
                        g.setColor(buildColors.get("red"));
                        g.drawOval(x + i10, yShift - i10, i45, i45);
                        g.fillArc(x + i20, yShift - i40, i30, i70, 225, 70);
                    } else if (xmlr.getAttributeValue(1).contains("field")) {
                        g.setColor(buildColors.get("field"));
                        g.fillRect(x, yShift, i25, i25);
                    } else if (xmlr.getAttributeValue(1).contains("site")) {
                        g.setColor(Color.DARK_GRAY);
                        g.fillOval(xShift - i5, yShift + i10, i35, i35);
                        g.setColor(buildColors.get("site"));
                        g.fillOval(xShift, yShift + i15, i30, i30);
                    } else if (xmlr.getAttributeValue(1).contains("trader")) {
                        g.setColor(Color.DARK_GRAY);
                        g.fillOval(x + i5, yShift - i5, i45, i45);
                        g.setColor(buildColors.get("trader"));
                        g.drawOval(x + i5, yShift - i5, i45, i45);
                        g.fillArc(x + i10, yShift - i10, i40, i40, 225, 90);
                        g.fillArc(x + i20, yShift + i5, i20, i20, 45, 90);
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

        File mapWithObjects = new File(path + "\\"+ "9_mapWithObjects.png");
        ImageIO.write(iBiomes, "PNG", mapWithObjects);
    }

    private void drawRoads() throws IOException {
        BufferedImage roads = ImageIO.read(new File(path + "\\splat3.png"));
        System.out.println("Roads loaded");
        Color roadColor;

        for(int xi = roads.getMinX(); xi < roads.getWidth() ; xi ++) {
            for(int yi = roads.getMinY(); yi < roads.getHeight() ; yi ++) {
                int p = roads.getRGB(xi, yi);
                if(p!=0) {
                    if (p==65280)
                        roadColor = new Color(141, 129, 106);
                    else
                        roadColor = new Color(52, 59, 65);

                    iBiomes.setRGB(xi/downScale, yi/downScale, roadColor.getRGB());
                }
            }
        }

        fileNum++;
        if (!checkFileExists("_map_with_roads")) {
            File map_with_roads = new File(path + "\\"+ fileNum + "_map_with_roads.png");
            ImageIO.write(iBiomes, "PNG", map_with_roads);
        }
    }

    private void applyHeightsToBiomes() throws IOException {
        long start, end;
        BufferedImage inputImage = ImageIO.read(new File(path + "\\biomes.png"));

        iBiomes = new BufferedImage(scaledSize,scaledSize,inputImage.getType());

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

        if(doBlureBiomes) {
            BufferedImage iBiomesBlured = new BufferedImage(scaledSize, scaledSize, inputImage.getType());
            new BoxBlurFilter(scaledSize / bloorK, scaledSize / bloorK, 1).filter(iBiomes, iBiomesBlured);
            iBiomes.flush();
            iBiomes = iBiomesBlured;
        }

        //Draw lakes
        WritableRaster iHeigthsRaster = iHeigths.getRaster();
        for (int x = 0; x < scaledSize; x++) {
            for (int y = 0; y < scaledSize; y++) {
                if(iHeigthsRaster.getSample(x, y, 0)<waterLine
                            && iWaterZones.getRaster().getSample(x, y, 0) > 0) {
                    iBiomes.setRGB(x, y, new Color(49, 87, 145).getRGB());
                }
            }
        }

        start = System.nanoTime();
        fileNum++;
        if (!checkFileExists("_bump")) {
            //write heights image to file
            File bump = new File(path + "\\"+ fileNum + "_bump.png");
            ImageIO.write(iHeigths, "PNG", bump);
        }
        fileNum++;
        if (!checkFileExists("_biomes")) {
            //write scaled biomes to file
            File biomes = new File(path + "\\"+ fileNum + "_biomes.png");
            ImageIO.write(iBiomes, "PNG", biomes);
        }
        end = System.nanoTime();
        System.out.println("File saving time:  = " + (end-start)/1000000000 + "s");

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
            File biomesShadow = new File(path + "\\"+ fileNum + "_biomesShadow.png");
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
        for (int x = raster.getMinX(); x < raster.getMinX()+raster.getWidth(); x++) {
            for (int y = raster.getMinY(); y < raster.getMinY()+raster.getHeight(); y++) {

                //get integer height value from a current pixel
                int color = raster.getSample(x, y, 0);

                //find min and max heights
                if(color < min) {
                    min = color;
                } else if (color > max) {
                    max = color;
                }

                //build histogram
                hist[color/256]++;

                //calulate MEAN
                mean += color*1./totalPixels;
                long lColor = (long)color;
                lColor*=lColor;
                lColor/=totalPixels;
                rms+= lColor;

                //just check pixels count
                tcount++;
            }
        }
        assert tcount==totalPixels;
        end = System.nanoTime();
        long t1 = end - start;
        System.out.println("Time to solve stats: " + t1/1000000 + "ms");
//        System.out.println("tcount = " + tcount);

        rms = Math.round(Math.sqrt(rms));
        int intrms = Math.toIntExact(rms);

        System.out.println("mean = " + Math.round(mean));
        System.out.println("rms = " + rms);
        System.out.println("min = " + min);
        System.out.println("max = " + max);

        StringBuilder sb = new StringBuilder();
        float D = 0;
        for (int i = 0; i < hist.length; i++) {
            sb.append(i*256+"\t").append(hist[i]).append('\n');
            long a = i * 256 - rms;
            double tmp = Math.pow(a, 2);
            tmp/=tcount;
            tmp*=hist[i];
            D += tmp;
        }

        Files.write(Paths.get(path+"\\heigthsHistogram.txt"), Collections.singleton(sb));

        D = Math.round(Math.sqrt(D));
        System.out.println("D2 = " + D);

        int startHist = Math.round(intrms - gamma*D);
        System.out.println("startHist = " + startHist);
        float k = 256*256/(max - min);
        System.out.println("k = " + k);

        waterLine = intrms - Math.round(1.7f*D);
        System.out.println("waterLine = " + waterLine);
        if(applyGammaCorrection) {
            waterLine = Math.round((waterLine - min) * k);
            System.out.println("after gamma waterLine = " + waterLine);
            for (int x = raster.getMinX(); x < raster.getMinX()+raster.getWidth(); x++) {
                for (int y = raster.getMinY(); y < raster.getMinY()+raster.getHeight(); y++) {
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
        if(!heightsFile.exists() || !heightsFile.isFile() || !heightsFile.canRead()) {
            System.err.println("File not found: " + dtmFileName);
            System.exit(1);
        }
        long fileLength = heightsFile.length();
        System.out.println("fileLength = " + fileLength);
        mapSize = (int) Math.round(Math.sqrt(fileLength /2.));
        System.out.println("Detected mapSize: " + mapSize);
        scaledSize = mapSize / downScale;
        System.out.println("Resulting image side size will be: " + scaledSize + "px");
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
                    int grayColor = (buf[i*2+1]<<8)|(((int)buf[i*2])&0xff);
                    raster.setSample(x, y, 0, grayColor);
                    curPixelNum++;
                    //Draw progress bar
                    if (curPixelNum % (mapSize * 512) == 0) {
                        System.out.print("-");
                    }
                }
            }
            System.out.println("|\nDone.");
        }
    }

    private void drawRadiation() throws IOException {
        int newR;
        int oldR, oldG, oldB;

        BufferedImage inputImage = ImageIO.read(new File(path + "\\radiation.png"));
        System.out.println("Radiation png loaded!");

        iRad = new BufferedImage(scaledSize,scaledSize,inputImage.getType());

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
                    if (newR<0) newR = 0;

                    iBiomes.setRGB(x, y, new Color(newR, oldG, oldB).getRGB());
                }
            }
        }

    }

    public boolean checkFileExists(String fileName) {
        String filePath = path + "\\" + fileNum + fileName + ".png";
        File f = new File(filePath);
        if(!f.exists() || !f.isFile() || !f.canRead()) {
            return false;
        } else {
            System.out.println("File already exists: " + fileNum + fileName + ".png");
        }

        return true;
    }
}
