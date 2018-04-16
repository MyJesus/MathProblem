package com.readboy.mathproblem.http.download;

import android.database.Observable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadConnectListener;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadSampleListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.exception.FileDownloadHttpException;
import com.liulishuo.filedownloader.util.FileDownloadUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.readboy.mathproblem.application.MathApplication;
import com.readboy.mathproblem.http.auth.AuthCallback;
import com.readboy.mathproblem.http.auth.AuthManager;
import com.readboy.mathproblem.http.auth.MultiAuthCallback;
import com.readboy.mathproblem.http.request.IdsParams;
import com.readboy.mathproblem.http.response.VideoInfoEntity;
import com.readboy.mathproblem.http.response.VideoInfoEntity.VideoInfo;
import com.readboy.mathproblem.http.service.PostVideoInfoService;
import com.readboy.mathproblem.util.FileUtils;
import com.readboy.mathproblem.util.SparseArrays;
import com.readboy.mathproblem.util.ToastUtils;
import com.readboy.mathproblem.util.VideoUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.readboy.mathproblem.http.download.DownloadContract.SUPPORT_BACKGROUND_DOWNLOAD;

/**
 * Created by oubin on 2017/9/29.
 * //TODO 视频下载数据库单独编写，方便解耦，复用。
 * 新的下载一定要先创建DownloadModel, 并且添加到数据库中，再生产BaseDownloadTask进行下载。
 * 重构mAdapter, 写成观察者模式，Observer，参考{@link RecyclerView.AdapterDataObservable}
 * @author oubin
 */

public class DownloadManager {
    private static final String TAG = "oubin_DownloadManager";

    private DownloadDbController dbController;

    private final List<DownloadModel> mDownloadModelVector = new Vector<>();
    //TODO 删除，合并到modelSparseArray.
    private SparseArray<BaseDownloadTask> taskSparseArray = new SparseArray<>();
    //确保线程安全, 防止多次网络请求获取下载链接。
    //key = id, value = videoInfo.
    private SparseArray<VideoInfo> mVideoSparseArray = new SparseArray<>();
    private static final String EMPTY_URL = "";

    private RecyclerView.Adapter mAdapter;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    private DownloadTaskObservable mTaskObservable;

    private DownloadManager() {
        Log.e(TAG, "DownloadManager: create a DownloadManager");
        dbController = new DownloadDbController();
        mTaskObservable = new DownloadTaskObservable();
        List<DownloadModel> modelList = dbController.getAllTasks();
        for (DownloadModel model : modelList) {
            Log.e(TAG, "DownloadManager: model : " + model.getFileName());
            if (VideoUtils.videoIsExist(model.getFileName())) {
                Log.e(TAG, "DownloadManager: file is completed : filename = " + model.getFileName());
                dbController.deleteTask(model.getTaskId());
            } else {
                addDownloadMode(model);
                model.setStatus(DownloadStatus.PAUSE);
            }
        }
    }

    public static DownloadManager getInstance() {
        return Inner.instance;
    }

    private final static class Inner {
        private static DownloadManager instance = new DownloadManager();
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        this.mAdapter = adapter;
    }

    public void addTaskWithThumbnail(final BaseDownloadTask task) {
        taskSparseArray.put(task.getId(), task);
    }

    public void removeTask(final int id) {
        taskSparseArray.remove(id);
    }

    public void deleteTaskById(final int id) {
        Log.e(TAG, "deleteTaskWithIndex() called with: id = " + id + "");
//        deleteTaskWithId(mDownloadModelVector.keyAt(index));
        int index = indexOfKey(id);
        if (index >= 0) {
            deleteTaskByIndex(indexOfKey(id));
        } else {
            Log.e(TAG, "deleteTaskById: index = " + index + ", id = " + id);
        }
    }

