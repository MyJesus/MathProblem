package com.example.errorqstupload.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

/**
 * 这个类是读取图片的类，图片的来源分别是res和asset下
 * @author Administrator
 *
 */
public class BitmapStream
{

  /**
   * 以指定格式，指定大小，解析res底下的图片；
   * @param context 上下文
   * @param resId 资源id
   * @param sampleSize 为0时，大小不变，小于0时，为缩小倍数，大于0时放大倍数
   * @param config config为null时，格式是ARGB_8888格式解析
   * @return 
   */
  public Bitmap bmpResStream(Context context, int resId, int sampleSize, Config config) 
  {
    Bitmap bitmap = null;
    InputStream inputStream = null;
    try{
      inputStream = context.getResources().openRawResource(resId);
      bitmap = bmpStream(inputStream, sampleSize, config);
    }catch (Exception e) {
    	Log.e("BitmapStream", "-------- bmpResStream resId is "+resId);
    	Log.e("BitmapStream", "-------- bmpResStream e is "+e);
    }
    finally{
      if (inputStream != null)
      {
        try {
          inputStream.close();
        } catch (IOException e) {
        	Log.e("BitmapStream", "-------- bmpResStream resId is "+resId);
        	Log.e("BitmapStream", "-------- bmpResStream e is "+e);
        }
      }
    }
//    WeakReference<Bitmap> softRef = new WeakReference<Bitmap>(bitmap);
//    return softRef.get();
    return bitmap;
   }
  
  
  /**
   * 以指定格式，大小，不变解析res底下的图片资源
   * @param context
   * @param resId
   * @param config 
   * @return
   */
  public Bitmap bmpResStream(Context context, int resId, Config config) {
    return bmpResStream(context, resId, 0, config);
  }
  
  
  /**
   * 以RGB_565格式解析res底下的图片资源
   * @param context
   * @param resId
   * @return
   */
  public Bitmap bmpResStream(Context context, int resId) {
    return bmpResStream(context, resId, Config.RGB_565);
  }
  
  
  /*********************************************************************************************/
  /**
   *  以指定格式，指定大小从Asset底下读取图片资源；
   * @param context
   * @param filePath 图片名称；
   * @param sampleSize sampleSize为0时，图片大小不变，sampleSize大于0，图片放大模式读取，sampleSize
   * 小于0，图片缩小模式读取
   * @param config config为null时，是以ARGB_8888格式读取图片资源；
   * @return
   */
  public Bitmap bmpAssetStream(Context context, String filePath, int sampleSize, Config config) {
    Bitmap bitmap = null;
    InputStream inputStream = null;
    try
    {
      inputStream = context.getResources().getAssets().open(filePath);
      bitmap = bmpStream(inputStream, sampleSize, config);
    }
    catch (IOException e) {
    	Log.e("BitmapStream", "-------- bmpAssetStream filePath is "+filePath);
    	Log.e("BitmapStream", "-------- bmpAssetStream e is "+e);
    }
    finally{
      if(inputStream != null){
          try {
            inputStream.close();
        } 
        catch (IOException e) {
        	Log.e("BitmapStream", "-------- bmpAssetStream filePath is "+filePath);
        	Log.e("BitmapStream", "-------- bmpAssetStream e is "+e);
        }
          inputStream = null;
        }
    }
    return bitmap;
  }
  
  
  /**
   * 以指定格式，大小不变读取图片资源；
   * @param context
   * @param filePath
   * @param config
   * @return
   */
  public Bitmap bmpAssetStream(Context context, String filePath, Config config) {
    return bmpAssetStream(context, filePath, 0, config);
  }
  
  
  /**
   * 以RGB_565格式读取Asset文件夹下的资源；
   * @param context
   * @param filePath  本地存储路径
   * @return
   */
  public Bitmap bmpAssetStream(Context context, String filePath) {
    return bmpAssetStream(context, filePath, Config.RGB_565);
  }
  
  
  /**
   * 文件路径名，返回一个bitmap
   * @param in
   * @return Bitmap
   */
  public Bitmap bmpStream(InputStream in) {
	  if (in == null) {
		  return null;
	  }
	  return bmpStream(in, 0, Config.RGB_565);
  }


  /**
   * 文件路径名，返回一个bitmap
   * @param config
   * @return
   */
  public BitmapFactory.Options option(Config config) {
	  BitmapFactory.Options opt = new BitmapFactory.Options();
	  if (config != null) {
		  opt.inPreferredConfig = config;
	  } else {
		  opt.inPreferredConfig = Config.ARGB_8888;
	  }
	  opt.inPurgeable = true;
	  
	  return opt;
  }


  /**
   * 文件路径名，返回一个bitmap
   * @param inputStream
   * @param sampleSize
   * @param config
   * @return
   */
  private Bitmap bmpStream(InputStream inputStream, int sampleSize, Config config) {
    Bitmap bitmap = null;
      BitmapFactory.Options opt = new BitmapFactory.Options();
      if (sampleSize > 0) {
        opt.inDensity = sampleSize;          //放大图片
      } else if(sampleSize < 0){
        opt.inSampleSize = Math.abs(sampleSize);   //缩小图片
      }
      if (config != null) {
        opt.inPreferredConfig = config;
      } else {
        opt.inPreferredConfig = Config.ARGB_8888;
      }
      opt.inPurgeable = true;
  
      try{
        bitmap = BitmapFactory.decodeStream(inputStream, null, opt);
      } catch (Exception e) {
    	  Log.e("BitmapStream", "-------- bmpStream is ");
      }
//    WeakReference<Bitmap> softRef = new WeakReference<Bitmap>(bitmap);
//    return softRef.get();
    return bitmap;
  }
  
}
