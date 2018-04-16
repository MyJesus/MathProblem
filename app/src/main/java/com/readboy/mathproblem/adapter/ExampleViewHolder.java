package com.readboy.mathproblem.adapter;

import android.animation.LayoutTransition;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.readboy.mathproblem.R;
import com.readboy.mathproblem.http.response.ProjectEntity;
import com.readboy.mathproblem.util.ViewUtils;

/**
 * Created by oubin on 2017/9/8.
 */

public class ExampleViewHolder extends BaseViewHolder<ProjectEntity.Project.Example> {

    private TextView mExampleContent;
    private TextView mExampleSolution;
    private TextView mExampleAnswer;
    private CheckBox mSolutionExpandSwitch;
    private View mSolutionSpace;
//    private View mAnswerLine;

    public ExampleViewHolder(View itemView) {
        super(itemView);
//        setLayoutTransition2((ViewGroup) itemView.findViewById(R.id.example_content_parent));
//        setLayoutTransition2((ViewGroup) itemView.findViewById(R.id.answer_content_parent));

        mExampleContent = (TextView) itemView.findViewById(R.id.example_content);
        mSolutionExpandSwitch = (CheckBox) itemView.findViewById(R.id.solution_expand_switch);
        mSolutionExpandSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mSolutionSpace.setVisibility(View.VISIBLE);
                mExampleSolution.setVisibility(View.VISIBLE);
            } else {
                mSolutionSpace.setVisibility(View.GONE);
                mExampleSolution.setVisibility(View.GONE);
            }
        });
        mExampleSolution = (TextView) itemView.findViewById(R.id.example_solution);
        CheckBox mAnswerExpandSwitch = (CheckBox) itemView.findViewById(R.id.answer_expand_switch);
        mAnswerExpandSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mExampleAnswer.setVisibility(View.VISIBLE);
//                mAnswerLine.setVisibility(View.VISIBLE);
            } else {
                mExampleAnswer.setVisibility(View.GONE);
//                mAnswerLine.setVisibility(View.GONE);
            }
        });
        mExampleAnswer = (TextView) itemView.findViewById(R.id.example_answer);
        mSolutionSpace = itemView.findViewById(R.id.solution_space);
//        View mAnswerSpace = itemView.findViewById(R.id.answer_space);
//        View mSolutionLine = itemView.findViewById(R.id.solution_line);
//        mAnswerLine = itemView.findViewById(R.id.answer_line);

    }

    private void setLayoutTransition(ViewGroup viewGroup) {
        LayoutTransition transition = new LayoutTransition();
        transition.setDuration(200);
        viewGroup.setLayoutTransition(transition);
    }

    private void setLayoutTransition2(ViewGroup viewGroup) {
        LayoutTransition transition = new LayoutTransition();
        transition.setDuration(150);
        transition.setAnimator(LayoutTransition.CHANGE_DISAPPEARING, null);
        viewGroup.setLayoutTransition(transition);
    }

    @Override
    public void bindView(ProjectEntity.Project.Example data) {
        super.bindView(data);
        String index = String.valueOf(getAdapterPosition() + 1);
        if (TextUtils.isEmpty(data.getSolution())) {
            mSolutionExpandSwitch.setVisibility(View.GONE);
            mSolutionSpace.setVisibility(View.GONE);
        } else {
            mSolutionExpandSwitch.setVisibility(View.VISIBLE);
            mSolutionSpace.setVisibility(View.VISIBLE);
            ViewUtils.setText(data.getSolution(), mExampleSolution);
        }
        ViewUtils.setText("例" + index + "、", data.getContent(), mExampleContent);
        ViewUtils.setText(data.getAnswer(), mExampleAnswer);

    }
}
