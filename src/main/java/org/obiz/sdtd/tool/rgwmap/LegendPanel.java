package org.obiz.sdtd.tool.rgwmap;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.util.Map;

public class LegendPanel extends Panel {
    private Map<String, Path> icons;
    public LegendPanel(Map<String, Path> icons) {
        this.icons = icons;

//        add(new Button("_________________"));
    }

    @Override
    public void paint(Graphics g) {
        int count = 0;
        int size = 20;
        for (String name : icons.keySet()) {
            int x = 0;
            int y = count * size;
            MapBuilder.drawIcon(g, name, size, x, y, false, icons,2);
            g.drawString(name, x + 20, y + 20);
            count++;
        }

    }
}
