package com.readboy.mathproblem.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper2;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.readboy.mathproblem.R;
import com.readboy.mathproblem.adapter.ExampleAdapter;
import com.readboy.mathproblem.application.MathApplication;
import com.readboy.mathproblem.application.SubjectType;
import com.readboy.mathproblem.bean.ProjectParcelable;
import com.readboy.mathproblem.cache.CacheEngine;
import com.readboy.mathproblem.cache.ProjectEntityWrapper;
import com.readboy.mathproblem.db.ProjectContract;
import com.readboy.mathproblem.db.Score;
import com.readboy.mathproblem.dialog.NoNetworkDialog;
import com.readboy.mathproblem.exercise.ExerciseActivity;
import com.readboy.mathproblem.exercise.PagerLayoutManager;
import com.readboy.mathproblem.http.HttpConfig;
import com.readboy.mathproblem.http.response.ProjectEntity;
import com.readboy.mathproblem.js.JsUtils;
import com.readboy.mathproblem.media.IMediaPlayer;
import com.readboy.mathproblem.media.MediaPlayerImpl;
import com.readboy.mathproblem.note.DraftPaperView;
import com.readboy.mathproblem.notetool.Note;
import com.readboy.mathproblem.notetool.NoteScrollView;
import com.readboy.mathproblem.util.Lists;
import com.readboy.mathproblem.util.NetworkUtils;
import com.readboy.mathproblem.util.SizeUtils;
import com.readboy.mathproblem.util.ToastUtils;
import com.readboy.mathproblem.video.movie.VideoExtraNames;
import com.readboy.mathproblem.widget.Dog;
import com.readboy.mathproblem.widget.LineItemDecoration;
import com.readboy.mathproblem.widget.SmallPlayerView;
import com.readboy.recyclerview.CommonAdapter;
import com.readboy.recyclerview.MultiItemTypeAdapter;
import com.readboy.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;


public class StudyActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "oubin_StudyActivity";

    public static final String EXTRA_INDEX = "index";
    private static final String KEY_POSITION = "key";
    private static final String KEY_IS_TEACHER_PANEL = "teacher_panel";

    private DrawerLayout mDrawerLayout;
    /**
     * 名师辅导
     */
    private View mTeacherSelectedParent;
    private View mTeacherParent;
    /**
     * 自定义View，包含mExplainTv, mVoice，控制显示。
     */
    private WebView mExplainWebView;
    private static final String JS_INTERFACE_NAME = "android";
    private View mTeacherDivider;
    //TODO：和MediaPlayer解耦出来。
    private View mVoice;
    private View mVoiceLoading;
    private AnimationDrawable mVoiceLoadingAnimation;
    private View mWebProgress;
    /**
     * 显示名师辅导时，是否需要更新数据。
     * 如不延迟，切换project时，初始化Note，获取宽高会有问题。
     */
    private boolean updateTeacherData;

    /**
     * 例题讲解
     */
    private View mExampleSelectedParent;
    private View mExampleParent;
    private View mExerciseBtn;
    private RecyclerView mExampleRv;
    private PagerLayoutManager mExampleLayoutManager;
    //    private LinearLayoutManager mExampleLayoutManager;
    private ExampleAdapter mExampleAdapter;

    /**
     * 视频播放
     */
    private SmallPlayerView mPlayerView;

    //笔记
    private Note mNote;
    private NoteScrollView mNoteScrollView;
    private int mNoteId;

    private DraftPaperView mDraftPaper;

    private ImageView mCat;
    private AnimationDrawable mCatAnimation;
    private Dog mDog;
    private View mPrevious;
    private View mNext;

    private TextView mProjectName;
    private RecyclerView mCatalogueRv;
    private CommonAdapter<ProjectEntity.Project> mCatalogueAdapter;

    private MediaPlayerImpl mPlayer = new MediaPlayerImpl();
    private final List<ProjectEntity.Project.Example> mExampleList = new ArrayList<>();
    private final List<ProjectEntity.Project> mProjectList = new ArrayList<>();

    private Handler mObserverHandler = new Handler(Looper.getMainLooper());
    private ContentObserver mScoreObserver;
    private AnimationDrawable mVoiceAnimation;

    /**
     * sdk < 23, 网络监听方式
     */
    private BroadcastReceiver mReceiver;
    /**
     * sdk >= 23, 网络监听方式
     */
    private NetworkCallback mNetworkCallback;
    private NoNetworkDialog mNoNetWorkDialog;

    private int mCurrentPosition;
    private int mGrade;
    private int mExampleIndex = 0;
    private SubjectType mSubjectType;
    private boolean hasRequestAudioFocus = false;
    private int mVideoIndex;
    private int mSeekPosition;
    private int mType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study);
