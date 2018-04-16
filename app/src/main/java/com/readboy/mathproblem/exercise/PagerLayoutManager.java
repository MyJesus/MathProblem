package com.readboy.mathproblem.exercise;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by oubin on 2017/9/25.
 * 类似ViewPager，只显示一个ItemView。
 * 扩展next, previous两个方法。
 */

public class PagerLayoutManager extends LinearLayoutManager {
    private static final String TAG = "oubin_LayoutManager";

    private int mCentralPosition = 0;

    public PagerLayoutManager(Context context) {
        super(context);
    }

    public PagerLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public PagerLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void scrollToPosition(int position) {
        super.scrollToPosition(position);
        Log.e(TAG, "scrollToPosition: position = " + position);
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        super.smoothScrollToPosition(recyclerView, state, position);
        Log.e(TAG, "smoothScrollToPosition: position = " + position);
    }

    @Override
    public void scrollToPositionWithOffset(int position, int offset) {
        super.scrollToPositionWithOffset(position, offset);
        Log.e(TAG, "scrollToPositionWithOffset() called with: position = " + position + ", offset = " + offset + "");
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
//        Log.e(TAG, "onScrollStateChanged: state = " + state);
        switch (state) {
            case RecyclerView.SCROLL_STATE_IDLE:
//                Log.e(TAG, "onScrollStateChanged: idle.");
                if (getItemCount() > 0) {
                    int lastPosition = mCentralPosition;
//                    mCentralPosition = findCentralPosition();
//                    Log.e(TAG, "onScrollStateChanged: findFirstCompletelyVisibleItemPosition = "
//                            + findFirstCompletelyVisibleItemPosition());
//                    Log.e(TAG, "onScrollStateChanged: findLastCompletelyVisibleItemPosition = "
//                            + findLastCompletelyVisibleItemPosition());
//                    Log.e(TAG, "onScrollStateChanged: findFirstVisibleItemPosition = " + findFirstVisibleItemPosition());
//                    Log.e(TAG, "onScrollStateChanged: findLastVisibleItemPosition = " + findLastVisibleItemPosition());
                    int first = findFirstVisibleItemPosition();
                    int last = findLastVisibleItemPosition();
                    if (first == last) {
                        mCentralPosition = first;
                        handlerPositionChangeEvent(lastPosition, mCentralPosition);
                    }
                }
                break;
            case RecyclerView.SCROLL_STATE_SETTLING:
                break;
            case RecyclerView.SCROLL_STATE_DRAGGING:
                break;
            default:
                Log.e(TAG, "onScrollStateChanged: other state = " + state);
                break;
        }
    }

    public void next() {
        int position = findCentralPosition();
        if (position < getItemCount() - 1) {
            scrollToPosition(position + 1);
            mCentralPosition = position + 1;
            handlerPositionChangeEvent(position, position + 1);
        } else {
            Log.e(TAG, "next: position = " + position);
        }
    }

    public void previous() {
        int position = findCentralPosition();
        if (position > 0) {
            scrollToPosition(position - 1);
            mCentralPosition = position - 1;
            handlerPositionChangeEvent(position, position - 1);
        } else {
            Log.e(TAG, "previous: position = " + position);
        }
    }

    private int findCentralPosition() {
        View view = findCentralView();
        if (view == null) {
            return 0;
        } else {
            return getPosition(findCentralView());
        }
    }

    private View findCentralView() {
        if (getOrientation() == LinearLayout.HORIZONTAL) {
            return findHorizontalCentralView();
        } else {
            return findVerticalCentralView();
        }
    }

    private View findHorizontalCentralView() {
        View view;
        int count = getChildCount();
        if (count == 0) {
            return null;
        }
        if (count == 1) {
            view = getChildAt(0);
        } else {
            view = getChildAt(count / 2 - 1);
            int centralY = getHeight() / 2;
            if (view != null && (view.getRight() > centralY)) {
                return view;
            }
            view = getChildAt(count / 2);
        }
        return view;
    }

    private View findVerticalCentralView() {
        View view;
        int count = getChildCount();
        Log.e(TAG, "findCentralView: child count = " + getChildCount() + ", item count = " + getItemCount());
        if (count == 1) {
            view = getChildAt(count - 1);
            return view;
        }
        view = getChildAt(count / 2 - 1);
        if (view == null) {
            Log.e(TAG, "findCentralView: view = null");
            return null;
        }
        int centralY = getHeight() / 2;
        if (view.getBottom() > centralY) {
            return view;
        }
        return getChildAt(count / 2);
    }

    private OnPositionChangeListener mPositionListener;

    private void handlerPositionChangeEvent(int lastPosition, int newPosition) {
        if (mPositionListener != null) {
            mPositionListener.onPositionChange(lastPosition, newPosition);
        }
    }

    public void setOnPositionChangeListener(OnPositionChangeListener listener) {
        this.mPositionListener = listener;
    }

    public interface OnPositionChangeListener {
        void onPositionChange(int lastPosition, int newPosition);
    }

}
