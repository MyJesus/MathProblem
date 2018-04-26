package com.readboy.mathproblem.adapter;

import android.view.View;
import android.widget.CheckBox;

/**
 * Created by oubin on 2017/9/29.
 */

public abstract class CheckViewHolder<D> extends BaseViewHolder<D> {

    protected CheckBox mCheckBox;

    public CheckViewHolder(View itemView) {
        super(itemView);
    }

    public void bindView(int position, boolean isChecked, D d) {
        super.bindView(position, d);
//        if (mCheckBox != null) {
            mCheckBox.setTag(position);
            mCheckBox.setChecked(isChecked);
//        }
    }

//    public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener listener) {
//        if (mCheckBox != null) {
//            mCheckBox.setOnCheckedChangeListener(listener);
//        }
//    }

    public void setCheckOnClickListener(View.OnClickListener listener){
        mCheckBox.setOnClickListener(listener);
    }

}
