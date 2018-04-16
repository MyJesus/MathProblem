package com.readboy.mathproblem.exercise;

/**
 * Created by oubin on 2017/9/11.
 */

public class ExerciseResult {

    /**
     * 0-1
     */
    private float mCorrectRate;
    private long mTime;
    private boolean mHasVideo;

    public ExerciseResult() {
        this(0, 0, false);
    }

    public ExerciseResult(int mCorrectRate, long mTime, boolean mHasVideo) {
        this.mCorrectRate = mCorrectRate;
        this.mTime = mTime;
        this.mHasVideo = mHasVideo;
    }

    public float getCorrectRate() {
        return mCorrectRate;
    }

    public void setCorrectRate(float mCorrectRate) {
        this.mCorrectRate = mCorrectRate;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long mTime) {
        this.mTime = mTime;
    }

    public boolean isHasVideo() {
        return mHasVideo;
    }

    public void setHasVideo(boolean mHasVideo) {
        this.mHasVideo = mHasVideo;
    }

    @Override
    public String toString() {
        return "ExerciseResult{" +
                "mCorrectRate=" + mCorrectRate +
                ", mTime=" + mTime +
                ", mHasVideo=" + mHasVideo +
                '}';
    }
}
