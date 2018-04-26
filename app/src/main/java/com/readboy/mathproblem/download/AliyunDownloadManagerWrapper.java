package com.readboy.mathproblem.download;

import android.content.SharedPreferences;
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
import com.readboy.mathproblem.application.Constants;
import com.readboy.mathproblem.application.MathApplication;
import com.readboy.mathproblem.http.download.DownloadDbController;
import com.readboy.mathproblem.http.download.DownloadEngine;
import com.readboy.mathproblem.http.download.DownloadModel;
import com.readboy.mathproblem.http.download.DownloadStatus;
import com.readboy.mathproblem.http.download.VideoInfoCallBack;
import com.readboy.mathproblem.http.request.IdsParams;
import com.readboy.mathproblem.http.response.VideoInfoEntity;
import com.readboy.mathproblem.http.response.VideoInfoEntity.VideoInfo;
import com.readboy.mathproblem.http.service.PostVideoInfoService;
import com.readboy.mathproblem.util.FileUtils;

import java.io.File;
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
 * Created by oubin on 2018/4/23
 * 新的下载一定要先创建DownloadModel, 并且添加到数据库中，再生产BaseDownloadTask进行下载。
 * 重构mAdapter, 写成观察者模式，Observer，参考{@link RecyclerView.AdapterDataObservable}
 *
 * @author oubin
 */

public class AliyunDownloadManagerWrapper {
    private static final String TAG = "oubin_AliyunDownloader";

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
    private DownloadDbController mDbController;

    private AliyunDownloadManagerWrapper() {
        Log.e(TAG, "AliyunDownloadManagerWrapper: create a AliyunDownloadManagerWrapper");
        mTaskObservable = new DownloadTaskObservable();
        mAliyunDownloadManager = AliyunDownloadManager.getInstance(MathApplication.getInstance());
        mVidStsHelper = new VidStsHelper();
        mDbController = new DownloadDbController();

    }

    public static AliyunDownloadManagerWrapper getInstance() {
        return Inner.instance;
    }

    private final static class Inner {
        private static AliyunDownloadManagerWrapper instance = new AliyunDownloadManagerWrapper();
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
        mAliyunDownloadManager.clearDownloadInfoListener();
    }

    public void onCreate() {
        Log.e(TAG, "onCreate: ");
        registerServiceConnectionListener();
        mRefreshStsCallback = new RefreshStsCallbackImpl();
        mAliyunDownloadManager.setRefreshStsCallback(mRefreshStsCallback);
        loadDownloadModel();
    }

    public void onDestroy() {
        Log.e(TAG, "onDestroy: ");

        if (!SUPPORT_BACKGROUND_DOWNLOAD) {
            mAliyunDownloadManager.stopDownloadMedias(mAliyunDownloadManager.getDownloadingMedias());
//            for (AliyunDownloadMediaInfo mediaInfo : mAliyunDownloadManager.getDownloadingMedias()) {
//                mAliyunDownloadManager.removeDownloadMedia(mediaInfo);
//            }
        }

        mAliyunDownloadManager.setRefreshStsCallback(null);
        mRefreshStsCallback = null;

        unregisterServiceConnectionListener();

        unregisterAllDownloadTaskObserver();

//        saveDownloadModel();
    }

    private void saveDownloadModel(){
        List<AliyunDownloadMediaInfo> mediaInfoList = mAliyunDownloadManager.getDownloadingMedias();
        Log.e(TAG, "saveDownloadModel: mediaInfoList = " + mediaInfoList.size());
        for (AliyunDownloadMediaInfo mediaInfo : mediaInfoList) {
            mDbController.replaceTask(mediaInfo.getVid(), mediaInfo.getTitle());
        }
    }

    private void loadDownloadModel(){
        List<DownloadModel> list = mDbController.getAllTasks();
        Log.e(TAG, "loadDownloadModel: size = " + list.size());
        for (DownloadModel model : list) {
            mDownloadModelVector.addAll(list);
            prepareDownload(model.getVid());
        }
    }

    public boolean isReady() {
        return true;
    }

    private AliyunDownloadMediaInfo getMediaInfo(int position) {
        return null;
    }

    private AliyunDownloadMediaInfo getMediaInfo(String vid) {
        List<AliyunDownloadMediaInfo> mediaInfoList = mAliyunDownloadManager.getDownloadingMedias();
        for (AliyunDownloadMediaInfo mediaInfo : mediaInfoList) {
            if (vid.equals(mediaInfo.getVid())) {
                return mediaInfo;
            }
        }
        return null;
    }

