package use_case.search_by_route;

import entities.Bus;
import entities.Position;
import entities.Route;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class SearchByRouteOutputDataTest {

    private static Bus createTestBus(int id) {
        Position position = new Position(43.6532, -79.3832, 45.0f, 10.0f);
        return new Bus(id, position, "MANY_SEATS_AVAILABLE");
    }

    @Test
    public void testConstructorSuccessCase() {
        // Arrange
        Route route = new Route(36);
        List<Bus> buses = Arrays.asList(createTestBus(1001), createTestBus(1002));

        // Act
        SearchByRouteOutputData outputData = new SearchByRouteOutputData(
                true, route, buses, null, false
        );

        // Assert
        assertTrue(outputData.isSuccess());
        assertEquals(route, outputData.getRoute());
        assertEquals(buses, outputData.getBuses());
        assertNull(outputData.getErrorMessage());
        assertFalse(outputData.isCached());
    }

    @Test
    public void testConstructorFailureCase() {
        // Arrange
        String errorMessage = "Route not found";

        // Act
        SearchByRouteOutputData outputData = new SearchByRouteOutputData(
                false, null, null, errorMessage, false
        );

        // Assert
        assertFalse(outputData.isSuccess());
        assertNull(outputData.getRoute());
        assertNull(outputData.getBuses());
        assertEquals(errorMessage, outputData.getErrorMessage());
        assertFalse(outputData.isCached());
    }

    @Test
    public void testConstructorCachedCase() {
        // Arrange
        Route route = new Route(501);
        List<Bus> buses = Arrays.asList(createTestBus(2001));

        // Act
        SearchByRouteOutputData outputData = new SearchByRouteOutputData(
                true, route, buses, null, true
        );

        // Assert
        assertTrue(outputData.isSuccess());
        assertEquals(route, outputData.getRoute());
        assertEquals(buses, outputData.getBuses());
        assertTrue(outputData.isCached());
    }

    @Test
    public void testConstructorWithEmptyBusList() {
        // Arrange
        Route route = new Route(99);
        List<Bus> emptyBuses = new ArrayList<>();

        // Act
        SearchByRouteOutputData outputData = new SearchByRouteOutputData(
                true, route, emptyBuses, null, false
        );

        // Assert
        assertTrue(outputData.isSuccess());
        assertEquals(route, outputData.getRoute());
        assertNotNull(outputData.getBuses());
        assertEquals(0, outputData.getBuses().size());
    }

    @Test
    public void testConstructorWithNullBuses() {
        // Arrange
        Route route = new Route(32);

        // Act
        SearchByRouteOutputData outputData = new SearchByRouteOutputData(
                true, route, null, null, false
        );

        // Assert
        assertTrue(outputData.isSuccess());
        assertEquals(route, outputData.getRoute());
        assertNull(outputData.getBuses());
    }

    @Test
    public void testConstructorWithNullRoute() {
        // Arrange
        List<Bus> buses = Arrays.asList(createTestBus(3001));

        // Act
        SearchByRouteOutputData outputData = new SearchByRouteOutputData(
                false, null, buses, "Route not found", false
        );

        // Assert
        assertFalse(outputData.isSuccess());
        assertNull(outputData.getRoute());
        assertEquals(buses, outputData.getBuses());
        assertEquals("Route not found", outputData.getErrorMessage());
    }

    @Test
    public void testConstructorWithLargeBusList() {
        // Arrange
        Route route = new Route(95);
        List<Bus> buses = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            buses.add(createTestBus(4000 + i));
        }

        // Act
        SearchByRouteOutputData outputData = new SearchByRouteOutputData(
                true, route, buses, null, false
        );

        // Assert
        assertTrue(outputData.isSuccess());
        assertEquals(20, outputData.getBuses().size());
        assertEquals(route, outputData.getRoute());
    }

    @Test
    public void testConstructorWithBusesWithoutPosition() {
        // Arrange
        Route route = new Route(29);
        Bus busWithoutPosition = new Bus(5001, null, "FEW_SEATS_AVAILABLE");
        List<Bus> buses = Arrays.asList(busWithoutPosition);

        // Act
        SearchByRouteOutputData outputData = new SearchByRouteOutputData(
                true, route, buses, null, false
        );

        // Assert
        assertTrue(outputData.isSuccess());
        assertEquals(1, outputData.getBuses().size());
        assertNull(outputData.getBuses().get(0).getPosition());
    }

    @Test
    public void testAllGetters() {
        // Arrange
        Route route = new Route(36);
        List<Bus> buses = Arrays.asList(createTestBus(1001));
        String errorMessage = "Test error";

        // Act
        SearchByRouteOutputData outputData = new SearchByRouteOutputData(
                true, route, buses, errorMessage, true
        );

        // Assert
        assertTrue(outputData.isSuccess());
        assertEquals(route, outputData.getRoute());
        assertEquals(buses, outputData.getBuses());
        assertEquals(errorMessage, outputData.getErrorMessage());
        assertTrue(outputData.isCached());
    }

    @Test
    public void testImmutability() {
        // Arrange
        Route route = new Route(36);
        List<Bus> buses = Arrays.asList(createTestBus(1001));
        SearchByRouteOutputData outputData = new SearchByRouteOutputData(
                true, route, buses, null, false
        );

        // Act - get values multiple times
        boolean success1 = outputData.isSuccess();
        boolean success2 = outputData.isSuccess();
        Route route1 = outputData.getRoute();
        Route route2 = outputData.getRoute();

        // Assert - values should be consistent
        assertEquals(success1, success2);
        assertEquals(route1, route2);
    }
}

