package com.readboy.mathproblem.http.auth;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by guh on 2017/8/1.
 */

public class MyJson {
    private static final String TAG = "MyJson";

    /**
     * 获取评测记录
     * @param jsonPath
     * @return
     */
    public static Authentication authentication(String jsonPath) {
        JSONObject jsonObj = getJsonDataFromKeyUtf8(jsonPath);
        Authentication auth = null;
        if (jsonObj != null && jsonObj.optInt("ok")==1) {
            Log.e(TAG, "authentication: jsonStr = " + jsonObj.toString());
            JSONArray jsobjdb = jsonObj.optJSONArray("data");
            if (jsobjdb != null && jsobjdb.length()>0) {
                for (int i=0; i<jsobjdb.length(); i++) {
                    JSONObject authobj = jsobjdb.optJSONObject(i);
                    if (authobj != null && "elpsky".equals(authobj.optString("type"))) {
                        auth = new Authentication(authobj.optString("domain"), authobj.optString("privateKey"), authobj.optInt("timestamp"));
                        break;
                    }
                }
            }
        }

        return auth;
    }


    /**
     * 读取.json文件的内容
     * @param jsonPath: .json文件的路径
     * @return 返回一个JSONObject, 错误返回null；
     */
    public static JSONObject getJsonDataFromKeyUtf8(String jsonPath) {
        File fl = new File(jsonPath);
        JSONObject jsonBack = null;
        if (fl.exists()) {
            FileInputStream fin = null;
            try {
                fin = new FileInputStream(fl);

                byte []arrNetJson = null;
                if (fin.available() > 4){
                    arrNetJson = new byte[fin.available()];
                    if (fin != null){
                        fin.read(arrNetJson);
                    }
                }

                if (arrNetJson != null) {
                    String strjson = new String(arrNetJson);
                    jsonBack = new JSONObject(strjson);
                }


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("", "==divhee=====onSuccess=error==1===");
            } finally {
                try {
                    if (fin != null) {
                        fin.close();
                        fin = null;
                    }
                } catch (IOException e1) {
                }
            }
        }

        return jsonBack;
    }


    private static String readString(InputStream io) {
        // 读取txt内容为字符串
        StringBuffer txtContent = new StringBuffer();
        // 每次读取的byte数
        byte[] b = new byte[8 * 1024];
        InputStream in = io;
        try {
            // 文件输入流
            while (in.read(b) != -1) {
                // 字符串拼接
                txtContent.append(new String(b));
            }
            // 关闭流
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return txtContent.toString();
    }

}
