package com.readboy.mathproblem.download;

import android.database.Observable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import com.aliyun.vodplayer.downloader.AliyunDownloadInfoListener;
import com.aliyun.vodplayer.downloader.AliyunDownloadManager;
import com.aliyun.vodplayer.downloader.AliyunDownloadMediaInfo;
import com.aliyun.vodplayer.downloader.AliyunRefreshStsCallback;
import com.aliyun.vodplayer.media.AliyunVidSts;
import com.readboy.aliyunplayerlib.helper.VidStsHelper;
import com.readboy.mathproblem.application.MathApplication;
import com.readboy.mathproblem.http.download.DownloadEngine;
import com.readboy.mathproblem.http.download.DownloadModel;
import com.readboy.mathproblem.http.download.DownloadStatus;
import com.readboy.mathproblem.http.download.DownloadUtils;
import com.readboy.mathproblem.http.download.VideoInfoCallBack;
import com.readboy.mathproblem.http.request.IdsParams;
import com.readboy.mathproblem.http.response.VideoInfoEntity;
import com.readboy.mathproblem.http.response.VideoInfoEntity.VideoInfo;
import com.readboy.mathproblem.http.service.PostVideoInfoService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.readboy.mathproblem.http.download.DownloadContract.SUPPORT_BACKGROUND_DOWNLOAD;

/**
 * Created by oubin on 2017/9/29.
 * //TODO 视频下载数据库单独编写，方便解耦，复用。
 * 新的下载一定要先创建DownloadModel, 并且添加到数据库中，再生产BaseDownloadTask进行下载。
 * 重构mAdapter, 写成观察者模式，Observer，参考{@link RecyclerView.AdapterDataObservable}
 *
 * @author oubin
 */

public class AliDownloadManagerWrapper {
    private static final String TAG = "oubin_AliDownloader";

    private final List<DownloadModel> mDownloadModelVector = new Vector<>();
    /**
     * key = id, value = videoInfo.
     */
    private SparseArray<VideoInfo> mVideoSparseArray = new SparseArray<>();

    private RecyclerView.Adapter mAdapter;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    private DownloadTaskObservable mTaskObservable;

    private AliyunDownloadManager mAliyunDownloadManager;
    private AliyunDownloadInfoListener mDownloadListener;
    private VidStsHelper mVidStsHelper;
    private AliyunRefreshStsCallback mRefreshStsCallback;

    private AliDownloadManagerWrapper() {
        Log.e(TAG, "AliDownloadManagerWrapper: create a AliDownloadManagerWrapper");
        mTaskObservable = new DownloadTaskObservable();
        mAliyunDownloadManager = AliyunDownloadManager.getInstance(MathApplication.getInstance());
        mVidStsHelper = new VidStsHelper();
        mRefreshStsCallback = new RefreshStsCallbackImpl();
        mAliyunDownloadManager.setRefreshStsCallback(mRefreshStsCallback);

    }

    public static AliDownloadManagerWrapper getInstance() {
        return Inner.instance;
    }

    private final static class Inner {
        private static AliDownloadManagerWrapper instance = new AliDownloadManagerWrapper();
    }

    public static AliyunDownloadMediaInfo getFakeMediaInfo() {
        return getMediaInfo("404b068d841a4054bb80de7340376ccc");
    }

    public static AliyunDownloadMediaInfo getMediaInfo(String vid) {
        AliyunDownloadMediaInfo mediaInfo = new AliyunDownloadMediaInfo();
        mediaInfo.setVid(vid);
        return mediaInfo;
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        this.mAdapter = adapter;
    }

    private void registerServiceConnectionListener() {
        unregisterServiceConnectionListener();
        mDownloadListener = new DownloadInfoListener();
        mAliyunDownloadManager.addDownloadInfoListener(mDownloadListener);
    }

    private void unregisterServiceConnectionListener() {
        if (mDownloadListener != null) {
            mAliyunDownloadManager.removeDownloadInfoListener(mDownloadListener);
        }
    }

    public void onCreate() {
        registerServiceConnectionListener();
    }

