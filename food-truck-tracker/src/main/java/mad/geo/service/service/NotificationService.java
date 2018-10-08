package mad.geo.service.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import mad.geo.model.AbstractTracking;
import mad.geo.service.TrackableService;
import mad.geo.view.activity.SettingsActivity;
import mad.geo.view.fragment.TrackingFragment;

import static mad.geo.view.fragment.TrackingFragment.NOTIFICATION_MSG;
import static mad.geo.view.fragment.TrackingFragment.NOTIFICATION_TITLE;

/**
 * @author : Chenglong Ma
 */
public class NotificationService extends IntentService {
    private static final String TAG = NotificationService.class.getName();
    private boolean isRunning = true;
    private LocalBroadcastManager mLocalBroadcastManager;
    private TrackableService trackableService;
    private Map<String, Double> reminderMap = new HashMap<>();
    private SharedPreferences preferences;

    public NotificationService() {
        super("NotificationService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        trackableService = TrackableService.getSingletonInstance(this);
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            isRunning = true;
            while (isRunning) {
                Thread.sleep(1000);//TODO
                double reminderMins = Double.parseDouble(preferences.getString(SettingsActivity.NOTIFICATION_TIME, "1"));
                Log.i(TAG, "Reminder time reads from preference: " + reminderMins);
                for (AbstractTracking tracking : trackableService.getTrackings()) {
                    String id = tracking.getTrackingId();
                    if (tracking.getMeetTime().getTime() <= System.currentTimeMillis() + 1000 * reminderMins) {
                        double mins = reminderMins;
                        if (reminderMap.containsKey(id)) {
                            mins = reminderMap.getOrDefault(id, reminderMins);
                            if (mins == -1) {
                                continue;
                            }
                        } else {
                            reminderMap.put(id, reminderMins);
                        }
                        remindMe(tracking.getTitle(), mins);
                    }
                }
            }
        } catch (InterruptedException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
    }

    private void remindMe(String title, double mins) {
        Intent msgIntent = new Intent(TrackingFragment.ACTION_TYPE_SERVICE);
        String msg = String.format(Locale.getDefault(), "It's time for your tracking activity: %s, in %.3f mins", title, mins);
        msgIntent.putExtra(NOTIFICATION_MSG, msg);
        msgIntent.putExtra(NOTIFICATION_TITLE, "Time to go");
        mLocalBroadcastManager.sendBroadcast(msgIntent);
    }

    // 发送服务状态信息
    private void sendServiceStatus(String status) {
//        Intent intent = new Intent(TrackingFragment.ACTION_TYPE_SERVICE);
//        intent.putExtra("status", status);
//        mLocalBroadcastManager.sendBroadcast(intent);
    }

    // 发送线程状态信息
    private void sendThreadStatus(String status, int progress) {
//        Intent intent = new Intent(TrackingFragment.ACTION_TYPE_THREAD);
//        intent.putExtra("status", status);
//        intent.putExtra("progress", progress);
//        mLocalBroadcastManager.sendBroadcast(intent);
    }
}