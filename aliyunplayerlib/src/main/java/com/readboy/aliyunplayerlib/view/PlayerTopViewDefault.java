package com.readboy.aliyunplayerlib.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import com.readboy.aliyunplayerlib.R;

/**
 * 提供默认的播放器顶部布局topView
 * Created by ldw on 2018/3/23.
 */

public class PlayerTopViewDefault extends PlayerTopViewBase {

    private TextView mBackView;


    public PlayerTopViewDefault(Context context) {
        super(context);
    }

    public PlayerTopViewDefault(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PlayerTopViewDefault(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutXml() {
        return R.layout.layout_player_view_top;
    }

    @Override
    protected void initViews() {
        mBackView = findViewById(R.id.video_player_back);
    }


    public TextView getBackView(){
        return mBackView;
    }

    public void setTitle(String title){
        if(!TextUtils.isEmpty(title)) {
            mBackView.setText(title);
        }
    }


}
