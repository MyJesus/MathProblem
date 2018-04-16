package com.readboy.mathproblem.widget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by oubin on 2017/9/4.
 */

public class LineItemDecoration extends RecyclerView.ItemDecoration {
    private static final String TAG = "LineItemDecoration";

    public static final int HORIZONTAL = LinearLayout.HORIZONTAL;
    public static final int VERTICAL = LinearLayout.VERTICAL;
    public static final int DEFAULT_COLOR = Color.GRAY;

    private int mSpace;
    private Paint mPaint;

    /**
     * Current orientation. Either {@link #HORIZONTAL} or {@link #VERTICAL}.
     */
    private int mOrientation;
    private boolean drawLastLine = true;

    public LineItemDecoration(int space) {
        this(HORIZONTAL, space);
    }

    public LineItemDecoration(int orientation, int space) {
        this(orientation, space, DEFAULT_COLOR);
    }

    public LineItemDecoration(int orientation, int space, int color) {
        this.mOrientation = orientation;
        this.mSpace = space;
        this.drawLastLine = true;
        mPaint = new Paint();
        mPaint.setColor(color);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) != 0) {
            if (mOrientation == HORIZONTAL) {
                outRect.set(0, mSpace, 0, 0);
            } else if (mOrientation == VERTICAL) {
                outRect.set(mSpace, 0, 0, 0);
            }
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getChildCount();
//        Log.e(TAG, "onDraw: drawLastLine = " + drawLastLine + ", childCount = " + childCount);
        if (!drawLastLine && childCount < 2) {
            return;
        }
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        if (!drawLastLine) {
            childCount--;
        }

        for (int i = 0; i < childCount; i++) {
            View view = parent.getChildAt(i);
            float top = view.getBottom();
            float bottom = view.getBottom() + mSpace;
            c.drawRect(left, top, right, bottom, mPaint);
        }
    }

    public void drawLastLine(boolean draw) {
        this.drawLastLine = draw;
    }
}
