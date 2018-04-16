package com.readboy.textbook.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
/**
 * 限制了里面的scrollView的高度
 * @author lacheo
 */
public class LimitLinearLayout extends LinearLayout {

	private int mHeight = 0;
	private int mMinHeight = 200;

	public LimitLinearLayout(Context context) {
		this(context, null);
	}

	public LimitLinearLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public LimitLinearLayout(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		mHeight = dm.heightPixels / 2;
		mMinHeight = dm.heightPixels / 3;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		measureChildren(widthMeasureSpec, heightMeasureSpec);

		int height = 0;
		int width = MeasureSpec.getSize(widthMeasureSpec);
		for (int index = 0; index < getChildCount(); index++) {
			final View child = getChildAt(index);
			int cWidth = child.getMeasuredWidth();
			int cHeight = child.getMeasuredHeight();
			if (child instanceof ScrollView) {
				cHeight = cHeight > mHeight ? mHeight : cHeight;
				cHeight = cHeight < mMinHeight? mMinHeight : cHeight;
				child.measure(MeasureSpec.EXACTLY+cWidth, MeasureSpec.EXACTLY+cHeight);
			}
			height += cHeight;
		}
		setMeasuredDimension(width, height);
	}

}
