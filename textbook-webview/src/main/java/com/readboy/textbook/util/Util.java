package com.readboy.textbook.util;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

public class Util {

	private static final String tag = Util.class.getSimpleName();

	/**
	 * �ж����Ƿ��ڶ��㼯�Ϲ��ɵķ��ͼ����
	 * 
	 * @param x
	 *            Ҫ�ж����x���
	 * @param y
	 *            Ҫ�ж����y���
	 * @param type
	 *            1Ϊ���� 2Ϊ��Բ�� 3Ϊ���������Σ���ݶ������ж�
	 * @param vectors
	 *            �����б�
	 * @return �Ƿ���ڷ��ͼ����
	 */
	public static boolean isInRegion(float x, float y, int type,
			ArrayList<Point> vectors) {
		if (type == 1) {
			// �����Σ�����2���㣬��2��������ж�
			Point vec1 = vectors.get(0);
			Point vec2 = vectors.get(1);
			if (x > vec1.x && x < vec2.x && y > vec1.y && y < vec2.y)
				return true;
		} else if (type == 2) {
			Point vec1 = vectors.get(0);
			Point vec2 = vectors.get(1);
			float b = (vec2.x - vec1.x) / 2;
			float a = (vec2.y - vec1.y) / 2;
			x -= (vec1.x + vec2.x) / 2;
			y -= (vec1.y + vec2.y) / 2;
			if (x * x / a / a + y * y / b / b <= 1)
				return true;
		} else if (type == 3) {
			if(isInPolygon(x,y,vectors)){
				return true;
			}
		}
		return false;
	}
	/**
	 * �жϵ��ǲ����ڶ�����ڲ�
	 * ���������ͨ��õ��ˮƽ�������θ��ߵĽ���
	 * ���ۣ����߽���Ϊ�������
	 * @param x
	 * @param y
	 * @param vectors
	 * @return
	 */
	private static boolean isInPolygon(float x, float y, ArrayList<Point> vectors){
		int nCross = 0;
		int count = vectors.size();
		for(int i = 0; i < count; i++){
			Point vec1 = vectors.get(i);
			Point vec2 = vectors.get((i + 1) % count);
			//��⽻��
			if(vec1.y == vec2.y){
				continue;
			}
			if(y < Math.min(vec1.y, vec2.y)){
				continue;
			}
			if(y >= Math.max(vec1.y, vec2.y)){
				continue;
			}
			double pX = (double)(y - vec1.y)*(double)(vec2.x - vec1.x) / 
					(double)(vec2.y - vec1.y) + vec1.x;
			if(pX > x){
				nCross ++;
			}
		}
		
		return (nCross % 2 == 1);
	}

	/**
	 * ɾ���ļ��������ļ�
	 * 
	 * @param f
	 *            Ҫɾ����ļ� �ļ�����
	 */
	public static void deleteFile(File f) {
		File[] files = f.listFiles();
		if (files != null) {
			// ɾ�������ļ�
			for (File file : files) {
				deleteFile(file);
			}
		}
		f.delete();
	}

	/**
	 * ���ַ�ת����utf-8���룬ֻת�������ַ� �÷������Ǻܺã���Ϊ����һЩ�������޷��ɹ�ת��������ʹ��uriת��������ת��
	 * 
	 * @param str
	 *            Ҫ������ַ�
	 * @return ������ַ�
	 */
	public static String UrlEncode(String str) {
		// ��������
		Pattern p = Pattern.compile("[\\s\\u4e00-\\u9fa5]+");
		Matcher mat = p.matcher(str);
		String dst = str;
		while (mat.find()) {
			try {
				dst = str.replace(mat.group(),
						URLEncoder.encode(mat.group(), "utf-8"));
			} catch (UnsupportedEncodingException e) {
				Log.e("UrlEncode", "utf-8ת��ʧ��");
			}
		}
		return dst;
	}

