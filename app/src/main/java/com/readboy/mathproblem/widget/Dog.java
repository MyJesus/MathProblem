package com.readboy.mathproblem.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.readboy.mathproblem.R;
import com.readboy.mathproblem.util.SizeUtils;

/**
 * Created by oubin on 2017/10/18.
 */

public class Dog extends AppCompatImageView {
    private static final String TAG = "Dog";

    private AnimationDrawable mDogLeftAnimation;
    private AnimationDrawable mDogRightAnimation;
    //开启省电模式会有问题
//    private ObjectAnimator mDogAnimator1;
//    private ObjectAnimator mDogAnimator2;
    private TranslateAnimation mAnimation1;
    private TranslateAnimation mAnimation2;
    private boolean isDogLeft = false;

    public Dog(Context context) {
        this(context, null);
    }

    public Dog(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Dog(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initAnimation();
        startAnimation();
    }

    private void initAnimation() {
        setBackgroundResource(R.drawable.dog_animation_left);
        mDogLeftAnimation = (AnimationDrawable) getBackground();
        setBackgroundResource(R.drawable.dog_animation_right);
        mDogRightAnimation = (AnimationDrawable) getBackground();
//        mDogAnimator1 = ObjectAnimator.ofFloat(this, "translationX", 0F,
//                SizeUtils.dp2px(getContext(), -138F));
//        mDogAnimator1.setDuration(9200);
//
//        mDogAnimator2 = ObjectAnimator.ofFloat(this, "translationX",
//                SizeUtils.dp2px(getContext(), -138F), 0);
//        mDogAnimator2.setDuration(9200);
//        mDogAnimator1.addListener(new CustomAnimatorListener() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                setBackground(mDogLeftAnimation);
//                mDogRightAnimation.stop();
//                mDogLeftAnimation.start();
//                mDogAnimator2.start();
//                isDogLeft = true;
//            }
//        });
//        mDogAnimator2.addListener(new CustomAnimatorListener() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                setBackground(mDogRightAnimation);
//                mDogLeftAnimation.stop();
//                mDogRightAnimation.start();
//                mDogAnimator1.start();
//                isDogLeft = false;
//            }
//        });

        mAnimation1 = new TranslateAnimation(0, SizeUtils.dp2px(getContext(), -138F), 0, 0);
        mAnimation1.setDuration(9200);
        mAnimation1.setAnimationListener(new CustomAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                setBackground(mDogLeftAnimation);
                mDogRightAnimation.stop();
                mDogLeftAnimation.start();
//                mAnimation2.start();
                setAnimation(mAnimation2);
                startAnimation(mAnimation2);
                isDogLeft = true;
            }
        });
        mAnimation2 = new TranslateAnimation(SizeUtils.dp2px(getContext(), -138F), 0, 0, 0);
        mAnimation2.setDuration(9200);
        mAnimation2.setAnimationListener(new CustomAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                setBackground(mDogRightAnimation);
                mDogLeftAnimation.stop();
                mDogRightAnimation.start();
                setAnimation(mAnimation1);
                startAnimation(mAnimation1);
                isDogLeft = false;
            }
        });

    }

    public void startAnimation() {
        if (isDogLeft) {
            mDogLeftAnimation.start();
//            mDogAnimator2.start();
            setAnimation(mAnimation2);
            setAnimation(mAnimation2);
        } else {
            mDogRightAnimation.start();
//            mDogAnimator1.start();
            setAnimation(mAnimation1);
            setAnimation(mAnimation1);
        }
    }

    public void stopAnimation() {
        mDogLeftAnimation.stop();
        mDogRightAnimation.stop();
//        mDogAnimator1.pause();
//        mDogAnimator2.pause();
        mAnimation1.cancel();

        mAnimation2.cancel();
    }

    private class CustomAnimatorListener implements Animator.AnimatorListener {

        @Override
        public void onAnimationStart(Animator animation) {

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
    }

    private class CustomAnimationListener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

}