//        if (recoveryData(savedInstanceState)) return;
        Log.e(TAG, "onCreate: ");
        parseIntent();
        assignView();
        initView();
        initAnimation();
        registerObserver();
        checkNetWork();
        if (savedInstanceState != null) {
            mCurrentPosition = savedInstanceState.getInt(KEY_POSITION, mCurrentPosition);
            boolean isTeacherPanel = savedInstanceState.getBoolean(KEY_IS_TEACHER_PANEL);
            if (!isTeacherPanel) {
                showExampleExplanation();
            }
        }

        if (!needRequestPermissions()) {
            initData();
        }
    }

    private boolean recoveryData(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            Log.e(TAG, "onCreate: saved = " + savedInstanceState);
            Intent intent = getIntent();
            if (intent != null) {
                ProjectParcelable parcelable = intent.getParcelableExtra(ProjectParcelable.EXTRA_PROJECT_PARCELABLE);
                if (parcelable == null) {
                    Log.e(TAG, "parseIntent: parcelable = null");
                    parcelable = new ProjectParcelable();
                }
                mCurrentPosition = parcelable.getPosition();
                mCurrentPosition = savedInstanceState.getInt(KEY_POSITION, mCurrentPosition);
                parcelable.setPosition(mCurrentPosition);
                intent.putExtra(ProjectParcelable.EXTRA_PROJECT_PARCELABLE, parcelable);
            }
            if (RequestPermissionsActivity.startPermissionActivity(this)) {
                finish();
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: ");
        startAnimation();
        registerReceiver();

        if (mPlayerView.hasData() && mExampleSelectedParent.getVisibility() == View.VISIBLE) {
            mPlayerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause: ");
        if (mNote != null) {
            mNote.saveNote();
        }
        stopAnimation();
        pauseAudio();
        pauseOrStopVideo();

        //解决再列题讲解界面有残影问题。
        //进入有视频的章节,例题讲解,下滑通知栏,设置,返回,视频会显出来。
        if (mPlayerView.hasData() && mExampleSelectedParent.getVisibility() == View.VISIBLE) {
            mPlayerView.setVisibility(View.GONE);
        }

        unregisterReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: ");
        unregisterObserver();

        stopAudio();
        stopVideo();
        releaseMedia();

        mExplainWebView.removeJavascriptInterface(JS_INTERFACE_NAME);

        if (mNote != null) {
            Log.e(TAG, "onDestroy: exit Note.");
            mNote.exit();
            mNote = null;
        }

        MathApplication.refWatch(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e(TAG, "onSaveInstanceState: position = " + mCurrentPosition);
        outState.putInt(KEY_POSITION, mCurrentPosition);
        outState.putBoolean(KEY_IS_TEACHER_PANEL, mTeacherParent.getVisibility() == View.VISIBLE);
    }

    @Override
    protected void onRequestPermissionsSuccess() {
        super.onRequestPermissionsSuccess();
        initData();
    }

    private void releaseMedia() {
        mPlayer.release();
        mPlayerView.release();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data != null) {
                String path = data.getStringExtra(VideoExtraNames.EXTRA_PATH);
                long seekPosition = data.getLongExtra(VideoExtraNames.EXTRA_SEEK_POSITION, 0);
                int videoIndex = data.getIntExtra(VideoExtraNames.EXTRA_INDEX, 0);
                Log.e(TAG, "onActivityResult: seek = " + seekPosition + ", index = " + videoIndex);
                mPlayerView.smoothScrollToPosition(videoIndex);
                mPlayerView.playVideo(videoIndex, seekPosition);
            }
        }

    }

    private void parseIntent() {
        Intent intent = getIntent();
        if (intent != null) {

            ProjectParcelable parcelable = intent.getParcelableExtra(ProjectParcelable.EXTRA_PROJECT_PARCELABLE);
            if (parcelable == null) {
                Log.e(TAG, "parseIntent: parcelable = null");
                parcelable = new ProjectParcelable();
            }
            mType = intent.getIntExtra(VideoExtraNames.EXTRA_FINISH_TYPE, -1);
            if (mType == VideoExtraNames.TYPE_GOTO) {
                mVideoIndex = parcelable.getVideoIndex();
                mSeekPosition = parcelable.getSeekPosition();
            }
            mGrade = parcelable.getGrade();
            mCurrentPosition = parcelable.getPosition();
            mSubjectType = SubjectType.valueOf(parcelable.getType());
            Log.e(TAG, "parseIntent: grade = " + mGrade + ", position = " + mCurrentPosition
                    + ", subject = " + mSubjectType);
        }
    }

    @Override
    protected void assignView() {
        super.assignView();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setScrimColor(Color.TRANSPARENT);
        mDraftPaper = (DraftPaperView) findViewById(R.id.draft_paper);
        mTeacherParent = findViewById(R.id.teacher_parent);
        mExampleParent = findViewById(R.id.example_parent);

        mPrevious = findViewById(R.id.previous);
        mPrevious.setOnClickListener(this);
        mNext = findViewById(R.id.next);
        mNext.setOnClickListener(this);
    }

    private void initView() {
        mTeacherSelectedParent = findViewById(R.id.teacher_selected_parent);
        mExampleSelectedParent = findViewById(R.id.example_selected_parent);
        findViewById(R.id.teacher_explanation_normal).setOnClickListener(this);
        findViewById(R.id.example_explanation_normal).setOnClickListener(this);

        mCat = (ImageView) findViewById(R.id.cat);
        mDog = (Dog) findViewById(R.id.dog);
        findViewById(R.id.drawer_layout_menu).setOnClickListener(this);
        mProjectName = (TextView) findViewById(R.id.project_name);
        mProjectName.setOnClickListener(this);
        TextView mGradeName = (TextView) findViewById(R.id.grade_name);
        mGradeName.setText(String.format("%s年级目录",
                getResources().getStringArray(R.array.chinese_number)[mGrade]));
        mCatalogueRv = (RecyclerView) findViewById(R.id.catalogue_list);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        //名师辅导
        mExplainWebView = (WebView) findViewById(R.id.explain_web_view);
        mExplainWebView.getSettings().setJavaScriptEnabled(true);
//        mExplainWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            mExplainWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
//        } else {
//            mExplainWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
//        }
//        mExplainWebView.setVerticalScrollBarEnabled(false);
//        mExplainWebView.setVerticalScrollbarOverlay(false);
//        mExplainWebView.setHorizontalScrollBarEnabled(false);
//        mExplainWebView.setHorizontalScrollbarOverlay(false);
//        mExplainWebView.setSaveEnabled(true);
        mExplainWebView.addJavascriptInterface(new JavaScriptInterface(), JS_INTERFACE_NAME);
        mExplainWebView.setOnLongClickListener(v -> true);
        mExplainWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.e(TAG, "onPageStarted: url = " + url);
                mWebProgress.setVisibility(View.VISIBLE);
                mExplainWebView.setActivated(false);
            }

            /**
             *
             * getHeight, getMeasureHeight, getContentHeight不可靠。
             * 在js里获取，该方式有待考证可靠性。
             * @see WebView#addJavascriptInterface(Object, String)
             * @see JavaScriptInterface#onLoadFinish(int)
             */
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.e(TAG, "onPageFinished: url = " + url);
                //获取高度不可靠。
                Log.e(TAG, "onPageFinished: webView height = " + view.getHeight()
                        + ", getMeasuredHeight = " + view.getMeasuredHeight()
                        + ", getContentHeight = " + view.getContentHeight());
                Log.e(TAG, "onPageFinished: scaleY = " + view.getScaleY());
                if (mWebProgress.isActivated()) {
                    mWebProgress.setVisibility(View.GONE);
                } else {
                    mWebProgress.setActivated(true);
                }
                int w = View.MeasureSpec.makeMeasureSpec(0,
                        View.MeasureSpec.UNSPECIFIED);
                int h = View.MeasureSpec.makeMeasureSpec(0,
                        View.MeasureSpec.UNSPECIFIED);
                Log.e(TAG, "run: webView w = " + w + ", h = " + h);
                //重新测量
//                    mExplainWebView.measure(w, h);
            }
        });

        mTeacherDivider = findViewById(R.id.teacher_explanation_divider);
        mVoice = findViewById(R.id.voice);
        mVoice.setOnClickListener(this);
        mVoice.setActivated(true);
        mVoiceLoading = findViewById(R.id.voice_loading);
        mVoiceLoadingAnimation = (AnimationDrawable) mVoiceLoading.getBackground();
        mVoiceLoading.setOnClickListener(this);
        mWebProgress = findViewById(R.id.web_progress);

        //列题解析
        mExerciseBtn = findViewById(R.id.go_to_exercise_btn);
        mExerciseBtn.setOnClickListener(this);
        findViewById(R.id.open_draft_paper).setOnClickListener(this);
        mExampleRv = (RecyclerView) findViewById(R.id.example_recycler_view);
        mExampleLayoutManager = new PagerLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mExampleLayoutManager.setOnPositionChangeListener((lastPosition, newPosition) -> {
            Log.e(TAG, "PagerLayoutManager onPositionChange : last = " + lastPosition + ", new = " + newPosition);
            mExampleIndex = newPosition;
            updatePreviousOrNextState();
        });
        mExampleRv.setLayoutManager(mExampleLayoutManager);
        mExampleAdapter = new ExampleAdapter(this);
        PagerSnapHelper2 snapHelper = new PagerSnapHelper2();
        snapHelper.attachToRecyclerView(mExampleRv);
        mExampleRv.setAdapter(mExampleAdapter);
        mExampleAdapter.setData(mExampleList);

        //笔记
