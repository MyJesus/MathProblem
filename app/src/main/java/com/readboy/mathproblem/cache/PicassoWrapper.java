package com.readboy.mathproblem.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.readboy.mathproblem.R;
import com.readboy.mathproblem.application.MathApplication;
import com.readboy.mathproblem.http.HttpConfig;
import com.readboy.mathproblem.http.OkHttp3Downloader;
import com.readboy.mathproblem.util.ViewUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;
import com.squareup.picasso.Target;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.OkHttpClient;

/**
 * Created by oubin on 2017/9/30.
 * Picasso的二次封装，方便控制，设置Picasso。
 * 获取缓存LruCache.
 * 用户缓存，加载，获取Html文本数据里图片。
 * TODO, 使用Picasso加载图片的内存优化实践, http://blog.csdn.net/ashqal/article/details/48005833
 */
public class PicassoWrapper {
    private static final String TAG = "PicassoWrapper";
    private static final int KEY_PADDING = 50;
    private static final char KEY_SEPARATOR = '\n';

    //静态，并且有引用context
    private static Picasso mPicasso;
    private static LruCache mLruCache;
    private static Map<String, Target> mTargetMap = new ConcurrentHashMap<>();

    /**
     * 初始化图片缓存框架，必须在Application.onCreate中初始化
     */
    public static void initPicasso(Context context) {
        OkHttpClient client = new OkHttpClient.Builder()
                .build();
        mLruCache = new LruCache(context);
        ThumbnailRequestHandler handler = new ThumbnailRequestHandler();
        Picasso.Builder builder = new Picasso.Builder(context)
                .downloader(new OkHttp3Downloader(client))
                .memoryCache(mLruCache)
                .addRequestHandler(handler);

        mPicasso = builder.build();
        Picasso.setSingletonInstance(mPicasso);
    }

    private static void addTarget(String source, Target target) {
        mTargetMap.put(source, target);
    }

    /**
     * 只适用通过Url方式加载图片，
     * TODO 获取LurCache的key, 该方式未必可靠。
     */
    private static String createKey(String url) {
        StringBuilder builder = new StringBuilder();
        if (!TextUtils.isEmpty(url)) {
            builder.ensureCapacity(url.length() + KEY_PADDING);
            builder.append(url);
        }
        builder.append(KEY_SEPARATOR);

        return builder.toString();
    }

    /**
     * 模仿{@link com.squareup.picasso.Utils#createKey(Request, StringBuilder)}
     *
     * @return 返回的key和mLruCache存的key对应。
     */
    private static String createKey(Request data, StringBuilder builder) {
        if (data.stableKey != null) {
            builder.ensureCapacity(data.stableKey.length() + KEY_PADDING);
            builder.append(data.stableKey);
        } else if (data.uri != null) {
            String path = data.uri.toString();
            builder.ensureCapacity(path.length() + KEY_PADDING);
            builder.append(path);
        } else {
            builder.ensureCapacity(KEY_PADDING);
            builder.append(data.resourceId);
        }
        builder.append(KEY_SEPARATOR);

        if (data.rotationDegrees != 0) {
            builder.append("rotation:").append(data.rotationDegrees);
            if (data.hasRotationPivot) {
                builder.append('@').append(data.rotationPivotX).append('x').append(data.rotationPivotY);
            }
            builder.append(KEY_SEPARATOR);
        }
        if (data.hasSize()) {
            builder.append("resize:").append(data.targetWidth).append('x').append(data.targetHeight);
            builder.append(KEY_SEPARATOR);
        }
        if (data.centerCrop) {
            builder.append("centerCrop").append(KEY_SEPARATOR);
        } else if (data.centerInside) {
            builder.append("centerInside").append(KEY_SEPARATOR);
        }

        if (data.transformations != null) {
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = data.transformations.size(); i < count; i++) {
                builder.append(data.transformations.get(i).key());
                builder.append(KEY_SEPARATOR);
            }
        }

