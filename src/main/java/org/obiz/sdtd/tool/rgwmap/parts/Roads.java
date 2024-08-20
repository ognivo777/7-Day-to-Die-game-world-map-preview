package org.obiz.sdtd.tool.rgwmap.parts;

import org.obiz.sdtd.tool.rgwmap.ImageMath;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.File;
import java.io.IOException;

import static org.obiz.sdtd.tool.rgwmap.MapBuilder.log;
import static org.obiz.sdtd.tool.rgwmap.MapBuilder.writeToFile;

public class Roads {

    private final int scaledSize;
    private final int downScale;
    private Color ROAD_MAIN_COLOR = new Color(141, 129, 106);
    private Color ROAD_SECONDARY_COLOR = new Color(52, 59, 65);

    private World world;
    private String path;
    private int mapSize;

    public Roads(World world) {
        this.world = world;
        this.path = world.getPath();
        this.mapSize =  world.getMapSize();
        scaledSize = world.getScaledSize();
        downScale = world.getDownScale();
    }


    public void drawRoadsTo(BufferedImage iBiomes) throws IOException {
        log("Load roads file");
        BufferedImage roads = ImageIO.read(new File(path + "\\splat3.png"));
        log("Roads loaded. Start drawing.");

//        Color roadColor;

        DataBuffer db = iBiomes.getRaster().getDataBuffer();

        DataBuffer rdb = roads.getAlphaRaster().getDataBuffer();
        boolean firstTime = true;

        System.out.println("TEST : " + (rdb.getSize()/4-mapSize*mapSize));

        //TODO multithread

        for (int i = 0; i < scaledSize; i++) {
            for (int j = 0; j < scaledSize; j++) {
                int c2 = rdb.getElem(ImageMath.xy2i(roads,i*downScale, j*downScale, 2));
                if(c2!=0) {
//                    db.setElem(ImageMath.xy2i(iBiomes, i, j), ImageMath.getPureIntFromRGB(255, 201, 14));
                    db.setElem(ImageMath.xy2i(iBiomes, i, j), ImageMath.getPureIntFromRGB(ROAD_MAIN_COLOR));
                }
                int c3 = rdb.getElem(ImageMath.xy2i(roads,i*downScale, j*downScale, 3));
                if(c3!=0) {
//                    db.setElem(ImageMath.xy2i(iBiomes, i, j), ImageMath.getPureIntFromRGB(67, 163, 203));
                    db.setElem(ImageMath.xy2i(iBiomes, i, j), ImageMath.getPureIntFromRGB(ROAD_SECONDARY_COLOR));
                }
            }
        }
        log("Finish roads drawing.");

        writeToFile(world.getPath(), "_map_with_roads", iBiomes);
    }
}
