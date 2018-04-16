package com.readboy.textbook.util;

import android.text.TextUtils;

import com.readboy.textbook.model.MyHtml;
import com.readboy.textbook.model.Question;
import com.readboy.textbook.model.Question.Item;
import com.readboy.textbook.model.Question.Item.Option;

import java.util.ArrayList;
import java.util.regex.Matcher;

/**
 * 构建Html文本，webView加载
 */
public class QuestionUtils
{
	
	/***显示答案与解析**/
	static boolean mIsShowExplain = false;
	/***自评**/
	static boolean mIsSelfRating = false;
	
	/***历史记录**/
	static boolean mIsHistory = false;
	
	static int mSubject = 0;
	
	public static void setSubject(int subject)
	{
		mSubject = subject;
	}
	
	public static void setIsHistory(boolean flag)
	{
		mIsHistory = flag;
	}
	
	public static void showExplain(boolean flag)
	{
		mIsShowExplain = flag;
	}
	
	public static String makeOneQuestion(final Question.Item item, StringBuilder questionHtmlBuilder)
	{
		if(item == null)
		{
			DebugLogger.getLogger().e("题目出错！！！！");
			return "";
		}
		String type = item.getType();
		if(!item.isChildren())
		{
			if(QuestionType.ONE_CHOICE_TYPE.equalsIgnoreCase(type)) //单项选择题
			{
				questionHtmlBuilder.append(makeChoiceQuestion(item));
			}
			else if(QuestionType.MORE_CHOICE_TYPE.equalsIgnoreCase(type)) //不定项选择题
			{
				questionHtmlBuilder.append(makeRechoiceQuestion(item));
			}
			else if(QuestionType.JUDGE_TYPE.equalsIgnoreCase(type)) //判断题
			{
				questionHtmlBuilder.append(makeJudgeQuestion(item));
			}
			else if(QuestionType.FILL_BLANK_TYPE.equalsIgnoreCase(type)) //填空题
			{
				questionHtmlBuilder.append(makeFillBlankQuestion(item));
			}
			else if(QuestionType.LONG_ANSWER_TYPE.equalsIgnoreCase(type)) 
			{
				questionHtmlBuilder.append(makeLongAnswerQuestion(item));
			}
			else if (QuestionType.BIG_QUESTION_TYPE.equalsIgnoreCase(type)) //大题
			{
				String title = item.getTitle();
				if(title != null)
				{
					questionHtmlBuilder.append(title);
				}
				questionHtmlBuilder.append("<div id='"+item.getId()+"'  qustion_type='"+type+"'>");
				questionHtmlBuilder.append(item.getContent());
				questionHtmlBuilder.append(makeChilrenQuestion(item));
				questionHtmlBuilder.append("</div>");
				questionHtmlBuilder.append("<div class='popup_div' id='popup_div'>");
				questionHtmlBuilder.append("</div>");
				questionHtmlBuilder.append(MyHtml.POPUP_DIV_CSS);
			}
			else if (QuestionType.CLOZE_TYPE.equalsIgnoreCase(type)) //英语完型填空
			{
				questionHtmlBuilder.append("<div id='"+item.getId()+"'  qustion_type='"+type+"'>");
				questionHtmlBuilder.append(item.getContent().replaceAll("#", ""));
				questionHtmlBuilder.append(makeChilrenQuestion(item));
				questionHtmlBuilder.append("</div>");
//				questionHtmlBuilder.append("<div class='popup_div' id='popup_div'>");
//				questionHtmlBuilder.append("</div>");
//				questionHtmlBuilder.append(MyHtml.POPUP_DIV_CSS);
			}
			else if(QuestionType.SELECT_SENTENCE_TYPE.equalsIgnoreCase(type)) //选择句子
			{
				questionHtmlBuilder.append("<div id='"+item.getId()+"'  qustion_type='"+type+"'>");
				questionHtmlBuilder.append(item.getContent().replaceAll("#", ""));
				questionHtmlBuilder.append(makeChilrenQuestion(item));
				questionHtmlBuilder.append("</div>");
			}
			else if(QuestionType.READING_TYPE.equalsIgnoreCase(type)) //阅读理解
			{
				questionHtmlBuilder.append("<div id='"+item.getId()+"'  qustion_type='"+type+"'>");
				questionHtmlBuilder.append(item.getContent().replaceAll("#", ""));
				questionHtmlBuilder.append(makeChilrenQuestion(item));
				questionHtmlBuilder.append("</div>");
			}
			else
			{
				questionHtmlBuilder.append(item.getContent());
			}
		}
		return questionHtmlBuilder.toString();
	}
	
