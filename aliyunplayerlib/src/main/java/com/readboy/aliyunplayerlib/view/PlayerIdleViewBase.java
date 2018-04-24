package com.readboy.aliyunplayerlib.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

/**
 * 播放器闲置布局抽象类。
 * 自定义闲置布局必须继承该抽象类。
 * Created by ldw on 2018/4/19.
 */

public abstract class PlayerIdleViewBase extends RelativeLayout {

    protected abstract int getLayoutXml();
    protected abstract void initViews();

    public PlayerIdleViewBase(Context context) {
        super(context);
        init();
    }

    public PlayerIdleViewBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PlayerIdleViewBase(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        LayoutInflater.from(this.getContext()).inflate(getLayoutXml(), this, true);
        initViews();
    }

}
