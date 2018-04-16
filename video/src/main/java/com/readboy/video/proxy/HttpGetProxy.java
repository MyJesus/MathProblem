package com.readboy.video.proxy;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.readboy.proxy.MediaEncrypt;
import com.readboy.video.proxy.Config.ProxyRequest;
import com.readboy.video.proxy.Config.ProxyResponse;

/**
 * @author dhm?
 *         TODO:使用线程池 ScheduledExecutorService
 */
public class HttpGetProxy {
    final static public String TAG = "HttpGetProxy";

    private static final int MAX_RETRY_TIME = 5;

    public interface OnErrorHttpStatusCodeListener {
        void onErrorCode(int httpStatusCode);
    }

    private static final ThreadFactory THREAD_FACTORY = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "HttpGetProxy #" + mCount.getAndIncrement());
        }
    };
    private ExecutorService mExecutorService;

    private String remoteHost;
    private int localPort;
    private String localHost;
    private SocketAddress mRemoteServer = null;
    private ServerSocket mLocalServer = null;
    //private Socket mRemoteSocket = null;
    //private Socket mPlayerSocket = null;

    private String mCacheDirPath = null;
    private String mCacheFileName = null;
    private String mMediaUrl = null;
    private boolean mIsOlineVideo = false;
    private boolean mIsEncrypt = false;
    private boolean mCacheEnable = false;
    private boolean mIsThreadAvailable = false;
    private int mCurrentRrtryTime = 0;

    private MediaEncrypt mEncrypt = null;

    private Proxy mProxy1 = null;
    private Proxy mProxy2 = null;

    private OnErrorHttpStatusCodeListener mErrorHttpStatusCodeListener = null;

    public HttpGetProxy(Context context, String fileName, OnErrorHttpStatusCodeListener listener) {
        this(context, fileName, null, null, false, listener);
        mIsEncrypt = true;
    }

    public HttpGetProxy(Context context, String url, String cacheDir, String cacheName, boolean isOnline, OnErrorHttpStatusCodeListener listener) {
        Log.e(TAG, "HttpGetProxy() called with: context = " + context + ", url = " + url + ", cacheDir = " + cacheDir + ", cacheName = " + cacheName + ", isOnline = " + isOnline + ", listener = " + listener + "");
        try {
            mIsOlineVideo = isOnline;
            mMediaUrl = url;
            mCacheDirPath = cacheDir;
            mErrorHttpStatusCodeListener = listener;
            if (mCacheDirPath != null && !mCacheDirPath.endsWith("/")) {
                mCacheDirPath = mCacheDirPath + "/";
            }
            mCacheFileName = cacheName;
            mEncrypt = new MediaEncrypt(context);

            mExecutorService = newExecutor();

            Log.i(TAG, "HttpGetProxy: ......000...........");
            //initialize proxy server
            localHost = Config.LOCAL_IP_ADDRESS;
            mLocalServer = new ServerSocket(0, 1, InetAddress.getByName(Config.LOCAL_IP_ADDRESS));
            localPort = mLocalServer.getLocalPort();

            Log.i(TAG, "HttpGetProxy: ......111..........." + localPort);
            Log.i(TAG, "HttpGetProxy: ......111..........." + mLocalServer.getLocalSocketAddress());
//			Log.i(TAG, "HttpGetProxy: ......111...........");
            //initialize remote server
            Log.i(TAG, "HttpGetProxy: ......mMediaUrl..........." + mMediaUrl);
            if (mIsOlineVideo) {
                mMediaUrl = mMediaUrl.replace(" ", "%20");
                Log.i(TAG, "HttpGetProxy: ......mMediaUrl...." + mMediaUrl);
                URI originalURI = URI.create(mMediaUrl);
                Log.i(TAG, "HttpGetProxy: ......originalURI..........." + originalURI.getHost());
                Log.i(TAG, "HttpGetProxy: ......originalURI..........." + originalURI.getPort());
                remoteHost = originalURI.getHost();
                if (originalURI.getPort() != -1) {
                    mRemoteServer = new InetSocketAddress(remoteHost, originalURI.getPort());
                } else {
                    //可能有耗时操作，10s，20s
                    mRemoteServer = new InetSocketAddress(remoteHost, Config.HTTP_PORT);
                }
            }
            Log.i(TAG, "HttpGetProxy: ......222...........");

            //start proxy server
            mIsThreadAvailable = true;
            startProxy();
        } catch (Exception e) {
            mIsThreadAvailable = false;
            mExecutorService.shutdown();
        }
    }

    private ExecutorService newExecutor(){
        return new ThreadPoolExecutor(3, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(), THREAD_FACTORY);
    }

    private void fireErrorStatusCode(int errorStatus){
        if (mErrorHttpStatusCodeListener != null){
            mErrorHttpStatusCodeListener.onErrorCode(errorStatus);
        }
    }

    public long getFileSize() {
        long fileSize = 0;
        if (mProxy1 != null) {
            fileSize = mProxy1.videoSize;
        }
        if (fileSize <= 0) {
            if (mProxy2 != null) {
                fileSize = mProxy2.videoSize;
            }
        }
        return fileSize;
    }

    private boolean checkCacheEnable(long cacheSize) {
        if (mCacheDirPath == null || mCacheFileName == null) {
            mCacheEnable = false;
            return mCacheEnable;
        }

        File dir = new File(mCacheDirPath);
        mCacheEnable = dir.exists();
        if (!mCacheEnable) {
            dir.mkdirs();
        }

        long freeSize = Utils.getAvailableSize(mCacheDirPath);
        mCacheEnable = (freeSize > cacheSize);

        return mCacheEnable;
    }

    public String getLocalURL() {
        return "http://" + localHost + ":" + localPort;
    }

    private void startProxy() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (mIsThreadAvailable) {
                    if (mLocalServer != null) {
                        Log.i(TAG, "......ready to start1 thread = " + Thread.currentThread().getId());
                        try {
                            Socket socket = mLocalServer.accept();
                            if (mProxy1 != null) {
                                mProxy1.closeSocket();
                            }
                            mProxy1 = new Proxy(socket);
                            Log.i(TAG, "......started1..........., "
//                                    + "HttpGetProxy hashCode = " + hashCode()
                            );
                            if (mIsOlineVideo) {
                                mProxy1.playOnlineMedia();
                            } else {
                                mProxy1.playLocalMedia();
                            }
                        } catch (IOException e) {
                            Log.i(TAG, "...... startProxy started1 catch ...........");
                            e.printStackTrace();
                            if (mProxy1 != null) {
                                mProxy1.closeSocket();
                            }
                            retry();
                        } catch (NullPointerException e) {
                            Log.e(TAG, "startProxy catch: e = " + e.toString(), e);
                        }
                    } else {
                        mIsThreadAvailable = false;
                    }
                }
            }
        };
