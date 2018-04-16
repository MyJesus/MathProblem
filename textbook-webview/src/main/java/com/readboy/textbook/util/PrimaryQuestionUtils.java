package com.readboy.textbook.util;

import java.util.ArrayList;
import java.util.regex.Matcher;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.readboy.textbook.model.MyHtml;
import com.readboy.textbook.model.PrimaryQuestion;
import com.readboy.textbook.model.PrimaryQuestion.Item;
import com.readboy.textbook.model.PrimaryQuestion.Item.Option;

public class PrimaryQuestionUtils
{
	
	/***显示答案与解析**/
	static boolean mIsShowExplain = false;
	/***自评**/
	static boolean mIsSelfRating = false;
	
	static PrimaryQuestion mQuestion;
	
	public static String makeOneQuestionHtml(final PrimaryQuestion.Item item, boolean isShowExplain, boolean isSelfRating)
	{
		mIsShowExplain = isShowExplain;
		mIsSelfRating = isSelfRating;
		StringBuilder questionHtmlBuilder = new StringBuilder();
		makeHtmlHead(questionHtmlBuilder);
		questionHtmlBuilder.append(makeOneQuestion(item));
		makeHtmlEnd(questionHtmlBuilder);
		return questionHtmlBuilder.toString();
	}
	
	public static String makeQuestionDetailHtml(PrimaryQuestion primaryQuestion, final PrimaryQuestion.Item item)
	{
		mQuestion = primaryQuestion;
		mIsSelfRating = false;
		mIsShowExplain = false;
		StringBuilder questionHtmlBuilder = new StringBuilder();
		questionHtmlBuilder.append(MyHtml.HTML_HEAD);
		questionHtmlBuilder.append(MyHtml.JQUERY);		
		questionHtmlBuilder.append(MyHtml.makeQuestionDetailCss());
		questionHtmlBuilder.append(MyHtml.QUESTION_DETAIL_JS);
		questionHtmlBuilder.append(MyHtml.MATHJAX_JS);
		questionHtmlBuilder.append(MyHtml.HTML_HEAD_END);
		questionHtmlBuilder.append(makeOneQuestion(item));
		questionHtmlBuilder.append(makeAnswerAndSolution(item));
		makeHtmlEnd(questionHtmlBuilder);
		return questionHtmlBuilder.toString();
	}
	
