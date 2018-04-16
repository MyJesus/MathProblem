package com.readboy.mathproblem.notetool;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap.Config;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;

import com.readboy.mathproblem.R;
import com.readboy.mathproblem.http.response.ProjectEntity.Project;
import com.readboy.mathproblem.util.FileUtils;


//TODO 存储不足增加提示，是否保存画笔属性，PenAttr.
public class Note implements View.OnClickListener {
    public static final String TAG = "oubin_Note";
    public static final String DRAFT_FILE_SUFFIX = ".drft";
    public static final int BACK_COLOR_VALUE = 0x00ffffff;
    // public static final int BACK_COLOR_VALUE = 0x50ff0000;
    public static Config BMP_CONFIG = Config.ARGB_8888;
    public static final int ERASER_WIDTH = 30;

    private static final int MENU_STATE_EXPANDED = 0;
    private static final int MENU_STATE_COLLAPSED = 1;
    private int mMenuState = MENU_STATE_COLLAPSED;

    private static long mLastTime;
    //笔记画笔和原本内容父View
    private NoteScrollView mNoteScrollView;
    //画画
    private NoteDrawView mNoteDrawView;
    private PerformType mPerformType;
    //笔记菜单所有面板，包括菜单和打开开关
    private View mNotePanel;
    //笔记菜单父View
    private View mNoteMenuGroup;
    private NoteToolboxDialog mToolboxDialog;

    private RadioButton mPenView;
    private RadioButton mHandView;
    /**
     * 用于记录上次笔记状态，用于展开菜单恢复。
     */
    private PerformType lastPerformType = PerformType.PerformDraw;

    private Context mContext;

    public Note(Activity activity) {
        initView(activity);
        init();
    }

//    public Note(View parent) {
//        initView(parent);
//    }

    private void initView(Activity parent) {
        mContext = parent;
        mNoteScrollView = (NoteScrollView) parent.findViewById(R.id.note_scroll_view);
        mNoteDrawView = (NoteDrawView) parent.findViewById(R.id.note_draw_view);
        mNoteDrawView.setIsNeedCache(true);
        mNoteScrollView.setScrollProcess(y -> mNoteDrawView.updateByScroll(y));

        mNotePanel = parent.findViewById(R.id.note_panel);
        mNoteMenuGroup = parent.findViewById(R.id.note_menu_group);

        mToolboxDialog = new NoteToolboxDialog(mContext);
        mToolboxDialog.setOnDismissListener(dialog -> setPenAttr(mToolboxDialog.getPenAttr()));

        mPenView = (RadioButton) parent.findViewById(R.id.note_menu_pen);
        mHandView = (RadioButton) parent.findViewById(R.id.note_menu_hand);
        mPenView.setChecked(true);
        registerOnClickListener(R.id.note_menu_switch, parent);
        registerOnClickListener(R.id.note_menu_save, parent);
        registerOnClickListener(R.id.note_menu_clear, parent);
        registerOnClickListener(R.id.note_menu_eraser, parent);
        registerOnClickListener(R.id.note_menu_pen, parent);
        registerOnClickListener(R.id.note_menu_hand, parent);

    }

    //初始状态
    private void init() {
        hideNoteMenu();
        mNoteDrawView.initDBInfo(NoteConfig.DB_PATH, NoteConfig.DB_VERSION);
    }

    private void registerOnClickListener(int resId, Activity context) {
        View view = context.findViewById(resId);
        if (view != null) {
            view.setOnClickListener(this);
        }
    }


