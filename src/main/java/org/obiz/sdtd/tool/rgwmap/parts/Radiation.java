package org.obiz.sdtd.tool.rgwmap.parts;

import org.obiz.sdtd.tool.rgwmap.ImageMath;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.File;
import java.io.IOException;

import static org.obiz.sdtd.tool.rgwmap.MapBuilder.MAP_IMAGE_TYPE;
import static org.obiz.sdtd.tool.rgwmap.MapBuilder.log;

public class Radiation {

    public static final String RADIATION_PNG = "\\radiation.png";
    private BufferedImage iRad;
    private World world;
    private int scaledSize;
    private String path;

    public Radiation(World world) {
        this.world = world;
        this.path = world.getPath();
        scaledSize = world.getScaledSize();
    }

    public void drawRadiation(BufferedImage iBiomes) throws IOException {
        File radiationFile;

        log("Load radiation map..");
        String radiationFileName = path + RADIATION_PNG;
        radiationFile = new File(radiationFileName);
        if (!radiationFile.exists() || !radiationFile.isFile() || !radiationFile.canRead()) {
            System.err.println("File not found: " + radiationFileName + " and that's OK");
        } else {
            BufferedImage inputImage = ImageIO.read(new File(path + RADIATION_PNG));

            log("Beware of radiation!");

            iRad = new BufferedImage(scaledSize, scaledSize, MAP_IMAGE_TYPE);

            // scale the input radiation zone image to the output image size
            log("Start scale radiation..");
            Graphics2D g2d = iRad.createGraphics();
            g2d.drawImage(inputImage, 0, 0, scaledSize, scaledSize, null);
            g2d.dispose();

            //free mem
            inputImage.flush();
            log("Start draw radiation.");
            //TODO multithread
            DataBuffer biomesDB = iBiomes.getRaster().getDataBuffer();
            DataBuffer radiationDB = iRad.getRaster().getDataBuffer();
            for (int i = 0; i < biomesDB.getSize(); i++) {
                int rgb = ImageMath.getFillIntFromPureInt(biomesDB.getElem(i));
                int rgbRad = ImageMath.getFillIntFromPureInt(radiationDB.getElem(i));
                if (rgbRad == -65536) {
                    int oldR = rgb >> 16 & 0xff;
                    int oldG = rgb >> 8 & 0xff;
                    int oldB = rgb & 0xff;

                    int newR = (int) (oldR * 1.5);
                    if (newR > 255) newR = 255;
                    if (newR < 0) newR = 0;

                    biomesDB.setElem(i, ImageMath.getPureIntFromRGB(newR, oldG, oldB));
//                iBiomes.setRGB(x, y, new Color(newR, oldG, oldB).getRGB());
                }

            }
        }
        log("End draw radiation.");

    }
}
