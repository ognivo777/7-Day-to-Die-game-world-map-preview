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
        add(new ImagePanel(img), BorderLayout.CENTER);
//        add(new LegendPanel(), BorderLayout.EAST);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
