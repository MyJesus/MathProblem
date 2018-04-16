package com.readboy.mathproblem.notetool;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

public class ParcelCol {
    private static final String TAG = "Note_ParcelCol";
    public static final int PARCEL_HEIGHT = 40;

    public int mParcelTotalNum;
    public int mParcelNumPerPage;
    public int mFirstParcelIdx;
    public int mLastParcelIdx;
    public int mLastParcelHeight;

    private int mTotalHeight;
    private int mPageWidth;
    private int mPageHeight;
    private int mScrollY;
    private Canvas mCanvas;
    private Paint mPaint;
    private Rect mDirtyRect;
    private Bitmap[] mBitmap;
    private ParcelCache mParcelCache;
    private NoteDatabase mNoteDatabase;
    private boolean mIsNeedCache;
    private boolean mIsDiskTooSmall;
    private View mNoteView;

    public ParcelCol() {
        mNoteDatabase = new NoteDatabase();
        mPaint = new Paint();
        mPaint.setAlpha(0xff);
        // mPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
        // mPaint.setXfermode(new PorterDuffXfermode(Mode.MULTIPLY));
        mCanvas = new Canvas();
        mDirtyRect = new Rect();

    }

    public void setIsNeedCache(boolean bNeedCache) {
        mIsNeedCache = bNeedCache;
    }

    public int getParcelCount() {
        return mParcelTotalNum;
    }

    public void initDBInfo(String filename, String version) {
        boolean bRet = mNoteDatabase.init(filename, version);
        if (!bRet) {
            // Toast.makeText(mActivity, mNoteDatabase.getLastError(),
            // Toast.LENGTH_LONG).show();
        }
    }

