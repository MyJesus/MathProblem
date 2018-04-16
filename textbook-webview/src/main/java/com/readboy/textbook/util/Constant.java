package com.readboy.textbook.util;

import java.io.File;

import android.os.Environment;
import android.text.TextUtils;

public class Constant {
	/*
	 * 目录结构讲解： 
	 * 1级目录为bookcapture
	 * 2级目录为缓存的书本名称(BookId)，临时文件（cache），下载目录文件（download暂不支持下载）
	 * 3级目录为书本名称(BookId)下的页面信息json（page），页面用到的图片文件（image），页面用到的声音文件（media）
	 * 3级目录临时文件（cache）为临时文件，可立即清除
	 */

	private static final String TAG = Constant.class.getSimpleName();
	/**
	 *  根目录
	 */
	public static String ROOT_PATH ;//= Environment.getExternalStorageDirectory() + "/eyesphone/";
	
	// 缓存目录
	public static String CACHE_PATH ;//= ROOT_PATH + "cache/";
	// 下载目录
	public static String DOWNLOAD_PATH ;//= ROOT_PATH + "download/";
	// 书本文件信息的文件名
	public static final String BOOK_CACHE = "/book_structure.json";
	
	public static final String NOMEDIA = ".nomedia";

	public static final String SETTING_PREF = "setting";
	
	// 缓存文件夹字段
	public final static String CACHE_PAGE = "/page";//存放页面json
	public final static String CACHE_IMAGE = "/image";//存放图片（png\bmp\gif...）文件
	public final static String CACHE_MEDIA = "/media";//存放声音（mp3\ogg...）文件
	public final static String CACHE_CACHE = "/cache";
	public final static String CACHE_MP3 = "/mp3";
	public final static String CACHE_SOURCE = "/source";
	public final static String CACHE_LATEX = "/latex";
	public final static String CACHE_EXERCISE = "/exercise";
		
	/**
	 * 下载文件标志名,对于下载文件，0-1字节标识下载状态(0:下载完成，1：下载中，2：下载更新)，2-5字节标识下载id管理id号，6字节以后存储目录json信息
	 */
	public final static String OFFLINE_SYMBOL = ".offline";
	public final static String JSON_SYMBOL = ".json";

	//是否直接显示答案
	public static boolean IS_SHOW_ANSWER = true;
	// 搜索数据服务器地址
//	private final static String PUBLIC_ADDRESS_SEARCH = "http://tiku.readboy.com/api/";//外网
	private final static String PUBLIC_ADDRESS_SEARCH = "http://timu.readboy.com/api/";//test，正式改
	private final static String PRIVATE_ADDRESS_SEARCH = "http://192.168.20.235/";//内网
	public static String ADDRESS_SEARCH = PRIVATE_ADDRESS_SEARCH;
	// 图片识别服务器地址
	private static final String PUBLIC_ADDRESS_OCR = "http://imso.dream.cn/v2/find";//外网
	private static final String PRIVATE_ADDRESS_OCR = "http://192.168.20.234/imgf.php";//内网
	public static final String ADDRESS_OCR = PUBLIC_ADDRESS_OCR;
	// 资源数据服务器地址
	//就host， 已弃用
//	private static final String PUBLIC_RESOURCE_OCR = "http://tkres.readboy.com";//外网
	private static final String PUBLIC_RESOURCE_OCR = "http://contres.readboy.com";//外网
//	private static final String PRIVATE_RESOURCE_OCR = "http://192.168.20.235/";//内网
	private static final String PRIVATE_RESOURCE_OCR = "http://contres.readboy.com";//内网
	public static String ADDRESS_RESOURCE = PRIVATE_RESOURCE_OCR;
	//名师视频
	public static final String ADDRESS_VIDEO = "http://7xl8lz.com1.z0.glb.clouddn.com/";
	//公式图片地址
	//弃用
//	public final static String ADDRESS_LATEX = "http://tkres.readboy.com/latex?fontsize=16&latex=";
	public final static String ADDRESS_LATEX = "http://latex.readboy.com/latex?fontsize=16&latex=";
	//解析公式图片地址
	public final static String PATTERN_ADDRESS_LATEX = "http\\:\\/\\/tkres\\.readboy\\.com\\/latex\\?fontsize=16&latex=";
	//反馈地址
	public final static String ADDRESS_COMMENT = "";

