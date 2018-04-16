package com.readboy.mathproblem.util;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.readboy.mathproblem.cache.PicassoWrapper;


/**
 * Created by oubin on 2017/9/8.
 */

public class ViewUtils {
    private static final String TAG = "oubin_ViewUtils";

    public static void setText(String html, TextView textView) {
        //容错设置
        if (TextUtils.isEmpty(html)) {
            Log.e(TAG, "setText: html = " + html);
            textView.setText("");
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT,
                    new PicassoWrapper.PicassoImageGetter(html, textView), null));
        } else {
            textView.setText(Html.fromHtml(html,
                    new PicassoWrapper.PicassoImageGetter(html, textView), null));
        }
    }

    public static void setText(String header, String html, TextView textView) {
        String newHtml;
        if (html.startsWith("<p>")) {
            newHtml = html.replaceFirst("<p>", "<p>" + header);
        } else if (html.startsWith("<p ")) {
            int index = html.indexOf(">");
            StringBuilder builder = new StringBuilder();
            builder.append(html.substring(0, index + 1));
            builder.append(header);
            builder.append(html.substring(index + 1, html.length()));
            newHtml = builder.toString();
        } else {
            newHtml = header + html;
        }
        setText(newHtml, textView);
    }

    public static void setTypeface(Context context, TextView textView) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "FZY4JW.TTF");
        textView.setTypeface(typeface);
    }

    public static View setSelectedPosition(int position, int resId, RecyclerView recyclerView) {
        Log.e(TAG, "setSelectedPosition: Thread  = " + Thread.currentThread().getName());
        Log.e(TAG, "setSelectedPosition() called with: position = " + position + ", resId = " + resId + ", recyclerView = " + recyclerView + "");
        int count = recyclerView.getChildCount();
        Log.e(TAG, "setSelectedPosition: count = " + count);
        for (int i = 0; i < count; i++) {
            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(i);
            if (viewHolder != null) {
                viewHolder.itemView.findViewById(resId).setSelected(false);
            } else {
                Log.e(TAG, "setSelectedPosition: viewHolder = null, position = " + position);
            }
        }

        RecyclerView.ViewHolder vh = recyclerView.findViewHolderForAdapterPosition(position);
        if (vh != null) {
            View result = vh.itemView.findViewById(resId);
            result.setSelected(true);
            return result;
        } else {
            Log.e(TAG, "setSelectedPosition: vh = null");
            return null;
        }
    }

    /**
     * @return itemView
     */
    public static View setSelectedPosition(int position, RecyclerView recyclerView, int... resIds) {
        int count = recyclerView.getChildCount();
        Log.e(TAG, "setSelectedPosition: count = " + count);
        for (int i = 0; i < count; i++) {
            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(i);
            if (viewHolder != null) {
                for (int resId : resIds) {
                    viewHolder.itemView.findViewById(resId).setSelected(false);
                }
            } else {
                Log.e(TAG, "setSelectedPosition: viewHolder = null, position = " + position);
            }
        }

        RecyclerView.ViewHolder vh = recyclerView.findViewHolderForAdapterPosition(position);
        if (vh != null) {
            for (int resId : resIds) {
                View result = vh.itemView.findViewById(resId);
                result.setSelected(true);
            }
            return vh.itemView;
        } else {
            Log.e(TAG, "setSelectedPosition: vh = null");
            return null;
        }

    }

}
