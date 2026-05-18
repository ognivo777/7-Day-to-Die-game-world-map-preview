package org.obiz.sdtd.tool.rgwmap;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public final class FastBoxBlurFilter {

    private final int radius;
    private final int iterations;

    // Reusable divide table cache
    private int[] divide;
    private int divideRadius = -1;

    public FastBoxBlurFilter(int radius, int iterations) {
        if (radius < 1) {
            throw new IllegalArgumentException("radius must be > 0");
        }

        if (iterations < 1) {
            throw new IllegalArgumentException("iterations must be > 0");
        }

        this.radius = radius;
        this.iterations = iterations;
    }

    /**
     * Blur image in-place.
     *
     * Requirements:
     * - image must be square
     * - TYPE_INT_ARGB or TYPE_INT_RGB recommended
     */
    public void filter(BufferedImage image) {
        final int width = image.getWidth();
        final int height = image.getHeight();

        // width == height optimization assumption
        if (width != height) {
            throw new IllegalArgumentException(
                    "Only square images supported: " + width + "x" + height
            );
        }

        final int size = width;

        final int[] pixels =
                ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

        final int[] temp = new int[pixels.length];

        ensureDivideTable(radius);

        for (int i = 0; i < iterations; i++) {
            blurHorizontal(pixels, temp, size, radius);
            blurVertical(temp, pixels, size, radius);
        }
    }

    private void ensureDivideTable(int radius) {
        if (divideRadius == radius) {
            return;
        }

        final int tableSize = radius * 2 + 1;
        final int max = 256 * tableSize;

        divide = new int[max];

        for (int i = 0; i < max; i++) {
            divide[i] = i / tableSize;
        }

        divideRadius = radius;
    }

    /**
     * Horizontal pass
     */
    private void blurHorizontal(
            int[] in,
            int[] out,
            int size,
            int radius
    ) {
        final int widthMinus1 = size - 1;
        final int[] divide = this.divide;

        int inIndex = 0;

        for (int y = 0; y < size; y++) {

            int outIndex = y * size;

            int ta = 0;
            int tr = 0;
            int tg = 0;
            int tb = 0;

            // Initial accumulation
            for (int i = -radius; i <= radius; i++) {

                final int rgb = in[inIndex + clamp(i, 0, widthMinus1)];

                ta += (rgb >>> 24);
                tr += (rgb >> 16) & 0xFF;
                tg += (rgb >> 8) & 0xFF;
                tb += rgb & 0xFF;
            }

            for (int x = 0; x < size; x++) {

                out[outIndex + x] =
                        (divide[ta] << 24)
                                | (divide[tr] << 16)
                                | (divide[tg] << 8)
                                | divide[tb];

                int i1 = x + radius + 1;
                if (i1 > widthMinus1) {
                    i1 = widthMinus1;
                }

                int i2 = x - radius;
                if (i2 < 0) {
                    i2 = 0;
                }

                final int rgb1 = in[inIndex + i1];
                final int rgb2 = in[inIndex + i2];

                ta += ((rgb1 >>> 24) - (rgb2 >>> 24));
                tr += (((rgb1 >> 16) & 0xFF) - ((rgb2 >> 16) & 0xFF));
                tg += (((rgb1 >> 8) & 0xFF) - ((rgb2 >> 8) & 0xFF));
                tb += ((rgb1 & 0xFF) - (rgb2 & 0xFF));
            }

            inIndex += size;
        }
    }

    private void blurVertical(
            int[] in,
            int[] out,
            int size,
            int radius
    ) {
        final int widthMinus1 = size - 1;
        final int[] divide = this.divide;

        for (int x = 0; x < size; x++) {

            int ta = 0;
            int tr = 0;
            int tg = 0;
            int tb = 0;

            // Initial accumulation
            for (int i = -radius; i <= radius; i++) {

                final int y = clamp(i, 0, widthMinus1);
                final int rgb = in[y * size + x];

                ta += (rgb >>> 24);
                tr += (rgb >> 16) & 0xFF;
                tg += (rgb >> 8) & 0xFF;
                tb += rgb & 0xFF;
            }

            for (int y = 0; y < size; y++) {

                out[y * size + x] =
                        (divide[ta] << 24)
                                | (divide[tr] << 16)
                                | (divide[tg] << 8)
                                | divide[tb];

                int i1 = y + radius + 1;
                if (i1 > widthMinus1) {
                    i1 = widthMinus1;
                }

                int i2 = y - radius;
                if (i2 < 0) {
                    i2 = 0;
                }

                final int rgb1 = in[i1 * size + x];
                final int rgb2 = in[i2 * size + x];

                ta += ((rgb1 >>> 24) - (rgb2 >>> 24));
                tr += (((rgb1 >> 16) & 0xFF) - ((rgb2 >> 16) & 0xFF));
                tg += (((rgb1 >> 8) & 0xFF) - ((rgb2 >> 8) & 0xFF));
                tb += ((rgb1 & 0xFF) - (rgb2 & 0xFF));
            }
        }
    }

    private static int clamp(int v, int min, int max) {
        return (v < min) ? min : Math.min(v, max);
    }
}