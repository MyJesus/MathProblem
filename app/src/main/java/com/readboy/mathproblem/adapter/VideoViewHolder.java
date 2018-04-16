package com.readboy.mathproblem.adapter;

import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.readboy.mathproblem.R;
import com.readboy.mathproblem.cache.PicassoWrapper;
import com.readboy.mathproblem.dialog.VideoInfo;
import com.squareup.picasso.Callback;

/**
 * Created by oubin on 2017/9/5.
 */

public class VideoViewHolder extends CheckViewHolder<VideoInfo> implements View.OnClickListener{
    private static final String TAG = "VideoViewHolder";

    private ImageView mVideoThumbnail;
    private TextView mVideoName;
    private TextView mVideoSize;

    public VideoViewHolder(View itemView) {
        super(itemView);
        mCheckBox = (CheckBox) itemView.findViewById(R.id.video_select);
        mVideoThumbnail = (ImageView) itemView.findViewById(R.id.video_thumbnail);
        mVideoName = (TextView) itemView.findViewById(R.id.small_player_video_name);
        mVideoSize = (TextView) itemView.findViewById(R.id.video_size);
        mVideoName.setOnClickListener(this);
        mVideoThumbnail.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        handlerInnerItemClickEvent();
    }

    @Override
    public void bindView(int position, boolean isChecked, VideoInfo video) {
        super.bindView(position, isChecked, video);
        Log.e(TAG, "bindView: path = " + video.getPath());
        PicassoWrapper.loadThumbnail(video.getPath(), mVideoThumbnail, new Callback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError() {
            }
        });
        mVideoName.setText(video.getName());
        mVideoSize.setText(video.getSize());
    }

}
