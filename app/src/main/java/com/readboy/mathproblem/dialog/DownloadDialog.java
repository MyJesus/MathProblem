package com.readboy.mathproblem.dialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.readboy.mathproblem.video.movie.MovieActivity;
import com.readboy.mathproblem.video.movie.VideoExtraNames;
import com.readboy.mathproblem.R;
import com.readboy.mathproblem.adapter.DownloadAdapter;
import com.readboy.mathproblem.adapter.VideoAdapter;
import com.readboy.mathproblem.application.Constants;
import com.readboy.mathproblem.http.download.DownloadManager;
import com.readboy.mathproblem.http.download.DownloadModel;
import com.readboy.mathproblem.util.FileUtils;
import com.readboy.mathproblem.util.SizeUtils;
import com.readboy.mathproblem.util.ToastUtils;
import com.readboy.mathproblem.widget.LineItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by oubin on 2017/9/5.
 * @author oubin
 */

public class DownloadDialog extends BaseVideoDialog {
    private static final String TAG = "DownloadDialog";

    /**
     * 已下载，本地视频
     */
    private RecyclerView mLocationRv;
    private VideoAdapter mLocationAdapter;
    private final List<VideoInfo> mLocationVideoList = new ArrayList<>();
    //正在下载中的视频
    private RecyclerView mDownloadRv;
    private DownloadAdapter mDownloadAdapter;
    /**
     * 和Adapter中的dataList使用同一对象，确保数据更新
     * 如果不是同一对象，每次更新数据，都需重新获取数据。
     */
    private List<DownloadModel> mDownloadArray;
//    private ArrayMap<Integer, DownloadModel> mDownloadArray;
//    private SparseArray<DownloadModel> mDownloadArray;

    //已下载
    private View mLocationBtnParent;
    private View mLocationBtn;
    //下载中
    private View mDownloadBtnParent;
    private View mDownloadBtn;

    private boolean shouldUpdateUi = false;
    private RecyclerView.AdapterDataObserver mDownloadDataObserver;
    private boolean hasRegisterObserver = false;

    private int mLastDownloadSize = 0;

