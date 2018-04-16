package com.readboy.mathproblem.dialog;

/**
 * Created by oubin on 2017/9/15.
 */

public class VideoInfo {

    private boolean isChecked;
    private String mPath;
    private String mName;
    private String mSize;

    public VideoInfo(boolean isChecked, String mPath, String mName, String mSize) {
        this.isChecked = isChecked;
        this.mPath = mPath;
        this.mName = mName;
        this.mSize = mSize;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        this.isChecked = checked;
    }

    public String getPath() {
        return mPath;
    }

    public String getName() {
        return mName;
    }

    public String getSize() {
        return mSize;
    }
}
