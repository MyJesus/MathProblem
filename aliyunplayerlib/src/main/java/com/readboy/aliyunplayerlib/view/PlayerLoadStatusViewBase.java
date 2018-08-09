package com.readboy.aliyunplayerlib.view;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.readboy.aliyunplayerlib.R;

/**
 * 播放器加载布局抽象类。
 * 自定义加载布局必须继承该抽象类。
 * Created by ldw on 2018/4/19.
 */

public abstract class PlayerLoadStatusViewBase extends FrameLayout implements View.OnClickListener {
    private static final String TAG = "PlayerLoadStatusViewBas";


    public static final int STATUS_IDLE = 0;//闲置状态
    public static final int STATUS_LOADING = 1;//加载中
    public static final int STATUS_ERROR_NO_VIDSTS = 2;//鉴权失败
    public static final int STATUS_ERROR_OTHER = 3;//错误
    public static final int STATUS_MOBILE_NET = 4;//当前流量网络
    public static final int STATUS_CONTINUE = 9;//继续播放
    public static final int STATUS_FINISH = 5;//结束
    public static final int STATUS_HIDE = 6;//隐藏
    public static final int STATUS_PREPROCESS = 7;//预处理，有些应用播放器需要获取vid才能进行播放
    public static final int STATUS_PREPROCESS_ERROR = 8;//预处理失败

    private int mCurStatus = -1;//默认-1初始化才能正常设置为IDLE状态

    private View mParentView;
    private ProgressBar mAnimView;
    private TextView mTextView;
    private TextView mContinueBtnView;
    private TextView mCancelBtnView;

    //闲置界面，用户可以在外部自己定义
    private View mIdleView = null;

    //结束界面，用户可以在外部自己定义
    private View mCompleteView = null;


    public PlayerLoadStatusViewBase(Context context) {
        super(context);
        init();
    }

    public PlayerLoadStatusViewBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PlayerLoadStatusViewBase(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        LayoutInflater.from(this.getContext()).inflate(getLayoutXml(), this, true);
        setClickable(true);
        mParentView = findViewById(getParentViewId());
        if(getProgressBarId() != 0) {
            mAnimView = findViewById(getProgressBarId());
        }
        if(getMessageTextViewId() != 0) {
            mTextView = findViewById(getMessageTextViewId());
        }
        if(getContinueViewId() != 0) {
            mContinueBtnView = findViewById(getContinueViewId());
        }
        if(getCancelViewId() != 0) {
            mCancelBtnView = findViewById(getCancelViewId());
        }

        if(mContinueBtnView != null) {
            mContinueBtnView.setOnClickListener(this);
        }
        if(mCancelBtnView != null){
            mCancelBtnView.setOnClickListener(this);
        }
        initViews();
    }

    protected abstract int getLayoutXml();
    protected abstract void initViews();

    protected abstract int getParentViewId();
    protected abstract int getProgressBarId();
    protected abstract int getMessageTextViewId();
    protected abstract int getContinueViewId();
    protected abstract int getCancelViewId();

    public void setIdleView(View idleView){
        if(idleView != null) {
            mIdleView = idleView;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            addView(mIdleView, params);
        }
    }

    public View getIdleView(){
        return mIdleView;
    }

    public void setCompleteView(View completeView){
        if(completeView != null) {
            mCompleteView = completeView;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            addView(mCompleteView, params);
        }
    }

    public View getCompleteView(){
        return mCompleteView;
    }

    public void setIdle(){
        if(mCurStatus == STATUS_IDLE){
            return;
        }
        mCurStatus = STATUS_IDLE;
        setBgBlack(true);
        setVisibility(View.VISIBLE);
        if(mIdleView != null){
            mIdleView.setVisibility(VISIBLE);
            if(mAnimView != null) {
                mAnimView.setVisibility(GONE);
            }
            if(mTextView != null) {
                mTextView.setVisibility(GONE);
            }
            if(mContinueBtnView != null) {
                mContinueBtnView.setVisibility(GONE);
            }
            if(mCancelBtnView != null) {
                mCancelBtnView.setVisibility(GONE);
            }
            if(mCompleteView != null) {
                mCompleteView.setVisibility(GONE);
            }
        }else{
            if(mAnimView != null) {
                mAnimView.setVisibility(GONE);
            }
            if(mTextView != null) {
                mTextView.setVisibility(GONE);
            }
            if(mContinueBtnView != null) {
                mContinueBtnView.setVisibility(VISIBLE);
                mContinueBtnView.setText(getResources().getString(R.string.player_load_status_view_btn_play));
            }
            if(mCancelBtnView != null) {
                mCancelBtnView.setVisibility(GONE);
            }
            if(mCompleteView != null) {
                mCompleteView.setVisibility(GONE);
            }
        }
    }

