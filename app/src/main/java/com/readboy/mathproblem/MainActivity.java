package com.readboy.mathproblem;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.readboy.mathproblem.activity.BaseActivity;
import com.readboy.mathproblem.activity.StudyActivity;
import com.readboy.mathproblem.adapter.BaseViewHolder;
import com.readboy.mathproblem.adapter.ProjectAdapter;
import com.readboy.mathproblem.application.MathApplication;
import com.readboy.mathproblem.application.SubjectType;
import com.readboy.mathproblem.bean.ProjectParcelable;
import com.readboy.mathproblem.bean.ProjectHolder;
import com.readboy.mathproblem.cache.CacheCallback;
import com.readboy.mathproblem.cache.CacheEngine;
import com.readboy.mathproblem.cache.ProjectEntityWrapper;
import com.readboy.mathproblem.db.ProjectContract;
import com.readboy.mathproblem.db.Score;
import com.readboy.mathproblem.dialog.DownloadDialog;
import com.readboy.mathproblem.dialog.FavoriteDialog;
import com.readboy.mathproblem.dialog.GradeListDialog;
import com.readboy.mathproblem.dialog.NoNetworkDialog;
import com.readboy.mathproblem.download.AliyunDownloadManagerWrapper;
import com.readboy.mathproblem.http.response.VideoInfoEntity.VideoInfo;
import com.readboy.mathproblem.http.response.ProjectEntity;
import com.readboy.mathproblem.util.FileUtils;
import com.readboy.mathproblem.util.Lists;
import com.readboy.mathproblem.util.NetworkUtils;
import com.readboy.mathproblem.util.PreferencesUtils;
import com.readboy.mathproblem.util.SizeUtils;
import com.readboy.mathproblem.util.ToastUtils;
import com.readboy.mathproblem.util.VideoUtils;
import com.readboy.mathproblem.util.ViewUtils;
import com.readboy.mathproblem.video.proxy.VideoProxyReceiver;
import com.readboy.mathproblem.widget.SpaceItemDecoration;
import com.readboy.mathproblem.widget.ZoomLayoutManager;
import com.readboy.recyclerview.CommonAdapter;
import com.tencent.bugly.crashreport.CrashReport;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by oubin on 2017/9/21.
 *
 * @author oubin
 */
