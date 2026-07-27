package com.cybermed.cdoc_patient.doctor;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.doctor.docDetail.model.InsuranceModel;

import java.util.List;
import java.util.Vector;

public class InsuranceAdapter extends RecyclerView.Adapter<InsuranceAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private Vector<InsuranceModel> list;

    public InsuranceAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        list = new Vector<>();

    }

    public void refreshData(List<InsuranceModel> list) {
        this.list.clear();
        //Tablet Mode
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void appendList(List<InsuranceModel> list) {
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.insurense_adapter_layout, parent, false);
        return new InsuranceAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InsuranceModel insuranceModel = list.get(position);
        if (position==0){
            holder.tv_name.setVisibility(View.VISIBLE);

        }else {
            holder.tv_name.setVisibility(View.INVISIBLE);
        }
        if (!TextUtils.isEmpty(insuranceModel.getCompany_name())) {
            holder.tv_insurance.setText(insuranceModel.getCompany_name()+ (!TextUtils.isEmpty(insuranceModel.getCompany_code())?" ["+insuranceModel.getCompany_code()+"]":""));
        } else {
            holder.tv_insurance.setText("");
        }

        if (!TextUtils.isEmpty(insuranceModel.getInsured_first_name()) && !TextUtils.isEmpty(insuranceModel.getInsured_last_name())) {
            holder.tv_name.setText(insuranceModel.getInsured_first_name()+" "+insuranceModel.getInsured_last_name());
        }else if (!TextUtils.isEmpty(insuranceModel.getInsured_first_name()) && TextUtils.isEmpty(insuranceModel.getInsured_last_name())) {
            holder.tv_name.setText(insuranceModel.getInsured_first_name());
        }else if (TextUtils.isEmpty(insuranceModel.getInsured_first_name()) && !TextUtils.isEmpty(insuranceModel.getInsured_last_name())) {
            holder.tv_name.setText(insuranceModel.getInsured_last_name());
        } else {
            holder.tv_name.setText("");
            holder.tv_name.setVisibility(View.GONE);
        }
    }
    public InsuranceModel getItem(int position) {
        return list.get(position);
    }
    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_insurance, tv_name,tv_status;


        public ViewHolder(View v) {
            super(v);
            tv_insurance = v.findViewById(R.id.tv_insurance);
            tv_name = v.findViewById(R.id.tv_name);
            tv_status = v.findViewById(R.id.tv_status);
        }
    }

}
