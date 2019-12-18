package org.obiz.sdtd.tool.rgwmap;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class LegendPanel extends Panel {
    private Map<String, Path> icons;
    private MapMagnifierPanel loupe;
    Map<String, BufferedImage> iconsCache; //local icons cache because of different render params

    int size = 32;
    private int posX = 0;
    private int posY = 0;
    private int countX = 1;
    private int countY = 0;

    public LegendPanel(Map<String, Path> icons, MapMagnifierPanel loupe) {
        this.icons = icons;
        this.loupe = loupe;
        iconsCache = new HashMap<>();
    }

    @Override
    public void paint(Graphics g) {
        int count = 0;
        JTextArea tip = new JTextArea(120, 32);
        setLayout(new GridLayout(30,10));
        setBackground(Color.LIGHT_GRAY);

        //Icons from the loupe come first
        int xd = 8;
        int yd = 8;
        /*for (String name : icons.keySet()) {
            definePosition(xd, yd);
            if (loupe.getVisibleIcons().contains(name)) {
                Panel iconPanel = new Panel();
                iconPanel.setLayout(new BorderLayout());
                iconPanel.setPreferredSize(new Dimension(size + xd / 2, size + yd / 2));
                add(iconPanel, BorderLayout.AFTER_LAST_LINE);

                MapBuilder.drawIcon(g, name, size, posX, posY, false, icons, 2, iconsCache, true);
                //g.drawString(name, x + xd, y + yd);
                countX++;
                count++;
            }
        }*/
        //Todo draw some devider
        //Icons not from the loupe comes bottom
        for (String name : icons.keySet()) {
            definePosition(xd, yd);
            if (!loupe.getVisibleIcons().contains(name)) {
                //Todo add localization
                tip.setToolTipText(name);

                JPanel iconPanel = new JPanel();
                iconPanel.setLayout(new BorderLayout());
                iconPanel.setPreferredSize(new Dimension(size + xd / 2, size + yd / 2));
                iconPanel.setBackground(Color.GREEN);

                //MapBuilder.drawIcon(g, name, size, posX, posY, false, icons, 2, iconsCache, true);
                iconPanel.add(tip);
                //iconPanel.addMouseListener();

                add(iconPanel, BorderLayout.PAGE_START);

                //g.drawString(name, x + xd, y + yd);
                countX++;
                count++;
            }
        }
    }



    public void definePosition(int xd, int yd) {
        if (countX > 10) {
            posX = 0;
            countX = 1;
            countY++;
        }
        posY = countY * size + yd;
        posX = (countX == 1) ? xd : posX + size + xd;
    }
}
