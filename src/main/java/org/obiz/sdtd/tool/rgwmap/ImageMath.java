package org.obiz.sdtd.tool.rgwmap;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class ImageMath {

    /**
     * Clamp a value to an interval.
     * @param a the lower clamp threshold
     * @param b the upper clamp threshold
     * @param x the input parameter
     * @return the clamped value
     */
    public static float clamp(float x, float a, float b) {
        return (x < a) ? a : (x > b) ? b : x;
    }

    /**
     * Clamp a value to an interval.
     * @param a the lower clamp threshold
     * @param b the upper clamp threshold
     * @param x the input parameter
     * @return the clamped value
     */
    public static int clamp(int x, int a, int b) {
        return (x < a) ? a : (x > b) ? b : x;
    }

    public static int getIntFromRaster(Raster raster, int x, int y) {
        return raster.getSample(x, y, 0)|(raster.getSample(x, y, 1)<<8)|(raster.getSample(x, y, 0)<<16);
    }

    public static void setIntToRaster(WritableRaster raster, int x, int y, int rgb) {
        int R = rgb >> 16;
        int G = rgb >> 8;
        int B = rgb;
        raster.setSample(x, y, 0, R);
        raster.setSample(x, y, 1, G);
        raster.setSample(x, y, 2, B);
    }

    public static Color getRGBFromInt(int rgb) {
        int R = (rgb >> 16) & 0xff;
        int G = (rgb >> 8 ) & 0xff;
        int B = (rgb & 0xff);

        return new Color(R, G, B);
    }

    public static int getFullIntFromRGB(int red, int green, int blue) {
        return getFillIntFromPureInt(getPureIntFromRGB(red, green,  blue));
    }

    public static int getFullIntFromRGB(Color color) {
        return getFillIntFromPureInt(getPureIntFromRGB(color.getRed(), color.getGreen(), color.getBlue()));
    }

    public static int getPureIntFromRGB(int red, int green, int blue) {
        int result = 0;
        result |= red<<16;
        result |= green<<8;
        result |= blue;
        return result;
    }

    public static int getPureIntFromRGB(Color color) {
        return getPureIntFromRGB(color.getRed(), color.getGreen(), color.getBlue());
    }

    public static int getFillIntFromPureInt(int rgb) {
        return rgb | 0xff<<24;
    }

    public static int xy2i(BufferedImage image, int x, int y, int c) {
        return image.getHeight()*y*4 + x*4 + c;
    }

    public static int xy2i(BufferedImage image, int x, int y) {
        return image.getHeight()*y + x;
    }

    public static int i2x(BufferedImage image, int i) {
        return i%image.getHeight();
    }

    public static int i2y(BufferedImage image, int i) {
        return i/image.getHeight();
    }

}
