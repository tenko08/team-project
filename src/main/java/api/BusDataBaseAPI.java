package api;

import entities.Bus;

import com.google.transit.realtime.GtfsRealtime;
import entities.Position;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class BusDataBaseAPI implements BusDataBase {
    private static final String API_URL = "https://bustime.ttc.ca/gtfsrt/vehicles";

    @Override
    public List<Bus> getAllBuses() {
        final OkHttpClient client = new OkHttpClient();

        final Request request = new Request.Builder().url(API_URL).build();

        List<Bus> buses = new ArrayList<>();

        try {
            final Response response = client.newCall(request).execute();
            final byte[] bytes = response.body().bytes();

            GtfsRealtime.FeedMessage feed =
                    GtfsRealtime.FeedMessage.parseFrom(bytes);

//            System.out.println(feed);

            for (GtfsRealtime.FeedEntity entity : feed.getEntityList()) {
//                System.out.println(entity);
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
    public Bus getBus(int id) {
        List<Bus> buses = getAllBuses();

        for (Bus bus : buses) {
            if (bus.getId() == id) {
                return bus;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        new BusDataBaseAPI().getAllBuses();

    }
}