	public static String makeOneQuestionHtml(final Question.Item item, boolean isShowExplain, boolean isSelfRating)
	{
		mIsShowExplain = isShowExplain;
		mIsSelfRating = isSelfRating;
		StringBuilder questionHtmlBuilder = new StringBuilder();
		makeHtmlHead(questionHtmlBuilder, MyHtml.ShowType.QUESTION);
		makeOneQuestion(item, questionHtmlBuilder);
		makeHtmlEnd(questionHtmlBuilder);
//		Log.e("", "1questionHtmlBuilder.toString() = "+questionHtmlBuilder.toString());
		return questionHtmlBuilder.toString();
	}
	
	public static String makeQuestionHtml(final Question question, boolean isShowExplain, boolean isSelfRating)
	{
		mIsShowExplain = isShowExplain;
		mIsSelfRating = isSelfRating;
		StringBuilder questionHtmlBuilder = new StringBuilder();
		makeHtmlHead(questionHtmlBuilder, MyHtml.ShowType.QUESTION);
		ArrayList<String> questionIds = question.getQuestionIds();
		int questionSize = questionIds.size();
		for (int i = 0; i < questionSize; i++)
		{
			makeOneQuestion(question.getQuestion(questionIds.get(i)), questionHtmlBuilder);
		}
		makeHtmlEnd(questionHtmlBuilder);
//		Log.e("", "2questionHtmlBuilder.toString() = "+questionHtmlBuilder.toString());
		return questionHtmlBuilder.toString();
	}
	
	public static void makeHtmlHead(StringBuilder questionHtmlBuilder, MyHtml.ShowType showType)
	{
		questionHtmlBuilder.append(MyHtml.HTML_HEAD);
		switch (showType)
		{
			case QUESTION:
				questionHtmlBuilder.append(MyHtml.makeQuestionCss());
				break;
				
			case EXAMPAPER:
				questionHtmlBuilder.append(MyHtml.makeQuestionCss());
				break;
	
			default:
				questionHtmlBuilder.append(MyHtml.makeExplainCss());
				break;
		}
		questionHtmlBuilder.append(MyHtml.JQUERY);
		if(mIsShowExplain || mIsSelfRating)
		{
			questionHtmlBuilder.append(MyHtml.SHOW_EXPLAIN_JS);
		}
		else
		{
			questionHtmlBuilder.append(MyHtml.QUESTION_JS);
		}
		questionHtmlBuilder.append(MyHtml.MATHJAX_JS);
		questionHtmlBuilder.append(MyHtml.HTML_HEAD_END);
	}
	
	public static void makeHtmlEnd(StringBuilder questionHtmlBuilder)
	{
		questionHtmlBuilder.append(MyHtml.HTML_END);
	}
	
