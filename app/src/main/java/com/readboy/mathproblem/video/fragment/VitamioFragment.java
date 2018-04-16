//package com.readboy.mathproblem.video.fragment;
//
//import io.vov.vitamio.MediaPlayer.OnCompletionListener;
//import io.vov.vitamio.MediaPlayer.OnErrorListener;
//import io.vov.vitamio.MediaPlayer.OnInfoListener;
//import io.vov.vitamio.MediaPlayer.OnPreparedListener;
//import io.vov.vitamio.MediaPlayer;
//import io.vov.vitamio.widget.VideoView;
//
//import android.net.Uri;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Toast;
//
//import com.readboy.mathproblem.video.movie.MovieActivity;
//
//public class VitamioFragment extends BaseFragment implements OnPreparedListener, OnCompletionListener, OnInfoListener,
//	OnErrorListener {
//	private static final String TAG = "VitamioFragment";
//	private boolean mPrepared = false;
//	private boolean mPause = true;
//	private int mLayoutType = VideoView.VIDEO_LAYOUT_SCALE;
//	private long mSeekPosition = 0;
//	private String mPlayPath = null;
//	private VideoView mVideo = null;
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//	}
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		if (mVideo == null) {
//			mVideo = new VideoView(getActivity(), null);
//			mVideo.setOnErrorListener(this);
//			mVideo.setOnInfoListener(this);
//			mVideo.setOnPreparedListener(this);
//			mVideo.setOnCompletionListener(this);
//		}
//		return mVideo;
//	}
//
//
//
//	@Override
//	public void onDestroyView() {
//		// TODO Auto-generated method stub
//		super.onDestroyView();
//		Log.i("", " VitamioFragment onDestroyView() ");
//	}
//
//	@Override
//	public void onDetach() {
//		// TODO Auto-generated method stub
//		super.onDetach();
//		Log.i("", " VitamioFragment onDetach() ");
//	}
//
//	@Override
//	public void onDestroy() {
//		if (mVideo != null) {
//			mVideo.stopPlayback();
//		}
//		super.onDestroy();
//		Log.i("", " VitamioFragment onDestroy() ");
//	}
//
//	@Override
//	public void onPause() {
//		super.onPause();
//		mPause = true;
//	}
//
//	@Override
//	public void onResume() {
//		super.onResume();
//		mPause = false;
//		if (mVideo != null) {
//			Log.i("", " VitamioFragment onResume w: "+mVideo.getWidth()+", h: "+mVideo.getHeight());
//		}
//	}
//
//	@Override
//	public void setPlayPath(String playPath) {
//		mPlayPath = playPath;
//		if (mPlayPath != null && mVideo != null) {
//			mVideo.setVideoURI(Uri.parse(mPlayPath));
//		}
//	}
//
//	@Override
//	public void start() {
//		if (mVideo != null) {
//			mVideo.start();
//		}
//	}
//
//	@Override
//	public void pause() {
//		if (mVideo != null) {
//			mVideo.pause();
//		}
//	}
//
//	@Override
//	public void seekTo(long mesc) {
//		mSeekPosition = mesc;
//		if (mVideo != null && mPrepared) {
//			mVideo.seekTo((int) mesc);
//		}
//	}
//
//	@Override
//	public void stopPlayback() {
//		if (mVideo != null) {
//			mVideo.stopPlayback();
//		}
//	}
//
//	@Override
//	public boolean isPlaying() {
//		boolean isPlaying = false;
//		if (mVideo != null) {
//			isPlaying = mVideo.isPlaying();
//		}
//		return isPlaying;
//	}
//
//	@Override
//	public int getBufferPercentage() {
//		int percent = 0;
//		if (mVideo != null) {
//			percent = mVideo.getBufferPercentage();
//		}
//		return percent;
//	}
//
//	@Override
//	public long getCurrentPosition() {
//		long current = 0;
//		if (mVideo != null) {
//			current = mVideo.getCurrentPosition();
//		}
//		return current;
//	}
//
//	@Override
//	public long getDuration() {
//		long duration = 0;
//		if (mVideo != null) {
//			duration = mVideo.getDuration();
//		}
//		return duration;
//	}
//
//	@Override
//	public void setLayout() {
//		if (mVideo != null) {
//			mVideo.setVideoLayout(mLayoutType, 0);
//		}
//	}
//
//	@Override
//	public void setLayout(int type) {
//		if (mVideo != null) {
//			mVideo.setVideoLayout(type, 0);
//			mLayoutType = type;
//		}
//	}
//
//	@Override
//	public void onCompletion(MediaPlayer mp) {
//		mSeekPosition = 0;
//		MovieActivity ac = (MovieActivity) getActivity();
//		ac.complete();
//	}
//
//	@Override
//	public void onPrepared(MediaPlayer mp) {
//		mPrepared = true;
//		// activity 提供一个接口，说明视频资源加载完成了
//		MovieActivity ac = (MovieActivity) getActivity();
//		ac.prepare();
//		if (mSeekPosition > 0 && mVideo !=null) {
//			mVideo.seekTo(mSeekPosition);
//		}
//	}
//
//	@Override
//	public boolean onInfo(MediaPlayer mp, int what, int extra) {
//		if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
//			if (mp != null) {
//				mp.pause();
//			}
//		} else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
//			if (mp != null && !mp.isPlaying() && !mPause) {
//				mp.setVideoQuality(MediaPlayer.VIDEOQUALITY_HIGH);
//				mp.setVideoChroma(MediaPlayer.VIDEOCHROMA_RGBA);
//				mp.start();
//				Log.d(TAG, " MovieActivity onInfo MEDIA_INFO_BUFFERING_END: mp start() ");
//			}
//		} else if (what == MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED) {
//
////			Log.d(TAG, " MovieActivity onInfo MEDIA_INFO_DOWNLOAD_RATE_CHANGED: 下载速度变化中： "+extra);
//		} else if (what == MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING) {
//
//			Log.d(TAG, " MovieActivity onInfo MEDIA_INFO_VIDEO_TRACK_LAGGING: 视频过于复杂，无法解码：不能快速解码帧。此时可能只能正常播放音频");
//		} else {
//			Log.d(TAG, " MovieActivity onInfo else what: " + what + ", extrad: " + extra);
//		}
//		return true;
//	}
//
//	@Override
//	public boolean onError(MediaPlayer mp, int framework_err, int impl_err) {
//		Log.d("", "Error: what: " + framework_err+ ", impl_err: "+ impl_err);
//		if (framework_err == MediaPlayer.MEDIA_ERROR_UNKNOWN) {
//			if (impl_err == MediaPlayer.MEDIA_ERROR_IO) {
//				Toast.makeText(getActivity(), "文件或者网络相关的操作错误， 将退出视频 ", Toast.LENGTH_LONG).show();
//			} else if (impl_err == MediaPlayer.MEDIA_ERROR_MALFORMED) {
//				Toast.makeText(getActivity(), "播放流不符合相关标准规范， 将退出视频 ", Toast.LENGTH_LONG).show();
//			} else if (impl_err == MediaPlayer.MEDIA_ERROR_UNSUPPORTED) {
//				Toast.makeText(getActivity(), "媒体框架不支持此功能， 将退出视频 ", Toast.LENGTH_LONG).show();
//			} else if (impl_err == MediaPlayer.MEDIA_ERROR_TIMED_OUT) {
//				Toast.makeText(getActivity(), "某些耗时操作导致播放错误， 将退出视频 ", Toast.LENGTH_LONG).show();
//			} else {
//				Toast.makeText(getActivity(), "视频文件未知错误，将退出播放 ", Toast.LENGTH_LONG).show();
//			}
//
//		}
//		getActivity().finish();
//		return true;
//	}
//
//}
