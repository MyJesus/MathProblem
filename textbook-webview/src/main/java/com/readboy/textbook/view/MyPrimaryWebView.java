package com.readboy.textbook.view;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.dream.textbook.external.OnShowExternalFragmentListener;
import com.readboy.textbook.model.Comment;
import com.readboy.textbook.model.MyHtml;
import com.readboy.textbook.model.PrimarySection;
import com.readboy.textbook.model.PrimarySection.T;
import com.readboy.textbook.model.PrimarySection.T.SPH;
import com.readboy.textbook.util.CommentUtils;
import com.readboy.textbook.util.DebugLogger;
import com.readboy.textbook.util.DownloadUtils;
import com.readboy.textbook.util.MediaPlayUtils;
import com.readboy.textbook.util.MyApplication;
import com.readboy.textbook.util.NetWorkUtils;
import com.readboy.textbook.util.PopupDialog;
import com.readboy.textbook.util.PopupDialog.OnTreeObserver;
import com.readboy.textbook.util.PrimaryContentParser;
import com.readboy.textbook.util.Util;
import com.readboy.textbookwebview.PrimaryJavaScriptObject;
import com.readboy.textbookwebview.R;

public class MyPrimaryWebView extends MyBaseWebView
{
	public static final int RESET_READ_BTN_MSG = 0x100;
	public static final int SOUND_PLAY_COMPLETION_MSG = 0x110;
	public static final int SHOWN_COMMENT_MSG = 0x120;
	private Context mContext;
	private PrimaryJavaScriptObject mJavaScriptObject;
	private Comment mComment;
	private ArrayList<PrimarySection> mPrimarySections;
	private MediaPlayUtils mMediaPlayUtils;
	private int mSoundIndex = 0;
	private boolean mIsAutoPlayNext = false;
	private boolean mIsCommentSound = false;
	private ArrayList<String> mSoundArrayList = new ArrayList<String>();
	private ArrayList<String> mCommentSoundArrayList = new ArrayList<String>();
	private ArrayList<Integer> mSectionSpnSize = new ArrayList<Integer>();
	private int mSoundStartIndex = 0;
	private int mPlaySoundCount = 0;
	private String mPlaySoundButtonId = "";
	private Handler mHandler = null;
	private Handler mParentHandler = null;
	private int mLastOffset = 0;
	private boolean mIsStartPlaySound = false;
	
	protected OnShowExternalFragmentListener mExternalFragmentListener = null;
	
	public MyPrimaryWebView(Context context)
	{
		super(context);
		mContext = context;
		init();
	}
	
	public MyPrimaryWebView(Context context, AttributeSet attrs)
	{
		super(context, attrs, 0);
		mContext = context;
		init();
	}
	
