package com.readboy.mathproblem.adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.readboy.mathproblem.R;
import com.readboy.mathproblem.dialog.VideoInfo;

/**
 * Created by oubin on 2017/9/5.
 */

public class VideoViewHolderOld extends RecyclerView.ViewHolder {
    private static final String TAG = "VideoViewHolder";

    private CheckBox mVideoSelect;
    private ImageView mVideoThumbnail;
    private TextView mVideoName;
    private TextView mVideoSize;
    private VideoInfo videoInfo;
    private Bitmap mThumbnail;

    public VideoViewHolderOld(View itemView) {
        super(itemView);
        mVideoSelect = (CheckBox) itemView.findViewById(R.id.video_select);
        mVideoThumbnail = (ImageView) itemView.findViewById(R.id.video_thumbnail);
        mVideoName = (TextView) itemView.findViewById(R.id.small_player_video_name);
        mVideoSize = (TextView) itemView.findViewById(R.id.video_size);
        mVideoSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (videoInfo != null) {
                videoInfo.setChecked(isChecked);
            }
        });
    }

    public void recycle(){
//        if (mThumbnail != null){
//            mThumbnail.recycle();
//        }
    }

    public void bindView(int position, VideoInfo video) {
        this.videoInfo = video;
        ViewGroup.LayoutParams lp = mVideoThumbnail.getLayoutParams();

//        Bitmap bitmap = BitmapUtils.getVideoThumbnail(video.getPath(),
//                lp.width, lp.height);
//        if (bitmap != null) {
//            mVideoThumbnail.setImageBitmap(bitmap);
//        }
        mVideoName.setText(video.getName());
        mVideoSize.setText(video.getSize());
        mVideoSelect.setChecked(video.isChecked());
        mVideoSelect.setTag(position);
    }

    public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener listener){
        mVideoSelect.setOnCheckedChangeListener(listener);
    }


}
