package com.readboy.mathproblem.dialog;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.readboy.mathproblem.R;
import com.readboy.mathproblem.adapter.FavoriteAdapter;
import com.readboy.mathproblem.db.Favorite;
import com.readboy.mathproblem.widget.LineItemDecoration;

/**
 * Created by oubin on 2017/9/5.
 */

public class FavoriteDialog extends BaseVideoDialog implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "FavoriteDialog";

    private RecyclerView mFavoriteList;
    private FavoriteAdapter mFavoriteAdapter;
    private Context mContext;

    public FavoriteDialog(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate: ");
        setContentView(R.layout.dialog_video_favorites);
        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        ((Activity) mContext).getLoaderManager().restartLoader(0, null, this);
        initView();
        initData();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        mFavoriteAdapter.setAllChecked(false);
    }

    @Override
    protected void initView() {
        super.initView();
        mFavoriteList = (RecyclerView) findViewById(R.id.favorite_list);
        mFavoriteList.setLayoutManager(new LinearLayoutManager(getContext()));
        mFavoriteList.addItemDecoration(new LineItemDecoration(LinearLayout.VERTICAL, 1,
                ContextCompat.getColor(getContext(), R.color.video_divider_color)));
        mFavoriteAdapter = new FavoriteAdapter(getContext());
        mFavoriteAdapter.setAllCheckedChangeListener(this);
        mFavoriteList.setAdapter(mFavoriteAdapter);
    }

    @Override
    protected void checkAll() {
        setAllChecked(true);
    }

    @Override
    protected void unCheckAll() {
        setAllChecked(false);
    }

    @Override
    protected boolean canShowDeleteDialog() {
        return mFavoriteAdapter.hasChecked();
    }

    private void initData() {

    }

    @Override
    public boolean deleteVideo() {
        return Favorite.delete(getContext().getContentResolver(), mFavoriteAdapter.getSelectedFavorites());
    }

    @Override
    public void deleteVideoAfter(boolean isSuccess) {

    }

    private void setAllChecked(boolean checked) {
        mFavoriteAdapter.setAllChecked(checked);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return Favorite.getFavoritesCursorLoader(getContext());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.e(TAG, "onLoadFinished: count = " + (data == null ? "null" : data.getCount()));
        int count = data == null ? 0 : data.getCount();
        updateCount(count);
        if (data != null) {
            if (data.getCount() == 0) {
                showEmptyContentView();
            } else {
                showRecyclerView();
            }
            mFavoriteAdapter.swapCursor(data);
        } else {
            showEmptyContentView();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.e(TAG, "onLoaderReset: ");
        mFavoriteAdapter.swapCursor(null);
    }

    @Override
    protected void showEmptyContentView() {
        super.showEmptyContentView();
        mFavoriteList.setVisibility(View.GONE);
    }

    private void showRecyclerView() {
        mEmptyContentTv.setVisibility(View.GONE);
        mFavoriteList.setVisibility(View.VISIBLE);
    }

    private void updateCount(int count) {
        mVideoCount.setText(getContext().getString(R.string.favorite_video_count, count));
        if (count == 0){
            mAllCheckedBox.setVisibility(View.GONE);
            mDeleteView.setVisibility(View.GONE);
        }else {
            mDeleteView.setVisibility(View.VISIBLE);
            mAllCheckedBox.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAllChecked(boolean isChecked) {
        mAllCheckedBox.setChecked(isChecked);
    }
}
