package org.obiz.sdtd.tool.rgwmap;

import java.awt.*;
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
        StringBuilder imagesHtml = new StringBuilder();
        try {
            Map<String, Path> stringPathMap = MapBuilder.loadIcons();
            for (String name : stringPathMap.keySet()) {
                Files.readAllLines(stringPathMap.get(name)).forEach(s -> objectsHtml.append(s).append("\n"));
            }

            System.out.println(objectsHtml);
            System.out.println(imagesHtml);

            Path destinationPath = Paths.get("allIcons.html");

            PrintWriter printWriter = new PrintWriter(Files.newBufferedWriter(destinationPath));

            Files.lines(MapBuilder.getPathForResource("/allIconsTemplate.html"))
                    .map(s -> {
                        if (s.equals("$objects$")) return objectsHtml.toString();
                        return s;
                    }).forEach(printWriter::println);
            printWriter.close();

            Desktop.getDesktop().browse(destinationPath.toUri());

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
