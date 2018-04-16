package com.example.errorqstupload.utils;

import com.example.errorqstupload.ErrorQuestionDB;
import com.example.errorqstupload.bean.QuestionInfo;
import com.example.errorqstupload.bean.TinyQuestionInfo;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

/**
 * Created by Sion's on 2017/12/26.
 */

public class TestData {
    public static void creaTestInfo(List<TinyQuestionInfo> eqList) {
        eqList.clear();
        try {
            JSONArray jsonArray = new JSONArray("[\n" +
                    "{\n" +
                    "\"origin\": \"（2017河北故城运河中学期中）\",\n" +
                    "\"status\": 0,\n" +
                    "\"updateTime\": 1512035417,\n" +
                    "\"version\": 589,\n" +
                    "\"qid\": 176006552,\n" +
                    "\"accessory\": [{\n" +
                    "\"type\": 101,\n" +
                    "\"options\": [\"in\",\n" +
                    "\"on\",\n" +
                    "\"with\",\n" +
                    "\"for\"]\n" +
                    "}],\n" +
                    "\"correctAnswer\": [\"D\"],\n" +
                    "\"id\": 176006552,\n" +
                    "\"no\": \"1、\",\n" +
                    "\"shortOrigin\": \"（2017河北故城运河中学期中）\",\n" +
                    "\"solution\": \"<p>句意：我们午饭通常吃米饭。此处“have...for+某餐”意为“某餐吃……”。</p>\",\n" +
                    "\"createTime\": 1512035417,\n" +
                    "\"content\": \"<p>We usually have rice <blk mlen=\\\"1\\\" mstyle=\\\"underline\\\"></blk> lunch.</p>\",\n" +
                    "\"score\": 10,\n" +
                    "\"role\": 0,\n" +
                    "\"answer\": \"<p>D</p>\",\n" +
                    "\"type\": 101\n" +
                    "},\n" +
                    "{\n" +
                    "\"origin\": \"（2017重庆江津实验中学期末）\",\n" +
                    "\"status\": 0,\n" +
                    "\"updateTime\": 1512035417,\n" +
                    "\"version\": 589,\n" +
                    "\"qid\": 176007552,\n" +
                    "\"accessory\": [{\n" +
                    "\"type\": 101,\n" +
                    "\"options\": [\"Yes, please\",\n" +
                    "\"Thank you\",\n" +
                    "\"Here you are\",\n" +
                    "\"You're welcome\"]\n" +
                    "}],\n" +
                    "\"correctAnswer\": [\"B\"],\n" +
                    "\"id\": 176007552,\n" +
                    "\"no\": \"2、\",\n" +
                    "\"shortOrigin\": \"（2017重庆江津实验中学期末）\",\n" +
                    "\"solution\": \"<p>Happy birthday to you! 是祝贺别人生日时说的祝福语，对方常用Thank you. 来回答。</p>\",\n" +
                    "\"createTime\": 1512035417,\n" +
                    "\"content\": \"<p>—Hi, Maria. Happy birthday to you!</p><p>—<blk mlen=\\\"1\\\" mstyle=\\\"underline\\\"></blk>.</p>\",\n" +
                    "\"score\": 10,\n" +
                    "\"role\": 0,\n" +
                    "\"answer\": \"<p>B</p>\",\n" +
                    "\"type\": 101\n" +
                    "},\n" +
                    "{\n" +
                    "\"origin\": \"（2016河北石家庄高邑月考）\",\n" +
                    "\"status\": 0,\n" +
                    "\"updateTime\": 1512035417,\n" +
                    "\"version\": 589,\n" +
                    "\"qid\": 176008552,\n" +
                    "\"accessory\": [{\n" +
                    "\"type\": 101,\n" +
                    "\"options\": [\"How color\",\n" +
                    "\"What color\",\n" +
                    "\"What's color\",\n" +
                    "\"What\"]\n" +
                    "}],\n" +
                    "\"correctAnswer\": [\"B\"],\n" +
                    "\"id\": 176008552,\n" +
                    "\"no\": \"3、\",\n" +
                    "\"shortOrigin\": \"（2016河北石家庄高邑月考）\",\n" +
                    "\"solution\": \"<p>根据答语中的black可知问句是问颜色的，故答案选B。</p>\",\n" +
                    "\"createTime\": 1512035417,\n" +
                    "\"content\": \"<p>—<blk mlen=\\\"1\\\" mstyle=\\\"underline\\\"></blk> is that quilt?</p><p>—It's black.</p>\",\n" +
                    "\"score\": 10,\n" +
                    "\"role\": 0,\n" +
                    "\"answer\": \"<p>B</p>\",\n" +
                    "\"type\": 101\n" +
                    "},\n" +
                    "{\n" +
                    "\"origin\": \"（2017辽宁新宾期中）\",\n" +
                    "\"status\": 0,\n" +
                    "\"updateTime\": 1512035417,\n" +
                    "\"version\": 589,\n" +
                    "\"qid\": 176009552,\n" +
                    "\"accessory\": [{\n" +
                    "\"type\": 101,\n" +
                    "\"options\": [\"fun\",\n" +
                    "\"relaxing\",\n" +
                    "\"boring\",\n" +
                    "\"interesting\"]\n" +
                    "}],\n" +
                    "\"correctAnswer\": [\"C\"],\n" +
                    "\"id\": 176009552,\n" +
                    "\"no\": \"4、\",\n" +
                    "\"shortOrigin\": \"（2017辽宁新宾期中）\",\n" +
                    "\"solution\": \"<p>A项“使人快乐的”；B项“轻松的”；C项“无趣的”；D项“有趣的”。由句意可知他们不喜欢这次运动的原因是他们认为它是无趣的。</p>\",\n" +
                    "\"createTime\": 1512035417,\n" +
                    "\"content\": \"<p>My classmates don't like the sport. They think it is <blk mlen=\\\"1\\\" mstyle=\\\"underline\\\"></blk>.</p>\",\n" +
                    "\"score\": 10,\n" +
                    "\"role\": 0,\n" +
                    "\"answer\": \"<p>C</p>\",\n" +
                    "\"type\": 101\n" +
                    "},\n" +
                    "{\n" +
                    "\"origin\": \"（2017江西吉安一中月考）\",\n" +
                    "\"status\": 0,\n" +
                    "\"updateTime\": 1512035417,\n" +
                    "\"version\": 589,\n" +
                    "\"qid\": 176010552,\n" +
                    "\"accessory\": [{\n" +
                    "\"type\": 101,\n" +
                    "\"options\": [\"are; is\",\n" +
                    "\"am; is\",\n" +
                    "\"is; are\",\n" +
                    "\"is; am\"]\n" +
                    "}],\n" +
                    "\"correctAnswer\": [\"A\"],\n" +
                    "\"id\": 176010552,\n" +
                    "\"no\": \"5、\",\n" +
                    "\"shortOrigin\": \"（2017江西吉安一中月考）\",\n" +
                    "\"solution\": \"<p>第一句话的主语是两个人，故be动词用are；第二句的主语He是第三人称单数形式，故be动词用is。</p>\",\n" +
                    "\"createTime\": 1512035417,\n" +
                    "\"content\": \"<p>Tom and I <u>　　</u> good friends. He <u>　　</u> twelve.(<blk mlen=\\\"1\\\"></blk>)</p>\",\n" +
                    "\"score\": 10,\n" +
                    "\"role\": 0,\n" +
                    "\"answer\": \"<p>A</p>\",\n" +
                    "\"type\": 101\n" +
                    "},\n" +
                    "{\n" +
                    "\"origin\": \"（2016陕西西安七十中月考）\",\n" +
                    "\"status\": 0,\n" +
                    "\"updateTime\": 1512035417,\n" +
                    "\"version\": 589,\n" +
                    "\"qid\": 176011552,\n" +
                    "\"accessory\": [{\n" +
                    "\"type\": 101,\n" +
                    "\"options\": [\"in\",\n" +
                    "\"to\",\n" +
                    "\"of\",\n" +
                    "\"at\"]\n" +
                    "}],\n" +
                    "\"correctAnswer\": [\"C\"],\n" +
                    "\"id\": 176011552,\n" +
                    "\"no\": \"6、\",\n" +
                    "\"shortOrigin\": \"（2016陕西西安七十中月考）\",\n" +
                    "\"solution\": \"<p>a photo of意为“一张……的照片”。</p>\",\n" +
                    "\"createTime\": 1512035417,\n" +
                    "\"content\": \"<p>—This is a photo <blk mlen=\\\"1\\\" mstyle=\\\"underline\\\"></blk> my aunt.</p><p>—It is nice.</p>\",\n" +
                    "\"score\": 10,\n" +
                    "\"role\": 0,\n" +
                    "\"answer\": \"<p>C</p>\",\n" +
                    "\"type\": 101\n" +
                    "},\n" +
                    "{\n" +
                    "\"origin\": \"（2016山东武城二中月考）\",\n" +
                    "\"status\": 0,\n" +
                    "\"updateTime\": 1512035417,\n" +
                    "\"version\": 589,\n" +
                    "\"qid\": 176012552,\n" +
                    "\"accessory\": [{\n" +
                    "\"type\": 101,\n" +
                    "\"options\": [\"spell\",\n" +
                    "\"listen\",\n" +
                    "\"meet\",\n" +
                    "\"draw\"]\n" +
                    "}],\n" +
                    "\"correctAnswer\": [\"A\"],\n" +
                    "\"id\": 176012552,\n" +
                    "\"no\": \"7、\",\n" +
                    "\"shortOrigin\": \"（2016山东武城二中月考）\",\n" +
                    "\"solution\": \"<p>根据答语可知问句是问“你能拼写你的名字吗？”。</p>\",\n" +
                    "\"createTime\": 1512035417,\n" +
                    "\"content\": \"<p>—Can you <blk mlen=\\\"1\\\" mstyle=\\\"underline\\\"></blk> your name?</p><p>—Yes, K-I-M-I, Kimi.</p>\",\n" +
                    "\"score\": 10,\n" +
                    "\"role\": 0,\n" +
                    "\"answer\": \"<p>A</p>\",\n" +
                    "\"type\": 101\n" +
                    "},\n" +
                    "{\n" +
                    "\"origin\": \"（2016安徽宿州埇桥区闵贤中学月考）\",\n" +
                    "\"status\": 0,\n" +
                    "\"updateTime\": 1512035417,\n" +
                    "\"version\": 589,\n" +
                    "\"qid\": 176013552,\n" +
                    "\"accessory\": [{\n" +
                    "\"type\": 101,\n" +
                    "\"options\": [\"You're welcome\",\n" +
                    "\"No, thanks\",\n" +
                    "\"That's right\",\n" +
                    "\"Of course\"]\n" +
                    "}],\n" +
                    "\"correctAnswer\": [\"A\"],\n" +
                    "\"id\": 176013552,\n" +
                    "\"no\": \"8、\",\n" +
                    "\"shortOrigin\": \"（2016安徽宿州埇桥区闵贤中学月考）\",\n" +
                    "\"solution\": \"<p>对别人的答谢常用You're welcome. 来回答。</p>\",\n" +
                    "\"createTime\": 1512035417,\n" +
                    "\"content\": \"<p>—Thank you for your help.</p><p>—<blk mlen=\\\"1\\\" mstyle=\\\"underline\\\"></blk>.</p>\",\n" +
                    "\"score\": 10,\n" +
                    "\"role\": 0,\n" +
                    "\"answer\": \"<p>A</p>\",\n" +
                    "\"type\": 101\n" +
                    "},\n" +
                    "{\n" +
                    "\"origin\": \"（2016安徽宁国西津中学月考）\",\n" +
                    "\"status\": 0,\n" +
                    "\"updateTime\": 1512035417,\n" +
                    "\"version\": 589,\n" +
                    "\"qid\": 176014552,\n" +
                    "\"accessory\": [{\n" +
                    "\"type\": 101,\n" +
                    "\"options\": [\"family\",\n" +
                    "\"first\",\n" +
                    "\"last\",\n" +
                    "\"full\"]\n" +
                    "}],\n" +
                    "\"correctAnswer\": [\"B\"],\n" +
                    "\"id\": 176014552,\n" +
                    "\"no\": \"9、\",\n" +
                    "\"shortOrigin\": \"（2016安徽宁国西津中学月考）\",\n" +
                    "\"solution\": \"<p>句意：我的姓名是Jenny Brown, Jenny是我的名字。</p>\",\n" +
                    "\"createTime\": 1512035417,\n" +
                    "\"content\": \"<p>My name is Jenny Brown. Jenny is my <blk mlen=\\\"1\\\" mstyle=\\\"underline\\\"></blk> name.</p>\",\n" +
                    "\"score\": 10,\n" +
                    "\"role\": 0,\n" +
                    "\"answer\": \"<p>B</p>\",\n" +
                    "\"type\": 101\n" +
                    "},\n" +
                    "{\n" +
                    "\"origin\": \"（2016吉林长春农安期末）\",\n" +
                    "\"status\": 0,\n" +
                    "\"updateTime\": 1512035417,\n" +
                    "\"version\": 589,\n" +
                    "\"qid\": 176015552,\n" +
                    "\"accessory\": [{\n" +
                    "\"type\": 101,\n" +
                    "\"options\": [\"short\",\n" +
                    "\"tall\",\n" +
                    "\"long\",\n" +
                    "\"small\"]\n" +
                    "}],\n" +
                    "\"correctAnswer\": [\"D\"],\n" +
                    "\"id\": 176015552,\n" +
                    "\"no\": \"10、\",\n" +
                    "\"shortOrigin\": \"（2016吉林长春农安期末）\",\n" +
                    "\"solution\": \"<p>根据答语中的“我想要一顶大的帽子。”可知这顶帽子太小了。</p>\",\n" +
                    "\"createTime\": 1512035417,\n" +
                    "\"content\": \"<p>—Do you like the hat?</p><p>—No, I don't. It's too <blk mlen=\\\"1\\\" mstyle=\\\"underline\\\"></blk>. I want a big one.</p>\",\n" +
                    "\"score\": 10,\n" +
                    "\"role\": 0,\n" +
                    "\"answer\": \"<p>D</p>\",\n" +
                    "\"type\": 101\n" +
                    "}\n" +
                    "]");
            List<QuestionInfo> infoList = ErrorQuestionDB.questionsFromJSONArray(jsonArray);
            for (int i = 0; i < infoList.size(); i++) {
                QuestionInfo info = infoList.get(i);
                TinyQuestionInfo tInfo = new TinyQuestionInfo(info.getId(), info.getType(), info.getRole(), info.getOrgInfo());
                eqList.add(tInfo);
            }

            jsonArray = new JSONArray("[\n" +
                    "{\n" +
                    "\"status\": 0,\n" +
                    "\"updateTime\": 1512035440,\n" +
                    "\"qid\": 176016552,\n" +
                    "\"createTime\": 1512035437,\n" +
                    "\"content\": \"<p>Do you know many students aren't healthy today?</p><p>Now many students know sports are good for <a href=\\\"#\\\" data-ref=\\\"1\\\"><u>　1　</u></a> health. But they don't like <a href=\\\"#\\\" data-ref=\\\"2\\\"><u>　2　</u></a>. Some of them only watch them <a href=\\\"#\\\" data-ref=\\\"3\\\"><u>　3　</u></a> TV. What's more（而且）, many of them only like watching TV and <a href=\\\"#\\\" data-ref=\\\"4\\\"><u>　4　</u></a> computer games. They often say,“Play soccer? Oh, it sounds <a href=\\\"#\\\" data-ref=\\\"5\\\"><u>　5　</u></a>. Play basketball? Oh, it sounds difficult.”</p><p><img src=\\\"/resources/fta2/201755/439451055/fceba42fb7f9f1fff4ea04175b5f09df.jpg\\\" width=\\\"298\\\" height=\\\"162\\\"/></p><p>And many boys and girls don't like <a href=\\\"#\\\" data-ref=\\\"6\\\"><u>　6　</u></a>. They don't have carrots for <a href=\\\"#\\\" data-ref=\\\"7\\\"><u>　7　</u></a> and they don't have broccoli for dinner. But vegetables and fruit are <a href=\\\"#\\\" data-ref=\\\"8\\\"><u>　8　</u></a> food. They need to eat lots of them every day. <a href=\\\"#\\\" data-ref=\\\"9\\\"><u>　9　</u></a> they like junk food（垃圾食品）. They like <a href=\\\"#\\\" data-ref=\\\"10\\\"><u>　10　</u></a> French fries, hamburgers, fast food...</p><p>So if（如果）you want to be healthy, eat vegetables and play sports every day!</p>\",\n" +
                    "\"version\": 589,\n" +
                    "\"role\": 1,\n" +
                    "\"relation\": [176017552,\n" +
                    "176018552,\n" +
                    "176019552,\n" +
                    "176020552,\n" +
                    "176021552,\n" +
                    "176022552,\n" +
                    "176023552,\n" +
                    "176024552,\n" +
                    "176025552,\n" +
                    "176026552],\n" +
                    "\"type\": 104,\n" +
                    "\"id\": 176016552\n" +
                    "},\n" +
                    "{\n" +
                    "\"status\": 0,\n" +
                    "\"updateTime\": 1512035440,\n" +
                    "\"version\": 589,\n" +
                    "\"qid\": 176017552,\n" +
                    "\"material\": {\n" +
                    "\"content\": \"<p>Do you know many students aren't healthy today?</p><p>Now many students know sports are good for <a href=\\\"#\\\" data-ref=\\\"1\\\"><u>　1　</u></a> health. But they don't like <a href=\\\"#\\\" data-ref=\\\"2\\\"><u>　2　</u></a>. Some of them only watch them <a href=\\\"#\\\" data-ref=\\\"3\\\"><u>　3　</u></a> TV. What's more（而且）, many of them only like watching TV and <a href=\\\"#\\\" data-ref=\\\"4\\\"><u>　4　</u></a> computer games. They often say,“Play soccer? Oh, it sounds <a href=\\\"#\\\" data-ref=\\\"5\\\"><u>　5　</u></a>. Play basketball? Oh, it sounds difficult.”</p><p><img src=\\\"/resources/fta2/201755/439451055/fceba42fb7f9f1fff4ea04175b5f09df.jpg\\\" width=\\\"298\\\" height=\\\"162\\\"/></p><p>And many boys and girls don't like <a href=\\\"#\\\" data-ref=\\\"6\\\"><u>　6　</u></a>. They don't have carrots for <a href=\\\"#\\\" data-ref=\\\"7\\\"><u>　7　</u></a> and they don't have broccoli for dinner. But vegetables and fruit are <a href=\\\"#\\\" data-ref=\\\"8\\\"><u>　8　</u></a> food. They need to eat lots of them every day. <a href=\\\"#\\\" data-ref=\\\"9\\\"><u>　9　</u></a> they like junk food（垃圾食品）. They like <a href=\\\"#\\\" data-ref=\\\"10\\\"><u>　10　</u></a> French fries, hamburgers, fast food...</p><p>So if（如果）you want to be healthy, eat vegetables and play sports every day!</p>\",\n" +
                    "\"id\": 176016552\n" +
                    "},\n" +
                    "\"accessory\": [{\n" +
                    "\"type\": 101,\n" +
                    "\"options\": [\"their\",\n" +
                    "\"her\",\n" +
                    "\"his\",\n" +
                    "\"your\"]\n" +
                    "}],\n" +
                    "\"relation\": [176017552,\n" +
                    "176018552,\n" +
                    "176019552,\n" +
                    "176020552,\n" +
                    "176021552,\n" +
                    "176022552,\n" +
                    "176023552,\n" +
                    "176024552,\n" +
                    "176025552,\n" +
                    "176026552],\n" +
                    "\"correctAnswer\": [\"A\"],\n" +
                    "\"id\": 176017552,\n" +
                    "\"solution\": \"<p>由主语many students和后面的名词health可知，用形容词性物主代词their。</p>\",\n" +
                    "\"createTime\": 1512035437,\n" +
                    "\"content\": \"<p>1、(<blk mlen=\\\"1\\\"></blk>)</p>\",\n" +
                    "\"score\": 10,\n" +
                    "\"role\": 2,\n" +
                    "\"answer\": \"<p>A</p>\",\n" +
                    "\"type\": 101\n" +
                    "},\n" +
                    "{\n" +
                    "\"status\": 0,\n" +
                    "\"updateTime\": 1512035440,\n" +
                    "\"version\": 589,\n" +
                    "\"qid\": 176018552,\n" +
                    "\"material\": {\n" +
                    "\"content\": \"<p>Do you know many students aren't healthy today?</p><p>Now many students know sports are good for <a href=\\\"#\\\" data-ref=\\\"1\\\"><u>　1　</u></a> health. But they don't like <a href=\\\"#\\\" data-ref=\\\"2\\\"><u>　2　</u></a>. Some of them only watch them <a href=\\\"#\\\" data-ref=\\\"3\\\"><u>　3　</u></a> TV. What's more（而且）, many of them only like watching TV and <a href=\\\"#\\\" data-ref=\\\"4\\\"><u>　4　</u></a> computer games. They often say,“Play soccer? Oh, it sounds <a href=\\\"#\\\" data-ref=\\\"5\\\"><u>　5　</u></a>. Play basketball? Oh, it sounds difficult.”</p><p><img src=\\\"/resources/fta2/201755/439451055/fceba42fb7f9f1fff4ea04175b5f09df.jpg\\\" width=\\\"298\\\" height=\\\"162\\\"/></p><p>And many boys and girls don't like <a href=\\\"#\\\" data-ref=\\\"6\\\"><u>　6　</u></a>. They don't have carrots for <a href=\\\"#\\\" data-ref=\\\"7\\\"><u>　7　</u></a> and they don't have broccoli for dinner. But vegetables and fruit are <a href=\\\"#\\\" data-ref=\\\"8\\\"><u>　8　</u></a> food. They need to eat lots of them every day. <a href=\\\"#\\\" data-ref=\\\"9\\\"><u>　9　</u></a> they like junk food（垃圾食品）. They like <a href=\\\"#\\\" data-ref=\\\"10\\\"><u>　10　</u></a> French fries, hamburgers, fast food...</p><p>So if（如果）you want to be healthy, eat vegetables and play sports every day!</p>\",\n" +
                    "\"id\": 176016552\n" +
                    "},\n" +
                    "\"accessory\": [{\n" +
                    "\"type\": 101,\n" +
                    "\"options\": [\"play sport\",\n" +
                    "\"playing sport\",\n" +
                    "\"play sports\",\n" +
                    "\"playing sports\"]\n" +
                    "}],\n" +
                    "\"relation\": [176017552,\n" +
                    "176018552,\n" +
                    "176019552,\n" +
                    "176020552,\n" +
                    "176021552,\n" +
                    "176022552,\n" +
                    "176023552,\n" +
                    "176024552,\n" +
                    "176025552,\n" +
                    "176026552],\n" +
                    "\"correctAnswer\": [\"D\"],\n" +
                    "\"id\": 176018552,\n" +
                    "\"solution\": \"<p>like doing sth. 喜欢做某事，play sports进行体育运动。</p>\",\n" +
                    "\"createTime\": 1512035437,\n" +
                    "\"content\": \"<p>2、(<blk mlen=\\\"1\\\"></blk>)</p>\",\n" +
                    "\"score\": 10,\n" +
                    "\"role\": 2,\n" +
                    "\"answer\": \"<p>D</p>\",\n" +
                    "\"type\": 101\n" +
                    "},\n" +
                    "{\n" +
                    "\"status\": 0,\n" +
                    "\"updateTime\": 1512035440,\n" +
                    "\"version\": 589,\n" +
                    "\"qid\": 176019552,\n" +
                    "\"material\": {\n" +
                    "\"content\": \"<p>Do you know many students aren't healthy today?</p><p>Now many students know sports are good for <a href=\\\"#\\\" data-ref=\\\"1\\\"><u>　1　</u></a> health. But they don't like <a href=\\\"#\\\" data-ref=\\\"2\\\"><u>　2　</u></a>. Some of them only watch them <a href=\\\"#\\\" data-ref=\\\"3\\\"><u>　3　</u></a> TV. What's more（而且）, many of them only like watching TV and <a href=\\\"#\\\" data-ref=\\\"4\\\"><u>　4　</u></a> computer games. They often say,“Play soccer? Oh, it sounds <a href=\\\"#\\\" data-ref=\\\"5\\\"><u>　5　</u></a>. Play basketball? Oh, it sounds difficult.”</p><p><img src=\\\"/resources/fta2/201755/439451055/fceba42fb7f9f1fff4ea04175b5f09df.jpg\\\" width=\\\"298\\\" height=\\\"162\\\"/></p><p>And many boys and girls don't like <a href=\\\"#\\\" data-ref=\\\"6\\\"><u>　6　</u></a>. They don't have carrots for <a href=\\\"#\\\" data-ref=\\\"7\\\"><u>　7　</u></a> and they don't have broccoli for dinner. But vegetables and fruit are <a href=\\\"#\\\" data-ref=\\\"8\\\"><u>　8　</u></a> food. They need to eat lots of them every day. <a href=\\\"#\\\" data-ref=\\\"9\\\"><u>　9　</u></a> they like junk food（垃圾食品）. They like <a href=\\\"#\\\" data-ref=\\\"10\\\"><u>　10　</u></a> French fries, hamburgers, fast food...</p><p>So if（如果）you want to be healthy, eat vegetables and play sports every day!</p>\",\n" +
                    "\"id\": 176016552\n" +
                    "},\n" +
                    "\"accessory\": [{\n" +
                    "\"type\": 101,\n" +
                    "\"options\": [\"in\",\n" +
                    "\"on\",\n" +
                    "\"at\",\n" +
                    "\"to\"]\n" +
                    "}],\n" +
                    "\"relation\": [176017552,\n" +
                    "176018552,\n" +
                    "176019552,\n" +
                    "176020552,\n" +
                    "176021552,\n" +
                    "176022552,\n" +
                    "176023552,\n" +
                    "176024552,\n" +
                    "176025552,\n" +
                    "176026552],\n" +
                    "\"correctAnswer\": [\"B\"],\n" +
                    "\"id\": 176019552,\n" +
                    "\"solution\": \"<p>通过电视观看……，用介词on。</p>\",\n" +
                    "\"createTime\": 1512035438,\n" +
                    "\"content\": \"<p>3、(<blk mlen=\\\"1\\\"></blk>)</p>\",\n" +
                    "\"score\": 10,\n" +
                    "\"role\": 2,\n" +
                    "\"answer\": \"<p>B</p>\",\n" +
                    "\"type\": 101\n" +
                    "},\n" +
                    "{\n" +
                    "\"status\": 0,\n" +
                    "\"updateTime\": 1512035440,\n" +
                    "\"version\": 589,\n" +
                    "\"qid\": 176020552,\n" +
                    "\"material\": {\n" +
                    "\"content\": \"<p>Do you know many students aren't healthy today?</p><p>Now many students know sports are good for <a href=\\\"#\\\" data-ref=\\\"1\\\"><u>　1　</u></a> health. But they don't like <a href=\\\"#\\\" data-ref=\\\"2\\\"><u>　2　</u></a>. Some of them only watch them <a href=\\\"#\\\" data-ref=\\\"3\\\"><u>　3　</u></a> TV. What's more（而且）, many of them only like watching TV and <a href=\\\"#\\\" data-ref=\\\"4\\\"><u>　4　</u></a> computer games. They often say,“Play soccer? Oh, it sounds <a href=\\\"#\\\" data-ref=\\\"5\\\"><u>　5　</u></a>. Play basketball? Oh, it sounds difficult.”</p><p><img src=\\\"/resources/fta2/201755/439451055/fceba42fb7f9f1fff4ea04175b5f09df.jpg\\\" width=\\\"298\\\" height=\\\"162\\\"/></p><p>And many boys and girls don't like <a href=\\\"#\\\" data-ref=\\\"6\\\"><u>　6　</u></a>. They don't have carrots for <a href=\\\"#\\\" data-ref=\\\"7\\\"><u>　7　</u></a> and they don't have broccoli for dinner. But vegetables and fruit are <a href=\\\"#\\\" data-ref=\\\"8\\\"><u>　8　</u></a> food. They need to eat lots of them every day. <a href=\\\"#\\\" data-ref=\\\"9\\\"><u>　9　</u></a> they like junk food（垃圾食品）. They like <a href=\\\"#\\\" data-ref=\\\"10\\\"><u>　10　</u></a> French fries, hamburgers, fast food...</p><p>So if（如果）you want to be healthy, eat vegetables and play sports every day!</p>\",\n" +
                    "\"id\": 176016552\n" +
                    "},\n" +
                    "\"accessory\": [{\n" +
                    "\"type\": 101,\n" +
                    "\"options\": [\"player\",\n" +
                    "\"play\",\n" +
                    "\"playing\",\n" +
                    "\"plays\"]\n" +
                    "}],\n" +
                    "\"relation\": [176017552,\n" +
                    "176018552,\n" +
                    "176019552,\n" +
                    "176020552,\n" +
                    "176021552,\n" +
                    "176022552,\n" +
                    "176023552,\n" +
                    "176024552,\n" +
                    "176025552,\n" +
                    "176026552],\n" +
                    "\"correctAnswer\": [\"C\"],\n" +
                    "\"id\": 176020552,\n" +
                    "\"solution\": \"<p>此处应与watching形式一致。</p>\",\n" +
                    "\"createTime\": 1512035438,\n" +
                    "\"content\": \"<p>4、(<blk mlen=\\\"1\\\"></blk>)</p>\",\n" +
                    "\"score\": 10,\n" +
                    "\"role\": 2,\n" +
                    "\"answer\": \"<p>C</p>\",\n" +
                    "\"type\": 101\n" +
                    "},\n" +
                    "{\n" +
                    "\"status\": 0,\n" +
                    "\"updateTime\": 1512035440,\n" +
                    "\"version\": 589,\n" +
                    "\"qid\": 176021552,\n" +
                    "\"material\": {\n" +
                    "\"content\": \"<p>Do you know many students aren't healthy today?</p><p>Now many students know sports are good for <a href=\\\"#\\\" data-ref=\\\"1\\\"><u>　1　</u></a> health. But they don't like <a href=\\\"#\\\" data-ref=\\\"2\\\"><u>　2　</u></a>. Some of them only watch them <a href=\\\"#\\\" data-ref=\\\"3\\\"><u>　3　</u></a> TV. What's more（而且）, many of them only like watching TV and <a href=\\\"#\\\" data-ref=\\\"4\\\"><u>　4　</u></a> computer games. They often say,“Play soccer? Oh, it sounds <a href=\\\"#\\\" data-ref=\\\"5\\\"><u>　5　</u></a>. Play basketball? Oh, it sounds difficult.”</p><p><img src=\\\"/resources/fta2/201755/439451055/fceba42fb7f9f1fff4ea04175b5f09df.jpg\\\" width=\\\"298\\\" height=\\\"162\\\"/></p><p>And many boys and girls don't like <a href=\\\"#\\\" data-ref=\\\"6\\\"><u>　6　</u></a>. They don't have carrots for <a href=\\\"#\\\" data-ref=\\\"7\\\"><u>　7　</u></a> and they don't have broccoli for dinner. But vegetables and fruit are <a href=\\\"#\\\" data-ref=\\\"8\\\"><u>　8　</u></a> food. They need to eat lots of them every day. <a href=\\\"#\\\" data-ref=\\\"9\\\"><u>　9　</u></a> they like junk food（垃圾食品）. They like <a href=\\\"#\\\" data-ref=\\\"10\\\"><u>　10　</u></a> French fries, hamburgers, fast food...</p><p>So if（如果）you want to be healthy, eat vegetables and play sports every day!</p>\",\n" +
                    "\"id\": 176016552\n" +
                    "},\n" +
                    "\"accessory\": [{\n" +
                    "\"type\": 101,\n" +
                    "\"options\": [\"interesting\",\n" +
                    "\"boring\",\n" +
                    "\"fun\",\n" +
                    "\"nice\"]\n" +
                    "}],\n" +
                    "\"relation\": [176017552,\n" +
                    "176018552,\n" +
                    "176019552,\n" +
                    "176020552,\n" +
                    "176021552,\n" +
                    "176022552,\n" +
                    "176023552,\n" +
                    "176024552,\n" +
                    "176025552,\n" +
                    "176026552],\n" +
                    "\"correctAnswer\": [\"B\"],\n" +
                    "\"id\": 176021552,\n" +
                    "\"solution\": \"<p>由语境可知，他们认为踢足球听起来很无聊（boring）。</p>\",\n" +
                    "\"createTime\": 1512035438,\n" +
                    "\"content\": \"<p>5、(<blk mlen=\\\"1\\\"></blk>)</p>\",\n" +
                    "\"score\": 10,\n" +
                    "\"role\": 2,\n" +
                    "\"answer\": \"<p>B</p>\",\n" +
                    "\"type\": 101\n" +
                    "},\n" +
                    "{\n" +
                    "\"status\": 0,\n" +
                    "\"updateTime\": 1512035440,\n" +
                    "\"version\": 589,\n" +
                    "\"qid\": 176022552,\n" +
                    "\"material\": {\n" +
                    "\"content\": \"<p>Do you know many students aren't healthy today?</p><p>Now many students know sports are good for <a href=\\\"#\\\" data-ref=\\\"1\\\"><u>　1　</u></a> health. But they don't like <a href=\\\"#\\\" data-ref=\\\"2\\\"><u>　2　</u></a>. Some of them only watch them <a href=\\\"#\\\" data-ref=\\\"3\\\"><u>　3　</u></a> TV. What's more（而且）, many of them only like watching TV and <a href=\\\"#\\\" data-ref=\\\"4\\\"><u>　4　</u></a> computer games. They often say,“Play soccer? Oh, it sounds <a href=\\\"#\\\" data-ref=\\\"5\\\"><u>　5　</u></a>. Play basketball? Oh, it sounds difficult.”</p><p><img src=\\\"/resources/fta2/201755/439451055/fceba42fb7f9f1fff4ea04175b5f09df.jpg\\\" width=\\\"298\\\" height=\\\"162\\\"/></p><p>And many boys and girls don't like <a href=\\\"#\\\" data-ref=\\\"6\\\"><u>　6　</u></a>. They don't have carrots for <a href=\\\"#\\\" data-ref=\\\"7\\\"><u>　7　</u></a> and they don't have broccoli for dinner. But vegetables and fruit are <a href=\\\"#\\\" data-ref=\\\"8\\\"><u>　8　</u></a> food. They need to eat lots of them every day. <a href=\\\"#\\\" data-ref=\\\"9\\\"><u>　9　</u></a> they like junk food（垃圾食品）. They like <a href=\\\"#\\\" data-ref=\\\"10\\\"><u>　10　</u></a> French fries, hamburgers, fast food...</p><p>So if（如果）you want to be healthy, eat vegetables and play sports every day!</p>\",\n" +
                    "\"id\": 176016552\n" +
                    "},\n" +
                    "\"accessory\": [{\n" +
                    "\"type\": 101,\n" +
                    "\"options\": [\"meat\",\n" +
                    "\"vegetables\",\n" +
                    "\"fruit\",\n" +
                    "\"dessert\"]\n" +
                    "}],\n" +
                    "\"relation\": [176017552,\n" +
                    "176018552,\n" +
                    "176019552,\n" +
                    "176020552,\n" +
                    "176021552,\n" +
                    "176022552,\n" +
                    "176023552,\n" +
                    "176024552,\n" +
                    "176025552,\n" +
                    "176026552],\n" +
                    "\"correctAnswer\": [\"B\"],\n" +
                    "\"id\": 176022552,\n" +
                    "\"solution\": \"<p>由后面他们不吃胡萝卜和西兰花可知，他们不喜欢吃蔬菜（vegetables）。</p>\",\n" +
                    "\"createTime\": 1512035438,\n" +
                    "\"content\": \"<p>6、(<blk mlen=\\\"1\\\"></blk>)</p>\",\n" +
                    "\"score\": 10,\n" +
                    "\"role\": 2,\n" +
                    "\"answer\": \"<p>B</p>\",\n" +
                    "\"type\": 101\n" +
                    "},\n" +
                    "{\n" +
                    "\"status\": 0,\n" +
                    "\"updateTime\": 1512035440,\n" +
                    "\"version\": 589,\n" +
                    "\"qid\": 176023552,\n" +
                    "\"material\": {\n" +
                    "\"content\": \"<p>Do you know many students aren't healthy today?</p><p>Now many students know sports are good for <a href=\\\"#\\\" data-ref=\\\"1\\\"><u>　1　</u></a> health. But they don't like <a href=\\\"#\\\" data-ref=\\\"2\\\"><u>　2　</u></a>. Some of them only watch them <a href=\\\"#\\\" data-ref=\\\"3\\\"><u>　3　</u></a> TV. What's more（而且）, many of them only like watching TV and <a href=\\\"#\\\" data-ref=\\\"4\\\"><u>　4　</u></a> computer games. They often say,“Play soccer? Oh, it sounds <a href=\\\"#\\\" data-ref=\\\"5\\\"><u>　5　</u></a>. Play basketball? Oh, it sounds difficult.”</p><p><img src=\\\"/resources/fta2/201755/439451055/fceba42fb7f9f1fff4ea04175b5f09df.jpg\\\" width=\\\"298\\\" height=\\\"162\\\"/></p><p>And many boys and girls don't like <a href=\\\"#\\\" data-ref=\\\"6\\\"><u>　6　</u></a>. They don't have carrots for <a href=\\\"#\\\" data-ref=\\\"7\\\"><u>　7　</u></a> and they don't have broccoli for dinner. But vegetables and fruit are <a href=\\\"#\\\" data-ref=\\\"8\\\"><u>　8　</u></a> food. They need to eat lots of them every day. <a href=\\\"#\\\" data-ref=\\\"9\\\"><u>　9　</u></a> they like junk food（垃圾食品）. They like <a href=\\\"#\\\" data-ref=\\\"10\\\"><u>　10　</u></a> French fries, hamburgers, fast food...</p><p>So if（如果）you want to be healthy, eat vegetables and play sports every day!</p>\",\n" +
                    "\"id\": 176016552\n" +
                    "},\n" +
                    "\"accessory\": [{\n" +
                    "\"type\": 101,\n" +
                    "\"options\": [\"drink\",\n" +
                    "\"dinner\",\n" +
                    "\"play\",\n" +
                    "\"lunch\"]\n" +
                    "}],\n" +
                    "\"relation\": [176017552,\n" +
                    "176018552,\n" +
                    "176019552,\n" +
                    "176020552,\n" +
                    "176021552,\n" +
                    "176022552,\n" +
                    "176023552,\n" +
                    "176024552,\n" +
                    "176025552,\n" +
                    "176026552],\n" +
                    "\"correctAnswer\": [\"D\"],\n" +
                    "\"id\": 176023552,\n" +
                    "\"solution\": \"<p>由后面的dinner可知，这里表示午餐（lunch）。</p>\",\n" +
                    "\"createTime\": 1512035439,\n" +
                    "\"content\": \"<p>7、(<blk mlen=\\\"1\\\"></blk>)</p>\",\n" +
                    "\"score\": 10,\n" +
                    "\"role\": 2,\n" +
                    "\"answer\": \"<p>D</p>\",\n" +
                    "\"type\": 101\n" +
                    "},\n" +
                    "{\n" +
                    "\"status\": 0,\n" +
                    "\"updateTime\": 1512035440,\n" +
                    "\"version\": 589,\n" +
                    "\"qid\": 176024552,\n" +
                    "\"material\": {\n" +
                    "\"content\": \"<p>Do you know many students aren't healthy today?</p><p>Now many students know sports are good for <a href=\\\"#\\\" data-ref=\\\"1\\\"><u>　1　</u></a> health. But they don't like <a href=\\\"#\\\" data-ref=\\\"2\\\"><u>　2　</u></a>. Some of them only watch them <a href=\\\"#\\\" data-ref=\\\"3\\\"><u>　3　</u></a> TV. What's more（而且）, many of them only like watching TV and <a href=\\\"#\\\" data-ref=\\\"4\\\"><u>　4　</u></a> computer games. They often say,“Play soccer? Oh, it sounds <a href=\\\"#\\\" data-ref=\\\"5\\\"><u>　5　</u></a>. Play basketball? Oh, it sounds difficult.”</p><p><img src=\\\"/resources/fta2/201755/439451055/fceba42fb7f9f1fff4ea04175b5f09df.jpg\\\" width=\\\"298\\\" height=\\\"162\\\"/></p><p>And many boys and girls don't like <a href=\\\"#\\\" data-ref=\\\"6\\\"><u>　6　</u></a>. They don't have carrots for <a href=\\\"#\\\" data-ref=\\\"7\\\"><u>　7　</u></a> and they don't have broccoli for dinner. But vegetables and fruit are <a href=\\\"#\\\" data-ref=\\\"8\\\"><u>　8　</u></a> food. They need to eat lots of them every day. <a href=\\\"#\\\" data-ref=\\\"9\\\"><u>　9　</u></a> they like junk food（垃圾食品）. They like <a href=\\\"#\\\" data-ref=\\\"10\\\"><u>　10　</u></a> French fries, hamburgers, fast food...</p><p>So if（如果）you want to be healthy, eat vegetables and play sports every day!</p>\",\n" +
                    "\"id\": 176016552\n" +
                    "},\n" +
                    "\"accessory\": [{\n" +
                    "\"type\": 101,\n" +
                    "\"options\": [\"healthy\",\n" +
                    "\"health\",\n" +
                    "\"first\",\n" +
                    "\"last\"]\n" +
                    "}],\n" +
                    "\"relation\": [176017552,\n" +
                    "176018552,\n" +
                    "176019552,\n" +
                    "176020552,\n" +
                    "176021552,\n" +
                    "176022552,\n" +
                    "176023552,\n" +
                    "176024552,\n" +
                    "176025552,\n" +
                    "176026552],\n" +
                    "\"correctAnswer\": [\"A\"],\n" +
                    "\"id\": 176024552,\n" +
                    "\"solution\": \"<p>由常识可知，蔬菜和水果是健康食品。healthy健康的。</p>\",\n" +
                    "\"createTime\": 1512035439,\n" +
                    "\"content\": \"<p>8、(<blk mlen=\\\"1\\\"></blk>)</p>\",\n" +
                    "\"score\": 10,\n" +
                    "\"role\": 2,\n" +
                    "\"answer\": \"<p>A</p>\",\n" +
                    "\"type\": 101\n" +
                    "},\n" +
                    "{\n" +
                    "\"status\": 0,\n" +
                    "\"updateTime\": 1512035440,\n" +
                    "\"version\": 589,\n" +
                    "\"qid\": 176025552,\n" +
                    "\"material\": {\n" +
                    "\"content\": \"<p>Do you know many students aren't healthy today?</p><p>Now many students know sports are good for <a href=\\\"#\\\" data-ref=\\\"1\\\"><u>　1　</u></a> health. But they don't like <a href=\\\"#\\\" data-ref=\\\"2\\\"><u>　2　</u></a>. Some of them only watch them <a href=\\\"#\\\" data-ref=\\\"3\\\"><u>　3　</u></a> TV. What's more（而且）, many of them only like watching TV and <a href=\\\"#\\\" data-ref=\\\"4\\\"><u>　4　</u></a> computer games. They often say,“Play soccer? Oh, it sounds <a href=\\\"#\\\" data-ref=\\\"5\\\"><u>　5　</u></a>. Play basketball? Oh, it sounds difficult.”</p><p><img src=\\\"/resources/fta2/201755/439451055/fceba42fb7f9f1fff4ea04175b5f09df.jpg\\\" width=\\\"298\\\" height=\\\"162\\\"/></p><p>And many boys and girls don't like <a href=\\\"#\\\" data-ref=\\\"6\\\"><u>　6　</u></a>. They don't have carrots for <a href=\\\"#\\\" data-ref=\\\"7\\\"><u>　7　</u></a> and they don't have broccoli for dinner. But vegetables and fruit are <a href=\\\"#\\\" data-ref=\\\"8\\\"><u>　8　</u></a> food. They need to eat lots of them every day. <a href=\\\"#\\\" data-ref=\\\"9\\\"><u>　9　</u></a> they like junk food（垃圾食品）. They like <a href=\\\"#\\\" data-ref=\\\"10\\\"><u>　10　</u></a> French fries, hamburgers, fast food...</p><p>So if（如果）you want to be healthy, eat vegetables and play sports every day!</p>\",\n" +
                    "\"id\": 176016552\n" +
                    "},\n" +
                    "\"accessory\": [{\n" +
                    "\"type\": 101,\n" +
                    "\"options\": [\"And\",\n" +
                    "\"So\",\n" +
                    "\"But\",\n" +
                    "\"Then\"]\n" +
                    "}],\n" +
                    "\"relation\": [176017552,\n" +
                    "176018552,\n" +
                    "176019552,\n" +
                    "176020552,\n" +
                    "176021552,\n" +
                    "176022552,\n" +
                    "176023552,\n" +
                    "176024552,\n" +
                    "176025552,\n" +
                    "176026552],\n" +
                    "\"correctAnswer\": [\"C\"],\n" +
                    "\"id\": 176025552,\n" +
                    "\"solution\": \"<p>由前后两句的意思可知，空格处表示转折关系，用but。</p>\",\n" +
                    "\"createTime\": 1512035439,\n" +
                    "\"content\": \"<p>9、(<blk mlen=\\\"1\\\"></blk>)</p>\",\n" +
                    "\"score\": 10,\n" +
                    "\"role\": 2,\n" +
                    "\"answer\": \"<p>C</p>\",\n" +
                    "\"type\": 101\n" +
                    "},\n" +
                    "{\n" +
                    "\"status\": 0,\n" +
                    "\"updateTime\": 1512035440,\n" +
                    "\"version\": 589,\n" +
                    "\"qid\": 176026552,\n" +
                    "\"material\": {\n" +
                    "\"content\": \"<p>Do you know many students aren't healthy today?</p><p>Now many students know sports are good for <a href=\\\"#\\\" data-ref=\\\"1\\\"><u>　1　</u></a> health. But they don't like <a href=\\\"#\\\" data-ref=\\\"2\\\"><u>　2　</u></a>. Some of them only watch them <a href=\\\"#\\\" data-ref=\\\"3\\\"><u>　3　</u></a> TV. What's more（而且）, many of them only like watching TV and <a href=\\\"#\\\" data-ref=\\\"4\\\"><u>　4　</u></a> computer games. They often say,“Play soccer? Oh, it sounds <a href=\\\"#\\\" data-ref=\\\"5\\\"><u>　5　</u></a>. Play basketball? Oh, it sounds difficult.”</p><p><img src=\\\"/resources/fta2/201755/439451055/fceba42fb7f9f1fff4ea04175b5f09df.jpg\\\" width=\\\"298\\\" height=\\\"162\\\"/></p><p>And many boys and girls don't like <a href=\\\"#\\\" data-ref=\\\"6\\\"><u>　6　</u></a>. They don't have carrots for <a href=\\\"#\\\" data-ref=\\\"7\\\"><u>　7　</u></a> and they don't have broccoli for dinner. But vegetables and fruit are <a href=\\\"#\\\" data-ref=\\\"8\\\"><u>　8　</u></a> food. They need to eat lots of them every day. <a href=\\\"#\\\" data-ref=\\\"9\\\"><u>　9　</u></a> they like junk food（垃圾食品）. They like <a href=\\\"#\\\" data-ref=\\\"10\\\"><u>　10　</u></a> French fries, hamburgers, fast food...</p><p>So if（如果）you want to be healthy, eat vegetables and play sports every day!</p>\",\n" +
                    "\"id\": 176016552\n" +
                    "},\n" +
                    "\"accessory\": [{\n" +
                    "\"type\": 101,\n" +
                    "\"options\": [\"doing\",\n" +
                    "\"eating\",\n" +
                    "\"watching\",\n" +
                    "\"playing\"]\n" +
                    "}],\n" +
                    "\"relation\": [176017552,\n" +
                    "176018552,\n" +
                    "176019552,\n" +
                    "176020552,\n" +
                    "176021552,\n" +
                    "176022552,\n" +
                    "176023552,\n" +
                    "176024552,\n" +
                    "176025552,\n" +
                    "176026552],\n" +
                    "\"correctAnswer\": [\"B\"],\n" +
                    "\"id\": 176026552,\n" +
                    "\"solution\": \"<p>后面全是食品，用eat。</p>\",\n" +
                    "\"createTime\": 1512035439,\n" +
                    "\"content\": \"<p>10、(<blk mlen=\\\"1\\\"></blk>)</p>\",\n" +
                    "\"score\": 10,\n" +
                    "\"role\": 2,\n" +
                    "\"answer\": \"<p>B</p>\",\n" +
                    "\"type\": 101\n" +
                    "}\n" +
                    "]");
            infoList = ErrorQuestionDB.questionsFromJSONArray(jsonArray);
            for (int i = 0; i < infoList.size(); i++) {
                QuestionInfo info = infoList.get(i);
                TinyQuestionInfo tInfo =  new TinyQuestionInfo(info.getId(), info.getType(), info.getRole(), info.getOrgInfo());
                eqList.add(tInfo);
            }

            jsonArray = new JSONArray("[\n" +
                    "{\n" +
                    "\"status\": 0,\n" +
                    "\"updateTime\": 1512035475,\n" +
                    "\"qid\": 176027552,\n" +
                    "\"createTime\": 1512035475,\n" +
                    "\"content\": \"<p align=\\\"center\\\">A</p><p>Dear Lily,</p><p>When you go to Huaxing Store, could you please return（退）my new soccer ball shoes? They are black. I don't like the color. Please get another pair in blue. They're on sale and cost $40.The receipt（收据）is in the box with the shoes.</p><p>Thanks.</p><p align=\\\"right\\\">Mike</p>\",\n" +
                    "\"version\": 589,\n" +
                    "\"role\": 1,\n" +
                    "\"relation\": [176028552,\n" +
                    "176029552,\n" +
                    "176030552,\n" +
                    "176031552,\n" +
                    "176032552],\n" +
                    "\"type\": 0,\n" +
                    "\"id\": 176027552\n" +
                    "},\n" +
                    "{\n" +
                    "\"status\": 0,\n" +
                    "\"updateTime\": 1512035475,\n" +
                    "\"version\": 589,\n" +
                    "\"qid\": 176028552,\n" +
                    "\"material\": {\n" +
                    "\"content\": \"<p align=\\\"center\\\">A</p><p>Dear Lily,</p><p>When you go to Huaxing Store, could you please return（退）my new soccer ball shoes? They are black. I don't like the color. Please get another pair in blue. They're on sale and cost $40.The receipt（收据）is in the box with the shoes.</p><p>Thanks.</p><p align=\\\"right\\\">Mike</p>\",\n" +
                    "\"id\": 176027552\n" +
                    "},\n" +
                    "\"accessory\": [{\n" +
                    "\"type\": 101,\n" +
                    "\"options\": [\"Pants.\",\n" +
                    "\"Shoes.\",\n" +
                    "\"T-shirts.\",\n" +
                    "\"Sweaters.\"]\n" +
                    "}],\n" +
                    "\"relation\": [176028552,\n" +
                    "176029552,\n" +
                    "176030552,\n" +
                    "176031552,\n" +
                    "176032552],\n" +
                    "\"correctAnswer\": [\"B\"],\n" +
                    "\"id\": 176028552,\n" +
                    "\"solution\": \"<p>由...could you please return my new soccer ball shoes? 可知，迈克买的是鞋子。</p>\",\n" +
                    "\"createTime\": 1512035475,\n" +
                    "\"content\": \"<p>1、What does Mike buy in Huaxing Store?(<blk mlen=\\\"1\\\"></blk>)</p>\",\n" +
                    "\"score\": 20,\n" +
                    "\"role\": 2,\n" +
                    "\"answer\": \"<p>B</p>\",\n" +
                    "\"type\": 101\n" +
                    "},\n" +
                    "{\n" +
                    "\"status\": 0,\n" +
                    "\"updateTime\": 1512035475,\n" +
                    "\"version\": 589,\n" +
                    "\"qid\": 176029552,\n" +
                    "\"material\": {\n" +
                    "\"content\": \"<p align=\\\"center\\\">A</p><p>Dear Lily,</p><p>When you go to Huaxing Store, could you please return（退）my new soccer ball shoes? They are black. I don't like the color. Please get another pair in blue. They're on sale and cost $40.The receipt（收据）is in the box with the shoes.</p><p>Thanks.</p><p align=\\\"right\\\">Mike</p>\",\n" +
                    "\"id\": 176027552\n" +
                    "},\n" +
                    "\"accessory\": [{\n" +
                    "\"type\": 101,\n" +
                    "\"options\": [\"Because he doesn't like the color.\",\n" +
                    "\"Because they are too small.\",\n" +
                    "\"Because they are too cheap.\",\n" +
                    "\"Because they cost too much.\"]\n" +
                    "}],\n" +
                    "\"relation\": [176028552,\n" +
                    "176029552,\n" +
                    "176030552,\n" +
                    "176031552,\n" +
                    "176032552],\n" +
                    "\"correctAnswer\": [\"A\"],\n" +
                    "\"id\": 176029552,\n" +
                    "\"solution\": \"<p>由I don't like the color. 可知退货的原因。</p>\",\n" +
                    "\"createTime\": 1512035475,\n" +
                    "\"content\": \"<p>2、Why doesn't Mike like his new shoes?(<blk mlen=\\\"1\\\"></blk>)</p>\",\n" +
                    "\"score\": 20,\n" +
                    "\"role\": 2,\n" +
                    "\"answer\": \"<p>A</p>\",\n" +
                    "\"type\": 101\n" +
                    "},\n" +
                    "{\n" +
                    "\"status\": 0,\n" +
                    "\"updateTime\": 1512035475,\n" +
                    "\"version\": 589,\n" +
                    "\"qid\": 176030552,\n" +
                    "\"material\": {\n" +
                    "\"content\": \"<p align=\\\"center\\\">A</p><p>Dear Lily,</p><p>When you go to Huaxing Store, could you please return（退）my new soccer ball shoes? They are black. I don't like the color. Please get another pair in blue. They're on sale and cost $40.The receipt（收据）is in the box with the shoes.</p><p>Thanks.</p><p align=\\\"right\\\">Mike</p>\",\n" +
                    "\"id\": 176027552\n" +
                    "},\n" +
                    "\"accessory\": [{\n" +
                    "\"type\": 101,\n" +
                    "\"options\": [\"Black.\",\n" +
                    "\"White.\",\n" +
                    "\"Yellow.\",\n" +
                    "\"Blue.\"]\n" +
                    "}],\n" +
                    "\"relation\": [176028552,\n" +
                    "176029552,\n" +
                    "176030552,\n" +
                    "176031552,\n" +
                    "176032552],\n" +
                    "\"correctAnswer\": [\"D\"],\n" +
                    "\"id\": 176030552,\n" +
                    "\"solution\": \"<p>由Please get another pair in blue. 可知，现在迈克想要蓝色的。</p>\",\n" +
                    "\"createTime\": 1512035475,\n" +
                    "\"content\": \"<p>3、What color does Mike like now?(<blk mlen=\\\"1\\\"></blk>)</p>\",\n" +
                    "\"score\": 20,\n" +
                    "\"role\": 2,\n" +
                    "\"answer\": \"<p>D</p>\",\n" +
                    "\"type\": 101\n" +
                    "},\n" +
                    "{\n" +
                    "\"status\": 0,\n" +
                    "\"updateTime\": 1512035475,\n" +
                    "\"version\": 589,\n" +
                    "\"qid\": 176031552,\n" +
                    "\"material\": {\n" +
                    "\"content\": \"<p align=\\\"center\\\">A</p><p>Dear Lily,</p><p>When you go to Huaxing Store, could you please return（退）my new soccer ball shoes? They are black. I don't like the color. Please get another pair in blue. They're on sale and cost $40.The receipt（收据）is in the box with the shoes.</p><p>Thanks.</p><p align=\\\"right\\\">Mike</p>\",\n" +
                    "\"id\": 176027552\n" +
                    "},\n" +
                    "\"accessory\": [{\n" +
                    "\"type\": 101,\n" +
                    "\"options\": [\"$ 40.\",\n" +
                    "\"$ 60.\",\n" +
                    "\"$ 70.\",\n" +
                    "\"$ 30.\"]\n" +
                    "}],\n" +
                    "\"relation\": [176028552,\n" +
                    "176029552,\n" +
                    "176030552,\n" +
                    "176031552,\n" +
                    "176032552],\n" +
                    "\"correctAnswer\": [\"A\"],\n" +
                    "\"id\": 176031552,\n" +
                    "\"solution\": \"<p>由They're on sale and cost $40.一句可知鞋子的价钱。</p>\",\n" +
                    "\"createTime\": 1512035475,\n" +
                    "\"content\": \"<p>4、How much are the shoes?(<blk mlen=\\\"1\\\"></blk>)</p>\",\n" +
                    "\"score\": 20,\n" +
                    "\"role\": 2,\n" +
                    "\"answer\": \"<p>A</p>\",\n" +
                    "\"type\": 101\n" +
                    "},\n" +
                    "{\n" +
                    "\"status\": 0,\n" +
                    "\"updateTime\": 1512035475,\n" +
                    "\"version\": 589,\n" +
                    "\"qid\": 176032552,\n" +
                    "\"material\": {\n" +
                    "\"content\": \"<p align=\\\"center\\\">A</p><p>Dear Lily,</p><p>When you go to Huaxing Store, could you please return（退）my new soccer ball shoes? They are black. I don't like the color. Please get another pair in blue. They're on sale and cost $40.The receipt（收据）is in the box with the shoes.</p><p>Thanks.</p><p align=\\\"right\\\">Mike</p>\",\n" +
                    "\"id\": 176027552\n" +
                    "},\n" +
                    "\"accessory\": [{\n" +
                    "\"type\": 101,\n" +
                    "\"options\": [\"In the shoes.\",\n" +
                    "\"In the shoe box.\",\n" +
                    "\"Mike lost it.\",\n" +
                    "\"In Huaxing Store.\"]\n" +
                    "}],\n" +
                    "\"relation\": [176028552,\n" +
                    "176029552,\n" +
                    "176030552,\n" +
                    "176031552,\n" +
                    "176032552],\n" +
                    "\"correctAnswer\": [\"B\"],\n" +
                    "\"id\": 176032552,\n" +
                    "\"solution\": \"<p>由The receipt is in the box with the shoes. 可知答案。</p>\",\n" +
                    "\"createTime\": 1512035475,\n" +
                    "\"content\": \"<p>5、Where is the receipt?(<blk mlen=\\\"1\\\"></blk>)</p>\",\n" +
                    "\"score\": 20,\n" +
                    "\"role\": 2,\n" +
                    "\"answer\": \"<p>B</p>\",\n" +
                    "\"type\": 101\n" +
                    "},\n" +
                    "{\n" +
                    "\"status\": 0,\n" +
                    "\"updateTime\": 1512035483,\n" +
                    "\"qid\": 176033552,\n" +
                    "\"createTime\": 1512035481,\n" +
                    "\"content\": \"<p align=\\\"center\\\">B</p><p><img src=\\\"/resources/fta2/201755/439451055/298a13e01ed3bac31d0fac7240ddb2ed.jpg\\\" width=\\\"599\\\" height=\\\"346\\\"/></p>\",\n" +
                    "\"version\": 589,\n" +
                    "\"role\": 1,\n" +
                    "\"relation\": [176034552,\n" +
                    "176035552,\n" +
                    "176036552,\n" +
                    "176037552,\n" +
                    "176038552],\n" +
                    "\"type\": 0,\n" +
                    "\"id\": 176033552\n" +
                    "},\n" +
                    "{\n" +
                    "\"status\": 0,\n" +
                    "\"updateTime\": 1512035483,\n" +
                    "\"version\": 589,\n" +
                    "\"qid\": 176034552,\n" +
                    "\"material\": {\n" +
                    "\"content\": \"<p align=\\\"center\\\">B</p><p><img src=\\\"/resources/fta2/201755/439451055/298a13e01ed3bac31d0fac7240ddb2ed.jpg\\\" width=\\\"599\\\" height=\\\"346\\\"/></p>\",\n" +
                    "\"id\": 176033552\n" +
                    "},\n" +
                    "\"accessory\": [{\n" +
                    "\"type\": 101,\n" +
                    "\"options\": [\"Four.\",\n" +
                    "\"Five.\",\n" +
                    "\"Six.\",\n" +
                    "\"Seven.\"]\n" +
                    "}],\n" +
                    "\"relation\": [176034552,\n" +
                    "176035552,\n" +
                    "176036552,\n" +
                    "176037552,\n" +
                    "176038552],\n" +
                    "\"correctAnswer\": [\"C\"],\n" +
                    "\"id\": 176034552,\n" +
                    "\"solution\": \"<p>根据图片可知共有六个启事。</p>\",\n" +
                    "\"createTime\": 1512035481,\n" +
                    "\"content\": \"<p>6、How many（多少） bulletin board notices（启事） in the Lost and Found?(<blk mlen=\\\"1\\\"></blk>)</p>\",\n" +
                    "\"score\": 20,\n" +
                    "\"role\": 2,\n" +
                    "\"answer\": \"<p>C</p>\",\n" +
                    "\"type\": 101\n" +
                    "},\n" +
                    "{\n" +
                    "\"status\": 0,\n" +
                    "\"updateTime\": 1512035483,\n" +
                    "\"version\": 589,\n" +
                    "\"qid\": 176035552,\n" +
                    "\"material\": {\n" +
                    "\"content\": \"<p align=\\\"center\\\">B</p><p><img src=\\\"/resources/fta2/201755/439451055/298a13e01ed3bac31d0fac7240ddb2ed.jpg\\\" width=\\\"599\\\" height=\\\"346\\\"/></p>\",\n" +
                    "\"id\": 176033552\n" +
                    "},\n" +
                    "\"accessory\": [{\n" +
                    "\"type\": 101,\n" +
                    "\"options\": [\"487-2349\",\n" +
                    "\"498-2456\",\n" +
                    "\"412-9856\",\n" +
                    "\"476-5939\"]\n" +
                    "}],\n" +
                    "\"relation\": [176034552,\n" +
                    "176035552,\n" +
                    "176036552,\n" +
                    "176037552,\n" +
                    "176038552],\n" +
                    "\"correctAnswer\": [\"B\"],\n" +
                    "\"id\": 176035552,\n" +
                    "\"solution\": \"<p>根据第五个启事可知是Lily捡到的钥匙，故要打她的电话号码。</p>\",\n" +
                    "\"createTime\": 1512035481,\n" +
                    "\"content\": \"<p>7、If（如果） you lost your keys, you may call <blk mlen=\\\"1\\\" mstyle=\\\"underline\\\"></blk> to find them.</p>\",\n" +
                    "\"score\": 20,\n" +
                    "\"role\": 2,\n" +
                    "\"answer\": \"<p>B</p>\",\n" +
                    "\"type\": 101\n" +
                    "},\n" +
                    "{\n" +
                    "\"status\": 0,\n" +
                    "\"updateTime\": 1512035483,\n" +
                    "\"version\": 589,\n" +
                    "\"qid\": 176036552,\n" +
                    "\"material\": {\n" +
                    "\"content\": \"<p align=\\\"center\\\">B</p><p><img src=\\\"/resources/fta2/201755/439451055/298a13e01ed3bac31d0fac7240ddb2ed.jpg\\\" width=\\\"599\\\" height=\\\"346\\\"/></p>\",\n" +
                    "\"id\": 176033552\n" +
                    "},\n" +
                    "\"accessory\": [{\n" +
                    "\"type\": 101,\n" +
                    "\"options\": [\"Mike's\",\n" +
                    "\"Lisa's\",\n" +
                    "\"Tom's\",\n" +
                    "\"John's\"]\n" +
                    "}],\n" +
                    "\"relation\": [176034552,\n" +
                    "176035552,\n" +
                    "176036552,\n" +
                    "176037552,\n" +
                    "176038552],\n" +
                    "\"correctAnswer\": [\"C\"],\n" +
                    "\"id\": 176036552,\n" +
                    "\"solution\": \"<p>根据第三个启事可知Tom丢了文具盒，故答案选C。</p>\",\n" +
                    "\"createTime\": 1512035482,\n" +
                    "\"content\": \"<p>8、If you found a pencil box, it may be（可能是）<blk mlen=\\\"1\\\" mstyle=\\\"underline\\\"></blk>.</p>\",\n" +
                    "\"score\": 20,\n" +
                    "\"role\": 2,\n" +
                    "\"answer\": \"<p>C</p>\",\n" +
                    "\"type\": 101\n" +
                    "},\n" +
                    "{\n" +
                    "\"status\": 0,\n" +
                    "\"updateTime\": 1512035483,\n" +
                    "\"version\": 589,\n" +
                    "\"qid\": 176037552,\n" +
                    "\"material\": {\n" +
                    "\"content\": \"<p align=\\\"center\\\">B</p><p><img src=\\\"/resources/fta2/201755/439451055/298a13e01ed3bac31d0fac7240ddb2ed.jpg\\\" width=\\\"599\\\" height=\\\"346\\\"/></p>\",\n" +
                    "\"id\": 176033552\n" +
                    "},\n" +
                    "\"accessory\": [{\n" +
                    "\"type\": 101,\n" +
                    "\"options\": [\"a blue and white cat\",\n" +
                    "\"a black schoolbag\",\n" +
                    "\"a black and white cat\",\n" +
                    "\"a blue and white book\"]\n" +
                    "}],\n" +
                    "\"relation\": [176034552,\n" +
                    "176035552,\n" +
                    "176036552,\n" +
                    "176037552,\n" +
                    "176038552],\n" +
                    "\"correctAnswer\": [\"C\"],\n" +
                    "\"id\": 176037552,\n" +
                    "\"solution\": \"<p>根据第六个启事可知Lisa捡到了一只黑白相间的猫，故答案选C。</p>\",\n" +
                    "\"createTime\": 1512035482,\n" +
                    "\"content\": \"<p>9、Lisa found <blk mlen=\\\"1\\\" mstyle=\\\"underline\\\"></blk> according to（根据）the Lost and Found.</p>\",\n" +
                    "\"score\": 20,\n" +
                    "\"role\": 2,\n" +
                    "\"answer\": \"<p>C</p>\",\n" +
                    "\"type\": 101\n" +
                    "},\n" +
                    "{\n" +
                    "\"status\": 0,\n" +
                    "\"updateTime\": 1512035483,\n" +
                    "\"version\": 589,\n" +
                    "\"qid\": 176038552,\n" +
                    "\"material\": {\n" +
                    "\"content\": \"<p align=\\\"center\\\">B</p><p><img src=\\\"/resources/fta2/201755/439451055/298a13e01ed3bac31d0fac7240ddb2ed.jpg\\\" width=\\\"599\\\" height=\\\"346\\\"/></p>\",\n" +
                    "\"id\": 176033552\n" +
                    "},\n" +
                    "\"accessory\": [{\n" +
                    "\"type\": 101,\n" +
                    "\"options\": [\"John.\",\n" +
                    "\"Mary.\",\n" +
                    "\"Lisa.\",\n" +
                    "\"Lily.\"]\n" +
                    "}],\n" +
                    "\"relation\": [176034552,\n" +
                    "176035552,\n" +
                    "176036552,\n" +
                    "176037552,\n" +
                    "176038552],\n" +
                    "\"correctAnswer\": [\"A\"],\n" +
                    "\"id\": 176038552,\n" +
                    "\"solution\": \"<p>根据第一个启事可知是John捡到的书，故答案选A。</p>\",\n" +
                    "\"createTime\": 1512035482,\n" +
                    "\"content\": \"<p>10、Who found a book?(<blk mlen=\\\"1\\\"></blk>)</p>\",\n" +
                    "\"score\": 20,\n" +
                    "\"role\": 2,\n" +
                    "\"answer\": \"<p>A</p>\",\n" +
                    "\"type\": 101\n" +
                    "},\n" +
                    "{\n" +
                    "\"status\": 0,\n" +
                    "\"updateTime\": 1512035487,\n" +
                    "\"qid\": 176039552,\n" +
                    "\"createTime\": 1512035487,\n" +
                    "\"content\": \"<p align=\\\"right\\\">（2017河北石家庄正定期中）</p><p align=\\\"center\\\">C</p><p>Dear Jane,</p><p>Thanks for the photo of your family. Here is a photo of my room.</p><p>A bed, a desk, a chair and a bookcase are in my room. My desk is next to my white bed and <u>it</u> is yellow. An English dictionary and some books are in the bookcase. My model plane and my hat are on the desk. My pencil box is on the desk, too. My pens and rulers are in the pencil box. My schoolbag is on the chair. My brother Jack's white baseball and my sister Mary's computer games are under my bed. You can see a kite（风筝）under the bed, too. It's mine.</p><p>Yours,</p><p align=\\\"right\\\">Dave</p>\",\n" +
                    "\"version\": 589,\n" +
                    "\"role\": 1,\n" +
                    "\"relation\": [176040552,\n" +
                    "176041552,\n" +
                    "176042552,\n" +
                    "176043552,\n" +
                    "176044552],\n" +
                    "\"type\": 0,\n" +
                    "\"id\": 176039552\n" +
                    "},\n" +
                    "{\n" +
                    "\"status\": 0,\n" +
                    "\"updateTime\": 1512035487,\n" +
                    "\"version\": 589,\n" +
                    "\"qid\": 176040552,\n" +
                    "\"material\": {\n" +
                    "\"content\": \"<p align=\\\"right\\\">（2017河北石家庄正定期中）</p><p align=\\\"center\\\">C</p><p>Dear Jane,</p><p>Thanks for the photo of your family. Here is a photo of my room.</p><p>A bed, a desk, a chair and a bookcase are in my room. My desk is next to my white bed and <u>it</u> is yellow. An English dictionary and some books are in the bookcase. My model plane and my hat are on the desk. My pencil box is on the desk, too. My pens and rulers are in the pencil box. My schoolbag is on the chair. My brother Jack's white baseball and my sister Mary's computer games are under my bed. You can see a kite（风筝）under the bed, too. It's mine.</p><p>Yours,</p><p align=\\\"right\\\">Dave</p>\",\n" +
                    "\"id\": 176039552\n" +
                    "},\n" +
                    "\"accessory\": [{\n" +
                    "\"type\": 101,\n" +
                    "\"options\": [\"Jane's family\",\n" +
                    "\"Dave's family\",\n" +
                    "\"Jane's room\",\n" +
                    "\"Dave's room\"]\n" +
                    "}],\n" +
                    "\"relation\": [176040552,\n" +
                    "176041552,\n" +
                    "176042552,\n" +
                    "176043552,\n" +
                    "176044552],\n" +
                    "\"correctAnswer\": [\"D\"],\n" +
                    "\"id\": 176040552,\n" +
                    "\"solution\": \"<p>这是Dave写给Jane的信，根据句中的“Here is a photo of my room.”可知答案选D。</p>\",\n" +
                    "\"createTime\": 1512035487,\n" +
                    "\"content\": \"<p>11、This is a photo of <blk mlen=\\\"1\\\" mstyle=\\\"underline\\\"></blk>.</p>\",\n" +
                    "\"score\": 20,\n" +
                    "\"role\": 2,\n" +
                    "\"answer\": \"<p>D</p>\",\n" +
                    "\"type\": 101\n" +
                    "},\n" +
                    "{\n" +
                    "\"status\": 0,\n" +
                    "\"updateTime\": 1512035487,\n" +
                    "\"version\": 589,\n" +
                    "\"qid\": 176041552,\n" +
                    "\"material\": {\n" +
                    "\"content\": \"<p align=\\\"right\\\">（2017河北石家庄正定期中）</p><p align=\\\"center\\\">C</p><p>Dear Jane,</p><p>Thanks for the photo of your family. Here is a photo of my room.</p><p>A bed, a desk, a chair and a bookcase are in my room. My desk is next to my white bed and <u>it</u> is yellow. An English dictionary and some books are in the bookcase. My model plane and my hat are on the desk. My pencil box is on the desk, too. My pens and rulers are in the pencil box. My schoolbag is on the chair. My brother Jack's white baseball and my sister Mary's computer games are under my bed. You can see a kite（风筝）under the bed, too. It's mine.</p><p>Yours,</p><p align=\\\"right\\\">Dave</p>\",\n" +
                    "\"id\": 176039552\n" +
                    "},\n" +
                    "\"accessory\": [{\n" +
                    "\"type\": 101,\n" +
                    "\"options\": [\"the bed\",\n" +
                    "\"the desk\",\n" +
                    "\"the bookcase\",\n" +
                    "\"the photo\"]\n" +
                    "}],\n" +
                    "\"relation\": [176040552,\n" +
                    "176041552,\n" +
                    "176042552,\n" +
                    "176043552,\n" +
                    "176044552],\n" +
                    "\"correctAnswer\": [\"B\"],\n" +
                    "\"id\": 176041552,\n" +
                    "\"solution\": \"<p>此句句意：我的书桌紧挨着我的白色的床，它是黄色的。故这里的it指的是书桌，故答案选B。</p>\",\n" +
                    "\"createTime\": 1512035487,\n" +
                    "\"content\": \"<p>12、文中画线单词“it”指代的是<blk mlen=\\\"1\\\" mstyle=\\\"underline\\\"></blk>。</p>\",\n" +
                    "\"score\": 20,\n" +
                    "\"role\": 2,\n" +
                    "\"answer\": \"<p>B</p>\",\n" +
                    "\"type\": 101\n" +
                    "},\n" +
                    "{\n" +
                    "\"status\": 0,\n" +
                    "\"updateTime\": 1512035487,\n" +
                    "\"version\": 589,\n" +
                    "\"qid\": 176042552,\n" +
                    "\"material\": {\n" +
                    "\"content\": \"<p align=\\\"right\\\">（2017河北石家庄正定期中）</p><p align=\\\"center\\\">C</p><p>Dear Jane,</p><p>Thanks for the photo of your family. Here is a photo of my room.</p><p>A bed, a desk, a chair and a bookcase are in my room. My desk is next to my white bed and <u>it</u> is yellow. An English dictionary and some books are in the bookcase. My model plane and my hat are on the desk. My pencil box is on the desk, too. My pens and rulers are in the pencil box. My schoolbag is on the chair. My brother Jack's white baseball and my sister Mary's computer games are under my bed. You can see a kite（风筝）under the bed, too. It's mine.</p><p>Yours,</p><p align=\\\"right\\\">Dave</p>\",\n" +
                    "\"id\": 176039552\n" +
                    "},\n" +
                    "\"accessory\": [{\n" +
                    "\"type\": 101,\n" +
                    "\"options\": [\"On the desk.\",\n" +
                    "\"In the bookcase.\",\n" +
                    "\"On the sofa.\",\n" +
                    "\"Under the bed.\"]\n" +
                    "}],\n" +
                    "\"relation\": [176040552,\n" +
                    "176041552,\n" +
                    "176042552,\n" +
                    "176043552,\n" +
                    "176044552],\n" +
                    "\"correctAnswer\": [\"A\"],\n" +
                    "\"id\": 176042552,\n" +
                    "\"solution\": \"<p>由句子“My model plane and my hat are on the desk.”可知他的飞机模型在书桌上。</p>\",\n" +
                    "\"createTime\": 1512035487,\n" +
                    "\"content\": \"<p>13、Where is Dave's model plane?(<blk mlen=\\\"1\\\"></blk>)</p>\",\n" +
                    "\"score\": 20,\n" +
                    "\"role\": 2,\n" +
                    "\"answer\": \"<p>A</p>\",\n" +
                    "\"type\": 101\n" +
                    "},\n" +
                    "{\n" +
                    "\"status\": 0,\n" +
                    "\"updateTime\": 1512035487,\n" +
                    "\"version\": 589,\n" +
                    "\"qid\": 176043552,\n" +
                    "\"material\": {\n" +
                    "\"content\": \"<p align=\\\"right\\\">（2017河北石家庄正定期中）</p><p align=\\\"center\\\">C</p><p>Dear Jane,</p><p>Thanks for the photo of your family. Here is a photo of my room.</p><p>A bed, a desk, a chair and a bookcase are in my room. My desk is next to my white bed and <u>it</u> is yellow. An English dictionary and some books are in the bookcase. My model plane and my hat are on the desk. My pencil box is on the desk, too. My pens and rulers are in the pencil box. My schoolbag is on the chair. My brother Jack's white baseball and my sister Mary's computer games are under my bed. You can see a kite（风筝）under the bed, too. It's mine.</p><p>Yours,</p><p align=\\\"right\\\">Dave</p>\",\n" +
                    "\"id\": 176039552\n" +
                    "},\n" +
                    "\"accessory\": [{\n" +
                    "\"type\": 101,\n" +
                    "\"options\": [\"It's red.\",\n" +
                    "\"It's black.\",\n" +
                    "\"It's white.\",\n" +
                    "\"It's green.\"]\n" +
                    "}],\n" +
                    "\"relation\": [176040552,\n" +
                    "176041552,\n" +
                    "176042552,\n" +
                    "176043552,\n" +
                    "176044552],\n" +
                    "\"correctAnswer\": [\"C\"],\n" +
                    "\"id\": 176043552,\n" +
                    "\"solution\": \"<p>由句子“My brother Jack's white baseball and my sister...”可知杰克的棒球是白色的。</p>\",\n" +
                    "\"createTime\": 1512035487,\n" +
                    "\"content\": \"<p>14、What color is Jack's baseball?(<blk mlen=\\\"1\\\"></blk>)</p>\",\n" +
                    "\"score\": 20,\n" +
                    "\"role\": 2,\n" +
                    "\"answer\": \"<p>C</p>\",\n" +
                    "\"type\": 101\n" +
                    "},\n" +
                    "{\n" +
                    "\"status\": 0,\n" +
                    "\"updateTime\": 1512035487,\n" +
                    "\"version\": 589,\n" +
                    "\"qid\": 176044552,\n" +
                    "\"material\": {\n" +
                    "\"content\": \"<p align=\\\"right\\\">（2017河北石家庄正定期中）</p><p align=\\\"center\\\">C</p><p>Dear Jane,</p><p>Thanks for the photo of your family. Here is a photo of my room.</p><p>A bed, a desk, a chair and a bookcase are in my room. My desk is next to my white bed and <u>it</u> is yellow. An English dictionary and some books are in the bookcase. My model plane and my hat are on the desk. My pencil box is on the desk, too. My pens and rulers are in the pencil box. My schoolbag is on the chair. My brother Jack's white baseball and my sister Mary's computer games are under my bed. You can see a kite（风筝）under the bed, too. It's mine.</p><p>Yours,</p><p align=\\\"right\\\">Dave</p>\",\n" +
                    "\"id\": 176039552\n" +
                    "},\n" +
                    "\"accessory\": [{\n" +
                    "\"type\": 101,\n" +
                    "\"options\": [\"Dave has a brother and two sisters.\",\n" +
                    "\"Jane has a photo of Dave's family.\",\n" +
                    "\"An English dictionary is in the bookcase.\",\n" +
                    "\"The computer games are Jack's.\"]\n" +
                    "}],\n" +
                    "\"relation\": [176040552,\n" +
                    "176041552,\n" +
                    "176042552,\n" +
                    "176043552,\n" +
                    "176044552],\n" +
                    "\"correctAnswer\": [\"C\"],\n" +
                    "\"id\": 176044552,\n" +
                    "\"solution\": \"<p>由句子“An English dictionary and some books are in the bookcase.”可知C项符合文意，其余三项的说法与原文不符。</p>\",\n" +
                    "\"createTime\": 1512035487,\n" +
                    "\"content\": \"<p>15、下列哪项陈述是正确的？(<blk mlen=\\\"1\\\"></blk>)</p>\",\n" +
                    "\"score\": 20,\n" +
                    "\"role\": 2,\n" +
                    "\"answer\": \"<p>C</p>\",\n" +
                    "\"type\": 101\n" +
                    "}\n" +
                    "]");
            infoList = ErrorQuestionDB.questionsFromJSONArray(jsonArray);
            for (int i = 0; i < infoList.size(); i++) {
                QuestionInfo info = infoList.get(i);
                TinyQuestionInfo tInfo = new TinyQuestionInfo(info.getId(), info.getType(), info.getRole(), info.getOrgInfo());
                eqList.add(tInfo);
            }

            jsonArray = new JSONArray("[\n" +
                    "{\n" +
                    "\"status\": 0,\n" +
                    "\"updateTime\": 1512035605,\n" +
                    "\"version\": 589,\n" +
                    "\"no\": \"1、\",\n" +
                    "\"qid\": 176055552,\n" +
                    "\"createTime\": 1512035605,\n" +
                    "\"content\": \"<p>He has a photo of Chen Xiao. （改为否定句）</p><p>He <blk mlen=\\\"7\\\" mstyle=\\\"underline\\\"></blk> <blk mlen=\\\"4\\\" mstyle=\\\"underline\\\"></blk> a photo of Chen Xiao.</p>\",\n" +
                    "\"score\": 20,\n" +
                    "\"role\": 0,\n" +
                    "\"answer\": \"<p>doesn't have</p>\",\n" +
                    "\"type\": 202,\n" +
                    "\"id\": 176055552\n" +
                    "},\n" +
                    "{\n" +
                    "\"status\": 0,\n" +
                    "\"updateTime\": 1512035605,\n" +
                    "\"version\": 589,\n" +
                    "\"no\": \"2、\",\n" +
                    "\"qid\": 176056552,\n" +
                    "\"createTime\": 1512035605,\n" +
                    "\"content\": \"<p>He is Li Yifeng. （改为一般疑问句）</p><p><blk mlen=\\\"2\\\" mstyle=\\\"underline\\\"></blk> <blk mlen=\\\"2\\\" mstyle=\\\"underline\\\"></blk> Li Yifeng?</p>\",\n" +
                    "\"score\": 20,\n" +
                    "\"role\": 0,\n" +
                    "\"answer\": \"<p>Is he</p>\",\n" +
                    "\"type\": 202,\n" +
                    "\"id\": 176056552\n" +
                    "},\n" +
                    "{\n" +
                    "\"status\": 0,\n" +
                    "\"updateTime\": 1512035605,\n" +
                    "\"version\": 589,\n" +
                    "\"no\": \"3、\",\n" +
                    "\"qid\": 176057552,\n" +
                    "\"createTime\": 1512035605,\n" +
                    "\"content\": \"<p>My book is <u>in the desk</u>. （对画线部分提问）</p><p><blk mlen=\\\"7\\\" mstyle=\\\"underline\\\"></blk> <blk mlen=\\\"4\\\" mstyle=\\\"underline\\\"></blk> book?</p>\",\n" +
                    "\"score\": 20,\n" +
                    "\"role\": 0,\n" +
                    "\"answer\": \"<p>Where's your</p>\",\n" +
                    "\"type\": 202,\n" +
                    "\"id\": 176057552\n" +
                    "},\n" +
                    "{\n" +
                    "\"status\": 0,\n" +
                    "\"updateTime\": 1512035605,\n" +
                    "\"version\": 589,\n" +
                    "\"no\": \"4、\",\n" +
                    "\"qid\": 176058552,\n" +
                    "\"createTime\": 1512035605,\n" +
                    "\"content\": \"<p>These are some boxes.（改为单数句）</p><p><blk mlen=\\\"4\\\" mstyle=\\\"underline\\\"></blk> <blk mlen=\\\"2\\\" mstyle=\\\"underline\\\"></blk> a box.</p>\",\n" +
                    "\"score\": 20,\n" +
                    "\"role\": 0,\n" +
                    "\"answer\": \"<p>This is</p>\",\n" +
                    "\"type\": 202,\n" +
                    "\"id\": 176058552\n" +
                    "},\n" +
                    "{\n" +
                    "\"status\": 0,\n" +
                    "\"updateTime\": 1512035605,\n" +
                    "\"version\": 589,\n" +
                    "\"no\": \"5、\",\n" +
                    "\"qid\": 176059552,\n" +
                    "\"createTime\": 1512035605,\n" +
                    "\"content\": \"<p>That is her schoolbag. （改为同义句）</p><p><blk mlen=\\\"4\\\" mstyle=\\\"underline\\\"></blk> schoolbag is <blk mlen=\\\"4\\\" mstyle=\\\"underline\\\"></blk>.</p>\",\n" +
                    "\"score\": 20,\n" +
                    "\"role\": 0,\n" +
                    "\"answer\": \"<p>That；hers</p>\",\n" +
                    "\"type\": 202,\n" +
                    "\"id\": 176059552\n" +
                    "}\n" +
                    "]");
            infoList = ErrorQuestionDB.questionsFromJSONArray(jsonArray);
            for (int i = 0; i < infoList.size(); i++) {
                QuestionInfo info = infoList.get(i);
                TinyQuestionInfo tInfo = new TinyQuestionInfo(info.getId(), info.getType(), info.getRole(), info.getOrgInfo());
                eqList.add(tInfo);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
