package com.readboy.textbook.view;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebSettings.LayoutAlgorithm;

/**
 * Created by 1 on 2016/3/23.
 */
public class MyBaseQuestionWebView extends MyBaseWebView {

    /**最后一道题作答完成*/
    public static final int MSG_ANSWER_SET_FINISH = 1000;
    /**自评消息*/
    public static final int MSG_SELFRATING_SET_FINISH = 1100;
    /**启动家长管理，验证密码*/
    public static final int PARENT_PASSWORD_REQUEST_CODE = 100;

    public MyBaseQuestionWebView(Context context) {
        super(context);
        init();
    }

    public MyBaseQuestionWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyBaseQuestionWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    
    private void init()
    {
		setVerticalScrollBarEnabled(false);
		setVerticalScrollbarOverlay(false);
    }
    
}
