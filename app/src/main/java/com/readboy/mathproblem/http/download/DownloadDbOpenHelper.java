package com.readboy.mathproblem.http.download;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DownloadDbOpenHelper extends SQLiteOpenHelper implements DownloadContract.DownloadColumns {
    public final static String DATABASE_NAME = "download2.db";
    /**
     * 初始版本
     */
    private final static int VERSION1 = 1;
    /**
     * 20180427001,4.1.19开始修改
     * 下载库使用阿里云的下载库，vid存入到url里
     */
    private final static int VERSION2 = 2;
    private final static int DATABASE_VERSION = VERSION2;

    public DownloadDbOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + DownloadDbController.TABLE_NAME
                + String.format(
                "("
                        + "%s INTEGER PRIMARY KEY AUTOINCREMENT, " // id, download id
                        + "%s VARCHAR, " // name
                        + "%s VARCHAR, " // url
                        + "%s VARCHAR, " // path
                        + "%s INTEGER "
                        + ")"
                , ID
                , NAME
                , URL
                , PATH
                , SIZE

        ));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == VERSION1 && newVersion == VERSION2) {
            db.delete(DownloadDbController.TABLE_NAME, null, null);
        }
    }
}