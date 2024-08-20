package org.obiz.sdtd.tool.rgwmap.parts;

import org.obiz.sdtd.tool.rgwmap.ImageMath;
import org.obiz.sdtd.tool.rgwmap.MapBuilder;

import javax.imageio.ImageIO;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

import static org.obiz.sdtd.tool.rgwmap.MapBuilder.*;
import static org.obiz.sdtd.tool.rgwmap.parts.Biomes.WATER_MAIN_COLOR;

public class Water {
    public static final String HEIGTHS_HISTOGRAM_TXT = "\\heigthsHistogram.txt";
    public static final String WATER_INFO_XML = "\\water_info.xml";
    public static final String SPLAT_4_PNG = "\\splat4.png";
    private final int scaledSize;
    private final int mapSize;
    private final int downScale;
    private final long totalPixels;
    private BufferedImage iWaterZones;
    private World world;
    private int waterLine;
    private BufferedImage iHeigths;

    public Water(World world) {
        this.world = world;
        scaledSize = world.getScaledSize();
        mapSize = world.getMapSize();
        downScale = world.getDownScale();
        totalPixels = world.getTotalPixels();
    }

    public void readWatersPoint() throws IOException, XMLStreamException {
        log("Load WaterZones.");

        if(Files.exists(Paths.get(WATER_INFO_XML))) {
            XMLInputFactory xmlif = XMLInputFactory.newInstance();
            XMLStreamReader xmlr = xmlif.createXMLStreamReader(WATER_INFO_XML, new FileInputStream(world.getPath() + WATER_INFO_XML));

            int watersPointsCounter = 0;

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
                        waterLine = Integer.parseInt(split[1].trim());
                        int y = (mapSize / 2 - Integer.parseInt(split[2].trim())) / downScale;
                        int z = Integer.parseInt(split[2].trim());
//                    int m

                        graphics.setColor(Color.WHITE);
                        graphics.fillRect((int) (x - i250 / downScale * 0.75), (int) (y - i250 / downScale * 1.25), i160, i200);
                        watersPointsCounter++;
                    }
                }
            }

            log(watersPointsCounter + " water sources loaded.");
            MapBuilder.writeToFile(world.getPath(), "_waterZones", iWaterZones);
        } else {
            log("No water zones found!");
        }
    }

    public void autoAjustImage(boolean applyGammaCorrection, BufferedImage iHeigths) throws IOException {
        this.iHeigths = iHeigths;
        int maxHeigth = 65545;
        log("Start autoAjustImage");
        WritableRaster raster =  iHeigths.getRaster();
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
        //TODO multithread
        for (int x = 0; x < scaledSize; x++) {
            for (int y = 0; y < scaledSize; y++) {

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

        rms = Math.round(Math.sqrt(rms));
        int intrms = Math.toIntExact(rms);
        int wLine = maxHeigth/256 * waterLine;

        //log("mean = " + Math.round(mean));
        //log("rms = " + rms);
        //log("min = " + min);
        //log("max = " + max);

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

        Files.write(Paths.get(world.getPath() + HEIGTHS_HISTOGRAM_TXT), Collections.singleton(sb));

        D = Math.round(Math.sqrt(D));
        float k = 256 * 256 / (float)(max - min);
        log("k = " + k);

        waterLine = wLine; //intrms - Math.round(0.5f * D);
        log("waterLine = " + waterLine);
        if (applyGammaCorrection) {
            waterLine = Math.round((waterLine - min) * k);
            log("after gamma waterLine = " + waterLine);
            log("Start apply gamma correction.");
            //TODO multithread
            for (int x = raster.getMinX(); x < raster.getMinX() + raster.getWidth(); x++) {
                for (int y = raster.getMinY(); y < raster.getMinY() + raster.getHeight(); y++) {
                    int grayColor = raster.getSample(x, y, 0);
                    int imageColor = Math.round((grayColor - min) * k);
                    raster.setSample(x, y, 0, imageColor);
                }
            }
            log("End apply gamma correction.");
        }
    }

    public void drawWater(BufferedImage iBiomes) throws IOException {
        log("Load water file");
        BufferedImage water = ImageIO.read(new File(world.getPath() + SPLAT_4_PNG));
        log("Water loaded. Start drawing.");

        DataBuffer buffer = iBiomes.getRaster().getDataBuffer();
        DataBuffer alfaBuffer = water.getAlphaRaster().getDataBuffer();

        System.out.println("TEST : " + (alfaBuffer.getSize()/4-mapSize*mapSize));

        //TODO multithread

        for (int i = 0; i < scaledSize; i++) {
            for (int j = 0; j < scaledSize; j++) {
                for (int k = 0; k < 4; k++) {
                    int c = alfaBuffer.getElem(ImageMath.xy2i(water,i*downScale, j*downScale, k));
                    if(c!=0) {
                        buffer.setElem(ImageMath.xy2i(iBiomes, i, j), ImageMath.getPureIntFromRGB(WATER_MAIN_COLOR));
                    }
                }
            }
        }
        writeToFile(world.getPath(), "_map_with_water", iBiomes);
        log("Finish water drawing.");
    }


    public int getWaterLine() {
        return waterLine;
    }

    public void setWaterLine(int waterLine) {
        this.waterLine = waterLine;
    }

    public BufferedImage getiWaterZones() {
        return iWaterZones;
    }

    public boolean pointIsInWater(int x, int y) {
        return
                iHeigths.getRaster().getSample(x, y, 0) < waterLine
                && iWaterZones.getRaster().getSample(x, y, 0) > 0;
    }
}
