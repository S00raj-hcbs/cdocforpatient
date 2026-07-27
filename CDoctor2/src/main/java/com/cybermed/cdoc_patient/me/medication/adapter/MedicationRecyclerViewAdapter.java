package com.cybermed.cdoc_patient.me.medication.adapter;

import static com.cybermed.cdoc_patient.util.AppConstant.MEDICATION_REMINDER;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.PreferenceUtil;
import com.cybermed.cdoc_patient.databinding.AdapterMedicationBinding;
import com.cybermed.cdoc_patient.me.medication.model.MedicationData;

import java.util.List;

public class MedicationRecyclerViewAdapter extends RecyclerView.Adapter<MedicationRecyclerViewAdapter.MyViewHolder> {

    List<MedicationData> medicationRecordsList;
    ItemClickListner itemClickListner;
    Context context;

    public MedicationRecyclerViewAdapter(List<MedicationData> medicationRecords, Context context) {
        this.medicationRecordsList = medicationRecords;
        this.context = context;
    }


    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterMedicationBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.adapter_medication, parent, false);
        return new MyViewHolder(binding);
    }


    @Override
    public int getItemCount() {
        return medicationRecordsList.size();
    }

    // stores and recycles views as they are scrolled off screen
    class MyViewHolder extends RecyclerView.ViewHolder {

        AdapterMedicationBinding binding = null;

        MyViewHolder(AdapterMedicationBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
            // ButterKnife.bind(this, itemView);
            binding.reminderTxt.setOnClickListener(v -> itemClickListner.itemClick(medicationRecordsList.get(getAdapterPosition())));

            binding.downArrow.setOnClickListener(view -> {
                binding.upArrow.setVisibility(View.VISIBLE);
                binding.downArrow.setVisibility(View.GONE);
                binding.medDrugInfo.setVisibility(View.VISIBLE);
            });
            binding.upArrow.setOnClickListener(view -> {
                binding.downArrow.setVisibility(View.VISIBLE);
                binding.upArrow.setVisibility(View.GONE);
                binding.medDrugInfo.setVisibility(View.GONE);
            });

        }


    }


    // binds the data to the TextView in each row

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MedicationData medicationRecord = medicationRecordsList.get(position);

        if (!TextUtils.isEmpty(medicationRecord.getDrugInfo())) {
            String st[] = medicationRecord.getDrugInfo().split(" ");
            if (st.length > 0) {
                holder.binding.medicineName.setText(st[0].substring(0, 1).toUpperCase() +
                        st[0].substring(1).toLowerCase());
                holder.binding.medDrugInfo.setText(medicationRecord.getDrugInfo().substring(0, 1).toUpperCase() +
                        medicationRecord.getDrugInfo().substring(1).toLowerCase());
            }
        }
        holder.binding.txtdate.setText(medicationRecord.getEntryDate());
        if (!TextUtils.isEmpty(medicationRecord.getSIG())) {
            holder.binding.tvDosage.setVisibility(View.VISIBLE);
            holder.binding.tvDosage.setText(context.getString(R.string.dosage)+" "+medicationRecord.getSIG());
        } else holder.binding.tvDosage.setVisibility(View.GONE);
        String txtReminder = PreferenceUtil.getString(MEDICATION_REMINDER + medicationRecord.getEntryDate() + medicationRecord.getDrugInfo(), null);
        if (!TextUtils.isEmpty(txtReminder)) {
            holder.binding.reminderTxt.setText(R.string.reschedule);
        } else {
            holder.binding.reminderTxt.setText(R.string.reminder);
        }


    }


    public void setList(List<MedicationData> medicationRecords) {
        this.medicationRecordsList.clear();
        this.medicationRecordsList = medicationRecords;
        notifyDataSetChanged();
    }

    public interface ItemClickListner {
        void itemClick(MedicationData data);
    }

    public void setListner(ItemClickListner itemClickListner) {
        this.itemClickListner = itemClickListner;
    }
}
