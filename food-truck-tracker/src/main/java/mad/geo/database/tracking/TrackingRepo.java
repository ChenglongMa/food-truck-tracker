package mad.geo.database.tracking;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import mad.geo.model.AbstractTracking;
import mad.geo.model.MealEvent;

import static android.content.ContentValues.TAG;
import static mad.geo.utils.DateHelper.toDate;

public class TrackingRepo {
    /**
     * 表名
     */
    public static final String TABLE_NAME = "tb_tracking";
    /**
     * 创建表的语句
     */
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + TrackingColumns.TRACKING_ID + " TEXT PRIMARY KEY,"
            + TrackingColumns.TRACKABLE_ID + " INTEGER,"
            + TrackingColumns.TITLE + " TEXT,"
            + TrackingColumns.START_TIME + " TEXT,"
            + TrackingColumns.END_TIME + " TEXT,"
            + TrackingColumns.MEET_TIME + " TEXT,"
            + TrackingColumns.CURR_LOCATION + " TEXT,"
            + TrackingColumns.MEET_LOCATION + " TEXT"
            + ")";
    /**
     * 删除表的语句
     */
    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    /**
     * 所有的字段
     */
    private static final String[] AVAILABLE_PROJECTION = new String[]{
            TrackingColumns.TRACKING_ID,
            TrackingColumns.TRACKABLE_ID,
            TrackingColumns.TITLE,
            TrackingColumns.START_TIME,
            TrackingColumns.END_TIME,
            TrackingColumns.MEET_TIME,
            TrackingColumns.CURR_LOCATION,
            TrackingColumns.MEET_LOCATION
    };


    /**
     * 判断下所请求的字段是否都存在，分支出现操作的字段不存在的错误
     */
    private static void checkColumns(String[] projection) {
        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<>(Arrays.asList(AVAILABLE_PROJECTION));
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException(TAG + "checkColumns()-> Unknown columns in projection");
            }
        }
    }

    /**
     * 记录是否存在
     */
    static boolean isExist(SQLiteOpenHelper helper, String trackingId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, AVAILABLE_PROJECTION,
                TrackingColumns.TRACKING_ID + " =? ",
                new String[]{trackingId}, null, null, null);
        if (cursor.moveToFirst()) {
            Log.d(TAG, "Found Item");
            cursor.close();
            return true;
        } else {
            Log.d(TAG, "Didn't find Item");
            cursor.close();
            return false;
        }
    }

    /**
     * 查询所有的学生
     */
    static List<AbstractTracking> queryAll(SQLiteOpenHelper helper) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, AVAILABLE_PROJECTION, null, null, null, null, null, null);
        List<AbstractTracking> trackings = new ArrayList<>();
        while (cursor.moveToNext()) {
            trackings.add(getTrackingFromCursor(cursor));
        }
        cursor.close();
        return trackings;
    }

    /**
     * 查询某个学生
     */
    static AbstractTracking query(SQLiteOpenHelper helper, String trackingId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, AVAILABLE_PROJECTION,
                TrackingColumns.TRACKING_ID + " =? ", new String[]{trackingId}, null, null, null, null);
        AbstractTracking tracking = null;
        if (cursor != null) {
            cursor.moveToFirst();
            tracking = getTrackingFromCursor(cursor);
        }
        return tracking;
    }

    /**
     * 更新学生对象
     */
    static void update(SQLiteOpenHelper helper, AbstractTracking tracking) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.update(TABLE_NAME, toContentValues(tracking),
                TrackingColumns.TRACKING_ID + " =? ",
                new String[]{String.valueOf(tracking.getTrackingId())});
    }

    /**
     * 插入新数据
     */
    static void insert(SQLiteOpenHelper helper, AbstractTracking tracking) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.insertOrThrow(TABLE_NAME, null, toContentValues(tracking));
    }

    static void insert(SQLiteDatabase db, AbstractTracking tracking) {
        db.insert(TABLE_NAME, null, toContentValues(tracking));
    }

    /**
     * 插入新数据，如果已经存在就替换
     */
    static void insertOrReplace(SQLiteOpenHelper helper, AbstractTracking tracking) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.insertWithOnConflict(TABLE_NAME, null,
                toContentValues(tracking),
                SQLiteDatabase.CONFLICT_REPLACE);
    }

    /**
     * 删除某条记录
     */
    static void delete(SQLiteOpenHelper helper, String trackingId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(TABLE_NAME, TrackingColumns.TRACKING_ID + " =? ",
                new String[]{trackingId});
    }

    /**
     * 清空学生表
     */
    static void clear(SQLiteOpenHelper helper) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
    }

    /**
     * 将对象保证成ContentValues
     */
    private static ContentValues toContentValues(AbstractTracking tracking) {
        ContentValues values = new ContentValues();
        values.put(TrackingColumns.TRACKING_ID, tracking.getTrackingId());
        values.put(TrackingColumns.TRACKABLE_ID, tracking.getTrackableId());
        values.put(TrackingColumns.TITLE, tracking.getTitle());
        values.put(TrackingColumns.START_TIME, tracking.getStartTimeStr());
        values.put(TrackingColumns.END_TIME, tracking.getEndTimeStr());
        values.put(TrackingColumns.MEET_TIME, tracking.getMeetTimeStr());
        values.put(TrackingColumns.CURR_LOCATION, tracking.getCurrLocation());
        values.put(TrackingColumns.MEET_LOCATION, tracking.getMeetLocation());
        return values;
    }

    /**
     * 将学生对象从Cursor中取出
     */
    private static AbstractTracking getTrackingFromCursor(Cursor cursor) {
        AbstractTracking tracking = new MealEvent();
        tracking.setTrackingId(cursor.getString(cursor.getColumnIndex(TrackingColumns.TRACKING_ID)));
        tracking.setTrackableId(cursor.getInt(cursor.getColumnIndex(TrackingColumns.TRACKABLE_ID)));
        tracking.setTitle(cursor.getString(cursor.getColumnIndex(TrackingColumns.TITLE)));
        try {
            tracking.setTarStartTime(toDate(cursor.getString(cursor.getColumnIndex(TrackingColumns.START_TIME))));
            tracking.setTarEndTime(toDate(cursor.getString(cursor.getColumnIndex(TrackingColumns.END_TIME))));
            tracking.setMeetTime(toDate(cursor.getString(cursor.getColumnIndex(TrackingColumns.MEET_TIME))));
        } catch (ParseException e) {
            Log.i(TAG, "Date conversion failure: " + e.getMessage());
        }
        tracking.setCurrLocation(cursor.getString(cursor.getColumnIndex(TrackingColumns.CURR_LOCATION)));
        tracking.setMeetLocation(cursor.getString(cursor.getColumnIndex(TrackingColumns.MEET_LOCATION)));
        return tracking;
    }
}
