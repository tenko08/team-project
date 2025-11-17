package app;

import javax.swing.*;


public class MainFrame {
    private static final String TITLE = "Real-Time TTC Map Viewer";
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame(TITLE);
            frame.setMinimumSize(new java.awt.Dimension(300, 200));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        });
    }
}
