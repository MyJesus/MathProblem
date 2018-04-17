package com.readboy.aliyunplayerlib.view;

import android.content.Context;
import android.util.AttributeSet;

import com.readboy.aliyunplayerlib.R;

/**
 * 提供默认的播放器底部布局bottomView
 * Created by ldw on 2018/3/23.
 */

public class PlayerBottomViewDefault extends PlayerBottomViewBase {


    public PlayerBottomViewDefault(Context context) {
        super(context);
    }

    public PlayerBottomViewDefault(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PlayerBottomViewDefault(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutXml() {
        return R.layout.layout_player_view_bottom;
    }

    @Override
    protected int getPlayPauseBtnId() {
        return R.id.video_player_play_pause;
    }

    @Override
    protected int getCurrentTextId() {
        return R.id.video_player_progress_current_text;
    }

    @Override
    protected int getDurationTextId() {
        return R.id.video_player_progress_duration_text;
    }

    @Override
    protected int getSeekBarId() {
        return R.id.video_player_seekbar;
    }

    @Override
    protected int getPlayBgId() {
        return R.drawable.player_btn_play_selector;
    }

    @Override
    protected int getPauseBgId() {
        return R.drawable.player_btn_pause_selector;
    }

    @Override
    protected void initViews() {

    }

}
