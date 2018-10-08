package mad.geo.service;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import mad.geo.R;
import mad.geo.database.trackable.TrackableManager;
import mad.geo.database.tracking.TrackingManager;
import mad.geo.model.AbstractTrackable;
import mad.geo.model.AbstractTracking;
import mad.geo.model.FoodTruck;
import mad.geo.model.MealEvent;

import static mad.geo.utils.DateHelper.advancedDate;
import static mad.geo.utils.DateHelper.dateToString;
import static mad.geo.utils.DateHelper.toDate;

/**
 * The trackable service to access data
 */
public class TrackableService {
    /**
     * The filter to get all instances
     */
    public static final String FILTER_ALL = "All";
    private static final String LOG_TAG = TrackableService.class.getName();
    private static Context context;
    private final TrackingService trackingService = TrackingService.getSingletonInstance(context);
    private final TrackableManager trackableManager = TrackableManager.getInstance(context);
    private final TrackingManager trackingManager = TrackingManager.getInstance(context);
    private List<AbstractTrackable> trackables = new ArrayList<>();
    private List<TrackingService.TrackingInfo> trackingInfos = new ArrayList<>();
    private List<AbstractTracking> trackings = new ArrayList<>();

    private TrackableService() {

    }

    public static TrackableService getSingletonInstance(Context context) {
        TrackableService.context = context;
        return LazyHolder.INSTANCE;
    }

    /**
     * Get the Food Truck information from the data source
     *
     * @return
     */
    @Deprecated
    private static List<AbstractTrackable> parseFoodTruck() {
        List<AbstractTrackable> res = new ArrayList<>();
        try (Scanner scanner = new Scanner(context.getResources().openRawResource(R.raw.food_truck_data))) {
            // match comma and 0 or more whitespace OR trailing space and newline
            scanner.useDelimiter("\"?\\s*,\\s*\"|\"\\s*\\n+");
            while (scanner.hasNext()) {
                AbstractTrackable trackable = new FoodTruck();
                trackable.setId(scanner.nextInt());
                trackable.setName(scanner.next());
                trackable.setDescription(scanner.next());
                trackable.setUrl(scanner.next());
                trackable.setCategory(scanner.next());
                res.add(trackable);
            }
        } catch (Resources.NotFoundException e) {
            Log.i(LOG_TAG, "File Not Found Exception Caught");
        }
        return res;
    }

    /**
     * Fill data into the database
     */


    public void addTrackable(AbstractTrackable trackable) {
        trackableManager.insert(trackable);
        Log.i(LOG_TAG, "Added Trackable:  " + trackable.getName() + "(ID="
                + trackable.getId() + ")");
    }

    public void removeTrackable(AbstractTrackable trackable) {
        trackableManager.delete(trackable.getIdString());
        Log.i(LOG_TAG, "Remove Trackable:  " + trackable.getName() + "(ID="
                + trackable.getId() + ")");
    }


    public void updateTrackable(AbstractTrackable trackable) {
        trackableManager.update(trackable);
        Log.i(LOG_TAG, "Update Trackable:  " + trackable.getName() + "(ID="
                + trackable.getId() + ")");
    }

    /**
     * Get End Time information by trackable
     *
     * @param trackable
     * @return
     */
    public List<String> getEndTimes(AbstractTrackable trackable) {
        List<String> times = new ArrayList<>();
        for (TrackingService.TrackingInfo info : getTrackingInfo(trackable)) {
            String endTime = dateToString(advancedDate(info.date, info.stopTime));
            times.add(endTime);
        }
        return times;
    }

    /**
     * Get location information by trackable
     *
     * @param trackable
     * @return
     */
    public List<String> getLocations(AbstractTrackable trackable) {
        List<String> locations = new ArrayList<>();
        for (TrackingService.TrackingInfo tracking : getTrackingInfo(trackable)) {
            if (tracking.stopTime > 0) {
                String location = String.format(Locale.getDefault(), "%f, %f", tracking.latitude, tracking.longitude);
                locations.add(location);
            }
        }
        return locations;
    }

    /**
     * Get Start Time information by trackable
     *
     * @param trackable
     * @return
     */
    public List<String> getStartTimes(AbstractTrackable trackable) {
        List<String> times = new ArrayList<>();
        for (TrackingService.TrackingInfo info : getTrackingInfo(trackable)) {
            String startTime = dateToString(info.date);
            times.add(startTime);
        }
        return times;
    }