    /**
     * 高度和上一次存储的高度要一样，要不会重置数据，显示为空。
     * @see NoteDatabase#setNoteInfo(int, int)
     * @param noteId          存入数据库中的ID, 一个笔记一个ID，唯一标识. 使用{@link Project#id}。
     * @param noteTotalHeight 高度,
     */
    public boolean setNoteId(int noteId, int noteTotalHeight) {
        // Log.i(Note.TAG, "setNoteId-noteHeiht="+noteHeiht);
        if (mNoteScrollView.getWidth() > 0 && mNoteScrollView.getHeight() > 0) {
            Log.e(TAG, "setNoteId: mNoteScrollView.getWidth = " + mNoteScrollView.getWidth());
            mNoteScrollView.post(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "setNoteId run: mNoteScrollView.getWidth = " + mNoteScrollView.getWidth()
                     + ", height = " + mNoteScrollView.getHeight());
                    if (mNoteMenuGroup.getVisibility() == View.GONE) {
                        mHandView.setChecked(true);
                        perform(PerformType.PerformMove);
                    }else {
                        mPenView.setChecked(true);
                        perform(PerformType.PerformDraw);
                    }
                    mNoteDrawView.updateNoteInfo(noteId, mNoteScrollView.getWidth(),
                            mNoteScrollView.getHeight(), noteTotalHeight);
                }
            });
