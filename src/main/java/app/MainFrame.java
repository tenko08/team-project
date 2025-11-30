package app;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import entities.Bus;
import entities.BusStop;
import entities.Position;
import entities.Route;
import entities.Trip;


public class MainFrame {


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AppBuilder appBuilder = new AppBuilder();
            JFrame app = appBuilder
                    .addMapView()
                    .addMapUseCase()
                    .addFindNearestRouteView()
                    .addFindNearestRouteUseCase()
                    .addAlertsView()
                    .addSearchByRouteUseCase()
                    .addSearchByRouteView()
                    .addBusScheduleUseCase()
                    .addBusScheduleView()
                    .addOccupancyUseCase()
                    .addOccupancyView()
                    .build();
            app.setMinimumSize(new java.awt.Dimension(300, 200));
            app.pack();
            app.setLocationRelativeTo(null);
            app.setVisible(true);
        });
        }
}
