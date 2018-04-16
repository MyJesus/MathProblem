package com.readboy.mathproblem.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oubin on 2017/9/8.
 */

public abstract class BaseAdapter<E, VH extends BaseViewHolder<E>> extends RecyclerView.Adapter<VH>
        implements BaseViewHolder.OnInnerClickListener {

    private static final String TAG = BaseAdapter.class.getSimpleName();

    protected Context mContext;
    protected LayoutInflater mInflater;

    protected List<E> mDataList = new ArrayList<>();

    public BaseAdapter(Context context) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public abstract VH onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(VH holder, int position) {
        holder.bindView(mDataList.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    @Override
    public void onViewRecycled(VH holder) {
        super.onViewRecycled(holder);
        holder.recycler();
    }

    public void setData(List<E> list) {
        mDataList = list;
        notifyDataSetChanged();
    }

    public void update(List<E> list) {
        mDataList.clear();
        if (list != null) {
            mDataList.addAll(list);
        }
        notifyDataSetChanged();
    }

    @Override
    public void onInnerClick(int position, BaseViewHolder holder) {
        handlerItemClick(position, holder);
    }

    protected OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void handlerItemClick(int position, BaseViewHolder viewHolder) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(position, viewHolder);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, BaseViewHolder viewHolder);
    }

}
