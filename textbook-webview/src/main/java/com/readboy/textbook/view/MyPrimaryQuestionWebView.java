package com.readboy.textbook.view;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

import com.dream.textbook.external.OnShowExternalFragmentListener;
import com.readboy.textbook.model.MyHtml;
import com.readboy.textbook.model.PrimaryQuestion;
import com.readboy.textbook.model.PrimaryQuestion.Item;
import com.readboy.textbook.util.DebugLogger;
import com.readboy.textbook.util.MyApplication;
import com.readboy.textbook.util.NetWorkUtils;
import com.readboy.textbook.util.PopupDialog;
import com.readboy.textbook.util.PrimaryQuestionUtils;
import com.readboy.textbook.util.TipInfo;
import com.readboy.textbookwebview.PrimaryQuestionJavaScriptObject;
import com.readboy.textbookwebview.R;
import com.readboy.textbookwebview.WebViewClientImpl;

public class MyPrimaryQuestionWebView extends MyBaseQuestionWebView
{
	private PrimaryQuestionJavaScriptObject mJavaScriptObject;
	private String mCurrentQuestionId;
	private int mCurrentQuestionIndex = 0;
	private PrimaryQuestion mQuestion = null;
	private int mQuestionCount;
	private PrimaryQuestion.Item mQuestionItem = null;
	private boolean mIsShowAnswerAndExplain = false;
	private boolean mIsSelfRating = false;
	private long mAnswerStartTime = 0;
	private long mAnswerEndTime = 0;
	private Handler mHandler = null;
	private boolean mIsSubsidiaryBook = false;
	private WebView mDetailWebView = null;
	protected OnShowExternalFragmentListener mExternalFragmentListener = null;

	public MyPrimaryQuestionWebView(Context context)
	{
		super(context);
		init();
	}
	
	public MyPrimaryQuestionWebView(Context context, AttributeSet attrs)
	{
		super(context, attrs, 0);
		init();
	}
	
