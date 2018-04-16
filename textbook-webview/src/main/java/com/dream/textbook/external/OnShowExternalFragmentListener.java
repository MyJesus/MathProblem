package com.dream.textbook.external;

import android.support.v4.app.Fragment;
import android.view.View;
import android.webkit.WebView;

public interface OnShowExternalFragmentListener {
	/** 外部fragment */
	void onShowFragment(Fragment fragment);
	void onShowView(View view);
	void onCloseFragment(boolean stopSound);
	/** 英语点读调用 */
	void onShowTranslate(String translateText);
	/** 密码管理调用 */
	void onShowPasswordManager(WebView webView);
	
	/**
	 * view点击事件
	 * @param type 调用类型
	 */
	void onClickView(int type, String content);
	/** 表示调用家长管理 */
	public static final int PASSWORD_MANAGER = 1;
	/** 课文整读 */
	public static final int TEXT_READING = 2;
}