    public void update(int scrollY) {
        int parcelIdx = scrollY / PARCEL_HEIGHT;
        int parcelNumNeedLoad;
        int parcelNumNeedMove;
        int idxSrc, idxDest, num;
        if (mBitmap == null) {
            return;
        }
        if (mBitmap[0] == null) {// 第一次的情况

            mFirstParcelIdx = parcelIdx;
            mLastParcelIdx = mFirstParcelIdx + mParcelNumPerPage - 1;

            for (idxDest = mFirstParcelIdx; idxDest <= mLastParcelIdx; idxDest++) {
                /* 这时缓存线程还没有开启，数据其实是在UI线程加载的 */
                if (idxDest == mParcelTotalNum - 1) {
                    mBitmap[idxDest - mFirstParcelIdx] = getParcel(idxDest,
                            mPageWidth, mLastParcelHeight);
                } else {
                    mBitmap[idxDest - mFirstParcelIdx] = getParcel(idxDest,
                            mPageWidth, PARCEL_HEIGHT);
                }

				/* 先把已经加载的bitmap添加到缓存中，避免二次加载 */
                if (mParcelCache != null) {
                    //TODO：内存泄露，OOM
                    Bitmap bmp = Bitmap.createBitmap(mBitmap[idxDest
                            - mFirstParcelIdx].getWidth(), mBitmap[idxDest
                            - mFirstParcelIdx].getHeight(), Note.BMP_CONFIG);
                    mCanvas.setBitmap(bmp);
                    mCanvas.drawBitmap(mBitmap[idxDest - mFirstParcelIdx], 0,
                            0, mPaint);
                    mParcelCache.addToArray(new Parcel(bmp, idxDest,
                            Parcel.ParcelType.ByCacheThread, true));
                }

            }
            /* 此时才真正开启缓存线程继续加载数据 */
            if (mParcelCache != null) {
                mParcelCache.startThread(mLastParcelIdx + 1);
            }
            return;
        }

        mScrollY = scrollY;
        if (parcelIdx < mFirstParcelIdx) {
            parcelNumNeedLoad = mFirstParcelIdx - parcelIdx;
            parcelNumNeedMove = mParcelNumPerPage - parcelNumNeedLoad;
            if (parcelNumNeedMove > 0) {
                for (idxSrc = mLastParcelIdx - parcelNumNeedLoad, idxDest = mLastParcelIdx, num = 0; num < parcelNumNeedMove; num++, idxSrc--, idxDest--) {
                    if (mBitmap[idxDest - mFirstParcelIdx] != null) {
                        mBitmap[idxDest - mFirstParcelIdx].recycle();
                    }
                    mBitmap[idxDest - mFirstParcelIdx] = mBitmap[idxSrc
                            - mFirstParcelIdx];
                    mBitmap[idxSrc - mFirstParcelIdx] = null;
                }

                mFirstParcelIdx = mFirstParcelIdx - parcelNumNeedLoad;
                mLastParcelIdx = mLastParcelIdx - parcelNumNeedLoad;

                for (idxDest = mFirstParcelIdx, num = 0; num < parcelNumNeedLoad; num++, idxDest++) {
                    if (idxDest == mParcelTotalNum - 1) {
                        mBitmap[idxDest - mFirstParcelIdx] = getParcel(idxDest,
                                mPageWidth, mLastParcelHeight);
                    } else {
                        mBitmap[idxDest - mFirstParcelIdx] = getParcel(idxDest,
                                mPageWidth, PARCEL_HEIGHT);
                    }
                }
            } else {
                mFirstParcelIdx = mFirstParcelIdx - parcelNumNeedLoad;
                mLastParcelIdx = mLastParcelIdx - parcelNumNeedLoad;
                for (idxDest = mFirstParcelIdx; idxDest < mLastParcelIdx; idxDest++) {
                    if (mBitmap[idxDest - mFirstParcelIdx] != null) {
                        mBitmap[idxDest - mFirstParcelIdx].recycle();
                    }
                    if (idxDest == mParcelTotalNum - 1) {
                        mBitmap[idxDest - mFirstParcelIdx] = getParcel(idxDest,
                                mPageWidth, mLastParcelHeight);
                    } else {
                        mBitmap[idxDest - mFirstParcelIdx] = getParcel(idxDest,
                                mPageWidth, PARCEL_HEIGHT);
                    }
                }
            }
        } else if (parcelIdx > mFirstParcelIdx) {
            if (parcelIdx + mParcelNumPerPage - 1 > mParcelTotalNum - 1) {
                //parcelIdx = mParcelTotalNum - mParcelNumPerPage;
                //Log.e(Note.TAG, "update -xxxxx ");
            }
            parcelNumNeedLoad = parcelIdx - mFirstParcelIdx;
            parcelNumNeedMove = mParcelNumPerPage - parcelNumNeedLoad;
            if (parcelNumNeedLoad <= 0) {
                return;
            }
            if (parcelNumNeedMove > 0) {
                for (idxSrc = mFirstParcelIdx + parcelNumNeedLoad, idxDest = mFirstParcelIdx, num = 0; num < parcelNumNeedMove; num++, idxSrc++, idxDest++) {
                    if (mBitmap[idxDest - mFirstParcelIdx] != null) {
                        mBitmap[idxDest - mFirstParcelIdx].recycle();
                    }
                    mBitmap[idxDest - mFirstParcelIdx] = mBitmap[idxSrc
                            - mFirstParcelIdx];
                    mBitmap[idxSrc - mFirstParcelIdx] = null;
                }

                mFirstParcelIdx = mFirstParcelIdx + parcelNumNeedLoad;
                mLastParcelIdx = mLastParcelIdx + parcelNumNeedLoad;
                for (idxDest = mLastParcelIdx + 1 - parcelNumNeedLoad, num = 0; num < parcelNumNeedLoad; num++, idxDest++) {
                    if (idxDest == mParcelTotalNum - 1) {
                        mBitmap[idxDest - mFirstParcelIdx] = getParcel(idxDest,
                                mPageWidth, mLastParcelHeight);
                    } else {
                        mBitmap[idxDest - mFirstParcelIdx] = getParcel(idxDest,
                                mPageWidth, PARCEL_HEIGHT);
                    }
                }
            } else {
                mFirstParcelIdx = mFirstParcelIdx + parcelNumNeedLoad;
                mLastParcelIdx = mLastParcelIdx + parcelNumNeedLoad;
                if (mLastParcelIdx > mParcelTotalNum - 1) {
                    mLastParcelIdx = mParcelTotalNum - 1;
                    mFirstParcelIdx = mLastParcelIdx - mParcelNumPerPage + 1;
                }
                for (idxDest = mFirstParcelIdx; idxDest < mLastParcelIdx; idxDest++) {
                    if (mBitmap[idxDest - mFirstParcelIdx] != null) {
                        mBitmap[idxDest - mFirstParcelIdx].recycle();
                    }
                    if (idxDest == mParcelTotalNum - 1) {
                        mBitmap[idxDest - mFirstParcelIdx] = getParcel(idxDest,
                                mPageWidth, mLastParcelHeight);
                    } else {
                        mBitmap[idxDest - mFirstParcelIdx] = getParcel(idxDest,
                                mPageWidth, PARCEL_HEIGHT);
                    }
                }
            }
        }
    }