        return builder.toString();
    }

    public static void loadThumbnailWithPath(String videoPath, ImageView imageView) {
        loadThumbnail(videoPath, imageView, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {

            }
        });
    }

    /**
     * @param videoPath 文件完全路径，需要添加ThumbnailRequestHandler.SCHEME_VIDEO.
     * @param imageView 显示图片的View
     */
    public static void loadThumbnail(String videoPath, ImageView imageView, Callback callback) {
        if (TextUtils.isEmpty(videoPath)){
            Log.e(TAG, "loadThumbnail: path = null.");
            return;
        }
        if (mPicasso == null){
            Log.e(TAG, "loadThumbnail: mPicasso == null.");
            return;
        }
        ViewGroup.LayoutParams lp = imageView.getLayoutParams();
        // TODO 如何设置resize，可靠，安全，可复用，默认值，判断等。
        mPicasso.load(ThumbnailRequestHandler.SCHEME_VIDEO + ":" + videoPath)
                .resize(lp.width, lp.height)
                .placeholder(R.drawable.video_thumbnail)
                .centerCrop()
                .into(imageView, callback);
    }

    /**
     * 通过网络获取缩略图。
     *
     * @param url 缩略图url
     */
    public static void loadThumbnail(String url, ImageView imageView) {
        if (TextUtils.isEmpty(url)){
            return;
        }
        ViewGroup.LayoutParams lp = imageView.getLayoutParams();
        mPicasso.load(url)
                .resize(lp.width, lp.height)
                .placeholder(R.drawable.video_thumbnail)
                .centerCrop()
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                    }
                });

    }

    private int getImageViewWidth() {
        return 0;
    }

    /**
     * 加载Html文本里的图片
     */
    public static class PicassoImageGetter implements Html.ImageGetter {

        private String htmlString;
        private TextView textView;

        public PicassoImageGetter(String html, TextView textView) {
            this.htmlString = html;
            this.textView = textView;
        }

        @Override
        public Drawable getDrawable(String source) {
            String url = HttpConfig.RESOURCE_HOST + source;
//            Log.e(TAG, "getDrawable: source = " + source);
            //缓存策略：memoryPolicy
            // memoryPolicy的NO_CACHE是指图片加载时放弃在内存缓存中查找，NO_STORE是指图片加载完不缓存在内存中。
            //加载策略：networkPolicy
            try {
                //valueAt() - 该方法也是一个异步线程,不过加载完成后会返回一个Bitmap,但是需要注意,该方法不能在主线程中调用,因为会造成线程阻塞;
                Bitmap bitmap = mLruCache.get(createKey(url));
                if (bitmap == null) {
                    if (mTargetMap.get(source) == null) {
                        ImageGetterTarget target = new ImageGetterTarget(htmlString, textView);
                        addTarget(source, target);
                        Picasso.with(MathApplication.getInstance())
                                .load(url)
                                .into(target);
                    }
                } else {
                    BitmapDrawable result = new BitmapDrawable(textView.getResources(), bitmap);
                    result.setTargetDensity(bitmap.getDensity());
                    //一定要创建边界，要不图片无法显示，因为图片宽高为0，0。
                    //原图太小，适当放大。
                    result.setBounds(0, 0,
                            (int) (bitmap.getWidth() * 2.3F), (int) (bitmap.getHeight() * 2.3F));
                    return result;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "getDrawable: e = " + e.toString(), e);
            }

            return null;
        }
    }

    /**
     * 异步加载Html文本里的图片，加载完重新setText.
     */
    private static class ImageGetterTarget implements Target {
        private String htmlString;
        private TextView textView;

        ImageGetterTarget(String html, TextView textView) {
            this.htmlString = html;
            this.textView = textView;
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            //不可通过getText获取TextView文本，因为已经被转码。
            ViewUtils.setText(htmlString, textView);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    }
}