    public DownloadDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate: ");
        setContentView(R.layout.dialog_video_download);
        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        initView();
        initData();
    }

    @Override
    public void show() {
        super.show();
//        Log.e(TAG, "show: ");
        DownloadManager.getInstance().setAdapter(mDownloadAdapter);
        if (!hasRegisterObserver) {
            mDownloadAdapter.registerAdapterDataObserver(mDownloadDataObserver);
            hasRegisterObserver = true;

        }
        if (mDownloadArray != null) {
            mLastDownloadSize = mDownloadArray.size();
        }
        mDownloadAdapter.notifyDataSetChanged();
        shouldUpdateUi = false;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        Log.e(TAG, "dismiss: ");
        mLocationAdapter.setAllChecked(false);
        mDownloadAdapter.setAllChecked(false);
        DownloadManager.getInstance().setAdapter(null);
        if (hasRegisterObserver) {
            mDownloadAdapter.unregisterAdapterDataObserver(mDownloadDataObserver);
            hasRegisterObserver = false;
        }
    }

    @Override
    protected void initView() {
        super.initView();
        findViewById(R.id.dialog_close).setOnClickListener(this);
        mLocationRv = (RecyclerView) findViewById(R.id.video_location_list);
        mDownloadRv = (RecyclerView) findViewById(R.id.video_download_list);
        mLocationBtnParent = findViewById(R.id.location_selected_parent);
        mLocationBtn = findViewById(R.id.location_btn_unselected);
        mLocationBtn.setOnClickListener(this);

        mDownloadBtnParent = findViewById(R.id.download_selected_parent);
        mDownloadBtn = findViewById(R.id.download_btn_unselected);
        mDownloadBtn.setOnClickListener(this);
        mLocationBtn.setSelected(true);
        mDownloadBtn.setSelected(false);
    }

    private void initData() {
        mLocationRv.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mLocationAdapter = new VideoAdapter(getContext());
        mLocationAdapter.setAllCheckedChangeListener(this);
        mLocationAdapter.setOnItemClickListener((position, viewHolder) -> {
            String path = mLocationVideoList.get(position).getPath();
//            VideoProxy.playWithPath(path, DownloadDialog.this.getContext());
            Intent intent = new Intent(DownloadDialog.this.getContext(), MovieActivity.class);
            intent.putExtra(VideoExtraNames.EXTRA_INDEX, position);
//            intent.putExtra(VideoExtraNames.EXTRA_PATH, path);
            ArrayList<String> paths = new ArrayList<>();
            for (VideoInfo info : mLocationVideoList) {
                paths.add(info.getPath());
            }
            intent.putExtra(VideoExtraNames.EXTRA_MEDIA_LIST, paths);
            getContext().startActivity(intent);
        });
        mLocationRv.addItemDecoration(new LineItemDecoration(LinearLayout.VERTICAL,
                SizeUtils.dp2px(getContext(), 1),
                ContextCompat.getColor(getContext(), R.color.video_divider_color)));
        mLocationRv.setAdapter(mLocationAdapter);
//        updateLocationListFaker();
        updateLocationVideoList();
        mLocationAdapter.setData(mLocationVideoList);
        updateCountView(mLocationVideoList.size());

        mDownloadRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mDownloadRv.addItemDecoration(new LineItemDecoration(LinearLayout.VERTICAL,
                SizeUtils.dp2px(getContext(), 1),
                ContextCompat.getColor(getContext(), R.color.video_divider_color)));
        mDownloadAdapter = new DownloadAdapter(getContext());
        mDownloadRv.setAdapter(mDownloadAdapter);
        mDownloadAdapter.setAllCheckedChangeListener(this);
        updateDownloadList();
        mDownloadAdapter.setData(mDownloadArray);
        mDownloadDataObserver = new DownloadDataObserver();
    }

    private void updateDownloadList() {
        mDownloadArray = DownloadManager.getInstance().getDownloadArray();
    }

    private void updateLocationVideoList() {
        mLocationVideoList.clear();
        File parent = new File(Constants.VIDEO_PATH);
        if (!parent.exists() && !parent.mkdirs()) {
            Log.e(TAG, "updateLocationVideoList: can't mkdirs, " + parent.getAbsolutePath());
            return;
        }
        File[] files = parent.listFiles(FileUtils::isVideo);
        if (files != null) {
            for (File file : files) {
                VideoInfo videoInfo = new VideoInfo(false, file.getAbsolutePath(),
                        FileUtils.getFileNameWithoutExtension(file.getName()), SizeUtils.formatMemorySize(file.length()));
                mLocationVideoList.add(videoInfo);
            }
        }
        notifyLocationDataChange();
    }

    private void updateLocationListFaker() {
        File parent = new File(Constants.VIDEO_PATH);
        if (!parent.exists()) {
            if (!parent.mkdirs()) {
                return;
            }
        }
        mLocationVideoList.clear();
        File[] files = parent.listFiles(FileUtils::isVideo);
        if (files != null) {
            for (File file : files) {
                int count = 20;
                for (int i = 0; i < count; i++) {
                    VideoInfo videoInfo = new VideoInfo(false, file.getAbsolutePath(),
                            file.getName() + i, SizeUtils.formatMemorySize(file.length()));
                    mLocationVideoList.add(videoInfo);
                }
            }
        }
//        mAllCheckedBox.setChecked(false);
    }

    private void updateCountView() {
        int count;
        if (mLocationBtn.isSelected()) {
            count = mLocationVideoList.size();
            mVideoCount.setText(getContext().getString(R.string.downloaded_video_count, count));
        } else {
            count = mDownloadArray.size();
//            Log.e(TAG, "updateCountView: count = " + count + ", mLastDownloadSize = " + mLastDownloadSize);
            if (count != mLastDownloadSize){
//                Log.e(TAG, "updateCountView: ");
                mAllCheckedBox.setChecked(false);
            }
            mLastDownloadSize = count;
            mVideoCount.setText(getContext().getString(R.string.downloading_video_count, count));
        }
        Log.e(TAG, "updateCountView: count = " + count);
        if (count == 0) {
            showEmptyContentView();
            mDeleteView.setVisibility(View.GONE);
            mAllCheckedBox.setVisibility(View.GONE);
        } else {
            mDeleteView.setVisibility(View.VISIBLE);
            mAllCheckedBox.setVisibility(View.VISIBLE);
            if (mLocationBtn.isSelected()) {
                showLocationList();
            } else {
                showDownloadList();
            }
        }
    }

    private void updateCountView(int count) {
        if (count == 0) {
            showEmptyContentView();
            mDeleteView.setVisibility(View.GONE);
            mAllCheckedBox.setVisibility(View.GONE);
        }else {
            mDeleteView.setVisibility(View.VISIBLE);
            mAllCheckedBox.setVisibility(View.VISIBLE);
        }
        if (mLocationBtn.isSelected()) {
            mVideoCount.setText(getContext().getString(R.string.downloaded_video_count, count));
        } else {
            mVideoCount.setText(getContext().getString(R.string.downloading_video_count, count));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.location_btn_unselected:
                mLocationBtnParent.setVisibility(View.VISIBLE);
                mDownloadBtnParent.setVisibility(View.GONE);
                mLocationBtn.setSelected(true);
                mDownloadBtn.setSelected(false);
                //TODO 效率低，应该监听下载目录，是否下载成功。
                updateLocationVideoList();
                showLocationList();
                updateCountView();
                mAllCheckedBox.setChecked(false);
                break;
            case R.id.download_btn_unselected:
                mLocationBtnParent.setVisibility(View.GONE);
                mDownloadBtnParent.setVisibility(View.VISIBLE);
                mDownloadBtn.setSelected(true);
                mLocationBtn.setSelected(false);
                unCheckAll();
                showDownloadList();
                updateCountView();
                mAllCheckedBox.setChecked(false);
                break;
            default:
                super.onClick(v);
        }
    }

    private void switchPanel(boolean isLocationPanel) {
        if (isLocationPanel) {
            mLocationBtnParent.setVisibility(View.VISIBLE);
            mDownloadBtnParent.setVisibility(View.GONE);
            mLocationBtn.setSelected(true);
            mDownloadBtn.setSelected(false);
            updateLocationVideoList();
            showLocationList();
        } else {
            mLocationBtnParent.setVisibility(View.GONE);
            mDownloadBtnParent.setVisibility(View.VISIBLE);
            mDownloadBtn.setSelected(true);
            mLocationBtn.setSelected(false);
            showDownloadList();
        }
        updateCountView();
        Log.e(TAG, "switchPanel: ");
        mAllCheckedBox.setChecked(false);
    }

    private void notifyLocationDataChange() {
        dismissDeleteAlertDialog();
        mLocationAdapter.notifyDataSetChanged();
        Log.e(TAG, "notifyLocationDataChange: ");
        mAllCheckedBox.setChecked(false);
    }

    private void notifyDownloadDataChange(){
        dismissDeleteAlertDialog();
        mDownloadAdapter.notifyDataSetChanged();
        mAllCheckedBox.setChecked(false);
    }

    @Override
    protected void checkAll() {
        if (mLocationBtn.isSelected()) {
            mLocationAdapter.setAllChecked(true);
        } else {
            mDownloadAdapter.setAllChecked(true);
        }
    }

    @Override
    protected void unCheckAll() {
        Log.e(TAG, "unCheckAll: ");
        if (mLocationBtn.isSelected()) {
            mLocationAdapter.setAllChecked(false);
        } else {
            mDownloadAdapter.setAllChecked(false);
        }

    }

    @Override
    protected boolean canShowDeleteDialog() {
        if (mLocationBtn.isSelected()) {
            return mLocationAdapter.hasChecked();
        } else {
            return mDownloadAdapter.hasChecked();
        }
    }

    @Override
    public boolean deleteVideo() {
        if (mLocationBtn.isSelected()) {
//            return deleteLocationVideoFaker();
            return deleteLocationVideo();
        } else {
            return deleteDownloadTask();
        }
    }

    @Override
    public void deleteVideoAfter(boolean isSuccess) {
        if (mLocationBtn.isSelected()) {
            notifyLocationDataChange();
        } else {
            mDownloadAdapter.notifyDataSetChanged();
        }
        updateCountView();
        shouldUpdateUi = true;
    }

    private boolean deleteLocationVideoFaker() {
        List<Integer> positions = mLocationAdapter.getSelectedPosition();
        for (int i = positions.size() - 1; i >= 0; i--) {
            mLocationVideoList.remove(i);
        }
        return true;
    }

    private boolean deleteLocationVideo() {
        List<Integer> positions = mLocationAdapter.getSelectedPosition();
        boolean result = true;
        int size = positions.size();
        for (int i = size - 1; i >= 0; i--) {
            int position = positions.get(i);
            String path = mLocationVideoList.get(position).getPath();
            if (!FileUtils.delete(path)) {
                result = false;
            } else {
                mLocationVideoList.remove(position);
            }
        }
        if (!result) {
            ToastUtils.show("部分文件删除失败！");
        }
        return result;
    }

    private boolean deleteDownloadTask() {
        List<Integer> positions = mDownloadAdapter.getSelectedPosition();
        int size = positions.size();
        for (int i = size - 1; i >= 0; i--) {
            int position = positions.get(i);
            DownloadManager.getInstance().deleteTaskByIndex(position);
        }
        return true;
    }

    @Override
    protected void showEmptyContentView() {
        super.showEmptyContentView();
        mLocationRv.setVisibility(View.GONE);
        mDownloadRv.setVisibility(View.GONE);
        dismissDeleteAlertDialog();
    }

    /**
     * 获取已下载视频
     */
    private void showLocationList() {
        mDownloadRv.setVisibility(View.GONE);
        if (mLocationRv.getVisibility() != View.VISIBLE) {
            mLocationRv.setVisibility(View.VISIBLE);
        }
        mEmptyContentTv.setVisibility(View.GONE);
    }

    private void showDownloadList() {
        if (mDownloadRv.getVisibility() != View.VISIBLE) {
            mDownloadRv.setVisibility(View.VISIBLE);
        }
        mLocationRv.setVisibility(View.GONE);
        mEmptyContentTv.setVisibility(View.GONE);
    }

    public boolean isShouldUpdateUI() {
        return shouldUpdateUi;
    }

    private int mLastDownloadCount=0;

    private class DownloadDataObserver extends RecyclerView.AdapterDataObserver {

        @Override
        public void onChanged() {
            super.onChanged();
//            Log.e(TAG, "onChanged: ");
            int count = mDownloadAdapter.getItemCount();
//            Log.e(TAG, "onChanged: count = " + count + ", mLastDownloadSize = " + mLastDownloadSize);
            if (count < mLastDownloadSize){
                updateLocationVideoList();
                mLastDownloadSize = count;
            }
            mLastDownloadSize = count;
//            updateLocationVideoList();
            updateCountView();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            Log.e(TAG, "onItemRangeRemoved: ");
//            updateLocationVideoList();
            updateCountView();
        }
    }

}
