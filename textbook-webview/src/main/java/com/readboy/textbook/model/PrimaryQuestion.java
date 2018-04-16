package com.readboy.textbook.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;

import android.text.TextUtils;

import com.readboy.textbook.chapter.Content;
import com.readboy.textbook.model.PrimaryQuestion.Item.AnswerInfo;
import com.readboy.textbook.model.PrimaryQuestion.Item.KeyPoint;
import com.readboy.textbook.util.PrimaryQuestionUtils;
import com.readboy.textbook.util.QuestionType;

public class PrimaryQuestion
{
	/**题目集合**/
	private HashMap<String, Item> mQuestionItemMap = new HashMap<String, Item>();
	/**题目ID**/
	private ArrayList<String> mQuestionIds = new ArrayList<String>();	
	/**作答信息**/
	private ArrayList<AnswerInfo> mAnswerInfos = null;

	private static PrimaryQuestion instance;

	public static PrimaryQuestion getInstance()
	{
		if (instance == null)
		{
			instance = new PrimaryQuestion();
		}
		return instance;
	}

	public PrimaryQuestion()
	{
		instance = this;
	}

	public HashMap<String, Item> getQuestionItemMap()
	{
		return mQuestionItemMap;
	}
	
	public ArrayList<String> getQuestionIds()
	{
		return mQuestionIds;
	}
	
	/**
	 * 移除题目中的多媒体标签
	 * @param content
	 * @return
	 */
	private String removeMediaTag(String content)
	{
		return content.replaceAll(MyHtml.AUDIO_TAG_REGEX, "");
	}

	/**加入一道题目*/
	public void addQuestionItem(Item item)
	{
		if(item != null)
		{			
			mQuestionItemMap.put(item.mId, item);
			if(!item.mIsChildren)
			{
				mQuestionIds.add(item.mId);
			}
		}
	}
	
	/**加入一道题目*/
	public void addQuestionItem(Item item, String title)
	{
		if(item != null)
		{	
			item.setTitle(title);
			mQuestionItemMap.put(item.mId, item);
			if(!item.mIsChildren)
			{
				mQuestionIds.add(item.mId);
			}
		}
	}
	
	/**加入一道小题*/
	private void addChildrenQuestionItem(Item item)
	{
		if(item != null)
		{
			item.addChildrenQuestionId(item.mId);
			mQuestionItemMap.put(item.mId, item);
		}
	}

	/**
	 * 根据题目id获取题目
	 * @param id
	 * @return item/null
	 */
	public Item getQuestion(String questionId)
	{
		Item item = mQuestionItemMap.get(questionId);
		return item != null ? item : null;
	}

	/**
	 * 根据题号获取题目
	 * @param index
	 * @return item/null
	 */
	public Item getQuestion(int index)
	{
		Item item = null;
		if (index > -1 && index < mQuestionIds.size())
		{
			item = mQuestionItemMap.get(mQuestionIds.get(index));
		}
		return item != null ? item : null;
	}

	/**
	 * 根据题号获取题目ID
	 * @param index
	 * @return questionId/"-1"
	 */
	public String getQuestionId(int index)
	{
		if (index > -1 && index < mQuestionIds.size())
		{
			return mQuestionIds.get(index);
		}
		return "-1";
	}
	
	/**
	 * 获取题目总数
	 * @return
	 */
	public int getQuestionCount()
	{
		return mQuestionIds.size();
	}

	/**
	 * set 选择题答案
	 * @param questionId
	 * @param userAnswer
	 */
	public void setUserAnswer(String questionId, String userAnswer)
	{
		Item item = mQuestionItemMap.get(questionId);
		if (item != null)
		{
			item.setUserAnswer(userAnswer);
		}
	}

