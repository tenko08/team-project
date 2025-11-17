package app;

import entities.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

import entities.Route;

public class MainFrame {


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AppBuilder appBuilder = new AppBuilder();
            JFrame app = appBuilder
                    .addMapView()
                    .build();
            app.setMinimumSize(new java.awt.Dimension(300, 200));
            app.pack();
            app.setLocationRelativeTo(null);
            app.setVisible(true);
        });
        List<Route> routes = new ArrayList<Route>();
        Route route929 = new Route(929);
        routes.add(route929);

    }
}
