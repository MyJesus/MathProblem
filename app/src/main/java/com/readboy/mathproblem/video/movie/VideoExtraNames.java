package com.readboy.mathproblem.video.movie;

/**
 * Created by oubin on 2017/10/12.
 */

public interface VideoExtraNames {

    //数据为String类型, 完整本地视频路径
    public static final String EXTRA_PATH = "path";
    //数据为int类型，
    public static final String EXTRA_URL_CONFIG = "urlConfig";
    //String类型，网络播放视频的地址路径
    public static final String EXTRA_URL = "url";

    //开始播放时的进度
    //long,
    public static final String EXTRA_SEEK_POSITION = "position";
    /**
     * int, 在视频列表时的位置
     */
    public static final String EXTRA_INDEX = "index";
    //ArrayList<String>, 视频列表
    public static final String EXTRA_MEDIA_LIST = "medialist";

    String EXTRA_DURATION = "duration";

    //是否为绝对路径或者url，数据类型为boolean
    String EXTRA_ABSOLUTE_PATH = "absolute_path";
    //String类型，非网站网络路径，需要鉴权获取完整路径。
    String EXTRA_URI = "uri";
    //boolean类型，是否需要鉴权。
    String EXTRA_NEED_AUTH = "auth";

    //是否显示同步练习按钮，value类型：boolean
    String EXTRA_EXERCISE_ENABLE = "exercise";

    //是否有练习题，value类型：boolean
    String EXTRA_HAS_EXERCISE = "has_exercise";

    //课程，练习题相关
    String EXTRA_PROJECT_SUBJECT = "subject";
    String EXTRA_PROJECT_GRADE = "grade";
    String EXTRA_PROJECT_INDEX = "project_index";

    /**
     * 全屏变小屏播放处理方式。
     */
    String EXTRA_FINISH_TYPE = "finish_type";

    String EXTRA_VIDEO_INFO_LIST = "video_info_list";

    String EXTRA_VIDEO_RESOURCE = "video_resource";
    public int TYPE_SET_RESULT = 0;
    public int TYPE_GOTO = 1;

}
