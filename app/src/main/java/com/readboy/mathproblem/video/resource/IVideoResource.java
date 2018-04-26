package com.readboy.mathproblem.video.resource;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Parcelable;

import com.readboy.aliyunplayerlib.view.AliPlayerView;

/**
 * Created by oubin on 2018/4/19.
 */

public interface IVideoResource extends Parcelable{

    /**
     * 播放处理，如果换了播放引擎，请修改该方法
     *
     * @param aliPlayerView 播放引擎界面
     * @param position      播放的其实位置
     */
    void play(AliPlayerView aliPlayerView, long position);

    /**
     * @return 视频名
     */
    String getVideoName();

    /**
     * 获取播放链接
     */
    Uri getVideoUri();

    /**
     * 是否已下载
     */
    boolean isDownloaded();

    boolean isDownloading();

    /**
     * 下载视频
     */
    boolean downloadVideo();

    /**
     * 是否已收藏
     */
    boolean isFavorite(ContentResolver resolver);

    /**
     * 收藏视频
     */
    boolean favoriteVideo(ContentResolver resolver);

    boolean unFavoriteVideo(ContentResolver resolver);

}