    private boolean isSame(AliyunDownloadMediaInfo outMediaInfo, AliyunDownloadMediaInfo downloadInfo) {
        if (downloadInfo != null && outMediaInfo != null) {
            return downloadInfo.getVid().equals(outMediaInfo.getVid())
                    && downloadInfo.getQuality().equals(outMediaInfo.getQuality())
                    && downloadInfo.getFormat().equals(outMediaInfo.getFormat())
                    && downloadInfo.isEncripted() == outMediaInfo.isEncripted();
        } else {
            return false;
        }
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
            DownloadModel model = new DownloadModel(mediaInfo);
            model.setStatus(DownloadStatus.convert(mediaInfo.getStatus()));
            modelList.add(model);
        }
        return modelList;
    }

    public int getStatus(final int id, String path) {

        return DownloadStatus.COMPLETED.ordinal();
    }

    public boolean isDownloading(String vid) {
        if (TextUtils.isEmpty(vid)) {
            return false;
        }
        List<AliyunDownloadMediaInfo> mediaInfoList = mAliyunDownloadManager.getDownloadingMedias();
        for (AliyunDownloadMediaInfo mediaInfo : mediaInfoList) {
            if (vid.equals(mediaInfo.getVid())) {
                return isDownloadingStatus(mediaInfo.getStatus());
            }
        }
        return false;
    }

    public void prepareDownload(final String vid) {
        mDbController.replaceTask(vid, "");
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

    private DownloadModel addTask(final String url, final String path) {
        Log.e(TAG, "addTask() called with: url = " + url + ", path = " + path + "");
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(path)) {
            return null;
        }

        return null;
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
                model.setMediaInfo(null);
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
     * @param vid    adapter中的位置，也就是{@link #mDownloadModelVector 中的位置}
     * @param status 想要改变的状态
     */
    public void updateDownloadStatus(String vid, DownloadStatus status) {
        switch (status) {
            case PAUSE:
                stopDownload(vid);
                break;
            case CONNECTING:
            case DOWNLOADING:
                startDownload(vid);
                break;
            case WAIT:
                stopDownload(vid);
                break;
            default:
                Log.e(TAG, "updateDownloadStatus: vid = " + vid + ", status = " + status);
                break;
        }
    }

    private void startDownload(String vid) {
        AliyunDownloadMediaInfo mediaInfo = getMediaInfo(vid);
        if (mediaInfo != null) {
            startDownload(mediaInfo);
        } else {
            prepareDownload(vid);
        }
    }

    private void startDownload(AliyunDownloadMediaInfo mediaInfo) {
        mAliyunDownloadManager.startDownloadMedia(mediaInfo);
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
        }
    }

    public void stopDownload(String vid) {
        AliyunDownloadMediaInfo mediaInfo = getMediaInfo(vid);
        if (mediaInfo != null) {
            stopDownload(mediaInfo);
        } else {
            Log.e(TAG, "stopDownload: stop fail, it not exit, vid = " + vid);
        }
    }

    private void stopDownload(AliyunDownloadMediaInfo mediaInfo) {
        mAliyunDownloadManager.stopDownloadMedia(mediaInfo);
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

    public List<AliyunDownloadMediaInfo> getDownloadMedias() {
        return mAliyunDownloadManager.getDownloadingMedias();
    }

    public void removeMediaInfo(AliyunDownloadMediaInfo mediaInfo) {
//        stopDownload(mediaInfo);
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

    public boolean isDownloadingStatus(AliyunDownloadMediaInfo.Status status) {
        return status == AliyunDownloadMediaInfo.Status.Prepare
                || status == AliyunDownloadMediaInfo.Status.Start
                || status == AliyunDownloadMediaInfo.Status.Wait;
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


    public void unregisterAllDownloadTaskObserver() {
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

        void onTaskProgress(AliyunDownloadMediaInfo mediaInfo, int soFarBytes, int totalBytes) {
//            Log.e(TAG, "onTaskProgress: mediaInfo id = " + mediaInfo.getId());
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                BaseDownloadTaskObserver observer = mObservers.get(i);
                if (observer.isContains(mediaInfo)) {
//                    Log.e(TAG, "onTaskProgress: i = " + i + ", mediaInfo = " + mediaInfo.getId());
                    observer.onTaskProgress(mediaInfo, soFarBytes, totalBytes);
                }
            }
        }

        void onTaskPause(AliyunDownloadMediaInfo mediaInfo) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                BaseDownloadTaskObserver observer = mObservers.get(i);
                if (observer.isContains(mediaInfo)) {
                    observer.onTaskPause(mediaInfo);
                }
            }
        }

        void onTaskWait(AliyunDownloadMediaInfo mediaInfo) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                BaseDownloadTaskObserver observer = mObservers.get(i);
                if (observer.isContains(mediaInfo)) {
                    observer.onTaskWait(mediaInfo);
                }
            }
        }

        void onTaskCompleted(AliyunDownloadMediaInfo mediaInfo) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                BaseDownloadTaskObserver observer = mObservers.get(i);
                if (observer.isContains(mediaInfo)) {
                    observer.onTaskCompleted(mediaInfo);
                }
            }
        }

        void onTaskError(AliyunDownloadMediaInfo mediaInfo, String message) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                BaseDownloadTaskObserver observer = mObservers.get(i);
                if (observer.isContains(mediaInfo)) {
                    observer.onTaskError(mediaInfo, message);
                }
            }
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

        public void onTaskStarted(AliyunDownloadMediaInfo info) {
            //do nothing
        }

        public void onTaskProgress(AliyunDownloadMediaInfo info, int soFarBytes, int totalBytes) {
        }

        public void onTaskPause(AliyunDownloadMediaInfo info) {
        }

        public void onTaskWait(AliyunDownloadMediaInfo info) {

        }

        public void onTaskCompleted(AliyunDownloadMediaInfo info) {
        }

        public void onTaskError(AliyunDownloadMediaInfo info, String message) {
        }

        public abstract boolean isContains(AliyunDownloadMediaInfo info);
    }

    /**
     * 下载的状态由该变量控制反馈给界面更新。收{@link #updateDownloadStatus(String, DownloadStatus)}影响
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
                        + ", Quality = " + mediaInfo.getQuality() + ", index = " + mediaInfo.getDownloadIndex()
                        + ", path = " + mediaInfo.getSavePath() + ", format = " + mediaInfo.getFormat()
                        + ", percent = " + mediaInfo.getProgress());
                if (size < mediaInfo.getSize()) {
                    info = mediaInfo;
                    size = mediaInfo.getSize();
                }
            }
            final AliyunDownloadMediaInfo mediaInfo = info;
            if (mAliyunDownloadManager.getStsRefreshCallback() == null) {
                Log.e(TAG, "run: why it is null.");
            }
            mAliyunDownloadManager.addDownloadMedia(mediaInfo);
            startDownload(mediaInfo);
//            notifyItemChanged(DownloadStatus.WAIT, list.get(0));
//            mTaskObservable.onTaskStarted();

        }

        @Override
        public void onStart(AliyunDownloadMediaInfo mediaInfo) {
            Log.e(TAG, "onStart: progress = " + mediaInfo.getProgress()
                    + ", size = " + mediaInfo.getSizeStr());
            mTaskObservable.onTaskStarted(mediaInfo);
        }

        @Override
        public void onProgress(AliyunDownloadMediaInfo mediaInfo, int i) {
            Log.e(TAG, "onProgress: size = " + mediaInfo.getSize() + ", i = " + i);
            long size = mediaInfo.getSize();
            mTaskObservable.onTaskProgress(mediaInfo, (int) (size * i * 0.01F), (int) size);
        }

        @Override
        public void onStop(AliyunDownloadMediaInfo mediaInfo) {
            Log.e(TAG, "onStop: ");
            mTaskObservable.onTaskPause(mediaInfo);
        }

        @Override
        public void onCompletion(AliyunDownloadMediaInfo mediaInfo) {
            Log.e(TAG, "onCompletion: oldPath = " + mediaInfo.getSavePath() + ", title = " + mediaInfo.getTitle());
            mTaskObservable.onTaskCompleted(mediaInfo);

            String oldPath = mediaInfo.getSavePath();
            String newPath = Constants.getVideoPath(mediaInfo.getTitle());
            FileUtils.renameTo(oldPath, newPath);

            mAliyunDownloadManager.removeDownloadMedia(mediaInfo);
            mDbController.deleteTask(mediaInfo.getVid());

        }

        @Override
        public void onError(AliyunDownloadMediaInfo mediaInfo, int i, String s, String s1) {
            Log.e(TAG, "onError() called with: i = " + i + ", s = " + s + ", s1 = " + s1 + "");
            mTaskObservable.onTaskError(mediaInfo, s);
        }

        @Override
        public void onWait(AliyunDownloadMediaInfo mediaInfo) {
            Log.e(TAG, "onWait: ");
            mTaskObservable.onTaskWait(mediaInfo);
        }

        @Override
        public void onM3u8IndexUpdate(AliyunDownloadMediaInfo mediaInfo, int i) {
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
