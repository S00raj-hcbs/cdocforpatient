package com.cybermed.cdoc_patient.me.labereport.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cybermed.cdoc_patient.R;

import java.util.List;

import com.cybermed.cdoc_patient.databinding.AdapterLabBinding;
import com.cybermed.cdoc_patient.me.labereport.model.LabReportData;
import com.cybermed.cdoc_patient.util.DateUtil;


import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import static com.cybermed.cdoc_patient.util.AppConstant.DATE_FORMAT;
import static com.cybermed.cdoc_patient.util.AppConstant.DATE_FORMAT_PROFILE;

public class LabRecyclerViewAdapter extends RecyclerView.Adapter<LabRecyclerViewAdapter.MyViewHolder> {

    List<LabReportData> labReportRecords;
    public ItemClickListner listner;

    public LabRecyclerViewAdapter(List<LabReportData> labReportRecords, ItemClickListner listner) {
        this.labReportRecords = labReportRecords;
        this.listner = listner;
    }


    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterLabBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.adapter_lab, parent, false);
        return new MyViewHolder(binding);
    }


    @Override
    public int getItemCount() {
        return labReportRecords.size();
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
                    listner.pdfClick(labReportRecords.get(getAdapterPosition()));
                }
            });
            binding.txtViewPdf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listner.pdfClick(labReportRecords.get(getAdapterPosition()));
                }
            });

        }
    }


    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        LabReportData labReportRecord = labReportRecords.get(position);
        holder.binding.labTestName.setText(labReportRecord.getTests());
        holder.binding.labDate.setText(DateUtil.formatedDate(labReportRecord.getReportDate(), DATE_FORMAT_PROFILE, DATE_FORMAT));

    }

    public interface ItemClickListner {
        void pdfClick(LabReportData labReportData);
    }


}