	public MyPrimaryQuestionWebView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}
	
	private void init()
	{
		mJavaScriptObject = new PrimaryQuestionJavaScriptObject(getContext(), this);
		addJavascriptInterface(mJavaScriptObject, "JavaScriptObject");
		
		setWebViewClient(new WebViewClientImpl(getContext()));
		
		setFocusableInTouchMode(true);
	}
	
	private void makeHtmlQuestion(final PrimaryQuestion.Item item)
	{
		String questionHtmlStr = PrimaryQuestionUtils.makeOneQuestionHtml(item, mIsShowAnswerAndExplain, mIsSelfRating);
		DebugLogger.getLogger().d(questionHtmlStr);
		loadDataWithBaseURL(NetWorkUtils.baseUrl, questionHtmlStr,"text/html", "UTF-8", "");
		mQuestionItem = item;
		mAnswerStartTime = System.currentTimeMillis();
	}
	
	public void nextQuestion()
	{
		mCurrentQuestionIndex ++;
		if(mCurrentQuestionIndex > mQuestionCount-1)
		{
			mCurrentQuestionIndex = mQuestionCount - 1;
			return;
		}
		mCurrentQuestionId = PrimaryQuestion.getInstance().getQuestionId(mCurrentQuestionIndex);
		Item item = PrimaryQuestion.getInstance().getQuestion(mCurrentQuestionId);
		if(item != null)
		{
			makeHtmlQuestion(item);
		}
	}
	
	public void previousQuestion()
	{
		mCurrentQuestionIndex --;
		if(mCurrentQuestionIndex < 0)
		{
			mCurrentQuestionIndex = 0;
			return;
		}
		mCurrentQuestionId = PrimaryQuestion.getInstance().getQuestionId(mCurrentQuestionIndex);
		Item item = PrimaryQuestion.getInstance().getQuestion(mCurrentQuestionId);
		if(item != null)
		{
			makeHtmlQuestion(item);
		}
		
	}
	
	public void gotoQuestion(String questionId)
	{
		makeHtmlQuestion(PrimaryQuestion.getInstance().getQuestion(questionId));
	}
	
	public String getCurrentQuestionId()
	{
		return mCurrentQuestionId;
	}
	
	public int getQuestionCount()
	{
		return PrimaryQuestion.getInstance().getQuestionCount();
	}
	
	/**
	 * set教材题目
	 * @param jsonArray 题目jsonArray
	 * @param showAnswerAndExplain 是否显示答案和解析
	 */
	public void setQuestion(JSONArray jsonArray, boolean showAnswerAndExplain)
	{
		mQuestion = new PrimaryQuestion();
		mQuestionCount = jsonArray.length();
		String questionHtmlStr = TipInfo.EMPTY_CONTENT_TIP;
		if(mQuestionCount > 0)
		{
			for (int i = 0; i < mQuestionCount; i++)
			{
				JSONObject jsonObject = jsonArray.optJSONObject(i);
				JSONArray exercise = jsonObject.optJSONArray("exercise");
				String title = jsonObject.optString("name");
				if(exercise == null)
				{
					mQuestion.setQuestion(jsonArray);
				}
				else
				{
					if(TextUtils.isEmpty(title))
					{
						mQuestion.addQuestion(exercise);
					}
					else
					{
						mQuestion.addQuestion(exercise, title);
					}
				}
			}			
			mIsShowAnswerAndExplain = showAnswerAndExplain;
			questionHtmlStr = PrimaryQuestionUtils.makeQuestionHtml(mQuestion, mIsShowAnswerAndExplain, mIsSelfRating);
		}
		DebugLogger.getLogger().d(jsonArray.toString());
		DebugLogger.getLogger().d(questionHtmlStr);
		loadDataWithBaseURL(NetWorkUtils.baseUrl, questionHtmlStr,"text/html", "UTF-8", "");		
	}
	
	/**
	 * set教辅题目
	 * @param jsonArray 题目jsonArray
	 */
	public void setSubsidiaryBookQuestion(JSONArray jsonArray)
	{
		mIsSubsidiaryBook = true;
		mQuestion = new PrimaryQuestion();
		mQuestionCount = jsonArray.length();
		String questionHtmlStr = TipInfo.EMPTY_CONTENT_TIP;
		if(mQuestionCount > 0)
		{
			for (int i = 0; i < mQuestionCount; i++)
			{
				JSONObject jsonObject = jsonArray.optJSONObject(i);
				JSONArray exercise = jsonObject.optJSONArray("exercise");
				String title = jsonObject.optString("name");
				if(exercise == null)
				{
					mQuestion.setQuestion(jsonArray);
				}
				else
				{
					if(TextUtils.isEmpty(title))
					{
						mQuestion.addQuestion(exercise);
					}
					else
					{
						mQuestion.addQuestion(exercise, title);
					}
				}
			}			
			questionHtmlStr = PrimaryQuestionUtils.makeSubsidiaryBookHtml(mQuestion);
		}
		DebugLogger.getLogger().d(jsonArray.toString());
		DebugLogger.getLogger().d(questionHtmlStr);
		loadDataWithBaseURL(NetWorkUtils.baseUrl, questionHtmlStr,"text/html", "UTF-8", "");		
	}
	
	public void setQuestionCount(int count)
	{
		mQuestionCount = count;
	}
	
	/**
	 * 加载一道题目
	 * @param item
	 */
	public boolean loadData(PrimaryQuestion.Item item)
	{
		if(item != null)
		{
			mCurrentQuestionId = item.getId();
			mCurrentQuestionIndex = item.getQuestionIndex();
			mIsShowAnswerAndExplain = false;
			mIsSelfRating = false;
			makeHtmlQuestion(item);
			return true;
		}
		return false;
	}
	
	/**
	 * 加载一道题目(题目作答提交后可以显示正确答案和解析)
	 * @param item 题目item
	 * @param showAnswerAndExplain 是否显示答案和解析
	 * @return true/false
	 */
	public boolean loadData(PrimaryQuestion.Item item, boolean showAnswerAndExplain)
	{
		if(item != null)
		{
			mCurrentQuestionId = item.getId();
			mCurrentQuestionIndex = item.getQuestionIndex();
			mIsShowAnswerAndExplain = showAnswerAndExplain;
			mIsSelfRating = false;
			makeHtmlQuestion(item);
			return true;
		}
		return false;
	}
	
	/**
	 * 加载一道题目(显示自评分)
	 * @param item
	 * @param showAnswerAndExplain 是否显示答案和解析
	 * @param selfRating 是否是自评分
	 * @return true/false
	 */
	public boolean loadData(PrimaryQuestion.Item item, boolean showAnswerAndExplain, boolean selfRating)
	{
		if(item != null)
		{
			mCurrentQuestionId = item.getId();
			mCurrentQuestionIndex = item.getQuestionIndex();
			mIsShowAnswerAndExplain = showAnswerAndExplain;
			mIsSelfRating = selfRating;
			makeHtmlQuestion(item);
			return true;
		}
		return false;
	}
	
	/**
	 * 加载一道题目(显示自评分)
	 * @param questionId 题目Id
	 * @param showAnswerAndExplain 是否显示答案和解析
	 * @param selfRating 是否是自评分
	 * @return true/false
	 */
	public boolean loadData(String questionId, boolean showAnswerAndExplain, boolean selfRating)
	{
		Item item = PrimaryQuestion.getInstance().getQuestion(questionId);
		if(item != null)
		{
			mCurrentQuestionId = questionId;
			mCurrentQuestionIndex = item.getQuestionIndex();
			mIsShowAnswerAndExplain = showAnswerAndExplain;
			mIsSelfRating = selfRating;
			makeHtmlQuestion(item);
			return true;
		}
		return false;

	}
	
	public void setUserAnswer(String questionId, String userAnswer)
	{
		Item item = PrimaryQuestion.getInstance().getQuestion(questionId);
		if(item != null)
		{
			item.setUserAnswer(userAnswer);
			mAnswerEndTime = System.currentTimeMillis();
			item.addAnswerTime(mAnswerEndTime-mAnswerStartTime);
			mAnswerStartTime = System.currentTimeMillis();
		}
	}

	public void setUserAnswer(String[] questionId, String[] userAnswer)
	{
		int length = questionId.length;
		mAnswerEndTime = System.currentTimeMillis();
		DebugLogger.getLogger().d("time="+System.currentTimeMillis());
		for (int i=0; i<length; i++)
		{
			Item item = PrimaryQuestion.getInstance().getQuestion(questionId[i]);
			if(item != null)
			{
				mAnswerEndTime = System.currentTimeMillis();
				item.addUserAnswer(userAnswer[i], i);
				item.addAnswerTime(mAnswerEndTime-mAnswerStartTime);
				mAnswerStartTime = System.currentTimeMillis();
			}
		}
		if(mHandler != null)
		{
			mHandler.sendEmptyMessage(MSG_ANSWER_SET_FINISH);
		}
		DebugLogger.getLogger().d("MyQuestion time="+System.currentTimeMillis());
	}
	
	public void setBlankUserAnswer(String questionId, String userAnswer)
	{		
		if(mQuestionItem != null)
		{
			if(questionId != null)
			{
				String[] ids = questionId.split("-");
				if (ids.length == 2)
				{
					int questionIndex = Integer.parseInt(ids[1]);
					mQuestionItem.setBlankUserAnswer(questionIndex, userAnswer);
				}
			}
		}
		else if(mQuestion != null)
		{
			mQuestion.setBlankUserAnswer(questionId, userAnswer);
		}
	}
	
	/**
	 * 显示答案和解析
	 */
	public void showAnswerAndExplain()
	{
		if(mQuestionItem != null)
		{
			mIsShowAnswerAndExplain =true;
			makeHtmlQuestion(mQuestionItem);
		}
	}
	
	/**
	 * 调用JavaScript去设置答案
	 */
	public void setUserAnswerCallJavaScript()
	{
		if(mQuestionItem != null)
		{
			String type = mQuestionItem.getType();
			if(type == "101" || type == "102" || type == "103")
			{
				return;
			}
		}
		loadUrl("javascript:setUserAnswer()");		
	}
	
	/**
	 * 调用JavaScript去设置答案
	 */
	public void setUserAnswerCallJavaScript(Handler handler)
	{
		if(mQuestionItem != null)
		{
			String type = mQuestionItem.getType();
			if("101".equals(type) ||  "102".equals(type) || "103".equals(type))
			{
				handler.sendEmptyMessage(MSG_ANSWER_SET_FINISH);
				return;
			}
		}
		mHandler = handler;
		loadUrl("javascript:setUserAnswer()");		
	}

	/**
	 * 获取大题下面的小题Id
	 * @param parentId 大题Id
	 * @param index 大题内的题号(从0开始)
	 * @return
	 */
	public String getChildrenQuestionId(String parentId, String index)
	{
		Item item = PrimaryQuestion.getInstance().getQuestion(parentId);
		if(item != null)
		{
			int idIndex = Integer.parseInt(index.trim());
			return item.getChildrenQuestionId().get(idIndex-1);
		}
		return null;
	}

	/**
	 * set 自评
	 * @param id
	 */
	public void setSelfRating(String id)
	{
		if(id != null)
		{
			String[] ids = id.split("-");
			if(ids.length == 3)
			{
				Item item = PrimaryQuestion.getInstance().getQuestion(ids[0]);
				if(item != null)
				{
					item.setSelfRating("right".equalsIgnoreCase(ids[2]));
				}
			}
			if(mHandler != null)
			{
				mHandler.sendMessage(mHandler.obtainMessage(MSG_SELFRATING_SET_FINISH, ids[0]));
			}
		}
	}
	
	public void setHandler(Handler handler)
	{
		mHandler = handler;
	}

	public float getDeviceScale()
	{
		return MyApplication.mDeviceScale;
	}

	/**
	 * 弹出题目答案解析
	 * @param id
	 */
	public void showAnswer(final String id)
	{
		this.post(new Runnable()
		{

			@Override
			public void run()
			{
				Item item = mQuestion.getQuestion(id);
				if(item != null && (mIsSubsidiaryBook || MyApplication.getDeviceMode() == MyApplication.READBOY_PAD))
				{
					showQuestionDetail(item);
//					loadDataWithBaseURL(NetWorkUtils.baseUrl, PrimaryQustionUtils.makeQuestionDetailHtml(item), "text/html; charset=UTF-8", "UTF-8", "");
					return;
				}
				WebView answerWebView = new WebView(getContext());
				initWebViewSetting(answerWebView);
				answerWebView.addJavascriptInterface(mJavaScriptObject, "JavaScriptObject");
				String data = makeAnswerHtml(id);
				answerWebView.loadDataWithBaseURL(NetWorkUtils.baseUrl, data, "text/html", "UTF-8", "");
				if (mExternalFragmentListener != null)
				{
					mExternalFragmentListener.onShowView(answerWebView);
				}
				else
				{
					PopupDialog popupDialog = PopupDialog.newInstance(answerWebView);
					popupDialog.show((Activity)getContext(), PopupDialog.TAG);
				}
			}
		});
	}
	
	private String makeAnswerHtml(String id)
	{
		Item item = mQuestion.getQuestion(id);
		StringBuilder questionHtmlBuilder = new StringBuilder();
		makeAnswerHtmlHead(questionHtmlBuilder);
		if(item != null)
		{
			makeAnswerContent(item, questionHtmlBuilder);
		}
		else
		{
			makeEmptyContentTip(questionHtmlBuilder);
		}
		questionHtmlBuilder.append(MyHtml.HTML_END);
		return questionHtmlBuilder.toString();
	}

	/**
	 * 什么都没有空空如也(人生有太多的不完美，偶尔发生的或许不是你所期待的;但是是你必须接受的。)
	 * @param questionHtmlBuilder
	 */
	private void makeEmptyContentTip(StringBuilder questionHtmlBuilder) {
		questionHtmlBuilder.append("<p>");
		questionHtmlBuilder.append(TipInfo.EMPTY_CONTENT_TIP);
		questionHtmlBuilder.append("</p>");
	}

	/**
	 * 答案、解析
	 * @param item
	 * @param questionHtmlBuilder
	 */
	private void makeAnswerContent(Item item, StringBuilder questionHtmlBuilder) {
		questionHtmlBuilder.append("<p>");
		questionHtmlBuilder.append(MyHtml.SPAN_BLUE_START_TAG);
		String step = item.getStep();
		if(step != "")
		{
			questionHtmlBuilder.append("解答过程</span><br />");
			questionHtmlBuilder.append(item.getStep());
			questionHtmlBuilder.append("</p>");
			questionHtmlBuilder.append("<p>");
			questionHtmlBuilder.append(MyHtml.SPAN_BLUE_START_TAG);
		}
        questionHtmlBuilder.append("答案</span><br />");
        questionHtmlBuilder.append(item.getAnswer());
		questionHtmlBuilder.append("</p>");
		String solution = item.getSolution();
		if(solution != "")
		{
			questionHtmlBuilder.append("<p>");
			questionHtmlBuilder.append(MyHtml.SPAN_BLUE_START_TAG);
			questionHtmlBuilder.append("解析</span><br />");
			questionHtmlBuilder.append(solution);
			questionHtmlBuilder.append("</p>");
		}
	}

	/**
	 * html头
	 * @param questionHtmlBuilder
	 */
	private void makeAnswerHtmlHead(StringBuilder questionHtmlBuilder) 
	{
		questionHtmlBuilder.append(MyHtml.HTML_HEAD);
		questionHtmlBuilder.append(MyHtml.JQUERY);
		questionHtmlBuilder.append(MyHtml.makeExplainCss());
		questionHtmlBuilder.append(MyHtml.EXPLAIN_JS);
		questionHtmlBuilder.append(MyHtml.MATHJAX_JS);
		questionHtmlBuilder.append(MyHtml.HTML_HEAD_END);
	}
	
	/**
	 * 点击习题，跳到这里(就是一个全屏的popupwindow)
	 * 然后就是actionbar 部分是透明的，造成一个假像以为是一个activity
	 * 一个光鲜亮丽的背后隐藏着无限的可能，其实我们看到的仅仅是一个伪装的世界....
	 * @param questionId
	 */
	public void showQuestionDetail(Item item)
	{		
//		WebView detailWebView = new WebView(getContext());
		if(mExternalFragmentListener != null)
		{
			mDetailWebView =  new WebView(getContext());
			initWebViewSetting(mDetailWebView);
			mDetailWebView.addJavascriptInterface(mJavaScriptObject, "JavaScriptObject");
			mDetailWebView.loadDataWithBaseURL(NetWorkUtils.baseUrl, PrimaryQuestionUtils.makeQuestionDetailHtml(mQuestion, item), "text/html", "UTF-8", "");		
			mExternalFragmentListener.onShowPasswordManager(mDetailWebView);
			return;
		}
		LinearLayout layout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.question_detail, null);
		mDetailWebView = (WebView) layout.findViewById(R.id.question_webview);
		initWebViewSetting(mDetailWebView);
		mDetailWebView.addJavascriptInterface(mJavaScriptObject, "JavaScriptObject");
		mDetailWebView.loadDataWithBaseURL(NetWorkUtils.baseUrl, PrimaryQuestionUtils.makeQuestionDetailHtml(mQuestion, item), "text/html", "UTF-8", "");		
		final PopupWindow popupWindow = new PopupWindow(layout, android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT);
		popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		popupWindow.setOutsideTouchable(false);
		popupWindow.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss() {
				mDetailWebView = null;
			}
		});
		layout.findViewById(R.id.question_detail_back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
				mDetailWebView = null;
			}
		});
//			Activity activity = (Activity) getContext();
//			View view = activity.getWindow().getDecorView().findViewWithTag("toolbar");
//			Rect frame = new Rect();
//			activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
//			int statusBarHeight = frame.top;
//			int height = 0;
//			if(view != null)
//			{
//				height = view.getHeight() + statusBarHeight;
//			}			
		popupWindow.setFocusable(true);
//			popupWindow.showAsDropDown(view);
		popupWindow.showAtLocation(this, Gravity.TOP, 0, 0);
		
	}
	
	public void startParentPasswordActivity()
	{
		if(mExternalFragmentListener != null)
		{
			mExternalFragmentListener.onClickView(OnShowExternalFragmentListener.PASSWORD_MANAGER, "");
		}
	}
	
	public void showAnswerAftenCheckPassword()
	{
		if(MyApplication.mCanSeenAnswer)
		{
			if(mDetailWebView != null)
			{
				mDetailWebView.loadUrl("javascript:showAnswer()");
			}
		}
	}
	
	public void setOnExternalFragmentListener(OnShowExternalFragmentListener listener){
		mExternalFragmentListener = listener;
	}
	
}