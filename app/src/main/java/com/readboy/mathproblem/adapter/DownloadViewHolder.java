package com.readboy.mathproblem.adapter;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloader;
import com.readboy.mathproblem.R;
import com.readboy.mathproblem.cache.PicassoWrapper;
import com.readboy.mathproblem.http.download.DownloadManager;
import com.readboy.mathproblem.http.download.DownloadModel;
import com.readboy.mathproblem.http.download.DownloadStatus;
import com.readboy.mathproblem.util.FileUtils;
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
    private DownloadStatus mStatus;
    private TaskIdTaskObserver mObserver;
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
        mObserver = new TaskIdTaskObserver();
        DownloadManager.getInstance().registerDownloadTaskObserver(mObserver);
    }

    @Override
    public void bindView(int position, boolean isChecked, DownloadModel model) {
        super.bindView(position, isChecked, model);
        mObserver.clearObserver();
        if (model.getDownloadTask() != null && !mObserver.isContains(model.getDownloadTask())) {
            mObserver.addObserver(model.getTaskId());
        }
        if (!TextUtils.isEmpty(model.getThumbnailUrl())) {
            PicassoWrapper.loadThumbnail(model.getThumbnailUrl(), mVideoThumbnail);
        } else {
            mVideoThumbnail.setBackgroundResource(R.drawable.video_thumbnail);
        }

        mCheckBox.setChecked(isChecked);
        mVideoName.setText(FileUtils.getFileNameWithoutExtension(model.getFileName()));
        mStatus = model.getStatus();
        mDownloadStatusIv.setImageResource(mStatus.getDrawableResId());
        if (mStatus == DownloadStatus.DOWNLOADING) {
            mDownloadStatusTv.setText(String.format("%dKB/s", model.getSpeed()));
        } else {
            mDownloadStatusTv.setText(mStatus.getDescribe());
        }
        long soFar = model.getSoFar();
//        Log.e(TAG, "bindView: task so far = " + SizeUtils.formatMemorySize(soFar));
        soFar = FileDownloader.getImpl().getSoFar(model.getTaskId());
        long total = FileDownloader.getImpl().getTotal(model.getTaskId());
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
        Log.e(TAG, "handlerStatusClickEvent: mStatus = " + mStatus);
        DownloadStatus nextStatus = mStatus;
        switch (mStatus) {
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
        DownloadManager.getInstance().updateDownloadStatus(getAdapterPosition(), nextStatus);
    }

    public class TaskIdTaskObserver extends DownloadManager.BaseDownloadTaskObserver<Integer> {

        @Override
        public boolean isContains(BaseDownloadTask task) {
//            Log.e(TAG, "isContains: position = " + getAdapterPosition());
            return set.contains(task.getId());
        }

        @Override
        public void onTaskStarted(BaseDownloadTask task) {
            super.onTaskStarted(task);

        }

        @Override
        public void onTaskProgress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            super.onTaskProgress(task, soFarBytes, totalBytes);
//            Log.e(TAG, "onTaskProgress: so far = " + SizeUtils.formatMemorySize(soFarBytes)
//                    + ", total = " + SizeUtils.formatMemorySize(totalBytes));
            String speedStr;
            int speed = task.getSpeed();
            if (speed >= 1024) {
                speedStr = String.format("%.2fMB", speed / 1024.0F);
            } else {
                speedStr = String.format("%dKB", speed);
            }
            mDownloadStatusTv.setText(String.format("%s/s", speedStr));
            mVideoMemory.setText(String.format("%s/%s",
                    SizeUtils.formatMemorySize(soFarBytes), SizeUtils.formatMemorySize(totalBytes)));
        }
    }

}
