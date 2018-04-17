package com.readboy.aliyunplayerlib.utils;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {

    private static final int BUFFER_SIZE = 8192;


	/**
	 * 获取字符串的md5值
	 * @param s
	 * @return
	 */
	public static String getMd5(String s) {
		if(TextUtils.isEmpty(s)){
			return s;
		}
	    try {
	        // Create MD5 Hash
	        MessageDigest digest = MessageDigest.getInstance("MD5");
	        digest.update(s.getBytes());
	        byte messageDigest[] = digest.digest();

	        // Create Hex String
	        StringBuffer hexString = new StringBuffer();
	        for (int i=0, v=0; i<messageDigest.length; i++){
	        	v = 0xFF & messageDigest[i];
	        	if(v<16){
	        		hexString.append("0");
	        	}
	            hexString.append(Integer.toHexString(v));
//				hexString.append(
//	            String.format("%02x", 0xFF & messageDigest[i]));
	        }
	        return hexString.toString();
	    } catch (NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    }
	    return s;
	}

	/**
	 * 获取16位MD5
	 * @param s
	 * @return
     */
	public static String get16bitMd5(String s){
		String ret = getMd5(s);
		ret = ret.substring(8, 24);
		return ret;
	}

	/**
	 * 计算assets下文件的MD5值
	 */
	public static String getAssetsFileMd5(Context context, String fileName) {
		int bytes;
		byte buf[] = new byte[BUFFER_SIZE];
		try {
			InputStream is = context.getAssets().open(fileName);
			MessageDigest md = MessageDigest.getInstance("MD5");
			while ((bytes = is.read(buf, 0, BUFFER_SIZE)) > 0) {
				md.update(buf, 0, bytes);
			}
			is.close();
			return bytes2hex(md.digest());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

    /**
     * 获取单个文件的MD5值
     */
    public static String getFileMd5(File file) {
        if(file == null || !file.isFile()){
            return null;
        }
        int bytes;
        byte buf[] = new byte[BUFFER_SIZE];
        try {
            FileInputStream is = new FileInputStream(file);
            MessageDigest md = MessageDigest.getInstance("MD5");
            while ((bytes = is.read(buf, 0, BUFFER_SIZE)) > 0) {
                md.update(buf, 0, bytes);
            }
            is.close();
            return bytes2hex(md.digest());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getFileMd5(String filePath) {
        if(!TextUtils.isEmpty(filePath)) {
            File file = new File(filePath);
            if (file.exists() && file.isFile()) {
                return getFileMd5(file);
            }
        }
        return null;
    }

	private static String bytes2hex(byte[] bytes) {
		StringBuffer sb = new StringBuffer(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			int v = bytes[i] & 0xff;
			if (v < 16) {
				sb.append('0');
			}
			sb.append(Integer.toHexString(v));
		}
		return sb.toString();
	}

}
