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

            JLabel label = new JLabel("Choose Location");
            JButton button = new JButton("Address");
            JButton button1 = new JButton("Map");

            JPanel panel = new JPanel();
            panel.add(label);
            panel.add(button);
            panel.add(button1);

            frame.getContentPane().add(panel);

            frame.pack();
            frame.setVisible(true);
        });
    }
}
