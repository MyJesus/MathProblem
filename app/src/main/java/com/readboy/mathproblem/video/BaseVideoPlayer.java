package com.readboy.mathproblem.video;

import android.support.annotation.NonNull;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by oubin on 2018/4/25.
 */

public abstract class BaseVideoPlayer implements IVideoPlayer {

    private PlayState mCurrentState;
    private List<IVideoPlayerListener> mPlayerListeners = new CopyOnWriteArrayList<>();

    @Override
    public void addMediaPlayerListener(IVideoPlayerListener listener) {
        if (!mPlayerListeners.contains(listener)) {
            mPlayerListeners.add(listener);
        }
    }

    @Override
    public void removeMediaPlayerListener(@NonNull IVideoPlayerListener listener) {
        mPlayerListeners.remove(listener);
    }

    protected void fireOnInit() {
        mCurrentState = PlayState.PREPARING;
        mPlayerListeners.forEach(IVideoPlayerListener::onInit);
    }

    protected void fireOnPrepared() {
        mCurrentState = PlayState.PREPARED;
        mPlayerListeners.forEach(IVideoPlayerListener::onPrepared);
    }

    protected void fireOnPlaying() {
        mCurrentState = PlayState.PLAYING;
        mPlayerListeners.forEach(IVideoPlayerListener::onPlaying);
    }

    protected void fireOnPaused() {
        mCurrentState = PlayState.PAUSED;
        mPlayerListeners.forEach(IVideoPlayerListener::onPaused);
    }

    protected void fireOnStopped() {
        mCurrentState = PlayState.STOPPED;
        mPlayerListeners.forEach(IVideoPlayerListener::onStopped);
    }

    protected void fireOnCompletion() {
        mCurrentState = PlayState.COMPLETED;
        mPlayerListeners.forEach(IVideoPlayerListener::onCompletion);
    }

    protected void fireOnError(String error, int errorNo) {
        mCurrentState = PlayState.ERROR;
        for (IVideoPlayerListener mPlayerListener : mPlayerListeners) {
            mPlayerListener.onError(error, errorNo);
        }
    }

    protected void fireOnInfo(int arg1, int arg2) {
        for (IVideoPlayerListener mPlayerListener : mPlayerListeners) {
            mPlayerListener.onInfo(arg1, arg2);
        }
    }

    protected void fireBuffingStart() {
        mPlayerListeners.forEach(IVideoPlayerListener::onBufferingStart);
    }

    protected void fireBuffingEnd() {
        mPlayerListeners.forEach(IVideoPlayerListener::onBufferingEnd);
    }

    protected void fireBuffingUpdate(int percent){
        for (IVideoPlayerListener mPlayerListener : mPlayerListeners) {
            mPlayerListener.onBufferingUpdate(percent);
        }
    }

}
