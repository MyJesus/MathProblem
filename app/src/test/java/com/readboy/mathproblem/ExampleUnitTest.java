package com.readboy.mathproblem;

import com.readboy.mathproblem.application.SubjectType;

import org.json.JSONObject;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void cloneTest() throws Exception {
        CloneTest test1 = new CloneTest();
        test1.mName = "test1";
        test1.cloneA = new CloneA();

        CloneTest test2 = test1.clone();
        test2.mName = "test2";
        test2.cloneA.mName = "newCloneA";
        CloneA cloneA2 = new CloneA();
        cloneA2.mName = "cloneA2";
        test2.cloneA = cloneA2;

        JSONObject jsonObject = new JSONObject();
        jsonObject.keys();
        jsonObject.names();


    }

    @Test
    public void replaceFirst() throws Exception {
        String html = "<p>杀佛静安寺</P>";
        String header = "1.";
        String newHtml = "";
        if (html.startsWith("<p>")) {
            newHtml = html.replaceFirst("<p>", "<p>" + header);
        } else {
            newHtml = header + html;
        }
        print(newHtml);
    }

    private void print(String msg) {
        System.out.print(msg);
    }

    @Test
    public void enumTest() throws Exception {
        SubjectType type = SubjectType.guide;
        String typeString = type.toString();
        String s = type.name();
        int i = type.ordinal();

        String s1 = "abcd1\r\n\r\n";
        String s4 = s1.trim();
        String s2 = s1.replace("a", "");

        String s3= s1;
        SubjectType method = SubjectType.method;
        String methodString = method.toString();
        String se = method.name();
        int i2 = method.ordinal();
        print(type.toString());

    }

    @Test
    public void test() throws Exception {

        String md5 = md5("test");
        String md51 = encryptMD5("test");

        List<User> userList = new ArrayList<>();
        userList.add(new User("jassic", 18));
        userList.add(null);
        userList.add(new User("orcel", 17));

        long time = System.currentTimeMillis() / 1000;


        DecimalFormat format = new DecimalFormat("0%");
        String f = format.format(0.0345F);
//        String d = String.format(Locale.SIMPLIFIED_CHINESE, "%d/%", 45);
        String s = String.format(Locale.SIMPLIFIED_CHINESE, "%02d", 145);
        String s1 = String.format(Locale.SIMPLIFIED_CHINESE, "%02d", 5);
        String s2 = String.format(Locale.SIMPLIFIED_CHINESE, "%02d", 0);
    }

    public static String md5(String plainText) {
        byte[] secretBytes = null;
        try {
            secretBytes = MessageDigest.getInstance("MD5").digest(plainText.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("不支持MD5编码!");
        }
        //16进制数
        String md5code = new BigInteger(1, secretBytes).toString(16);
        //如果生成数字没满32位,需要前面补0
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        return md5code;
    }

    public static String encryptMD5(String data) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(data.getBytes());
            byte[] b = md.digest();
            int i;
            StringBuilder buf = new StringBuilder();
            for (byte aB : b) {
                i = aB;
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append(0);
                }
                buf.append(Integer.toHexString(i));
            }

            result = buf.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Test
    public void testList() throws Exception {
        List<User> userList = new ArrayList<>();
        userList.add(new User("David", 12));
        userList.add(new User("Peter", 15));
        userList.add(new User("ming", 14));

        userList.get(0).setAge(16);
        User user = userList.get(1);
        user.setName("Peter2");
        String[] userArray = new String[]{"Ou", "bin"};
        String msg = userList.toArray().toString();
        String s = Arrays.toString(userList.toArray());
        String s2 = Arrays.toString(userArray);


    }

    @Test
    public void testSplit() throws Exception {
        String uri = "/download/mp4qpsp/探秘(行程)之路_流水行船问题.mp4";
        formatUTF8(uri);
        String result = uri.substring(0, uri.lastIndexOf("."));
        String url = "http://d.elpsky.com/download/mp4rjb1/%E8%AF%AD%E6%96%87%E5%B0%8F%E5%AD%A62%E4%B8%8A%E5%B0%8F%E8%9D%8C%E8%9A%AA%E6%89%BE%E5%A6%88%E5%A6%88_9BF6.mp4?auth_key=1509349009-0-0-8abb91ff4f87df35576caf13ee5347ad";
        String regex1 = "auth_key=";
        int index = url.lastIndexOf(regex1);
        String regex2 = "-0-0-";
        int lastIndex = url.lastIndexOf(regex2);
        String time = url.substring(index + regex1.length(), lastIndex);
        int date = Integer.valueOf(time);

    }

    private static String formatUTF8(String url) {
        if (url == null || "".equals(url)) {
            return "";
        }
        String regex = "/";
        String[] splitArray = url.split(regex);
        StringBuilder result = new StringBuilder();
        int size = splitArray.length;
        for (int i = 0; ; i++) {
            try {
                String encode = URLEncoder.encode(splitArray[i], "utf-8");
                result.append(encode);
                if (i == size - 1) {
                    return result.toString();
                }
                result.append(regex);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return "";
            }

        }
    }

    private class User {
        String name;
        int age;

        public User(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        @Override
        public String toString() {
            return "User{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }

}