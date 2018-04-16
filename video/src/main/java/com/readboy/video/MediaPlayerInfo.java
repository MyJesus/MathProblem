package com.readboy.video;

/**
 * Created by oubin on 2017/11/21.
 */

public interface MediaPlayerInfo {

    int MEDIA_INFO_UNKNOWN = 1;//未知信息
    int MEDIA_INFO_STARTED_AS_NEXT = 2;//播放下一条
    int MEDIA_INFO_VIDEO_RENDERING_START = 3;//视频开始整备中
    int MEDIA_INFO_VIDEO_TRACK_LAGGING = 700;//视频日志跟踪
    int MEDIA_INFO_BUFFERING_START = 701;//开始缓冲中
    int MEDIA_INFO_BUFFERING_END = 702;//缓冲结束
    int MEDIA_INFO_NETWORK_BANDWIDTH = 703;//网络带宽，网速方面
    int MEDIA_INFO_BAD_INTERLEAVING = 800;//
    int MEDIA_INFO_NOT_SEEKABLE = 801;//不可设置播放位置，直播方面
    int MEDIA_INFO_METADATA_UPDATE = 802;//
    int MEDIA_INFO_TIMED_TEXT_ERROR = 900;
    int MEDIA_INFO_UNSUPPORTED_SUBTITLE = 901;//不支持字幕
    int MEDIA_INFO_SUBTITLE_TIMED_OUT = 902;//字幕超时

    int MEDIA_INFO_VIDEO_INTERRUPT = -10000;//数据连接中断
    int MEDIA_INFO_VIDEO_ROTATION_CHANGED = 10001;//视频方向改变
    int MEDIA_INFO_AUDIO_RENDERING_START = 10002;//音频开始整备中

}
