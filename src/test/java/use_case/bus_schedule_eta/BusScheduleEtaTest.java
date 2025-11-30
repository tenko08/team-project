package use_case.bus_schedule_eta;
import static org.junit.Assert.*;
import org.junit.Test;

import interface_adapter.bus_schedule_eta.BusScheduleGateway;
import java.util.*;
public class BusScheduleEtaTest {
    // Stub for BusScheduleGateway
    private static class BusScheduleGatewayStub implements BusScheduleGateway {
        private final Map<String, Map<String, Object>> scheduleData = new HashMap<>();
        private final Map<String, Map<String, Object>> etaData = new HashMap<>();
        private boolean shouldThrowException = false;
        private String exceptionMessage = "Test exception";

        public void addScheduleData(String stopId, Map<String, Object> data) {
            scheduleData.put(stopId, data);
        }

        public void addETAData(String stopId, String routeId, Map<String, Object> data) {
            String key = stopId + "_" + routeId;
            etaData.put(key, data);
        }

        public void setShouldThrowException(boolean shouldThrow, String message) {
            this.shouldThrowException = shouldThrow;
            this.exceptionMessage = message;
        }

        @Override
        public Map<String, Object> getBusSchedule(String stopId) {
            if (shouldThrowException) {
                throw new RuntimeException(exceptionMessage);
            }

            Map<String, Object> result = new HashMap<>();
            if (scheduleData.containsKey(stopId)) {
                Map<String, Object> data = scheduleData.get(stopId);
                result.put("success", true);
                result.put("cached", data.getOrDefault("cached", false));
                result.put("noBuses", data.getOrDefault("noBuses", false));
                result.put("data", data.get("scheduleData"));
                result.put("message", data.getOrDefault("message", "Success"));
            } else {
                result.put("success", false);
                result.put("cached", false);
                result.put("noBuses", false);
                result.put("message", "Stop not found");
            }
            return result;
        }

        @Override
        public Map<String, Object> getBusETA(String stopId, String routeId) {
            String key = stopId + "_" + routeId;
            Map<String, Object> result = new HashMap<>();
            if (etaData.containsKey(key)) {
                Map<String, Object> data = etaData.get(key);
                result.put("success", true);
                result.put("cached", data.getOrDefault("cached", false));
                result.put("data", data.get("etaData"));
                result.put("message", "Success");
            } else {
                result.put("success", false);
                result.put("cached", false);
                result.put("message", "ETA data not found");
            }
            return result;
        }
    }

    // Mock for BusScheduleOutputBoundary
    private static class BusScheduleOutputBoundaryMock implements BusScheduleOutputBoundary {
        private BusScheduleOutputData successData;
        private BusScheduleOutputData cachedData;
        private String failMessage;
        private int successCallCount = 0;
        private int cachedCallCount = 0;
        private int failCallCount = 0;

        @Override
        public void prepareSuccessView(BusScheduleOutputData outputData) {
            this.successData = outputData;
            successCallCount++;
        }

        @Override
        public void prepareCachedView(BusScheduleOutputData outputData) {
            this.cachedData = outputData;
            cachedCallCount++;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            this.failMessage = errorMessage;
            failCallCount++;
        }

        public BusScheduleOutputData getSuccessData() {
            return successData;
        }

        public BusScheduleOutputData getCachedData() {
            return cachedData;
        }

        public String getFailMessage() {
            return failMessage;
        }

        public int getSuccessCallCount() {
            return successCallCount;
        }

        public int getCachedCallCount() {
            return cachedCallCount;
        }

        public int getFailCallCount() {
            return failCallCount;
        }

        public void reset() {
            successData = null;
            cachedData = null;
            failMessage = null;
            successCallCount = 0;
            cachedCallCount = 0;
            failCallCount = 0;
        }
    }

