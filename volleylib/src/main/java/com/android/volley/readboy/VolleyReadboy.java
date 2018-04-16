package com.android.volley.readboy;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.SSLSocketFactoryEx;
import com.android.volley.toolbox.Volley;

import org.apache.http.HttpVersion;
import org.apache.http.client.CookieStore;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.security.KeyStore;

/**
 * 访问个人中心接口需要调用初始化
 * Created by ldw on 2017/3/21.
 */

public class VolleyReadboy {

    /**
     * 请求个人中心的接口需要添加这个
     * @param context
     */
    public static RequestQueue init(Context context){
        RequestQueue requestQueue;
        DefaultHttpClient mHttpClient;
        CookieStore mCookieStore = new PersistentCookieStore(context);
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);  //允许所有主机的验证
            //HttpParams params = new BasicHttpParams();
            BasicHttpParams httpParams = new BasicHttpParams();
            HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(httpParams, HTTP.DEFAULT_CONTENT_CHARSET);
            HttpProtocolParams.setUserAgent(httpParams, getMachineModule());
            HttpProtocolParams.setUseExpectContinue(httpParams, true);

            //BasicHttpParams httpParams = new BasicHttpParams();
            SchemeRegistry schemeRegistry = new SchemeRegistry();
            schemeRegistry.register(new Scheme("http", PlainSocketFactory
                    .getSocketFactory(), 80));
            schemeRegistry.register(new Scheme("https", sf, 443));
            ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(
                    httpParams, schemeRegistry);
            mHttpClient = new DefaultHttpClient(cm, httpParams);

            mHttpClient.setCookieStore(mCookieStore);

            requestQueue = Volley.newRequestQueue(context,new HttpClientStack(mHttpClient));
        }catch(Exception e){
            mHttpClient = new DefaultHttpClient();
            mHttpClient.setCookieStore(mCookieStore);
            requestQueue = Volley.newRequestQueue(context,new HttpClientStack(mHttpClient));
        }
        return requestQueue;
    }

    /**
     * 获得机器型号
     */
    private static String getMachineModule() {
        String module = "";
        try {
            Class<android.os.Build> build_class = android.os.Build.class;
            // 取得牌子
            // java.lang.reflect.Field manu_field = build_class
            // .getField("MANUFACTURER");
            // manufacturer = (String) manu_field.get(new android.os.Build());
            // 取得型號
            java.lang.reflect.Field field2 = build_class.getField("MODEL");
            module = (String) field2.get(new android.os.Build());
        } catch (Exception e) {
            module = "unkown";
        }
        return module;
    }

}
