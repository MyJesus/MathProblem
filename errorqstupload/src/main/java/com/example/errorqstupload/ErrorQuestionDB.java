package com.example.errorqstupload;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.text.TextUtils;
import android.util.SparseArray;

import com.example.errorqstupload.bean.QuestionInfo;
import com.example.errorqstupload.bean.TinyQuestionInfo;
import com.example.errorqstupload.bean.item.Ability;
import com.example.errorqstupload.bean.item.Accessory;
import com.example.errorqstupload.bean.item.KeyPoint;
import com.example.errorqstupload.bean.item.Section;
import com.example.errorqstupload.utils.TestData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Sion's on 2017/11/21.
 */

public class ErrorQuestionDB {
    public static final String TABLE_KYB = "kyb"; //口语宝
    public static final String TABLE_ZBKT = "zbkt"; //直播课堂
    public static final String TABLE_AS = "mathas"; //奥数
    public static final String TABLE_PC = "intelligencetesting"; //智能测评
    public static final String TABLE_YYTZD = "yytzd"; //应用题指导
    public static final String TABLE_XXY = "studyEye"; //学习眼
    public static final String TABLE_XXXT = "studySystem"; //学习系统
    public static final String TABLE_ZZDWKT = "zzdwkt"; //知识点微课堂
    public static final String TABLE_SXJCQJ = "sxjcqj"; //数学教材全解
    public static final String TABLE_YYJCQJ = "yyjcqj"; //英语教材全解
    public static final String TABLE_YWJCQJ = "ywjcqj"; //语文教材全解
    public static final String TABLE_FIVETHREE = "fivethree"; //五三

    public static final String DIR = "ErrorCollection";
    public static final String DB_NAME = "ErrorQuestions";
    private static final String AUTHORITY = "com.error.questionprovider";
    private static final String TABLE_NAME = TABLE_YYTZD; //当前的表名

    //表的列名
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DATA = "data";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_COUNT = "count";
    public static final String COLUMN_UPDATE_COUNT = "uc";

    //支持的试题role = 0, type
    public static int[] ROLE_ZERO_TYPE = new int[]{
            101,
            102,
            103,
            201,
            202,
            203,
            301,
            302
    };

    public static int[] ROLE_ONE_TYPE = new int[]{
            0,
            104,
            105,
            106
    };

