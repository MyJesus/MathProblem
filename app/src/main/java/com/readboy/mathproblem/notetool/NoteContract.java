package com.readboy.mathproblem.notetool;

import android.provider.BaseColumns;

/**
 * Created by oubin on 2017/11/9.
 * 笔记数据库字段
 */

public class NoteContract {

    interface FileInfoColumns {
        String FILE = "file";
        String VERSION = "ver";
        String LENGTH = "len";
    }

    //TODO: 源代码不使用_id，用id替换
    interface NoteInfoColumns extends BaseColumns {
        String ID = "id";
        String SERIAL = "serial";
        String WIDTH = "width";
        String HEIGHT = "height";
        /**
         * 存放图片数据，value类型byte
         */
        String IMAGE = "img";

        int INDEX_ID = 0;
        int INDEX_SERIAL = 1;
        int INDEX_WIDTH = 2;
        int INDEX_HEIGHT = 3;
        int INDEX_IMAGE = 4;

    }

}
