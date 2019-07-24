package org.obiz.sdtd.tool.rgwmap;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class IconsListBuilder {

    public static void main(String[] args) {
        StringBuilder objectsHtml = new StringBuilder();
        try {
            Map<String, Path> stringPathMap = MapBuilder.loadIcons();
            int tableSize = Math.round(Math.round(Math.sqrt(stringPathMap.size())))+1;
            int cellSize = 40;
            BufferedImage icons = new BufferedImage(cellSize *tableSize, cellSize *tableSize, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = icons.createGraphics();
            int count = 0;
            for (String name : stringPathMap.keySet()) {
                Files.readAllLines(stringPathMap.get(name)).forEach(s -> objectsHtml.append(s).append("\n"));
                int x = count % tableSize;
                int y = count / tableSize;
                MapBuilder.drawIcon(g, name, cellSize/2, x* cellSize, y* cellSize, true, stringPathMap,2, false);
                count++;
            }

            Path destinationPath = Paths.get("allIcons.html");

            PrintWriter printWriter = new PrintWriter(Files.newBufferedWriter(destinationPath));

            Files.lines(MapBuilder.getPathForResource("/allIconsTemplate.html"))
                    .map(s -> {
                        if (s.equals("$objects$")) return objectsHtml.toString();
                        return s;
                    }).forEach(printWriter::println);
            printWriter.close();

            Desktop.getDesktop().browse(destinationPath.toUri());
            Thread.sleep(500);
            JFrame frame = new PreviewFrame(icons, stringPathMap);
            frame.setAutoRequestFocus(true);
            frame.setVisible(true);

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
