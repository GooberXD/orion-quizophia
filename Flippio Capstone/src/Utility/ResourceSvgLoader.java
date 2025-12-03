package Utility;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URI;

/**
 * Attempts to render SVGs using SVG Salamander at runtime via reflection.
 * If the library is not present, returns null so callers can fallback.
 */
public final class ResourceSvgLoader {
    private ResourceSvgLoader() {}

    public static ImageIcon loadSvgAsIcon(String resourcePath, int targetW, int targetH) {
        try (InputStream is = openSvgStream(resourcePath)) {
            if (is == null) return null;

            // Load classes reflectively to avoid compile-time dependency
            Class<?> universeCls = Class.forName("com.kitfox.svg.SVGUniverse");
            Object universe = universeCls.getConstructor().newInstance();

            // URI uri = universe.loadSVG(is, resourcePath)
            Method loadSvg = universeCls.getMethod("loadSVG", InputStream.class, String.class);
            Object uriObj = loadSvg.invoke(universe, is, resourcePath);
            if (!(uriObj instanceof URI)) {
                // Some versions may return java.net.URI; if not, try toString->URI
                uriObj = new URI(String.valueOf(uriObj));
            }

            // diagram = universe.getDiagram(uri)
            Method getDiagram = universeCls.getMethod("getDiagram", URI.class);
            Object diagram = getDiagram.invoke(universe, (URI) uriObj);
            if (diagram == null) return null;

            // Obtain view bounds
            Class<?> diagramCls = diagram.getClass();
            Method getViewRect = null;
            try {
                getViewRect = diagramCls.getMethod("getViewRect");
            } catch (NoSuchMethodException ignored) {}

            java.awt.geom.Rectangle2D bounds;
            if (getViewRect != null) {
                Object rect = getViewRect.invoke(diagram);
                if (rect instanceof java.awt.geom.Rectangle2D) {
                    bounds = (java.awt.geom.Rectangle2D) rect;
                } else {
                    bounds = new java.awt.geom.Rectangle2D.Double(0, 0, targetW, targetH);
                }
            } else {
                bounds = new java.awt.geom.Rectangle2D.Double(0, 0, targetW, targetH);
            }

            if (targetW <= 0 || targetH <= 0) {
                targetW = (int) Math.max(1, bounds.getWidth());
                targetH = (int) Math.max(1, bounds.getHeight());
            }

            BufferedImage img = new BufferedImage(targetW, targetH, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = img.createGraphics();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                double sx = targetW / bounds.getWidth();
                double sy = targetH / bounds.getHeight();
                double s = Math.min(sx, sy);
                double dx = (targetW - bounds.getWidth() * s) / 2.0;
                double dy = (targetH - bounds.getHeight() * s) / 2.0;
                g2.translate(dx - bounds.getX() * s, dy - bounds.getY() * s);
                g2.scale(s, s);

                // diagram.render(g2)
                Method render = diagramCls.getMethod("render", Graphics2D.class);
                render.invoke(diagram, g2);
            } finally {
                g2.dispose();
            }

            return new ImageIcon(img);
        } catch (Throwable t) {
            // Library not present or render failed
            return null;
        }
    }

    private static InputStream openSvgStream(String resourcePath) {
        try {
            InputStream is = ResourceLoader.openClasspath(resourcePath);
            if (is != null) return is;
        } catch (Exception ignored) {}
        try {
            java.io.File f1 = new java.io.File(resourcePath);
            if (f1.exists()) return new java.io.FileInputStream(f1);
        } catch (Exception ignored) {}
        try {
            java.io.File f2 = new java.io.File("Resources", resourcePath);
            if (f2.exists()) return new java.io.FileInputStream(f2);
        } catch (Exception ignored) {}
        return null;
    }
}
