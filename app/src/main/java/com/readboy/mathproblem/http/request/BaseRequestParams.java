package com.readboy.mathproblem.http.request;

import com.readboy.auth.Auth;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by oubin on 2017/9/16.
 */

public class BaseRequestParams {

    private String sn;
//    private static final String deviceId = BuildUtils.getDeviceId();
    private String deviceId;
    private String date;

//    static {
//        Auth.setParameters("device_id", deviceId);
//    }

    public BaseRequestParams() {
//        this.date = String.valueOf(System.currentTimeMillis() / 1000);
//        sn = NativeApi.getSignature(date);
        Properties properties = Auth.getSignature();
        this.date = properties.getProperty("t");
        this.sn = properties.getProperty("sn");
        this.deviceId = properties.getProperty("device_id");

    }

    public Map<String, String> getMap() {
        Map<String, String> params = new HashMap<>(3);
        params.put("sn", sn);
        params.put("device_id", deviceId);
        params.put("t", date);
        return params;
    }

    public String unitParams() {
        Map<String, String> map = getMap();
        StringBuilder builder = new StringBuilder();
        int pos = 0;
        String regex = "&";
        for (String key : map.keySet()) {
            if (pos > 0) {
                builder.append(regex);
            }
            builder.append(key).append("=").append(map.get(key));
            pos++;
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        return getMap().toString();
    }
}
