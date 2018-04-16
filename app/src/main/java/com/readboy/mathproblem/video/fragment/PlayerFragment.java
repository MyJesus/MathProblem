package com.readboy.mathproblem.video.fragment;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.readboy.mathproblem.video.db.VideoDatabaseInfo;
import com.readboy.mathproblem.video.movie.MovieActivity;
import com.readboy.video.IVideoPlayerListener;
import com.readboy.video.view.VideoView;

/**
 * @author dhm?
 */

public class PlayerFragment extends BaseFragment implements OnSeekCompleteListener,
        OnPreparedListener, OnCompletionListener, MediaPlayer.OnErrorListener {
    private static final String TAG = "oubin_PlayerFragment";

    private long mSeekPosition = 0;
    private String mPlayPath = null;
    private VideoView mVideo = null;
    private IVideoPlayerListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView: ");
        if (mVideo == null) {
            mVideo = new VideoView(getActivity(), null);
            mVideo.setOnPreparedListener(this);
            mVideo.setOnCompletionListener(this);
            mVideo.setOnErrorListener(this);
            mVideo.addVideoPlayerListener(mListener);
        }

        Log.i(TAG, "onCreateView ");

        return mVideo;
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy: ");
        if (mVideo != null) {
            mVideo.stopPlayback();
            if (mListener != null) {
                mVideo.removeVideoPlayerListener(mListener);
                mListener = null;
            }
        }
        Log.i(TAG, " AndroidFragment onDestroy() ");
        super.onDestroy();

        if (mVideo != null) {
            mVideo.release();
        }
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        Log.d(TAG, "-------- onPause ");
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (mVideo != null) {
            Log.i(TAG, " AndroidFragment onResume w: " + mVideo.getWidth() + ", h: " + mVideo.getHeight());
        }
        Log.d(TAG, "-------- onResume ");
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        MovieActivity ac = (MovieActivity) getActivity();
        ac.seekComplete();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        // activity 提供一个接口，说明视频资源加载完成了
        MovieActivity ac = (MovieActivity) getActivity();
        ac.prepare();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        // activity 提供一个接口，说明，视频资源播放完了
        mSeekPosition = 0;
        MovieActivity ac = (MovieActivity) getActivity();
        ac.complete();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e(TAG, "onError: what = " + what + ", extra = " + extra);
        MovieActivity ac = (MovieActivity) getActivity();
//        return ac.onError(mp, what, extra);
        return true;
    }

    @Override
    public void setPlayPath(String path, VideoDatabaseInfo info) {
        mPlayPath = path;
        if (mPlayPath != null && mVideo != null) {
            Log.e(TAG, "PATH3:" + path);
            Log.e(TAG, "setPlayPath: seekPosition = " + mSeekPosition);
//            mVideo.setVideoPath(mPlayPath);
            Log.e(TAG, "setPlayPath: video database info = " + info.toString());
            mVideo.setVideoURI(mPlayPath, info.mCachePath, true);
//            mVideo.setVideoPath(path, true);
            if (mSeekPosition > 0) {
                mVideo.seekTo((int) mSeekPosition);
                mSeekPosition = 0;
            }
        }
        Log.d(TAG, "-------- setPlayPath mSeekPosition: " + mSeekPosition);
    }

    @Override
    public boolean start() {
        boolean isStart = false;
        if (mVideo != null) {
            Log.e(TAG, " AndroidFragment start mVideo start");
            isStart = mVideo.start();
        }
        return isStart;
    }

    @Override
    public void pause() {
        if (mVideo != null) {
            mVideo.pause();
        }
    }

    @Override
    public void seekTo(long mesc) {
        Log.e(TAG, "seekTo: mesc = " + mesc);
        mSeekPosition = mesc;
        if (mVideo != null) {
            mVideo.seekTo((int) mesc);
        }
    }

    @Override
    public void stopPlayback() {
        Log.e(TAG, "stopPlayback: ");
        if (mVideo != null) {
            mVideo.stopPlayback();
        }
    }

    @Override
    public boolean isPlaying() {
        boolean isPlaying = false;
        if (mVideo != null) {
            isPlaying = mVideo.isPlaying();
        }
        return isPlaying;
    }

    @Override
    public int getBufferPercentage() {
        int percent = 0;
        if (mVideo != null) {
            percent = mVideo.getBufferPercentage();
        }
        return percent;
    }

    @Override
    public int getCurrentPosition() {
        int current = 0;
        if (mVideo != null) {
            current = mVideo.getCurrentPosition();
        }
        return current;
    }

    @Override
    public long getDuration() {
        int duration = 0;
        if (mVideo != null) {
            duration = mVideo.getDuration();
        }
        return duration;
    }

    @Override
    public void setLayout() {
        if (mVideo != null) {
            mVideo.setLayout();
        }
    }

    @Override
    public void setLayout(int type) {
        if (mVideo != null) {
            mVideo.setLayout(type == VideoView.VIDEO_LAYOUT_ORIGIN ? VideoView.VIDEO_LAYOUT_ORIGIN : VideoView.VIDEO_LAYOUT_STRETCH, 0.0f, 0.0f);
        }
    }

    @Override
    public boolean isInPlaybackState() {
        boolean isState = false;
        if (mVideo != null) {
            isState = mVideo.isInPlaybackState();
        }
        return isState;
    }

    public void addVideoPlayerListener(IVideoPlayerListener listener) {
        Log.e(TAG, "addVideoPlayerListener: ");
        mListener = listener;
        if (mVideo != null) {
            mVideo.addVideoPlayerListener(listener);
        } else {
            Log.e(TAG, "addVideoPlayerListener: mVideo = null");
        }
    }

    public int getPlayerState() {
        return mVideo == null ? -1 : mVideo.getCurrentState();
    }
}
