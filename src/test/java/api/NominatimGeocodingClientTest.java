package api;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class NominatimGeocodingClientTest {

    @Test
    public void parseResults_parsesValidItemsAndSkipsInvalid() {
        String sample = "[\n" +
                "  {\n" +
                "    \"display_name\": \"Toronto, Ontario, Canada\",\n" +
                "    \"lat\": \"43.653226\",\n" +
                "    \"lon\": \"-79.3831843\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"display_name\": \"Invalid coords\",\n" +
                "    \"lat\": \"not-a-number\",\n" +
                "    \"lon\": \"-79.0\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"display_name\": \"Missing lat/lon\"\n" +
                "  }\n" +
                "]";

        List<NominatimGeocodingClient.Result> results = NominatimGeocodingClient.parseResults(sample);
        assertEquals(1, results.size());
        NominatimGeocodingClient.Result r = results.get(0);
        assertEquals("Toronto, Ontario, Canada", r.getDisplayName());
        assertEquals(43.653226, r.getLatitude(), 1e-6);
        assertEquals(-79.3831843, r.getLongitude(), 1e-6);
    }
}
