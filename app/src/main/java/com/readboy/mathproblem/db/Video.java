package com.readboy.mathproblem.db;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by oubin on 2017/9/4.
 */

public class Video implements ProjectContract.VideoColumns {
    private static final String TAG = "Video";

    public static final long INVALID_ID = -1;

    //无视频, 指的是没有存入数据库
    public static final int STATUS_NONE = -1;

    public static final int STATUS_DOWNLOAD = 1;
    public static final int STATUS_PAUSE = 2;
    public static final int STATUS_COMPLETED = 3;
    public static final int STATUS_WAIT = 4;

    @IntDef({STATUS_DOWNLOAD, STATUS_PAUSE, STATUS_COMPLETED, STATUS_WAIT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Status {
    }

    //按照时间排序，最新的排前面
//    private static final String DEFAULT_SORT_ORDER =
//            ProjectOpenHelper.VIDEO_TABLE_NAME + "." + _ID + " ASC";

    private static final String DEFAULT_SORT_ORDER = null;

    private static final String[] QUERY_COLUMNS = {
//            _ID,
            IID,
            STATUS,
            FAVORITE,
            THUMBNAIL,
            URL,
            PATH,
            NAME,
            PROGRESS,
    };

//    public int _id;
    public long mIid;
    @Status
    public int mStatus = STATUS_WAIT;
    /**
     * 收藏时间，如果为-1为未收藏
     */
    public long favorite = -1L;
    public String mThumbnail;
    public String mUrl;
    public String mPath;
    public String mName;
    public int mProgress;

    public Video(Cursor cursor) {
//        this._id = cursor.getInt(_ID_INDEX);
        this.mIid = cursor.getInt(IID_INDEX);
        this.mStatus = formatStatus(cursor.getInt(STATUS_INDEX));
        this.favorite = cursor.getInt(FAVORITE_INDEX);
        this.mThumbnail = cursor.getString(THUMBNAIL_INDEX);
        this.mUrl = cursor.getString(URL_INDEX);
        this.mPath = cursor.getString(PATH_INDEX);
        this.mName = cursor.getString(NAME_INDEX);
        this.mProgress = cursor.getInt(PROGRESS_INDEX);

    }

    public static ContentValues createContentValues(Video video) {
        ContentValues values = new ContentValues();
//        if (video._id != INVALID_ID) {
//            values.put(_ID, video._id);
//        }
        values.put(IID, video.mIid);
        values.put(STATUS, video.mStatus);
        values.put(FAVORITE, video.favorite);
        values.put(THUMBNAIL, video.mThumbnail);
        values.put(URL, video.mUrl);
        values.put(PATH, video.mPath);
        values.put(NAME, video.mName);
        values.put(PROGRESS, video.mProgress);
        return values;
    }

    public static List<Video> getDownloadVideos(ContentResolver resolver) {
        return getVideos(resolver, STATUS + "!=" + STATUS_COMPLETED);
    }

    public static List<Video> getFavoriteVideos(ContentResolver resolver) {
        return getVideos(resolver, FAVORITE + "!=" + String.valueOf(-1));
    }

    public static List<Video> getVideos(ContentResolver resolver, String selection,
                                        String... selectionArgs) {
        final List<Video> result = new ArrayList<>();
        try (Cursor cursor = resolver.query(CONTENT_URI, QUERY_COLUMNS, selection, selectionArgs, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    result.add(new Video(cursor));
                } while (cursor.moveToNext());
            }
        }
        return result;
    }

    public static Uri insert(ContentResolver resolver, Video video) {
        return resolver.insert(CONTENT_URI, createContentValues(video));
    }

    public static boolean update(ContentResolver resolver, Video video) {
        return 1 == resolver.update(getUri(video.mIid), createContentValues(video), null, null);
    }

    public static boolean delete(ContentResolver resolver, Video video) {
        Uri uri = getUri(video.mIid);
        return 1 == resolver.delete(uri, "", null);
    }

    /**
     * @param id 服务器上对应的id
     */
    private static Uri getUri(long id) {
        return ContentUris.withAppendedId(CONTENT_URI, id);
    }

    @Video.Status
    private int formatStatus(int status) {
        int result = STATUS_WAIT;
        switch (status) {
            case STATUS_DOWNLOAD:
                result = STATUS_DOWNLOAD;
                break;
            case STATUS_COMPLETED:
                result = STATUS_COMPLETED;
                break;
            case STATUS_PAUSE:
                result = STATUS_PAUSE;
                break;
            case STATUS_WAIT:
                result = STATUS_WAIT;
                break;
            default:
                break;
        }

        return result;
    }

    public static CursorLoader getVideoCursorLoader(Context context) {
        return new CursorLoader(context, CONTENT_URI, QUERY_COLUMNS, null, null, DEFAULT_SORT_ORDER);
    }

    public static int getStatus(ContentResolver resolver, int projectId) {
        int status = STATUS_NONE;
        try (Cursor cursor = resolver.query(CONTENT_URI, QUERY_COLUMNS, IID + "=" + projectId, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                Video video = new Video(cursor);
                status = video.mStatus;
            }
        }
        return status;
    }


}
