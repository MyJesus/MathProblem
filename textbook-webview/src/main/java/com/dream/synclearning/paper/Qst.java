package com.dream.synclearning.paper;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * 试题结构
 * @author guh
 *
 */
public class Qst {
	
	public String tag = "Qst --- ";
	
	public int qid;
	
	public int score;
	
	public String no;
	
	public Qst() {
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
