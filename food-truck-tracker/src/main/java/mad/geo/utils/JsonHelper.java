package mad.geo.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author : Chenglong Ma
 */
public class JsonHelper {
    private static boolean correctResult(JSONObject json) {
        try {
            if (json.getString(ResultKey.STATUS).toLowerCase().equals("ok")) {
                return true;
            }
        } catch (JSONException e) {
            return false;
        }
        return false;
    }

    public static String getErrorMsg(JSONObject json) throws JSONException {
        return json.getString(ErrorKey.Error_MSG);
    }

    private static JSONObject getResult(JSONObject json) throws JSONException {
        JSONArray res = json.getJSONArray(ResultKey.ROWS);
        if (res.length() > 0) {
            return res.getJSONObject(0)
                    .getJSONArray(ResultKey.ELEMENTS)
                    .getJSONObject(0);
        }
        return null;
    }

    private static JSONObject getDisJson(JSONObject json) throws JSONException {
        JSONObject res = getResult(json);
        if (res != null) {
            return res.getJSONObject(ResultKey.DISTANCE);
        }
        return null;
    }

    private static JSONObject getDurJson(JSONObject json) throws JSONException {
        JSONObject res = getResult(json);
        if (res != null) {
            return res.getJSONObject(ResultKey.DURATION);
        }
        return null;
    }

    public static double getDistance(JSONObject json) throws JSONException {
        JSONObject dis = getDisJson(json);
        if (dis != null) {
            return dis.getDouble(ResultKey.VALUE);
        }
        return Double.NaN;
    }

    public static String getDistanceStr(JSONObject json) throws JSONException {
        JSONObject dis = getDisJson(json);
        if (dis != null) {
            return dis.getString(ResultKey.TEXT);
        }
        return null;
    }

    public static double getDuration(JSONObject json) throws JSONException {
        JSONObject dur = getDurJson(json);
        if (dur != null) {
            return dur.getDouble(ResultKey.VALUE);
        }
        return Double.NaN;
    }

    public static String getDurationStr(JSONObject json) throws JSONException {
        JSONObject dis = getDurJson(json);
        if (dis != null) {
            return dis.getString(ResultKey.TEXT);
        }
        return null;
    }

    private static class ResultKey {
        static final String STATUS = "status";
        static final String ROWS = "rows";
        static final String ORI_ADDR = "origin_addresses";
        static final String DEST_ADDR = "destination_addresses";
        static final String ELEMENTS = "elements";
        static final String DISTANCE = "distance";
        static final String DURATION = "duration";
        static final String TEXT = "text";
        static final String VALUE = "value";
    }

    private static class ErrorKey extends ResultKey {
        static final String Error_MSG = "error_message";

    }
}
