package com.dream.synclearning.paper;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PaperContent {
	
	public String tag = "PaperContent --- ";
	
	/**
	 * 编号
	 */
	public int id;
	
	/**
	 * 名称
	 */
	public String name;
	
	/**
	 * 年级
	 */
	public int grade;
	
	/**
	 * 科目
	 */
	public int subject;
	
	/**
	 * 课程
	 */
	public int courseId;
	
	/**
	 * 类型定义
	  101:单元检测
	  102:期中检测
	  103:期末检测
	  104:小升初真题
	  105:中考真题
	  106:高考真题
	  107:小升初模拟
	  108:中考模拟
	  109:高考模拟
	  110月考
	  111会考
	 */
	public int type;
	
	/**
	 * 年份
	 */
	public int year;
	
	/**
	 * 创建时间
	 */
	public int createTime;
	
	/**
	 * 更新时间
	 */
	public int updateTime;
	
	/**
	 * 试卷出处（来自具体书本）
	 */
	public int source;
	
	/**
	 * 文件名
	 */
	public String fileName;
	
	/**
	 * 章节编号（来自具体书本章节）
	 */
	public int sid;
	
	/**
	 * 试卷数据出处
	 * 1-五三试卷；2-名校名卷（有地区，可能有学校）
	 */
	public int from;
	
	/**
	 * 附加信息（考试公式提示、说明等信息），数据是xml格式
	 */
	public String addition;
	
	/**
	 * 考试时间
	 */
	public int time;
	
	/**
	 * 考试总分
	 */
	public int score;
	
	/**
	 * 状态
	 * 0-正常
	 * 1-旧版本已废弃
	 * 2-未完成
	 * 3-冻结
	 */
	public int status;
	
	/**
	 * 0:未收藏；1：已收藏
	 */
	public int star;
	
	/**
	 * 试卷的地区信息
	 */
	public Region region;

	/**
	* 试题的大题，如填空题，简答题，作文题等；
	*/
	public ArrayList<Children> childrenList;
	
	public PaperContent() {
		childrenList = new ArrayList<Children>();
	}
	
	public boolean parseJson(JSONObject jsonObject) {
		childrenList.clear();
		try {
			id = jsonObject.getInt("id");
			name = jsonObject.optString("name");
			grade = jsonObject.optInt("grade");
			subject = jsonObject.optInt("subject");
			courseId = jsonObject.optInt("courseId");
			type = jsonObject.optInt("type");
			year = jsonObject.optInt("year");
			createTime = jsonObject.optInt("createTime");
			updateTime = jsonObject.optInt("updateTime");
			source = jsonObject.optInt("source");
			fileName = jsonObject.optString("fileName");
			sid = jsonObject.optInt("sid");
			from = jsonObject.optInt("from");
			addition = jsonObject.optString("addition");
			time = jsonObject.optInt("time");
			score = jsonObject.optInt("score");
			status = jsonObject.optInt("status");
			star = jsonObject.optInt("star");
			
			JSONObject jsonObjectRegion = jsonObject.optJSONObject("region");
			if (jsonObjectRegion != null) {
				region = new Region();
				region.parseJson(jsonObjectRegion);
			}
			
			JSONArray jsonArray = jsonObject.optJSONArray("children");
			if (jsonArray != null && jsonArray.length() > 0) {
				for (int i = 0; i < jsonArray.length(); i++) {
					Children children = new Children();
					children.parseJson(jsonArray.getJSONObject(i));
					childrenList.add(children);
				}
			}
		} catch (JSONException e) {
			Log.e(tag, "parseJson() --- jsonOjbect.toString() = "+jsonObject.toString());
			Log.e(tag, "parseJson() --- e = "+e.getMessage());
			
			return false;
		}
		
		return true;
	}
}
