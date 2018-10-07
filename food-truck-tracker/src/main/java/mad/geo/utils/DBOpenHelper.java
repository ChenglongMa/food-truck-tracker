package mad.geo.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import mad.geo.database.trackable.TrackableManager;
import mad.geo.database.trackable.TrackableRepo;
import mad.geo.database.tracking.TrackingManager;
import mad.geo.database.tracking.TrackingRepo;

/**
 * @author : Chenglong Ma
 */
public class DBOpenHelper extends SQLiteOpenHelper {
    private static final String LOG_TAG = DBOpenHelper.class.getName();
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "tracker.db";

    private static DBOpenHelper singletonInstance;
    private Context context;

    private DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public static DBOpenHelper getSingletonInstance(Context context) {
        if (singletonInstance == null)
            singletonInstance = new DBOpenHelper(
                    context.getApplicationContext());

        return singletonInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Log some information about our database
        Log.i(LOG_TAG, "Created database: " + db.getPath());
        Log.i(LOG_TAG, "Database Version: " + db.getVersion());
        Log.i(LOG_TAG, "Database Page Size: " + db.getPageSize());
        Log.i(LOG_TAG, "Database Max Size: " + db.getMaximumSize());

        Log.i(LOG_TAG, "Database Open?  " + db.isOpen());
        Log.i(LOG_TAG, "Database readonly?  " + db.isReadOnly());
        Log.i(LOG_TAG, "Database Locked by current thread?  "
                + db.isDbLockedByCurrentThread());

        // CREATE TABLES
        Log.i(LOG_TAG, "Create the 'trackable' table using execSQL()");
        db.execSQL(TrackableRepo.CREATE_TABLE);

        Log.i(LOG_TAG, "Create the 'tracking' table using execSQL()");
        db.execSQL(TrackingRepo.CREATE_TABLE);
        // Add some records (within a transaction) .. note use of helper class
        TrackableManager.getInstance(context).initData(db);
        Log.i(LOG_TAG, "Database Transaction?  " + db.inTransaction());
        Log.i(LOG_TAG,
                "Database Locked by current thread?  "
                        + db.isDbLockedByCurrentThread());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //not used
    }
}
