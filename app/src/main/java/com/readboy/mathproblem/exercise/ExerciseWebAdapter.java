package com.readboy.mathproblem.exercise;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.readboy.mathproblem.R;
import com.readboy.mathproblem.adapter.BaseAdapter;
import com.readboy.textbook.model.Question;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by oubin on 2017/9/7.
 */

public class ExerciseWebAdapter extends BaseAdapter<Question.Item, ExerciseWebViewHolder> {
    private static final String TAG = "ExerciseAdapter";

    /**
     * 是否展开答案和解析
     */
    private boolean showSolution;
    private List<ExerciseWebViewHolder> holderList = new ArrayList<>();

    public ExerciseWebAdapter(Context context) {
        super(context);
    }

    @Override
    public ExerciseWebViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.item_exercise2, parent, false);
        ExerciseWebViewHolder holder = new ExerciseWebViewHolder(itemView);
        //TODO: 该方式有待考量，有可能添加过多的无效holder.
        holderList.add(holder);
        Log.e(TAG, "onCreateViewHolder: ");
        return holder;
    }

    @Override
    public void onBindViewHolder(ExerciseWebViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Log.e(TAG, "onBindViewHolder: position = " + position);
//        holder.clearData();
        holder.bindView(mDataList.get(position), showSolution);
    }

    public void showSolution(boolean showSolution) {
        this.showSolution = showSolution;
    }

    public void clearData(){
//        holderList.forEach(ExerciseWebViewHolder::clearData);
        for (ExerciseWebViewHolder holder : holderList) {
            holder.clearData();
        }
    }

}