	/**
	 * 构造一道单项选择题
	 * @param item
	 * @return html文本
	 */
	public static String makeChoiceQuestion(Question.Item item)
	{
		StringBuilder questionHtmlBuilder = new StringBuilder();
		questionHtmlBuilder.append("<div  class='ti' id='div-"+item.getId()+"'>");
		if(mIsShowExplain || mIsSelfRating)
		{
			questionHtmlBuilder.append("<div style='position: relative;'>");
		}
		String content = item.getContent();
		String userAnswer = item.getUserAnswerString();
		content = content.replaceFirst(QuestionType.BRACKET_REGEX, "<span id='"+item.getId()+"'>("+userAnswer+")</span>");
		questionHtmlBuilder.append(content);
		questionHtmlBuilder.append("<br />");//add by whb 2017-10-14
		char A = 'A';
		boolean hasUserAnswer = false;
		boolean isAnswerWrong = false;
		if(!TextUtils.isEmpty(userAnswer))
		{
			hasUserAnswer = true;
		}
		for (Option option : item.getOptions())
		{
			for (String optStr : option.getOptions())
			{
//				Log.e("", "1 optStr = "+optStr);
				optStr = removeOptionPartFlag(optStr);

//				Log.e("", "2 optStr = "+optStr);

				optStr = makeMathFormulaFlag(optStr);
//				Log.e("", "3 1 optStr = "+optStr);

				//add by whb 2017-10-17  截取答案选项中的选项内容
				optStr = makeMathFrmula(optStr);

//				Log.e("", "3 2 optStr = "+optStr);
				optStr = makeUnderPoint(optStr);

//				Log.e("", "4 optStr = "+optStr);
				if(hasUserAnswer && A == userAnswer.charAt(0))
				{
					if(mIsShowExplain)
					{
						if(item.checkAnswer())
						{
//							questionHtmlBuilder.append("<button class='answer-right'><label>");
                            questionHtmlBuilder.append("<dl><dt class='select' type='radio' id='");
							makeOneCheckedRadio(item, questionHtmlBuilder, A);
						}
						else
						{
							isAnswerWrong = true;
//							questionHtmlBuilder.append("<button class='answer-wrong'><label>");
                            questionHtmlBuilder.append("<dl><dt class='wrong' type='radio' id='");
							makeOneCheckedRadio(item, questionHtmlBuilder, A);
						}
					}
					else
					{
//						questionHtmlBuilder.append("<button class='button-option'><label>");
                        questionHtmlBuilder.append("<dl><dt class='select' type='radio' id='");
						makeOneCheckedRadio(item, questionHtmlBuilder, A);
					}
				}
				else
				{
//					questionHtmlBuilder.append("<button class='button-option'><label><input type='radio' id='");
					questionHtmlBuilder.append("<dl><dt class='normal' type='radio' id='");
					questionHtmlBuilder.append(item.getId());
					questionHtmlBuilder.append("-");
					questionHtmlBuilder.append(A);
					questionHtmlBuilder.append("' value='");
					questionHtmlBuilder.append(A);
					questionHtmlBuilder.append("' name='");
					questionHtmlBuilder.append(item.getId());
					questionHtmlBuilder.append("'>");
				}

//				Log.e("", "optStr = "+optStr);
//				questionHtmlBuilder.append(A+":");
				questionHtmlBuilder.append(A);
				questionHtmlBuilder.append("</dt><dd type='opt' id='");
                questionHtmlBuilder.append(item.getId());
                questionHtmlBuilder.append("-");
                questionHtmlBuilder.append(A);
                questionHtmlBuilder.append("-opt'>");
				questionHtmlBuilder.append(optStr);
				questionHtmlBuilder.append("</dd></dl>");
//				questionHtmlBuilder.append("</input></label></button><br />");
				A++;
			}
			
		}
		if(mIsShowExplain || mIsSelfRating)
		{
			if (!isAnswerWrong && hasUserAnswer)
			{
//				questionHtmlBuilder.append("<div class='result-icon'><img src='file:///android_asset/icon/right_nor.png' /></div>");	//add by whb 2017-10-14
			}
			else
			{
//				questionHtmlBuilder.append("<div class='result-icon'><img src='file:///android_asset/icon/wrong_nor.png' /></div>");	//add by whb 2017-10-14
			}
			questionHtmlBuilder.append("</div>");
			makeExplanPlan(questionHtmlBuilder, item);
			questionHtmlBuilder.append(makeExplain(item));
			
		}
		questionHtmlBuilder.append("</div>");
		return questionHtmlBuilder.toString();
	}

	private static void makeOneCheckedRadio(Question.Item item, StringBuilder questionHtmlBuilder, char A) 
	{
//		questionHtmlBuilder.append("<input type='radio' id='");
		questionHtmlBuilder.append(item.getId());
		questionHtmlBuilder.append("-");
		questionHtmlBuilder.append(A);
		questionHtmlBuilder.append("' value='");
		questionHtmlBuilder.append(A);
		questionHtmlBuilder.append("' name='");
		questionHtmlBuilder.append(item.getId());
		questionHtmlBuilder.append("' checked='checked'>");
	}
	
