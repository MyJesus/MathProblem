package com.dream.synclearning.paper;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class QuestionHistory {
	private String tag = "QuestionHistory --- ";
	
	public long endTime;
	
	public int from;
	
	public int uid;
	
	public int grade;
	
	public long time;
	
	public int userScore;
	
	public double correctRate;
	
	public int qstType;
	
	public long startTime;
	
	public int qstCategory;
	
	public long duration;
	
	public String guid;
	
	public String examId;
	
	public int id;
	
	public int subject;
	
	public ArrayList<String> userAnswerList;
	
	public QuestionHistory() {
		userAnswerList = new ArrayList<String>();
	}
	
	public boolean parseJson(JSONObject jsonObject) {
		boolean ret = true;
		try {
			userAnswerList.clear();
			
			endTime = jsonObject.optLong("endTime");
			from = jsonObject.optInt("from");
			uid = jsonObject.optInt("uid");
			grade = jsonObject.optInt("grade");
			time = jsonObject.optLong("time");
			userScore = jsonObject.optInt("userScore");
			correctRate = jsonObject.optDouble("correctRate");
			qstType = jsonObject.optInt("qstType");
			startTime = jsonObject.optLong("startTime");
			qstCategory = jsonObject.optInt("qstCategory");
			duration = jsonObject.optLong("duration");
			guid = jsonObject.optString("guid");
			examId = jsonObject.optString("examId");
			id = jsonObject.optInt("id");
			subject = jsonObject.optInt("subject");
			
			JSONArray jsonArray = jsonObject.optJSONArray("userAnswer");
			if (jsonArray != null && jsonArray.length() > 0) {
				for (int i = 0; i < jsonArray.length(); i++) {
					String string = jsonArray.getString(i);
					userAnswerList.add(string);
				}
			}
			
		} catch (JSONException e) {
			ret = false;
			Log.e(tag, "parseJson() --- error ! e = "+e.getMessage());
		}
		
		return ret;
	}
}
