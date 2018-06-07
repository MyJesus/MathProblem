package com.readboy.mathproblem.cache;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.util.SparseArrayCompat;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import com.readboy.mathproblem.application.MathApplication;
import com.readboy.mathproblem.application.SubjectType;
import com.readboy.mathproblem.http.HttpRequestImpl;
import com.readboy.mathproblem.http.auth.AuthManager;
import com.readboy.mathproblem.http.download.DownloadEngine;
import com.readboy.mathproblem.http.response.VideoInfoEntity;
import com.readboy.mathproblem.http.response.VideoInfoEntity.VideoInfo;
import com.readboy.mathproblem.http.service.PostVideoInfoService;
import com.readboy.mathproblem.http.request.IdsParams;
import com.readboy.mathproblem.http.response.ProjectEntity;
import com.readboy.mathproblem.util.DateUtils;
import com.readboy.mathproblem.util.FileUtils;
import com.readboy.mathproblem.util.JsonMapper;
import com.readboy.mathproblem.util.Lists;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by oubin on 2017/9/21.
 * TODO 1.文件读写是否需要异步；2.最好加上更新日志。
 * <p>
 * 缓存策略：1.
 */

public final class CacheEngine implements CacheConfig {
    private static final String TAG = "oubin_CacheEngine";

    /**
     * TODO: 集合类型待测试，评估。可使用LruCache等Collection，有同步锁，数据访问安全
     * key: 为0-5，0代表一年级。
     */
    private static SparseArrayCompat<ProjectEntityWrapper> mGuideArray = new SparseArrayCompat<>();
    private static SparseArrayCompat<ProjectEntityWrapper> mMethodArray = new SparseArrayCompat<>();

    /**
     * 当前选中ProjectWrapper
     * TODO, 可能数据会丢失，不可靠，建议通过Intent在Activity直接传递
     */
    private static ProjectEntityWrapper mProjectWrapper;
    /**
     * 当前选中的ProjectEntity.Project在mProjectWrapper.projectList中的位置
     * 记得在Activity更新该值
     */
    private static int mIndex = INVALID_INDEX;
    private static SubjectType mSubjectType = SubjectType.guide;
    private static Call<ProjectEntity> mProjectCall;

    private CacheEngine() {
        Log.e(TAG, "CacheEngine: create.");
    }

    public static void intiCacheEngine() {
    }

