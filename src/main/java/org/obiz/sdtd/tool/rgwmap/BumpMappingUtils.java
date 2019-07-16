package org.obiz.sdtd.tool.rgwmap;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.PixelGrabber;
import java.awt.image.Raster;

public class BumpMappingUtils {
    public static void FindNormalVectors(BufferedImage imagemap, float[] nvX, float[] nvY, float[] nvZ) {
        Raster r = imagemap.getRaster();
//        DataBuffer db = r.getDataBuffer();

        int imageWidth = imagemap.getWidth();
        int imageHeight = imagemap.getHeight();

        // get pixels into pixelarray
//        int[] pixelarraymap = new int [imageWidth * imageHeight];

//        pg = new PixelGrabber (imagemap, 0, 0, imageWidth, imageHeight, pixelarraymap, 0, imageWidth);

//        try {
//            pg.grabPixels ();
//        }
//        catch (InterruptedException e) {
//            System.err.println("interrupted waiting for pixels!");
//        }

        for (int x = 1; x < imageWidth - 1; x++)
        {
            for (int y = 1; y < imageHeight - 1; y++)
            {

                int X0 = r.getSample(x + 1, y, 0);
                int X1 = r.getSample(x - 1, y, 0);
//                int X0 = db.getElem(ImageMath.xy2i(imagemap, x + 1, y));
//                int X1 = db.getElem(ImageMath.xy2i(imagemap, x - 1, y));

                int Y0 = r.getSample(x, y + 1, 0);
                int Y1 = r.getSample(x, y - 1, 0);
//                int Y0 = db.getElem(ImageMath.xy2i(imagemap, x, y + 1));
//                int Y1 = db.getElem(ImageMath.xy2i(imagemap, x, y - 1));

                float Xd = X0 - X1;
                float Yd = Y0 - Y1;

                // maximum for Xd, Yd is: (MAX - 0) + (MAX - 0) + (MAX - 0) = 3 * MAX

                Xd /= (float)(256*256);
                Yd /= (float)(256*256);

                float Nx = Xd;
                float Ny = Yd;
                float Nz = (float)(1 - Math.sqrt ((Xd * Xd) + (Yd * Yd)));

                if (Nz < 0.0f)
                    Nz = 0.0f;

                nvX[y * imageWidth + x] = Nx;
                nvY[y * imageWidth + x] = Ny;
                nvZ[y * imageWidth + x] = Nz;

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