    public void exit() {
        if (mNoteDatabase != null) {
            mNoteDatabase.close();
        }
        recycleBitmap();
    }

    private void recycleBitmap() {
        if (mBitmap != null) {
            for (int i = 0; i < mBitmap.length; i++) {
                if (mBitmap[i] != null) {
                    mBitmap[i].recycle();
                    mBitmap[i] = null;
                }
            }
            mBitmap = null;
        }
        if (mParcelCache != null) {
            mParcelCache.recycleBitmap();
            mParcelCache = null;
        }
    }

    public void draw(Canvas canvas, boolean bOnlyDrawToBmp) {// , Paint paint
        Rect srcRc = new Rect(), dstRc = new Rect();
        int idx;
        int scrollY = mScrollY;
        int parcelNum = mLastParcelIdx - mFirstParcelIdx + 1;
        int y = scrollY;
        int offsetY = y % PARCEL_HEIGHT;
        int height = 0;

        srcRc.left = 0;
        srcRc.right = mPageWidth;
        dstRc.left = 0;
        dstRc.right = mPageWidth;
        if (mBitmap == null) {
            return;
        }
        if (bOnlyDrawToBmp) {
            y = 0;
            scrollY = 0;
        }
        for (idx = 0; idx < parcelNum; idx++) {
            if (idx == 0) {
                height = PARCEL_HEIGHT - offsetY;
                srcRc.top = offsetY;
            } else {
                height = PARCEL_HEIGHT;
                srcRc.top = 0;
                if (y + height > scrollY + mPageHeight) {
                    height = scrollY + mPageHeight - y;
                }
            }

            srcRc.bottom = srcRc.top + height;
            dstRc.top = y;
            dstRc.bottom = y + height;
            canvas.drawBitmap(mBitmap[idx], srcRc, dstRc, mPaint);
            y += height;

            if (y - scrollY >= mPageHeight) {
                break;
            }

        }
    }

    public void updateNoteInfo(int noteId, int pageWidth, int pageHeight,
                               int totalHeight, View noteView) {
        Log.e(TAG, "updateNoteInfo: ");
        mScrollY = 0;
        mPageWidth = pageWidth;
        mPageHeight = pageHeight;
        mTotalHeight = totalHeight;
        mNoteView = noteView;
        if (mPageHeight < PARCEL_HEIGHT) {
            mPageHeight = PARCEL_HEIGHT;
        }
        if (mTotalHeight < mPageHeight) {
            mTotalHeight = mPageHeight;
        }

        mParcelTotalNum = (mTotalHeight + PARCEL_HEIGHT - 1) / PARCEL_HEIGHT;
        mParcelTotalNum += 1;
        //TODO：是否限制最大值
        mParcelTotalNum = Math.min(mParcelTotalNum, NoteConfig.MAX_COUNT_ONE_NOTE);
        int totalNum = mNoteDatabase.setNoteInfo(noteId, getParcelCount());
        //调整，校准数据
        if (totalNum > 0 && totalNum != mParcelTotalNum) {
            Log.e(TAG, "updateNoteInfo: totalNum = " + totalNum + ", mParcelTotalNum = " + mParcelTotalNum);
            mParcelTotalNum = totalNum;
            mTotalHeight = mParcelTotalNum * PARCEL_HEIGHT + 1 - PARCEL_HEIGHT;
            Log.e(TAG, "updateNoteInfo: mTotalHeight = " + mTotalHeight);
        }
        mLastParcelHeight = mTotalHeight % PARCEL_HEIGHT;
        if (mLastParcelHeight == 0) {
            mLastParcelHeight = PARCEL_HEIGHT;
        }

        mParcelNumPerPage = (mPageHeight + PARCEL_HEIGHT - 1) / PARCEL_HEIGHT;
        mParcelNumPerPage += 1;

        if (mParcelTotalNum < mParcelNumPerPage) {
            mParcelNumPerPage = mParcelTotalNum;
        }

        Log.e(TAG, "updateNoteInfo: mParcelNumPerPage = " + mParcelNumPerPage);
        mFirstParcelIdx = 0;
        mLastParcelIdx = mFirstParcelIdx + mParcelNumPerPage - 1;
        recycleBitmap();
        mBitmap = new Bitmap[mParcelNumPerPage];
        if (mIsNeedCache) {
            mParcelCache = new ParcelCache(mPageWidth, PARCEL_HEIGHT,
                    mLastParcelHeight, mParcelNumPerPage, mParcelTotalNum,
                    mNoteDatabase);
            mParcelCache.setLoadCallback(new ParcelCache.LoadCallback() {

                @Override
                public void update(int idx) {
                    // Log.i(Note.TAG, "update idx=" + idx +
                    // "; mFirstParcelIdx="
                    // + mFirstParcelIdx + "; mLastParcelIdx="
                    // + mLastParcelIdx);
                    if (idx >= mFirstParcelIdx && idx <= mLastParcelIdx) {
                        mNoteView.postInvalidate();
                    }
                }
            });
        }
//        mNoteDatabase.setNoteInfo(noteId, getParcelCount());
    }

