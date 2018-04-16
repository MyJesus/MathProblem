package com.readboy.mathproblem.test;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.readboy.mathproblem.R;
import com.readboy.mathproblem.http.HttpConfig;
import com.readboy.mathproblem.js.JsUtils;

public class WebViewActivity extends Activity {

    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        webView = (WebView) findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
//        webView.loadUrl("file:///android_asset/js/exercise.html");
        webView.addJavascriptInterface(WebViewActivity.this, "android");
        String html = JsUtils.makeBaseHtmlText(JsUtils.TEXT);
        webView.loadDataWithBaseURL(HttpConfig.RESOURCE_HOST, html, "text/html", "UTF-8", "");

    }


    //由于安全原因 targetSdkVersion>=17需要加 @JavascriptInterface
    //JS调用Android JAVA方法名和HTML中的按钮 onclick后的别名后面的名字对应
    @JavascriptInterface
    public void startFunction() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(WebViewActivity.this, "show", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @JavascriptInterface
    public void startFunction(final String text) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                new AlertDialog.Builder(WebViewActivity.this).setMessage(text).show();

            }
        });


    }
}
