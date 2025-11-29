package app;

import entities.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;


public class MainFrame {


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AppBuilder appBuilder = new AppBuilder();
            JFrame app = appBuilder
                    .addMapView()
                    .addMapUseCase()
                    .addFindNearestRouteView()
                    .addFindNearestRouteUseCase()
                    .addAlertsUseCase()
                    .addAlertsView()
                    .addSearchByRouteUseCase()
                    .addSearchByRouteView()
                    .addBusScheduleUseCase()
                    .addBusScheduleView()
                    .build();
            app.setMinimumSize(new java.awt.Dimension(300, 200));
            app.pack();
            app.setLocationRelativeTo(null);
            app.setVisible(true);
        });
    }
}
