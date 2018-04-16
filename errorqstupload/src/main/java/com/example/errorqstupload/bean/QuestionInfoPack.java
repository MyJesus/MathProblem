package com.example.errorqstupload.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sion's on 2017/8/11.
 */

public class QuestionInfoPack implements Parcelable {
    private QuestionInfo info;
    private String htmlStr;
    //临时存储的数据
    private int mode; //显示模式
    //自己的答案
    private String userAnswer = "-1";
    private String userBlkAnswer;
    //EI信息
    private String singleSubTime;
    private String singleSubAveTime;
    private String praNum;
    private String diff;
    //答题情况
    private int checkRes = -1;
    //排序题答案
    private String sortAnswer = "";

    public QuestionInfoPack() {}

    protected QuestionInfoPack(Parcel in) {
        info = in.readParcelable(QuestionInfo.class.getClassLoader());
        htmlStr = in.readString();
        mode = in.readInt();
        userAnswer = in.readString();
        userBlkAnswer = in.readString();
        singleSubTime = in.readString();
        singleSubAveTime = in.readString();
        praNum = in.readString();
        diff = in.readString();
        checkRes = in.readInt();
        sortAnswer = in.readString();
    }

    public static final Creator<QuestionInfoPack> CREATOR = new Creator<QuestionInfoPack>() {
        @Override
        public QuestionInfoPack createFromParcel(Parcel in) {
            return new QuestionInfoPack(in);
        }

        @Override
        public QuestionInfoPack[] newArray(int size) {
            return new QuestionInfoPack[size];
        }
    };

    public QuestionInfo getInfo() {
        return info;
    }

    public void setInfo(QuestionInfo info) {
        this.info = info;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String answer) {
        this.userAnswer = answer;
    }

    public String getUserBlkAnswer() {
        return userBlkAnswer;
    }

    public void setUserBlkAnswer(String blkAnswer) {
        this.userBlkAnswer = blkAnswer;
    }

    public String getSingleSubTime() {
        return singleSubTime;
    }

    public void setSingleSubTime(String singleSubTime) {
        this.singleSubTime = singleSubTime;
    }

    public String getSingleSubAveTime() {
        return singleSubAveTime;
    }

    public void setSingleSubAveTime(String singleSubAveTime) {
        this.singleSubAveTime = singleSubAveTime;
    }

    public String getPraNum() {
        return praNum;
    }

    public void setPraNum(String praNum) {
        this.praNum = praNum;
    }

    public String getDiff() {
        return diff;
    }

    public void setDiff(String diff) {
        this.diff = diff;
    }

    public String getHtmlStr() {
        return htmlStr;
    }

    public void setHtmlStr(String htmlStr) {
        this.htmlStr = htmlStr;
    }

    public int getCheckRes() {
        return checkRes;
    }

    public void setCheckRes(int checkRes) {
        this.checkRes = checkRes;
    }

    public String getSortAnswer() {
        return sortAnswer;
    }

    public void setSortAnswer(String sortAnswer) {
        this.sortAnswer = sortAnswer;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(info, i);
        parcel.writeString(htmlStr);
        parcel.writeInt(mode);
        parcel.writeString(userAnswer);
        parcel.writeString(userBlkAnswer);
        parcel.writeString(singleSubTime);
        parcel.writeString(singleSubAveTime);
        parcel.writeString(praNum);
        parcel.writeString(diff);
        parcel.writeInt(checkRes);
        parcel.writeString(sortAnswer);
    }
}
