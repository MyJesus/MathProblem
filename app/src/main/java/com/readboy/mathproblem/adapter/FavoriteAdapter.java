package com.readboy.mathproblem.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.readboy.mathproblem.R;
import com.readboy.mathproblem.aliplayer.AliyunPlayerActivity;
import com.readboy.mathproblem.db.Favorite;
import com.readboy.mathproblem.http.response.VideoInfoEntity;
import com.readboy.mathproblem.util.VideoUtils;
import com.readboy.mathproblem.video.movie.VideoExtraNames;
import com.readboy.mathproblem.video.proxy.VideoProxy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by oubin on 2017/9/14.
 */

public class FavoriteAdapter extends CheckAdapter<Favorite, FavoriteViewHolder> {
    private static final String TAG = "FavoriteAdapter";

    private Cursor mCursor;

    public FavoriteAdapter(Context context) {
        super(context);
    }

    @Override
    public FavoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_video_favorite, parent, false);
        FavoriteViewHolder viewHolder = new FavoriteViewHolder(view);
        viewHolder.setOnInnerClickListener(this);
        viewHolder.setCheckOnClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FavoriteViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position)) {
            Log.e(TAG, "onBindViewHolder: move to position fail");
            return;
        }

        final Favorite favorite = new Favorite(mCursor);
        holder.bindView(position, mSelectedArray[position], favorite);
    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    public ArrayList<Long> getSelectedFavorites() {
        List<Integer> positions = getSelectedPosition();
        ArrayList<Long> idList = new ArrayList<>();
        for (Integer position : positions) {
            mCursor.moveToPosition(position);
            Favorite favorite = new Favorite(mCursor);
            idList.add(favorite._id);
        }
        return idList;
    }

    public void swapCursor(Cursor cursor) {
        if (mCursor == cursor) {
            return;
        }
        if (mCursor != null) {
            mCursor.close();
        }
        this.mCursor = cursor;
        if (mCursor != null) {
            mSelectedArray = new boolean[mCursor.getCount()];
        } else {
            mSelectedArray = new boolean[0];
        }
        notifyDataSetChanged();
    }

    @Override
    public void handlerItemClick(int position, BaseViewHolder viewHolder) {
        super.handlerItemClick(position, viewHolder);
        Log.e(TAG, "handlerItemClick() called with: position = " + position + ", viewHolder = " + viewHolder + "");
        //数据防御，ViewHolder.getAdapterPosition
        if (position < 0 || position > getItemCount()) {
            Log.e(TAG, "handlerItemClick: position is invalid, item maybe has been removed from the adapter" +
                    ", position = " + position + ", videoHolder = " + viewHolder.toString());
            return;
        }
        mCursor.moveToPosition(position);
        Favorite favorite = new Favorite(mCursor);
        String url = favorite.mUrl;
        ArrayList<VideoInfoEntity.VideoInfo> videoInfos = new ArrayList<>();
        VideoInfoEntity.VideoInfo videoInfo = Favorite.convertVideoInfo(favorite);
        Intent intent = new Intent(mContext, AliyunPlayerActivity.class);
        videoInfos.add(videoInfo);
        intent.putExtra(VideoExtraNames.EXTRA_VIDEO_INFO_LIST, videoInfos);
        mContext.startActivity(intent);
//        if (!TextUtils.isEmpty(url)) {
//            VideoProxy.playWithUrl(url, mContext);
//        } else {
//            String path = VideoUtils.getVideoPath(favorite.mName);
//            File file = new File(path);
//            if (file.exists()) {
//                VideoProxy.playWithPath(file.getAbsolutePath(), mContext);
//            }
//        }
    }
}
