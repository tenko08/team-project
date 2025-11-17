package use_case.find_nearest_route;

import entities.BusStop;
import entities.Position;
import entities.Route;

import java.util.List;

public class FindNearestRouteInteractor implements FindNearestRouteInputBoundary {

    private final FindNearestRouteDataAccessInterface dataAccess;
    private final FindNearestRouteOutputBoundary presenter;

    public FindNearestRouteInteractor(FindNearestRouteDataAccessInterface dataAccess,
                                         FindNearestRouteOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(FindNearestRouteInputData inputData) {

        Position p = inputData.getPosition();
        List<Route> routes = dataAccess.getAllRoutes();

        if (routes.isEmpty()) {
            presenter.prepareFailView("No routes available.");
            return;
        }

        Route closestRoute = null;
        BusStop closestStop = null;
        double minDistance = Double.MAX_VALUE;

        for (Route route : routes) {
            List<BusStop> stops = route.getBusStopList();
            if (stops == null || stops.isEmpty())
                continue;

            for (BusStop stop : stops) {
                double dist = p.distanceTo(stop.getPosition());
                if (dist < minDistance) {
                    minDistance = dist;
                    closestRoute = route;
                    closestStop = stop;
                }
            }
        }

        if (closestRoute == null) {
            presenter.prepareFailView("No bus stops found on any route.");
            return;
        }

        FindNearestRouteOutputData outputData =
                new FindNearestRouteOutputData(
                        closestRoute,
                        closestStop,
                        minDistance
                );

        presenter.prepareSuccessView(outputData);
    }
}
