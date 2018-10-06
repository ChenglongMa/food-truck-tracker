package mad.geo.database.tracking;

import android.provider.BaseColumns;

/**
 * @author : Chenglong Ma
 */
public interface TrackingColumns extends BaseColumns {
    String TRACKING_ID      = "id";
    String TRACKABLE_ID     = "tid";
    String TITLE            = "title";
    String START_TIME       = "start_time";
    String END_TIME         = "end_time";
    String MEET_TIME        = "meet_time";
    String CURR_LOCATION    = "corr_location";
    String MEET_LOCATION    = "meet_location";
}
