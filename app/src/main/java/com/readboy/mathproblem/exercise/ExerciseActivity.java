package com.readboy.mathproblem.exercise;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper2;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.errorqstupload.ErrorQuestionDB;
import com.example.errorqstupload.bean.TinyQuestionInfo;
import com.readboy.mathproblem.R;
import com.readboy.mathproblem.activity.BaseActivity;
import com.readboy.mathproblem.application.MathApplication;
import com.readboy.mathproblem.application.SubjectType;
import com.readboy.mathproblem.cache.CacheEngine;
import com.readboy.mathproblem.cache.ProjectEntityWrapper;
import com.readboy.mathproblem.db.Score;
import com.readboy.mathproblem.dialog.BaseDialog;
import com.readboy.mathproblem.dialog.CommonDialog;
import com.readboy.mathproblem.http.response.ProjectEntity;
import com.readboy.mathproblem.note.DraftPaperView;
import com.readboy.mathproblem.util.JsonMapper;
import com.readboy.mathproblem.util.Lists;
import com.readboy.mathproblem.util.ToastUtils;
import com.readboy.mathproblem.widget.Dog;
import com.readboy.textbook.model.Question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 练习题界面。
 *
 * @author oubin
 */
public class ExerciseActivity extends BaseActivity implements View.OnClickListener,
        RadioGroup.OnCheckedChangeListener, ExerciseResultDialog.OnClickListener {
    private static final String TAG = "oubin_ExerciseActivity";

    public static final String EXTRA_TYPE = "extra_type";  //数据类型：Subject
    public static final String EXTRA_GRADE = "extra_grade";  //数据类型int
    public static final String EXTRA_INDEX = "extra_index";  //数据类型int

    private static final int DELAY_NEXT_MILLIS = 2000;
    private static final int MESSAGE_NEXT_EXERCISE = 1;

    private RecyclerView mExerciseRv;
    private PagerLayoutManager mLayoutManager;
    //    private ExerciseAdapter mExerciseAdapter;
    private DraftPaperView mDraftPaper;
    private ImageView mCat;
    private AnimationDrawable mCatAnimation;
    private Dog mDog;
    private TextView mExerciseProgress;
    private View mPrevious;
    private View mNext;
    private TextView mExerciseSubmit;
    private TextView mProjectName;

    //    private final List<Exercise> mExerciseList = new ArrayList<>();
    private int mCurPosition = 0;
    private ExerciseResult mResult = new ExerciseResult();
    private long mStartTime;
    private long mPauseTime;

    private final List<Question.Item> mQuestionItemList = new ArrayList<>();
    private ExerciseWebAdapter mExerciseWebAdapter;

    private ProjectEntity.Project mProject;
    private SubjectType mSubjectType;
    private int mGrade;
    private int mIndex;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_NEXT_EXERCISE:
                    nextExercise();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);
        parseIntent();

        initView();
        assignView();
        initAnimation();

        if (!needRequestPermissions()) {
            initData();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startAnimation();
        adjustExerciseTime();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopAnimation();
        mPauseTime = System.currentTimeMillis();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: ");
        MathApplication.refWatch(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mSubjectType != null) {
            outState.putString(EXTRA_TYPE, mSubjectType.name());
            outState.putInt(EXTRA_GRADE, mGrade);
            outState.putInt(EXTRA_INDEX, mIndex);
        } else {
            Log.e(TAG, "onSaveInstanceState: subject = null.");
        }
    }

    @Override
    protected void assignView() {
        super.assignView();
    }

    @Override
    protected void onRequestPermissionsSuccess() {
        super.onRequestPermissionsSuccess();
        initData();
    }

    public void openDraftPaper() {
        mDraftPaper.show();
    }

    @Override
    public void onBackPressed() {
        Log.e(TAG, "onBackPressed: ");
        if (mDraftPaper.getVisibility() == View.VISIBLE) {
            mDraftPaper.hide();
        } else {
            super.onBackPressed();
            Log.e(TAG, "onBackPressed: super back.");
            stopWebLoading();
        }
    }

    @Override
    public void finish() {
        super.finish();
        Log.e(TAG, "finish: ");
    }

    private void stopWebLoading() {
        int count = mExerciseWebAdapter.getItemCount();
        for (int i = 0; i < count; i++) {
            ExerciseWebViewHolder viewHolder = (ExerciseWebViewHolder) mExerciseRv.findViewHolderForAdapterPosition(i);
            if (viewHolder != null) {
                viewHolder.stopLoading();
            }
        }
    }

    private void parseIntent() {
        Intent intent = getIntent();
        //通过Intent传递数据。
        if (intent != null) {
            mSubjectType = (SubjectType) intent.getSerializableExtra(EXTRA_TYPE);
            mGrade = intent.getIntExtra(EXTRA_GRADE, 0);
            mIndex = intent.getIntExtra(EXTRA_INDEX, 0);
        }
    }

    private void initView() {
        mProjectName = (TextView) findViewById(R.id.project_name);
        mCat = (ImageView) findViewById(R.id.cat);
        mDog = (Dog) findViewById(R.id.dog);
        mExerciseProgress = (TextView) findViewById(R.id.exercise_progress);
        mPrevious = findViewById(R.id.previous);
        mPrevious.setOnClickListener(this);
        mNext = findViewById(R.id.next);
        mNext.setOnClickListener(this);
//        草稿纸
        findViewById(R.id.open_draft_paper).setOnClickListener(this);
        mExerciseSubmit = (TextView) findViewById(R.id.exercise_submit);
        mExerciseSubmit.setOnClickListener(this);
        mDraftPaper = (DraftPaperView) findViewById(R.id.draft_paper);
        mDraftPaper.bringToFront();

        initRecyclerView();
    }

    private void initRecyclerView() {
        mExerciseRv = (RecyclerView) findViewById(R.id.exercise_recycler_view);
        mLayoutManager = new PagerLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mLayoutManager.setOnPositionChangeListener((lastPosition, newPosition) -> updateProgressView(newPosition));
        mExerciseRv.setLayoutManager(mLayoutManager);

        PagerSnapHelper2 snapHelper = new PagerSnapHelper2();
        snapHelper.attachToRecyclerView(mExerciseRv);
//        mExerciseAdapter = new ExerciseAdapter(this);
//        mExerciseRv.setAdapter(mExerciseAdapter);
//        mExerciseAdapter.setData(mExerciseList);

        mExerciseWebAdapter = new ExerciseWebAdapter(this);
        mExerciseRv.setAdapter(mExerciseWebAdapter);
        mExerciseWebAdapter.setData(mQuestionItemList);

    }

    private void initData() {
        Log.e(TAG, "initData: subject = " + mSubjectType + ", mGrade = " + mGrade + ", index = " + mIndex);
        //确保数据安全
        if (mSubjectType == null) {
            ProjectEntityWrapper wrapper = CacheEngine.getCurrentProjectWrapper();
            if (wrapper == null) {
                ToastUtils.showShort(this, "无法初始化数据。");
                finish();
                return;
            }
            mSubjectType = wrapper.getType();
            mGrade = wrapper.getGrade();
            mIndex = CacheEngine.getCurrentIndex();
        }

        //确保数据丢失，重新网络获取等。
        CacheEngine.getProject(mSubjectType, mGrade, this);
    }

    private void initQuestion(int index, ProjectEntityWrapper wrapper) {
        if (wrapper != null && Lists.isNotEmpty(wrapper.getProjectList())) {
            ProjectEntity.Project project = wrapper.getProjectList().get(index);
            if (project == null) {
                ToastUtils.show("初始化数据失败。");
                finish();
            }
            mProject = project;
            initQuestionItemList(project);
        }
    }

    private void initQuestionItemList(ProjectEntity.Project project) {
        Question.getInstance().clearAll();
        if (project != null && Lists.isNotEmpty(project.getExercises())) {
            JSONArray array = new JSONArray();
            int size = project.getExercises().size();
            for (int i = 0; i < size; i++) {
                try {
                    ProjectEntity.Project.Exercises data = project.getExercises().get(i);
                    JSONObject object = new JSONObject(JsonMapper.toJson(data));
                    array.put(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Question.getInstance().setQuestion(array);
            mQuestionItemList.clear();
            mQuestionItemList.addAll(Question.getInstance().getQuestionItemMap().values());
            mExerciseWebAdapter.notifyDataSetChanged();
        }
        mStartTime = System.currentTimeMillis();
    }

    private void initData(int index, ProjectEntityWrapper wrapper) {
        mSubjectType = wrapper.getType();
        mGrade = wrapper.getGrade();
        mIndex = index;
        initQuestion(index, wrapper);
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

    @Override
    public void onResponse(ProjectEntityWrapper entity) {
        super.onResponse(entity);
        initData(mIndex, entity);
        mProjectName.setText(mProject.getName());
        updateProgressView();
    }

    @Override
    public void onClick(View v) {
        Log.e(TAG, "onClick: v = " + v.getId());
        switch (v.getId()) {
            case R.id.previous:
                previousExercise();
                break;
            case R.id.next:
                nextExercise();
                break;
            case R.id.open_draft_paper:
                openDraftPaper();
                break;
            case R.id.exercise_submit:
                if (getString(R.string.exercise_again).equals(mExerciseSubmit.getText())) {
                    exerciseAgain();
                } else {
                    if (isFinishExercise()) {
                        showResultDialog();
                    } else {
                        showRemindDialog();
                    }
                }
                break;
            default:
                Log.e(TAG, "onClick: default = " + v.getId());
                break;
        }
    }

    private void adjustExerciseTime() {
        if (mPauseTime != 0) {
            mStartTime = mStartTime + (System.currentTimeMillis() - mPauseTime);
            mPauseTime = 0;
        }
    }

    private boolean isFinishExercise() {
        for (Question.Item.AnswerInfo answerInfo : Question.getInstance().getAnswerInfo()) {
            if (!answerInfo.isAnswered()) {
                Log.e(TAG, "isFinishExercise: false.");
                return false;
            }
        }
        Log.e(TAG, "isFinishExercise: true.");
        return true;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        Log.e(TAG, "onCheckedChanged: checkedId = " + checkedId);
    }

    /**
     * 练习结果, 查看习题解析点击回调
     */
    @Override
    public void onExplainClick(ExerciseResultDialog dialog) {
        mExerciseWebAdapter.clearData();
        mExerciseWebAdapter.showSolution(true);
        mExerciseWebAdapter.notifyDataSetChanged();
        mExerciseSubmit.setText(getString(R.string.exercise_again));
    }

    @Override
    public void onCancelClick(ExerciseResultDialog dialog) {
        finish();
    }

    /**
     * 练习结果， 视频点击回调
     */
    @Override
    public void onVideoClick(ExerciseResultDialog dialog) {

    }

    private void exerciseAgain() {
        mExerciseSubmit.setText(getString(R.string.submit));
        mExerciseWebAdapter.clearData();
        reset();
        mExerciseWebAdapter.notifyDataSetChanged();
        mExerciseRv.scrollToPosition(0);
        updateProgressView(0);
    }

    private void reset() {
//        int count = mExerciseWebAdapter.getItemCount();
//        for (int i = 0; i < count; i++) {
//            ExerciseWebViewHolder holder = (ExerciseWebViewHolder) mExerciseRv.findViewHolderForAdapterPosition(i);
//            if (holder != null){
//                holder.clearData();
//            }else {
//                Log.e(TAG, "reset: holder = null.");
//            }
//        }
        mStartTime = System.currentTimeMillis();
        mPauseTime = 0;
        initQuestionItemList(mProject);
        mExerciseWebAdapter.showSolution(false);
    }

    private void showResultDialog() {
        float correctRate;
        float correctCount = 0.0f;
        int count = Question.getInstance().getQuestionCount();
        List<TinyQuestionInfo> tinyQuestionInfoList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Question.Item.AnswerInfo answerInfo = Question.getInstance().getAnswerInfo().get(i);
            String[] userAnswers = answerInfo.getUserAnswers();
            if (answerInfo.isAnswerRight()) {
                correctCount++;
            } else if (userAnswers != null && userAnswers.length > 0) {
                ProjectEntity.Project.Exercises exercises = mProject.getExercises().get(i);
                String sourceInfo = JsonMapper.toJson(exercises);
                TinyQuestionInfo tinyQuestionInfo = new TinyQuestionInfo(exercises.getId(), exercises.getType(),
                        exercises.getRole(), sourceInfo);
                tinyQuestionInfoList.add(tinyQuestionInfo);
            }
        }
        if (!Lists.isEmpty(tinyQuestionInfoList)) {
            ErrorQuestionDB.updateErrorCollection(getContentResolver(), tinyQuestionInfoList);
        }
//        for (Question.Item.AnswerInfo answerInfo : Question.getInstance().getAnswerInfo()) {
//            if (answerInfo.isAnswerRight()) {
//                correctCount++;
//            } else {
//
//            }
//        }
        correctRate = correctCount / mQuestionItemList.size();
        Log.e(TAG, "showResultDialog: correctCount = " + correctCount
                + ", count = " + Question.getInstance().getQuestionCount()
                + ", rate = " + correctRate);
        mResult.setTime((System.currentTimeMillis() - mStartTime) / 1000);
        mResult.setCorrectRate(correctRate);
        mResult.setHasVideo(mProject.getVideoInfoList() != null && mProject.getVideoInfoList().size() != 0);
        Score score = new Score(mProject.getId(), (int) correctCount);
        Score.insertScore(getContentResolver(), score);
        ExerciseResultDialog resultDialog = new ExerciseResultDialog(this, mResult);
        resultDialog.setOnClickListener(this);
        resultDialog.show();
    }

    /**
     * 提示还有没作答的题目
     */
    private void showRemindDialog() {
        CommonDialog.Builder builder = new CommonDialog.Builder(this)
                .content(getString(R.string.dialog_not_finish_exercise))
                .leftText(getString(R.string.submit))
                .rightText(getString(R.string.continue_exercise));
        CommonDialog dialog = builder.build();
        dialog.setOnClickListener(new BaseDialog.OnClickListener() {
            @Override
            public void onLeftClick(BaseDialog dialog) {
                showResultDialog();
                dialog.dismiss();
            }

            @Override
            public void onRightClick(BaseDialog dialog) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void nextExercise() {
        Log.e(TAG, "nextExercise: ");
        mLayoutManager.next();
    }

    public void previousExercise() {
        Log.e(TAG, "previousExercise: ");
        mLayoutManager.previous();
    }

    private void updateProgressView(int progress) {
        mCurPosition = progress;
        updateProgressView();
    }

    private void updateProgressView() {
        mExerciseProgress.setText(mCurPosition + 1 + "/" + mQuestionItemList.size());
        if (mCurPosition == mQuestionItemList.size() - 1) {
            mNext.setVisibility(View.GONE);
            if (mPrevious.getVisibility() != View.VISIBLE) {
                mPrevious.setVisibility(View.VISIBLE);
            }
        } else if (mCurPosition == 0) {
            mPrevious.setVisibility(View.GONE);
            if (mNext.getVisibility() != View.VISIBLE) {
                mNext.setVisibility(View.VISIBLE);
            }
        } else {
            if (mPrevious.getVisibility() != View.VISIBLE) {
                mPrevious.setVisibility(View.VISIBLE);
            }
            if (mNext.getVisibility() != View.VISIBLE) {
                mNext.setVisibility(View.VISIBLE);
            }
        }
    }
}
