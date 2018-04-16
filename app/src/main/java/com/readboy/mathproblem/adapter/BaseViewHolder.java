package com.readboy.mathproblem.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by oubin on 2017/9/8.
 */

public class BaseViewHolder<E> extends RecyclerView.ViewHolder {

    protected E mData;

    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    public void bindView(E data) {

    }

    public void bindView(int position, E data){

    }

    public void recycler() {

    }

    protected OnInnerClickListener mInnerClickListener;

    public void setOnInnerClickListener(OnInnerClickListener listener) {
        this.mInnerClickListener = listener;
    }

    protected void handlerInnerItemClickEvent() {
        if (mInnerClickListener != null) {
            mInnerClickListener.onInnerClick(getAdapterPosition(), this);
        }
    }

    /**
     * 用于反馈给Adapter的OnItemClickListener事件
     */
    public interface OnInnerClickListener {
        void onInnerClick(int position, BaseViewHolder holder);
    }

}
