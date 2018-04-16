package com.readboy.video.proxy;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URI;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.readboy.proxy.MediaEncrypt;
import com.readboy.video.db.DataLoadInfoItem;
import com.readboy.video.db.DownloadInfoProxy;
import com.readboy.video.proxy.Config.ProxyResponse;
import com.readboy.video.proxy.Config.ProxyRequest;

import android.content.Context;
import android.util.Log;

public class HttpGetProxy2 {
    final static public String TAG = "HttpGetProxy";

    public interface OnErrorHttpStatusCodeListener {
        public void onErrorCode(int httpStatusCode);
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
    private boolean mIsOnlineVideo = false;
    private boolean mIsEncrypt = false;
    private boolean mCacheEnable = false;
    private boolean mIsThreadAvailable = false;

    private MediaEncrypt mEncrypt = null;

    private Proxy mProxy1 = null;
    private Proxy mProxy2 = null;

    private OnErrorHttpStatusCodeListener mOnHttpStatusCodeListener = null;
    private List<DataLoadInfoItem> mDdLoadInfo = new Vector<>();

    public HttpGetProxy2(Context context, String fileName, OnErrorHttpStatusCodeListener listener) {
        this(context, fileName, null, null, false, listener);
        mIsEncrypt = true;
    }

    public HttpGetProxy2(Context context, String url, String cacheDir, String cacheName, boolean isOnline, OnErrorHttpStatusCodeListener listener) {
        try {
            this.mIsOnlineVideo = isOnline;
            this.mMediaUrl = url;
            this.mCacheDirPath = cacheDir;
            this.mOnHttpStatusCodeListener = listener;
            if (mCacheDirPath != null && !mCacheDirPath.endsWith("/")) {
                mCacheDirPath = mCacheDirPath + "/";
            }
            mCacheFileName = cacheName;
            mEncrypt = new MediaEncrypt(context);

            mExecutorService = new ThreadPoolExecutor(0,
                    Integer.MAX_VALUE, 90, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), THREAD_FACTORY);

            Log.i(TAG, "......000...........");
            //initialize proxy server
            localHost = Config.LOCAL_IP_ADDRESS;
            mLocalServer = new ServerSocket(0, 1, InetAddress.getLocalHost());
            localPort = mLocalServer.getLocalPort();

            Log.i(TAG, "......111..........." + localPort);
            Log.i(TAG, "......111..........." + mLocalServer.getLocalSocketAddress());
//			Log.i(TAG, "......111...........");
            //initialize remote server
            Log.i(TAG, "......mMediaUrl..........." + mMediaUrl);
            if (mIsOnlineVideo) {
                mMediaUrl = mMediaUrl.replace(" ", "%20");
//				String decodeUrl = mMediaUrl;//URLDecoder.decode(mMediaUrl, "utf-8");
//				String tempName = decodeUrl.substring(decodeUrl.lastIndexOf("/")+1, decodeUrl.length());
//				String tempNameUtf8 = URLEncoder.encode(tempName, "utf-8");
//				mMediaUrl = decodeUrl.replace(tempName, tempNameUtf8);
                Log.i(TAG, "......mMediaUrl...." + mMediaUrl);
                URI originalURI = URI.create(mMediaUrl);
                Log.i(TAG, "......originalURI..........." + originalURI.getHost());
                Log.i(TAG, "......originalURI..........." + originalURI.getPort());
                remoteHost = originalURI.getHost();
                if (originalURI.getPort() != -1) {
                    mRemoteServer = new InetSocketAddress(remoteHost, originalURI.getPort());
                } else {
                    mRemoteServer = new InetSocketAddress(remoteHost, Config.HTTP_PORT);
                }
//				mDdLoadInfo = DownloadInfoProxy.queryItems(DownloadInfoProxy.DEFAULT_DB_PATH, DownloadInfoProxy.DEFSULT_DB_NAME, mCacheFileName);
                getOnlineCache();
            }

            Log.i(TAG, "......222...........");

            //start proxy server
            mIsThreadAvailable = true;
            startProxy();
        } catch (Exception e) {
            Log.e(TAG, "HttpGetProxy: e = " + e.toString(), e);
            mIsThreadAvailable = false;
            mExecutorService.shutdown();
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

    public long getDownloadBit() {
        long downloadSize = 0;
        if (mProxy1 != null) {
            downloadSize = mProxy1.mDownloadBit;
        }
        return downloadSize;
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
                        Log.i(TAG, "......ready to start1...........");
                        try {
                            Socket socket = mLocalServer.accept();
                            if (mProxy1 != null) {
                                mProxy1.closeSocket();
                            }
                            mProxy1 = new Proxy(socket);
                            Log.i(TAG, "......started1...........");
                            if (mIsOnlineVideo) {
                                mProxy1.playOnlineMedia();
                            } else {
                                mProxy1.playLocalMedia();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.i(TAG, "...... startProxy started1 catch ...........");
                            if (mProxy1 != null) {
                                mProxy1.closeSocket();
                            }
                        }
                    } else {
                        mIsThreadAvailable = false;
                    }
                }
            }
        };
        mExecutorService.submit(runnable);
    }

    public void stopProxy() {
        mIsThreadAvailable = false;
        try {
            if (mLocalServer != null) {
                mLocalServer.close();
                mLocalServer = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        saveOnlineCache();
        if (mProxy1 != null) {
            mProxy1.closeSocket();
        }
        if (mProxy2 != null) {
            mProxy2.closeSocket();
        }
    }

    private class Proxy {
        String TAG = "FirstProxy";
        private boolean mIsSaveDecryptFile = false;

        /**
         * 客户端，用于请求视频数据，
         * InputStream读取服务器返回的视频数据
         * OutputStream用于发送请求给服务器
         */
        private Socket mRemoteSocket = null;
        /**
         * 模拟的服务器，用于解密后的数据传输给MediaPlayer
         * InputStream是MediaPlayer发出的请求报文
         * OutputStream用于输出数据给MediaPlayer.
         */
        private Socket mPlayerSocket = null;

        private FileInputStream fis = null;
        private RandomAccessFile mRaf = null;

        private HttpParser mHttpParser = new HttpParser();
        private ProxyResponse mResponse = null;
        boolean interrupt = true;
        /**
         * 解码后的视频位置，由MediaPlayer请求，和position对应。
         */
        private long mBeginBit = 0;
        private long mDownloadBit = 0;
        /**
         * 视频读取资源到达的位置, 原始视频位置。
         */
        private long position = 0;
        /**
         * 视频总大小
         */
        private long videoSize = 0;
        private long cacheSize = 0;
        private long lastCacheSize = 0;
//		private static final int CACHENUM = 10;

        public Proxy(Socket socket) {
            Log.i(TAG, "Proxy: init:" + socket.getLocalSocketAddress());
            mPlayerSocket = socket;
            cacheSize = 0;
            lastCacheSize = 0;
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
                //get start 2048bytes to check encrypt
                String requestStr = mHttpParser.makeProxyRequest(mMediaUrl, 0, 2047);
                Log.i(TAG, "request source data for init: " + requestStr);
                sentToServer(requestStr);
                int header_len = 0;
                while (mRemoteSocket != null) {
                    bytes_read = mRemoteSocket.getInputStream().read(buffer);
                    Log.i(TAG, "-----222------");
                    Log.i(TAG, "bytes_read=" + bytes_read);
                    if (bytes_read != -1) {
//						Log.e(TAG, new String(buffer, 0, bytes_read));
                        System.arraycopy(buffer, 0, header_buffer, header_len, bytes_read);
                        header_len += bytes_read;
                    } else {
                        break;
                    }
                    Log.i(TAG, "-----header_len： " + header_len);
                }
                Log.i(TAG, "read response over! ");
//				Log.i(TAG, "first response: "+new String(buffer));
                if (mOnHttpStatusCodeListener != null) {
                    int status = mHttpParser.getHTTPStatusCode(header_buffer, header_len);
                    if (status >= HttpURLConnection.HTTP_BAD_REQUEST) {
                        mOnHttpStatusCodeListener.onErrorCode(status);
                        return;
                    }
                }
                mResponse = mHttpParser.getProxyResponse(header_buffer, header_len);
                if (mResponse == null) {
                    Log.i(TAG, "no response, error!");
                    closeSocket();
                    return;
                }
//				Log.e(TAG, " header other: "+new String(mResponse._other));
                mIsEncrypt = mEncrypt.setHeader(mResponse._other, mResponse._duration);

                Log.i(TAG, " mIsEncrypt: " + mIsEncrypt);

                //get mediaplayer input
                Log.i(TAG, "-----000------");
                bytes_read = mPlayerSocket.getInputStream().read(buffer);
                Log.i(TAG, "bytes_read=" + bytes_read);
                Log.i(TAG, "mPlayerSocket mediaPlayer request:" + new String(buffer, 0, bytes_read));
                position = mHttpParser.getProxyRequestRange(buffer, bytes_read);
                videoSize = mResponse._duration;
                mBeginBit = position;
//				cacheSize = videoSize/CACHENUM+1;
//				lastCacheSize = videoSize-cacheSize*(CACHENUM-1);
                Log.i(TAG, "MediaPlayer request position=" + position + ", mResponse._currentPosition=" + mResponse._currentPosition
                        + ", videoSize=" + videoSize);
                checkCacheEnable(videoSize);

                String responseStr;
                if (mIsEncrypt) {
                    responseStr = mHttpParser.makeProxyResponseString(position, mEncrypt.getRealSize());
//                    responseStr = mHttpParser.makeProxyResponseStringAlive(position, mEncrypt.getRealSize());
                    //有MediaPlayer请求的播放位置，校准为原始数据的位置。
                    position += mEncrypt.getRealStartOffset();
                    Log.e(TAG, "playOnlineMedia: RealStartOffset position = " + position);
                } else {
                    responseStr = mHttpParser.makeProxyResponseString(position, videoSize);
                }
                Log.i(TAG, "send to MediaPlayer responseStr=" + responseStr);
                //发送响应的MediaPlayer，告知文件大小等信息。
                sendToMP(responseStr.getBytes());

                long bytes = 0;
                //position%cacheSize;
                long beginLoadIndex = mBeginBit == 0 ? 0 : position;
                Log.e(TAG, "playOnlineMedia: beginLoadIndex=" + beginLoadIndex + ", mBeginBit=" + mBeginBit
                        + ", position=" + position + ", videoSize=" + videoSize);
                while (!interrupt && beginLoadIndex < videoSize && position < videoSize) {
                    try {
                        Log.i(TAG, "111  beginLoad: " + beginLoadIndex);
                        bytes = 0;
                        long hadLoad = isCacheExist(beginLoadIndex);
                        if (hadLoad != -1) {
                            Log.i(TAG, "cache file is exist!");
                            beginLoadIndex = readStreamFromCache(beginLoadIndex, hadLoad);
                        } else {
                            Log.i(TAG, "cache file isn't exist!");
                            getRandomAccessFile();
                            if (mRaf != null) {
                                mRaf.seek(beginLoadIndex);
                            }
                            String body = mHttpParser.makeProxyRequestAlive(mMediaUrl, beginLoadIndex,
                                    getWillDownloadBit(beginLoadIndex, videoSize) - 1);
                            Log.i(TAG, "source data socket request body: " + body);
                            DataLoadInfoItem item = DataLoadInfoItem.createOrder(beginLoadIndex, mDdLoadInfo);
                            mDdLoadInfo.add(item.mId, item);
                            sentToServer(body);
                            bytes = downloadCacheFromServer(item.mId, beginLoadIndex);
                            Log.e(TAG, "playOnlineMedia: bytes = " + bytes);
                        }
                    } catch (IOException ioe) {
                        Log.i(TAG, "OH YEAH, playOnlineMedia exception ");
                        ioe.printStackTrace();
                        closeSocket();
                        break;
                    }

                    mDownloadBit += bytes;
                    beginLoadIndex += bytes;
                    Log.i(TAG, "position=" + position + ", videoSize=" + videoSize + ", mDownloadBit=" + mDownloadBit
                            + ", beginLoadIndex=" + beginLoadIndex);
                }
                closeSocket();
            } catch (Exception e) {
                Log.i(TAG, "-----333------", e);
                e.printStackTrace();
                closeSocket();
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

            Log.i(TAG, "index=" + index + ", skip=" + skip);
            fis.skip(skip);
            bytes_total = skip;
            while (!interrupt && ((bytes_read = fis.read(remote_reply)) != -1)) {
                //	Log.e(TAG, "bytes_read="+bytes_read);
                if (mIsEncrypt) {
                    decrypt_len = mEncrypt.decrypt(remote_reply, 0, bytes_read, bytes_total + mDownloadBit);
                    //Log.e(TAG, "cacheReadIndex="+cacheReadIndex+",decrypt_len111="+decrypt_len);
                    sendToMP(remote_reply, (int) decrypt_len);
                } else {
                    sendToMP(remote_reply, bytes_read);
                }

                bytes_total += bytes_read;
            }
            return bytes_total;
        }

        private long readStreamFromCache(long skip, long end) throws IOException {
            Log.i(TAG, "readStreamFromCache() called with: skip = " + skip + ", end = " + end + "");
            int bytes_read = 0;
            long bytes_total = 0, decrypt_len = 0;
            byte[] remote_reply = new byte[2048];

            getFileInputStreamRaf();
            if (fis == null) {
                return 0;
            }

            Log.i(TAG, " skip=" + skip);
            fis.skip(skip);
            bytes_total = skip;
            while (!interrupt && bytes_total < end && ((bytes_read = fis.read(remote_reply)) != -1)) {
                //	Log.e(TAG, "bytes_read="+bytes_read);
                if (mIsEncrypt) {
                    if (bytes_total + bytes_read > end) {
                        bytes_read = (int) (end - bytes_total);
                    }
                    decrypt_len = mEncrypt.decrypt(remote_reply, 0, bytes_read, bytes_total);
                    //Log.e(TAG, "cacheReadIndex="+cacheReadIndex+",decrypt_len111="+decrypt_len);
                    sendToMP(remote_reply, (int) decrypt_len);
                } else {
                    sendToMP(remote_reply, bytes_read);
                }
                bytes_total += bytes_read;
            }
            Log.i(TAG, " readStreamFromCache write over! end bytes_total: " + bytes_total);
            return bytes_total;
        }

        private long downloadCacheFromServer(int index, long skip) throws IOException {
            Log.i(TAG, "downloadCacheFromServer() called with: index = " + index + ", skip = " + skip + "");
            boolean bReadHeader = false;
            int bytes_read = 0;
            long bytes_total = 0;
            long decrypt_len = 0;
            byte[] temp_reply, remote_reply = new byte[1024];
            ProxyResponse result = null;
            long tempTotal = 0;

            int debbugIdx = 0;
            while (!interrupt
                    && mRemoteSocket != null
                    && ((bytes_read = mRemoteSocket.getInputStream().read(remote_reply)) != -1)) {
                if (!bReadHeader) {
                    Log.i(TAG, "downloadCacheFromServer: remote_reply: " + bytes_read + ", " + new String(remote_reply, 0, bytes_read));
                    result = mHttpParser.getProxyResponse(remote_reply, bytes_read);
                    if (result != null && result._body != null) {
                        bReadHeader = true;
                        temp_reply = result._other;
                        bytes_read = result._other != null ? result._other.length : 0;
                        Log.i(TAG, "111 bytes_read=" + bytes_read + ", skip: " + skip);
                        Log.i(TAG, "downloadCacheFromServer：ignore socket header!!!");
                        Log.i(TAG, "downloadCacheFromServer: bytes_total=" + bytes_total
                                + ", bytes_read=" + bytes_read + ", mDownloadBit=" + mDownloadBit);
                    } else {
                        continue;
                    }
                } else {
                    temp_reply = remote_reply;
                }
                if (bytes_total + bytes_read + mDownloadBit >= videoSize) {
                    bytes_read = (int) (videoSize - bytes_total);
                    interrupt = true;
                    Log.i(TAG, " bytes_total: " + bytes_total + ", bytes_read: " + bytes_read + ", interrupt: " + interrupt);
                }
//				Log.e(TAG, " 1111 bytes_total: "+bytes_total+", bytes_read: "+bytes_read+", interrupt: "+interrupt);
                if (bytes_read > 0) {
                    if (mCacheEnable && mRaf != null) {
                        mRaf.write(temp_reply, 0, bytes_read);
                    }
//					Log.e(TAG, " 2222 bytes_total: "+bytes_total+", bytes_read: "+bytes_read+", interrupt: "+interrupt);
                    if (mIsEncrypt) {
                        decrypt_len = mEncrypt.decrypt(temp_reply, 0, bytes_read, bytes_total + skip);
//						Log.e(TAG, "indx: "+indx+", decrypt_len111: "+decrypt_len);
                        sendToMP(temp_reply, (int) decrypt_len);
                        tempTotal += decrypt_len;
                    } else {
                        sendToMP(temp_reply, 0, bytes_read);
                    }
                }
                bytes_total += bytes_read;
                mDdLoadInfo.get(index).mEndBit = bytes_total + skip;
            }
            Log.i(TAG, "downloadCacheFromServer: write over! bytes_total = " + bytes_total + ", tempTotal = " + tempTotal);
            if (mRaf != null) {
                mRaf.close();
                mRaf = null;
            }

            interrupt = false;

            return bytes_total;
        }

        /**
         * play local media
         */
        public void playLocalMedia() {
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
                Log.i(TAG, "-----000------");
                Log.i(TAG, "bytes_read=" + bytes_read);
                Log.i(TAG, "MediaPlayer request = " + new String(buffer, 0, bytes_read));
                request = mHttpParser.modifyProxyRequest(buffer, bytes_read, mMediaUrl);
                Log.i(TAG, "-----111------");
                Log.i(TAG, request._body);
                // step4 & step5
                long encryptSize = getLocalFileInputStream();
                Log.i(TAG, "encryptSize=" + encryptSize);
                if (encryptSize < 1024) {
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
                    Log.i(TAG, "externalLen000=" + externalLen);
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
                        Log.i(TAG, "pic_length1=" + pic_length);
                        externalLen += pic_length;
                        fis.skip(pic_length);
                        fis.read(temp);
                        pic_length = bytes2int(temp);
                        Log.i(TAG, "pic_length2=" + pic_length);
                        externalLen += pic_length;
                    }
                    header_len = 4 + 2 + 4 + 1 + 5 + 0 + externalLen;
                    Log.i(TAG, "externalLen111=" + externalLen);
                }
                Log.i(TAG, "header_length=" + header_len);
                header = new byte[header_len];
                getLocalFileInputStream();
                bytes_read = fis.read(header);
                Log.i(TAG, "bytes_read=" + bytes_read);
                if (bytes_read < header.length) {
                    closeSocket();
                    return;
                }
//				Log.e(TAG, "header: "+new String(header));
                mEncrypt.setHeader(header, encryptSize);
                long realSize = mEncrypt.getRealSize();
                long encryptOffset = mEncrypt.getRealStartOffset();
                Log.i(TAG, "realSize=" + realSize);
                Log.i(TAG, "encryptOffset=" + encryptOffset);

                mResponse = mHttpParser.makeProxyResponse(request._rangePosition, realSize);
                Log.i(TAG, "<-----\n" + new String(mResponse._body));
                sendToMP(mResponse._body);

                cacheSize = encryptSize;
                long bytes = 0;
                boolean delta_done = false;
                long delta_size = mResponse._currentPosition == 0 ? 0 : mResponse._currentPosition + encryptOffset;
//				Log.e(TAG, "000delta_size=" + delta_size);
//				Log.e(TAG, "000cacheSize=" + cacheSize);
                while (mResponse._currentPosition < realSize) {
                    try {
                        Log.i(TAG, "cache file is exist!");
                        getLocalFileInputStream();
                        if (!delta_done) {
                            bytes = getStreamFromLocal(delta_size);
                        } else {
                            bytes = getStreamFromLocal(0);
                        }
                    } catch (IOException ioe) {
                        Log.i(TAG, "playLocalMedia, OH YEAH, exception!!!!");
                        ioe.printStackTrace();
                        break;
                    }

                    mResponse._currentPosition += bytes;
                    Log.i(TAG, "_currentPosition="
                            + mResponse._currentPosition + ", realSize="
                            + realSize + ", total="
                            + (realSize + encryptOffset));

                    delta_done = true;
                }

                closeSocket();
            } catch (Exception e) {
                e.printStackTrace();
            }

            closeSocket();
        }

        private long getStreamFromLocal(long skip) throws IOException {
            Log.i(TAG, "getStreamFromLocal() called with: skip = " + skip + "");
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

                long size = mEncrypt.decrypt(remote_reply, offset, length, bytes_total + offset);
                if (size > 0) {
                    sendToMP(remote_reply, (int) size);
                } else {
                    Log.i(TAG, "decrypt size=" + size + ", bytes_total=" + bytes_total);
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
            if (file != null && file.exists()) {
                fis = new FileInputStream(file);
                return file.length();
            }

            return 0;
        }

        private boolean checkIsCacheExist(int index) {
            String fileName = "" + mCacheFileName + "_" + index + ".dat";
            File file = new File(mCacheDirPath + fileName);
            if (file != null && file.exists()) {
                if (index < mDownloadBit && file.length() == videoSize) {
                    return true;
                } else if (index == mDownloadBit && file.length() == videoSize) {
                    return true;
                }
            }

            return false;
        }

        private long isCacheExist(long beginIndex) {
            long exist = -1;
            if (mDdLoadInfo.size() > 0) {
                for (DataLoadInfoItem item : mDdLoadInfo) {
                    item.printf();
                    if (item.mBeginBit <= beginIndex && item.mEndBit > beginIndex) {
                        exist = item.mEndBit;
                        break;
                    }
                }
            }
            return exist;
        }

        private long getWillDownloadBit(long beginbit, long size) {
            long tempSize = size;
            if (mDdLoadInfo.size() > 0) {
                for (DataLoadInfoItem item : mDdLoadInfo) {
                    Log.e(TAG, "getWillDownloadBit: item : " + item.toString());
                    if (item.mBeginBit > beginbit) {
                        tempSize = item.mBeginBit;
                        break;
                    }
                }
            }
            return tempSize;
        }

        private long getFileInputStream(int index) throws IOException {
            if (fis != null) {
                fis.close();
            }

            String fileName = "" + mCacheFileName + "_" + index + ".dat";
            File file = new File(mCacheDirPath + fileName);
            if (file != null && file.exists()) {
                // Log.e(TAG, ""+file.lastModified());
                fis = new FileInputStream(file);
                return file.length();
            }

            return 0;
        }

        private long getFileInputStreamRaf() throws IOException {
            if (fis != null) {
                fis.close();
            }

            String fileName = "" + mCacheFileName + "_raf.dat";
            File file = new File(mCacheDirPath + fileName);
            if (file.exists()) {
                // Log.i(TAG, ""+file.lastModified());
                fis = new FileInputStream(file);
                return file.length();
            }

            return 0;
        }

        private long getRandomAccessFile() throws IOException {
            if (!mCacheEnable) {
                mRaf = null;
                return 0;
            }
            if (mRaf != null) {
                mRaf.close();
            }

            String fileName = "" + mCacheFileName + "_raf.dat";
            File file = new File(mCacheDirPath + fileName);
            if (file.exists()) {
                file.createNewFile();
            }
            Log.i("", " getRandomAccessFile mCacheDirPath: " + mCacheDirPath + ", fileName: " + fileName);
            if (file != null) {
                mRaf = new RandomAccessFile(file, "rw");
            }
            return file.length();
        }

        private void closeSocket() {
            Log.e(TAG, "closeSocket: ");
            interrupt = true;
            try {
                if (mRemoteSocket != null) {
                    mRemoteSocket.close();
                    mRemoteSocket = null;
                }

                if (mPlayerSocket != null) {
                    mPlayerSocket.close();
                    mPlayerSocket = null;
                    Log.e(TAG, "closeSocket: close playerSocket");
                }

                if (fis != null) {
                    fis.close();
                    fis = null;
                }

                if (mRaf != null) {
                    mRaf.close();
                    mRaf = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * TODO: 三个seenToMP合并一个调用，方便维护
         */
        public void sendToMP(byte[] bytes) throws IOException {
//            if (bytes == null || bytes.length == 0) {
//                return;
//            }
//            if (mPlayerSocket == null || mPlayerSocket.getOutputStream() == null) {
//                Log.w(TAG, "socket has already closed!");
//                return;
//            }
//            //Log.w(TAG, "sendToMP bytes.length="+bytes.length);
//            mPlayerSocket.getOutputStream().write(bytes);
//            mPlayerSocket.getOutputStream().flush();
            sendToMP(bytes, 0, bytes.length);
        }

        public void sendToMP(byte[] bytes, int length) throws IOException {
//            if (bytes == null || bytes.length == 0) {
//                return;
//            }
//            if (mPlayerSocket == null || mPlayerSocket.getOutputStream() == null) {
//                Log.w(TAG, "socket has already closed!");
//                return;
//            }
////			Log.w(TAG, "sendToMP length: "+length+", bytes.length: "+bytes.length);
//            mPlayerSocket.getOutputStream().write(bytes, 0, length);
//            mPlayerSocket.getOutputStream().flush();
            sendToMP(bytes, 0, length);
        }

        public void sendToMP(byte[] bytes, int offset, int length) throws IOException {
            if (bytes == null || bytes.length == 0) {
                return;
            }
            if (mPlayerSocket == null || mPlayerSocket.getOutputStream() == null) {
                Log.w(TAG, "socket has already closed!");
                closeSocket();
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

    private void getOnlineCache() {
        String fileName = "" + mCacheFileName + "_raf.dat";
        File file = new File(mCacheDirPath + fileName);
        if (!file.exists()) {
            DownloadInfoProxy.clearItem(DownloadInfoProxy.DEFAULT_DB_PATH, DownloadInfoProxy.DEFSULT_DB_NAME, mCacheFileName);
        }
        mDdLoadInfo = DownloadInfoProxy.queryItems(DownloadInfoProxy.DEFAULT_DB_PATH, DownloadInfoProxy.DEFSULT_DB_NAME, mCacheFileName);
    }

    private void saveOnlineCache() {
        if (mIsOnlineVideo) {
            DownloadInfoProxy.updateItems(DownloadInfoProxy.DEFAULT_DB_PATH, DownloadInfoProxy.DEFSULT_DB_NAME, mCacheFileName, mDdLoadInfo);
        }
    }
}