package com.readboy.mathproblem.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.readboy.mathproblem.R;
import com.readboy.mathproblem.util.SizeUtils;
import com.readboy.mathproblem.widget.LineItemDecoration;
import com.readboy.recyclerview.CommonAdapter;
import com.readboy.recyclerview.base.ViewHolder;

import java.util.Arrays;
import java.util.List;

/**
 * Created by oubin on 2017/10/15.
 */

public class GradeListDialog extends Dialog {
    private static final String TAG = "GradeListDialog";

    private RecyclerView mGradeNameRv;
    private CommonAdapter<String> mGradeAdapter;
    private int mSelectedPosition;

    public GradeListDialog(@NonNull Context context) {
        super(context, R.style.TransparentDialog);
    }

    public GradeListDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate: ");
        setContentView(R.layout.dialog_grade_list);
        initWindow();
        init();
    }

    private void initWindow() {
        Window window = getWindow();
        if (window == null) {
            Log.e(TAG, "NoteToolBox: 无法获取Dialog");
            return;
        }
        window.setContentView(R.layout.dialog_grade_list);
        window.setGravity(Gravity.START | Gravity.TOP);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setDimAmount(0);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
//        window.setWindowAnimations(R.style.dialogWindowAnim);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        lp.x = SizeUtils.dp2px(getContext(), 46);
        lp.y = SizeUtils.dp2px(getContext(), 100);
        window.setAttributes(lp);
    }

    private void init() {
        mGradeNameRv = (RecyclerView) findViewById(R.id.grade_list);
        mGradeNameRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        LineItemDecoration decoration = new LineItemDecoration(LinearLayout.VERTICAL, 1, 0xffff9933);
        decoration.drawLastLine(false);
        mGradeNameRv.addItemDecoration(decoration);

        String[] gradeNameS = getContext().getResources().getStringArray(R.array.grade_name_group);
        List<String> gradeNameList = Arrays.asList(gradeNameS);
        mGradeAdapter = new CommonAdapter<String>(getContext(), R.layout.item_grade_name, gradeNameList) {
            @Override
            protected void convert(ViewHolder holder, String s, int position) {
                TextView name = (TextView) holder.itemView.findViewById(R.id.item_grade_name);
                name.setText(s);
                if (position == mSelectedPosition) {
                    name.setSelected(true);
                } else {
                    name.setSelected(false);
                }
            }
        };
        mGradeNameRv.setAdapter(mGradeAdapter);
    }

    /**
     * @param position 选中的位置
     * @return 选中的itemView
     */
    public void setSelectedPosition(int position) {
        mGradeAdapter.notifyItemChanged(mSelectedPosition);
        mSelectedPosition = position;
        mGradeAdapter.notifyItemChanged(mSelectedPosition);
//        int count = mGradeAdapter.getItemCount();
//        for (int i = 0; i < count; i++) {
//            RecyclerView.ViewHolder viewHolder = mGradeNameRv.findViewHolderForAdapterPosition(i);
//            if (viewHolder != null) {
//                viewHolder.itemView.setSelected(false);
//            } else {
//                Log.e(TAG, "setSelectedPosition: viewHolder = null, position = " + i);
//            }
//        }
//        RecyclerView.ViewHolder vh = mGradeNameRv.findViewHolderForAdapterPosition(position);
//        if (vh != null) {
//            vh.itemView.setSelected(true);
//            return vh.itemView;
//        }else {
//            return null;
//        }
    }

    public void setItemClickListener(CommonAdapter.OnItemClickListener<String> listener) {
        mGradeAdapter.setOnItemClickListener(listener);
    }


}
