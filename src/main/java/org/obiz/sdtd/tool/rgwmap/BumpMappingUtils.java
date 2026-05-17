package org.obiz.sdtd.tool.rgwmap;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
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

    public static void paint(
            BufferedImage source,
            int width,
            int height,
            float[] nvX,
            float[] nvY,
            float[] nvZ)
    {
        final DataBufferInt buffer =
                (DataBufferInt) source.getRaster().getDataBuffer();

        final int[] pixels = buffer.getData();

        final int lightX = width / 3;
        final int lightY = height * 6;
        final float lightZ = width * 0.1f;

        for (int y = 0; y < height; y++) {

            final int row = y * width;

            for (int x = 0; x < width; x++) {

                final int idx = row + x;

                // light vector
                final float lx = lightX - x;
                final float ly = lightY - y;

                // normalize
                final float invLen = 1.0f /
                        (float)Math.sqrt(lx * lx + ly * ly + lightZ * lightZ);

                // dot product
                final float intensity =
                        0.6f + 20.0f * (
                                nvX[idx] * lx * invLen +
                                        nvY[idx] * ly * invLen +
                                        nvZ[idx] * lightZ * invLen
                        );

                final int rgb = pixels[idx];

                int r = (int)(((rgb >>> 16) & 0xFF) * intensity);
                int g = (int)(((rgb >>> 8) & 0xFF) * intensity);
                int b = (int)((rgb & 0xFF) * intensity);

                // fast clamp
                r = r < 0 ? 0 : Math.min(r, 255);
                g = g < 0 ? 0 : Math.min(g, 255);
                b = b < 0 ? 0 : Math.min(b, 255);

                pixels[idx] =
                        (r << 16) |
                                (g << 8) |
                                b;
            }
        }
    }

}
