package org.obiz.sdtd.tool.rgwmap.parts;

import org.obiz.sdtd.tool.rgwmap.BoxBlurFilter;
import org.obiz.sdtd.tool.rgwmap.BumpMappingUtils;
import org.obiz.sdtd.tool.rgwmap.ImageMath;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import static org.obiz.sdtd.tool.rgwmap.MapBuilder.*;

public class Biomes {
    public static final String BIOMES_PNG = "\\biomes.png";
    private BufferedImage iBiomes;
    private int bloorK = 256; //part of image size used as blure radius
    private World world;
    private int scaledSize;
    private Water water;

    public static Color WATER_MAIN_COLOR = new Color(49, 87, 145);

    //biome colors
    public static final Color forest = new Color(55, 95, 68);
    public static final Color snow = new Color(203, 197, 194);
    public static final Color desert = new Color(175, 154, 107);
    public static final Color wasteland = new Color(124, 116, 94);
    public static final Color burned = new Color(68, 70, 67);
    public static final Color darknessFalls = new Color(100, 83, 67);


    public static final int forestInt = ImageMath.getPureIntFromRGB(forest);
    public static final int burnedInt = ImageMath.getPureIntFromRGB(burned);
    public static final int desertInt = ImageMath.getPureIntFromRGB(desert);
    public static final int snowInt = ImageMath.getPureIntFromRGB(snow);
    public static final int wastelandInt = ImageMath.getPureIntFromRGB(wasteland);
    public static final int darknessFallsInt = ImageMath.getPureIntFromRGB(darknessFalls);



    public Biomes(World world, Water water) {
        this.world = world;
        scaledSize = world.getScaledSize();
        this.water = water;
    }

    public BufferedImage loadBiomes() throws IOException {
        log("start load biomes.png");
        BufferedImage inputImage = ImageIO.read(new File(world.getPath() + BIOMES_PNG));
        log("Finish load biomes.png. Start scaling.");

        iBiomes = new BufferedImage(scaledSize, scaledSize, MAP_IMAGE_TYPE);

        // scale the input biomes image to the output image size
        Graphics2D g2d = iBiomes.createGraphics();
        g2d.drawImage(inputImage, 0, 0, scaledSize, scaledSize, null);
        g2d.dispose();

        //free mem
        inputImage.flush();

        log("Finish scaling. Start color mapping.");

        DataBuffer dataBuffer = iBiomes.getRaster().getDataBuffer();

        int dataBufferSize = dataBuffer.getSize();
        System.out.println("dataBufferSize = " + dataBufferSize);
        for (int i = 0; i < dataBufferSize; i++) {
            dataBuffer.setElem(i, mapBiomeRasterColor(dataBuffer.getElem(i)));
        }

        log("Finish color mapping.");
        writeToFile(world.getPath(), "_recolorBiomes", iBiomes);
        log("File written.");
        return inputImage;
    }

    private int mapBiomeRasterColor(int rgb) {
        switch (rgb) {
            case 16777215:
                return snowInt; //snow
            case 16770167:
                return desertInt;  //desert
            case 16384:
                return forestInt; //forest
            case 16754688:
                return wastelandInt; //wasteland
            case 12189951:
                return burnedInt; //burned
            case 4986880:
                return darknessFallsInt;
        }
        return rgb;
    }


    private int mapBiomeColor(int rgb) {
        switch (rgb) {
            case -16760832:
                return forest.getRGB();
            case -1:
                return snow.getRGB();
            case -7049:
                return desert.getRGB();
            case -22528:
                return wasteland.getRGB();
            case -4587265:
                return burned.getRGB();
        }
        return rgb;
    }

    public void applyHeightsToBiomes(BufferedImage iHeigths) throws IOException {
        long start, end;


        //Draw lakes
        //TODO no need to  walk through whole image. Save water points and use it to walk around +/- water spot square
        WritableRaster iHeigthsRaster = iHeigths.getRaster();
        for (int x = 0; x < scaledSize; x++) {
            for (int y = 0; y < scaledSize; y++) {
                if (water.pointIsInWater(x, y)) {
                    iBiomes.setRGB(x, y, WATER_MAIN_COLOR.getRGB());
                }
            }
        }

        log("Finish drawing lakes.");

        start = System.nanoTime();
        writeToFile(world.getPath(), "_bump", iHeigths);
        writeToFile(world.getPath(), "_biomes", iBiomes);
        end = System.nanoTime();
        log("File saving time:  = " + (end - start) / 1000000000 + "s");

        // normal vectors array
        log("Start alloc normal vectors array");
        float[] normalVectorsX = new float[scaledSize * scaledSize];
        float[] normalVectorsY = new float[scaledSize * scaledSize];
        float[] normalVectorsZ = new float[scaledSize * scaledSize];
        log("Finish alloc normal vectors array");
        // precalculate normal vectors
        BumpMappingUtils.FindNormalVectors(iHeigths, normalVectorsX, normalVectorsY, normalVectorsZ);
        log("Normal vectors are saved.");
        //free mem
        iHeigths.flush();
        //apply bump-mapping using normal vectors
        BumpMappingUtils.paint(iBiomes, scaledSize, scaledSize, normalVectorsX, normalVectorsY, normalVectorsZ);
        log("Bump mapping applied.");
        //Write bump-mapped biomes
        writeToFile(world.getPath(), "_biomesShadow", iBiomes);
    }

    public void blure() {
        log("Start bluring biomes.");
        BufferedImage iBiomesBlured = new BufferedImage(scaledSize, scaledSize, MAP_IMAGE_TYPE);
        new BoxBlurFilter(scaledSize / bloorK, scaledSize / bloorK, 1).filter(iBiomes, iBiomesBlured);
        iBiomes.flush();
        iBiomes = iBiomesBlured;
        log("Finish bluring biomes. Start drawing lakes.");
    }

    public BufferedImage getiBiomes() {
        return iBiomes;
    }
}
