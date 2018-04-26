package com.readboy.mathproblem.http.auth;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.DataFormatException;

import static com.readboy.mathproblem.http.auth.Authentication.APP_EFFECTIVE_SECOND_TIME;
import static com.readboy.mathproblem.http.auth.Authentication.TEST_MODE;

/**
 * Created by oubin on 2017/10/30.
 *
 * @author oubin
 */

public class AuthManager {
    private static final String TAG = "oubin_AuthManager";

    public static final int ERROR_CODE_EXPIRE = 403;

    private volatile static Authentication sAuthentication;
    private static final String AUTH_FILE_NAME = "authentication.json";

    private AuthManager() throws IllegalAccessException {
        Log.e(TAG, "AuthManager: init.");
        throw new IllegalAccessException("no permission.");
    }

    public static void registerAuth(Context context, Set<String> uriSet, @NonNull final MultiAuthCallback callback) {
        if (isValid()) {
            sendMultiAuthEvent(uriSet, callback);
            return;
        }

        readFileCache();
        if (isValid()) {
            sendMultiAuthEvent(uriSet, callback);
            return;
        }

        final String filepath = FileHelper.getAuthAbsolutePath(AUTH_FILE_NAME);

    }

    private static void sendMultiAuthEvent(Set<String> uriSet, MultiAuthCallback callback) {
        Map<String, String> map = new HashMap<>(uriSet.size());
        for (String uri : uriSet) {
            map.put(uri, sAuthentication.authUrl(uri));
        }
        callback.onAuth(map);
    }

    public static void registerAuth(Context context, final String uri, @NonNull final AuthCallback callback) {
        if (isValid()) {
            Log.e(TAG, "registerAuth: RAM");
            callback.onAuth(sAuthentication.authUrl(uri));
            return;
        }
        if (!TEST_MODE) {
            readFileCache();
        }
        if (isValid()) {
            Log.e(TAG, "registerAuth: file ");
            callback.onAuth(sAuthentication.authUrl(uri));
            return;
        }

        final String filepath = FileHelper.getAuthAbsolutePath(AUTH_FILE_NAME);
        Log.e(TAG, "registerAuth: volley. ");

    }

    private static void readFileCache() {
        Log.e(TAG, "readFileCache: ");
        final String filepath = FileHelper.getAuthAbsolutePath(AUTH_FILE_NAME);
        if (FileHelper.isExist(filepath)) {
            Authentication a = MyJson.authentication(filepath);
            if (isValid(a)) {
                Log.e(TAG, "readFileCache: is valid");
                sAuthentication = a;
            }
        }
    }

    private static boolean isValid(Authentication authentication) {
        if (authentication != null) {
            Log.e(TAG, "isValid: current = " + System.currentTimeMillis() / 1000
                    + ", stamp = " + authentication.getTimestamp());
        }
        return authentication != null
                && System.currentTimeMillis() / 1000 < authentication.getTimestamp();
    }

    private static boolean isValid() {
        Log.e(TAG, "isValid: sAuthentication = " + sAuthentication);
        if (sAuthentication != null) {
//            Log.e(TAG, "isValid: current = " + System.currentTimeMillis() / 1000
//                    + ", timestamp = "
//                    + (sAuthentication.getTimestamp() + APP_EFFECTIVE_SECOND_TIME + DEFAULT_EFFECTIVE_SECOND_TIME));
        }
        return sAuthentication != null
                && System.currentTimeMillis() / 1000
                < sAuthentication.getTimestamp() + APP_EFFECTIVE_SECOND_TIME;
    }

    public static boolean isValid(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        String regex1 = "auth_key=";
        int index = url.lastIndexOf(regex1);
        String regex2 = "-0-0-";
        int lastIndex = url.lastIndexOf(regex2);
        if (lastIndex < 0 || index < 0) {
            return true;
        }
        String time = url.substring(index + regex1.length(), lastIndex);
        return System.currentTimeMillis() / 1000 < Integer.valueOf(time);
    }

    public static String getUri(String url) {
        if (TextUtils.isEmpty(url)) {
            return "";
        }
        String regex1 = "?auth_key=";
        int index = url.lastIndexOf(regex1);
        String regex2 = "-0-0-";
        int lastIndex = url.lastIndexOf(regex2);

        return null;
    }

    public static String auth(String uri) {
        return isValid() ? sAuthentication.authUrl(uri) : "";
    }

}
