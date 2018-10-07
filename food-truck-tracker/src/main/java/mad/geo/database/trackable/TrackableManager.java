package mad.geo.database.trackable;

import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;
import java.util.Scanner;

import mad.geo.R;
import mad.geo.model.AbstractTrackable;
import mad.geo.model.FoodTruck;
import mad.geo.utils.DBOpenHelper;

import static android.content.ContentValues.TAG;


/**
 * 方便对AbstractTrackable表的访问
 * <p>
 * Created by michael on 2017/9/22.
 */
public class TrackableManager {
    private static Context context;
    private final SQLiteOpenHelper dbHelper;

    private TrackableManager() {
        this.dbHelper = DBOpenHelper.getSingletonInstance(context);
    }

    public static TrackableManager getInstance(Context context) {
        TrackableManager.context = context;
        return LazyHolder.INSTANCE;
    }

    /**
     * 记录是否存在
     */
    public boolean isExist(String trackableId) {
        if (TextUtils.isEmpty(trackableId)) {
            return false;
        }
        return TrackableRepo.isExist(dbHelper, trackableId);
    }

    /**
     * 查询所有学生
     */
    public List<AbstractTrackable> queryAll() {
        return TrackableRepo.queryAll(dbHelper);
    }

    /**
     * 查询某个学生
     */
    public AbstractTrackable query(String trackableId) {
        if (TextUtils.isEmpty(trackableId)) {
            return null;
        }
        return TrackableRepo.query(dbHelper, trackableId);
    }

    /**
     * 插入一条数据
     */
    public void insert(AbstractTrackable trackable) {
        if (trackable == null) {
            return;
        }
        TrackableRepo.insert(dbHelper, trackable);
    }

    /**
     * 插入某条记录，如果已经存在就覆盖
     */
    public void insertOrReplace(AbstractTrackable trackable) {
        if (trackable == null) {
            return;
        }
        TrackableRepo.insertOrReplace(dbHelper, trackable);
    }

    /**
     * 更新某条记录
     */
    public void update(AbstractTrackable trackable) {
        if (trackable == null) {
            return;
        }
        TrackableRepo.update(dbHelper, trackable);
    }

    /**
     * 删除某条记录
     */
    public void delete(String trackableId) {
        if (TextUtils.isEmpty(trackableId)) {
            return;
        }
        TrackableRepo.delete(dbHelper, trackableId);
    }

    /**
     * 清空表
     */
    public void clear() {
        TrackableRepo.clear(dbHelper);
    }

    public void initData(SQLiteDatabase db) {
        Log.i(TAG, "Database Transaction Start");
        db.beginTransaction();
        //context.getResources().openRawResource(R.raw.food_truck_data)
        try (Scanner scanner = new Scanner(context.getResources().openRawResource(R.raw.food_truck_data))) {
            Log.i(TAG, "Database Transaction?  " + db.inTransaction());
            Log.i(TAG, "Database Locked by current thread?  "
                    + db.isDbLockedByCurrentThread());
            // match comma and 0 or more whitespace OR trailing space and newline
            scanner.useDelimiter("\"?\\s*,\\s*\"|\"\\s*\\n+");
            while (scanner.hasNext()) {
                AbstractTrackable trackable = new FoodTruck();
                trackable.setId(scanner.nextInt());
                trackable.setName(scanner.next());
                trackable.setDescription(scanner.next());
                trackable.setUrl(scanner.next());
                trackable.setCategory(scanner.next());
                insert(db, trackable);
            }
            db.setTransactionSuccessful();
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "File Not Found Exception Caught");
        } catch (Exception e) {
            Log.e(TAG, "Transaction failed. Exception: " + e.getMessage());
        } finally {
            db.endTransaction();
        }
        Log.i(TAG, "Database Transaction End");
    }

    private void insert(SQLiteDatabase db, AbstractTrackable trackable) {
        if (trackable == null) {
            return;
        }
        TrackableRepo.insert(db, trackable);
    }

    public List<AbstractTrackable> queryByCategory(String... keys) {
        return TrackableRepo.queryByCategory(dbHelper, keys);
    }

    private static class LazyHolder {
        static final TrackableManager INSTANCE = new TrackableManager();
    }

}
