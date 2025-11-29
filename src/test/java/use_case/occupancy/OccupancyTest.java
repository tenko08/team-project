package use_case.occupancy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import api.BusDataBase;
import entities.Bus;
import entities.Position;
import entities.Route;

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

    @Test
    public void testExecuteInvalidInputData() {
        // Arrange
        BusDataBaseStub dbStub = new BusDataBaseStub();
        OccupancyOutputBoundaryMock presenterMock = new OccupancyOutputBoundaryMock();
        OccupancyInteractor interactor = new OccupancyInteractor(dbStub, presenterMock);

        // Create input data with busId == -1 and route == null using reflection
        OccupancyInputData inputData = new OccupancyInputData(-1, null);

        // Act
        interactor.execute(inputData);

        // Assert
        assertNull("Success data should be null", presenterMock.getSuccessData());
        assertNotNull("Fail message should not be null", presenterMock.getFailMessage());
        assertEquals("Fail message should be 'Invalid input data'", "Invalid input data", presenterMock.getFailMessage());
    }

    @Test
    public void testExecuteSingleBusWithNullOccupancy() {
        // Arrange
        BusDataBaseStub dbStub = new BusDataBaseStub();
        OccupancyOutputBoundaryMock presenterMock = new OccupancyOutputBoundaryMock();
        OccupancyInteractor interactor = new OccupancyInteractor(dbStub, presenterMock);

        int busId = 103;
        Bus bus = new Bus(busId, new Position(0, 0), null);
        dbStub.addBus(bus);

        Route route = new Route(501);
        OccupancyInputData inputData = new OccupancyInputData(busId, route);

        // Act
        interactor.execute(inputData);

        // Assert
        assertNull("Success data should be null", presenterMock.getSuccessData());
        assertNotNull("Fail message should not be null", presenterMock.getFailMessage());
        assertTrue("Fail message should mention no occupancy data",
                presenterMock.getFailMessage().contains("No occupancy data available"));
        assertTrue("Fail message should contain bus ID",
                presenterMock.getFailMessage().contains(String.valueOf(busId)));
    }

    @Test
    public void testExecuteSingleBusWithEmptyOccupancy() {
        // Arrange
        BusDataBaseStub dbStub = new BusDataBaseStub();
        OccupancyOutputBoundaryMock presenterMock = new OccupancyOutputBoundaryMock();
        OccupancyInteractor interactor = new OccupancyInteractor(dbStub, presenterMock);

        int busId = 104;
        Bus bus = new Bus(busId, new Position(0, 0), "");
        dbStub.addBus(bus);

        Route route = new Route(501);
        OccupancyInputData inputData = new OccupancyInputData(busId, route);

        // Act
        interactor.execute(inputData);

        // Assert
        assertNull("Success data should be null", presenterMock.getSuccessData());
        assertNotNull("Fail message should not be null", presenterMock.getFailMessage());
        assertTrue("Fail message should mention no occupancy data",
                presenterMock.getFailMessage().contains("No occupancy data available"));
    }

    @Test
    public void testExecuteSingleBusWithUnknownOccupancy() {
        // Arrange
        BusDataBaseStub dbStub = new BusDataBaseStub();
        OccupancyOutputBoundaryMock presenterMock = new OccupancyOutputBoundaryMock();
        OccupancyInteractor interactor = new OccupancyInteractor(dbStub, presenterMock);

        int busId = 105;
        Bus bus = new Bus(busId, new Position(0, 0), "UNKNOWN");
        dbStub.addBus(bus);

        Route route = new Route(501);
        OccupancyInputData inputData = new OccupancyInputData(busId, route);

        // Act
        interactor.execute(inputData);

        // Assert
        assertNull("Success data should be null", presenterMock.getSuccessData());
        assertNotNull("Fail message should not be null", presenterMock.getFailMessage());
        assertTrue("Fail message should mention no occupancy data",
                presenterMock.getFailMessage().contains("No occupancy data available"));
    }

    @Test
    public void testExecuteSingleBusWithUnknownOccupancyStatus() {
        // Arrange
        BusDataBaseStub dbStub = new BusDataBaseStub();
        OccupancyOutputBoundaryMock presenterMock = new OccupancyOutputBoundaryMock();
        OccupancyInteractor interactor = new OccupancyInteractor(dbStub, presenterMock);

        int busId = 106;
        Bus bus = new Bus(busId, new Position(0, 0), "SOME_UNKNOWN_STATUS");
        dbStub.addBus(bus);

        Route route = new Route(501);
        OccupancyInputData inputData = new OccupancyInputData(busId, route);

        // Act
        interactor.execute(inputData);

        // Assert
        assertNull("Success data should be null", presenterMock.getSuccessData());
        assertNotNull("Fail message should not be null", presenterMock.getFailMessage());
        assertTrue("Fail message should mention unknown occupancy status",
                presenterMock.getFailMessage().contains("Unknown occupancy status"));
        assertTrue("Fail message should contain the status",
                presenterMock.getFailMessage().contains("SOME_UNKNOWN_STATUS"));
    }

    @Test
    public void testExecuteSingleBusWithLowercaseOccupancy() {
        // Arrange
        BusDataBaseStub dbStub = new BusDataBaseStub();
        OccupancyOutputBoundaryMock presenterMock = new OccupancyOutputBoundaryMock();
        OccupancyInteractor interactor = new OccupancyInteractor(dbStub, presenterMock);

        int busId = 107;
        Bus bus = new Bus(busId, new Position(0, 0), "empty");
        dbStub.addBus(bus);

        Route route = new Route(501);
        OccupancyInputData inputData = new OccupancyInputData(busId, route);

        // Act
        interactor.execute(inputData);

        // Assert
        assertNotNull("Success data should not be null", presenterMock.getSuccessData());
        assertEquals("Occupancy should be Empty", "Empty", presenterMock.getSuccessData().getOccupancyLevel());
    }

    @Test
    public void testExecuteSingleBusWithMixedCaseOccupancy() {
        // Arrange
        BusDataBaseStub dbStub = new BusDataBaseStub();
        OccupancyOutputBoundaryMock presenterMock = new OccupancyOutputBoundaryMock();
        OccupancyInteractor interactor = new OccupancyInteractor(dbStub, presenterMock);

        int busId = 108;
        Bus bus = new Bus(busId, new Position(0, 0), "FeW_SeAtS_AvAiLaBlE");
        dbStub.addBus(bus);

        Route route = new Route(501);
        OccupancyInputData inputData = new OccupancyInputData(busId, route);

        // Act
        interactor.execute(inputData);

        // Assert
        assertNotNull("Success data should not be null", presenterMock.getSuccessData());
        assertEquals("Occupancy should be Almost Full", "Almost Full", presenterMock.getSuccessData().getOccupancyLevel());
    }

    @Test
    public void testExecuteSingleBusWithWhitespaceOccupancy() {
        // Arrange
        BusDataBaseStub dbStub = new BusDataBaseStub();
        OccupancyOutputBoundaryMock presenterMock = new OccupancyOutputBoundaryMock();
        OccupancyInteractor interactor = new OccupancyInteractor(dbStub, presenterMock);

        int busId = 109;
        Bus bus = new Bus(busId, new Position(0, 0), "  FULL  ");
        dbStub.addBus(bus);

        Route route = new Route(501);
        OccupancyInputData inputData = new OccupancyInputData(busId, route);

        // Act
        interactor.execute(inputData);

        // Assert
        assertNotNull("Success data should not be null", presenterMock.getSuccessData());
        assertEquals("Occupancy should be Full", "Full", presenterMock.getSuccessData().getOccupancyLevel());
    }

    @Test
    public void testExecuteRouteWithNullOccupancyBuses() {
        // Arrange
        BusDataBaseStub dbStub = new BusDataBaseStub();
        OccupancyOutputBoundaryMock presenterMock = new OccupancyOutputBoundaryMock();
        OccupancyInteractor interactor = new OccupancyInteractor(dbStub, presenterMock);

        int routeId = 507;
        Bus bus1 = new Bus(301, new Position(0, 0), null);
        Bus bus2 = new Bus(302, new Position(0, 0), "UNKNOWN_STATUS");
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
        assertEquals("Bus 301 should be Unknown", "Unknown", occupancies.get(301));
        assertEquals("Bus 302 should be Unknown", "Unknown", occupancies.get(302));
    }

    @Test
    public void testExecuteRouteWithMixedOccupancyBuses() {
        // Arrange
        BusDataBaseStub dbStub = new BusDataBaseStub();
        OccupancyOutputBoundaryMock presenterMock = new OccupancyOutputBoundaryMock();
        OccupancyInteractor interactor = new OccupancyInteractor(dbStub, presenterMock);

        int routeId = 508;
        Bus bus1 = new Bus(401, new Position(0, 0), "EMPTY");
        Bus bus2 = new Bus(402, new Position(0, 0), null);
        Bus bus3 = new Bus(403, new Position(0, 0), "FULL");
        dbStub.addBusToRoute(routeId, bus1);
        dbStub.addBusToRoute(routeId, bus2);
        dbStub.addBusToRoute(routeId, bus3);

        Route route = new Route(routeId);
        OccupancyInputData inputData = new OccupancyInputData(route);

        // Act
        interactor.execute(inputData);

        // Assert
        assertNotNull("Success data should not be null", presenterMock.getSuccessData());
        Map<Integer, String> occupancies = presenterMock.getSuccessData().getBusOccupancies();
        assertEquals("Should have 3 buses", 3, occupancies.size());
        assertEquals("Bus 401 should be Empty", "Empty", occupancies.get(401));
        assertEquals("Bus 402 should be Unknown", "Unknown", occupancies.get(402));
        assertEquals("Bus 403 should be Full", "Full", occupancies.get(403));
    }

    @Test
    public void testExecuteSingleBusWithNullRoute() {
        // Arrange
        BusDataBaseStub dbStub = new BusDataBaseStub();
        OccupancyOutputBoundaryMock presenterMock = new OccupancyOutputBoundaryMock();
        OccupancyInteractor interactor = new OccupancyInteractor(dbStub, presenterMock);

        int busId = 110;
        Bus bus = new Bus(busId, new Position(0, 0), "EMPTY");
        dbStub.addBus(bus);

        OccupancyInputData inputData = new OccupancyInputData(busId, null);

        // Act
        interactor.execute(inputData);

        // Assert
        assertNotNull("Success data should not be null", presenterMock.getSuccessData());
        assertEquals("Occupancy should be Empty", "Empty", presenterMock.getSuccessData().getOccupancyLevel());
        assertEquals("Bus ID should match", busId, presenterMock.getSuccessData().getBusId());
    }

    // Helper method to create a Route with null busList for testing
    private Route createRouteWithNullBusList(int routeNumber) {
        Route route = new Route(routeNumber);
        // Use reflection to set busList to null for testing
        try {
            java.lang.reflect.Field field = Route.class.getDeclaredField("busList");
            field.setAccessible(true);
            field.set(route, null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set busList to null for testing", e);
        }
        return route;
    }

    @Test
    public void testExecuteSingleBusWithRouteHavingNullBusList() {
        // Arrange
        BusDataBaseStub dbStub = new BusDataBaseStub();
        OccupancyOutputBoundaryMock presenterMock = new OccupancyOutputBoundaryMock();
        OccupancyInteractor interactor = new OccupancyInteractor(dbStub, presenterMock);

        int busId = 111;
        Bus bus = new Bus(busId, new Position(0, 0), "FULL");
        dbStub.addBus(bus);

        Route route = createRouteWithNullBusList(501);
        OccupancyInputData inputData = new OccupancyInputData(busId, route);

        // Act
        interactor.execute(inputData);

        // Assert
        assertNotNull("Success data should not be null", presenterMock.getSuccessData());
        assertEquals("Occupancy should be Full", "Full", presenterMock.getSuccessData().getOccupancyLevel());
        assertEquals("Bus ID should match", busId, presenterMock.getSuccessData().getBusId());
    }

    @Test
    public void testExecuteSingleBusWithRouteContainingDifferentBus() {
        // Arrange
        BusDataBaseStub dbStub = new BusDataBaseStub();
        OccupancyOutputBoundaryMock presenterMock = new OccupancyOutputBoundaryMock();
        OccupancyInteractor interactor = new OccupancyInteractor(dbStub, presenterMock);

        int busId = 112;
        Bus bus = new Bus(busId, new Position(0, 0), "FEW_SEATS_AVAILABLE");
        dbStub.addBus(bus);

        Route route = new Route(501);
        Bus otherBus = new Bus(999, new Position(0, 0), "EMPTY");
        route.addBus(otherBus); // Add a different bus to the route

        OccupancyInputData inputData = new OccupancyInputData(busId, route);

        // Act
        interactor.execute(inputData);

        // Assert
        assertNotNull("Success data should not be null", presenterMock.getSuccessData());
        assertEquals("Occupancy should be Almost Full", "Almost Full", presenterMock.getSuccessData().getOccupancyLevel());
        assertEquals("Bus ID should match", busId, presenterMock.getSuccessData().getBusId());
    }

    @Test
    public void testExecuteSingleBusWithFullOccupancyExplicit() {
        // Arrange - Test the "FULL" case explicitly (separate from MANY_SEATS_AVAILABLE)
        BusDataBaseStub dbStub = new BusDataBaseStub();
        OccupancyOutputBoundaryMock presenterMock = new OccupancyOutputBoundaryMock();
        OccupancyInteractor interactor = new OccupancyInteractor(dbStub, presenterMock);

        int busId = 113;
        Bus bus = new Bus(busId, new Position(0, 0), "FULL");
        dbStub.addBus(bus);

        Route route = new Route(501);
        OccupancyInputData inputData = new OccupancyInputData(busId, route);

        // Act
        interactor.execute(inputData);

        // Assert
        assertNotNull("Success data should not be null", presenterMock.getSuccessData());
        assertEquals("Occupancy should be Full", "Full", presenterMock.getSuccessData().getOccupancyLevel());
        assertEquals("Bus ID should match", busId, presenterMock.getSuccessData().getBusId());
    }

    @Test
    public void testExecuteRouteWithEmptyOccupancyString() {
        // Arrange - Test route handling with empty string occupancy
        BusDataBaseStub dbStub = new BusDataBaseStub();
        OccupancyOutputBoundaryMock presenterMock = new OccupancyOutputBoundaryMock();
        OccupancyInteractor interactor = new OccupancyInteractor(dbStub, presenterMock);

        int routeId = 509;
        Bus bus1 = new Bus(501, new Position(0, 0), "");
        Bus bus2 = new Bus(502, new Position(0, 0), "EMPTY");
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
        assertEquals("Bus 501 should be Unknown", "Unknown", occupancies.get(501));
        assertEquals("Bus 502 should be Empty", "Empty", occupancies.get(502));
    }
}
