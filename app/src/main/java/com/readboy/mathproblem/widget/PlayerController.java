package com.readboy.mathproblem.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * Created by oubin on 2017/11/17.
 */

public class PlayerController extends AppCompatImageView {

    public PlayerController(Context context) {
        this(context, null);
    }

    public PlayerController(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayerController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        setActivated(true);
    }




}
