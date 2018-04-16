package com.readboy.mathproblem.notetool;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import com.readboy.mathproblem.R;
import com.readboy.mathproblem.notetool.NoteDrawView;
import com.readboy.mathproblem.notetool.PenDemoView;
import com.readboy.mathproblem.util.SizeUtils;

/**
 * Created by oubin on 2017/9/28.
 * @author oubin
 */

public class NoteToolboxDialog extends Dialog implements View.OnClickListener {
    private static final String TAG = "NoteToolBox";

    private static final int COLOR_NUM = 12;
    private PenAttrUpdate mPenAttrUpdate;
    private PenDemoView mDemoViewPoint;
    private PenDemoView mDemoViewLine;
    private RadioGroup mRadioGroup1;
    private RadioGroup mRadioGroup2;
    private View mParentView;
    private SeekBar mSeekBar;
    private int mRadioGroup1CheckId;
    private int mRadioGroup2CheckId;
    private NoteDrawView.PenAttr mPenAttr;

    private Context mContext;

    public NoteToolboxDialog(Context context) {
        super(context, R.style.TransparentDialog);
        this.mContext = context;
    }

    public NoteToolboxDialog(Context context, NoteDrawView.PenAttr penAttr, PenAttrUpdate update) {
        this(context);
        this.mContext = context;
        mPenAttrUpdate = update;
        mPenAttr = penAttr;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate: ");
        setContentView(R.layout.note_toolbox);

        initWindow();
        assignView();

    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.e(TAG, "onAttachedToWindow: ");
        updateView(mPenAttr);
    }

    @Override
    public void show() {
        super.show();
//        Log.e(TAG, "show: ");
//        showAnimation();
    }

    private void assignView() {
        mParentView = findViewById(R.id.palette_toolbox_parent);

        mDemoViewPoint = (PenDemoView) findViewById(R.id.note_tool_demo_point);
        mDemoViewPoint.setPenDemoType(PenDemoView.TYPE_POINT, mPenAttr);

        mDemoViewLine = (PenDemoView) findViewById(R.id.note_tool_demo_line);
        mDemoViewLine.setPenDemoType(PenDemoView.TYPE_LINE, mPenAttr);

        mSeekBar = (SeekBar) findViewById(R.id.note_tool_seekbar);
        int progress = mPenAttr.mWidthBarProgress;
        if (progress < 0) {
            progress = (mPenAttr.mPenWidth * mSeekBar.getMax() / NoteDrawView.PenAttr.MAX_PEN_WIDTH);
        }
        mSeekBar.setProgress(progress);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mPenAttr.mWidthBarProgress = progress;
                mPenAttr.mPenWidth = (NoteDrawView.PenAttr.MAX_PEN_WIDTH - NoteDrawView.PenAttr.MIN_PEN_WIDTH) * progress / seekBar.getMax() + NoteDrawView.PenAttr.MIN_PEN_WIDTH;
                if (mPenAttr.mPenWidth < NoteDrawView.PenAttr.MIN_PEN_WIDTH) {
                    mPenAttr.mPenWidth = NoteDrawView.PenAttr.MIN_PEN_WIDTH;
                }

                mDemoViewPoint.setPenDemoType(PenDemoView.TYPE_POINT, mPenAttr);
                mDemoViewLine.setPenDemoType(PenDemoView.TYPE_LINE, mPenAttr);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mRadioGroup1 = (RadioGroup) findViewById(R.id.note_color_radiogroup1);
        mRadioGroup2 = (RadioGroup) findViewById(R.id.note_color_radiogroup2);
        int colorBtnId = getBtnIdByColor(mPenAttr.mColor);
        if (colorBtnId == R.id.note_btn_color1
                || colorBtnId == R.id.note_btn_color2
                || colorBtnId == R.id.note_btn_color3
                || colorBtnId == R.id.note_btn_color4
                || colorBtnId == R.id.note_btn_color5
                || colorBtnId == R.id.note_btn_color6) {
            mRadioGroup1.check(colorBtnId);
        } else {
            mRadioGroup2.check(colorBtnId);
        }
        mRadioGroup1.setOnCheckedChangeListener((group, checkedId) -> {
            if (mRadioGroup1CheckId == checkedId || checkedId == -1) {
                mRadioGroup1CheckId = checkedId;
                return;
            }
            mRadioGroup1CheckId = checkedId;
            mRadioGroup2.clearCheck();
            mRadioGroup1.check(checkedId);
            mPenAttr.mColor = getColorValue(checkedId);
            mDemoViewPoint.setPenDemoType(PenDemoView.TYPE_POINT, mPenAttr);
            mDemoViewLine.setPenDemoType(PenDemoView.TYPE_LINE, mPenAttr);
        });
        mRadioGroup2.setOnCheckedChangeListener((group, checkedId) -> {
            if (mRadioGroup2CheckId == checkedId || checkedId == -1) {
                mRadioGroup2CheckId = checkedId;
                return;
            }
            mRadioGroup2CheckId = checkedId;
            mRadioGroup1.clearCheck();
            mRadioGroup2.check(checkedId);
            mPenAttr.mColor = getColorValue(checkedId);
            mDemoViewPoint.setPenDemoType(PenDemoView.TYPE_POINT, mPenAttr);
            mDemoViewLine.setPenDemoType(PenDemoView.TYPE_LINE, mPenAttr);
        });
    }

