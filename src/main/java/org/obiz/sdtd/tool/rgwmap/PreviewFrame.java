package org.obiz.sdtd.tool.rgwmap;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.Map;

public class PreviewFrame extends JFrame {

    public PreviewFrame(BufferedImage img, Map<String, Path> icons) throws HeadlessException {
        super("Map preview");
        setSize(1035, 1055);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        add(new ImagePanel(img), BorderLayout.CENTER);
        LegendPanel legendPanel = new LegendPanel(icons);
        ScrollPane jScrollPane = new ScrollPane();
        legendPanel.setPreferredSize(new Dimension(130, 20*icons.size()));
        jScrollPane.setPreferredSize(new Dimension(160, 130));
        jScrollPane.add(legendPanel);
//        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(jScrollPane, BorderLayout.EAST);
        legendPanel.setSize(150, legendPanel.getHeight());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
