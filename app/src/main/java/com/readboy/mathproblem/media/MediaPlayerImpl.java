/*
 * Copyright (c) 2017 Baidu, Inc. All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.readboy.mathproblem.media;

import android.app.Service;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.readboy.mathproblem.application.MathApplication;
import com.readboy.mathproblem.util.LogUtil;
import com.readboy.mathproblem.util.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * android MediaPlayer -android平台中实现音频的播放逻辑
 * <p>
 * Created by guxiuzhong@baidu.com on 2017/5/31.
 * /**
 * 需要外部处理{@link AudioManager.OnAudioFocusChangeListener}，更新UI, 防止多应用重音问题。
 * TODO: 播放网络音频是否需要做缓存处理。
 */
public class MediaPlayerImpl implements IMediaPlayer {
    //    private static final String TAG = MediaPlayerImpl.class.getSimpleName();
    private static final String TAG = "oubin_MediaPlayerImpl";
    public static final String ASSERT_PREFIX = "assets://";
    private static final String KEY_SP_VOLUME = "currentVolume";
    private static final String KEY_SP_MUTE = "isMute";
    private IMediaPlayer.PlayState mCurrentState = IMediaPlayer.PlayState.IDLE;
    private MediaPlayer mMediaPlayer;
    private float currentVolume = 0.8f;                 // 默认音量80%
    private boolean isMute;
    private boolean isError38;
    private float currentPercent;
    private int currentSeekMilliseconds;
    private IAudioStreamStore audioStreamStore;         // stream流数据保存
    private List<IMediaPlayerListener> mediaPlayerListeners;
    private boolean isActive;
    private TelephonyManager telephonyManager;

