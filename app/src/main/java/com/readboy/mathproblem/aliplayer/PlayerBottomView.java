package com.readboy.mathproblem.aliplayer;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.readboy.aliyunplayerlib.view.PlayerBottomViewBase;
import com.readboy.mathproblem.R;
import com.readboy.mathproblem.util.FileUtils;
import com.readboy.mathproblem.util.ToastUtils;
import com.readboy.mathproblem.video.proxy.VideoProxy;
import com.readboy.mathproblem.video.resource.IVideoResource;

/**
 * Created by oubin on 2018/4/17.
 */

public class PlayerBottomView extends PlayerBottomViewBase implements View.OnClickListener {
    private static final String TAG = "PlayerBottomView";

    private View mFavoriteBtn;
    /**
     * selected = true代表已下载，或正在下载。
     */
    private View mDownloadBtn;

    private IVideoResource mVideoResource;

    private static final int[] VIEW_IDS = {R.id.fullscreen};

    public PlayerBottomView(Context context) {
        this(context, null);
    }

    public PlayerBottomView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayerBottomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    protected int getLayoutXml() {
        return R.layout.player_bottom_bar;
    }

    @Override
    protected int getPlayPauseBtnId() {
        return R.id.player_player;
    }

    @Override
    protected int getCurrentTextId() {
        return R.id.player_current_time;
    }

    @Override
    protected int getDurationTextId() {
        return R.id.player_total_time;
    }

    @Override
    protected int getSeekBarId() {
        return R.id.player_seek_bar;
    }

    @Override
    protected int getPlayBgId() {
        return R.drawable.btn_play_normal;
    }

    @Override
    protected int getPauseBgId() {
        return R.drawable.btn_pause_normal;
    }

    @Override
    protected void initViews() {
        mFavoriteBtn = findViewById(R.id.favorite);
        mFavoriteBtn.setOnClickListener(this);
        mDownloadBtn = findViewById(R.id.download);
        mDownloadBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
//        String mPath = mVideoResource.getVideoName();
        switch (v.getId()) {
            case R.id.favorite:
                Log.e(TAG, "onClick: favorite isChecked = " + mFavoriteBtn.isSelected());
                if (mFavoriteBtn.isSelected()) {
                    if (mVideoResource.unFavoriteVideo(getContext().getContentResolver())) {
                        mFavoriteBtn.setSelected(false);
                        ToastUtils.showShort(getContext(), "取消收藏成功");
                    } else {
                        ToastUtils.show(getContext(), "取消收藏失败！");
                    }
                } else {
                    if (mVideoResource.favoriteVideo(getContext().getContentResolver())) {
                        ToastUtils.showShort(getContext(), "收藏成功");
                        mFavoriteBtn.setSelected(true);
                    } else {
                        ToastUtils.showShort(getContext(), "收藏失败");
                    }
                }
                break;
            case R.id.download:
                Log.e(TAG, "onClick: download filename = " + FileUtils.getFileName(mVideoResource.getVideoName()));
                if (!mVideoResource.isDownloaded()) {
                    if (mDownloadBtn.isSelected() && mVideoResource.isDownloading()) {
                        ToastUtils.show(getContext(), "正在下载中");
                        return;
                    }
                    mVideoResource.downloadVideo();
                    ToastUtils.showShort(getContext(), "已添加到下载队列");
                    mDownloadBtn.setSelected(true);
                } else {
                    ToastUtils.show(getContext(), "已下载");
                }
                break;
            case R.id.fullscreen:
                handFullScreenClick();
                break;
            default:
                break;
        }
    }

    public void setVideoResource(IVideoResource resource) {
        this.mVideoResource = resource;
        updateFavoriteAndDownload();
    }

    public void setFavoriteVisibility(int visibility) {
        mFavoriteBtn.setVisibility(visibility);
    }

    public void setDownloadVisibility(int visibility) {
        mDownloadBtn.setVisibility(visibility);
    }

    public void setUnitClickListener(OnClickListener listener) {
        for (int viewId : VIEW_IDS) {
            findViewById(viewId).setOnClickListener(listener);
        }
    }

    private void handFullScreenClick() {

    }

    /**
     * TODO 下载状态的判断和收藏状态有待处理
     */
    private void updateFavoriteAndDownload() {
        if (mVideoResource == null) {
            return;
        }
        if (mVideoResource.isDownloading() || mVideoResource.isDownloaded()) {
            //代表是本地视频，或者正在下载中。
            mDownloadBtn.setSelected(true);
        } else {
            mDownloadBtn.setSelected(false);
        }
        if (mVideoResource.isFavorite(getContext().getContentResolver())) {
            mFavoriteBtn.setSelected(true);
        } else {
            mFavoriteBtn.setSelected(false);
        }
    }

}
