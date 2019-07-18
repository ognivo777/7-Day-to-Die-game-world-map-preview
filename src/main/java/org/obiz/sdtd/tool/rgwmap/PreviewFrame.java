package org.obiz.sdtd.tool.rgwmap;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class PreviewFrame extends JFrame {

    public PreviewFrame(BufferedImage img) throws HeadlessException {
        super("Map preview");
        setSize(1035, 1055);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        ImagePanel image = new ImagePanel(img);
        add(image, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
