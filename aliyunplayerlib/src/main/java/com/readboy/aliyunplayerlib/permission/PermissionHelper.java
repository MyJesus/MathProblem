package com.readboy.aliyunplayerlib.permission;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import java.lang.ref.WeakReference;

/**
 * 动态权限帮助类
 * Created by ldw on 2018/1/10.
 */
public class PermissionHelper {

    private WeakReference<Activity> mActivity;
    private PermissionInterface mPermissionInterface;

    public PermissionHelper(@NonNull Activity activity, @NonNull PermissionInterface permissionInterface) {
        mActivity = new WeakReference<>(activity);
        mPermissionInterface = permissionInterface;
    }

    /**
     * 开始请求权限。
     * 方法内部已经对Android M 或以上版本进行了判断，外部使用不再需要重复判断。
     * 如果设备还不是M或以上版本，则也会回调到requestPermissionsSuccess方法。
     */
    public void requestPermissions(){
        Activity activity = mActivity.get();
        if(activity != null) {
            String[] deniedPermissions = PermissionUtil.getDeniedPermissions(activity, mPermissionInterface.getPermissions());
            if (deniedPermissions != null && deniedPermissions.length > 0) {
                PermissionUtil.requestPermissions(activity, deniedPermissions, mPermissionInterface.getPermissionsRequestCode());
            } else {
                mPermissionInterface.requestPermissionsSuccess();
            }
        }else{
            mPermissionInterface.requestPermissionsFail();
        }
    }

    /**
     * 在Activity中的onRequestPermissionsResult中调用
     * @param requestCode
     * @param permissions
     * @param grantResults
     * @return true 代表对该requestCode感兴趣，并已经处理掉了。false 对该requestCode不感兴趣，不处理。
     */
    public boolean requestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if(requestCode == mPermissionInterface.getPermissionsRequestCode()){
            boolean isAllGranted = true;//是否全部权限已授权
            for(int result : grantResults){
                if(result == PackageManager.PERMISSION_DENIED){
                    isAllGranted = false;
                    break;
                }
            }
            if(isAllGranted){
                //已全部授权
                mPermissionInterface.requestPermissionsSuccess();
            }else{
                //权限有缺失
                mPermissionInterface.requestPermissionsFail();
            }
            return true;
        }
        return false;
    }

}
