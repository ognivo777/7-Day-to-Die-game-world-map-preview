package org.obiz.sdtd.tool.rgwmap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

public class ButtonPanel extends Panel {
    private int lastCursorPositionX, lastCursorPositionY;

    public ButtonPanel() {
        super();

        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        setLayout(gbl);

        c.anchor = GridBagConstraints.CENTER;
        c.fill   = GridBagConstraints.HORIZONTAL;
        c.gridwidth  = 0;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = GridBagConstraints.RELATIVE;
        c.insets = new Insets(8, 0, 0, 0);
        c.ipadx = 0;
        c.ipady = 0;
        c.weightx = 0.0;
        c.weighty = 0.0;

        Button btn01 = new Button("Адзин Адзин Адз");
        add(btn01,c);
        Button btn02 = new Button("Эта кнопка дела");
        add(btn02,c);
        Button btn03 = new Button("Полный список");
        add(btn03,c);
    }

    public void addButton(String name) {
        ///
    }
}