    /**
     * 删除下载任务。
     *
     * @param index {@link #mDownloadModelVector}中的位置
     */
    public void deleteTaskByIndex(final int index) {
        Log.e(TAG, "deleteTaskByIndex: thread = " + Thread.currentThread().getName());
        //1.停止下载
        DownloadModel model = mDownloadModelVector.get(index);
        if (model == null) {
            Log.e(TAG, "deleteTaskByIndex: model == null, id = " + index);
//            notifyDataSetChanged();
            return;
        }
        BaseDownloadTask task = mDownloadModelVector.get(index).getDownloadTask();
        if (task != null && task.isRunning()) {
            task.pause();
        } else {
            Log.e(TAG, "deleteTaskByIndex: task == null or not running, task = " + task);
        }

        //2.移除内存
//        int position = mDownloadModelVector.indexOfKey(id);
//        int position = mDownloadModelVector.indexOfKey(id);
//        int position = position;
        Log.e(TAG, "deleteTaskByIndex: position = " + index);
        //4.通知界面更新
//        if (mAdapter != null && position >= 0) {
//            mHandler.post(() -> {
        mDownloadModelVector.remove(index);
//                mAdapter.notifyItemRemoved(position);
//            });
//        }

        //3.移除数据库
        dbController.deleteTask(model.getTaskId());

    }

    private void releaseTask() {
        int count = taskSparseArray.size();
        for (int i = 0; i < count; i++) {
            BaseDownloadTask task = taskSparseArray.valueAt(i);
            if (task!= null){
                task.setListener(null);
            }
        }
//        taskSparseArray.clear();
//        mDownloadModelVector.clear();
//        urlMap.clear();
    }

    private FileDownloadConnectListener listener;

    private void registerServiceConnectionListener() {
        if (listener != null) {
            FileDownloader.getImpl().removeServiceConnectListener(listener);
        }

        listener = new FileDownloadConnectListener() {

            @Override
            public void connected() {
                Log.e(TAG, "connected: ");
            }

            @Override
            public void disconnected() {
                Log.e(TAG, "disconnected: ");
            }
        };

        FileDownloader.getImpl().addServiceConnectListener(listener);
    }

    private void unregisterServiceConnectionListener() {
        FileDownloader.getImpl().removeServiceConnectListener(listener);
        listener = null;
    }

    public void onCreate() {
        if (!FileDownloader.getImpl().isServiceConnected()) {
            Log.e(TAG, "onCreate: ");
            FileDownloader.getImpl().bindService();
            registerServiceConnectionListener();
        }

//        initModelFaker();
        //正在下载过程中，退出应用，再次初始化model
//        if (mDownloadModelVector != null) {
//            int size = mDownloadModelVector.size();
//            for (int i = 0; i < size; i++) {
//                mDownloadModelVector.valueAt(i).setStatus(DownloadStatus.PAUSE);
//            }
//        }
    }

    public void onDestroy() {
        Log.e(TAG, "onDestroy: ");
        FileDownloader.getImpl().pauseAll();
        if (FileDownloader.getImpl().isServiceConnected()) {
            unregisterServiceConnectionListener();
            if (!SUPPORT_BACKGROUND_DOWNLOAD) {
                FileDownloader.getImpl().unBindService();
            }
        }
//        releaseTask();
        unregisterAllDownloadTastObserver();
    }

    public boolean isReady() {
        return FileDownloader.getImpl().isServiceConnected();
    }

    public DownloadModel valueAt(final int index) {
//        return mDownloadModelVector.valueAt(position);
        return mDownloadModelVector.get(index);
    }

    public DownloadModel getById(final int id) {
        int index = indexOfKey(id);
        return index >= 0 ? mDownloadModelVector.get(index) : null;
    }

    private int indexOfKey(int key) {
        int size = mDownloadModelVector.size();
        for (int i = 0; i < size; i++) {
            if (valueAt(i).getTaskId() == key) {
                return i;
            }
        }
        return -1;
    }

    private void addDownloadMode(DownloadModel model) {
        Log.e(TAG, "addDownloadMode: model = " + model.getFileName());
        mDownloadModelVector.add(model);
//        mDownloadModelVector.put(model.getTaskId(), model);
    }

//    public SparseArray<DownloadModel> getDownloadArray() {
//        return mDownloadModelVector;
//    }

//    public ArrayMap<Integer, DownloadModel> getDownloadArray() {
//        return mDownloadModelVector;
//    }

    public List<DownloadModel> getDownloadArray() {
        return mDownloadModelVector;
    }