	public static String makeOneQuestion(final PrimaryQuestion.Item item)
	{
		if(item == null)
		{
			DebugLogger.getLogger().e("题目出错！！！！");
			return "";
		}
		StringBuilder htmlBuilder = new StringBuilder();
		String type = item.getType();
		htmlBuilder.append("<div id='");
		htmlBuilder.append(item.getId());
		htmlBuilder.append("'question_type='show_answer' class='text'>");
		if(!item.isChildren())
		{
			if(QuestionType.ONE_CHOICE_TYPE.equalsIgnoreCase(type)) //单项选择题
			{
				htmlBuilder.append(makeChoiceQuestion(item));
				htmlBuilder.append("</div>");
			}
			else if(QuestionType.MORE_CHOICE_TYPE.equalsIgnoreCase(type)) //不定项选择题
			{
				htmlBuilder.append(makeRechoiceQuestion(item));
				htmlBuilder.append("</div>");
			}
			else if(QuestionType.JUDGE_TYPE.equalsIgnoreCase(type)) //判断题
			{
				htmlBuilder.append(makeJudgeQuestion(item));
				htmlBuilder.append("</div>");
			}
			else if(QuestionType.FILL_BLANK_TYPE.equalsIgnoreCase(type)) //填空题
			{
				htmlBuilder.append(makeFillBlankQuestion(item));
				htmlBuilder.append("</div>");
			}
			else if(QuestionType.LONG_ANSWER_TYPE.equalsIgnoreCase(type)) 
			{
				htmlBuilder.append(makeLongAnswerQuestion(item));
				htmlBuilder.append("</div>");
			}
			else if (QuestionType.BIG_QUESTION_TYPE.equalsIgnoreCase(type)) //大题
			{
				String title = item.getTitle();
				if(title != null)
				{
					htmlBuilder.append(title);
				}
				htmlBuilder.append(item.getContent());
				htmlBuilder.append(makeChilrenQuestion(item));
				htmlBuilder.append("</div>");
			}
			else if (QuestionType.CLOZE_TYPE.equalsIgnoreCase(type)) //英语完型填空
			{
				htmlBuilder.append(item.getContent());
				htmlBuilder.append(makeChilrenQuestion(item));
				htmlBuilder.append("</div>");
				htmlBuilder.append("<div class='popup_div' id='popup_div'>");
				htmlBuilder.append("</div>");
				htmlBuilder.append(MyHtml.POPUP_DIV_CSS);
			}
			else if (QuestionType.UNABLE_ANSWER_TYPE.equalsIgnoreCase(type)) //不可作答不可评分填空
			{
				htmlBuilder.append(makeUnableAnswerQuestion(item));
				htmlBuilder.append("</div>");
			}
			else if(QuestionType.SELECT_SENTENCE_TYPE.equalsIgnoreCase(type)) //选择句子
			{
				htmlBuilder.append(item.getContent());
				htmlBuilder.append(makeChilrenQuestion(item));
				htmlBuilder.append("</div>");
			}
			else if(QuestionType.READING_TYPE.equalsIgnoreCase(type)) //阅读理解
			{
				htmlBuilder.append(item.getContent());
				htmlBuilder.append(makeChilrenQuestion(item));
				htmlBuilder.append("</div>");
			}
			else
			{
				htmlBuilder.append(item.getContent());
				htmlBuilder.append("</div>");
			}
		}
		else
		{
			htmlBuilder.append(item.getContent());
			htmlBuilder.append("</div>");
		}
		return makeUnderPoint(htmlBuilder.toString());
	}
	
	@NonNull
	public static String makeQuestionHtml(final PrimaryQuestion primaryQuestion, boolean isShowExplain, boolean isSelfRating)
	{
		mIsShowExplain = isShowExplain;
		mIsSelfRating = isSelfRating;
		mQuestion = primaryQuestion;
		StringBuilder questionHtmlBuilder = new StringBuilder();
		makeHtmlHead(questionHtmlBuilder);
		ArrayList<String> questionIds = primaryQuestion.getQuestionIds();
		int questionSize = questionIds.size();
		boolean unableAnswer = (!mIsSelfRating && !mIsShowExplain);
		if(unableAnswer)
		{
			questionHtmlBuilder.append("<ul class='u-clearfix'>");
		}
		for (int i = 0; i < questionSize; i++)
		{
			if(unableAnswer)
			{
				makeQuestionBody(questionHtmlBuilder, i+1);
			}
			questionHtmlBuilder.append(makeOneQuestion(primaryQuestion.getQuestion(questionIds.get(i))));
			if(unableAnswer)
			{
				makeQuestionBodyEnd(questionHtmlBuilder);
			}
		}
		if(unableAnswer)
		{
			questionHtmlBuilder.append("</ul>");
		}
		makeHtmlEnd(questionHtmlBuilder);
		return questionHtmlBuilder.toString();
	}
	
	/**
	 * 教辅
	 * @param primaryQuestion
	 * @return
	 */
	public static String makeSubsidiaryBookHtml(final PrimaryQuestion primaryQuestion)
	{
		mQuestion = primaryQuestion;
		StringBuilder questionHtmlBuilder = new StringBuilder();
		questionHtmlBuilder.append(MyHtml.HTML_HEAD);
		questionHtmlBuilder.append(MyHtml.JQUERY);		
		questionHtmlBuilder.append(MyHtml.makeSubsidiaryBookCss());
		questionHtmlBuilder.append(MyHtml.SUBSIDIARY_BOOK_JS);
		questionHtmlBuilder.append(MyHtml.MATHJAX_JS);
		questionHtmlBuilder.append(MyHtml.HTML_HEAD_END);
		ArrayList<String> questionIds = primaryQuestion.getQuestionIds();
		int questionSize = questionIds.size();
		questionHtmlBuilder.append("<div class='wrapper'><div class='tip pl'>你要找的题目都在下面喔……</div><div class='pl'><table border='0'>");
		for (int i = 0; i < questionSize; i++)
		{
			questionHtmlBuilder.append("<tr><td class='icon-box'><div class='icon'>"+(i+1)+"</div></td><td width='100%'>");
			questionHtmlBuilder.append(makeOneQuestion(primaryQuestion.getQuestion(questionIds.get(i))));
			questionHtmlBuilder.append("</td><td class='img-box'><div class='fr hr-icon'><span class='indicate-icon'></span></div></td></tr>");
		}
		questionHtmlBuilder.append("</table></div></div>");
		makeHtmlEnd(questionHtmlBuilder);
		return questionHtmlBuilder.toString();
	}
	
