package com.cybermed.cdoc_patient.me;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cybermed.cdoc_patient.R;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SettingsStateAdapter extends RecyclerView.Adapter<SettingsStateAdapter.StateHolder> {
    ArrayList<String> items;
    String selected;
    Context context;
    IStateCallBack iStateCallBack;

    public SettingsStateAdapter(ArrayList<String> items, Context context, IStateCallBack iStateCallBack) {
        this.items = items;
        this.context = context;
        this.iStateCallBack = iStateCallBack;
    }

    @NonNull
    @NotNull
    @Override
    public StateHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_state_items, parent, false);
        return new StateHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull StateHolder holder, int position) {
        holder.state.setText(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public String getSelected() {
        return selected;
    }

    class StateHolder extends RecyclerView.ViewHolder {
        TextView state;
        ImageView tick;

        public StateHolder(@NonNull View itemView) {
            super(itemView);
            state = itemView.findViewById(R.id.filterText);
            tick = itemView.findViewById(R.id.selectedFilter);
            state.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    iStateCallBack.selectedState(items.get(getAdapterPosition()));
                }
            });
        }

    }

    public interface IStateCallBack {
        void selectedState(String state);
    }
}
