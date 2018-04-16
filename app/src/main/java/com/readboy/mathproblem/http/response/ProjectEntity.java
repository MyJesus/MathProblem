package com.readboy.mathproblem.http.response;

import java.util.List;

/**
 * Created by oubin on 2017/9/19.
 */

public class ProjectEntity extends BaseResponse<ProjectEntity.Project> {

    public static class Project extends BaseResponse.Data {

        /**
         * status : 0
         * updateTime : 1503647670
         * name : 钟表和时间问题
         * "video2": [486318110]
         * courseId : 66
         * explain : <p>指点迷津：</p><p>生活中我们要用到钟表，要解决和时间有关的知识，首先要认识钟面。</p><p>钟面上一共有12个数字，从1到12，把钟面分成了12格，通常有两根指针，较长的是分针，较短的是时针。</p><p>时针走一大格是1小时，分针走一大格是5分钟。</p><p>1小时＝60分。</p>
         * explainAudio : /resources/mathproblem/2017/68921/f46489c41ef1233db96d2ec08f7e399e.mp3
         * module : 1
         * createTime : 1503620759
         * source : 68921
         * version : 492
         * example : [{"category":9,"status":0,"updateTime":1503647171,"courseId":66,"from":64,"grade":1,"solution":"<p>从上班到下班，时针从8走到12走了4大格，所以妈妈在单位工作的时间是4小时。<\/p>","createTime":1503647171,"content":"<p>妈妈早上8点钟上班，中午12点下班，妈妈在单位工作了多少小时？<\/p>","difficulty":0,"version":492,"role":0,"answer":"<p>解：12－8＝4（小时）<\/p><p>答：妈妈在单位工作了4小时。<\/p>","opinion":[],"type":203,"id":138924,"subject":2},{"category":9,"status":0,"updateTime":1503647231,"courseId":66,"from":64,"grade":1,"solution":"<p>火车7点40分开车，步行要20分钟，所以至少要比7点40提前20分钟出发，7点40分倒退20分钟是7点20分，所以小红最迟要7点20分出发。<\/p>","createTime":1503647231,"content":"<p>小红从家到火车站步行要20分钟，火车7点40分开车，到火车站坐火车她最迟要几点从家里出发？<\/p>","difficulty":0,"version":492,"role":0,"answer":"<p>他最迟要7点20分出发。<\/p>","opinion":[],"type":203,"id":148924,"subject":2}]
         * grade : 1
         * exercises : [{"category":1,"status":0,"updateTime":1503647311,"courseId":66,"from":65,"grade":1,"solution":"<p>小明用的时间比小莉少，所以小明要快些。<\/p>","createTime":1503647311,"accessory":[{"type":101,"options":["小莉","小明","一样快","无法判断"]}],"content":"<p>小明写36个字要１分钟，小莉写36个字要1分25秒，他们两个谁写得快一些？<\/p>","difficulty":0,"version":492,"role":0,"correctAnswer":["B"],"opinion":[],"type":101,"id":268925,"subject":2},{"category":1,"status":0,"updateTime":1503647355,"courseId":66,"from":65,"grade":1,"solution":"<p>17－9＝8（小时）<\/p>","createTime":1503647355,"accessory":[{"type":101,"options":["7","8","9","10"]}],"content":"<p>银行一天的营业时间是9:00－17:00，银行一天营业几小时？<\/p>","difficulty":0,"version":492,"role":0,"correctAnswer":["B"],"opinion":[],"type":101,"id":278925,"subject":2},{"category":1,"status":0,"updateTime":1503647393,"courseId":66,"from":65,"grade":1,"solution":"<p>22－14＝8（小时）<\/p>","createTime":1503647393,"accessory":[{"type":101,"options":["8","9","10","11"]}],"content":"<p>某超市收银员从14:00工作到22:00，工作了几小时？<\/p>","difficulty":0,"version":492,"role":0,"correctAnswer":["A"],"opinion":[],"type":101,"id":288925,"subject":2},{"category":1,"status":0,"updateTime":1503647454,"courseId":66,"from":65,"grade":1,"solution":"<p>从钟面上看，8点向前走40分钟是8点40分。<\/p>","createTime":1503647454,"accessory":[{"type":101,"options":["9:00","8:30","8:40","8:45"]}],"content":"<p>上午第一节课是8:00开始上课，一节课40分钟，上午第一节课是几点几分下课？<\/p>","difficulty":0,"version":492,"role":0,"correctAnswer":["C"],"opinion":[],"type":101,"id":298925,"subject":2},{"category":1,"status":0,"updateTime":1503647514,"courseId":66,"from":65,"grade":1,"solution":"<p>从钟面上看，7点20分向前走15分钟是7点35分。<\/p>","createTime":1503647514,"accessory":[{"type":101,"options":["5点35分","6点20分","6点35分","7点35分"]}],"content":"<p>小华7:20从家出门到学校用了15分钟，到学校的时间是几点几分？<\/p>","difficulty":0,"version":492,"role":0,"correctAnswer":["D"],"opinion":[],"type":101,"id":308925,"subject":2}]
         * video : [{"from":"qpsp","name":"探秘\u201c行程\u201d之路__行程问题·钟表问题","iid":486318110,"filePath":"download/mp4qpsp","thumbnail":null,"fileName":"探秘(行程)之路_钟表问题.mp4","source":"全品视频","duration":null,"id":170626082824433250}]
         * id : 128922
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
        private List<Integer> video2;
        private List<Example> example;
        private List<Exercises> exercises;
//        private List<Video> video;
        private List<VideoInfoEntity.VideoInfo> videoInfoList;

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

        public List<Integer> getVideo2() {
            return video2;
        }

        public void setVideo2(List<Integer> video2) {
            this.video2 = video2;
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

        public List<Example> getExample() {
            return example;
        }

        public void setExample(List<Example> example) {
            this.example = example;
        }

        public List<Exercises> getExercises() {
            return exercises;
        }

        public void setExercises(List<Exercises> exercises) {
            this.exercises = exercises;
        }

//        public List<Video> getVideo() {
//            return video;
//        }
//
//        public void setVideo(List<Video> video) {
//            this.video = video;
//        }

        public List<VideoInfoEntity.VideoInfo> getVideoInfoList() {
            return videoInfoList;
        }

        public void setVideoInfoList(List<VideoInfoEntity.VideoInfo> video2List) {
            this.videoInfoList = video2List;
        }

        public static class Example {
            /**
             * category : 9
             * status : 0
             * updateTime : 1503647171
             * courseId : 66
             * from : 64
             * grade : 1
             * solution : <p>从上班到下班，时针从8走到12走了4大格，所以妈妈在单位工作的时间是4小时。</p>
             * createTime : 1503647171
             * content : <p>妈妈早上8点钟上班，中午12点下班，妈妈在单位工作了多少小时？</p>
             * difficulty : 0
             * version : 492
             * role : 0
             * answer : <p>解：12－8＝4（小时）</p><p>答：妈妈在单位工作了4小时。</p>
             * opinion : []
             * type : 203
             * id : 138924
             * subject : 2
             */

