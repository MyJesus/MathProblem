package com.readboy.mathproblem.exercise;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import com.readboy.mathproblem.R;
import com.readboy.mathproblem.application.Constants;
import com.readboy.mathproblem.util.BitmapUtils;

import java.text.DecimalFormat;
import java.util.Locale;

/**
 * Created by oubin on 2017/9/11.
 */

public class ExerciseResultDialog extends Dialog implements View.OnClickListener {
    private static final String TAG = "ExerciseResultDialog";

    private LinearLayout mProjectScoreGroup;
    private TextView mExerciseResultRate;
    private TextView mExerciseResultTime;
    private Space mSpace;
    /**
     * 看视频
     */
//    private Button mExerciseResultVideo;
//    private Button mExerciseResultCancel;
    private ImageView mExerciseResultMaster;

    private ExerciseResult mResult;
    private OnClickListener mOnClickListener;

    public ExerciseResultDialog(@NonNull Context context, ExerciseResult result) {
        super(context, R.style.TransparentDialog);
        this.mResult = result;
        Log.e(TAG, "ExerciseResultDialog: result =" + result.toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_exercise_result);
        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        assignView();
        initView();
        setCancelable(false);
        Log.e(TAG, "onCreate: ");
    }

    private void assignView() {
        mProjectScoreGroup = (LinearLayout) findViewById(R.id.project_score_group);
        mExerciseResultRate = (TextView) findViewById(R.id.exercise_result_rate);
        mExerciseResultTime = (TextView) findViewById(R.id.exercise_result_time);
        //看解析
        findViewById(R.id.exercise_result_explain).setOnClickListener(this);
        //看视频
        findViewById(R.id.exercise_result_video).setOnClickListener(this);
        //返回
        findViewById(R.id.exercise_result_cancel).setOnClickListener(this);
        mSpace = (Space) findViewById(R.id.space);
        mExerciseResultMaster = (ImageView) findViewById(R.id.exercise_result_master);
    }

    private void initView() {
        int count = mProjectScoreGroup.getChildCount();
        float rate = mResult.getCorrectRate();
        for (int i = 0; i < count; i++) {
            View view = mProjectScoreGroup.getChildAt(i);
            if (rate >= 0.2F) {
                view.setBackgroundResource(BitmapUtils.getResID(getContext(),
                        Constants.Drawable.STAR_POSITIVE_BIG, i));
            } else {
                break;
            }
            rate = rate - 0.2F;
        }

        DecimalFormat format = new DecimalFormat("0%");
        Log.e(TAG, "initView: format = " + format.format(mResult.getCorrectRate()));
        mExerciseResultRate.setText(format.format(mResult.getCorrectRate()));
        int minute = (int) (mResult.getTime() / 60);
        int second = (int) (mResult.getTime() % 60);
        mExerciseResultTime.setText(String.format(Locale.SIMPLIFIED_CHINESE, "%02d", minute)
                + ":" + String.format(Locale.SIMPLIFIED_CHINESE, "%02d", second));
        //去掉看视频按钮
//        if (mResult.isHasVideo()) {
//            mExerciseResultVideo.setVisibility(View.VISIBLE);
//            mSpace.setVisibility(View.GONE);
//        }
        if (mResult.getCorrectRate() >= 1) {
            mExerciseResultMaster.setImageResource(R.drawable.project_master);
        } else {
            mExerciseResultMaster.setImageResource(R.drawable.project_not_master);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.exercise_result_explain:
                handlerExplainClick();
                break;
            case R.id.exercise_result_video:
                handlerVideoClick();
                break;
            case R.id.exercise_result_cancel:
                handlerCancelClick();
                break;
            default:
                break;
        }
    }

    public void setOnCLickListener(OnClickListener listener) {
        this.mOnClickListener = listener;
    }

    private void handlerExplainClick() {
        if (mOnClickListener != null) {
            mOnClickListener.onExplainClick(this);
        }
        dismiss();
    }

    private void handlerVideoClick() {
        if (mOnClickListener != null) {
            mOnClickListener.onVideoClick(this);
        }
        dismiss();
    }

    private void handlerCancelClick() {
        if (mOnClickListener != null) {
            mOnClickListener.onCancelClick(this);
        }
        dismiss();
    }

    public interface OnClickListener {

        /**
         * 左边，“看解析”点击事件
         */
        void onExplainClick(ExerciseResultDialog dialog);

        /**
         * 中间，“看视频”点击事件
         */
        void onVideoClick(ExerciseResultDialog dialog);

        /**
         * 右边，“返回”点击事件
         * @param dialog
         */
        void onCancelClick(ExerciseResultDialog dialog);
    }

}
