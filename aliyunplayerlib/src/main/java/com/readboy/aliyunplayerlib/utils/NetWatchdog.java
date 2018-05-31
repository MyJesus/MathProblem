package com.readboy.aliyunplayerlib.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.alivc.player.VcPlayerLog;

/**
 * Created by ldw on 2018/5/23.
 */

public class NetWatchdog {

    private static final String a = NetWatchdog.class.getSimpleName();
    private Context b;
    private NetWatchdog.NetChangeListener c;
    private IntentFilter d = new IntentFilter();
    private BroadcastReceiver e = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager cm = (ConnectivityManager)context.getSystemService("connectivity");
            NetworkInfo wifiNetworkInfo = cm.getNetworkInfo(1);
            NetworkInfo mobileNetworkInfo = cm.getNetworkInfo(0);
            NetworkInfo.State wifiState = NetworkInfo.State.UNKNOWN;
            NetworkInfo.State mobileState = NetworkInfo.State.UNKNOWN;
            if(wifiNetworkInfo != null) {
                wifiState = wifiNetworkInfo.getState();
            }

            if(mobileNetworkInfo != null) {
                mobileState = mobileNetworkInfo.getState();
            }

            if(NetworkInfo.State.CONNECTED != wifiState && NetworkInfo.State.CONNECTED == mobileState) {
                VcPlayerLog.d(NetWatchdog.a, "onWifiTo4G()");
                if(NetWatchdog.this.c != null) {
                    NetWatchdog.this.c.onWifiTo4G();
                }
            } else if(NetworkInfo.State.CONNECTED == wifiState && NetworkInfo.State.CONNECTED != mobileState) {
                if(NetWatchdog.this.c != null) {
                    NetWatchdog.this.c.on4GToWifi();
                }
            } else if(NetworkInfo.State.CONNECTED != wifiState && NetworkInfo.State.CONNECTED != mobileState && NetWatchdog.this.c != null) {
                NetWatchdog.this.c.onNetDisconnected();
            }

        }
    };

    public NetWatchdog(Context context) {
        this.b = context.getApplicationContext();
        this.d.addAction("android.net.conn.CONNECTIVITY_CHANGE");
    }

    public void setNetChangeListener(NetWatchdog.NetChangeListener l) {
        this.c = l;
    }

    public void startWatch() {
        try {
            this.b.registerReceiver(this.e, this.d);
        } catch (Exception var2) {
            ;
        }

    }

    public void stopWatch() {
        try {
            this.b.unregisterReceiver(this.e);
        } catch (Exception var2) {
            ;
        }

    }

    public static boolean hasNet(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getApplicationContext().getSystemService("connectivity");
        NetworkInfo wifiNetworkInfo = cm.getNetworkInfo(1);
        NetworkInfo mobileNetworkInfo = cm.getNetworkInfo(0);
        NetworkInfo.State wifiState = NetworkInfo.State.UNKNOWN;
        NetworkInfo.State mobileState = NetworkInfo.State.UNKNOWN;
        if(wifiNetworkInfo != null) {
            wifiState = wifiNetworkInfo.getState();
        }

        if(mobileNetworkInfo != null) {
            mobileState = mobileNetworkInfo.getState();
        }

        return NetworkInfo.State.CONNECTED == wifiState || NetworkInfo.State.CONNECTED == mobileState;
    }

    public static boolean is4GConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getApplicationContext().getSystemService("connectivity");
        NetworkInfo mobileNetworkInfo = cm.getNetworkInfo(0);
        NetworkInfo.State mobileState = NetworkInfo.State.UNKNOWN;
        if(mobileNetworkInfo != null) {
            mobileState = mobileNetworkInfo.getState();
        }

        return NetworkInfo.State.CONNECTED == mobileState;
    }

    public interface NetChangeListener {
        void onWifiTo4G();

        void on4GToWifi();

        void onNetDisconnected();
    }

}
