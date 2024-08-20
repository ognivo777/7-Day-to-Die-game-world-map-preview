package org.obiz.sdtd.tool.rgwmap.parts;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.obiz.sdtd.tool.rgwmap.MapBuilder.log;

public class DTMHeights {
    public static final String DTM_RAW = "dtm.raw";
    private final String path;
    private final File heightsFile;
    private final World world;
    private final long fileLength;
    private BufferedImage iHeigths;
    private int[][] bH;

    public DTMHeights(World world) {
        this.world = world;
        path = world.getPath();
        heightsFile = new File(path + "\\" + DTM_RAW);
        fileLength = heightsFile.length();
        log(DTM_RAW + " fileLength = " + fileLength);
    }

    public void readWorldHeights() throws IOException {
        //Result processed heights image
        int scaledSize = world.getScaledSize();
        int mapSize = world.getMapSize();
        int downScale = world.getDownScale();
//        totalPixels = world.getTotalPixels();

        iHeigths = new BufferedImage(scaledSize, scaledSize, BufferedImage.TYPE_USHORT_GRAY);
        WritableRaster raster = iHeigths.getRaster();

        bH = new int[scaledSize][scaledSize];

        try (FileInputStream hmis = new FileInputStream(heightsFile)) {
            byte buf[] = new byte[mapSize * 4];

            int readedBytes;
            int curPixelNum = 0;
            Set<Integer> grayColors = new HashSet<>();
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
                    int grayColor = (buf[i * 2 + 1] << 8) | (((int) buf[i * 2]) & 0xff);
                    if(!grayColors.contains(grayColor)){
                        grayColors.add(grayColor);
                    }
                    raster.setSample(x, y, 0, grayColor);
//                    int sample = raster.getSample(x, y, 0);
//                    if(sample !=grayColor ) {
//                        log("PROBLEM: sample="+sample+" grayColor="+grayColor);
//                    }
                    bH[x][y] = grayColor;
                    curPixelNum++;
                    //Draw progress bar
                    if (curPixelNum % (mapSize * 512) == 0) {
                        System.out.print("-");
                    }
                }
            }
            log("|\nFinish load " + DTM_RAW + ". Colors count:" + grayColors.size());
        }
    }

    public long getFileLength() {
        return fileLength;
    }

    public BufferedImage getiHeigths() {
        return iHeigths;
    }
}
