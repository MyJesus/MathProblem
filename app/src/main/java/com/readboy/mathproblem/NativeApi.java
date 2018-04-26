package com.readboy.mathproblem;

/**
 * Created by oubin on 2017/8/15.
 */

public class NativeApi {
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native static String stringFromJNI();

    public native static String getSignature(String date);

    public native static String getAppSecret();

}
