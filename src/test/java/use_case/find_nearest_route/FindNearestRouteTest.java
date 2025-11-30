package use_case.find_nearest_route;

import entities.BusStop;
import entities.Position;
import entities.Route;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class FindNearestRouteTest {

    // Mock Data Access
    private static class DataAccessStub implements FindNearestRouteDataAccessInterface {
        private final List<Route> routes = new ArrayList<>();

        public void addRoute(Route route) {
            routes.add(route);
        }

        @Override
        public List<Route> getAllRoutes() {
            return routes;
        }
    }

    // Mock Presenter
    private static class PresenterMock implements FindNearestRouteOutputBoundary {
        private FindNearestRouteOutputData successData;
        private String failMessage;

        @Override
        public void prepareSuccessView(FindNearestRouteOutputData outputData) {
            this.successData = outputData;
        }

        @Override
        public void prepareFailView(String error) {
            this.failMessage = error;
        }

        public FindNearestRouteOutputData getSuccessData() {
            return successData;
        }

        public String getFailMessage() {
            return failMessage;
        }
    }

    @Test
    public void successDifferentRoutesTest() {
        // Arrange
        Position userPos = new Position(0, 0);

        // Route 1, position (6, 8) so distance of 10
        Route r1 = new Route(1);
        r1.addBusStop(new BusStop(101, 1, "StopA", new Position(6, 8)));

        // Route 2, position (3,4) so distance is 5
        Route r2 = new Route(2);
        r2.addBusStop(new BusStop(201, 1, "StopB", new Position(3, 4)));

        DataAccessStub db = new DataAccessStub();
        db.addRoute(r1);
        db.addRoute(r2);

        PresenterMock presenter = new PresenterMock();
        FindNearestRouteInteractor interactor = new FindNearestRouteInteractor(db, presenter);

        FindNearestRouteInputData inputData = new FindNearestRouteInputData(userPos);

        // Act
        interactor.execute(inputData);

        // Assert
        assertNotNull(presenter.getSuccessData());
        assertEquals(2, presenter.getSuccessData().getRoute().getRouteNumber());
        assertEquals("StopB", presenter.getSuccessData().getBusStop().getName());
        assertEquals(5, presenter.getSuccessData().getDistance(), 1e-6);
        assertNull(presenter.getFailMessage());
    }

    @Test
    public void successSameRouteDifferentBusStopsTest() {
        // Arrange
        Position userPos = new Position(0, 0);

        // 1 route,  position (6, 8) so distance of 10
        Route r1 = new Route(1);
        r1.addBusStop(new BusStop(101, 1, "StopA", new Position(6, 8)));
        r1.addBusStop(new BusStop(201, 1, "StopB", new Position(3, 4)));

        DataAccessStub db = new DataAccessStub();
        db.addRoute(r1);

        PresenterMock presenter = new PresenterMock();
        FindNearestRouteInteractor interactor = new FindNearestRouteInteractor(db, presenter);

        FindNearestRouteInputData inputData = new FindNearestRouteInputData(userPos);

        // Act
        interactor.execute(inputData);

        // Assert
        assertNotNull(presenter.getSuccessData());
        assertEquals(1, presenter.getSuccessData().getRoute().getRouteNumber());
        assertEquals("StopB", presenter.getSuccessData().getBusStop().getName());
        assertEquals(5, presenter.getSuccessData().getDistance(), 1e-6);
        assertNull(presenter.getFailMessage());
    }

    @Test
    public void failureNoRoutesTest() {
        // Arrange
        DataAccessStub db = new DataAccessStub();
        PresenterMock presenter = new PresenterMock();
        FindNearestRouteInteractor interactor = new FindNearestRouteInteractor(db, presenter);

        Position userPos = new Position(0, 0);
        FindNearestRouteInputData inputData = new FindNearestRouteInputData(userPos);

        // Act
        interactor.execute(inputData);

        // Assert
        assertNull(presenter.getSuccessData());
        assertEquals("No routes available.", presenter.getFailMessage());
    }

    @Test
    public void failureNoStopsOnAnyRouteTest() {
        // Arrange
        Route r1 = new Route(1);

        DataAccessStub db = new DataAccessStub();
        db.addRoute(r1);

        PresenterMock presenter = new PresenterMock();
        FindNearestRouteInteractor interactor = new FindNearestRouteInteractor(db, presenter);

        Position userPos = new Position(0, 0);
        FindNearestRouteInputData inputData = new FindNearestRouteInputData(userPos);

        // Act
        interactor.execute(inputData);

        // Assert
        assertNull(presenter.getSuccessData());
        assertEquals("No bus stops found on any route.", presenter.getFailMessage());
    }
}
