package com.readboy.mathproblem.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.readboy.aliyunplayerlib.view.PlayerLoadStatusViewBase;
import com.readboy.mathproblem.R;

import javax.security.auth.login.LoginException;

/**
 * Created by oubin on 2018/5/14.
 */

public class SmallPlayerLoadingView extends PlayerLoadStatusViewBase {

    private static final String TAG = "SmallPlayerLoadingView";

    public SmallPlayerLoadingView(Context context) {
        this(context, null);
    }

    public SmallPlayerLoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SmallPlayerLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    protected int getLayoutXml() {
        return R.layout.video_small_loading;
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected int getParentViewId() {
        return R.id.video_progress_bar;
    }

    @Override
    protected int getProgressBarId() {
        return R.id.video_progress_bar;
    }

    @Override
    protected int getMessageTextViewId() {
        return 0;
    }

    @Override
    protected int getContinueViewId() {
        return 0;
    }

    @Override
    protected int getCancelViewId() {
        return 0;
    }
}
