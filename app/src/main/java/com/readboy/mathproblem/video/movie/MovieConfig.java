package com.readboy.mathproblem.video.movie;

import android.os.Build;

public class MovieConfig {
	public static boolean USED_CEDARX = true;
	
	/**   url地址时效性有限制      */
	public static final int URL_TEMPORARY = 0x0;
	/**   url地址时效性无限制      */
	public static final int URL_PERMANENT  = 0x1;
	/**   url地址对应的文件为加密文件      */
	public static final int URL_ENCRYPT = 0x2; //Encrypt 
	
	static {
		if(Build.MODEL.contains("60")
				|| Build.MODEL.contains("500")
				|| Build.MODEL.contains("C10")
				|| Build.MODEL.contains("C2")
				|| Build.MODEL.contains("C3")
				|| Build.MODEL.contains("C1")
				) {
			USED_CEDARX = false; 
		}
	}
	

}
