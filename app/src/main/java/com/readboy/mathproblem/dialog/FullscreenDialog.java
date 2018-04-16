package com.readboy.mathproblem.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.readboy.mathproblem.R;

/**
 * Created by oubin on 2017/10/25.
 */

public class FullscreenDialog extends Dialog {
    private static final String TAG = "FullscreenDialog";


    public FullscreenDialog(@NonNull Context context) {
        super(context);
//        hideNavigationBar();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        hideNavigationBar();
//        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialog_common);
//        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Window window = this.getWindow();

        Log.e(TAG, "onStart: system ui visibility = " + getWindow().getDecorView().getSystemUiVisibility());

//Window window = getDialog().getWindow();如果是在activity中则用这段代码
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//window.requestWindowFeature(Window.FEATURE_NO_TITLE); 用在activity中，去标题
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        window.getDecorView().setSystemUiVisibility(uiOptions);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        Log.e(TAG, "dismiss: system ui visibility = " + getWindow().getDecorView().getSystemUiVisibility());

    }

    @Override
    public void onAttachedToWindow() {
//        hideNavigationBar();
        super.onAttachedToWindow();
        Log.e(TAG, "onAttachedToWindow: ");
    }

    private void hideNavigationBar() {
        if (getWindow() == null) {
            Log.e(TAG, "hideNavigationBar: window = null");
            return;
        }
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//        getWindow().setFlags(0x02000000, 0x02000000); //隐藏系统条
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}
