package com.readboy.aliyunplayerlib.view;

import android.content.Context;
import android.util.AttributeSet;

import com.readboy.aliyunplayerlib.R;

/**
 * 提供默认的播放器加载界面
 * 加载界面统一：
 * 1、一个ProgressBar
 * 2、一个TextView显示提示语
 * 3、一个TextView按钮在出错的情况下可以重新加载等操作
 * 4、单独全屏界面加一个TextView按钮做退出
 * Created by ldw on 2018/3/23.
 */

public class PlayerLoadStatusViewDefault extends PlayerLoadStatusViewBase {

    public PlayerLoadStatusViewDefault(Context context) {
        super(context);
    }

    public PlayerLoadStatusViewDefault(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PlayerLoadStatusViewDefault(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutXml() {
        return R.layout.layout_player_view_loading;
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected int getParentViewId() {
        //涉及到背景颜色设置，所以参考default的做法，多写一个id进来。必须
        return R.id.load_status_view_parent;
    }

    @Override
    protected int getProgressBarId() {
        //ProgressBar，加载动画统一用ProgressBar，不用则加载时没显示，不用返回0，建议必须
        return R.id.load_status_view_progressbar;
    }

    @Override
    protected int getMessageTextViewId() {
        //TextView，播放器会往这TextView设置加载和出错等的一些提示语，不用则看不到提示语，不用返回0，建议必须
        return R.id.load_status_view_text;
    }

    @Override
    protected int getContinueViewId() {
        //TextView，出错等原因，可以点击重新加载播放，不用则无法重新加载，不用返回0，建议必须
        return R.id.load_status_view_btn_continue;
    }

    @Override
    protected int getCancelViewId() {
        //TextView，这是取消和退出按钮，不用返回0，选用
        return R.id.load_status_view_btn_cancel;
    }

}
