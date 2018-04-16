package com.readboy.mathproblem.video.tools;

import android.content.Context;
import android.os.PowerManager;

/**
 * Created by oubin on 2016/10/25.
 *
 * 类型	                cpu	屏幕	键盘
 * PARTIAL_WAKE_LOCK	on	off	 off
 * SCREEN_DIM_WAKE_LOCK	on	dim	off
 * SCREEN_BRIGHT_WAKE_LOCK	on	Bright	off
 * FULL_WAKE_LOCK	on	Bright	Bright
 */

public class WakeUtil {

    private static PowerManager.WakeLock sCpuWakeLock;

    private static PowerManager.WakeLock createPartialWakeLock(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//        return powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, context.getPackageName());
        return powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, context.getPackageName());
    }

    public static void acquireCpuWakeLock(Context context) {
        if (sCpuWakeLock != null) {
            return;
        }

        sCpuWakeLock = createPartialWakeLock(context);
        sCpuWakeLock.setReferenceCounted(false);
        sCpuWakeLock.acquire();
    }

    public static void releaseCpuLock() {
        if (sCpuWakeLock != null) {
            sCpuWakeLock.release();
            sCpuWakeLock = null;
        }
    }
}
