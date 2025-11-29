package use_case.alerts;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class AlertEntityTest {
    // Import the entity explicitly since this test lives under use_case.alerts
    private static class _Imports { entities.Alert _a; }

    @Test
    public void constructorAndGetters_withNonNullLists_shouldDefensivelyCopyAndExposeUnmodifiable() {
        // Prepare mutable input lists
        List<String> routes = new ArrayList<>(Arrays.asList("1", "2"));
        List<String> stops = new ArrayList<>(Collections.singletonList("S1"));

        long ts = 1700000000L;
        entities.Alert alert = new entities.Alert(
                "id-123",
                "Header text",
                "Description text",
                "ACCIDENT",
                "DETOUR",
                ts,
                routes,
                stops
        );

        // Mutate original lists after construction to ensure defensive copy was made
        routes.add("99");
        stops.clear();

        // Validate scalar getters
        assertEquals("id-123", alert.getId());
        assertEquals("Header text", alert.getHeaderText());
        assertEquals("Description text", alert.getDescriptionText());
        assertEquals("ACCIDENT", alert.getCause());
        assertEquals("DETOUR", alert.getEffect());
        assertEquals(ts, alert.getTimestamp());

        // Validate list getters reflect original state, not mutated inputs
        List<String> routeIds = alert.getRouteIds();
        List<String> stopIds = alert.getStopIds();
        assertEquals(Arrays.asList("1", "2"), routeIds);
        assertEquals(Collections.singletonList("S1"), stopIds);

        // Ensure unmodifiable views are returned
        try {
            routeIds.add("new");
            fail("Route IDs list should be unmodifiable");
        } catch (UnsupportedOperationException expected) {
            // expected
        }
        try {
            stopIds.remove(0);
            fail("Stop IDs list should be unmodifiable");
        } catch (UnsupportedOperationException expected) {
            // expected
        }

        // toString contains meaningful fields
        String s = alert.toString();
        assertTrue(s.contains("id='id-123'"));
        assertTrue(s.contains("header='Header text'"));
        assertTrue(s.contains("cause='ACCIDENT'"));
        assertTrue(s.contains("effect='DETOUR'"));
        assertTrue(s.contains("routes=[1, 2]"));
        assertTrue(s.contains("stops=[S1]"));
    }

    @Test
    public void constructor_withNullLists_shouldUseEmptyUnmodifiableLists() {
        entities.Alert alert = new entities.Alert(
                "id-0",
                "H",
                "D",
                "CAUSE",
                "EFFECT",
                1L,
                null,
                null
        );

        // Should be empty lists
        assertTrue(alert.getRouteIds().isEmpty());
        assertTrue(alert.getStopIds().isEmpty());

        // And unmodifiable
        try {
            alert.getRouteIds().add("x");
            fail("Route IDs list should be unmodifiable when null provided");
        } catch (UnsupportedOperationException expected) {
            // expected
        }
        try {
            alert.getStopIds().add("y");
            fail("Stop IDs list should be unmodifiable when null provided");
        } catch (UnsupportedOperationException expected) {
            // expected
        }
    }
}