    private Bitmap getParcel(int parcelIdx, int w, int h) {
        Bitmap bmp = null;
        Bitmap bmpCache = null;

        if (mParcelCache != null) {
            bmpCache = mParcelCache.getParcel(parcelIdx);
            if (bmpCache != null) {
                bmp = Bitmap.createBitmap(w, h, Note.BMP_CONFIG);
                mCanvas.setBitmap(bmp);
                mCanvas.drawBitmap(bmpCache, 0, 0, mPaint);
                return bmp;
            }
        }

        byte[] bytes = mNoteDatabase.getParcelBytes(parcelIdx);

        // Log.i(Note.TAG, "getParcel parcelIdx=" + parcelIdx);
        // Log.i(Note.TAG, "getParcel w=" + w);
        // Log.i(Note.TAG, "getParcel h=" + h);
        // Log.i(Note.TAG, "getParcel bytes=" + bytes);
        if (bytes == null) {
//            Log.e(TAG, "getParcel: valuseAt Parcel bytes = " + parcelIdx + ", bytes = null");
            bmp = Bitmap.createBitmap(w, h, Note.BMP_CONFIG);
        } else {
//            Log.e(TAG, "getParcel: valuseAt Parcel bytes = " + parcelIdx + ", length = " + bytes.length);
            Options op = new Options();
            op.inMutable = true;

            bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, op);
            // Log.i(Note.TAG, "getParcel bmp w=" + bmp.getWidth());
            // Log.i(Note.TAG, "getParcel bmp h=" + bmp.getHeight());
        }