	/**
	 * 构造一道不定项选择题
	 * @param item
	 * @return
	 */
	public static String makeRechoiceQuestion(Question.Item item)
	{
		StringBuilder questionHtmlBuilder = new StringBuilder();
		questionHtmlBuilder.append("<div  class='ti' id='div-");
		questionHtmlBuilder.append(item.getId()+"'>");
		if(mIsShowExplain || mIsSelfRating)
		{
			questionHtmlBuilder.append("<div style='position: relative;'>");
		}
		String userAnswer = item.getUserAnswerString();
		String content = item.getContent();
		content = content.replaceFirst(QuestionType.BRACKET_REGEX, "<span id='"+item.getId()+"'>("+userAnswer+")</span>");
		questionHtmlBuilder.append(content);
		char A = 'A';
		boolean hasUserAnswer = false;
		char[] answerChar = {};
		if(!TextUtils.isEmpty(userAnswer))
		{
			hasUserAnswer = true;
			answerChar = userAnswer.toCharArray();
		}
		for (Option option : item.getOptions())
		{
			for (String optStr : option.getOptions())
			{
				if(hasUserAnswer)
				{
					boolean isSetAnswer = false;
					for (int i = 0; i < answerChar.length; i++)
					{
						if(answerChar[i] == A)
						{
//							questionHtmlBuilder.append("<label><input type='checkbox' id='");
                            questionHtmlBuilder.append("<dl><dt class='select' type='checkbox' id='");
							questionHtmlBuilder.append(item.getId());
							questionHtmlBuilder.append("-");
							questionHtmlBuilder.append(A);
							questionHtmlBuilder.append("' name='");
							questionHtmlBuilder.append(item.getId());
							questionHtmlBuilder.append("'checked='checked'>");
							isSetAnswer = true;
							break;
						}
					}
					if(!isSetAnswer)
					{
//						questionHtmlBuilder.append("<label><input type='checkbox' id='");
                        questionHtmlBuilder.append("<dl><dt class='normal' type='checkbox' id='");
						makeOneCheckbox(item, questionHtmlBuilder, A);
					}
				}
				else
				{
//					questionHtmlBuilder.append("<label><input type='checkbox' id='");
                    questionHtmlBuilder.append("<dl><dt class='normal' type='checkbox' id='");
					makeOneCheckbox(item, questionHtmlBuilder, A);
				}					
				questionHtmlBuilder.append(A);
                questionHtmlBuilder.append("</dt><dd type='mult-opt' id='");
                questionHtmlBuilder.append(item.getId());
                questionHtmlBuilder.append("-");
                questionHtmlBuilder.append(A);
                questionHtmlBuilder.append("-opt'>");
				removeOptionPartFlag(optStr);
				optStr = makeMathFormulaFlag(optStr);
				questionHtmlBuilder.append(optStr);
                questionHtmlBuilder.append("</dd></dl><br />");
//				questionHtmlBuilder.append("</input></label><br />");
				A++;
			}			
		}
		if(mIsShowExplain || mIsSelfRating)
		{
			if (item.checkAnswer())
			{
				questionHtmlBuilder.append("<div class='result-icon'><img src='file:///android_asset/icon/right_icon.png' /></div>");				
			}
			else
			{
				questionHtmlBuilder.append("<div class='result-icon'><img src='file:///android_asset/icon/wrong_icon.png' /></div>");
			}
			questionHtmlBuilder.append("</div>");
			makeExplanPlan(questionHtmlBuilder, item);
			questionHtmlBuilder.append(makeExplain(item));
		}
		questionHtmlBuilder.append("</div>");		
		return questionHtmlBuilder.toString();
	}

	private static void makeOneCheckbox(Question.Item item, StringBuilder questionHtmlBuilder, char A) {
		questionHtmlBuilder.append(item.getId());
		questionHtmlBuilder.append("-");
		questionHtmlBuilder.append(A);
		questionHtmlBuilder.append("' name='");
		questionHtmlBuilder.append(item.getId());
		questionHtmlBuilder.append("'>");
	}
	
