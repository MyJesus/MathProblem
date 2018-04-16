package com.example.errorqstupload.bean.item;

/**
 * Created by guh on 2017/8/1.
 */

public class Section {

    /**    章节来源    */
    private int mSource = 0;
    /**    章节编号    */
    private int mId = 0;
    /**    章节名称    */
    private String mName = null;

    public Section(int id, int source, String name) {
        mId = id;
        mSource = source;
        mName = name;
    }

    public int getId() {
        return mId;
    }

    public int getSource() {
        return mSource;
    }

    public String getName() {
        return mName;
    }
}
