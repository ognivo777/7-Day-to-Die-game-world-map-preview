package org.obiz.sdtd.tool.rgwmap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

public class MapMagnifierPanel extends Panel {
    private ImagePanel imagePanel;
    private Set<String> visibleIcons;
    private int lastCursorPositionX, lastCursorPositionY;

    public MapMagnifierPanel(ImagePanel imagePanel) {
        super();
        this.imagePanel = imagePanel;
        visibleIcons = new HashSet<>();

        imagePanel.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {

            }

            @Override
            public void mouseMoved(MouseEvent e) {
//                System.out.println("I see the moving!");
                lastCursorPositionX = e.getX();
                lastCursorPositionY = e.getY();
                SwingUtilities.invokeLater(() -> repaint());
            }
        });
    }

    @Override
    public void repaint() {
        Graphics g = getGraphics();
        BufferedImage image = imagePanel.getImage();
        double scale = imagePanel.getScale();

        int x = Math.round(Math.round((this.lastCursorPositionX - imagePanel.getMapDx())/scale));
        int y = Math.round(Math.round((this.lastCursorPositionY - imagePanel.getMapDy())/scale));

        x = getWidth()/2 - x;
        y = getHeight()/2 - y;

        //draw bounds
        if(x > 0) x = 0;
        if(y > 0) y = 0;
        if(x < getWidth() - image.getWidth()) x = getWidth() - image.getWidth();
        if(y < getHeight() - image.getHeight()) y = getHeight() - image.getHeight();


        g.setColor(Color.white);
        g.drawImage(image, x, y, null);
        g.setColor(Color.ORANGE);
        g.drawRect(0,0, getWidth()-1, getHeight()-1);
    }

    public Set<String> getVisibleIcons() {
        //TODO
        return visibleIcons;
    }
}
