package com.readboy.textbook.chapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;


public class Content {
	private String tag = "Content --- ";
	
	/**
	 * 目录名称
	 */
	public String name;
	
	/**
	 * 0：普通文本标题(目录)，默认值 
20：考卷一级标题(目录) 
21：考卷的计分选择类型题目 
22：考卷的计分多项选择类型题目 
23：考卷的填空类型题目(可作答，可计分) 
24：考卷的填空类型题目(可作答，不可计分) 
25：考卷的填空类型题目(不可作答，不可计分) 
26：考卷的判断对错类型题目 
27：考卷的完形填空类型题目 
28：考卷的选句填空类型题目 
50：超链接类，功能同type=250 
200+0：module讲解类 
200+1：module试题类，不计分，不计时 
200+20：module考卷类，采用标准试卷结构 
200+50：module超链接类，可链接各类模块 
200+60：module提示信息类，五三数据用它挂到单元下
	 */
	public int type;
	
	/**
	 * 试题类才有分数
	 */
	public int score;
	
	public String explain;
	
	public JSONArray comment;
	
	public Exercise[] exercise;
	
	public Content() {
		name = null;
		type = -1;
		score = -1;
		explain = null;
		comment = null;
	}
	
	public boolean parseJson(JSONObject jsonObject) {
		try {
			if (jsonObject.has("name")) {
				this.name = jsonObject.getString("name");
			}
			
			if (jsonObject.has("type")) {
				this.type = jsonObject.getInt("type");
			}
			
			if (jsonObject.has("score")) {
				this.score = jsonObject.getInt("score");
			}
			
			if (jsonObject.has("explain")) {
				this.explain = jsonObject.getString("explain");
			}
			
			if (jsonObject.has("comment")) {
				this.comment = jsonObject.getJSONArray("comment");
			}
			
			if (jsonObject.has("exercise")) {
				JSONArray exerciseJsonArray = jsonObject.optJSONArray("exercise");
				int length = exerciseJsonArray.length();
				exercise = new Exercise[length];
				for (int i = 0; i < length; i++) {
					exercise[i] = new Exercise();
					exercise[i].parseJson(exerciseJsonArray.getJSONObject(i));
				}
			}
		} catch (JSONException e) {
			Log.e(tag, "e = "+e.toString()+", jsonObject = "+jsonObject.toString());
			
			return false;
		}
		
		return true;
	}
}
