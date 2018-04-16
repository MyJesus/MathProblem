package com.readboy.mathproblem.notetool;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.util.Log;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ParcelCache implements Runnable {
	private static final String TAG = "Note_ParcelCache";

	private final int MAX_PARCEL_NUM_TO_CACHE = 80;
	private final int MIN_PARCEL_NUM_BEFORE_RELEASE = 10;
	private int mParcelNumToCache;
	private int mParcelNumPerPage;
	private int mTotalParcelNum;
	private NoteDatabase mNoteDatabase;
	private Thread mThread;
	private Parcel[] mParcelArray;
	private Lock mLock;
	private int mParcelW;
	private int mParcelH;
	private int mLastParcelH;
	private int mParcelIdxInCacheThread;
	private CacheIntent mCacheIntentInCacheThread;
	private int mSleepMillsInCacheThread;
	/* 记录读取数据库的状态：0：未读，1，读了但无数据，2，读了有数据 */
	private byte[] mParcelStatesInChacheThread;

	private ObjectOutputStream mObjectOutputSteam = null;
	private LoadCallback mLoadCallback;

	/* 是否开启动态加载，目前不支持 */
	private boolean mEnableDynamic;

	public ParcelCache(int parcelW, int parcelH, int lastParcelH,
			int parcelNumPerPage, int totalParcelNum, NoteDatabase noteDb) {
		Log.e(TAG, "ParcelCache() called with: parcelW = " + parcelW + ", parcelH = "
				+ parcelH + ", lastParcelH = " + lastParcelH + ", parcelNumPerPage = "
				+ parcelNumPerPage + ", totalParcelNum = " + totalParcelNum);
		mParcelW = parcelW;
		mParcelH = parcelH;
		mLastParcelH = lastParcelH;
		mParcelNumPerPage = parcelNumPerPage;
		mTotalParcelNum = totalParcelNum;
		mNoteDatabase = noteDb;
		mEnableDynamic = false;
		mParcelNumToCache = MAX_PARCEL_NUM_TO_CACHE;
		if (totalParcelNum < MAX_PARCEL_NUM_TO_CACHE) {
			mParcelNumToCache = totalParcelNum;
		}

		if (totalParcelNum <= parcelNumPerPage) {
			mParcelNumToCache = 0;
		}

		if (mParcelNumToCache > 0) {
			mParcelArray = new Parcel[mParcelNumToCache];
			mParcelStatesInChacheThread = new byte[mTotalParcelNum];
			mThread = new Thread(this);
		}

		mLock = new ReentrantLock();
		mCacheIntentInCacheThread = new CacheIntent();
		mSleepMillsInCacheThread = 0;
	}

	private void lock() {
		mLock.lock();
		// Log.i(Note.TAG, "lock lock");
	}

	private void unlock() {
		// Log.i(Note.TAG, "lock unlock");
		mLock.unlock();
	}

	public void setLoadCallback(LoadCallback callback) {
		mLoadCallback = callback;
	}

	public void startThread(int parcelStartIdx) {
		if (mParcelNumToCache <= 0) {
			return;
		}

		mCacheIntentInCacheThread.mIdxExpectToLoad = parcelStartIdx;
		mParcelIdxInCacheThread = parcelStartIdx;
		mCacheIntentInCacheThread.mIsIdxExpectToLoadChange = true;
		mCacheIntentInCacheThread.mIsDownCache = true;

		mThread.start();
		mCacheIntentInCacheThread.mCacheState = CacheState.Going;
		Log.i(Note.TAG, "ParcelCache-startThread-threadId=" + mThread.getId());
	}

	public Bitmap getParcel(int parcelIdx) {
		if (mParcelNumToCache <= 0) {
			return null;
		}

		Parcel parcel = getParcelEx(parcelIdx, true);
		if (parcel != null) {
			return parcel.mBitmap;
		}
		return null;
	}

	public Parcel getParcelEx(int parcelIdx, boolean bDownExpect) {
		if (mParcelNumToCache <= 0) {
			return null;
		}

		lock();
		int idx, lastIdx, emptyParcelNum;
		Parcel parcel = null;
		Parcel lastParcel = null;
		for (idx = 0; idx < mParcelArray.length; idx++) {
			if (mParcelArray[idx] != null) {
				lastParcel = mParcelArray[idx];
				lastIdx = idx;
				if (mParcelArray[idx].mIdx == parcelIdx) {
					// Log.i(Note.TAG,
					// "ParcelCache getParcel--parcelIdx="+parcelIdx);
					// Log.i(Note.TAG,
					// "ParcelCache getParcel--mBitmap="+mParcelArray[idx].mBitmap);
					parcel = mParcelArray[idx];
					break;
				}
			}
		}
		// mCacheIntent.mIdxExpectToLoad = parcelIdx;
		// 如果需要动态加载
		if (mEnableDynamic) {
			if (mTotalParcelNum > MAX_PARCEL_NUM_TO_CACHE) {
				emptyParcelNum = getEmptyNumInParcelArray();
				if (emptyParcelNum <= MIN_PARCEL_NUM_BEFORE_RELEASE) {
					if (bDownExpect) {

					} else {

					}
				}
			}
		}

		unlock();
		return parcel;
	}

	private void arrayMove(int step, boolean bRecycle) {
		int idx, endIdx;

		if (step > 0) {// 下移
			for (idx = mParcelArray.length - 1; idx >= step; idx--) {
				if (bRecycle) {
					if (mParcelArray[idx] != null) {
						mParcelArray[idx].recycle();
					}
				}
				mParcelArray[idx] = mParcelArray[idx - step];
				mParcelArray[idx - step] = null;
			}

			for (idx = 0; idx < step; idx++) {
				mParcelArray[idx] = null;
			}
		} else if (step < 0) {// 上移
			step = 0 - step;
			endIdx = mParcelArray.length - step;
			for (idx = 0; idx < endIdx; idx++) {
				if (bRecycle) {
					if (mParcelArray[idx] != null) {
						mParcelArray[idx].recycle();
					}
				}
				mParcelArray[idx] = mParcelArray[idx + step];
				mParcelArray[idx + step] = null;
			}

			endIdx = mParcelArray.length - step;
			for (idx = mParcelArray.length - 1; idx >= endIdx; idx--) {
				mParcelArray[idx] = null;
			}
		}
	}

	private int[] getParcelInfoNeedToCache() {
		int lastParcelIdx = -1;
		int[] ints = new int[2];
		ints[0] = -1;
		ints[1] = -1;

		if (mParcelArray[0] == null) {

		} else {
			for (int idx = 0; idx < mParcelArray.length; idx++) {
				if (mParcelArray[idx] != null) {
					lastParcelIdx = mParcelArray[idx].mIdx;
				} else {
					if (lastParcelIdx >= 0) {
						lastParcelIdx += 1;
						if (lastParcelIdx > mTotalParcelNum - 1) {
							lastParcelIdx = -1;
						}
					}
					ints[0] = idx;
					ints[1] = lastParcelIdx;
					return ints;
				}
			}
		}

		return ints;
	}

	private int getEmptyNumInParcelArray() {
		for (int idx = 0; idx < mParcelArray.length; idx++) {
			if (mParcelArray[idx] == null) {
				return mParcelArray.length - idx;
			}
		}

		return 0;
	}

	public void addToArray(Parcel parcel) {
		if (mParcelArray == null) {
            return;
        }
		lock();

		for (int idx = 0; idx < mParcelArray.length; idx++) {
            if (mParcelArray[idx] == null || mParcelArray[idx].mIdx == parcel.mIdx) {
				if (mParcelArray[idx] != null) {
					mParcelArray[idx].recycle();
				}
				mParcelArray[idx] = parcel;
				//TODO: 可能会超界。
				if (parcel.mIdx >=0  && parcel.mIdx < mParcelStatesInChacheThread.length) {
					mParcelStatesInChacheThread[parcel.mIdx] = 2;// UI线程写时有意义
				}else {
					Log.e(TAG, "addToArray: parcel index= " + parcel.mIdx + ", length = " + mParcelStatesInChacheThread.length);
				}
				break;
			} else if (mParcelArray[idx].mIdx > parcel.mIdx) {
				if (mParcelArray[mParcelArray.length - 1] != null) {
					mParcelArray[mParcelArray.length - 1].recycle();
				}
				for (int idxEx = mParcelArray.length - 1; idxEx > idx; idxEx--) {
					mParcelArray[idxEx] = mParcelArray[idxEx - 1];
				}
				mParcelArray[idx] = parcel;
				mParcelStatesInChacheThread[parcel.mIdx] = 2;// UI线程写时有意义
				break;
			}
		}
		unlock();
	}

	public void recycleBitmap() {
		if (mParcelNumToCache <= 0) {
			return;
		}
		lock();
		mCacheIntentInCacheThread.mCacheState = CacheState.Exit;
		unlock();
		mThread.interrupt();

		for (int i = 0; i < mParcelArray.length; i++) {
			if (mParcelArray[i] != null) {
				mParcelArray[i].recycle();
				mParcelArray[i] = null;
			}
		}
	}

	public void clear(int color) {
		if (mParcelNumToCache <= 0) {
			return;
		}
		lock();
		for (int i = 0; i < mParcelArray.length; i++) {
			if (mParcelArray[i] != null && mParcelArray[i].mBitmap != null) {
				mParcelArray[i].mBitmap.eraseColor(color);
			}
		}
		unlock();
	}

	@Override
	public void run() {
		while (mCacheIntentInCacheThread.mCacheState != CacheState.Exit) {
			try {
				if (mSleepMillsInCacheThread > 0) {
					Thread.sleep(mSleepMillsInCacheThread);
				}
				int idx;
				int emptyParcelNum;
				lock();
				emptyParcelNum = getEmptyNumInParcelArray();
				idx = getFirstEmptyStateIdx();
				if (mCacheIntentInCacheThread.mIsIdxExpectToLoadChange) {
					mCacheIntentInCacheThread.mIsIdxExpectToLoadChange = false;
					mParcelIdxInCacheThread = mCacheIntentInCacheThread.mIdxExpectToLoad;
				}
				unlock();
				if (mCacheIntentInCacheThread.mCacheState != CacheState.Going
						|| emptyParcelNum <= 0 || idx < 0) {
					mSleepMillsInCacheThread = 200;
					continue;
				}

				Bitmap bmp = null;
				byte[] bytes;

				if (mParcelIdxInCacheThread >= 0
						&& mParcelIdxInCacheThread < mTotalParcelNum
						&& mParcelStatesInChacheThread[mParcelIdxInCacheThread] == 0) {
					bytes = mNoteDatabase
							.getParcelBytes(mParcelIdxInCacheThread);
					// Log.i(Note.TAG, "ParcelCache-mParcelIdxInCacheThread="
					// + mParcelIdxInCacheThread);
					if (bytes == null) {
//						Log.e(TAG, "run: valuseAt parcelBytes : " + mParcelIdxInCacheThread + ", bytes = null.");
						mParcelStatesInChacheThread[mParcelIdxInCacheThread] = 1;
					} else {
//						Log.e(TAG, "run: valuseAt parcelBytes : " + mParcelIdxInCacheThread + ", bytes = " + bytes.length);
						Options op = new Options();
						op.inMutable = true;
						bmp = BitmapFactory.decodeByteArray(bytes, 0,
								bytes.length, op);
						if (bmp == null) {
							Log.e(Note.TAG, "ParcelCache-run-err-1-");
							mParcelStatesInChacheThread[mParcelIdxInCacheThread] = 0;
						} else {
							mParcelStatesInChacheThread[mParcelIdxInCacheThread] = 2;
//							Log.e(TAG, "run: mParcelIdxInCacheThread = " + mParcelIdxInCacheThread);
							addToArray(new Parcel(bmp, mParcelIdxInCacheThread,
									Parcel.ParcelType.ByCacheThread, true));
							if (mLoadCallback != null) {
								mLoadCallback.update(mParcelIdxInCacheThread);
							}
						}
					}
				}

				if (mCacheIntentInCacheThread.mIsDownCache) {
					mParcelIdxInCacheThread += 1;
					if (mParcelIdxInCacheThread >= mTotalParcelNum) {
						// 加满了，而mParcelArray还有空位，往回找。
						idx = getLastEmptyStateIdx();
						if (idx != -1) {
							mCacheIntentInCacheThread.setIntent(idx, false,
									true);
						}
					}
				} else {
					mParcelIdxInCacheThread -= 1;
					if (mParcelIdxInCacheThread < 0) {
						idx = getFirstEmptyStateIdx();
						if (idx != -1) {
							mCacheIntentInCacheThread
									.setIntent(idx, true, true);
						}
					}
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private int getLastEmptyStateIdx() {
		for (int idx = mParcelStatesInChacheThread.length - 1; idx >= 0; idx--) {
			if (mParcelStatesInChacheThread[idx] == 0) {
				return idx;
			}
		}

		return -1;
	}

	private int getFirstEmptyStateIdx() {
		for (int idx = 0; idx < mParcelStatesInChacheThread.length; idx++) {
			if (mParcelStatesInChacheThread[idx] == 0) {
				return idx;
			}
		}

		return -1;
	}

	private class CacheIntent {
		public int mIdxExpectToLoad;
		public boolean mIsIdxExpectToLoadChange;
		private CacheState mCacheState;
		public boolean mIsDownCache;

		public CacheIntent() {
			mIdxExpectToLoad = 0;
			mIsDownCache = false;
		}

		public CacheIntent(int parcelIdxToLoad, boolean isDownCache,
				boolean isIdxExpectToLoadChange) {
			mIdxExpectToLoad = parcelIdxToLoad;
			mIsDownCache = isDownCache;
			mIsIdxExpectToLoadChange = isIdxExpectToLoadChange;
		}

		public void setState(CacheState cacheState) {
			mCacheState = cacheState;
		}

		public void setIntent(int parcelIdxToLoad, boolean isDownCache,
				boolean isIdxExpectToLoadChange) {
			mIdxExpectToLoad = parcelIdxToLoad;
			mIsDownCache = isDownCache;
			mIsIdxExpectToLoadChange = isIdxExpectToLoadChange;
		}
	}

	public interface LoadCallback {
		public void update(int idx);
	}

	enum CacheState {
		Pause, Going, Exit
	}
}