	/**
	 * set 填空题答案  多个空答案以，号分割
	 * @param questionId
	 * @param userAnswer
	 */
	public void setBlankUserAnswer(String questionId, String userAnswer)
	{
		if (questionId != null)
		{
			String[] ids = questionId.split("-");
			if (ids.length == 2)
			{
				Item item = mQuestionItemMap.get(ids[0]);
				if (item != null)
				{
					String answer = item.getUserAnswerString();
					int index = Integer.parseInt(ids[1]);
					int answerCount = item.getAnswercount();
					if (!TextUtils.isEmpty(answer))
					{
						String[] answerArr = answer.split(",");
						if (answerCount > index)
						{
							answerArr[index] = userAnswer;
							StringBuilder answerBuilder = new StringBuilder();
							for (int i = 0; i < answerCount; i++)
							{
								answerBuilder.append(answerArr[i]);
								answerBuilder.append(",");
							}
							item.setUserAnswer(answerBuilder.toString());
						}

					}
					else
					{
						StringBuilder answerBuilder = new StringBuilder();
						for (int i = 0; i < answerCount; i++)
						{
							if (i == index)
							{
								answerBuilder.append(userAnswer);
							}
							else
							{
								answerBuilder.append(", ");
							}
						}
						String answerStr = answerBuilder.toString();
						// if(answerStr.charAt(answerStr.length()-1) == ',')
						// {
						// answerStr = answerStr.substring(0,
						// answerStr.length()-2);
						// }
						item.setUserAnswer(answerStr);
					}
				}
			}
		}

	}
	
	/**
	 * 从jsonArray 中构造题目集合
	 * @param jsonArray
	 */
	public void setQuestion(JSONArray jsonArray)
	{
		mQuestionItemMap.clear();
		mQuestionIds.clear();
		if(mAnswerInfos != null)
		{
			mAnswerInfos.clear();
			mAnswerInfos = null;
		}
		int length = jsonArray.length();
		for (int i = 0; i < length; i++)
		{
			JSONObject questionJsonObject = jsonArray.optJSONObject(i);
			if(questionJsonObject != null)
			{
				addQuestionItem(makeQuestionItem(questionJsonObject, null));
			}
		}
	}
	
	/**
	 * 从jsonArray 中构造题目集合,可以多次追加jsonArray题目
	 * @param jsonArray
	 */
	public void addQuestion(JSONArray jsonArray)
	{
		int length = jsonArray.length();
		for (int i = 0; i < length; i++)
		{
			JSONObject questionJsonObject = jsonArray.optJSONObject(i);
			if(questionJsonObject != null)
			{
				addQuestionItem(makeQuestionItem(questionJsonObject, null));
			}
		}
	}
	
	/**
	 * 从jsonArray 中构造题目集合,可以多次追加jsonArray题目
	 * @param jsonArray
	 */
	public void addQuestion(JSONArray jsonArray, String title)
	{
		int length = jsonArray.length();
		for (int i = 0; i < length; i++)
		{
			JSONObject questionJsonObject = jsonArray.optJSONObject(i);
			if(questionJsonObject != null)
			{
				addQuestionItem(makeQuestionItem(questionJsonObject, null), title);
			}
		}
	}
	
	
	/**
	 * 从questionjsonArray 中构造题目集合
	 * @param questionjsonArray
	 */
	public void setQuestion(JSONArray questionjsonArray, JSONArray questionTitleJsonArray)
	{
		mQuestionItemMap.clear();
		mQuestionIds.clear();
		if(mAnswerInfos != null)
		{
			mAnswerInfos.clear();
			mAnswerInfos = null;
		}
		int length = questionjsonArray.length();
		HashMap<String, String> titleHashMap = getBigQuestionTitle(questionTitleJsonArray);
		for (int i = 0; i < length; i++)
		{
			JSONObject questionJsonObject = questionjsonArray.optJSONObject(i);
			if(questionJsonObject != null)
			{
				addQuestionItem(makeQuestionItem(questionJsonObject, titleHashMap));
			}
		}
	}
	
	/**
	 * 构造带(type=0 大题)标题的题目集合
	 * @param questionjsonArray
	 * @param contents
	 */
	public void setQuestion(JSONArray questionjsonArray, Content[] contents)
	{
		mQuestionItemMap.clear();
		mQuestionIds.clear();
		if(mAnswerInfos != null)
		{
			mAnswerInfos.clear();
			mAnswerInfos = null;
		}
		int length = questionjsonArray.length();
		HashMap<String, String> titleHashMap = getBigQuestionTitle(contents);
		for (int i = 0; i < length; i++)
		{
			JSONObject questionJsonObject = questionjsonArray.optJSONObject(i);
			if(questionJsonObject != null)
			{
				addQuestionItem(makeQuestionItem(questionJsonObject, titleHashMap));
			}
		}
	}
	
