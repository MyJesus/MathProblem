package com.example.errorqstupload.bean.item;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by guh on 2017/8/1.
 */

public class KeyPoint {

    /**    知识点编号    */
    private int mId = 0;

    private int mTotal = 0;

    private int mRight = 0;

    /**    知识点名称    */
    private String mName = null;

    public KeyPoint(int id, String name) {
        mId = id;
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public int getId() {
        return mId;
    }

    public int getTotal() {
        return mTotal;
    }

    public int getRight() {
        return mRight;
    }

    public void setTotal(int total) {
        mTotal = total;
    }

    public void setRight(int right) {
        mRight = right;
    }

    public JSONObject getJsonObject() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("k_id", mId);
        obj.put("k_total", mTotal);
        obj.put("k_right", mRight);
        try {
            String encodeStr = new String(mName.getBytes(),"utf-8");
            obj.put("k_name", encodeStr);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return obj;
    }



}
