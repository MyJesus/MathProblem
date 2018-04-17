package com.readboy.aliyunplayerlib.utils;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.view.View;

import com.readboy.aliyunplayerlib.R;

/**
 * 属性动画工具类
 * @author ldw
 */
public class AnimatorUtil {

    /**
     * Y轴移动view
     * @param view
     * @param fromY
     * @param toY
     * @param duration
     */
    public static void translateY(View view, float fromY, float toY, long duration){
        view.setVisibility(View.VISIBLE);
        ObjectAnimator translateAnimator = ObjectAnimator.ofFloat(view, "translationY", fromY, toY);
        translateAnimator.setDuration(duration);
        translateAnimator.start();
    }

    /**
     * X轴移动view
     * @param view
     * @param fromX
     * @param toX
     * @param duration
     */
    public static void translateX(View view, float fromX, float toX, long duration){
        view.setVisibility(View.VISIBLE);
        ObjectAnimator translateAnimator = ObjectAnimator.ofFloat(view, "translationX", fromX, toX);
        translateAnimator.setDuration(duration);
        translateAnimator.start();
    }

    /**
     * view在Y轴移动后，把view设置为Gone状态
     * @param view
     * @param fromY
     * @param toY
     * @param duration
     */
    public static void translateYToGone(final View view, float fromY, float toY, long duration){
        animatorToGone("translationY", view, fromY, toY, duration);
    }

    /**
     * View透明度渐变后，设置为Gone状态
     * @param view
     * @param fromAlpha
     * @param toAlpha
     * @param duration
     */
    public static void alphaToGone(final View view, float fromAlpha, float toAlpha, long duration){
        animatorToGone("alpha", view, fromAlpha, toAlpha, duration);
    }

    /**
     * 指定的view做完指定动画后消失
     * @param animator
     * @param view
     * @param from
     * @param to
     * @param duration
     */
    private static void animatorToGone(String animator, final View view, float from, float to, long duration){
        view.setVisibility(View.VISIBLE);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, animator, from, to);
        objectAnimator.setDuration(duration);
        objectAnimator.start();
        view.setTag(R.id.view_animator_tag, "start");
        objectAnimator.addListener(new AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }
            @Override
            public void onAnimationRepeat(Animator animation) {
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setTag(R.id.view_animator_tag, "end");
                view.setVisibility(View.GONE);
                view.setAlpha(1);
            }
            @Override
            public void onAnimationCancel(Animator animation) {
            }
        });
    }

    /**
     * 获取该view是否正在执行动画。该view执行的动画必须是有监听动画结束的，例如调用了animatorToGone方法的
     * @param view
     * @return  true 正在执行动画，false 动画执行完毕或者没有执行动画
     */
    public static boolean isAnimatorWithView(final View view){
        String tag = (String) view.getTag(R.id.view_animator_tag);
        return tag != null && tag.equalsIgnoreCase("start");
    }

}
