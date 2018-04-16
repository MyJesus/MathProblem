package com.readboy.mathproblem.video.search;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class StorageEventReceiver {
    private static final String TAG = "StorageEventReceiver";

    private static final String RB_ACTION_FILE_CHANGE = "com.readboy.fileexplore.FILE_CHANGE";
    private static final String RB_ACTION_MEDIA_UNSHARED = "android.intent.action.MEDIA_UNSHARED";

    private static final int MSG_FILECHANGED = 0x01;
    private static final int MSG_MOUNTED = 0x02;
    private static final int MSG_UNMOUNTED = 0x03;

    List<StorageEventListener> mListeners;
    Handler mHandler;
    InnerReceiver mReceiver;

    public StorageEventReceiver() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_FILECHANGED:
                        performFileChanged();
                        break;
                    case MSG_MOUNTED:
                        performMounted((String) msg.obj);
                        break;
                    case MSG_UNMOUNTED:
                        performUnmounted((String) msg.obj);
                        break;
                    default:
                        break;
                }
            }
        };
        mReceiver = new InnerReceiver();
    }

    class InnerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Uri data = intent.getData();
            Log.e(TAG, "action=" + action + ", data=" + data);

            switch (action) {
                case RB_ACTION_FILE_CHANGE:
//				performFileChanged();
                    notifyFileChanged();
                    break;
                case Intent.ACTION_MEDIA_MOUNTED:
                    //performMounted(data.getPath());
                    notifyMounted(data.getPath());
                    break;
                case Intent.ACTION_MEDIA_UNMOUNTED:
                case Intent.ACTION_MEDIA_EJECT:
//              case Intent.ACTION_MEDIA_REMOVED:
//                    performUnmounted(data.getPath());
                    notifyUnmounted(data.getPath());
                    break;
                case Intent.ACTION_MEDIA_SHARED:
                    break;
                default:
                    break;
            }
        }
    }

    protected void onFileChanged() {
    }

    protected void onMounted(String path) {
    }

    protected void onUnmounted(String path) {
    }


    private void notifyFileChanged() {
        if (mHandler != null) {
            mHandler.obtainMessage(MSG_FILECHANGED).sendToTarget();
        }
    }

    private void notifyMounted(String path) {
        if (mHandler != null) {
            mHandler.obtainMessage(MSG_MOUNTED, path).sendToTarget();
        }
    }

    private void notifyUnmounted(String path) {
        if (mHandler != null) {
            mHandler.obtainMessage(MSG_UNMOUNTED, path).sendToTarget();
        }
    }

    private void performFileChanged() {
        if (mListeners != null) {
            for (StorageEventListener l : mListeners) {
                l.onFileChanged();
            }
        }
        onFileChanged();
    }

    private void performMounted(String path) {
        if (mListeners != null) {
            for (StorageEventListener l : mListeners) {
                l.onMounted(path);
            }
        }
        onMounted(path);
    }

    private void performUnmounted(String path) {
        if (mListeners != null) {
            for (StorageEventListener l : mListeners) {
                l.onUnmounted(path);
            }
        }
        onUnmounted(path);
    }

    public void addStorageEventListener(StorageEventListener listener) {
        if (listener != null) {
            if (mListeners == null) {
                mListeners = new ArrayList<StorageEventListener>();
            }
            if (!mListeners.contains(listener)) {
                mListeners.add(listener);
            }
        }
    }

    public void removeStorageEventListener(StorageEventListener listener) {
        if (mListeners == null) {
            return;
        }
        if (listener == null) {
            mListeners.clear();
        } else {
            mListeners.remove(listener);
        }
    }

    public void register(Context ctx) {
        IntentFilter filter = new IntentFilter();
        filter.addDataScheme("file");
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_EJECT);
        filter.addAction(Intent.ACTION_MEDIA_SHARED);
        filter.addAction(RB_ACTION_MEDIA_UNSHARED);
        ctx.registerReceiver(mReceiver, filter);
        filter = new IntentFilter();
        filter.addAction(RB_ACTION_FILE_CHANGE);
        ctx.registerReceiver(mReceiver, filter);
        Log.e(TAG, "StorageEventReceiver registered");
    }

    public void unregister(Context ctx) {
        try {
            ctx.unregisterReceiver(mReceiver);
            Log.e(TAG, "StorageEventReceiver unregistered");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
