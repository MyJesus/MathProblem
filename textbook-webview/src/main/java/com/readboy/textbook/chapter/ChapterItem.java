package com.readboy.textbook.chapter;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;


public class ChapterItem {
	private String tag = "ChapterItem --- ";
	
	/**
	 * 目录id
	 */
	public int id;
	
	/**
	 * 书本id
	 */
	public int souce;
	
	/**
	 * 目录名称
	 */
	public String name;
	
	/**
	 * 对应的上一级的章节id
	 */
	public int sid;
	
	/**
	 * 课程id
	 */
	public int courseId;
	
	/**
	 * 0：普通文本标题(目录)，默认值 
200+0：module讲解类 
200+1：module试题类，不计分，不计时 
200+20：module考卷类，采用标准试卷结构 
200+50：module超链接类，可链接各类模块 
200+60：module提示信息类，五三数据用它挂到单元下
	 */
	public int type;
	
	/**
	 * 标题层级
	 */
	public int level;
	
	/**
	 * 有几个child,只有当count值为0时才会有content值
	 */
	public int count;
	
	public Content[] content;
	
	public ArrayList<ChapterItem> mChapterItemList;
	
	public ChapterItem(){
		mChapterItemList = new ArrayList<ChapterItem>();
	}
	
	public boolean parseJson(JSONObject jsonObject) {
		try {
			this.id = jsonObject.optInt("id");
			this.souce = jsonObject.optInt("souce");
			this.name = jsonObject.optString("name");
			this.sid = jsonObject.optInt("sid");
			this.courseId = jsonObject.optInt("courseId");
			this.type = jsonObject.optInt("type");
			this.level = jsonObject.optInt("level");
			this.count = 0;
			
			JSONArray array = jsonObject.optJSONArray("children");
			if (array != null && array.length() > 0) {
				this.count = array.length();
				for (int i = 0; i < array.length(); i++) {
					ChapterItem chapterItem = new ChapterItem();
					chapterItem.parseJson(array.getJSONObject(i));
					mChapterItemList.add(chapterItem);
				}
			} else if (array == null) {
				JSONArray contentJsonArray = jsonObject.optJSONArray("content");
				int length = contentJsonArray.length();
				content = new Content[length];
				for (int i = 0; i < length; i++) {
					content[i] = new Content();
					content[i].parseJson(contentJsonArray.getJSONObject(i));
				}
			}
		} catch (JSONException e) {
			Log.e(tag, "e = "+e.toString()+", jsonObject = "+jsonObject.toString());
			return false;
		}
		
		return true;
	}
}
