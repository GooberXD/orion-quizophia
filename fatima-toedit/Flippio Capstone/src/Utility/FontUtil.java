package Utility;

import java.awt.*;

public final class FontUtil {
    private static volatile boolean registered = false;

    private FontUtil() {}

    private static void ensureRegistered() {
        if (registered) return;
        synchronized (FontUtil.class) {
            if (registered) return;
            try {
                // Register Montserrat Bold if available on classpath or file system
                Font mont = ResourceLoader.loadFont("Montserrat-Bold.ttf", 14f, new Font("SansSerif", Font.PLAIN, 14));
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(mont.deriveFont(14f));
            } catch (Exception ignored) {
            } finally {
                registered = true;
            }
        }
    }

    public static Font montserrat(float size, int style, Font fallback) {
        ensureRegistered();
        Font base = ResourceLoader.loadFont("Montserrat-Bold.ttf", size, fallback);
        return base.deriveFont(style, size);
    }

    public static void applyToTree(java.awt.Container root, Font font) {
        if (root == null || font == null) return;
        applyFontRecursive(root, font);
        root.revalidate();
        root.repaint();
    }

    private static void applyFontRecursive(java.awt.Component comp, Font font) {
        try {
            comp.setFont(font);
        } catch (Exception ignored) {}
        if (comp instanceof java.awt.Container) {
            for (java.awt.Component child : ((java.awt.Container) comp).getComponents()) {
                applyFontRecursive(child, font);
            }
        }
    }
}
