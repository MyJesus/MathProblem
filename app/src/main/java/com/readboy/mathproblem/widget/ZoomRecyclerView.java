package com.readboy.mathproblem.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.Scroller;

/**
 * Created by oubin on 2016/12/27.
 */

public class ZoomRecyclerView extends RecyclerView {
    private static final String TAG = "ZoomRecyclerView";

    public static final int HORIZONTAL = LinearLayout.HORIZONTAL;
    public static final int VERTICAL = LinearLayout.VERTICAL;
    /**
     * 摩擦因子，控制fling滑动效果
     * 值越大，越容易停
     */
    private static final int FRICTION = 500;

    private Scroller mScroller;
    private int mCentralY;
    private int mCentralX;
    private int mOrientation = HORIZONTAL;

    public ZoomRecyclerView(Context context) {
        super(context);
        init(context);
    }

    public ZoomRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mScroller = new Scroller(context, new DecelerateInterpolator(), true);
        this.setItemAnimator(new CustomItemAnimator());
//        this.setItemAnimator(null);
    }

    public void setOrientation(int orientation) {
        this.mOrientation = orientation == VERTICAL ? VERTICAL : HORIZONTAL;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCentralY = h / 2;
        mCentralX = w / 2;
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {
//        Log.e(TAG, "fling: velocityX = " + velocityX + "  Y = " + velocityY);
        int count = getAdapter().getItemCount();
        int curPosition = getChildLayoutPosition(getCentralView());
        if (mOrientation == VERTICAL) {
            mScroller.fling(0, 0, 0, velocityY, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
        } else {
            mScroller.fling(0, 0, velocityX, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
        }
        int offsetPos;
        if (mOrientation == VERTICAL) {
            offsetPos = mScroller.getFinalY() / FRICTION;
        } else {
            offsetPos = mScroller.getFinalX() / FRICTION;
        }
        offsetPos = offsetPos > 0 ? Math.min(offsetPos, 5) : Math.max(offsetPos, -5);
        Log.e(TAG, "fling: offsetPos = " + offsetPos + " curPosition = " + curPosition);
        //因为有header，所以最小值为1， 因为有footer，所以最大值为getAdapter().getItemCount() - 2
        int finalPosition = Math.min(count, Math.max(1, curPosition + offsetPos));
        Log.e(TAG, "fling: finalPosition = " + finalPosition);
        smoothScrollToPosition(finalPosition);
        return true;
//        return super.fling(velocityX, velocityY);
    }

    private View getCentralView() {
        int count = getChildCount();
        View view1 = getChildAt(count / 2 - 1);
        if (mOrientation == VERTICAL) {
            if (mCentralY == 0) {
                mCentralY = getHeight() / 2;
            }
            if (view1.getBottom() > mCentralY) {
                return view1;
            }
        } else {
            if (mCentralX == 0) {
                mCentralX = getWidth() / 2;
            }
            if (view1.getRight() > mCentralX) {
                return view1;
            }
        }
        return getChildAt(count / 2);
    }

    /**
     * 用于处理删除闹钟后，重复时间daysOfWeek的显隐问题
     */
    private class CustomItemAnimator extends DefaultItemAnimator {

        @Override
        public void onRemoveStarting(ViewHolder item) {
            super.onRemoveStarting(item);
//            ZoomRecyclerView.this.getLayoutManager().onScrollStateChanged(SCROLL_STATE_DRAGGING);
        }

        @Override
        public void onRemoveFinished(ViewHolder item) {
            super.onRemoveFinished(item);
            getLayoutManager().onScrollStateChanged(SCROLL_STATE_IDLE);
        }

        @Override
        public boolean animateRemove(ViewHolder holder) {
            return super.animateRemove(holder);
        }
    }

}
