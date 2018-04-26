package com.readboy.mathproblem.test;

import android.app.Activity;
import android.app.Dialog;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.PagerSnapHelper2;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import com.readboy.mathproblem.NativeApi;
import com.readboy.mathproblem.R;
import com.readboy.mathproblem.aliplayer.AliyunPlayerActivity;
import com.readboy.mathproblem.application.Constants;
import com.readboy.mathproblem.application.SubjectType;
import com.readboy.mathproblem.dialog.CommonDialog;
import com.readboy.mathproblem.exercise.PagerLayoutManager;
import com.readboy.mathproblem.http.HttpEngine;
import com.readboy.mathproblem.http.HttpRequestImpl;
import com.readboy.mathproblem.http.request.RequestParams;
import com.readboy.mathproblem.http.response.ProjectEntity;
import com.readboy.mathproblem.http.rxjava.GetProjectService;
import com.readboy.mathproblem.http.service.ProjectTestService;
import com.readboy.mathproblem.video.movie.VideoExtraNames;
import com.readboy.recyclerview.CommonAdapter;
import com.readboy.recyclerview.MultiItemTypeAdapter;
import com.readboy.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by oubin on 2017/9/5.
 */

public class TestActivity extends Activity {
    private static final String TAG = "TestActivity";

    private HttpRequestImpl mHttpRequest;

    private boolean isSelected;

    private RecyclerView recyclerView;
    private List<String> list = new ArrayList<>();
    private int[] colors = new int[]{Color.BLUE, Color.RED, Color.YELLOW, Color.CYAN, Color.GREEN};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        hideNavigationBar();
        setContentView(R.layout.activity_test);

//        seekBarTest();

//        ExampleLinearLayout exampleLinearLayout = (ExampleLinearLayout) findViewById(R.id.example_parent);
//        exampleLinearLayout.addUnion(new Union("title1", "content1"));
//        exampleLinearLayout.addUnion(new Union("title2", "content2"));

//        openDialog(null);

//        recyclerViewTest();
//        HashSet<String> hashSet = new HashSet<>();
//
//        LinkedList<String> linkedList = new LinkedList<>();
//        Map<String, String> m = new LinkedHashMap<>();
//
//        Vector<String> vector = new Vector<>();
//        ArrayMap<Integer, String> arrayMap = new ArrayMap<>();
//        HashMap<Integer, String> hashMap = new HashMap<>();
//        SparseArray<String> sparseArray = new SparseArray<>();
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);

    }

    public void play(View view) {
        ArrayList<String> paths = new ArrayList<>();
//        paths.add(Constants.URL.VIDEO1);
//        paths.add(Constants.URL.VIDEO2);
        paths.add(Constants.URL.VIDEO3);
        paths.add(Constants.URL.VIDEO4);
        paths.add(Constants.URL.VIDEO5);

        Intent intent = new Intent(this, AliyunPlayerActivity.class);
        intent.putStringArrayListExtra(VideoExtraNames.EXTRA_MEDIA_LIST, paths);
        startActivity(intent);
    }

    private void recyclerViewTest() {
        for (int i = 0; i < 5; i++) {
            list.add("Text" + i);
        }
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        PagerLayoutManager layoutManager = new PagerLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//        LinearLayoutManager layoutManager =
//                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        PagerSnapHelper snapHelper = new PagerSnapHelper();
//        snapHelper.attachToRecyclerView(recyclerView);
        LinearSnapHelper snapHelper2 = new LinearSnapHelper();
//        snapHelper.attachToRecyclerView(recyclerView);
        PagerSnapHelper2 snapHelper3 = new PagerSnapHelper2();
        snapHelper3.attachToRecyclerView(recyclerView);
        CommonAdapter<String> adapter = new CommonAdapter<String>(this, R.layout.item_text_view, list) {
            @Override
            protected void convert(ViewHolder holder, String s, int position) {
                holder.setText(R.id.text_view, s);
                holder.itemView.findViewById(R.id.text_view).setBackgroundColor(colors[position]);
            }
        };
        adapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, Object o, int position) {
                CommonDialog dialog = new CommonDialog(TestActivity.this);
                dialog.show();
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, Object o, int position) {
                return false;
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.e(TAG, "onScrollStateChanged: newState = " + newState);
            }
        });

    }

    private void rxjavaTest() {
        RequestParams params = new RequestParams("1");
        Observable<ProjectEntity> projectObservable
                = HttpEngine.getInstance().create(GetProjectService.class)
                .getProjects(SubjectType.guide.name(), params.getMap());
    }

    private void seekBarTest() {
        SeekBar seekBar = (SeekBar) findViewById(R.id.player_seek_bar);
        if (seekBar == null) {
            Log.e(TAG, "seekBarTest: seekbar == null");
            return;
        }
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void openDialog(View view) {
//        CustomProgressDialog dialog = new CustomProgressDialog(this);
//        dialog.show();
//        dialog.setCancelClickListener(v -> dialog.dismiss());
//        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialog) {
//                hideNavigationBar();
//            }
//        });


    }

    private void hideNavigationBar() {
        Log.e(TAG, "hideNavigationBar: before system ui visibility = " + getWindow().getDecorView().getSystemUiVisibility());
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_IMMERSIVE
//                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                | 0x00002000);
        getWindow().getDecorView().setSystemUiVisibility(14086);
//        getWindow().setFlags(0x02000000, 0x02000000); //隐藏系统条
        Log.e(TAG, "hideNavigationBar:after system ui visibility = " + getWindow().getDecorView().getSystemUiVisibility());
    }

    private void getProjectTest() {
        ProjectTestService service = HttpEngine.getInstance().create(ProjectTestService.class);
        RequestParams params = new RequestParams("1");

        Call<ProjectEntity> call = service.guide(params.getMap());
        call.enqueue(new Callback<ProjectEntity>() {
            @Override
            public void onResponse(Call<ProjectEntity> call, Response<ProjectEntity> response) {
                ProjectEntity enity = response.body();
                Request request = call.request();
                Request.Builder builder = request.newBuilder();
                RequestBody body = request.body();
                HttpUrl httpUrl = request.url();
                List<String> segments = httpUrl.pathSegments();
            }

            @Override
            public void onFailure(Call<ProjectEntity> call, Throwable t) {

            }
        });

    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.e(TAG, "onAttachedToWindow: ");
    }

    private void printNativeString() {
        Log.e(TAG, "printNativeString: currentTime = " + (System.currentTimeMillis() / 1000));
        Log.e(TAG, "printNativeString: signature = "
                + NativeApi.getSignature(String.valueOf(System.currentTimeMillis() / 1000)));
    }

    public void onSelected(View view) {
        if (view.isSelected()) {
            view.setSelected(false);
        } else {
            view.setSelected(true);
        }
    }

    public void showDialog(View view) {
        Dialog dialog = new Dialog(TestActivity.this);
        dialog.setContentView(R.layout.dialog_common);
        isValidContext(dialog.getContext());
        if (isFinishing()) {
            Log.e(TAG, "showDialog: not finishing ");
        }
        dialog.show();
    }


    private boolean isValidContext(Context c) {

        if (c instanceof Activity) {
            Log.e(TAG, "isValidContext: activity");
            Activity a = (Activity) c;
            if (a.isDestroyed() || a.isFinishing()) {
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }


}
