package use_case.alerts;

import api.AlertDataBase;
import entities.Alert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class AlertsTest {

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
}
