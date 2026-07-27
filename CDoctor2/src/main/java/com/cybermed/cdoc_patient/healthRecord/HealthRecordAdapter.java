package com.cybermed.cdoc_patient.healthRecord;

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

public class HealthRecordAdapter extends RecyclerView.Adapter<HealthRecordAdapter.HealthRecordHolder> {
    ArrayList<String> items;
    ItemClickListner itemClickListner;

    public HealthRecordAdapter(ArrayList<String> items, ItemClickListner itemClickListner) {
        this.items = items;
        this.itemClickListner = itemClickListner;
    }

    @NonNull
    @NotNull
    @Override
    public HealthRecordHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_health_records, parent, false);
        return new HealthRecordHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull HealthRecordHolder holder, int position) {
        holder.label.setText(items.get(position));
        switch (position) {
            case 0:
                holder.imgItem.setImageResource(R.drawable.ic_color_lab);
                break;
            case 1:
                holder.imgItem.setImageResource(R.drawable.ic_color_immune);
                break;
            case 2:
                holder.imgItem.setImageResource(R.drawable.ic_color_allergy);
                break;
            case 3:
                holder.imgItem.setImageResource(R.drawable.ic_color_medicine);
                break;
           /* case 4:
                holder.label.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_color_vital, 0, 0, 0);
                break;*/
            case 4:
                holder.imgItem.setImageResource(R.drawable.ic_color_referral);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class HealthRecordHolder extends RecyclerView.ViewHolder {
        TextView label;
        ImageView imgItem;

        public HealthRecordHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            label = itemView.findViewById(R.id.text_label);
            imgItem = itemView.findViewById(R.id.imgItem);
            //label = itemView.findViewById(R.id.tv_lab_records);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListner.itemClick(items.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface ItemClickListner {
        void itemClick(String item);
    }
}
