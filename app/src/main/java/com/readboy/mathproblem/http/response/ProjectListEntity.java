package com.readboy.mathproblem.http.response;

import java.util.List;

/**
 * Created by oubin on 2017/9/18.
 */

public class ProjectListEntity {

    /**
     * msg : success
     * data : [{"status":0,"updateTime":1503627895,"name":"求总数的应用题","courseId":66,"explain":"<p>指点迷津：<\/p><p>求总数的应用题：已知甲数是多少，乙数是多少，求甲乙两数的和是多少。<\/p><p>求两个数的和的应用题基本解法是将一个数加上另一个数。<\/p><p>在解答时关键是看要解决的问题是要把哪两个数合起来，然后就到题目中去找到这两个数对应的数量即可。<\/p>","explainAudio":"/resources/mathproblem/2017/68921/eaa5b5fcc53e4d0ccc12a30d60e70acc.mp3","module":1,"createTime":1503620549,"source":68921,"grade":1,"version":492,"example":[38924,48924],"exercises":[18925,28925,38925,48925,58925],"id":78922},{"status":0,"updateTime":1503645564,"name":"求剩余的应用题","courseId":66,"explain":"<p>指点迷津：<\/p><p>求剩余的应用题：从已知数中去掉一部分，求剩下的部分。<\/p><p>求剩余的应用题基本解法是从总数中减去其中的一部分数，余下的就是要求的数。<\/p>","explainAudio":"/resources/mathproblem/2017/68921/d51aeb5f4ef0205a4228259e4b2c447e.mp3","module":1,"createTime":1503620582,"source":68921,"version":492,"example":[58924,68924],"grade":1,"exercises":[68925,78925,88925,98925,108925],"id":88922},{"status":0,"updateTime":1503646325,"name":"求两个数相差多少的应用题","courseId":66,"explain":"<p>指点迷津：<\/p><p>求两个数相差多少的应用题：已知甲乙两数各是多少，求甲数比乙数多多少，或乙数比甲数少多少。<\/p><p>解题的方法是用较大的数减去较小的数，得到的就是相差数。<\/p>","explainAudio":"/resources/mathproblem/2017/68921/042901d2a7e5afe5777ef0e3ef7976e2.mp3","module":1,"createTime":1503620610,"source":68921,"version":492,"example":[78924,88924],"grade":1,"exercises":[118925,128925,138925,148925,158925],"id":98922},{"status":0,"updateTime":1503646744,"name":"求比一个数多几的数的应用题","courseId":66,"explain":"<p>指点迷津：<\/p><p>求比一个数多几的数的应用题：已知甲数是多少和乙数比甲数多多少，求乙数是多少。<\/p><p>重点在于：分析和理解求比一个数多几的数的数量关系。<\/p><p>难点在于：分清楚较大数是由和较小数同样多的部分与比较小数多的部分组成的。<\/p><p>较大数分成两部分，一部分和小数同样多，另一部分比小数多，要求较大的数，就要把这两部分合起来，所以用加法计算。<\/p>","explainAudio":"/resources/mathproblem/2017/68921/83afb5f8ff3d9345e82aba2f6e2d1eb7.mp3","module":1,"createTime":1503620686,"source":68921,"version":492,"example":[98924,108924],"grade":1,"exercises":[168925,178925,188925,198925,208925],"id":108922},{"status":0,"updateTime":1503647110,"name":"求比一个数少几的数的应用题","courseId":66,"explain":"<p>指点迷津：<\/p><p>求比一个数少几的数的应用题：已知甲数是多少，乙数比甲数少多少，求乙数是多少。<\/p><p>重点在于：分析和理解求比一个数少几的数的数量关系。<\/p><p>我们可以把较大数分成两部分，从大数中去掉大数比小数多的部分，就是小数与大数同样多的部分，也就是小数的数值；也可以把较小数假设和较大数同样多，再去掉比较大数少的部分就是较小数。<\/p><p>因此，求比一个数少几的数的应用题，用减法计算。<\/p>","explainAudio":"/resources/mathproblem/2017/68921/bac02f9ec6bd2148a8cdfce3d96276b4.mp3","module":1,"createTime":1503620727,"source":68921,"version":492,"example":[118924,128924],"grade":1,"exercises":[218925,228925,238925,248925,258925],"id":118922},{"status":0,"updateTime":1503647670,"name":"钟表和时间问题","courseId":66,"explain":"<p>指点迷津：<\/p><p>生活中我们要用到钟表，要解决和时间有关的知识，首先要认识钟面。<\/p><p>钟面上一共有12个数字，从1到12，把钟面分成了12格，通常有两根指针，较长的是分针，较短的是时针。<\/p><p>时针走一大格是1小时，分针走一大格是5分钟。<\/p><p>1小时＝60分。<\/p>","explainAudio":"/resources/mathproblem/2017/68921/f46489c41ef1233db96d2ec08f7e399e.mp3","module":1,"createTime":1503620759,"source":68921,"version":492,"example":[138924,148924],"grade":1,"exercises":[268925,278925,288925,298925,308925],"video":[{"from":"qpsp","name":"探秘\u201c行程\u201d之路__行程问题·钟表问题","iid":486318110,"filePath":"download/mp4qpsp","thumbnail":null,"fileName":"探秘(行程)之路_钟表问题.mp4","source":"全品视频","duration":null,"id":170626082824433250}],"id":128922}]
     * ok : 1
     */

