package com.readboy.mathproblem.http.request;

import com.readboy.auth.Auth;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by oubin on 2017/9/16.
 */

public class RequestParams {

    private String sn;
//    private static final String deviceId = BuildUtils.getDeviceId();
    private String deviceId;
    private String date;
    private String grade;

    public RequestParams(String grade) {
        this.grade = grade;
//        this.date = String.valueOf(System.currentTimeMillis() / 1000);
//        sn = NativeApi.getSignature(date);

        Properties properties = Auth.getSignature();
        this.date = properties.getProperty("t");
        this.sn = properties.getProperty("sn");
        this.deviceId = properties.getProperty("device_id");
    }

    public Map<String, String> getMap() {
        Map<String, String> params = new HashMap<>(4);
        params.put("sn", sn);
        params.put("device_id", deviceId);
        params.put("t", date);
        params.put("grade", grade);
        return params;
    }

    @Override
    public String toString() {
        return getMap().toString();
    }
}
