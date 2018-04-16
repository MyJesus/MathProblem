package com.readboy.textbook.view;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;

import com.readboy.textbook.model.Question;
import com.readboy.textbook.model.Question.Item;
import com.readboy.textbook.util.DebugLogger;
import com.readboy.textbook.util.MyApplication;
import com.readboy.textbook.util.NetWorkUtils;
import com.readboy.textbook.util.QuestionUtils;
import com.readboy.textbookwebview.QuestionJavaScriptObject;
import com.readboy.textbookwebview.WebViewClientImpl;

public class MyQuestionWebView extends WebView {
    private static final String TAG = "MyQuestionWebView";

    /**
     * 最后一道题作答完成
     */
    public static final int MSG_ANSWER_SET_FINISH = 1000;
    /**
     * 自评消息
     */
    public static final int MSG_SELFRATING_SET_FINISH = 1100;
    private QuestionJavaScriptObject mJavaScriptObject;
    private String mCurrentQuestionId;
    private int mCurrentQuestionIndex = 0;
    private Question mQuestion = null;
    private int mQuestionCount;
    private Question.Item mQuestionItem = null;
    private boolean mIsShowAnswerAndExplain = false;
    private boolean mIsSelfRating = false;
    private long mAnswerStartTime = 0;
    private long mAnswerEndTime = 0;
    private Handler mHandler = null;

    private float downX, downY;
    private boolean tryInterceptParent;

    public MyQuestionWebView(Context context) {
        super(context);
        init();
    }

    public MyQuestionWebView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        init();
    }

    public MyQuestionWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
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
        ws.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        ws.setAppCacheEnabled(true);

        mJavaScriptObject = new QuestionJavaScriptObject(getContext(), this);
        addJavascriptInterface(mJavaScriptObject, "JavaScriptObject");

        setWebViewClient(new WebViewClientImpl(getContext()));

        setFocusableInTouchMode(true);
        setBackgroundColor(0);

        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //和外层RecyclerView或者ViewPager滑动冲突处理。
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getRawX();
                downY = event.getRawY();
                tryInterceptParent = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (!tryInterceptParent){
                    break;
                }
                ViewParent parent = getParent();
                if (parent != null) {
                    float x = event.getRawX();
                    float y = event.getRawY();
                    boolean intercept = Math.abs(y - downY) > Math.abs(x - downX);
//                    Log.e(TAG, "onTouchEvent: intercept = " + intercept);
                    parent.requestDisallowInterceptTouchEvent(intercept);
                }
                tryInterceptParent = false;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                ViewParent viewParent = getParent();
                if (viewParent != null){
                    viewParent.requestDisallowInterceptTouchEvent(false);
                }
        }
        return super.onTouchEvent(event);
    }

    private void makeHtmlQuestion(final Question.Item item) {
        String questionHtmlStr = QuestionUtils.makeOneQuestionHtml(item, mIsShowAnswerAndExplain, mIsSelfRating);
        DebugLogger.getLogger().d(questionHtmlStr);
        loadDataWithBaseURL(NetWorkUtils.RESOURCE_HOST, questionHtmlStr, "text/html", "UTF-8", "");
        mQuestionItem = item;
        mAnswerStartTime = System.currentTimeMillis();
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
        int length = jsonArray.length();
        mQuestionCount = length;
        for (int i = 0; i < length; i++) {
            JSONObject questionJsonObject = jsonArray.optJSONObject(i);
            String id = questionJsonObject.optString("id");
            String questionType = questionJsonObject.optString("type");
            String content = questionJsonObject.optString("content");
            JSONArray accessoryJsonArray = questionJsonObject.optJSONArray("accessory");
            JSONArray correctAnswerJsonArray = questionJsonObject.optJSONArray("correctAnswer");
            String solution = questionJsonObject.optString("solution");
            JSONArray solutionAccessoryJsonArray = questionJsonObject.optJSONArray("solutionAccessory");

            Question.Item item = mQuestion.new Item(id, questionType, content, solution);
            if (accessoryJsonArray != null) {
                for (int j = 0; j < accessoryJsonArray.length(); j++) {
                    JSONArray optionJsonArray = accessoryJsonArray.optJSONObject(j).optJSONArray("options");
                    String type = accessoryJsonArray.optJSONObject(j).optString("type");
                    Question.Item.Option option = item.new Option();
                    if (optionJsonArray != null && type != null) {
                        option.setType(type);
                        for (int k = 0; k < optionJsonArray.length(); k++) {
                            option.addOption(optionJsonArray.optString(k));
                        }
                    }
                    item.addOptions(option);
                }
            }
            if (correctAnswerJsonArray != null) {
                for (int j = 0; j < correctAnswerJsonArray.length(); j++) {
                    item.addCorrectAnswer(correctAnswerJsonArray.optString(j));
                }
            }
            if (solutionAccessoryJsonArray != null) {
                for (int j = 0; j < solutionAccessoryJsonArray.length(); j++) {
                    item.addSolutionAccessory(solutionAccessoryJsonArray.optString(j));
                }
            }
            mQuestion.addQuestionItem(item);
        }
        mCurrentQuestionId = mQuestion.getQuestionId(0);
        makeHtmlQuestion(mQuestion.getQuestion(mCurrentQuestionId));
    }

    public void setQuestionCount(int count) {
        mQuestionCount = count;
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
     * @param questionId
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
            }
        }
        if (mHandler != null) {
            mHandler.sendEmptyMessage(MSG_ANSWER_SET_FINISH);
        }
        DebugLogger.getLogger().d("MyQuestion time=" + System.currentTimeMillis());
    }

    public void setBlankUserAnswer(String questionId, String userAnswer) {
        if (mQuestionItem != null) {
            if (questionId != null) {
                String[] ids = questionId.split("-");
                if (ids.length == 2) {
                    int questionIndex = Integer.parseInt(ids[1]);
                    mQuestionItem.setBlankUserAnswer(questionIndex, userAnswer);
                }
            }
        } else if (mQuestion != null) {
            mQuestion.setBlankUserAnswer(questionId, userAnswer);
        }
    }

    /**
     * 显示答案和解析
     */
    public void showAnswerAndExplain() {
        if (mQuestionItem != null) {
            mIsShowAnswerAndExplain = true;
            makeHtmlQuestion(mQuestionItem);
        }
    }

    /**
     * 调用JavaScript去设置答案
     */
    public void setUserAnswerCallJavaScript() {
        if (mQuestionItem != null) {
            String type = mQuestionItem.getType();
            if (type == "101" || type == "102" || type == "103") {
                return;
            }
        }
        loadUrl("javascript:setUserAnswer()");
    }

    /**
     * 调用JavaScript去设置答案
     */
    public void setUserAnswerCallJavaScript(Handler handler) {
        if (mQuestionItem != null) {
            String type = mQuestionItem.getType();
            if (type == "101" || type == "102" || type == "103") {
                handler.sendEmptyMessage(MSG_ANSWER_SET_FINISH);
                return;
            }
        }
        mHandler = handler;
        loadUrl("javascript:setUserAnswer()");
    }

    public String getChildrenQuestionId(String parentId, String index) {
        Item item = Question.getInstance().getQuestion(parentId);
        if (item != null) {
            int idIndex = Integer.parseInt(index.trim());
            return item.getChildrenQuestionId().get(idIndex - 1);
        }
        return null;
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
                mHandler.sendMessage(mHandler.obtainMessage(MSG_SELFRATING_SET_FINISH, ids[0]));
            }
        }
    }

    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    public float getDeviceScale() {
        return MyApplication.mDeviceScale;
    }


}
