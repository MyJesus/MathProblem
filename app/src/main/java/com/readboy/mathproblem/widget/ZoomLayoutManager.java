package com.readboy.mathproblem.widget;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;


/**
 * Created by oubin on 2016/12/26.
 */
public class ZoomLayoutManager extends LinearLayoutManager {
    private static final String TAG = "ZoomLayoutManager";

    private static final float MIN_SCALE_X = 0.9f;
    private static final float MIN_ALPHA_SCALE = 0.9f;
    private int mPrePosition = 1;
    private int mFinalPosition = 1;
    private int centralY = 0;
    private int mScrollState = RecyclerView.SCROLL_STATE_IDLE;
    private boolean needGotoCenter = false;

    public ZoomLayoutManager(Context context) {
        super(context, LinearLayout.HORIZONTAL, false);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        if (getOrientation() == LinearLayout.HORIZONTAL) {
            zoomHorizontalViews();
        } else {
            zoomVerticalViews();
        }
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        Log.e(TAG, "scrollVerticallyBy: dy = " + dy);
        int i = super.scrollVerticallyBy(dy, recycler, state);
        if (getOrientation() == LinearLayout.VERTICAL) {
            zoomVerticalViews();
        }
        return i;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int i = super.scrollHorizontallyBy(dx, recycler, state);
        if (getOrientation() == LinearLayout.HORIZONTAL) {
            zoomHorizontalViews();
        }

        return i;
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        if (position < 0) {
            Log.e(TAG, "smoothScrollToPosition: IllegalArgumentException, position = " + position);
            return;
        }
        mFinalPosition = position;
        FlexibleSmoothScroller smoothScroller = new FlexibleSmoothScroller(recyclerView.getContext());
        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);
    }

    @Override
    public void onScrollStateChanged(int state) {
        mScrollState = state;
//        showStateLog(state);
        switch (state) {
            case RecyclerView.SCROLL_STATE_IDLE:
//                Log.e(TAG, "onScrollStateChanged: showDaysView ");
                needGotoCenter = true;
                break;
            case RecyclerView.SCROLL_STATE_SETTLING:
                //getItemCount = alarmCount + headerCount + footCount;
                //如果删除的是最后一个Item，则对mPrePosition进行调整;
                // 解决：删除后，点击最后一个item的repeat，不调用hideDaysView.
                needGotoCenter = false;
                if (mFinalPosition != mPrePosition) {
                    mPrePosition = mFinalPosition;
                }
                break;
            case RecyclerView.SCROLL_STATE_DRAGGING:
                break;
            default:
                break;
        }
    }

    private void showStateLog(int state) {
        switch (state) {
            case RecyclerView.SCROLL_STATE_IDLE:
                Log.e(TAG, "showStateLog: scroll state idle ");
                break;
            case RecyclerView.SCROLL_STATE_SETTLING:
                Log.e(TAG, "showStateLog: scroll state settling ");
                break;
            case RecyclerView.SCROLL_STATE_DRAGGING:
                Log.e(TAG, "showStateLog: scroll state dragging ");
                break;
            default:
                break;
        }
    }

    private void zoomHorizontalViews() {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            zoomHorizontalView(view);
        }
    }

    private void zoomHorizontalView(View view) {
        final int left = view.getLeft();
        final int width = view.getMeasuredWidth();
        float centerX = (getWidth() - width) / 2;
        // (x - c)^2 * 4 * (m - 1) / c^2  + 1
        float result = (float) (Math.pow((left - centerX), 2) * (MIN_SCALE_X - 1)
                / Math.pow(centerX, 2) + 1);
        float scale = result < MIN_SCALE_X ? MIN_SCALE_X : result;
//        Log.e(TAG, "zoomHorizontalView: left = " + left + ", centerX = " + centerX + "scale = " + scale);
        view.setScaleX(scale);
        view.setScaleY(scale);

    }

    private void zoomVerticalViews() {
        int count = getChildCount();
        Log.e(TAG, "zoomVerticalViews: count = " + count);
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            zoomVerticalView(view);
        }
    }

    private void zoomVerticalView(View view) {
        float scale = computeScaleX(view);
        Log.e(TAG, "zoomVerticalView: scaleX = " + String.valueOf(scale));
//        float alphaScale = computeAlpha(view);
//        float alphaScale = (scale - MIN_SCALE_X) * (1 - MIN_ALPHA_SCALE) / (1 - MIN_SCALE_X) + MIN_ALPHA_SCALE;
        view.setScaleX(scale);
        view.setScaleY(scale);
//        view.setTransitionAlpha(alphaScale);
    }

    private float computeScaleX(View view) {
        final int left = view.getLeft();
//        Log.e(TAG, "computeScale: top = " + top);
        final int width = view.getMeasuredWidth();
        float centerX = (getWidth() - width) / 2;
        float result = (float) (Math.pow((left - centerX), 2) * (MIN_SCALE_X - 1)
                / Math.pow(centerX, 2) + 1);
//        Log.e(TAG, "computeScale: scale = " + result);
        return result < MIN_SCALE_X ? MIN_SCALE_X : result;
    }

    private float computeScale(View view) {
        int left = view.getLeft();
        int itemHeight = view.getWidth();
        int height = getWidth();
        float scale = (-2 * itemHeight * itemHeight)
                * (left - height / 2 + itemHeight * 3 / 2) * (left - height / 2 - 3 * itemHeight / 2);
        return Math.min(1, Math.max(scale, 0));
    }


    private class FlexibleSmoothScroller extends LinearSmoothScroller {
        /**
         * 数值越大，变动也慢，滑动范围也越短
         */
        private static final float MILLISECONDS_PER_INCH = 100.0f;

        public FlexibleSmoothScroller(Context context) {
            super(context);
        }

        @Override
        protected void onStart() {
            super.onStart();
        }

        @Override
        public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
            return (int) ((boxEnd + boxStart) / 2.0f - (viewEnd + viewStart) / 2.0f);
        }

        @Override
        protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
            return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
        }

        @Override
        public PointF computeScrollVectorForPosition(int targetPosition) {
            if (getChildCount() == 0) {
                return null;
            }
            final int firstChildPos = getPosition(getChildAt(0));
            final int direction = targetPosition < firstChildPos ? -1 : 1;
            if (getOrientation() == HORIZONTAL) {
                return new PointF(direction, 0);
            } else {
                return new PointF(0, direction);
            }
        }

        /**
         * {@inheritDoc} 解决{@link #smoothScrollToPosition(RecyclerView, RecyclerView.State, int)}
         * 如果滑动到边沿，就没有正常执行全部scroll state
         */
        @Override
        protected void onTargetFound(View targetView, RecyclerView.State state, Action action) {
            final int dx = calculateDxToMakeVisible(targetView, getHorizontalSnapPreference());
            final int dy = calculateDyToMakeVisible(targetView, getVerticalSnapPreference());
            final int distance = (int) Math.sqrt(dx * dx + dy * dy);
            final int time = calculateTimeForDeceleration(distance);
            if (time > 0) {
                action.update(-dx, -dy, time, mDecelerateInterpolator);
            } else {
//                Log.e(TAG, "onTargetFound: time < 0  targetPos = " + state.getTargetScrollPosition());
                action.update(-dx, -dy, 2, mDecelerateInterpolator);
            }
        }
    }

    public void clearFinalPosition() {
        mFinalPosition = 0;
    }


}
