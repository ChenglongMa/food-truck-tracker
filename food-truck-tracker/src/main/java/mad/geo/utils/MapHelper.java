package mad.geo.utils;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import mad.geo.R;
import mad.geo.model.AbstractTrackable;
import mad.geo.model.AsyncTaskResult;
import mad.geo.service.TrackableService;
import mad.geo.service.service.SuggestionService;

/**
 * @author : Chenglong Ma
 */
public class MapHelper {
    private static final String TAG = MapHelper.class.getName();
    public static final LatLng ORIGIN = new LatLng(-37.807425, 144.963814);//TODO: hard code
    private final SuggestionService service;
    private final TrackableService trackableService;

    public MapHelper(SuggestionService service) {
        this.service = service;
        trackableService = TrackableService.getSingletonInstance(service);
    }

    public void getNearestTrackable(List<AbstractTrackable> trackables) {
        DistanceTask distanceTask = new DistanceTask(trackables);
        distanceTask.execute();
    }

    public String requestDistance(String reqUrl) throws IOException {
        String responseString = "";
        HttpURLConnection httpURLConnection;
        URL url = new URL(reqUrl);
        httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.connect();
        try (InputStream inputStream = httpURLConnection.getInputStream()) {
            //Get the response result
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder stringBuffer = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpURLConnection.disconnect();
        }
        return responseString;
    }

    public String getDisRequestUrl(LatLng origin, LatLng dest) {
        String originParam = String.format(Locale.getDefault(),
                "origins=%f,%f&",
                origin.latitude, origin.longitude);
        String destParam = String.format(Locale.getDefault(),
                "destinations=%f,%f&",
                dest.latitude, dest.longitude);
        String output = "json?";
        String mode = "mode=walking&";
        String key = "key=" + service.getText(R.string.google_maps_key);
        String param = originParam + destParam + mode + key;
        return service.getText(R.string.distance_url) + output + param;
    }

    public enum Key {
        DISTANCE,
        DISTANCE_TEXT,
        DURATION,
        DURATION_TEXT
    }

    private class DistanceTask extends ExceptionAsyncTask<Void> {
        private Map<Key, String> result = new HashMap<>();
        private JSONObject nearest = null;
        private double nearestDis = Double.MAX_VALUE;
        private String nearestDisStr;
        private String nearestDuration;
        private List<AbstractTrackable> trackables;
        private AbstractTrackable nearestItem;


        DistanceTask(List<AbstractTrackable> trackables) {
            this.trackables = trackables;
        }

        @Override
        protected void doInBackground(Void unused) {
            try {
                for (AbstractTrackable trackable : trackables) {
                    int id = trackable.getId();
                    for (LatLng dest : trackableService.getRouteInfo(id)) {
                        String url = getDisRequestUrl(ORIGIN, dest);
                        JSONObject res = new JSONObject(requestDistance(url));
                        double dis = JsonHelper.getDistance(res);
                        if (dis < nearestDis) {
                            nearest = res;
                            nearestDis = dis;
                            nearestItem = trackable;
                            nearestDisStr = JsonHelper.getDistanceStr(res);
                            nearestDuration = JsonHelper.getDurationStr(res);
                        }
                    }
                }
            } catch (JSONException | IOException e) {
                throw new IllegalArgumentException("Cannot get available route information");
            }
        }

        @Override
        protected void dealWithException(Exception e) {
            service.sendException(e);
        }

        @Override
        protected void postExecute(AsyncTaskResult<Void> result) {
            if (nearestItem == null) {
                return;
            }
            String msg = String.format("There is trackable around you, would you like to tracking it?\nName:%s\nDistance:%s\nWalking Duration:%s"
                    , nearestItem.getName(), nearestDisStr, nearestDuration);
            Log.i(TAG, msg);
            service.sendMsg(nearestItem.getId(), msg);
        }

//        @Override
//        protected Map<Key, String> doInBackground(LatLng... locations) {
//            if (locations.length < 2) {
//                return null;
//            }
//            Map<Key, String> result = new HashMap<>();
//            LatLng origin = locations[0];
//            LatLng dest = locations[1];
//            try {
//                String url = getDisRequestUrl(origin, dest);
//                JSONObject res = new JSONObject(requestDistance(url));
//                result.put(Key.DISTANCE, String.valueOf(JsonHelper.getDistance(res)));
//                result.put(Key.DISTANCE_TEXT, Objects.requireNonNull(JsonHelper.getDistanceStr(res)));
//                result.put(Key.DURATION, String.valueOf(JsonHelper.getDuration(res)));
//                result.put(Key.DURATION_TEXT, Objects.requireNonNull(JsonHelper.getDurationStr(res)));
//            } catch (JSONException | IOException e) {
//                Log.e(TAG, e.getLocalizedMessage());
//            }
//            return result;
//        }

    }

//    private class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>>> {
//
//        @Override
//        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
//            JSONObject jsonObject;
//            List<List<HashMap<String, String>>> routes = null;
//            try {
//                jsonObject = new JSONObject(strings[0]);
//                JsonHelper.getDistance(jsonObject);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            return routes;
//        }
//
//        @Override
//        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
//            //Get list route and display it into the map
//
//            ArrayList<LatLng> points = null;
//
//            PolylineOptions polylineOptions = null;
//
//            for (List<HashMap<String, String>> path : lists) {
//                points = new ArrayList<>();
//                polylineOptions = new PolylineOptions();
//
//                for (HashMap<String, String> point : path) {
//                    double lat = Double.parseDouble(point.get("lat"));
//                    double lon = Double.parseDouble(point.get("lon"));
//
//                    points.add(new LatLng(lat, lon));
//                }
//
//                polylineOptions.addAll(points);
//                polylineOptions.width(15);
//                polylineOptions.color(Color.BLUE);
//                polylineOptions.geodesic(true);
//            }
//
//            if (polylineOptions != null) {
//                mMap.addPolyline(polylineOptions);
//            } else {
//                Toast.makeText(getApplicationContext(), "Direction not found!", Toast.LENGTH_SHORT).show();
//            }
//
//        }
//    }
}
