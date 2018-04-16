package com.readboy.mathproblem.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.readboy.mathproblem.R;
import com.readboy.mathproblem.adapter.CheckAdapter;
import com.readboy.mathproblem.db.Video;
import com.readboy.mathproblem.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oubin on 2017/9/5.
 */

public abstract class BaseVideoDialog extends Dialog implements View.OnClickListener,
        CheckAdapter.OnAllCheckedChangeListener {
    private static final String TAG = "BaseVideoDialog";

    protected TextView mVideoCount;

    protected CheckBox mAllCheckedBox;
    protected TextView mEmptyContentTv;
    protected View mDeleteView;
    protected CommonDialog mDeleteAlertDialog;

    protected List<Video> mVideoList = new ArrayList<>();

    public BaseVideoDialog(@NonNull Context context) {
        super(context, R.style.TransparentDialog);
    }

    public BaseVideoDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void dismiss() {
        super.dismiss();
        mAllCheckedBox.setChecked(false);
    }

    protected void initView() {
        mVideoCount = (TextView) findViewById(R.id.video_count);
        mAllCheckedBox = (CheckBox) findViewById(R.id.all_checked_box);

        mAllCheckedBox.setOnClickListener(v -> {
            Log.e(TAG, "initView: isChecked = " + mAllCheckedBox.isChecked());
            if (mAllCheckedBox.isChecked()) {
                checkAll();
            } else {
                unCheckAll();
            }
        });
        findViewById(R.id.cancel).setOnClickListener(this);
        mDeleteView = findViewById(R.id.delete);
        mDeleteView.setOnClickListener(this);
        findViewById(R.id.dialog_close).setOnClickListener(this);
        mEmptyContentTv = (TextView) findViewById(R.id.empty_content);
    }

    protected abstract void checkAll();

    protected abstract void unCheckAll();

    protected abstract boolean canShowDeleteDialog();

    @Override
    protected void onStop() {
        super.onStop();
//        Log.e(TAG, "onStop: ");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_close:
                dismiss();
                break;
            case R.id.delete:
                if (canShowDeleteDialog()) {
                    showDeleteDialog();
                }else {
                    ToastUtils.show("未选中任何内容");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onAllChecked(boolean isChecked) {
        Log.e(TAG, "onAllChecked() called with: isChecked = " + isChecked + "");
        mAllCheckedBox.setChecked(isChecked);
    }

    /**
     * 显示“是否删除视频对话框”
     */
    private void showDeleteDialog() {
        if (mDeleteAlertDialog == null) {
            mDeleteAlertDialog = new CommonDialog(getContext());
            mDeleteAlertDialog.setContent("确定删除这些视频吗？");
            mDeleteAlertDialog.setOnClickListener(new BaseDialog.OnClickListener() {
                @Override
                public void onLeftClick(BaseDialog dialog) {
                    asyncDelete();
                    dialog.dismiss();
                }

                @Override
                public void onRightClick(BaseDialog dialog) {
                    dialog.dismiss();
                }
            });
        }
        mDeleteAlertDialog.show();
    }

    protected void showEmptyContentView() {
        mEmptyContentTv.setVisibility(View.VISIBLE);
        Log.e(TAG, "showEmptyContentView: ");
//        mAllCheckedBox.setChecked(false);
    }

    protected void dismissDeleteAlertDialog(){
        if (mDeleteAlertDialog != null && mDeleteAlertDialog.isShowing()){
            mDeleteAlertDialog.dismiss();
        }
    }

    /**
     * 异步删除，运行在子线程中
     *
     * @return return true if success, otherwise.
     * @see #asyncDelete()
     */
    public abstract boolean deleteVideo();

    /**
     * 删除文件后运行，运行在主线程中
     */
    public abstract void deleteVideoAfter(boolean isSuccess);

    private void asyncDelete() {
        final AsyncTask<Void, Void, Boolean> asyncTask = new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mDeleteView.setEnabled(false);
                ToastUtils.show(getContext(), "删除中");
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                return deleteVideo();
            }

            @Override
            protected void onPostExecute(Boolean success) {
                super.onPostExecute(success);
                mDeleteView.setEnabled(true);
                deleteVideoAfter(success);
                Log.e(TAG, "onPostExecute: ");
                mAllCheckedBox.setChecked(false);
                if (success) {
                    ToastUtils.show(getContext(), "删除成功");
                } else {
                    ToastUtils.show(getContext(), "删除失败");
                }
            }
        };
        asyncTask.execute();
    }

}