    @Test
    public void testExecuteSuccessWithLiveData() {
        // Arrange
        BusScheduleGatewayStub gatewayStub = new BusScheduleGatewayStub();
        BusScheduleOutputBoundaryMock presenterMock = new BusScheduleOutputBoundaryMock();
        BusScheduleInteractor interactor = new BusScheduleInteractor(gatewayStub, presenterMock);

        String stopId = "12345";
        Map<String, Object> mockScheduleData = createMockScheduleDataWithArrivals();

        Map<String, Object> gatewayResponse = new HashMap<>();
        gatewayResponse.put("scheduleData", mockScheduleData);
        gatewayResponse.put("cached", false);
        gatewayResponse.put("noBuses", false);

        gatewayStub.addScheduleData(stopId, gatewayResponse);
        BusScheduleInputData inputData = new BusScheduleInputData(stopId);

        // Act
        interactor.execute(inputData);

        // Assert
        assertEquals("Should call success view once", 1, presenterMock.getSuccessCallCount());
        assertEquals("Should not call cached view", 0, presenterMock.getCachedCallCount());
        assertEquals("Should not call fail view", 0, presenterMock.getFailCallCount());

        BusScheduleOutputData outputData = presenterMock.getSuccessData();
        assertNotNull("Output data should not be null", outputData);
        assertTrue("Should be successful", outputData.isSuccess());
        assertEquals("Stop ID should match", stopId, outputData.getStopId());
        assertFalse("Should not be cached", outputData.isCached());
        assertFalse("Should have buses", outputData.isNoBuses());
        assertNotNull("Schedule data should not be null", outputData.getScheduleData());
    }

    @Test
    public void testExecuteSuccessWithCachedData() {
        // Arrange
        BusScheduleGatewayStub gatewayStub = new BusScheduleGatewayStub();
        BusScheduleOutputBoundaryMock presenterMock = new BusScheduleOutputBoundaryMock();
        BusScheduleInteractor interactor = new BusScheduleInteractor(gatewayStub, presenterMock);

        String stopId = "12346";
        Map<String, Object> mockScheduleData = createMockScheduleDataWithArrivals();

        Map<String, Object> gatewayResponse = new HashMap<>();
        gatewayResponse.put("scheduleData", mockScheduleData);
        gatewayResponse.put("cached", true);
        gatewayResponse.put("noBuses", false);

        gatewayStub.addScheduleData(stopId, gatewayResponse);
        BusScheduleInputData inputData = new BusScheduleInputData(stopId);

        // Act
        interactor.execute(inputData);

        // Assert
        assertEquals("Should call cached view once", 1, presenterMock.getCachedCallCount());
        assertEquals("Should not call success view", 0, presenterMock.getSuccessCallCount());
        assertEquals("Should not call fail view", 0, presenterMock.getFailCallCount());

        BusScheduleOutputData outputData = presenterMock.getCachedData();
        assertNotNull("Output data should not be null", outputData);
        assertTrue("Should be successful", outputData.isSuccess());
        assertEquals("Stop ID should match", stopId, outputData.getStopId());
        assertTrue("Should be cached", outputData.isCached());
        assertFalse("Should have buses", outputData.isNoBuses());
    }

    @Test
    public void testExecuteSuccessWithNoBuses() {
        // Arrange
        BusScheduleGatewayStub gatewayStub = new BusScheduleGatewayStub();
        BusScheduleOutputBoundaryMock presenterMock = new BusScheduleOutputBoundaryMock();
        BusScheduleInteractor interactor = new BusScheduleInteractor(gatewayStub, presenterMock);

        String stopId = "12347";
        Map<String, Object> mockScheduleData = createMockScheduleDataNoBuses();

        Map<String, Object> gatewayResponse = new HashMap<>();
        gatewayResponse.put("scheduleData", mockScheduleData);
        gatewayResponse.put("cached", false);
        gatewayResponse.put("noBuses", true);

        gatewayStub.addScheduleData(stopId, gatewayResponse);
        BusScheduleInputData inputData = new BusScheduleInputData(stopId);

        // Act
        interactor.execute(inputData);

        // Assert
        assertEquals("Should call success view once", 1, presenterMock.getSuccessCallCount());

        BusScheduleOutputData outputData = presenterMock.getSuccessData();
        assertNotNull("Output data should not be null", outputData);
        assertTrue("Should be successful", outputData.isSuccess());
        assertTrue("Should have no buses", outputData.isNoBuses());
        assertFalse("Should not be cached", outputData.isCached());
    }

