package com.cybermed.cdoc_patient.me.referral.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.databinding.AdapterLabBinding;
import com.cybermed.cdoc_patient.databinding.AdapterReffralBinding;
import com.cybermed.cdoc_patient.me.referral.model.ReferralData;
import com.cybermed.cdoc_patient.util.AppConstant;
import com.cybermed.cdoc_patient.util.DateUtil;

import java.util.List;

import static com.cybermed.cdoc_patient.util.AppConstant.DATE_TIME_FORMAT;

public class ReferralAdapter extends RecyclerView.Adapter<ReferralAdapter.MyViewHolder> {

    List<ReferralData> mReferralList;
    ItemClickListner itemClickListner;

    public ReferralAdapter(List<ReferralData> referralData, ItemClickListner itemClickListner) {
        this.mReferralList = referralData;
        this.itemClickListner = itemClickListner;
    }


    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterLabBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),R.layout.adapter_lab, parent, false);
        return new MyViewHolder(binding);
    }


    @Override
    public int getItemCount() {
        return mReferralList.size();
    }

    // stores and recycles views as they are scrolled off screen
    class MyViewHolder extends RecyclerView.ViewHolder {

        AdapterLabBinding binding = null;
        MyViewHolder(AdapterLabBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;

            binding.imgPdf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                     itemClickListner.pdfClick(mReferralList.get(getAdapterPosition()));
                }
            });
            binding.txtViewPdf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListner.pdfClick(mReferralList.get(getAdapterPosition()));
                }
            });
        }


    }


    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ReferralData medicationRecord = mReferralList.get(position);
        holder.binding.labTestName.setText(medicationRecord.getReferFrom());
        String date = DateUtil.formatedDate(medicationRecord.getReferDate(),
                AppConstant.SERVER_DATE_FORMAT, DATE_TIME_FORMAT);
        if (date != null)
            holder.binding.labDate.setText(date);

    }

    public interface ItemClickListner {
        void pdfClick(ReferralData labReportData);
    }
}
