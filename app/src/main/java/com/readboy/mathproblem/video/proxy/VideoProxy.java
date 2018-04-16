package com.readboy.mathproblem.video.proxy;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.readboy.mathproblem.application.SubjectType;
import com.readboy.mathproblem.cache.CacheEngine;
import com.readboy.mathproblem.cache.ProjectEntityWrapper;
import com.readboy.mathproblem.db.Favorite;
import com.readboy.mathproblem.exercise.ExerciseActivity;
import com.readboy.mathproblem.http.auth.AuthCallback;
import com.readboy.mathproblem.http.auth.AuthManager;
import com.readboy.mathproblem.http.download.DownloadManager;
import com.readboy.mathproblem.http.download.VideoUrlCallback;
import com.readboy.mathproblem.http.response.ProjectEntity;
import com.readboy.mathproblem.http.response.VideoInfoEntity;
import com.readboy.mathproblem.http.response.VideoInfoEntity.VideoInfo;
import com.readboy.mathproblem.js.JsUtils;
import com.readboy.mathproblem.util.FileUtils;
import com.readboy.mathproblem.util.ToastUtils;
import com.readboy.mathproblem.util.VideoUtils;
import com.readboy.mathproblem.video.movie.MovieActivity;
import com.readboy.mathproblem.video.movie.VideoExtraNames;
import com.readboy.textbook.chapter.Content;

import java.util.ArrayList;
import java.util.List;

import retrofit2.http.HTTP;


/**
 * Created by oubin on 2017/10/12.
 */

public class VideoProxy implements VideoExtraNames {
    private static final String TAG = "oubin_VideoProxy";

    public static final int REQUEST_CODE = 2;
    public static final String FILENAME_SCHEME = "filename://";
    public static final String HTTP_SCHEME = "http";
    /**
     * 注意，因为有收藏功能，收藏的视频不知是否通过网络，所有，统一用该scheme收藏，标志video，path。
     */
    public static final String VIDEO_URI_SCHEME = "videoUri";


    public static void play(VideoExtras extras, Context context) {
        Intent intent = new Intent(context, MovieActivity.class);
        intent.putExtra(EXTRA_PATH, extras.path);
        intent.putExtra(EXTRA_URL_CONFIG, extras.urlConfig);
        intent.putExtra(EXTRA_URL, extras.url);
        intent.putExtra(EXTRA_SEEK_POSITION, extras.position);
        intent.putExtra(EXTRA_INDEX, extras.index);
        intent.putExtra(EXTRA_ABSOLUTE_PATH, extras.isAbsolutePath);
        intent.putExtra(EXTRA_EXERCISE_ENABLE, extras.exerciseEnable);
        intent.putExtra(EXTRA_FINISH_TYPE, extras.finishType);
        intent.putStringArrayListExtra(EXTRA_MEDIA_LIST, extras.mediaList);
        ProjectEntityWrapper wrapper = CacheEngine.getCurrentProjectWrapper();
        if (wrapper != null) {
            intent.putExtra(EXTRA_PROJECT_SUBJECT, wrapper.getType());
            intent.putExtra(EXTRA_PROJECT_GRADE, wrapper.getGrade());
            intent.putExtra(EXTRA_PROJECT_INDEX, CacheEngine.getCurrentIndex());
        }
        Log.e(TAG, "play: extras = " + extras.toString());
        if (context instanceof Activity) {
            Log.e(TAG, "play: startActivity for result.");
            ((Activity) context).startActivityForResult(intent, REQUEST_CODE);
        } else {
            context.startActivity(intent);
        }
    }

    public static void playSmallWindow(int seekPosition, int index, Context context) {
//        Intent intent = new Intent(context, )

    }

    public static void play(String path, ArrayList<String> pathList, Context context) {
        Intent intent = new Intent(context, MovieActivity.class);
        intent.putExtra(VideoExtraNames.EXTRA_PATH, path);
        intent.putExtra(VideoExtraNames.EXTRA_MEDIA_LIST, pathList);
        context.startActivity(intent);
    }

    public static void playWithCurrentProject(Context context) {
        playWithCurrentProject(0, -1, context);
    }

    public static void playWithCurrentProject(Context context, int finishType) {
        playWithCurrentProject(0, -1, finishType, context);
    }

    public static void playWithCurrentProject(int videoIndex, int seekPosition, Context context) {
        playWithCurrentProject(videoIndex, seekPosition, TYPE_SET_RESULT, context);
    }

    /**
     * 更具当前Project，获取播放列表
     *
     * @param videoIndex   Project.getVideo中的位置
     * @param seekPosition 进度，单位秒
     */
    public static void playWithCurrentProject(int videoIndex, int seekPosition, int finishType, Context context) {
        Log.e(TAG, "playWithCurrentProject() called with: videoIndex = " + videoIndex + ", seekPosition = " + seekPosition);
        ProjectEntity.Project project = CacheEngine.getCurrentProject();
        if (project == null) {
            Log.e(TAG, "playWithCurrentProject: project == null");
            ToastUtils.show("无法获取当前视频列表。");
            return;
        }
        playWithProject(videoIndex, seekPosition, finishType, context, project);
    }

