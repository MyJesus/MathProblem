package com.readboy.mathproblem.util;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

/**
 * Created by oubin on 2018/8/6.
 */

public class AudioFocusManager {
    private static final String TAG = "AudioFocusManager";

    private boolean hasRequestAudioFocus;


    private int requestAudioFocusTransient(Context context) {
        Log.d(TAG, "requestAudioFocusTransient: hasRequestAudioFocus = " + hasRequestAudioFocus);
        if (hasRequestAudioFocus) {
//            Log.e(TAG, "requestAudioFocusTransient: has not request audio focus.");
            return AudioManager.AUDIOFOCUS_REQUEST_FAILED;
        }
        AudioManager sAudioManager =
                (AudioManager) context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        if (sAudioManager != null) {
            int ret = sAudioManager.requestAudioFocus(mAudioFocusChangeListener,
                    AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            Log.e(TAG, "requestAudioFocusTransient: ret = " + ret);
            if (ret == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
                Log.e(TAG, "requestAudioFocus fail: ret = " + ret);
            } else if (ret == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                hasRequestAudioFocus = true;
            }
            return ret;
        }
        Log.e(TAG, "requestAudioFocus: valueAt audio service fail");
        return AudioManager.AUDIOFOCUS_REQUEST_FAILED;
    }

    /**
     * 恢复播放
     *
     * @return 是否抢焦点成功，如果为{@link AudioManager#AUDIOFOCUS_GAIN}代表抢焦点成功，反之。
     */
    public int abandonAudioFocus(Context context) {
        Log.d(TAG, "abandonAudioFocus: hasRequestAudioFocus = " + hasRequestAudioFocus);
        if (!hasRequestAudioFocus) {
//            Log.e(TAG, "abandonAudioFocus: has not request audio focus.");
            return AudioManager.AUDIOFOCUS_REQUEST_FAILED;
        }
        AudioManager sAudioManager =
                (AudioManager) context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        if (sAudioManager != null) {
            int ret = sAudioManager.abandonAudioFocus(mAudioFocusChangeListener);
            Log.d(TAG, "abandonAudioFocus: ret = " + ret);
            if (ret == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
                Log.e(TAG, "abandonAudioFocus fail: ret = " + ret);
            } else if (ret == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                hasRequestAudioFocus = false;
            }

            return ret;
        }
        Log.e(TAG, "abandonAudioFocus: valueAt audio service fail");
        return AudioManager.AUDIOFOCUS_REQUEST_FAILED;
    }

    private AudioManager.OnAudioFocusChangeListener mAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            Log.e(TAG, "onAudioFocusChange: focusChange = " + focusChange);
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
//                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    Log.e(TAG, "onAudioFocusChange: loss focus. focus flag = " + focusChange);
                    //doSomething,
                    hasRequestAudioFocus = false;
                    break;
                default:
                    Log.e(TAG, "onAudioFocusChange: default focus = " + focusChange);
            }
        }
    };

    public static interface OnAduioFocusChangeListener{
        void onAduioFocusChange(int focusChange);
    }

}