	public MyPrimaryWebView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		mContext = context;
		init();
	}
	
	private void init()
	{	
		mJavaScriptObject = new PrimaryJavaScriptObject(mContext, this);
		addJavascriptInterface(mJavaScriptObject, "JavaScriptObject");
		

		mHandler = new Handler()
		{
			@Override
			public void handleMessage(Message msg) 
			{
				if(msg.what == RESET_READ_BTN_MSG)
				{
					resetReadButton(mPlaySoundButtonId, true);
				}
				else if (msg.what == SOUND_PLAY_COMPLETION_MSG) 
				{
					mSoundIndex++;
					playNextSound();
				}
				else if (msg.arg1 == DownloadUtils.DOWNLOAD_FINISH_MSG) 
				{
					playSoundAfterDownload(msg.obj);
				}
				else if(msg.arg1 == DownloadUtils.DOWNLOAD_ERROR_MSG)
				{
					Toast.makeText(mContext, R.string.sound_loading_error, Toast.LENGTH_LONG).show();
					sendMsgToParentHandler(RESET_READ_BTN_MSG);
					removeReadTextToSign();
				}
				else if(msg.arg1 == DownloadUtils.DOWNLOAD_TIMEOUT_MSG)
				{
					Toast.makeText(mContext, R.string.sound_loading_timeout_error, Toast.LENGTH_LONG).show();
					sendMsgToParentHandler(RESET_READ_BTN_MSG);
					removeReadTextToSign();
				}
				else if(msg.what == SHOWN_COMMENT_MSG)
				{
					showOrUpdateComment(msg.obj.toString());
				}
				else if(MediaPlayUtils.DELAY_CONFIGRM_MSG == msg.what)
				{
					removeReadTextToSign();
					MediaPlayUtils.delayStop();
				}
				else if(MediaPlayUtils.MUSIC_STOP_MSG == msg.what)
				{
					removeReadTextToSign();
				}
			}
		};
	}
	
	private void playSoundAfterDownload(Object object)
	{
		if(object != null)
		{
			if(mIsCommentSound)
			{
				if(mSoundIndex < mCommentSoundArrayList.size() && (NetWorkUtils.baseUrl + mCommentSoundArrayList.get(mSoundIndex)).equals(object.toString()))
				{
					playNextSound();
				}
			}
			else
			{
				if(mSoundIndex < mSoundArrayList.size() && (NetWorkUtils.baseUrl + mSoundArrayList.get(mSoundIndex)).equals(object.toString()))
				{							
					playNextSound();
				}
			}
		}
	}
	
	private void showOrUpdateComment(String content)
	{
//		WebView answerWebView = new WebView(getContext());
//		initWebViewSetting(answerWebView);
//		String data = makeAnswerHtml(id);
//		answerWebView.loadDataWithBaseURL(NetWorkUtils.baseUrl, data, "text/html", "UTF-8", "");
		TextView textView = new TextView(mContext);
		textView.setTextSize(20);
		if(MyApplication.getDeviceMode() == MyApplication.READBOY_PAD)
		{
			textView.setTextColor(Color.WHITE);
		}
		textView.setText(content);
		textView.setSaveEnabled(true);
		if (mExternalFragmentListener != null)
		{
			mExternalFragmentListener.onShowView(textView);
		}
		else
		{
			PopupDialog popupDialog = PopupDialog.newInstance(textView);
			popupDialog.show((Activity)mContext, PopupDialog.TAG);
			popupDialog.setOnTreeObserverListener(new OnTreeObserver()
			{
				
				@Override
				public void Observer(int width, int height)
				{
					if(width == 0 && height == 0)
					{
						MediaPlayUtils.pause();
						mIsCommentSound = false;
					}
				}
			});
		}
	}

	@Override
	protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect)
	{
		super.onFocusChanged(focused, direction, previouslyFocusedRect);
		if(!focused)
		{
			if(!isShown())
			{
				MediaPlayUtils.stop();
			}
		}
	}

	@Override
	public void setOnFocusChangeListener(OnFocusChangeListener l)
	{
		// TODO Auto-generated method stub
		super.setOnFocusChangeListener(l);
	}

	@Override
	public void destroy() 
	{
	
		DebugLogger.getLogger().d("webview destroy");
		super.destroy();
	}

	@Override
	public void onPause() 
	{
		
		DebugLogger.getLogger().d("webview pause");
		super.onPause();
	}

	/**
	 * 显示一段content
	 */
	public void setContent(String content)
	{
		loadView(content);
	}
	
	/**
	 * 显示一段content
	 */
	public void setContent(JSONArray content)
	{

		if (content != null && content.length() > 0) 
		{
			StringBuilder explain = new StringBuilder();
			ArrayList<JSONArray> commentArrayList = new ArrayList<JSONArray>();
			for (int i = 0; i < content.length(); i++) 
			{
				JSONObject contentJson = content.optJSONObject(i);
				if (contentJson != null) 
				{
//					JSONArray textbookJsonArray = contentJson.optJSONArray("content");
//					String contentId = contentJson.optString("id", "");
//					if(textbookJsonArray != null)
//					{
//						for (int j = 0; j < textbookJsonArray.length(); j++)
//						{
//							JSONObject textbookJson  = textbookJsonArray.optJSONObject(j);
//							explain.append(textbookJson.optString("explain", ""));	
//							JSONArray commentJsonArray = textbookJson.optJSONArray("comment");
//							if(commentJsonArray != null)
//							{
//								commentArrayList.add(commentJsonArray);
//							}
//						}
//					}
					explain.append(contentJson.optString("explain", ""));	
					JSONArray commentJsonArray = contentJson.optJSONArray("comment");
					if(commentJsonArray != null)
					{
						commentArrayList.add(commentJsonArray);
					}
				}
			}
			loadView(explain.toString());
			if(commentArrayList.size() > 0)
			{
				setComment(commentArrayList.get(0));
			}
		}		
		
	}
	
	/**
	 * set注解(文言文解释、作者、背景等引用)
	 */
	public void setComment(JSONArray comment)
	{
		if(comment != null)
		{
			mComment = new Comment();
			int count = comment.length();
			for(int i=0; i<count; i++)
			{
				JSONObject commentItemJsonObject = comment.optJSONObject(i);
				if(commentItemJsonObject != null)
				{
					String id = commentItemJsonObject.optString("id");
					String type = commentItemJsonObject.optString("type");
					String content = commentItemJsonObject.optString("content");
					if(!TextUtils.isEmpty(id) && !TextUtils.isEmpty(content))
					{
						mComment.addCommentItem(mComment.new Item(id, type, content));
					}
				}
			}
		}
	}
	
	public void setHandler(Handler handler)
	{
		mParentHandler = handler;
	}
	
	/**
	 * 弹出注解
	 */
	public void showComment(String id)
	{
		String commentId = id.trim();
		if(!TextUtils.isEmpty(commentId) && mComment != null)
		{
			String commentText = mComment.getCommentText(commentId);
			if(commentText != null)
			{
//				resetReadButton(mPlaySoundButtonId, true);
				HashMap<String, String> commentHashMap = CommentUtils.ParserPrimaryComment(commentText);
				commentText = commentHashMap.get("text");
				if(!TextUtils.isEmpty(commentText))
				{
					String snd = commentHashMap.get("snd");
					mCommentSoundArrayList.clear();
					if(!TextUtils.isEmpty(snd))
					{
						fillCommentSoundUrl(snd);
					}
					Message msg = new Message();
					msg.what = SHOWN_COMMENT_MSG;
					msg.obj = commentText;
					mHandler.sendMessage(msg);
//					TextView textView = new TextView(mContext);
//					textView.setTextSize(18);
//					textView.setPadding(30, 30, 30, 30);
//					textView.setText(commentText);
//					textView.setId(123);
//					textView.setSaveEnabled(true);
////					ExternalFragment commentFragment = new ExternalFragment();
////					commentFragment.show(textView);
//					if (mExternalFragmentListener != null)
//					{
//						mExternalFragmentListener.onShowView(textView);
//					}
//					PopupDialog popupDialog = PopupDialog.newInstance(textView);
//					popupDialog.show((Activity)mContext, PopupDialog.TAG);
//					popupDialog.setOnTreeObserverListener(new OnTreeObserver()
//					{
//						
//						@Override
//						public void Observer(int width, int height)
//						{
//							if(width == 0 && height == 0)
//							{
//								MediaPlayUtils.pause();
//								mIsCommentSound = false;
//							}
//						}
//					});
				}
			}
		}
	}
	
	/**
	 * 课本领读
	 */
	public void read()
	{
		if(mHandler.hasMessages(MediaPlayUtils.MUSIC_STOP_MSG))
		{
			mHandler.removeMessages(MediaPlayUtils.MUSIC_STOP_MSG);
		}
		Toast.makeText(mContext, R.string.sound_loading, Toast.LENGTH_SHORT).show();
		mIsCommentSound = false;
		if(MediaPlayUtils.isPlaying())
		{
			MediaPlayUtils.pause();
		}
//		else
		{
			if(mSoundArrayList.size() > 0)
			{
				mSoundIndex = 0;
				playSound(mSoundArrayList.get(mSoundIndex));
//				resetReadButton(mPlaySoundButtonId, false);
				setReadTextToSign(0);
				setKeepScreenOn(true);
				mPlaySoundButtonId = null;
			}
		}
	}
	
	/**
	 * 朗读语音
	 * @param id
	 */
	public void read(String id)
	{
		mIsCommentSound = false;
		if(MediaPlayUtils.isPlaying() && id.equalsIgnoreCase(mPlaySoundButtonId))
		{
			MediaPlayUtils.pause();
			return;
		}
		if(!TextUtils.isEmpty(id))
		{
			if(!id.equalsIgnoreCase(mPlaySoundButtonId))
			{
				resetReadButton(mPlaySoundButtonId, true);
				mPlaySoundButtonId = id;
			}
			String[] ids = id.split("-");
			if(ids.length == 3)
			{
				mSoundStartIndex = Integer.parseInt(ids[1]);
				mPlaySoundCount = Integer.parseInt(ids[2]);
				mSoundIndex = mSoundStartIndex;
			}
			if(mSoundArrayList.size() > 0)
			{
				setReadTextToSign(0);
				playSound(mSoundArrayList.get(mSoundIndex));
//				MediaPlayUtils.getInstance().playUrl(mSoundArrayList.subList(mSoundStartIndex, mSoundStartIndex+mPlaySoundCount));
			}
		}
	}
	
	public void playSound(String soundUrl)
	{
		MediaPlayUtils.getInstance().setHandler(mHandler);
		MediaPlayUtils.getInstance().playUrl(NetWorkUtils.baseUrl+soundUrl, true);
//		mMediaPlayUtils.playUrl(NetWorkUtils.baseUrl+soundUrl);	
	}
	
	private void playNextSound()
	{
		if(mIsCommentSound)
		{
			if(mSoundIndex < mCommentSoundArrayList.size())
			{
				playSound(mCommentSoundArrayList.get(mSoundIndex));
			}
			else
			{
				mIsAutoPlayNext = false;
				mSoundIndex = 0;
			}
		}
		else
		{
			if(mSoundIndex < mSoundArrayList.size())
			{
				if(mPlaySoundCount > 0 && mSoundIndex == mSoundStartIndex+mPlaySoundCount)
				{
					mPlaySoundCount = 0;
					mSoundStartIndex = 0;
					resetReadButton(mPlaySoundButtonId, true);								
					return;
				}
				else
				{
					scrollReadTextToTop(mSoundIndex);
//					loadUrl("javascript:getDivPosition('"+mSoundIndex+"')");	
					
				}							
				playSound(mSoundArrayList.get(mSoundIndex));
			}
			else
			{
				mIsAutoPlayNext = false;
				mSoundIndex = 0;
				textReadComplete();
			}
		}
	}
	
	private void sendMsgToParentHandler(int what)
	{
		if(mParentHandler != null)
		{
			mParentHandler.sendEmptyMessage(what);
		}
	}
	
	
	/**
	 * 停止播放声音
	 * 释放声音资源
	 */
	public void soundStop()
	{
		MediaPlayUtils.stop();
//		mMediaPlayUtils.stop();
	}
	
	private void fillSoundUrl()
	{
		if(mPrimarySections != null)
		{
			for (PrimarySection primarySection : mPrimarySections)
			{
				for (T t :  primarySection.getSectionT())
				{
					ArrayList<SPH> sphArrayList = t.getSph();
					for (SPH sph : sphArrayList)
					{
						String snd = sph.mSnd;
						if(!TextUtils.isEmpty(snd))
						{
							String[] sndPath = snd.split("\\|");
							for (int i = 0; i < sndPath.length; i++)
							{
								mSoundArrayList.add(sndPath[i]);
							}
							mSectionSpnSize.add(sndPath.length);
						}
					}
				}
			}
		}
	}
	
	/**
	 * 把诵读的文本滑动到顶部
	 * @param sectionId
	 */
	private void scrollReadTextToTop(final int soundIndex)
	{
		int size = 0;
		int index = 0;
		for (int i = 0; i < mSectionSpnSize.size(); i++)
		{
			size += mSectionSpnSize.get(i);
			if(size > soundIndex)
			{
				final int sectionIndex = i;
				this.post(new Runnable()
				{
					
					@Override
					public void run()
					{
//						loadUrl("javascript:scrollDivToTop('"+sectionId+"')");
						loadUrl("javascript:getDivPosition('"+sectionIndex+"')");
					}
				});
				break;
			}
		}			
	}
	
	/**
	 * 把诵读的文本框起来
	 * @param sectionId
	 */
	private void setReadTextToSign(final int id)
	{
		this.post(new Runnable()
		{			
			@Override
			public void run()
			{
				loadUrl("javascript:setReadSelect('"+id+"')");
			}
		});			
	}
	
	/**
	 * 把诵读的文本框起来
	 * @param sectionId
	 */
	private void removeReadTextToSign()
	{
		setKeepScreenOn(false);
		this.post(new Runnable()
		{			
			@Override
			public void run()
			{
				loadUrl("javascript:removeReadSelect()");
			}
		});			
	}
	
	/**
	 * 
	 * @param readButtonId
	 * @param isSendMsg
	 */
	private void resetReadButton(final String readButtonId, boolean isSendMsg)
	{
		if(!TextUtils.isEmpty(readButtonId))
		{
			this.post(new Runnable()
			{
				
				@Override
				public void run()
				{
					loadUrl("javascript:resetReadButton('"+readButtonId+"')");
				}
			});
		}
		if(isSendMsg)
		{
			sendMsgToParentHandler(RESET_READ_BTN_MSG);
		}
		removeReadTextToSign();
	}
	
	/**
	 * 整篇课文朗读完成
	 */
	private void textReadComplete()
	{
		removeReadTextToSign();
		sendMsgToParentHandler(SOUND_PLAY_COMPLETION_MSG);
	}
	
	private void fillCommentSoundUrl(String soundUrl)
	{
		String[] sndPath = soundUrl.split("\\|");
		for (int i = 0; i < sndPath.length; i++)
		{
			mCommentSoundArrayList.add(sndPath[i]);
		}
		mIsCommentSound = true;
		mSoundIndex = 0;
		if(MediaPlayUtils.isPlaying())
		{
			MediaPlayUtils.pause();
		}
		sendMsgToParentHandler(RESET_READ_BTN_MSG);
		Toast.makeText(mContext, R.string.sound_loading, Toast.LENGTH_SHORT).show();
		playSound(mCommentSoundArrayList.get(0));
	}
	
