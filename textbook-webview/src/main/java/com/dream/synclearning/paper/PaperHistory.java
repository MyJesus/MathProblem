package com.dream.synclearning.paper;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class PaperHistory {
	
	private String tag = "PaperHistory --- ";
	
	public long endTime;
	
	public int uid;
	
	public String name;
	
	public int grade;
	
	public int userScore;
	
	public double correctRate;
	
	public int finished;
	
	public int originScore;
	
	public long startTime;
	
	public long time;
	
	public long duration;
	
	public String guid;
	
	public String examId;
	
	public int id;
	
	public int subject;
	
	public ArrayList<String> userAnswerList;
	
	public ArrayList<QuestionHistory> questionHistoryList;
	
	public PaperHistory() {
		userAnswerList = new ArrayList<String>();
		questionHistoryList = new ArrayList<QuestionHistory>();
	}
	
	public boolean parseJson(JSONObject jsonObject) {
		boolean ret = true;
		try {
			userAnswerList.clear();
			questionHistoryList.clear();
			
			endTime = jsonObject.optLong("endTime");
			uid = jsonObject.optInt("uid");
			name = jsonObject.optString("name");
			grade = jsonObject.optInt("grade");
			userScore = jsonObject.optInt("userScore");
			correctRate = jsonObject.optDouble("correctRate");
			finished = jsonObject.optInt("finished");
			originScore = jsonObject.optInt("originScore");
			startTime = jsonObject.optLong("startTime");
			time = jsonObject.optLong("time");
			duration = jsonObject.optLong("duration");
			guid = jsonObject.optString("guid");
			examId = jsonObject.optString("examId");
			id = jsonObject.optInt("id");
			subject = jsonObject.optInt("subject");
			
			JSONArray jsonArrayAnswer = jsonObject.optJSONArray("userAnswer");
			if (jsonArrayAnswer != null && jsonArrayAnswer.length() > 0) {
				for (int i = 0; i < jsonArrayAnswer.length(); i++) {
					String string = jsonArrayAnswer.getString(i);
					userAnswerList.add(string);
				}
			}
			
			JSONArray jsonArrayQst = jsonObject.optJSONArray("children");
			if (jsonArrayQst != null && jsonArrayQst.length() > 0) {
				for (int i = 0; i < jsonArrayQst.length(); i++) {
					JSONObject jsonObjectQst = jsonArrayQst.optJSONObject(i);
					QuestionHistory questionHistory = new QuestionHistory();
					questionHistory.parseJson(jsonObjectQst);
					questionHistoryList.add(questionHistory);
				}
			}
		} catch (JSONException e) {
			ret = false;
			Log.e(tag, "parseJson() --- error ! e = "+e.getMessage());
		}
		
		return ret;
	}
	
	public QuestionHistory getQuestionHistory(int qstId)
	{
		for (QuestionHistory questionHistory : questionHistoryList)
		{
			if(questionHistory.id == qstId)
			{
				return questionHistory;
			}
		}
		return null;
	}
}
