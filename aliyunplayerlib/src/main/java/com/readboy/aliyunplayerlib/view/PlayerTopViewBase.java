package com.readboy.aliyunplayerlib.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

/**
 * 播放器顶部布局抽象类。
 * 自定义顶部布局必须继承该抽象类。
 * Created by ldw on 2018/3/23.
 */

public abstract class PlayerTopViewBase extends RelativeLayout {


    public PlayerTopViewBase(Context context) {
        super(context);
        init();
    }

    public PlayerTopViewBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PlayerTopViewBase(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        LayoutInflater.from(this.getContext()).inflate(getLayoutXml(), this, true);
        initViews();
    }

    protected abstract int getLayoutXml();

    protected abstract void initViews();

}
