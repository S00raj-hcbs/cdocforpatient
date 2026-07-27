package com.cybermed.cdoc_patient.main.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cybermed.cdoc_patient.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuHolder> {
    ArrayList<String> items;
    private Context context;

    ItemClickListner itemClickListner;

    public MenuAdapter(Context context, ArrayList<String> items, ItemClickListner itemClickListner) {
        this.context = context;
        this.items = items;
        this.itemClickListner = itemClickListner;
    }

    @NonNull
    @NotNull
    @Override
    public MenuHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_menu, parent, false);
        return new MenuHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull  MenuHolder holder, int position) {
        holder.label.setText(items.get(position));
        switch (position) {
            case 0:
                holder.img_label.setImageDrawable(ContextCompat.getDrawable(this.context, R.drawable.dashboard_ic));
                break;
          /*  case 1:
                holder.img_label.setImageDrawable(ContextCompat.getDrawable(this.context, R.drawable.health_record_ic));
                break;*/
            case 1:
                holder.img_label.setImageDrawable(ContextCompat.getDrawable(this.context, R.drawable.support_ic));
                break;
            case 2:
                holder.img_label.setImageDrawable(ContextCompat.getDrawable(this.context, R.drawable.setting_ic));
                break;
            case 3:
                holder.img_label.setImageDrawable(ContextCompat.getDrawable(this.context, R.drawable.signout_ic));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MenuHolder extends RecyclerView.ViewHolder {
        TextView label;
        ImageView img_label;
        LinearLayout layout_menu;

        public MenuHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            label = itemView.findViewById(R.id.tv_menu);
            img_label = itemView.findViewById(R.id.img_label);
            layout_menu = itemView.findViewById(R.id.layout_menu);
            layout_menu.setOnClickListener(new View.OnClickListener() {
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