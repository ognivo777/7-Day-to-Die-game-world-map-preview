package org.obiz.sdtd.tool.rgwmap;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.Map;

public class PreviewFrame extends JFrame {

    private final int rightPanelWidth = 190;

    public PreviewFrame(BufferedImage img, Map<String, Path> icons, String mapFolder) throws HeadlessException {
        super("Map preview (" + mapFolder + ")");
        setSize(905 + rightPanelWidth, 905);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        ImagePanel imagePanel = new ImagePanel(img);
        add(imagePanel, BorderLayout.CENTER);
        MapMagnifierPanel loupe = new MapMagnifierPanel(imagePanel);
        LegendPanel legendPanel = new LegendPanel(icons, loupe);

        //Container for Map magnifier and icons legend
        Panel rightPanel = new Panel();
//        rightPanel.setPreferredSize(new Dimension(rightPanelWidth + 30, 500));
        rightPanel.setLayout(new BorderLayout());

        ScrollPane jScrollPane = new ScrollPane();
        loupe.setPreferredSize(new Dimension(rightPanelWidth, rightPanelWidth));
        legendPanel.setPreferredSize(new Dimension(rightPanelWidth, 20*icons.size()));
        jScrollPane.setPreferredSize(new Dimension(rightPanelWidth + 30, rightPanelWidth));
        jScrollPane.add(legendPanel);
//        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        rightPanel.add(loupe, BorderLayout.NORTH);
        rightPanel.add(jScrollPane, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.LINE_END);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