	/**
	 * 
	 * @param contents
	 * @return
	 */
	private HashMap<String, String> getBigQuestionTitle(Content[] contents)
	{
		int length = contents.length;
		HashMap<String, String> titleHashMap = new HashMap<String, String>();
		for (int i = 0; i < length; i++)
		{
			Content content = contents[i];
			if(content != null)
			{
				int type = content.type;
				if(type == 0)
				{
					
					String title = content.name;
					if(!TextUtils.isEmpty(title))
					{
						String questionId = Integer.toString(content.exercise[0].qid);
						titleHashMap.put(questionId, title);
					}
				}
			}
		}
		return titleHashMap;
	}
	
	private HashMap<String, String> getBigQuestionTitle(JSONArray jsonArray)
	{
		int length = jsonArray.length();
		HashMap<String, String> titleHashMap = new HashMap<String, String>();
		for (int i = 0; i < length; i++)
		{
			JSONObject jsonObject = jsonArray.optJSONObject(i);
			if(jsonObject != null)
			{
				String type = jsonObject.optString("type");
				if(QuestionType.BIG_QUESTION_TYPE.equalsIgnoreCase(type))
				{
					JSONArray questionIdArray = jsonObject.optJSONArray("exercise");
					if(questionIdArray != null)
					{
						JSONObject idJsonObject = questionIdArray.optJSONObject(0);
						if(idJsonObject != null)
						{
							String questionId = idJsonObject.optString("qid");
							String title = jsonObject.optString("name");
							if(!TextUtils.isEmpty(questionId) && !TextUtils.isEmpty(title))
							{
								titleHashMap.put(questionId, title);
							}
						}
					}
				}
			}
		}
		return titleHashMap;
	}
	
	private Item makeQuestionItem(JSONObject questionJsonObject, HashMap<String, String> titleHashMap)
	{
		String id = questionJsonObject.optString("id");
		if(TextUtils.isEmpty(id))
		{
			id = questionJsonObject.optString("qid");
			if(TextUtils.isEmpty(id))
			{
				return null;
			}
		}
		String questionType = questionJsonObject.optString("type");
		String  content = questionJsonObject.optString("content");
		JSONArray accessoryJsonArray = questionJsonObject.optJSONArray("accessory");
		JSONArray correctAnswerJsonArray = questionJsonObject.optJSONArray("correctAnswer");
		String solution = questionJsonObject.optString("solution");
		JSONArray solutionAccessoryJsonArray = questionJsonObject.optJSONArray("solutionAccessory");
		String role = questionJsonObject.optString("role");
		String answer = questionJsonObject.optString("answer");
		String step = questionJsonObject.optString("step");
		String summary = questionJsonObject.optString("summary");
		JSONArray keypointJsonArray = questionJsonObject.optJSONArray("keypoint");
		Item item = new Item(id, questionType, removeMediaTag(content), solution);
		if(QuestionType.BIG_QUESTION_TYPE.equalsIgnoreCase(questionType) || QuestionType.CLOZE_TYPE.equalsIgnoreCase(questionType)
				|| QuestionType.SELECT_SENTENCE_TYPE.equalsIgnoreCase(questionType) || QuestionType.READING_TYPE.equalsIgnoreCase(questionType))
		{
			if(titleHashMap != null)
			{
				String title = titleHashMap.get(id);
				if(title != null)
				{
					item.setTitle(title);
				}
			}
		}
		if("1".equalsIgnoreCase(role))
		{
				//小题id
				JSONArray relation = questionJsonObject.optJSONArray("relation");
				if(relation != null)
				{
					for (int i = 0; i < relation.length(); i++)
					{
						item.addChildrenQuestionId(relation.optString(i));
					}
				}
		}
		if("2".equalsIgnoreCase(role))
		{
			item.setIsChildren(true);

		}
		if(accessoryJsonArray != null)
		{
			for (int j = 0; j < accessoryJsonArray.length(); j++)
			{
				JSONArray optionJsonArray = accessoryJsonArray.optJSONObject(j).optJSONArray("options");
				String type = accessoryJsonArray.optJSONObject(j).optString("type");
				Item.Option option = item.new Option();
				if(optionJsonArray != null && type != null)
				{
					option.setType(type);
					for (int k = 0; k < optionJsonArray.length(); k++)
					{
						option.addOption(optionJsonArray.optString(k));
					}
				}
				item.addOptions(option);
			}
		}
		if(correctAnswerJsonArray != null)
		{
			for (int j = 0; j < correctAnswerJsonArray.length(); j++)
			{
				item.addCorrectAnswer(correctAnswerJsonArray.optString(j));
			}
		}
		if(solutionAccessoryJsonArray != null)
		{
			for (int j = 0; j < solutionAccessoryJsonArray.length(); j++)
			{
				item.addSolutionAccessory(solutionAccessoryJsonArray.optString(j));
			}
		}
		if(!TextUtils.isEmpty(answer))
		{
			item.setAnswer(answer);
		}
		if(!TextUtils.isEmpty(step))
		{
			item.setStep(step);
		}
		if(!TextUtils.isEmpty(summary))
		{
			item.setSummary(summary);
		}
		if(keypointJsonArray != null)
		{
			int keyPointSize = keypointJsonArray.length();
			KeyPoint [] keyPoints = new KeyPoint[keyPointSize];
			for (int i = 0; i < keyPointSize; i++) 
			{
				JSONObject keypointObject = keypointJsonArray.optJSONObject(i);
				String keyPonitId = keypointObject.optString("id");
				String keyPonitName = keypointObject.optString("name");
				keyPoints[i] = item.new KeyPoint(keyPonitId, keyPonitName);
			}
			item.mKeyPoint = keyPoints;
		}
		return item;
	
	}
	
