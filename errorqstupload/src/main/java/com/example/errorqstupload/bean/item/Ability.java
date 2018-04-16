package com.example.errorqstupload.bean.item;

import com.example.errorqstupload.utils.MyJson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by guh on 2017/8/1.
 */

public class Ability {

    public static final String CONFIG_PATH = "config.json";

    public static final String KEY_CH_ACCUMULATE_MEMORY = "101";     //积累记忆
    public static final String KEY_CH_LANGUAGE_UNDERSTANDING = "102";//语言理解
    public static final String KEY_CH_LOGIC_ANALYSIS = "103";        //逻辑分析
    public static final String KEY_CH_EXPRESSION_APPLY = "104";      //表达应用
    public static final String KEY_CH_APPRECIATE_EVALUATION = "105"; //鉴赏评价
    public static final String KEY_CH_EMOTIONAL_ATTITUDE = "106";    //情感态度

    public static final String KEY_MA_KNOW_UNDERSTAND = "201";        //认识与理解
    public static final String KEY_MA_OPERATION = "202";              //运算
    public static final String KEY_MA_SPATIAL_IMGGINATION = "203";    //空间想象
    public static final String KEY_MA_LOGICAL_THINKING = "204";       //逻辑思维
    public static final String KEY_MA_APPLY = "205";                  //应用

    public static final String KEY_EN_ACCUMULATE_MEMORY = "301";   //积累记忆
    public static final String KEY_EN_UNDERSTAND_ANALYSIS = "302"; //理解分析
    public static final String KEY_EN_SOCIAL_COGNITION = "303";    //社会认知
    public static final String KEY_EN_EXPRESSION_APPLY = "304";    //表达应用

    public static final String ABILITY_KEY[][] = {
            {KEY_CH_ACCUMULATE_MEMORY, KEY_CH_LANGUAGE_UNDERSTANDING, KEY_CH_LOGIC_ANALYSIS,
            KEY_CH_EXPRESSION_APPLY, KEY_CH_APPRECIATE_EVALUATION, KEY_CH_EMOTIONAL_ATTITUDE},
            {KEY_MA_KNOW_UNDERSTAND, KEY_MA_OPERATION, KEY_MA_SPATIAL_IMGGINATION,
            KEY_MA_LOGICAL_THINKING, KEY_MA_APPLY},
            {KEY_EN_ACCUMULATE_MEMORY, KEY_EN_UNDERSTAND_ANALYSIS,
            KEY_EN_SOCIAL_COGNITION, KEY_EN_EXPRESSION_APPLY}
    };
    public static final String ABILITY_NAME[][] = {
            {"积累记忆", "语言理解", "逻辑分析", "表达应用", "鉴赏评价", "情感态度"},
            {"认识与理解", "运算", "空间想象", "逻辑思维", "应用"},
            {"积累记忆", "理解分析", "社会认知", "表达应用"}
    };
    // 积累记忆 语言理解 逻辑分析 表达应用 鉴赏评价 情感态度
    // 认识与理解 运算 空间想象 逻辑思维 应用
    // 积累记忆 理解分析 社会认知 表达应用


    private int mId = 0;
    private String mName = null;
    private float mTotal = 0;
    private float mRight = 0;

    public Ability(int id, String name) {
        mId = id;
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public int getId() {
        return mId;
    }

    public float getRight() {
        return mRight;
    }

    public float getTotal() {
        return mTotal;
    }

    public void setRight(float right) {
        this.mRight = right;
    }

    public void setTotal(float total) {
        this.mTotal = total;
    }

    public JSONObject getJsonObject() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("a_total", mTotal);
        obj.put("a_right", mRight);
        return obj;
    }

    public static int getId(String key) {
        int id = Integer.valueOf(key);
        return id;
    }

    public static String[] getAbilityKeys(String configpath, int subject) {
        ArrayList<Ability> abs = MyJson.getAbilitys(configpath, subject);
        String[] back = null;
        if (abs.size() > 0) {
            back = new String[abs.size()];
            for (int i=0; i<back.length; i++) {
                back[i] = String.valueOf(abs.get(i).getId());
            }
        } else {
            back = Ability.ABILITY_KEY[subject-1];
        }
        return back;
    }

    public static String[] getAbilityNames(String configpath, int subject) {
        ArrayList<Ability> abs = MyJson.getAbilitys(configpath, subject);
        String[] back = null;
        if (abs.size() > 0) {
            back = new String[abs.size()];
            for (int i=0; i<back.length; i++) {
//                Log.i("", "subject: "+subject+", abs.get(i).getName(): "+abs.get(i).getName());
                back[i] = abs.get(i).getName();
            }
        } else {
            back = Ability.ABILITY_NAME[subject-1];
        }
        return back;
    }

    public static String getAbilityValue(String configpath, String key) {
        String back = "";
        int subject = Integer.valueOf(key)/100;
        if (subject > 0) {

            ArrayList<Ability> abs = MyJson.getAbilitys(configpath, subject);
            if (abs.size() >0) {
                for (Ability a: abs) {
                    if (key.equals(String.valueOf(a.getId()))) {
                        back = a.getName();
                        break;
                    }
                }
            }
            if (back.equals("")) {
                int subjectindx = subject - 1;
                for (int j = 0; j < ABILITY_KEY[subjectindx].length; j++) {
                    if (ABILITY_KEY[subjectindx][j].equals(key)) {
                        back = ABILITY_NAME[subjectindx][j];
                        break;
                    }
                }
            }
        }

        return back;
    }
}