	/**
	 * 构造一道判断题
	 * @param item
	 * @return
	 */
	public static String makeJudgeQuestion(Question.Item item)
	{
		StringBuilder questionHtmlBuilder = new StringBuilder();		
		questionHtmlBuilder.append("<div id='div-"+item.getId()+"'>");
		String userAnswer = item.getUserAnswerString();
		String content = item.getContent();
		if(userAnswer.equals("right"))
		{
			userAnswer = "&radic;";
		}
		else if (userAnswer.equals("wrong")) 
		{
			userAnswer = "&Chi;";
		}
		content = content.replaceFirst(QuestionType.BRACKET_REGEX, "<span id='"+item.getId()+"'>( "+userAnswer+" )</span>");
		questionHtmlBuilder.append(content);
		
		if(item.isAnswered())
		{
			if("right".equalsIgnoreCase(userAnswer))
			{
				questionHtmlBuilder.append("<input autofocus='autofocus' class='judge right' input_type='judge' type='button' id='"+item.getId()+"-right"+"' name='"+item.getId()+"'><br />");
				questionHtmlBuilder.append("<input class='judge wrong' input_type='judge' type='button' id='"+item.getId()+"-wrong"+"' name='"+item.getId()+"'><br />");
			}
			else
			{
				questionHtmlBuilder.append("<input class='judge right' input_type='judge' type='button' id='"+item.getId()+"-right"+"' name='"+item.getId()+"'><br />");
				questionHtmlBuilder.append("<input autofocus='autofocus' class='judge wrong' input_type='judge' type='button' id='"+item.getId()+"-wrong"+"' name='"+item.getId()+"'><br />");
			}
		}
		else
		{
			questionHtmlBuilder.append("<input class='judge right' input_type='judge' type='button' id='"+item.getId()+"-right"+"' name='"+item.getId()+"'><br />");
			questionHtmlBuilder.append("<input class='judge wrong' input_type='judge' type='button' id='"+item.getId()+"-wrong"+"' name='"+item.getId()+"'><br />");
		}
		if(mIsShowExplain || mIsSelfRating)
		{
			makeExplanPlan(questionHtmlBuilder, item);
			questionHtmlBuilder.append("<div id='");
			questionHtmlBuilder.append(item.getId());
			questionHtmlBuilder.append("-explain-div'>");
			questionHtmlBuilder.append("<p>");
			questionHtmlBuilder.append("正确答案：");
			questionHtmlBuilder.append(item.getAnswer());
			questionHtmlBuilder.append("</p>");
			questionHtmlBuilder.append(item.getSolution());
			questionHtmlBuilder.append("</div>");
		}
		questionHtmlBuilder.append("</div>");
		return questionHtmlBuilder.toString();
	}
	
