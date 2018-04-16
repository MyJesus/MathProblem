package com.readboy.mathproblem.db;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.readboy.mathproblem.util.BuildUtils;

import static com.readboy.mathproblem.db.ProjectOpenHelper.SCORE_TABLE_NAME;
import static com.readboy.mathproblem.db.ProjectOpenHelper.VIDEO_TABLE_NAME;
import static com.readboy.mathproblem.db.ProjectOpenHelper.FAVORITE_TABLE_NAME;

import com.readboy.mathproblem.db.ProjectContract.ScoreColumns;
import com.readboy.mathproblem.db.ProjectContract.VideoColumns;
import com.readboy.mathproblem.db.ProjectContract.FavoriteColumns;

import java.util.Arrays;


/**
 * Created by oubin on 2017/9/14.
 */

public class ProjectProvider extends ContentProvider {
    private static final String TAG = "ProjectProvider";

    private static final int SCORE = 1;
    private static final int SCORE_ID = 2;
    private static final int VIDEO = 3;
    private static final int VIDEO_ID = 4;
    private static final int FAVORITE = 5;
    private static final int FAVORITE_ID = 6;


    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(ProjectContract.AUTHORITY, "scores", SCORE);
        sUriMatcher.addURI(ProjectContract.AUTHORITY, "scores/#", SCORE_ID);
        sUriMatcher.addURI(ProjectContract.AUTHORITY, "videos", VIDEO);
        sUriMatcher.addURI(ProjectContract.AUTHORITY, "videos/#", VIDEO_ID);
        sUriMatcher.addURI(ProjectContract.AUTHORITY, "favorites", FAVORITE);
        sUriMatcher.addURI(ProjectContract.AUTHORITY, "favorites/#", FAVORITE_ID);
    }

    private ProjectOpenHelper mOpenHelper;


    @Override
    public boolean onCreate() {
        final Context context = getContext();
        if (context == null) {
            Log.e(TAG, "onCreate: create the alarm clock provider failed, because getContext() == null");
            return false;
        }
        final Context storageContext;
        if (BuildUtils.isNOrLater()) {
            // All N devices have split storage areas, but we may need to
            // migrate existing database into the new device protected
            // storage area, which is where our data lives from now on.
            final Context deviceContext = context.createDeviceProtectedStorageContext();
            if (!deviceContext.moveDatabaseFrom(context, ProjectOpenHelper.DATABASE_NAME)) {
                Log.e(TAG, "onCreate: Failed to migrate database");
            }
            storageContext = deviceContext;
        } else {
            storageContext = context;
        }

        mOpenHelper = new ProjectOpenHelper(storageContext);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sort) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Log.e(TAG, "query: match = " + match);
        switch (match) {
            case SCORE:
                qb.setTables(SCORE_TABLE_NAME);
                break;
            case SCORE_ID:
                qb.setTables(SCORE_TABLE_NAME);
                qb.appendWhere(ScoreColumns.PROJECT_ID + "=");
                qb.appendWhere(uri.getLastPathSegment());
                break;
            case VIDEO:
                qb.setTables(VIDEO_TABLE_NAME);
                break;
            case VIDEO_ID:
                qb.setTables(VIDEO_TABLE_NAME);
                qb.appendWhere(VideoColumns.IID + "=");
                qb.appendWhere(uri.getLastPathSegment());
                break;
            case FAVORITE:
                qb.setTables(FAVORITE_TABLE_NAME);
                break;
            case FAVORITE_ID:
                qb.setTables(FAVORITE_TABLE_NAME);
                qb.appendWhere(FavoriteColumns._ID + "=");
                qb.appendWhere(uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        Cursor ret = qb.query(db, projection, selection, selectionArgs, null, null, sort);

        if (ret == null) {
            Log.e(TAG, "query: fail, uri = " + uri);
        } else {
            if (getContext() == null) {
                Log.e(TAG, "query: fail, getContent() = null");
                return null;
            }
            ret.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return ret;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case SCORE:
                return "com.readboy.mathproblem/scores";
            case SCORE_ID:
                return "com.readboy.mathproblem/scores";
            case VIDEO:
                return "com.readboy.mathproblem/videos";
            case VIDEO_ID:
                return "com.readboy.mathproblem/videos";
            default:
                throw new IllegalArgumentException("Unknown URI");
        }
    }

    /**
     * 修改过策略，不是和一般的insert有区别。
     * 内部使用replace接口，projectId为主key。数据库不存在该记录则添加，存在则更新。
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        if (values == null) {
            return null;
        }
        long rowId;
        //服务器保存的数据对应的id
        long serverId = -1;
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Log.e(TAG, "insert: values = " + values.toString());
        switch (sUriMatcher.match(uri)) {
            case SCORE:
                rowId = db.replace(SCORE_TABLE_NAME, ScoreColumns.PROJECT_ID, values);
                serverId = values.getAsLong(Score.PROJECT_ID);
                break;
            case VIDEO:
                rowId = db.insert(VIDEO_TABLE_NAME, null, values);
                serverId = values.getAsLong(Video.IID);
                break;
            case FAVORITE:
                rowId = db.insert(FAVORITE_TABLE_NAME, null, values);
//                serverId = values.getAsLong(Favorite._ID);
                serverId = rowId;
                break;
            default:
                throw new IllegalArgumentException("Cannot insert from URI: " + uri);
        }

        if (serverId < 0) {
            Log.e(TAG, "insert: failed, can not valueAt server id.");
            return null;
        }
        Uri uriResult = ContentUris.withAppendedId(uri, serverId);
        notifyChange(getContext().getContentResolver(), uriResult);
        return uriResult;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String where, @Nullable String[] whereArgs) {
        Log.e(TAG, "delete: uri = " + uri + ", where = " + where + ", args = " + Arrays.toString(whereArgs));
        int count;
        String primaryKey;
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case SCORE:
                count = db.delete(SCORE_TABLE_NAME, where, whereArgs);
                break;
            case SCORE_ID:
                primaryKey = uri.getLastPathSegment();
                if (TextUtils.isEmpty(where)) {
                    where = ScoreColumns.PROJECT_ID + "=" + primaryKey;
                } else {
                    where = ScoreColumns.PROJECT_ID + "=" + primaryKey + " AND (" + where + ")";
                }
                count = db.delete(SCORE_TABLE_NAME, where, whereArgs);
                break;
            case VIDEO:
                count = db.delete(VIDEO_TABLE_NAME, where, whereArgs);
                break;
            case VIDEO_ID:
                primaryKey = uri.getLastPathSegment();
                if (TextUtils.isEmpty(where)) {
                    where = VideoColumns.IID + "=" + primaryKey;
                } else {
                    where = VideoColumns.IID + "=" + primaryKey + " AND (" + where + ")";
                }
                count = db.delete(VIDEO_TABLE_NAME, where, whereArgs);
                break;
            case FAVORITE:
                count = db.delete(FAVORITE_TABLE_NAME, where, whereArgs);
                break;
            case FAVORITE_ID:
                primaryKey = uri.getLastPathSegment();
                if (TextUtils.isEmpty(where)) {
                    where = FavoriteColumns._ID + "=" + primaryKey;
                } else {
                    where = FavoriteColumns._ID + "=" + primaryKey + " AND (" + where + ")";
                }
                count = db.delete(FAVORITE_TABLE_NAME, where, whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Cannot delete from URI: " + uri);
        }
        if (getContext() == null) {
            Log.e(TAG, "query: fail, getContent() = null");
            return 0;
        }
        notifyChange(getContext().getContentResolver(), uri);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int count;
        String serverId;
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case SCORE_ID:
                serverId = uri.getLastPathSegment();
                count = db.update(SCORE_TABLE_NAME, values,
                        ScoreColumns.PROJECT_ID + "=" + serverId,
                        null);
                break;
            case VIDEO_ID:
                serverId = uri.getLastPathSegment();
                count = db.update(VIDEO_TABLE_NAME, values,
                        VideoColumns.IID + "=" + serverId,
                        null);
                break;
            case FAVORITE_ID:
                serverId = uri.getLastPathSegment();
                count = db.update(FAVORITE_TABLE_NAME, values,
                        FavoriteColumns._ID + "=" + serverId,
                        null);
                break;
            default: {
                throw new UnsupportedOperationException("Cannot update URI: " + uri);
            }
        }
        Log.v(TAG, "*** notifyChange() id: " + serverId + " url " + uri);
        if (getContext() == null) {
            Log.e(TAG, "query: fail, getContent() = null");
            return 0;
        }
        notifyChange(getContext().getContentResolver(), uri);
        return count;
    }

    /**
     * Notify affected URIs of changes.
     */
    private void notifyChange(ContentResolver resolver, Uri uri) {
        resolver.notifyChange(uri, null);
    }
}