    public void setLoading(){
        if(mCurStatus == STATUS_LOADING){
            return;
        }
        mCurStatus = STATUS_LOADING;
        setVisibility(View.VISIBLE);
        if(mAnimView != null) {
            mAnimView.setVisibility(VISIBLE);
        }
        if(mTextView != null) {
            mTextView.setVisibility(VISIBLE);
            mTextView.setEnabled(false);
            mTextView.setText(getResources().getString(R.string.player_load_status_view_text_loading));
        }
        if(mContinueBtnView != null) {
            mContinueBtnView.setVisibility(GONE);
        }
        if(mCancelBtnView != null) {
            mCancelBtnView.setVisibility(VISIBLE);
        }
        if(mCompleteView != null){
            mCompleteView.setVisibility(GONE);
        }
        if(mIdleView != null) {
            mIdleView.setVisibility(GONE);
        }
    }

    public void setErrorNoVidsts(String msg){
        if(mCurStatus == STATUS_ERROR_NO_VIDSTS){
            return;
        }
        mCurStatus = STATUS_ERROR_NO_VIDSTS;
        setVisibility(View.VISIBLE);
        if(mAnimView != null) {
            mAnimView.setVisibility(GONE);
        }
        if(mTextView != null) {
            mTextView.setVisibility(VISIBLE);
            if(!TextUtils.isEmpty(msg)){
                mTextView.setText(msg);
            }else {
                mTextView.setText(getResources().getString(R.string.player_load_status_view_text_error));
            }
        }
        if(mContinueBtnView != null) {
            mContinueBtnView.setVisibility(VISIBLE);
            mContinueBtnView.setText(getResources().getString(R.string.player_load_status_view_btn_load));
        }
        if(mCancelBtnView != null) {
            mCancelBtnView.setVisibility(VISIBLE);
        }
        if(mCompleteView != null){
            mCompleteView.setVisibility(GONE);
        }
        if(mIdleView != null) {
            mIdleView.setVisibility(GONE);
        }
    }

    public void setErrorOther(String errorMsg){
        if(mCurStatus == STATUS_ERROR_OTHER){
            return;
        }
        mCurStatus = STATUS_ERROR_OTHER;
        setVisibility(View.VISIBLE);
        if(mAnimView != null) {
            mAnimView.setVisibility(GONE);
        }
        if(mTextView != null) {
            mTextView.setVisibility(VISIBLE);
            if(!TextUtils.isEmpty(errorMsg)){
                mTextView.setText(errorMsg);
            }else {
                mTextView.setText(getResources().getString(R.string.player_load_status_view_text_error));
            }
        }
        if(mContinueBtnView != null) {
            mContinueBtnView.setVisibility(VISIBLE);
            mContinueBtnView.setText(getResources().getString(R.string.player_load_status_view_btn_load));
        }
        if(mCancelBtnView != null) {
            mCancelBtnView.setVisibility(VISIBLE);
        }
        if(mCompleteView != null){
            mCompleteView.setVisibility(GONE);
        }
        if(mIdleView != null) {
            mIdleView.setVisibility(GONE);
        }
    }

    public void setMobileNet(){
        if(mCurStatus == STATUS_MOBILE_NET){
            return;
        }
        mCurStatus = STATUS_MOBILE_NET;
        setVisibility(View.VISIBLE);
        if(mAnimView != null) {
            mAnimView.setVisibility(GONE);
        }
        if(mTextView != null) {
            mTextView.setVisibility(VISIBLE);
            mTextView.setText(getResources().getString(R.string.player_load_status_view_text_mobile_net));
        }
        if(mContinueBtnView != null) {
            mContinueBtnView.setVisibility(VISIBLE);
            mContinueBtnView.setText(getResources().getString(R.string.player_load_status_view_btn_continue));
        }
        if(mCancelBtnView != null) {
            mCancelBtnView.setVisibility(VISIBLE);
        }
        if(mCompleteView != null){
            mCompleteView.setVisibility(GONE);
        }
        if(mIdleView != null) {
            mIdleView.setVisibility(GONE);
        }
    }

    public void setContinue(){
        if(mCurStatus == STATUS_CONTINUE){
            return;
        }
        mCurStatus = STATUS_CONTINUE;
        setVisibility(View.VISIBLE);
        if(mAnimView != null) {
            mAnimView.setVisibility(GONE);
        }
        if(mTextView != null) {
            mTextView.setVisibility(VISIBLE);
            mTextView.setText(getResources().getString(R.string.player_load_status_view_text_continue));
        }
        if(mContinueBtnView != null) {
            mContinueBtnView.setVisibility(VISIBLE);
            mContinueBtnView.setText(getResources().getString(R.string.player_load_status_view_btn_continue));
        }
        if(mCancelBtnView != null) {
            mCancelBtnView.setVisibility(VISIBLE);
        }
        if(mCompleteView != null){
            mCompleteView.setVisibility(GONE);
        }
        if(mIdleView != null) {
            mIdleView.setVisibility(GONE);
        }
    }

