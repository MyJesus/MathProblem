package com.readboy.mathproblem.db;

import android.net.Uri;
import android.provider.BaseColumns;

import com.readboy.mathproblem.video.proxy.VideoProxy;

/**
 * Created by oubin on 2017/9/4.
 */

public final class ProjectContract {

    public static final String AUTHORITY = "com.readboy.mathproblem";

    private ProjectContract() {
    }


    public interface ScoreColumns {

        /**
         * The content:// style URL for this table.
         */
        Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/scores");

        /**
         * 来自服务上的id, Project.id
         */
        String PROJECT_ID = "project_id";
        String SCORE = "score";

//        int _ID_INDEX = 0;
//        int PROJECT_ID_INDEX = 1;
//        int SCORE_INDEX = 2;
        int PROJECT_ID_INDEX = 0;
        int SCORE_INDEX = 1;
    }

    public interface VideoColumns {

        /**
         * The content:// style URL for this table.
         */
        Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/videos");

        /**
         * 视频id，来自服务器数据库
         */
        String IID = "iid";

        /**
         * 下载状态
         *
         * @see com.readboy.mathproblem.db.Video.Status
         */
        String STATUS = "status";

        /**
         * 是否收藏，收藏时间，long类型
         * -1代表没有收藏
         */
        String FAVORITE = "favorite";

        /**
         * 视频小图链接或者本地路径
         */
        String THUMBNAIL = "thumbnail";

        /**
         * 视频下载链接
         */
        String URL = "url";

        /**
         * 视频下载路径
         */
        String PATH = "path";

        /**
         * 视频标题
         */
        String NAME = "name";

        /**
         * 下载进度，用于断点续下载
         */
        String PROGRESS = "progress";

//        int _ID_INDEX = 0;
        int IID_INDEX = 0;
        int STATUS_INDEX = 1;
        int FAVORITE_INDEX = 2;
        int THUMBNAIL_INDEX = 3;
        int URL_INDEX = 4;
        int PATH_INDEX = 5;
        int NAME_INDEX = 6;
        int PROGRESS_INDEX = 7;
    }

    public interface Video2Columns extends BaseColumns {
        String FILE_NAME = "filename";
    }

    public interface FavoriteColumns extends BaseColumns {

        /**
         * The content:// style URL for this table.
         */
        Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/favorites");

        /**
         * 视频小图链接或者本地路径
         */
        String THUMBNAIL = "thumbnail";

        /**
         * 视频下载链接, http开头，或者{@link VideoProxy#SCHEME_VIDEO_URI}videoUri
         */
        String URL = "url";

        /**
         * 视频下载路径
         */
        String PATH = "path";

        /**
         * 视频标题
         */
        String NAME = "name";

        /**
         * 收藏时间
         */
        String TIME = "time";

        int _ID_INDEX = 0;
        int THUMBNAIL_INDEX = 1;
        int URL_INDEX = 2;
        int PATH_INDEX = 3;
        int NAME_INDEX = 4;
        int TIME_INDEX = 5;

    }
}