    /**
     * 并且把上次更新的时间缓存到运行内存里，加速读取，而不是每次都读取Preferences,
     * 获取可以通过File.lastModified()获取上次文件修改时间。
     */
    private static void asyncUpdateCache(SubjectType type, int grade) {
        Log.e(TAG, "asyncUpdateCache() called with: type = " + type + ", grade = " + grade + "");
        final AsyncTask<Void, Void, Boolean> asyncTask = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                getProjectFromHttp(type, grade, new EmptyCacheCallback() {
                    @Override
                    public void onResponse(ProjectEntityWrapper entity) {
                        super.onResponse(entity);
                        Log.e(TAG, "asyncUpdateCache onResponse: update success! subjectType = " + type + ", grade = " + grade);
                    }

                    @Override
                    public void onError(String message, Throwable e) {
                        super.onError(message, e);
                        Log.e(TAG, "onError: e = " + message + e.toString());
                        Log.e(TAG, "asyncUpdateCache onError: update fail! subjectType = " + type + ", grade = " + grade);
                    }
                });
                return null;
            }
        };
        asyncTask.execute();
    }

    private static boolean shouldUpdateCache(String path) {
        File file = new File(path);
        long lastModified = file.lastModified();
        Log.e(TAG, "shouldUpdateCache: lastModified = " + lastModified + ", current = " + System.currentTimeMillis());
        return lastModified < System.currentTimeMillis() - DEFAULT_UPDATE_PERIOD;
    }

    private static void appendLog(String content) {
        String log = "\n" + DateUtils.getCurDateString() + " " + content;
        FileUtils.appendStringToFile(log, DATA_UPDATE_LOG);
    }

    private static void updateCache(final String type, final String grade) {
        Log.e(TAG, "updateCache() called with: type = " + type + ", grade = " + grade + "");
        final String file = uniteFilePath(type, grade);
        HttpRequestImpl.getProjects(type, grade, new Callback<ProjectEntity>() {
            @Override
            public void onResponse(Call<ProjectEntity> call, Response<ProjectEntity> response) {
                ProjectEntity entity = response.body();
                if (entity != null) {
                    String jsonStr = JsonMapper.toJson(entity);
                    if (FileUtils.writeString(jsonStr,
                            file)) {
                        appendLog(file + "更新成功");
                    } else {
                        //TODO 内存不足，无法缓存数据。
                        logWhyCanNotWriteFile(new File(file).getParent(),
                                jsonStr == null ? 0 : jsonStr.getBytes().length);
                    }
//                    if (entity.getData() != null && entity.getData().size() != 0){
                    ProjectEntityWrapper wrapper = new ProjectEntityWrapper(SubjectType.valueOf(type),
                            Integer.valueOf(grade) - 1);
                    wrapper.setProjectList(entity.getData());
                    updateSparseArray(wrapper);
                } else {
                    appendLog(file + "更新失败，ProjectEntity == null");
                }
            }

            @Override
            public void onFailure(Call<ProjectEntity> call, Throwable t) {
                appendLog(file + "更新失败，t = " + t.toString());
            }
        });
    }

    private static void logWhyCanNotWriteFile(String dir, long targetFileLength){
        Log.e(TAG, "logWhyCanNotWriteFile() called with: dir = " + dir + ", targetFileLength = " + targetFileLength + "");
        Log.e(TAG, "onResponse: 内存不足，无法缓存。");
        Log.e(TAG, "logWhyCanNotWriteFile: getAvailableSize = " + FileUtils.getAvailableSize(dir));
    }

    /**
     * 获取到的视频完整链接未必是有效的，缓存中的url可能已经过去不可直接使用，需要重新判断，鉴权。
     * 可能为空，如果RAM和文件中没有缓存，则返回空，需要通过网络获取。
     */
    public static ProjectEntityWrapper getProject(SubjectType type, int grade) {
        ProjectEntityWrapper result = getProjectFromArray(type, grade);
        if (result == null) {
            result = getProjectFromFile(type, grade);
        }
        return result;
    }

    private static ProjectEntityWrapper getProjectFromArray(SubjectType type, int grade) {
        switch (type) {
            case guide:
                return mGuideArray.get(grade);
            case method:
                return mMethodArray.get(grade);
            default:
                return null;
        }
    }

    /**
     * 获取到的视频完整链接未必是有效的，缓存中的url可能已经过去不可直接使用，需要重新判断，鉴权。
     */
    private static ProjectEntityWrapper getProjectFromFile(SubjectType type, int grade) {
        Log.e(TAG, "getProjectFromFile: ");
        String filePath = uniteFilePath(type, grade);
        ProjectEntityWrapper wrapper = new ProjectEntityWrapper(type, grade);
        String jsonString = FileUtils.readString(filePath);
        if (TextUtils.isEmpty(jsonString)) {
            Log.e(TAG, "getProjectFromFile: jsonString = null");
            return null;
        }
        ProjectEntity entity = JsonMapper.fromJson(jsonString, ProjectEntity.class);
        //如数据正常，但是data为空，是否重新通过网络获取。
        //entity是防止串改数据
        if (entity != null && entity.getData() != null) {
            wrapper.setProjectList(entity.getData());
            if (shouldUpdateCache(filePath)) {
                Log.e(TAG, "getProjectFromFile: shouldUpdateCache. type = " + type + ", grade = " + grade);
                asyncUpdateCache(type, grade);
            } else {
                Log.e(TAG, "getProjectFromFile: not need update cache. type = " + type + ", grade = " + grade);
            }
            updateSparseArray(wrapper);
            return wrapper;
        } else {
            Log.e(TAG, "getProjectFromFile: 数据解析出错，可能数据被修改过。");
            return null;
        }
    }

    public static void getProjectFromHttp(SubjectType type, int grade, CacheCallback callback) {
        sendBeforeEvent(callback);
        String gradeStr = String.valueOf(grade + 1);
        mProjectCall = HttpRequestImpl.getProjects(type.name(), gradeStr, new Callback<ProjectEntity>() {
            @Override
            public void onResponse(Call<ProjectEntity> call, Response<ProjectEntity> response) {
                ProjectEntity entity = response.body();
                if (entity != null && entity.getErrNo() == 0) {
                    getVideoInfoFromHttp(entity, type, grade, callback);
                } else {
                    String message = "response body() = null";
                    sendErrorEvent(callback, message, new ServerException(""));
                }

            }

            @Override
            public void onFailure(Call<ProjectEntity> call, Throwable t) {
                Log.e(TAG, "onFailure: is canceled = " + call.isCanceled());
                Log.e(TAG, "project onFailure: t = " + t.toString(), t);
                if (call.isCanceled()) {
                    Log.e(TAG, "onFailure: request was canceled, t = " + t.toString());
                    return;
                }
                sendErrorEvent(callback, t.toString(), t);
            }
        });
    }

    private static void getVideoInfoFromHttp(ProjectEntity entity, final SubjectType type, final int grade, final CacheCallback callback) {
        List<Integer> idList = new ArrayList<>();
        final List<ProjectEntity.Project> projectList = entity.getData();
//        projectList.stream().filter(project -> project.getVideo2() != null).forEach(project -> idList.addAll(project.getVideo2()));
        for (ProjectEntity.Project project : projectList) {
            if (project.getVideo2() != null) {
                idList.addAll(project.getVideo2());
            }
        }
        if (idList.size() > 0) {
            getVideoInfoFromHttp(idList, new Callback<VideoInfoEntity>() {
                @Override
                public void onResponse(Call<VideoInfoEntity> call, Response<VideoInfoEntity> response) {
                    VideoInfoEntity videoInfoEntity = response.body();
                    if (videoInfoEntity != null && videoInfoEntity.getOk() == 1
                            && videoInfoEntity.getData() != null) {
                        SparseArray<VideoInfo> temp = new SparseArray<>();
                        for (VideoInfo videoInfo : videoInfoEntity.getData()) {
//                            videoInfo.setUrl(AuthManager.auth(videoInfo.getVideoUri()));
                            temp.put(videoInfo.getId(), videoInfo);
                        }
                        Log.e(TAG, "onResponse: uri = " + AuthManager.auth("/download/qm/test.mp4"));
                        for (ProjectEntity.Project project : projectList) {
                            if (!Lists.isEmpty(project.getVideo2())) {
                                List<VideoInfo> video2List = new ArrayList<>();
                                for (Integer id : project.getVideo2()) {
                                    video2List.add(temp.get(id));
                                }
                                project.setVideoInfoList(video2List);
                            }
                        }

                        handlerNewProject(entity, type, grade, callback);
                    }
                }

                @Override
                public void onFailure(Call<VideoInfoEntity> call, Throwable t) {
                    Log.e(TAG, "video2 onFailure: t : " + t.toString(), t);
                    sendErrorEvent(callback, t);
//                    handlerNewProject(entity, type, grade, callback);
                }
            });
        } else {
            handlerNewProject(entity, type, grade, callback);
        }
    }

    private static void handlerNewProject(ProjectEntity entity, SubjectType type, int grade, CacheCallback callback) {
        boolean noVideoInfo = false;
        for (ProjectEntity.Project project : entity.getData()) {
            if (isInvalidProject(project)) {
                noVideoInfo = true;
                Log.e(TAG, "handlerNewProject: no complete video info, project = " + project.getGrade()
                        + ", " + project.getName());
                Log.e(TAG, "handlerNewProject: video2 = " + Arrays.toString(project.getVideo2().toArray())
                        + ", videoInfo = " + Arrays.toString(project.getVideoInfoList().toArray()));
                break;
            }
        }

        //确保数据安全了，才更新到缓存中。主要原因是VideoInfo获取需要再次访问另外接口。
        if (!noVideoInfo) {
            updateSparseArray(type, grade, entity);
            updateDataToFile(type, grade, entity);
        }
        ProjectEntityWrapper wrapper = new ProjectEntityWrapper(type, grade);
        wrapper.setProjectList(entity.getData());
//        cacheVideoUrl(wrapper);
        sendResponseEvent(callback, wrapper);
    }

    private static boolean isInvalidProject(ProjectEntity.Project project) {
        return !Lists.isEmpty(project.getVideo2()) &&
                (Lists.isEmpty(project.getVideoInfoList())
                        || project.getVideoInfoList().size() != project.getVideo2().size());
    }

    public static void getProject(SubjectType type, int grade, CacheCallback callback) {
        sendBeforeEvent(callback);
        ProjectEntityWrapper wrapper = getProject(type, grade);
        if (wrapper != null) {
            for (ProjectEntity.Project project : wrapper.getProjectList()) {
                if (!Lists.isEmpty(project.getVideo2()) && Lists.isEmpty(project.getVideoInfoList())) {
                    Log.e(TAG, "getProject: videoInfo is null.");
                    ProjectEntity entity = new ProjectEntity();
                    entity.setData(wrapper.getProjectList());
                    getVideoInfoFromHttp(entity, type, grade, callback);
                    return;
                }
            }
            callback.onResponse(wrapper);
            callback.onAfter();
        } else {
            Log.e(TAG, "updateDataList: data = null");
            cancelHttpRequest();
            getProjectFromHttp(type, grade, callback);
        }

    }

    public static Call<VideoInfoEntity> getVideoInfoFromHttp(List<Integer> ids, Callback<VideoInfoEntity> callback) {
        PostVideoInfoService service = DownloadEngine.getInstance().create(PostVideoInfoService.class);
        IdsParams params = new IdsParams(ids);
        Call<VideoInfoEntity> call = service.getVideoUrl(params.getMap());
        call.enqueue(callback);
        return call;
    }

    /**
     * 不可靠，可能会丢失数据，比如关闭权限
     */
    public static ProjectEntityWrapper getCurrentProjectWrapper() {
        return mProjectWrapper;
    }

    public static int getCurrentIndex() {
        return mIndex;
    }

    public static ProjectEntity.Project getCurrentProject() {
        Log.e(TAG, "getCurrentProject: mIndex = " + mIndex + ", mProjectWrapper = " + mProjectWrapper);
        return mProjectWrapper == null ? null : mProjectWrapper.getProjectList().get(mIndex);
    }

    public static void setCurrentIndex(int projectIndex) {
        Log.d(TAG, "setCurrentIndex() called with: projectIndex = " + projectIndex + "");
        if (mProjectWrapper == null) {
            Log.e(TAG, "setCurrentIndex: projectWrapper = null");
            return;
        }
        //TODO: 数据防御，是否需要处理，
        // 否，测试期间直接抛出异常处理。
        // 是，可能测试期间无法察觉出问题，但是不会抛出异常。
        int size = mProjectWrapper.getProjectList().size();
        if (projectIndex < 0 || projectIndex >= size) {
            Log.e(TAG, "setCurrentIndex: index = " + projectIndex + ", size = " + projectIndex);
            CrashReport.postCatchedException(new ArrayIndexOutOfBoundsException("setCurrentIndex: index = "
                    + projectIndex + ", size = " + projectIndex));
        }

        mIndex = projectIndex;
    }

    public static void setCurrentProjectWrapper(ProjectEntityWrapper wrapper) {
        Log.e(TAG, "setCurrentProjectWrapper() called with: wrapper = " + wrapper + "");
        setCurrentProjectWrapper(INVALID_INDEX, wrapper);
    }

    public static void setCurrentProjectWrapper(int index, ProjectEntityWrapper wrapper) {
        Log.e(TAG, "setCurrentProjectWrapper: index = " + index + ", wrapper = " + wrapper);
        if (wrapper == null) {
            mProjectWrapper = null;
            mIndex = -1;
            return;
        }
        mProjectWrapper = wrapper;
        mIndex = index;
    }

    private static void updateSparseArray(SubjectType type, int grade, ProjectEntity entity) {
        updateSparseArray(new ProjectEntityWrapper(type, grade, entity.getData()));
    }

    /**
     * 保存数据到运行内存里，方便下次调用
     */
    private static void updateSparseArray(ProjectEntityWrapper wrapper) {
        Log.e(TAG, "updateSparseArray: subjectType = " + wrapper.getType().name()
                + ", grade = " + wrapper.getGrade());
        int grade = wrapper.getGrade();
        switch (wrapper.getType()) {
            case guide:
                mGuideArray.put(grade, wrapper);
                break;
            case method:
                mMethodArray.put(grade, wrapper);
                break;
            default:
                Log.e(TAG, "updateSparseArray: default = " + wrapper.getType());
        }
    }

    /**
     * 保存数据到本地
     *
     * @param entity 需要确保数据是完整的，其中包括视频的详细信息。
     */
    private static void updateDataToFile(SubjectType type, int grade, ProjectEntity entity) {
        Log.e(TAG, "updateDataToFile: ");
        String file = uniteFilePath(type, grade);
        if (TextUtils.isEmpty(file)) {
            Log.e(TAG, "updateDataToFile: filePath is null or empty, file = " + file);
            return;
        }
        String jsonStr = JsonMapper.toJson(entity);
        if (FileUtils.writeString(jsonStr, file)) {
            appendLog(file + " 更新成功");
        } else{
            //TODO 内存不足，无法缓存数据。
            logWhyCanNotWriteFile(new File(file).getParent(),
                    jsonStr == null ? 0 : jsonStr.getBytes().length);
            appendLog(file + " 更新失败");
        }
    }

    private static String uniteFilePath(SubjectType type, int grade) {
        return uniteFilePath(type.name(), String.valueOf(grade + 1));
    }

    private static String uniteFilePath(String type, String grade) {
        if (MathApplication.getInstance() == null) {
            CrashReport.postCatchedException(new NullPointerException("MathApplication.getInstance is null."));
            return "";
        }
        File file = MathApplication.getInstance().getExternalCacheDir();
        String parent = file != null ? file.getAbsolutePath()
                : PATH;
        //180125001， 4.1.17版本之前
//        return parent + File.separator + "cache" + File.separator + type + "_" + grade + CACHE_FILE_EXTENSION;
        return parent + File.separator + "project" + File.separator + type + "_" + grade + CACHE_FILE_EXTENSION;
//        return CACHE_FILE_PATH + File.separator + type + "_" + grade + CACHE_FILE_EXTENSION;
    }

    public static void cancelHttpRequest() {
        if (mProjectCall != null && !mProjectCall.isCanceled()) {
            Log.e(TAG, "cancelHttpRequest: ");
            mProjectCall.cancel();
        }
    }

    private void logToFile(String msg) {

    }

    public static void versionChangeHandle(Context context) {
        try {
            int versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0)
                    .versionCode;
            Log.e(TAG, "versionChangeHandle: version = " + versionCode);
            if (versionCode <= 180125001) {
                asyncDeleteCache();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void asyncDeleteCache() {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                return null;
            }
        }.execute();
    }

    private static void sendBeforeEvent(CacheCallback cacheCallback) {
        if (cacheCallback != null) {
            cacheCallback.onBefore();
        }
    }

    private static void sendAfterEvent(CacheCallback cacheCallback) {
        if (cacheCallback != null) {
            cacheCallback.onAfter();
        }
    }

    private static void sendResponseEvent(CacheCallback cacheCallback, ProjectEntityWrapper wrapper) {
        if (cacheCallback != null) {
            cacheCallback.onResponse(wrapper);
            cacheCallback.onAfter();
        }
    }

    private static void sendErrorEvent(CacheCallback cacheCallback, String msg, Throwable e) {
        if (cacheCallback != null) {
            cacheCallback.onError(msg, e);
            cacheCallback.onAfter();
        }
    }

    private static void sendErrorEvent(CacheCallback cacheCallback, Throwable e) {
        sendErrorEvent(cacheCallback, e.toString(), e);
    }

}