    public static void playWithProject(int videoIndex, int seekPosition, int finishType, Context context, ProjectEntity.Project project) {
        Log.e(TAG, "playWithProject() called with: videoIndex = " + videoIndex + ", seekPosition = "
                + seekPosition + ", finishType = " + finishType + ", context = " + context
                + ", project = " + project + "");
        ArrayList<String> paths = new ArrayList<>();
        List<VideoInfo> videos = project.getVideoInfoList();
        if (videos == null) {
            Log.e(TAG, "playWithCurrentProject: video = null");
            return;
        }
        for (VideoInfo video : videos) {
            //为了保证收藏视频时，是收藏uri，而不是本地链接。
//            String fileName = FileUtils.getFileName(video.getVideoUri());
//            if (VideoUtils.videoIsExist(fileName)) {
//                paths.add(VideoUtils.getVideoPath(fileName));
//            } else {
//                String url = video.getUrl();
//                if (!TextUtils.isEmpty(url) && AuthManager.isValid(url)) {
//                    paths.add(url);
//                } else {
            paths.add(VIDEO_URI_SCHEME + "://" + video.getVideoUri());
//                }
//            }
        }
//        Log.e(TAG, "playWithCurrentProject: mediaList = " + Arrays.toString(paths.toArray()));
        VideoExtras extras = new VideoExtras();
        extras.mediaList = paths;
        extras.index = videoIndex;
        extras.position = seekPosition;
        extras.isAbsolutePath = true;
        extras.finishType = finishType;
        if (project.getExercises() == null || project.getExercises().size() == 0) {
            extras.exerciseEnable = false;
        } else {
            extras.exerciseEnable = true;
        }
        VideoProxy.play(extras, context);
    }

//    @Deprecated
//    public static void playWithCurrentProject2(int index, int seekPosition, Context context) {
//        ArrayList<String> paths = new ArrayList<>();
//        ProjectEntity.Project project = CacheEngine.getCurrentProject();
//        List<VideoInfo> videos = project.getVideoInfoList();
//        if (videos == null) {
//            Log.e(TAG, "playWithCurrentProject: video = null");
//            return;
//        }
//        for (VideoInfo video : videos) {
//            paths.add(video.getVideoUri());
//        }
////        Log.e(TAG, "playWithCurrentProject: mediaList = " + Arrays.toString(paths.toArray()));
//        VideoProxy.VideoExtras extras = new VideoProxy.VideoExtras();
//        extras.mediaList = paths;
//        extras.index = index;
//        extras.position = seekPosition;
//        extras.isAbsolutePath = false;
//        VideoProxy.play(extras, context);
//    }

    public static void playWithPath(String path, Context context) {
        Intent intent = createIntent(context);
        putPath(path, intent);
        context.startActivity(intent);
    }

    public static void playWithUrl(String url, Context context) {
        Intent intent = new Intent(context, MovieActivity.class);
        intent.putExtra(VideoExtraNames.EXTRA_URL, url);
        context.startActivity(intent);
    }

    public static Intent createIntent(Context context) {
        return new Intent(context, MovieActivity.class);
    }

    public static Intent putPath(String path, Intent intent) {
        intent.putExtra(VideoExtraNames.EXTRA_PATH, path);
        return intent;
    }

    public static Intent putUrl(String url, Intent intent) {
        intent.putExtra(VideoExtraNames.EXTRA_URL, url);
        return intent;
    }

    public static Intent putMediaList(ArrayList<String> list, Intent intent) {
        intent.putStringArrayListExtra(VideoExtraNames.EXTRA_MEDIA_LIST, list);
        return intent;
    }

    public static Intent putIndex(int index, Intent intent) {
        intent.putExtra(VideoExtraNames.EXTRA_URL, index);
        return intent;
    }

    public static boolean gotoExerciseActivity(Context context) {
//        ProjectEntity.Project project = CacheEngine.getCurrentProject();
//        if (project == null || Lists.isEmpty(project.getExercises())){
//            ToastUtils.show(context, "暂无对应练习题。");
//            return false;
//        }
        Intent intent = new Intent(context, ExerciseActivity.class);
        ProjectEntityWrapper wrapper = CacheEngine.getCurrentProjectWrapper();
        if (wrapper != null) {
            intent.putExtra(ExerciseActivity.EXTRA_TYPE, wrapper.getType());
            intent.putExtra(ExerciseActivity.EXTRA_GRADE, wrapper.getGrade());
            intent.putExtra(ExerciseActivity.EXTRA_INDEX, CacheEngine.getCurrentIndex());
            context.startActivity(intent);
            return true;
        } else {
            ToastUtils.showShort(context, "非正常操作，数据丢失，无法跳转到练习题界面");
            return false;
        }
    }

    public static void gotoStudyActivity(Content content, int index, int position) {

    }

    public static boolean isFavorite(ContentResolver resolver, String filename) {
        return Favorite.hasFavorite(resolver, filename);
    }

