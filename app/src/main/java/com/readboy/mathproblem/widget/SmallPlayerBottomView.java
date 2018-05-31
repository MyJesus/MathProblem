package com.readboy.mathproblem.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.readboy.aliyunplayerlib.view.PlayerBottomViewBase;
import com.readboy.mathproblem.R;

/**
 * Created by oubin on 2018/5/14.
 */

public class SmallPlayerBottomView extends PlayerBottomViewBase {

    public SmallPlayerBottomView(Context context) {
        this(context, null);
    }

    public SmallPlayerBottomView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SmallPlayerBottomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    protected int getLayoutXml() {
        return R.layout.small_player_bottom;
    }

    @Override
    protected int getPlayPauseBtnId() {
        return R.id.small_player_controller;
    }

    @Override
    protected int getCurrentTextId() {
        return 0;
    }

    @Override
    protected int getDurationTextId() {
        return 0;
    }

    @Override
    protected int getSeekBarId() {
        return 0;
    }

    @Override
    protected int getPlayBgId() {
        return R.drawable.btn_player_play_white_selector;
    }

    @Override
    protected int getPauseBgId() {
        return R.drawable.btn_player_pause_white_selector;
    }

    @Override
    protected void initViews() {

    }
}
