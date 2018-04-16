package com.readboy.mathproblem.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.readboy.mathproblem.R;
import com.readboy.mathproblem.http.download.DownloadModel;

/**
 * Created by oubin on 2017/9/5.
 */

public class DownloadAdapter extends CheckAdapter<DownloadModel, DownloadViewHolder> {

    public DownloadAdapter(Context context) {
        super(context);
    }

    @Override
    public DownloadViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_video_downloading, parent, false);
        DownloadViewHolder viewHolder = new DownloadViewHolder(view);
        viewHolder.setOnInnerClickListener(this);
        viewHolder.setCheckOnClickListener(this);
        return viewHolder;
    }

    @Override
    protected void initSelectedArray() {
        mSelectedArray = new boolean[getItemCount()];
    }

}
