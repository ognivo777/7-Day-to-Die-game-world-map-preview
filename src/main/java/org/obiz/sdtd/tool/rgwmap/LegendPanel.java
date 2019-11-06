package org.obiz.sdtd.tool.rgwmap;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class LegendPanel extends Panel {
    private Map<String, Path> icons;
    private MapMagnifierPanel loupe;
    Map<String, BufferedImage> iconsCache; //local icons cache because of different render params
    public LegendPanel(Map<String, Path> icons, MapMagnifierPanel loupe) {
        this.icons = icons;
        this.loupe = loupe;
        iconsCache = new HashMap<>();
    }

    @Override
    public void paint(Graphics g) {
        int count = 0;
        int size = 20;

        //Icons from the loupe come first
        int xd = 22;
        int yd = 13;
        for (String name : icons.keySet()) {
            int x = 0;
            int y = count * size;
            if (loupe.getVisibleIcons().contains(name)) {
                MapBuilder.drawIcon(g, name, size, x, y, false, icons, 2, iconsCache, true);
                g.drawString(name, x + xd, y + yd);
                count++;
            }
        }
        //Todo draw some devider
        //Icons not from the loupe comes bottom
        for (String name : icons.keySet()) {
            int x = 0;
            int y = count * size;
            if (!loupe.getVisibleIcons().contains(name)) {
                MapBuilder.drawIcon(g, name, size, x, y, false, icons, 2, iconsCache, true);
                g.drawString(name, x + xd, y + yd);
                count++;
            }
        }
    }
}