//        mNote = new Note(this);
//        mNoteScrollView = findViewById(R.id.note_scroll_view);

        mPlayerView = (SmallPlayerView) findViewById(R.id.small_player_view);
        mPlayerView.setCurrentProject(mGrade, mSubjectType);
        mPlayerView.setOnPlayBeforeListener(() -> {
            Log.e(TAG, "initView: onPlayBefore ");
            pauseAudio();
            requestAudioFocusTransient();
        });
    }

    /**
     * 必须获取权限了运行该方法。
     */
    private void initData() {
        mNote = new Note(this);
        mNoteScrollView = (NoteScrollView) findViewById(R.id.note_scroll_view);
        if (mTeacherSelectedParent.getVisibility() == View.VISIBLE) {
            mNote.showNote();
        } else {
            mNote.hideNote();
        }

        mCatalogueRv.setLayoutManager(new LinearLayoutManager(this));
        mCatalogueRv.addItemDecoration(new LineItemDecoration(LinearLayout.VERTICAL, 1, 0x55000000));
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, LinearLayout.VERTICAL);
        itemDecoration.setDrawable(new ColorDrawable(0xffff0000));
//        mCatalogueRv.addItemDecoration(itemDecoration);
        mCatalogueAdapter = new CommonAdapter<ProjectEntity.Project>(this, R.layout.item_catalogue, mProjectList) {
            @Override
            protected void convert(ViewHolder holder, ProjectEntity.Project data, int position) {
//                Log.e(TAG, "convert: position = " + position);
                TextView name = (TextView) holder.itemView.findViewById(R.id.project_name);
                name.setText(data.getName());
                if (mCurrentPosition == position) {
                    name.setSelected(true);
                } else {
                    name.setSelected(false);
                }
                TextView scoreView = (TextView) holder.itemView.findViewById(R.id.project_score);
                if (Lists.isEmpty(data.getExercises())) {
                    scoreView.setVisibility(View.GONE);
                } else {
                    scoreView.setVisibility(View.VISIBLE);
                    int score = getScore(data.getId());
                    scoreView.setText(String.valueOf(getScore(data.getId())));
                    if (score <= 0) {
                        scoreView.setEnabled(false);
                    } else {
                        scoreView.setEnabled(true);
                    }
                }
            }
        };
        mCatalogueAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener<ProjectEntity.Project>() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, ProjectEntity.Project project, int position) {
                Log.e(TAG, "onItemClick: position = " + position + ", project name = " + project.getName());
                Log.e(TAG, "onItemClick: adapter position = " + holder.getAdapterPosition());
                if (mCurrentPosition == position) {
                    Log.e(TAG, "onItemClick: same position, position = " + position);
                    return;
                }

                CacheEngine.setCurrentIndex(position);
                mCurrentPosition = position;
//                mExplainWebView.loadUrl(Constants.EMPTY_URL);
                mExplainWebView.loadDataWithBaseURL(null, "", "text/html", "UTF-8", null);
                updateView();
                mCatalogueAdapter.notifyDataSetChanged();
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, ProjectEntity.Project project, int position) {
                return false;
            }
        });
        mCatalogueRv.setAdapter(mCatalogueAdapter);

        initProjectList();

        mPlayer.addMediaPlayerListener(new MediaPlayerListener());
    }

    private void updateData(ProjectEntityWrapper wrapper) {
        mProjectList.clear();
        mProjectList.addAll(wrapper.getProjectList());
        mCatalogueAdapter.notifyDataSetChanged();
//        mCatalogueRv.post(() -> ViewUtils.setSelectedPosition(mCurrentPosition, R.id.project_name, mCatalogueRv));
    }

    private void updateView() {
        stopAudio();
        stopVideo();
        if (mNote != null) {
            mNote.saveNote();
        }
        updateView(mProjectList.get(mCurrentPosition));
        if (mTeacherSelectedParent.getVisibility() == View.VISIBLE) {
            mPrevious.setVisibility(View.GONE);
            mNext.setVisibility(View.GONE);
        }
    }

    private void updateView(ProjectEntity.Project data) {
        mProjectName.setText(data.getName());
        updateExampleView(data);
        if (!isTeacherExplanationShowing()) {
            updateTeacherData = true;
        } else {
            updateTeacherView(data);
        }
//        updateNote(data.getId());
    }

    /**
     * 使用ViewTreeObserver针对WebView不可靠。使用WebViewClient.onPageFinished, View.post。
     *
     * @param noteId 存入数据库的笔记ID, 使用Project.id
     */
    private void updateNote(final int noteId, int height) {
        Log.e(TAG, "updateNote() called with: noteId = " + noteId + ", height = " + height + "");
        int id = mProjectList.get(mCurrentPosition).getId();
        if (mNote != null) {
            Log.e(TAG, "onPageFinished: save note.");
//            mNote.saveNote();
//        }
//        if (mNote == null) {
//            Log.e(TAG, "onPageFinished: new Note.");
//            mNote = new Note(StudyActivity.this);
//        }
            mNote.setNoteId(id, (int) (height * 1.5F));
            mNoteScrollView.scrollTo(0, 0);
        } else {
            Log.e(TAG, "updateNote: mNote = null. ");
        }
    }

    private void closeDrawer() {
        mDrawerLayout.closeDrawer(Gravity.START);
    }

    private void openDrawer() {
        Log.e(TAG, "openDrawer: ");
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    private int getScore(int projectId) {
        return Score.getScore(getContentResolver(), projectId);
    }

    private void initProjectList() {
        ProjectEntityWrapper wrapper = CacheEngine.getProject(mSubjectType, mGrade);
        if (wrapper != null && !Lists.isEmpty(wrapper.getProjectList())) {
            updateData(wrapper);
            updateView();
        } else {
            Log.e(TAG, "initProjectList: wrapper = null");
            //通过网络获取数据。
            CacheEngine.getProjectFromHttp(mSubjectType, mGrade, this);
        }
    }

    @Override
    public void onResponse(ProjectEntityWrapper entity) {
        super.onResponse(entity);
        Log.e(TAG, "onResponse() called with: entity = " + entity.toString() + "");
        updateData(entity);
        updateView();
        CacheEngine.setCurrentProjectWrapper(mCurrentPosition, entity);
    }

    private void initAnimation() {
        mCatAnimation = (AnimationDrawable) mCat.getBackground();
    }

    private void startAnimation() {
        mCatAnimation.start();
        mDog.startAnimation();
    }

    private void stopAnimation() {
        mCatAnimation.stop();
        mDog.stopAnimation();
    }

    private void registerObserver() {
        mScoreObserver = new ContentObserver(mObserverHandler) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange, uri);
                Log.e(TAG, "onChange() called with: selfChange = " + selfChange + ", uri = " + uri + "");
                mCatalogueAdapter.notifyDataSetChanged();
            }
        };
        getContentResolver().registerContentObserver(
                ProjectContract.ScoreColumns.CONTENT_URI, true, mScoreObserver);

    }

    private void unregisterObserver() {
        getContentResolver().unregisterContentObserver(mScoreObserver);
    }

    @Override
    public void onClick(View v) {
        Log.e(TAG, "onClick: v = " + v.getId());
        switch (v.getId()) {
            case R.id.teacher_explanation_normal:
                showTeacherExplanation();
                break;
            case R.id.example_explanation_normal:
                showExampleExplanation();
                break;
            case R.id.drawer_layout_menu:
            case R.id.project_name:
                openDrawer();
                break;
            case R.id.go_to_exercise_btn:
                gotoExerciseActivity();
                break;
            case R.id.open_draft_paper:
                openDraftPaper();
                break;
            case R.id.voice:
                if (!mVoice.isActivated()) {
                    ToastUtils.showShort(this, "请连接网络");
                } else {
                    playOrPauseAudio();
                }
                break;
            case R.id.previous:
                previousExample();
                break;
            case R.id.next:
                nextExample();
                break;
            default:
                break;
        }
    }

    private void previousExample() {
        mExampleLayoutManager.previous();
    }

    private void nextExample() {
        mExampleLayoutManager.next();
    }

    private void updatePreviousOrNextState() {
        if (mExampleList.size() <= 1) {
            mPrevious.setVisibility(View.GONE);
            mNext.setVisibility(View.GONE);
        } else if (mExampleIndex == mExampleList.size() - 1) {
            mNext.setVisibility(View.GONE);
            mPrevious.setVisibility(View.VISIBLE);
        } else if (mExampleIndex == 0) {
            mPrevious.setVisibility(View.GONE);
            mNext.setVisibility(View.VISIBLE);
        } else {
            mPrevious.setVisibility(View.VISIBLE);
            mNext.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 是否可做到缓存起来，方便下次调用，URL
     */
    private void playOrPauseAudio() {
        Log.e(TAG, "playOrPauseAudio: mediaPlayer state = " + mPlayer.getPlayState());
        switch (mPlayer.getPlayState()) {
            case COMPLETED:
            case STOPPED:
            case IDLE:
                playAudio();
                break;
            case PAUSED:
                resumeAudio();
                break;
            case PREPARING:
            case PLAYING:
                pauseAudio();
                break;
            default:
                Log.e(TAG, "playOrPauseAudio: mPlayer play state = " + mPlayer.getPlayState());
        }
    }

    /**
     * @return true 网络可用，false 网络不可用。
     */
    private boolean checkNetwork() {
        Log.e(TAG, "checkNetwork: ");
        if (!NetworkUtils.isConnected(this)) {
            showNoNetworkDialog();
//            mVoice.setEnabled(false);
            mVoice.setActivated(false);
            return false;
        }
        return true;
    }

    private void showNoNetworkDialog() {
        if (mNoNetWorkDialog == null) {
            mNoNetWorkDialog = new NoNetworkDialog(this);
        }
        if (!mPlayerView.noNetworkDialogIsShowing()) {
            mNoNetWorkDialog.show();
        }
    }

    private void dismissNoNotworkDialog() {
        mPlayerView.dismissNoNetworkDialog();
        if (mNoNetWorkDialog != null && mNoNetWorkDialog.isShowing()) {
            mNoNetWorkDialog.dismiss();
        }
    }

    private void playAudio() {
        Log.e(TAG, "playAudio: ");
        if (!checkNetwork()) {
            return;
        }
        if (mCurrentPosition >= mProjectList.size()) {
            ToastUtils.showShort(this, "无法获取音频数据");
            return;
        }
        pauseOrStopVideo();
        requestAudioFocusTransient();
        String url = HttpConfig.AUDIO_HOST + mProjectList.get(mCurrentPosition).getExplainAudio();
        mPlayer.play(new IMediaPlayer.MediaResource(url));
    }

    private void resumeAudio() {
        pauseOrStopVideo();
        mPlayer.resume();
    }

    /**
     * 暂停音频，或者视频
     */
    private void pauseMedia() {
        pauseAudio();
        pauseVideo();
    }

    private void stopMedia() {
        stopAudio();
        stopVideo();
    }

    private void pauseAudio() {
        Log.e(TAG, "pauseAudio: ");
        mPlayer.pause();
        abandonAudioFocus();
    }

    private void stopAudio() {
        mPlayer.stop();
        abandonAudioFocus();
    }

    private void pauseOrStopVideo() {
//        if (mPlayerView.isPlaying()) {
        Log.e(TAG, "pauseOrStopVideo: isPlaying = " + mPlayerView.isPlaying());
            pauseVideo();
//        } else {
//            stopVideo();
//        }
    }

    private void pauseVideo() {
        mPlayerView.pauseVideo();
        abandonAudioFocus();
    }

    private void stopVideo() {
        mPlayerView.stopVideo();
        abandonAudioFocus();
    }

    private void startPlayingAnimation() {
        stopAndClearAnimation(mVoiceAnimation);
        mVoice.setBackgroundResource(R.drawable.ic_voice_animation);
        mVoiceAnimation = (AnimationDrawable) mVoice.getBackground();
        mVoiceAnimation.start();
    }

    private void stopPlayingAnimation() {
        stopAndClearAnimation(mVoiceAnimation);
        mVoice.setBackgroundResource(R.drawable.ic_voice_play_selector);
    }

    private void startVoiceLoadingAnimation() {
        mVoiceLoading.setVisibility(View.VISIBLE);
        mVoiceLoadingAnimation.start();
    }

    private void stopVoiceLoadingAnimation() {
        stopAndClearAnimation(mVoiceLoadingAnimation);
        mVoiceLoading.setVisibility(View.GONE);
    }

    private void stopAndClearAnimation(AnimationDrawable animation) {
        if (animation != null) {
            animation.stop();
        }
    }

    private void gotoExerciseActivity() {
        Intent intent = new Intent(this, ExerciseActivity.class);
        intent.putExtra(ExerciseActivity.EXTRA_TYPE, mSubjectType);
        intent.putExtra(ExerciseActivity.EXTRA_GRADE, mGrade);
        intent.putExtra(ExerciseActivity.EXTRA_INDEX, mCurrentPosition);
        startActivity(intent);
    }

    private void openDraftPaper() {
        mDraftPaper.setVisibility(View.VISIBLE);
    }

    private void showExampleExplanation() {
        mTeacherSelectedParent.setVisibility(View.GONE);
        mTeacherParent.setVisibility(View.GONE);
        //解决再列题讲解界面有残影问题。
//        mPlayerView.setVisibility(View.GONE);
        if (mNote != null) {
            mNote.hideNote();
        }
        mExampleSelectedParent.setVisibility(View.VISIBLE);
        mExampleParent.setVisibility(View.VISIBLE);
        updatePreviousOrNextState();
        pauseOrStopVideo();
        pauseAudio();
    }

    private void showTeacherExplanation() {
        mTeacherParent.setVisibility(View.VISIBLE);
        mTeacherSelectedParent.setVisibility(View.VISIBLE);
//        if (mPlayerView.hasData()) {
//            mPlayerView.setVisibility(View.VISIBLE);
//        }
        if (mNote != null) {
            mNote.showNote();
        }
        mExampleParent.setVisibility(View.GONE);
        mExampleSelectedParent.setVisibility(View.GONE);
        mPrevious.setVisibility(View.GONE);
        mNext.setVisibility(View.GONE);
        if (updateTeacherData) {
            updateTeacherView(mProjectList.get(mCurrentPosition));
        }
    }

    private void updateTeacherView(ProjectEntity.Project data) {
        updateTeacherData = false;
//        mExplainWebView.loadUrl("file:///android_asset/js/explain.html");
//        mPlayerView.initVideoList(mCurrentPosition, data.getVideoInfoList(), false);
        boolean play = mType == VideoExtraNames.TYPE_GOTO;
        mPlayerView.initVideoList(mCurrentPosition, mVideoIndex, mSeekPosition, data.getVideoInfoList(), play);
        reset();
//        mExplainWebView.clearCache(true);
//        mExplainWebView.clearHistory();
        mExplainWebView.loadDataWithBaseURL(HttpConfig.RESOURCE_HOST,
                JsUtils.makeExplainHtmlText(data.getExplain()), "text/html", "UTF-8", "");
        if (Lists.isEmpty(data.getVideoInfoList())) {
            mTeacherDivider.setVisibility(View.GONE);
        } else {
            mTeacherDivider.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 用完intent解析出来的数据， 马上置0，否则影响切换project时接着跳进度。
     */
    private void reset() {
        mType = VideoExtraNames.TYPE_SET_RESULT;
        mSeekPosition = 0;
        mVideoIndex = 0;
    }

    private void updateExampleView(ProjectEntity.Project data) {
        if (data.getExercises() == null || data.getExercises().size() == 0) {
            mExerciseBtn.setVisibility(View.GONE);
        } else {
            mExerciseBtn.setVisibility(View.VISIBLE);
        }

        List<ProjectEntity.Project.Example> examples = data.getExample();
        if (examples == null || examples.size() == 0) {
            toastNoData();
        } else {
            mExampleList.clear();
            mExampleList.addAll(examples);
            mExampleAdapter.notifyDataSetChanged();
        }
        mExampleIndex = 0;
        mExampleRv.scrollToPosition(0);
        updatePreviousOrNextState();
    }

    /**
     * 是否在名师辅导界面。
     */
    private boolean isTeacherExplanationShowing() {
        return mTeacherSelectedParent.getVisibility() == View.VISIBLE;
    }

    private void toastNoData() {
        ToastUtils.show(StudyActivity.this, "暂无数据");
    }

    //监听网络变化广播
    private void registerReceiver() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            mReceiver = new NetworkChangeReceiver();
            registerReceiver(mReceiver, filter);
        } else {
            ConnectivityManager manager =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            mNetworkCallback = new NetworkCallback();
            manager.requestNetwork(new NetworkRequest.Builder().build(), mNetworkCallback);
        }
    }

    private void unregisterReceiver() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            unregisterReceiver(mReceiver);
        } else {
            ConnectivityManager manager =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            manager.unregisterNetworkCallback(mNetworkCallback);
        }
    }

    private void checkNetWork() {
        handleNetWorkChange(NetworkUtils.isConnected(this));
    }

    private void handleNetWorkChange(boolean isAvailable) {
        Log.e(TAG, "handleNetWorkChange() called with: isAvailable = " + isAvailable + "");
        runOnUiThread(() -> {
            if (isAvailable) {
                dismissNoNotworkDialog();
                mPlayerView.enablePlayerController(true);
//                mVoice.setEnabled(true);
                mVoice.setActivated(true);
            } else {
                ToastUtils.showLong(StudyActivity.this, "网络连接已断开");

                int videoState = mPlayerView.getPlayState();
                Log.e(TAG, "handleNetWorkChange: videi state = " + videoState);
                if (videoState == 1) {
                    mPlayerView.stopVideo();
                    checkNetwork();
                }

                Log.e(TAG, "handleNetWorkChange: lost mediaPlayer state = " + mPlayer.getPlayState());
                IMediaPlayer.PlayState state = mPlayer.getPlayState();
                switch (state) {
                    case PREPARED:
                    case PLAYING:
//                        mVoice.setEnabled(true);
                        mVoice.setActivated(true);
                        break;
                    case PREPARING:
                        pauseAudio();
                    default:
//                        mVoice.setEnabled(false);
                        break;
                }
            }
        });

    }

    /**
     * 网络连接广播
     */
    private class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "onReceive: action = " + intent.getAction());
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                if (NetworkUtils.isConnected(StudyActivity.this)) {
                    handleNetWorkChange(true);
                } else {
                    handleNetWorkChange(false);
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private class NetworkCallback extends ConnectivityManager.NetworkCallback {
        @Override
        public void onAvailable(Network network) {
            super.onAvailable(network);
            Log.e(TAG, "onAvailable: ");
            handleNetWorkChange(true);
        }

        @Override
        public void onLost(Network network) {
            super.onLost(network);
            Log.e(TAG, "onLost: ");
            handleNetWorkChange(false);
        }
    }

    private int requestAudioFocusTransient() {
        Log.d(TAG, "requestAudioFocusTransient: hasRequestAudioFocus = " + hasRequestAudioFocus);
        if (hasRequestAudioFocus) {
//            Log.e(TAG, "requestAudioFocusTransient: has not request audio focus.");
            return AudioManager.AUDIOFOCUS_REQUEST_FAILED;
        }
        AudioManager sAudioManager =
                (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        if (sAudioManager != null) {
            int ret = sAudioManager.requestAudioFocus(mAudioFocusChangeListener,
                    AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            Log.e(TAG, "requestAudioFocusTransient: ret = " + ret);
            if (ret == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
                Log.e(TAG, "requestAudioFocus fail: ret = " + ret);
            } else if (ret == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                hasRequestAudioFocus = true;
            }
            return ret;
        }
        Log.e(TAG, "requestAudioFocus: valueAt audio service fail");
        return AudioManager.AUDIOFOCUS_REQUEST_FAILED;
    }

    /**
     * 恢复播放
     *
     * @return 是否抢焦点成功，如果为{@link AudioManager#AUDIOFOCUS_GAIN}代表抢焦点成功，反之。
     */
    private int abandonAudioFocus() {
        Log.d(TAG, "abandonAudioFocus: hasRequestAudioFocus = " + hasRequestAudioFocus);
        if (!hasRequestAudioFocus) {
//            Log.e(TAG, "abandonAudioFocus: has not request audio focus.");
            return AudioManager.AUDIOFOCUS_REQUEST_FAILED;
        }
        AudioManager sAudioManager =
                (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        if (sAudioManager != null) {
            int ret = sAudioManager.abandonAudioFocus(mAudioFocusChangeListener);
            Log.d(TAG, "abandonAudioFocus: ret = " + ret);
            if (ret == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
                Log.e(TAG, "abandonAudioFocus fail: ret = " + ret);
            } else if (ret == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                hasRequestAudioFocus = false;
            }

            return ret;
        }
        Log.e(TAG, "abandonAudioFocus: valueAt audio service fail");
        return AudioManager.AUDIOFOCUS_REQUEST_FAILED;
    }

    private AudioManager.OnAudioFocusChangeListener mAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            Log.e(TAG, "onAudioFocusChange: focusChange = " + focusChange);
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
//                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    Log.e(TAG, "onAudioFocusChange: loss focus. focus flag = " + focusChange);
                    //doSomething,
                    hasRequestAudioFocus = false;
                    pauseMedia();
                    break;
                default:
                    Log.e(TAG, "onAudioFocusChange: default focus = " + focusChange);
            }
        }
    };

    private class MediaPlayerListener implements IMediaPlayer.IMediaPlayerListener {

        @Override
        public void onInit() {
            startVoiceLoadingAnimation();
        }

        @Override
        public void onPrepared() {
            stopVoiceLoadingAnimation();
        }

        @Override
        public void onRelease() {

        }

        @Override
        public void onPlaying() {
            startPlayingAnimation();
        }

        @Override
        public void onPaused() {
            stopPlayingAnimation();
        }

        @Override
        public void onStopped() {
            stopVoiceLoadingAnimation();
            stopPlayingAnimation();
        }

        @Override
        public void onCompletion() {
            stopPlayingAnimation();
        }

        @Override
        public void onError(String error, IMediaPlayer.ErrorType errorType) {
            //TODO：访问失败， 判断是否是资源有问题，更新缓存。
            Log.e(TAG, "onError() called with: error = " + error + ", errorType = " + errorType + "");
//                无网络：{"msg":"what: 1; extra:-2147483648"}, errorType = MEDIA_ERROR_UNKNOWN
            switch (errorType) {
                case MEDIA_ERROR_UNKNOWN:
                    stopAudio();
                    stopPlayingAnimation();
                    stopVoiceLoadingAnimation();
                    if (checkNetwork()) {
                        ToastUtils.showShort(StudyActivity.this, "未知错误！");
                    }
                    break;
                default:
                    ToastUtils.showLong(StudyActivity.this, errorType.toString());
                    break;
            }
        }

        @Override
        public void onBufferingUpdate(int percent) {

        }

        @Override
        public void onBufferingStart() {

        }

        @Override
        public void onBufferingEnd() {

        }
    }

    private class JavaScriptInterface {
        @JavascriptInterface
        public void onLoadFinish(int height) {
            Log.e(TAG, "onLoadFinish() called with: webView height = " + height + "");
            int id = mProjectList.get(mCurrentPosition).getId();
//            height = SizeUtils.dipToPixels(StudyActivity.this, height);
            //解决底部空白问题。
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mWebProgress.isActivated()) {
                        mWebProgress.setVisibility(View.GONE);
                    } else {
                        mWebProgress.setActivated(true);
                    }
                    if (mTeacherSelectedParent.getVisibility() == View.VISIBLE) {
                    }
                    updateNote(id, height);
                    Log.e(TAG, "run: webView height = " + mExplainWebView.getHeight());
                    //解决android4.4及以下系统，加载新的数据时，webView尾部后大段空白的问题
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        int offset = SizeUtils.dp2px(StudyActivity.this, 39);
                        ViewGroup.LayoutParams lp = mExplainWebView.getLayoutParams();
                        lp.height = (int) ((height + offset) * getResources().getDisplayMetrics().density);
                        mExplainWebView.setLayoutParams(lp);
                    }
                }
            });
        }
    }

}
