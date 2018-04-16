package com.readboy.mathproblem.exercise;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.readboy.mathproblem.R;
import com.readboy.mathproblem.adapter.BaseViewHolder;
import com.readboy.mathproblem.application.Constants;
import com.readboy.textbook.model.Question;
import com.readboy.textbook.view.MyQuestionWebView;

/**
 * Created by oubin on 2017/11/9.
 */

public class ExerciseWebViewHolder extends BaseViewHolder<Question.Item> {
    private static final String TAG = "ExerciseWebViewHolder";

    private static final String EMPTY_URL = Constants.EMPTY_URL;

    private MyQuestionWebView mWebView;
    private View mProgressBar;

    public ExerciseWebViewHolder(View itemView) {
        super(itemView);
        mWebView = (MyQuestionWebView) itemView.findViewById(R.id.exercise_web_view);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mProgressBar = itemView.findViewById(R.id.web_progress);
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
//                Log.e(TAG, "onPageStarted: ");
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mProgressBar.setVisibility(View.GONE);
//                Log.e(TAG, "onPageFinished: ");
            }
        });
    }

    public void bindView(Question.Item data, boolean showSolution) {
        super.bindView(data);
//        clearData();
        mWebView.loadData(data, showSolution);
    }

    public void stopLoading(){
        mWebView.stopLoading();
    }

    public void clearData(){
        Log.e(TAG, "clearData: ");
        mWebView.loadUrl(EMPTY_URL);
//        mWebView.clearFormData();
    }

}
