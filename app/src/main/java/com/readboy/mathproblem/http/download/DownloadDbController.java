package com.readboy.mathproblem.http.download;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;

import com.aliyun.vodplayer.downloader.AliyunDownloadMediaInfo;
import com.readboy.mathproblem.application.Constants;
import com.readboy.mathproblem.application.MathApplication;
import com.readboy.mathproblem.db.Favorite;
import com.readboy.mathproblem.http.response.VideoInfoEntity;
import com.readboy.mathproblem.test.FindViewActivity;
import com.readboy.mathproblem.util.FileUtils;
import com.readboy.textbook.chapter.Content;
import com.readboy.textbook.model.PrimarySection;
import com.readboy.textbook.util.Util;

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
                mediaInfo.setSize(c.getInt(c.getColumnIndex(SIZE)));
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

    boolean addTask(final String url, final String name) {
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(name)) {
            return false;
        }

        // have to use FileDownloadUtils.generateId to associate TasksManagerModel with FileDownloader
        final int id = 1;

//        DownloadModel model = new DownloadModel();
//        model.setTaskId(id);
//        model.setFileName(FileUtils.getFileName(url));
//        Log.e(TAG, "addTask: filename = " + model.getFileName());
//        model.setUrl(url);
//        model.setPath(name);

//        final boolean succeed = db.replace(TABLE_NAME, null, createContentValues(model)) != -1;
//        return succeed ? model : null;
        ContentValues values = new ContentValues();
        values.put(URL, url);
        values.put(NAME, name);
        return db.insert(TABLE_NAME, null, values) != -1;

    }

    public boolean addTask(final String vid) {
        ContentValues values = new ContentValues();
        values.put(URL, vid);
        return db.insert(TABLE_NAME, null, values) > 0;
    }

    public boolean addTask(VideoInfoEntity.VideoInfo info) {
        return db.insert(TABLE_NAME, null, createContentValues(info)) > 0;
    }

    public boolean deleteTask(final int id) {
        return db.delete(TABLE_NAME, ID + "=" + id, null) > 0;
    }

    public boolean deleteTask(final String vid) {
        return db.delete(TABLE_NAME, URL + "=?", new String[]{vid}) > 0;
    }

    public boolean updateTask(DownloadModel model) {
        ContentValues values = createContentValues(model);
        return db.update(TABLE_NAME, values, ID + "=" + model.getTaskId(), null) > 0;
    }

    public boolean updateTask(AliyunDownloadMediaInfo mediaInfo) {
        ContentValues values = createContentValues(mediaInfo);
        return db.update(TABLE_NAME, values, URL + "=?", new String[]{mediaInfo.getVid()}) > 0;
    }

    public boolean replaceTask(final String vid) {
        if (TextUtils.isEmpty(vid)) {
            return false;
        }
        if (getTask(vid) > 0) {
            Log.e(TAG, "replaceTask: is exit. vid = " + vid);
            return true;
        }
        return addTask(vid);
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

    public boolean replaceTask(final VideoInfoEntity.VideoInfo videoInfo) {
//        开发过程不过滤，及时发现数据有误
//        if (videoInfo == null) {
//            Log.e(TAG, "replaceTask: videoInfo = null.");
//            return false;
//        }
        if (getTask(videoInfo.getVid()) > 0) {
            Log.e(TAG, "replaceTask: is exit, videoInfo : " + videoInfo.getVid() + ", " + videoInfo.getName());
            return true;
        }

        return addTask(videoInfo);
    }

    public int getTask(String vid) {
        try (Cursor cursor = db.query(TABLE_NAME, new String[]{ID, URL}, URL + "=?", new String[]{vid}, null, null, null)) {
            if (cursor == null) {
                return 0;
            }
            return cursor.getCount();
        }
    }

    private ContentValues createContentValues(DownloadModel model) {
        ContentValues values = new ContentValues();
        values.put(ID, model.getTaskId());
        values.put(NAME, model.getFileName());
        values.put(URL, model.getUrl());
        values.put(PATH, model.getPath());
        return values;
    }

    private ContentValues createContentValues(AliyunDownloadMediaInfo mediaInfo) {
        ContentValues values = new ContentValues();
        values.put(NAME, mediaInfo.getTitle());
        values.put(URL, mediaInfo.getVid());
        values.put(PATH, mediaInfo.getSavePath());
        return values;
    }

    private ContentValues createContentValues(VideoInfoEntity.VideoInfo videoInfo) {
        ContentValues values = new ContentValues();
        values.put(NAME, videoInfo.getName());
        values.put(URL, videoInfo.getVid());
        values.put(SIZE, videoInfo.getName());
        return values;
    }

}