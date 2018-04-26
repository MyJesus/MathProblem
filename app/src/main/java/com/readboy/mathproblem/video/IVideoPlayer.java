package com.readboy.mathproblem.video;

import android.net.Uri;

/**
 *
 * 视频播放相关抽象接口，所有视频引擎，视频播放界面Surface建议都继承该接口，
 * 并且转换实现对应的功能，方便和界面处理解耦。
 * Created by oubin on 2018/4/25.
 * @author oubin
 * @date 2018/4/25
 */

public interface IVideoPlayer {

    /**
     * 播放状态
     */
    enum PlayState {
        // 播放错误
        ERROR(-1),
        // 播放未开始
        IDLE(0),
        // 播放准备中
        PREPARING(1),
        // 播放准备就绪
        PREPARED(2),
        // 正在播放
        PLAYING(3),
        // 暂停播放
        PAUSED(4),
        // 停止状态
        STOPPED(5),
        // 播放完成
        COMPLETED(6),
        // 被释放掉
        RELEASED(7);

        private int state;

        PlayState(int state) {
            this.state = state;
        }

        public int getState() {
            return state;
        }
    }

    /**
     * 播放器播放状态回调接口
     */
    interface IVideoPlayerListener {

        /**
         * 调用完play方法后回调此方法
         */
        void onInit();

        /**
         * 播放器准备完成后回调
         */
        void onPrepared();

        /**
         * 正在播放时回调
         */
        void onPlaying();

        /**
         * 暂停后回调
         */
        void onPaused();

        void onStopped();

        /**
         * 播放完成后回调
         */
        void onCompletion();

        /**
         * 播放器销毁后回调
         */
        void onRelease();

        void onInfo(int var1, int var2);

        /**
         * 播放出错
         */
        void onError(String error, int errorNo);

        /**
         * 播放器缓冲回调
         *
         * @param percent 缓冲的进度 0-100
         */
        void onBufferingUpdate(int percent);

        /**
         * 开始缓冲时回调
         */
        void onBufferingStart();

        /**
         * 结束缓冲时回调
         */
        void onBufferingEnd();

    }

    /**
     * 视频资源接口，如:vid方式播放，数据流播放方式，本地视频等，都实现该接口
     * 解决不同方式加载视频资源问题。
     * TODO 待考虑，待优化。
     */
    interface IVideoResource{
        /**
         * 准备播放，加载视频资源
         */
        void play(IVideoPlayer player);

        /**
         * 带初始播放进度的播放
         */
        void play(IVideoPlayer player, long seekPosition);

        Uri getUri();
    }

    /**
     * 获取当前的播放状态
     *
     * @return PlayState
     */
    PlayState getPlayState();

    /**
     * 播放
     */
    void play(IVideoResource videoResource);

    void play(IVideoResource videoResource, long seekPosition);

    /**
     * 暂停
     */
    void pause();

    /**
     * 停止播放
     */
    void stop();

    /**
     * 调用pause暂停后重新播放
     */
    void resume();

    /**
     * 从milliseconds位置开始播放
     *
     * @param milliseconds 毫秒
     */
    void seekTo(long milliseconds);

    /**
     * 释放，销毁播放器
     */
    void release();

    /**
     * 设置音量 volume：0-1
     *
     * @param volume 音量（0-1之间的浮点数）
     */
    void setVolume(float volume);

    float getVolume();

    /**
     * 设置静音
     *
     * @param mute 静音开关
     */
    void setMute(boolean mute);

    boolean getMute();

    /**
     * 获取当前的播放位置
     *
     * @return 当前的播放位置
     */
    long getCurrentPosition();

    /**
     * 获取当前音频文件／流的总时长，单位:ms
     *
     * @return ms
     */
    long getDuration();

    /**
     * 获取当前缓冲到多少 0-100f
     *
     * @return 0-100f
     */
    float getBufferPercentage();

    /**
     * 添加播放器状态回调
     *
     * @param listener listener
     */
    void addMediaPlayerListener(IVideoPlayerListener listener);

    void removeMediaPlayerListener(IVideoPlayerListener listener);

    /**
     * 是否可用
     *
     * @param isActive 是否处在活跃状态
     */
    void setActive(boolean isActive);

    boolean isActive();

    /**
     * 设置播放速度
     */
    void setPlaySpeed(float var1);

    /**
     * 切换播放质量
     */
    void changeQuality(String var1);

}
