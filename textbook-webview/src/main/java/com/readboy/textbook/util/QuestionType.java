package com.readboy.textbook.util;

public class QuestionType
{
	/***大题(下面有小题)*/
	public static final String BIG_QUESTION_TYPE = "0";
	/**单项选择题*/
	public static final String ONE_CHOICE_TYPE = "101";
	/**不定项选择题*/
	public static final String MORE_CHOICE_TYPE = "102";
	/**判断对错题*/
	public static final String JUDGE_TYPE = "103";
	/**填空题*/
	public static final String FILL_BLANK_TYPE = "201";
	/**作答题*/
	public static final String LONG_ANSWER_TYPE = "202";
	/**不可作答不可评分填空*/
	public static final String UNABLE_ANSWER_TYPE = "203";
	/***英语完型填空*/
	public static final String CLOZE_TYPE = "104";
	/**选择句子题*/
	public static final String SELECT_SENTENCE_TYPE = "105";
	/***英语阅读理解*/
	public static final String READING_TYPE = "106";

	public static final String BRACKET_REGEX = "\\(.blk.*\\)|（.blk.*）|<blk.*.*</blk>?";


	/**中文空格点位符*/
	public static final String PLACEHOLDER_CN = "";
	
	public static final String UNDERLINE_STRING = "____ ";
}
