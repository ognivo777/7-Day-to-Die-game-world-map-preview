package org.obiz.sdtd.tool.rgwmap;

//import javafx.scene.layout.Pane;

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
//        ButtonPanel buttons = new ButtonPanel();
        LegendPanel legendPanel = new LegendPanel(icons, loupe);

        JScrollPane legendScrollPanel = new JScrollPane(legendPanel);
        legendScrollPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        legendScrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        legendScrollPanel.setBounds(0, 0, rightPanelWidth, 900);

        loupe.setPreferredSize(new Dimension((int)(rightPanelWidth * 1),(int)(rightPanelWidth * 1)));
//        buttons.setPreferredSize(new Dimension((int)(rightPanelWidth * 0.3), (int)(rightPanelWidth * 0.3)));

        //Container for Map magnifier and icons legend
        //Set NORTH panel
        Panel northPanel = new Panel();
        northPanel.setLayout(new BorderLayout());
        northPanel.add(loupe, BorderLayout.WEST);
//        northPanel.add(buttons, BorderLayout.EAST);
        //Set RIGHT panel
        Panel rightPanel = new Panel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.add(northPanel, BorderLayout.NORTH);
        rightPanel.add(legendScrollPanel, BorderLayout.CENTER);

        add(rightPanel, BorderLayout.LINE_END);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
