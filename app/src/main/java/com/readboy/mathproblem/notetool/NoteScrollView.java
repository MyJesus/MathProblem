package com.readboy.mathproblem.notetool;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;


public class NoteScrollView extends ScrollView {
    private static final String TAG = "NoteScrollView";

    private static final int MSG_CHECK_SCROLL_STOP = 1;
    private static final int CHECK_SCROLL_TIME_LEN = 200;
    private boolean mIgnoreTouchEvent = false;
    private MsgHandler mMsgHandler;
    public int mScrollY;
    public int mLastScrollY;
    private ScrollProcess mMoveProcess;
    private boolean mIsScrollStop;
    //	private SimpleWordView mSimpleWordView;
    private float mScrollSpeedReduce = 1;

    public NoteScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public NoteScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NoteScrollView(Context context) {
        super(context);
        init();
    }

    public void setScrollSpeedReduce(float speedReduce) {
        mScrollSpeedReduce = speedReduce;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!mIgnoreTouchEvent) {
            if (ev.getAction() == MotionEvent.ACTION_UP) {
                mMsgHandler.sendEmptyMessageDelayed(MSG_CHECK_SCROLL_STOP, CHECK_SCROLL_TIME_LEN);
//                mMsgHandler.sendEmptyMessageDelayed(MSG_CHECK_SCROLL_STOP, CHECK_SCROLL_TIME_LEN);
            }
            return super.onTouchEvent(ev);
        } else {
            return false;
        }
    }

    private void init() {
        mIsScrollStop = true;
        mMsgHandler = new MsgHandler();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return !mIgnoreTouchEvent && super.onInterceptTouchEvent(ev);
    }

    public void ignoreTouchEvent(boolean ignore) {
        mIgnoreTouchEvent = ignore;
        if (mIgnoreTouchEvent) {
            this.setVerticalScrollBarEnabled(false);
            this.setEnabled(false);
        } else {
            this.setVerticalScrollBarEnabled(true);
            this.setEnabled(true);
        }
    }

    public void setScrollProcess(ScrollProcess func) {
        mMoveProcess = func;
    }

//	public void setSimpleView(SimpleWordView simleWordView){
//		mSimpleWordView = simleWordView;
//	}


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        mScrollY = t;
        //Log.i(Note.TAG, "DraftScroll onScrollChanged t= " + t);
        if (mMoveProcess != null) {
            mMoveProcess.move(t);
        }
//		if (mSimpleWordView !=null){
//			//Log.i("xxx", "mScrollY ="+mScrollY);
//			mSimpleWordView.swOnScrollListener(mScrollY);
//		}
    }


    public interface ScrollProcess {
        public void move(int y);
    }

    public boolean isScrollStop() {
        return mIsScrollStop;
    }

    private class MsgHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_CHECK_SCROLL_STOP) {
                if (mLastScrollY == mScrollY) {
                    mIsScrollStop = true;
                } else {
//                    Log.e(TAG, "handleMessage: mLastScrollY = " + mLastScrollY + ", mScrollY = " + mScrollY);
                    mIsScrollStop = false;
                    mLastScrollY = mScrollY;
                    sendEmptyMessageDelayed(MSG_CHECK_SCROLL_STOP, CHECK_SCROLL_TIME_LEN);
                }
            }
        }
    }

    @Override
    public void fling(int velocityY) {
        super.fling((int) (velocityY / mScrollSpeedReduce));
    }

}
