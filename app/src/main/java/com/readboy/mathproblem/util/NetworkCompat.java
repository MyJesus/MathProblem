package com.readboy.mathproblem.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;


/**
 * Created by oubin on 2018/5/14.
 */

public class NetworkCompat {
    private static final String TAG = "oubin_NetworkCompat";

    private NetworkListener mListener;
    private BroadcastReceiver mReceiver;
    private ConnectivityManager.NetworkCallback mNetworkCallback;

    public void start(Context context) {
        registerReceiver(context);
    }

    public void stop(Context context){
        unregisterReceiver(context);
    }

    private void registerReceiver(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            mReceiver = new NetworkReceiver();
            context.registerReceiver(mReceiver, filter);
        } else {
            ConnectivityManager manager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            mNetworkCallback = new NetworkCallback();
            manager.requestNetwork(new NetworkRequest.Builder().build(), mNetworkCallback);
        }
    }

    private void unregisterReceiver(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            context.unregisterReceiver(mReceiver);
        } else {
            ConnectivityManager manager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            manager.unregisterNetworkCallback(mNetworkCallback);
        }
    }

    public void setNetworkListener(NetworkListener listener) {
        this.mListener = listener;
    }

    private void fireOnAvailableEvent() {
        if (mListener != null) {
            mListener.onAvailable();
        }
    }

    private void fireOnLostEvent() {
        if (mListener != null) {
            mListener.onLost();
        }
    }

    public interface NetworkListener {
        void onAvailable();

        void onLost();
    }

    private class NetworkReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "onReceive: action = " + intent.getAction());
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                if (NetworkUtils.isConnected(context)) {
                    fireOnAvailableEvent();
                } else {
                    fireOnLostEvent();
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private class NetworkCallback extends ConnectivityManager.NetworkCallback {
        @Override
        public void onAvailable(Network network) {
            super.onAvailable(network);
            Log.e(TAG, "onAvailable: ");
            fireOnAvailableEvent();
        }

        @Override
        public void onLost(Network network) {
            super.onLost(network);
            Log.e(TAG, "onLost: ");
            fireOnLostEvent();
        }
    }

}
