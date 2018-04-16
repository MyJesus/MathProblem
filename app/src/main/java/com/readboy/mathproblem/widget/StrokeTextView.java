package com.readboy.mathproblem.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.support.v7.widget.AppCompatTextView;

import com.readboy.mathproblem.R;
import com.readboy.mathproblem.util.ViewUtils;

/**
 * Created by oubin on 2017/9/14.
 */

public class StrokeTextView extends AppCompatTextView {
    private static final String TAG = "StrokeTextView";

    private int mStrokeWidth;
    private int mStrokeColor = Color.BLUE;

    public StrokeTextView(Context context) {
        super(context);
        ViewUtils.setTypeface(context, this);
    }

    public StrokeTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StrokeTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.StrokeTextView);
        float size = getTextSize();
        mStrokeWidth = array.getDimensionPixelSize(R.styleable.StrokeTextView_strokeWidth, (int) (size / 5));
        mStrokeColor = array.getColor(R.styleable.StrokeTextView_strokeColor, mStrokeColor);
        array.recycle();
        ViewUtils.setTypeface(context, this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mStrokeWidth == 0) {
            super.onDraw(canvas);
            return;
        }
        Paint paint = getPaint();
        Paint.Style style = paint.getStyle();
        int color = paint.getColor();
        paint.setStrokeWidth(mStrokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(mStrokeColor);
        String text = getText().toString();
        canvas.drawText(text, (getWidth() - paint.measureText(text)) / 2, getBaseline(), paint);
        paint.setColor(color);
        paint.setStyle(style);
        super.onDraw(canvas);
    }
}
