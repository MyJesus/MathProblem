package com.readboy.mathproblem.http.response;

import java.util.List;

/**
 * Created by oubin on 2017/9/19.
 */

public class BaseResponse<T extends BaseResponse.Data> {

    /**
     * msg : success
     * data : [{"category":1,"status":0,"updateTime":1503624648,"courseId":66,"from":65,"grade":1,"solution":"<p>3＋2＝5（支）<\/p>","createTime":1503621006,"accessory":[{"type":101,"options":["4支","5支","6支","7支"]}],"content":"<p>老师给了陶星3支铅笔，给了陈佳林2支，他们两人一共有几支？<\/p>","difficulty":0,"version":492,"role":0,"correctAnswer":["B"],"opinion":[],"type":101,"id":18925,"subject":2},{"category":1,"status":0,"updateTime":1503624648,"courseId":66,"from":65,"grade":1,"solution":"<p>25＋25＝50（棵）<\/p>","createTime":1503621089,"accessory":[{"type":101,"options":["27棵","25棵","23棵","50棵"]}],"content":"<p>人民路两侧各栽有25棵梧桐树，人民路一共栽有多少棵梧桐树？<\/p>","difficulty":0,"version":492,"role":0,"correctAnswer":["D"],"opinion":[],"type":101,"id":28925,"subject":2},{"category":1,"status":0,"updateTime":1504075065,"courseId":66,"from":65,"grade":1,"solution":"<p>4＋3＝7（辆）<\/p>","createTime":1503621157,"accessory":[{"type":101,"options":["９辆","６辆","10辆","７辆"]}],"content":"<p>停车场原来停了4辆小汽车，又开来了3辆，现在停车场有几辆车？<\/p>","difficulty":0,"version":499,"role":0,"correctAnswer":["D"],"opinion":[],"type":101,"id":38925,"subject":2},{"category":1,"status":0,"updateTime":1503624648,"courseId":66,"from":65,"grade":1,"solution":"<p>4＋2＝6（位）<\/p>","createTime":1503621237,"accessory":[{"type":101,"options":["3位","5位","6位","7位"]}],"content":"<p>教室里原有4位同学，又进来了2位，现在教室里有几位同学？<\/p>","difficulty":0,"version":492,"role":0,"correctAnswer":["C"],"opinion":[],"type":101,"id":48925,"subject":2},{"category":1,"status":0,"updateTime":1504075042,"courseId":66,"from":65,"grade":1,"solution":"<p>40＋20＝60（只）<\/p>","createTime":1503621339,"accessory":[{"type":101,"options":["60只","42只","24只","20只"]}],"content":"<p>笼子里有40只鸡和20只鸭，笼子里的鸡和鸭一共有多少只？<\/p>","difficulty":0,"version":499,"role":0,"correctAnswer":["A"],"opinion":[],"type":101,"id":58925,"subject":2}]
     * ok : 1
     */

    private String msg;
    private int ok;
    private List<T> data;

    private int errno;

    public int getErrNo() {
        return errno;
    }

    public void setErrno(int error) {
        this.errno = error;
    }


    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getOk() {
        return ok;
    }

    public void setOk(int ok) {
        this.ok = ok;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public static class Data {

    }

}
