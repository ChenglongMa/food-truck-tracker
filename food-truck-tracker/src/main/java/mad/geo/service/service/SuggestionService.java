package mad.geo.service.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import mad.geo.model.AbstractTrackable;
import mad.geo.service.TrackableService;
import mad.geo.utils.JsonHelper;
import mad.geo.utils.MapHelper;
import mad.geo.view.activity.SettingsActivity;
import mad.geo.view.fragment.TrackableFragment;

/**
 * @author : Chenglong Ma
 */
public class SuggestionService extends IntentService {
    private static final String TAG = SuggestionService.class.getName();
    private final MapHelper mapHelper;
    private TrackableService trackableService;
    private SharedPreferences preferences;
    private LocalBroadcastManager mLocalBroadcastManager;
    private boolean isRunning = true;


    public SuggestionService() {
        super("SuggestionService");
        mapHelper = new MapHelper(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        trackableService = TrackableService.getSingletonInstance(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        double nearestDis = Double.MAX_VALUE;
        String nearestDisStr = null;
        String nearestDuration = null;
        AbstractTrackable nearestItem = null;
        try {
            while (isRunning) {
                boolean suggestion_switch = preferences.getBoolean(SettingsActivity.SUGGESTION_SWITCH, false);
                if (!suggestion_switch) {
                    continue;
                }
                double mins = Double.parseDouble(preferences.getString(SettingsActivity.SUGGESTION_INTERVAL, "5"));
                Thread.sleep((long) (mins * 1000 * 60));
                List<AbstractTrackable> trackables = trackableService.getFilterTrackables();
                for (AbstractTrackable trackable : trackables) {
                    int id = trackable.getId();
                    for (LatLng dest : trackableService.getRouteInfo(id)) {
                        String url = mapHelper.getDisRequestUrl(MapHelper.ORIGIN, dest);
                        JSONObject res = new JSONObject(mapHelper.requestDistance(url));
                        double dis = JsonHelper.getDistance(res);
                        if (Double.isNaN(dis)) {
                            String error = "Please Check your google api key\n";
                            error += JsonHelper.getErrorMsg(res);
                            Log.e(TAG, error);
//                            sendException(new IllegalAccessException(error));
                            isRunning = false;
                            stopSelf();
                        }
                        if (dis < nearestDis) {
                            nearestDis = dis;
                            nearestItem = trackable;
                            nearestDisStr = JsonHelper.getDistanceStr(res);
                            nearestDuration = JsonHelper.getDurationStr(res);
                        }
                    }
                }
                if (nearestItem == null) {
                    continue;
                }
                String msg = String.format("There is trackable around you, would you like to tracking it?\nName:%s\nDistance:%s\nWalking Duration:%s"
                        , nearestItem.getName(), nearestDisStr, nearestDuration);
                Log.i(TAG, msg);
                sendMsg(nearestItem.getId(), msg);
            }
        } catch (InterruptedException | JSONException | IOException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
    }

    public void sendMsg(int nearestId, String msg) {
        Intent intent = new Intent(TrackableFragment.SUGGESTION_REPLY);
        intent.putExtra(TrackableFragment.SUGGESTION_MSG, msg);
        intent.putExtra(TrackableFragment.SUGGESTION_TRACKABLE_ID, nearestId);
        mLocalBroadcastManager.sendBroadcast(intent);
    }

    public void sendException(Exception e) {
        Intent intent = new Intent(TrackableFragment.SUGGESTION_ERROR);
        intent.putExtra(TrackableFragment.SUGGESTION_MSG, e.getLocalizedMessage());
        mLocalBroadcastManager.sendBroadcast(intent);
    }
}