    /**
     * @param uriPath 可能是文件路径，或者url
     */
    public static boolean favoriteVideo(ContentResolver resolver, String uriPath) {
        if (TextUtils.isEmpty(uriPath)) {
            return false;
        }
        Favorite favorite = new Favorite();
        Uri uri = Uri.parse(uriPath);
        if (HTTP_SCHEME.equals(uri.getScheme()) || isUriPath(uriPath)) {
            favorite.mUrl = uriPath;
        } else {
            favorite.mPath = uriPath;
        }
        favorite.mName = FileUtils.getFileName(uriPath);
        Log.e(TAG, "favoriteVideo: favorite= " + favorite.toString());
        return Favorite.insert(resolver, favorite) != null;
    }

    public static boolean unFavoriteVideo(ContentResolver resolver, String uriPath) {
        if (TextUtils.isEmpty(uriPath)) {
            return false;
        }
        String name = FileUtils.getFileName(uriPath);
        return Favorite.delete(resolver, name);
    }

    public static boolean isUriPath(String uriString) {
        Uri uri = Uri.parse(uriString);
        return VIDEO_URI_SCHEME.equals(uri.getScheme());
    }

    public static void downloadVideo(String filename) {
        Log.e(TAG, "downloadVideo: fileName = " + filename);
//        DownloadManager.getInstance().addTask(filename);
    }

    public static void downloadVideo(int videoId) {
//        DownloadManager.getInstance().addTaskWithId(videoId);
    }

    public static void downloadVideoWithUrl(String url) {
//        DownloadManager.getInstance().addTaskWithUrl(url);
    }

    public static void downloadVideoWithUrl(Context context, String url) {
//        Intent intent = new Intent(VideoProxyReceiver.ACTION_DOWNLOAD_VIDEO);
//        intent.putExtra(EXTRA_URL, url);
////        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
//        context.sendBroadcast(intent);

        Uri uri = Uri.parse(url);
        if (VideoUtils.videoIsExist(FileUtils.getFileName(url))) {
            Log.e(TAG, "onReceive: have downloaded. filename = " + FileUtils.getFileName(url));
        } else {
            String scheme = uri.getScheme();
            if (VideoProxy.VIDEO_URI_SCHEME.equalsIgnoreCase(scheme)) {
                Log.e(TAG, "onReceive: video uri = " + uri.getPath());
                DownloadManager.getInstance().addTaskWithUri(uri.getPath(), null);
            } else if (VideoProxy.HTTP_SCHEME.equalsIgnoreCase(scheme)) {
                Log.e(TAG, "onReceive: video http = " + url);
                DownloadManager.getInstance().addTaskWithUrl(url);
            } else {
                Log.e(TAG, "onReceive: can not parse url, url = " + url);
            }
        }

    }

    public static boolean isDownloading(String path) {
        Uri uri = Uri.parse(path);
        String p = uri.getPath();
        return DownloadManager.getInstance().isDownloading(FileUtils.getFileName(p));
    }

    //通过文件名判断是否已下载
    public static boolean isDownloaded(String filename) {
        return VideoUtils.videoIsExist(filename);
    }

    public static void getVideoUrl(List<Integer> idList, VideoUrlCallback callback) {
        DownloadManager.getInstance().getVideoUrl(idList, callback);
    }

    public static boolean isValid(String url) {
        return AuthManager.isValid(url);
    }

    public static void getVideoAbsoluteUri(String uri, Context context, @NonNull AbsoluteUriCallback callback) {
        if (VideoUtils.exists(uri)) {
            callback.onResponse(VideoUtils.getVideoPath(FileUtils.getFileName(uri)), false);
        } else {
            AuthManager.registerAuth(context, uri, new AuthCallback() {
                @Override
                public void onAuth(String url) {
                    callback.onResponse(url, true);
                }

                @Override
                public void onError(Throwable throwable) {
                    callback.onError(throwable);
                }
            });
        }
    }

    public interface AbsoluteUriCallback {
        /**
         * 根据uri获取完整视频链接（鉴权）回调，可能会经过网络加载
         *
         * @param uri   uri
         * @param isUrl 是否是网络链接（http://...）, 否则为本地视频
         */
        void onResponse(String uri, boolean isUrl);

        /**
         * 获取失败回调
         *
         * @param throwable 异常类型
         */
        void onError(Throwable throwable);
    }

    public static class VideoExtras {
        public String path;
        public int urlConfig;
        public String url;
        public int position;
        public int index;
        public boolean isAbsolutePath = true;
        public boolean exerciseEnable;
        public int finishType;
        public ArrayList<String> mediaList;

        @Override
        public String toString() {
            return "VideoExtras{" +
                    "path='" + path + '\'' +
                    ", urlConfig=" + urlConfig +
                    ", url='" + url + '\'' +
                    ", position=" + position +
                    ", index=" + index +
                    ", isAbsolutePath=" + isAbsolutePath +
                    ", exerciseEnable=" + exerciseEnable +
                    '}';
        }
    }

}
