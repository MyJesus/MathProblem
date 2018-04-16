package com.readboy.mathproblem.notetool;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

//import android.graphics.PixelXorXfermode;

public class NoteDrawView extends View {
    private static final String TAG = "NoteDrawView";

    // private Context mContext;
    private Point mCurPoint = new Point(-1, -1);
    private Point mLastPoint = new Point(-1, -1);

    //画笔
    private Paint mPenPaint;
    //橡皮擦
    private Paint mEraserPaint;

    private Paint mBitmapPaint;
    private Bitmap mDrawBitmap;
    private PenAttr mPenAttr;

    private Canvas mCanvas;

    private Path mDrawPath;
    private int mScrollY;
    private int mPageWidth;
    private int mPageHeight;
    private StateType mStateType;
    private ParcelCol mParcelCol;
    private int mPathLength;
    private Rect mDirtyRect;
    private boolean mHasTouchUp;
    //是否开启画画功能。true代表禁用
    private boolean mIsNoteSwitching;
    private Rect mLayoutRect;

    public NoteDrawView(Context context) {
        super(context);
        init(context);
    }

    public NoteDrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NoteDrawView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        // mContext = context;
        mLayoutRect = new Rect();
        mIsNoteSwitching = true;
        mStateType = StateType.StateMove;
        mDrawPath = new Path();

        mDirtyRect = new Rect();
        mPenAttr = new PenAttr();
        mPenAttr.mColor = PenAttr.DEFAULT_COLOR;
        mPenAttr.mPenWidth = PenAttr.DEFAULT_PEN_WIDTH;

        mParcelCol = new ParcelCol();

        mPenPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPenPaint.setColor(mPenAttr.mColor);
        mPenPaint.setStrokeWidth(mPenAttr.mPenWidth);
        mPenPaint.setAlpha(0xff);

        mPenPaint.setStyle(Paint.Style.STROKE);
        mPenPaint.setStrokeCap(Cap.ROUND);
        setPenMask();

        // mPenPaint.setStrokeJoin(Paint.Join.ROUND);//PixelXorXfermode
        // mPenPaint.setXfermode(new PixelXorXfermode(2));
        // mPenPaint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        // EmbossMaskFilter maskFilter = new EmbossMaskFilter(new float[] { 1,
        // 1, 1 }, 0.4f, 6, 3.5f);
        // BlurMaskFilter maskFilter = new BlurMaskFilter(1,
        // BlurMaskFilter.Blur.NORMAL);
        // maskFilter = new BlurMaskFilter(4, BlurMaskFilter.Blur.NORMAL);
        // mPenPaint.setMaskFilter(maskFilter);
        // mPenPaint.setPathEffect(new CornerPathEffect(10));
        // mPenPaint.setPathEffect(new ComposePathEffect());

        mEraserPaint = new Paint();
        mEraserPaint.setColor(Note.BACK_COLOR_VALUE);
        mEraserPaint.setStrokeWidth(Note.ERASER_WIDTH);
        // mEraserPaint.setPathEffect(new CornerPathEffect(10));
        mEraserPaint.setStyle(Paint.Style.STROKE);
        mEraserPaint.setStrokeCap(Cap.ROUND);
        mEraserPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));

        mBitmapPaint = new Paint();
        mBitmapPaint.setAlpha(0xff);