//	@SuppressWarnings("unchecked")
	private void loadView(String content)
	{
//		DebugLogger.getLogger().d(content);
		mSoundArrayList.clear();
		mPrimarySections = new PrimaryContentParser().xmlPullParseSection(content);
//		fillSoundUrl();
		HashMap<String, Object> sectionHashMap = MyHtml.PrimarySectionTohtml(mPrimarySections);
		String htmlString = sectionHashMap.get("html").toString();
		mSoundArrayList = (ArrayList<String>) sectionHashMap.get("sound");
		mSectionSpnSize = (ArrayList<Integer>) sectionHashMap.get("soundSize");
		DebugLogger.getLogger().d(htmlString);
		loadDataWithBaseURL(NetWorkUtils.baseUrl, htmlString,"text/html", "UTF-8", "");
	}
	
	public void loadData(String html)
	{
		loadDataWithBaseURL(NetWorkUtils.baseUrl, html, "text/html", "UTF-8", "");
	}
	
	public float getDeviceScale()
	{
		return MyApplication.mDeviceScale;
	}
	
	public void setOnExternalFragmentListener(OnShowExternalFragmentListener listener){
		mExternalFragmentListener = listener;
	}
	
	public void scrollPositionToTop(String position)
	{
		String[] positions = position.split(";");
		if(positions.length > 0)
		{
			final int[] postion = new int[2];
			getLocationInWindow(postion);
			DebugLogger.getLogger().d("y="+(postion[1])+"x="+postion[0]);
			getLocationOnScreen(postion);
			ViewParent parentView = getParent();
			while (true)
			{
				if(parentView == null || parentView instanceof ScrollView)
				{
//					DebugLogger.getLogger().d("parent="+parentView==null? "":parentView.toString());
					break;
				}
				parentView = parentView.getParent();		
				
			}
			if(parentView != null)
			{
				ScrollView rootView = ((ScrollView)parentView);
				int y = postion[1];
				rootView.getLocationInWindow(postion);
				int padding = y - postion[0];
				DebugLogger.getLogger().d("padding="+padding);
				if(rootView != null)
				{
					int count = rootView.getChildCount();
					int height = 0;
					for(int i=0; i<count; i++)
					{
						View view = rootView.getChildAt(i);
						height += view.getHeight();
					}
					try
					{
						int divOffset = (int) (Float.parseFloat(positions[0])*MyApplication.mDeviceScale);
//						listView.scrollTo(0, postion[1]+divOffset+listView.getScrollY());
						int childTop = ((View)getParent()).getTop() + getTop() + divOffset;	
						int divHeight = (int) (Float.parseFloat(positions[1])*MyApplication.mDeviceScale);
						int childbottom = childTop + divHeight;
						int scrollY = rootView.getScrollY();
						int scrollHeight = rootView.getHeight() ;
						int cTB = childbottom - scrollY;
						int offset = (MyApplication.mDeviceScale*20 > 30? 40:20);
						
						if ( cTB <= offset ) {
							// view的下边界超出了ListView的上边界
//							listView.scrollListBy(childTop - Util.dip2px(getContext(), 5));
							rootView.smoothScrollTo(0, childTop - offset - 60);
						}else if ( cTB > 0 ) {
							if ( cTB - divHeight < offset ) {
								// view的上边界超出了ListView的上边界，但下边界不超出
//								listView.scrollListBy(childTop - Util.dip2px(getContext(), 5));
								rootView.smoothScrollTo(0, childTop - offset - 60);
							}else if( cTB - divHeight >= 0 && cTB < scrollHeight ){
								// view的上下边界在ListView的可见范围内
							}else {
								// view的下边界超出了ListView的下边界(不管view的上边界有没有超出ListView的下边界)
//								listView.scrollListBy(childbottom - scrollHeight + Util.dip2px(getContext(), 5));
								rootView.smoothScrollTo(0, childbottom - scrollHeight + offset);
							}
						}
//						listView.scrollTo(0, childTop);
//						if(childTop != mLastOffset)
//						{
//							rootView.smoothScrollBy(childTop-mLastOffset, 200);
//							mLastOffset = childTop;
//						}
						DebugLogger.getLogger().d("x="+getScrollX()+"y="+(childTop-scrollY)+"height="+positions[1]);
					}
					catch(NumberFormatException numberFormatException)
					{
		
					}				
				}
			}
		}
	}
}
