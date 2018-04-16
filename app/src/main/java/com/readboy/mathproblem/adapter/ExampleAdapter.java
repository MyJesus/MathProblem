package com.readboy.mathproblem.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.readboy.mathproblem.R;
import com.readboy.mathproblem.http.response.ProjectEntity;

/**
 * Created by oubin on 2017/9/8.
 */

public class ExampleAdapter extends BaseAdapter<ProjectEntity.Project.Example, ExampleWebViewHolder> {

    public ExampleAdapter(Context context) {
        super(context);
    }

    @Override
    public ExampleWebViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.item_example_web, parent, false);
        return new ExampleWebViewHolder(itemView);
    }

}
