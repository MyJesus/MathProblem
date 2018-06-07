package com.readboy.mathproblem.download;

import android.content.Context;
import android.database.Observable;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;

import com.aliyun.vodplayer.downloader.AliyunDownloadInfoListener;
import com.aliyun.vodplayer.downloader.AliyunDownloadManager;
import com.aliyun.vodplayer.downloader.AliyunDownloadMediaInfo;
import com.aliyun.vodplayer.downloader.AliyunRefreshStsCallback;
import com.aliyun.vodplayer.media.AliyunVidSts;
import com.readboy.aliyunplayerlib.helper.VidStsHelper;
import com.readboy.mathproblem.application.Constants;
import com.readboy.mathproblem.application.MathApplication;
import com.readboy.mathproblem.http.download.DownloadDbController;
import com.readboy.mathproblem.http.download.DownloadModel;
import com.readboy.mathproblem.http.download.DownloadStatus;
import com.readboy.mathproblem.http.response.VideoInfoEntity.VideoInfo;
import com.readboy.mathproblem.util.FileUtils;
import com.readboy.mathproblem.util.NetworkUtils;
import com.readboy.mathproblem.util.ToastUtils;
import com.readboy.mathproblem.util.VideoUtils;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentSkipListMap;

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

    @Deprecated
    private final List<DownloadModel> mDownloadModelVector = new Vector<>();

    /**
     * key: vid, String类型
     * value: DownloadModel
     */
    private Map<String, DownloadModel> mDownloadMap = new ConcurrentSkipListMap<>();

    private RecyclerView.Adapter mAdapter;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    private DownloadTaskObservable mTaskObservable;

    private AliyunDownloadManager mAliyunDownloadManager;
    private AliyunDownloadInfoListener mDownloadListener;
    private VidStsHelper mVidStsHelper;
    private AliyunRefreshStsCallback mRefreshStsCallback;
    private DownloadDbController mDbController;
    private AliyunVidSts mAliyunVidSts;

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

    protected void setAdapter(RecyclerView.Adapter adapter) {
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
            mDownloadListener = null;
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
        }

        mAliyunDownloadManager.setRefreshStsCallback(null);
        mRefreshStsCallback = null;

        unregisterServiceConnectionListener();

        unregisterAllDownloadTaskObserver();

    }

    private void loadDownloadModel() {
        List<DownloadModel> list = mDbController.getAllTasks();
        Log.e(TAG, "loadDownloadModel: size = " + list.size());
        for (DownloadModel model : list) {
//            mDownloadModelVector.addAll(list);
            if (VideoUtils.videoIsExist(model.getFileName())) {
                Log.e(TAG, "loadDownloadModel: file is exit.");
                mDbController.deleteTask(model.getVid());
            } else {
                addDownloadMode(model);
//                prepareDownload(model.getVid());
            }
        }
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

//    private DownloadModel valueAt(final int index) {
//        return mDownloadModelVector.get(index);
//    }

    private void addDownloadMode(DownloadModel model) {
        Log.e(TAG, "addDownloadMode: model = " + model.getFileName());
//        mDownloadModelVector.add(model);
        mDownloadMap.put(model.getVid(), model);
    }

    /**
     * 删除缓存数据，界面显示的数据
     * @param key vid
     */
    private void removeDownloadMode(String key){
        mDownloadMap.remove(key);
    }

    private DownloadModel getDownloadMode(String key){
        return mDownloadMap.get(key);
    }

    /**
     * 为了兼容旧的界面，DownloadModel
     */
    public List<DownloadModel> getDownloadArray() {
//        List<AliyunDownloadMediaInfo> infoList = mAliyunDownloadManager.getDownloadingMedias();
//        List<DownloadModel> modelList = new ArrayList<>();
//        for (AliyunDownloadMediaInfo mediaInfo : infoList) {
//            DownloadModel model = new DownloadModel(mediaInfo);
//            model.setStatus(DownloadStatus.convert(mediaInfo.getStatus()));
//            modelList.add(model);
//        }
//        return modelList;

        return new ArrayList<>(mDownloadMap.values());
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
        Log.e(TAG, "prepareDownload() called with: vid = " + vid + "");
        boolean result = mDbController.replaceTask(vid);
        Log.e(TAG, "prepareDownload: replaceTask result = " + result);
        Log.e(TAG, "prepareDownload: size = " + mAliyunDownloadManager.getDownloadingMedias().size());
        if (mAliyunVidSts != null) {
            mAliyunVidSts.setVid(vid);
            mAliyunDownloadManager.prepareDownloadMedia(mAliyunVidSts);
        } else {
            mVidStsHelper.getVidSts(new VidStsHelper.OnStsResultListener() {
                @Override
                public void onSuccess(String akid, String akSecret, String token) {
                    Log.e(TAG, "onSuccess() called with: akid = " + akid + ", akSecret = " + akSecret + ", token = " + token + "");
                    AliyunVidSts vidSts = new AliyunVidSts();
                    vidSts.setVid(vid);
                    vidSts.setAcId(akid);
                    vidSts.setAkSceret(akSecret);
                    vidSts.setSecurityToken(token);
                    mAliyunVidSts = vidSts;
                    mAliyunDownloadManager.prepareDownloadMedia(vidSts);
                }

                @Override
                public void onFail(int errno) {
                    Log.e(TAG, "onFail() called with: errno = " + errno + "");
                    checkNetwork(errno);
                }
            });
        }
    }

    /**
     * 检查网络情况，并做统一处理
     *
     * @return 无网络，返回false;
     * 有网络返回true。
     */
    private boolean checkNetwork(int errno) {
        Context context = MathApplication.getInstance();
        if (!NetworkUtils.isConnected(context)){
            ToastUtils.show(context, "下载失败：网络不可用，请检查网络");
            return false;
        }else {
            if (errno == VidStsHelper.ERRNO_SIGNATURE_INVALID) {
                ToastUtils.show("下载失败：请确保系统时间正常，再点击重新加载");
            } else if (errno == VidStsHelper.ERRNO_DEVICE_UNAUTH) {
                ToastUtils.show("下载失败：机器未授权，暂时无法播放");
            } else {
                ToastUtils.show("下载失败：未知错误");
            }
            return true;
        }
    }

    private boolean checkNetwork(){
        Context context = MathApplication.getInstance();
        if (!NetworkUtils.isConnected(context)){
            ToastUtils.show(context, "下载失败：网络不可用，请检查网络");
            return false;
        }else {
            return true;
        }
    }

    public void prepareDownload(VideoInfo videoInfo) {
        Log.e(TAG, "prepareDownload: vieoInfo vid = " + videoInfo);
        //先判断是否有在下载队列里
        AliyunDownloadMediaInfo mediaInfo = getMediaInfo(videoInfo.getVid());
        if (mediaInfo != null) {
            startDownload(mediaInfo);
            return;
        }
        if (getDownloadMode(videoInfo.getVid()) != null){
            Log.e(TAG, "prepareDownload: is preparing. name = " + videoInfo.getName());
            return;
        }
        DownloadModel model = new DownloadModel();
        model.setFileName(videoInfo.getName());
        model.setUrl(videoInfo.getVid());
        model.setStatus(DownloadStatus.CONNECTING);
        addDownloadMode(model);
        final String vid = videoInfo.getVid();
        boolean result = mDbController.replaceTask(videoInfo);
        Log.e(TAG, "prepareDownload: insert db result = " + result);
        if (mAliyunVidSts != null) {
            mAliyunVidSts.setVid(vid);
            mAliyunDownloadManager.prepareDownloadMedia(mAliyunVidSts);
        } else {
            mVidStsHelper.getVidSts(new VidStsHelper.OnStsResultListener() {
                @Override
                public void onSuccess(String akid, String akSecret, String token) {
                    Log.e(TAG, "onSuccess() called with: akid = " + akid + ", akSecret = " + akSecret + ", token = " + token + "");
                    AliyunVidSts vidSts = new AliyunVidSts();
                    vidSts.setVid(vid);
                    vidSts.setAcId(akid);
                    vidSts.setAkSceret(akSecret);
                    vidSts.setSecurityToken(token);
                    mAliyunVidSts = vidSts;
                    mAliyunDownloadManager.prepareDownloadMedia(vidSts);
                }

                @Override
                public void onFail(int errno) {
                    Log.e(TAG, "onFail() called with: errno = " + errno + "");
                    mAliyunVidSts = null;
                    checkNetwork(errno);
                }
            });
        }
    }

    private void notifyItemChanged(DownloadStatus status, AliyunDownloadMediaInfo info) {
        if (info == null) {
            Log.e(TAG, "notifyItemChanged: mediaInfo = null.");
            return;
        }
        DownloadModel downloadMode = getDownloadMode(info.getVid());
        if (downloadMode != null){
            downloadMode.setMediaInfo(info);
            downloadMode.setStatus(status);
        }else {
            Log.e(TAG, "notifyItemChanged: downloadMode = null.");
            return;
        }
    }

    private void notifyDataSetChanged() {
        if (mAdapter == null) {
            return;
        }
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 主动修改状态，但不通知界面更新，由{@link #mDownloadListener}反馈通知界面更新。
     *
     * @param vid    adapter中的位置，也就是{@link #mDownloadModelVector 中的位置}
     * @param status 想要改变的状态
     */
    public void updateDownloadStatus(String vid, DownloadStatus status) {
        DownloadModel downloadModel = getDownloadMode(vid);
        if (downloadModel != null){
            downloadModel.setStatus(status);
        }
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

    private void stopDownload(String vid) {
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

    public void removeMediaInfo(AliyunDownloadMediaInfo mediaInfo) {
//        stopDownload(mediaInfo);

        if (isCompleteMediaInfo(mediaInfo)) {
            //该方法会删除之前下载的文件
            mAliyunDownloadManager.removeDownloadMedia(mediaInfo);
        }else {
            Log.d(TAG, "removeMediaInfo: not complete media info.");
        }
        mDbController.deleteTask(mediaInfo.getVid());
        removeDownloadMode(mediaInfo.getVid());
    }

    /**
     * 判断是否是完整的mediaInfo，不判断可能会导致AliyunDownloadManager.removeDownloadMedia()内部出错，
     * 或者该mediaInfo根本都没加入到AliyunDownloadManager中
     */
    private boolean isCompleteMediaInfo(AliyunDownloadMediaInfo mediaInfo){
        return !TextUtils.isEmpty(mediaInfo.getVid())
                && !TextUtils.isEmpty(mediaInfo.getQuality())
                && !TextUtils.isEmpty(mediaInfo.getFormat());
    }

    public void registerDownloadTaskObserver(BaseDownloadTaskObserver observer) {
        mTaskObservable.registerObserver(observer);
    }

    public void unregisterDownloadTaskObserver(BaseDownloadTaskObserver observer) {
        mTaskObservable.unregisterObserver(observer);
    }

    private boolean isDownloadingStatus(AliyunDownloadMediaInfo.Status status) {
        return status == AliyunDownloadMediaInfo.Status.Prepare
                || status == AliyunDownloadMediaInfo.Status.Start
                || status == AliyunDownloadMediaInfo.Status.Wait;
    }

//    private void logArray() {
//        logArray(mDownloadModelVector);
//    }

    private void logArray(List<DownloadModel> modelList) {
        int size = modelList.size();
        for (int i = 0; i < size; i++) {
            DownloadModel model = modelList.get(i);
            Log.e(TAG, "logArray: i = " + i + " : " + "id:" + model.getTaskId() + ",   " + model.getFileName());
        }
    }

    private void unregisterAllDownloadTaskObserver() {
        mTaskObservable.unregisterAll();
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
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                BaseDownloadTaskObserver observer = mObservers.get(i);
                if (observer.isContains(mediaInfo)) {
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

        protected Set<T> set = new HashSet<>();

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
            if (mediaInfo == null) {
                Log.e(TAG, "onPrepared: mediaInfo = null. why?");
                return;
            }
            DownloadModel model = getDownloadMode(mediaInfo.getVid());
            if (model == null) {
                //可能用户已经执行了删除操作等。
                return;
            }else {
                mAliyunDownloadManager.addDownloadMedia(mediaInfo);
                Log.d(TAG, "onPrepared: model status = " + model.getStatus());
                //开始到这里，可能用户已经暂停了。
                if (model.getStatus() != DownloadStatus.PAUSE) {
                    startDownload(mediaInfo);
                }
                model.setMediaInfo(mediaInfo);
            }
            mDownloadMap.put(mediaInfo.getVid(), model);
            boolean result = mDbController.updateTask(mediaInfo);
            Log.e(TAG, "onPrepared: result = " + result);
            notifyItemChanged(DownloadStatus.WAIT, mediaInfo);

        }

        @Override
        public void onStart(AliyunDownloadMediaInfo mediaInfo) {
            Log.e(TAG, "onStart: progress = " + mediaInfo.getProgress()
                    + ", size = " + mediaInfo.getSizeStr());
            notifyItemChanged(DownloadStatus.STARTED, mediaInfo);
            mTaskObservable.onTaskStarted(mediaInfo);
        }

        @Override
        public void onProgress(AliyunDownloadMediaInfo mediaInfo, int i) {
            Log.e(TAG, "onProgress: size = " + mediaInfo.getSize() + ", i = " + i);
            notifyItemChanged(DownloadStatus.DOWNLOADING, mediaInfo);
            long size = mediaInfo.getSize();
            mTaskObservable.onTaskProgress(mediaInfo, (int) (size * i * 0.01F), (int) size);
        }

        @Override
        public void onStop(AliyunDownloadMediaInfo mediaInfo) {
            Log.e(TAG, "onStop: ");
            notifyItemChanged(DownloadStatus.PAUSE, mediaInfo);
            mTaskObservable.onTaskPause(mediaInfo);
        }

        @Override
        public void onCompletion(AliyunDownloadMediaInfo mediaInfo) {
            Log.e(TAG, "onCompletion: oldPath = " + mediaInfo.getSavePath() + ", title = " + mediaInfo.getTitle());

            String oldPath = mediaInfo.getSavePath();
            String newPath = Constants.getVideoPath(mediaInfo.getTitle());
            FileUtils.renameTo(oldPath, newPath);

            //可以去掉吗？
            notifyItemChanged(DownloadStatus.COMPLETED, mediaInfo);
            removeMediaInfo(mediaInfo);
            mTaskObservable.onTaskCompleted(mediaInfo);

        }

        @Override
        public void onError(AliyunDownloadMediaInfo mediaInfo, int i, String s, String s1) {
            Log.e(TAG, "onError() called with: i = " + i + ", s = " + s + ", s1 = " + s1 + "");
            notifyItemChanged(DownloadStatus.ERROR, mediaInfo);
            mTaskObservable.onTaskError(mediaInfo, s);
            mAliyunVidSts = null;
            if (checkNetwork()) {
                ToastUtils.show("下载出错：" + mediaInfo.getTitle() + ":" + s);
            }
            //TODO 如果是租期过期，应自动重新获取，对用户不可见
        }

        @Override
        public void onWait(AliyunDownloadMediaInfo mediaInfo) {
            Log.e(TAG, "onWait: ");
            notifyItemChanged(DownloadStatus.WAIT, mediaInfo);
            mTaskObservable.onTaskWait(mediaInfo);
        }

        @Override
        public void onM3u8IndexUpdate(AliyunDownloadMediaInfo mediaInfo, int i) {
            Log.e(TAG, "onM3u8IndexUpdate: mediaInfo = " + mediaInfo.getTitle() + ", index = " + i);
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