	/**
	 * ��ȡuri���ļ���ʽ����������Ŀ¼������"/resources/pages/40800000000/image/P0025.jpg"�����õ�
	 * "/image/P0025.jpg" ���Ϊ��ʽ����ȡ��ʽ��md5���룬���϶���Ŀ¼����
	 * 
	 * @param src
	 *            ԭʼuri
	 * @return ��������ļ���
	 */
	public static String getSubFormat(String src) {
		String dst = src;
		Pattern pat = Pattern.compile("/\\w+/\\w+.\\w+$");
		Matcher mat = pat.matcher(src);
		if (mat.find()) {
			dst = mat.group();
		}

		// ���������������Ϊ��ʽ�Ļ���Ŀ¼
		if (dst.equals(src)) {
			Log.d(tag, "getSubFormat 1:"+src);
			pat = Pattern.compile(Constant.PATTERN_ADDRESS_LATEX);
			mat = pat.matcher(src);
			if (mat.find()) {
				String gongshi = src.substring(mat.end(), src.length());
				try {
					dst = Constant.CACHE_LATEX + "/"
							+ Util.EncodeMD5(URLDecoder.decode(gongshi, "utf-8")) + ".png";
				} catch (UnsupportedEncodingException e) {
					Log.d(tag, "��ʽת��ʧ��");
					return dst;
				}
			}
		}
		return dst;
	}

	/**
	 * ��ȡuri���ļ���ʽ����������Ŀ¼������"/resources/pages/40800000000/image/P0025.jpg"�����õ�
	 * "/image/P0025.jpg" ,���type = Constant.EXERCISE_TYPE_OUT�������õ�"../P0025.jpg"
	 * ���Ϊ��ʽ����ȡ��ʽ��md5���룬���϶���Ŀ¼����
	 * 
	 * @param src
	 *            ԭʼuri
	 * @param type
	 * 			   ���ͣ�type = Constant.EXERCISE_TYPE_IN ��ʾ�ڲ����
	 * 					type = Constant.EXERCISE_TYPE_OUT ��ʾ�ⲿ���
	 * @return ��������ļ���
	 */
	public static String getSubFormat(String src,int type) {
		String dst = src;
		Log.d(tag, "type:"+type);
		if(type == Constant.EXERCISE_TYPE_OUT){
			Pattern pat = Pattern.compile("/\\w+.\\w+$");
			Matcher mat = pat.matcher(src);
			if (mat.find()) {
				dst = mat.group();
			}
			// ���������������Ϊ��ʽ�Ļ���Ŀ¼
			if (dst.equals(src)) {
				Log.d(tag, "getSubFormat 2:"+src);
				pat = Pattern
						.compile(Constant.PATTERN_ADDRESS_LATEX);
				mat = pat.matcher(src);
				if (mat.find()) {
					String gongshi = src.substring(mat.end(), src.length());
					try {
						dst = Constant.CACHE_LATEX + "/"
								+ Util.EncodeMD5(URLDecoder.decode(gongshi, "utf-8")) + ".png";
					} catch (UnsupportedEncodingException e) {
						Log.d(tag, "��ʽת��ʧ��");
						return dst;
					}
				}
			}else{
				dst = Constant.CACHE_EXERCISE + dst;
			}
			
			return dst;
		}
		Pattern pat = Pattern.compile("/\\w+/\\w+.\\w+$");
		Matcher mat = pat.matcher(src);
		if (mat.find()) {
			dst = mat.group();
		}

		// ���������������Ϊ��ʽ�Ļ���Ŀ¼
		if (dst.equals(src)) {
			pat = Pattern
					.compile(Constant.PATTERN_ADDRESS_LATEX);
			mat = pat.matcher(src);
			if (mat.find()) {
				String gongshi = src.substring(mat.end(), src.length());
				try {
					dst = Constant.CACHE_LATEX + "/"
							+ Util.EncodeMD5(URLDecoder.decode(gongshi, "utf-8")) + ".png";
				} catch (UnsupportedEncodingException e) {
					Log.d(tag, "��ʽת��ʧ��");
					return dst;
				}
			}
		}
		return dst;
	}
	
