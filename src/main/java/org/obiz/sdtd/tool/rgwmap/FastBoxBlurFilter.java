package org.obiz.sdtd.tool.rgwmap;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.stream.IntStream;

public final class FastBoxBlurFilter {

    private final int radius;
    private final int iterations;

    private int[] divide;
    private int divideRadius = -1;

    private int[] clampTable;
    private int size;

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
     * <p>
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

        size = width;

        clampTable = new int[size + radius * 2];

        for (int i = -radius; i < size + radius; i++) {
            clampTable[i + radius] =
                    //Math.max(0, Math.min(i, size - 1));
                    (i < 0) ? 0 : Math.min(i, size - 1);
        }

        final int[] pixels =
                ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

        final int[] temp = new int[pixels.length];

        ensureDivideTable(radius);

        for (int i = 0; i < iterations; i++) {

            blurTranspose(
                    pixels,
                    temp
            );

            blurTranspose(
                    temp,
                    pixels
            );
        }
    }

    private void ensureDivideTable(int radius) {

        if (divideRadius == radius) {
            return;
        }

        final int tableSize = radius * 2 + 1;

        divide = new int[256 * tableSize];

        for (int i = 0; i < divide.length; i++) {
            divide[i] = i / tableSize;
        }

        divideRadius = radius;
    }

    /**
     * Blur + transpose.
     * <p>
     * This is the key optimization.
     */
    private void blurTranspose(
            int[] in,
            int[] out
    ) {

        final int size = this.size;
        final int radius = this.radius;
        final int sizeMinus1 = size - 1;

        final int[] divide = this.divide;
        final int[] clampTable = this.clampTable;

        IntStream.range(0, size)
                .parallel()
                .forEach(y -> {

                    final int inIndex = y * size;

                    int outIndex = y;

                    int ta = 0;
                    int tr = 0;
                    int tg = 0;
                    int tb = 0;

                    // initial window
                    for (int i = -radius; i <= radius; i++) {

                        final int rgb =
                                in[inIndex + clampTable[i + radius]];

                        ta += (rgb >>> 24);
                        tr += (rgb >> 16) & 0xFF;
                        tg += (rgb >> 8) & 0xFF;
                        tb += rgb & 0xFF;
                    }

                    for (int x = 0; x < size; x++) {

                        out[outIndex] =
                                (divide[ta] << 24)
                                        | (divide[tr] << 16)
                                        | (divide[tg] << 8)
                                        | divide[tb];

                        int i1 = x + radius + 1;
                        if (i1 > sizeMinus1) {
                            i1 = sizeMinus1;
                        }

                        int i2 = x - radius;
                        if (i2 < 0) {
                            i2 = 0;
                        }

                        final int rgb1 = in[inIndex + i1];
                        final int rgb2 = in[inIndex + i2];

                        ta += ((rgb1 >>> 24) - (rgb2 >>> 24));
                        tr += (((rgb1 >> 16) & 0xFF) -
                                ((rgb2 >> 16) & 0xFF));
                        tg += (((rgb1 >> 8) & 0xFF) -
                                ((rgb2 >> 8) & 0xFF));
                        tb += ((rgb1 & 0xFF) -
                                (rgb2 & 0xFF));

                        // transpose write
                        outIndex += size;
                    }
                });
    }

    private static int clamp(int v, int min, int max) {
        return (v < min) ? min : Math.min(v, max);
    }
}