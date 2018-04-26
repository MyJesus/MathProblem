package com.readboy.mathproblem.aliplayer;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.readboy.aliyunplayerlib.view.PlayerTopViewBase;
import com.readboy.mathproblem.R;

/**
 * Created by oubin on 2018/4/17.
 */

public class PlayerTopView extends PlayerTopViewBase {

    private TextView mVideoName;
    private static int[] VIEW_IDS = {R.id.player_back, R.id.player_video_list_switch,
            R.id.player_exercise};

    public PlayerTopView(Context context) {
        this(context, null);
    }

    public PlayerTopView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayerTopView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    protected int getLayoutXml() {
        return R.layout.player_top_bar;
    }

    @Override
    protected void initViews() {
        mVideoName = (TextView) findViewById(R.id.player_video_name);
    }

    public void setVideoName(String name) {
        mVideoName.setText(name);
    }

    public void setUnitOnClickListener(OnClickListener listener) {
        if (listener == null) {
            return;
        }
        initClickListener(listener);
    }

    private void initClickListener(OnClickListener listener) {
        for (int viewId : VIEW_IDS) {
            findViewById(viewId).setOnClickListener(listener);
        }
    }

    public void setViewVisibility(int resId, int visibility){
        findViewById(resId).setVisibility(visibility);
    }

}
