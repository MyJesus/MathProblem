package com.readboy.mathproblem.video.resource;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Parcel;

import com.readboy.aliyunplayerlib.view.AliPlayerView;
import com.readboy.mathproblem.util.FileUtils;

import java.io.File;

/**
 * Created by oubin on 2018/4/19.
 */

public class LocalPathVideoResource implements IVideoResource {

    private String mPath;

    public LocalPathVideoResource(String path) {
        this.mPath = path;
    }

    @Override
    public void play(AliPlayerView aliPlayerView, long position) {
        aliPlayerView.playWithPath(mPath);
        if (position > 0) {
            aliPlayerView.seekTo(position);
        }
    }

    @Override
    public String getVideoName() {
        return FileUtils.getFileNameWithoutExtension(mPath);
    }

    @Override
    public Uri getVideoUri() {
        return Uri.parse(mPath);
    }

    @Override
    public boolean isDownloaded() {
        File file = new File(mPath);
        return file.exists();
    }

    @Override
    public boolean isDownloading() {
        return false;
    }

    @Override
    public boolean downloadVideo() {
        return false;
    }

    @Override
    public boolean isFavorite(ContentResolver resolver) {
        return false;
    }

    @Override
    public boolean favoriteVideo(ContentResolver resolver) {
        return false;
    }

    @Override
    public boolean unFavoriteVideo(ContentResolver resolver) {
        return false;
    }

    @Override
    public String toString() {
        return "LocalPathVideoResource{" +
                "mPath='" + mPath + '\'' +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mPath);
    }

    protected LocalPathVideoResource(Parcel in) {
        this.mPath = in.readString();
    }

    public static final Creator<LocalPathVideoResource> CREATOR = new Creator<LocalPathVideoResource>() {
        @Override
        public LocalPathVideoResource createFromParcel(Parcel source) {
            return new LocalPathVideoResource(source);
        }

        @Override
        public LocalPathVideoResource[] newArray(int size) {
            return new LocalPathVideoResource[size];
        }
    };
}
