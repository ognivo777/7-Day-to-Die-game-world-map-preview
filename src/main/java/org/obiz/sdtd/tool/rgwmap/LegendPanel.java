package org.obiz.sdtd.tool.rgwmap;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.obiz.sdtd.tool.rgwmap.parts.Icons.imgDrawIcon;

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

        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);

        setBackground(Color.ORANGE);
        showLegend();
    }

    public void showLegend() {
        int cShift = -32;
        int count = 0;
        JPanel iconGBL = new JPanel();
        iconGBL.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill   = GridBagConstraints.BOTH;
        c.gridwidth  = 1;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(cShift, cShift, cShift, cShift);
        c.ipadx = -4;
        c.ipady = -4;
        c.weightx = 0.0;
        c.weighty = 0.0;

        iconGBL.setPreferredSize(new Dimension(400,500));
        //Todo draw some devider

        //Icons from the loupe come first
        int xd = 8;
        int yd = 8;
        for (String name : icons.keySet()) {
            definePosition(xd, yd);
            c.gridx = countX - 1;
            c.gridy = countY;

            if (!loupe.getVisibleIcons().contains(name)) {
                JLabel iconLabel = new JLabel(new ImageIcon(imgDrawIcon(name, size, 0, 0, false, icons, 2, iconsCache, true)));
                //Todo add localization
                iconLabel.setToolTipText(name);
                iconGBL.add(iconLabel, c);

                countX++;
                count++;
            }
        }
        //Icons not from the loupe comes bottom
        /*for (String name : icons.keySet()) {
            definePosition(xd, yd);
            c.gridx = countX - 1;
            c.gridy = countY;

            if (!loupe.getVisibleIcons().contains(name)) {
                JLabel iconLabel = new JLabel(new ImageIcon(MapBuilder.imgDrawIcon(name, size, 0, 0, false, icons, 2, iconsCache, true)));
                //Todo add localization
                iconLabel.setToolTipText(name);
                iconGBL.add(iconLabel, c);

                countX++;
                count++;
            }
        }*/
        //JOptionPane.showMessageDialog(null, iconGBL); //for debugging
        add(iconGBL);

    }

    public void definePosition(int xd, int yd) {
        if (countX > 6) {
            posX = 0;
            countX = 1;
            countY++;
        }
        posY = countY * size + yd;
        posX = (countX == 1) ? xd : posX + size + xd;
    }

    /*@Override
    public void paint(Graphics g) {

        int count = 0;
        JPanel iconGBL = new JPanel();
        iconGBL.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill   = GridBagConstraints.BOTH;
        c.gridheight = 1;
        c.gridwidth  = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(0, 0, 0, 0);
        c.ipadx = 0;
        c.ipady = 0;
        c.weightx = 0.0;
        c.weighty = 0.0;

        iconGBL.setPreferredSize(new Dimension(400,500));

        //Icons from the loupe come first
        int xd = 8;
        int yd = 8;
        for (String name : icons.keySet()) {
            if (loupe.getVisibleIcons().contains(name)) {
                 MapBuilder.drawIcon(g, name, size, posX, posY, false, icons, 2, iconsCache, true);
                //g.drawString(name, x + xd, y + yd);
                countX++;
                count++;
            }
        }
        //Todo draw some devider
        //Icons not from the loupe comes bottom
        for (String name : icons.keySet()) {
            definePosition(xd, yd);
            c.gridx = countX - 1;
            c.gridy = countY;

            //if (!loupe.getVisibleIcons().contains(name)) {
                //Todo add localization
                JLabel iconLabel = new JLabel(new ImageIcon(MapBuilder.imgDrawIcon(name, size, 0, 0, false, icons, 2, iconsCache, true)));
                iconLabel.setToolTipText(name);
                iconGBL.add(iconLabel, c);

                countX++;
                count++;
            //}
        }

        //for debugging
        //if (countX == 10) JOptionPane.showMessageDialog(null, iconGBL);
        add(iconGBL);

    }*/
}
