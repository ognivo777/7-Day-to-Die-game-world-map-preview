package org.obiz.sdtd.tool.rgwmap;

import javafx.scene.layout.Pane;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.Map;

public class PreviewFrame extends JFrame {

    private final int rightPanelWidth = 400;

    public PreviewFrame(BufferedImage img, Map<String, Path> icons, String mapFolder) throws HeadlessException {
        super("Map preview (" + mapFolder + ")");
        setSize(905 + rightPanelWidth, 905);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        ImagePanel imagePanel = new ImagePanel(img);
        add(imagePanel, BorderLayout.CENTER);
        MapMagnifierPanel loupe = new MapMagnifierPanel(imagePanel);
        ButtonPanel buttons = new ButtonPanel();
        LegendPanel legendPanel = new LegendPanel(icons, loupe);

        //Container for Map magnifier and icons legend
        Panel rightPanel = new Panel();
        Panel northPanel = new Panel();
        rightPanel.setLayout(new BorderLayout());
        northPanel.setLayout(new BorderLayout());

        ScrollPane jScrollPane = new ScrollPane();
        loupe.setPreferredSize(new Dimension((int)(rightPanelWidth * 0.7),(int)(rightPanelWidth * 0.7)));
        buttons.setPreferredSize(new Dimension((int)(rightPanelWidth * 0.3), (int)(rightPanelWidth * 0.3)));
        legendPanel.setPreferredSize(new Dimension(rightPanelWidth, 20*icons.size()/3));
        jScrollPane.setPreferredSize(new Dimension(rightPanelWidth + 30, rightPanelWidth));
        jScrollPane.add(legendPanel);
//        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        northPanel.add(loupe, BorderLayout.WEST);
        northPanel.add(buttons, BorderLayout.EAST);
        rightPanel.add(northPanel, BorderLayout.NORTH);
        rightPanel.add(jScrollPane, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.LINE_END);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
