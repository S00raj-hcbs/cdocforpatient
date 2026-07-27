package com.cybermed.cdoc_patient.me.securemessages.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.me.securemessages.Filterable;
import com.cybermed.cdoc_patient.me.securemessages.view.SearchText;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;



public class SearchAdapter<T extends Filterable> extends RecyclerView.Adapter<SearchAdapter.ItemViewHolder> implements android.widget.Filterable {

    private boolean showAsLocale;
    private List<T> itemList;
    private ItemSelectedListener<T> listener;
    private SearchText<T> searchCountryName;

    public SearchAdapter() {
        showAsLocale = false;
        this.itemList = new ArrayList<>();
    }

    public SearchAdapter(boolean showAsLocale) {
        this();
        this.showAsLocale = showAsLocale;
    }

    public SearchAdapter(List<T> list, boolean showAsLocale) {
        this(showAsLocale);
        if (list != null)
            this.itemList.addAll(list);
    }

    public void setItemSelectedListener(ItemSelectedListener<T> listener) {
        this.listener = listener;
    }

    public void setItemList(List<? extends Filterable> list) {
        if (itemList == null || itemList.isEmpty())
            this.itemList = new ArrayList<>();

        itemList.clear();

        if (list != null) {
            itemList.addAll((Collection<T>) list);
        }
        notifyDataSetChanged();

    }

    public List<T> getItemList() {
        return itemList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(
                (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.textview, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ItemViewHolder holder, int position) {
        ((TextView) holder.itemView).setText(getItemList().get(position).getFilter(showAsLocale));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public Filter getFilter() {

        if (searchCountryName == null) {
            searchCountryName = new SearchText<>(this, itemList, showAsLocale);
        }
        return searchCountryName;

    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {


        ItemViewHolder(@NonNull TextView itemView) {
            super(itemView);


            itemView.setOnClickListener(v -> {

                if (listener != null) {
                    listener.onItemSelected(itemList.get(getAdapterPosition()), getAdapterPosition());
                }
            });
        }
    }

    public interface ItemSelectedListener<T extends Filterable> {
        void onItemSelected(T item, int position);
    }

    public interface CancelListener {
        void onCancel();
    }

}