    @Test
    public void testExecuteGatewayFailure() {
        // Arrange
        BusScheduleGatewayStub gatewayStub = new BusScheduleGatewayStub();
        BusScheduleOutputBoundaryMock presenterMock = new BusScheduleOutputBoundaryMock();
        BusScheduleInteractor interactor = new BusScheduleInteractor(gatewayStub, presenterMock);

        String stopId = "99999"; // Stop not in stub
        BusScheduleInputData inputData = new BusScheduleInputData(stopId);

        // Act
        interactor.execute(inputData);

        // Assert
        assertEquals("Should call fail view once", 1, presenterMock.getFailCallCount());
        assertEquals("Should not call success view", 0, presenterMock.getSuccessCallCount());
        assertEquals("Should not call cached view", 0, presenterMock.getCachedCallCount());

        assertNotNull("Fail message should not be null", presenterMock.getFailMessage());
        assertTrue("Fail message should indicate stop not found",
                presenterMock.getFailMessage().contains("Stop not found"));
    }

    @Test
    public void testExecuteWithException() {
        // Arrange
        BusScheduleGatewayStub gatewayStub = new BusScheduleGatewayStub();
        BusScheduleOutputBoundaryMock presenterMock = new BusScheduleOutputBoundaryMock();
        BusScheduleInteractor interactor = new BusScheduleInteractor(gatewayStub, presenterMock);

        String stopId = "12348";
        gatewayStub.setShouldThrowException(true, "Database connection failed");
        BusScheduleInputData inputData = new BusScheduleInputData(stopId);

        // Act
        interactor.execute(inputData);

        // Assert
        assertEquals("Should call fail view once", 1, presenterMock.getFailCallCount());
        assertEquals("Should not call success view", 0, presenterMock.getSuccessCallCount());
        assertEquals("Should not call cached view", 0, presenterMock.getCachedCallCount());

        assertNotNull("Fail message should not be null", presenterMock.getFailMessage());
        assertTrue("Fail message should contain system error",
                presenterMock.getFailMessage().contains("System error"));
        assertTrue("Fail message should contain original exception",
                presenterMock.getFailMessage().contains("Database connection failed"));
    }

    @Test
    public void testExecuteWithEmptyScheduleData() {
        // Arrange
        BusScheduleGatewayStub gatewayStub = new BusScheduleGatewayStub();
        BusScheduleOutputBoundaryMock presenterMock = new BusScheduleOutputBoundaryMock();
        BusScheduleInteractor interactor = new BusScheduleInteractor(gatewayStub, presenterMock);

        String stopId = "12349";
        Map<String, Object> emptyScheduleData = new HashMap<>();

        Map<String, Object> gatewayResponse = new HashMap<>();
        gatewayResponse.put("scheduleData", emptyScheduleData);
        gatewayResponse.put("cached", false);
        gatewayResponse.put("noBuses", false);

        gatewayStub.addScheduleData(stopId, gatewayResponse);
        BusScheduleInputData inputData = new BusScheduleInputData(stopId);

        // Act
        interactor.execute(inputData);

        // Assert
        assertEquals("Should call success view once", 1, presenterMock.getSuccessCallCount());

        BusScheduleOutputData outputData = presenterMock.getSuccessData();
        assertNotNull("Output data should not be null", outputData);
        assertTrue("Should be successful", outputData.isSuccess());
        assertNotNull("Schedule data should not be null", outputData.getScheduleData());
        assertTrue("Schedule data should be empty", outputData.getScheduleData().isEmpty());
    }

    @Test
    public void testExecuteWithNullScheduleDataFromGateway() {
        // Arrange
        BusScheduleGatewayStub gatewayStub = new BusScheduleGatewayStub();
        BusScheduleOutputBoundaryMock presenterMock = new BusScheduleOutputBoundaryMock();
        BusScheduleInteractor interactor = new BusScheduleInteractor(gatewayStub, presenterMock);

        String stopId = "12350";

        Map<String, Object> gatewayResponse = new HashMap<>();
        gatewayResponse.put("scheduleData", null); // Null schedule data
        gatewayResponse.put("cached", false);
        gatewayResponse.put("noBuses", true);
        gatewayResponse.put("success", true);

        gatewayStub.addScheduleData(stopId, gatewayResponse);
        BusScheduleInputData inputData = new BusScheduleInputData(stopId);

        // Act
        interactor.execute(inputData);

        // Assert
        assertEquals("Should call success view once", 1, presenterMock.getSuccessCallCount());

        BusScheduleOutputData outputData = presenterMock.getSuccessData();
        assertNotNull("Output data should not be null", outputData);
        assertTrue("Should be successful", outputData.isSuccess());
        assertNull("Schedule data should be null", outputData.getScheduleData());
        assertTrue("Should have no buses", outputData.isNoBuses());
    }