    public void onDestroy() {
        Log.e(TAG, "onDestroy: ");

        if (!SUPPORT_BACKGROUND_DOWNLOAD) {
            mAliyunDownloadManager.stopDownloadMedias(mAliyunDownloadManager.getDownloadingMedias());
            for (AliyunDownloadMediaInfo mediaInfo : mAliyunDownloadManager.getDownloadingMedias()) {
                mAliyunDownloadManager.removeDownloadMedia(mediaInfo);
            }
        }

        mAliyunDownloadManager.setRefreshStsCallback(null);
        mRefreshStsCallback = null;
        mAliyunDownloadManager.clearDownloadInfoListener();

        unregisterServiceConnectionListener();

        unregisterAllDownloadTastObserver();
    }

    public boolean isReady() {
        return true;
    }

    private AliyunDownloadMediaInfo getMediaInfo(int position) {
        return null;
    }

    public DownloadModel valueAt(final int index) {
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
    }

    /**
     * 为了兼容旧的界面，DownloadModel
     */
    public List<DownloadModel> getDownloadArray() {
        List<AliyunDownloadMediaInfo> infoList = mAliyunDownloadManager.getDownloadingMedias();
        List<DownloadModel> modelList = new ArrayList<>();
        for (AliyunDownloadMediaInfo mediaInfo : infoList) {
            modelList.add(new DownloadModel(mediaInfo));
        }
        return mDownloadModelVector;
    }

    public int getStatus(final int id, String path) {

        return DownloadStatus.COMPLETED.ordinal();
    }

    public boolean isDownloading(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return false;
        }
        boolean result = false;
        int size = mDownloadModelVector.size();
        for (int i = 0; i < size; i++) {
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

    public void prepareDownload(final String vid) {
        Log.e(TAG, "prepareDownload() called with: vid = " + vid + "");
        Log.e(TAG, "prepareDownload: size = " + mAliyunDownloadManager.getDownloadingMedias().size());
        mVidStsHelper.getVidSts(new VidStsHelper.OnStsResultListener() {
            @Override
            public void onSuccess(String akid, String akSecret, String token) {
                Log.e(TAG, "onSuccess() called with: akid = " + akid + ", akSecret = " + akSecret + ", token = " + token + "");
                AliyunVidSts vidSts = new AliyunVidSts();
                vidSts.setVid(vid);
                vidSts.setAcId(akid);
                vidSts.setAkSceret(akSecret);
                vidSts.setSecurityToken(token);
                mAliyunDownloadManager.prepareDownloadMedia(vidSts);
            }

            @Override
            public void onFail(int errno) {
                Log.e(TAG, "onFail() called with: errno = " + errno + "");
            }
        });
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

        return null;
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

        return "";
    }


    private void notifyItemChanged(AliyunDownloadMediaInfo info) {
        int position = indexOfKey(info.getDownloadIndex());
        if (mAdapter != null) {
            mAdapter.notifyItemChanged(position);
        }
    }

    private void notifyItemChanged(DownloadStatus status, AliyunDownloadMediaInfo info) {
        if (info == null) {
            Log.e(TAG, "notifyItemChanged: task = null");
            return;
        }
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

    private void notifyItemRemoved(AliyunDownloadMediaInfo info) {

    }

    private void notifyDataSetChanged() {
        if (mAdapter == null) {
            return;
        }
        mAdapter.notifyDataSetChanged();
    }

    private void notifyItemChange(DownloadStatus status, AliyunDownloadMediaInfo info) {

    }

    /**
     * 主动修改状态，但不通知界面更新，由{@link #mDownloadListener}反馈通知界面更新。
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
            mAliyunDownloadManager.stopDownloadMedia(getMediaInfo(position));
            Log.e(TAG, "stopDownload: not downloading.");
            notifyItemChanged(DownloadStatus.PAUSE, model.getTaskId());
            valueAt(position).setStatus(DownloadStatus.PAUSE);
            valueAt(position).setDownloadTask(null);
        }
    }

    private Call<VideoInfoEntity> getVideoInfoFromHttp(int id, Callback<VideoInfoEntity> callback) {
        return getVideoInfoFromHttp(Collections.singletonList(id), callback);
    }

    private Call<VideoInfoEntity> getVideoInfoFromHttp(List<Integer> ids, Callback<VideoInfoEntity> callback) {
        PostVideoInfoService service = DownloadEngine.getInstance().create(PostVideoInfoService.class);
        IdsParams params = new IdsParams(ids);
        Call<VideoInfoEntity> call = service.getVideoUrl(params.getMap());
        call.enqueue(callback);
        return call;
    }

    public List<AliyunDownloadMediaInfo> getDownloadMedias(){
        return mAliyunDownloadManager.getDownloadingMedias();
    }

    public void removeMediaInfo(AliyunDownloadMediaInfo mediaInfo){
        mAliyunDownloadManager.removeDownloadMedia(mediaInfo);
    }

    public void registerDownloadTaskObserver(BaseDownloadTaskObserver observer) {
        mTaskObservable.registerObserver(observer);
    }

    public void unregisterDownloadTaskObserver(BaseDownloadTaskObserver observer) {
        mTaskObservable.unregisterObserver(observer);
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

    /**
     * 需要校准顺序，防止界面显示混乱。
     */
    private DownloadModel updateModelCauseUrl(DownloadModel sourceModel, String newUrl) {
        Log.e(TAG, "updateModelCauseUrl: init.");
        logArray();
        int oldTaskId = sourceModel.getTaskId();
        int index = indexOfKey(oldTaskId);
//        dbController.deleteTask(oldTaskId);
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

        void onTaskStarted(AliyunDownloadMediaInfo info) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                BaseDownloadTaskObserver observer = mObservers.get(i);
                if (observer.isContains(info)) {
                    observer.onTaskStarted(info);
                }
            }
        }

        void onTaskProgress(AliyunDownloadMediaInfo task, int soFarBytes, int totalBytes) {
//            Log.e(TAG, "onTaskProgress: task id = " + task.getId());
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                BaseDownloadTaskObserver observer = mObservers.get(i);
                if (observer.isContains(task)) {
//                    Log.e(TAG, "onTaskProgress: i = " + i + ", task = " + task.getId());
                    observer.onTaskProgress(task, soFarBytes, totalBytes);
                }
            }
        }

