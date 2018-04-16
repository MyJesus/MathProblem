/**
 * you can find the path of sdcard,flash and usbhost in here
 * @author chenjd
 * @email chenjd@allwinnertech.com
 * @data 2011-8-10
 */
package com.readboy.mathproblem.video.search;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.util.Log;

/**
 * define the root path of flash,sdcard,usbhost
 * 
 * @author chenjd
 * 
 */
public class DiskManager {
	private static final String TAG = DiskManager.class.getSimpleName();

	/**
	 * 获取存储设备列表
	 * @param context
	 * @return
	 */
	public static ArrayList<String> initDevicePath(Context context) {
		ArrayList<String>  totalDevicesList = new ArrayList<String>();
		try {
			StorageManager stmg = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
			Method mMethod = stmg.getClass().getMethod("getVolumePaths");
			String[] list = (String[]) mMethod.invoke(stmg);
			// 获取存储器列表
			if (list == null || list.length < 1) {
				return totalDevicesList;
			}
			int length = list.length;
			for (int i = 0; i < length; i++) {
				if (isExistDisk(list[i])) {
					totalDevicesList.add(list[i]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return totalDevicesList;
	}

	/**
	 * 获取自带sdcard的路径
	 * @return
	 */
	public static String getInterStoragePath() {
		return Environment.getExternalStorageDirectory().getPath();
	}
	
	/**
	 * 获取外部sd卡的路径
	 * @param context
	 * @return
	 */
	public static String getSdStoragePath(Context context) {
		String path = null;
		ArrayList<String> totalDevicesList = initDevicePath(context);
		int size = totalDevicesList.size();
		for (int i = 0; i < size; i++) {
			if (!getInterStoragePath().equals(totalDevicesList.get(i))) {
//				if (totalDevicesList.valuseAt(i).contains("st")) {
//				}
				path = totalDevicesList.get(i);
				break;
			}
		}
		return path;
	}

	/**
	 * 获取usb的路径
	 * @param context
	 * @return
	 */
	public static String getUsbStoragePath(Context context) {
		String path = null;
		ArrayList<String> totalDevicesList = initDevicePath(context);
		int size = totalDevicesList.size();
		for (int i = 0; i < size; i++) {
			if (!getInterStoragePath().equals(totalDevicesList.get(i))) {
				if (totalDevicesList.get(i).contains("usb")) {
					path = totalDevicesList.get(i);
					break;
				}
			}
		}
		return path;
	}

	/**
	 * 判断路径是否由多部分组成
	 * @param context
	 * @param dPath
	 * @return
	 */
	public static boolean hasMultiplePartition(Context context, String dPath) {
		try {
			if (TextUtils.isEmpty(dPath)) {
				return false;
			}
			ArrayList<String> totalDevicesList = initDevicePath(context);
			int size = totalDevicesList.size();
			for (int i = 0; i < size; i++) {
				if (dPath.equals(totalDevicesList.get(i))) {
					File file = new File(dPath);
					String[] list = file.list();
					int length = list.length;
					for (int j = 0; j < length; j++) {
						/* 如果目录命名规则不满足"主设备号:次设备号"(当前分区的命名规则),则返回false */
						int lst = list[j].lastIndexOf("_");
						if (lst != -1 && lst != (list[j].length() - 1)) {
							try {
								String major = list[j].substring(0, lst);
								String minor = list[j].substring(lst + 1, list[j].length());
								Integer.valueOf(major);
								Integer.valueOf(minor);
							} catch (NumberFormatException e) {
								/* 如果该字符串不能被解析为数字,则退出 */
								return false;
							} catch (Throwable e) {
								e.printStackTrace();
								return false;
							}
						} else {
							return false;
						}
					}
					return true;
				}
			}
			return false;
		} catch (Throwable e) {
			Log.e(TAG, "hasMultiplePartition() exception : " + e);
			return false;
		}
	}
	
	
	/**
	 * 判断外部sd卡是否存在
	 */
	public static boolean isExistSDCard(Context context) {
		return isExistDisk(getSdStoragePath(context));
	}

	/**
	 * 判断usb是否存在
	 */
	public static boolean isExistUSB(Context context) {
		return isExistDisk(getUsbStoragePath(context));
	}
	
	/**
	 * 判断路径是否存在
	 * @param path
	 * @return
	 */
	public static boolean isExistDisk(String path) {
		boolean isTrue = false;
		if (!TextUtils.isEmpty(path)) {
			File file = new File(path);
			// 如果总的大小为0，表示该存储器不存在
			if (file.getTotalSpace() > 0) {
				isTrue = true;
			}
		}
		Log.i(TAG, "isExistDisk(" + path + ") = " + isTrue);
		return isTrue;
	}
	
	/**
     * 获取空闲空间大小
     * @param path
     * @return
     */ 
	@SuppressWarnings("deprecation")
	public static long getVacantSpaceSize(String path) {
        StatFs fileStats = new StatFs(path); 
        fileStats.restat(path);
        long size = ((long) fileStats.getAvailableBlocks()*(long) fileStats.getBlockSize());
        Log.i(TAG, "getVacantSpaceSize() size = " + size);
        return size; 
    } 
}
