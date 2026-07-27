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

public class SettingsLanguageAdapter extends RecyclerView.Adapter<SettingsLanguageAdapter.LanguageHolder> {
    ArrayList<String> items;
    String selected;
    Context context;
    ILanguageCallback iLanguageCallback;
    int rowIndex = -1;
    String defaultLang;

    public SettingsLanguageAdapter(ArrayList<String> items, String defaultLang, Context context, ILanguageCallback iLanguageCallback) {
        this.items = items;
        this.context = context;
        this.iLanguageCallback = iLanguageCallback;
        this.defaultLang = defaultLang;
    }

    @NonNull
    @NotNull
    @Override
    public LanguageHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_language, parent, false);
        return new LanguageHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull LanguageHolder holder, int position) {
        holder.languageLabel.setText(items.get(position));

        if (rowIndex == position || defaultLang.equals(items.get(position))) {
            holder.tick.setVisibility(View.VISIBLE);
            holder.languageLabel.setTextAppearance(R.style.selectedFilter);
        } else {
            holder.tick.setVisibility(View.GONE);
            holder.languageLabel.setTextAppearance(R.style.unselectedFilter);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public String getSelected() {
        return selected;
    }

    class LanguageHolder extends RecyclerView.ViewHolder {
        ImageView tick;
        TextView languageLabel;

        public LanguageHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tick = itemView.findViewById(R.id.tick1);
            languageLabel = itemView.findViewById(R.id.tv_language);
            languageLabel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    iLanguageCallback.selectedLanguage(items.get(getAdapterPosition()));
                    rowIndex = getAdapterPosition();
                    notifyDataSetChanged();

                }
            });

        }
    }

    public interface ILanguageCallback {
        void selectedLanguage(String language);
    }
}
