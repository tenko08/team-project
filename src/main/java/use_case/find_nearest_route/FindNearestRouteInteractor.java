package use_case.find_nearest_route;

import entities.BusStop;
import entities.Position;
import entities.Route;
import use_case.search_by_route.SearchByRouteInputBoundary;
import use_case.search_by_route.SearchByRouteInputData;

import java.util.List;

public class FindNearestRouteInteractor implements FindNearestRouteInputBoundary {

    private final FindNearestRouteDataAccessInterface dataAccess;
    private final FindNearestRouteOutputBoundary presenter;
    private final SearchByRouteInputBoundary searchByRouteInputBoundary;

    public FindNearestRouteInteractor(FindNearestRouteDataAccessInterface dataAccess,
                                      FindNearestRouteOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
        this.searchByRouteInputBoundary = null;
    }

    public FindNearestRouteInteractor(FindNearestRouteDataAccessInterface dataAccess,
                                      FindNearestRouteOutputBoundary presenter,
                                      SearchByRouteInputBoundary searchByRouteInputBoundary) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
        this.searchByRouteInputBoundary = searchByRouteInputBoundary;
    }

    @Override
    public void execute(FindNearestRouteInputData inputData) {
        Position p = inputData.getPosition();
        List<Route> routes = dataAccess.getAllRoutes();
//        System.out.println(routes);
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
//            System.out.println(stops);
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
        searchByRouteInputBoundary.execute(new SearchByRouteInputData(String.valueOf(closestRoute.getRouteNumber())));
    }
}
