/*
 * Copyright (c) 2016, The Linux Foundation. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *   * Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above
 *     copyright notice, this list of conditions and the following
 *     disclaimer in the documentation and/or other materials provided
 *     with the distribution.
 *   * Neither the name of The Linux Foundation nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.

 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
 * IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


package com.readboy.mathproblem.activity;

import android.Manifest.permission;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Activity that requests permissions needed for activities exported from filemanager.
 */
public class RequestPermissionsActivity extends Activity {

    private static final String TAG = "FileManagerPermissions";
    private static final String PREVIOUS_INTENT = "previous_intent";
    private static final int REQUEST_ALL_PERMISSIONS = 1;
    private Intent mPreviousIntent;

    private static final String[] REQUIRED_PERMISSIONS = new String[]{
            permission.WRITE_EXTERNAL_STORAGE,
            permission.READ_EXTERNAL_STORAGE,
            permission.WAKE_LOCK};

    public static boolean startPermissionActivity(Activity activity) {
        return startRequestPermissionActivity(activity, REQUIRED_PERMISSIONS,
                RequestPermissionsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreviousIntent = (Intent) getIntent().getExtras().get(
                PREVIOUS_INTENT);

        if (savedInstanceState == null) {
            requestPermissions();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantedResults) {
        if (permissions.length > 0 && arePermissionsGranted(permissions, grantedResults)) {
            mPreviousIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(mPreviousIntent);
            finish();
        } else {
            Toast.makeText(this, "您已禁用了一个所需的权限", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    protected static boolean startRequestPermissionActivity(Activity activity,
                                                            String[] requiredPermissions, Class<?> newActivityClass) {
        if (!RequestPermissionsActivity.checkPermissions(activity,
                requiredPermissions)) {
            final Intent intent = new Intent(activity, newActivityClass);
            intent.putExtra(PREVIOUS_INTENT, activity.getIntent());
            activity.startActivity(intent);
            activity.finish();
            return true;
        }
        return false;
    }

    private static boolean checkPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean hasWritePermissions(Context context) {
        return checkPermissions(context, permission.WRITE_EXTERNAL_STORAGE);
    }

    private boolean arePermissionsGranted(String[] permissions, int[] grantResult) {
        for (int i = 0; i < permissions.length; i++) {
            if (grantResult[i] != PackageManager.PERMISSION_GRANTED
                    && Arrays.asList(REQUIRED_PERMISSIONS).contains(permissions[i])) {
                return false;
            }
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestPermissions() {
        final ArrayList<String> noGrantedPermissions = new ArrayList<>();
        for (String permission : REQUIRED_PERMISSIONS) {
            if (checkSelfPermission(permission)
                    != PackageManager.PERMISSION_GRANTED) {
                noGrantedPermissions.add(permission);
            }
        }
        if (noGrantedPermissions.size() == 0) {
            Log.e(TAG, "Request permission activity was called even"
                    + " though all permissions are satisfied.");
        }
        requestPermissions(noGrantedPermissions.toArray(new String[noGrantedPermissions.size()]),
                REQUEST_ALL_PERMISSIONS);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
