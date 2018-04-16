package com.example.errorqstupload.bean;

/**
 * Created by Sion's on 2017/12/26.
 */

public class TinyQuestionInfo {
    public int id; //试题ID
    public int type;
    public int role;
    public String orgInfo; //试题的JSON STR
    public boolean isArray = false;

    public TinyQuestionInfo(int id, int type, int role, String orgInfo) {
        this.id = id;
        this.type = type;
        this.role = role;
        this.orgInfo = orgInfo;
    }
}
