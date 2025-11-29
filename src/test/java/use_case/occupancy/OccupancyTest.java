package use_case.occupancy;

import api.BusDataBase;
import entities.Bus;
import entities.Position;
import entities.Route;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class OccupancyTest {

    // Stub for BusDataBase
    private static class BusDataBaseStub implements BusDataBase {
        private final Map<Integer, Bus> buses = new HashMap<>();
        private final Map<Integer, List<Bus>> routeBuses = new HashMap<>();

        public void addBus(Bus bus) {
            buses.put(bus.getId(), bus);
        }

        public void addBusToRoute(int routeId, Bus bus) {
            routeBuses.computeIfAbsent(routeId, k -> new ArrayList<>()).add(bus);
        }

        @Override
        public Bus getBus(int id) {
            return buses.get(id);
        }

        @Override
        public List<Bus> getAllBuses() {
            return new ArrayList<>(buses.values());
        }

        @Override
        public List<Bus> getBusesByRouteId(int routeId) {
            return routeBuses.getOrDefault(routeId, new ArrayList<>());
        }
    }

    // Mock for OccupancyOutputBoundary
    private static class OccupancyOutputBoundaryMock implements OccupancyOutputBoundary {
        private OccupancyOutputData successData;
        private String failMessage;

        @Override
        public void prepareSuccessView(OccupancyOutputData outputData) {
            this.successData = outputData;
        }

        @Override
        public void prepareFailView(String error) {
            this.failMessage = error;
        }

        public OccupancyOutputData getSuccessData() {
            return successData;
        }

        public String getFailMessage() {
            return failMessage;
        }
    }

    @Test
    public void testExecuteSingleBusFoundInRoute() {
        // Arrange
        BusDataBaseStub dbStub = new BusDataBaseStub();
        OccupancyOutputBoundaryMock presenterMock = new OccupancyOutputBoundaryMock();
        OccupancyInteractor interactor = new OccupancyInteractor(dbStub, presenterMock);

        int busId = 101;
        Bus bus = new Bus(busId, new Position(0, 0), "EMPTY");
        Route route = new Route(501);
        route.addBus(bus);

        OccupancyInputData inputData = new OccupancyInputData(busId, route);

        // Act
        interactor.execute(inputData);

        // Assert
        assertNotNull("Success data should not be null", presenterMock.getSuccessData());
        assertEquals("Occupancy should be Empty", "Empty", presenterMock.getSuccessData().getOccupancyLevel());
        assertEquals("Bus ID should match", busId, presenterMock.getSuccessData().getBusId());
    }

    @Test
    public void testExecuteSingleBusFoundInDatabase() {
        // Arrange
        BusDataBaseStub dbStub = new BusDataBaseStub();
        OccupancyOutputBoundaryMock presenterMock = new OccupancyOutputBoundaryMock();
        OccupancyInteractor interactor = new OccupancyInteractor(dbStub, presenterMock);

        int busId = 102;
        Bus bus = new Bus(busId, new Position(0, 0), "FULL");
        dbStub.addBus(bus);

        Route route = new Route(501); // Bus not in route object
        OccupancyInputData inputData = new OccupancyInputData(busId, route);

        // Act
        interactor.execute(inputData);

        // Assert
        assertNotNull("Success data should not be null", presenterMock.getSuccessData());
        assertEquals("Occupancy should be Full", "Full", presenterMock.getSuccessData().getOccupancyLevel());
        assertEquals("Bus ID should match", busId, presenterMock.getSuccessData().getBusId());
    }

    @Test
    public void testExecuteSingleBusNotFound() {
        // Arrange
        BusDataBaseStub dbStub = new BusDataBaseStub();
        OccupancyOutputBoundaryMock presenterMock = new OccupancyOutputBoundaryMock();
        OccupancyInteractor interactor = new OccupancyInteractor(dbStub, presenterMock);

        int busId = 999;
        Route route = new Route(501);
        OccupancyInputData inputData = new OccupancyInputData(busId, route);

        // Act
        interactor.execute(inputData);

        // Assert
        assertNull("Success data should be null", presenterMock.getSuccessData());
        assertNotNull("Fail message should not be null", presenterMock.getFailMessage());
        assertTrue("Fail message should contain bus ID",
                presenterMock.getFailMessage().contains(String.valueOf(busId)));
    }

    @Test
    public void testExecuteRouteSuccess() {
        // Arrange
        BusDataBaseStub dbStub = new BusDataBaseStub();
        OccupancyOutputBoundaryMock presenterMock = new OccupancyOutputBoundaryMock();
        OccupancyInteractor interactor = new OccupancyInteractor(dbStub, presenterMock);

        int routeId = 505;
        Bus bus1 = new Bus(201, new Position(0, 0), "FEW_SEATS_AVAILABLE");
        Bus bus2 = new Bus(202, new Position(0, 0), "MANY_SEATS_AVAILABLE");
        dbStub.addBusToRoute(routeId, bus1);
        dbStub.addBusToRoute(routeId, bus2);

        Route route = new Route(routeId);
        OccupancyInputData inputData = new OccupancyInputData(route);

        // Act
        interactor.execute(inputData);

        // Assert
        assertNotNull("Success data should not be null", presenterMock.getSuccessData());
        Map<Integer, String> occupancies = presenterMock.getSuccessData().getBusOccupancies();
        assertEquals("Should have 2 buses", 2, occupancies.size());
        assertEquals("Bus 201 should be Almost Full", "Almost Full", occupancies.get(201));
        assertEquals("Bus 202 should be Full", "Full", occupancies.get(202));
        assertEquals("Route object should be passed", route, presenterMock.getSuccessData().getRoute());
    }

    @Test
    public void testExecuteRouteNoBuses() {
        // Arrange
        BusDataBaseStub dbStub = new BusDataBaseStub();
        OccupancyOutputBoundaryMock presenterMock = new OccupancyOutputBoundaryMock();
        OccupancyInteractor interactor = new OccupancyInteractor(dbStub, presenterMock);

        int routeId = 506;
        Route route = new Route(routeId);
        OccupancyInputData inputData = new OccupancyInputData(route);

        // Act
        interactor.execute(inputData);

        // Assert
        assertNull("Success data should be null", presenterMock.getSuccessData());
        assertNotNull("Fail message should not be null", presenterMock.getFailMessage());
        assertTrue("Fail message should mention route",
                presenterMock.getFailMessage().contains(String.valueOf(routeId)));
    }
}