    public int getStatus(final int id, String path) {
        return FileDownloader.getImpl().getStatus(id, path);
    }

    public boolean isDownloading(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return false;
        }
        boolean result = false;
        int size = mDownloadModelVector.size();
        for (int i = 0; i < size; i++) {
//            DownloadModel model = mDownloadModelVector.valueAt(i);
            DownloadModel model = valueAt(i);
            if (fileName.equals(model.getFileName()) && DownloadUtils.isDownloading(model.getTaskId())) {
                result = true;
                break;
            }
        }
        return result;
    }

    public boolean isDownloading(int id) {
        int size = mDownloadModelVector.size();
        for (int i = 0; i < size; i++) {
            DownloadModel model = valueAt(i);
            if (model.getVideoId() == id && DownloadUtils.isDownloading(model.getTaskId())) {
                return true;
            }
        }
        return false;
    }

//    @Deprecated
//    public void addTaskWithId(int videoId) {
//        if (VideoUtils.videoIsExist(videoId)) {
//            return;
//        }
//        VideoInfo videoInfo = mVideoSparseArray.valueAt(videoId);
//        //无需获取下载链接，直接下载。
//        if (videoInfo != null) {
//            AuthManager.registerAuth(MathApplication.getInstance(), videoInfo.getVideoUri(), new SimpleAuthCallback() {
//                @Override
//                public void onAuth(String url) {
//                    DownloadModel model = addTaskWithUrl(url);
//                    model.setThumbnailUrl(videoInfo.getThumbnailUrl());
////                    startDownload(model);
//                }
//            });
//        } else {
//            getVideoInfo(videoId, new VideoInfoCallBack() {
//                @Override
//                public void onResponse(List<VideoInfo> videoList) {
//                    for (VideoInfo info : videoList) {
//                        mVideoSparseArray.put(videoId, info);
//                        DownloadModel model = addTaskWithUrl(info.getUrl());
//                        model.setThumbnailUrl(info.getThumbnailUrl());
////                        startDownload(model);
//                    }
//                }
//
//                @Override
//                public void onError(Throwable throwable) {
//                    ToastUtils.show(throwable.toString());
//                }
//            });
//        }
//    }

    /**
     * @param uri          videoInfo
     * @param thumbnailUrl 缩列图。
     */
    public void addTaskWithUri(String uri, final String thumbnailUrl) {
        String fileName = FileUtils.getFileName(uri);
        if (VideoUtils.exists(uri)) {
            Log.e(TAG, "addTaskWithUri: Video is exit: filename = " + uri);
            return;
        }
        DownloadModel downloadModel = getDownloadModel(fileName);
        if (downloadModel != null && !AuthManager.isValid(downloadModel.getUrl())) {
            Log.e(TAG, "addTaskWithUri: remove uri = " + uri);
            mDownloadModelVector.remove(indexOfKey(downloadModel.getTaskId()));
        }
        int index = indexOfKey(getTaskId(fileName));
        if (index < 0) {
            AuthManager.registerAuth(MathApplication.getInstance(), uri, new AuthCallback() {
                @Override
                public void onAuth(String url) {
                    DownloadModel model = addTaskWithUrl(url);
                    if (model != null) {
                        model.setThumbnailUrl(thumbnailUrl);
//                        int index = mDownloadModelVector.indexOfValue(model);
//                        int index = indexOfKey(model.getTaskId());
//                        startDownload(index);
                    } else {
                        Log.e(TAG, "addTaskWithUri onAuth : model == null");
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    ToastUtils.show("无法下载视频，请检查网络");
                }
            });
        } else {
            startDownload(index);
        }
    }

    public DownloadModel addTaskWithUrl(final String url) {
        DownloadModel model = addTask(url, createPath(url));
        addDownloadMode(model);
        startDownload(model);
        return model;
    }

    private DownloadModel addTask(final String url, final String path) {
        Log.e(TAG, "addTask() called with: url = " + url + ", path = " + path + "");
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(path)) {
            return null;
        }

        final int id = FileDownloadUtils.generateId(url, path);
        DownloadModel model = getById(id);
        if (model != null) {
            Log.e(TAG, "addTask: is exit. id = " + model.getTaskId());
            return model;
        }
        final DownloadModel newModel = dbController.addTask(url, path);
        if (newModel != null) {
            Log.e(TAG, "addTask: append ; " + newModel.getFileName());
//            addDownloadMode(newModel);
        }
        return newModel;
    }

    /**
     * @param fileName 未编码的文件名，包括文件后缀
     * @return task id
     */
    private int getTaskId(String fileName) {
        int size = mDownloadModelVector.size();
        for (int i = 0; i < size; i++) {
            DownloadModel model = valueAt(i);
            if (fileName.equals(model.getFileName())) {
                return model.getTaskId();
            }
        }
        return -1;
    }

    private DownloadModel getDownloadModel(String filename) {
        int size = mDownloadModelVector.size();
        for (int i = 0; i < size; i++) {
            DownloadModel model = valueAt(i);
            if (filename.equals(model.getFileName())) {
                return model;
            }
        }
        return null;
    }

    private String createPath(final String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        Uri uri = Uri.parse(url);
        return FileDownloadUtils.getDefaultSaveFilePath(uri.getPath());
    }

    /**
     * 下载的状态由该变量控制反馈给界面更新。收{@link #updateDownloadStatus(int, DownloadStatus)}影响
     */
    private FileDownloadListener taskDownloadListener = new FileDownloadSampleListener() {

        @Override
        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            super.pending(task, soFarBytes, totalBytes);
            Log.e(TAG, "pending() called with: task = " + task.getId() + ", soFarBytes = " + soFarBytes
                    + ", totalBytes = " + totalBytes + "");
            notifyItemChanged(DownloadStatus.WAIT, task);
            mTaskObservable.onTaskStarted(task);
        }

        @Override
        protected void started(BaseDownloadTask task) {
            super.started(task);
            Log.e(TAG, "started: task id = " + task.getId());
            notifyItemChanged(DownloadStatus.CONNECTING, task);
        }

        @Override
        protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
            super.connected(task, etag, isContinue, soFarBytes, totalBytes);
            Log.e(TAG, "connected() called with: task = " + task.getId() + ", etag = " + etag
                    + ", isContinue = " + isContinue + ", soFarBytes = " + soFarBytes
                    + ", totalBytes = " + totalBytes + "");
            notifyItemChanged(DownloadStatus.DOWNLOADING, task);
        }

        @Override
        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            super.progress(task, soFarBytes, totalBytes);