    @Test
    public void testExecuteWithRouteId() {
        // Arrange
        BusScheduleGatewayStub gatewayStub = new BusScheduleGatewayStub();
        BusScheduleOutputBoundaryMock presenterMock = new BusScheduleOutputBoundaryMock();
        BusScheduleInteractor interactor = new BusScheduleInteractor(gatewayStub, presenterMock);

        String stopId = "12351";
        String routeId = "505";
        Map<String, Object> mockScheduleData = createMockScheduleDataWithArrivals();

        Map<String, Object> gatewayResponse = new HashMap<>();
        gatewayResponse.put("scheduleData", mockScheduleData);
        gatewayResponse.put("cached", false);
        gatewayResponse.put("noBuses", false);

        gatewayStub.addScheduleData(stopId, gatewayResponse);
        BusScheduleInputData inputData = new BusScheduleInputData(stopId, routeId);

        // Act
        interactor.execute(inputData);

        // Assert
        assertEquals("Should call success view once", 1, presenterMock.getSuccessCallCount());

        BusScheduleOutputData outputData = presenterMock.getSuccessData();
        assertNotNull("Output data should not be null", outputData);
        assertTrue("Should be successful", outputData.isSuccess());
        assertEquals("Stop ID should match", stopId, outputData.getStopId());
    }

    @Test
    public void testExecuteWithDefaultValuesWhenMissing() {
        // Arrange
        BusScheduleGatewayStub gatewayStub = new BusScheduleGatewayStub();
        BusScheduleOutputBoundaryMock presenterMock = new BusScheduleOutputBoundaryMock();
        BusScheduleInteractor interactor = new BusScheduleInteractor(gatewayStub, presenterMock);

        String stopId = "12352";

        // Gateway response with missing fields
        Map<String, Object> gatewayResponse = new HashMap<>();
        gatewayResponse.put("success", true);
        // Missing cached, noBuses, data fields

        // Manually add to stub's internal map
        gatewayStub.addScheduleData(stopId, gatewayResponse);
        BusScheduleInputData inputData = new BusScheduleInputData(stopId);

        // Act
        interactor.execute(inputData);

        // Assert
        // Should handle missing fields gracefully and call success view
        assertEquals("Should call success view once", 1, presenterMock.getSuccessCallCount());
    }

    // Helper methods
    private Map<String, Object> createMockScheduleDataWithArrivals() {
        Map<String, Object> scheduleData = new HashMap<>();
        scheduleData.put("stopName", "Main Street Station");
        scheduleData.put("lastUpdated", "12:34:56");
        scheduleData.put("numberOfBuses", 4);

        List<Map<String, Object>> arrivals = new ArrayList<>();
        arrivals.add(createArrival("26", "Dupont", "12:35", 1, "Route 26"));
        arrivals.add(createArrival("96", "Wilson", "12:43", 9, "Route 96"));
        arrivals.add(createArrival("506", "Carlton", "12:52", 18, "Route 506"));

        scheduleData.put("arrivals", arrivals);
        return scheduleData;
    }

    private Map<String, Object> createMockScheduleDataNoBuses() {
        Map<String, Object> scheduleData = new HashMap<>();
        scheduleData.put("stopName", "Empty Station");
        scheduleData.put("lastUpdated", "12:34:56");
        scheduleData.put("numberOfBuses", 0);
        scheduleData.put("arrivals", new ArrayList<>());
        return scheduleData;
    }

    private Map<String, Object> createArrival(String routeId, String destination, String arrivalTime, int eta, String routeName) {
        Map<String, Object> arrival = new HashMap<>();
        arrival.put("routeId", routeId);
        arrival.put("destination", destination);
        arrival.put("arrivalTime", arrivalTime);
        arrival.put("eta", eta);
        arrival.put("routeName", routeName);
        return arrival;
    }
}
