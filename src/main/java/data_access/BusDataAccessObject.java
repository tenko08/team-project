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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.opencsv.CSVReader;
import use_case.map.MapDataAccessInterface;
import use_case.search_by_route.SearchByRouteDataAccessInterface;

import java.io.FileReader;
import java.util.List;


public class BusDataAccessObject implements FindNearestRouteDataAccessInterface, SearchByRouteDataAccessInterface, MapDataAccessInterface {
    private static final String API_URL_VEHICLES = "https://bustime.ttc.ca/gtfsrt/vehicles";

        private static final String API_URL_TRIPS = "https://bustime.ttc.ca/gtfsrt/trips";

    private final OkHttpClient client = new OkHttpClient();

    private final Request requestVehicles = new Request.Builder().url(API_URL_VEHICLES).build();

    private final Request requestTrips = new Request.Builder().url(API_URL_TRIPS).build();

    @Override
    public File getCacheDir() {
        // Return cache directory similar to CacheAccessObject
        return new File(System.getProperty("user.home") + File.separator + ".jxmapviewer2");
    }

    @Override
    public List<Bus> getAllBuses() {
        final OkHttpClient client = new OkHttpClient();


        List<Bus> buses = new ArrayList<>();

        try {

            final Response response = client.newCall(requestVehicles).execute();
            final byte[] bytes = response.body().bytes();

            GtfsRealtime.FeedMessage feed =
                    GtfsRealtime.FeedMessage.parseFrom(bytes);

//            System.out.println(feed);

            for (GtfsRealtime.FeedEntity entity : feed.getEntityList()) {
                System.out.println(entity);
                int vehicleId = -1;
                Position position = null;
                String occupancy = "UNKNOWN";

                if (entity.hasVehicle()) {
                    GtfsRealtime.VehiclePosition vp = entity.getVehicle();

                    if (vp.hasVehicle() && vp.getVehicle().hasId()) {
                        vehicleId = Integer.parseInt(vp.getVehicle().getId());
                    }

                    if (vp.hasVehicle() && vp.hasPosition()) {
                        position = new Position(vp.getPosition().getLatitude(), vp.getPosition().getLongitude(), vp.getPosition().getBearing(),
                                vp.getPosition().getSpeed());

                    }

                    if (vp.hasVehicle() && vp.hasOccupancyStatus()) {
                        occupancy = String.valueOf(vp.getOccupancyStatus());
                    }

                }
                Bus bus = new Bus(vehicleId, position, occupancy);
                buses.add(bus);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(buses);
        return buses;

    }

    @Override
    public Map<String, Object> getBusesByRoute(String routeNumber) {
        Map<String, Object> result = new HashMap<>();
        List<Bus> buses = new ArrayList<>();
        Route route = null;

        try {
            final Response response = client.newCall(requestVehicles).execute();
            final byte[] bytes = response.body().bytes();

            GtfsRealtime.FeedMessage feed = GtfsRealtime.FeedMessage.parseFrom(bytes);

            for (GtfsRealtime.FeedEntity entity : feed.getEntityList()) {
//                System.out.println(entity);
                if (entity.hasVehicle()) {
                    GtfsRealtime.VehiclePosition vp = entity.getVehicle();

                    // Extract route ID from trip descriptor
                    String routeId = null;
                    if (vp.hasTrip() && vp.getTrip().hasRouteId()) {
                        routeId = vp.getTrip().getRouteId();
                    }

                    // Only process buses matching the requested route
                    if (routeId != null && routeId.equals(routeNumber)) {
                        // Create route object if not already created
                        if (route == null) {
                            try {
                                int routeNum = Integer.parseInt(routeNumber);
                                route = new Route(routeNum);
                            } catch (NumberFormatException e) {
                                // If route number can't be parsed as int, use 0 as default
                                route = new Route(0);
                            }
                        }

                        int vehicleId = -1;
                        Position position = null;
                        String occupancy = "UNKNOWN";

                        if (vp.hasVehicle() && vp.getVehicle().hasId()) {
                            vehicleId = Integer.parseInt(vp.getVehicle().getId());
                        }

                        if (vp.hasPosition()) {
                            position = new Position(
                                    vp.getPosition().getLatitude(),
                                    vp.getPosition().getLongitude(),
                                    vp.getPosition().getBearing(),
                                    vp.getPosition().getSpeed()
                            );
                        }

                        if (vp.hasOccupancyStatus()) {
                            occupancy = String.valueOf(vp.getOccupancyStatus());
                        }

                        // Get direction from trip
//                        String direction = null;
//                        if (vp.hasTrip() && vp.getTrip().hasDirectionId()) {
//                            direction = vp.getTrip().getDirectionId() == 0 ? "Outbound" : "Inbound";
//                        }

                        // Create Bus entity with direction
                        Bus bus = new Bus(vehicleId, position, occupancy);
                        buses.add(bus);
                    }
                }
            }

            // Cache the result
            Map<String, Object> routeData = new HashMap<>();
            routeData.put("route", route);
            routeData.put("buses", buses);
            routeData.put("routeNumber", routeNumber);
            routeData.put("lastUpdated", new SimpleDateFormat("HH:mm:ss").format(new Date()));

            if (buses.isEmpty() || route == null) {
                result.put("success", false);
                result.put("message", "Route not found");
            } else {
                result.put("success", true);
                result.put("route", route);
                result.put("buses", buses);
                result.put("routeNumber", routeNumber);
                result.put("cached", false);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
    @Override
    public List<Route> getAllRoutes() {
        List<Route> routes = new ArrayList<>();

        HashMap<Integer, BusStop> allBusStops;
        HashMap<Integer, List<Bus>> allBuses;

        try {
            allBusStops = getAllBusStops();
            allBuses = getAllBusesMap();
        } catch (IOException | CsvException e) {
            e.printStackTrace();
            System.out.println("here2");
            return routes;
        }

        try {
            final Response response = client.newCall(requestTrips).execute();
            final byte[] bytes = response.body().bytes();

            GtfsRealtime.FeedMessage feed =
                    GtfsRealtime.FeedMessage.parseFrom(bytes);


            // First get the stop ids for each route
            for (GtfsRealtime.FeedEntity entity : feed.getEntityList()) {
                Route route = new Route();

                if (entity.hasTripUpdate()) {
                    GtfsRealtime.TripUpdate tripUpdate = entity.getTripUpdate();
                    String routeId = tripUpdate.getTrip().getRouteId();
                    route.setRouteNumber(Integer.parseInt(routeId));

                    List<Bus> buses = allBuses.get(Integer.parseInt(routeId));
                    route.addAllBuses(buses);

                    for (GtfsRealtime.TripUpdate.StopTimeUpdate stu : tripUpdate.getStopTimeUpdateList()) {
                        String stopId = stu.getStopId();
                        // Find the Bus Stop based on the stopId
                        BusStop busStop = allBusStops.get(Integer.parseInt(stopId));
                        route.addBusStop(busStop);
                    }

                    }
                // Adds the route to the routes array list
                routes.add(route);
//                if(!route.getBusStopList().isEmpty())
//                    System.out.println(route);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

//        System.out.println("here1 " + routes.size());
        return routes;

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

    // Returns a hashmap mapping the routeId to a list of Bus Objects for better lookup
    private HashMap<Integer, List<Bus>> getAllBusesMap() {
        HashMap<Integer, List<Bus>> busList = new HashMap<>();

        try {
            final Response response = client.newCall(requestVehicles).execute();
            final byte[] bytes = response.body().bytes();

            GtfsRealtime.FeedMessage feed =
                    GtfsRealtime.FeedMessage.parseFrom(bytes);

            for (GtfsRealtime.FeedEntity entity : feed.getEntityList()) {

                if (entity.hasVehicle()) {
                    GtfsRealtime.VehiclePosition vp = entity.getVehicle();

                    // Extract route ID
                    if (!vp.hasTrip() || !vp.getTrip().hasRouteId()) {
                        continue; // skip buses with no route
                    }

                    int routeId = Integer.parseInt(vp.getTrip().getRouteId());

                    // Vehicle ID
                    int vehicleId = vp.hasVehicle() && vp.getVehicle().hasId()
                            ? Integer.parseInt(vp.getVehicle().getId())
                            : -1;

                    // Position
                    Position position = null;
                    if (vp.hasPosition()) {
                        position = new Position(
                                vp.getPosition().getLatitude(),
                                vp.getPosition().getLongitude(),
                                vp.getPosition().getBearing(),
                                vp.getPosition().getSpeed()
                        );
                    }

                    // Occupancy
                    String occupancy = vp.hasOccupancyStatus()
                            ? vp.getOccupancyStatus().name()
                            : "UNKNOWN";

                    Bus bus = new Bus(vehicleId, position, occupancy);

                    // Add bus to the list for this route
                    busList.computeIfAbsent(routeId, k -> new ArrayList<>()).add(bus);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return busList;
    }

    public static void main(String[] args) throws IOException, CsvException {
        BusDataAccessObject busDataAccessObject = new BusDataAccessObject();
//        List<String> routeIds = busDataAccessObject.getAllRouteIds();
//        System.out.println(routeIds);
//        HashMap<Integer, BusStop> busStopList = busDataAccessObject.getAllBusStops();
//        System.out.println(busStopList);
        List<Route> allRoutes = busDataAccessObject.getAllRoutes();
        System.out.println(allRoutes);
//        HashMap<Integer, List<Bus>> allBuses = busDataAccessObject.getAllBuses();
//        System.out.println(allBuses);
    }



}