//            Log.e(TAG, "progress() called with: task = " + task.getId() + ", soFarBytes = " + soFarBytes
//                    + ", totalBytes = " + totalBytes + "");
            mTaskObservable.onTaskProgress(task, soFarBytes, totalBytes);
        }

        @Override
        protected void error(BaseDownloadTask task, Throwable e) {
            super.error(task, e);
            Log.e(TAG, "error: task = " + FileUtils.getFileName(task.getUrl()) + ", e:" + e.toString());
            if (e instanceof FileDownloadHttpException) {
                FileDownloadHttpException exception = (FileDownloadHttpException) e;
                if (exception.getCode() == AuthManager.ERROR_CODE_EXPIRE) {
                    Log.e(TAG, "error: id = " + task.getId());
                    DownloadModel model = getById(task.getId());
                    if (model != null) {
                        asyncAdjustUrl(getById(task.getId()));
                        return;
                    }
                }
            }
            notifyItemChanged(DownloadStatus.ERROR, task);
            taskSparseArray.remove(task.getId());
            mTaskObservable.onTaskError(task, e);
        }

        /**
         * 暂停，可能是手动暂停，也有可能是取消下载任务。
         */
        @Override
        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            super.paused(task, soFarBytes, totalBytes);
            Log.e(TAG, "paused() called with: task = " + FileUtils.getFileName(task.getFilename())
                    + ", soFarBytes = " + soFarBytes + ", totalBytes = " + totalBytes + "");
            notifyItemChanged(DownloadStatus.PAUSE, task);
            removeTask(task.getId());
            mTaskObservable.onTaskPause(task);
        }

        @Override
        protected void completed(BaseDownloadTask task) {
            super.completed(task);
            Log.e(TAG, "completed: task = " + FileUtils.getFileName(task.getUrl()));
            File file = new File(task.getPath());
            File completedFile = new File(file.getParent(), FileUtils.getFileName(task.getUrl()));

            boolean isRenameSuccess = file.renameTo(completedFile);
            if (!isRenameSuccess) {
                ToastUtils.show("重命名失败！");
            } else {
                deleteTaskById(task.getId());
                notifyDataSetChanged();
            }
            mTaskObservable.onTaskCompleted(task);
        }
    };

    private void notifyItemChanged(BaseDownloadTask task) {
        int position = indexOfKey(task.getId());
        if (mAdapter != null) {
            mAdapter.notifyItemChanged(position);
        }
    }

    private void notifyItemChanged(DownloadStatus status, BaseDownloadTask task) {
        if (task == null) {
            Log.e(TAG, "notifyItemChanged: task = null");
            return;
        }
        notifyItemChanged(status, task.getId());
    }

    private void notifyItemChanged(DownloadStatus status, int taskId) {
        Log.e(TAG, "notifyItemChanged: size = " + mDownloadModelVector.size());
        int index = indexOfKey(taskId);
        DownloadModel model = getById(taskId);
        if (model != null) {
            model.setStatus(status);
            if (status == DownloadStatus.PAUSE) {
                model.setDownloadTask(null);
            }
        }
        if (mAdapter != null) {
            //TODO: 写法有问题，可能model已被移除
            if (model != null) {
                Log.e(TAG, "notifyItemChanged: notify index = " + index);
                if (index >= 0) {
                    mAdapter.notifyItemChanged(index);
                }
            } else {
                Log.e(TAG, "notifyItemChanged: remove index = " + index);
                if (index >= 0) {
                    mAdapter.notifyItemRemoved(index);
                }
            }
        }
    }

    private void notifyItemRemoved(BaseDownloadTask task) {
        int index = indexOfKey(task.getId());
        mDownloadModelVector.remove(indexOfKey(task.getId()));
        if (mAdapter != null) {
            Log.e(TAG, "notifyItemRemoved: index = " + index);
            mAdapter.notifyItemRemoved(index);
        }
    }

    private void notifyDataSetChanged() {
        if (mAdapter == null) {
            return;
        }
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 主动修改状态，但不通知界面更新，由{@link #taskDownloadListener}反馈通知界面更新。
     *
     * @param position adapter中的位置，也就是{@link #mDownloadModelVector 中的位置}
     * @param status   想要改变的状态
     */
    public void updateDownloadStatus(int position, DownloadStatus status) {
        switch (status) {
            case PAUSE:
                stopDownload(position);
                break;
            case CONNECTING:
            case DOWNLOADING:
                startDownload(position);
                break;
            case WAIT:
                stopDownload(position);
                break;
            default:
                Log.e(TAG, "updateDownloadStatus: position = " + position + ", status = " + status);
                break;
        }
    }

    private void startDownload(DownloadModel model) {
//        int index = mDownloadModelVector.indexOfValue(model);
        if (model == null) {
            return;
        }
        int index = indexOfKey(model.getTaskId());
        if (index >= 0) {
            startDownload(index);
        } else {
            Log.e(TAG, "startDownload: can't download, index = " + index);
        }
    }

    private void startDownload(int position) {
        if (position < 0 || position > mDownloadModelVector.size()) {
            Log.e(TAG, "startDownload: ArrayIndexOutOfBoundsException, position = " + position);
//            notifyDataSetChanged();
            return;

        }
        Log.e(TAG, "startDownload: position = " + position);
        DownloadModel model = valueAt(position);
        Log.e(TAG, "startDownload: url = " + model.getUrl());
        if (!AuthManager.isValid(model.getUrl())) {
            Log.e(TAG, "startDownload: url not valid, url = " + model.getUrl());
            syncAdjustUrl(model);
            model = valueAt(position);
        }
        if (DownloadUtils.isDownloading(model.getTaskId())) {
            DownloadStatus status = model.getStatus();
            Log.e(TAG, "startDownload: 正在下载中。。。, now status = " + status.getDescribe());
            if (status == DownloadStatus.PAUSE || status == DownloadStatus.ERROR) {
                model.setStatus(DownloadStatus.DOWNLOADING);
                notifyItemChanged(DownloadStatus.DOWNLOADING, model.getTaskId());
            }
            return;
        }
        final BaseDownloadTask task = FileDownloader.getImpl().create(model.getUrl())
                .setPath(model.getPath())
                .setCallbackProgressTimes(200)
                .setListener(taskDownloadListener);
        if (task == null) {
            ToastUtils.showLong("未知错误，无法下载。");
            return;
        }
        if (task.getId() != model.getTaskId()) {
            Log.e(TAG, "startDownload: new id : old = " + model.getTaskId() + ", newId = " + task.getId());
            int oldId = model.getTaskId();
            int index = indexOfKey(oldId);
            dbController.deleteTask(oldId);

            model.setTaskId(task.getId());
            model.setPath(task.getPath());
            DownloadModel newModel = addTask(task.getUrl(), task.getPath());
            mDownloadModelVector.set(index, newModel);
        }
        addTaskWithThumbnail(task);
        task.start();

        valueAt(position).setStatus(DownloadStatus.DOWNLOADING);
        valueAt(position).setDownloadTask(task);
    }

    public void stopDownload(int position) {
        if (position < 0 || position > mDownloadModelVector.size()) {
            Log.e(TAG, "stopDownload: ArrayIndexOutOfBoundsException, position = " + position);
            notifyDataSetChanged();
            return;
        }
        Log.e(TAG, "stopDownload() called with: position = " + position + "");
        DownloadModel model = valueAt(position);
        if (model != null) {
            if (FileDownloader.getImpl().pause(model.getTaskId()) == 0) {
                Log.e(TAG, "stopDownload: not downloading.");
//                deleteTaskById(model.getTaskId());
//                notifyDataSetChanged();
                notifyItemChanged(DownloadStatus.PAUSE, model.getTaskId());
//                return;
            }
            valueAt(position).setStatus(DownloadStatus.PAUSE);
            valueAt(position).setDownloadTask(null);
        }
    }

    private Call<VideoInfoEntity> getVideoInfoFromHttp(int id, Callback<VideoInfoEntity> callback) {
        return getVideoInfoFromHttp(Collections.singletonList(id), callback);
    }

    private Call<VideoInfoEntity> getVideoInfoFromHttp(List<Integer> ids, Callback<VideoInfoEntity> callback) {
//        Log.e(TAG, "getVideoInfoFromHttp: ");
        PostVideoInfoService service = DownloadEngine.getInstance().create(PostVideoInfoService.class);
        IdsParams params = new IdsParams(ids);
        Call<VideoInfoEntity> call = service.getVideoUrl(params.getMap());
        call.enqueue(callback);
        return call;
    }

    public void registerDownloadTaskObserver(BaseDownloadTaskObserver observer) {
        mTaskObservable.registerObserver(observer);
    }

    public void unregisterDownloadTaskObserver(BaseDownloadTaskObserver observer) {
        mTaskObservable.unregisterObserver(observer);
    }

    public void cacheVideoInfo(final int id) {
        if (mVideoSparseArray.get(id) == null) {
            return;
        }
        getVideoInfoFromHttp(id, new Callback<VideoInfoEntity>() {
            @Override
            public void onResponse(Call<VideoInfoEntity> call, Response<VideoInfoEntity> response) {
                VideoInfoEntity entity = response.body();
                if (entity != null && entity.getData() != null) {
                    for (VideoInfo info : entity.getData()) {
                        mVideoSparseArray.put(info.getId(), info);
                    }
                }
            }

            @Override
            public void onFailure(Call<VideoInfoEntity> call, Throwable t) {
                Log.e(TAG, "onFailure: t = " + t.toString());
            }
        });
    }

    private Call<VideoInfoEntity> getVideoInfo(int videoId, @NonNull VideoInfoCallBack callBack) {
        return getVideoInfo(Collections.singletonList(videoId), callBack);
    }

    public Call<VideoInfoEntity> getVideoInfo(List<Integer> idList, @NonNull VideoInfoCallBack callBack) {
        List<VideoInfo> responseList = new ArrayList<>();
        List<Integer> unCacheData = new ArrayList<>();
        for (Integer id : idList) {
            VideoInfo videoInfo = mVideoSparseArray.get(id);
            if (videoInfo != null) {
                responseList.add(videoInfo);
            } else {
                unCacheData.add(id);
            }
        }
        if (unCacheData.size() == 0) {
            return null;
        } else {
            return getVideoInfoFromHttp(unCacheData, new Callback<VideoInfoEntity>() {
                @Override
                public void onResponse(Call<VideoInfoEntity> call, Response<VideoInfoEntity> response) {
                    VideoInfoEntity entity = response.body();
                    if (entity != null) {
                        if (entity.getData() != null) {
                            responseList.addAll(entity.getData());
                            for (VideoInfo videoInfo : entity.getData()) {
                                mVideoSparseArray.put(videoInfo.getId(), videoInfo);
                            }

                        } else {
                            callBack.onError(new NullPointerException("服务器返回数据为空！"));
                        }
                    }
                }

                @Override
                public void onFailure(Call<VideoInfoEntity> call, Throwable t) {
                    callBack.onError(t);
                }
            });
        }
    }

    public void getVideoUrl(List<Integer> idList, VideoUrlCallback callback) {
        SparseArray<String> uriArray = new SparseArray<>();
        for (int id : idList) {
            VideoInfo videoInfo = mVideoSparseArray.get(id);
            if (videoInfo != null) {
                uriArray.put(id, videoInfo.getVideoUri());
            } else {
                break;
            }
        }
        if (uriArray.size() == idList.size()) {
            registerAuth(callback, uriArray);
        } else {
            getVideoInfo(idList, new VideoInfoCallBack() {
                @Override
                public void onResponse(List<VideoInfo> videoList) {
                    SparseArray<String> uris = new SparseArray<String>();
                    for (VideoInfo videoInfo : videoList) {
                        uris.put(videoInfo.getId(), videoInfo.getVideoUri());
                    }
                    registerAuth(callback, uris);
                }

                @Override
                public void onError(Throwable throwable) {
                    callback.onError(throwable);
                }
            });
        }
    }


    /**
     * 需要校准顺序，防止界面显示混乱。
     */
    private DownloadModel updateModelCauseUrl(DownloadModel sourceModel, String newUrl) {
        Log.e(TAG, "updateModelCauseUrl: init.");
        logArray();
        int oldTaskId = sourceModel.getTaskId();
        int index = indexOfKey(oldTaskId);
        dbController.deleteTask(oldTaskId);
        Log.e(TAG, "updateModelCauseUrl: index = " + index);
        if (index >= 0) {
            DownloadModel newModel = addTask(newUrl, sourceModel.getPath());
            mDownloadModelVector.set(index, newModel);
            logArray();
            return newModel;
        } else {
            Log.e(TAG, "updateModelCauseUrl: index = " + index);
        }
        return null;
    }

    private void logArray() {
        logArray(mDownloadModelVector);
    }

    private void logArray(List<DownloadModel> modelList) {
        int size = modelList.size();
        for (int i = 0; i < size; i++) {
            DownloadModel model = modelList.get(i);
            Log.e(TAG, "logArray: i = " + i + " : " + "id:" + model.getTaskId() + ",   " + model.getFileName());
        }
    }

    /**
     * 可能鉴权失败，因为{@link AuthManager#sAuthentication}可能为空。
     */
    private void syncAdjustUrl(DownloadModel model) {
        if (model == null) {
            Log.e(TAG, "syncAdjustUrl: model = null");
            return;
        }
        Log.e(TAG, "syncAdjustUrl: model url = " + model.getUrl());
        Uri uri = Uri.parse(model.getUrl());
        String newUrl = AuthManager.auth(uri.getPath());
        Log.e(TAG, "syncAdjustUrl: newUrl = " + newUrl);
        if (TextUtils.isEmpty(newUrl)) {
            Log.e(TAG, "syncAdjustUrl: auth fail.");
        } else {
            updateModelCauseUrl(model, newUrl);
        }
    }

    private void asyncAdjustUrl(DownloadModel model) {
        if (model == null) {
            Log.e(TAG, "asyncAdjustUrl: model = null.");
            return;
        }
        String url = model.getUrl();
        Log.e(TAG, "adjustUrl: url = " + url);
        Uri uri = Uri.parse(url);
        String path = uri.getPath();
        AuthManager.registerAuth(MathApplication.getInstance(), path, new AuthCallback() {
            @Override
            public void onAuth(String url) {
                Log.e(TAG, "onAuth() called with: url = " + url + "");
                DownloadModel newModel = updateModelCauseUrl(model, url);
                startDownload(newModel);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e(TAG, "onError: throwable = " + throwable.toString());
                ToastUtils.show("无法下载视频。");
            }
        });

    }

    private void registerAuth(final VideoUrlCallback callback, final SparseArray<String> uriArray) {
        AuthManager.registerAuth(MathApplication.getInstance(), SparseArrays.valueSet(uriArray), new MultiAuthCallback() {
            @Override
            public void onAuth(Map<String, String> urlMap) {
                SparseArray<String> urlArray = new SparseArray<>();
                int size = uriArray.size();
                for (int i = 0; i < size; i++) {
                    urlArray.put(uriArray.keyAt(i), urlMap.get(uriArray.valueAt(i)));
                }
                callback.onResponse(urlArray);
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        });
    }

    public void unregisterAllDownloadTastObserver() {
        mTaskObservable.unregisterAll();
    }

    public boolean hasObservers() {
        return mTaskObservable.hasObservers();
    }

    private static class DownloadTaskObservable extends Observable<BaseDownloadTaskObserver> {

        boolean hasObservers() {
            return !mObservers.isEmpty();
        }

        void onTaskStarted(BaseDownloadTask task) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                BaseDownloadTaskObserver observer = mObservers.get(i);
                if (observer.isContains(task)) {
                    observer.onTaskStarted(task);
                }
            }
        }

        void onTaskProgress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
//            Log.e(TAG, "onTaskProgress: task id = " + task.getId());
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                BaseDownloadTaskObserver observer = mObservers.get(i);
                if (observer.isContains(task)) {
//                    Log.e(TAG, "onTaskProgress: i = " + i + ", task = " + task.getId());
                    observer.onTaskProgress(task, soFarBytes, totalBytes);
                }
            }
        }

        void onTaskPause(BaseDownloadTask task) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                BaseDownloadTaskObserver observer = mObservers.get(i);
                if (observer.isContains(task)) {
                    observer.onTaskPause(task);
                }
            }
        }

        void onTaskCompleted(BaseDownloadTask task) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                BaseDownloadTaskObserver observer = mObservers.get(i);
                if (observer.isContains(task)) {
                    observer.onTaskCompleted(task);
                }
            }
        }

        void onTaskError(BaseDownloadTask task, Throwable t) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                BaseDownloadTaskObserver observer = mObservers.get(i);
                if (observer.isContains(task)) {
                    observer.onTaskError(task, t);
                }
            }
        }
    }

    public static class FileNameTaskObserver extends BaseDownloadTaskObserver<String> {

        @Override
        public boolean isContains(BaseDownloadTask task) {
//            Log.e(TAG, "isContains: task = " + FileUtils.getFileName(task.getUrl()));
            return set.contains(FileUtils.getFileName(task.getUrl()));
        }
    }

    public static abstract class BaseDownloadTaskObserver<T> {

        protected Set<T> set = new HashSet<T>();

        public int size() {
            return set.size();
        }

        public void addObserver(T t) {
            set.add(t);
        }

        public void removeObserver(T t) {
            set.remove(t);
        }

        public void clearObserver() {
            set.clear();
        }

        public void onTaskStarted(BaseDownloadTask task) {
            //do nothing
        }

        public void onTaskProgress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        }

        public void onTaskPause(BaseDownloadTask task) {
        }

        public void onTaskCompleted(BaseDownloadTask task) {
        }

        public void onTaskError(BaseDownloadTask task, Throwable t) {
        }

        public abstract boolean isContains(BaseDownloadTask task);
    }

}
