package com.readboy.mathproblem.widget;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by oubin on 2017/9/4.
 */

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    public static final int HORIZONTAL = LinearLayout.HORIZONTAL;
    public static final int VERTICAL = LinearLayout.VERTICAL;

    private int mSpace;

    /**
     * Current orientation. Either {@link #HORIZONTAL} or {@link #VERTICAL}.
     */
    private int mOrientation;

    public SpaceItemDecoration(int space) {
        this(HORIZONTAL, space);
    }

    public SpaceItemDecoration(int orientation, int space) {
        this.mOrientation = orientation;
        this.mSpace = space;
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
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {


    }
}
