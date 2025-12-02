package use_case.search_by_route;

import entities.Bus;
import entities.Position;
import entities.Route;
import interface_adapter.search_by_route.SearchByRouteGateway;
import org.junit.Test;
import use_case.map.MapInputBoundary;
import use_case.map.RouteShapeDataAccessInterface;

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
            return responseToReturn != null ? responseToReturn : new HashMap<>();
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

    // Mock RouteShapeDataAccessInterface
    private static class RouteShapeDataAccessMock implements RouteShapeDataAccessInterface {
        private boolean hasRouteResult = false;
        private java.util.ArrayList<String> branchesResult = new java.util.ArrayList<>();

        public void setHasRouteResult(boolean result) {
            this.hasRouteResult = result;
        }

        public void setBranchesResult(java.util.ArrayList<String> branches) {
            this.branchesResult = branches;
        }

        @Override
        public entities.RouteShape getShapeById(String routeId) {
            return null;
        }

        @Override
        public boolean hasRoute(int id) {
            return hasRouteResult;
        }

        @Override
        public java.util.ArrayList<String> getListOfBranches(int routeNumber) {
            return branchesResult;
        }
    }

    // Mock MapInputBoundary
    private static class MapInputBoundaryMock implements MapInputBoundary {
        private String lastRouteShown;
        private boolean showRouteCalled = false;

        public String getLastRouteShown() {
            return lastRouteShown;
        }

        public boolean isShowRouteCalled() {
            return showRouteCalled;
        }

        @Override
        public void showRoute(String routeNumber) {
            this.lastRouteShown = routeNumber;
            this.showRouteCalled = true;
        }

        @Override
        public void markWaypoint(use_case.map.MapInputData mapInputData) {
            // Not used in these tests
        }

        @Override
        public void setMapViewer(org.jxmapviewer.JXMapViewer mapViewer) {
            // Not used in these tests
        }

        @Override
        public void setFindNearestRouteOutputBoundary(use_case.find_nearest_route.FindNearestRouteOutputBoundary findNearestRoutePresenter) {
            // Not used in these tests
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
        RouteShapeDataAccessMock routeShapeDataAccess = new RouteShapeDataAccessMock();
        MapInputBoundaryMock mapInputBoundary = new MapInputBoundaryMock();
        SearchByRouteInteractor interactor = new SearchByRouteInteractor(gateway, presenter, routeShapeDataAccess, mapInputBoundary);

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
        assertTrue(mapInputBoundary.isShowRouteCalled());
        assertEquals("36", mapInputBoundary.getLastRouteShown());
    }

    @Test
    public void testExecuteSuccessWithCachedData() {
        // Arrange
        SearchByRouteGatewayMock gateway = new SearchByRouteGatewayMock();
        SearchByRoutePresenterMock presenter = new SearchByRoutePresenterMock();
        RouteShapeDataAccessMock routeShapeDataAccess = new RouteShapeDataAccessMock();
        MapInputBoundaryMock mapInputBoundary = new MapInputBoundaryMock();
        SearchByRouteInteractor interactor = new SearchByRouteInteractor(gateway, presenter, routeShapeDataAccess, mapInputBoundary);

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
        assertTrue(mapInputBoundary.isShowRouteCalled());
        assertEquals("501", mapInputBoundary.getLastRouteShown());
    }

    @Test
    public void testExecuteSuccessWithEmptyBusList() {
        // Arrange
        SearchByRouteGatewayMock gateway = new SearchByRouteGatewayMock();
        SearchByRoutePresenterMock presenter = new SearchByRoutePresenterMock();
        RouteShapeDataAccessMock routeShapeDataAccess = new RouteShapeDataAccessMock();
        MapInputBoundaryMock mapInputBoundary = new MapInputBoundaryMock();
        SearchByRouteInteractor interactor = new SearchByRouteInteractor(gateway, presenter, routeShapeDataAccess, mapInputBoundary);

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
        assertTrue(mapInputBoundary.isShowRouteCalled());
        assertEquals("99", mapInputBoundary.getLastRouteShown());
    }

    @Test
    public void testExecuteFailureRouteNotFound() {
        // Arrange
        SearchByRouteGatewayMock gateway = new SearchByRouteGatewayMock();
        SearchByRoutePresenterMock presenter = new SearchByRoutePresenterMock();
        RouteShapeDataAccessMock routeShapeDataAccess = new RouteShapeDataAccessMock();
        routeShapeDataAccess.setHasRouteResult(false);
        MapInputBoundaryMock mapInputBoundary = new MapInputBoundaryMock();
        SearchByRouteInteractor interactor = new SearchByRouteInteractor(gateway, presenter, routeShapeDataAccess, mapInputBoundary);

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
        assertFalse(mapInputBoundary.isShowRouteCalled());
    }

    @Test
    public void testExecuteFailureEmptyRouteNumber() {
        // Arrange
        SearchByRouteGatewayMock gateway = new SearchByRouteGatewayMock();
        SearchByRoutePresenterMock presenter = new SearchByRoutePresenterMock();
        RouteShapeDataAccessMock routeShapeDataAccess = new RouteShapeDataAccessMock();
        MapInputBoundaryMock mapInputBoundary = new MapInputBoundaryMock();
        SearchByRouteInteractor interactor = new SearchByRouteInteractor(gateway, presenter, routeShapeDataAccess, mapInputBoundary);

        SearchByRouteInputData inputData = new SearchByRouteInputData("");

        // Act
        interactor.execute(inputData);

        // Assert
        assertFalse(presenter.isSuccessViewCalled());
        assertFalse(presenter.isCachedViewCalled());
        assertTrue(presenter.isFailViewCalled());
        assertEquals("Route number cannot be empty", presenter.getFailMessage());
        assertFalse(mapInputBoundary.isShowRouteCalled());
    }

    @Test
    public void testExecuteFailureNullRouteNumber() {
        // Arrange
        SearchByRouteGatewayMock gateway = new SearchByRouteGatewayMock();
        SearchByRoutePresenterMock presenter = new SearchByRoutePresenterMock();
        RouteShapeDataAccessMock routeShapeDataAccess = new RouteShapeDataAccessMock();
        MapInputBoundaryMock mapInputBoundary = new MapInputBoundaryMock();
        SearchByRouteInteractor interactor = new SearchByRouteInteractor(gateway, presenter, routeShapeDataAccess, mapInputBoundary);

        SearchByRouteInputData inputData = new SearchByRouteInputData(null);

        // Act
        interactor.execute(inputData);

        // Assert
        assertFalse(presenter.isSuccessViewCalled());
        assertFalse(presenter.isCachedViewCalled());
        assertTrue(presenter.isFailViewCalled());
        assertEquals("Route number cannot be empty", presenter.getFailMessage());
        assertFalse(mapInputBoundary.isShowRouteCalled());
    }

    @Test
    public void testExecuteFailureWhitespaceRouteNumber() {
        // Arrange
        SearchByRouteGatewayMock gateway = new SearchByRouteGatewayMock();
        SearchByRoutePresenterMock presenter = new SearchByRoutePresenterMock();
        RouteShapeDataAccessMock routeShapeDataAccess = new RouteShapeDataAccessMock();
        MapInputBoundaryMock mapInputBoundary = new MapInputBoundaryMock();
        SearchByRouteInteractor interactor = new SearchByRouteInteractor(gateway, presenter, routeShapeDataAccess, mapInputBoundary);

        SearchByRouteInputData inputData = new SearchByRouteInputData("   ");

        // Act
        interactor.execute(inputData);

        // Assert
        assertFalse(presenter.isSuccessViewCalled());
        assertFalse(presenter.isCachedViewCalled());
        assertTrue(presenter.isFailViewCalled());
        assertEquals("Route number cannot be empty", presenter.getFailMessage());
        assertFalse(mapInputBoundary.isShowRouteCalled());
    }

    @Test
    public void testExecuteFailureGatewayException() {
        // Arrange
        SearchByRouteGatewayMock gateway = new SearchByRouteGatewayMock();
        SearchByRoutePresenterMock presenter = new SearchByRoutePresenterMock();
        RouteShapeDataAccessMock routeShapeDataAccess = new RouteShapeDataAccessMock();
        MapInputBoundaryMock mapInputBoundary = new MapInputBoundaryMock();
        SearchByRouteInteractor interactor = new SearchByRouteInteractor(gateway, presenter, routeShapeDataAccess, mapInputBoundary);

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
        assertFalse(mapInputBoundary.isShowRouteCalled());
    }

    @Test
    public void testExecuteFailureMissingSuccessKey() {
        // Arrange
        SearchByRouteGatewayMock gateway = new SearchByRouteGatewayMock();
        SearchByRoutePresenterMock presenter = new SearchByRoutePresenterMock();
        RouteShapeDataAccessMock routeShapeDataAccess = new RouteShapeDataAccessMock();
        routeShapeDataAccess.setHasRouteResult(false);
        MapInputBoundaryMock mapInputBoundary = new MapInputBoundaryMock();
        SearchByRouteInteractor interactor = new SearchByRouteInteractor(gateway, presenter, routeShapeDataAccess, mapInputBoundary);

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
        assertFalse(mapInputBoundary.isShowRouteCalled());
    }

    @Test
    public void testExecuteSuccessWithMultipleBuses() {
        // Arrange
        SearchByRouteGatewayMock gateway = new SearchByRouteGatewayMock();
        SearchByRoutePresenterMock presenter = new SearchByRoutePresenterMock();
        RouteShapeDataAccessMock routeShapeDataAccess = new RouteShapeDataAccessMock();
        MapInputBoundaryMock mapInputBoundary = new MapInputBoundaryMock();
        SearchByRouteInteractor interactor = new SearchByRouteInteractor(gateway, presenter, routeShapeDataAccess, mapInputBoundary);

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
        assertTrue(mapInputBoundary.isShowRouteCalled());
        assertEquals("95", mapInputBoundary.getLastRouteShown());
    }

    @Test
    public void testExecuteSuccessWithBusesWithoutPosition() {
        // Arrange
        SearchByRouteGatewayMock gateway = new SearchByRouteGatewayMock();
        SearchByRoutePresenterMock presenter = new SearchByRoutePresenterMock();
        RouteShapeDataAccessMock routeShapeDataAccess = new RouteShapeDataAccessMock();
        MapInputBoundaryMock mapInputBoundary = new MapInputBoundaryMock();
        SearchByRouteInteractor interactor = new SearchByRouteInteractor(gateway, presenter, routeShapeDataAccess, mapInputBoundary);

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
        assertTrue(mapInputBoundary.isShowRouteCalled());
        assertEquals("29", mapInputBoundary.getLastRouteShown());
    }

    @Test
    public void testExecuteFailureMissingMessageKey() {
        // Arrange
        SearchByRouteGatewayMock gateway = new SearchByRouteGatewayMock();
        SearchByRoutePresenterMock presenter = new SearchByRoutePresenterMock();
        RouteShapeDataAccessMock routeShapeDataAccess = new RouteShapeDataAccessMock();
        routeShapeDataAccess.setHasRouteResult(false);
        MapInputBoundaryMock mapInputBoundary = new MapInputBoundaryMock();
        SearchByRouteInteractor interactor = new SearchByRouteInteractor(gateway, presenter, routeShapeDataAccess, mapInputBoundary);

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
        assertFalse(mapInputBoundary.isShowRouteCalled());
    }

    @Test
    public void testExecuteSuccessWithNullBusesList() {
        // Arrange
        SearchByRouteGatewayMock gateway = new SearchByRouteGatewayMock();
        SearchByRoutePresenterMock presenter = new SearchByRoutePresenterMock();
        RouteShapeDataAccessMock routeShapeDataAccess = new RouteShapeDataAccessMock();
        MapInputBoundaryMock mapInputBoundary = new MapInputBoundaryMock();
        SearchByRouteInteractor interactor = new SearchByRouteInteractor(gateway, presenter, routeShapeDataAccess, mapInputBoundary);

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
        assertTrue(mapInputBoundary.isShowRouteCalled());
        assertEquals("32", mapInputBoundary.getLastRouteShown());
    }

    @Test
    public void testExecuteFailureRouteExistsButNoBuses() {
        // Arrange
        SearchByRouteGatewayMock gateway = new SearchByRouteGatewayMock();
        SearchByRoutePresenterMock presenter = new SearchByRoutePresenterMock();
        RouteShapeDataAccessMock routeShapeDataAccess = new RouteShapeDataAccessMock();
        routeShapeDataAccess.setHasRouteResult(true); // Route exists but no buses
        MapInputBoundaryMock mapInputBoundary = new MapInputBoundaryMock();
        SearchByRouteInteractor interactor = new SearchByRouteInteractor(gateway, presenter, routeShapeDataAccess, mapInputBoundary);

        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "No buses found");

        gateway.setResponse(response);

        SearchByRouteInputData inputData = new SearchByRouteInputData("36");

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(presenter.isFailViewCalled());
        assertEquals("No buses running at this time.", presenter.getFailMessage());
        assertTrue(mapInputBoundary.isShowRouteCalled());
        assertEquals("36", mapInputBoundary.getLastRouteShown());
    }
}

