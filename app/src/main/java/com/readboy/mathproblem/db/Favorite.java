package com.readboy.mathproblem.db;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oubin on 2017/9/4.
 */

public class Favorite implements ProjectContract.FavoriteColumns {
    private static final String TAG = "Favorite";

    //按照时间排序，最新的排前面
    private static final String DEFAULT_SORT_ORDER =
            ProjectOpenHelper.FAVORITE_TABLE_NAME + "." + TIME + " ASC";

    public static final long INVALID_ID = -1;

    public long _id = INVALID_ID;
    public String mThumbnail;
    public String mUrl;
    public String mPath;
    public String mName;
    public long mTime;

    private static final String[] QUERY_COLUMNS = {
            _ID,
            THUMBNAIL,
            URL,
            PATH,
            NAME,
            TIME,
    };

    public Favorite() {
        mTime = System.currentTimeMillis();
    }

    public Favorite(Cursor cursor) {
        this._id = cursor.getLong(_ID_INDEX);
        this.mThumbnail = cursor.getString(THUMBNAIL_INDEX);
        this.mUrl = cursor.getString(URL_INDEX);
        this.mPath = cursor.getString(PATH_INDEX);
        this.mName = cursor.getString(NAME_INDEX);
        this.mTime = cursor.getLong(TIME_INDEX);
    }

    public static ContentValues createContentValues(Favorite favorite) {
        ContentValues values = new ContentValues();
        if (favorite._id != INVALID_ID) {
            values.put(_ID, favorite._id);
        }
        values.put(THUMBNAIL, favorite.mThumbnail);
        values.put(URL, favorite.mUrl);
        values.put(PATH, favorite.mPath);
        values.put(NAME, favorite.mName);
        values.put(TIME, favorite.mTime);
        return values;
    }

    public static CursorLoader getFavoritesCursorLoader(Context context) {
        return new CursorLoader(context, CONTENT_URI, QUERY_COLUMNS, null, null, DEFAULT_SORT_ORDER);
    }

    private static Uri getUri(long id) {
        return ContentUris.withAppendedId(CONTENT_URI, id);
    }

    public static Uri insert(ContentResolver resolver, Favorite favorite) {
        //TODO 目前阶段直接判断filename是否已收藏, 新接口用videoId
        String selection = NAME + "=?";
        try (Cursor cursor = resolver.query(CONTENT_URI, QUERY_COLUMNS, selection,
                new String[]{favorite.mName}, null)) {
            if (cursor != null && cursor.getCount() > 0) {
                Log.e(TAG, "insert: 已收藏，favorite = " + favorite.mName);
                long id = cursor.getInt(cursor.getColumnIndex(_ID));
                return getUri(id);
            }
        }
        return resolver.insert(CONTENT_URI, createContentValues(favorite));
    }

    public static Uri query(ContentResolver resolver, String name) {
        String selection = NAME + "=?";
        try (Cursor cursor = resolver.query(CONTENT_URI, QUERY_COLUMNS, selection,
                new String[]{name}, null)) {
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                long id = cursor.getLong(_ID_INDEX);
                return getUri(id);
            }
        }
        return null;
    }

    public static boolean delete(ContentResolver resolver, List<Long> ids) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        for (Long id : ids) {
            ops.add(ContentProviderOperation.newDelete(CONTENT_URI)
                    .withSelection(_ID + "=" + id, null)
                    .build());
        }
        try {
            ContentProviderResult[] results = resolver.applyBatch(ProjectContract.AUTHORITY, ops);
            boolean b = true;
            for (ContentProviderResult result : results) {
                if (result.count != 1) {
                    b = false;
                }
            }
            Log.d(TAG, "delete result = " + b);
            return b;
        } catch (Exception e) {
            Log.d(TAG, "delete failed");
            Log.e(TAG, e.getMessage());
        }
        Log.w(TAG, "**delete end**");
        return false;
    }

    public static boolean delete(ContentResolver resolver, long id) {
        return id != INVALID_ID && 1 == resolver.delete(getUri(id), "", null);
    }

    public static boolean delete(ContentResolver resolver, String name) {
        Uri uri = query(resolver, name);
        return uri == null || resolver.delete(uri, null, null) > 0;
    }

    /**
     * 是否已收藏
     *
     * @param fileName 文件名，包括文件后缀
     */
    public static boolean hasFavorite(ContentResolver resolver, String fileName) {
        String selection = NAME + " = ?";
        try (Cursor cursor = resolver.query(CONTENT_URI, QUERY_COLUMNS, selection,
                new String[]{fileName}, null)) {
            if (cursor != null && cursor.getCount() > 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Favorite{" +
                "_id=" + _id +
                ", mThumbnail='" + mThumbnail + '\'' +
                ", mUrl='" + mUrl + '\'' +
                ", mPath='" + mPath + '\'' +
                ", mName='" + mName + '\'' +
                ", mTime=" + mTime +
                '}';
    }
}
