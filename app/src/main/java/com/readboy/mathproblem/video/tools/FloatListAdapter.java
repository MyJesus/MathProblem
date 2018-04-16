package com.readboy.mathproblem.video.tools;

import java.util.ArrayList;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 选书界面，年级列表的Adapter
 * @author guh
 *
 */
public class FloatListAdapter extends BaseAdapter {
	private Context mContext;
	private int mItemSelIdx;
	private boolean mActive = false;
	
	private ArrayList<String> mPathLst = new ArrayList<>();

	public FloatListAdapter(Context context, ArrayList<String> lsts) {
		mContext = context;
		mItemSelIdx = lsts.size() - 1;
		mPathLst = lsts;
	}

	@Override
	public int getCount() {
		return mPathLst.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setSelIdx(int selIdx) {
		mItemSelIdx = selIdx;
		notifyDataSetChanged();
	}
	
	public void setActive(boolean active) {
		mActive = active;
	}
	
	public boolean getActive() {
		return mActive;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView imageView;
		if (convertView == null) {
			imageView = new TextView(mContext);
			
			String path = mPathLst.get(position);
			String str = path.substring(path.lastIndexOf("/")+1, path.length());
			imageView.setText(str);
			
			imageView.setHeight(128);
			imageView.setGravity(Gravity.CENTER);
		} else {
			imageView = (TextView) convertView;
		}
		
		if (mItemSelIdx == position) {
			imageView.setTextColor(0xffffffff);
		} else {
			imageView.setTextColor(0xff000000);
		}
		mActive = true;
		return imageView;
	}
}
