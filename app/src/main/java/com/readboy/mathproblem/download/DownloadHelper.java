package com.readboy.myapplication.download;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.aliyun.vodplayer.downloader.AliyunDownloadConfig;
import com.aliyun.vodplayer.downloader.AliyunDownloadInfoListener;
import com.aliyun.vodplayer.downloader.AliyunDownloadManager;
import com.aliyun.vodplayer.downloader.AliyunDownloadMediaInfo;
import com.aliyun.vodplayer.downloader.AliyunRefreshStsCallback;
import com.aliyun.vodplayer.media.AliyunVidSts;
import com.aliyun.vodplayer.media.IAliyunVodPlayer;
import com.readboy.aliyunplayerlib.helper.VidStsHelper;
import com.readboy.aliyunplayerlib.utils.AliLogUtil;
import com.readboy.aliyunplayerlib.utils.FileUtil;
import com.readboy.aliyunplayerlib.utils.MD5Util;
import com.readboy.aliyunplayerlib.utils.VidStsUtil;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

/**
 * 下载帮助类
 * Created by ldw on 2018/3/30.
 */

public class DownloadHelper {
    private static final String TAG = "DownloadHelper";

    private Context mContext;
    private AliyunDownloadManager mDownloadManager;
    private Stack<String> mVideoQuality = new Stack<>();
    private HashMap<String, DownloadBean> mAllDownloadBeans = new HashMap<>();

    private boolean mHasInitConfig = false;
    private VidStsHelper mVidStsHelper = null;
    private String mAcId = null;
    private String mAkSceret = null;
    private String mSecurityToken = null;
    private HashSet<String> mVidSet = new HashSet<>();

    private int mCurDownloadIndex = 1;


    public DownloadHelper(Context context) {
        AliLogUtil.v(TAG, "---DownloadHelper---");
        mContext = context.getApplicationContext();
        mDownloadManager = AliyunDownloadManager.getInstance(mContext);
        mVideoQuality.add(IAliyunVodPlayer.QualityValue.QUALITY_LOW);//标清
        mVideoQuality.add(IAliyunVodPlayer.QualityValue.QUALITY_FLUENT);//流畅
        mVideoQuality.add(IAliyunVodPlayer.QualityValue.QUALITY_STAND);//高清
        mVideoQuality.add(IAliyunVodPlayer.QualityValue.QUALITY_HIGH);//超清
        mVideoQuality.add(IAliyunVodPlayer.QualityValue.QUALITY_2K);//2k
        mVideoQuality.add(IAliyunVodPlayer.QualityValue.QUALITY_4K);//4k
        mVideoQuality.add(IAliyunVodPlayer.QualityValue.QUALITY_ORIGINAL);//原画

        //设置刷新VidSts回调事件。下载模块会在需要的时候回调这个接口获取新的vidSts信息。作用是避免vidSts的过期。
        mDownloadManager.setRefreshStsCallback(new MyRefreshStsCallback());
        //添加下载状态监听事件
        mDownloadManager.addDownloadInfoListener(new MyDownloadInfoListener());

        mVidStsHelper = new VidStsHelper();
    }

    /**
     * 初始化配置，必须保证有读写存储权限。
     */
    public void initConfig(){
        AliLogUtil.v(TAG, "---initConfig---mHasInitConfig = " + mHasInitConfig);
        if(!mHasInitConfig) {
            mHasInitConfig = true;
            //拷贝安全文件
            copySecretImageFromAssetsToSd();

            //配置下载
            AliyunDownloadConfig config = new AliyunDownloadConfig();
            config.setSecretImagePath(getFileDir().getAbsolutePath() + "/encryptedApp.dat");
            //设置保存路径。请确保有SD卡访问权限。
            String downloadDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/平板名师辅导班/";
            config.setDownloadDir(downloadDir);
            FileUtil.createNomediaFile(downloadDir);
            //设置同时下载个数
            config.setMaxNums(1);
            mDownloadManager.setDownloadConfig(config);
        }
    }