	/**
	 * set 大题中的小题
	 * @param questionJsonArray
	 * @param parentId 大题Id
	 */
	public void setChildrenQuestion(JSONArray jsonArray, String parentId)
	{
		Item item = getQuestion(parentId);
		if(item != null)
		{
			if(QuestionType.BIG_QUESTION_TYPE.equals(item.mType))
			{
				int length = jsonArray.length();
				for (int i = 0; i < length; i++)
				{
					JSONObject questionJsonObject = jsonArray.optJSONObject(i);
					addChildrenQuestionItem(makeQuestionItem(questionJsonObject, null));
				}
			}
		}
	}
	
	/**
	 * 获取未作答的题目ID
	 * @return
	 */
	public ArrayList<String> getUnanswerQuestionId()
	{
		ArrayList<String> unanswerQuestionId =  new ArrayList<String>();
		Iterator<Entry<String, Item>> iter = mQuestionItemMap.entrySet().iterator();
		while (iter.hasNext())
		{
			Entry<String, Item> entry = iter.next();
			if(entry.getValue().isAnswered())
			{
				unanswerQuestionId.add(entry.getValue().getId());
			}
			
		}
		return unanswerQuestionId;
	}
	
	/**
	 * 获取答题信息
	 * @return
	 */
	public ArrayList<AnswerInfo> getAnswerInfo()
	{
		if(mAnswerInfos == null)
		{
			mAnswerInfos = new ArrayList<AnswerInfo>();
			for (String questionId : mQuestionIds)
			{
				Item item = getQuestion(questionId);
				if(item != null)
				{
					mAnswerInfos.add(item.getAnswerInfo());
//					mAnswerInfos.add(new AnswerInfo(item.isAnswered(), item.isEnableCheckAnswer(), item.checkAnswer()
//							, questionId, item.getAnswerTime(), item.mUserAnswers));
				}
			}
		}
		return mAnswerInfos;
	}
	
	/**
	 * 根据题目Id获取答题信息
	 * @param questionId
	 * @return AnswerInfo/null
	 */
	public AnswerInfo getAnswerInfo(String questionId)
	{
		Item item = getQuestion(questionId);
		AnswerInfo answerInfo = null;
		if(item != null)
		{
			answerInfo = item.getAnswerInfo();
//			answerInfo = new AnswerInfo(item.isAnswered(), item.isEnableCheckAnswer()
//					, item.checkAnswer(), questionId, item.getAnswerTime(), item.mUserAnswers);
		}		
		return answerInfo;
	}
	
	/**
	 * 根据题目索引获取答题信息
	 * @param questionIndex
	 * @return AnswerInfo/null
	 */
	public AnswerInfo getAnswerInfo(int questionIndex)
	{
		Item item = getQuestion(questionIndex);
		AnswerInfo answerInfo = null;
		String questionId = getQuestionId(questionIndex);
		if(item != null && questionId != null)
		{
			answerInfo = item.getAnswerInfo();
//			answerInfo = new AnswerInfo(item.isAnswered(), item.isEnableCheckAnswer()
//					, item.checkAnswer(), questionId, item.getAnswerTime(), item.mUserAnswers);
		}		
		return answerInfo;
	}

