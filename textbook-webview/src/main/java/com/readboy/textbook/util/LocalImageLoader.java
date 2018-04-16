package com.readboy.textbook.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import com.readboy.textbook.util.DebugLogger;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

/**
 * 本地图片异步加载类,不包括网络部分，网络部分交由volley处理
 * 
 * @author lacheo
 *
 */
public class LocalImageLoader {

	private static final String TAG = LocalImageLoader.class.getSimpleName();

	private static LocalImageLoader INSTANCE;

	public static LocalImageLoader getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new LocalImageLoader();
		}
		return INSTANCE;
	}

	/**
	 * 缓存区
	 */
	private MemoryCache memoryCache = new MemoryCache();
	private ExecutorService executorService;

	/**
	 * 图片标签集合，用来确保图片不会错位
	 */
	private Map<ImageView, String> imageViews = Collections
			.synchronizedMap(new WeakHashMap<ImageView, String>());

	private LocalImageLoader() {
		// 最多同时运行3个线程加载图片
		executorService = Executors.newFixedThreadPool(3);
	}

	/**
	 * 带有监听事件的图片加载项
	 * 
	 * @param url
	 * @param img
	 * @param pMaxWidth
	 * @param pMaxHeight
	 * @param isLoadOnlyFromCache
	 * @param pImageLoaderListener
	 */
	public boolean DisplayImage(String url, ImageView img, int pMaxWidth,
			int pMaxHeight, boolean isLoadOnlyFromCache,
			ImageLoaderListener pImageLoaderListener) {
		File file = new File(url);
		if (!file.exists())
			return false;
		imageViews.put(img, url);

		// 内存中有么？没有则开启新线程加载
		Bitmap bitmap = memoryCache.get(url);
		if (bitmap != null)
			img.setImageBitmap(bitmap);
		else if (!isLoadOnlyFromCache) {
			queuePhoto(url, img, pMaxWidth, pMaxHeight, pImageLoaderListener);
		}
		return true;
	}

	/**
	 * 带有最大最小宽高的图片加载例
	 * 
	 * @param url
	 * @param img
	 * @param pMaxWidth
	 * @param pMaxHeight
	 */
	public boolean DisplayImage(String url, ImageView img, int pMaxWidth,
			int pMaxHeight, boolean isLoadOnlyFromCache) {
		return DisplayImage(url, img, pMaxWidth, pMaxHeight,
				isLoadOnlyFromCache, null);
	}

	public boolean DisplayImage(String url, ImageView img, int pMaxWidth,
			int pMaxHeight) {
		return DisplayImage(url, img, pMaxWidth, pMaxHeight, false, null);
	}

	/**
	 * 不压缩图片，加载完整图片
	 */
	public boolean DisplayImage(String url, ImageView img,
			boolean isLoadOnlyFromCache) {
		return DisplayImage(url, img, 0, 0, isLoadOnlyFromCache);
	}

	public boolean DisplayImage(String url, ImageView img) {
		return DisplayImage(url, img, 0, 0);
	}

	private void queuePhoto(String url, ImageView img, int pMaxWidth,
			int pMaxHeight, ImageLoaderListener pImageLoaderListener) {
		PhotoToLoad p = new PhotoToLoad(url, img, pMaxWidth, pMaxHeight,
				pImageLoaderListener);

		executorService.submit(new PhotosLoader(p));
	}

	private class PhotoToLoad {
		public String url;
		public ImageView img;

		public int maxWidth;
		public int maxHeight;

		ImageLoaderListener mImageLoaderListener;

		public PhotoToLoad(String u, ImageView i, int pMaxWidth, int pMaxHeight) {
			url = u;
			img = i;
			maxWidth = pMaxWidth;
			maxHeight = pMaxHeight;
		}

		public PhotoToLoad(String u, ImageView i, int pMaxWidth,
				int pMaxHeight, ImageLoaderListener pImageLoaderListener) {
			this(u, i, pMaxWidth, pMaxHeight);
			this.mImageLoaderListener = pImageLoaderListener;
		}
	}

	// 防止图片错位
	boolean imageViewReused(PhotoToLoad photoToLoad) {
		String tag = imageViews.get(photoToLoad.img);
		if (tag == null || !tag.equals(photoToLoad.url))
			return true;
		return false;
	}

	// 新线程载入图片
	class PhotosLoader implements Runnable {

		PhotoToLoad photoToLoad;
		boolean isCanceled;

		PhotosLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad = photoToLoad;
			this.isCanceled = false;
		}

		@Override
		public void run() {
			if (imageViewReused(photoToLoad))
				return;

			// 根据图片宽高获取图片
			Bitmap bm = getBitmap(photoToLoad.url, photoToLoad.maxWidth,
					photoToLoad.maxHeight);
			memoryCache.put(photoToLoad.url, bm);
			if (imageViewReused(photoToLoad))
				return;
			BitmapDisplayer bd = new BitmapDisplayer(bm, photoToLoad);
//			photoToLoad.img.post(bd);
			Activity a = scanForActivity(photoToLoad.img.getContext());
			a.runOnUiThread(bd);
		}
		
		private Bitmap getBitmap(String url, int pMaxWidth, int pMaxHeight) {

			Bitmap b = null;

			Pattern p = Pattern.compile("^http:");
			// 如果是网络请求的地址则从网络加载图片，否则从本地文件加载图片
			if (p.matcher(url).find()) {//本地地址不会跑这里
				b = decodeFileFromHttp(url, pMaxWidth, pMaxHeight);
			} else {
				b = decodeFile(url, pMaxWidth, pMaxHeight);
			}
			return b;
		}

		/**
		 * 从本地文件加载图片
		 * 
		 * @param url
		 * @return
		 */
		private Bitmap decodeFile(String pUrl, int pMaxWidth, int pMaxHeight) {
			try {
				File f = new File(pUrl);
				if (f == null || !f.exists())
					return null;
				Bitmap bitmap;
				// 如果限制宽高，则加载时时进行缩放，否则原大小加载
				if (pMaxWidth != 0 && pMaxHeight != 0) {
					BitmapFactory.Options option = new BitmapFactory.Options();
					option.inJustDecodeBounds = true;
					BitmapFactory.decodeStream(new FileInputStream(f), null,
							option);
					final int REQUIRED_WIDTH = pMaxWidth;
					final int REQUIRED_HEIGHT = pMaxHeight;
					int w = option.outHeight, h = option.outHeight;
					int scale = 1;
					while (true) {
						if (w / 2 < REQUIRED_WIDTH || h / 2 < REQUIRED_HEIGHT)
							break;
						w /= 2;
						h /= 2;
						scale *= 2;
					}
					BitmapFactory.Options option2 = new BitmapFactory.Options();
					option2.inSampleSize = scale;
					bitmap = BitmapFactory.decodeStream(new FileInputStream(f),
							null, option2);
				} else {
					bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
				}
//				if(Pattern.compile(".gif$").matcher(pUrl).find()){
//					return BitmapUtils.createGifThumbnail(App.getInstance(), bitmap, bitmap.getWidth(), bitmap.getHeight());
//				}
				return bitmap;
			} catch (FileNotFoundException e) {
			}
			return null;
		}

		/**
		 * 从网络加载图片，已废除，不修改了
		 * 
		 * @param url
		 * @return
		 */
		@Deprecated
		private Bitmap decodeFileFromHttp(String pUrl, int pMaxWidth,
				int pMaxHeight) {
			File temp = null;
			try {
				URL url = new URL(Util.UrlEncode(pUrl));
				// 扫描缓存文件夹查看是否存在缓存图片，如存在则直接从本地文件缓存读取文件
				String md5FileName = Util.getSubFormat(url.toString(),Constant.EXERCISE_TYPE_IN) ;
				if (searchCacheFile(md5FileName)) {
					return decodeFile(md5FileName, pMaxWidth, pMaxHeight);
				}

				InputStream is = null;
				URLConnection openConnection = url.openConnection();
				if (openConnection == null || openConnection.getDate() <= 0)
					return null;

				HttpURLConnection connection = (HttpURLConnection) openConnection;
				is = connection.getInputStream();
				if (is == null)
					return null;

				// 每次读取1024字节，如果线程取消则退出
				byte[] byts = new byte[1024];
				// 用来做本地缓存的文件,文件名为对url进行md5加密
				temp = new File(md5FileName);
				temp.createNewFile();
				FileOutputStream os = new FileOutputStream(temp);

				while (!isCanceled) {
					int len = is.read(byts);
					if (len <= 0) {
						break;
					}
					os.write(byts, 0, len);
				}

				os.close();

				// 线程未取消，继续加载图片
				// 如果限制宽高，则加载时时进行缩放，否则原大小加载
				return decodeFile(temp.getAbsolutePath(), pMaxWidth, pMaxHeight);
			} catch (MalformedURLException e) {
				DebugLogger.getLogger().e("url format err");
			} catch (Exception e) {
				DebugLogger.getLogger().e("write into fail");
			} finally {
				// 如果任务取消了，则删除缓存文件
				if (isCanceled) {
					temp.delete();
					return null;
				}
			}
			return null;
		}
	}

	private boolean searchCacheFile(final String target) {
		File folder = new File("");
		String[] file = folder.list(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String filename) {
				if (filename.equals(target))
					return true;
				return false;
			}
		});
		if (file != null && file.length > 0)
			return true;
		return false;
	}

	// 更新界面
	class BitmapDisplayer implements Runnable {
		Bitmap bm;
		PhotoToLoad pl;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
			this.bm = b;
			this.pl = p;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (imageViewReused(pl))
				return;
			if (pl.mImageLoaderListener != null) {
				pl.mImageLoaderListener.onCompletion();
			}
			if (bm != null)
				pl.img.setImageBitmap(bm);
		}
	}

	public void clearCache() {
		memoryCache.clear();
	}

	public interface ImageLoaderListener {
		void onCompletion();
	}

	private static Activity scanForActivity(Context cont) {
	    if (cont == null)
	        return null;
	    else if (cont instanceof Activity)
	        return (Activity)cont;
	    else if (cont instanceof ContextWrapper)
	        return scanForActivity(((ContextWrapper)cont).getBaseContext());

	    return null;
	}
	
	class MemoryCache {
		/**
		 * 缓存列表
		 */
		private Map<String, Bitmap> cache = Collections
				.synchronizedMap(new LinkedHashMap<String, Bitmap>(10, 1.5f, true));

		private long size = 0;
		private long limit = 1000000;

		public MemoryCache() {
			setLimit(Runtime.getRuntime().maxMemory() / 5);
		}

		public void setLimit(long new_limit) {
			limit = new_limit;
		}

		public Bitmap get(String id) {
			try {
				if (!cache.containsKey(id))
					return null;
				return cache.get(id);
			} catch (NullPointerException e) {
				return null;
			}
		}

		public void put(String id, Bitmap bitmap) {
			try {
				if (cache.containsKey(id))
					size -= getSizeInBytes(cache.get(id));
				cache.put(id, bitmap);
				size += getSizeInBytes(cache.get(id));
				checkSize();
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}

		private void checkSize() {
			if (size > limit) {
				Iterator<Entry<String, Bitmap>> iter = cache.entrySet().iterator();
				while (iter.hasNext()) {
					Entry<String, Bitmap> entry = iter.next();
					size -= getSizeInBytes(entry.getValue());
					iter.remove();
					if (size <= limit)
						break;
				}
			}
		}

		public void clear() {
			cache.clear();
			System.gc();
		}

		long getSizeInBytes(Bitmap bitmap) {
			if (bitmap == null)
				return 0;
			return bitmap.getRowBytes() * bitmap.getHeight();
		}
	}
}