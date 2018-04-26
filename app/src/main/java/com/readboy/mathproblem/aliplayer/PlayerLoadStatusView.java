package com.readboy.mathproblem.aliplayer;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.readboy.aliyunplayerlib.view.PlayerLoadStatusViewDefault;

/**
 * Created by oubin on 2018/4/26.
 */

public class PlayerLoadStatusView extends PlayerLoadStatusViewDefault {
    private static final String TAG = "PlayerLoadStatusView";


    public PlayerLoadStatusView(Context context) {
        this(context, null);
    }

    public PlayerLoadStatusView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayerLoadStatusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    public void setFinish() {
//        super.setFinish();
        Log.e(TAG, "setFinish: ");
        fireOnCompleted();
    }

    @Override
    public void setHide() {
        super.setHide();
    }

    @Override
    public void setPreprocess() {
        super.setPreprocess();
    }

    @Override
    public void setPreprocessError(String errorMsg) {
        super.setPreprocessError(errorMsg);
    }

    @Override
    public void setBgBlack(boolean black) {
        super.setBgBlack(black);
    }

    @Override
    public void setOnBtnClickListener(OnBtnClickListener listener) {
        super.setOnBtnClickListener(listener);
    }

    public void showFinishView(){
        super.setFinish();
    }

    public void setUnitOnClickListener(View.OnClickListener listener){
        int[] resIds = {getContinueViewId()};
        for (int resId : resIds) {
            if (resId != 0){
                findViewById(resId).setOnClickListener(listener);
            }
        }
    }

    private OnCompletedListener completedListener;

    public void setOnCompletedListener(OnCompletedListener listener) {
        this.completedListener = listener;
    }

    private void fireOnCompleted() {
        if (completedListener != null) {
            completedListener.onCompleted();
        }
    }

    public interface OnCompletedListener {
        void onCompleted();
    }


    public interface OnLoadingListener{
        void onLoading();
    }


}
