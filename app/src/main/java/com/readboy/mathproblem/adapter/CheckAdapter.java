package com.readboy.mathproblem.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by oubin on 2017/9/29.
 * 或者使用Adapter.getItemCount(), 是否可靠，发生的先后顺序，待测试。
 */

public abstract class CheckAdapter<D, VH extends CheckViewHolder<D>> extends BaseAdapter<D, VH> implements
        View.OnClickListener {
    private static final String TAG = "CheckAdapter";

    //是不是效率更高，不用拆箱，装箱
    boolean[] mSelectedArray;
    //    private SparseBooleanArray mSelectedSparse = new SparseBooleanArray();
    private boolean isAllChecked = false;
    private final RecyclerView.AdapterDataObserver mObserver;
    private boolean isUpdateCheckBox = false;

    public CheckAdapter(Context context) {
        super(context);
        mObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
//                Log.e(TAG, "onChanged: isUpdateCheckBox = " + isUpdateCheckBox);
                if (!isUpdateCheckBox) {
                    initSelectedArray();
                }
                isUpdateCheckBox = false;
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                Log.e(TAG, "onItemRangeInserted() called with: positionStart = " + positionStart
                        + ", itemCount = " + itemCount + "");
                initSelectedArray();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                Log.e(TAG, "onItemRangeRemoved() called with: positionStart = " + positionStart
                        + ", itemCount = " + itemCount + "");
                initSelectedArray();
            }
        };
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        super.onBindViewHolder(holder, position);
//        Log.e(TAG, "onBindViewHolder: mSelected length = " + mSelectedArray.length
//                + ", position = " + position + ", data size = " + mDataList.size());
        holder.bindView(position, mSelectedArray[position], mDataList.get(position));
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        registerAdapterDataObserver(mObserver);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        unregisterAdapterDataObserver(mObserver);
    }


    @Override
    public void update(List<D> list) {
        super.update(list);
        initSelectedArray();
    }

    @Override
    public void setData(List<D> list) {
        super.setData(list);
        initSelectedArray();
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        setChecked(position, ((CompoundButton) v).isChecked());
        handlerAllCheckedChange(isAllChecked());
    }

    protected void initSelectedArray() {
        mSelectedArray = new boolean[getItemCount()];
        handlerAllCheckedChange(false);
    }

    public void setChecked(int position, boolean isChecked) {
        if (0 <= position && position < mSelectedArray.length) {
            mSelectedArray[position] = isChecked;
//            mSelectedSparse.put(position, isChecked);
        }
    }

    /**
     * 调用该方法后，切记不可外部调用notifyDataSetChanged().
     * 会导致全部为非选择状态。
     * {@link #mObserver}
     */
    public void setAllChecked(boolean checked) {
        isAllChecked = checked;
        if (mSelectedArray == null){
            mSelectedArray = new boolean[getItemCount()];
        }
        int size = mSelectedArray.length;
        for (int i = 0; i < size; i++) {
            setChecked(i, checked);
        }
        isUpdateCheckBox = true;
        notifyDataSetChanged();
    }

    public void setAllChecked() {
        setAllChecked(!isAllChecked());
    }

    public boolean hasChecked() {
        for (boolean b : mSelectedArray) {
            if (b) {
                return true;
            }
        }
        return false;
    }

    private boolean isAllChecked() {
        for (boolean b : mSelectedArray) {
            if (!b) {
                return false;
            }
        }
        return true;
    }

    public boolean[] getSelectedArray() {
        return Arrays.copyOf(mSelectedArray, mSelectedArray.length);
    }

    //
    public List<Integer> getSelectedPosition() {
        List<Integer> positions = new ArrayList<>();
        for (int i = 0, length = mSelectedArray.length; i < length; i++) {
            if (mSelectedArray[i]) {
                positions.add(i);
            }
        }
        return positions;
    }

    private OnAllCheckedChangeListener mCheckedListener;

    public void handlerAllCheckedChange(boolean isChecked) {
        if (mCheckedListener != null && isChecked != isAllChecked) {
            mCheckedListener.onAllChecked(isChecked);
            isAllChecked = isChecked;
        }
    }

    public void setAllCheckedChangeListener(OnAllCheckedChangeListener listener) {
        this.mCheckedListener = listener;
    }

    public interface OnAllCheckedChangeListener {
        void onAllChecked(boolean isChecked);
    }

}
