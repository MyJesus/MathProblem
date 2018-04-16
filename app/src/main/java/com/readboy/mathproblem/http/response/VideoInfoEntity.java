package com.readboy.mathproblem.http.response;

import java.util.List;

/**
 * Created by oubin on 2017/10/23.
 */

public class VideoInfoEntity {

    /**
     * msg : success
     * data : [{"videoUri":"/download/ZhiShiDian/video/语文/声母的分类XXYW002_知识点讲解.mp4","name":"声母的分类","id":16338120,"fileSize":21333486,"duration":363.99,"thumbnailUrl":"http://contres.readboy.com/video_resource/wkt/thumbnail/ed/c8/93/edc8931bc33a39d66c20bc4973fa604e.jpg"}]
     * "data": [{
     * "videoUri": "/download/ZhiShiDian/video/语文/声母的分类XXYW002_知识点讲解.mp4",
     * "name": "声母的分类",
     * "id": 16338120,
     * "fileSize": 21333486,
     * "duration": 363.99,
     * "thumbnailUrl": "http://contres.readboy.com/video_resource/wkt/thumbnail/ed/c8/93/edc8931bc33a39d66c20bc4973fa604e.jpg"
     * }]
     * ok : 1
     */

    private String msg;
    private int ok;
    private List<VideoInfo> data;

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

    public List<VideoInfo> getData() {
        return data;
    }

    public void setData(List<VideoInfo> data) {
        this.data = data;
    }

    public static class VideoInfo {
        /**
         * videoUri : /download/ZhiShiDian/video/语文/声母的分类XXYW002_知识点讲解.mp4
         * name : 声母的分类
         * id : 16338120
         * fileSize : 21333486
         * duration : 363.99
         * thumbnailUrl : http://contres.readboy.com/video_resource/wkt/thumbnail/ed/c8/93/edc8931bc33a39d66c20bc4973fa604e.jpg
         */

        private String videoUri;
        private String name;
        private int id;
        private int fileSize;
        private double duration;
        private String thumbnailUrl;
        private String url;

        public String getVideoUri() {
            return videoUri;
        }

        public void setVideoUri(String videoUri) {
            this.videoUri = videoUri;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getFileSize() {
            return fileSize;
        }

        public void setFileSize(int fileSize) {
            this.fileSize = fileSize;
        }

        public double getDuration() {
            return duration;
        }

        public void setDuration(double duration) {
            this.duration = duration;
        }

        public String getThumbnailUrl() {
            return thumbnailUrl;
        }

        public void setThumbnailUrl(String thumbnailUrl) {
            this.thumbnailUrl = thumbnailUrl;
        }

        /**
         *
         * @return 可能为空，因为没有经过鉴权。
         */
        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        @Override
        public String toString() {
            return "VideoInfo:{" +
                    "videoUri='" + videoUri + '\'' +
                    ", name='" + name + '\'' +
                    ", id=" + id + '\'' +
                    ", fileSize=" + fileSize + '\'' +
                    ", duration=" + duration + '\'' +
                    ", thumbnailUrl='" + thumbnailUrl + '\'' +
                    ", url='" + url + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "VideoInfoEntity:{" +
                "msg='" + msg + '\'' +
                ", ok=" + ok +
                ", data=" + data +
                '}';
    }
}