    //拷贝encryptedApp.dat
    private void copySecretImageFromAssetsToSd(){
        String sdPath = getFileDir().getAbsolutePath()+"/encryptedApp.dat";
        String assetsPath = "encryptedApp.dat";
        File file = new File(sdPath);
        if(!file.exists()) {
            File sdParentFile = file.getParentFile();
            if(sdParentFile == null || !sdParentFile.exists()) {
                sdParentFile.mkdirs();
            }
            FileUtil.copyAssetToSD(mContext, assetsPath, sdPath);
        }else{
            String assetsMd5 = MD5Util.getAssetsFileMd5(mContext, assetsPath);
            String sdMd5 = MD5Util.getFileMd5(sdPath);
            if(!TextUtils.isEmpty(assetsMd5) && !assetsMd5.equalsIgnoreCase(sdMd5)){
                FileUtil.deleteFile(sdPath);
                FileUtil.copyAssetToSD(mContext, assetsPath, sdPath);
            }
        }
    }

    private File getFileDir(){
        File targetDir = mContext.getExternalFilesDir(null);
        if (targetDir == null || !targetDir.exists()) {
            targetDir = mContext.getFilesDir();
        }
        return targetDir;
    }


    public void prepareDownloadMedia(final String vid){
        DownloadBean downloadBean = mAllDownloadBeans.get(vid);
        if(downloadBean != null){

            return;
        }
        if(hasSts()){
            prepareOneMedia(vid);
        }else{
            if(mVidStsHelper.isGettingVidsts()){
                mVidSet.add(vid);
            }else{
                mVidStsHelper.getVidSts(new VidStsHelper.OnStsResultListener() {
                    @Override
                    public void onSuccess(String akid, String akSecret, String token) {
                        mAcId = akid;
                        mAkSceret = akSecret;
                        mSecurityToken = token;
                        prepareOneMedia(vid);
                        for(String v : mVidSet){
                            prepareOneMedia(v);
                        }
                    }

                    @Override
                    public void onFail(int errno) {
                        mVidSet.add(vid);
                    }
                });
            }
        }
    }

    private boolean hasSts(){
        return !TextUtils.isEmpty(mAcId) && !TextUtils.isEmpty(mAkSceret) && !TextUtils.isEmpty(mSecurityToken);
    }

    private void prepareOneMedia(final String vid){
        AliyunVidSts aliyunVidSts = new AliyunVidSts();
        aliyunVidSts.setVid(vid);
        aliyunVidSts.setAcId(mAcId);
        aliyunVidSts.setAkSceret(mAkSceret);
        aliyunVidSts.setSecurityToken(mSecurityToken);
        mDownloadManager.prepareDownloadMedia(aliyunVidSts);
        mVidSet.remove(vid);
    }














    public DownloadBean getDownloadBean(String vid){
        return mAllDownloadBeans.get(vid);
    }

    /**
     * 开始下载
     * @param vid
     * @param isDownload 是否直接下载。有些需要等待WiFi才能下载
     */
    /*public void addDownload(String vid, boolean isDownload) {
        //添加下载视频信息。准备成功，将需要下载的信息添加到DownloadManager中

        mAllDownloadBeans.put(info.getVid(), downloadVideoBean);
        if(isDownload) {
            downloadVideoBean.mStatus = DownloadBean.STATUS_DOWNLOAD;
            downloadVideoBean.mDownloadIndex = mCurDownloadIndex;
            mCurDownloadIndex++;
            //开始下载视频。下载开始，回调AliyunDownloadInfoListener中的onStart方法。失败则回调onError方法。等待则回调onWait方法。
            mDownloadManager.startDownloadMedia(info);
        }
    }*/

