package data_access;

import com.google.transit.realtime.GtfsRealtime;
import entities.Bus;
import entities.Route;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import use_case.find_nearest_route.FindNearestRouteDataAccessInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BusDataAccessObject implements FindNearestRouteDataAccessInterface {
    private static final String API_URL = "https://bustime.ttc.ca/gtfsrt/vehicles";

    @Override
    public List<Route> getAllRoutes() {
        final OkHttpClient client = new OkHttpClient();

        final Request request = new Request.Builder().url(API_URL).build();

        List<Route> routes = new ArrayList<>();

        try {

            final Response response = client.newCall(request).execute();
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


}
