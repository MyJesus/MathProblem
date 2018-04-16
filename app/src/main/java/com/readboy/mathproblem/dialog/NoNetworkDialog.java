package com.readboy.mathproblem.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;

/**
 * Created by oubin on 2017/10/20.
 */

public class NoNetworkDialog extends CommonDialog {


    public NoNetworkDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContent("网络又开溜了，赶快追回来！");
        setLeftText("稍后再说");
        setRightText("设置网络");
    }

    @Override
    protected void handlerLeftClick() {
        super.handlerLeftClick();
        dismiss();
    }

    @Override
    protected void handlerRightClick() {
        super.handlerRightClick();
        getContext().startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
        dismiss();
    }
}
