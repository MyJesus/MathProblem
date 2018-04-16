package com.readboy.mathproblem.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by oubin on 2017/9/14.
 */

public class SelectorTextView extends AppCompatTextView {
    private static final String TAG = "SelectorTextView";


    private StateListDrawable mDrwable;
    private int mWidth;
    private int mHeight;

    public SelectorTextView(Context context) {
        super(context);
    }

    public SelectorTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SelectorTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Drawable drawable = getBackground();
        if (drawable != null && drawable instanceof StateListDrawable) {
            mDrwable = (StateListDrawable) drawable;
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.e(TAG, "onLayout() called with: changed = " + changed + ", left = " + left +
                ", top = " + top + ", right = " + right + ", bottom = " + bottom + "");

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.e(TAG, "onDraw: baseLine = " + getBaseline());

    }

    @Override
    public int getBaseline() {
        int line = super.getBaseline();
        if (!isSelected()) {
            line = line +5;
        }
        Log.e(TAG, "getBaseline: line = " + line);
        return line;
    }
}
