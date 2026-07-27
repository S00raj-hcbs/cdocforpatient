package com.cybermed.cdoc_patient.widget;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.cybermed.cdoc_patient.R;

import java.util.ArrayList;

public class ProgressAdapter extends RecyclerView.Adapter<ProgressAdapter.MyView> {

    private final ArrayList<Drawable> icons;

    public static class MyView extends RecyclerView.ViewHolder {
        ImageView icon;

        public MyView(View view) {
            super(view);
            icon = view.findViewById(R.id.icon);
        }
    }

    public ProgressAdapter(ArrayList<Drawable> icons) {
        this.icons = icons;
    }

    @NonNull
    @Override
    public MyView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView
                = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.cdoc_progress_item,
                        parent,
                        false);


        return new MyView(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyView holder, int position) {
        holder.icon.setImageDrawable(icons.get(position));
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return icons.size();
    }
}
