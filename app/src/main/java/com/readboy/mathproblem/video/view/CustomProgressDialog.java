package com.readboy.mathproblem.video.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.readboy.mathproblem.R;
import com.readboy.mathproblem.video.movie.MovieActivity;

public class CustomProgressDialog extends Dialog {
    private static final String TAG = "oubin_ProgressDialog";

    private boolean mInit = false;
    private String mMsg = "(0%)";
    private View mCancelBtn;

    public CustomProgressDialog(Context context) {
        this(context, R.style.CustomProgressDialog);
    }

    public CustomProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_video_loading);
        Log.e(TAG, "onCreate: ");
        init();
    }

    private void init() {
//        this.getWindow().getAttributes().gravity = Gravity.CENTER;
        mCancelBtn = findViewById(R.id.progress_cancel);
        TextView tvMsg = (TextView) findViewById(R.id.message);
        setCancelable(false);
        if (tvMsg != null) {
            tvMsg.setText("精彩即将呈现");
        }
        tvMsg = (TextView) findViewById(R.id.netspeed);
        if (tvMsg != null) {
            tvMsg.setText(mMsg);
        }
        mInit = true;
    }

    @Override
    public void onBackPressed() {
        getContext().sendBroadcast(new Intent(MovieActivity.ACTION_APPSWITCH));
    }

    public void setNetworkSpeed(String speeds) {
        TextView tvMsg = (TextView) this.findViewById(R.id.netspeed);
        if (tvMsg != null) {
            tvMsg.setText(speeds);
        }
    }

    public void setMessage(String msg) {
        mMsg = msg;
        if (mInit) {
            TextView tvMsg = (TextView) this.findViewById(R.id.netspeed);
            if (tvMsg != null) {
                tvMsg.setText(msg);
            }
        }
    }

    public void setCancelClickListener(View.OnClickListener listener) {
        mCancelBtn.setOnClickListener(listener);
    }

}
