package com.readboy.mathproblem.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.AttributeSet;

/**
 * Created by oubin on 2017/11/3.
 */

public class ChoiceRadioButton extends AppCompatRadioButton {

    public ChoiceRadioButton(Context context) {
        this(context, null);
    }

    public ChoiceRadioButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChoiceRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        return super.onCreateDrawableState(extraSpace);

    }
}
