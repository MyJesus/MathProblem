package com.readboy.mathproblem.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.readboy.mathproblem.R;
import com.readboy.mathproblem.cache.CacheCallback;
import com.readboy.mathproblem.cache.ProjectEntityWrapper;
import com.readboy.mathproblem.util.ToastUtils;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by oubin on 2017/8/15.
 */

public class BaseActivity extends Activity implements CacheCallback {
    private static final String TAG = "BaseActivity";

    private static final int REQUEST_ALL_PERMISSIONS = 1;

    protected ProgressBar mProgressBar;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WAKE_LOCK};


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!checkPermissions(this, REQUIRED_PERMISSIONS)) {
            requestPermissions();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantedResults) {
        if (permissions.length > 0 && arePermissionsGranted(permissions, grantedResults)) {
            onRequestPermissionsSuccess();
        } else {
            Toast.makeText(this, "您已禁用了一个所需的权限", Toast.LENGTH_SHORT).show();
            onRequestPermissionsFail();
            finish();
        }
    }

    protected boolean needRequestPermissions() {
        return !checkPermissions(this, REQUIRED_PERMISSIONS);
    }

    protected void onRequestPermissionsSuccess() {

    }

    protected void onRequestPermissionsFail() {

    }

    /**
     * @param permissions 权限
     * @return true 已有权限，false 还需申请权限。
     */
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
            return;
        }
        requestPermissions(noGrantedPermissions.toArray(new String[noGrantedPermissions.size()]),
                REQUEST_ALL_PERMISSIONS);
    }

    protected void assignView() {
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
    }

    protected void showProgressBar() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    protected void hideProgressBar() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBefore() {
        showProgressBar();
    }

    @Override
    public void onAfter() {
        hideProgressBar();
    }

    //网络获取数据
    @Override
    public void onResponse(ProjectEntityWrapper entity) {

    }

    @Override
    public void onError(String message, Throwable e) {
        Log.e(TAG, "onError: message = " + message, e);
//        ToastUtils.show(this, "获取数据失败：" + message);
        if (e instanceof UnknownHostException) {
            ToastUtils.show(this, "获取数据失败。");
        }
    }
}
