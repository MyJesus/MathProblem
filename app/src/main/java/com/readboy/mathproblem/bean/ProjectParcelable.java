package com.readboy.mathproblem.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.readboy.mathproblem.http.HttpConfig;

/**
 * Created by oubin on 2017/9/20.
 * @author oubin
 */

public class ProjectParcelable implements Parcelable {

    public static final String EXTRA_PROJECT_PARCELABLE = "project";
    public static final int TYPE_GUIDE = 0;
    public static final int TYPE_METHOD = 1;

    private int videoIndex;
    private int seekPosition;
    private int position;
    private int projectId;
    private int type;
    /**
     * 0代表一年级
     */
    private int grade;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.position);
        dest.writeInt(this.projectId);
        dest.writeInt(this.type);
        dest.writeInt(this.grade);
        dest.writeInt(this.videoIndex);
        dest.writeInt(this.seekPosition);
    }

    public ProjectParcelable() {
    }

    public ProjectParcelable(int position, int projectId, int type, int grade) {
        this.position = position;
        this.projectId = projectId;
        this.type = type;
        this.grade = grade;
    }

    protected ProjectParcelable(Parcel in) {
        this.position = in.readInt();
        this.projectId = in.readInt();
        this.type = in.readInt();
        this.grade = in.readInt();
        this.videoIndex = in.readInt();
        this.seekPosition = in.readInt();
    }

    public static final Creator<ProjectParcelable> CREATOR = new Creator<ProjectParcelable>() {
        @Override
        public ProjectParcelable createFromParcel(Parcel source) {
            return new ProjectParcelable(source);
        }

        @Override
        public ProjectParcelable[] newArray(int size) {
            return new ProjectParcelable[size];
        }
    };

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

//    public int getProjectId() {
//        return projectId;
//    }
//
//    public void setProjectId(int projectId) {
//        this.projectId = projectId;
//    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getTypeString() {
        return type == TYPE_GUIDE ? "guide" : "method";
    }

    public String getTypeListString() {
        return type == TYPE_GUIDE ? HttpConfig.GUIDE_LIST : HttpConfig.METHOD_LIST;
    }

    public int getVideoIndex() {
        return videoIndex;
    }

    public void setVideoIndex(int videoIndex) {
        this.videoIndex = videoIndex;
    }

    public int getSeekPosition() {
        return seekPosition;
    }

    public void setSeekPosition(int seekPosition) {
        this.seekPosition = seekPosition;
    }

    @Override
    public String toString() {
        return "ProjectParcelable{" +
                "videoIndex=" + videoIndex +
                ", seekPosition=" + seekPosition +
                ", position=" + position +
                ", projectId=" + projectId +
                ", type=" + type +
                ", grade=" + grade +
                '}';
    }
}
