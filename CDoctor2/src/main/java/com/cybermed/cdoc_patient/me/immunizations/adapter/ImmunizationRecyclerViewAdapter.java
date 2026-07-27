package com.cybermed.cdoc_patient.me.immunizations.adapter;
import static com.cybermed.cdoc_patient.util.AppConstant.DATE_FORMAT2;
import static com.cybermed.cdoc_patient.util.AppConstant.SERVER_DATE_FORMAT;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.databinding.AdapterImmunizationListBinding;
import com.cybermed.cdoc_patient.me.immunizations.Model.ImmunizationData;
import com.cybermed.cdoc_patient.util.DateUtil;
import java.util.List;

public class ImmunizationRecyclerViewAdapter extends RecyclerView.Adapter<ImmunizationRecyclerViewAdapter.MyViewHolder> {

    List<ImmunizationData> immunizationDataList;

    Context context;

    public ImmunizationRecyclerViewAdapter(List<ImmunizationData> ImmunizationRecords, Context context) {
        this.immunizationDataList = ImmunizationRecords;
        this.context = context;
    }

    @Override
    @NonNull
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterImmunizationListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.adapter_immunization_list, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ImmunizationData immunizationData = immunizationDataList.get(position);
        holder.binding.tvVaccineDate.setText(DateUtil.formatedDate(immunizationData.getDose_date(), SERVER_DATE_FORMAT, DATE_FORMAT2));
        holder.binding.tvVaccineName.setText(immunizationData.getVaccine_name());
        holder.binding.tvVaccineDose.setText(immunizationData.getDose_number());
    }

    @Override
    public int getItemCount() {
        return immunizationDataList.size();
    }

    public void setList(List<ImmunizationData> medicationRecords) {
        this.immunizationDataList.clear();
        this.immunizationDataList = medicationRecords;
        notifyDataSetChanged();
    }
    class MyViewHolder extends RecyclerView.ViewHolder {
        AdapterImmunizationListBinding binding = null;
        MyViewHolder(AdapterImmunizationListBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}
