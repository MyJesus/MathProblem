package com.example.errorqstupload.bean;

/**
 * Created by oubin on 2017/12/28.
 */

public class OrginInfo {

    /**
     * origin : （2016山东泰安中考，22，★☆☆）
     * status : 0
     * updateTime : 1512011943
     * shortOrigin : （2016山东泰安中考，22，★☆☆）
     * solution : <p>解答好此题要靠平时的积累。如果遇到没有见过的歇后语，要通过字面意思展开联想，从而推断出接近的答案。</p>
     * id : 194049531
     * content : <p>填写出歇后语的后半截。（3分）</p><p>（1）泥菩萨过河——<blk mlen="5" mstyle="underline"></blk></p><p>（2）哑巴吃黄连——<blk mlen="4" mstyle="underline"></blk></p><p>（3）徐庶进曹营——<blk mlen="5" mstyle="underline"></blk></p>
     * version : 589
     * role : 0
     * answer : <p>（1）自身难保。</p><p>（2）有苦难言（有苦说不出）。</p><p>（3）一言不发。</p>
     * type : 202
     * createTime : 1512011943
     */

    private String origin;
    private int status;
    private int updateTime;
    private String shortOrigin;
    private String solution;
    private int id;
    private String content;
    private int version;
    private int role;
    private String answer;
    private int type;
    private int createTime;

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(int updateTime) {
        this.updateTime = updateTime;
    }

    public String getShortOrigin() {
        return shortOrigin;
    }

    public void setShortOrigin(String shortOrigin) {
        this.shortOrigin = shortOrigin;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCreateTime() {
        return createTime;
    }

    public void setCreateTime(int createTime) {
        this.createTime = createTime;
    }
}
