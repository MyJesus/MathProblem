package com.readboy.mathproblem.note;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.readboy.mathproblem.R;
import com.readboy.mathproblem.notetool.*;

/**
 * Created by oubin on 2017/8/24.
 */

public class DraftPaperView extends FrameLayout implements View.OnClickListener, NoteToolboxDialog.PenAttrUpdate {
    private static final String TAG = "OubinDraftPaper";

    private PaletteView mPaletteView;
    private View mHandView;
    private View mUndoView;
    private View mRedoView;
    private View mClearView;
    private View mCloseView;

    public DraftPaperView(Context context) {
        this(context, null);
    }

    public DraftPaperView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DraftPaperView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        addView(parent);
        initView();
    }

    private void initView() {
        mPaletteView = new PaletteView(getContext());
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mPaletteView.setLayoutParams(lp);
        mPaletteView.setPenColor(Color.WHITE);
        addView(mPaletteView);

        View menuParent = LayoutInflater.from(getContext()).inflate(R.layout.draft_paper_menu, this);
        assignView(menuParent);
        initClickEvent();

    }

    private void assignView(View parent) {
        mHandView = parent.findViewById(R.id.hand);
        mUndoView = parent.findViewById(R.id.undo);
        mRedoView = parent.findViewById(R.id.redo);
        mClearView = parent.findViewById(R.id.clear);
        mCloseView = parent.findViewById(R.id.close);
    }

    private void initClickEvent() {
        mUndoView.setOnClickListener(this);
        mRedoView.setOnClickListener(this);
        mClearView.setOnClickListener(this);
        mCloseView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hand:
                //TODO 抓手工具，或者不独立一个开关，而是使用双点触摸触发。
                break;
            case R.id.undo:
                mPaletteView.undo();
                break;
            case R.id.redo:
                mPaletteView.redo();
                break;
            case R.id.clear:
                mPaletteView.clear();
                break;
            case R.id.close:
                setVisibility(GONE);
                break;
            default:
                Log.e(TAG, "onClick: default = " + v.getId());
                break;
        }
    }

    @Override
    public void update(NoteDrawView.PenAttr penAttr) {
        mPaletteView.setPenWidth(penAttr.mPenWidth);
        mPaletteView.setPenColor(penAttr.mColor);
    }

    public boolean isShowing(){
        return getVisibility() == VISIBLE;
    }

    public void show(){
        setVisibility(VISIBLE);
    }

    public void hide(){
        setVisibility(GONE);
    }
}
