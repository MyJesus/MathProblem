package com.readboy.mathproblem.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.readboy.aliyunplayerlib.view.PlayerTopViewBase;
import com.readboy.mathproblem.R;

/**
 * Created by oubin on 2018/5/14.
 */

public class EmptyPlayerTopView extends PlayerTopViewBase {

    public EmptyPlayerTopView(Context context) {
        this(context, null);
    }

    public EmptyPlayerTopView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmptyPlayerTopView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    protected int getLayoutXml() {
        return R.layout.video_small_top;
    }

    @Override
    protected void initViews() {

    }


}
