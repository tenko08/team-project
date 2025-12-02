package use_case.alerts;

import api.AlertDataBase;
import entities.Alert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class   AlertsTest {

    // Stub for AlertDataBase
    private static class AlertDataBaseStub implements AlertDataBase {
        List<Alert> alertsToReturn = new ArrayList<>();
        RuntimeException toThrow;

        @Override
        public List<Alert> getAllAlerts() {
            if (toThrow != null) throw toThrow;
            return alertsToReturn;
        }

        @Override
        public List<Alert> getAlertsForRoute(String routeId) {
            return alertsToReturn;
        }

        @Override
        public List<Alert> getAlertsForStop(String stopId) {
            return alertsToReturn;
        }
    }

    // Mock for AlertsOutputBoundary
    private static class AlertsPresenterMock implements AlertsOutputBoundary {
        private AlertsOutputData lastOutput;

        @Override
        public void present(AlertsOutputData outputData) {
            this.lastOutput = outputData;
        }

        public AlertsOutputData getLastOutput() {
            return lastOutput;
        }
    }

    // Mock for optional SearchByRouteInputBoundary
    private static class SearchByRouteMock implements use_case.search_by_route.SearchByRouteInputBoundary {
        boolean called = false;
        boolean throwOnExecute = false;

        @Override
        public void execute(use_case.search_by_route.SearchByRouteInputData inputData) {
            called = true;
            if (throwOnExecute) {
                throw new RuntimeException("searchByRoute boom");
            }
        }
    }

    private static Alert sampleAlert(String id) {
        return new Alert(id, "Header", "Description", "CAUSE", "EFFECT", 1L,
                Arrays.asList("1"), Arrays.asList("S1"));
    }

    @Test
    public void testExecuteSuccessWithAlerts() {
        AlertDataBaseStub db = new AlertDataBaseStub();
        db.alertsToReturn = Arrays.asList(sampleAlert("a1"), sampleAlert("a2"));
        AlertsPresenterMock presenter = new AlertsPresenterMock();
        AlertsInteractor interactor = new AlertsInteractor(db, presenter);

        AlertsInputData input = new AlertsInputData("1", "S1");
        interactor.execute(input);

        AlertsOutputData out = presenter.getLastOutput();
        assertNotNull(out);
        assertTrue(out.isSuccess());
        assertNull(out.getErrorMessage());
        assertEquals(2, out.getAlerts().size());
        assertEquals("1", out.getSelectedRouteId());
        assertEquals("S1", out.getSelectedStopId());
    }

    @Test
    public void testExecuteSuccessWithEmptyList() {
        AlertDataBaseStub db = new AlertDataBaseStub();
        db.alertsToReturn = new ArrayList<>();
        AlertsPresenterMock presenter = new AlertsPresenterMock();
        AlertsInteractor interactor = new AlertsInteractor(db, presenter);

        interactor.execute(new AlertsInputData(null, null));

        AlertsOutputData out = presenter.getLastOutput();
        assertNotNull(out);
        assertTrue("Even empty list should be treated as success", out.isSuccess());
        assertNotNull(out.getAlerts());
        assertEquals(0, out.getAlerts().size());
    }

    @Test
    public void testExecuteNullListFromDb_ShouldFail() {
        AlertDataBaseStub db = new AlertDataBaseStub();
        db.alertsToReturn = null; // Simulate faulty DAO
        AlertsPresenterMock presenter = new AlertsPresenterMock();
        AlertsInteractor interactor = new AlertsInteractor(db, presenter);

        interactor.execute(new AlertsInputData("5", null));

        AlertsOutputData out = presenter.getLastOutput();
        assertNotNull(out);
        assertFalse(out.isSuccess());
        assertEquals("Failed to load alerts", out.getErrorMessage());
    }

    @Test
    public void testExecuteExceptionPath_ShouldFail() {
        AlertDataBaseStub db = new AlertDataBaseStub();
        db.toThrow = new RuntimeException("boom");
        AlertsPresenterMock presenter = new AlertsPresenterMock();
        AlertsInteractor interactor = new AlertsInteractor(db, presenter);

        interactor.execute(new AlertsInputData(null, "S2"));

        AlertsOutputData out = presenter.getLastOutput();
        assertNotNull(out);
        assertFalse(out.isSuccess());
        assertEquals("boom", out.getErrorMessage());
        assertNotNull("Alerts list should be non-null in failure", out.getAlerts());
    }

    @Test
    public void testExecute_WithOptionalSearchByRoute_Called() {
        // Arrange datasource with a couple of alerts that match route 1
        AlertDataBaseStub db = new AlertDataBaseStub();
        db.alertsToReturn = Arrays.asList(sampleAlert("a1"), sampleAlert("a2"));
        AlertsPresenterMock presenter = new AlertsPresenterMock();
        SearchByRouteMock searchMock = new SearchByRouteMock();
        AlertsInteractor interactor = new AlertsInteractor(db, presenter, searchMock);

        // Act: provide a non-null route so the optional collaborator should be invoked
        interactor.execute(new AlertsInputData("1", null));

        // Assert: primary output is presented and collaborator was called
        AlertsOutputData out = presenter.getLastOutput();
        assertNotNull(out);
        assertTrue(out.isSuccess());
        assertTrue("Optional SearchByRoute should be called when routeId is provided", searchMock.called);
    }

    @Test
    public void testExecute_WithOptionalSearchByRoute_ExceptionIsIgnored() {
        // Arrange
        AlertDataBaseStub db = new AlertDataBaseStub();
        db.alertsToReturn = Arrays.asList(sampleAlert("a1"));
        AlertsPresenterMock presenter = new AlertsPresenterMock();
        SearchByRouteMock searchMock = new SearchByRouteMock();
        searchMock.throwOnExecute = true; // Force exception in optional call path
        AlertsInteractor interactor = new AlertsInteractor(db, presenter, searchMock);

        // Act
        interactor.execute(new AlertsInputData("1", null));

        // Assert: The exception in optional path must not override the presented output
        AlertsOutputData out = presenter.getLastOutput();
        assertNotNull(out);
        assertTrue(out.isSuccess());
        assertTrue("Optional SearchByRoute was attempted", searchMock.called);
    }

    @Test
    public void testAlertsOutputData_ThreeArgConstructor_Defaults() {
        // Using the 3-arg constructor should default selected route/stop to null and alerts list non-null
        AlertsOutputData out = new AlertsOutputData(true, null, null);
        assertTrue(out.isSuccess());
        assertNull(out.getErrorMessage());
        assertNotNull(out.getAlerts());
        assertEquals(0, out.getAlerts().size());
        assertNull(out.getSelectedRouteId());
        assertNull(out.getSelectedStopId());
    }

    @Test
    public void testExecute_BlankRouteId_DoesNotCallOptionalCollaborator() {
        AlertDataBaseStub db = new AlertDataBaseStub();
        db.alertsToReturn = Arrays.asList(sampleAlert("a1"));
        AlertsPresenterMock presenter = new AlertsPresenterMock();
        SearchByRouteMock searchMock = new SearchByRouteMock();
        AlertsInteractor interactor = new AlertsInteractor(db, presenter, searchMock);

        interactor.execute(new AlertsInputData("   ", null));

        AlertsOutputData out = presenter.getLastOutput();
        assertNotNull(out);
        assertTrue(out.isSuccess());
        assertFalse("Blank routeId should not trigger optional collaborator", searchMock.called);
    }

    @Test
    public void testExecute_NullInputData_SucceedsAndNoOptionalCall() {
        AlertDataBaseStub db = new AlertDataBaseStub();
        db.alertsToReturn = Arrays.asList(sampleAlert("a1"));
        AlertsPresenterMock presenter = new AlertsPresenterMock();
        SearchByRouteMock searchMock = new SearchByRouteMock();
        AlertsInteractor interactor = new AlertsInteractor(db, presenter, searchMock);

        interactor.execute(null);

        AlertsOutputData out = presenter.getLastOutput();
        assertNotNull(out);
        assertTrue(out.isSuccess());
        assertNull(out.getSelectedRouteId());
        assertNull(out.getSelectedStopId());
        assertFalse("Null input should not call optional collaborator", searchMock.called);
    }

    @Test
    public void testExecute_StopFilterNonMatching_YieldsEmpty() {
        AlertDataBaseStub db = new AlertDataBaseStub();
        db.alertsToReturn = Arrays.asList(sampleAlert("a1")); // only has stop S1
        AlertsPresenterMock presenter = new AlertsPresenterMock();
        AlertsInteractor interactor = new AlertsInteractor(db, presenter);

        interactor.execute(new AlertsInputData(null, "S9"));

        AlertsOutputData out = presenter.getLastOutput();
        assertNotNull(out);
        assertTrue(out.isSuccess());
        assertEquals(0, out.getAlerts().size());
    }

    @Test
    public void testExecute_BlankStopId_DoesNotFilter() {
        // Arrange: include two alerts with different stopIds; blank stopId should not filter either out
        AlertDataBaseStub db = new AlertDataBaseStub();
        List<Alert> alerts = new ArrayList<>();
        alerts.add(sampleAlert("a1")); // has stop S1
        alerts.add(new Alert("b1", "Header2", "Description2", "CAUSE", "EFFECT", 2L,
                Arrays.asList("1"), Arrays.asList("S9")));
        db.alertsToReturn = alerts;

        AlertsPresenterMock presenter = new AlertsPresenterMock();
        AlertsInteractor interactor = new AlertsInteractor(db, presenter);

        // Act: provide a blank stopId so the stop filter predicate's left side is true (non-null)
        // but right side is false (blank), and overall the filter is skipped
        interactor.execute(new AlertsInputData(null, "   "));

        // Assert: no filtering by stop should have occurred; both alerts are returned
        AlertsOutputData out = presenter.getLastOutput();
        assertNotNull(out);
        assertTrue(out.isSuccess());
        assertEquals(2, out.getAlerts().size());
    }
}
