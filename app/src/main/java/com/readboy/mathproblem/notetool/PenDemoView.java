package com.readboy.mathproblem.notetool;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Path;
import android.graphics.Shader;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.View;

import com.readboy.mathproblem.notetool.NoteDrawView.PenAttr;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class PenDemoView extends View {

    public static final int TYPE_UNKNOWN = -1;
    public static final int TYPE_POINT = 0;
    public static final int TYPE_LINE = 1;

    @IntDef({TYPE_UNKNOWN, TYPE_LINE, TYPE_POINT})
    @Retention(RetentionPolicy.SOURCE)
    @interface Type {
    }

    @Type
    private int mPenDemoType;
    private Paint mPaint;
    private PenAttr mPenAttr;
    private Path mCurvePath;
    private int mViewWidth;
    private int mViewHeight;
    private Bitmap mBitmap;

    public PenDemoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public PenDemoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PenDemoView(Context context) {
        super(context);
        init();
    }

    private void init() {
        mPenDemoType = TYPE_UNKNOWN;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCurvePath = new Path();
        mPaint.setStrokeCap(Cap.ROUND);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mViewWidth = right - left;
        mViewHeight = bottom - top;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mViewWidth > 0 && mViewHeight > 0) {
            if (mBitmap == null) {
                mBitmap = Bitmap.createBitmap(mViewWidth, mViewHeight,
                        Config.ARGB_8888);
            }
            mBitmap.eraseColor(0x00ffffff);
            drawToBitmap(mBitmap);
            canvas.drawBitmap(mBitmap, 0, 0, mPaint);
        }
    }

    private void drawToBitmap(Bitmap bitmap) {
        Canvas canvas = new Canvas();
        canvas.setBitmap(bitmap);

        int x, y;
        switch (mPenDemoType) {
            case TYPE_POINT:
                if (mPenAttr.mPenWidth <= 1) {
                    canvas.drawPoint(canvas.getWidth() / 2, canvas.getHeight() / 2,
                            mPaint);
                } else {
                    canvas.drawCircle(canvas.getWidth() / 2,
                            canvas.getHeight() / 2, mPenAttr.mPenWidth / 2, mPaint);
                }
                break;
            case TYPE_LINE:
                if (mCurvePath.isEmpty()) {
                    int width = canvas.getWidth();
                    int height = canvas.getHeight();
                    x = 24;

                    y = height / 2;
                    mCurvePath.moveTo(x, y);
                    width = width - 2 * x;
                    height = height - 20;
                    height = height / 2;

                    float x1, y1;
                    float lastX, lastY;
                    lastX = x;
                    lastY = y;
                    for (int i = 0; i < width; i++) {
                        x1 = x + i;
                        y1 = (float) (height * Math.sin(i * Math.PI / 180) + y);
                        mCurvePath.quadTo(lastX, lastY, x1, y1);
                        lastX = x1;
                        lastY = y1;
                    }
                }
                canvas.drawPath(mCurvePath, mPaint);
                break;
            case TYPE_UNKNOWN:
                break;
            default:
                break;
        }
    }

    public void exit() {
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }

    public void setPenDemoType(@Type int demoState, PenAttr attr) {
        mPenDemoType = demoState;
        mPenAttr = attr;
        if (mPenDemoType == TYPE_POINT) {
            mPaint.setStyle(Paint.Style.FILL);
        } else if (mPenDemoType == TYPE_LINE) {
            mPaint.setStyle(Paint.Style.STROKE);
        }

        if (0xff000000 == mPenAttr.mColor) {
            Shader shader = new LinearGradient(0, 0, 300, 0, new int[]{
                    Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW}, null,
                    Shader.TileMode.MIRROR);
            mPaint.setShader(shader);
        } else {
            mPaint.setShader(null);
        }

        int w = mPenAttr.mPenWidth / 3;
        if (w > 3) {
            w = 3;
        }
        if (w < 1) {
            w = 1;
        }
        // w=5;
        mPaint.setColor(mPenAttr.mColor);
        mPaint.setStrokeWidth(mPenAttr.mPenWidth);
        BlurMaskFilter maskFilter = new BlurMaskFilter(w,
                BlurMaskFilter.Blur.NORMAL);
        mPaint.setMaskFilter(maskFilter);
        mPaint.setAlpha(0xff);
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }

        invalidate();
    }


}
