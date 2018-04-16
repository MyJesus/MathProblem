package com.example.errorqstupload.bean;

/**
 * Created by Sion's on 2017/11/18.
 */

public class Question {
    private int id;
    private String data;
    private int count;
    private long time;
    private boolean isFirstAfterSort = false;

    public boolean isFirstAfterSort() {
       return isFirstAfterSort;
    }

    public void setFirstAfterSort(boolean flag) {
        isFirstAfterSort = flag;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