        void onTaskPause(AliyunDownloadMediaInfo task) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                BaseDownloadTaskObserver observer = mObservers.get(i);
                if (observer.isContains(task)) {
                    observer.onTaskPause(task);
                }
            }
        }

        void onTaskWait(AliyunDownloadMediaInfo task) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                BaseDownloadTaskObserver observer = mObservers.get(i);
                if (observer.isContains(task)) {
                    observer.onTaskWait(task);
                }
            }
        }

        void onTaskCompleted(AliyunDownloadMediaInfo task) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                BaseDownloadTaskObserver observer = mObservers.get(i);
                if (observer.isContains(task)) {
                    observer.onTaskCompleted(task);
                }
            }
        }

        void onTaskError(AliyunDownloadMediaInfo task, String message) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                BaseDownloadTaskObserver observer = mObservers.get(i);
                if (observer.isContains(task)) {
                    observer.onTaskError(task, message);
                }
            }
        }
    }

    public static class FileNameTaskObserver extends BaseDownloadTaskObserver<String> {

        @Override
        public boolean isContains(AliyunDownloadMediaInfo info) {
//            Log.e(TAG, "isContains: task = " + FileUtils.getFileName(task.getUrl()));
            return set.contains(info.getTitle());
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

        public void onTaskStarted(AliyunDownloadMediaInfo task) {
            //do nothing
        }

        public void onTaskProgress(AliyunDownloadMediaInfo task, int soFarBytes, int totalBytes) {
        }

        public void onTaskPause(AliyunDownloadMediaInfo task) {
        }

        public void onTaskWait(AliyunDownloadMediaInfo info) {

        }

        public void onTaskCompleted(AliyunDownloadMediaInfo task) {
        }

        public void onTaskError(AliyunDownloadMediaInfo task, String message) {
        }

        public abstract boolean isContains(AliyunDownloadMediaInfo task);
    }

    /**
     * 下载的状态由该变量控制反馈给界面更新。收{@link #updateDownloadStatus(int, DownloadStatus)}影响
     */
    private class DownloadInfoListener implements AliyunDownloadInfoListener {

        @Override
        public void onPrepared(List<AliyunDownloadMediaInfo> list) {
            Log.e(TAG, "onPrepared: thread = " + Thread.currentThread());
            Log.e(TAG, "onPrepared: size = " + list.size());
            AliyunDownloadMediaInfo info = null;
            int size = 0;
            for (AliyunDownloadMediaInfo mediaInfo : list) {
                Log.e(TAG, "onPrepared: mediaInfo = " + mediaInfo.getTitle() + ". size = " + mediaInfo.getSizeStr()
                        + ", " + mediaInfo.getCoverUrl() + ", " + mediaInfo.getVid() + ", " + mediaInfo.getDownloadIndex()
                        + ", Quality = " + mediaInfo.getQuality());
                if (size < mediaInfo.getSize()) {
                    info = mediaInfo;
                    size = mediaInfo.getSize();
                }
            }
            final AliyunDownloadMediaInfo mediaInfo = info;
            mAliyunDownloadManager.addDownloadMedia(mediaInfo);
            if (mAliyunDownloadManager.getStsRefreshCallback() == null) {
                Log.e(TAG, "run: why it is null.");
            }
            mAliyunDownloadManager.startDownloadMedia(mediaInfo);
//            notifyItemChanged(DownloadStatus.WAIT, list.get(0));
//            mTaskObservable.onTaskStarted();

        }

        @Override
        public void onStart(AliyunDownloadMediaInfo aliyunDownloadMediaInfo) {
            Log.e(TAG, "onStart: ");
            mTaskObservable.onTaskStarted(aliyunDownloadMediaInfo);
        }

        @Override
        public void onProgress(AliyunDownloadMediaInfo mediaInfo, int i) {
            Log.e(TAG, "onProgress: size = " + mediaInfo.getSize() + ", duration = " + mediaInfo.getDuration() + ", i = " + i);
            long size = mediaInfo.getDuration();
            mTaskObservable.onTaskProgress(mediaInfo, (int) (size * i * 0.01F), (int) size);
        }

        @Override
        public void onStop(AliyunDownloadMediaInfo aliyunDownloadMediaInfo) {
            Log.e(TAG, "onStop: ");
            mTaskObservable.onTaskPause(aliyunDownloadMediaInfo);
        }

        @Override
        public void onCompletion(AliyunDownloadMediaInfo aliyunDownloadMediaInfo) {
            Log.e(TAG, "onCompletion: ");
            mTaskObservable.onTaskCompleted(aliyunDownloadMediaInfo);
        }

        @Override
        public void onError(AliyunDownloadMediaInfo aliyunDownloadMediaInfo, int i, String s, String s1) {
            Log.e(TAG, "onError() called with: i = " + i + ", s = " + s + ", s1 = " + s1 + "");
            mTaskObservable.onTaskError(aliyunDownloadMediaInfo, s);
        }

        @Override
        public void onWait(AliyunDownloadMediaInfo aliyunDownloadMediaInfo) {
            Log.e(TAG, "onWait: ");
            mTaskObservable.onTaskWait(aliyunDownloadMediaInfo);
        }

        @Override
        public void onM3u8IndexUpdate(AliyunDownloadMediaInfo aliyunDownloadMediaInfo, int i) {
            Log.e(TAG, "onM3u8IndexUpdate: i = " + i);
        }
    }

    private class RefreshStsCallbackImpl implements AliyunRefreshStsCallback {

        @Override
        public AliyunVidSts refreshSts(String vid, String quality, String format, String title, boolean encript) {
            Log.e(TAG, "refreshSts() called with: vid = " + vid + ", quality = " + quality + ", format = " + format + ", title = " + title + ", encript = " + encript + "");
            AliyunVidSts vidSts = mVidStsHelper.getVidSts();
            if (vidSts == null) {
                Log.e(TAG, "refreshSts: vidSts = null.");
                return null;
            } else {
                vidSts.setVid(vid);
                vidSts.setQuality(quality);
                vidSts.setTitle(title);
                return vidSts;
            }

        }
    }

    public static class DownloadException extends Exception {

        private String message;

        public DownloadException(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return super.toString() + ", message = " + message;
        }
    }

}
