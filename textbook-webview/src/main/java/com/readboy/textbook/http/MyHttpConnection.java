package com.readboy.textbook.http;

import java.util.Map;

import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;

import android.app.DownloadManager.Request;
import android.content.Context;

import com.readboy.textbook.util.NetWorkUtils;

public class MyHttpConnection
{
	private static MyHttpConnection mInstance=null;
//	private RequestQueue mRequestQueue;
	private Context mContext=null;
	private DefaultHttpClient mHttpClient=null;
	
	public static final String UNKNOWN_ERROR = "未知错误";
	public static final String NOT_NETWORK = "网络不可用！！";
	public static final String PARAMS_ERROR = "参数出错！！";
	
    /** Base URL for the v2 Sample Sync Service */
    public static final String BASE_URL = "http://192.168.20.171/webview/textbook";
    /** URI for sync service */
    public static final String GET_BOOK_CONTENT_URL = BASE_URL + "/book.php/get?id=7";
    
    public static final String GET_BOOK_QUESTION_URL = "http://192.168.20.235/questions?sn=tikutest&";
    
    public static final String GET_BOOK_WISDOM_URL = BASE_URL + "/book.php/wisdom";
	
	/**
	 * Url访问网络监听器
	 */
	public static interface UrlListener 
	{
		/**
		 * 访问成功
		 * @param result 返回的对象
		 */
		public void onResult(String result);
		/**
		 * 访问失败
		 * @param msg 错误信息
		 */
		public void onError(String msg);
	}
	
	public MyHttpConnection(Context context)
	{
		mContext = context;
		try
		{
//			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
//			trustStore.load(null, null);
//			SSLSocketFactory sf = new com.android.volley.toolbox.SSLSocketFactoryEx(trustStore);
//			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);//允许所有主机的验证
	
			BasicHttpParams httpParams = new BasicHttpParams();
			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
//			schemeRegistry.register(new Scheme("https", sf, 443));
			ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(httpParams, schemeRegistry);
			
			mHttpClient = new DefaultHttpClient(cm, httpParams);
//			mHttpClient.setCookieStore(mCookieStore);
		}
		catch(Exception e)
		{
			return;
		}

//		mRequestQueue = Volley.newRequestQueue(context,new HttpClientStack(mHttpClient));
//		mRequestQueue.start();
	}
	
	public static MyHttpConnection getInstance(Context context)
	{
		if (mInstance == null)
		{
			mInstance = new MyHttpConnection(context);
		}
		return mInstance;
	}
	
//	/**
//	 * Adds the specified request to the global queue 
//	 * @param req
//	 */
//	private <T> void addToRequestQueue(Request<T> req)
//	{
//		req.setTag("URLConnection");
//		mRequestQueue.add(req);
//	}
//	
//	/**
//	 * 取消所有请求,activity退出时调用
//	 */
//	public void cancel()
//	{
//		mRequestQueue.cancelAll(mContext);
//	}
//	
//	/**
//	 * Volley的StringRequest请求，以POST方式，支持参数，适合隐藏用户重要信息
//	 * @param url 网络请求地址
//	 * @param params 参数集合
//	 * @param listen 请求反馈Listener
//	 */
//	public void httpStringRequest(String url, final Map<String, String> params, final UrlListener listen)
//	{
//		if(!NetWorkUtils.isNetworkAvailable(mContext))
//		{
//			listen.onError(NOT_NETWORK);
//			return;
//		}
//		
//		StringRequest mRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
//			@Override
//			public void onResponse(String response) {
//					listen.onResult(response);
//			}
//		}, new Response.ErrorListener(){
//
//			@Override
//			public void onErrorResponse(VolleyError error)
//			{
//				String errMsg = error.getMessage();
//				listen.onError( errMsg );
//				
//			}
//		}){
//			@Override
//			protected Map<String, String> getParams() throws AuthFailureError {
//				return params;
//		}};
//	
//		addToRequestQueue(mRequest);
//	}
}
