package com.readboy.aliyunplayerlib.view;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.readboy.aliyunplayerlib.R;


/**
 * LoadStatus布局
 * Created by ldw on 2017/12/7.
 */

public class PlayerLoadStatusView extends FrameLayout implements View.OnClickListener {

    public static final int STATUS_LOADING = 1;//加载中
    public static final int STATUS_ERROR_NO_VIDSTS = 2;//鉴权失败
    public static final int STATUS_ERROR_OTHER = 3;//错误
    public static final int STATUS_MOBILE_NET = 4;//当前流量网络
    public static final int STATUS_FINISH = 5;//结束
    public static final int STATUS_HIDE = 6;//隐藏
    public static final int STATUS_PREPROCESS = 7;//预处理，有些应用播放器需要获取vid才能进行播放
    public static final int STATUS_PREPROCESS_ERROR = 8;//预处理失败

    private int mCurStatus = 0;

    private View mParentView;
    private ProgressBar mAnimView;
    private TextView mTextView;
    private TextView mContinueBtnView;
    private TextView mCancelBtnView;

    //结束界面，用户可以在外部自己定义
    private View mCompleteView = null;


    public PlayerLoadStatusView(Context context) {
        this(context, null);
    }

    public PlayerLoadStatusView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayerLoadStatusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(this.getContext()).inflate(R.layout.layout_player_view_loading, this, true);
        setClickable(true);
        mParentView = findViewById(R.id.load_status_view_parent);
        mAnimView = findViewById(R.id.load_status_view_progressbar);
        mTextView = findViewById(R.id.load_status_view_text);
        mContinueBtnView = findViewById(R.id.load_status_view_btn_continue);
        mCancelBtnView = findViewById(R.id.load_status_view_btn_cancel);
        mContinueBtnView.setOnClickListener(this);
        mCancelBtnView.setOnClickListener(this);
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

    public void setLoading(){
        if(mCurStatus == STATUS_LOADING){
            return;
        }
        mCurStatus = STATUS_LOADING;
        setVisibility(View.VISIBLE);
        mAnimView.setVisibility(VISIBLE);
        mTextView.setVisibility(VISIBLE);
        mContinueBtnView.setVisibility(GONE);
        mCancelBtnView.setVisibility(VISIBLE);
        if(mCompleteView != null){
            mCompleteView.setVisibility(GONE);
        }
        mTextView.setEnabled(false);
        mTextView.setText(getResources().getString(R.string.player_load_status_view_text_loading));
    }

    public void setErrorNoVidsts(String msg){
        if(mCurStatus == STATUS_ERROR_NO_VIDSTS){
            return;
        }
        mCurStatus = STATUS_ERROR_NO_VIDSTS;
        setVisibility(View.VISIBLE);
        mAnimView.setVisibility(GONE);
        mTextView.setVisibility(VISIBLE);
        mContinueBtnView.setVisibility(VISIBLE);
        mCancelBtnView.setVisibility(VISIBLE);
        if(mCompleteView != null){
            mCompleteView.setVisibility(GONE);
        }
        if(!TextUtils.isEmpty(msg)){
            mTextView.setText(msg);
        }else {
            mTextView.setText(getResources().getString(R.string.player_load_status_view_text_error));
        }
        mContinueBtnView.setText(getResources().getString(R.string.player_load_status_view_btn_load));
    }

    public void setErrorOther(String errorMsg){
        if(mCurStatus == STATUS_ERROR_OTHER){
            return;
        }
        mCurStatus = STATUS_ERROR_OTHER;
        setVisibility(View.VISIBLE);
        mAnimView.setVisibility(GONE);
        mTextView.setVisibility(VISIBLE);
        mContinueBtnView.setVisibility(VISIBLE);
        mCancelBtnView.setVisibility(VISIBLE);
        if(mCompleteView != null){
            mCompleteView.setVisibility(GONE);
        }
        if(!TextUtils.isEmpty(errorMsg)){
            mTextView.setText(errorMsg);
        }else {
            mTextView.setText(getResources().getString(R.string.player_load_status_view_text_error));
        }
        mContinueBtnView.setText(getResources().getString(R.string.player_load_status_view_btn_load));
    }

    public void setMobileNet(){
        if(mCurStatus == STATUS_MOBILE_NET){
            return;
        }
        mCurStatus = STATUS_MOBILE_NET;
        setVisibility(View.VISIBLE);
        mAnimView.setVisibility(GONE);
        mTextView.setVisibility(VISIBLE);
        mContinueBtnView.setVisibility(VISIBLE);
        mCancelBtnView.setVisibility(VISIBLE);
        if(mCompleteView != null){
            mCompleteView.setVisibility(GONE);
        }
        mTextView.setText(getResources().getString(R.string.player_load_status_view_text_mobile_net));
        mContinueBtnView.setText(getResources().getString(R.string.player_load_status_view_btn_load));
    }

    public void setFinish(){
        if(mCurStatus == STATUS_FINISH){
            return;
        }
        mCurStatus = STATUS_FINISH;
        setVisibility(View.VISIBLE);
        if(mCompleteView != null){
            mCompleteView.setVisibility(VISIBLE);
            mAnimView.setVisibility(GONE);
            mTextView.setVisibility(GONE);
            mContinueBtnView.setVisibility(GONE);
            mCancelBtnView.setVisibility(GONE);
        }else {
            mAnimView.setVisibility(GONE);
            mTextView.setVisibility(VISIBLE);
            mContinueBtnView.setVisibility(VISIBLE);
            mCancelBtnView.setVisibility(VISIBLE);
            mTextView.setText(getResources().getString(R.string.player_load_status_view_text_finish));
            mContinueBtnView.setText(getResources().getString(R.string.player_load_status_view_btn_replay));
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
        mAnimView.setVisibility(VISIBLE);
        mTextView.setVisibility(VISIBLE);
        mContinueBtnView.setVisibility(GONE);
        mCancelBtnView.setVisibility(VISIBLE);
        if(mCompleteView != null){
            mCompleteView.setVisibility(GONE);
        }
        mTextView.setEnabled(false);
        mTextView.setText(getResources().getString(R.string.player_load_status_view_text_loading));
    }
    
    public void setPreprocessError(String errorMsg){
        if(mCurStatus == STATUS_PREPROCESS_ERROR){
            return;
        }
        mCurStatus = STATUS_PREPROCESS_ERROR;
        setVisibility(View.VISIBLE);
        mAnimView.setVisibility(GONE);
        mTextView.setVisibility(VISIBLE);
        mContinueBtnView.setVisibility(VISIBLE);
        mCancelBtnView.setVisibility(VISIBLE);
        if(mCompleteView != null){
            mCompleteView.setVisibility(GONE);
        }
        if(!TextUtils.isEmpty(errorMsg)){
            mTextView.setText(errorMsg);
        }else {
            mTextView.setText(getResources().getString(R.string.player_load_status_view_text_error));
        }
        mContinueBtnView.setText(getResources().getString(R.string.player_load_status_view_btn_load));
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
    public void setOnBtnClickListener(OnBtnClickListener listener) {
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

    private OnBtnClickListener mOnBtnClickListener = null;

    public interface OnBtnClickListener {
        void onContinueBtnClick();
        void onCancelBtnClick();
    }
}
