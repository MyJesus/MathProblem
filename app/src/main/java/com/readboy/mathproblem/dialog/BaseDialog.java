package com.readboy.mathproblem.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;

import com.readboy.mathproblem.R;

/**
 * Created by oubin on 2017/9/15.
 */

public class BaseDialog extends Dialog {

    protected OnClickListener mOnClickListener;

    public BaseDialog(@NonNull Context context) {
        super(context, R.style.TransparentDialog);
    }

    public void setOnClickListener(OnClickListener listener) {
        this.mOnClickListener = listener;
    }

    public interface OnClickListener {
        void onLeftClick(BaseDialog dialog);

        void onRightClick(BaseDialog dialog);
    }

    protected void handlerLeftClick() {
        if (mOnClickListener != null) {
            mOnClickListener.onLeftClick(this);
        }
    }

    protected void handlerRightClick() {
        if (mOnClickListener != null) {
            mOnClickListener.onRightClick(this);
        }
    }
}
