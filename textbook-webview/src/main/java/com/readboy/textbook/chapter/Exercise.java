package com.readboy.textbook.chapter;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Exercise {
	
	private String tag = "Exercise --- ";
	
	/**
	 * 题目编号
	 */
	public int qid;
	
	/**
	 * 题目序号
	 */
	public String no;

	/**
	 * 题目分数
	 */
	public int score;
	
	public Exercise() {
		qid = -1;
		no = null;
		score = -1;
	}
	
	public boolean parseJson(JSONObject jsonObject) {
		try {
			if (jsonObject.has("qid")) {
				this.qid = jsonObject.getInt("qid");
			}
			
			if (jsonObject.has("no")) {
				this.no = jsonObject.getString("no");
			}
			
			if (jsonObject.has("score")) {
				this.score = jsonObject.getInt("score");
			}
		} catch (JSONException e) {
			Log.e(tag, "e = "+e.toString()+", jsonObject = "+jsonObject.toString());
			
			return false;
		}
		return true;
	}
}
