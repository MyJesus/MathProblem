package com.readboy.mathproblem.http.request;


import com.readboy.auth.Auth;
import com.readboy.mathproblem.util.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by oubin on 2017/9/19.
 */

public class IdsParams extends BaseRequestParams {

    private List<Integer> idList = new ArrayList<>();
    private String sn;
//    private static final String deviceId = BuildUtils.getDeviceId();
    private String deviceId;
    private String date;

    public IdsParams(List<Integer> list) {
        this.idList.clear();
        this.idList.addAll(list);
//        this.date = String.valueOf(System.currentTimeMillis() / 1000);
//        sn = NativeApi.getSignature(date);

        Properties properties = Auth.getSignature();
        this.date = properties.getProperty("t");
        this.sn = properties.getProperty("sn");
        this.deviceId = properties.getProperty("device_id");
    }

    @Override
    public Map<String, String> getMap() {
        Map<String, String> params = super.getMap();
        params.put("sn", sn);
        params.put("device_id", deviceId);
        params.put("t", date);
        params.put("ids", Lists.unite(",", idList));
        return params;
    }
}
