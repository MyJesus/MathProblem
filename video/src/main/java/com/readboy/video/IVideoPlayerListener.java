package com.readboy.video;

/**
 * Created by oubin on 2017/11/20.
 * @author oubin
 */

public interface IVideoPlayerListener {

    /**
     * 调用完play方法后回调此方法
     */
    void onInit();

    /**
     * 播放器准备完成后回调
     */
    void onPrepared();

    /**
     * 播放器销毁后回调
     */
    void onRelease();

    /**
     * 正在播放时回调
     */
    void onPlaying();

    /**
     * 暂停后回调
     */
    void onPaused();

    /**
     * 停止播放，未必会释放资源
     */
    void onStopped();

    /**
     * 播放完成后回调
     */
    void onCompletion();

    /**
     * 播放出错
     */
    void onError(String error, ErrorType errorType);

    /**
     * 播放器缓冲回调
     *
     * @param percent 缓冲的进度 0-100
     */
    void onBufferingUpdate(int percent);

    /**
     * 网络差，播放过程需要重新缓存资源, 提显示加载中对话框
     */
    void onBufferingStart();

    /**
     * 结束缓冲时回调, 缓存一定量了，可以播放了。未必成对出现。
     */
    void onBufferingEnd();

}
