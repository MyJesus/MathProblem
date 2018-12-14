package com.readboy.aliyunplayerlib.helper;

import android.os.AsyncTask;

import com.aliyun.vodplayer.media.AliyunVidSts;
import com.aliyun.vodplayer.utils.HttpClientUtil;
import com.readboy.aliyunplayerlib.utils.AliLogUtil;
import com.readboy.auth.Auth;

import org.json.JSONObject;

import java.util.Properties;

/**
 *
 * Created by ldw on 2018/4/1.
 */

public class VidStsHelper {
    private static final String TAG = VidStsHelper.class.getSimpleName();

    public static final int ERRNO_DEVICE_UNAUTH = 6003;//签名失效，未授权的机型
    public static final int ERRNO_SIGNATURE_INVALID = 6005;//签名失效，有可能系统时间有误

    private AsyncTask<Void, Void, AliyunVidSts> mAsyncTask = null;
    private boolean mIsGettingVidsts = false;
    private int mErrono = -1;


    public AliyunVidSts getVidSts() {
        try {
            //获取临时鉴权add by dway 180312
            String url = "http://api.video.readboy.com/videoSts?";

            Properties properties = Auth.getSignature();
            url += "device_id=" + properties.getProperty("device_id");
            url += "&t=" + properties.getProperty("t");
            url += "&sn=" + properties.getProperty("sn");
            AliLogUtil.v(TAG, "---getVidSts---url = "/* + url*/);

            String response = HttpClientUtil.doGet(url);
            AliLogUtil.v(TAG, "---getVidSts---response = " + response);
            JSONObject json = new JSONObject(response);
            if(json.has("ok") && json.optInt("ok") == 1) {
                JSONObject credentials = json.getJSONObject("data");

                String accessKeyId = credentials.getString("AccessKeyId");
                String accessKeySecret = credentials.getString("AccessKeySecret");
                String securityToken = credentials.getString("SecurityToken");
                AliLogUtil.v(TAG, "---getVidSts---accessKeyId = " + accessKeyId + " , accessKeySecret = " + accessKeySecret +
                        " , securityToken = " + securityToken);

                AliyunVidSts vidSts = new AliyunVidSts();
                vidSts.setAcId(accessKeyId);
                vidSts.setAkSceret(accessKeySecret);
                vidSts.setSecurityToken(securityToken);
                return vidSts;
            }else if(json.has("ok") && json.optInt("ok") == 0){
                mErrono = json.optInt("errno", -1);
                return null;
            }
        } catch (Exception e) {
            AliLogUtil.e(TAG, "---getVidSts--- e = " + e.getMessage());
        }
        mErrono = -1;
        return null;
    }

    /**
     * 获取零时vidsts授权
     * @param onStsResultListener
     */
    public void getVidSts(final OnStsResultListener onStsResultListener) {
        mIsGettingVidsts = true;
        mAsyncTask = new AsyncTask<Void, Void, AliyunVidSts>() {

            @Override
            protected AliyunVidSts doInBackground(Void... params) {
                return getVidSts();
            }

            @Override
            protected void onPostExecute(AliyunVidSts s) {
                //AliLogUtil.v(TAG, "---onPostExecute---");
                if (s == null) {
                    onStsResultListener.onFail(mErrono);
                } else {
                    onStsResultListener.onSuccess(s.getAcId(), s.getAkSceret(), s.getSecurityToken());
                }
                mIsGettingVidsts = false;
            }
        };
        mAsyncTask.execute();
    }

    /**
     * 取消获取授权
     */
    public void cancelRequest(){
        if(mAsyncTask != null && !mAsyncTask.isCancelled()){
            mAsyncTask.cancel(true);
        }
        mIsGettingVidsts = false;
    }

    /**
     * 是否正在请求
     * @return
     */
    public boolean isGettingVidsts(){
        return mIsGettingVidsts;
    }


    public interface OnStsResultListener {
        void onSuccess(String akid, String akSecret, String token);
        void onFail(int errno);
    }

}
