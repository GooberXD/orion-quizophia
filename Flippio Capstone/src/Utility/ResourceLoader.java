package Utility;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URL;

/**
 * Centralized helper to load resources from the classpath under "/Resources" with
 * a safe fallback to the working directory when running from IDE.
 */
public final class ResourceLoader {
    private ResourceLoader() {}

    public static InputStream openClasspath(String relativePath) {
        // Normalize: ensure no leading backslash; always use forward slashes for classpath
        String norm = relativePath.replace("\\", "/");
        if (!norm.startsWith("/")) {
            norm = "/" + norm;
        }
        InputStream is = ResourceLoader.class.getResourceAsStream(norm);
        if (is == null) {
            // Try with "/Resources/" prefix if not already provided
            if (!norm.startsWith("/Resources/")) {
                is = ResourceLoader.class.getResourceAsStream("/Resources/" + relativePath.replace("\\", "/"));
            }
        }
        return is;
    }

    public static Font loadFont(String resourcePath, float size, Font fallback) {
        try (InputStream is = openClasspath(resourcePath)) {
            if (is != null) {
                return Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(size);
            }
        } catch (Exception ignored) {}
        // File-system fallback
        try (InputStream fis = new FileInputStream(resourcePath)) {
            return Font.createFont(Font.TRUETYPE_FONT, fis).deriveFont(size);
        } catch (Exception ignored) {}
        try (InputStream fis = new FileInputStream("Resources" + File.separator + resourcePath)) {
            return Font.createFont(Font.TRUETYPE_FONT, fis).deriveFont(size);
        } catch (Exception ignored) {}
        return fallback;
    }

    public static ImageIcon loadImageIcon(String resourcePath) {
        // Basic guard: Swing ImageIcon cannot render SVG without extra libraries
        String lower = resourcePath.toLowerCase();
        if (lower.endsWith(".svg")) {
            return null; // caller should supply PNG/JPG or handle null
        }
        // Try classpath
        String norm = resourcePath.replace("\\", "/");
        if (!norm.startsWith("/")) norm = "/" + norm;
        URL url = ResourceLoader.class.getResource(norm);
        if (url == null && !norm.startsWith("/Resources/")) {
            url = ResourceLoader.class.getResource("/Resources/" + resourcePath.replace("\\", "/"));
        }
        if (url != null) {
            return new ImageIcon(url);
        }
        // Filesystem fallbacks
        File f1 = new File(resourcePath);
        if (f1.exists()) return new ImageIcon(resourcePath);
        File f2 = new File("Resources", resourcePath);
        if (f2.exists()) return new ImageIcon(f2.getAbsolutePath());
        return null;
    }
}
