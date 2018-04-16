package com.readboy.mathproblem.http.response;

import java.util.List;

/**
 * Created by oubin on 2017/9/19.
 */

public class MethodListEntity {

    /**
     * msg : success
     * data : [{"status":0,"updateTime":1503627895,"name":"观察法","courseId":66,"explain":"<p>指点迷津：<\/p><p>观察法在解答数学题时，第一步是观察。观察是基础，是发现问题、解决问题的首要步骤。<\/p><p>小学数学教材，特别重视培养观察力，把培养观察力作为开发与培养学生智力的第一步。<\/p><p>观察法，是通过观察题目中数字的变化规律及位置特点，条件与结论之间的关系，题目的结构特点及图形的特征，从而发现题目中的数量关系，把题目解答出来的一种解题方法。<\/p><p>观察要有次序，要看得仔细、看得真切，在观察中要动脑，要想出道理、找出规律。<\/p>","explainAudio":"/resources/mathproblem/2017/18921/1a11786049d0f36db30e9584f2298051.mp3","module":2,"createTime":1503561369,"source":18921,"version":492,"example":[18924],"grade":1,"id":38923},{"status":0,"updateTime":1503627895,"name":"尝试法","courseId":66,"explain":"<p>指点迷津：<\/p><p>尝试法解应用题时，按照自己认为可能的想法，通过尝试，探索规律，从而获得解题方法，叫做尝试法。<\/p><p>尝试法也叫\u201c尝试探索法\u201d。<\/p><p>一般来说，在尝试时可以提出假设、猜想，无论是假设或猜想，都要目的明确，尽可能恰当、合理，都要知道在假设、猜想和尝试过程中得到的结果是什么，从而减少尝试的次数，提高解题的效率。<\/p>","explainAudio":"/resources/mathproblem/2017/18921/e802127be2b0cd4e38d4bb9af0b17dd1.mp3","module":2,"createTime":1503561424,"source":18921,"version":492,"example":[28924],"grade":1,"id":48923}]
     * ok : 1
     */

    private String msg;
    private int ok;
    private List<Data> data;
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

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public static class Data {
        /**
         * status : 0
         * updateTime : 1503627895
         * name : 观察法
         * courseId : 66
         * explain : <p>指点迷津：</p><p>观察法在解答数学题时，第一步是观察。观察是基础，是发现问题、解决问题的首要步骤。</p><p>小学数学教材，特别重视培养观察力，把培养观察力作为开发与培养学生智力的第一步。</p><p>观察法，是通过观察题目中数字的变化规律及位置特点，条件与结论之间的关系，题目的结构特点及图形的特征，从而发现题目中的数量关系，把题目解答出来的一种解题方法。</p><p>观察要有次序，要看得仔细、看得真切，在观察中要动脑，要想出道理、找出规律。</p>
         * explainAudio : /resources/mathproblem/2017/18921/1a11786049d0f36db30e9584f2298051.mp3
         * module : 2
         * createTime : 1503561369
         * source : 18921
         * version : 492
         * example : [18924]
         * grade : 1
         * id : 38923
         */

        private int status;
        private int updateTime;
        private String name;
        private int courseId;
        private String explain;
        private String explainAudio;
        private int module;
        private int createTime;
        private int source;
        private int version;
        private int grade;
        private int id;
        private List<Integer> example;

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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getCourseId() {
            return courseId;
        }

        public void setCourseId(int courseId) {
            this.courseId = courseId;
        }

        public String getExplain() {
            return explain;
        }

        public void setExplain(String explain) {
            this.explain = explain;
        }

        public String getExplainAudio() {
            return explainAudio;
        }

        public void setExplainAudio(String explainAudio) {
            this.explainAudio = explainAudio;
        }

        public int getModule() {
            return module;
        }

        public void setModule(int module) {
            this.module = module;
        }

        public int getCreateTime() {
            return createTime;
        }

        public void setCreateTime(int createTime) {
            this.createTime = createTime;
        }

        public int getSource() {
            return source;
        }

        public void setSource(int source) {
            this.source = source;
        }

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }

        public int getGrade() {
            return grade;
        }

        public void setGrade(int grade) {
            this.grade = grade;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public List<Integer> getExample() {
            return example;
        }

        public void setExample(List<Integer> example) {
            this.example = example;
        }
    }
}