	/**
	 * 一道题目的实体
	 * @author dgy
	 * @文件名 Question.java
	 * @version 1.0 2015-11-6
	 */
	public class Item
	{
		/**题目ID*/
		private String mId;
		/**题目类型 大题：0 英语完形填空：104 英语选择句子：105 英语阅读理解：106 单选题：101 多选题：102 判断题：103
			可作答可评分填空： 201 可作答不可评分填空： 202 不可作答不可评分填空：203*/
		private String mType;
		/**题目标题*/
		private String mTitle;
		/**题干*/
		private String mContent;
		/**选项/材料*/
		private ArrayList<Option> mAccessory = new ArrayList<Option>();
		/**正确答案**/
		private ArrayList<String> mCorrectAnswer = new ArrayList<String>();
		/**答案(不可判断的答案)*/
		private String mAnswer = "";
		/**解答**/
		private String mSolution;
		/**解析**/
		private ArrayList<String> mSolutionAccessory = new ArrayList<String>();
		/**解题步骤**/
		private String mStep;
		/***总结***/
		private String mSummary;
		/**用户答案**/
//		private String mUserAnswer = "";
		private String[] mUserAnswers = new String[0];
		/**答案个数**/
		private int mAnswercount = 1;
		/**作答时长**/
		private int mAnswerTime = 0;
		/**用户自评是否正确**/
		private boolean mSelfRating = false;		
		/***是否是大题中的小题*/
		private boolean mIsChildren = false; 
		/**大题中的小题id*/
		private ArrayList<String> mChildrenQuestionId = new ArrayList<String>(); 
		
		private AnswerInfo mAnswerInfo;
		
		private KeyPoint[] mKeyPoint;

		public Item(String id, String type, String content, String solution)
		{
			mId = id;
			mType = type;
			mContent = PrimaryQuestionUtils.makeMathFormulaFlag(content);
			mSolution = solution;
			mAnswerInfo = new AnswerInfo();
		}

		/**
		 * get 题目Id
		 * @return
		 */
		public String getId()
		{
			return mId;
		}

		public void setId(String id)
		{
			mId = id;
		}

		/**
		 * get 题目类型
		 * @return
		 */
		public String getType()
		{
			return mType;
		}

		public void setType(String type)
		{
			mType = type;
		}

		/**
		 * get 题干
		 * @return
		 */
		public String getContent()
		{
			
			return mIsChildren? mContent : mTitle == null? mContent : mTitle+mContent;
		}

		public void setContent(String content)
		{
			mContent = PrimaryQuestionUtils.makeMathFormulaFlag(content);
		}

		/**
		 * 题目选项，材料
		 * @return
		 */
		public ArrayList<Option> getOptions()
		{
			return mAccessory;
		}

		public void addOptions(Option option)
		{
			mAccessory.add(option);
		}

		public void setOptions(ArrayList<Option> options)
		{
			mAccessory = options;
		}

		/**
		 * get 正确答案
		 * @return
		 */
		public ArrayList<String> getCorrectAnswer()
		{
			return mCorrectAnswer;
		}
		
		/**
		 * get 正确答案
		 * @return
		 */
		public String getCorrectAnswerString()
		{
			StringBuilder answerBuilder = new StringBuilder();
			if(mCorrectAnswer.size() > 0)
			{
				for (String answer : mCorrectAnswer)
				{
					answerBuilder.append(answer);
					answerBuilder.append(MyHtml.PLACEHOLDER_CN);
				}
			}
			return PrimaryQuestionUtils.makeMathFormulaFlag(answerBuilder.toString());
		}

		public void addCorrectAnswer(String correctAnswer)
		{
			mCorrectAnswer.add(correctAnswer);
		}

		public void setCorrectAnswer(ArrayList<String> correctAnswer)
		{
			mCorrectAnswer = correctAnswer;
		}

		public String getAnswer()
		{
			if(TextUtils.isEmpty(mAnswer))
			{
				return getCorrectAnswerString();
			}
			return PrimaryQuestionUtils.makeMathFormulaFlag(mAnswer);
		}

		public void setAnswer(String answer)
		{
			mAnswer = answer;
		}

		/**
		 * get 解答
		 * @return
		 */
		public String getSolution()
		{
			return PrimaryQuestionUtils.makeMathFormulaFlag(mSolution);
		}

		public void setSolution(String solution)
		{
			mSolution = solution;
		}
		
