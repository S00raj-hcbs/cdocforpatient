package com.cybermed.cdoc_patient.doctor.docDetail;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.doctor.docDetail.model.ProviderLocationListModel;

import java.util.ArrayList;
import java.util.List;

public class ProviderLocationAdapter extends RecyclerView.Adapter<ProviderLocationAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<ProviderLocationListModel> list;
    private IProviderLocationCallback IProviderLocationCallback;
    int selectedPosition = -1;
    public ProviderLocationAdapter(Context context, IProviderLocationCallback iProviderLocationCallback) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        list = new ArrayList<>();

        this.IProviderLocationCallback = iProviderLocationCallback;
    }


    public void appendList(List<ProviderLocationListModel> list) {
        this.list.addAll(list);
        if (!list.isEmpty()){
            this.selectedPosition=0;
        }else {
            this.selectedPosition=-1;
        }
        notifyDataSetChanged();
    }
    public interface IProviderLocationCallback {

        void itemSelect(ProviderLocationListModel listModel);
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        RadioButton radio_card;
        TextView locationName;
        LinearLayout viewParent;


        public ViewHolder(View v) {
            super(v);
            radio_card = v.findViewById(R.id.radio_card);
            locationName = v.findViewById(R.id.locationName);
            viewParent = v.findViewById(R.id.viewParent);
        }
    }


    public ProviderLocationListModel getItem(int position) {
        return list.get(position);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.provider_location_adapter_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProviderLocationListModel listModel = list.get(position);
        holder.locationName.setText(!formatAddress(listModel).trim().isEmpty() ?formatAddress(listModel):"");

        if (selectedPosition == position) {
            holder.radio_card.setChecked(true);
        } else {
            holder.radio_card.setChecked(false);
        }
        holder.viewParent.setOnClickListener(v1 -> {
            selectedPosition = position;
            IProviderLocationCallback.itemSelect(list.get(position));
            notifyDataSetChanged();
        });
        holder.radio_card.setOnClickListener(v1 -> {
            selectedPosition = position;
            IProviderLocationCallback.itemSelect(list.get(position));
            notifyDataSetChanged();
        });

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return  list == null ? 0 : list.size();
    }




    public void clearList() {
        list.clear();
        notifyDataSetChanged();
    }

    private String formatAddress(ProviderLocationListModel facility) {
        StringBuilder addressBuilder = new StringBuilder();

        if (!TextUtils.isEmpty(facility.getFacility_addr1())) {
            addressBuilder.append(facility.getFacility_addr1());
        }

        if (!TextUtils.isEmpty(facility.getFacility_addr2())) {
            if (addressBuilder.length() > 0) {
                addressBuilder.append(", ");
            }
            addressBuilder.append(facility.getFacility_addr2());
        }

        if (!TextUtils.isEmpty(facility.getFacility_city())) {
            if (addressBuilder.length() > 0) {
                addressBuilder.append(", ");
            }
            addressBuilder.append(facility.getFacility_city());
        }

        if (!TextUtils.isEmpty(facility.getFacility_state())) {
            if (addressBuilder.length() > 0) {
                addressBuilder.append(", ");
            }
            addressBuilder.append(facility.getFacility_state());
        }

        if (!TextUtils.isEmpty(facility.getFacility_zip())) {
            if (addressBuilder.length() > 0) {
                addressBuilder.append(" ");
            }
            addressBuilder.append(facility.getFacility_zip());
        }

        return addressBuilder.toString();
    }
}
