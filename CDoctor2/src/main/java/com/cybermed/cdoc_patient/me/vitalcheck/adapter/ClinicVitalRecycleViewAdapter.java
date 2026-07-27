package com.cybermed.cdoc_patient.me.vitalcheck.adapter;

import static com.cybermed.cdoc_patient.util.AppConstant.DATE_FORMAT2;
import static com.cybermed.cdoc_patient.util.AppConstant.SERVER_DATE_FORMAT;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.databinding.AdapterClinicVitalBinding;
import com.cybermed.cdoc_patient.me.vitalcheck.model.ClinicVitaldata;
import com.cybermed.cdoc_patient.util.DateUtil;
import java.util.List;


public class ClinicVitalRecycleViewAdapter extends RecyclerView.Adapter<ClinicVitalRecycleViewAdapter.MyViewHolder>{

    List<ClinicVitaldata> clinicVitalDataList;
    Context context;

    int pos=-1;

    public ClinicVitalRecycleViewAdapter(List<ClinicVitaldata> clinicVitaldata, Context context) {
        this.clinicVitalDataList = clinicVitaldata;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterClinicVitalBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.adapter_clinic_vital, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ClinicVitaldata clinicVitaldata = clinicVitalDataList.get(position);
        holder.binding.tvDate.setText(DateUtil.formatedDate(clinicVitaldata.getVitalDate(), SERVER_DATE_FORMAT, DATE_FORMAT2));
        if (clinicVitaldata.getBMI()==null||clinicVitaldata.getBMI().equals("null")){
            holder.binding.tvBmi.setText("--");
        }else {
            holder.binding.tvBmi.setText(clinicVitaldata.getBMI());
        }
        if (clinicVitaldata.getBP()==null||clinicVitaldata.getBP().equals("null")){
            holder.binding.tvBp.setText("--");
        }else {
            holder.binding.tvBp.setText(clinicVitaldata.getBP());
        }

        if (clinicVitaldata.getHC()==null||clinicVitaldata.getHC().equals("null")){
            holder.binding.tvHc.setText("--");
        }else {
            holder.binding.tvHc.setText(clinicVitaldata.getHC());
        }


        if (clinicVitaldata.getHeight()==null||clinicVitaldata.getHeight().equals("null")){
            holder.binding.tvHeight.setText("--");
        }else {
            holder.binding.tvHeight.setText(clinicVitaldata.getHeight());
        }


        if (clinicVitaldata.getWeight()==null||clinicVitaldata.getWeight().equals("null")){
            holder.binding.tvWeight.setText("--");
        }else {
            holder.binding.tvWeight.setText(clinicVitaldata.getWeight());
        }

        if (clinicVitaldata.getTemp()==null||clinicVitaldata.getTemp().equals("null")){
            holder.binding.tvTemp.setText("--");
        }else {
            holder.binding.tvTemp.setText(clinicVitaldata.getTemp());
        }

        if (clinicVitaldata.getPulse()==null||clinicVitaldata.getPulse().equals("null")){
            holder.binding.tvPulse.setText("--");
        }else {
            holder.binding.tvPulse.setText(clinicVitaldata.getPulse());
        }

        if (clinicVitaldata.getGlucose()==null||clinicVitaldata.getGlucose().equals("null")){
            holder.binding.tvGlucose.setText("--");
        }else {
            holder.binding.tvGlucose.setText(clinicVitaldata.getGlucose());
        }

        if (clinicVitaldata.getPeak_Flow()==null||clinicVitaldata.getPeak_Flow().equals("null")){
            holder.binding.tvPickflow.setText("--");
        }else {
            holder.binding.tvPickflow.setText(clinicVitaldata.getPeak_Flow());
        }

        if (clinicVitaldata.getHGB()==null||clinicVitaldata.getHGB().equals("null")){
            holder.binding.tvHgb.setText("--");
        }else {
            holder.binding.tvHgb.setText(clinicVitaldata.getHGB());
        }
        if (position==clinicVitalDataList.size()-1){
            holder.binding.viewLine.setVisibility(View.INVISIBLE);
        }else {
            holder.binding.viewLine.setVisibility(View.VISIBLE);
        }

      /*  if (pos==position){
            holder.binding.linData.setVisibility(View.VISIBLE);
            holder.binding.imgArrow.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_action_up_arrow));
            holder.binding.extraView.setVisibility(View.GONE);
        }
        else {
            holder.binding.linData.setVisibility(View.GONE);
            holder.binding.imgArrow.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_action_down_arrow));
            holder.binding.extraView.setVisibility(View.VISIBLE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position!=pos){
                    pos=position;
                    notifyDataSetChanged();
                }

            }
        });*/
    }

    @Override
    public int getItemCount() {
        return clinicVitalDataList.size();
    }
    public void setList(List<ClinicVitaldata> ClinicVitalRecords) {
        this.clinicVitalDataList.clear();
        this.clinicVitalDataList = ClinicVitalRecords;
        notifyDataSetChanged();
    }
    class MyViewHolder extends RecyclerView.ViewHolder {
        AdapterClinicVitalBinding binding = null;
        MyViewHolder(AdapterClinicVitalBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;

        }
    }

}