    public void setFinish(){
        if(mCurStatus == STATUS_FINISH){
            return;
        }
        mCurStatus = STATUS_FINISH;
        setVisibility(View.VISIBLE);
        if(mCompleteView != null){
            mCompleteView.setVisibility(VISIBLE);
            if(mAnimView != null) {
                mAnimView.setVisibility(GONE);
            }
            if(mTextView != null) {
                mTextView.setVisibility(GONE);
            }
            if(mContinueBtnView != null) {
                mContinueBtnView.setVisibility(GONE);
            }
            if(mCancelBtnView != null) {
                mCancelBtnView.setVisibility(GONE);
            }
            if(mIdleView != null) {
                mIdleView.setVisibility(GONE);
            }
        }else {
            mAnimView.setVisibility(GONE);
            if(mTextView != null) {
                mTextView.setVisibility(VISIBLE);
                mTextView.setText(getResources().getString(R.string.player_load_status_view_text_finish));
            }
            if(mContinueBtnView != null) {
                mContinueBtnView.setVisibility(VISIBLE);
                mContinueBtnView.setText(getResources().getString(R.string.player_load_status_view_btn_replay));
            }
            if(mCancelBtnView != null) {
                mCancelBtnView.setVisibility(VISIBLE);
            }
            if(mIdleView != null) {
                mIdleView.setVisibility(GONE);
            }
        }
    }

    public void setHide(){
        if(mCurStatus == STATUS_HIDE){
            return;
        }
        mCurStatus = STATUS_HIDE;
        setVisibility(View.GONE);
    }

    public void setPreprocess(){
        if(mCurStatus == STATUS_PREPROCESS){
            return;
        }
        mCurStatus = STATUS_PREPROCESS;
        setVisibility(View.VISIBLE);
        if(mAnimView != null) {
            mAnimView.setVisibility(VISIBLE);
        }
        if(mTextView != null) {
            mTextView.setVisibility(VISIBLE);
            mTextView.setEnabled(false);
            mTextView.setText(getResources().getString(R.string.player_load_status_view_text_loading));
        }
        if(mContinueBtnView != null) {
            mContinueBtnView.setVisibility(GONE);
        }
        if(mCancelBtnView != null) {
            mCancelBtnView.setVisibility(VISIBLE);
        }
        if(mCompleteView != null){
            mCompleteView.setVisibility(GONE);
        }
        if(mIdleView != null) {
            mIdleView.setVisibility(GONE);
        }
    }

    public void setPreprocessError(String errorMsg){
        if(mCurStatus == STATUS_PREPROCESS_ERROR){
            return;
        }
        mCurStatus = STATUS_PREPROCESS_ERROR;
        setVisibility(View.VISIBLE);
        if(mAnimView != null) {
            mAnimView.setVisibility(GONE);
        }
        if(mTextView != null) {
            mTextView.setVisibility(VISIBLE);
            if(!TextUtils.isEmpty(errorMsg)){
                mTextView.setText(errorMsg);
            }else {
                mTextView.setText(getResources().getString(R.string.player_load_status_view_text_error));
            }
        }
        if(mContinueBtnView != null) {
            mContinueBtnView.setVisibility(VISIBLE);
            mContinueBtnView.setText(getResources().getString(R.string.player_load_status_view_btn_load));
        }
        if(mCancelBtnView != null) {
            mCancelBtnView.setVisibility(VISIBLE);
        }
        if(mCompleteView != null){
            mCompleteView.setVisibility(GONE);
        }
        if(mIdleView != null) {
            mIdleView.setVisibility(GONE);
        }
    }

    /**
     * 设置背景颜色
     * @param black true 不透明黑色，false 半透明
     */
    public void setBgBlack(boolean black){
        if(black){
            mParentView.setBackgroundColor(0xff000000);
        }else{
            mParentView.setBackgroundColor(0x3f000000);
        }
    }

    public int getStatus(){
        return mCurStatus;
    }


    @Override
    public void onClick(View v) {
        if (getVisibility() != View.VISIBLE){
            Log.w(TAG, "onClick: view not visible.");
            return;
        }
        if(v == mContinueBtnView){
            if(mOnBtnClickListener != null){
                mOnBtnClickListener.onContinueBtnClick();
            }
        }else if(v == mCancelBtnView){
            if(mOnBtnClickListener != null){
                mOnBtnClickListener.onCancelBtnClick();
            }
        }
    }

    /**
     * 设置点击刷新监听
     * @param listener listener
     */
    public void setOnBtnClickListener(PlayerLoadStatusViewBase.OnBtnClickListener listener) {
        mOnBtnClickListener = listener;
    }

    /**
     * 打开设置
     */
    private void openSettings(){
        Intent intent = new Intent();
        intent.setAction(android.provider.Settings.ACTION_SETTINGS);
        getContext().startActivity(intent);
    }

    private PlayerLoadStatusViewBase.OnBtnClickListener mOnBtnClickListener = null;

    public interface OnBtnClickListener {
        void onContinueBtnClick();
        void onCancelBtnClick();
    }

}
