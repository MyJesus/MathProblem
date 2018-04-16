package com.readboy.mathproblem.video.proxy;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MidroVideoUrl {
	
	public static String md5(String id) {
		String back = null;
		MessageDigest md5 = null;
		id += "_typeindex_1_readboy";
		try {
			md5 = MessageDigest.getInstance("MD5");
			byte[] md5Bytes = md5.digest(id.getBytes());
	        back = new String(md5Bytes);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}  
		
		return back;
		
	}
	
	public static String getMd5(String md5String) {
		String back = null;
		if (md5String != null) {
			try {
				MessageDigest digest = MessageDigest.getInstance("MD5");
				digest.update(md5String.getBytes());
				BigInteger bigInt = new BigInteger(1, digest.digest());
				String md5 = bigInt.toString(16);
				while (md5.length() < 32) {
					md5 = "0" + md5;
				}
				back = md5;
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}

		return back;
	}
	
	public static String getUrl(String id, String packagename) {
		String back = null;
		String vcode = md5(id);
//		String url = "app-wkt.strongwind.cn/resource?vCode="+vcode+"&isEncrypt=1&version="+appversion+"&device="+device+"&pkg="+ packagename;
		return back;
	}

}