	/**
	 * 构造一道填空题
	 * @param item
	 * @return
	 */
	public static String makeFillBlankQuestion(final Question.Item item)
	{
		StringBuilder questionHtmlBuilder = new StringBuilder();
		questionHtmlBuilder.append("<div id='div-");
		questionHtmlBuilder.append(item.getId());
		questionHtmlBuilder.append("'>");
		String content = item.getContent();
		content = content.replaceAll("<p>", "<div>");
		content = content.replaceAll("</p>", "</div>");
		content = RegexStringUtils.replaceAll(content, "<blk.*?></blk>", new AbstractReplaceCallBack()
		{

			String[] userAnswer = item.getUserAnswer();				

			@Override
			public String doReplace(String text, int index, Matcher matcher)
			{
				String value = "";
				if(userAnswer.length == 0)
				{
					item.setAnswercount(index+1);
					if(index == 0)
					{
						//4.4的有bug  <div contenteditable=true....>前面有一个空格则显示异常,没空格或者多个空格都是可以的
						return "  <div contenteditable=true class='div_input' id='"+item.getId()+"-"+index+"' >"+value + QuestionType.PLACEHOLDER_CN + "</div>";
					}
				}
				else
				{
					if(userAnswer.length > index)
					{
						String answer = userAnswer[index];
						if(!TextUtils.isEmpty(answer))
						{
							value = answer;
						}
					}
				}
				//4.4的有bug  <div contenteditable=true....>前面有一个空格则显示异常,没空格或者多个空格都是可以的
				return "  <div contenteditable=true class='div_input' id='"+item.getId()+"-"+index+"' >" + value + QuestionType.PLACEHOLDER_CN + "</div>";
//				return "<input contenteditable=true id=\""+item.getId()+"-"+index+"\" value=\""+value
//						+"\" input_type=\"blank\" maxlength=\"$1\" ></input>";
			}
			
		});
//		content = content.replaceFirst("<u>\\s*</u>", "<input autofocus=\"autofocus\" contenteditable=true id=\""+item.getId()+"\"></input>");
		questionHtmlBuilder.append(makeUnderPoint(content));
		questionHtmlBuilder.append("</div>");
		if(mIsShowExplain || mIsSelfRating)
		{
			makeExplanPlan(questionHtmlBuilder, item);
			questionHtmlBuilder.append(makeExplain(item));
		}
		return questionHtmlBuilder.toString();
	}
	
	
	public static String makeLongAnswerQuestion(final Question.Item item)
	{
		StringBuilder questionHtmlBuilder = new StringBuilder();
		questionHtmlBuilder.append("<div id='div-"+item.getId()+"'>");
		String content = item.getContent();
		content = content.replaceAll("<p>", "<div>");
		content = content.replaceAll("</p>", "</div>");
		content = RegexStringUtils.replaceAll(content, "<blk.*?</blk>", new AbstractReplaceCallBack()
		{

			String[] userAnswer = item.getUserAnswer();				

			@Override
			public String doReplace(String text, int index, Matcher matcher)
			{				
				String value = "";
				if(userAnswer.length == 0)
				{
					item.setAnswercount(index+1);
					//4.4的有bug  <div contenteditable=true....>前面有一个空格则显示异常,没空格或者多个空格都是可以的
					return "  <div contenteditable=true class='div_input' id='"+item.getId()+"-"+index+"' >"+value + QuestionType.PLACEHOLDER_CN + "</div>";
				}
				else
				{
					if(userAnswer.length > index)
					{
						String answer = userAnswer[index];
						if(!TextUtils.isEmpty(answer))
						{
							value = answer;
						}
					}
				}
				//4.4的有bug  <div contenteditable=true....>前面有一个空格则显示异常,没空格或者多个空格都是可以的
				return "  <div contenteditable=true class='div_input' id='"+item.getId()+"-"+index+"' >" + value + QuestionType.PLACEHOLDER_CN + "</div>";
			}
			
		});
//		questionHtmlBuilder.append(content);
		questionHtmlBuilder.append(makeUnderPoint(content));
		questionHtmlBuilder.append("</div>");
		if(mIsShowExplain || mIsSelfRating)
		{
			makeExplanPlan(questionHtmlBuilder, item);
			questionHtmlBuilder.append(makeExplain(item));
		}
		return questionHtmlBuilder.toString();
	}
	
	/**
	 * 为题干加题号
	 * @param content
	 * @param no
	 * @return
	 */
	public String addNoForQuestionContent(String content, int no)
	{
		return content.replaceFirst("<p>", "<p>"+(no+1)+"、");
	}
	
	/**
	 * 构造正解的解答思路，方法，解析
	 * @param item
	 * @return
	 */
	public static String makeExplain(Question.Item item)
	{
		StringBuilder questionHtmlBuilder = new StringBuilder();
		questionHtmlBuilder.append("<div class='explain_div_content' id='");
		questionHtmlBuilder.append(item.getId());
		questionHtmlBuilder.append("-explain-div'>");
		questionHtmlBuilder.append("<p class='correct_answer_title'>");
		questionHtmlBuilder.append("<img src='file:///android_asset/icon/ic_correct_answer.png' />&nbsp;&nbsp;&nbsp;&nbsp;正确答案：");
		questionHtmlBuilder.append(item.getCorrectAnswerString());
		questionHtmlBuilder.append("</p>");
		questionHtmlBuilder.append("<p class='explain_title'><img src='file:///android_asset/icon/ic_solution.png' />&nbsp;&nbsp;&nbsp;&nbsp;解析：</p>");//add by whb 2017-10-14
		if(mIsSelfRating)
		{
			questionHtmlBuilder.append("<div style='margin:0 auto;width:300px;height:200px;'>");
			questionHtmlBuilder.append("<button class='button orange margin_right_big' id='"+item.getId()+"-self-right' button_type='self-rating'>自评正确</button>");
			questionHtmlBuilder.append("<button class='button orange' id='"+item.getId()+"-self-wrong' button_type='self-rating'>自评错误</button>");
			questionHtmlBuilder.append("</div>");
		}
		else
		{
			questionHtmlBuilder.append("<div class='solution'>");
			questionHtmlBuilder.append(item.getSolution());
			questionHtmlBuilder.append("</div>");
		}
		questionHtmlBuilder.append("</div>");
		return questionHtmlBuilder.toString();
	}
	
