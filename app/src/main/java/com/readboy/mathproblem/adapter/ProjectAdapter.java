package com.readboy.mathproblem.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.readboy.mathproblem.R;
import com.readboy.mathproblem.bean.ProjectHolder;

import java.util.List;

/**
 * Created by oubin on 2017/9/1.
 */

public class ProjectAdapter extends BaseAdapter<ProjectHolder, ProjectViewHolder> {

    public ProjectAdapter(Context context) {
        super(context);
    }

    @Override
    public ProjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_project, parent, false);
        ProjectViewHolder holder = new ProjectViewHolder(view);
        holder.setOnInnerClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(ProjectViewHolder holder, int position) {
        holder.bindView(position, mDataList.get(position));
    }

    @Override
    public void update(List<ProjectHolder> list) {
        super.update(list);
//        if (mDataList.size() == 0) {
//            ToastUtils.show(mContext, "暂无数据");
//        } else {
//            ToastUtils.cancel();
//        }
    }
}
