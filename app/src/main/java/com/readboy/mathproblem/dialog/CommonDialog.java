package com.readboy.mathproblem.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.readboy.mathproblem.R;

/**
 * Created by oubin on 2017/9/15.
 */

public class CommonDialog extends BaseDialog implements View.OnClickListener {
    private static final String TAG = "CommonDialog";

    private TextView mDialogContent;
    private Button mLeftButton;
    private Button mRightButton;

    private String mContent;
    private String mLeftText;
    private String mRightText;

    public CommonDialog(@NonNull Context context) {
        super(context);
    }

    private CommonDialog(Builder builder) {
        super(builder.mContext);
        this.mContent = builder.mContent;
        this.mLeftText = builder.mLeftText;
        this.mRightText = builder.mRightText;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_common);
        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        initView();
    }

    private void initView() {
        mDialogContent = (TextView) findViewById(R.id.dialog_content);
        if (!TextUtils.isEmpty(mContent)) {
            mDialogContent.setText(mContent);
        }
        mLeftButton = (Button) findViewById(R.id.dialog_button_left);
        mLeftButton.setOnClickListener(this);
        if (!TextUtils.isEmpty(mLeftText)) {
            mLeftButton.setText(mLeftText);
        }
        mRightButton = (Button) findViewById(R.id.dialog_button_right);
        mRightButton.setOnClickListener(this);
        if (!TextUtils.isEmpty(mRightText)) {
            mRightButton.setText(mRightText);
        }

    }

    public void setContent(String mContent) {
        this.mContent = mContent;
        setText(mContent, mDialogContent);
    }

    public void setLeftText(String mLeftText) {
        this.mLeftText = mLeftText;
        setText(mLeftText, mLeftButton);
    }

    public void setRightText(String mRightText) {
        this.mRightText = mRightText;
        setText(mRightText, mRightButton);
    }

    private void setText(String text, TextView textView) {
        if (textView != null) {
            textView.setText(text);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_button_left:
                handlerLeftClick();
                break;
            case R.id.dialog_button_right:
                handlerRightClick();
                break;
            default:
                Log.e(TAG, "onClick: default : " + v.getId());
        }
    }

    public static class Builder {
        private String mContent;
        private String mLeftText;
        private String mRightText;
        private Context mContext;

        public Builder(Context context) {
            this.mContext = context;
        }

        public Builder content(String mContent) {
            this.mContent = mContent;
            return this;
        }

        public Builder leftText(String mLeftText) {
            this.mLeftText = mLeftText;
            return this;
        }

        public Builder rightText(String mRightText) {
            this.mRightText = mRightText;
            return this;
        }

        public Builder fromPrototype(CommonDialog prototype) {
            mContent = prototype.mContent;
            mLeftText = prototype.mLeftText;
            mRightText = prototype.mRightText;
            return this;
        }

        public CommonDialog build() {
            return new CommonDialog(this);
        }
    }
}
