package com.readboy.mathproblem.adapter;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliyun.vodplayer.downloader.AliyunDownloadMediaInfo;
import com.readboy.mathproblem.R;
import com.readboy.mathproblem.cache.PicassoWrapper;
import com.readboy.mathproblem.download.AliyunDownloadManagerWrapper;
import com.readboy.mathproblem.download.DownloadSpeedMonitor;
import com.readboy.mathproblem.http.download.DownloadModel;
import com.readboy.mathproblem.http.download.DownloadStatus;
import com.readboy.mathproblem.util.SizeUtils;

/**
 * Created by oubin on 2017/9/5.
 */

public class DownloadViewHolder extends CheckViewHolder<DownloadModel> implements View.OnClickListener {
    private static final String TAG = "DownloadViewHolder";

    private ImageView mVideoThumbnail;
    private TextView mVideoName;
    private TextView mDownloadStatusTv;
    /**
     * 20.3M/60.67M
     */
    private TextView mVideoMemory;
    private ImageView mDownloadStatusIv;
    private DownloadStatus mDownloadStatus = DownloadStatus.WAIT;
    private VidTaskObserver mObserver;
    private DownloadSpeedMonitor mSpeedMonitor;
    /**
     * 切记，该对象是和DownloadDialog#mDownloadArray里DownloadModel是同一个对象,
     * 修改对象内容，同步更新。
     */
    private DownloadModel mDownloadModel;

    public DownloadViewHolder(View itemView) {
        super(itemView);
        mCheckBox = (CheckBox) itemView.findViewById(R.id.video_select);
        mVideoThumbnail = (ImageView) itemView.findViewById(R.id.video_thumbnail);
        mVideoName = (TextView) itemView.findViewById(R.id.small_player_video_name);
        mDownloadStatusTv = (TextView) itemView.findViewById(R.id.video_download_status_tv);
        mVideoMemory = (TextView) itemView.findViewById(R.id.video_download_memory);
        mDownloadStatusIv = (ImageView) itemView.findViewById(R.id.video_download_status_iv);
        mDownloadStatusIv.setOnClickListener(this);
        mObserver = new VidTaskObserver();
//        DownloadManager.getInstance().registerDownloadTaskObserver(mObserver);
        AliyunDownloadManagerWrapper.getInstance().registerDownloadTaskObserver(mObserver);
        mSpeedMonitor = new DownloadSpeedMonitor();
    }

