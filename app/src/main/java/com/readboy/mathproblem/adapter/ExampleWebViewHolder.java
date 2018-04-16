package com.readboy.mathproblem.adapter;

import android.graphics.Bitmap;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.readboy.mathproblem.R;
import com.readboy.mathproblem.http.HttpConfig;
import com.readboy.mathproblem.http.response.ProjectEntity;
import com.readboy.mathproblem.js.JsUtils;

/**
 * Created by oubin on 2017/9/8.
 */

public class ExampleWebViewHolder extends BaseViewHolder<ProjectEntity.Project.Example> {
    private static final String TAG = "ExampleWebViewHolder";

    private WebView mWebView;
    private View mProgressBar;

    public ExampleWebViewHolder(View itemView) {
        super(itemView);
        mWebView = (WebView) itemView.findViewById(R.id.example_web_view);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setBackgroundColor(0);
        mProgressBar = itemView.findViewById(R.id.web_progress);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void bindView(ProjectEntity.Project.Example data) {
        super.bindView(data);
        String position = String.valueOf(getAdapterPosition() + 1);
        String header = "例" + position + "、";
        String htmlString = JsUtils.makeExampleHtmlText(header, data);
//        Log.e(TAG, "bindView: html = " + htmlString);
        mWebView.loadDataWithBaseURL(HttpConfig.RESOURCE_HOST, htmlString, "text/html", "UTF-8", "");
    }
}
