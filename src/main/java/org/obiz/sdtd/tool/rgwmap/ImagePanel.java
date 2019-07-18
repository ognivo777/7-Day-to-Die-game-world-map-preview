package org.obiz.sdtd.tool.rgwmap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class ImagePanel extends JPanel {

    private BufferedImage image;
    private int width;
    private int height;
    private double scale = 1;
    private final MouseListener mouseListener;

    public ImagePanel(BufferedImage image) {
        this.image = image;
        width = (int) Math.round(image.getWidth() * scale);
        height = (int) Math.round(image.getHeight() * scale);
//        setPreferredSize(new Dimension(width, height));

        mouseListener = new MouseListener();
        addMouseListener(mouseListener);
        addMouseMotionListener(mouseListener);
        addMouseWheelListener(mouseListener);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        width = (int) Math.round(image.getWidth() * scale);
        height = (int) Math.round(image.getHeight() * scale);
        g.drawImage(image, mouseListener.x, mouseListener.y, width, height, this); // see javadoc for more info on the parameters
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
            System.out.println("started");
            started = true;
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            started = false;
            System.out.println("unstarted");
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
                System.out.format("drag: %d:%d\n", e.getX() - x, e.getY() - y);
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
            System.out.format("pos: %d:%d\n", e.getX(), e.getY());
            System.out.println(e.getScrollAmount());
            System.out.println(e.getWheelRotation());
            System.out.println(e.getPreciseWheelRotation());
            System.out.println(e.getScrollType());
            double d = 1 + e.getWheelRotation() / 5f;
            System.out.println("d = " + d);
            System.out.println("e.getX() = " + e.getX());
            System.out.println("e.getY() = " + e.getY());
            double oldScale = ImagePanel.this.scale;
            ImagePanel.this.scale *= d;
            double dd = oldScale/scale;
//            double dd = scale/oldScale;
//            double dd = scale;
//            double dd = d;
            System.out.println("scale = " + scale);
            System.out.println("dd = " + dd);
            int k = 0;
            startX = e.getX() - k;
            startY = e.getY() - k;


//            x =  Math.round(Math.round((startX*(dd-1))/(1-dd)));
//            y =  Math.round(Math.round((startY*(dd-1))/(1-dd)));

//            x =  Math.round(Math.round(startX));
//            y =  Math.round(Math.round(startY));

            x =  Math.round(Math.round(dd*(startX-x)-startX));
            x =  Math.round(Math.round(dd*(startY-y)-startY));


            System.out.println(x + " : " + y);
            SwingUtilities.invokeLater(() -> e.getComponent().repaint(20));
        }
    }

}
