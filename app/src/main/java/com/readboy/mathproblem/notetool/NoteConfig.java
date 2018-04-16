package com.readboy.mathproblem.notetool;

import android.os.Environment;

import java.io.File;

/**
 * Created by oubin on 2017/9/28.
 */

public class NoteConfig {

    public static String DB_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() +
            File.separator + "MathProblem" + File.separator + "note";

    public static String DB_FILE_NAME = "笔记.db";

    public static String DB_FILE_ABSOLUTE_PATH = DB_PATH + File.separator + DB_FILE_NAME;

    public static String DB_VERSION = "20170928";

    /**
     * 一个笔记Id，最多可存放的长度。
     */
    static int MAX_COUNT_ONE_NOTE = 120;


}
