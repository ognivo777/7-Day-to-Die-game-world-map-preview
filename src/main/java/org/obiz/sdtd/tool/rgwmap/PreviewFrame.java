package org.obiz.sdtd.tool.rgwmap;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class PreviewFrame extends JFrame {
    private final JScrollPane scrollPane;
    private ImagePanel image;
    private BufferedImage img;

    public PreviewFrame(BufferedImage img) throws HeadlessException {
        super("Map preview");
        this.img = img;
        setSize(1035, 1055);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        image = new ImagePanel(img);
        scrollPane = new JScrollPane(image);
        add(scrollPane, BorderLayout.CENTER);
    }
}
