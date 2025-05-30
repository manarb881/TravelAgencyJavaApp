package com.travelagency.main;

import com.travelagency.ui.MainAppFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class MainApplication {

    public static void main(String[] args) {
        // Set mysqli error reporting for all DAOs using DatabaseConnection
        // (though it's more for PHP context, in Java, exceptions are standard)
        // No, this line is not for Java: mysqli_report(MYSQLI_REPORT_ERROR | MYSQLI_REPORT_STRICT);

        // Optional: Set a nicer Look and Feel for Swing
        try {
            // Use a modern look and feel if available
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) { // Nimbus is a good cross-platform L&F
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
            // If Nimbus is not found, it will fall back to the default metal L&F or system L&F
        } catch (Exception e) {
            // If an error occurs, use the default L&F
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace(); // Fallback failed too
            }
        }


        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MainAppFrame().setVisible(true);
            }
        });
    }
}