package com.readboy.mathproblem.exercise;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.readboy.mathproblem.R;
import com.readboy.mathproblem.adapter.BaseViewHolder;
import com.readboy.mathproblem.http.HttpConfig;
import com.readboy.mathproblem.js.JsUtils;
import com.readboy.mathproblem.util.ViewUtils;

import java.util.List;

/**
 * Created by oubin on 2017/9/7.
 */

public class ExerciseViewHolder extends BaseViewHolder<Exercise> implements View.OnClickListener {
    private static final String TAG = "ExerciseViewHolder";

    private TextView mExerciseContent;
    //    private RadioButton mChoiceA;
//    private RadioButton mChoiceB;
//    private RadioButton mChoiceC;
//    private RadioButton mChoiceD;
    private RadioButton[] mChoicesArray;
    private RadioGroup mChoices;
    private TextView mExerciseAnswer;
    private TextView mExerciseSolution;
    private WebView mExerciseSolutionWebView;
    private LinearLayout mExerciseSolutionParent;
    private RadioButton mErrorAnswer;

    @SuppressLint("SetJavaScriptEnabled")
    public ExerciseViewHolder(View itemView) {
        super(itemView);
        mExerciseContent = (TextView) itemView.findViewById(R.id.exercise_content);
        mChoices = (RadioGroup) itemView.findViewById(R.id.choices);
        int count = mChoices.getChildCount();
        mChoicesArray = new RadioButton[count];
        for (int i = 0; i < count; i++) {
            mChoicesArray[i] = (RadioButton) mChoices.getChildAt(i);
            mChoicesArray[i].setOnClickListener(this);
        }
        mExerciseAnswer = (TextView) itemView.findViewById(R.id.exercise_answer);
        mExerciseSolution = (TextView) itemView.findViewById(R.id.exercise_solution);
        mExerciseSolutionWebView = (WebView) itemView.findViewById(R.id.exercise_solution_web_view);
        mExerciseSolutionWebView.getSettings().setJavaScriptEnabled(true);
        mExerciseSolutionParent = (LinearLayout) itemView.findViewById(R.id.exercise_solution_parent);
    }

    private void updateExerciseView(Exercise exercises) {
        ViewUtils.setText(getAdapterPosition() + 1 + ".", exercises.getContent(), mExerciseContent);
        List<String> options = exercises.getOptions();
        int count = Math.min(options.size(), mChoicesArray.length);
        for (int i = 0; i < count; i++) {
            String content = options.get(i);
            ViewUtils.setText(content, mChoicesArray[i]);
        }
        int myAnswer = exercises.getMyAnswer();
        Log.e(TAG, "updateExerciseView: myAnswer = " + myAnswer);
        if (myAnswer < 0) {
            mChoices.clearCheck();
        } else {
            mChoicesArray[myAnswer].setChecked(true);
        }
//        ViewUtils.setText(exercises.getSolution(), mExerciseSolution);
        mExerciseSolutionWebView.loadDataWithBaseURL(HttpConfig.RESOURCE_HOST,
                JsUtils.makeBaseHtmlText(exercises.getSolution()), "text/html", "UTF-8", "");
        ViewUtils.setText("正确答案: ", exercises.getCorrectAnswer(), mExerciseAnswer);
        if (exercises.isShowSolution()) {
            int correctAnswer = exercises.getCorrectAnswerIndex();
            if (correctAnswer != myAnswer) {
                if (myAnswer >= 0) {
                    mErrorAnswer = mChoicesArray[myAnswer];
                    mErrorAnswer.setSelected(true);
                }
                mChoicesArray[correctAnswer].setChecked(true);
            }
            for (RadioButton radioButton : mChoicesArray) {
                radioButton.setEnabled(false);
            }
            mExerciseSolutionParent.setVisibility(View.VISIBLE);
        } else {
            if (mErrorAnswer != null) {
                mErrorAnswer.setSelected(false);
                mErrorAnswer = null;
            }
            for (RadioButton radioButton : mChoicesArray) {
                radioButton.setEnabled(true);
            }
            mExerciseSolutionParent.setVisibility(View.GONE);
        }
    }

    @Override
    public void bindView(Exercise data) {
        super.bindView(data);
        updateExerciseView(data);
    }


    private OnCheckedListener mCheckedListener;

    private void handlerCheckedChangeEvent(int checkedIndex) {
        Log.e(TAG, "handlerCheckedChangeEvent() called with: checkedIndex = " + checkedIndex + "");
        if (mCheckedListener != null) {
            if (mChoicesArray[checkedIndex].isChecked()) {
                mCheckedListener.onCheckedChange(getAdapterPosition(), checkedIndex);
            } else {
                Log.e(TAG, "handlerCheckedChangeEvent: unchecked,  checkedIndex = " + checkedIndex);
            }
        }
    }

    public void setOnCheckListener(OnCheckedListener listener) {
        this.mCheckedListener = listener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.choice_a:
                handlerCheckedChangeEvent(0);
                break;
            case R.id.choice_b:
                handlerCheckedChangeEvent(1);
                break;
            case R.id.choice_c:
                handlerCheckedChangeEvent(2);
                break;
            case R.id.choice_d:
                handlerCheckedChangeEvent(3);
                break;
            default:
                Log.e(TAG, "ExerciseViewHolder: default = " + v.getId());
        }
    }

    public interface OnCheckedListener {
        void onCheckedChange(int parentIndex, int checkedIndex);
    }

}
