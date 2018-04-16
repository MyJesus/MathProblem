package com.android.volley.toolbox;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestParams;
import com.android.volley.Response;

public class UploadFileRequest extends Request<JSONObject> {
	private Response.Listener<JSONObject> mListener = null;
	private RequestParams mParams = null;
	private HttpEntity httpEntity = null;
	
	public UploadFileRequest(String url, RequestParams params, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener)
	{
		this(Method.POST, url, params, listener, errorListener);
	}
	
	public UploadFileRequest(int method, String url, RequestParams params, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener)
	{
		super(method, url, errorListener);
		
		mParams = params;
		mListener = listener;
	}

	@Override
	public byte[] getBody() throws AuthFailureError {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if(mParams != null) {
			
			/*if(httpEntity == null)
				httpEntity = mParams.getEntity();*/ 
			
			try {
				httpEntity.writeTo(baos);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		return baos.toByteArray();
	}
	
	@Override
	protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
		
		Log.e("xxx", "------------");
		
		try {
			String jsonString = new String(response.data,
					HttpHeaderParser.parseCharset(response.headers));
			Log.e("xxx", "-------jsonString = " + jsonString);
			return Response.success(new JSONObject(jsonString),
					HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			//errorListener.setFlag(false);
			return Response.error(new ParseError(response));
		} catch (JSONException je) {
			//errorListener.setFlag(false);
			return Response.error(new ParseError(response));
		}
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		Map<String, String> headers = super.getHeaders();
		if (null == headers || headers.equals(Collections.emptyMap())) {
			headers = new HashMap<String, String>();
		}
		//MainApplication.getInstance().addSessionCookie(headers);
		return headers;
	}
	
	@Override
	public String getBodyContentType() {
		
		if(httpEntity == null){
			httpEntity = mParams.getEntity(); 
		}
		
		return httpEntity.getContentType().getValue();
	}
	
	@Override
	protected void deliverResponse(JSONObject response) {
		mListener.onResponse(response); 
	}
}