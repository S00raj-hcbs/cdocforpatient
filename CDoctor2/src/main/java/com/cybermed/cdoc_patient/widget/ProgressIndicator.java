package com.cybermed.cdoc_patient.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.databinding.ProgressIndicatorBinding;

import java.util.ArrayList;

public class ProgressIndicator extends LinearLayout {

    private final ArrayList<Drawable> icons;
    private int progress;
    private final Context context;
    private final TypedArray attributes;
    private ProgressAdapter adapter;
    ProgressIndicatorBinding binding;

    public ProgressIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        icons = new ArrayList<>();
        progress = 0;
        inflate(context, R.layout.progress_indicator, this);
        attributes = context.obtainStyledAttributes(attrs, R.styleable.ProgressIndicator);
        loadIcons(attributes);
        updateRecyclerView();
    }

    public void setProgress(int value) {
        progress = value;
        loadIcons(attributes);
        adapter.notifyDataSetChanged();
    }

    public int getProgress() {
        return progress;
    }

    private void updateRecyclerView() {
        adapter = new ProgressAdapter(icons);
        adapter.notifyDataSetChanged();
        RecyclerView progressBar = findViewById(R.id.progress_bar);
        progressBar.setHasFixedSize(true);
        progressBar.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        progressBar.setAdapter(adapter);
    }

    private void loadIcons(TypedArray attributes) {
        int max = attributes.getInt(R.styleable.ProgressIndicator_max, 4);
        if (progress >= max)
            return;
        icons.clear();
        for (int i = 0; i < progress; i++) {
            icons.add(attributes.getDrawable(R.styleable.ProgressIndicator_processedIcon));
            icons.add(attributes.getDrawable(R.styleable.ProgressIndicator_defaultProgressLine));
        }
        icons.add(attributes.getDrawable(R.styleable.ProgressIndicator_progressIcon));
        for (int i = progress + 1; i < max; i++) {
            icons.add(attributes.getDrawable(R.styleable.ProgressIndicator_defaultProgressLine));
            icons.add(attributes.getDrawable(R.styleable.ProgressIndicator_defaultIcon));
        }
    }
}

