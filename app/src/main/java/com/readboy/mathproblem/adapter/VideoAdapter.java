package com.readboy.mathproblem.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.readboy.mathproblem.R;
import com.readboy.mathproblem.dialog.VideoInfo;

/**
 * Created by oubin on 2017/9/5.
 */

public class VideoAdapter extends CheckAdapter<VideoInfo, VideoViewHolder>{

    public VideoAdapter(Context context) {
        super(context);
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_video_location, parent, false);
        VideoViewHolder holder = new VideoViewHolder(view);
        holder.setCheckOnClickListener(this);
        holder.setOnInnerClickListener(this);
        return holder;
    }

}