public class MainActivity extends BaseActivity implements View.OnClickListener,
        ProjectAdapter.OnItemClickListener {
    private static final String TAG = "oubin_MainActivity";

    public static final String EXTRA_GRADE = "grade";
    public static final String EXTRA_TYPE = "type";
    public static final String EXTRA_PROJECT_ID = "mProjectId";

    private ImageView mCat;
    private RecyclerView mProjectRv;
    private ProjectAdapter mProjectAdapter;
    private CheckBox mGradeNameCb;
    private View mTryAgainBtn;

    private ImageView mCatHands;
    /**
     * 应用题指导
     */
    private View mGuideSelectedParent;
    /**
     * 应用题技巧
     */
    private View mMethodSelectedParent;

    /**
     * 从0开始，但是数据请求时需要+1
     */
    private int mCurGrade;
    private ProjectEntityWrapper mProjectWrapper;
    private final List<ProjectEntity.Project> mDataList = new ArrayList<>();
    private SubjectType mSubjectType = SubjectType.guide;

    private FavoriteDialog mFavoriteDialog;
    private DownloadDialog mDownloadDialog;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private ContentObserver mScoreObserver;
    private ContentObserver mVideoObserver;
//    private DownloadManager.FileNameTaskObserver mDownloadObserver;

    private GradeListDialog mGradeListDialog;
    private NoNetworkDialog mNetworkDialog;
    private BroadcastReceiver mDownloadReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        DownloadManager.getInstance().onCreate();
        AliyunDownloadManagerWrapper.getInstance().onCreate();
        Log.e(TAG, "onCreate: ");
//        if (RequestPermissionsActivity.startPermissionActivity(this)) {
//            finish();
//            return;
//        }
        setContentView(R.layout.activity_main);
//        registerReceiver();
//        registerDownloadObserver();
        registerObserver();
        assignView();
        initView();
        if (!needRequestPermissions()) {
            MathApplication.initFile(this);
            initData();
        }
//        Log.e(TAG, "onCreate: " + AppUidUtil.getCertificateSHA1Fingerprint(this));

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e(TAG, "onSaveInstanceState: ");
    }

    @Override
    protected void onResume() {
        Log.e(TAG, "onResume: ");
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //保存喜欢，方便下次打开应用初始化数据
        Log.e(TAG, "onDestroy: currentGrade = " + mCurGrade + ", subjectType = " + mSubjectType);
        PreferencesUtils.saveGrade(mCurGrade);
        PreferencesUtils.saveSubject(mSubjectType);
    }

    @Override
    protected void onDestroy() {
//        DownloadManager.getInstance().onDestroy();
        super.onDestroy();
        Log.e(TAG, "onDestroy: ");
        unregisterObserver();
        ToastUtils.cancel();
//        unregisterDownloadObserver();
//        unregisterReceiver();

        if (mGradeListDialog != null) {
            mGradeListDialog.cancel();
        }

        CacheEngine.cancelHttpRequest();

        MathApplication.refWatch(this);
        AliyunDownloadManagerWrapper.getInstance().onDestroy();
    }

    @Override
    protected void onRequestPermissionsSuccess() {
        super.onRequestPermissionsSuccess();
        initData();
        MathApplication.initFile(this);
    }

    private void testDb(int i) {
        Score score = new Score(78922, 1 + i);
        Score.insertScore(getContentResolver(), score);
        Score score2 = new Score(88922, 2 + i);
        Score.insertScore(getContentResolver(), score2);
    }

    private void initView() {
        initRecyclerView();
        mGradeNameCb = (CheckBox) findViewById(R.id.grade_name_cb);
        ViewUtils.setTypeface(this, mGradeNameCb);
        mGradeNameCb.setOnClickListener(this);
        mTryAgainBtn = findViewById(R.id.try_again);
        mTryAgainBtn.setOnClickListener(this);

        mCat = (ImageView) findViewById(R.id.cat);
        mCatHands = (ImageView) findViewById(R.id.cat_hands);
        findViewById(R.id.download).setOnClickListener(this);
        findViewById(R.id.favorite).setOnClickListener(this);
        mGuideSelectedParent = findViewById(R.id.guide_selected_parent);
        mMethodSelectedParent = findViewById(R.id.method_selected_parent);
        TextView mProblemGuideSelected = (TextView) findViewById(R.id.problem_guide_selected);
        ViewUtils.setTypeface(this, mProblemGuideSelected);
        mProblemGuideSelected.setOnClickListener(this);
        View mProblemGuideNormal = findViewById(R.id.problem_guide_normal);
        mProblemGuideNormal.setOnClickListener(this);
        View mProblemMethodSelected = findViewById(R.id.problem_method_selected);
        mProblemMethodSelected.setOnClickListener(this);
        View mProblemMethodNormal = findViewById(R.id.problem_method_normal);
        mProblemMethodNormal.setOnClickListener(this);
    }

    private void initRecyclerView() {
        mProjectRv = (RecyclerView) findViewById(R.id.project_recycler_view);
        mProjectRv.setNestedScrollingEnabled(false);
        ZoomLayoutManager layoutManager = new ZoomLayoutManager(this);
        //https://github.com/leochuan/ViewPagerLayoutManager
        //http://www.jianshu.com/p/7bb7556bbe10
//        mProjectRv.setLayoutManager(new LinearLayoutManager(this, LinearLayout.HORIZONTAL, false));
        mProjectRv.setLayoutManager(layoutManager);
        LinearSnapHelper mSnapHelper = new LinearSnapHelper();
        mSnapHelper.attachToRecyclerView(mProjectRv);
        mProjectAdapter = new ProjectAdapter(this);
        mProjectAdapter.setOnItemClickListener(this);
        mProjectRv.setAdapter(mProjectAdapter);
        mProjectRv.addItemDecoration(new SpaceItemDecoration(LinearLayout.VERTICAL,
                SizeUtils.dp2px(this, 36)));
    }

    /**
     * 记录上次打开的是哪一年级的内容，再次打开，直接加载该年级。
     */
    private void initData() {
        String[] gradeNameS = getResources().getStringArray(R.array.grade_name_group);
        List<String> gradeNameList = Arrays.asList(gradeNameS);

        //根据SharedPreferences初始化数据
        mCurGrade = PreferencesUtils.getGrade(0);
        mSubjectType = PreferencesUtils.getSubject(SubjectType.guide);
        Log.e(TAG, "initData: grade = " + mCurGrade + " mSubjectType = " + mSubjectType.name());
        updateSelectedBtn();
        mGradeNameCb.setText(gradeNameList.get(mCurGrade));
        updateDataList();
    }

    private void registerReceiver() {
        if (mDownloadReceiver == null) {
            mDownloadReceiver = new VideoProxyReceiver();
        }
        IntentFilter filter = new IntentFilter(VideoProxyReceiver.ACTION_DOWNLOAD_VIDEO);
        registerReceiver(mDownloadReceiver, filter);
    }

    private void unregisterReceiver() {
        if (mDownloadReceiver != null) {
            unregisterReceiver(mDownloadReceiver);
        }
    }

    private void registerObserver() {
        if (mScoreObserver == null) {
            mScoreObserver = new ContentObserver(mHandler) {
                @Override
                public void onChange(boolean selfChange, Uri uri) {
                    super.onChange(selfChange, uri);
                    Log.e(TAG, "onChange() called with: selfChange = " + selfChange + ", uri = " + uri + "");
                    updateProjectWrapper(mProjectWrapper);
                }
            };
            Log.e(TAG, "registerObserver: mScoreObserver");
            getContentResolver().registerContentObserver(
                    ProjectContract.ScoreColumns.CONTENT_URI, true, mScoreObserver);
        }
        if (mVideoObserver == null) {
            mVideoObserver = new ContentObserver(mHandler) {
                @Override
                public void onChange(boolean selfChange, Uri uri) {
                    super.onChange(selfChange, uri);
//                Log.e(TAG, "onChange() called with: selfChange = " + selfChange + ", uri = " + uri + "");
                }
            };
            Log.e(TAG, "registerObserver: mVideoObserver");
            getContentResolver().registerContentObserver(
                    ProjectContract.VideoColumns.CONTENT_URI, true, mVideoObserver);
        }
    }

    private void unregisterObserver() {
        if (mScoreObserver != null) {
            Log.e(TAG, "unregisterObserver: mScoreObserver");
            getContentResolver().unregisterContentObserver(mScoreObserver);
            mScoreObserver = null;
        }
        if (mVideoObserver != null) {
            Log.e(TAG, "unregisterObserver: mVideoObserver");
            getContentResolver().unregisterContentObserver(mVideoObserver);
            mVideoObserver = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.grade_name_cb:
                if (mGradeNameCb.isChecked()) {
                    showGradeNameList();
                } else {
                    hideGradeNameList();
                }
                break;
            case R.id.download:
                openDownloadDialog();
//                AliyunDownloadManagerWrapper.getInstance().prepareDownload("8d18c54ccc25418caacca486c4a8073d");
//                AliyunDownloadManagerWrapper.getInstance().prepareDownload("0d59501f36c443008a294844adad3c64");
                break;
            case R.id.favorite:
                openFavoriteDialog();
//                AliyunDownloadManagerWrapper.getInstance().prepareDownload("7d4193bdcc5c467696bd7ea04ded4d91");
                break;
            case R.id.problem_guide_normal:
                changeSubjectType();
                updateSelectedBtn();
                updateDataList();
                break;
            case R.id.problem_method_normal:
                changeSubjectType();
                updateSelectedBtn();
                updateDataList();
                break;
            case R.id.try_again:
                updateDataList();
                break;
            default:
                Log.e(TAG, "onClick: id = " + v.getId());
                break;
        }
    }

    private void updateSelectedBtn() {
        if (mSubjectType == SubjectType.method) {
            mMethodSelectedParent.setVisibility(View.VISIBLE);
            mGuideSelectedParent.setVisibility(View.GONE);
        } else {
            mGuideSelectedParent.setVisibility(View.VISIBLE);
            mMethodSelectedParent.setVisibility(View.GONE);
        }
    }

    private void changeSubjectType() {
        if (mSubjectType == SubjectType.guide) {
            mSubjectType = SubjectType.method;
        } else {
            mSubjectType = SubjectType.guide;
        }
    }

    private void openDownloadDialog() {
        if (mDownloadDialog == null) {
            mDownloadDialog = new DownloadDialog(this);
            mDownloadDialog.setOnDismissListener(dialog -> {
                Log.e(TAG, "onDismiss: ");
                if (mDownloadDialog.isShouldUpdateUI()) {
                    updateDataList();
                }
            });
        }
        mDownloadDialog.show();
    }

    private void openFavoriteDialog() {
        if (mFavoriteDialog == null) {
            mFavoriteDialog = new FavoriteDialog(this);
        }
        mFavoriteDialog.show();
    }

    private void updateDataList() {
//        ProjectEntityWrapper wrapper = CacheEngine.getProject(mSubjectType, mCurGrade);
//        if (wrapper != null) {
//            updateProjectWrapper(wrapper);
//        } else {
//            Log.e(TAG, "updateDataList: data = null");
//            CacheEngine.getProjectFromHttp(mSubjectType, mCurGrade, this);
//        }
        updateProjectWrapper(null);
        CacheEngine.getProject(mSubjectType, mCurGrade, this);

    }

    private void updateProjectWrapper(ProjectEntityWrapper wrapper) {
        Log.e(TAG, "updateProjectWrapper: ");
        mProjectWrapper = wrapper;
        //为了视频精讲，跳转到Activity中
//        CacheEngine.setCurrentProjectWrapper(wrapper);
        if (wrapper == null) {
            updateDataList(null);
        } else {
            updateDataList(wrapper.getProjectList());
        }
        showData();
    }

    @Override
    public void onBefore() {
        super.onBefore();
        mTryAgainBtn.setVisibility(View.GONE);
    }

    /**
     * @see CacheCallback#onResponse(ProjectEntityWrapper)
     * 通过网络获取ProjectEntityWrapper，获取成功回调。
     */
    @Override
    public void onResponse(ProjectEntityWrapper entity) {
        super.onResponse(entity);
        Log.e(TAG, "onResponse() called with: entity = " + entity + "");
        if (entity == null) {
            mTryAgainBtn.setVisibility(View.VISIBLE);
        } else {
            mTryAgainBtn.setVisibility(View.GONE);
        }
        updateProjectWrapper(entity);
        CacheEngine.setCurrentProjectWrapper(entity);
    }

    /**
     * @see CacheCallback#onError(String, Throwable)
     * 通过网络获取ProjectEntityWrapper，获取失败回调。
     */
    @Override
    public void onError(String message, Throwable e) {
        super.onError(message, e);
        Log.e(TAG, "onError() called with: message = " + message + ", e = " + e + "");
        if (isFinishing() || isDestroyed()) {
            Log.e(TAG, "onError: isFinishing or isDestroyed, " + isFinishing() + ", " + isDestroyed());
            CrashReport.postCatchedException(e);
            return;
        }
        if (e instanceof UnknownHostException) {
            checkNetwork();
        } else if (e instanceof SocketTimeoutException) {
            ToastUtils.showShort(this, "网络不稳定, 网络连接超时");
        } else {
            ToastUtils.show(this, message);
        }
        updateProjectWrapper(null);
        mTryAgainBtn.setVisibility(View.VISIBLE);
    }

    public void updateDataList(List<ProjectEntity.Project> dataList) {
        mDataList.clear();
        if (dataList != null) {
            mDataList.addAll(dataList);
            List<ProjectHolder> projectList = new ArrayList<>();
            for (ProjectEntity.Project data : dataList) {
                int score;
                if (Lists.isEmpty(data.getExercises())) {
                    //如果没有练习题，显示满分，5分。
                    score = 5;
                } else {
                    score = Score.getScore(getContentResolver(), data.getId());
                }
                int status = ProjectHolder.VIDEO_STATUS_NONE;
                ProjectHolder project = new ProjectHolder();
                List<VideoInfo> videoInfoList = data.getVideoInfoList();
                if (!Lists.isEmpty(videoInfoList)) {
                    List<VideoInfo> videoList = new ArrayList<>();
                    boolean hasVideoDownloading = false;
                    for (VideoInfo videoInfo : videoInfoList) {
                        String fileName = FileUtils.getFileName(videoInfo.getVideoUri());
                        //判断是否已经下载。
                        if (!VideoUtils.videoIsExist(fileName)) {
                            //是否正在下载。
//                            if (DownloadManager.getInstance().isDownloading(fileName)) {
//                                status = ProjectHolder.VIDEO_STATUS_DOWNLOADING;
//                                hasVideoDownloading = true;
////                                addObserver(fileName);
//                            } else {
                            videoList.add(videoInfo);
                            //监听是否有下载，通知mProjectRv更新Item
//                                addObserver(fileName);
//                            }
                        } else {
                            if (!hasVideoDownloading) {
                                status = ProjectHolder.VIDEO_STATUS_COMPLETED;
                            }
                        }
                    }
                    if (!Lists.isEmpty(videoList)) {
                        status = ProjectHolder.VIDEO_STATUS_WAIT;
                        project.setVideoInfoList(videoList);
                    }
                }
                project.setId(data.getId());
                project.setScore(score);
                project.setName(data.getName());
                project.setVideoStatus(status);
                if (!TextUtils.isEmpty(data.getExplain())) {
                    String regex = "</p>";
                    int startIndex = data.getExplain().indexOf(regex) + regex.length();
                    if (startIndex < 0) {
                        startIndex = 0;
                    }
                    String explain = data.getExplain().substring(startIndex);
                    project.setExplain(explain);
                } else {
                    Log.e(TAG, "updateDataList: explain=nul. ");
                }
                projectList.add(project);
            }
            runOnUiThread(() -> mProjectAdapter.update(projectList));
            if (mDataList.size() == 0) {
                ToastUtils.show(this, "暂无数据");
            } else {
                ToastUtils.cancel();
            }
        } else {
            mProjectAdapter.update(null);
        }
    }

    private void showData() {
        mProjectRv.setVisibility(View.VISIBLE);
    }

    private void showGradeNameList() {
        if (mGradeListDialog == null) {
            mGradeListDialog = new GradeListDialog(this);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mGradeListDialog.create();
            }
            mGradeListDialog.setOnDismissListener(dialog1 -> mGradeNameCb.setChecked(false));
            mGradeListDialog.setOnShowListener(dialog -> {
                Log.e(TAG, "showGradeNameList: mCurGrade = " + mCurGrade);
            });
            mGradeListDialog.show();
            mGradeListDialog.setSelectedPosition(mCurGrade);
            String[] gradeNameS = getResources().getStringArray(R.array.grade_name_group);
            List<String> gradeNameList = Arrays.asList(gradeNameS);
            mGradeListDialog.setItemClickListener(new CommonAdapter.OnItemClickListener<String>() {

                @Override
                public void onItemClick(View view, RecyclerView.ViewHolder holder, String o, int position) {
                    if (position == mCurGrade) {
                        return;
                    }
                    mGradeNameCb.setText(gradeNameList.get(position));
                    Log.e(TAG, "onItemClick: grade name = " + o + ", grade name checkbox = " + mGradeNameCb.getText());
                    mCurGrade = position;
                    updateDataList();
                    hideGradeNameList();
                    mGradeListDialog.setSelectedPosition(mCurGrade);
                }

                @Override
                public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, String o, int position) {
                    return false;
                }
            });
        } else {
            mGradeListDialog.show();
        }
    }

    private void hideGradeNameList() {
        if (mGradeListDialog != null && mGradeListDialog.isShowing()) {
            mGradeListDialog.dismiss();
        }
    }

    @Override
    public void onItemClick(int position, BaseViewHolder viewHolder) {
        gotoStudyActivity(position);
    }

    private void gotoStudyActivity(int position) {
        Intent intent = new Intent(MainActivity.this, StudyActivity.class);
        int type = mSubjectType.ordinal();
        ProjectParcelable parcelable = new ProjectParcelable(
                position,
                mDataList.get(position).getId(),
                type,
                mCurGrade);
        intent.putExtra(ProjectParcelable.EXTRA_PROJECT_PARCELABLE, parcelable);

        //保存喜欢，方便下次打开应用初始化数据
        PreferencesUtils.saveGrade(mCurGrade);
        PreferencesUtils.saveSubject(mSubjectType);
        startActivity(intent);
        CacheEngine.setCurrentProjectWrapper(position, mProjectWrapper);
    }

    private void checkNetwork() {
        if (!NetworkUtils.isConnected(MainActivity.this)) {
            if (mNetworkDialog == null) {
                mNetworkDialog = new NoNetworkDialog(MainActivity.this);
            }
            if (!mNetworkDialog.isShowing()) {
                mNetworkDialog.show();
            }
        }
    }


//    private void unregisterDownloadObserver() {
//        if (mDownloadObserver != null) {
//            DownloadManager.getInstance().unregisterDownloadTaskObserver(mDownloadObserver);
//        }
//    }
//
//    public static class VideoIdTaskObserver extends DownloadManager.BaseDownloadTaskObserver<Integer> {
//
//        @Override
//        public boolean isContains(BaseDownloadTask task) {
//            return set.contains(task.getTag());
//        }
//    }

}
