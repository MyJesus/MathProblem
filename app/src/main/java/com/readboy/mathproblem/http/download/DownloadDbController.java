package com.readboy.mathproblem.http.download;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.liulishuo.filedownloader.util.FileDownloadUtils;
import com.readboy.mathproblem.application.MathApplication;
import com.readboy.mathproblem.util.FileUtils;

import java.util.ArrayList;
import java.util.List;

public class DownloadDbController implements DownloadContract.DownloadColumns {
    private static final String TAG = "DownloadDbController";

    public final static String TABLE_NAME = "download";
    private final SQLiteDatabase db;

    DownloadDbController() {
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
                list.add(model);
            } while (c.moveToPrevious());
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return list;
    }

    DownloadModel addTask(final String url, final String path) {
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(path)) {
            return null;
        }

        // have to use FileDownloadUtils.generateId to associate TasksManagerModel with FileDownloader
        final int id = FileDownloadUtils.generateId(url, path);

        DownloadModel model = new DownloadModel();
        model.setTaskId(id);
        model.setFileName(FileUtils.getFileName(url));
        Log.e(TAG, "addTask: filename = " + model.getFileName());
        model.setUrl(url);
        model.setPath(path);

        final boolean succeed = db.replace(TABLE_NAME, null, createContentValues(model)) != -1;
        return succeed ? model : null;
    }

    boolean deleteTask(final int id) {
        return db.delete(TABLE_NAME, ID + "=" + id, null) > 0;
    }

    boolean updateTask(DownloadModel model) {
        ContentValues values = createContentValues(model);
        return db.update(TABLE_NAME, values, ID + "=" + model.getTaskId(), null) > 0;
    }

    @Deprecated
    public DownloadModel replaceTask(final String url, final String path) {
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(path)) {
            return null;
        }

        // have to use FileDownloadUtils.generateId to associate TasksManagerModel with FileDownloader
        final int id = FileDownloadUtils.generateId(url, path);

        DownloadModel model = new DownloadModel();
        model.setTaskId(id);
        model.setFileName(FileUtils.getFileName(url));
        model.setUrl(url);
        model.setPath(path);

        final boolean succeed = db.replace(TABLE_NAME, null, createContentValues(model)) != -1;
        return succeed ? model : null;
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