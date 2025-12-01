package use_case.search_by_route;

import entities.Bus;
import entities.Position;
import entities.Route;
import interface_adapter.search_by_route.SearchByRouteGateway;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class SearchByRouteInteractorTest {

    // Mock Gateway implementation
    private static class SearchByRouteGatewayMock implements SearchByRouteGateway {
        private Map<String, Object> responseToReturn;
        private RuntimeException exceptionToThrow;

        public void setResponse(Map<String, Object> response) {
            this.responseToReturn = response;
        }

        public void setExceptionToThrow(RuntimeException exception) {
            this.exceptionToThrow = exception;
        }

        @Override
        public Map<String, Object> getBusesByRoute(String routeNumber) {
            if (exceptionToThrow != null) {
                throw exceptionToThrow;
            }
            return responseToReturn;
        }
    }

    // Mock Presenter
    private static class SearchByRoutePresenterMock implements SearchByRouteOutputBoundary {
        private SearchByRouteOutputData successData;
        private SearchByRouteOutputData cachedData;
        private String failMessage;
        private boolean successViewCalled;
        private boolean cachedViewCalled;
        private boolean failViewCalled;

        @Override
        public void prepareSuccessView(SearchByRouteOutputData outputData) {
            this.successData = outputData;
            this.successViewCalled = true;
        }

        @Override
        public void prepareCachedView(SearchByRouteOutputData outputData) {
            this.cachedData = outputData;
            this.cachedViewCalled = true;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            this.failMessage = errorMessage;
            this.failViewCalled = true;
        }

        public SearchByRouteOutputData getSuccessData() {
            return successData;
        }

        public SearchByRouteOutputData getCachedData() {
            return cachedData;
        }

        public String getFailMessage() {
            return failMessage;
        }

        public boolean isSuccessViewCalled() {
            return successViewCalled;
        }

        public boolean isCachedViewCalled() {
            return cachedViewCalled;
        }

        public boolean isFailViewCalled() {
            return failViewCalled;
        }

        public void reset() {
            successData = null;
            cachedData = null;
            failMessage = null;
            successViewCalled = false;
            cachedViewCalled = false;
            failViewCalled = false;
        }
    }

    private static Bus createTestBus(int id, double lat, double lon) {
        Position position = new Position(lat, lon, 45.0f, 10.0f);
        return new Bus(id, position, "MANY_SEATS_AVAILABLE");
    }

    @Test
    public void testExecuteSuccessWithBuses() {
        // Arrange
        SearchByRouteGatewayMock gateway = new SearchByRouteGatewayMock();
        SearchByRoutePresenterMock presenter = new SearchByRoutePresenterMock();
        SearchByRouteInteractor interactor = new SearchByRouteInteractor(gateway, presenter);

        Route route = new Route(36);
        List<Bus> buses = Arrays.asList(
                createTestBus(1001, 43.6532, -79.3832),
                createTestBus(1002, 43.6540, -79.3840)
        );

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("route", route);
        response.put("buses", buses);
        response.put("cached", false);

        gateway.setResponse(response);

        SearchByRouteInputData inputData = new SearchByRouteInputData("36");

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(presenter.isSuccessViewCalled());
        assertFalse(presenter.isCachedViewCalled());
        assertFalse(presenter.isFailViewCalled());

        SearchByRouteOutputData outputData = presenter.getSuccessData();
        assertNotNull(outputData);
        assertTrue(outputData.isSuccess());
        assertEquals(route, outputData.getRoute());
        assertEquals(buses, outputData.getBuses());
        assertNull(outputData.getErrorMessage());
        assertFalse(outputData.isCached());
    }

    @Test
    public void testExecuteSuccessWithCachedData() {
        // Arrange
        SearchByRouteGatewayMock gateway = new SearchByRouteGatewayMock();
        SearchByRoutePresenterMock presenter = new SearchByRoutePresenterMock();
        SearchByRouteInteractor interactor = new SearchByRouteInteractor(gateway, presenter);

        Route route = new Route(501);
        List<Bus> buses = Arrays.asList(createTestBus(2001, 43.6500, -79.3800));

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("route", route);
        response.put("buses", buses);
        response.put("cached", true);

        gateway.setResponse(response);

        SearchByRouteInputData inputData = new SearchByRouteInputData("501");

        // Act
        interactor.execute(inputData);

        // Assert
        assertFalse(presenter.isSuccessViewCalled());
        assertTrue(presenter.isCachedViewCalled());
        assertFalse(presenter.isFailViewCalled());

        SearchByRouteOutputData outputData = presenter.getCachedData();
        assertNotNull(outputData);
        assertTrue(outputData.isSuccess());
        assertEquals(route, outputData.getRoute());
        assertEquals(buses, outputData.getBuses());
        assertTrue(outputData.isCached());
    }

    @Test
    public void testExecuteSuccessWithEmptyBusList() {
        // Arrange
        SearchByRouteGatewayMock gateway = new SearchByRouteGatewayMock();
        SearchByRoutePresenterMock presenter = new SearchByRoutePresenterMock();
        SearchByRouteInteractor interactor = new SearchByRouteInteractor(gateway, presenter);

        Route route = new Route(99);
        List<Bus> emptyBuses = new ArrayList<>();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("route", route);
        response.put("buses", emptyBuses);
        response.put("cached", false);

        gateway.setResponse(response);

        SearchByRouteInputData inputData = new SearchByRouteInputData("99");

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(presenter.isSuccessViewCalled());
        SearchByRouteOutputData outputData = presenter.getSuccessData();
        assertNotNull(outputData);
        assertTrue(outputData.isSuccess());
        assertEquals(route, outputData.getRoute());
        assertNotNull(outputData.getBuses());
        assertEquals(0, outputData.getBuses().size());
    }

    @Test
    public void testExecuteFailureRouteNotFound() {
        // Arrange
        SearchByRouteGatewayMock gateway = new SearchByRouteGatewayMock();
        SearchByRoutePresenterMock presenter = new SearchByRoutePresenterMock();
        SearchByRouteInteractor interactor = new SearchByRouteInteractor(gateway, presenter);

        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Route not found");

        gateway.setResponse(response);

        SearchByRouteInputData inputData = new SearchByRouteInputData("999");

        // Act
        interactor.execute(inputData);

        // Assert
        assertFalse(presenter.isSuccessViewCalled());
        assertFalse(presenter.isCachedViewCalled());
        assertTrue(presenter.isFailViewCalled());
        assertEquals("Route not found", presenter.getFailMessage());
    }

    @Test
    public void testExecuteFailureEmptyRouteNumber() {
        // Arrange
        SearchByRouteGatewayMock gateway = new SearchByRouteGatewayMock();
        SearchByRoutePresenterMock presenter = new SearchByRoutePresenterMock();
        SearchByRouteInteractor interactor = new SearchByRouteInteractor(gateway, presenter);

        SearchByRouteInputData inputData = new SearchByRouteInputData("");

        // Act
        interactor.execute(inputData);

        // Assert
        assertFalse(presenter.isSuccessViewCalled());
        assertFalse(presenter.isCachedViewCalled());
        assertTrue(presenter.isFailViewCalled());
        assertEquals("Route number cannot be empty", presenter.getFailMessage());
    }

    @Test
    public void testExecuteFailureNullRouteNumber() {
        // Arrange
        SearchByRouteGatewayMock gateway = new SearchByRouteGatewayMock();
        SearchByRoutePresenterMock presenter = new SearchByRoutePresenterMock();
        SearchByRouteInteractor interactor = new SearchByRouteInteractor(gateway, presenter);

        SearchByRouteInputData inputData = new SearchByRouteInputData(null);

        // Act
        interactor.execute(inputData);

        // Assert
        assertFalse(presenter.isSuccessViewCalled());
        assertFalse(presenter.isCachedViewCalled());
        assertTrue(presenter.isFailViewCalled());
        assertEquals("Route number cannot be empty", presenter.getFailMessage());
    }

    @Test
    public void testExecuteFailureWhitespaceRouteNumber() {
        // Arrange
        SearchByRouteGatewayMock gateway = new SearchByRouteGatewayMock();
        SearchByRoutePresenterMock presenter = new SearchByRoutePresenterMock();
        SearchByRouteInteractor interactor = new SearchByRouteInteractor(gateway, presenter);

        SearchByRouteInputData inputData = new SearchByRouteInputData("   ");

        // Act
        interactor.execute(inputData);

        // Assert
        assertFalse(presenter.isSuccessViewCalled());
        assertFalse(presenter.isCachedViewCalled());
        assertTrue(presenter.isFailViewCalled());
        assertEquals("Route number cannot be empty", presenter.getFailMessage());
    }

    @Test
    public void testExecuteFailureGatewayException() {
        // Arrange
        SearchByRouteGatewayMock gateway = new SearchByRouteGatewayMock();
        SearchByRoutePresenterMock presenter = new SearchByRoutePresenterMock();
        SearchByRouteInteractor interactor = new SearchByRouteInteractor(gateway, presenter);

        gateway.setExceptionToThrow(new RuntimeException("Network error"));

        SearchByRouteInputData inputData = new SearchByRouteInputData("36");

        // Act
        interactor.execute(inputData);

        // Assert
        assertFalse(presenter.isSuccessViewCalled());
        assertFalse(presenter.isCachedViewCalled());
        assertTrue(presenter.isFailViewCalled());
        assertTrue(presenter.getFailMessage().contains("System error"));
        assertTrue(presenter.getFailMessage().contains("Network error"));
    }

    @Test
    public void testExecuteFailureMissingSuccessKey() {
        // Arrange
        SearchByRouteGatewayMock gateway = new SearchByRouteGatewayMock();
        SearchByRoutePresenterMock presenter = new SearchByRoutePresenterMock();
        SearchByRouteInteractor interactor = new SearchByRouteInteractor(gateway, presenter);

        Map<String, Object> response = new HashMap<>();
        // Missing "success" key
        response.put("route", new Route(36));

        gateway.setResponse(response);

        SearchByRouteInputData inputData = new SearchByRouteInputData("36");

        // Act
        interactor.execute(inputData);

        // Assert
        assertFalse(presenter.isSuccessViewCalled());
        assertFalse(presenter.isCachedViewCalled());
        assertTrue(presenter.isFailViewCalled());
        assertEquals("Route not found", presenter.getFailMessage());
    }

    @Test
    public void testExecuteSuccessWithMultipleBuses() {
        // Arrange
        SearchByRouteGatewayMock gateway = new SearchByRouteGatewayMock();
        SearchByRoutePresenterMock presenter = new SearchByRoutePresenterMock();
        SearchByRouteInteractor interactor = new SearchByRouteInteractor(gateway, presenter);

        Route route = new Route(95);
        List<Bus> buses = Arrays.asList(
                createTestBus(3001, 43.6532, -79.3832),
                createTestBus(3002, 43.6540, -79.3840),
                createTestBus(3003, 43.6550, -79.3850),
                createTestBus(3004, 43.6560, -79.3860)
        );

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("route", route);
        response.put("buses", buses);
        response.put("cached", false);

        gateway.setResponse(response);

        SearchByRouteInputData inputData = new SearchByRouteInputData("95");

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(presenter.isSuccessViewCalled());
        SearchByRouteOutputData outputData = presenter.getSuccessData();
        assertNotNull(outputData);
        assertEquals(4, outputData.getBuses().size());
        assertEquals(route, outputData.getRoute());
    }

    @Test
    public void testExecuteSuccessWithBusesWithoutPosition() {
        // Arrange
        SearchByRouteGatewayMock gateway = new SearchByRouteGatewayMock();
        SearchByRoutePresenterMock presenter = new SearchByRoutePresenterMock();
        SearchByRouteInteractor interactor = new SearchByRouteInteractor(gateway, presenter);

        Route route = new Route(29);
        Bus busWithoutPosition = new Bus(4001, null, "FEW_SEATS_AVAILABLE");
        List<Bus> buses = Arrays.asList(busWithoutPosition);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("route", route);
        response.put("buses", buses);
        response.put("cached", false);

        gateway.setResponse(response);

        SearchByRouteInputData inputData = new SearchByRouteInputData("29");

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(presenter.isSuccessViewCalled());
        SearchByRouteOutputData outputData = presenter.getSuccessData();
        assertNotNull(outputData);
        assertEquals(1, outputData.getBuses().size());
        assertNull(outputData.getBuses().get(0).getPosition());
    }

    @Test
    public void testExecuteFailureWithCustomErrorMessage() {
        // Arrange
        SearchByRouteGatewayMock gateway = new SearchByRouteGatewayMock();
        SearchByRoutePresenterMock presenter = new SearchByRoutePresenterMock();
        SearchByRouteInteractor interactor = new SearchByRouteInteractor(gateway, presenter);

        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Invalid route format");

        gateway.setResponse(response);

        SearchByRouteInputData inputData = new SearchByRouteInputData("abc");

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(presenter.isFailViewCalled());
        assertEquals("Invalid route format", presenter.getFailMessage());
    }

    @Test
    public void testExecuteFailureMissingMessageKey() {
        // Arrange
        SearchByRouteGatewayMock gateway = new SearchByRouteGatewayMock();
        SearchByRoutePresenterMock presenter = new SearchByRoutePresenterMock();
        SearchByRouteInteractor interactor = new SearchByRouteInteractor(gateway, presenter);

        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        // Missing "message" key

        gateway.setResponse(response);

        SearchByRouteInputData inputData = new SearchByRouteInputData("999");

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(presenter.isFailViewCalled());
        assertEquals("Route not found", presenter.getFailMessage()); // Default message
    }

    @Test
    public void testExecuteSuccessWithNullBusesList() {
        // Arrange
        SearchByRouteGatewayMock gateway = new SearchByRouteGatewayMock();
        SearchByRoutePresenterMock presenter = new SearchByRoutePresenterMock();
        SearchByRouteInteractor interactor = new SearchByRouteInteractor(gateway, presenter);

        Route route = new Route(32);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("route", route);
        response.put("buses", null);
        response.put("cached", false);

        gateway.setResponse(response);

        SearchByRouteInputData inputData = new SearchByRouteInputData("32");

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(presenter.isSuccessViewCalled());
        SearchByRouteOutputData outputData = presenter.getSuccessData();
        assertNotNull(outputData);
        assertNull(outputData.getBuses());
    }
}

