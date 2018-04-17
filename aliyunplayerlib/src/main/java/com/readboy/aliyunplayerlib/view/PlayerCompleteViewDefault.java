package com.readboy.aliyunplayerlib.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.readboy.aliyunplayerlib.R;

/**
 * 提供默认的播放结束界面completeView
 * Created by ldw on 2018/3/23.
 */

public class PlayerCompleteViewDefault extends PlayerCompleteViewBase {

    private TextView mBackView;
    private TextView mTextView;
    private TextView mContinueView;
    private TextView mCancelView;


    public PlayerCompleteViewDefault(Context context) {
        super(context);
    }

    public PlayerCompleteViewDefault(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PlayerCompleteViewDefault(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutXml() {
        return R.layout.layout_player_view_complete;
    }

    @Override
    protected void initViews() {
        mBackView = findViewById(R.id.player_complete_back);
        mTextView = findViewById(R.id.player_complete_text);
        mContinueView = findViewById(R.id.player_complete_btn_continue);
        mCancelView = findViewById(R.id.player_complete_btn_cancel);
    }

    public TextView getBackView(){
        return mBackView;
    }

    public TextView getTextView(){
        return mTextView;
    }

    public TextView getContinueView(){
        return mContinueView;
    }

    public TextView getCancelView(){
        return mCancelView;
    }

}
