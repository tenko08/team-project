package app;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class MainFrame {


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AppBuilder appBuilder = new AppBuilder();
            JFrame app = appBuilder
                    .addMapView()
                    .addMapUseCase()
                    .addSearchByRouteUseCase()
                    .addSearchByRouteView()
                    .addFindNearestRouteView()
                    .addFindNearestRouteUseCase()
                    .addLandingView()
                    .addAlertsView()
                    .addBusScheduleUseCase()
                    .addBusScheduleView()
                    .addOccupancyUseCase()
                    .addOccupancyView()
                    .build();
            app.setMinimumSize(new java.awt.Dimension(300, 200));
            app.pack();
            app.setLocationRelativeTo(null);
            app.setVisible(true);

            app.addWindowListener(new WindowAdapter() {
                //I skipped unused callbacks for readability

                @Override
                public void windowClosing(WindowEvent e) {
                    appBuilder.saveConfig();
                    app.setVisible(false);
                    app.dispose();
                    System.exit(0);
                }
            });
        });
    }
}