    public MediaPlayerImpl() {
        mMediaPlayer = new MediaPlayer();
        // set audio stream type
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnBufferingUpdateListener(bufferingUpdateListener);
        mMediaPlayer.setOnErrorListener(errorListener);
        mMediaPlayer.setOnPreparedListener(preparedListener);
        mMediaPlayer.setOnCompletionListener(completionListener);
        mMediaPlayer.setOnSeekCompleteListener(seekCompleteListener);
        // 不同的音频源，此回调有的不回调！！！
        // mMediaPlayer.setOnInfoListener(infoListener);

        // 读取音量和静音的数据
        currentVolume = (float) MediaPlayerPreferenceUtil.get(MathApplication.getInstance(),
                KEY_SP_VOLUME, 0.8f);
        isMute = (boolean) MediaPlayerPreferenceUtil.get(MathApplication.getInstance(),
                KEY_SP_MUTE, false);
        // LinkedList
        mediaPlayerListeners = Collections.synchronizedList(new LinkedList<IMediaPlayerListener>());

        // 来电监听
        telephonyManager = (TelephonyManager)
                MathApplication.getInstance().getSystemService(Service.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    private PhoneStateListener phoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                // 电话挂断
                case TelephonyManager.CALL_STATE_IDLE:
                    resume();
                    break;
                // 等待接电话
                case TelephonyManager.CALL_STATE_RINGING:
                    pause();
                    break;
                // 通话中
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    break;
                default:
                    break;
            }
        }
    };

    private IAudioStreamStore.OnStoreListener onStoreListener = new IAudioStreamStore.SimpleOnStoreListener() {
        @Override
        public void onComplete(String path) {
            Log.e(TAG, "onStoreListener,path:" + path);
            // after down star play
            File file = new File(path);
            if (file.exists() && file.length() > 0) {
                play(path);
            } else {
                mCurrentState = IMediaPlayer.PlayState.ERROR;
                fireOnError("play path not exists or length<0 ",
                        IMediaPlayer.ErrorType.MEDIA_ERROR_INTERNAL_DEVICE_ERROR);
            }
        }
    };

    @Override
    public void play(MediaResource mediaResource) {
        if (mCurrentState == PlayState.PLAYING) {
            stop();
        }
        if (mediaResource.isStream) {
            play(mediaResource.stream);
        } else {
            play(mediaResource.url);
        }
    }

    private void play(InputStream stream) {
        Log.e(TAG, "play stream");
        if (audioStreamStore == null) {
            audioStreamStore = new AudioStreamStoreImpl();
        }
        audioStreamStore.setOnStoreListener(onStoreListener);
        audioStreamStore.save(stream);
    }

    private void play(String url) {
        Log.e(TAG, "play: url = " + url);
        if (TextUtils.isEmpty(url)) {
            Log.d(TAG, "play-url is empty");
            fireOnError("play-url is empty.",
                    IMediaPlayer.ErrorType.MEDIA_ERROR_INTERNAL_DEVICE_ERROR);
            mCurrentState = IMediaPlayer.PlayState.ERROR;
            return;
        }
        fireOnInit();
        Log.d(TAG, "play-url:" + url);
        if (url.startsWith(ASSERT_PREFIX) && url.length() > ASSERT_PREFIX.length()) {
            playAsset(url.substring(ASSERT_PREFIX.length()));
        } else {
            try {
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(url);
                // Async
                mMediaPlayer.prepareAsync();
                mCurrentState = IMediaPlayer.PlayState.PREPARING;
            } catch (IOException e) {
                e.printStackTrace();
                LogUtil.d(TAG, "playPath", e);
                mCurrentState = IMediaPlayer.PlayState.ERROR;
                fireOnError("IOException play url :"
                        + url, IMediaPlayer.ErrorType.MEDIA_ERROR_INTERNAL_DEVICE_ERROR);
            }
        }
    }

    private void playAsset(String resName) {
        LogUtil.d(TAG, "playAsset:" + resName);
        Log.e(TAG, "playAsset: resName = " + resName);
        try {
            AssetManager am = MathApplication.getInstance().getAssets();
            AssetFileDescriptor afd = am.openFd(resName);
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(afd.getFileDescriptor(),
                    afd.getStartOffset(), afd.getLength());
            mMediaPlayer.prepareAsync();
            mCurrentState = IMediaPlayer.PlayState.PREPARING;
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.d(TAG, "playAsset", e);
            mCurrentState = IMediaPlayer.PlayState.ERROR;
            fireOnError("IOException play playAsset",
                    IMediaPlayer.ErrorType.MEDIA_ERROR_INTERNAL_DEVICE_ERROR);
        }
    }

    @Override
    public void pause() {
        Log.e(TAG, "pause: current state = " + mCurrentState);
        switch (mCurrentState) {
            case PREPARING:
            case PREPARED:
                stop();
                break;
            case PLAYING:
                mMediaPlayer.pause();
                mCurrentState = PlayState.PAUSED;
                fireOnPaused();
                break;
            default:
                Log.e(TAG, "pause: other state = " + mCurrentState);
                break;
        }
    }

    @Override
    public void stop() {
        Log.e(TAG, "stop: ");
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.stop();
            mCurrentState = IMediaPlayer.PlayState.STOPPED;
            // delete audio file
            if (audioStreamStore != null) {
                audioStreamStore.cancel();
                audioStreamStore.speakAfter();
            }
            fireStopped();
        }
    }


    @Override
    public void resume() {
        if (mCurrentState == IMediaPlayer.PlayState.PAUSED) {
            Log.e(TAG, "resume: start() ");
            mMediaPlayer.start();
            mCurrentState = IMediaPlayer.PlayState.PLAYING;
            firePlaying();
        }
    }

    @Override
    public void release() {
        Log.e(TAG, "release: ");
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mCurrentState = IMediaPlayer.PlayState.IDLE;
            fireOnRelease();
        }
        if (audioStreamStore != null) {
            audioStreamStore.cancel();
            audioStreamStore.speakAfter();
        }
        mediaPlayerListeners.clear();
    }

    @Override
    public PlayState getPlayState() {
        return mCurrentState;
    }

    @Override
    public void seekTo(int pos) {
        currentSeekMilliseconds = pos;
        Log.e(TAG, "seekTo() called with: pos = " + pos + "");
        LogUtil.d(TAG, "seekTo,currentSeekMilliseconds:" + currentSeekMilliseconds);
        if (mMediaPlayer != null && mCurrentState == IMediaPlayer.PlayState.PREPARED) {
            mMediaPlayer.seekTo(pos);
        }
    }

    /**
     * 设置音量
     *
     * @param volume 0.0 -1.0
     */
    @Override
    public void setVolume(float volume) {
        // 设置音量就不再静音了，比如：说了调衡音量等操作
        Log.e(TAG, "setVolume() called with: volume = " + volume + "");
        isMute = false;
        currentVolume = volume;
        if (mMediaPlayer != null) {
            mMediaPlayer.setVolume(volume, volume);
        }
        //  保存数据
        MediaPlayerPreferenceUtil.put(MathApplication.getInstance(),
                KEY_SP_VOLUME, currentVolume);
        MediaPlayerPreferenceUtil.put(MathApplication.getInstance(),
                KEY_SP_MUTE, isMute);
    }

    @Override
    public float getVolume() {
        return currentVolume;
    }

    @Override
    public void setMute(boolean mute) {
        isMute = mute;
        if (mMediaPlayer != null) {
            if (mute) {
                mMediaPlayer.setVolume(0, 0);
            } else {
                mMediaPlayer.setVolume(currentVolume, currentVolume);
            }
        }
        //  保存数据
        MediaPlayerPreferenceUtil.put(MathApplication.getInstance(),
                KEY_SP_MUTE, isMute);
    }

    @Override
    public boolean getMute() {
        return isMute;
    }

    @Override
    public long getCurrentPosition() {
        if (mMediaPlayer == null) {
            return 0;
        }
        if (mCurrentState == IMediaPlayer.PlayState.IDLE || mCurrentState == IMediaPlayer.PlayState.ERROR) {
            return 0;
        }
        return mMediaPlayer.getCurrentPosition();
    }

    @Override
    public long getDuration() {
        if (mMediaPlayer == null) {
            return 0;
        }
        if (mCurrentState == IMediaPlayer.PlayState.IDLE || mCurrentState == IMediaPlayer.PlayState.ERROR) {
            return 0;
        }
        return mMediaPlayer.getDuration();
    }

    @Override
    public float getBufferPercentage() {
        return currentPercent;
    }


    @Override
    public void addMediaPlayerListener(IMediaPlayerListener listener) {
        if (!mediaPlayerListeners.contains(listener)) {
            mediaPlayerListeners.add(listener);
        }
    }

    @Override
    public void removeMediaPlayerListener(IMediaPlayerListener listener) {
        if (mediaPlayerListeners.contains(listener)) {
            mediaPlayerListeners.remove(listener);
        }
    }

    @Override
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    private void fireOnError(String error, ErrorType errorType) {
        ToastUtils.show(MathApplication.getInstance(), error);
        for (IMediaPlayer.IMediaPlayerListener listener : mediaPlayerListeners) {
            if (listener != null) {
                listener.onError(error, errorType);
            }
        }
    }

    private void fireOnInit() {
        for (IMediaPlayer.IMediaPlayerListener listener : mediaPlayerListeners) {
            if (listener != null) {
                listener.onInit();
            }
        }
    }

    private void fireOnPaused() {
        for (IMediaPlayer.IMediaPlayerListener listener : mediaPlayerListeners) {
            if (listener != null) {
                listener.onPaused();
            }
        }
    }

    private void fireStopped() {
        for (IMediaPlayer.IMediaPlayerListener listener : mediaPlayerListeners) {
            if (listener != null) {
                listener.onStopped();
            }
        }
    }

    private void firePlaying() {
        for (IMediaPlayer.IMediaPlayerListener listener : mediaPlayerListeners) {
            if (listener != null) {
                listener.onPlaying();
            }
        }
    }

    private void fireOnRelease() {
        for (IMediaPlayer.IMediaPlayerListener listener : mediaPlayerListeners) {
            if (listener != null) {
                listener.onRelease();
            }
        }
    }

    private void fireOnPrepared() {
        for (IMediaPlayer.IMediaPlayerListener listener : mediaPlayerListeners) {
            if (listener != null) {
                listener.onPrepared();
            }
        }
    }

    private void fireOonBufferingUpdate(int percent) {
        for (IMediaPlayer.IMediaPlayerListener listener : mediaPlayerListeners) {
            if (listener != null) {
                listener.onBufferingUpdate(percent);
            }
        }
    }

    private void fireOnCompletion() {
        for (IMediaPlayer.IMediaPlayerListener listener : mediaPlayerListeners) {
            if (listener != null) {
                listener.onCompletion();
            }
        }
    }

    private void fireOnBufferEnd() {
        for (IMediaPlayerListener listener : mediaPlayerListeners) {
            if (listener != null) {
                listener.onBufferingEnd();
            }
        }
    }

    private MediaPlayer.OnPreparedListener preparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            LogUtil.d(TAG, "onPrepared");
            mCurrentState = IMediaPlayer.PlayState.PREPARED;
            isError38 = false;
            fireOnPrepared();
            // must be called  after  prepareAsync or prepare
            LogUtil.d(TAG, "currentVolume:" + currentVolume);
            LogUtil.d(TAG, "currentSeekMilliseconds:" + currentSeekMilliseconds);
            //  一开始就说话让它静音了
            if (isMute) {
                mMediaPlayer.setVolume(0, 0);
            } else {
                setVolume(currentVolume);
            }
            seekTo(currentSeekMilliseconds);
        }
    };

    private MediaPlayer.OnBufferingUpdateListener bufferingUpdateListener =
            new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mp, int percent) {
//                    Log.e(TAG, "onBufferingUpdate: percent = " + percent);
                    currentPercent = percent * 1.0f;
                    fireOonBufferingUpdate(percent);
                }
            };
    private MediaPlayer.OnErrorListener errorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            LogUtil.e(TAG, "onError:" + what + ", extra:" + extra);
            if (what == -38) {
                isError38 = true;
                return false;
            }
            isError38 = false;
            mCurrentState = IMediaPlayer.PlayState.ERROR;
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("msg", "what: " + what + "; extra:" + extra);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            IMediaPlayer.ErrorType errorType;
            switch (what) {
                case MediaPlayer.MEDIA_ERROR_IO:  //-1004
                    // Stream服务端返回错误 (bad request, unauthorized, forbidden, not found etc)
                    errorType = IMediaPlayer.ErrorType.MEDIA_ERROR_INVALID_REQUEST;
                    break;
                case MediaPlayer.MEDIA_ERROR_TIMED_OUT:  //-110
                    // 端无法连接stream服务端
                    errorType = IMediaPlayer.ErrorType.MEDIA_ERROR_SERVICE_UNAVAILABLE;
                    break;
                case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:  //-1010
                    // 端内部错误
                    errorType = IMediaPlayer.ErrorType.MEDIA_ERROR_INTERNAL_DEVICE_ERROR;
                    break;
                case MediaPlayer.MEDIA_ERROR_MALFORMED:  //-1007
                    // stream服务端接受请求，但未能正确处理 ?????
                    errorType = IMediaPlayer.ErrorType.MEDIA_ERROR_INTERNAL_SERVER_ERROR;
                    break;
                default:
                    // 未知错误
                    errorType = IMediaPlayer.ErrorType.MEDIA_ERROR_UNKNOWN;
                    break;
            }
            fireOnError(jsonObject.toString(), errorType);
            return false;
        }
    };

    private MediaPlayer.OnSeekCompleteListener seekCompleteListener = new MediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(MediaPlayer mp) {
            LogUtil.d(TAG, "onSeekComplete mCurrentState = " + mCurrentState);
            if (mCurrentState == IMediaPlayer.PlayState.PREPARED) {
                Log.e(TAG, "onSeekComplete: start play");
                mp.start();
                mCurrentState = IMediaPlayer.PlayState.PLAYING;
                firePlaying();
            }
        }
    };

    private MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            if (isError38) {
                return;
            }
            LogUtil.e(TAG, "onCompletion");
            // delete audio file
            if (audioStreamStore != null) {
                audioStreamStore.cancel();
                audioStreamStore.speakAfter();
            }
            mCurrentState = IMediaPlayer.PlayState.COMPLETED;
            fireOnCompletion();
        }
    };
}