	/**
	 * ���ַ���base64����󷵻�
	 * 
	 * @param src
	 * @return
	 */
	public static String DecodeBase64(String src) {
		byte[] byts = Base64.decode(src.getBytes(), Base64.DEFAULT);
		String dst = byts.toString();
		return dst;
	}

	/**
	 * ��Base64������ַ����󷵻�
	 * 
	 * @param src
	 * @return
	 */
	public static String EncodeBase64(String src) {
		if (src == null)
			return null;
		String dst = Base64.encodeToString(src.getBytes(), Base64.DEFAULT);
		return dst;
	}

	/**
	 * ���ַ����md5����
	 * 
	 * @param src
	 * @return
	 */
	public static String EncodeMD5(String src) {
		String dst = src;
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			char[] charArray = src.toCharArray();
			byte[] byteArray = new byte[charArray.length];

			for (int i = 0; i < charArray.length; i++)
				byteArray[i] = (byte) charArray[i];
			byte[] md5Bytes = md5.digest(byteArray);
			StringBuffer hexValue = new StringBuffer();
			for (int i = 0; i < md5Bytes.length; i++) {
				int val = ((int) md5Bytes[i]) & 0xff;
				if (val < 16)
					hexValue.append("0");
				hexValue.append(Integer.toHexString(val));
			}
			dst = hexValue.toString();
		} catch (NoSuchAlgorithmException e) {
			Log.v("md5", "md5�����㷨����ʧ��");
		}

