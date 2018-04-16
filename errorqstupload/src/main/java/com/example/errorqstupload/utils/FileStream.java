package com.example.errorqstupload.utils;

import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * 提供静态方法读取文件夹的文件，文件的内容
 * @author Administrator
 *
 */
public class FileStream {
	
	public static boolean deleteFile(String path) {
		File file = new File(path);
		boolean back =false;
		if (file.exists()) {
			back = file.delete();
		}
		return back;
	}

	/**
	 * 判断file相关的json问价是否存在
	 * @return
	 */
	public static boolean isExist(String filePath) {
		boolean reback = false;
		File file = new File(filePath);
		if (file.exists()) {
			reback = true;
		}
		return reback;
	}
	
	/**
	 * 获得filePath的父文件夹
	 * @param filePath：文件路径名
	 * @return 返回filePath的父文件夹.末尾已经添加了File.separator分隔符
	 */
	public static String getParentPath(String filePath) {
		String path = filePath;
		if (filePath != null) {
			int suffixOffset = filePath.lastIndexOf(".");
			path = filePath.substring(0, suffixOffset);
			
			File file = new File(path);
			if (!file.exists()) {
				file.mkdirs();
			}
		}
		return path+File.separator;
	}

	public static String getJsonPath(String filename) {
		String filePath = Environment.getExternalStorageDirectory().getPath();
		filePath += File.separator + "Android/data/com.readboy.intelligencetesting/cache";
		File parent = new File(filePath);
		if (!parent.exists()) {
			parent.mkdirs();
		}
		filePath += File.separator + filename;
		return filePath;
	}


	
	public static String getPath(String dirname) {
		String filePath = Environment.getExternalStorageDirectory().getPath();
		filePath += File.separator + dirname;
		return filePath;
	}
	
	/**
	 * 获得json文件的路劲名
	 * @param filePath
	 * @param jsonMd5
	 * @return
	 */
	public static String getJsonPath(String filePath, String jsonMd5) {
		String path = null;
		if (filePath != null) {
			int suffixOffset = filePath.lastIndexOf(".");
			String parentpath = filePath.substring(0, suffixOffset);
			
			File file = new File(parentpath);
			if (!file.exists()) {
				file.mkdirs();
			}
			path = parentpath + File.separator + jsonMd5 +".json"; // 同名文件夹下json路径

		}
		return path;
	}
	
	/**
	 * 
	 * @param srcFl
	 * @param dstFl 目标文件有的话，直接被覆盖；
	 */
	public static boolean copy(String srcFl, String dstFl){
		File src = new File(srcFl);
		boolean back = copy(src, dstFl);
		
		return back;
	}
	
	/**
	 * 复制srcFl文件到dstpath
	 * @param srcFl： 源文件
	 * @param dstpath： 目标文件有的话，直接被覆盖；
	 * @return 
	 */
	public static boolean copy(File srcFl, String dstpath){
		boolean back = false;
		InputStream srcIn = null;
		OutputStream dstOut = null;
		if (!srcFl.exists()) {
			return false;
		}
		File dst = new File(dstpath);
		if (!dst.exists()) {
			File pr = new File(dst.getParent());
			if (!pr.exists()) {
				pr.mkdir();
			}
		}
		try {
			srcIn = new FileInputStream(srcFl);
			dstOut = new FileOutputStream(dstpath, false);
			byte[] mBy = new byte[1024]; 
			int len = -1;
			try {
				while ((len = srcIn.read(mBy)) != -1) {
					dstOut.write(mBy, 0, len);
				}
				back = true;
			} catch (IOException e) {
				Log.e("", "---- copy read e is "+e);
			}
		} catch (FileNotFoundException e1) {
			Log.e("", "---- copy new stream e1 is "+e1);
		} finally {
			try {
				if (srcIn != null) {
					srcIn.close();
				}
				if (dstOut != null) {
					dstOut.close();
				} 
			} catch (IOException e) {
				Log.e("", "---- copy close e is "+e);
			}
		}
		return back;
	}
	
	
	/**
	 * @aim 获取文件的编码类型
	 * @param file
	 * @return
	 */
	public static String getCharset(File file) {
        String charset = "GBK";
        BufferedInputStream bis = null;
        byte[] first3Bytes = new byte[3];
        try {
            bis = new BufferedInputStream(new FileInputStream(file));
            bis.mark(0);
            int read = bis.read(first3Bytes, 0, 3);
            if (read != -1) {
            	if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
            		charset = "UTF-16LE";
            	} else if (first3Bytes[0] == (byte) 0xFE && first3Bytes[1] == (byte) 0xFF) {
            		charset = "UTF-16BE";
            	} else if (first3Bytes[0] == (byte) 0xEF && first3Bytes[1] == (byte) 0xBB
            			&& first3Bytes[2] == (byte) 0xBF) {
            		charset = "UTF-8";
            	}
            }
            bis.close();
            bis = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	try {
        		if (bis != null) {
        			bis.close();
        			bis = null;
        		}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
        return charset;
    }
	
	/**
	 * @aim 获取文件的编码类型
	 * @return
	 */
	public static String getCharset(byte[] first3Bytes) {
        String charset = "GBK";
        try {
        	if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
        		charset = "UTF-16LE";
        	} else if (first3Bytes[0] == (byte) 0xFE && first3Bytes[1] == (byte) 0xFF) {
        		charset = "UTF-16BE";
        	} else if (first3Bytes[0] == (byte) 0xEF && first3Bytes[1] == (byte) 0xBB
        			&& first3Bytes[2] == (byte) 0xBF) {
        		charset = "UTF-8";
        	}
        } catch (Exception e) {
            e.printStackTrace();
		}
        return charset;
    }

	
	
