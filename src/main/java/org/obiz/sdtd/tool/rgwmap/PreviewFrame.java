package org.obiz.sdtd.tool.rgwmap;

//import javafx.scene.layout.Pane;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.Map;

public class PreviewFrame extends JFrame {

//    private final int rightPanelWidth = 400;

    public PreviewFrame(BufferedImage img, String mapFolder) throws HeadlessException {
        super("Map preview (" + mapFolder + ")");
        setSize(905, 905);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        ImagePanel imagePanel = new ImagePanel(img);
        add(imagePanel, BorderLayout.CENTER);
//        MapMagnifierPanel loupe = new MapMagnifierPanel(imagePanel);

//        loupe.setPreferredSize(new Dimension(rightPanelWidth, rightPanelWidth));

        //Container for Map magnifier and icons legend
        //Set NORTH panel
//        Panel northPanel = new Panel();
//        northPanel.setLayout(new BorderLayout());
//        northPanel.add(loupe, BorderLayout.WEST);
        //Set RIGHT panel
//        Panel rightPanel = new Panel();
//        rightPanel.setLayout(new BorderLayout());
//        rightPanel.add(northPanel, BorderLayout.NORTH);
//        rightPanel.add(legendScrollPanel, BorderLayout.CENTER);

//        add(rightPanel, BorderLayout.LINE_END);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