	private static void makeHtmlHead(StringBuilder questionHtmlBuilder)
	{
		questionHtmlBuilder.append(MyHtml.HTML_HEAD);
		questionHtmlBuilder.append(MyHtml.JQUERY);		
		if(mIsShowExplain || mIsSelfRating)
		{
			questionHtmlBuilder.append(MyHtml.makeExplainCss());
			questionHtmlBuilder.append(MyHtml.EXPLAIN_JS);
		}
		else
		{
			questionHtmlBuilder.append(MyHtml.makeQuestionCss());
			questionHtmlBuilder.append(MyHtml.QUESTION_JS);
		}
		questionHtmlBuilder.append(MyHtml.MATHJAX_JS);
		questionHtmlBuilder.append(MyHtml.HTML_HEAD_END);
	}
	
	private static void makeHtmlEnd(StringBuilder questionHtmlBuilder)
	{
		questionHtmlBuilder.append(MyHtml.HTML_END);
	}
	
	/**
	 * 构造一道单项选择题
	 * @param item
	 * @return
	 */
	public static String makeChoiceQuestion(PrimaryQuestion.Item item)
	{
		StringBuilder questionHtmlBuilder = new StringBuilder();
		String content = item.getContent();
		String userAnswer = item.getUserAnswerString();		
		content = content.replaceFirst(QuestionType.BRACKET_REGEX, "<span id=\""+item.getId()+"\">("+userAnswer+")</span>");
		questionHtmlBuilder.append(content);
		char A = 'A';
		boolean hasUserAnswer = false;
		if(!TextUtils.isEmpty(userAnswer))
		{
			hasUserAnswer = true;
		}
		for (Option option : item.getOptions())
		{
			for (String optStr : option.getOptions())
			{
				if(hasUserAnswer && A == userAnswer.charAt(0))
				{
					if(mIsShowExplain)
					{
						if(item.checkAnswer())
						{
							questionHtmlBuilder.append("<button class='answer-right'><label><input type=\"radio\" id=\""+item.getId()+"-"+A+"\" value=\""+A+"\" name=\""+item.getId()+"\" checked=\"checked\">");
						}
						else
						{
							questionHtmlBuilder.append("<button class='answer-wrong'><label><input type=\"radio\" id=\""+item.getId()+"-"+A+"\" value=\""+A+"\" name=\""+item.getId()+"\" checked=\"checked\">");
						}
					}
					else
					{
						questionHtmlBuilder.append("<button><label><input type=\"radio\" id=\""+item.getId()+"-"+A+"\" value=\""+A+"\" name=\""+item.getId()+"\" checked=\"checked\">");
					}
				}
				else
				{
					questionHtmlBuilder.append("<label>");
				}
				questionHtmlBuilder.append(A+":");
				optStr = removeOptionPartFlag(optStr);
				optStr = makeMathFormulaFlag(optStr);
				questionHtmlBuilder.append(optStr);
				questionHtmlBuilder.append("</label><br />");
				A++;
			}
			
		}
		if(mIsShowExplain || mIsSelfRating)
		{
			questionHtmlBuilder.append(makeExplain(item));
		}
		return questionHtmlBuilder.toString();
	}
	
