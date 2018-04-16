package com.readboy.mathproblem.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.readboy.mathproblem.R;
import com.readboy.mathproblem.note.DraftPaperView;

import java.util.ArrayList;
import java.util.List;

public class PaletteActivity extends Activity {

    private DraftPaperView mDraftPaperView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity_palette);

        mDraftPaperView = (DraftPaperView) findViewById(R.id.draft_paper);
    }

    public void openDraftPaper(View view) {
        mDraftPaperView.setVisibility(View.VISIBLE);
    }

    private List<String> get() {
        return new ArrayList<>();
    }

}
