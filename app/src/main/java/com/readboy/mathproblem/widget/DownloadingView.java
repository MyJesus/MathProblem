package com.readboy.mathproblem.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.readboy.mathproblem.util.SizeUtils;

/**
 * Created by oubin on 2017/9/14.
 * <p>
 * Android 自定义View 实现较美观的loading进度条的绘制
 * http://blog.csdn.net/RichieZhu/article/details/52863267
 */

public class DownloadingView extends AppCompatTextView {
    private static final String TAG = "DownloadingView";


    private static final int DELAY_MILLI = 200;
    private static final float PROGRESS_OFFSET = 0.1F;
    private static final float DEFAULT_STROKE_WIDTH = 2.0F;

    private Paint mPaint;
    private Paint mAnimatorPaint;
    private Path mPath;
    private float mStrokeWidth = 2.0F; //dp
    private int mStrokeColor = 0xFF32AB15;
    //    private int mStrokeColor = 0xffff0000;
    private int mLoadingColor = 0xFF66CC00;
    //    private int mLoadingColor = 0xff99ff00;
    private int mBackgroundColor = 0xFF99FF00;
//    private int mBackgroundColor = 0xff66cc00;

    private int mWidth;
    private int mHeight;
    //0-1
    private float mProgress;
    private boolean isRunning = true;

    public DownloadingView(Context context) {
        this(context, null);
    }

    public DownloadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DownloadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mBackgroundColor);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        mAnimatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mAnimatorPaint.setColor(mLoadingColor);
        mAnimatorPaint.setStyle(Paint.Style.FILL);

        mPath = new Path();
        mStrokeWidth = SizeUtils.dp2px(getContext(), DEFAULT_STROKE_WIDTH);
    }

    private void initPath() {
        mPath = new Path();
        float offset = mStrokeWidth / 2.0F;
        float radius = mHeight / 2 - offset;
        //TODO 没做兼容性处理，只适用于mWidth >= mHeight情况。
        mPath.addCircle(radius + offset, radius + offset, radius, Path.Direction.CW);
        RectF rect = new RectF(radius + offset, offset, mWidth - radius - offset, mHeight - offset);
        Path path2 = new Path();
        path2.addRect(rect, Path.Direction.CW);
        mPath.op(path2, Path.Op.UNION);
        Path path3 = new Path();
        path3.addCircle(mWidth - radius - offset, radius + offset, radius, Path.Direction.CW);
        mPath.op(path3, Path.Op.UNION);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        initPath();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBg(canvas);
        if (isRunning) {
            drawAnimation(canvas);
        }
        drawStroke(canvas);
        super.onDraw(canvas);
    }

    private void drawBg(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mBackgroundColor);
        canvas.drawPath(mPath, mPaint);
    }

    private void drawAnimation(Canvas canvas) {
//        canvas.save();
        Path path = new Path();
        Path test = new Path();
        test.addRect(mWidth / 2, 0, mWidth, mHeight, Path.Direction.CW);
//        canvas.drawPath(path, mAnimatorPaint);
        path.addRect(0, 0, mProgress * mWidth, mHeight, Path.Direction.CW);
        mProgress += PROGRESS_OFFSET;
        if (mProgress >= 1.0F + PROGRESS_OFFSET) {
            mProgress = 0;
        }
        path.op(mPath, Path.Op.REVERSE_DIFFERENCE);
        canvas.drawPath(path, mAnimatorPaint);
        postInvalidateDelayed(DELAY_MILLI);
//        canvas.restore();
    }

    private void drawStroke(Canvas canvas) {
        canvas.save();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mStrokeColor);
        canvas.drawPath(mPath, mPaint);
        canvas.restore();
    }

    public void start() {
        isRunning = true;
    }

    public void stop() {
        isRunning = false;
    }
}
