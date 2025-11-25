package data_access;

import com.google.transit.realtime.GtfsRealtime;
import com.opencsv.exceptions.CsvException;
import entities.Bus;
import entities.BusStop;
import entities.Position;
import entities.Route;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import use_case.find_nearest_route.FindNearestRouteDataAccessInterface;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.opencsv.CSVReader;
import java.io.FileReader;
import java.util.List;


public class BusDataAccessObject implements FindNearestRouteDataAccessInterface {
    private static final String API_URL_VEHICLES = "https://bustime.ttc.ca/gtfsrt/vehicles";

    private static final String API_URL_TRIPS = "https://bustime.ttc.ca/gtfsrt/trips";

    private final OkHttpClient client = new OkHttpClient();

    private final Request requestVehicles = new Request.Builder().url(API_URL_VEHICLES).build();

    private final Request requestTrips = new Request.Builder().url(API_URL_TRIPS).build();

    @Override
    public List<Route> getAllRoutes() {
        List<Route> routes = new ArrayList<>();


        try {
            final Response response = client.newCall(requestTrips).execute();
            final byte[] bytes = response.body().bytes();

            GtfsRealtime.FeedMessage feed =
                    GtfsRealtime.FeedMessage.parseFrom(bytes);


            for (GtfsRealtime.FeedEntity entity : feed.getEntityList()) {
                Route route = null;

                if (entity.hasVehicle()) {
                    GtfsRealtime.VehiclePosition vp = entity.getVehicle();

                    String routeId = null;
                    if (vp.hasTrip() && vp.getTrip().getRouteId() != null) {
                        routeId = vp.getTrip().getRouteId();
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return List.of();

    }

    private List<String> getAllRouteIds() throws IOException, CsvException {
        List<String> routeIds = new ArrayList<>();
        CSVReader reader = new CSVReader(new FileReader("src/main/java/data/routes.csv"));
        List<String[]> rows = reader.readAll();

        for (int i = 1; i < rows.size(); i++) {
            String routeId = rows.get(i)[2];
            routeIds.add(routeId);
        }

        reader.close();
        return routeIds;
    }

    // Returns a hashmap mapping the butStopId to the BusStop object for better lookup
    private HashMap<Integer, BusStop> getAllBusStops() throws IOException, CsvException {
        HashMap<Integer, BusStop> busStopList = new HashMap<>();
        CSVReader reader = new CSVReader(new FileReader("src/main/java/data/stops.csv"));
        List<String[]> rows = reader.readAll();

        for (int i = 1; i < rows.size(); i++) {
            int stopId = Integer.parseInt(rows.get(i)[0]);
            double latitude = Double.parseDouble(rows.get(i)[4]);
            double longitude = Double.parseDouble(rows.get(i)[5]);
            String name = rows.get(i)[2];
            Position position = new Position(latitude, longitude);
            BusStop busStop = new BusStop(stopId, name, position);
            busStopList.put(stopId, busStop);
        }

        reader.close();

        return busStopList;
    }

    public static void main(String[] args) throws IOException, CsvException {
        BusDataAccessObject busDataAccessObject = new BusDataAccessObject();
        List<String> routeIds = busDataAccessObject.getAllRouteIds();
        System.out.println(routeIds);
        HashMap<Integer, BusStop> busStopList = busDataAccessObject.getAllBusStops();
        System.out.println(busStopList);
    }



}