    @Override
    public void bindView(int position, boolean isChecked, DownloadModel model) {
        super.bindView(position, isChecked, model);
        this.mDownloadModel = model;
        mObserver.clearObserver();
        if (model.getMediaInfo() != null && !mObserver.isContains(model.getMediaInfo())) {
            mObserver.addObserver(model.getVid());
        }
        if (!TextUtils.isEmpty(model.getThumbnailUrl())) {
            PicassoWrapper.loadThumbnail(model.getThumbnailUrl(), mVideoThumbnail);
        } else {
            mVideoThumbnail.setBackgroundResource(R.drawable.video_thumbnail);
        }

        mCheckBox.setChecked(isChecked);
        mVideoName.setText(model.getFileName());
        mDownloadStatus = model.getStatus();
        mSpeedMonitor.start(model.getSoFar());
        updateDownloadStatusView();

        long soFar = model.getSoFar();
//        Log.e(TAG, "bindView: task so far = " + SizeUtils.formatMemorySize(soFar));
        long total = model.getTotal();
//        Log.e(TAG, "bindView: downloader so far = " + SizeUtils.formatMemorySize(soFar)
//                + ", total = " + SizeUtils.formatMemorySize(total));
        Log.e(TAG, "bindView: soFar = " + model.getSoFar() + ", total = " + total);
        mVideoMemory.setText(String.format("%s/%s",
                SizeUtils.formatMemorySize(soFar), SizeUtils.formatMemorySize(total)));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video_download_status_iv:
                handlerStatusClickEvent();
                break;
            default:
                Log.e(TAG, "onClick: default id = " + v.getId());
        }
    }

    private void handlerStatusClickEvent() {
        Log.e(TAG, "handlerStatusClickEvent: mDownloadStatus = " + mDownloadStatus);
        DownloadStatus nextStatus = mDownloadStatus;
        switch (mDownloadStatus) {
            case ERROR:
            case PAUSE:
//                nextStatus = DownloadStatus.DOWNLOADING;
                nextStatus = DownloadStatus.CONNECTING;
                break;
            case CONNECTING:
            case STARTED:
            case DOWNLOADING:
                nextStatus = DownloadStatus.PAUSE;
                break;
            case WAIT:
                nextStatus = DownloadStatus.PAUSE;
                break;
            case COMPLETED:
                nextStatus = DownloadStatus.COMPLETED;
                break;
            default:
                Log.e(TAG, "handlerStatusClickEvent: status = " + nextStatus);
                return;
        }
        AliyunDownloadManagerWrapper.getInstance().updateDownloadStatus(mDownloadModel.getVid(), nextStatus);
        updateDownloadStatus(nextStatus);
    }

    private void updateDownloadStatusView() {
        Log.e(TAG, "updateDownloadStatusView: downloadStatus = " + mDownloadStatus);
        mDownloadStatusIv.setImageResource(mDownloadStatus.getDrawableResId());
        if (mDownloadStatus == DownloadStatus.DOWNLOADING) {
//            mDownloadStatusTv.setText(String.format("%dKB/s", mSpeedMonitor.getSpeed()));
//            mDownloadStatusTv.setText(mDownloadStatus.getDescribe());
            updateSpeedView();
        } else {
            mDownloadStatusTv.setText(mDownloadStatus.getDescribe());
        }
    }

    private void updateSpeedView() {
        String speedStr;
        int speed = mSpeedMonitor.getSpeed();
        if (speed >= 1024) {
            speedStr = String.format("%.2fMB", speed / 1024.0F);
        } else {
            speedStr = String.format("%dKB", speed);
        }
        Log.e(TAG, "updateSpeedView: speed = " + speedStr);
        mDownloadStatusTv.setText(String.format("%s/s", speedStr));
    }

    private void updateDownloadStatus(DownloadStatus status) {
        Log.d(TAG, "updateDownloadStatus() called with: status = " + status + "");
        mDownloadStatus = status;
        mDownloadModel.setStatus(status);
        updateDownloadStatusView();
    }

    private class VidTaskObserver extends AliyunDownloadManagerWrapper.BaseDownloadTaskObserver<String> {

        @Override
        public boolean isContains(AliyunDownloadMediaInfo mediaInfo) {
            return set.contains(mediaInfo.getVid());
        }

        @Override
        public void onTaskStarted(AliyunDownloadMediaInfo task) {
            super.onTaskStarted(task);
            updateDownloadStatus(DownloadStatus.STARTED);
            long start = task.getSize() * task.getProgress();
            mSpeedMonitor.start(start);
        }

        @Override
        public void onTaskWait(AliyunDownloadMediaInfo info) {
            super.onTaskWait(info);
            updateDownloadStatus(DownloadStatus.WAIT);
        }

        @Override
        public void onTaskPause(AliyunDownloadMediaInfo task) {
            super.onTaskPause(task);
            Log.e(TAG, "onTaskPause: ");
            updateDownloadStatus(DownloadStatus.PAUSE);
        }

        @Override
        public void onTaskProgress(AliyunDownloadMediaInfo task, int soFarBytes, int totalBytes) {
            super.onTaskProgress(task, soFarBytes, totalBytes);
            Log.e(TAG, "onTaskProgress() called with: soFarBytes = " + soFarBytes + ", totalBytes = " + totalBytes + "");
            mSpeedMonitor.update(soFarBytes);
            updateSpeedView();
            mVideoMemory.setText(String.format("%s/%s",
                    SizeUtils.formatMemorySize(soFarBytes), SizeUtils.formatMemorySize(totalBytes)));

        }

        @Override
        public void onTaskCompleted(AliyunDownloadMediaInfo info) {
            super.onTaskCompleted(info);
            mSpeedMonitor.end(info.getSize());
            updateDownloadStatus(DownloadStatus.COMPLETED);
        }

        @Override
        public void onTaskError(AliyunDownloadMediaInfo info, String message) {
            super.onTaskError(info, message);
            updateDownloadStatus(DownloadStatus.ERROR);
        }
    }

}
