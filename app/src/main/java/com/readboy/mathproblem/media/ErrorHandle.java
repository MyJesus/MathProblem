/* Copyright Statement:
 *
 * This software/firmware and related documentation ("MediaTek Software") are
 * protected under relevant copyright laws. The information contained herein is
 * confidential and proprietary to MediaTek Inc. and/or its licensors. Without
 * the prior written permission of MediaTek inc. and/or its licensors, any
 * reproduction, modification, use or disclosure of MediaTek Software, and
 * information contained herein, in whole or in part, shall be strictly
 * prohibited.
 *
 * MediaTek Inc. (C) 2016. All rights reserved.
 *
 * BY OPENING THIS FILE, RECEIVER HEREBY UNEQUIVOCALLY ACKNOWLEDGES AND AGREES
 * THAT THE SOFTWARE/FIRMWARE AND ITS DOCUMENTATIONS ("MEDIATEK SOFTWARE")
 * RECEIVED FROM MEDIATEK AND/OR ITS REPRESENTATIVES ARE PROVIDED TO RECEIVER
 * ON AN "AS-IS" BASIS ONLY. MEDIATEK EXPRESSLY DISCLAIMS ANY AND ALL
 * WARRANTIES, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NONINFRINGEMENT. NEITHER DOES MEDIATEK PROVIDE ANY WARRANTY WHATSOEVER WITH
 * RESPECT TO THE SOFTWARE OF ANY THIRD PARTY WHICH MAY BE USED BY,
 * INCORPORATED IN, OR SUPPLIED WITH THE MEDIATEK SOFTWARE, AND RECEIVER AGREES
 * TO LOOK ONLY TO SUCH THIRD PARTY FOR ANY WARRANTY CLAIM RELATING THERETO.
 * RECEIVER EXPRESSLY ACKNOWLEDGES THAT IT IS RECEIVER'S SOLE RESPONSIBILITY TO
 * OBTAIN FROM ANY THIRD PARTY ALL PROPER LICENSES CONTAINED IN MEDIATEK
 * SOFTWARE. MEDIATEK SHALL ALSO NOT BE RESPONSIBLE FOR ANY MEDIATEK SOFTWARE
 * RELEASES MADE TO RECEIVER'S SPECIFICATION OR TO CONFORM TO A PARTICULAR
 * STANDARD OR OPEN FORUM. RECEIVER'S SOLE AND EXCLUSIVE REMEDY AND MEDIATEK'S
 * ENTIRE AND CUMULATIVE LIABILITY WITH RESPECT TO THE MEDIATEK SOFTWARE
 * RELEASED HEREUNDER WILL BE, AT MEDIATEK'S OPTION, TO REVISE OR REPLACE THE
 * MEDIATEK SOFTWARE AT ISSUE, OR REFUND ANY SOFTWARE LICENSE FEES OR SERVICE
 * CHARGE PAID BY RECEIVER TO MEDIATEK FOR SUCH MEDIATEK SOFTWARE AT ISSUE.
 *
 * The following software/firmware and/or related documentation ("MediaTek
 * Software") have been modified by MediaTek Inc. All revisions are subject to
 * any receiver's applicable license agreements with MediaTek Inc.
 */
package com.readboy.mathproblem.media;

import android.app.Activity;
import android.content.Context;

/**
 * M: class ErrorHandle--using to show error information,
 *  any error info should call this class to show.
 */
class ErrorHandle {
    // receive EJECT/UNMOUNTED broadcast when RecordingFileList
    public static final int ERROR_SD_UNMOUNTED_ON_FILE_LIST = 0;
    // receive EJECT/UNMOUNTED broadcast when record
    public static final int ERROR_SD_UNMOUNTED_ON_RECORD = 1;
    // when record, storage is becoming full
    public static final int ERROR_STORAGE_FULL_WHEN_RECORD = 2;
    // when launch SoundRecorder, storage is already full
    public static final int ERROR_STORAGE_FULL_WHEN_LAUNCH = 3;
    // when launch SoundRecorder or click record button, no SD card mounted
    public static final int ERROR_NO_SD = 4;
    // when record, MediaRecorder is occupied by other application
    public static final int ERROR_RECORDER_OCCUPIED = 5;
    // when record, catch other exceptions
    public static final int ERROR_RECORDING_FAILED = 6;
    // when play, can not valuseAt audio focus
    public static final int ERROR_PLAYER_OCCUPIED = 7;
    // when play, catch other exceptions
    public static final int ERROR_PLAYING_FAILED = 8;
    // when delete recording file, failed to access data base
    public static final int ERROR_FILE_DELETED_WHEN_PLAY = 9;
    // when create recording file, catch other exceptions
    public static final int ERROR_CREATE_FILE_FAILED = 10;
    // when save file, fail to access data base
    public static final int ERROR_SAVE_FILE_FAILED = 11;
    // when delete file, fail to access data base
    public static final int ERROR_DELETING_FAILED = 12;
    // when list recording files, fail to access data base
    public static final int ERROR_ACCESSING_DB_FAILED_WHEN_QUERY = 13;
    // receive EJECT/UNMOUNTED broadcast when idle/play in SoundRecorder
    public static final int ERROR_SD_UNMOUNTED_WHEN_IDLE = 14;

    public static final String ERROR_DIALOG_TAG = "error_dialog";
    private static final String TAG = "SR/ErrorHandle";

    /**
     * static method, use to show error information(Toast/DialogFragment)
     * according to errorCode parameter.
     *
     * @param activity
     *            the activity which should show error information, using to
     *            getFragmentManager
     * @param errorCode
     *            the code of occur error, defined in ErrorHandle
     */
    public static void showErrorInfo(Activity activity, int errorCode) {

    }

    public static void showErrorInfoInToast(Context context, int errorCode) {

    }
}