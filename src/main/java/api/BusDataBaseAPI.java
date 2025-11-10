package api;

import entities.Bus;


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
    private static final String API_URL = "https://bustime.ttc.ca/gtfsrt/vehicles?debug";
    private static final String STATUS_CODE = "status_code";
    private static final int SUCCESS_CODE = 200;



    @Override
    public Bus getBus(int id) {
        final OkHttpClient client = new OkHttpClient().newBuilder().build();

        final Request request = new Request.Builder().url(API_URL).build();

        try {
            final Response response = client.newCall(request).execute();
            final JSONObject responseBody = new JSONObject(response.body().string());

            if (responseBody.getInt(STATUS_CODE) == SUCCESS_CODE) {
                System.out.println(responseBody);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Bus();
    }

    @Override
    public Bus[] getAllBuses() {
        return new Bus[0];
    }

    public static void main(String[] args) {

    }
}