//        new Thread(runnable).start();
//        mExecutorService.execute(runnable);
        if (mExecutorService.isShutdown()){
            mExecutorService = newExecutor();
        }
        mExecutorService.submit(runnable);
    }

    public void stopProxy() {
        Log.e(TAG, "stopProxy: ");
        mIsThreadAvailable = false;
        try {
            if (mLocalServer != null) {
                mLocalServer.close();
                mLocalServer = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "stopProxy: e = " + e.toString(), e);
        }

        if (mProxy1 != null) {
            mProxy1.closeSocket();
        }
        if (mProxy2 != null) {
            mProxy2.closeSocket();
        }

        if (mExecutorService != null){
            mExecutorService.shutdown();
        }
    }

    /**
     * 发生异常（如：java.net.UnknownHostException），重连，重发。
     * 最大重连次数{@link #MAX_RETRY_TIME}
     */
    private void retry() {
        if (mCurrentRrtryTime > MAX_RETRY_TIME) {
            stopProxy();
        } else {
            mCurrentRrtryTime++;
        }
    }

    private class Proxy {
        String TAG = "FirstProxy";
        private boolean mIsSaveDecryptFile = false;

        private Socket mRemoteSocket = null;
        private Socket mPlayerSocket = null;

        private FileInputStream fis = null;
        private FileOutputStream fos = null;
        private FileOutputStream fos_decrypt = null;

        private HttpParser mHttpParser = new HttpParser();
        private ProxyResponse mResponse = null;
        boolean interrupt = true;
        private long position = 0;
        private long videoSize = 0;
        private long cacheSize = 0;
        private long lastCacheSize = 0;
        private static final int CACHENUM = 100;

        public Proxy(Socket socket) {
            mPlayerSocket = socket;
            cacheSize = 0;
            lastCacheSize = 0;
        }

        private long getFileDecryptOutputStream() throws IOException {
            if (!mCacheEnable && !mIsSaveDecryptFile) {
                fos_decrypt = null;
                return 0;
            }
            if (fos_decrypt != null) {
                fos_decrypt.flush();
                fos_decrypt.close();
            }

            String fileName = "" + mCacheFileName + "_decrypt" + ".dat";
            File file = new File(mCacheDirPath + fileName);
            if (file != null) {
                fos_decrypt = new FileOutputStream(mCacheDirPath + fileName, false);
            }

            return file.length();
        }

        /**
         * play online media
         */
        public void playOnlineMedia() {
            int bytes_read;
            byte[] buffer = new byte[1024];
            byte[] header_buffer = new byte[4096];

            interrupt = false;
            try {
                Log.i(TAG, "<------------------Online----------------->");
                Log.e(TAG, "playOnlineMedia: hashCode = " + hashCode());
                //get start 2048bytes to check encrypt
                String requestStr = mHttpParser.makeProxyRequest(mMediaUrl, 0, 2047);
                Log.i(TAG, requestStr);
                sentToServer(requestStr);
                int header_len = 0;
                while (mRemoteSocket != null) {
                    bytes_read = mRemoteSocket.getInputStream().read(buffer);
                    Log.i(TAG, "playOnlineMedia: -----222------");
                    Log.e(TAG, "playOnlineMedia: bytes_read=" + bytes_read);
                    if (bytes_read != -1) {
//						Log.e(TAG, new String(buffer, 0, bytes_read));
                        System.arraycopy(buffer, 0, header_buffer, header_len, bytes_read);
                        header_len += bytes_read;
                    } else {
                        break;
                    }
                }
                Log.e(TAG, "playOnlineMedia: read response over! ");
                Log.e(TAG, "playOnlineMedia: response:" + new String(header_buffer));
                if (mErrorHttpStatusCodeListener != null) {
                    int status = mHttpParser.getHTTPStatusCode(header_buffer, header_len);
                    if (status >= HttpURLConnection.HTTP_BAD_REQUEST) {
                        mErrorHttpStatusCodeListener.onErrorCode(status);
                        return;
                    }
                }
                mResponse = mHttpParser.getProxyResponse(header_buffer, header_len);
                if (mResponse == null) {
                    Log.e(TAG, "playOnlineMedia: no response, error!");
                    closeSocket();
                    return;
                }
                mIsEncrypt = mEncrypt.setHeader(mResponse._other, mResponse._duration);

                Log.e(TAG, "playOnlineMedia:  mIsEncrypt: " + mIsEncrypt);

                //get mediaplayer input
                Log.i(TAG, "playOnlineMedia: -----000------");
                bytes_read = mPlayerSocket.getInputStream().read(buffer);
                Log.i(TAG, "bytes_read=" + bytes_read);
                Log.i(TAG, "MediaPlayer request: " + new String(buffer, 0, bytes_read));
                position = mHttpParser.getProxyRequestRange(buffer, bytes_read);
                videoSize = mResponse._duration;
                cacheSize = videoSize / CACHENUM + 1;
                lastCacheSize = videoSize - cacheSize * (CACHENUM - 1);
                Log.i(TAG, "position=" + position);
                Log.e(TAG, "videoSize=" + videoSize);
                Log.e(TAG, "cacheSize=" + cacheSize + ", lastCacheSize=" + lastCacheSize);
                checkCacheEnable(videoSize);

                String responseStr;
                if (mIsEncrypt) {
                    responseStr = mHttpParser.makeProxyResponseString(position, mEncrypt.getRealSize());
                    position += mEncrypt.getRealStartOffset();
                } else {
                    responseStr = mHttpParser.makeProxyResponseString(position, videoSize);
                }
                Log.e(TAG, "responseStr=" + responseStr);
                sendToMP(responseStr.getBytes());

                if (/*mIsEncrypt && */mIsSaveDecryptFile) {
                    getFileDecryptOutputStream();
                }

                long bytes = 0;
                boolean delta_done = false;
                long delta_size = position % cacheSize;
                int index = (int) (position / cacheSize);
                while (!interrupt && index < CACHENUM && position < videoSize) {
                    try {
                        Log.e(TAG, "111 index=" + index);
                        if (checkIsCacheExist(index)) {
                            Log.e(TAG, "cache file is exist!");
                            if (!delta_done) {
                                bytes = readStreamFromCache(index, delta_size);
                            } else {
                                bytes = readStreamFromCache(index, 0);
                            }
                        } else {
                            Log.e(TAG, "cache file isn't exist!");
                            getFileOutputStream(index);
                            String body = mHttpParser.makeProxyRequest(
                                    mMediaUrl, index * cacheSize, (index + 1) * cacheSize - 1);
//							Log.e(TAG, "cache file isn't request body: "+body);
                            position = index * cacheSize;
                            sentToServer(body);
                            if (!delta_done) {
                                bytes = downloadCacheFromServer(index, delta_size);
                            } else {
                                bytes = downloadCacheFromServer(index, 0);
                            }
                        }
                    } catch (IOException ioe) {
                        Log.e(TAG, "playOnlineMedia: hashCode " + hashCode() + "， thread : " + Thread.currentThread().getId()
                                + " exception " + ioe.toString(), ioe);
//                        ioe.printStackTrace();
                        break;
                    }

//					Log.e(TAG, "index="+index);
//					Log.e(TAG, "bytes="+bytes);
//					Log.e(TAG, "position="+position + ", videoSize="+videoSize);
                    if (!delta_done) {
                        position += bytes - delta_size;
                    } else {
                        position += bytes;
                    }
//                    Log.e(TAG, "position=" + position + ", videoSize=" + videoSize);

                    index++;
                    delta_done = true;
                }
                closeSocket();
            } catch (Exception e) {
                Log.i(TAG, "playOnlineMedia: -----333------");
                e.printStackTrace();
                closeSocket();
                retry();
            }
        }

        private long readStreamFromCache(int index, long skip) throws IOException {
            int bytes_read = 0;
            long bytes_total = 0, decrypt_len = 0;
            byte[] remote_reply = new byte[2048];

            getFileInputStream(index);
            if (fis == null) {
                return 0;
            }

            Log.e(TAG, "index=" + index + ", skip=" + skip
//                    + ", hashCode = " + hashCode()
            );
            long skipNumber = fis.skip(skip);
            bytes_total = skip;
            while (!interrupt && ((bytes_read = fis.read(remote_reply)) != -1)) {
                //	Log.e(TAG, "bytes_read="+bytes_read);
                if (mIsEncrypt) {
                    decrypt_len = mEncrypt.decrypt(remote_reply, 0, bytes_read, bytes_total + index * cacheSize);
                    //Log.e(TAG, "cacheReadIndex="+cacheReadIndex+",decrypt_len111="+decrypt_len);
                    sendToMP(remote_reply, (int) decrypt_len);
                } else {
                    sendToMP(remote_reply, bytes_read);
                }

                bytes_total += bytes_read;
            }
            return bytes_total;
        }

        private long downloadCacheFromServer(int index, long skip) throws IOException {
            boolean bReadHeader = false;
            int bytes_read = 0;
            long bytes_total = 0;
            long decrypt_len = 0;
            byte[] temp_reply, remote_reply = new byte[1024];
            ProxyResponse result = null;

            getFileOutputStream(index);
            int debbugIdx = 0;
            boolean readOver = false;
            while (!interrupt && !readOver
                    && mRemoteSocket != null
                    && ((bytes_read = mRemoteSocket.getInputStream().read(remote_reply)) != -1)) {
                if (!bReadHeader) {
                    result = mHttpParser.getProxyResponse(remote_reply, bytes_read);
//                    Log.e(TAG, "ignore socket header remote_reply: " + new String(remote_reply));
                    if (result != null && result._body != null) {
                        bReadHeader = true;
                        temp_reply = result._other;
                        bytes_read = result._other != null ? result._other.length : 0;
//                        Log.e(TAG, "111 bytes_read=" + bytes_read);
//                        Log.e(TAG, "ignore socket header!!!");
                    } else {
                        continue;
                    }
                } else {
                    temp_reply = remote_reply;
                }

                if (bytes_total + bytes_read >= cacheSize) {
                    bytes_read = (int) (cacheSize - bytes_total);
//                    interrupt = true;
                    readOver = true;
//                    Log.e(TAG, " bytes_total: " + bytes_total + ", bytes_read: " + bytes_read + ", interrupt: " + interrupt);
                }

                if (bytes_read > 0) {
                    if (mCacheEnable && fos != null) {
                        fos.write(temp_reply, 0, bytes_read);
                    }

                    if (bytes_total + bytes_read <= skip) {

                    } else if (bytes_total >= skip) {
                        if (mIsEncrypt) {
                            decrypt_len = mEncrypt.decrypt(temp_reply, 0, bytes_read, bytes_total + index * cacheSize);
//                            Log.e(TAG, "index=" + index + ",decrypt_len111=" + decrypt_len + ", bytes_read = " + bytes_read);
                            sendToMP(temp_reply, (int) decrypt_len);
                        } else {
                            sendToMP(temp_reply, 0, bytes_read);
                        }
                    } else {
                        int offset = (int) (skip - bytes_total);
                        if (mIsEncrypt) {
                            decrypt_len = mEncrypt.decrypt(temp_reply, offset, bytes_read - offset, bytes_total + index * cacheSize + offset);
//							Log.e(TAG, "offset： "+offset+",decrypt_len222="+decrypt_len+", index*cacheSize: "+index*cacheSize);
                            sendToMP(temp_reply, offset, (int) decrypt_len);
                        } else {
                            sendToMP(temp_reply, offset, bytes_read - offset);
                        }
                    }
                }
//				if (debbugIdx > 32) {
//					debbugIdx = 0;
//					Log.e(TAG, "bytes_total: "+bytes_total+", bytes_read: "+bytes_read+", skip: "+skip);
//				} else {
//					debbugIdx++;
//				}
                bytes_total += bytes_read;
            }
            Log.e(TAG, "write over!");
            if (fos != null) {
                fos.flush();
                fos.close();
            }

//            interrupt = false;

            return bytes_total;
        }

        /**
         * play local media
         */
        public void playLocalMedia() {
            Log.e(TAG, "playLocalMedia: ");
            int bytes_read;
            int header_len = 8192;
            ProxyRequest request = null;
            byte[] buffer = new byte[2048];
            byte[] header = new byte[8192];

            interrupt = false;
            mResponse = null;
            try {
                Log.i(TAG, "<------------------Local Enc----------------->");
                bytes_read = mPlayerSocket.getInputStream().read(buffer);
                Log.i(TAG, "playLocalMedia: -----000------");
                Log.i(TAG, "MediaPlayer request : " + bytes_read);
                Log.i(TAG, new String(buffer, 0, bytes_read));
                request = mHttpParser.modifyProxyRequest(buffer, bytes_read, mMediaUrl);
                Log.i(TAG, "playLocalMedia: -----111------");
                Log.i(TAG, request._body);
                // step4 & step5
                long encryptSize = getLocalFileInputStream();
                Log.e(TAG, "encryptSize=" + encryptSize);

                if (encryptSize < 1024) {
                    //文件不存在
                    //TODO: 本地视频被删除等。
                    fireErrorStatusCode(HttpURLConnection.HTTP_NOT_FOUND);

                    closeSocket();
                    return;
                }
                byte[] info = new byte[4];
                bytes_read = fis.read(info);
                if (bytes_read < info.length) {
                    closeSocket();
                    return;
                }
                if ((info[0] == 'R' && info[1] == 'M' && info[2] == 'R' && info[3] == 'B')
                        || (info[0] == 'M' && info[1] == 'P' && info[2] == 'R' && info[3] == 'B')) {
                    fis.skip(2);
                    int externalLen;
                    boolean bHasPic = false;
                    byte[] temp = new byte[4];
                    fis.read(temp);
                    externalLen = bytes2int(temp);
                    Log.e(TAG, "externalLen000=" + externalLen);
                    temp = new byte[1];
                    fis.read(temp);
                    if (bytes2int(temp) == 1) {
                        bHasPic = true;
                    } else if (bytes2int(temp) == 0) {
                        bHasPic = false;
                    } else {
                        closeSocket();
                        return;
                    }
                    fis.skip(5);
                    if (bHasPic) {
                        int pic_length;
                        temp = new byte[4];
                        fis.read(temp);
                        pic_length = bytes2int(temp);
                        Log.e(TAG, "pic_length1=" + pic_length);
                        externalLen += pic_length;
                        fis.skip(pic_length);
                        fis.read(temp);
                        pic_length = bytes2int(temp);
                        Log.e(TAG, "pic_length2=" + pic_length);
                        externalLen += pic_length;
                    }
                    header_len = 4 + 2 + 4 + 1 + 5 + 0 + externalLen;
                    Log.e(TAG, "externalLen111=" + externalLen);
                }
                Log.e(TAG, "header_length=" + header_len);
                header = new byte[header_len];
                getLocalFileInputStream();
                bytes_read = fis.read(header);
                Log.e(TAG, "bytes_read=" + bytes_read);
                if (bytes_read < header.length) {
                    closeSocket();
                    return;
                }
//				Log.e(TAG, "header: "+new String(header));
                mEncrypt.setHeader(header, encryptSize);
                long realSize = mEncrypt.getRealSize();
                long encryptOffset = mEncrypt.getRealStartOffset();
                Log.e(TAG, "realSize=" + realSize);
                Log.e(TAG, "encryptOffset=" + encryptOffset);

                mResponse = mHttpParser.makeProxyResponse(request._rangePosition, realSize);
                Log.e(TAG, "MediaPlayer response: \n" + new String(mResponse._body));
                sendToMP(mResponse._body);

                cacheSize = encryptSize;
                long bytes = 0;
                boolean delta_done = false;
                long delta_size = mResponse._currentPosition == 0 ? 0 : mResponse._currentPosition + encryptOffset;
                Log.e(TAG, "000delta_size=" + delta_size);
                Log.e(TAG, "000cacheSize=" + cacheSize);
                while (!interrupt && mResponse._currentPosition < realSize) {
                    try {
                        Log.e(TAG, "cache file is exist!");
                        getLocalFileInputStream();
                        if (!delta_done) {
                            bytes = getStreamFromLocal(delta_size);
                        } else {
                            bytes = getStreamFromLocal(0);
                        }
                    } catch (IOException ioe) {
                        Log.e(TAG, "playLocalMedia, OH YEAH, exception! " + ioe, ioe);
                        ioe.printStackTrace();
                        break;
                    }

                    mResponse._currentPosition += bytes;
//                    Log.e(TAG, "_currentPosition="
//                            + mResponse._currentPosition + ", realSize="
//                            + realSize + ", total="
//                            + (realSize + encryptOffset));

                    delta_done = true;
                }

                closeSocket();
            } catch (Exception e) {
                e.printStackTrace();
            }

            closeSocket();
        }

        private long getStreamFromLocal(long skip) throws IOException {
            Log.e(TAG, "getStreamFromLocal() called with: skip = " + skip + "");
            int offset = 0;
            int length = 0;
            int bytes_read = 0;
            long bytes_total = 0;
            byte[] remote_reply = new byte[1024];

            if (fis == null) {
                return 0;
            }
            fis.skip(skip);
            bytes_total += skip;
            while (!interrupt && ((bytes_read = fis.read(remote_reply)) != -1)) {
                offset = 0;
                length = bytes_read;
                //数据解密
                long size = mEncrypt.decrypt(remote_reply, offset, length, (long) (bytes_total + offset));
                if (size > 0) {
                    sendToMP(remote_reply, (int) size);
                } else {
                    Log.e(TAG, "decrypt size=" + size + ", bytes_total=" + bytes_total);
                }
                bytes_total += bytes_read;
            }
            return bytes_total - skip;
        }

        private long getLocalFileInputStream() throws IOException {
            if (fis != null) {
                fis.close();
            }

            File file = new File(mMediaUrl);
            if (file.exists()) {
                fis = new FileInputStream(file);
                return file.length();
            }

            return 0;
        }

        private boolean checkIsCacheExist(int index) {
            String fileName = "" + mCacheFileName + "_" + index + ".dat";
            File file = new File(mCacheDirPath + fileName);
//            Log.e(TAG, "checkIsCacheExist: file = " + file.getAbsolutePath());
            if (file.exists()) {
                if (index < CACHENUM - 1 && file.length() == cacheSize) {
                    return true;
                } else if (index == CACHENUM - 1 && file.length() == lastCacheSize) {
                    return true;
                }
            }

            return false;
        }

        private long getFileInputStream(int index) throws IOException {
            if (fis != null) {
                fis.close();
            }

            String fileName = "" + mCacheFileName + "_" + index + ".dat";
            File file = new File(mCacheDirPath + fileName);
            if (file.exists()) {
                // Log.e(TAG, ""+file.lastModified());
                fis = new FileInputStream(file);
                return file.length();
            }

            return 0;
        }

        private long getFileOutputStream(int index) throws IOException {
            if (!mCacheEnable) {
                fos = null;
                return 0;
            }
            if (fos != null) {
                fos.flush();
                fos.close();
            }

            String fileName = "" + mCacheFileName + "_" + index + ".dat";
            File file = new File(mCacheDirPath + fileName);
            if (file != null) {
                fos = new FileOutputStream(mCacheDirPath + fileName, false);
            }

            return file.length();
        }

        private void closeSocket() {
            Log.e(TAG, "closeSocket: "
//                    + "HttpGetProxy hashCode = " + HttpGetProxy.this.hashCode()
            );
            interrupt = true;
//            try {
            if (mRemoteSocket != null) {
                try {
                    mRemoteSocket.close();
                } catch (IOException | NullPointerException e) {
                    //多线程操控，可能发生NullPointerException
                    e.printStackTrace();
                }
                mRemoteSocket = null;
            }

            if (mPlayerSocket != null) {
                try {
                    mPlayerSocket.close();
                } catch (IOException | NullPointerException e) {
                    //多线程操控，可能发生NullPointerException
                    e.printStackTrace();
                }
                mPlayerSocket = null;
            }

            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException | NullPointerException e) {
                    //多线程操控，可能发生NullPointerException
                    e.printStackTrace();
                }
                fis = null;
            }

            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException | NullPointerException e) {
                    e.printStackTrace();
                }
                fos = null;
            }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }

        public void sendToMP(byte[] bytes) throws IOException {
            sendToMP(bytes, 0, bytes.length);
        }

        public void sendToMP(byte[] bytes, int length) throws IOException {
            sendToMP(bytes, 0, length);
        }

        public void sendToMP(byte[] bytes, int offset, int length) throws IOException {
            if (bytes == null || bytes.length == 0) {
                return;
            }
            if (mPlayerSocket == null || mPlayerSocket.getOutputStream() == null) {
                Log.w(TAG, "socket has already closed!, thread = " + Thread.currentThread().getId());
                return;
            }

            mPlayerSocket.getOutputStream().write(bytes, offset, length);
            mPlayerSocket.getOutputStream().flush();
        }

        public void sentToServer(String requestStr) throws IOException {
            if (mRemoteSocket != null) {
                mRemoteSocket.close();
            }
            mRemoteSocket = new Socket();
            mRemoteSocket.connect(mRemoteServer);
            mRemoteSocket.getOutputStream().write(requestStr.getBytes());
            mRemoteSocket.getOutputStream().flush();
        }

        private int bytes2int(byte[] bytes) {
            int len = bytes.length;
            int value = 0;
            for (int i = 0; i < len; i++) {
                value |= ((bytes[i] & 0xff) << (8 * i));
            }
            return value;
        }
    }
}