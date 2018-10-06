package mad.geo.service;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import mad.geo.R;
import mad.geo.model.AbstractTrackable;
import mad.geo.model.FoodTruck;
import mad.geo.service.interfaces.FilterStrategy;

/**
 * GeoTracking
 *
 * @author : Charles Ma
 * @date : 30-08-2018
 * @time : 16:54
 * @description :
 */
public class TrackableService {
    private static final String LOG_TAG = TrackableService.class.getName();
    private List<AbstractTrackable> trackables = new ArrayList<>();
    private static Context context;

    private TrackableService() {
//        parseFile(context);
    }

    private static class LazyHolder {
        static final TrackableService INSTANCE = new TrackableService();
    }

    public static TrackableService getSingletonInstance(Context context) {
        TrackableService.context = context;
        return LazyHolder.INSTANCE;
    }

    private void parseFile(Context context) {
        trackables.clear();
        // resource reference to tracking_data.txt in res/raw/ folder of your project
        // supports trailing comments with //
        try (Scanner scanner = new Scanner(context.getResources().openRawResource(R.raw.food_truck_data))) {
            // match comma and 0 or more whitespace OR trailing space and newline
            scanner.useDelimiter("\"?\\s*,\\s*\"|\"\\s*\\n+");
            while (scanner.hasNext()) {
                AbstractTrackable trackable = new FoodTruck();
                trackable.setId(scanner.nextInt());
                trackable.setName(scanner.next());
                trackable.setDescription(scanner.next());
                trackable.setWebSiteUrl(scanner.next());
                trackable.setCategory(scanner.next());
                trackables.add(trackable);
            }
        } catch (Resources.NotFoundException e) {
            Log.i(LOG_TAG, "File Not Found Exception Caught");
        }
//        catch (ParseException e)
//        {
//            Log.i(LOG_TAG, "ParseException Caught (Incorrect File Format)");
//        }

    }

    public AbstractTrackable getTrackableById(final int id) {
        parseFile(context);
        return trackables.stream().filter(new Predicate<AbstractTrackable>() {
            @Override
            public boolean test(AbstractTrackable t) {
                return t.getId() == id;
            }
        }).findFirst().orElse(null);
    }

    public List<AbstractTrackable> getTrackablesByCategory(final String... cates) {
        parseFile(context);
        List<AbstractTrackable> res = new ArrayList<>();
        List<String> cons = Arrays.asList(cates);
        for (AbstractTrackable trackable : trackables) {
            if (cons.contains(trackable.getCategory())) {
                res.add(trackable);
            }
        }
        return res;
    }

    public String getRouteInfo(AbstractTrackable trackable) {

    }

    private class FilterByCategory implements FilterStrategy<String> {

        @Override
        public List<AbstractTrackable> filter(String... conditions) {
            List<AbstractTrackable> res = new ArrayList<>();
            List<String> cons = Arrays.asList(conditions);
            for (AbstractTrackable trackable : trackables) {
                if (cons.contains(trackable.getCategory())) {
                    res.add(trackable);
                }
            }
            return res;
        }
    }

    private class FilterByDuration implements FilterStrategy<Date> {

        @Override
        public List<AbstractTrackable> filter(Date... conditions) {
            return null;
        }
    }

}
