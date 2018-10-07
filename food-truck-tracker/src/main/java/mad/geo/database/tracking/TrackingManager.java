package mad.geo.database.tracking;

import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

import mad.geo.model.AbstractTracking;
import mad.geo.service.TrackableService;
import mad.geo.utils.DBOpenHelper;


public class TrackingManager {
    private static final String LOG_TAG = TrackingManager.class.getName();
    private static Context context;
    private final SQLiteOpenHelper dbHelper;

    private TrackingManager() {
        this.dbHelper = DBOpenHelper.getSingletonInstance(context);
    }

    public static TrackingManager getInstance(Context context) {
        TrackingManager.context = context;
        return LazyHolder.INSTANCE;
    }

    /**
     * 记录是否存在
     */
    public boolean isExist(String trackingId) {
        if (TextUtils.isEmpty(trackingId)) {
            return false;
        }
        return TrackingRepo.isExist(dbHelper, trackingId);
    }

    /**
     * 查询所有学生
     */
    public List<AbstractTracking> queryAll() {
        return TrackingRepo.queryAll(dbHelper);
    }

    /**
     * 查询某个学生
     */
    public AbstractTracking query(String trackingId) {
        if (TextUtils.isEmpty(trackingId)) {
            return null;
        }
        return TrackingRepo.query(dbHelper, trackingId);
    }

    /**
     * 插入一条数据
     */
    public void insert(AbstractTracking tracking) {
        if (tracking == null) {
            return;
        }
        TrackingRepo.insert(dbHelper, tracking);
    }

    /**
     * 插入某条记录，如果已经存在就覆盖
     */
    public void insertOrReplace(AbstractTracking tracking) {
        if (tracking == null) {
            return;
        }
        TrackingRepo.insertOrReplace(dbHelper, tracking);
    }

    /**
     * 更新某条记录
     */
    public void update(AbstractTracking tracking) {
        if (tracking == null) {
            return;
        }
        TrackingRepo.update(dbHelper, tracking);
    }

    /**
     * 删除某条记录
     */
    public void delete(String trackingId) {
        if (TextUtils.isEmpty(trackingId)) {
            return;
        }
        TrackingRepo.delete(dbHelper, trackingId);
    }

    /**
     * 清空表
     */
    public void clear() {
        TrackingRepo.clear(dbHelper);
    }

    @Deprecated
    public void initData(SQLiteDatabase db) {

//        TrackingService.getSingletonInstance(context).getTrackingInfoForTimeRange()
        Log.i(LOG_TAG, "Database Transaction Start");
        db.beginTransaction();
        try {
            Log.i(LOG_TAG, "Database Transaction?  " + db.inTransaction());
            Log.i(LOG_TAG, "Database Locked by current thread?  "
                    + db.isDbLockedByCurrentThread());
            List<AbstractTracking> trackings = TrackableService.getSingletonInstance(context).getInitTrackings();
            for (AbstractTracking tracking : trackings) {
                insert(db, tracking);
            }
            db.setTransactionSuccessful();
        } catch (Resources.NotFoundException e) {
            Log.e(LOG_TAG, "File Not Found Exception Caught");
        } catch (Exception e) {
            Log.e(LOG_TAG, "Transaction failed. Exception: " + e.getMessage());
        } finally {
            db.endTransaction();
        }
        Log.i(LOG_TAG, "Database Transaction End");
    }

    private void insert(SQLiteDatabase db, AbstractTracking tracking) {
        if (tracking == null) {
            return;
        }
        TrackingRepo.insert(db, tracking);
    }

    private static class LazyHolder {
        static final TrackingManager INSTANCE = new TrackingManager();
    }
}
