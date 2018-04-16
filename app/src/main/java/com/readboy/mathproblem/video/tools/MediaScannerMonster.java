package com.readboy.mathproblem.video.tools;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;

/**
 * Created by oubin on 2017/11/22.
 */

public class MediaScannerMonster implements MediaScannerConnection.MediaScannerConnectionClient {

    private MediaScannerConnection connection;
    private String path;

    public MediaScannerMonster(Context context, String path){
        this.path = path;
        connection = new MediaScannerConnection(context, this);
        connection.connect();
    }

    @Override
    public void onMediaScannerConnected() {
        connection.scanFile(path, null);
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        connection.disconnect();
    }
}