	public final static String PAGE_FIELD = "pageres/";//"pageres/"
	public final static String PAGE_HASH_FIELD = "pageres/hash/";//"pageres/hash/";
	public final static String BARCODE_FIELD = "books/barcode/";
	public final static String BOOK_FIELD = "books/";
	public final static String SECTION_FIELD = "sections/";
	public final static String EXEX_FIELD = "exex/";
	public final static String GAME_FIELD = "games/section/";
	public final static String WOED_FIELD = "words";
	public final static String HANZI_FILED = "hanzi";
	
	public final static String QUESTION_FIELD = "questions/";
	public final static String EDITION_FIELD = "/editions";
	
	public final static String SHAREDPREFERENCES_NAME = "eyephone";
	
	/** 左滑距离 */
	public final static int FLING_LEFT = 200;
	/** 左滑Y轴距离限制 */
	public final static int FLING_HEIGHT_RESTRICT = 100;
	
	/** webview最大缓存 30M*/
	public static final long WEB_VIEW_MAX_CACHE_SIZE = 1024 * 1024 * 30;
	
	static{
		boolean exist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
		if(exist){
			ROOT_PATH = MyApplication.getInstance().getExternalCacheDir().getParentFile().getAbsolutePath() + "/eyesphone/";
			
			String path = Environment.getExternalStorageDirectory() + "/eyephone.test";
			File file = new File(path);
			if(file.exists()){
				ADDRESS_SEARCH = PRIVATE_ADDRESS_SEARCH;
				ADDRESS_RESOURCE = PRIVATE_RESOURCE_OCR;
			}else{
				ADDRESS_SEARCH = PUBLIC_ADDRESS_SEARCH;
				ADDRESS_RESOURCE = PUBLIC_RESOURCE_OCR;
			}
			NetWorkUtils.baseUrl = ADDRESS_RESOURCE;
		}
		
		if ( TextUtils.isEmpty(ROOT_PATH) ) {
			ROOT_PATH = MyApplication.getInstance().getCacheDir().getParentFile().getAbsolutePath() + "/eyesphone/";
		}
		
		CACHE_PATH = ROOT_PATH + "cache/";
		DOWNLOAD_PATH = ROOT_PATH + "download/";
	}
	//=================start=====================
	/**
	 * 状态栏高度
	 */
	public static int STATUSBAR_HEIGHT;
	/**
	 * actionBar高度
	 */
	public static int ACTIONBAR_HEIGHT;
	/**
	 * 设备宽度
	 */
	public static int DESIRED_WIDTH;
	/**
	 * 设备高度
	 */
	public static int DESIRED_HEIGHT;
	/**
	 * 设备dpi
	 */
	public static float DPI;
	//=================end=====================
	
	/**
	 * 扫描图片默认宽高
	 */
	public final static int DEFAULT_WIDTH = 480;
	/**
	 * 扫描图片默认宽高
	 */
	public final static int DEFAULT_HEIGHT = 720;

	/**
	 * 图片显示区域缩放系数
	 */
	public static float FACTORY;

	/**
	 * wifi网络
	 */
	public static final int NETTYPE_WIFI = 0x01;
	/**
	 * 2g网络
	 */
	public static final int NETTYPE_CMWAP = 0x02;
	/**
	 * 3g网络
	 */
	public static final int NETTYPE_CMNET = 0x03;
	/**
	 * 内部数据
	 */
	public static final int EXERCISE_TYPE_IN = 0x01;
	/**
	 * 外部数据
	 */
	public static final int EXERCISE_TYPE_OUT = 0x02;
}
