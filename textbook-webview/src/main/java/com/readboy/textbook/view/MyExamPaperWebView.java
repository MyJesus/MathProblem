package com.readboy.textbook.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.dream.synclearning.paper.Children;
import com.dream.synclearning.paper.PaperContent;
import com.dream.synclearning.paper.PaperHistory;
import com.readboy.textbook.model.MyHtml;
import com.readboy.textbook.model.Question;
import com.readboy.textbook.model.Question.Item;
import com.readboy.textbook.util.DebugLogger;
import com.readboy.textbook.util.NetWorkUtils;
import com.readboy.textbook.util.QuestionUtils;
import com.readboy.textbookwebview.ExamPaperJavaScriptObject;
import com.readboy.textbookwebview.R;
import com.readboy.textbookwebview.WebViewClientImpl;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MyExamPaperWebView extends WebView implements OnClickListener {
    /**
     * 最后一道题作答完成
     */
    public static final int MSG_ANSWER_SET_FINISH = 1000;
    /**
     * 自评消息
     */
    public static final int MSG_SELFRATING_SET = 1100;
    /**
     * 自评分数消息(arg1:分数, obj:题目id(string))
     */
    public static final int MSG_SELFRATING_SCORE = 1200;
    /**
     * 微视频消息(obj:知识点Id)
     */
    public static final int MSG_MICO_VIDEO = 1300;
    /**
     * 相似题消息(obj:知识点Id)
     */
    public static final int MSG_SAME_TYPE_QUESTION = 1400;
    /**
     * 内容加载完成
     */
    public static final int MSG_CONTENT_LOAD_FINISH = 1500;

    /**
     * 答题数刷新
     */
    public static final int MSG_UPDATE_ANSWEREDCOUNT = 1600;
    private ExamPaperJavaScriptObject mJavaScriptObject;
    private String mCurrentQuestionId;
    private int mCurrentQuestionIndex = 0;
    private Question mQuestion = null;
    private int mQuestionCount;
    private boolean mIsShowAnswerAndExplain = false;
    private boolean mIsSelfRating = false;
    private long mAnswerStartTime = 0;
    private long mAnswerEndTime = 0;
    private Handler mHandler = null;
    private Context mContext;
    PopupWindow mSelfRatingPopupWindow = null;
    TextView mSelfRatingScore = null;
    String mSelfRatingQuestionId;
    ArrayList<Integer> mQuestionOffsetList = new ArrayList<Integer>();
    ProgressDialog mProgressBar;

    private int lastOffset;

    public MyExamPaperWebView(Context context) {
        super(context);
        init();
        mContext = context;
    }

    public MyExamPaperWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        mContext = context;
    }

    public MyExamPaperWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        mContext = context;
    }

    private void init() {
        WebSettings ws = getSettings();
        ws.setCacheMode(WebSettings.LOAD_NO_CACHE);// LOAD_CACHE_ELSE_NETWORK
        ws.setDatabaseEnabled(true);
        ws.setDomStorageEnabled(true);
        ws.setJavaScriptEnabled(true);
        ws.setAllowFileAccess(true);
        ws.setBuiltInZoomControls(true);
        ws.setSupportZoom(true);
        ws.setDefaultTextEncodingName("utf-8");
        ws.setBuiltInZoomControls(false);
//		WebView.setWebContentsDebuggingEnabled(true);
        ws.setUseWideViewPort(true);
        ws.setLoadWithOverviewMode(true);
        setHorizontalScrollBarEnabled(false);
        setHorizontalScrollbarOverlay(false);
        ws.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        ws.setAppCacheEnabled(true);

        mJavaScriptObject = new ExamPaperJavaScriptObject(getContext(), this);
        addJavascriptInterface(mJavaScriptObject, "JavaScriptObject");

        setWebViewClient(new WebViewClientImpl(getContext()));

        setFocusableInTouchMode(true);
        setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    if (mProgressBar != null) {
                        mProgressBar.dismiss();
                        mProgressBar = null;
                    } else {
                        if (mHandler != null) {
                            mHandler.sendEmptyMessage(MSG_CONTENT_LOAD_FINISH);
                        }
                    }

                }
            }

        });
    }

    private void makeHtmlQuestion(final Question.Item item) {
        String questionHtmlStr = QuestionUtils.makeOneQuestionHtml(item, mIsShowAnswerAndExplain, mIsSelfRating);
        DebugLogger.getLogger().d(questionHtmlStr);
        loadDataWithBaseURL(NetWorkUtils.baseUrl, questionHtmlStr, "text/html", "UTF-8", "");
        mAnswerStartTime = System.currentTimeMillis();
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        lastOffset = t;
    }

    public int getLastScrollOffset() {
        return lastOffset;
    }

    public void nextQuestion() {
        mCurrentQuestionIndex++;
        if (mCurrentQuestionIndex > mQuestionCount - 1) {
            mCurrentQuestionIndex = mQuestionCount - 1;
            return;
        }
        mCurrentQuestionId = Question.getInstance().getQuestionId(mCurrentQuestionIndex);
        Item item = Question.getInstance().getQuestion(mCurrentQuestionId);
        if (item != null) {
            makeHtmlQuestion(item);
        }
    }

    public void previousQuestion() {
        mCurrentQuestionIndex--;
        if (mCurrentQuestionIndex < 0) {
            mCurrentQuestionIndex = 0;
            return;
        }
        mCurrentQuestionId = Question.getInstance().getQuestionId(mCurrentQuestionIndex);
        Item item = Question.getInstance().getQuestion(mCurrentQuestionId);
        if (item != null) {
            makeHtmlQuestion(item);
        }

    }

    public void gotoQuestion(String questionId) {
        makeHtmlQuestion(Question.getInstance().getQuestion(questionId));
    }

    public String getCurrentQuestionId() {
        return mCurrentQuestionId;
    }

    public int getQuestionCount() {
        return Question.getInstance().getQuestionCount();
    }

    public void setQuestion(JSONArray jsonArray) {
        mQuestion = Question.getInstance();
        mQuestionCount = jsonArray.length();
        mQuestion.setQuestion(jsonArray);
        String questionHtmlStr = QuestionUtils.makeQuestionHtml(mQuestion, mIsShowAnswerAndExplain, mIsSelfRating);
        DebugLogger.getLogger().d(questionHtmlStr);
        loadDataWithBaseURL(NetWorkUtils.baseUrl, questionHtmlStr, "text/html", "UTF-8", "");
    }

    /**
     * 显示jsonArry中的试卷
     *
     * @param jsonArray
     * @param childrens
     * @param submit    提交标记
     */
    public void setQuestion(JSONArray jsonArray, PaperContent paperContent, boolean submit) {
        if (mQuestion == null) {
            mQuestion = Question.getInstance();
//			mQuestionCount = jsonArray.length();
//			mQuestion.setQuestion(jsonArray);
        }
        //4.4 让输入框失去焦点
        if (submit) {
            setVisibility(View.GONE);
            setVisibility(View.VISIBLE);
            try {
                if (mProgressBar == null) {
                    mProgressBar = new ProgressDialog(mContext);
                    mProgressBar.setMax(100);
                    mProgressBar.setMessage("加载中....");
                    mProgressBar.setCancelable(false);
                    mProgressBar.setProgress(0);
                }
                mProgressBar.show();
            } catch (Exception e) {
                mProgressBar = null;
            }
        } else {
            Question.getInstance().clearQuestion();
        }
        StringBuilder questionHtmlBuilder = new StringBuilder();
        QuestionUtils.showExplain(submit);
        QuestionUtils.setIsHistory(false);
        QuestionUtils.makeHtmlHead(questionHtmlBuilder, MyHtml.ShowType.EXAMPAPER);
        if (paperContent.childrenList != null) {
            QuestionUtils.setSubject(paperContent.subject);
            int questionOffset = 0;
            int index = 0;
            if (submit)
                Question.getInstance().resetSortQuestionAnswer();
            else {
                mQuestion.getQuestionIds().clear();
                mQuestion.getmRealQuestionIds().clear();
            }
            for (Children children : paperContent.childrenList) {
                questionHtmlBuilder.append("<div class='hd-title'>");
                questionHtmlBuilder.append(children.name);
                questionHtmlBuilder.append("</div>");
                int questionSize = 0;
                ArrayList<String> questionIds;
                if (submit) {
                    questionSize = mQuestionOffsetList.get(index);
                    questionIds = mQuestion.getQuestionIds();
                } else {
                    if (children.qstList != null) {
                        Question.getInstance().newTempMap();
                        for (JSONObject jsonObject : children.qstList) {
                            mQuestion.addQuestion(jsonObject);
                        }
                        Question.getInstance().addSortQuestion();
                    }
                    questionIds = mQuestion.getQuestionIds();
                    questionSize = questionIds.size();
                    mQuestionOffsetList.add(questionSize);
                }
                for (int i = questionOffset; i < questionSize; i++) {
                    Question.Item item = mQuestion.getQuestion(questionIds.get(i));
                    if (item != null && !item.isChildren()) {
                        questionHtmlBuilder.append("<fieldset class='select-field' id='fieldset-");
                        questionHtmlBuilder.append(item.getId());
                        questionHtmlBuilder.append("'>");
                        QuestionUtils.makeOneQuestion(item, questionHtmlBuilder);
                        questionHtmlBuilder.append("</fieldset>");
                    }

                }
                questionOffset = questionSize;
                index++;
            }
        }
//		String questionHtmlStr = QuestionUtils.makeQuestionHtml(mQuestion, mIsShowAnswerAndExplain, mIsSelfRating);
//		System.err.println("questionHtmlBuilder.toString() == " + questionHtmlBuilder.toString());
        DebugLogger.getLogger().d(questionHtmlBuilder.toString());
        loadDataWithBaseURL(NetWorkUtils.baseUrl, questionHtmlBuilder.toString(), "text/html", "UTF-8", "");
    }

    /**
     * 显示jsonArry中的试卷
     *
     * @param childrens
     * @param submit    提交标记
     */
    public void setQuestion(ArrayList<Children> childrens, boolean submit) {
        if (mQuestion == null) {
            mQuestion = Question.getInstance();
        }
        StringBuilder questionHtmlBuilder = new StringBuilder();
        QuestionUtils.showExplain(submit);
        QuestionUtils.setIsHistory(false);
        QuestionUtils.makeHtmlHead(questionHtmlBuilder, MyHtml.ShowType.EXAMPAPER);
        if (childrens != null) {
            int questionOffset = 0;
            for (Children children : childrens) {
                questionHtmlBuilder.append("<div class='hd-title'>");
                questionHtmlBuilder.append(children.name);
                questionHtmlBuilder.append("</div>");
                if (children.qstList != null) {
                    for (JSONObject jsonObject : children.qstList) {
                        mQuestion.addQuestion(jsonObject);
                    }
                }
                ArrayList<String> questionIds = mQuestion.getQuestionIds();
                int questionSize = questionIds.size();
                for (int i = questionOffset; i < questionSize; i++) {
                    Question.Item item = mQuestion.getQuestion(questionIds.get(i));
                    if (item != null && !item.isChildren()) {
                        questionHtmlBuilder.append("<fieldset class='select-field' id='fieldset-");
                        questionHtmlBuilder.append(item.getId());
                        questionHtmlBuilder.append("'><div style='position: relative;'>");
                        QuestionUtils.makeOneQuestion(item, questionHtmlBuilder);
                        questionHtmlBuilder.append("</div></fieldset>");
                    }

                }
                questionOffset = questionSize;

            }
        }
//		String questionHtmlStr = QuestionUtils.makeQuestionHtml(mQuestion, mIsShowAnswerAndExplain, mIsSelfRating);
        DebugLogger.getLogger().d(questionHtmlBuilder.toString());
        loadDataWithBaseURL(NetWorkUtils.baseUrl, questionHtmlBuilder.toString(), "text/html", "UTF-8", "");
    }

    /**
     * 显示jsonArry中的试卷
     *
     * @param childrens
     * @param paperHistory 历史记录
     */
    public void setQuestion(JSONArray jsonArray, PaperContent paperContent, PaperHistory paperHistory) {
        if (mQuestion == null) {
            mQuestion = Question.getInstance();
//			mQuestionCount = jsonArray.length();
//			mQuestion.setQuestion(jsonArray);
        }
        mQuestion.clearAll();
        StringBuilder questionHtmlBuilder = new StringBuilder();
        QuestionUtils.showExplain(true);
        QuestionUtils.setIsHistory(true);
        QuestionUtils.setSubject(paperHistory.subject);
        QuestionUtils.makeHtmlHead(questionHtmlBuilder, MyHtml.ShowType.EXAMPAPER);
        if (paperContent.childrenList != null) {
            int questionOffset = 0;
            for (Children children : paperContent.childrenList) {
                questionHtmlBuilder.append("<div class='hd-title'>" + children.name + "</div>");
                if (children.qstList != null) {
                    for (JSONObject jsonObject : children.qstList) {
                        mQuestion.addQuestion(jsonObject, paperHistory);
                    }
                }
                ArrayList<String> questionIds = mQuestion.getQuestionIds();
                int questionSize = questionIds.size();
                for (int i = questionOffset; i < questionSize; i++) {
                    Question.Item item = mQuestion.getQuestion(questionIds.get(i));
                    if (item != null && !item.isChildren()) {
                        questionHtmlBuilder.append("<fieldset class='select-field' id='fieldset" + item.getId() + "'>");
                        QuestionUtils.makeOneQuestion(item, questionHtmlBuilder);
                        questionHtmlBuilder.append("</fieldset>");
                    }

                }
                questionOffset = questionSize;

            }
        }
//		String questionHtmlStr = QuestionUtils.makeQuestionHtml(mQuestion, mIsShowAnswerAndExplain, mIsSelfRating);
        DebugLogger.getLogger().d(questionHtmlBuilder.toString());
        loadDataWithBaseURL(NetWorkUtils.baseUrl, questionHtmlBuilder.toString(), "text/html", "UTF-8", "");
    }

    public void setQuestionCount(int count) {
        mQuestionCount = count;
    }

    /**
     * 加载一道题目
     *
     * @param item
     */
    public void loadData() {
        String questionHtmlStr = QuestionUtils.makeQuestionHtml(Question.getInstance(), mIsShowAnswerAndExplain, mIsSelfRating);
        DebugLogger.getLogger().d(questionHtmlStr);
        loadDataWithBaseURL(NetWorkUtils.baseUrl, questionHtmlStr, "text/html", "UTF-8", "");
    }

    /**
     * 加载一道题目
     *
     * @param item
     */
    public boolean loadData(Question.Item item) {
        if (item != null) {
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
     *
     * @param item                 题目item
     * @param showAnswerAndExplain 是否显示答案和解析
     * @return true/false
     */
    public boolean loadData(Question.Item item, boolean showAnswerAndExplain) {
        if (item != null) {
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
     *
     * @param item
     * @param showAnswerAndExplain 是否显示答案和解析
     * @param selfRating           是否是自评分
     * @return true/false
     */
    public boolean loadData(Question.Item item, boolean showAnswerAndExplain, boolean selfRating) {
        if (item != null) {
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
     *
     * @param item
     * @param showAnswerAndExplain 是否显示答案和解析
     * @param selfRating           是否是自评分
     * @return true/false
     */
    public boolean loadData(String questionId, boolean showAnswerAndExplain, boolean selfRating) {
        Item item = Question.getInstance().getQuestion(questionId);
        if (item != null) {
            mCurrentQuestionId = questionId;
            mCurrentQuestionIndex = item.getQuestionIndex();
            mIsShowAnswerAndExplain = showAnswerAndExplain;
            mIsSelfRating = selfRating;
            makeHtmlQuestion(item);
            return true;
        }
        return false;

    }


    public void setUserAnswer(String questionId, String userAnswer) {
        Item item = Question.getInstance().getQuestion(questionId);
        if (item != null) {
            item.setUserAnswer(userAnswer);
            mAnswerEndTime = System.currentTimeMillis();
            item.addAnswerTime(mAnswerEndTime - mAnswerStartTime);
            mAnswerStartTime = System.currentTimeMillis();
            Question.getInstance().addSortQuestionAnswer(item, mHandler);
        }
    }

    public void setUserAnswer(String[] questionId, String[] userAnswer) {
        int length = questionId.length;
        mAnswerEndTime = System.currentTimeMillis();
        DebugLogger.getLogger().d("time=" + System.currentTimeMillis());
        for (int i = 0; i < length; i++) {
            Item item = Question.getInstance().getQuestion(questionId[i]);
            if (item != null) {
                mAnswerEndTime = System.currentTimeMillis();
                item.setUserAnswer(userAnswer[i], i);
                item.addAnswerTime(mAnswerEndTime - mAnswerStartTime);
                mAnswerStartTime = System.currentTimeMillis();
                Question.getInstance().addSortQuestionAnswer(item, mHandler);
            }
        }
        if (mHandler != null) {
            mHandler.sendEmptyMessage(MSG_ANSWER_SET_FINISH);
        }
        DebugLogger.getLogger().d("MyQuestion time=" + System.currentTimeMillis());
    }

    public void setBlankUserAnswer(String questionId, String userAnswer) {
        if (mQuestion != null) {
            if (questionId != null) {
                String[] ids = questionId.split("-");
                if (ids.length == 2) {
                    Item item = mQuestion.getQuestion(ids[0]);
                    if (item != null) {
                        int questionIndex = Integer.parseInt(ids[1]);
                        item.setUserAnswer(userAnswer, questionIndex);
                        Question.getInstance().addSortQuestionAnswer(item, mHandler);
                    }
                }
            }
        }
    }

    /**
     * 4.4 专用，因为javascript 监听不了退格键，所以加了失去焦点时检查一下
     *
     * @param questionId
     * @param userAnswer
     */
    public void setCheckUserAnswer(String questionId, String userAnswer) {
        if (mQuestion != null) {
            if (questionId != null) {
                String[] ids = questionId.split("-");
                if (ids.length == 2) {
                    Item item = mQuestion.getQuestion(ids[0]);
                    if (item != null) {
                        int questionIndex = Integer.parseInt(ids[1]);
                        if (questionIndex < item.getUserAnswer().length) {
                            item.setUserAnswer(userAnswer, questionIndex);
                        }

                    }
                }
            }
        }
    }

//	/**
//	 * 显示答案和解析
//	 */
//	public void showAnswerAndExplain()
//	{
//		if(mQuestionItem != null)
//		{
//			mIsShowAnswerAndExplain =true;
//			makeHtmlQuestion(mQuestionItem);
//		}
//	}
//	
//	/**
//	 * 调用JavaScript去设置答案
//	 */
//	public void setUserAnswerCallJavaScript()
//	{
//		if(mQuestionItem != null)
//		{
//			String type = mQuestionItem.getType();
//			if(type == "101" || type == "102" || type == "103")
//			{
//				return;
//			}
//		}
//		loadUrl("javascript:setUserAnswer()");		
//	}
//	
//	/**
//	 * 调用JavaScript去设置答案
//	 */
//	public void setUserAnswerCallJavaScript(Handler handler)
//	{
//		if(mQuestionItem != null)
//		{
//			String type = mQuestionItem.getType();
//			if(type == "101" || type == "102" || type == "103")
//			{
//				handler.sendEmptyMessage(MSG_ANSWER_SET_FINISH);
//				return;
//			}
//		}
//		mHandler = handler;
//		loadUrl("javascript:setUserAnswer()");		
//	}

    public String getChildrenQuestionId(String parentId, String index) {
        Item item = Question.getInstance().getQuestion(parentId);
        if (item != null) {
            int idIndex = Integer.parseInt(index.trim());
            return item.getChildrenQuestionId().get(idIndex - 1);
        }
        return null;
    }

    /**
     * 滑动指定题目到顶部
     *
     * @param sectionId
     */
    public void scrollQuesttionToTop(String questionId) {
        loadUrl("javascript:scrollDivToTop('" + questionId + "')");
    }

    /**
     * set 自评
     *
     * @param id
     */
    public void setSelfRating(String id) {
        if (id != null) {
            String[] ids = id.split("-");
            if (ids.length == 3) {
                Item item = Question.getInstance().getQuestion(ids[0]);
                if (item != null) {
                    item.setSelfRating("right".equalsIgnoreCase(ids[2]));
                }
            }
            if (mHandler != null) {
                mHandler.sendMessage(mHandler.obtainMessage(MSG_SELFRATING_SET, ids[0]));
            }
        }
    }

    /**
     * 最后一道题作答完成 MSG_ANSWER_SET_FINISH = 1000;
     * 自评消息 MSG_SELFRATING_SET_FINISH = 1100;
     * 自评分数消息(arg1:分数, obj:题目id(string))MSG_SELFRATING_SCORE_FINISH = 1200;
     *
     * @param handler
     */
    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    public float getDeviceScale() {
        return getResources().getDisplayMetrics().density;
    }

    /**
     * 自评分小窗口
     *
     * @param id
     */
    public void showSelfRatingPlan(String id, String isRight) {
        Item item = Question.getInstance().getQuestion(id);
        if (item != null) {
            mSelfRatingQuestionId = id;
            int selfRatingScore = 0;
            if (isRight.equals("1")) {
                selfRatingScore = item.getScore() == 0 ? 1 : item.getScore();
            }
            item.setSelfRatingScore(selfRatingScore);
            item.setIsSelfRatinged(true);
            Question.getInstance().setSortQusetionAnswerStatus(item.getId(), true);
            if (mHandler != null) {
                Message message = mHandler.obtainMessage();
                message.what = MSG_SELFRATING_SCORE;
                message.arg1 = selfRatingScore;
                message.obj = mSelfRatingQuestionId;
                mHandler.sendMessage(message);
            }
        }
        /*
        if(item != null)
		{
			mSelfRatingQuestionId = id;
			if(mSelfRatingPopupWindow == null)
			{
				View view = LayoutInflater.from(mContext).inflate(R.layout.self_rating_plan, null); 
				mSelfRatingPopupWindow = new PopupWindow(view, android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT);
				mSelfRatingPopupWindow.setOutsideTouchable(false);
				view.findViewById(R.id.number_clear).setOnClickListener(this);
				view.findViewById(R.id.number_dot).setOnClickListener(this);
				view.findViewById(R.id.number_zero).setOnClickListener(this);
				view.findViewById(R.id.number_one).setOnClickListener(this);
				view.findViewById(R.id.number_two).setOnClickListener(this);
				view.findViewById(R.id.number_three).setOnClickListener(this);
				view.findViewById(R.id.number_four).setOnClickListener(this);
				view.findViewById(R.id.number_five).setOnClickListener(this);
				view.findViewById(R.id.number_six).setOnClickListener(this);
				view.findViewById(R.id.number_seven).setOnClickListener(this);
				view.findViewById(R.id.number_eight).setOnClickListener(this);
				view.findViewById(R.id.number_nine).setOnClickListener(this);
				view.findViewById(R.id.self_rating_done).setOnClickListener(this);
				view.findViewById(R.id.self_rating_cancel).setOnClickListener(this);
				mSelfRatingScore = (TextView) view.findViewById(R.id.self_rating_score);
				mSelfRatingPopupWindow.setFocusable(true);
				mSelfRatingPopupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
			}
			mSelfRatingScore.setText("");
			String score = item.getScore() == 0? "1" : mContext.getString(R.string.max_score) + (item.getScore()/10);
			mSelfRatingScore.setHint(score);
			mSelfRatingPopupWindow.showAtLocation(getRootView(), Gravity.CENTER, 0, 0);
		}*/
    }

    private void setSelfRatingScoreText(int resId) {
        if (mSelfRatingScore != null && resId != 0) {
            if (resId == R.string.number_clear) {
                mSelfRatingScore.setText("");
            } else if (resId == R.string.number_dot) {
                CharSequence text = mSelfRatingScore.getText();
                if (text != null && text.length() > 0) {
                    mSelfRatingScore.setText(text.length() == 1 ? "" : text.subSequence(0, text.length() - 1));
                }
            } else {
                try {
                    String number = mContext.getString(resId);
                    int score = Integer.parseInt(mSelfRatingScore.getText() + number) * 10;
                    Item item = Question.getInstance().getQuestion(mSelfRatingQuestionId);
                    if (item != null) {
                        if (item.getScore() > 0 && score > item.getScore()) {
                            Toast.makeText(mContext, mContext.getString(R.string.max_score) + (item.getScore() / 10), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    mSelfRatingScore.append(number);
                } catch (NumberFormatException numberFormatException) {

                }

            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        int stringResId = 0;
        if (R.id.number_clear == id) {
            stringResId = R.string.number_clear;
        } else if (R.id.number_dot == id) {
            stringResId = R.string.number_dot;
        } else if (R.id.number_nine == id) {
            stringResId = R.string.number_nine;
        } else if (R.id.number_eight == id) {
            stringResId = R.string.number_eight;
        } else if (R.id.number_seven == id) {
            stringResId = R.string.number_seven;
        } else if (R.id.number_six == id) {
            stringResId = R.string.number_six;
        } else if (R.id.number_five == id) {
            stringResId = R.string.number_five;
        } else if (R.id.number_four == id) {
            stringResId = R.string.number_four;
        } else if (R.id.number_three == id) {
            stringResId = R.string.number_three;
        } else if (R.id.number_two == id) {
            stringResId = R.string.number_two;
        } else if (R.id.number_one == id) {
            stringResId = R.string.number_one;
        } else if (R.id.number_zero == id) {
            stringResId = R.string.number_zero;
        } else if (R.id.self_rating_cancel == id) {
            mSelfRatingPopupWindow.dismiss();
        } else if (R.id.self_rating_done == id) {
            Item item = Question.getInstance().getQuestion(mSelfRatingQuestionId);
            if (item != null) {
                try {
                    int selfRatingScore = Integer.parseInt(mSelfRatingScore.getText().toString()) * 10;
                    item.setSelfRatingScore(selfRatingScore);
                    item.setIsSelfRatinged(true);
                    Question.getInstance().setSortQusetionAnswerStatus(item.getId(), true);
                    if (mHandler != null) {
                        Message message = mHandler.obtainMessage();
                        message.what = MSG_SELFRATING_SCORE;
                        message.arg1 = selfRatingScore;
                        message.obj = mSelfRatingQuestionId;
                        mHandler.sendMessage(message);
                    }
                } catch (NumberFormatException numberFormatException) {

                }
            }
            mSelfRatingPopupWindow.dismiss();
        }
        setSelfRatingScoreText(stringResId);
    }

    public void getKeyPoint(String id) {
        if (mQuestion != null) {
            String[] ids = id.split("-");
            if (ids.length == 2) {
                String keyPointId = getKeyPointIds(mQuestion.getQuestion(ids[0]));
                if (!TextUtils.isEmpty(keyPointId)) {
                    if ("video".equals(ids[1])) {
                        sendMessageToHand(MSG_MICO_VIDEO, keyPointId);
                    } else if ("same".equals(ids[1])) {
                        sendMessageToHand(MSG_SAME_TYPE_QUESTION, keyPointId);
                    }

                }
            }
        }
    }

    private String getKeyPointIds(Item item) {
        String keyPointId = "";
        if (item != null) {
            Item.KeyPoint[] keyPoints = item.getKeyPoints();
            if (keyPoints != null && keyPoints.length > 0) {
                for (int i = 0; i < keyPoints.length; i++) {
                    if (i != 0) {
                        keyPointId += ",";
                    }
                    keyPointId += keyPoints[i].mKeyPointId;
                }
            }
        }
        return keyPointId;
    }

    private void sendMessageToHand(int what, Object obj) {
        if (mHandler != null) {
            Message msg = mHandler.obtainMessage();
            msg.what = what;
            msg.obj = obj;
            mHandler.sendMessage(msg);
        }
    }

}
