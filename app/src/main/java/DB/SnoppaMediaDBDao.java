package DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


import com.pvj.xlibrary.log.Logger;

import java.util.ArrayList;
import java.util.List;

import bean.SnoppaMedia;


/**
 * author: jk
 * created on: 2016/11/8 16:51
 * description: 媒体库的数据库操作类
 */
public class SnoppaMediaDBDao {
    private static final String TAG = SnoppaMediaDBDao.class.getSimpleName();
    public static final String SNOPPA_MEDIA = "snoppa_media_info";//数据表名称

    public static final String KEY_ID = "id";
    public static final String KEY_URL = "url";
    public static final String KEY_FAVORITE = "favorite";
    public static final String KEY_EXIST = "exist";


    private SQLiteDatabase mDatabase;
    private Context mContext;
    private DBOpenHelper mDbOpenHelper;//数据库打开帮助类


    public SnoppaMediaDBDao(Context context) {
        mContext = context;
    }

    //打开数据库
    public void openDataBase() {
        mDbOpenHelper = new DBOpenHelper(mContext, DBConstants.DB_NAME, null, DBConstants.DB_VERSION);
        try {
            mDatabase = mDbOpenHelper.getWritableDatabase();//获取可写数据库
        } catch (SQLException e) {
            mDatabase = mDbOpenHelper.getReadableDatabase();//获取只读数据库
        }
    }

    //关闭数据库
    public void closeDataBase() {
        if (mDatabase != null) {
            mDatabase.close();
        }
    }

    //插入一条数据
    public long insertData(SnoppaMedia snoppaMedia) {
        long insert = 0;
//        List<SnoppaMedia> snoppaMedias = queryData(snoppaMedia.url);
//        if (snoppaMedias==null&&snoppaMedias.size()<1){
        ContentValues values = new ContentValues();
        values.put(KEY_FAVORITE, snoppaMedia.isFavorite);
        values.put(KEY_EXIST, snoppaMedia.isExist);
        values.put(KEY_URL, snoppaMedia.url);
        Logger.i(TAG, "insertData:" + snoppaMedia.toString());
        insert = mDatabase.insert(SNOPPA_MEDIA, null, values);
//        }
        return insert;


    }

    // 通过id 删除一条数据
    public long deleteDataById(long id) {
        return mDatabase.delete(SNOPPA_MEDIA, KEY_ID + "=" + id, null);
    }

    //通过URL 删除一条数据
    public long deleteDataByUrl(String url) {
        return mDatabase.delete(SNOPPA_MEDIA, KEY_URL + "=" + "'" + url + "'", null);
    }

    //通过isExist 删除不存在的数据
    public long deleteUnExist() {
        return mDatabase.delete(SNOPPA_MEDIA, KEY_EXIST + "=" + 0, null);
    }

    //删除所有数据
    public long deleteAllData() {
        return mDatabase.delete(SNOPPA_MEDIA, null, null);
    }
    //删除某张表，慎用
//    public void deleteTable(String tableName){
//        String sqlStr = "DROP TABLE "+tableName+";";
//        mDatabase.execSQL(sqlStr);
//    }

    //更新一条数据
    public long updateData(long id, SnoppaMedia snoppaMedia) {
        ContentValues values = new ContentValues();
        values.put(KEY_URL, snoppaMedia.url);
        values.put(KEY_FAVORITE, snoppaMedia.isFavorite);
        values.put(KEY_EXIST, snoppaMedia.isExist);
        return mDatabase.update(SNOPPA_MEDIA, values, KEY_ID + "=" + id, null);
    }

    public long updateFavoriteByUrl(String url, int isFavorite) {
        ContentValues values = new ContentValues();
//        values.put(KEY_URL, snoppaMedia.url);
        values.put(KEY_FAVORITE, isFavorite);
        return mDatabase.update(SNOPPA_MEDIA, values, KEY_URL + "=" + "'" + url + "'", null);

    }

    /**
     * @param url
     * @param isExist
     * @return
     */
    public long updateExistByUrl(String url, int isExist) {
        ContentValues values = new ContentValues();
//        values.put(KEY_URL, snoppaMedia.url);
        values.put(KEY_EXIST, isExist);
        return mDatabase.update(SNOPPA_MEDIA, values, KEY_URL + "=" + "'" + url + "'", null);

    }


