package Utility;

import javax.swing.JFrame;

public class PageNavigation {

    // Closes the current window and opens the next one
    public static void switchViews(JFrame currentView, JFrame nextView) {
        if (currentView != null) {
            currentView.dispose();
        }
        if (nextView != null) {
            nextView.setVisible(true);
        }
    }

    // Helper to center a frame on screen
    public static void prepareFrame(JFrame frame) {
        frame.setLocationRelativeTo(null); // Centers the window
        frame.setVisible(true);
    }
}