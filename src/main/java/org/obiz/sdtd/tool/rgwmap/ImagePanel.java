package org.obiz.sdtd.tool.rgwmap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class ImagePanel extends JPanel {

    private BufferedImage image;
    private double scale = 0;
    private double startScale = 0;
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

        if(scale==0) {
            /* For the first time painting calculate staring scale to fit with panel size */
            scale = Math.min(getHeight(), getWidth()) / (1d * Math.max(image.getHeight(), image.getWidth()));
            startScale = scale;
        }

        int scaledWidth;
        int scaledHeight;
        scaledWidth = (int) Math.round(image.getWidth() * scale);
        scaledHeight = (int) Math.round(image.getHeight() * scale);
        g.drawImage(image, mouseListener.x, mouseListener.y, scaledWidth, scaledHeight, this);
    }

    class MouseListener implements java.awt.event.MouseListener, MouseMotionListener, MouseWheelListener {
        private boolean started = false;
        int startX = 0, startY = 0;
        int x = 0, y = 0;

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() % 2 == 0) {
                applyScale(e, 1.6, true);
            }
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
            if (started
                    /* here skip the dragging if image smaller than panel */
                    && scale>startScale) {
                x = e.getX() - startX;
                y = e.getY() - startY;
                fixPosByBorders();
                SwingUtilities.invokeLater(() -> e.getComponent().repaint(20));
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
//            if (started)
//                System.out.format("move: %d:%d\n", x, y);
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            double d = 1 + e.getWheelRotation() / 5f;
            applyScale(e, d, false);
        }

        private void applyScale(MouseEvent e, double scaleMultiplier, boolean cyclicZoom) {
            int Px = e.getX();
            int Py = e.getY();
            double oldScale = ImagePanel.this.scale;
            scale /= scaleMultiplier;
            if(scale > 2) {
                if (cyclicZoom) {
                    scale = startScale;
                    x = 0;
                    y = 0;
                    SwingUtilities.invokeLater(() -> e.getComponent().repaint(20));
                    return;
                } else {
                    scale = 2;
                }
            }
            if(scale < 0.2) {
                scale = 0.2;
            }


            if(scale<(startScale*.85)) {
                scale=startScale*.85;
                x = Math.round(Math.round((getWidth()-image.getWidth()*scale)/2));
                y = Math.round(Math.round((getHeight()-image.getHeight()*scale)/2));
            } else {
                double dd = scale / oldScale;
                x = Math.round(Math.round(
                        Px - (Px - this.x) * dd
                ));
                y = Math.round(Math.round(
                        Py - (Py - this.y) * dd
                ));

                fixPosByBorders();

            }
            SwingUtilities.invokeLater(() -> e.getComponent().repaint(20));
        }

        /* Prevent drag map corners inside the panel if map size > panel size.*/
        private void fixPosByBorders() {
            if(scale>startScale) {
                if(mouseListener.x>0) {
                    mouseListener.x = 0;
                }
                if(mouseListener.y>0) {
                    mouseListener.y = 0;
                }
                if(mouseListener.x < -(image.getWidth()*scale-getWidth())) {
                    mouseListener.x = -Math.round(Math.round(image.getWidth()*scale-getWidth()));
                }
                if(mouseListener.y < -(image.getHeight()*scale-getHeight())) {
                    mouseListener.y = -Math.round(Math.round(image.getHeight()*scale-getHeight()));
                }
            }
        }
    }

    public BufferedImage getImage() {
        return image;
    }

    public double getScale() {
        return scale;
    }

    public int getMapDx() {
        return mouseListener.x;
    }

    public int getMapDy() {
        return mouseListener.y;
    }
}