    //查询一条数据
    public List<SnoppaMedia> queryData(long id) {
        Cursor results = mDatabase.query(SNOPPA_MEDIA, new String[]{KEY_ID, KEY_FAVORITE, KEY_EXIST, KEY_URL},
                KEY_ID + "=" + id, null, null, null, null);
        return convertToSnoppaMedia(results);
    }

    //查询一条数据
    public List<SnoppaMedia> queryData(String url) {
        Cursor results = mDatabase.query(SNOPPA_MEDIA, new String[]{KEY_ID, KEY_FAVORITE, KEY_EXIST, KEY_URL},
                KEY_URL + " = " + "'" + url + "'", null, null, null, null);
        return convertToSnoppaMedia(results);
    }

    //查询一条数据
    public boolean queryDataIsHave(String url) {

        Cursor results = mDatabase.query(SNOPPA_MEDIA, new String[]{KEY_ID, KEY_FAVORITE, KEY_EXIST, KEY_URL},
                "'" + KEY_URL + "'" + " = " + "'" + url + "'", null, null, null, null);
        int resultCounts = results.getCount();
        if (resultCounts == 0 || !results.moveToFirst()) {
            return false;
        }
        return true;
    }


    //查询所有数据
    public List<SnoppaMedia> queryDataList() {
        Cursor results = mDatabase.query(SNOPPA_MEDIA, new String[]{KEY_ID, KEY_FAVORITE, KEY_EXIST, KEY_URL},
                null, null, null, null, null);
        return convertToSnoppaMedia(results);

    }

    //查询标记为精读的
    public List<SnoppaMedia> queryFavoriteDataList() {
        Cursor results = mDatabase.query(SNOPPA_MEDIA, new String[]{KEY_ID, KEY_FAVORITE, KEY_EXIST, KEY_URL},
                KEY_FAVORITE + " = " + 1, null, null, null, null);
        return convertToSnoppaMedia(results);
    }

    //倒序查询标记为精读的
    public List<SnoppaMedia> queryDescFavoriteDataList() {
        Cursor results = mDatabase.query(SNOPPA_MEDIA, new String[]{KEY_ID, KEY_FAVORITE, KEY_EXIST, KEY_URL},
                KEY_FAVORITE + " = " + 1, null, null, null, KEY_ID + " DESC");
        return convertToSnoppaMedia(results);
    }

    //查询普通的媒体
    public List<SnoppaMedia> queryCommonDataList() {
        Cursor results = mDatabase.query(SNOPPA_MEDIA, new String[]{KEY_ID, KEY_FAVORITE, KEY_EXIST, KEY_URL},
                KEY_FAVORITE + " = " + 0, null, null, null, null);
        return convertToSnoppaMedia(results);
    }

    //倒序查询普通的媒体
    public List<SnoppaMedia> queryDescCommonDataList() {
        Cursor results = mDatabase.query(SNOPPA_MEDIA, new String[]{KEY_ID, KEY_FAVORITE, KEY_EXIST, KEY_URL},
                KEY_FAVORITE + " = " + 0, null, null, null, KEY_ID + " DESC");
        return convertToSnoppaMedia(results);
    }

    //倒序查询普通的媒体
    public List<SnoppaMedia> queryAll() {
        Cursor results = mDatabase.query(SNOPPA_MEDIA, new String[]{KEY_ID, KEY_FAVORITE, KEY_EXIST, KEY_URL}, null, null, null, null, KEY_ID + " DESC");
        return convertToSnoppaMedia(results);
    }

    private List<SnoppaMedia> convertToSnoppaMedia(Cursor cursor) {
        int resultCounts = cursor.getCount();
        if (resultCounts == 0 || !cursor.moveToFirst()) {
            return null;
        }
        List<SnoppaMedia> mSnoppaMediaList = new ArrayList<>();
        for (int i = 0; i < resultCounts; i++) {
            SnoppaMedia snoppaMedia = new SnoppaMedia();
            snoppaMedia.id = (cursor.getInt(0));
            snoppaMedia.isFavorite = (cursor.getInt(cursor.getColumnIndex(KEY_FAVORITE)));
            snoppaMedia.isExist = (cursor.getInt(cursor.getColumnIndex(KEY_EXIST)));
            snoppaMedia.url = (cursor.getString(cursor.getColumnIndex(KEY_URL)));
            mSnoppaMediaList.add(snoppaMedia);
            cursor.moveToNext();
        }
        return mSnoppaMediaList;
    }
}