    private void initWindow() {
        Window window = getWindow();
        if (window == null) {
            Log.e(TAG, "NoteToolBox: 无法获取Dialog");
            return;
        }
        window.setContentView(R.layout.note_toolbox);
        window.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = 1.0f;
        lp.x = SizeUtils.dp2px(getContext(), 66);
        lp.y = SizeUtils.dp2px(getContext(), 0);
        window.setAttributes(lp);
    }

    private int getColorValue(int btnId) {
        int color;

        switch (btnId) {
            case R.id.note_btn_color2:
                color = 0xff979797;
                break;
            case R.id.note_btn_color3:
                color = 0xfff9f000;
                break;
            case R.id.note_btn_color4:
                color = 0xff20e300;
                break;
            case R.id.note_btn_color5:
                color = 0xff00e4ff;
                break;
            case R.id.note_btn_color6:
                color = 0xff0090ff;
                break;
            case R.id.note_btn_color7:
                color = 0xffa92dff;
                break;
            case R.id.note_btn_color8:
                color = 0xffff4ce8;
                break;
            case R.id.note_btn_color9:
                color = 0xffffba00;
                break;
            case R.id.note_btn_color10:
                color = 0xffff6000;
                break;
            case R.id.note_btn_color11:
                color = 0xffff1f39;
                break;
            case R.id.note_btn_color12:
                color = 0xff000000;
                break;
            default:
                color = 0xff1b1b1b;
        }
        return color;
    }

    private int getBtnIdByColor(int color) {
        int btnId;

        switch (color) {
            case 0xff979797:
                btnId = R.id.note_btn_color2;
                break;
            //	case 0xffd4d4d4:
            //		btnId = R.id.note_btn_color3;
            //		break;
            case 0xfff9f000:
                btnId = R.id.note_btn_color3;
                break;
            case 0xff20e300:
                btnId = R.id.note_btn_color4;
                break;
            case 0xff00e4ff:
                btnId = R.id.note_btn_color5;
                break;
            case 0xff0090ff:
                btnId = R.id.note_btn_color6;
                break;
            case 0xffa92dff:
                btnId = R.id.note_btn_color7;
                break;
            case 0xffff4ce8:
                btnId = R.id.note_btn_color8;
                break;
            case 0xffffba00:
                btnId = R.id.note_btn_color9;
                break;
            case 0xffff6000:
                btnId = R.id.note_btn_color10;
                break;
            case 0xffff1f39:
                btnId = R.id.note_btn_color11;
                break;
            case 0xff000000:
                btnId = R.id.note_btn_color12;
                break;
            default:
                btnId = R.id.note_btn_color1;
        }
        return btnId;
    }

    public void showAnimation() {
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(mParentView, "alpha", 0.0F, 1.0F);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mParentView, "scaleX", 0, 1);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(mParentView, "scaleY", 0, 1);
        ObjectAnimator animator4 = ObjectAnimator.ofFloat(mParentView, "x", 0, mParentView.getMeasuredWidth());
        ObjectAnimator animator5 = ObjectAnimator.ofFloat(mParentView, "y", 0, mParentView.getMeasuredWidth());

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(animator1);
        animatorSet.play(animator2);
        animatorSet.play(animator3);
        animatorSet.play(animator4);
        animatorSet.play(animator5);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                Log.e(TAG, "onAnimationStart: ");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.start();
    }

    public void hideAnimation() {
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(mParentView, "alpha", 1.0F, 0.0F);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mParentView, "scaleX", 1, 0);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(mParentView, "scaleY", 1, 0);
        ObjectAnimator animator4 = ObjectAnimator.ofFloat(mParentView, "x", mParentView.getMeasuredWidth(), 0);
        ObjectAnimator animator5 = ObjectAnimator.ofFloat(mParentView, "y", mParentView.getMeasuredWidth(), 0);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(animator1);
        animatorSet.play(animator2);
        animatorSet.play(animator3);
        animatorSet.play(animator4);
        animatorSet.play(animator5);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.e(TAG, "onAnimationEnd: ");
                mParentView.setVisibility(View.GONE);
                hide();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Log.e(TAG, "onAnimationCancel: ");

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.start();
    }

    public void exit() {
        dismiss();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        handlerPenAttrUpdateEvent(mPenAttr);
        mDemoViewPoint.exit();
        mDemoViewLine.exit();
    }

    @Override
    public void onClick(View arg0) {

    }

    public void setPenAttr(NoteDrawView.PenAttr penAttr) {
        this.mPenAttr = penAttr;
    }

    private void updateView(NoteDrawView.PenAttr penAttr) {
        mDemoViewPoint.setPenDemoType(PenDemoView.TYPE_POINT, penAttr);
        mDemoViewLine.setPenDemoType(PenDemoView.TYPE_LINE, penAttr);
        int progress = penAttr.mWidthBarProgress;
        if (progress < 0) {
            progress = (penAttr.mPenWidth * mSeekBar.getMax() / NoteDrawView.PenAttr.MAX_PEN_WIDTH);
        }
        mSeekBar.setProgress(progress);

    }

    public NoteDrawView.PenAttr getPenAttr() {
        return mPenAttr;
    }

    private void handlerPenAttrUpdateEvent(NoteDrawView.PenAttr penAttr) {
        if (mPenAttrUpdate != null) {
            mPenAttrUpdate.update(penAttr);
        }
    }

    public void setPenAttrUpdate(PenAttrUpdate update) {
        this.mPenAttrUpdate = update;
    }

    public interface PenAttrUpdate {
        /**
         * 画笔参数更新
         * @param penAttr 画笔参数
         */
        void update(NoteDrawView.PenAttr penAttr);
    }
}
