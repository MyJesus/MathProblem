package com.readboy.mathproblem.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.readboy.mathproblem.R;
import com.readboy.mathproblem.dialog.VideoInfo;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by oubin on 2017/9/5.
 */

public class VideoAdapterOld extends RecyclerView.Adapter<VideoViewHolderOld>
        implements CompoundButton.OnCheckedChangeListener {

    private Context mContext;
    private LayoutInflater mInflater;

    private List<VideoInfo> mVideoList = new ArrayList<>();

    public VideoAdapterOld(Context context) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public VideoViewHolderOld onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_video_location, parent, false);
        VideoViewHolderOld holder = new VideoViewHolderOld(view);
        holder.setOnCheckedChangeListener(this);
        return new VideoViewHolderOld(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolderOld holder, int position) {
        holder.bindView(position, mVideoList.get(position));

    }

    @Override
    public void onViewRecycled(VideoViewHolderOld holder) {
        super.onViewRecycled(holder);
        holder.recycle();
    }

    @Override
    public int getItemCount() {
        return mVideoList.size();
    }

    public void setChecked(int position, boolean isChecked) {
        if (0 <= position && position < mVideoList.size()) {
            mVideoList.get(position).setChecked(isChecked);
        }
    }

    public void setAllChecked(boolean checked) {
        for (VideoInfo videoInfo : mVideoList) {
            videoInfo.setChecked(checked);
        }
        notifyDataSetChanged();
    }

    public List<VideoInfo> getCheckedList() {
        List<VideoInfo> list = new ArrayList<>();
        int size = mVideoList.size();
        for (int i = 0; i < size; i++) {
            VideoInfo info = mVideoList.get(i);
            if (info.isChecked()) {
                Log.e(TAG, "getCheckedList: position = " + i);
            }
        }

        for (VideoInfo videoInfo : mVideoList) {
            if (videoInfo.isChecked()) {
                list.add(videoInfo);
            }
        }
        return list;
    }

    public void update(List<VideoInfo> list) {
        if (list == null){
            return;
        }
        mVideoList.clear();
        mVideoList.addAll(list);
        notifyDataSetChanged();
    }

    public void setData(List<VideoInfo> list) {
        this.mVideoList = list;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int position = (int) buttonView.getTag();
        try {
            mVideoList.get(position).setChecked(isChecked);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "onCheckedChanged: IndexOutOfBoundsException, position = " + position
                    + ", size = " + mVideoList.size());
        }
    }
}
