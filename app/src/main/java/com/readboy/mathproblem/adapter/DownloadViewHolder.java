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
    private DownloadModel model;

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
        this.model = model;
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
        updateDownloadStatusView();

        long soFar = model.getSoFar();
//        Log.e(TAG, "bindView: task so far = " + SizeUtils.formatMemorySize(soFar));
        long total = model.getTotal();
//        Log.e(TAG, "bindView: downloader so far = " + SizeUtils.formatMemorySize(soFar)
//                + ", total = " + SizeUtils.formatMemorySize(total));
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
        AliyunDownloadManagerWrapper.getInstance().updateDownloadStatus(model.getVid(), nextStatus);
    }

    private void updateDownloadStatusView() {
        mDownloadStatusIv.setImageResource(mDownloadStatus.getDrawableResId());
        if (mDownloadStatus == DownloadStatus.DOWNLOADING) {
//            mDownloadStatusTv.setText(String.format("%dKB/s", mSpeedMonitor.getSpeed()));
            mDownloadStatusTv.setText(mDownloadStatus.getDescribe());
        } else {
            mDownloadStatusTv.setText(mDownloadStatus.getDescribe());
        }
    }

    public class VidTaskObserver extends AliyunDownloadManagerWrapper.BaseDownloadTaskObserver<String> {

        @Override
        public boolean isContains(AliyunDownloadMediaInfo mediaInfo) {
            return set.contains(mediaInfo.getVid());
        }

        @Override
        public void onTaskStarted(AliyunDownloadMediaInfo task) {
            super.onTaskStarted(task);
            mDownloadStatus = DownloadStatus.STARTED;
            updateDownloadStatusView();
            long start = task.getSize() * task.getProgress();
            mSpeedMonitor.start(start);
            mSpeedMonitor.end(task.getSize());
        }

        @Override
        public void onTaskWait(AliyunDownloadMediaInfo info) {
            super.onTaskWait(info);
            mDownloadStatus = DownloadStatus.WAIT;
            updateDownloadStatusView();
        }

        @Override
        public void onTaskPause(AliyunDownloadMediaInfo task) {
            super.onTaskPause(task);
            mDownloadStatus = DownloadStatus.PAUSE;
            updateDownloadStatusView();
        }

        @Override
        public void onTaskProgress(AliyunDownloadMediaInfo task, int soFarBytes, int totalBytes) {
            super.onTaskProgress(task, soFarBytes, totalBytes);
            Log.e(TAG, "onTaskProgress() called with: soFarBytes = " + soFarBytes + ", totalBytes = " + totalBytes + "");
            mSpeedMonitor.update(soFarBytes);
//            String speedStr;
//            int speed = 0;
//            if (speed >= 1024) {
//                speedStr = String.format("%.2fMB", speed / 1024.0F);
//            } else {
//                speedStr = String.format("%dKB", speed);
//            }
//            mDownloadStatusTv.setText(String.format("%s/s", speedStr));
            mVideoMemory.setText(String.format("%s/%s",
                    SizeUtils.formatMemorySize(soFarBytes), SizeUtils.formatMemorySize(totalBytes)));

        }

        @Override
        public void onTaskCompleted(AliyunDownloadMediaInfo info) {
            super.onTaskCompleted(info);
            mDownloadStatus = DownloadStatus.COMPLETED;
            updateDownloadStatusView();
        }
    }

}
