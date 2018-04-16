package com.example.errorqstupload.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by guh on 2017/8/4.
 * 做题详情
 */

public class Reply implements Parcelable{

    /**     题目序号    */
    public int mSerialNumber =0;

    /**    题目ID    */
    public int mQstId = 0;

    /**    答案正误    */
    public int mRight = 0;

    /**    做题时间   */
    public int mDuration = 0;

    /**    用户答案    */
    public String mUserAnswer = null;

    public Reply() {}

    protected Reply(Parcel in) {
        mSerialNumber = in.readInt();
        mQstId = in.readInt();
        mRight = in.readInt();
        mDuration = in.readInt();
        mUserAnswer = in.readString();
    }

    public static final Creator<Reply> CREATOR = new Creator<Reply>() {
        @Override
        public Reply createFromParcel(Parcel in) {
            return new Reply(in);
        }

        @Override
        public Reply[] newArray(int size) {
            return new Reply[size];
        }
    };

    public JSONObject getJsonObject() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("number", mSerialNumber);
        obj.put("qid", mQstId);
        obj.put("right", mRight);
        obj.put("user_answer", mUserAnswer);
        obj.put("duration", mDuration);
        return obj;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mSerialNumber);
        parcel.writeInt(mQstId);
        parcel.writeInt(mRight);
        parcel.writeInt(mDuration);
        parcel.writeString(mUserAnswer);
    }
}
