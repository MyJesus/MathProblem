package com.readboy.aliyunplayerlib.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.readboy.aliyunplayerlib.utils.TimeUtil;

/**
 * 播放器底部布局抽象类。
 * 自定义底部布局必须继承该抽象类。
 * Created by ldw on 2018/3/23.
 */

public abstract class PlayerBottomViewBase extends RelativeLayout {

    protected View mPlayPauseView;
    protected TextView mCurrentTextView;
    protected TextView mDurationTextView;
    protected SeekBar mSeekBar;


    protected abstract int getLayoutXml();
    protected abstract int getPlayPauseBtnId();
    protected abstract int getCurrentTextId();
    protected abstract int getDurationTextId();
    protected abstract int getSeekBarId();
    protected abstract int getPlayBgId();
    protected abstract int getPauseBgId();
    protected abstract void initViews();

    public PlayerBottomViewBase(Context context) {
        super(context);
        init();
    }

    public PlayerBottomViewBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PlayerBottomViewBase(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        LayoutInflater.from(this.getContext()).inflate(getLayoutXml(), this, true);

        mPlayPauseView = findViewById(getPlayPauseBtnId());
        mCurrentTextView = findViewById(getCurrentTextId());
        mDurationTextView = findViewById(getDurationTextId());
        mSeekBar = findViewById(getSeekBarId());
        initViews();
    }

    public void setPlayPauseStatus(boolean isPlaying){
        if(isPlaying){
            if(mPlayPauseView != null) {
                mPlayPauseView.setBackgroundResource(getPauseBgId());
            }
        }else{
            if(mPlayPauseView != null) {
                mPlayPauseView.setBackgroundResource(getPlayBgId());
            }
        }
    }

    public void setSeekBarMax(int max){
        if(mSeekBar != null){
            mSeekBar.setMax(max);
        }
    }

    public void setSeekBarProgress(int progress){
        if(mSeekBar != null){
            mSeekBar.setProgress(progress);
        }
    }

    public void setSeekBarSecondaryProgress(int secondaryProgress){
        if(mSeekBar != null){
            mSeekBar.setSecondaryProgress(secondaryProgress);
        }
    }

    public void setCurrentText(long current){
        if(mCurrentTextView != null){
            mCurrentTextView.setText(TimeUtil.formatTime(current));
        }
    }

    public void setDurationText(long duration){
        if(mDurationTextView != null){
            mDurationTextView.setText(TimeUtil.formatTime(duration));
        }
    }

}
