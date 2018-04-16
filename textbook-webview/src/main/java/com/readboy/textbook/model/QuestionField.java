package com.readboy.textbook.model;

public class QuestionField 
{
	/**题目ID*/
	public static final String ID_KEY = "id";
	/**题目ID*/
	public static final String QID_KEY = "qid";
	/**题目类型 大题：0 英语完形填空：104 英语选择句子：105 英语阅读理解：106 单选题：101 多选题：102 判断题：103
	可作答可评分填空： 201 可作答不可评分填空： 202 不可作答不可评分填空：203*/
	public static final String TYPE_KEY = "type";
	/**题干*/
	public static final String CONTENT_KEY = "content";
	/**选项/材料*/
	public static final String ACCESSORY_KEY = "accessory";
	/**正确答案(用于程序判断)**/
	public static final String CORRECT_ANSWER_KEY = "correctAnswer";
	/**解答**/
	public static final String SOLUTION_KEY = "solution";
	/**解析**/
	public static final String SOLUTION_ACCESSORY_KEY = "solutionAccessory";
	/***大小题(1-大题,2-小题，0-普通题*/
	public static final String ROLE_KEY = "role";
	/**答案(显示给用户)**/
	public static final String ANSWER_KEY = "answer";
	/**题目序号*/
	public static final String NO_KEY = "no";
	/***知识点*/
	public static final String KEYPOINT_KEY = "keypoint";
	/**题目来源(用于统计)*/
	public static final String FROM_KEY = "from";
	/***题目类型(用于统计)**/
	public static final String CATEGORY_KEY = "category";
	/***大题下对应的小题*/
	public static final String RELATION_KEY = "relation";
	/***题目选项*/
	public static final String OPTION_KEY = "options";
	/**分数**/
	public static final String SCORE= "score";		
}
