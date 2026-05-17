package org.obiz.sdtd.tool.rgwmap;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferUShort;

public class BumpMappingUtils {

    public static void findNormalVectors(
            BufferedImage image,
            float[] nvX,
            float[] nvY,
            float[] nvZ)
    {
        final short[] pixels =
                ((DataBufferUShort) image.getRaster().getDataBuffer()).getData();

        final int width = image.getWidth();
        final int height = image.getHeight();

        final float scale = 1.0f / (256.0f * 256.0f);

        for (int y = 1; y < height - 1; y++) {

            final int row = y * width;

            for (int x = 1; x < width - 1; x++) {

                final int idx = row + x;

                final float xd =
                        ((pixels[idx + 1] & 0xFFFF) -
                                (pixels[idx - 1] & 0xFFFF)) * scale;

                final float yd =
                        ((pixels[idx + width] & 0xFFFF) -
                                (pixels[idx - width] & 0xFFFF)) * scale;

                final float len2 = xd * xd + yd * yd;

                float nz = 1.0f - (float)Math.sqrt(len2);

                if (nz < 0.0f) {
                    nz = 0.0f;
                }

                nvX[idx] = xd;
                nvY[idx] = yd;
                nvZ[idx] = nz;
            }
        }
    }

    public static void paint(BufferedImage source, int width, int height, float[] nvX, float[] nvY, float[] nvZ)
    {
        int x, y;
        float intensity;

        DataBuffer dataBuffer = source.getRaster().getDataBuffer();

        int newR, newG, newB;
        int oldR, oldG, oldB;

        //TODO multithread
        for (x = 0; x < width; x++)
        {
            for (y = 0; y < height; y++)
            {
                intensity = 0.6f + 20*BumpIntensity (width/3, height*6, x, y, nvX, nvY, nvZ, width);

                // brighten slightly (ambient)
                //intensity += 0.18;
                //if (intensity > 1.0) intensity = (float)1.0;

//                int sample = source.getRaster().getSample(x, y, 0)>> 8 & 0xff;
                int i = ImageMath.xy2i(source, x, y);
                int rgb = dataBuffer.getElem(i);
//                int rgb = source.getRGB(x, y);//>> 8 & 0xff;
                oldR = rgb >> 16 & 0xff;
                oldG = rgb >> 8 & 0xff;
                oldB = rgb & 0xff;

                newR = (int)(intensity * oldR);
                newG = (int)(intensity * oldG);
                newB = (int)(intensity * oldB);
                if (newR>255) newR = 255;
                if (newG>255) newG = 255;
                if (newB>255) newB = 255;

                if (newR<0) newR = 0;
                if (newG<0) newG = 0;
                if (newB<0) newB = 0;

//                Color c = new Color (newR, newG, newB);
//                source.setRGB(x, y, c.getRGB());
                dataBuffer.setElem(i, ImageMath.getPureIntFromRGB(newR, newG, newB));

            }
        }
    }

    private static float BumpIntensity (int lightX, int lightY, int pixelX, int pixelY, float[] nvX, float[] nvY, float[] nvZ, int width)
    {
        float Nx, Ny, Nz;

        // get normal vector of map
        Nx = nvX[pixelY * width + pixelX];
        Ny = nvY[pixelY * width + pixelX];
        Nz = nvZ[pixelY * width + pixelX];

        // make vector from pixel to light
        float lightvX = (float)(lightX - pixelX);
        float lightvY = (float)(lightY - pixelY);
        float lightvZ = (float)(width/10);

        // normalize
        float length = (float)Math.sqrt (lightvX*lightvX + lightvY*lightvY + lightvZ*lightvZ);

        lightvX /= length;
        lightvY /= length;
        lightvZ /= length;

        // take dot product
        float intensity = (Nx * lightvX + Ny * lightvY + Nz * lightvZ);

//        if (intensity < 0.0f) intensity = 0.0f;

        return intensity;
    }

}
