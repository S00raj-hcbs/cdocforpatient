package com.cybermed.cdoc_patient.me.document.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.databinding.AdapterDocumentBinding;
import com.cybermed.cdoc_patient.me.document.model.doc_model;


import java.util.List;

public class DocumentRecycleViewAdapter extends RecyclerView.Adapter<DocumentRecycleViewAdapter.MyViewHolder> {

    List<doc_model> labDocRecords;
    public ItemClickListner listner;

    public DocumentRecycleViewAdapter(List<doc_model> labReportRecords, ItemClickListner listner) {
        this.labDocRecords = labReportRecords;
        this.listner = listner;
    }

    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterDocumentBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.adapter_document, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return labDocRecords.size();
    }
    // stores and recycles views as they are scrolled off screen
    class MyViewHolder extends RecyclerView.ViewHolder {
        AdapterDocumentBinding binding=null;
        MyViewHolder(AdapterDocumentBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
            /*binding.imgPdf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listner.pdfClick(labDocRecords.get(getAdapterPosition()));
                }
            });*/
            binding.txtViewPdf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listner.pdfClick(labDocRecords.get(getAdapterPosition()));
                }
            });
            binding.cardDocument.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listner.pdfClick(labDocRecords.get(getAdapterPosition()));
                }
            });
        }
    }


    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        doc_model doc_Record = labDocRecords.get(position);
        holder.binding.labTestName.setText(doc_Record.getDoc_title());
        //holder.binding.labDate.setText(DateUtil.formatedDate(doc_Record.getReportDate(), DATE_FORMAT_PROFILE, DATE_FORMAT));
        holder.binding.labDate.setText(doc_Record.getEntry_date());

    }

    public interface ItemClickListner {
        void pdfClick(doc_model labReportData);
    }
}