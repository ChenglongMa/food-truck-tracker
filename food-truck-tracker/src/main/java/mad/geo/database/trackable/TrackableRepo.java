package mad.geo.database.trackable;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import mad.geo.model.AbstractTrackable;
import mad.geo.model.FoodTruck;

import static android.content.ContentValues.TAG;

public class TrackableRepo {
    /**
     * 表名
     */
    public static final String TABLE_NAME = "tb_trackable";
    /**
     * 创建表的语句
     */
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + TrackableColumns._ID + " INTEGER PRIMARY KEY,"
            + TrackableColumns.NAME + " TEXT,"
            + TrackableColumns.URL + " TEXT,"
            + TrackableColumns.CATEGORY + " TEXT,"
            + TrackableColumns.IMAGE + " INTEGER,"
            + TrackableColumns.DESCRIPTION + " TEXT"
            + ")";
    /**
     * 删除表的语句
     */
    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    /**
     * 所有的字段
     */
    private static final String[] AVAILABLE_PROJECTION = new String[]{
            TrackableColumns._ID,
            TrackableColumns.NAME,
            TrackableColumns.URL,
            TrackableColumns.CATEGORY,
            TrackableColumns.IMAGE,
            TrackableColumns.DESCRIPTION,
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
    public static boolean isExist(SQLiteOpenHelper helper, String trackableId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, AVAILABLE_PROJECTION,
                TrackableColumns._ID + " =? ",
                new String[]{trackableId}, null, null, null);
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
    public static List<AbstractTrackable> queryAll(SQLiteOpenHelper helper) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, AVAILABLE_PROJECTION, null, null, null, null, null, null);
        List<AbstractTrackable> trackables = new ArrayList<>();
        while (cursor.moveToNext()) {
            trackables.add(getTrackableFromCursor(cursor));
        }
        cursor.close();
        return trackables;
    }

    /**
     * 查询某个学生
     */
    public static AbstractTrackable query(SQLiteOpenHelper helper, String trackableId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, AVAILABLE_PROJECTION,
                TrackableColumns._ID + " =? ", new String[]{trackableId}, null, null, null, null);
        AbstractTrackable trackable = null;
        if (cursor != null) {
            cursor.moveToFirst();
            trackable = getTrackableFromCursor(cursor);
        }
        return trackable;
    }

    /**
     * 更新学生对象
     */
    public static void update(SQLiteOpenHelper helper, AbstractTrackable trackable) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.update(TABLE_NAME, toContentValues(trackable),
                TrackableColumns._ID + " =? ",
                new String[]{String.valueOf(trackable.getId())});
    }

    /**
     * 插入新数据
     */
    public static void insert(SQLiteOpenHelper helper, AbstractTrackable trackable) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.insertOrThrow(TABLE_NAME, null, toContentValues(trackable));
    }

    public static void insert(SQLiteDatabase db, AbstractTrackable trackable) {
        db.insert(TABLE_NAME, null, toContentValues(trackable));
    }

    /**
     * 插入新数据，如果已经存在就替换
     */
    public static void insertOrReplace(SQLiteOpenHelper helper, AbstractTrackable trackable) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.insertWithOnConflict(TABLE_NAME, null,
                toContentValues(trackable),
                SQLiteDatabase.CONFLICT_REPLACE);
    }

    /**
     * 删除某条记录
     */
    public static void delete(SQLiteOpenHelper helper, String trackableId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(TABLE_NAME, TrackableColumns._ID + " =? ", new String[]{trackableId});
    }

    /**
     * 清空学生表
     */
    public static void clear(SQLiteOpenHelper helper) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
    }

    /**
     * 将对象保证成ContentValues
     */
    private static ContentValues toContentValues(AbstractTrackable trackable) {
        ContentValues values = new ContentValues();
        values.put(TrackableColumns._ID, trackable.getId());
        values.put(TrackableColumns.NAME, trackable.getName());
        values.put(TrackableColumns.URL, trackable.getUrl());
        values.put(TrackableColumns.CATEGORY, trackable.getCategory());
        values.put(TrackableColumns.IMAGE, trackable.getImage());
        values.put(TrackableColumns.DESCRIPTION, trackable.getDescription());
        return values;
    }

    /**
     * 将学生对象从Cursor中取出
     */
    private static AbstractTrackable getTrackableFromCursor(Cursor cursor) {
        AbstractTrackable trackable = new FoodTruck();
        trackable.setId(cursor.getInt(cursor.getColumnIndex(TrackableColumns._ID)));
        trackable.setName(cursor.getString(cursor.getColumnIndex(TrackableColumns.NAME)));
        trackable.setCategory(cursor.getString(cursor.getColumnIndex(TrackableColumns.CATEGORY)));
        trackable.setUrl(cursor.getString(cursor.getColumnIndex(TrackableColumns.URL)));
        trackable.setImage(cursor.getInt(cursor.getColumnIndex(TrackableColumns.IMAGE)));
        return trackable;
    }

    public static List<AbstractTrackable> queryByCategory(SQLiteOpenHelper dbHelper, String... keys) {
        return null;
    }
}