	/**
	 * 构造一道不定项选择题
	 * @param item
	 * @return
	 */
	public static String makeRechoiceQuestion(PrimaryQuestion.Item item)
	{
		StringBuilder questionHtmlBuilder = new StringBuilder();
		String userAnswer = item.getUserAnswerString();
		String content = item.getContent();
		content = content.replaceFirst(QuestionType.BRACKET_REGEX, "<span id=\""+item.getId()+"\">("+userAnswer+")</span>");
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
							questionHtmlBuilder.append("<label><input type=\"checkbox\" id=\""+item.getId()+"-"+A+"\" name=\""+item.getId()+"\"checked=\"checked\">");
							isSetAnswer = true;
							break;
						}
					}
					if(!isSetAnswer)
					{
						questionHtmlBuilder.append("<label><input type=\"checkbox\" id=\""+item.getId()+"-"+A+"\" name=\""+item.getId()+"\">");
					}
				}
				else
				{
					questionHtmlBuilder.append("<label>");
				}					
				questionHtmlBuilder.append(A);
				optStr = removeOptionPartFlag(optStr);
				optStr = makeMathFormulaFlag(optStr);
				questionHtmlBuilder.append(optStr);
				questionHtmlBuilder.append("</label><br />");
				A++;
			}			
		}
		if(mIsShowExplain || mIsSelfRating)
		{
			questionHtmlBuilder.append(makeExplain(item));
		}
		return questionHtmlBuilder.toString();
	}
	
	/**
	 * 构造一道判断题
	 * @param item
	 * @return
	 */
	public static String makeJudgeQuestion(PrimaryQuestion.Item item)
	{
		StringBuilder questionHtmlBuilder = new StringBuilder();		
		String userAnswer = item.getUserAnswerString();
		String content = item.getContent();
		content = content.replaceFirst(QuestionType.BRACKET_REGEX, "<span id='"+item.getId()+"'>( "+userAnswer+" )</span>");
		questionHtmlBuilder.append(content);
		
		if(item.isAnswered())
		{
			if("正确".equalsIgnoreCase(userAnswer))
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
			questionHtmlBuilder.append(makeExplain(item));
		}
		return questionHtmlBuilder.toString();
	}
	
	/**
	 * 构造一道填空题
	 * @param item
	 * @return
	 */
	public static String makeFillBlankQuestion(final PrimaryQuestion.Item item)
	{
		StringBuilder questionHtmlBuilder = new StringBuilder();
		String content = item.getContent();		
		content = RegexStringUtils.replaceAll(content, "<blk.*?</blk>", new AbstractReplaceCallBack()
		{		
			@Override
			public String doReplace(String text, int index, Matcher matcher)
			{
				return QuestionType.UNDERLINE_STRING;
			}
			
		});
//		content = content.replaceFirst("<u>\\s*</u>", "<input autofocus=\"autofocus\" contenteditable=true id=\""+item.getId()+"\"></input>");
		questionHtmlBuilder.append(content);
		if(mIsShowExplain || mIsSelfRating)
		{
			questionHtmlBuilder.append(makeExplain(item));
		}
		return questionHtmlBuilder.toString();
	}
	
	
	public static String makeLongAnswerQuestion(final PrimaryQuestion.Item item)
	{
		StringBuilder questionHtmlBuilder = new StringBuilder();
		String content = item.getContent();
		content = content.replaceAll("<p>", "<div>");
		content = content.replaceAll("</p>", "</div>");
		content = RegexStringUtils.replaceAll(content, "<blk.*?</blk>", new AbstractReplaceCallBack()
		{

//			String[] userAnswer = item.getUserAnswer();				

			@Override
			public String doReplace(String text, int index, Matcher matcher)
			{				
//				String value = "";
//				if(userAnswer.length == 0)
//				{
//					item.setAnswercount(index+1);
//					return "<div class='div_input' id='"+item.getId()+"-"+index+"' >  "+value + QuestionType.PLACEHOLDER_CN + "  </div>";
//				}
//				else
//				{
//					if(userAnswer.length > index)
//					{
//						String answer = userAnswer[index];
//						if(!TextUtils.isEmpty(answer))
//						{
//							value = answer;
//						}
//					}
//				}
//				return "<div class='div_input' id='"+item.getId()+"-"+index+"' >  " + value + QuestionType.PLACEHOLDER_CN + "  </div>";
				return QuestionType.UNDERLINE_STRING;
			}
			
		});
		questionHtmlBuilder.append(content);
		if(mIsShowExplain || mIsSelfRating)
		{
			questionHtmlBuilder.append(makeExplain(item));
		}
		return questionHtmlBuilder.toString();
	}
	
	public static String makeUnableAnswerQuestion(final PrimaryQuestion.Item item)
	{
		StringBuilder questionHtmlBuilder = new StringBuilder();
		String content = item.getContent();
		content = content.replaceAll("<p>", "<div>");
		content = content.replaceAll("</p>", "</div>");
		content = RegexStringUtils.replaceAll(content, "<blk.*?</blk>", new AbstractReplaceCallBack()
		{		

			@Override
			public String doReplace(String text, int index, Matcher matcher)
			{				
				return QuestionType.UNDERLINE_STRING;
			}
			
		});
		questionHtmlBuilder.append(content);
		if(mIsShowExplain || mIsSelfRating)
		{
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
	 * 构造正确的解答思路，方法，解析
	 * @param item
	 * @return
	 */
	public static String makeExplain(PrimaryQuestion.Item item)
	{
		StringBuilder questionHtmlBuilder = new StringBuilder();
		if(!TextUtils.isEmpty(item.getAnswer()))
		{
			questionHtmlBuilder.append("<p>");
			questionHtmlBuilder.append("正确答案：");
			questionHtmlBuilder.append(item.getAnswer());
			questionHtmlBuilder.append("</p>");
		}
		if(mIsSelfRating)
		{
			questionHtmlBuilder.append("<div style='margin:0 auto;width:300px;height:200px;'>");
			questionHtmlBuilder.append("<button class='button orange margin_right_big' id='");
			questionHtmlBuilder.append(item.getId());
			questionHtmlBuilder.append("-self-right' button_type=\"self-rating\">自评正确</button>");
			questionHtmlBuilder.append("<button class='button orange' id='");
			questionHtmlBuilder.append(item.getId());
			questionHtmlBuilder.append("-self-wrong' button_type='self-rating'>自评错误</button>");
			questionHtmlBuilder.append("</div>");
		}
		else
		{
			if(!item.checkAnswer())
			{
				questionHtmlBuilder.append(item.getSolution());
			}
			else
			{
				questionHtmlBuilder.append("<p>");
				questionHtmlBuilder.append("<button id='");
				questionHtmlBuilder.append(item.getId());
				questionHtmlBuilder.append("-explain' button_type='show_and_hide'>解析：</button>");
				questionHtmlBuilder.append("</p>");
				questionHtmlBuilder.append("<div id='");
				questionHtmlBuilder.append(item.getId());
				questionHtmlBuilder.append("-explain-div' style='display:none;'>");
				questionHtmlBuilder.append(item.getSolution());
				questionHtmlBuilder.append("</div>");
			}
		}
		return questionHtmlBuilder.toString();
	}
	
	public static String makeSolution(PrimaryQuestion.Item item) 
	{
		StringBuilder questionHtmlBuilder = new StringBuilder();
		questionHtmlBuilder.append("<p>");
		questionHtmlBuilder.append(MyHtml.SPAN_BLUE_START_TAG);
		questionHtmlBuilder.append("解析</span><br />");
		questionHtmlBuilder.append(item.getSolutionAccessoryString());
		questionHtmlBuilder.append("</p>");
		return questionHtmlBuilder.toString();
	}
	
	public static String makeAnswer(PrimaryQuestion.Item item) 
	{
		StringBuilder questionHtmlBuilder = new StringBuilder();
		questionHtmlBuilder.append("<p>");
		questionHtmlBuilder.append(MyHtml.SPAN_BLUE_START_TAG);
		questionHtmlBuilder.append("答案</span><br />");
		questionHtmlBuilder.append(item.getCorrectAnswerString());
		questionHtmlBuilder.append("</p>");
		return questionHtmlBuilder.toString();
	}
	
	public static String makeAnswerAndSolution(PrimaryQuestion.Item item)
	{
		StringBuilder questionHtmlBuilder = new StringBuilder();
		questionHtmlBuilder.append("<p>");
		questionHtmlBuilder.append(MyHtml.SPAN_BLUE_START_TAG);
		questionHtmlBuilder.append("<br />解析:</span><br /><br />");
		String answer;
		if(item.getChildrenQuestionId().size() > 0)
		{
			questionHtmlBuilder.append(item.getBigQuestionSolution());
			answer = item.getBigQuestionAnswer();
		}
		else
		{
			questionHtmlBuilder.append(item.getSolution());
			answer = item.getAnswer();
		}		
		questionHtmlBuilder.append("</p>");
		questionHtmlBuilder.append("<br /><button class='button-text answer_btn'>答&ensp;&ensp;&ensp;案</button></span><br/><br/>");
		questionHtmlBuilder.append("<div id='answer-div' style='display:none;'>");
        String step = item.getStep();
        if(step != "")
        {
            questionHtmlBuilder.append("解答过程<br />");
            questionHtmlBuilder.append(item.getStep());
            questionHtmlBuilder.append("<br />答案<br />");
        }
		questionHtmlBuilder.append(answer);
		questionHtmlBuilder.append("</div>");
		return questionHtmlBuilder.toString();
	}
		
	public static String makeChilrenQuestion(Item parentItem)
	{
		ArrayList<String> childrenQuestionId = parentItem.getChildrenQuestionId();
		StringBuilder questionHtmlBuilder = new StringBuilder();
		for (String questionId : childrenQuestionId)
		{
			Item childrenItem = null;
			if(mQuestion != null)
			{
				childrenItem = mQuestion.getQuestion(questionId);
			}
			
			if(childrenItem != null)
			{
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
				else if (QuestionType.CLOZE_TYPE.equalsIgnoreCase(childrenItem.getType())) //英语完型填空
				{
					String content = childrenItem.getContent();
					content = RegexStringUtils.replaceAll(content, "<blk.*?</blk>", new AbstractReplaceCallBack()
					{		

						@Override
						public String doReplace(String text, int index, Matcher matcher)
						{				
							return QuestionType.UNDERLINE_STRING;
						}
						
					});
					questionHtmlBuilder.append(content);
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
						else if (QuestionType.UNABLE_ANSWER_TYPE.equalsIgnoreCase(option.getType())) //不可作答不可评分填空
						{
							questionHtmlBuilder.append(makeUnableAnswerQuestion(childrenItem));
							questionHtmlBuilder.append("</div>");
						}
						else
						{
							questionHtmlBuilder.append(childrenItem.getContent());
							questionHtmlBuilder.append("</div>");
						}
					}
				}
			}
		}
		return questionHtmlBuilder.toString();
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
	
	
	/**
	 * 把公式标签转换成mathjax公式(性能有问题啊！！！，头痛)
	 * @param source
	 * @return
	 */
	public static String makeMathFormulaFlag(String source)
	{
        if(TextUtils.isEmpty(source))
        {
            return "";
        }
		source = source.replaceAll(MyHtml.MATH_START_REPLACE_REGEX, MyHtml.MATH_SEPARATOR);
		return source.replaceAll(MyHtml.MATH_END_REPLACE_REGEX, MyHtml.MATH_SEPARATOR);
	}
	
	/**
	 * 去除<p>标签,因为<p>里面不能嵌套<div>
	 * @param source
	 * @return
	 */
	public static String removeOptionPartFlag(String source)
	{
        if(TextUtils.isEmpty(source))
        {
            return "";
        }
		source = source.replaceFirst("<p>", "");
		return source.replaceFirst("<p>", "");
	}
	
	public static void makeQuestionBody(StringBuilder questionHtmlBuilder, int index)
	{
		questionHtmlBuilder.append("<li class='u-clearfix'><div class='nb'><div class='nb-circle'>"+index+"</div></div>");		
	}
	
	public static void makeQuestionBodyEnd(StringBuilder questionHtmlBuilder)
	{
		questionHtmlBuilder.append("</li>");		
	}
}