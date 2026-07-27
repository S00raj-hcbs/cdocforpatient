package com.cybermed.cdoc_patient.me.allergies.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.databinding.AdapterAllergiesBinding;
import com.cybermed.cdoc_patient.me.allergies.model.AllergiesData;
import java.util.List;

public class AllergiesRecyclerViewAdapter extends RecyclerView.Adapter<AllergiesRecyclerViewAdapter.MyViewHolder> {

    List<AllergiesData> AllergiesDataList;

    Context context;

    public AllergiesRecyclerViewAdapter(List<AllergiesData> AllergiesRecords, Context context) {
        this.AllergiesDataList = AllergiesRecords;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterAllergiesBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.adapter_allergies, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        AllergiesData allergiesData = AllergiesDataList.get(position);
        holder.binding.tvName.setText(allergiesData.getAllergy_name());
        holder.binding.tvStatus.setText(allergiesData.getAllergy_status());
        holder.binding.tvSeverity.setText(allergiesData.getSeverity_name());
        holder.binding.tvReactionNote.setText(allergiesData.getReaction_notes());
    }

    @Override
    public int getItemCount() {
        return AllergiesDataList.size();
    }

    public void setList(List<AllergiesData> medicationRecords) {
        this.AllergiesDataList.clear();
        this.AllergiesDataList = medicationRecords;
        notifyDataSetChanged();
    }
    class MyViewHolder extends RecyclerView.ViewHolder {
        AdapterAllergiesBinding binding = null;
        MyViewHolder(AdapterAllergiesBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;


        }
    }
}