//        initDBInfo(NoteConfig.DB_PATH, NoteConfig.DB_VERSION);
    }

    public void setIsNeedCache(boolean bNeedCache) {
        mParcelCol.setIsNeedCache(bNeedCache);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        mLayoutRect.left = left;
        mLayoutRect.top = top;
        mLayoutRect.right = right;
        mLayoutRect.bottom = bottom;
        //Log.i(Note.TAG,"onLayout changed="+changed+"; left="+left+"; top="+top+"; right="+right+"; bottom="+bottom);
        super.onLayout(changed, left, top, right, bottom);
    }

    public void reduceViewHeight() {
        /*解决切换内容时，瞬间又滚动条显示的问题*/
        setMinimumHeight(10);
    }

    /**
     * 注意：需要读写文件的权限，否者无法获取初始化mCursor.
     */
    public void initDBInfo(String parentPath, String version) {
        mParcelCol.initDBInfo(parentPath, version);
    }

    public void updateNoteInfo(int noteId, int pageWidth, int pageHeight,
                               int totalHeight) {
        Log.e(TAG, "updateNoteInfo() called with: noteId = " + noteId + ", pageWidth = "
                + pageWidth + ", pageHeight = " + pageHeight
                + ", totalHeight = " + totalHeight + "");
        //mScrollY =0;
        mPageWidth = pageWidth;
        mPageHeight = pageHeight;
        //if (mLayoutRect.bottom-mLayoutRect.top< mPageHeight){
        setMinimumHeight(mPageHeight);
        //}
        //Log.i(Note.TAG,"onLayout layoutRectHeight="+(mLayoutRect.bottom-mLayoutRect.top)+"; mPageHeight="+mPageHeight);
        if (mDrawBitmap != null) {
            mDrawBitmap.recycle();
            mDrawBitmap = null;
        }

        mDrawBitmap = Bitmap.createBitmap(mPageWidth, mPageHeight,
                Note.BMP_CONFIG);
        mDrawBitmap.eraseColor(Note.BACK_COLOR_VALUE);
        mCanvas = new Canvas(mDrawBitmap);
        mParcelCol.updateNoteInfo(noteId, mPageWidth, mPageHeight, totalHeight, this);
        Log.e(TAG, "updateNoteInfo: mParcelCol count = " + mParcelCol.getParcelCount());
        mIsNoteSwitching = false;
        invalidate();
    }

    public void setPenAttr(PenAttr attr) {
        if (mPenAttr.mPenWidth < PenAttr.MIN_PEN_WIDTH) {
            mPenAttr.mPenWidth = PenAttr.MIN_PEN_WIDTH;
        }
        mPenAttr = attr;
        mPenPaint.setColor(mPenAttr.mColor);
        mPenPaint.setStrokeWidth(mPenAttr.mPenWidth);

        if (Color.BLACK == mPenAttr.mColor) {
            Shader shader = new LinearGradient(0, 0, 300, 0, new int[]{
                    Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW}, null,
                    Shader.TileMode.MIRROR);
            mPenPaint.setShader(shader);
        } else {
            mPenPaint.setShader(null);
        }
        setPenMask();
    }

    private void setPenMask() {
        int w = Math.max(1, Math.min(3, mPenAttr.mPenWidth / 3));
        BlurMaskFilter maskFilter = new BlurMaskFilter(w,
                BlurMaskFilter.Blur.NORMAL);
        mPenPaint.setMaskFilter(maskFilter);
    }

    public PenAttr getPenAttr() {
        return mPenAttr;
    }

    /**
     * 是否删除数据库数据
     */
    public void clear(boolean isDeleteFromDB) {
        if (mDrawBitmap != null) {
            mDrawBitmap.eraseColor(Note.BACK_COLOR_VALUE);
            invalidate();
        }

        if (isDeleteFromDB) {
            mParcelCol.clear(Note.BACK_COLOR_VALUE);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // canvas.drawColor(Note.BACK_COLOR_VALUE);
//        Log.e(TAG, "onDraw: isNoteSwitching = " + mIsNoteSwitching);
        if (!mIsNoteSwitching) {
            if (mStateType == StateType.StateEraser) {// 橡皮擦
                if (mDrawBitmap != null) {
                    if (!mDrawPath.isEmpty()) {
                        mCanvas.drawPath(mDrawPath, mEraserPaint);
                    }
                    canvas.drawBitmap(mDrawBitmap, 0, mScrollY, mBitmapPaint);
                    //canvas.drawPath(mDrawPath, mEraserPaint);
                }
            } else {// 滚动, 画笔
                //TODO：内存泄露，OOM
                mParcelCol.update(mScrollY);
                mParcelCol.draw(canvas, false);
                if (mStateType == StateType.StateDraw && !mHasTouchUp) {
                    if (mDrawBitmap != null) {
                        if (!mDrawPath.isEmpty()) {
                            mDrawBitmap.eraseColor(Note.BACK_COLOR_VALUE);
                            mCanvas.drawPath(mDrawPath, mPenPaint);
                        }
                        canvas.drawBitmap(mDrawBitmap, 0, mScrollY,
                                mBitmapPaint);
//						if (mDrawPath.isEmpty() == false) {
//							canvas.drawPath(mDrawPath, mPenPaint);
//						}
                    }
                }
            }
        }
    }

    public void exit() {
        if (mDrawBitmap != null && !mDrawBitmap.isRecycled()) {
            mDrawBitmap.recycle();
            mDrawBitmap = null;
        }
        mParcelCol.exit();
    }

    @Override
    public void draw(Canvas canvas) {
        // Log.i("DraftDrawView",
        // "draw canvas.getHeight()="+canvas.getHeight());
        super.draw(canvas);
    }

    public void updateState(StateType stateType) {
        if (mStateType == stateType) {
            return;
        }

        if (stateType == StateType.StateDraw) {
//            Log.e(TAG, "updateState: dirty Rect = " + mDirtyRect.toString());
            if (mStateType == StateType.StateEraser) {
                correctDirtyRect(Note.ERASER_WIDTH);
                mParcelCol.bitmapToParcels(mDrawBitmap, mDirtyRect, true);
                mParcelCol.saveParcels();
            }
            if (mDrawBitmap != null) {
                mDrawBitmap.eraseColor(Note.BACK_COLOR_VALUE);
            }
            mDirtyRect.set(0, 0, 0, 0);
        } else if (stateType == StateType.StateMove) {
            if (mStateType == StateType.StateEraser) {
                correctDirtyRect(Note.ERASER_WIDTH);
                mParcelCol.bitmapToParcels(mDrawBitmap, mDirtyRect, true);
                mParcelCol.saveParcels();
            } else if (mStateType == StateType.StateDraw) {
                correctDirtyRect(mPenAttr.mPenWidth);
                mParcelCol.bitmapToParcels(mDrawBitmap, mDirtyRect, false);
                mParcelCol.saveParcels();
            }
            if (mDrawBitmap != null) {
                mDrawBitmap.eraseColor(Note.BACK_COLOR_VALUE);
            }
            mDirtyRect.set(0, 0, 0, 0);
        } else if (stateType == StateType.StateEraser) {
            if (mStateType == StateType.StateDraw) {
                correctDirtyRect(mPenAttr.mPenWidth);
                mParcelCol.bitmapToParcels(mDrawBitmap, mDirtyRect, false);
                mParcelCol.saveParcels();
            }
            if (mDrawBitmap != null) {
                mDrawBitmap.eraseColor(Note.BACK_COLOR_VALUE);
                mParcelCol.parcelsToBitmap(mDrawBitmap);
            }
            mDirtyRect.set(0, 0, 0, 0);
        }
        mDrawPath.reset();
        mStateType = stateType;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mStateType == StateType.StateMove || mIsNoteSwitching
                || mDrawBitmap == null) {
            return false;
        }

        int x = (int) event.getX();
        int y = (int) event.getY() - mScrollY;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mHasTouchUp = false;
                mDrawPath.reset();
                mCurPoint.x = x;
                mCurPoint.y = y;
                if (mDirtyRect.top == mDirtyRect.bottom) {
                    mDirtyRect.left = x;
                    mDirtyRect.top = y;
                    mDirtyRect.right = x;
                    mDirtyRect.bottom = y;
                } else {
                    if (mDirtyRect.left > x) {
                        mDirtyRect.left = x;
                    }
                    if (mDirtyRect.top > y) {
                        mDirtyRect.top = y;
                    }
                    if (mDirtyRect.right < x) {
                        mDirtyRect.right = x;
                    }
                    if (mDirtyRect.bottom < y) {
                        mDirtyRect.bottom = y;
                    }
                }
                mDrawPath.moveTo(x, y);
                mPathLength = 0;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                // mPathSize
                // > 30
                if (mDirtyRect.left > x) {
                    mDirtyRect.left = x;
                }
                if (mDirtyRect.top > y) {
                    mDirtyRect.top = y;
                }
                if (mDirtyRect.right < x) {
                    mDirtyRect.right = x;
                }
                if (mDirtyRect.bottom < y) {
                    mDirtyRect.bottom = y;
                }

                float dx = Math.abs(x - mCurPoint.x);
                float dy = Math.abs(y - mCurPoint.x);

                if (dx > 4 || dy > 4) {
                    if (mPathLength > 30000) {
                        if (mStateType == StateType.StateEraser) {
                            mCanvas.drawPath(mDrawPath, mEraserPaint);
                        } else {
                            mDrawBitmap.eraseColor(Note.BACK_COLOR_VALUE);
                            mCanvas.drawPath(mDrawPath, mPenPaint);
                            correctDirtyRect(mPenAttr.mPenWidth);
                            mParcelCol.bitmapToParcels(mDrawBitmap, mDirtyRect,
                                    false);
                            mDrawBitmap.eraseColor(Note.BACK_COLOR_VALUE);
                        }
                        mDrawPath.reset();
                        mDrawPath.moveTo(mLastPoint.x, mLastPoint.y);
                        mPathLength = 0;
                    }

                    mLastPoint.x = mCurPoint.x;
                    mLastPoint.y = mCurPoint.y;
                    mDrawPath.quadTo(mCurPoint.x, mCurPoint.y,
                            (x + mCurPoint.x) / 2, (y + mCurPoint.y) / 2);
                    mPathLength++;
                    mCurPoint.x = (int) event.getX();
                    mCurPoint.y = (int) event.getY() - mScrollY;

                    if (mStateType == StateType.StateEraser) {
                        // mCanvas.drawPath(mDrawPath, mEraserPaint);
                    } else {
                        // mDrawBitmap.eraseColor(Note.BACK_COLOR_VALUE);
                        // mCanvas.drawPath(mDrawPath, mPenPaint);
                    }
                    invalidate();
                }

                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mStateType == StateType.StateEraser) {
                    mDrawPath.reset();
                    return true;
                }
                if (mDirtyRect.left > x) {
                    mDirtyRect.left = x;
                }
                if (mDirtyRect.top > y) {
                    mDirtyRect.top = y;
                }
                if (mDirtyRect.right < x) {
                    mDirtyRect.right = x;
                }
                if (mDirtyRect.bottom < y) {
                    mDirtyRect.bottom = y;
                }
                correctDirtyRect(mPenAttr.mPenWidth);
                mHasTouchUp = true;
                mDrawBitmap.eraseColor(Note.BACK_COLOR_VALUE);
                mCanvas.drawPath(mDrawPath, mPenPaint);
                mParcelCol.bitmapToParcels(mDrawBitmap, mDirtyRect, false);
                mDrawBitmap.eraseColor(Note.BACK_COLOR_VALUE);
                invalidate();
                break;
            default:
                Log.e(TAG, "onTouchEvent: default: " + event.getAction());
        }

        return true;
    }

    private void correctDirtyRect(int expandWidth) {
        if (mDirtyRect.top == mDirtyRect.bottom) {
            return;
        }

        mDirtyRect.left -= expandWidth;
        if (mDirtyRect.left < 0) {
            mDirtyRect.left = 0;
        }

        mDirtyRect.top -= expandWidth;
        if (mDirtyRect.top < 0) {
            mDirtyRect.top = 0;
        }

        mDirtyRect.right += expandWidth;
        if (mDirtyRect.right > mPageWidth) {
            mDirtyRect.right = mPageWidth;
        }
        mDirtyRect.bottom += expandWidth;
        if (mDirtyRect.bottom > mPageHeight) {
            mDirtyRect.bottom = mPageHeight;
        }
    }

    public void setIsDiskTooSmall(boolean isDiskTooSmall) {
        mParcelCol.setIsDiskTooSmall(isDiskTooSmall);
    }

    public void setNoteSwitching(boolean isSwitching) {
        Log.e(TAG, "setNoteSwitching: note switching = false");
        mIsNoteSwitching = isSwitching;
        if (mIsNoteSwitching && mDrawBitmap != null) {
            if (mStateType == StateType.StateEraser) {
                correctDirtyRect(Note.ERASER_WIDTH);
                mParcelCol.bitmapToParcels(mDrawBitmap, mDirtyRect, true);
                mParcelCol.saveParcels();
            } else if (mStateType == StateType.StateDraw) {
                correctDirtyRect(mPenAttr.mPenWidth);
                mParcelCol.bitmapToParcels(mDrawBitmap, mDirtyRect, false);
                mParcelCol.saveParcels();
            }
        }
    }

    public void updateByScroll(int y) {
        mScrollY = y;
        invalidate();
        //Log.i(Note.TAG, "DraftDrawView update len= " + (y - mScrollY));
    }

    public void saveNote() {
        mParcelCol.saveParcels();
    }

    enum StateType {
        //抓手
        StateMove,
        //画画
        StateDraw,
        //橡皮擦
        StateEraser
    }

    public class PenAttr {
        public static final int MAX_PEN_WIDTH = 18;
        public static final int MIN_PEN_WIDTH = 2;
        public static final int DEFAULT_PEN_WIDTH = 4;
        public static final int DEFAULT_COLOR = 0xff1b1b1b;
        public int mStyle;
        public int mPenWidth;
        public int mColor;
        public int mWidthBarProgress = -1;
    }

}
