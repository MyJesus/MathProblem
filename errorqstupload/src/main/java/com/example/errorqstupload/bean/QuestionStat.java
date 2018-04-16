package com.example.errorqstupload.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by guh on 2017/9/7.
 */

public class QuestionStat implements Parcelable{

    public int mSubject = 0;
    public int mGrade = 0;

    /**     */
    public int mQstId= 0;

    /**    做过次数   */
    public int mExamTotal =0;

    /**    耗时 单位秒    */
    public int mDuration = 0;

    public QuestionStat() {}

    protected QuestionStat(Parcel in) {
        mSubject = in.readInt();
        mGrade = in.readInt();
        mQstId = in.readInt();
        mExamTotal = in.readInt();
        mDuration = in.readInt();
    }

    public static final Creator<QuestionStat> CREATOR = new Creator<QuestionStat>() {
        @Override
        public QuestionStat createFromParcel(Parcel in) {
            return new QuestionStat(in);
        }

        @Override
        public QuestionStat[] newArray(int size) {
            return new QuestionStat[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mSubject);
        parcel.writeInt(mGrade);
        parcel.writeInt(mQstId);
        parcel.writeInt(mExamTotal);
        parcel.writeInt(mDuration);
    }
}
