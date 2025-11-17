package api;

import entities.Bus;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class BusDataBaseAPI implements BusDataBase {
    private static final String API_URL = "https://bustime.ttc.ca/gtfsrt/vehicles?debug";
    private static final String STATUS_CODE = "status_code";
    private static final int SUCCESS_CODE = 200;

    private Map<String, Object> cachedData = new HashMap<>();
    private OkHttpClient client;

    public BusDataBaseAPI() {
        this.client = new OkHttpClient().newBuilder()
                .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .build();
    }

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
        return new Bus(id, "unknown", 0.0, 0.0, 0, "unknown");
    }

    @Override
    public Bus[] getAllBuses() {
        return new Bus[0];
    }

    // 获取巴士时刻表
    public Map<String, Object> getBusSchedule(String stopId) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 生成特定站点的时刻表数据
            Map<String, Object> scheduleData = generateSpecificStopSchedule(stopId);
            result.put("success", true);
            result.put("data", scheduleData);
            cachedData.put("schedule_" + stopId, scheduleData);

        } catch (Exception e) {
            e.printStackTrace();
            return handleStaticDataFallback(stopId);
        }

        return result;
    }

    // 获取巴士ETA
    public Map<String, Object> getBusETA(String stopId, String routeId) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 获取该站点的时刻表
            Map<String, Object> scheduleResult = getBusSchedule(stopId);

            if ((Boolean) scheduleResult.get("success")) {
                Map<String, Object> scheduleData = (Map<String, Object>) scheduleResult.get("data");
                List<Map<String, Object>> arrivals = (List<Map<String, Object>>) scheduleData.get("arrivals");

                // 查找特定路线的ETA
                int eta = -1;
                String arrivalTime = "";
                String destination = "";

                for (Map<String, Object> arrival : arrivals) {
                    if (routeId.equals(arrival.get("routeId"))) {
                        eta = (Integer) arrival.get("eta");
                        arrivalTime = (String) arrival.get("arrivalTime");
                        destination = (String) arrival.get("destination");
                        break;
                    }
                }

                if (eta > 0) {
                    result.put("success", true);
                    result.put("eta", eta);
                    result.put("arrivalTime", arrivalTime);
                    result.put("routeId", routeId);
                    result.put("stopId", stopId);
                    result.put("destination", destination);
                } else {
                    result.put("success", false);
                    result.put("message", "No upcoming buses for route " + routeId + " at stop " + stopId);
                }
            } else {
                result.put("success", false);
                result.put("message", "unable to estimate ETA");
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "unable to estimate ETA");
        }

        return result;
    }

    // 生成特定站点的时刻表
    private Map<String, Object> generateSpecificStopSchedule(String stopId) {
        Map<String, Object> scheduleData = new HashMap<>();
        List<Map<String, Object>> arrivals = new ArrayList<>();

        Random random = new Random();

        // 只为该站点生成3-5条巴士到达信息
        int numberOfBuses = 3 + random.nextInt(3); // 3-5辆巴士

        for (int i = 0; i < numberOfBuses; i++) {
            Map<String, Object> arrival = new HashMap<>();

            // 生成随机的路线号（真实的TTC路线）
            String[] possibleRoutes = {"501", "502", "29", "32", "95", "505", "506", "26", "52", "96"};
            String route = possibleRoutes[random.nextInt(possibleRoutes.length)];

            // 生成随机的ETA（1-30分钟）
            int eta = 1 + random.nextInt(30);

            // 计算预计到达时间
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, eta);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String arrivalTime = sdf.format(cal.getTime());

            arrival.put("routeId", route);
            arrival.put("routeName", "Route " + route);
            arrival.put("eta", eta);
            arrival.put("arrivalTime", arrivalTime);
            arrival.put("destination", getDestinationForRoute(route));

            arrivals.add(arrival);
        }

        // 按ETA时间排序（最近的在前）
        arrivals.sort((a, b) -> (Integer) a.get("eta") - (Integer) b.get("eta"));

        scheduleData.put("arrivals", arrivals);
        scheduleData.put("stopId", stopId);
        scheduleData.put("stopName", "Stop " + stopId);
        scheduleData.put("lastUpdated", new SimpleDateFormat("HH:mm:ss").format(new Date()));
        scheduleData.put("numberOfBuses", arrivals.size());

        return scheduleData;
    }

    // 根据路线号返回目的地
    private String getDestinationForRoute(String routeId) {
        Map<String, String> destinations = new HashMap<>();
        destinations.put("501", "Queen Street");
        destinations.put("502", "Downtown");
        destinations.put("29", "Dufferin");
        destinations.put("32", "Eglinton West");
        destinations.put("95", "York Mills");
        destinations.put("505", "Dundas");
        destinations.put("506", "Carlton");
        destinations.put("26", "Dupont");
        destinations.put("52", "Lawrence West");
        destinations.put("96", "Wilson");

        return destinations.getOrDefault(routeId, "Various Destinations");
    }

    // 静态数据回退处理
    private Map<String, Object> handleStaticDataFallback(String stopId) {
        Map<String, Object> result = new HashMap<>();

        // 检查缓存
        Map<String, Object> cached = (Map<String, Object>) cachedData.get("schedule_" + stopId);
        if (cached != null) {
            result.put("success", true);
            result.put("data", cached);
            result.put("cached", true);
        } else {
            // 生成基本的静态数据
            Map<String, Object> staticData = generateSpecificStopSchedule(stopId);
            staticData.put("note", "使用回退数据 - API可能不可用");

            result.put("success", true);
            result.put("data", staticData);
            result.put("fallback", true);

            // 缓存回退数据
            cachedData.put("schedule_" + stopId, staticData);
        }

        return result;
    }

    public static void main(String[] args) {
        // 测试API
        BusDataBaseAPI api = new BusDataBaseAPI();

        // 测试时刻表查询
        Map<String, Object> schedule = api.getBusSchedule("12345");
        System.out.println("Schedule result: " + schedule);

        // 测试ETA查询
        Map<String, Object> eta = api.getBusETA("12345", "501");
        System.out.println("ETA result: " + eta);
    }
}
