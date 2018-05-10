package com.readboy.mathproblem.db;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by oubin on 2017/9/4.
 * Uri为CONTENT_URI + mProjectId
 * /**
 * 0-5分，对应0-100%
 */

public class Score implements ProjectContract.ScoreColumns {

    public static final long INVALID_ID = -1;

    //    public long _id = INVALID_ID;
    public long mProjectId;
    /**
     * 0-5分，对应0-100%
     */
    public int mScore = 0;

    private static final String[] QUERY_COLUMNS = {
//            _ID,
            PROJECT_ID,
            SCORE
    };

    /**
     * @param projectId 课题的id, Project.id
     * @param score     分数 0-5分
     */
    public Score(int projectId, int score) {
//        this._id = INVALID_ID;
        this.mProjectId = projectId;
        this.mScore = score;
    }

    public Score(Cursor cursor) {
//        this._id = cursor.getInt(_ID_INDEX);
        this.mProjectId = cursor.getInt(PROJECT_ID_INDEX);
        this.mScore = cursor.getInt(SCORE_INDEX);
    }

    public static ContentValues createContentValues(Score score) {
        ContentValues values = new ContentValues();
//        if (score._id != INVALID_ID) {
//            values.put(_ID, score._id);
//        }
        values.put(PROJECT_ID, score.mProjectId);
        values.put(SCORE, score.mScore);

        return values;
    }

    public static int getScore(ContentResolver resolver, int projectId) {
        int score = 0;
        try (Cursor cursor = resolver.query(CONTENT_URI, QUERY_COLUMNS, PROJECT_ID + "=?",
                new String[]{String.valueOf(projectId)}, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                Score scoreObj = new Score(cursor);
                score = scoreObj.mScore;
            }
        }
        return score;
    }

    public static Uri getUri(long projectId) {
        return ContentUris.withAppendedId(CONTENT_URI, projectId);
    }

    public static boolean insertScore(ContentResolver resolver, Score score) {
        return resolver.insert(CONTENT_URI, createContentValues(score)) != null;
    }

    public static boolean updateScore(ContentResolver resolver, Score score) {
        int row = resolver.update(getUri(score.mProjectId), createContentValues(score), null, null);
        return row == 1;
    }

    public static boolean deleteScore(ContentResolver resolver, Score score) {
        return -1 != resolver.delete(getUri(score.mProjectId), null, null);
    }
}
