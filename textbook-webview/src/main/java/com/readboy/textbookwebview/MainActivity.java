package com.readboy.textbookwebview;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.readboy.textbook.http.MyHttpConnection;
import com.readboy.textbook.model.MyHtml;
import com.readboy.textbook.model.Question;
import com.readboy.textbook.model.Question.Item.Option;
import com.readboy.textbook.util.DebugLogger;
import com.readboy.textbook.util.XmlParser;
import com.readboy.textbook.view.MyBaseQuestionWebView;
import com.readboy.textbook.view.MyQuestionWebView;
import com.readboy.textbook.view.MyWebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class MainActivity extends Activity {

    private MyWebView mWebView;
    private ListView mListView;
    private JSONArray mJsonArray;
    private UrlCache mUrlCache;
    private Question mQuestion;
    private JavaScriptObject mJavaScriptObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWebView();
        initListView();
        getBookContent();
        getBookQuestion();

        MyQuestionWebView webView = new MyQuestionWebView(this);

    }

    @Override
    public File getCacheDir() {
        File extStorageAppCachePath = getAppExternalStorageDataCache();
        if (extStorageAppCachePath != null) {
            return extStorageAppCachePath;
        } else {
            return super.getCacheDir();
        }
    }

    public File getAppExternalStorageDataCache() {
        File extStorageAppCachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File externalStorageDir = Environment.getExternalStorageDirectory();
            File extStorageAppBasePath = null;
            if (externalStorageDir != null) {
                // {SD_PATH}/Android/data/
                extStorageAppBasePath = new File(externalStorageDir.getAbsolutePath() + File.separator + "Android"
                        + File.separator + "data" + File.separator + getPackageName());
            }

            if (extStorageAppBasePath != null) {
                extStorageAppCachePath = new File(extStorageAppBasePath.getAbsolutePath() + File.separator
                        + "webViewCache");
                boolean isCachePathAvailable = true;
                if (!extStorageAppCachePath.exists()) {
                    isCachePathAvailable = extStorageAppCachePath.mkdirs();
                    if (!isCachePathAvailable) {
                        extStorageAppCachePath = null;
                    }
                }
            }
        }
        return extStorageAppCachePath;
    }

    private void initListView() {
        mListView = (ListView) findViewById(R.id.listView1);
        String[] item = {"中心意旨", "结构思路", "表达技巧", "重点难点", "易错易混全解", "古今对译"};
        mListView.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, item));
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String htmlString;
                try {
                    htmlString = MyHtml.tohtml(new XmlParser().xmlPullParseSection(mJsonArray.getJSONObject(position).getString("content")));
//					mWebView.loadDataWithBaseURL("http://192.168.20.235", htmlString,"text/html; charset=UTF-8", "UTF-8", "");
                    JSONArray commentJsonArray = mJsonArray.getJSONObject(position).optJSONArray("comment");
                    mWebView.setComment(commentJsonArray);
                    mWebView.loadData(htmlString);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

    private void initWebView() {
        mWebView = (MyWebView) findViewById(R.id.book_content_webView);
        WebSettings ws = mWebView.getSettings();
        // String appCacheDir = getApplicationContext().getDir("cache",
        // Context.MODE_PRIVATE).getPath();
        String appCacheDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Textbook/webViewCache";
        File file = new File(appCacheDir);
        if (!file.exists()) {
            boolean success = file.mkdirs();
            if (!success) {
                appCacheDir = getCacheDir().getAbsolutePath();
            }
        }
        ws.setAppCachePath(getCacheDir().getAbsolutePath());

		/*
		 * if (Constant.ADDRESS_SEARCH.compareTo(Constant.PUBLIC_ADDRESS_SEARCH)
		 * == 0)
		 * mWebView.loadUrl("http://115.182.0.156/static/bookcapture/exercise.html"
		 * ); else
		 * mWebView.loadUrl("http://192.168.20.190/bookcapture/exercise.html");
		 */
//		mWebView.loadUrl("http://192.168.20.171/webview/xmlTohtml/index.html");

    }

    public void getBookContent() {
        MyHttpConnection httpConnection = MyHttpConnection.getInstance(getApplicationContext());
//		httpConnection.httpStringRequest(MyHttpConnection.GET_BOOK_CONTENT_URL, null, new MyHttpConnection.UrlListener()
//				{
//					@Override
//					public void onResult(String result)
//					{
//						DebugLogger.getLogger().d(result);
//						try
//						{
//							JSONObject jsonObject = new JSONObject(result);
//							JSONArray jsonArray = jsonObject.getJSONArray("children");
//							JSONObject jsonObject2 = jsonArray.getJSONObject(0);
//							mJsonArray = jsonObject2.getJSONArray("children");
//						}
//						catch (JSONException e)
//						{
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//						new RegexTest().test(result);
//						String htmlString;
//						try
//						{
//							htmlString = MyHtml.tohtml(new XmlParser().xmlPullParseSection(mJsonArray.getJSONObject(0).getString("content")));
//							mWebView.loadData(htmlString,"text/html; charset=UTF-8", "UTF-8");
//						}
//						catch (JSONException e)
//						{
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//						
//
//					}
//		
//					@Override
//					public void onError(String msg)
//					{
//						
//					}
//				});
    }

    public void getBookQuestion() {
        MyHttpConnection httpConnection = MyHttpConnection.getInstance(getApplicationContext());
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("ids", "1534,2534,7534,44534,3534,4534,5534,6534,8534,9534,10534,11534,12534,13534,14534,15534");
        String ids = "1534,2534,7534,44534,3534,4534,5534,6534,8534,9534,10534,11534,12534,13534,14534,15534";
//		httpConnection.httpStringRequest(MyHttpConnection.GET_BOOK_QUESTION_URL + "ids="+ids, params, new MyHttpConnection.UrlListener()
//		{
//
//			@Override
//			public void onResult(String result)
//			{
//				DebugLogger.getLogger().d(result);
//				try
//				{
//					JSONObject srcJsonObject = new JSONObject(result);
//					JSONArray qstJsonArray;
//					int ret = srcJsonObject.getInt("ok");
//					if (ret == 1) 
//					{
//						qstJsonArray = srcJsonObject.getJSONArray("data");
//						makeQuestion(qstJsonArray);
//					}
//				}
//				catch (JSONException e)
//				{
//					e.printStackTrace();
//				}
//				
//			}
//
//			@Override
//			public void onError(String msg)
//			{
//				
//			}
//			
//		});
    }

    private void makeQuestion(JSONArray jsonArray) {
        mQuestion = Question.getInstance();
        int length = jsonArray.length();
        for (int i = 0; i < length; i++) {
            JSONObject questionJsonObject = jsonArray.optJSONObject(i);
            String id = questionJsonObject.optString("id");
            String questionType = questionJsonObject.optString("type");
            String content = questionJsonObject.optString("content");
            JSONArray accessoryJsonArray = questionJsonObject.optJSONArray("accessory");
            JSONArray correctAnswerJsonArray = questionJsonObject.optJSONArray("correctAnswer");
            String solution = questionJsonObject.optString("solution");
            JSONArray solutionAccessoryJsonArray = questionJsonObject.optJSONArray("solutionAccessory");

            Question.Item item = mQuestion.new Item(id, questionType, content, solution);
            if (accessoryJsonArray != null) {
                for (int j = 0; j < accessoryJsonArray.length(); j++) {
                    JSONArray optionJsonArray = accessoryJsonArray.optJSONObject(j).optJSONArray("options");
                    String type = accessoryJsonArray.optJSONObject(j).optString("type");
                    Option option = item.new Option();
                    if (optionJsonArray != null && type != null) {
                        option.setType(type);
                        for (int k = 0; k < optionJsonArray.length(); k++) {
                            option.addOption(optionJsonArray.optString(k));
                        }
                    }
                    item.addOptions(option);
                }
            }
            if (correctAnswerJsonArray != null) {
                for (int j = 0; j < correctAnswerJsonArray.length(); j++) {
                    item.addCorrectAnswer(correctAnswerJsonArray.optString(j));
                }
            }
            if (solutionAccessoryJsonArray != null) {
                for (int j = 0; j < solutionAccessoryJsonArray.length(); j++) {
                    item.addSolutionAccessory(solutionAccessoryJsonArray.optString(j));
                }
            }
            mQuestion.addQuestionItem(item);
        }
        makeHtmlQuestion();
    }

    public void makeHtmlQuestion() {
        Iterator<Entry<String, Question.Item>> iter = mQuestion.getQuestionItemMap().entrySet().iterator();
        StringBuilder questionHtmlBuilder = new StringBuilder();
        while (iter.hasNext()) {
            Entry<String, Question.Item> entry = iter.next();
            Question.Item item = entry.getValue();
            String type = item.getType();
            if ("101".equalsIgnoreCase(type)) {
                questionHtmlBuilder.append("<div>");
                String content = item.getContent();
                content = content.replaceFirst("\\(.*\\)|（.*）", "<span id=\"" + item.getId() + "\">( )</span>");
                questionHtmlBuilder.append(content);
                char A = 'A';
                for (Option option : item.getOptions()) {
                    for (String optStr : option.getOptions()) {
                        questionHtmlBuilder.append("<input type=\"radio\" id=\"" + item.getId() + "-" + A + "\" value=\"" + A + "\" name=\"" + item.getId() + "\">");
                        questionHtmlBuilder.append(A + ":");
                        questionHtmlBuilder.append(optStr);
                        questionHtmlBuilder.append("</input>");
                        A++;
                    }

                }
                questionHtmlBuilder.append("</div>");
            } else if ("102".equalsIgnoreCase(type)) {
                questionHtmlBuilder.append("<div id=\"" + item.getId() + "\">");
                questionHtmlBuilder.append(item.getContent());
                char A = 'A';
                for (Option option : item.getOptions()) {
                    for (String optStr : option.getOptions()) {
                        questionHtmlBuilder.append("<input type=\"checkbox\" id=\"" + item.getId() + "-" + A + "\" name=\"" + item.getId() + "\">");
                        questionHtmlBuilder.append(A);
                        questionHtmlBuilder.append(optStr);
                        questionHtmlBuilder.append("</input>");
                        A++;
                    }

                }
                questionHtmlBuilder.append("</div>");
            } else if ("103".equalsIgnoreCase(type)) {
                questionHtmlBuilder.append("<div id=\"" + item.getId() + "\">");
                questionHtmlBuilder.append(item.getContent());
                char A = 'A';
                for (Option option : item.getOptions()) {
                    for (String optStr : option.getOptions()) {
                        questionHtmlBuilder.append("<input type=\"radio\" id=\"" + item.getId() + "-" + A + "\" value=\"" + A + "\" name=\"" + item.getId() + "\">");
                        questionHtmlBuilder.append(A);
                        questionHtmlBuilder.append(optStr);
                        questionHtmlBuilder.append("</input>");
                        A++;
                    }

                }
                questionHtmlBuilder.append("</div>");
            } else {

            }
            item.getContent();

        }
        DebugLogger.getLogger().d(questionHtmlBuilder.toString());
    }


}