	/**
	 * 读取.json文件的内容
	 * @param jsonPath: .json文件的路径
	 * @return 返回一个JSONObject, 错误返回null；
	 */
	public static JSONObject getJsonDataFromKeyUtf8(String jsonPath) {
		File fl = new File(jsonPath);
		JSONObject jsonBack = null;
		if (fl.exists()) {
			FileInputStream fin = null;
			try {
				fin = new FileInputStream(fl);

				byte []arrNetJson = null;
				if (fin.available() > 4){
					arrNetJson = new byte[fin.available()];
					if (fin != null){
						fin.read(arrNetJson);
					}
				}
					
				if (arrNetJson != null) {
					String strjson = new String(arrNetJson);
					jsonBack = new JSONObject(strjson);
				}
		
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("", "====onSuccess=error====");
			} finally {
				try {
		            if (fin != null) {
		            	fin.close();
		            	fin = null;
		            }
				} catch (IOException e1) {
				}
			}
		}
		
		return jsonBack;
	}
	
	/**
	 * 
	 * @param jsonPath
	 */
	public static JSONArray getJsonDataFromKey(String jsonPath) {
		File fl = new File(jsonPath);
		JSONArray jsonBack = null;
		if (fl.exists()) {
			FileInputStream fin = null;
			try {
				fin = new FileInputStream(fl);
//				Log.i("", "available: "+fin.available());
//		        String strJson = null;
//		        while ((byteread = fin.read(tempchars)) != -1) {
//		        	strJson = new String(tempchars, 0, byteread, "UTF-16LE");
//		        	if (jsonBack != null) {
//		        		jsonBack += strJson;
//		        	} else {
//		        		jsonBack = strJson;
//		        	}
//		        }

				byte []arrNetJson = null;
				if (fin.available() > 4){
					arrNetJson = new byte[fin.available()];
					if (fin != null){
						fin.read(arrNetJson);
					}
				}
				if (arrNetJson != null) {
					int iStart = (arrNetJson[0] == 0xff && arrNetJson[1] == 0xfe) ? 2 : 0;
					String strNetJson = new String(arrNetJson, iStart, arrNetJson.length - iStart, "UTF-16LE");;
					Log.w("", "=======onSuccess====" + strNetJson);
					if (strNetJson != null && strNetJson.length() > 0){
						jsonBack = new JSONArray(strNetJson);
						Log.w("", "=======onSuccess==netJsonArray==" + jsonBack);
					}
				}
		
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("", "==divhee=====onSuccess=error==1===");
			} finally {
				try {
		            if (fin != null) {
		            	fin.close();
		            	fin = null;
		            }
				} catch (IOException e1) {
				}
			}
		}
		
		return jsonBack;
		
	}
	
	
	/**
	 * @aim 读取JSON数据
	 * @param fileName
	 *            文件名
	 * @return String JSON字段
	 * 
	 */
	public static String readFtmFileJsonData(String fileName) {
		String strJson = "";
		FileInputStream fis = null;
		try {
//			if (!bCheckFileIsOk(fileName)){
//				return strJson;
//			}
			fis = new FileInputStream(fileName);
			if (fis != null) {
				byte[] arrByte = new byte[128];
				int iBitDataTag = 0, iFileLenth = 0, iJsonStart = 0, iJsonEnd = 0, idCyc = 0;
				iFileLenth = fis.available();
				Log.i("", " iFileLenth: "+iFileLenth);
				if (iFileLenth > 128){
					fis.read(arrByte, 0, 128);
					if (arrByte[0x4c] == 'f' && arrByte[0x4d] == 't'
							&& arrByte[0x4e] == 'm' && (arrByte[0x4f] & 0x07) == 0x07) {
						iBitDataTag = arrByte[0x4f];
						iJsonStart = ((arrByte[0x58] & 0x00ff) << 0)
								+ ((arrByte[0x59] & 0x00ff) << 8)
								+ ((arrByte[0x5a] & 0x00ff) << 16)
								+ ((arrByte[0x5b] & 0x00ff) << 24);
						if ((iBitDataTag & 0xf8) == 0) {
							iJsonEnd = iFileLenth - 64;
						} else {
							for (idCyc = 3; idCyc < 12; idCyc++) {
								if ((iBitDataTag & (1 << idCyc)) != 0) {
									idCyc = 0x50 + idCyc * 4;
									iJsonEnd = ((arrByte[idCyc + 0] & 0x00ff) << 0)
											+ ((arrByte[idCyc + 1] & 0x00ff) << 8)
											+ ((arrByte[idCyc + 2] & 0x00ff) << 16)
											+ ((arrByte[idCyc + 3] & 0x00ff) << 24);
								}
							}
						}
						arrByte = new byte[iJsonEnd - iJsonStart];
						fis.skip(iJsonStart - 128);
						idCyc = fis.read(arrByte, 0, iJsonEnd - iJsonStart);
						if (idCyc != (iJsonEnd - iJsonStart)) {
							strJson = "";
						} else {
							strJson = new String(arrByte, 0, idCyc, "UTF-16LE");
						}
						Log.i("", " strJson 0 : "+strJson);
					}
					Log.i("", " arrByte 1 : "+arrByte);
				}
				fis.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				if (fis != null) {
					fis.close();
					fis = null;
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return strJson;
	}
	
	
	/**
	 * @aim 验证文件是否正确
	 * @param fileName
	 *            文件全路径
	 * @return 是否是好的数据
	 */
	public static boolean bCheckFileIsOk(String fileName) {
		boolean bFileIsOk = true;
		FileInputStream fis = null;
		try {
			if (!bFileExists(fileName)) {
				// 文件不存在
				bFileIsOk = false;
				Log.w("", "=====bCheckFileIsOk=1===");
			} else {
				String strTempFile = fileName.toLowerCase();
				fis = new FileInputStream(fileName);
				if (fis != null) {
					byte[] arrByte = new byte[128];
					if (fis.available() > 128){
						fis.read(arrByte, 0, 128);
						fis.close();
						if (strTempFile.endsWith(".ftm")) {
							if (!(arrByte[0x4c] == 'f' && arrByte[0x4d] == 't' && arrByte[0x4e] == 'm')) {
								// 文件验证码有误
								bFileIsOk = false;
								Log.w("", "=====bCheckFileIsOk=2===");
							}
						}
						long iCrcVaule = 0;
						iCrcVaule += ((arrByte[127] & 0x00ff) << 24);
						iCrcVaule += ((arrByte[126] & 0x00ff) << 16);
						iCrcVaule += ((arrByte[125] & 0x00ff) << 8);
						iCrcVaule += ((arrByte[124] & 0x00ff) << 0);
						// 校验值不正确
						bFileIsOk = ftmprd_DictGenCRC32(arrByte, 124, iCrcVaule);
					} else {
						bFileIsOk = false;
						fis.close();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.w("", "=====bCheckFileIsOk=4===");
			try {
				// 文件处理出错啦
				bFileIsOk = false;
				if (fis != null) {
					fis.close();
					fis = null;
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		Log.w("", "=====bCheckFileIsOk=====" + bFileIsOk);
		return bFileIsOk;
	}
	
	/**
	 * @aim 该文件路径所指文件是否存在
	 * @param filepath
	 *            文件全路径
	 * @return true 文件存在 false 文件不存在
	 */
	public static boolean bFileExists(String filepath) {
		try {
			if (filepath != null && filepath.length() > 0) {
				File f = new File(filepath);
				if (f.exists() && f.isFile()) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/** 获取prd格式的CRC校验值 */
	private static boolean ftmprd_DictGenCRC32(byte[] ptr, int len, long fileCRC) {
		int id = 0;
		byte ic = 0;
		int crc = 0;
		int[] crc_table = new int[] {
				// CRC余式表
				0x0000, 0x1021, 0x2042, 0x3063, 0x4084, 0x50a5, 0x60c6, 0x70e7,
				0x8108, 0x9129, 0xa14a, 0xb16b, 0xc18c, 0xd1ad, 0xe1ce, 0xf1ef, };

		while (id < len) {
			ic = (byte) ((crc >> 8) >> 4);
			crc <<= 4;
			crc ^= crc_table[(ic ^ ((ptr[id] & 0x00ff) / 0xF)) & 0xF];
			ic = (byte) ((crc >> 8) >> 4);
			crc <<= 4;
			crc ^= crc_table[(ic ^ ((ptr[id] & 0x00ff) & 0xF)) & 0xF];
			id++;
		}
		if (fileCRC == crc) {
			return true;
		}
		Log.w("", "-------- ftmprd_DictGenCRC32 fail ");
		return false;
	}

	/**
	 * 写文件
	 *
	 * @param fileName
	 * @param result
	 */
	public static void writeToFile(String fileName, String result) {
		try {
			File f = new File(fileName);
			if (!f.exists()) {
				f.createNewFile();
			}
			OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(f),"utf-8");
			BufferedWriter writer = new BufferedWriter(write);
			writer.write(result);
			write.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}