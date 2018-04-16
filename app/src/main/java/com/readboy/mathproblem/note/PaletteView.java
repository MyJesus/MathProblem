package com.readboy.mathproblem.note;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.readboy.mathproblem.util.SizeUtils;
import com.tencent.bugly.crashreport.CrashReport;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义控件三部曲之绘图篇(七)——Paint之函数大汇总,
 * http://blog.csdn.net/harvic880925/article/details/51010839
 */
public class PaletteView extends View {
    private static final String TAG = "DraftPaletteView";

    private static final int MAX_CACHE_STEP = 300;

    //画笔，绘画
    public static final int MODE_DRAW = 0;
    //抓手，移动画布
    public static final int MODE_MOVE = 1;
    //橡皮擦
    public static final int MODE_ERASER = 2;

    @IntDef({MODE_DRAW, MODE_MOVE, MODE_ERASER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MODE {
    }

    @MODE
    private int mMode = MODE_DRAW;

    private Paint mPaint;
    private Path mPath;
    private PenAttr mPenAttr;
    private float mLastX;
    private float mLastY;
    private Bitmap mBufferBitmap;
    private Canvas mBufferCanvas;

    private final List<DrawingInfo> mDrawingList = new ArrayList<>(MAX_CACHE_STEP);
    private final List<DrawingInfo> mRemovedList = new ArrayList<>(MAX_CACHE_STEP);
    private Xfermode mClearMode;
    private boolean mCanEraser;
    private Callback mCallback;

    public PaletteView(Context context) {
        this(context, null);
    }

    public PaletteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDrawingCacheEnabled(true);
        init(context);
    }

    public interface Callback {
        void onUndoRedoStatusChanged();
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    private void init(Context context) {
        mPenAttr = new PenAttr();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
//        mPaint.setFilterBitmap(true);
        //作用于拐角
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        //作用于线的开头结尾
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        //拐角更加圆滑
//        mPaint.setPathEffect(new CornerPathEffect(3));
        mPaint.setStrokeWidth(SizeUtils.dp2px(context, mPenAttr.mPenWidth));
        mPaint.setColor(mPenAttr.mColor);
        //设置边缘虚化，可缓解锯齿现象
//        setPaintMask();

        mClearMode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
    }

    private void setPaintMask() {
        int w = 1;
        BlurMaskFilter maskFilter = new BlurMaskFilter(w,
                BlurMaskFilter.Blur.NORMAL);
        mPaint.setMaskFilter(maskFilter);
    }

    private void initBuffer() {
        mBufferBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        mBufferCanvas = new Canvas(mBufferBitmap);
    }

    private abstract static class DrawingInfo {
        //TODO 查看Paint对象内存占用情况，多则修改为保存color，Xfermode等信息即可
        Paint paint;

        abstract void draw(Canvas canvas);
    }

    private static class PathDrawingInfo extends DrawingInfo {
        Path path;

        @Override
        void draw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }
    }

    @MODE
    public int getMode() {
        return mMode;
    }

    public void setMode(@MODE int mode) {
        if (mode != mMode) {
            mMode = mode;
            switch (mode) {
                case MODE_DRAW:
                    mPaint.setXfermode(null);
                    mPaint.setStrokeWidth(mPenAttr.mPenWidth);
                    break;
                case MODE_ERASER:
                    mPaint.setXfermode(mClearMode);
                    mPaint.setStrokeWidth(mPenAttr.mEraserWidth);
                    break;
                case MODE_MOVE:
                    break;
                default:
                    break;
            }
        }
    }

    public void setEraserWidth(float size) {
        mPenAttr.mEraserWidth = size;
    }

    public void setPenWidth(float size) {
        mPenAttr.mPenWidth = size;
    }

    public void setPenColor(int color) {
        mPenAttr.mColor = color;
        mPaint.setColor(color);
    }

    public void setPenAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    private void reDraw() {
        if (mDrawingList != null) {
            if (mBufferBitmap != null) {
                mBufferBitmap.eraseColor(Color.TRANSPARENT);
            }
            for (DrawingInfo drawingInfo : mDrawingList) {
                if (drawingInfo != null) {
                    drawingInfo.draw(mBufferCanvas);
                } else {
                    CrashReport.postCatchedException(new Throwable("reDraw: mDrawingList has a null " +
                            "DrawingInfo, size = " + mDrawingList));
                }
            }
            invalidate();
        }
    }

    public boolean canRedo() {
        return mRemovedList != null && mRemovedList.size() > 0;
    }

    public boolean canUndo() {
        return mDrawingList != null && mDrawingList.size() > 0;
    }

    //重做
    public void redo() {
        int size = mRemovedList == null ? 0 : mRemovedList.size();
        Log.e(TAG, "redo: size = " + size);
        if (size > 0) {
            DrawingInfo info = mRemovedList.remove(size - 1);
            if (info != null) {
                mDrawingList.add(info);
            }
            mCanEraser = true;
            reDraw();
            if (mCallback != null) {
                mCallback.onUndoRedoStatusChanged();
            }
        }
    }

    //撤销，
    public void undo() {
        int size = mDrawingList == null ? 0 : mDrawingList.size();
        Log.e(TAG, "undo: size = " + size);
        if (size > 0) {
            DrawingInfo info = mDrawingList.remove(size - 1);
            if (size == 1) {
                mCanEraser = false;
            }
            if (info != null) {
                mRemovedList.add(info);
            } else {
                CrashReport.postCatchedException(new NullPointerException("undo: mDrawingList " +
                        "remove a null info, size = " + size));
            }
            reDraw();
            if (mCallback != null) {
                mCallback.onUndoRedoStatusChanged();
            }
        }
    }

    public void clear() {
        if (mBufferBitmap != null) {
            if (mDrawingList != null) {
                mDrawingList.clear();
            }
            if (mRemovedList != null) {
                mRemovedList.clear();
            }
            mCanEraser = false;
            mBufferBitmap.eraseColor(Color.TRANSPARENT);
            invalidate();
            if (mCallback != null) {
                mCallback.onUndoRedoStatusChanged();
            }
        }
    }

    public Bitmap buildBitmap() {
        Bitmap bm = getDrawingCache();
        Bitmap result = Bitmap.createBitmap(bm);
        destroyDrawingCache();
        return result;
    }

    private void saveDrawingPath() {
        if (mDrawingList.size() == MAX_CACHE_STEP) {
            mDrawingList.remove(0);
        }
        Path cachePath = new Path(mPath);
        Paint cachePaint = new Paint(mPaint);
        PathDrawingInfo info = new PathDrawingInfo();
        info.path = cachePath;
        info.paint = cachePaint;
        mDrawingList.add(info);
        mCanEraser = true;
        if (mCallback != null) {
            mCallback.onUndoRedoStatusChanged();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBufferBitmap != null) {
            canvas.drawBitmap(mBufferBitmap, 0, 0, null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction() & MotionEvent.ACTION_MASK;
        final float x = event.getX();
        final float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastX = x;
                mLastY = y;
                if (mPath == null) {
                    mPath = new Path();
                }
                mPath.moveTo(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                //这里终点设为两点的中心点的目的在于使绘制的曲线更平滑，如果终点直接设置为x,y，效果和lineto是一样的,实际是折线效果
                mPath.quadTo(mLastX, mLastY, (x + mLastX) / 2, (y + mLastY) / 2);
                if (mBufferBitmap == null) {
                    initBuffer();
                }
                if (mMode == MODE_ERASER && !mCanEraser) {
                    break;
                }
                mBufferCanvas.drawPath(mPath, mPaint);
                invalidate();
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
                if (mMode == MODE_DRAW || mCanEraser) {
                    saveDrawingPath();
                }
                mPath.reset();
                mRemovedList.clear();
                break;
            default:
                break;
        }
        return true;
    }

    public PenAttr getPenAttr() {
        return mPenAttr.clone();
    }

    public class PenAttr implements Cloneable {
        public static final int MAX_PEN_WIDTH = 18;
        public static final int MIN_PEN_WIDTH = 1;
        //单位dp
        public static final float DEFAULT_PEN_WIDTH = 2;
        public static final float DEFAULT_ERASER_WIDTH = 6;
        public static final int DEFAULT_COLOR = Color.BLACK;
        public int mStyle;
        public float mPenWidth = DEFAULT_PEN_WIDTH;
        public float mEraserWidth = DEFAULT_ERASER_WIDTH;
        public int mColor = DEFAULT_COLOR;
        public int mWidthBarProgress = -1;

        //此方法为浅克隆，因为成员变量都是基本数据类型，所以已满足要求
        @Override
        public PenAttr clone() {
            try {
                return (PenAttr) super.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                Log.e("PaletteView", "clone: ", e);
                return null;
            }
        }

    }

}
