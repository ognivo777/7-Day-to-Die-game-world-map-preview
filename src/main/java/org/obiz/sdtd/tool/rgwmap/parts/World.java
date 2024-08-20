package org.obiz.sdtd.tool.rgwmap.parts;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;

import static org.obiz.sdtd.tool.rgwmap.MapBuilder.log;

public class World {
    private final Water water;
    private final Biomes biomes;
    private final Radiation radiation;
    private final Roads roads;
    private final Prefabs prefabs;
    private final String path;
    private int downScale = 2; //2 - better definition
    private int mapSize;
    private int scaledSize;
    private long totalPixels;
    private String mapFolder;
    private DTMHeights dtmHeights;

    public World(Path path) throws IOException, URISyntaxException {
        this.path = path.toString();
        mapFolder = path.getName(path.getNameCount()-1).toString();
        dtmHeights = new DTMHeights(this);
        mapSize = (int) Math.round(Math.sqrt(dtmHeights.getFileLength() / 2.));
        log("Detected mapSize: " + mapSize);
        scaledSize = mapSize / downScale;
        log("Resulting image side size will be: " + scaledSize + "px");

        //TODO rename to totalScaledPixels
        totalPixels = scaledSize;
        totalPixels *= totalPixels;

        water = new Water(this);
        biomes = new Biomes(this, water);
        radiation = new Radiation(this);
        roads = new Roads(this);
        prefabs = new Prefabs(this);
    }


    public void readAll(boolean applyGammaCorrection, boolean doBlureBiomes) throws IOException, XMLStreamException {
        dtmHeights.readWorldHeights();
        water.readWatersPoint();
        water.autoAjustImage(applyGammaCorrection, dtmHeights.getiHeigths());
        biomes.loadBiomes();
        radiation.drawRadiation(biomes.getiBiomes());
        if(doBlureBiomes) {
            biomes.blure();
        }
        biomes.applyHeightsToBiomes(dtmHeights.getiHeigths());
        roads.drawRoadsTo(biomes.getiBiomes());
        water.drawWater(biomes.getiBiomes());
        prefabs.drawPrefabsIcons(biomes.getiBiomes());
    }


    public String getPath() {
        return path;
    }

    public int getDownScale() {
        return downScale;
    }

    public int getMapSize() {
        return mapSize;
    }

    public int getScaledSize() {
        return scaledSize;
    }

    public long getTotalPixels() {
        return totalPixels;
    }

    public String getMapFolder() {
        return mapFolder;
    }

    public Biomes getBiomes() {
        return biomes;
    }
}
