package com.readboy.textbook.util;

import android.os.Environment;
import android.text.TextUtils;

public class CacheUtils {
	
	
	public static final String ROOT_PATH = Environment.getExternalStorageDirectory() + "/eyesphone/";
	// 缓存目录
	public static final String CACHE_PATH = ROOT_PATH + "cache/";
	
	public static final String NOMEDIA = ".nomedia";

	public final static String CACHE_MEDIA = "/media";//存放声音（mp3\ogg...）文件
	
	/**
	 * 根据书本id和声音的Uri获取本地保存的位置
	 * 如果bookid传空，则保存在cache路径下
	 * 
	 * @return
	 */
	public static String getPageMediaByMediaUri(String bookId, String mediaUrl){
		String folder = "";
		if(TextUtils.isEmpty(bookId)){
			folder = Constant.CACHE_PATH;
		}else{
			folder = Constant.ROOT_PATH + bookId + Constant.CACHE_MEDIA;//音频目录
		}
		String path = folder + getFileName(mediaUrl);

		return path;
	}
	
	public static String getFileName(String filePath){         
        int start=filePath.lastIndexOf("/");  
        int end=filePath.lastIndexOf(".");  
        if(start != -1 && end != -1){  
            return filePath.substring(start+1,end);    
        }else if(start == -1 && end != -1){  
             return filePath;
        }else {
        	 return null;
		}  
          
    } 

}