	public static String makeChilrenQuestion(Item parentItem)
	{
		ArrayList<String> childrenQuestionId = parentItem.getChildrenQuestionId();
		StringBuilder questionHtmlBuilder = new StringBuilder();
		int count = childrenQuestionId.size();
		for (int i=0;  i<count; i++)
		{
			Item childrenItem = Question.getInstance().getQuestion(childrenQuestionId.get(i));
			if(childrenItem != null)
			{
				if(i != 0)
				{
					questionHtmlBuilder.append("<div class='select-line'></div>");
				}
				if(QuestionType.ONE_CHOICE_TYPE.equalsIgnoreCase(childrenItem.getType())) //单项选择题
				{
					questionHtmlBuilder.append(makeChoiceQuestion(childrenItem));
				}
				else if(QuestionType.MORE_CHOICE_TYPE.equalsIgnoreCase(childrenItem.getType())) //不定项选择题
				{
					questionHtmlBuilder.append(makeRechoiceQuestion(childrenItem));
				}
				else if(QuestionType.JUDGE_TYPE.equalsIgnoreCase(childrenItem.getType())) //判断题
				{
					questionHtmlBuilder.append(makeJudgeQuestion(childrenItem));
				}
				else if(QuestionType.FILL_BLANK_TYPE.equalsIgnoreCase(childrenItem.getType())) //填空题
				{
					questionHtmlBuilder.append(makeFillBlankQuestion(childrenItem));
				}
				else if(QuestionType.LONG_ANSWER_TYPE.equalsIgnoreCase(childrenItem.getType())) 
				{
					questionHtmlBuilder.append(makeLongAnswerQuestion(childrenItem));
				}
				else if (QuestionType.UNABLE_ANSWER_TYPE.equalsIgnoreCase(childrenItem.getType())) 
				{
					questionHtmlBuilder.append("<div id='div-"+childrenItem.getId()+"'>");
					questionHtmlBuilder.append(childrenItem.getContent());
					questionHtmlBuilder.append("</div>");
				}
				else if (QuestionType.CLOZE_TYPE.equalsIgnoreCase(childrenItem.getType())) //英语完型填空
				{
					ArrayList<Item.Option> options = childrenItem.getOptions();
					for (Option option : options)
					{
						if(QuestionType.ONE_CHOICE_TYPE.equalsIgnoreCase(option.getType())) //单项选择题
						{
							questionHtmlBuilder.append(makeChoiceQuestion(childrenItem));
						}
						else if(QuestionType.MORE_CHOICE_TYPE.equalsIgnoreCase(option.getType())) //不定项选择题
						{
							questionHtmlBuilder.append(makeRechoiceQuestion(childrenItem));
						}
						else if(QuestionType.JUDGE_TYPE.equalsIgnoreCase(option.getType())) //判断题
						{
							questionHtmlBuilder.append(makeJudgeQuestion(childrenItem));
						}
						else if(QuestionType.FILL_BLANK_TYPE.equalsIgnoreCase(option.getType())) //填空题
						{
							questionHtmlBuilder.append(makeFillBlankQuestion(childrenItem));
						}
						else if(QuestionType.LONG_ANSWER_TYPE.equalsIgnoreCase(option.getType())) 
						{
							questionHtmlBuilder.append(makeLongAnswerQuestion(childrenItem));
						}
					}
				}
				else 
				{
					questionHtmlBuilder.append("<div id='div-"+childrenItem.getId()+"'>");
					questionHtmlBuilder.append(childrenItem.getContent());
					questionHtmlBuilder.append("</div>");
				}
			}
		}
		return questionHtmlBuilder.toString();
	}
	
	public static String makeMathFormulaFlag(String source)
	{
		source = source.replaceAll(MyHtml.MATH_START_REPLACE_REGEX, MyHtml.MATH_SEPARATOR);
		return source.replaceAll(MyHtml.MATH_END_REPLACE_REGEX, MyHtml.MATH_SEPARATOR);
	}

