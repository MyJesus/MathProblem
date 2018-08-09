package com.readboy.mathproblem.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Handler;
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
    private OnNetChangeListener mNetChangeListener;

    public void start(Context context) {
        registerReceiver(context);
    }

    public void stop(Context context) {
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
            mNetworkCallback = new NetworkCallback(context);
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

    /**
     * 获取活动网络信息
     *
     * @param context 上下文
     * @return NetworkInfo
     */
    private static NetworkInfo getActiveNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
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

    private void handleNetType(int type) {
        if (mNetChangeListener == null) {
            return;
        }
        mNetChangeListener.onNetworkChange();
        switch (type) {
            case -1:
                mNetChangeListener.onLost();
                break;
            case ConnectivityManager.TYPE_WIFI:
                mNetChangeListener.onAvailable();
                mNetChangeListener.onWifi();
                break;
            case ConnectivityManager.TYPE_MOBILE:
                mNetChangeListener.onAvailable();
                mNetChangeListener.onMobile();
                break;
            case ConnectivityManager.TYPE_ETHERNET:

                break;
            default:
                break;
        }

    }

    public void setNetChangeListener(OnNetChangeListener changeListener) {
        this.mNetChangeListener = changeListener;
    }

    public static class OnNetChangeSample implements OnNetChangeListener {

        @Override
        public void onNetworkChange() {

        }

        @Override
        public void onWifi() {

        }

        @Override
        public void onMobile() {

        }

        @Override
        public void onAvailable() {

        }

        @Override
        public void onLost() {

        }

        @Override
        public void onUnknown() {

        }
    }

    public interface OnNetChangeListener {
        void onNetworkChange();

        void onWifi();

        void onMobile();

        void onAvailable();

        void onLost();

        void onUnknown();
    }

    private class NetworkReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "onReceive: action = " + intent.getAction());
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                NetworkInfo info = getActiveNetworkInfo(context);
                handleNetType(info == null ? -1 : info.getType());
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

        private Context mContext;

        private NetworkCallback(Context context) {
            this.mContext = context;
        }

        @Override
        public void onAvailable(Network network) {
            super.onAvailable(network);
            Log.e(TAG, "onAvailable: ");
            NetworkInfo info = getActiveNetworkInfo(mContext);
            handleNetType(info == null ? -1 : info.getType());
            fireOnAvailableEvent();
        }

        @Override
        public void onLost(Network network) {
            super.onLost(network);
            Log.e(TAG, "onLost: ");
            handleNetType(-1);
            fireOnLostEvent();
        }
    }

}