    /**
     * 暂停下载任务
     */
    /*public void stopDownload(AliyunDownloadMediaInfo info, boolean stopByUser) {
        mDownloadManager.stopDownloadMedia(info);
        DownloadBean downloadVideoBean = mAllDownloadBeans.get(info.getVid());
        if(downloadVideoBean != null){
            AliyunDownloadMediaInfo tempMediaInfo = downloadVideoBean.mAliyunDownloadMediaInfo;
            tempMediaInfo.setSavePath(info.getSavePath());
            tempMediaInfo.setProgress(info.getProgress());
            tempMediaInfo.setStatus(AliyunDownloadMediaInfo.Status.Stop);
            if(stopByUser) {
                downloadVideoBean.mStatus = DownloadBean.STATUS_STOP;
                //暂停，保存状态

            }
        }
    }*/




    private class MyRefreshStsCallback implements AliyunRefreshStsCallback {

        @Override
        public AliyunVidSts refreshSts(String vid, String quality, String format, String title, boolean encript) {
            //NOTE: 此回调已经在非ui线程了，所以请求网络时，不能在新线程中处理，防止出现异步操作。
            AliLogUtil.v(TAG, "---refreshSts---vid = " + vid);
            AliyunVidSts vidSts = VidStsUtil.getVidSts();
            if (vidSts == null) {
                return null;
            } else {
                vidSts.setVid(vid);
                vidSts.setQuality(quality);
                vidSts.setTitle(title);
                return vidSts;
            }
        }
    }

    private class MyDownloadInfoListener implements AliyunDownloadInfoListener {

        @Override
        public void onPrepared(List<AliyunDownloadMediaInfo> infos) {
            //可以下载的项,准备成功时回调
            AliLogUtil.v(TAG, "---onPrepared---infos = " + infos);
            for(String quality : mVideoQuality) {
                for (AliyunDownloadMediaInfo info : infos) {
                    if(quality.equalsIgnoreCase(info.getQuality())){
                        //开始下载
                        mDownloadManager.startDownloadMedia(info);
                        //把下载保存进列表
                        DownloadBean downloadBean = new DownloadBean();
                        downloadBean.mAliyunDownloadMediaInfo = info;
                        mAllDownloadBeans.put(info.getVid(), downloadBean);
                        break;
                    }
                }
            }
        }

        @Override
        public void onStart(AliyunDownloadMediaInfo info) {
            //开始下载时回调
            AliLogUtil.v(TAG, "---onStart---info = " + info);

        }

        @Override
        public void onProgress(AliyunDownloadMediaInfo info, int percent) {
            //下载项进度百分比,下载进度更新回调
            AliLogUtil.v(TAG, "---onProgress---info = " + info);

        }

        @Override
        public void onStop(AliyunDownloadMediaInfo info) {
            //下载停止时回调
            AliLogUtil.v(TAG, "---onStop---info = " + info);

        }

        @Override
        public void onCompletion(AliyunDownloadMediaInfo info) {
            //下载完成时回调
            AliLogUtil.v(TAG, "---onCompletion---info = " + info);

        }

        @Override
        public void onError(AliyunDownloadMediaInfo info, int code, String msg, String reuqestId) {
            //int code:错误码 String msg:错误信息。下载错误时回调
            AliLogUtil.v(TAG, "---onError---info = " + info);
            if(mAllDownloadBeans.get(info.getVid()) == null){
                //下载列表不存在，说明是获取准备信息出错
                mVidSet.add(info.getVid());//保存，以便下载继续获取准备信息
            }else{
                //下载出错

            }




            /*if(info != null) {
                if(code != AliyunErrorCode.ALIVC_ERR_AUTH_EXPIRED.getCode()) {
                    stopDownload(info, false);

                }

            }*/


        }

        @Override
        public void onWait(AliyunDownloadMediaInfo outMediaInfo) {
            //下载等待时回调
            AliLogUtil.v(TAG, "---onWait---outMediaInfo = " + outMediaInfo);

        }

        @Override
        public void onM3u8IndexUpdate(AliyunDownloadMediaInfo outMediaInfo, int index) {
            AliLogUtil.v(TAG, "---onM3u8IndexUpdate---outMediaInfo = " + outMediaInfo);
        }
    }




}
