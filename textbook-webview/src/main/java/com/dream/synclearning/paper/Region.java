package com.dream.synclearning.paper;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * 试卷的地区信息
 * @author guh
 *
 */
public class Region {
	
	public String tag = "Region --- ";
	
	/**
	 * 省份编号
	 */
	public int province;
	
	/**
	 * 市编号
	 */
	public int city;
	
	/**
	 * 区/县编号
	 */
	public int district;
	
	/**
	 * 学校编号
	 */
	public int school;
	
	public Region() {
		province = -1;
		city = -1;
		district = -1;
		school = -1;
	}
	
	public boolean parseJson(JSONObject jsonObject) {
		try {
			province = jsonObject.getInt("province");
			city = jsonObject.optInt("city");
			district = jsonObject.optInt("district");
			school = jsonObject.optInt("school");
		} catch (JSONException e) {
			Log.e(tag, "parseJson() --- jsonObject.toString() = "+jsonObject.toString());
			Log.e(tag, "parseJson() --- e = "+e.getMessage());
			
			return false;
		}
		
		return true;
	}
}