		/**
		 * get 大题下面所有小题的解答
		 * @return
		 */
		public String getBigQuestionSolution()
		{
			StringBuilder solutionBuilder = new StringBuilder();
			for (String questionId : mChildrenQuestionId)
			{
				Item item = getQuestion(questionId);
				if(item != null)
				{
					solutionBuilder.append(item.getSolution());
				}
			}
			return solutionBuilder.toString();
		}
		
		public String getBigQuestionAnswer()
		{
			StringBuilder answerBuilder = new StringBuilder();
			for (String questionId : mChildrenQuestionId)
			{
				Item item = getQuestion(questionId);
				if(item != null)
				{
					answerBuilder.append(item.getAnswer());
				}
			}
			return answerBuilder.toString();
		}

		/**
		 * get 解析
		 * @return
		 */
		public ArrayList<String> getSolutionAccessory()
		{
			return mSolutionAccessory;
		}
		
		/**
		 * get 解析
		 * @return
		 */
		public String getSolutionAccessoryString()
		{
			StringBuilder solutionBuilder = new StringBuilder();
			for (String solutionAccessory : mSolutionAccessory)
			{
				solutionBuilder.append(solutionAccessory);
			}
			return PrimaryQuestionUtils.makeMathFormulaFlag(solutionBuilder.toString());
		}

		public void addSolutionAccessory(String solutionAccessory)
		{
			mSolutionAccessory.add(solutionAccessory);
		}

		public void setSolutionAccessory(ArrayList<String> solutionAccessory)
		{
			mSolutionAccessory = solutionAccessory;
		}

		public String getStep()
		{
			return TextUtils.isEmpty(mStep)? "" :PrimaryQuestionUtils.makeMathFormulaFlag(mStep);
		}

		public void setStep(String step)
		{
			mStep = step;
		}

		public String getSummary()
		{
			return mSummary;
		}

		public void setSummary(String summary)
		{
			mSummary = summary;
		}

		/*
		 * get 用户答案
		 */
		public String getUserAnswerString()
		{
			StringBuilder answerBuilder = new StringBuilder();
			if(mUserAnswers.length > 0)
			{
				if(mAnswercount == 1)
				{
					return mUserAnswers[0];
				}
				for (String answer: mUserAnswers)
				{
					answerBuilder.append(answer);
					answerBuilder.append(";");
				}
			}
			return answerBuilder.toString();
		}
		
		public String[] getUserAnswer()
		{
			return mUserAnswers;
		}

		public void setUserAnswer(String userAnswer)
		{
			if(mUserAnswers.length == 0)
			{
				mUserAnswers = new String[mAnswercount];
			}
			mUserAnswers[0] = userAnswer;
			mAnswerInfo.setIsAnswered(isAnswered());
		}
		
		public void addUserAnswer(String userAnswer, int index)
		{
			if(mUserAnswers.length == 0)
			{
				mUserAnswers = new String[mAnswercount];
			}
			if(index < mAnswercount)
			{
				mUserAnswers[index] = userAnswer;
			}
			mAnswerInfo.setIsAnswered(isAnswered());
		}

		/**
		 * get 答案个数
		 * @return
		 */
		public int getAnswercount()
		{
			return mAnswercount;
		}

		public void setAnswercount(int answercount)
		{
			mAnswercount = answercount;
		}
		
		public boolean isSelfRating()
		{
			return mSelfRating;
		}

		public void setSelfRating(boolean selfRating)
		{
//			if(mAnswerInfos != null)
//			{
//				for (AnswerInfo answerInfo : mAnswerInfos)
//				{
//					if(answerInfo.mQuestionId == mId)
//					{
//						answerInfo.setIsAnswerRight(selfRating);
//						break;
//					}
//				}
//			}
			mSelfRating = selfRating;
			setIsSelfRatinged(true);
		}

		public void addAnswerTime(long time)
		{
			if(time > 1000)
			{
				mAnswerTime += time;
			}
		}
		
		/**
		 * get 作答时间(单位s)
		 * @return
		 */
		public int getAnswerTime()
		{
			/**
			 * 大题作答时间等于各小题时间相加
			 */
			if(mChildrenQuestionId.size() > 0) 
			{
				int time = 0;
				for (String questionId : mChildrenQuestionId)
				{
					Item item = getQuestion(questionId);
					if(item != null)
					{
						time += item.mAnswerTime;
					}
				}
				return time;
			}
			return (mAnswerTime+200)/1000;
		}
		
		public boolean isChildren()
		{
			return mIsChildren;
		}

