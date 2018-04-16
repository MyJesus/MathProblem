package com.readboy.mathproblem.adapter;

import android.support.annotation.IntDef;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.readboy.mathproblem.R;
import com.readboy.mathproblem.cache.PicassoWrapper;
import com.readboy.mathproblem.db.Favorite;
import com.readboy.mathproblem.util.DateUtils;
import com.readboy.mathproblem.util.FileUtils;
import com.readboy.mathproblem.util.SizeUtils;
import com.readboy.mathproblem.util.VideoUtils;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;

/**
 * Created by oubin on 2017/9/5.
 */

public class FavoriteViewHolder extends CheckViewHolder<Favorite> implements View.OnClickListener {

    public static final int TYPE_LOCATION = 1;
    public static final int TYPE_FAVORITE = 2;

    @IntDef({TYPE_LOCATION, TYPE_FAVORITE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }

    private ImageView mVideoThumbnail;
    private TextView mVideoName;
    private TextView mVideoFavoriteDate;

    @Type
    private int mType = TYPE_FAVORITE;

    public FavoriteViewHolder(View itemView) {
        this(TYPE_FAVORITE, itemView);
    }

    public FavoriteViewHolder(@Type int type, View itemView) {
        super(itemView);
        this.mType = type;
        mCheckBox = (CheckBox) itemView.findViewById(R.id.video_select);
        mVideoThumbnail = (ImageView) itemView.findViewById(R.id.video_thumbnail);
        mVideoName = (TextView) itemView.findViewById(R.id.small_player_video_name);
        mVideoFavoriteDate = (TextView) itemView.findViewById(R.id.video_favorite_date);
//        mVideoName.setOnClickListener(this);
//        mVideoThumbnail.setOnClickListener(this);
        itemView.setOnClickListener(this);
    }

    @Override
    public void bindView(int position, boolean isChecked, Favorite favorite) {
        if (VideoUtils.videoIsExist(favorite.mName)) {
            PicassoWrapper.loadThumbnailWithPath(VideoUtils.getVideoPath(favorite.mName), mVideoThumbnail);
        } else if (!TextUtils.isEmpty(favorite.mThumbnail)) {
            PicassoWrapper.loadThumbnail(favorite.mThumbnail, mVideoThumbnail);
        } else {
            mVideoThumbnail.setBackgroundResource(R.drawable.video_thumbnail);
        }
        mVideoName.setText(FileUtils.getFileNameWithoutExtension(favorite.mName));
        if (mType == TYPE_FAVORITE) {
            mVideoFavoriteDate.setText(DateUtils.dateToString(new Date(favorite.mTime)));
        } else {
            File file = new File(favorite.mPath);
            if (file.exists()) {
                mVideoFavoriteDate.setText(SizeUtils.formatMemorySize(file.length()));
            } else {
                mVideoFavoriteDate.setText("0");
            }
        }
        mCheckBox.setChecked(isChecked);
        mCheckBox.setTag(position);
    }

    @Override
    public void onClick(View v) {
        handlerInnerItemClickEvent();
    }

}
