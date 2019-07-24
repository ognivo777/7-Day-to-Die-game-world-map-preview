package org.obiz.sdtd.tool.rgwmap;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class LegendPanel extends Panel {
    private Map<String, Path> icons;
    Map<String, BufferedImage> iconsCache; //local icons cache because of different render params
    public LegendPanel(Map<String, Path> icons) {
        this.icons = icons;
        iconsCache = new HashMap<>();
    }

    @Override
    public void paint(Graphics g) {
        int count = 0;
        int size = 20;

        for (String name : icons.keySet()) {
            int x = 0;
            int y = count * size;
            if(name.equals("football_stadium")) {
                System.out.println(name);
            }
            if(name.equals("waste_")) {
                System.out.println(name);
            }
            MapBuilder.drawIcon(g, name, size, x, y, false, icons,2, iconsCache,  true);
            g.drawString(name, x + 20, y + 20);
            count++;
        }

    }
}
