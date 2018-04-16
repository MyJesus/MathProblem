package com.readboy.textbookwebview;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.webkit.WebResourceResponse;

public class UrlCache
{

	public static final long ONE_SECOND = 1000L;
	public static final long ONE_MINUTE = 60L * ONE_SECOND;
	public static final long ONE_HOUR = 60L * ONE_MINUTE;
	public static final long ONE_DAY = 24 * ONE_HOUR;
	public static final long ONE_YEAR = ONE_DAY * 365;

	private static class CacheEntry
	{
		public String url;
		public String fileName;
		public String mimeType;
		public String encoding;
		public long maxAgeMillis;

		private CacheEntry(String url, String fileName, String mimeType, String encoding, long maxAgeMillis)
		{
			this.url = url;
			this.fileName = fileName;
			this.mimeType = mimeType;
			this.encoding = encoding;
			this.maxAgeMillis = maxAgeMillis;
		}
	}

	protected Map<String, CacheEntry> mCacheEntries = new HashMap<String, CacheEntry>();
	protected Context mContext = null;
	protected String mCachePath = null;

	public UrlCache(Context context)
	{
		mContext = context;
		// this.rootDir = this.activity.getFilesDir();
		mCachePath = mContext.getCacheDir().getPath();
	}

	public UrlCache(Context context, String cachePath)
	{
		mContext = context;
		mCachePath = cachePath;
	}

	private CacheEntry register(String url, String cacheFileName, String mimeType, String encoding, long maxAgeMillis)
	{

		CacheEntry entry = new CacheEntry(url, cacheFileName, mimeType, encoding, maxAgeMillis);

		mCacheEntries.put(url, entry);
		return entry;
	}

	public WebResourceResponse load(final String url, String mimeType, String encoding)
	{
		CacheEntry cacheEntry = mCacheEntries.get(url);

		if (cacheEntry == null)
		{
			int index = url.lastIndexOf("/");
			if(index != -1)
			{
				cacheEntry = register(url, url.substring(index+1), mimeType, encoding, ONE_YEAR);
			}
			else
			{
				return null;
			}
			
		}
		final File cachedFile = new File(mCachePath + File.separator + cacheEntry.fileName);

		if (cachedFile.exists())
		{
			long cacheEntryAge = System.currentTimeMillis() - cachedFile.lastModified();
			if (cacheEntryAge == cacheEntry.maxAgeMillis)
			{
				cachedFile.delete();

				// cached file deleted, call load() again.
				return load(url, mimeType, encoding);
			}

			// cached file exists and is not too old. Return file.
			try
			{
				return new WebResourceResponse(cacheEntry.mimeType, cacheEntry.encoding,
						new FileInputStream(cachedFile));
			}
			catch (FileNotFoundException e)
			{
				e.getMessage();
			}

		}
		else
		{
			InputStream inputStream = downloadAndStore(url, cacheEntry, cachedFile);
			if(inputStream != null)
			{
//				return new WebResourceResponse(cacheEntry.mimeType, cacheEntry.encoding, inputStream);
			}
		}

		return null;
	}

	private InputStream downloadAndStore(String url, CacheEntry cacheEntry, File cachedFile)
	{

		try
		{
			URL urlObj = new URL(url);
			try
			{
				URLConnection urlConnection = urlObj.openConnection();
				InputStream urlInput = urlConnection.getInputStream();
				FileOutputStream fileOutputStream;
				try
				{
					
					fileOutputStream = new FileOutputStream(cachedFile);
					byte[] buffer = new byte[4096];
					int lenght = -1;
					while ((lenght = urlInput.read(buffer)) != -1)
					{
						fileOutputStream.write(buffer, 0, lenght);
					}

					urlInput.close();
					fileOutputStream.close();
					return urlInput;
				}
				catch (FileNotFoundException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		catch (MalformedURLException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
		
	}
}