		public void setIsChildren(boolean isChildren)
		{
			mIsChildren = isChildren;
		}

		public String getTitle()
		{
			return mTitle;
		}

		public void setTitle(String title)
		{
			mTitle = title;
		}

		public void addChildrenQuestionId(String questionId)
		{
			mChildrenQuestionId.add(questionId);
		}
		
		public ArrayList<String> getChildrenQuestionId()
		{
			return mChildrenQuestionId;
		}
		
		public AnswerInfo getAnswerInfo()
		{
			mAnswerInfo.setIsAnswered(isAnswered());
			return mAnswerInfo;
		}

		public void setAnswerInfo(AnswerInfo answerInfo)
		{
			mAnswerInfo = answerInfo;
		}
		
		public KeyPoint[] getKeyPoints()
		{
			return mKeyPoint;
		}

		/**
		 * get 题目索引号（从0开始）
		 * @return
		 */
		public int getQuestionIndex()
		{
			return mQuestionIds.indexOf(mId);
		}

		/**
		 * set 填空题答案
		 * @param answerIndex 填空
		 * @param userAnswer
		 */
		public void setBlankUserAnswer(int answerIndex, String userAnswer)
		{
			String answer = getUserAnswerString();
			int answerCount = getAnswercount();
			if (!TextUtils.isEmpty(answer))
			{
				String[] answerArr = answer.split(",");
				if (answerCount > answerIndex)
				{
					answerArr[answerIndex] = userAnswer;
					StringBuilder answerBuilder = new StringBuilder();
					for (int i = 0; i < answerCount; i++)
					{
						answerBuilder.append(answerArr[i]);
						answerBuilder.append(",");
					}
					setUserAnswer(answerBuilder.toString());
				}

			}
			else
			{
				StringBuilder answerBuilder = new StringBuilder();
				for (int i = 0; i < answerCount; i++)
				{
					if (i == answerIndex)
					{
						answerBuilder.append(userAnswer);
					}
					else
					{
						answerBuilder.append(", ");
					}
				}
				String answerStr = answerBuilder.toString();
				setUserAnswer(answerStr);
			}
		}
		
		/**
		 * 是否已作答
		 * @return
		 */
		public boolean isAnswered()
		{
			if(mUserAnswers.length > 0)
			{
				for (String answer : mUserAnswers)
				{
					if("".equalsIgnoreCase(answer.replaceAll("\\s*", "").trim()))
					{
						return false;
					}
				}
				return true;
			}
			else if(mChildrenQuestionId.size() > 0) //大题
			{
				for (String questionId : mChildrenQuestionId)
				{
					Item item = getQuestion(questionId);
					if(item != null)
					{
						if(item.mUserAnswers.length > 0)
						{
							for (String answer : item.mUserAnswers)
							{
								if("".equalsIgnoreCase(answer.replaceAll("\\s*", "").trim()))
								{
									return false;
								}
							}
						}
						else
						{
							return false;
						}
					}
				}
				return true;
			}
			return false;
		}
		
		/**
		 * 判断答案
		 * @return true-答案正确/false-答案错误或者题目不可判断答案
		 */
		public boolean checkAnswer()
		{
			if(isEnableCheckAnswer())
			{
				if(mUserAnswers.length > 0)
				{
					for (String answer : mUserAnswers)
					{
						if(!TextUtils.isEmpty(answer.trim()))
						{
							if(mCorrectAnswer.indexOf(answer) == -1)
							{
								return false;
							}
						}
						else
						{
							return false;
						}
					}
					return true;
				}
			}
			return mSelfRating;
			
		}
		
		/**
		 * 是否可以判断答案（是否可评分）
		 * @return
		 */
		public boolean isEnableCheckAnswer()
		{
			if("101".equalsIgnoreCase(mType) || "102".equalsIgnoreCase(mType))
			{
				return true;
			}
			return false;
		}
		
		public void setIsSelfRatinged(boolean isSelfRating)
		{
			mAnswerInfo.setIsSelfRating(isSelfRating);
		}

		/**
		 * 题目选项
		 * @author dgy
		 * @文件名 Question.java
		 * @version 1.0 2015-11-10
		 */
		public class Option
		{
			private String mType;
			private ArrayList<String> mOptions;

			public Option()
			{
				mOptions = new ArrayList<String>();
			}

			public String getType()
			{
				return mType;
			}

			public void setType(String type)
			{
				mType = type;
			}

