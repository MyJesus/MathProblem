package com.readboy.mathproblem.test;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.readboy.mathproblem.R;

/**
 * Created by oubin on 2017/9/21.
 */

public class FindViewActivity extends Activity {

    private TextView mExerciseContent;
    private RadioButton mChoiceA;
    private RadioButton mChoiceB;
    private RadioButton mChoiceC;
    private RadioButton mChoiceD;
    private RadioGroup mChoices;
    private TextView mExerciseAnswer;
    private TextView mExerciseSolution;
    private LinearLayout mExerciseSolutionParent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_exercise);
        initView();

    }

    private void initView() {
        mExerciseContent = (TextView) findViewById(R.id.exercise_content);
        mChoiceA = (RadioButton) findViewById(R.id.choice_a);
        mChoiceB = (RadioButton) findViewById(R.id.choice_b);
        mChoiceC = (RadioButton) findViewById(R.id.choice_c);
        mChoiceD = (RadioButton) findViewById(R.id.choice_d);
        mChoices = (RadioGroup) findViewById(R.id.choices);
        mExerciseAnswer = (TextView) findViewById(R.id.exercise_answer);
        mExerciseSolution = (TextView) findViewById(R.id.exercise_solution);
        mExerciseSolutionParent = (LinearLayout) findViewById(R.id.exercise_solution_parent);
    }
}
