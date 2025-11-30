package view;

import interface_adapter.ViewManagerModel;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * Simple landing page shown before navigating to map or address search.
 */
public class LandingView extends JPanel {
    private final String viewName = "landing";
    private final ViewManagerModel viewManagerModel;
    private final String addressViewName;
    private final String mapViewName;

    public LandingView(ViewManagerModel viewManagerModel, String addressViewName, String mapViewName) {
        super(new BorderLayout());
        this.viewManagerModel = viewManagerModel;
        this.addressViewName = addressViewName;
        this.mapViewName = mapViewName;

        // Soften the background slightly for contrast with the card
        setBackground(new Color(245, 247, 250));

        // Container to center the card
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);

        // Create a "card" panel for nicer look
        JPanel card = new JPanel();
        card.setOpaque(true);
        card.setBackground(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        // Card border: subtle drop shadow + rounded line + inner padding
        // Outer shadow using matte border (bottom/right heavier)
        Border shadow = BorderFactory.createMatteBorder(1, 1, 6, 6, new Color(225, 229, 235));
        // Middle rounded line border
        Border rounded = BorderFactory.createLineBorder(new Color(210, 215, 222), 1, true);
        // Inner padding
        Border padding = BorderFactory.createEmptyBorder(24, 28, 24, 28);
        card.setBorder(BorderFactory.createCompoundBorder(shadow,
                BorderFactory.createCompoundBorder(rounded, padding)));

        // Title
        JLabel title = new JLabel("Find your route!");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 28f));
        title.setForeground(new Color(33, 37, 41));

        // Subtitle for a bit more context
        JLabel subtitle = new JLabel("Choose how you want to start");
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setFont(subtitle.getFont().deriveFont(Font.PLAIN, 14f));
        subtitle.setForeground(new Color(100, 110, 120));

        // Buttons row
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        buttons.setOpaque(false);

        JButton addressBtn = new JButton("Address");
        JButton mapBtn = new JButton("Map");

        Dimension btnSize = new Dimension(130, 36);
        addressBtn.setPreferredSize(btnSize);
        mapBtn.setPreferredSize(btnSize);

        // Slightly accent colors for buttons
        addressBtn.setBackground(new Color(52, 120, 246));
        addressBtn.setForeground(Color.WHITE);
        addressBtn.setFocusPainted(false);
        mapBtn.setBackground(new Color(108, 117, 125));
        mapBtn.setForeground(Color.WHITE);
        mapBtn.setFocusPainted(false);

        // Navigation actions
        addressBtn.addActionListener(e -> {
            viewManagerModel.setState(addressViewName);
            viewManagerModel.firePropertyChange();
        });
        mapBtn.addActionListener(e -> {
            viewManagerModel.setState(mapViewName);
            viewManagerModel.firePropertyChange();
        });

        buttons.add(addressBtn);
        buttons.add(mapBtn);

        // Assemble card
        card.add(Box.createVerticalStrut(6));
        card.add(title);
        card.add(Box.createVerticalStrut(6));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(16));
        card.add(buttons);

        // Keep card at a reasonable width
        card.setMaximumSize(new Dimension(480, 220));

        // Center the card
        centerWrapper.add(card, new GridBagConstraints());

        add(centerWrapper, BorderLayout.CENTER);
    }

    public String getViewName() {
        return viewName;
    }
}
