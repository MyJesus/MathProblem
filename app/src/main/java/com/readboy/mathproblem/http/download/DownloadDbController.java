package com.readboy.mathproblem.http.download;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;

import com.aliyun.vodplayer.downloader.AliyunDownloadMediaInfo;
import com.readboy.mathproblem.application.MathApplication;
import com.readboy.mathproblem.util.FileUtils;
import com.readboy.textbook.model.PrimarySection;

import java.util.ArrayList;
import java.util.List;

public class DownloadDbController implements DownloadContract.DownloadColumns {
    private static final String TAG = "DownloadDbController";

    public final static String TABLE_NAME = "download2";
    private final SQLiteDatabase db;

    public DownloadDbController() {
        DownloadDbOpenHelper openHelper = new DownloadDbOpenHelper(MathApplication.getInstance());
        db = openHelper.getWritableDatabase();
    }

    public List<DownloadModel> getAllTasks() {
        final Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        final List<DownloadModel> list = new ArrayList<>();
        try {
            if (!c.moveToLast()) {
                return list;
            }

            do {
                DownloadModel model = new DownloadModel();
                model.setTaskId(c.getInt(c.getColumnIndex(ID)));
                model.setFileName(c.getString(c.getColumnIndex(NAME)));
                model.setUrl(c.getString(c.getColumnIndex(URL)));
                model.setPath(c.getString(c.getColumnIndex(PATH)));
                AliyunDownloadMediaInfo mediaInfo = new AliyunDownloadMediaInfo();
                mediaInfo.setVid(c.getString(c.getColumnIndex(URL)));
                model.setMediaInfo(mediaInfo);
                list.add(model);
            } while (c.moveToPrevious());
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return list;
    }

    DownloadModel addTask(final String url, final String name) {
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(name)) {
            return null;
        }

        // have to use FileDownloadUtils.generateId to associate TasksManagerModel with FileDownloader
        final int id = 1;

        DownloadModel model = new DownloadModel();
        model.setTaskId(id);
        model.setFileName(FileUtils.getFileName(url));
        Log.e(TAG, "addTask: filename = " + model.getFileName());
        model.setUrl(url);
        model.setPath(name);

        final boolean succeed = db.replace(TABLE_NAME, null, createContentValues(model)) != -1;
        return succeed ? model : null;
    }

    public boolean deleteTask(final int id) {
        return db.delete(TABLE_NAME, ID + "=" + id, null) > 0;
    }

    public boolean deleteTask(final String vid) {
        return db.delete(TABLE_NAME, URL + "=" + vid, null) > 0;
    }

    public boolean updateTask(DownloadModel model) {
        ContentValues values = createContentValues(model);
        return db.update(TABLE_NAME, values, ID + "=" + model.getTaskId(), null) > 0;
    }

    public void replaceTask(final String url, final String name) {
        Log.e(TAG, "replaceTask() called with: url = " + url + ", name = " + name + "");
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(name)) {
            return;
        }

        // have to use FileDownloadUtils.generateId to associate TasksManagerModel with FileDownloader
        final int id = 1;

        if (getTask(url) > 0) {
            Log.e(TAG, "replaceTask: is exit.");
            return;
        }

        DownloadModel model = new DownloadModel();
        model.setTaskId(id);
        model.setFileName(FileUtils.getFileName(url));
        model.setUrl(url);
        model.setPath(name);

        addTask(url, name);
    }

    public int getTask(String vid) {
        Cursor cursor = db.query(TABLE_NAME, new String[]{ID, URL}, URL + "=?", new String[]{vid}, null, null, null);
        if (cursor == null) {
            return 0;
        }
        return cursor.getCount();
    }

    private ContentValues createContentValues(DownloadModel model) {
        ContentValues values = new ContentValues();
        values.put(ID, model.getTaskId());
        values.put(NAME, model.getFileName());
        values.put(URL, model.getUrl());
        values.put(PATH, model.getPath());
        return values;
    }

}