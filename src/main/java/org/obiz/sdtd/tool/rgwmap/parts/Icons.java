package org.obiz.sdtd.tool.rgwmap.parts;

import com.kitfox.svg.Group;
import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGException;
import com.kitfox.svg.SVGUniverse;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.obiz.sdtd.tool.rgwmap.MapBuilder.getPathForResource;

public class Icons {
    private Map<String, Path> icons;
    private static Map<String, BufferedImage> iconsCache = new HashMap<>();

    public Icons() throws IOException, URISyntaxException {
        icons = loadIcons();
    }

    public Map<String, Path> getIcons() {
        return icons;
    }

    public static Map<String, Path> loadIcons() throws IOException, URISyntaxException {
        Map<String, Path> result = new HashMap<>();
        String resourceName = "/icons";
        Path myPath = getPathForResource(resourceName);
        Stream<Path> walk = Files.walk(myPath, 1);

        walk.forEach(
                next -> {
                    String nextFile = next.getFileName().toString();
                    if(Files.isRegularFile(next) && nextFile.endsWith(".svg")) {
                        nextFile = nextFile.substring(0, nextFile.lastIndexOf("."));
                        result.put(nextFile, next);
                    }
                }
        );
        return result;
    }

    public static void clearIconsCache() {
        iconsCache.clear();
    }

    public static void drawIcon(Graphics gMap, String iconName, int targetSize, int x, int y, boolean showAxis, Map<String, Path> icons, int sizeBufferScale, boolean ignoreScale) {
        drawIcon(gMap, iconName, targetSize, x, y, showAxis, icons, sizeBufferScale, iconsCache, ignoreScale);
    }
    public static void drawIcon(Graphics gMap, String iconName, int targetSize, int x, int y, boolean showAxis, Map<String, Path> icons, int sizeBufferScale, Map<String, BufferedImage> iconsCache, boolean ignoreScale) {
        BufferedImage sprite;
        sprite = iconsCache.get(iconName);
        if(sprite == null) {
            sprite = createSprite(iconName, targetSize, showAxis, icons, sizeBufferScale, ignoreScale);
            iconsCache.put(iconName, sprite);
        }
        gMap.drawImage(sprite, x - targetSize * sizeBufferScale, y - targetSize * sizeBufferScale, null);
    }

    public static BufferedImage imgDrawIcon(String iconName, int targetSize, int x, int y, boolean showAxis, Map<String, Path> icons, int sizeBufferScale, Map<String, BufferedImage> iconsCache, boolean ignoreScale) {
        BufferedImage sprite;
        sprite = iconsCache.get(iconName);
        if(sprite == null) {
            sprite = createSprite(iconName, targetSize, showAxis, icons, sizeBufferScale, ignoreScale);
            iconsCache.put(iconName, sprite);
        }
        return sprite;
    }

    public static BufferedImage createSprite(String name, int width, boolean showAxis, Map<String, Path> icons, int halfSize, boolean ignoreScale) {
        int fullSize = halfSize*2;
        BufferedImage img = new BufferedImage(width* fullSize, width* fullSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        try {
            Path path = icons.get(name);
            SVGUniverse svgUniverse = new SVGUniverse();

            URI uri = svgUniverse.loadSVG(Files.newInputStream(path), path.getFileName().toString());
            SVGDiagram diagram = svgUniverse.getDiagram(uri);
            int svgWidth =  width;
            int svgX = 0;
            int svgY = 0;
            if(ignoreScale) {
                svgWidth = Math.round(width / (diagram.getRoot().getDeviceHeight() / 100));
                AffineTransform xForm = ((Group) diagram.getRoot().getChild(0)).getXForm();
                if(xForm!=null) {
                    int svgViewBoxWidth = diagram.getRoot().getPresAbsolute("viewBox").getIntList()[3];
                    double scale = svgWidth/(svgViewBoxWidth*1.);
                    svgX = (int) -(xForm.getTranslateX() * scale);
                    svgY = (int) -(xForm.getTranslateY() * scale);
                }
            }
            diagram.setDeviceViewport(new Rectangle(svgWidth, svgWidth));
//            diagram.
            Graphics graphics = g.create(svgX, svgY, width* fullSize, width* fullSize);
            if(showAxis) {
                g.setColor(Color.GREEN);
                g.drawLine(0, width* halfSize, width* fullSize -1, width* halfSize);
                g.drawLine(width* halfSize,0, width* halfSize, width* fullSize -1);
            }
            graphics.translate(width* halfSize, width* halfSize);
            diagram.render((Graphics2D) graphics);
            svgUniverse.clear();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SVGException e) {
            e.printStackTrace();
        }
        return img;
    }
}
