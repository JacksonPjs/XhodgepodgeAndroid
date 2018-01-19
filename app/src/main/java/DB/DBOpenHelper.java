package DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * author: jk
 * created on: 2016/11/8 16:21
 * description: 数据库基类
 */
public class DBOpenHelper extends SQLiteOpenHelper {

    public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建snoppa media 表
        final String sqlStr1 = "create table if not exists " +
                SnoppaMediaDBDao.SNOPPA_MEDIA + " (" + SnoppaMediaDBDao.KEY_ID + " integer primary key autoincrement, "
                + SnoppaMediaDBDao.KEY_FAVORITE + " integer DEFAULT 0, "
                + SnoppaMediaDBDao.KEY_EXIST + " integer DEFAULT 1, "
                + SnoppaMediaDBDao.KEY_URL + " VARCHAR UNIQUE);";
        db.execSQL(sqlStr1);
        // 创建表 snoppa Blue
//        final String sqlStr2 = "create table if not exists " +
//                SnoppaBlueDao.SNOPPA_BLUE + " (" + SnoppaBlueDao.BLUE_ID + " integer primary key autoincrement, "
//                + SnoppaBlueDao.BLUE_ACCOUNT + " VARCHAR DEFAULT 'SNOPPA BLUE', "
//                + SnoppaBlueDao.BLUE_PASSWORD + " VARCHAR DEFAULT '000000', "
//                + SnoppaBlueDao.BLUE_ADDRESS + " VARCHAR UNIQUE);";
//        db.execSQL(sqlStr2);

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //删除表snoppa media
        final String sqlStr = "DROP TABLE IF EXISTS " + SnoppaMediaDBDao.SNOPPA_MEDIA;
        db.execSQL(sqlStr);

        //删除表snoppa Blue
//        final String sqlStr2 = "DROP TABLE IF EXISTS " + SnoppaBlueDao.SNOPPA_BLUE;
//        db.execSQL(sqlStr2);

        onCreate(db);
    }

}
