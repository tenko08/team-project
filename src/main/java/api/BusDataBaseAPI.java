package api;

import entities.Bus;

import com.google.transit.realtime.GtfsRealtime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;


public class BusDataBaseAPI implements BusDataBase {
    private static final String API_URL = "https://bustime.ttc.ca/gtfsrt/vehicles";



    @Override
    public Bus getBus(int id) {
        final OkHttpClient client = new OkHttpClient();

        final Request request = new Request.Builder().url(API_URL).build();

        try {
            final Response response = client.newCall(request).execute();
            final byte[] bytes = response.body().bytes();

            GtfsRealtime.FeedMessage feed =
                    GtfsRealtime.FeedMessage.parseFrom(bytes);

            System.out.println(feed);

            for (GtfsRealtime.FeedEntity entity : feed.getEntityList()) {
                System.out.println(entity);
                if (entity.hasVehicle()) {
                    GtfsRealtime.VehiclePosition vp = entity.getVehicle();

                    if (vp.hasVehicle() && vp.getVehicle().hasId()) {

                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Bus(1);
    }

    @Override
    public Bus[] getAllBuses() {
        return new Bus[0];
    }

    public static void main(String[] args) {
        new BusDataBaseAPI().getBus(5);

    }
}
