package org.obiz.sdtd.tool.rgwmap.parts;

import org.obiz.sdtd.tool.rgwmap.Timer;


import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Set;

import static org.obiz.sdtd.tool.rgwmap.MapBuilder.*;
import static org.obiz.sdtd.tool.rgwmap.parts.Icons.drawIcon;

public class Prefabs {
    public static final String PREFABS_XML = "\\prefabs.xml";
    private final boolean DRAW_ICON_AXIS = false;
    private final int DRAW_ICON_SPRITE_BUF_SCALE = 2;

    private final Icons icons;
    private World world;
    private int mapSize;
    private int downScale;

    public Prefabs(World world) throws IOException, URISyntaxException {
        this.world = world;
        mapSize = world.getMapSize();
        downScale = world.getDownScale();
        icons = new Icons();
    }

    public void drawPrefabsIcons(BufferedImage iBiomes) throws IOException, XMLStreamException {
        String prefabs = PREFABS_XML;
        XMLInputFactory xmlif = XMLInputFactory.newInstance();
        XMLStreamReader xmlr = xmlif.createXMLStreamReader(prefabs, new FileInputStream(world.getPath() + prefabs));

        Graphics2D g = iBiomes.createGraphics();

        int eventType;



        Set<String> prefabsGroups = icons.getIcons().keySet();
        int prefabsSVGCounter = 0;
        int prefabsCounter = 0;

        Timer.startTimer("Draw prefabs");
        log("Processing prefabs: ");

        while (xmlr.hasNext()) {
            eventType = xmlr.next();
            if (eventType == XMLEvent.START_ELEMENT) {
                if (xmlr.getAttributeCount() == 4) {
                    String attributeValue = xmlr.getAttributeValue(2);
                    String[] split = attributeValue.split(",");
                    int x = (mapSize / 2 + Integer.parseInt(split[0])) / downScale;
                    int y = (mapSize / 2 - Integer.parseInt(split[2])) / downScale;

                    int rot = Integer.parseInt(xmlr.getAttributeValue(3));
                    int xShift = x + i10;
                    int yShift = y - i45;

                    String prefabName = xmlr.getAttributeValue(1);
                    String foundPrefabGroup = null;

                    loopPrefabsGroups:
                    for (String prefabsGroup : prefabsGroups) {
                        if (prefabName.toLowerCase().contains(prefabsGroup)) {
                            foundPrefabGroup = prefabsGroup;
                            prefabsSVGCounter++;
                            break loopPrefabsGroups;
                        }
                    }

                    prefabsCounter++;

                    if (foundPrefabGroup != null) {
                        drawIcon(g, foundPrefabGroup, i40, xShift, yShift, DRAW_ICON_AXIS, icons.getIcons(), DRAW_ICON_SPRITE_BUF_SCALE, false);
                    } else if (prefabName.startsWith("part_") || prefabName.startsWith("deco_") || prefabName.startsWith("sign_") || prefabName.startsWith("street_") ||
                            prefabName.startsWith("streets_") || prefabName.startsWith("player_") || prefabName.startsWith("desert_") || prefabName.startsWith("rock_")) {
                        //skip, this is part of main object (signs, boards, road_parts etc)
                    } else if (prefabName.contains("rwg_tile_")) {
                        //skip, this is part of rural streets roads
                    } else if (prefabName.contains("sign")) {
                        g.setColor(new Color(51, 49, 51));
                        g.fill3DRect(x, y, i10, i10, true);
                    } else {
                        drawIcon(g, "NA", i40, x, y, DRAW_ICON_AXIS, icons.getIcons(), DRAW_ICON_SPRITE_BUF_SCALE, false);
                        //Debug svg files by prefab name
                        //g.drawString(prefabName, x, y);
                    }
                }
            }
        }


        log( prefabsCounter + " prefabs added, " + prefabsSVGCounter + " of them added from SVG.");
        Timer.stopTimer("Draw prefabs");
        log("Start write finish image.");
        writeToFile(world.getPath(), "_mapWithObjects", iBiomes, false);
        log("Finish write finish image.");
    }
}
