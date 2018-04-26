package com.readboy.mathproblem.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.readboy.mathproblem.R;
import com.readboy.mathproblem.activity.StudyActivity;
import com.readboy.mathproblem.application.Constants;
import com.readboy.mathproblem.bean.ProjectHolder;
import com.readboy.mathproblem.cache.CacheEngine;
import com.readboy.mathproblem.http.response.VideoInfoEntity;
import com.readboy.mathproblem.util.BitmapUtils;
import com.readboy.mathproblem.util.HtmlRegexUtils;
import com.readboy.mathproblem.video.movie.VideoExtraNames;
import com.readboy.mathproblem.video.proxy.VideoProxy;

import java.util.List;
import java.util.Locale;

/**
 * Created by oubin on 2017/9/1.
 */

public class ProjectViewHolder extends BaseViewHolder<ProjectHolder> implements View.OnClickListener {
    private static final String TAG = "ProjectViewHolder";

    private Context mContext;
    private TextView mIdTv;
    private LinearLayout mScoreParent;
    private TextView mNameTv;
    private TextView mExplain;
    private View mStatusWait;
    private View mStatusDownloading;
    private View mPlayVideoBtn;
    //    private List<Integer> videoList;
    private List<VideoInfoEntity.VideoInfo> mVideoInfoList;
    private AnimationDrawable downloadingAnimation;
    private View mSpace;

    public ProjectViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(v -> handlerInnerItemClickEvent());
        mContext = itemView.getContext();
        mIdTv = (TextView) itemView.findViewById(R.id.project_id);
        mScoreParent = (LinearLayout) itemView.findViewById(R.id.project_score_group);
        mNameTv = (TextView) itemView.findViewById(R.id.project_name);
        mNameTv.setOnClickListener(this);
        mStatusWait = itemView.findViewById(R.id.project_video_status_wait);
        mStatusWait.setOnClickListener(this);
        mStatusDownloading = itemView.findViewById(R.id.project_video_download);
        mStatusDownloading.setOnClickListener(this);
        downloadingAnimation = (AnimationDrawable) mStatusDownloading.getBackground();
        mPlayVideoBtn = itemView.findViewById(R.id.play_video);
        mPlayVideoBtn.setOnClickListener(this);
        mExplain = (TextView) itemView.findViewById(R.id.project_explain);
        mSpace = itemView.findViewById(R.id.space);
    }

    @Override
    public void bindView(int position, ProjectHolder data) {
        this.mData = data;
        mIdTv.setText(String.format(Locale.CHINA, "%02d", position + 1));
        updateScoreParent(data.getScore());
        mNameTv.setText(data.getName());
//        String regex = "<br/>";
//        String html = data.getExplain().replaceAll(regex, "");
//        Log.e(TAG, "bindView: explain = " + data.getExplain());
        String temp = HtmlRegexUtils.filterHtml(data.getExplain());
        //去掉空行，数据防御
        temp = temp.replaceAll("(?m)^\\s*$"+System.lineSeparator(), "");
//        Log.e(TAG, "bindView: temp = " + temp);
//        html = html.replaceAll("<p>", "");
//        html = html.replaceAll("</p>", "<br />");
//        Log.e(TAG, "bindView: data = " + data.getExplain());
//        Log.e(TAG, "bindView: html = " + html);
//        Log.e(TAG, "bindView: HtmlRegexUtil = " + HtmlRegexUtils.filterHtmlTag(data.getExplain(), "p"));
//        Log.e(TAG, "bindView: regex = " + HtmlRegexUtils.filterHtmlPTag(data.getExplain()));
//        ViewUtils.setText(html, mExplain);
        mExplain.setText(temp);
        mVideoInfoList = data.getVideoInfoList();
        if (data.getVideoStatus() == ProjectHolder.VIDEO_STATUS_NONE) {
            mPlayVideoBtn.setVisibility(View.GONE);
            mSpace.setVisibility(View.GONE);
        } else {
            mPlayVideoBtn.setVisibility(View.VISIBLE);
            mSpace.setVisibility(View.VISIBLE);
        }
//        switch (data.getVideoStatus()) {
//            case ProjectHolder.VIDEO_STATUS_DOWNLOADING:
//                mStatusWait.setVisibility(View.GONE);
//                mStatusCompleted.setVisibility(View.GONE);
//                mStatusDownloading.setVisibility(View.VISIBLE);
//                downloadingAnimation.start();
//                break;
//            case ProjectHolder.VIDEO_STATUS_WAIT:
//                downloadingAnimation.stop();
//                mStatusDownloading.setVisibility(View.GONE);
//                mStatusCompleted.setVisibility(View.GONE);
//                mStatusWait.setVisibility(View.VISIBLE);
//                break;
//            case ProjectHolder.VIDEO_STATUS_COMPLETED:
//                mStatusDownloading.setVisibility(View.GONE);
//                mStatusWait.setVisibility(View.GONE);
//                mStatusCompleted.setVisibility(View.VISIBLE);
//                break;
//            default:
//                mStatusDownloading.setVisibility(View.GONE);
//                mStatusCompleted.setVisibility(View.GONE);
//                mStatusWait.setVisibility(View.GONE);
//                break;
//        }
    }

    private void updateScoreParent(int score) {
        for (int i = 0; i < score; i++) {
            mScoreParent.getChildAt(i)
                    .setBackgroundResource(BitmapUtils
                            .getResID(mContext, Constants.Drawable.STAR_POSITIVE, i));
        }
        for (int i = score; i < 5; i++) {
            mScoreParent.getChildAt(i)
                    .setBackgroundResource(
                            BitmapUtils.getResID(mContext, Constants.Drawable.STAR_NEGATIVE, i));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.project_video_status_wait:
                for (VideoInfoEntity.VideoInfo videoInfo : mVideoInfoList) {

                }
                break;
            case R.id.play_video:
                gotoMovieActivity();
                break;
            case R.id.project_name:
                handlerInnerItemClickEvent();
                break;
            case R.id.project_video_download:
                //do nothing. 防止响应onItemClick事件。
                break;
            default:
                break;
        }
    }

    private void gotoStudyActivity() {
        mContext.startActivity(new Intent(mContext, StudyActivity.class));
    }

    private void gotoMovieActivity() {
//        ProjectEntity.Project project = CacheEngine.getAndSetCurrentIndex(getAdapterPosition());
//        List<ProjectEntity.Project.Video> videoList = project.getVideo();
//        ArrayList<String> paths = new ArrayList<>();
//        for (ProjectEntity.Project.Video video : videoList) {
//            paths.add(Utils.getVideoPath(video.getFileName()));
//        }
//        VideoProxy.VideoExtras extras = new VideoProxy.VideoExtras();
//        extras.mediaList = paths;
//        VideoProxy.play(extras, mContext);
        int position = getAdapterPosition();
        CacheEngine.setCurrentIndex(position);
        VideoProxy.playWithCurrentProject(mContext, VideoExtraNames.TYPE_GOTO);
    }

}