		return dst;
	}
	
	public static int dip2px(Context context, float dpValue){
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
	
	public static int px2dip(Context context, float dpValue){
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue / scale + 0.5f);
	}
	
	/**
	 * �ж��ַ��Ƿ�Ϊ��
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(CharSequence str){
		if(TextUtils.isEmpty(str)){
			return true;
		}
		if(TextUtils.equals(str, "null") || TextUtils.equals(str, "NULL")){
			return true;
		}
		
		return false;
	}
	
	/**
	 * ����ͼƬ�ļ�
	 * 
	 * @param path
	 *            ��ַ
	 * @param bitmap
	 *            ͼƬbitmap
	 */
	public static void saveBitmap(String path, Bitmap bitmap) {
		// �����ļ�������
		try {
			FileOutputStream out = new FileOutputStream(path);
			bitmap.compress(CompressFormat.JPEG, 100, out);
			try {
				out.close();
			} catch (IOException e) {
				Log.e(tag, "ͼƬ�����ļ��ر�ʧ��");
			}
		} catch (FileNotFoundException e) {
			Log.e(tag, "ͼƬ����ʧ�� "+path);
		}
	}

	/**
	 * ��ȡwifi�Ƿ�����
	 * 
	 * @param context
	 * @return
	 */
	public int getNetworkType(Context context) {
		int netType = 0;
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			String extraInfo = networkInfo.getExtraInfo();
			if (extraInfo != null) {
				if (extraInfo.toLowerCase(Locale.getDefault()).equals("cmnet")) {
					netType = Constant.NETTYPE_CMNET;
				} else {
					netType = Constant.NETTYPE_CMWAP;
				}
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = Constant.NETTYPE_WIFI;
		}
		return netType;
	}

	/**
	 * ��ѹ���ļ�����ǰĿ¼��
	 * 
	 * @param zipFile
	 *            zip�ļ�·��
	 * @param destination
	 *            Ŀ���ѹ��ַ
	 * @return ���ؽ�ѹ���һ��Ŀ¼����û����Ϊ��һ��
	 */
	public static void unzip(File zipFile, String destination)
			throws IOException {
		ZipFile zip = new ZipFile(zipFile);
		Enumeration<? extends ZipEntry> en = zip.entries();
		ZipEntry entry = null;
		byte[] buffer = new byte[8192];
		int length = -1;
		InputStream input = null;
		BufferedOutputStream bos = null;
		File file = null;

		while (en.hasMoreElements()) {
			entry = (ZipEntry) en.nextElement();
			if (entry.isDirectory()) {
				continue;
			}

			input = zip.getInputStream(entry);
			file = new File(destination, entry.getName());
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			bos = new BufferedOutputStream(new FileOutputStream(file));

			while (true) {
				length = input.read(buffer);
				if (length == -1)
					break;
				bos.write(buffer, 0, length);
			}
			bos.close();
			input.close();
		}
		zip.close();

		// ��ȡ�鱾id�ţ������������߱�־�ļ�
		Pattern pat = Pattern.compile("\\d+");
		Matcher mat = pat.matcher(zipFile.getName());
		if (mat.find()) {
			// �������ļ���Ϣ���浽�����ļ���
			DataInputStream dis = new DataInputStream(new FileInputStream(
					Constant.ROOT_PATH + mat.group() + Constant.BOOK_CACHE));
			DataOutputStream dos = new DataOutputStream(new FileOutputStream(
					Constant.ROOT_PATH + mat.group() + "/" + mat.group()
							+ Constant.OFFLINE_SYMBOL));
			// ״̬����������ֵ��Ϊ0��������������
			dos.writeInt(0);
			dos.writeLong(0);

			byte[] buf = new byte[1024];
			length = -1;
			// ��dis��ʣ���ֽڿ�����dos��
			while ((length = dis.read(buf)) != -1) {
				dos.write(buf, 0, length);
			}
			dis.close();
			dos.close();
		}
		// ɾ��ѹ���ļ�
	}

	public static String TimeStamp2Date(int timestamp) {
		long time = ((long) timestamp * 1000);
		String date = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss",
				Locale.US).format(new java.util.Date(time));
		return date;
	}

	public static byte[] charTobyte(char[] c) {
		byte[] b = new byte[c.length * 2];
		for (int i = 0; i < c.length; i++) {
			b[i * 2] = (byte) ((c[i] & 0xff00) >> 8);
			b[i * 2 + 1] = (byte) (c[i] & 0xff);
		}
		return b;
	}

	public static char[] byteTochar(byte[] b) {
		char[] c = new char[b.length / 2];
		for (int i = 0; i < c.length; i++) {
			c[i] = (char) (b[i * 2] & 0xff00 << 8 + b[i * 2 + 1] & 0xff);
		}
		return c;
	}

	/**
	 * �����ļ��У���nomedia��
	 * @param dirPath
	 */
	public static void mkdirs(String dirPath){
		try{
			File file = new File(dirPath);
			if(!file.exists()){
				file.mkdirs();
			}
			String filePath = dirPath.endsWith(File.separator) ? (dirPath +  Constant.NOMEDIA) : (dirPath + File.separator + Constant.NOMEDIA);
			File f = new File(filePath);
			if(!f.exists()){
				try {
					f.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * ������/������������Stringת����������int
	 * @param s
	 * @return
	 */
	public static int numStringToInt(String s){
		if(isEmpty(s)){
			return -1;
		}
		try{
			return Integer.parseInt(s);
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
		int result = 0;
		int current = 0;
		int pre = singleRomanToInt(s.charAt(0));
		int temp = pre;
		for(int i = 1; i < s.length(); i++){
			current = singleRomanToInt(s.charAt(i));
			if(current == pre){
				temp += current;
			}else if(current > pre){
				temp = current - temp;
			}else{
				result += temp;
				temp = current;
			}
			pre = current;
		}
		result += temp;
		
		return result;
	}

	private static int singleRomanToInt(char c){
		switch(c){
		case 'I':
		case 'i':
			return 1;
		case 'V':
		case 'v':
			return 5;
		case 'X':
		case 'x':
			return 10;
		case 'L':
		case 'l':
			return 50;
		case 'C':
		case 'c':
			return 100;
		case 'D':
		case 'd':
			return 500;
		case 'M':
		case 'm':
			return 1000;
		default:
			return 0;
		}
	}
}
