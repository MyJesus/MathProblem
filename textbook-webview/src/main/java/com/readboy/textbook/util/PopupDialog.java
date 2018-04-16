package com.readboy.textbook.util;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.readboy.textbookwebview.R;

public class PopupDialog extends DialogFragment
{

	public static final String TAG = "ExternalDialogFragment";

	FrameLayout content_layout;
	Fragment fragment;
	View view;

	public static PopupDialog newInstance(Fragment fragment)
	{
		PopupDialog dialog = new PopupDialog();
		dialog.fragment = fragment;
		return dialog;
	}

	public static PopupDialog newInstance(View view)
	{
		PopupDialog dialog = new PopupDialog();
		dialog.view = view;
		return dialog;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setStyle(STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_dialog_external, container, false);

		content_layout = (FrameLayout) rootView.findViewById(R.id.content_layout);

		if (view != null)
		{
			content_layout.addView(view);
		}
		else if (fragment != null)
		{
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.content_layout, fragment, TAG);
			ft.commitAllowingStateLoss();
		}

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		final Window window = getDialog().getWindow();
		window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
		window.setGravity(Gravity.BOTTOM);
		window.setWindowAnimations(R.style.PopupDialogAnimation);
		window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		window.setBackgroundDrawableResource(android.R.color.transparent);

		getView().getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener()
		{

			@Override
			public void onGlobalLayout()
			{
				if (getView() != null && getView().getViewTreeObserver() != null)
				{	
					if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN)
					{
						getView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
					}
					else
					{
						getView().getViewTreeObserver().removeGlobalOnLayoutListener(this);
					}
					if (mOnTreeObserver != null)
					{
						mOnTreeObserver.Observer(getView().getWidth(), getView().getHeight());
					}
				}
			}
		});
	}

	@Override
	public void onDismiss(DialogInterface dialog)
	{
		super.onDismiss(dialog);
		if (mOnTreeObserver != null)
		{
			mOnTreeObserver.Observer(0, 0);
		}		
	}

	//	@Override
	public void show(Activity activity, String tag)
	{
		show(activity.getFragmentManager(), tag);
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		if(view != null){
			
			content_layout.removeView(view);
		}
	}

	private OnTreeObserver mOnTreeObserver;
	

	public interface OnTreeObserver
	{
		public void Observer(int width, int height);
	}

	public void setOnTreeObserverListener(OnTreeObserver onTreeObserver)
	{
		mOnTreeObserver = onTreeObserver;
	}
}
