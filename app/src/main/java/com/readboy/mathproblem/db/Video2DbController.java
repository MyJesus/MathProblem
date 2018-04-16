package com.readboy.mathproblem.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.readboy.mathproblem.application.MathApplication;
import com.readboy.mathproblem.http.download.DownloadDbOpenHelper;

import static com.readboy.mathproblem.db.ProjectOpenHelper.VIDEO2_TABLE_NAME;

/**
 * Created by oubin on 2017/10/30.
 */

public class Video2DbController implements ProjectContract.Video2Columns {
    private static final String TAG = "Video2DbController";

    private static final String[] QUERY_COLUMNS = {
            _ID,
            FILE_NAME
    };

    private final SQLiteDatabase db;

    private static class Inner {
        static Video2DbController INSTANCE = new Video2DbController();
    }

    public Video2DbController() {
        DownloadDbOpenHelper openHelper = new DownloadDbOpenHelper(MathApplication.getInstance());
        db = openHelper.getWritableDatabase();
    }

    public static Video2DbController getInstance() {
        return Inner.INSTANCE;
    }

    boolean deleteId(final int id) {
        return db.delete(VIDEO2_TABLE_NAME, _ID + "=" + id, null) > 0;
    }

    public long replace(int id, String filename) {
        if (TextUtils.isEmpty(filename)) {
            return -1;
        }

        return db.replace(VIDEO2_TABLE_NAME, null, createContentValue(id, filename));
    }

    public String getFileName(int id) {
        String result;
//        try (Cursor cursor = db.rawQuery("SELECT * FROM " + VIDEO2_TABLE_NAME + " WHERE " + _ID + " = ?",
//                new String[]{String.valueOf(id)})) {
        try (Cursor cursor = db.query(VIDEO2_TABLE_NAME, QUERY_COLUMNS, _ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null)) {
            cursor.moveToFirst();
            result = cursor.getString(cursor.getColumnIndex(FILE_NAME));
            Log.e(TAG, "getFileName: count = " + cursor.getCount() + ", result = " + result);
        }
        return result;
    }

    private ContentValues createContentValue(int id, String filename) {
        ContentValues values = new ContentValues();
        values.put(_ID, id);
        values.put(FILE_NAME, filename);
        return values;
    }
}
