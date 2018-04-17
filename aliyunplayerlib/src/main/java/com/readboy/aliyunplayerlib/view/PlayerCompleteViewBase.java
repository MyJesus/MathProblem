package com.readboy.aliyunplayerlib.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

/**
 * 播放器结束布局抽象类。
 * 自定义结束布局必须继承该抽象类。
 * Created by ldw on 2018/3/23.
 */

public abstract class PlayerCompleteViewBase extends RelativeLayout {

    protected abstract int getLayoutXml();
    protected abstract void initViews();

    public PlayerCompleteViewBase(Context context) {
        super(context);
        init();
    }

    public PlayerCompleteViewBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PlayerCompleteViewBase(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        LayoutInflater.from(this.getContext()).inflate(getLayoutXml(), this, true);
        initViews();
    }

}
