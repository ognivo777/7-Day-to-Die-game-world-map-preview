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

    private static final String OBJECT_TAG = "<object type=\"image/svg+xml\" data=\"icons/%s\"></object>\n";
    private static final String IMAGE_TAG = "<img src=\"icons/%s\" height=\"64px\" title=\"%s\"/>\n";

    public static void main(String[] args) {
        StringBuilder objectsHtml = new StringBuilder();
        StringBuilder imagesHtml = new StringBuilder();
        try {
            Map<String, Path> stringPathMap = MapBuilder.loadIcons();
            for (String name : stringPathMap.keySet()) {
                System.out.println(name + ":" + stringPathMap.get(name).getFileName());
//                objectsHtml.append(String.format(OBJECT_TAG, stringPathMap.get(name).getFileName()));
                Files.readAllLines(stringPathMap.get(name)).forEach(s -> objectsHtml.append(s).append("\n"));

                imagesHtml.append(String.format(IMAGE_TAG, stringPathMap.get(name).getFileName(), name));
            }

            System.out.println(objectsHtml);
            System.out.println(imagesHtml);

            Path destinationPath = Paths.get("allIcons.html");

            PrintWriter printWriter = new PrintWriter(Files.newBufferedWriter(destinationPath));

            Files.lines(MapBuilder.getPathForResource("/allIconsTemplate.html"))
                    .map(s -> {
                        if (s.equals("$objects$")) return objectsHtml.toString();
                        if (s.equals("$images$")) return imagesHtml.toString();
                        return s;
                    }).forEach(printWriter::println);
            printWriter.close();

            Desktop.getDesktop().browse(destinationPath.toUri());

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