    //URI
    public static final Uri QUESES_CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);

    public static boolean isNeedUpdateError = true;
    public static String DATA_FORMAT = "yyyy-MM-dd HH:mm:00";

    private static ErrorQuestionDB errorQuestionDB;

    private ErrorQuestionDB() {
        super();
    }

    public static ErrorQuestionDB getInstance() {
        if (null == errorQuestionDB) {
            errorQuestionDB = new ErrorQuestionDB();
        }
        return errorQuestionDB;
    }

    /**
     * 上传到错题本
     *
     * @param eqList
     */
    public static void updateErrorCollection(ContentResolver cr, List<TinyQuestionInfo> eqList) {
        if (null == eqList || eqList.size() == 0) {
            return;
        }
//        TestData.creaTestInfo(eqList);
        //移除不支持的题目
        Iterator<TinyQuestionInfo> it = eqList.iterator();
        while (it.hasNext()) {
            TinyQuestionInfo tInfo = it.next();
            if (tInfo == null) {
                it.remove();
                continue;
            }
            int type = tInfo.type;
            int role = tInfo.role;
            if (role != 0 && role != 2) {
                if (role == 1) {
                    boolean isOk = false;
                    for (int i = 0; i < ROLE_ONE_TYPE.length; i++) {
                        if (type == ROLE_ONE_TYPE[i]) {
                            isOk = true;
                            //当前为大题
                            break;
                        }
                    }
                    if (!isOk) {
                        it.remove();
                    }
                } else {
                    it.remove();
                }
                continue;
            } else {
                boolean isOk = false;
                for (int i = 0; i < ROLE_ZERO_TYPE.length; i++) {
                    if (type == ROLE_ZERO_TYPE[i]) {
                        isOk = true;
                        break;
                    }
                }
                if (!isOk) {
                    it.remove();
                }
            }
        }

        List<Integer> removeIdList = new ArrayList<>();
        List<Integer> reCheckIdList = new ArrayList<>();

        //转换成questionInfo的temp数组
        SparseArray<QuestionInfo> tempInfoArray = new SparseArray<>();
        for (int i = 0; i < eqList.size(); i++) {
            TinyQuestionInfo tInfo = eqList.get(i);
            try {
                QuestionInfo resInfo = questionFromJson(new JSONObject(tInfo.orgInfo));
                tempInfoArray.put(tInfo.id, resInfo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //遍历eqList，若为大题则组合
        it = eqList.iterator();
        while (it.hasNext()) {
            TinyQuestionInfo info = it.next();
            int id = info.id;
            int type = info.type;
            int role = info.role;
            if (role == 1) {
                reCheckIdList.add(id);
                for (int i = 0; i < ROLE_ONE_TYPE.length; i++) {
                    if (type == ROLE_ONE_TYPE[i]) {
                        JSONArray newArray = new JSONArray();
                        try {
                            newArray.put(new JSONObject(info.orgInfo));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //大题则找出relate并组合
                        if (tempInfoArray.indexOfKey(id) >= 0) {
                            QuestionInfo bInfo = tempInfoArray.get(id);
                            ArrayList<Integer> relateList = bInfo.getmRelation();
                            //遍历relateList
                            for (int j = 0; j < relateList.size(); j++) {
                                int rId = relateList.get(j);
                                if (rId == id || reCheckIdList.contains(rId))
                                    continue;
                                if (tempInfoArray.indexOfKey(rId) >= 0) {
                                    QuestionInfo rInfo = tempInfoArray.get(rId);
                                    String rOrgInfo = rInfo.getOrgInfo();
                                    if (!TextUtils.isEmpty(rOrgInfo)) {
                                        try {
                                            JSONObject rJObj = new JSONObject(rOrgInfo);
                                            newArray.put(rJObj);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    removeIdList.add(rId);
                                }
                            }
                            //JSONArray加入到orgStr
                            info.orgInfo = newArray.toString();
                            info.isArray = true;
                        }
                        break;
                    }
                }
            }
        }

        //移除多余的info
        it = eqList.iterator();
        while (it.hasNext()) {
            if (removeIdList.contains(it.next().id)) {
                it.remove();
            }
        }

        if (isNeedUpdateError) {
            //同一批次的重复ID题目不上传
            try {
                ContentValues[] cvs = new ContentValues[eqList.size()];
                long curTime = System.currentTimeMillis();
                String deleteMillis = longToString(curTime, DATA_FORMAT);
                curTime = stringToLong(deleteMillis, DATA_FORMAT);

                for (int i = 0; i < eqList.size(); i++) {
                    TinyQuestionInfo info = eqList.get(i);
                    if (info == null || TextUtils.isEmpty(info.orgInfo))
                        throw new Exception();
                    ContentValues cv = new ContentValues();
                    cv.put(COLUMN_ID, info.id);
                    cv.put(COLUMN_DATA, info.orgInfo);
                    cv.put(COLUMN_TIME, curTime);
                    cvs[i] = cv;
                }
                cr.bulkInsert(QUESES_CONTENT_URI, cvs);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //时间转换
    public static long stringToLong(String strTime, String formatType)
            throws ParseException {
        Date date = stringToDate(strTime, formatType); // String类型转成date类型
        if (date == null) {
            return 0;
        } else {
            long currentTime = dateToLong(date); // date类型转成long类型
            return currentTime;
        }
    }

    public static Date stringToDate(String strTime, String formatType)
            throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
        date = formatter.parse(strTime);
        return date;
    }

    public static long dateToLong(Date date) {
        return date.getTime();
    }

    public static String longToString(long currentTime, String formatType)
            throws ParseException {
        Date date = longToDate(currentTime, formatType); // long类型转成Date类型
        String strTime = dateToString(date, formatType); // date类型转成String
        return strTime;
    }

    public static String dateToString(Date data, String formatType) {
        return new SimpleDateFormat(formatType).format(data);
    }

    public static Date longToDate(long currentTime, String formatType)
            throws ParseException {
        Date dateOld = new Date(currentTime); // 根据long类型的毫秒数生命一个date类型的时间
        String sDateTime = dateToString(dateOld, formatType); // 把date类型的时间转换为string
        Date date = stringToDate(sDateTime, formatType); // 把String类型转换为Date类型
        return date;
    }

    //json转换
    public static ArrayList<QuestionInfo> questionsFromJSONArray(JSONArray jsonarr) {
        ArrayList<QuestionInfo> qstLst = new ArrayList<>();
        if (jsonarr != null && jsonarr.length() > 0) {
            for (int i = 0; i < jsonarr.length(); i++) {
                JSONObject testobj = jsonarr.optJSONObject(i);
                try {
                    qstLst.add(questionFromJson(testobj));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return qstLst;
    }

    public static QuestionInfo questionFromJson(JSONObject testobj) throws JSONException {
        if (null == testobj)
            return null;
        QuestionInfo qst = new QuestionInfo();
        qst.setOrgInfo(testobj.toString());
        String content = testobj.optString("content", null);

        qst.setContent(content);
        qst.setRole(testobj.optInt("role", 0));
        qst.setCategory(testobj.optInt("category", 0));
        qst.setStatus(testobj.optInt("status", 0));
        qst.setFrom(testobj.optInt("from", 0));
        qst.setDifficulty(testobj.optInt("difficulty", 0));
        qst.setCourseId(testobj.optInt("courseId", 0));
        qst.setId(testobj.optInt("id", 0));
        qst.setType(testobj.optInt("type", 0));
        qst.setSolution(testobj.optString("solution", ""));
        qst.setAnswer(testobj.optString("answer", ""));
        qst.setOrgInfo(testobj.toString());

        JSONArray abilityarr = testobj.optJSONArray("ability");
        if (abilityarr != null) {
            for (int j = 0; j < abilityarr.length(); j++) {
                JSONObject abilityobj = abilityarr.optJSONObject(j);
                Ability ability = new Ability(abilityobj.optInt("id", 0), abilityobj.optString("name", null));
                qst.addAbility(ability);
            }
        }

        JSONArray sectionarr = testobj.optJSONArray("section");
        if (sectionarr != null) {
            for (int j = 0; j < sectionarr.length(); j++) {
                JSONObject sectionobj = sectionarr.optJSONObject(j);
                Section section = new Section(sectionobj.optInt("id", 0), sectionobj.optInt("source", 0), sectionobj.optString("name", null));
                qst.addSection(section);
            }
        }

        JSONArray keypointarr = testobj.optJSONArray("keypoint");
        if (keypointarr != null) {
            for (int j = 0; j < keypointarr.length(); j++) {
                JSONObject keypointobj = keypointarr.optJSONObject(j);
                KeyPoint keypoint = new KeyPoint(keypointobj.optInt("id", 0), keypointobj.optString("name", null));
                qst.addKeyPoint(keypoint);
            }
        }

        JSONArray rightAnsarr = testobj.optJSONArray("correctAnswer");
        if (rightAnsarr != null) {
            for (int j = 0; j < rightAnsarr.length(); j++) {
                qst.addCorrectAnswer(rightAnsarr.optString(j, null));
            }
        }

        JSONArray relatearr = testobj.optJSONArray("relation");
        if (relatearr != null) {
            for (int j = 0; j < relatearr.length(); j++) {
                qst.getmRelation().add((Integer) relatearr.get(j));
            }
        }

        JSONArray accessoryarr = testobj.optJSONArray("accessory");
        if (accessoryarr != null) {
            for (int j = 0; j < accessoryarr.length(); j++) {
                JSONObject accobj = accessoryarr.optJSONObject(j);
                Accessory acc = new Accessory();
                acc.setType(accobj.optInt("type", 0));
                acc.setMode(accobj.optInt("mode", 0));
                acc.setContent(accobj.optString("content", null));
                acc.setLabel(accobj.optString("label", null));
                acc.setTranslation(accobj.optString("translation", null));

                JSONArray options = accobj.optJSONArray("options");
                for (int k = 0; k < options.length(); k++) {
                    acc.addOption(options.optString(k, null));
                }

                JSONArray speechs = accobj.optJSONArray("speech");
                if (speechs != null) {
                    for (int k = 0; k < speechs.length(); k++) {
                        acc.addSpeech(speechs.optString(k, null));
                    }
                }

                JSONArray words = accobj.optJSONArray("words");
                if (words != null) {
                    for (int k = 0; k < words.length(); k++) {
                        acc.addWord(words.optString(k, null));
                    }
                }

                JSONArray transwords = accobj.optJSONArray("transWords");
                if (transwords != null) {
                    for (int k = 0; k < transwords.length(); k++) {
                        acc.addTransWord(transwords.optString(k, null));
                    }
                }

                qst.addAccessory(acc);
            }
        }
        return qst;
    }
}
