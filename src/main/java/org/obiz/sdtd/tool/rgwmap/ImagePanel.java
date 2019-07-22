package org.obiz.sdtd.tool.rgwmap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class ImagePanel extends JPanel {

    private BufferedImage image;
    private double scale = 1;
    private final MouseListener mouseListener;

    public ImagePanel(BufferedImage image) {
        this.image = image;
        mouseListener = new MouseListener();
        addMouseListener(mouseListener);
        addMouseMotionListener(mouseListener);
        addMouseWheelListener(mouseListener);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int width;
        int height;
        width = (int) Math.round(image.getWidth() * scale);
        height = (int) Math.round(image.getHeight() * scale);
        g.drawImage(image, mouseListener.x, mouseListener.y, width, height, this);
    }

    class MouseListener implements java.awt.event.MouseListener, MouseMotionListener, MouseWheelListener {
        private boolean started = false;
        int startX = 0, startY = 0;
        int x = 0, y = 0;

        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {
            startX = e.getX() - x;
            startY = e.getY() - y;
            started = true;
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            started = false;
        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (started) {
                x = e.getX() - startX;
                y = e.getY() - startY;
                SwingUtilities.invokeLater(() -> e.getComponent().repaint(20));
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            if (started)
                System.out.format("move: %d:%d\n", x, y);
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            int Px = e.getX();
            int Py = e.getY();
            double d = 1 + e.getWheelRotation() / 10f;
            ImagePanel.this.scale *= d;
            this.x =  Math.round(Math.round(
                    Px - (Px - this.x)*d
            ));
            this.y =  Math.round(Math.round(
                    Py - (Py - this.y)*d
            ));
            SwingUtilities.invokeLater(() -> e.getComponent().repaint(20));
        }
    }

}
