package org.obiz.sdtd.tool.rgwmap;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImagePanel extends JPanel {

    private BufferedImage image;
    private final int width;
    private final int height;

    private static final int SCALE = 2;

    public ImagePanel(BufferedImage image) {
            this.image = image;
        width = image.getWidth() / SCALE;
        height = image.getHeight() / SCALE;
        setPreferredSize(new Dimension(width, height));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, width, height, this); // see javadoc for more info on the parameters
    }

}