			public ArrayList<String> getOptions()
			{
				return mOptions;
			}

			public void addOption(String option)
			{
				mOptions.add(option);
			}

			public void setOptions(ArrayList<String> options)
			{
				mOptions = options;
			}

			@Override
			public String toString()
			{
				StringBuilder contentBuilder = new StringBuilder();
				for (String option : mOptions)
				{
					contentBuilder.append(option);
				}
				return contentBuilder.toString();
			}

		}

		@Override
		public String toString()
		{
			return mContent;
		}
		

		/**
		 * 答题信息 （是否作答、是否回答正确、是否可以判断答案（可评分））
		 * @author dgy
		 * @文件名 Question.java
		 * @version 1.0 2015-11-10
		 */
		public class AnswerInfo
		{
			/**是否已作答*/
			private boolean mIsAnswered = false;
			/**是否可以判断答案*/
			private boolean mIsEnableCheckAnswer;
			/**是否已自评**/
			private boolean mIsSelfRating = false;
//			/**答案是否正确*/
//			private boolean mIsAnswerRight;
//			/**作答时间(单位s)*/
//			private int mAnswerTime;
//			/**题目ID*/
//			private String mQuestionId;
//			/**用户答案*/
//			private String[] mUserAnswers;
			
			public AnswerInfo()
			{
				mIsEnableCheckAnswer = isEnableCheckAnswer();
			}
			
			public AnswerInfo(boolean isAnswered, boolean isEnableCheckAnswer, boolean isAnswerRight
					, String questionId, int answerTime, String[] userAnswer)
			{
				setIsAnswered(isAnswered);
				setIsEnableCheckAnswer(isEnableCheckAnswer);
//				setIsAnswerRight(isAnswerRight);
//				this.mQuestionId = questionId;
//				this.mAnswerTime = answerTime;
//				this.mUserAnswers = userAnswer;
			}

			/**
			 * 是否已作答
			 * @return
			 */
			public boolean isAnswered()
			{
				return this.mIsAnswered;
			}

			/**
			 * set 是否已作答
			 * @param isAnswered
			 */
			public void setIsAnswered(boolean isAnswered)
			{
				this.mIsAnswered = isAnswered;
			}

			/**是否可以判断答案
			 *  @return
			 */
			public boolean isEnableCheckAnswer()
			{
				return this.mIsEnableCheckAnswer;
			}

			/**
			 * set 是否可以判断答案
			 * @param isEnableCheckAnswer
			 */
			public void setIsEnableCheckAnswer(boolean isEnableCheckAnswer)
			{
				mIsEnableCheckAnswer = isEnableCheckAnswer;
			}

			/**
			 * 答案是否正确
			 * @return
			 */
			public boolean isAnswerRight()
			{
				return checkAnswer();
			}

			/**
			 * set 答案是否正确
			 * @param isAnswerRight
			 */
			public void setIsAnswerRight(boolean isAnswerRight)
			{
//				this.mIsAnswerRight = isAnswerRight;
			}
			
			public String getQuestionId()
			{
				return mId;
			}

			/**
			 * get 作答时间(单位s)
			*/
			public int getAnswerTime()
			{
				return mAnswerTime;
			}

			public void setAnswerTime(int answerTime)
			{
				mAnswerTime = answerTime;
			}
			
			/**
			 * 是否已经自评
			 * @return
			 */
			public boolean isSelfRating()
			{
				return mIsSelfRating;
			}

			public void setIsSelfRating(boolean isSelfRating)
			{
				mIsSelfRating = isSelfRating;
			}

			/**
			 * get 用户答案
			 * @return
			 */
			public String[] getUserAnswers()
			{
				return mUserAnswers;
			}

			public void setUserAnswers(String[] userAnswers)
			{
				mUserAnswers = userAnswers;
			}
		}
		
		public class KeyPoint
		{
			public String mKeyPointId;
			public String mKeyPonintName;
			public KeyPoint (String id, String name)
			{
				mKeyPointId = id;
				mKeyPonintName = name;
			}
		}
	}
	

	@Override
	public String toString()
	{
		Iterator<Entry<String, Item>> iter = mQuestionItemMap.entrySet().iterator();
		StringBuilder contentBuilder = new StringBuilder();
		while (iter.hasNext())
		{
			Entry<String, Item> entry = iter.next();
			contentBuilder.append(entry.getValue().toString());
		}
		return contentBuilder.toString();
	}
}