            private int category;
            private int status;
            private int updateTime;
            private int courseId;
            private int from;
            private int grade;
            private String solution;
            private int createTime;
            private String content;
            private int difficulty;
            private int version;
            private int role;
            private String answer;
            private int type;
            private int id;
            private int subject;
            private List<?> opinion;

            public int getCategory() {
                return category;
            }

            public void setCategory(int category) {
                this.category = category;
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

            public int getCourseId() {
                return courseId;
            }

            public void setCourseId(int courseId) {
                this.courseId = courseId;
            }

            public int getFrom() {
                return from;
            }

            public void setFrom(int from) {
                this.from = from;
            }

            public int getGrade() {
                return grade;
            }

            public void setGrade(int grade) {
                this.grade = grade;
            }

            public String getSolution() {
                return solution;
            }

            public void setSolution(String solution) {
                this.solution = solution;
            }

            public int getCreateTime() {
                return createTime;
            }

            public void setCreateTime(int createTime) {
                this.createTime = createTime;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public int getDifficulty() {
                return difficulty;
            }

            public void setDifficulty(int difficulty) {
                this.difficulty = difficulty;
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

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getSubject() {
                return subject;
            }

            public void setSubject(int subject) {
                this.subject = subject;
            }

            public List<?> getOpinion() {
                return opinion;
            }

            public void setOpinion(List<?> opinion) {
                this.opinion = opinion;
            }
        }

        public static class Exercises implements Cloneable {
            /**
             * category : 1
             * status : 0
             * updateTime : 1503647311
             * courseId : 66
             * from : 65
             * grade : 1
             * solution : <p>小明用的时间比小莉少，所以小明要快些。</p>
             * createTime : 1503647311
             * accessory : [{"type":101,"options":["小莉","小明","一样快","无法判断"]}]
             * content : <p>小明写36个字要１分钟，小莉写36个字要1分25秒，他们两个谁写得快一些？</p>
             * difficulty : 0
             * version : 492
             * role : 0
             * correctAnswer : ["B"]
             * opinion : []
             * type : 101
             * id : 268925
             * subject : 2
             */

            private int category;
            private int status;
            private int updateTime;
            private int courseId;
            private int from;
            private int grade;
            private String solution;
            private int createTime;
            private String content;
            private int difficulty;
            private int version;
            private int role;
            private int type;
            private int id;
            private int subject;
            private List<Accessory> accessory;
            private List<String> correctAnswer;
            private List<String> opinion;

            public int getCategory() {
                return category;
            }

            public void setCategory(int category) {
                this.category = category;
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

            public int getCourseId() {
                return courseId;
            }

            public void setCourseId(int courseId) {
                this.courseId = courseId;
            }

            public int getFrom() {
                return from;
            }

            public void setFrom(int from) {
                this.from = from;
            }

            public int getGrade() {
                return grade;
            }

            public void setGrade(int grade) {
                this.grade = grade;
            }

            public String getSolution() {
                return solution;
            }

            public void setSolution(String solution) {
                this.solution = solution;
            }

            public int getCreateTime() {
                return createTime;
            }

            public void setCreateTime(int createTime) {
                this.createTime = createTime;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public int getDifficulty() {
                return difficulty;
            }

            public void setDifficulty(int difficulty) {
                this.difficulty = difficulty;
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

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getSubject() {
                return subject;
            }

            public void setSubject(int subject) {
                this.subject = subject;
            }

            public List<Accessory> getAccessory() {
                return accessory;
            }

            public void setAccessory(List<Accessory> accessory) {
                this.accessory = accessory;
            }

            public List<String> getCorrectAnswer() {
                return correctAnswer;
            }

            public void setCorrectAnswer(List<String> correctAnswer) {
                this.correctAnswer = correctAnswer;
            }

            public List<String> getOpinion() {
                return opinion;
            }

            public void setOpinion(List<String> opinion) {
                this.opinion = opinion;
            }

            public static class Accessory {
                /**
                 * type : 101
                 * options : ["小莉","小明","一样快","无法判断"]
                 */

                private int type;
                private List<String> options;

                public int getType() {
                    return type;
                }

                public void setType(int type) {
                    this.type = type;
                }

                public List<String> getOptions() {
                    return options;
                }

                public void setOptions(List<String> options) {
                    this.options = options;
                }
            }

            @Override
            public Object clone(){
                Exercises exercise = null;
                try {
                    exercise = (Exercises) super.clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
                return exercise;
            }
        }
    }

}
