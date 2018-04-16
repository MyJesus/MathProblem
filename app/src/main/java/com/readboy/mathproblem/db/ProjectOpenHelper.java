package com.readboy.mathproblem.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.readboy.mathproblem.db.ProjectContract.ScoreColumns;
import com.readboy.mathproblem.db.ProjectContract.VideoColumns;
import com.readboy.mathproblem.db.ProjectContract.FavoriteColumns;
import com.readboy.mathproblem.db.ProjectContract.Video2Columns;

/**
 * Created by oubin on 2017/9/4.
 */

public class ProjectOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = "ProjectOpenHelper";

    public static final String DATABASE_NAME = "math.db";
    public static final String SCORE_TABLE_NAME = "scores";
    public static final String VIDEO_TABLE_NAME = "videos";
    public static final String FAVORITE_TABLE_NAME = "favorite";
    public static final String VIDEO2_TABLE_NAME = "video2";
    private static final int VERSION = 1;

    private static final String DEFAULT_FAVORITE =
            "(NULL, '探秘(行程)之路_钟表问题.mp4', 'download/mp4qpsp', '探秘“行程”之路__行程问题·钟表问题'," +
                    System.currentTimeMillis() + ");";

    private static final String DEFAULT_VIDEO = "" +
            "(486318110, 4, '-1', NULL, '探秘(行程)之路_钟表问题.mp4', 'download/mp4qpsp', " +
            "'探秘“行程”之路__行程问题·钟表问题', 0)";

    private static final String DEFAULT_VIDEO2 = "" + "(486318110, '探秘(行程)之路_钟表问题.mp4')";

    public ProjectOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e(TAG, "onCreate: start");
        createScoreTable(db);
        createVideoTable(db);
        createVideo2Table(db);
        createFavoriteTable(db);

        String cs = ", "; //comma and space
        //创建默认收藏视频，用于测试
//        String insertFavorite = "INSERT INTO " + FAVORITE_TABLE_NAME + " (" +
//                FavoriteColumns.THUMBNAIL + cs +
//                FavoriteColumns.URL + cs +
//                FavoriteColumns.PATH + cs +
//                FavoriteColumns.NAME + cs +
//                FavoriteColumns.TIME + ") VALUES ";
//        db.execSQL(insertFavorite + DEFAULT_FAVORITE);

        String insertVideo2 = "INSERT INTO " + VIDEO2_TABLE_NAME + " (" +
                Video2Columns._ID + cs +
                Video2Columns.FILE_NAME + ") VALUES ";
        db.execSQL(insertVideo2 + DEFAULT_VIDEO2);

        String insertVideo = "INSERT INTO " + VIDEO_TABLE_NAME + " (" +
                VideoColumns.IID + cs +
                VideoColumns.STATUS + cs +
                VideoColumns.FAVORITE + cs +
                VideoColumns.THUMBNAIL + cs +
                VideoColumns.URL + cs +
                VideoColumns.PATH + cs +
                VideoColumns.NAME + cs +
                VideoColumns.PROGRESS + ") VALUES ";
        db.execSQL(insertVideo + DEFAULT_VIDEO);

        Log.e(TAG, "onCreate: end");

    }


    private void createScoreTable(SQLiteDatabase db) {
//        db.execSQL("CREATE TABLE " + SCORE_TABLE_NAME + " (" +
//                ScoreColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
//                ScoreColumns.PROJECT_ID + " INTEGER NOT NULL UNIQUE ON CONFLICT REPLACE, " +
//                ScoreColumns.SCORE + " INTEGER NOT NULL DEFAULT 0);");
        db.execSQL("CREATE TABLE " + SCORE_TABLE_NAME + " (" +
                ScoreColumns.PROJECT_ID + " INTEGER NOT NULL PRIMARY KEY, " +
                ScoreColumns.SCORE + " INTEGER NOT NULL DEFAULT 0);");
        Log.i(TAG, "Score Table created");
    }

    private void createVideoTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + VIDEO_TABLE_NAME + " (" +
//                VideoColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                VideoColumns.IID + " INTEGER NOT NULL PRIMARY KEY, " +
                VideoColumns.STATUS + " INTEGER NOT NULL, " +
                VideoColumns.FAVORITE + " INTEGER NOT NULL DEFAULT -1, " +
                VideoColumns.THUMBNAIL + " TEXT, " +
                VideoColumns.URL + " TEXT NOT NULL, " +
                VideoColumns.PATH + " TEXT, " +
                VideoColumns.NAME + " TEXT NOT NULL, " +
                VideoColumns.PROGRESS + " INTEGER NOT NULL DEFAULT 0);");
        Log.i(TAG, "createVideoTable: ");
    }

    private void createVideo2Table(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + VIDEO2_TABLE_NAME + " (" +
                Video2Columns._ID + " INTEGER NOT NULL PRIMARY KEY, " +
                Video2Columns.FILE_NAME + " TEXT NOT NULL);");
        Log.e(TAG, "createVideo2Table: ");
    }

    private void createFavoriteTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + FAVORITE_TABLE_NAME + " (" +
                FavoriteColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FavoriteColumns.THUMBNAIL + " TEXT, " +
                FavoriteColumns.URL + " TEXT, " +
                FavoriteColumns.PATH + " TEXT, " +
                FavoriteColumns.NAME + " TEXT NOT NULL, " +
                FavoriteColumns.TIME + " INTEGER NOT NULL DEFAULT 0);");
        Log.i(TAG, "createFavoriteTable: ");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e(TAG, "onUpgrade() called with: db = " + db + ", oldVersion = " + oldVersion
                + ", newVersion = " + newVersion + "");
    }

}