    private String msg;
    private int ok;
    private int errno;
    private List<Data> data;

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

    public int getErrNo() {
        return errno;
    }

    public void setErrno(int error) {
        this.errno = error;
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
         * name : 求总数的应用题
         * courseId : 66
         * explain : <p>指点迷津：</p><p>求总数的应用题：已知甲数是多少，乙数是多少，求甲乙两数的和是多少。</p><p>求两个数的和的应用题基本解法是将一个数加上另一个数。</p><p>在解答时关键是看要解决的问题是要把哪两个数合起来，然后就到题目中去找到这两个数对应的数量即可。</p>
         * explainAudio : /resources/mathproblem/2017/68921/eaa5b5fcc53e4d0ccc12a30d60e70acc.mp3
         * module : 1
         * createTime : 1503620549
         * source : 68921
         * grade : 1
         * version : 492
         * example : [38924,48924]
         * exercises : [18925,28925,38925,48925,58925]
         * id : 78922
         * video : [{"from":"qpsp","name":"探秘\u201c行程\u201d之路__行程问题·钟表问题","iid":486318110,"filePath":"download/mp4qpsp","thumbnail":null,"fileName":"探秘(行程)之路_钟表问题.mp4","source":"全品视频","duration":null,"id":170626082824433250}]
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
        private int grade;
        private int version;
        private int id;
        private List<Integer> example;
        private List<Integer> exercises;
        private List<Video> video;

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

        public int getGrade() {
            return grade;
        }

        public void setGrade(int grade) {
            this.grade = grade;
        }

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
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

        public List<Integer> getExercises() {
            return exercises;
        }

        public void setExercises(List<Integer> exercises) {
            this.exercises = exercises;
        }

        public List<Video> getVideo() {
            return video;
        }

        public void setVideo(List<Video> video) {
            this.video = video;
        }

        public static class Video {
            /**
             * from : qpsp
             * name : 探秘“行程”之路__行程问题·钟表问题
             * iid : 486318110
             * filePath : download/mp4qpsp
             * thumbnail : null
             * fileName : 探秘(行程)之路_钟表问题.mp4
             * source : 全品视频
             * duration : null
             * id : 170626082824433250
             */

            private String from;
            private String name;
            private int iid;
            private String filePath;
            private Object thumbnail;
            private String fileName;
            private String source;
            private Object duration;
            private long id;

            public String getFrom() {
                return from;
            }

            public void setFrom(String from) {
                this.from = from;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getIid() {
                return iid;
            }

            public void setIid(int iid) {
                this.iid = iid;
            }

            public String getFilePath() {
                return filePath;
            }

            public void setFilePath(String filePath) {
                this.filePath = filePath;
            }

            public Object getThumbnail() {
                return thumbnail;
            }

            public void setThumbnail(Object thumbnail) {
                this.thumbnail = thumbnail;
            }

            public String getFileName() {
                return fileName;
            }

            public void setFileName(String fileName) {
                this.fileName = fileName;
            }

            public String getSource() {
                return source;
            }

            public void setSource(String source) {
                this.source = source;
            }

            public Object getDuration() {
                return duration;
            }

            public void setDuration(Object duration) {
                this.duration = duration;
            }

            public long getId() {
                return id;
            }

            public void setId(long id) {
                this.id = id;
            }
        }
    }
}
