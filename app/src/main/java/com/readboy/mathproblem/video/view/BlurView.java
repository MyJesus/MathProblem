package com.readboy.mathproblem.video.view;

import com.readboy.mathproblem.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class BlurView extends AppCompatImageView {
	
	public BlurView(Context context) {
		super(context);
		Log.e("BlurView", "-------- Constructer 00 ");
	}

	public BlurView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.e("BlurView", "-------- Constructer 11 ");
	}
	
	public BlurView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		Log.e("BlurView", "-------- Constructer 22 ");
	}
	
//	public BlurView(Context context, AttributeSet attrs, int defStyleAttr,
//			int defStyleRes) {
//		super(context, attrs, defStyleAttr, defStyleRes);
//		Log.e("BlurView", "-------- Constructer 33 ");
//	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		Log.e("BlurView", "-------- onDraw() ");
		bluring();
	}



	/**
	 * 初始化BitmapShader
	 */
	public void bluring() {
		Log.e("BlurView", "-------- blur 144");
		Drawable drawable = getDrawable();
		if (drawable == null) {
			drawable = ActivityCompat.getDrawable(getContext(), R.drawable.blur_white);
			Log.e("", "-------- blur 33 ");
		}
		Log.e("", "-------- blur 11");
		Bitmap bmp;
		if (drawable instanceof BitmapDrawable) {
			BitmapDrawable bd = (BitmapDrawable) drawable;
			bmp =  bd.getBitmap();
			Log.e("", "-------- blur 00");
			Log.e("", "-------- blur 22");
			blur(bmp, this, 20);
		} 

	}
	
	private void blur(Bitmap bkg, View view, float radius) {
        Bitmap overlay = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.drawBitmap(bkg, -view.getLeft(), -view.getTop(), null);
        RenderScript rs = RenderScript.create(getContext());
        Allocation overlayAlloc = Allocation.createFromBitmap(rs, overlay);
        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, overlayAlloc.getElement());
        blur.setInput(overlayAlloc);
        blur.setRadius(radius);
        blur.forEach(overlayAlloc);
        overlayAlloc.copyTo(overlay);
        view.setBackground(new BitmapDrawable(getResources(), overlay));
        rs.destroy();
    }

}
