package com.readboy.mathproblem.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.readboy.mathproblem.util.SizeUtils;

/**
 * Created by oubin on 2017/9/27.
 */

public class SolutionView extends FrameLayout {

    private static final float DEFAULT_SPACE = 40.0F;

    private float mSpace = 40.0F;
    private Paint mPaint;
    private int mLineWidth;
    private float mRadius = 1;
    private TextView mTextView;

    public SolutionView(Context context) {
        this(context, null);
        init(context);
    }

    public SolutionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init(context);
    }

    public SolutionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChild(mTextView, widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize = (int) (mTextView.getMeasuredHeight() + mSpace * 2);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int newHeightSpec = MeasureSpec.makeMeasureSpec(heightSpecSize, heightSpecMode);
        setMeasuredDimension(widthSize, heightSpecSize);
    }

    private void init(Context context) {
        mSpace = SizeUtils.dp2px(context, DEFAULT_SPACE);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(0xfff2b03f);
        mLineWidth = SizeUtils.dp2px(context, 1);

        mTextView = new TextView(context);
        mTextView.setTextSize(SizeUtils.sp2px(context, 32));
        mTextView.setTextColor(0xff484848);
        mTextView.setText("送到家啦分局公安额埃及哦v额阿飞的洛杉矶飞拉丝的金发绿色空间");
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = SizeUtils.dp2px(context, 35);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float starX = mRadius - mLineWidth / 2;
        int height = getMeasuredHeight();
        canvas.drawRect(starX, 0, starX + mLineWidth, height, mPaint);
        canvas.drawCircle(mRadius, height / 2, mRadius, mPaint);
    }
}
