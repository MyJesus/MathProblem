package com.readboy.mathproblem.application;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkRequest;
import android.os.Build;

/**
 * Created by oubin on 2017/11/2.
 */

public class NetworkBroadcastReceiver extends BroadcastReceiver {

    private boolean isEnable = false;
    private ConnectivityManager.NetworkCallback mNetworkCallback;

    private class Inner{
        NetworkBroadcastReceiver INSTANCE = new NetworkBroadcastReceiver();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)){

        }
    }

    public void setEnable(boolean enable) {

    }

    private void registerReceiver(Context context) {
        //region 监听网络变化广播
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            context.registerReceiver(this, filter);
        } else {
            ConnectivityManager manager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            mNetworkCallback = new ConnectivityManager.NetworkCallback();
            manager.requestNetwork(new NetworkRequest.Builder().build(), mNetworkCallback);
        }


    }

    public static void unregisterReceiver(Context context){
    }


}