//            mNoteDrawView.updateNoteInfo(noteId, mNoteScrollView.getWidth(),
//                    mNoteScrollView.getHeight(), noteTotalHeight);
            return true;
        }
        // Log.e(Note.TAG, "setNoteId- err width or height ==0");
        return false;
    }

    /**
     * @deprecated 直接在NoteDrawView初始化，默认值。
     */
    public void initNoteInfo(String parentPath, String version) {
        mNoteDrawView.initDBInfo(parentPath, version);
    }

    /**
     * 退出activity，一定记得调用该方法，释放bitmap等资源。
     * 内部有自动保存数据，mNoteDrawView.updateState(NoteDrawView.StateType.StateMove);
     */
    public void exit() {
        mNoteDrawView.updateState(NoteDrawView.StateType.StateMove);
        Log.i(TAG, "---- Note exit ");
        mNoteDrawView.exit();
    }

    public PerformType getLastPerformType() {
        return mPerformType;
    }

    public boolean perform(PerformType type) {
        Log.e(TAG, "perform 1 : " + type + ", mPerform = " + mPerformType);
        if (mPerformType == type) {
            return false;
        }
//        Log.e(TAG, "perform 2 : " + type);
        switch (type) {
            case PerformDraw:
//                if (!mNoteScrollView.isScrollStop()) {
//                    Log.e(TAG, "perform: draw not scroll stop.");
//                    return false;
//                }
                mPerformType = type;
                mNoteScrollView.ignoreTouchEvent(true);
                mNoteDrawView.updateState(NoteDrawView.StateType.StateDraw);
                mNoteDrawView.setNoteSwitching(false);
                break;
            case PerformEraser:
                mPerformType = type;
                mNoteScrollView.ignoreTouchEvent(true);
                mNoteDrawView.updateState(NoteDrawView.StateType.StateEraser);
                mNoteDrawView.setNoteSwitching(false);
                break;
            case PerformClear:
                //TODO: 会自动保存。
                mNoteDrawView.clear(true);
//                mNoteScrollView.ignoreTouchEvent(true);
                if (mPerformType == PerformType.PerformEraser) {
                    mPenView.setChecked(true);
                    perform(PerformType.PerformDraw);
                }
                mNoteDrawView.setNoteSwitching(false);
                break;
            case PerformMove:
                mPerformType = type;
                mNoteScrollView.ignoreTouchEvent(false);
                mNoteDrawView.updateState(NoteDrawView.StateType.StateMove);
                mNoteDrawView.setNoteSwitching(false);
                break;
            case PerformNoteSwitching:
                mPerformType = type;
                mNoteScrollView.ignoreTouchEvent(false);
                mNoteDrawView.updateState(NoteDrawView.StateType.StateMove);
//                mNoteDrawView.clear(false);
                mNoteDrawView.setNoteSwitching(true);
                mNoteDrawView.reduceViewHeight();
                break;
            default:
                Log.e(TAG, "perform: type = " + type.name());
                break;
        }
        return true;
    }

    public void setIsDiskTooSmall(boolean isDiskTooSmall) {
        mNoteDrawView.setIsDiskTooSmall(isDiskTooSmall);
    }

    public void setNoteVisible(int visibility) {
        mNoteDrawView.setVisibility(visibility);
        Log.i(TAG, "-------- NoteVisible visibility: " + visibility);
    }

    public void setPenAttr(NoteDrawView.PenAttr attr) {
        mNoteDrawView.setPenAttr(attr);
    }

    public NoteDrawView.PenAttr getPenAttr() {
        return mNoteDrawView.getPenAttr();
    }

    /**
     * 展开画笔设置面板，
     * TODO 是否每次都需要创建一个新的NoteToolbox
     */
    private void showPenToolbox() {
        mToolboxDialog.setPenAttr(getPenAttr());
        mToolboxDialog.show();

    }

    /**
     * 隐藏画笔设置面板
     */
    private void hidePenToolbox() {

    }

    /**
     * 显示笔记功能
     */
    public void showNote() {
        mNotePanel.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏笔记功能
     */
    public void hideNote() {
        mNotePanel.setVisibility(View.GONE);
    }

    /**
     * 展开笔记菜单栏
     * expand
     */
    private void showNoteMenu() {
        Log.e(TAG, "showNoteMenu: ");
        mMenuState = MENU_STATE_EXPANDED;
        mNoteMenuGroup.setVisibility(View.VISIBLE);
//        mNoteDrawView.setVisibility(View.VISIBLE);
        //TODO bug, 待处理
        Log.e(TAG, "showNoteMenu: last perform = " + lastPerformType);
//        perform(lastPerformType);
        mPenView.setChecked(true);
        perform(PerformType.PerformDraw);
    }

    /**
     * 隐藏笔记菜单栏
     * collapse
     */
    private void hideNoteMenu() {
        Log.e(TAG, "hideNoteMenu: ");
        mHandView.setChecked(true);
        perform(PerformType.PerformMove);
        mMenuState = MENU_STATE_COLLAPSED;
        mNoteMenuGroup.setVisibility(View.GONE);
//        mNoteDrawView.setVisibility(View.INVISIBLE);
        mNoteScrollView.ignoreTouchEvent(false);
        Log.e(TAG, "hideNoteMenu: ");
    }

    public void saveNote(){
        Log.e(TAG, "saveNote: ");
        PerformType temp = mPerformType;
        perform(PerformType.PerformMove);
        mNoteDrawView.saveNote();
        FileUtils.sendFileBroadcast(mContext, NoteConfig.DB_FILE_ABSOLUTE_PATH);
        perform(temp);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.note_menu_save:
                mNoteDrawView.saveNote();
                FileUtils.sendFileBroadcast(mContext, NoteConfig.DB_FILE_ABSOLUTE_PATH);
                break;
            case R.id.note_menu_clear:
                perform(PerformType.PerformClear);
                break;
            case R.id.note_menu_eraser:
                perform(PerformType.PerformEraser);
                break;
            case R.id.note_menu_pen:
                if (mPerformType == PerformType.PerformDraw) {
                    showPenToolbox();
                } else {
                    perform(PerformType.PerformDraw);
                }
                break;
            case R.id.note_menu_hand:
                Log.e(TAG, "onClick: hand");
                perform(PerformType.PerformMove);
                break;
            case R.id.note_menu_switch:
                if (mMenuState == MENU_STATE_COLLAPSED) {
                    showNoteMenu();
                } else {
                    Log.e(TAG, "onClick: menu switch mPerform = " + mPerformType);
                    lastPerformType = mPerformType;
                    hideNoteMenu();
                }
                break;
            default:
                Log.e(TAG, "onClick: id = " + v.getId());
                break;
        }
    }


    //TODO 重构，命名规范，状态
    public enum PerformType {
        /**
         * 抓手工具
         */
        PerformMove,
        //画笔
        PerformDraw,
        //橡皮擦
        PerformEraser,
        //清除
        PerformClear,
        //关闭菜单栏，禁用状态
        PerformNoteSwitching
    }

}

