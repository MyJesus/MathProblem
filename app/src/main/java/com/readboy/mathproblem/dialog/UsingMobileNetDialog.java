package com.readboy.mathproblem.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

/**
 * Created by oubin on 2018/8/6.
 */

public class UsingMobileNetDialog extends CommonDialog {
    private boolean isAgreeUsingMobileNet = false;

    public UsingMobileNetDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContent("当前为非wifi情况下, 要继续播放吗？");
        setLeftText("继续");
        setRightText("退出");
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void handlerLeftClick() {
        super.handlerLeftClick();
    }

    @Override
    protected void handlerRightClick() {
        super.handlerRightClick();
    }
}
