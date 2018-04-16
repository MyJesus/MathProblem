package com.readboy.video.db;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guh on 2017/4/20.
 */

public class DataLoadInfoItem {

    public int mId = 0;
    /** 这一段对应时长开始位置 */
    public long mBeginPosition = 0;
    /** 这一段对应时长结束的位置 */
    public long mEndPosition = 0;

    /** 这一段开始操作的位置  */
    public long mBeginBit = 0;
    /**  这一段操作完的位置  */
    public long mEndBit = 0;
    /**  这一段共操作的大小； 理论上： mSize = mEndBit - mBeginBit */
    public long mSize = 0;

    public long mTime = 0;

    public DataLoadInfoItem() {

    }

    public DataLoadInfoItem(int id, long beginBit, long endBit) {
        mId = id;
        mBeginBit = beginBit;
        mEndBit = endBit;
    }

    public void printf() {
//        Log.i("", "DataLoadInfoItem mId: "+ mId + ", mBeginPosition: " + mBeginPosition + ", mEndPosition: " + mEndPosition
//                + ", mSize: " + mSize + ", mBeginBit: " + mBeginBit + ", mEndBit: " + mEndBit + ", mTime: " + mTime);
    }

    static public DataLoadInfoItem createOrder(long beginbit, List<DataLoadInfoItem> lists) {
        int id = -1;
        if (lists.size()>0) {
            for (int i=0; i<lists.size(); i++) {
                if (lists.get(i).mBeginBit > beginbit) {
                    lists.get(i).mId += 1;
                    if (-1 == id) {
                        id = i;
                    }
                }
                lists.get(i).printf();
            }
        }
        if (-1 == id) {
            id = lists.size();
        }

        DataLoadInfoItem item = new DataLoadInfoItem(id, beginbit, beginbit);
        item.printf();
        return item;
    }

    @Override
    public String toString() {
        return "DataLoadInfoItem{" +
                "mId=" + mId +
                ", mBeginPosition=" + mBeginPosition +
                ", mEndPosition=" + mEndPosition +
                ", mBeginBit=" + mBeginBit +
                ", mEndBit=" + mEndBit +
                ", mSize=" + mSize +
                ", mTime=" + mTime +
                '}';
    }
}