        return bmp;
    }

    public void clear(int color) {
        if (mBitmap == null || mBitmap.length == 0) {
            return;
        }
        int parcelNum = mLastParcelIdx - mFirstParcelIdx + 1;
        for (int idx = 0; idx < parcelNum; idx++) {
//            mBitmap[idx].eraseColor(color);
            if (mBitmap[idx] != null) {
                Log.e(TAG, "clear: idx = " + idx);
                mBitmap[idx].eraseColor(color);
            } else {
                Log.e(TAG, "clear: bitmpat = null, idx = " + idx + ", length = " + mBitmap.length);
            }
        }
        mNoteDatabase.deleteNote();
        if (mParcelCache != null) {
            mParcelCache.clear(color);
        }
    }


    public void parcelsToBitmap(Bitmap bitmap) {
        Canvas canvas = new Canvas();
        canvas.setBitmap(bitmap);
        draw(canvas, true);
    }

    public void bitmapToParcels(Bitmap bitmap, Rect rcDirty, boolean isOnlyCopy) {
        Rect srcRc = new Rect(), dstRc = new Rect();
        int idx;
        int scrollY = mScrollY;
        int offsetY = scrollY % PARCEL_HEIGHT;
        int height = 0;
        int dirtyHeight = 0;
        if (bitmap == null || mBitmap == null || rcDirty == null) {
            return;
        }

        if (rcDirty.top == rcDirty.bottom) {
            return;
        }

        if (isOnlyCopy) {

        }

        srcRc.left = 0;
        srcRc.top = 0;
        srcRc.right = mPageWidth;

        dstRc.left = 0;
        dstRc.right = mPageWidth;
        if (rcDirty.top != rcDirty.bottom) {
            for (idx = 0; idx < mParcelNumPerPage; idx++) {
                if (mBitmap[idx] == null) {
                    break;
                }
                if (idx == 0) {
                    height = PARCEL_HEIGHT - offsetY;
                    dstRc.top = offsetY;
                } else if (idx == mParcelNumPerPage - 1) {
                    height = mPageHeight - srcRc.top;
                    dstRc.top = 0;
                } else {
                    height = PARCEL_HEIGHT;
                    dstRc.top = 0;
                }

                srcRc.bottom = srcRc.top + height;
                dstRc.bottom = dstRc.top + height;

                if (!(dirtyHeight + mBitmap[idx].getHeight() < rcDirty.top || dirtyHeight > rcDirty.bottom)) {
                    mCanvas.setBitmap(mBitmap[idx]);
                    if (isOnlyCopy) {
                        mPaint.setStyle(Paint.Style.FILL);
                        mPaint.setXfermode(new PorterDuffXfermode(Mode.SRC));

                        mPaint.setColor(Note.BACK_COLOR_VALUE);
                        mCanvas.drawRect(dstRc, mPaint);
                        mPaint.setAlpha(0xff);
                        mPaint.setXfermode(null);
						/*
						 * if (dstRc.bottom == mBitmap[idx].getHeight() &&
						 * dstRc.top == 0){
						 * mBitmap[idx].eraseColor(Note.BACK_COLOR_VALUE); }
						 * else{ reverseRect.left = dstRc.left;
						 * reverseRect.right = dstRc.right; if (dstRc.top == 0){
						 * reverseRect.top = dstRc.right; reverseRect.bottom =
						 * mBitmap[idx].getHeight(); } else{ reverseRect.top =
						 * 0; reverseRect.bottom = reverseRect.top; }
						 * 
						 * //mCanvas.drawBitmap(mClearBitmap, dstRc, dstRc,
						 * mPaint); Log.e(Note.TAG, "bitmapToParcels dstRc.top"
						 * + dstRc.top); Log.e(Note.TAG,
						 * "bitmapToParcels dstRc.bottom" + dstRc.bottom); }
						 */
                    }
                    mCanvas.drawBitmap(bitmap, srcRc, dstRc, mPaint);
                }

                dirtyHeight += height;
                srcRc.top += height;
                if (srcRc.top >= mPageHeight) {
                    break;
                }
            }
        }

        // dirty区域 可以累积，保存时候会用到。
        if (mDirtyRect.top == mDirtyRect.bottom) {
            mDirtyRect = rcDirty;
        } else {
            if (mDirtyRect.top > rcDirty.top) {
                mDirtyRect.top = rcDirty.top;
            }
            if (mDirtyRect.bottom < rcDirty.bottom) {
                mDirtyRect.bottom = rcDirty.bottom;
            }
        }

    }

    public void clearDirtyRect() {
        mDirtyRect.set(0, 0, 0, 0);
    }

    public void setIsDiskTooSmall(boolean isDiskTooSmall) {
        mIsDiskTooSmall = isDiskTooSmall;
    }

    public void saveParcels() {
        Log.e(TAG, "saveParcels: 1");
        if (mBitmap == null) {
            Log.e(TAG, "saveParcels: bitmap = null");
            return;
        }
        int offsetY = mScrollY % PARCEL_HEIGHT;
        int dirtyHeight = 0;

        if (mDirtyRect.top != mDirtyRect.bottom) {
            Log.e(TAG, "saveParcels: 2");
            for (int idx = 0; idx < mParcelNumPerPage; idx++) {
                if (mBitmap[idx] == null) {
                    break;
                }
                if (!(dirtyHeight + mBitmap[idx].getHeight() < mDirtyRect.top || dirtyHeight > mDirtyRect.bottom)) {
                    // Log.i(Note.TAG, "getParcel saveParcel="
                    // + (mFirstParcelIdx + idx));
                    Log.e(TAG, "saveParcels: 3, mIsDiskTooSmall = " + mIsDiskTooSmall);
                    if (!mIsDiskTooSmall) {
                        mNoteDatabase.saveParcel(mBitmap[idx], mFirstParcelIdx
                                + idx);
                    }
                    if (mParcelCache != null) {
                        Bitmap bmp = Bitmap.createBitmap(
                                mBitmap[idx].getWidth(),
                                mBitmap[idx].getHeight(), Note.BMP_CONFIG);
                        mCanvas.setBitmap(bmp);
                        mCanvas.drawBitmap(mBitmap[idx], 0, 0, mPaint);
//                        Log.e(TAG, "saveParcels: mFirstParcelIdx = " + mFirstParcelIdx);
                        mParcelCache.addToArray(new Parcel(bmp, mFirstParcelIdx
                                + idx, Parcel.ParcelType.ByCacheThread, true));
                    }
                }

                if (idx == 0) {
                    dirtyHeight += PARCEL_HEIGHT - offsetY;
                } else {
                    dirtyHeight += mBitmap[idx].getHeight();
                }
            }
            mNoteDatabase.reQueryAfterDbUpdate();
        }
        clearDirtyRect();
    }


}