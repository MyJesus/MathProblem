package com.example.errorqstupload.utils;

import android.os.Environment;

import com.example.errorqstupload.bean.item.Ability;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by guh on 2017/8/1.
 */

public class MyJson {

    public static ArrayList<Ability> getAbilitys(String configpath, int subject) {
        ArrayList<Ability> ablst = new ArrayList<>();
        JSONObject jsobj = FileStream.getJsonDataFromKeyUtf8(configpath);
        if (jsobj != null) {
            JSONObject jsobjdb = jsobj.optJSONObject("data");
            if (jsobjdb != null) {
                JSONArray abconfig = jsobjdb.optJSONArray("ability");
                if (abconfig != null && abconfig.length() > 0) {
                    for (int i = 0; i < abconfig.length(); i++) {
                        JSONObject item = abconfig.optJSONObject(i);
                        if (item != null) {
                            if (subject == item.optInt("subject")) {
                                Ability ab = new Ability(item.optInt("id"), item.optString("name"));
                                ablst.add(ab);
                            }
                        }
                    }
                }
            }
        }
        return ablst;
    }

    public static ArrayList<Ability> readAverageAbilitys(String jsonpath) {
        ArrayList<Ability> abilityLst = new ArrayList<>();
        JSONObject jsobj = FileStream.getJsonDataFromKeyUtf8(jsonpath);

        if (jsobj != null) {
            JSONObject jsobjdb = jsobj.optJSONObject("data");
            if (jsobjdb != null) {
                JSONObject avgobj = jsobjdb.optJSONObject("avg");
                if (avgobj != null) {
                    JSONObject objabi = avgobj.optJSONObject("ability");
                    if (objabi != null) {
                        abilityLst = getAbilitys(objabi, avgobj.optInt("subject", 1));
                    }
                }
            }
        }

        return abilityLst;
    }

    /*
     * 获取评测的题目
     * @return
     */
    public static int getAppUserTimes(JSONObject jsobj) {
        int userTimes = 0;
        if (jsobj != null) {
            JSONObject jsobjdb = jsobj.optJSONObject("data");
            if (jsobjdb != null) {
                userTimes = jsobjdb.optInt("times");
            }
        }

        return userTimes;
    }

    private static ArrayList<Ability> getAbilitys(JSONObject obj, int subject) {
        ArrayList<Ability> ablst = new ArrayList<>();
//        Log.e("MyJson", "-- getAbilitys obj: "+obj);
        if (obj != null) {
            if (subject > 0) {
                String configpath = MyJson.getConfigPath(Ability.CONFIG_PATH);
                String abiKey[] = Ability.getAbilityKeys(configpath, subject);
                for (int j = 0; j < abiKey.length; j++) {
                    JSONObject item = obj.optJSONObject(abiKey[j]);
                    if (item != null) {
                        Ability ability = getAbilityFromJsonObject(item, Ability.getId(abiKey[j]), Ability.getAbilityValue(configpath, abiKey[j]));
                        ablst.add(ability);
//                        Log.e("MyJson", "-- getAbilitys name: "+ability.getName()+" ability: "+ability);
                    }
                }
            }
        }

        return ablst;
    }

    private static Ability getAbilityFromJsonObject(JSONObject obj, int id, String name) {
        if (obj != null) {
            Ability ability = new Ability(id, name);
            ability.setRight((float)obj.optDouble("a_right", 0.0));
            ability.setTotal((float)obj.optDouble("a_total", 1.0));
            return ability;
        }
        return null;
    }

    public static String getUserInfoPath(int uid) {
        String section = "user_"+uid+".json";
        String filePath = getFullPath(section);
        return filePath;
    }

    public static String getBookJsonPath(int uid, int grade, int subject, int bookid) {
        String section = "book_"+uid+"_"+grade+"_"+subject+"_"+bookid+".json";
        String filePath = getFullPath(section);
        return filePath;
    }

    public static String getRecommBookJsonDefaultPath(int grade) {
        String recomm = "default_recommbook_"+grade+".json";
        String filePath = getFullPath(recomm);
        return filePath;
    }

    public static String getRecommBookJsonPath(int uid, int grade, ArrayList<Integer> booksid) {
        String subjectstr = "";
        if (booksid!=null && booksid.size() > 0) {
            for (int i=0; i<booksid.size(); i++) {
                subjectstr = "_"+booksid.get(i);
            }
        }
        String recomm = "recommbook_"+uid+"_"+grade+subjectstr+".json";
        String filePath = getFullPath(recomm);
        return filePath;
    }

    public static String getSectionJsonPath(int section_id) {
        String section = "section_"+section_id+".json";
        String filePath = getFullPath(section);
        return filePath;
    }

    public static String getSectionJsonPath(int grade, int subject, int section_id) {
        String section = "section_"+section_id+"_"+grade+"_"+subject+".json";
        String filePath = getFullPath(section);
        return filePath;
    }

    public static String getConfigPath(String filename) {
        String filePath = getFullPath(filename);
        return filePath;
    }

    private static String getFullPath(String filename) {
        String filePath = Environment.getExternalStorageDirectory().getPath();
        filePath += File.separator + "Android/data/com.readboy.intelligencetesting/cache";
        File parent = new File(filePath);
        if (!parent.exists()) {
            parent.mkdirs();
        }
        filePath += File.separator + filename;
        return filePath;
    }

    private static String readString(InputStream io) {
        // 读取txt内容为字符串
        StringBuffer txtContent = new StringBuffer();
        // 每次读取的byte数
        byte[] b = new byte[8 * 1024];
        InputStream in = io;
        try {
            // 文件输入流
            while (in.read(b) != -1) {
                // 字符串拼接
                txtContent.append(new String(b));
            }
            // 关闭流
            in.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return txtContent.toString();
    }

}
