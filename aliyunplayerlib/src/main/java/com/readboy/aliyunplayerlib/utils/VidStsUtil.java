package com.readboy.aliyunplayerlib.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.alivc.player.VcPlayerLog;
import com.aliyun.vodplayer.media.AliyunVidSts;
import com.aliyun.vodplayer.utils.HttpClientUtil;
import com.readboy.auth.Auth;

import org.json.JSONObject;

import java.util.Properties;

/**
 * Created by pengshuang on 31/08/2017.
 */

public class VidStsUtil {

    private static final String TAG = VidStsUtil.class.getSimpleName();

    public static AliyunVidSts getVidSts() {
        try {
            //获取临时鉴权add by dway 180312
            String url = "http://api.video.readboy.com/videoSts?";

            Properties properties = Auth.getSignature();
            url += "device_id=" + properties.getProperty("device_id");
            url += "&t=" + properties.getProperty("t");
            url += "&sn=" + properties.getProperty("sn");
            Log.v(TAG, "---getVidSts---url = "/* + url*/);

            String response = HttpClientUtil.doGet(url);
            Log.v(TAG, "---getVidSts---response = " + response);
            JSONObject json = new JSONObject(response);
            JSONObject credentials = json.getJSONObject("data");

            String accessKeyId = credentials.getString("AccessKeyId");
            String accessKeySecret = credentials.getString("AccessKeySecret");
            String securityToken = credentials.getString("SecurityToken");
            VcPlayerLog.e(TAG, "accessKeyId = " + accessKeyId + " , accessKeySecret = " + accessKeySecret +
                    " , securityToken = " + securityToken);

            AliyunVidSts vidSts = new AliyunVidSts();
            vidSts.setAcId(accessKeyId);
            vidSts.setAkSceret(accessKeySecret);
            vidSts.setSecurityToken(securityToken);
            return vidSts;
        } catch (Exception e) {
            VcPlayerLog.e(TAG, "e = " + e.getMessage());
            return null;
        }
    }


    public interface OnStsResultListener {
        void onSuccess(String akid, String akSecret, String token);
        void onFail();
    }


    public static void getVidSts(final OnStsResultListener onStsResultListener) {
        AsyncTask<Void, Void, AliyunVidSts> asyncTask = new AsyncTask<Void, Void, AliyunVidSts>() {

            @Override
            protected AliyunVidSts doInBackground(Void... params) {
                return getVidSts();
            }

            @Override
            protected void onPostExecute(AliyunVidSts s) {
                if (s == null) {
                    onStsResultListener.onFail();
                } else {
                    onStsResultListener.onSuccess(s.getAcId(), s.getAkSceret(), s.getSecurityToken());
                }
            }
        };
        asyncTask.execute();
    }


}