    @Deprecated
    public List<AbstractTracking> getInitTrackings() {
        List<AbstractTracking> res = new ArrayList<>();
        parseTrackingData();
        for (TrackingService.TrackingInfo info : trackingInfos) {
            AbstractTracking tracking = new MealEvent();
            tracking.setTarStartTime(info.date);
            tracking.setTarEndTime(advancedDate(info.date, info.stopTime));
            tracking.setMeetLocation(info.latitude, info.longitude);
            tracking.setTitle("Default");
            tracking.setTrackableId(info.trackableId);
            res.add(tracking);
        }
        return res;
    }

    public List<TrackingService.TrackingInfo> getTrackingInfo(AbstractTrackable trackable) {
        List<TrackingService.TrackingInfo> infos = new ArrayList<>();
        parseTrackingData();
        for (TrackingService.TrackingInfo tracking : trackingInfos) {
            if (tracking.trackableId != trackable.getId()) {
                continue;
            }
            infos.add(tracking);
        }
        return infos;
    }

    /**
     * Filter the tracking info by {@link AbstractTrackable} and location
     *
     * @param trackable
     * @param location
     * @return
     */
    public List<String> getStartTimes(AbstractTrackable trackable, String location) {
        List<String> times = new ArrayList<>();
        String[] locations = location.split(",");
        for (TrackingService.TrackingInfo tracking : getTrackingInfo(trackable)) {
            if (tracking.latitude == Double.parseDouble(locations[0]) && tracking.longitude == Double.parseDouble(locations[1])) {
                String startTime = dateToString(tracking.date);
                times.add(startTime);
            }
        }
        return times;
    }

    /**
     * Filter the tracking info by {@link AbstractTrackable} and location
     *
     * @param trackable
     * @param location
     * @return
     */
    public List<String> getEndTimes(AbstractTrackable trackable, String location) {
        List<String> times = new ArrayList<>();
        String[] locations = location.split(",");
        for (TrackingService.TrackingInfo tracking : getTrackingInfo(trackable)) {
            if (tracking.latitude == Double.parseDouble(locations[0]) && tracking.longitude == Double.parseDouble(locations[1])) {
                String endTime = dateToString(advancedDate(tracking.date, tracking.stopTime));
                times.add(endTime);
            }
        }
        return times;
    }

    public List<AbstractTrackable> getTrackables() {
        return trackableManager.queryAll();
    }

    @Deprecated//TODO：AsyncTaskToRead
    private void parseTrackingData() {
        try {
            String searchDate = "11/10/2018 1:00:00 PM";//TODO: change time
            int searchWindow = 24 * 60;
            Date date = toDate(searchDate);
            trackingInfos = trackingService.getTrackingInfoForTimeRange(date, searchWindow, 0);
        } catch (ParseException e) {
            Log.e(LOG_TAG, "ParseException Caught (Incorrect File Format)");
        }
    }

    public AbstractTrackable getTrackableById(final int id) {
        return trackableManager.query(String.valueOf(id));
    }

    public List<AbstractTrackable> getTrackablesByCategory(final String... keys) {
        if (keys == null || keys.length <= 0 || Arrays.asList(keys).contains(FILTER_ALL)) {
            return trackableManager.queryAll();
        }
        return trackableManager.queryByCategory(keys);
    }

    public List<LatLng> getRouteInfo(int trackableId) {
        List<LatLng> res = new ArrayList<>();
        parseTrackingData();
        for (TrackingService.TrackingInfo info : trackingInfos) {
            if (info.trackableId == trackableId) {
                res.add(new LatLng(info.latitude, info.longitude));
            }
        }
        return res;
    }

    private String getDisRequestUrl(LatLng origin, LatLng dest) {
        String originParam = String.format(Locale.getDefault(),
                "origin=%f,%f&",
                origin.latitude, origin.longitude);
        String destParam = String.format(Locale.getDefault(),
                "destination=%f,%f&",
                dest.latitude, dest.longitude);
        String output = "json?";
        String mode = "mode=walking&";
        String key = "key=" + context.getText(R.string.google_maps_key);
        String param = originParam + destParam + mode + key;
        return context.getText(R.string.distance_url) + output + param;
    }

    public void updateTracking(AbstractTracking tracking) {
        trackingManager.update(tracking);
    }

    public void addTracking(AbstractTracking tracking) {
        trackingManager.insert(tracking);
    }

    public void removeTracking(AbstractTracking tracking) {
        trackingManager.delete(tracking.getTrackingId());
    }

    public List<AbstractTracking> getTrackings() {
        trackings = trackingManager.queryAll();
        trackings.sort(Comparator.comparing(AbstractTracking::getMeetTime));
        return trackings;
    }

    private static class LazyHolder {
        static final TrackableService INSTANCE = new TrackableService();
    }
}
