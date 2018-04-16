package com.readboy.mathproblem.exercise;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.readboy.mathproblem.R;
import com.readboy.mathproblem.adapter.BaseAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by oubin on 2017/9/7.
 */

public class ExerciseAdapter extends BaseAdapter<Exercise, ExerciseViewHolder>
        implements ExerciseViewHolder.OnCheckedListener {
    private static final String TAG = "ExerciseAdapter";


    public ExerciseAdapter(Context context) {
        super(context);
    }

    @Override
    public ExerciseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.item_exercise, parent, false);
        ExerciseViewHolder holder = new ExerciseViewHolder(itemView);
        holder.setOnCheckListener(this);
        return holder;
    }

    @Override
    public void onCheckedChange(int parentIndex, int checkedIndex) {
        Log.e(TAG, "onCheckedChange() called with: parentIndex = " + parentIndex + ", checkedIndex = " + checkedIndex + "");
        mDataList.get(parentIndex).setMyAnswer(checkedIndex);
    }

    public float getCorrectRate() {
        List<Exercise> exerciseList = new ArrayList<>(mDataList);
        int count = exerciseList.size();
        float correctCount = 0;
        for (Exercise exercise : exerciseList) {
            Log.e(TAG, "getCorrectRate: myAnswer = " + exercise.getMyAnswer());
            if (exercise.getCorrectAnswerIndex() == exercise.getMyAnswer()) {
                correctCount++;
            }
        }
        return correctCount / count;
    }

    public boolean isFinishExercise() {
        List<Exercise> exercises = new ArrayList<>(mDataList);
        for (Exercise exercise : exercises) {
            if (exercise.getMyAnswer() == Exercise.INVALID_ANSWER) {
                return false;
            }
        }
        return true;
    }

}