	//add by whb 2017-10-17
	public static String makeMathFrmula(String source) {
		if (source.indexOf("$") >= 0
				&& (source.indexOf("$") == source.lastIndexOf("$"))) {
			String retString = "";
			int firstIndex = source.indexOf("\">");
			if (firstIndex > 0) {
				retString = source.substring(0, firstIndex);
				retString = retString + "$";

				int lastIndex = source.lastIndexOf(">");
				if (lastIndex > 0) {
					String lastString = source.substring(lastIndex + 1);
					retString = retString+lastString;
				}

				return retString;
			}
		}
		return source;
	}
	
	public static String removeOptionPartFlag(String source)
	{
		source = source.replaceFirst("<p>", "");
		return source.replaceFirst("<p>", "");
	}
	
	/**
	 * 加点字
	 * @param string
	 * @return
	 */
	public static String makeUnderPoint(String string)
	{
		return RegexStringUtils.replaceAll(string, "<e>.*?</e>", new AbstractReplaceCallBack()
		{
			
			@Override
			public String doReplace(String text, int index, Matcher matcher)
			{
				return "<span style="+MyHtml.UNDER_POINT_STYLE+">"+text+"</span>";
			}
		});
	}
	
	public static void makeExplanPlan(StringBuilder questionHtmlBuilder, Item item)
	{		
		String questionId = item.getId();
//		questionHtmlBuilder.append("<div class='select-line'></div>");
		if(item.getKeyPoints() != null)
		{
			questionHtmlBuilder.append("<div><button class='button-text button1' id='");
			questionHtmlBuilder.append(questionId);
			questionHtmlBuilder.append("-video'>知识点微视频学习</button>");
			if(mSubject == 2)
			{
				questionHtmlBuilder.append("<button class='button-text button2' id='");
				questionHtmlBuilder.append(questionId);
				questionHtmlBuilder.append("-same'>相似题强化训练</button>");
			}
		}
		else
		{
			questionHtmlBuilder.append("<div class='u-clearfix'>");
		}
		if(!item.isEnableCheckAnswer())
		{
			if(mIsHistory)
			{
				questionHtmlBuilder.append("<span style='float:right; margin-left:1.5em''>自评: ");
				questionHtmlBuilder.append(item.getSelfRatingScore()/10);
				questionHtmlBuilder.append("分</span>");
			}
			else if(!mIsHistory)
			{
				/*
				* 	<div id="dropdown"><p myflag="true" id="001-p-id">自我评分</p>
						<ul id="001-ul-id"><li><a rel="1" id="001-a-id-1">答对了</a></li>
							<li><a rel="0" id="001-a-id-0">答错了</a></li></ul></div>*/
				questionHtmlBuilder.append("<div id='dropdown'><p myflag='true' id='");
				questionHtmlBuilder.append(questionId);
				questionHtmlBuilder.append("-self-rating'>自我评分</p><ul id='");
				questionHtmlBuilder.append(questionId);
				questionHtmlBuilder.append("-ul-id'><li><a rel='1' id='");
				questionHtmlBuilder.append(questionId);
				questionHtmlBuilder.append("-a-id-1'>答对了</a></li><li><a rel='0' id='");
				questionHtmlBuilder.append(questionId);
				questionHtmlBuilder.append("-a-id-0'>答错了</a></li></ul></div>");
			}
		}
		//add by whb 2017-09-05
		/*questionHtmlBuilder.append("<button class='button-text button5' id='");
		questionHtmlBuilder.append(questionId);
		questionHtmlBuilder.append("-answer' button_type='show_and_hide'>收起答案</button>")*/;
		questionHtmlBuilder.append("</div><div class='select-line'></div>");
	}

	public static String addHeaderText(String header, String html) {
		if (TextUtils.isEmpty(header)) {
			return html;
		}
		String newHtml;
		if (html.startsWith("<p>")) {
			newHtml = html.replaceFirst("<p>", "<p>" + header);
		} else if (html.startsWith("<p ")) {
			int index = html.indexOf(">");
			newHtml = html.substring(0, index + 1)
					+ header
					+ html.substring(index + 1, html.length());
		} else {
			newHtml = header + html;
		}
		return newHtml;
	}
}


