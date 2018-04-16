package com.dream.synclearning.paper;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Children {
	
	public String tag = "Children --- ";
	
	/**
	 * 编号
	 */
	public int id;
	
	/**
	 * 名称
	 */
	public String name;
	
	/**
	 * 值定义
	  20：考卷一级标题(目录)
	  21：考卷的计分选择类型题目
	  22：考卷的计分多项选择类型题目
	  23：考卷的填空类型题目(可作答，可计分)
	  24：考卷的填空类型题目(可作答，不可计分)
	  25：考卷的填空类型题目(不可作答，不可计分)
	  26：考卷的判断对错类型题目
	  27：考卷的完形填空类型题目
	  28：考卷的选句填空类型题目
	 */
	public int type;
	
	/**
	 * 出处
	 */
	public int source;
	
	/**
	 * 课程
	 */
	public int courseId;
	
	/**
	 * 分数
	 */
	public int score;
	
	/**
	 * 附加信息
	 * 仅用于"听力"的示例短文及声音，只存在于最后一级目录
	 */
	public String addition;
	
	/**
	 * 层次
	 */
	public int level;
	
	/**
	 * 当没有下一级时存在试题
	 */
	public ArrayList<JSONObject> qstList;
	
	public ArrayList<Children> childrenList;
	
	public Children() {
		qstList = new ArrayList<JSONObject>();
		childrenList = new ArrayList<Children>();
	}
	
	public boolean parseJson(JSONObject jsonObject) {
		try {
			id = jsonObject.getInt("id");
			name = jsonObject.optString("name");
			type = jsonObject.optInt("type");
			source = jsonObject.optInt("source");
			courseId = jsonObject.optInt("courseId");
			score = jsonObject.optInt("score");
			addition = jsonObject.optString("addition");
			level = jsonObject.optInt("level");
			
			JSONArray jsonArray = jsonObject.optJSONArray("children");
			if (jsonArray != null && jsonArray.length() > 0) {
				for (int i = 0; i < jsonArray.length(); i++) {
					Children children = new Children();
					children.parseJson(jsonArray.getJSONObject(i));
					childrenList.add(children);
				}
			} else if (jsonArray == null) {
				JSONArray jsonArrayQst = jsonObject.optJSONArray("qst");
				if (jsonArrayQst != null && jsonArrayQst.length() > 0) {
					for (int j = 0; j < jsonArrayQst.length(); j++) {
//						Qst qst = new Qst();
//						qst.parseJson(jsonArrayQst.getJSONObject(j));
						qstList.add(jsonArrayQst.getJSONObject(j));
					}
				}
			}
			
		} catch (JSONException e) {
			Log.e(tag, "parseJson() --- jsonObject.toString() = "+jsonObject.toString());
			Log.e(tag, "parseJson() --- e = "+e.getMessage());
			
			return false;
		}
		
		return true;
	}
}
