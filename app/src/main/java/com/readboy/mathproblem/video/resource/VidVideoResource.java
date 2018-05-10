package com.readboy.mathproblem.video.resource;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.aliyun.vodplayer.downloader.AliyunDownloadMediaInfo;
import com.readboy.aliyunplayerlib.utils.FileUtil;
import com.readboy.aliyunplayerlib.view.AliPlayerView;
import com.readboy.mathproblem.application.Constants;
import com.readboy.mathproblem.db.Favorite;
import com.readboy.mathproblem.db.Video;
import com.readboy.mathproblem.download.AliyunDownloadManagerWrapper;
import com.readboy.mathproblem.http.response.VideoInfoEntity;
import com.readboy.mathproblem.util.FileUtils;
import com.readboy.mathproblem.util.VideoUtils;
import com.readboy.mathproblem.video.tools.Constant;

import java.io.File;

/**
 * Created by oubin on 2018/4/19.
 */

public class VidVideoResource implements IVideoResource {
    private static final String TAG = "oubin_AliyunVResource";

    public static final String SCHEME = "vid";

    private VideoInfoEntity.VideoInfo mVideoInfo;
    private String mFavoriteName;

    public VidVideoResource(@NonNull VideoInfoEntity.VideoInfo videoInfo) {
        this.mVideoInfo = videoInfo;
//        this.mFavoriteName = FileUtils.getFileNameWithoutExtension(videoInfo.getVideoUri());
        this.mFavoriteName = videoInfo.getName();
    }

    @Override
    public void play(AliPlayerView aliPlayerView, long position) {
        Log.e(TAG, "play: position = " + position);
        if (VideoUtils.videoIsExist(mVideoInfo.getName())){
            Log.e(TAG, "play: video is downloaded.");
        }
        aliPlayerView.playWithVid(mVideoInfo.getVid());
        aliPlayerView.seekTo(position);
    }

    @Override
    public String getVideoName() {
//        return FileUtils.getFileNameWithoutExtension(mVideoInfo.getVideoUri());
        return mVideoInfo.getName();
    }

    @Override
    public Uri getVideoUri() {
        String vid = "dfasdfas";
        String uri = SCHEME + "://" + mVideoInfo.getVid();
        return Uri.parse(uri);
    }

    @Override
    public boolean isDownloaded() {
//        File file = new File(Constants.ALIYUN_DOWNLOAD_DIR + File.separator
//                + FileUtils.getFileName(mVideoInfo.getVideoUri()));
        File file = new File(Constants.getVideoPath(mVideoInfo.getName()));
        Log.e(TAG, "isDownloaded: file exits = " + file.exists());
        return file.exists();
    }

    @Override
    public boolean isDownloading() {
        return AliyunDownloadManagerWrapper.getInstance().isDownloading(mVideoInfo.getVid());
    }

    @Override
    public boolean downloadVideo() {
        AliyunDownloadManagerWrapper.getInstance().prepareDownload(mVideoInfo);
        return true;
    }

    @Override
    public boolean isFavorite(ContentResolver resolver) {
        Log.d(TAG, "isFavorite: mFavoriteName = " + mFavoriteName);
        return Favorite.hasFavorite(resolver, mFavoriteName);
    }

    @Override
    public boolean favoriteVideo(ContentResolver resolver) {
        if (TextUtils.isEmpty(mFavoriteName)) {
            return false;
        }
        Favorite favorite = Favorite.convertFavorite(mVideoInfo);
        Log.e(TAG, "favoriteVideo: favorite= " + favorite.toString());
        return Favorite.insert(resolver, favorite) != null;
    }

    @Override
    public boolean unFavoriteVideo(ContentResolver resolver) {
        if (TextUtils.isEmpty(mFavoriteName)) {
            return false;
        }
        return Favorite.delete(resolver, mFavoriteName);
    }

    @Override
    public String toString() {
        return "VidVideoResource{" +
                "mVideoInfo=" + mVideoInfo +
                "mFavoriteName = " + mFavoriteName +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mVideoInfo, flags);
        dest.writeString(this.mFavoriteName);
    }

    protected VidVideoResource(Parcel in) {
        this.mVideoInfo = in.readParcelable(VideoInfoEntity.VideoInfo.class.getClassLoader());
        this.mFavoriteName = in.readString();
    }

    public static final Creator<VidVideoResource> CREATOR = new Creator<VidVideoResource>() {
        @Override
        public VidVideoResource createFromParcel(Parcel source) {
            return new VidVideoResource(source);
        }

        @Override
        public VidVideoResource[] newArray(int size) {
            return new VidVideoResource[size];
        }
    };
}
