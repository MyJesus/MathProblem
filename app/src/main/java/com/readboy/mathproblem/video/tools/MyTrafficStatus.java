package com.readboy.mathproblem.video.tools;

import android.content.Context;
import android.net.TrafficStats;

public class MyTrafficStatus {

    private int mIndex = 0;
    private long mLastTime = System.currentTimeMillis();
    private long mLastBytes = 0;


    public MyTrafficStatus(Context context) {
        mLastBytes = TrafficStats.getUidRxBytes(context.getApplicationInfo().uid) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes()); //转为KB
        mLastTime = System.currentTimeMillis();
    }

    public String getRxSpeed(Context context) {
        String netSpeed = null;
        mIndex++;
        if (mIndex > 2) {
            long rxBytes = TrafficStats.getUidRxBytes(context.getApplicationInfo().uid) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes()); //转为KB
            long curTime = System.currentTimeMillis();
//            Log.i("oubin_MovieActivity", " mLastTime: " + mLastTime + ", mLastBytes: " + mLastBytes + ", curTime: " + curTime + ", rxBytes: " + rxBytes);
            long dipByte = rxBytes - mLastBytes;
            long dipTime = curTime - mLastTime;
            long speed = (long) (dipByte * 1.0F / dipTime * 1000);
//            Log.i("oubin_MovieActivity", " speed: " + speed + ", dipTime: " + dipTime + ", dipByte: " + dipByte);
            if (speed > 1024 * 1024) {
                netSpeed = String.format("%.2f", speed * 1.0F / (1024 * 1024)) + "MB/s";
            } else if (speed > 1024) {
                netSpeed = speed / 1024 + "KB/s";
            } else {
                netSpeed = speed + "B/s";
            }
            mLastBytes = rxBytes;
            mLastTime = curTime;
            mIndex = 0;
        }

        return netSpeed;

    }

}
 