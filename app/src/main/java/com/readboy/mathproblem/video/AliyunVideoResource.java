package com.readboy.mathproblem.video;

import android.net.Uri;

import com.aliyun.vodplayer.media.AliyunVidSts;
import com.aliyun.vodplayer.media.AliyunVodPlayer;
import com.readboy.aliyunplayerlib.helper.VidStsHelper;

/**
 * Created by oubin on 2018/4/25.
 */

public class AliyunVideoResource implements IVideoPlayer.IVideoResource {

    private AliyunVidSts mVidSts;
    private VidStsHelper mVidStsHelper = null;

    public void play(AliyunVodPlayer vodPlayer){

    }

    /**
     * 准备播放，加载视频资源
     *
     * @param player
     */
    @Override
    public void play(IVideoPlayer player) {

    }

    /**
     * 带初始播放进度的播放
     *
     * @param player
     * @param seekPosition
     */
    @Override
    public void play(IVideoPlayer player, long seekPosition) {

    }

    @Override
    public Uri getUri() {
        return null;
    }
}
