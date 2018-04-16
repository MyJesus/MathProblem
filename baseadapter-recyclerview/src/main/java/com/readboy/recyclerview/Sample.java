package com.readboy.recyclerview;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.readboy.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 1 on 2016/8/30.
 */

public class Sample extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RecyclerView recyclerView = new RecyclerView(this);
        int layoutId = 0;
        final List<String> datas = new ArrayList<String>();
        final int viewId = 0;
        recyclerView.setAdapter(new CommonAdapter<String>(this,layoutId,datas) {
            @Override
            protected void convert(ViewHolder holder, String string, int position) {
                holder.setText(viewId,string);
            }
        });

        CommonAdapter<String> adapter = new CommonAdapter<String>(this, layoutId, datas) {
            @Override
            protected void convert(ViewHolder holder, String s, int position) {

            }
        };

        adapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, Object o, int position) {

            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, Object o, int position) {
                return false;
            }
        });


    }
}
