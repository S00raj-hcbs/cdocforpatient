package com.cybermed.cdoc_patient.me.vitalcheck.adapter;

import static com.cybermed.cdoc_patient.me.vitalcheck.VitalMonitorFragment.convertFtInToMeters;
import static com.cybermed.cdoc_patient.me.vitalcheck.VitalMonitorFragment.convertToKg;
import static com.cybermed.cdoc_patient.util.AppConstant.KEY_BMI;
import static com.cybermed.cdoc_patient.util.AppConstant.KEY_HEIGHT;
import static com.cybermed.cdoc_patient.util.AppConstant.KEY_WEIGHT;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.videoui.Constant;
import com.cybermed.cdoc_patient.databinding.AdapterClinicVitalBinding;
import com.cybermed.cdoc_patient.databinding.VitalRecyclerAdapterLayoutBinding;
import com.cybermed.cdoc_patient.me.vitalcheck.BMIGraphActivity;
import com.cybermed.cdoc_patient.me.vitalcheck.model.VitalDataNew;
import com.cybermed.cdoc_patient.util.AppConstant;

import java.util.List;

public class VitalRecycleViewAdapter extends RecyclerView.Adapter<VitalRecycleViewAdapter.MyViewHolder>{
    List<VitalDataNew> clinicVitalDataList;
    Context context;
    private final NavController navController;
    String height="";
    String weight="";
    private final FragmentManager fragmentManager;

    public VitalRecycleViewAdapter(List<VitalDataNew> clinicVitaldata, Context context,@NonNull NavController navController,FragmentManager fragmentManager) {
        this.clinicVitalDataList = clinicVitaldata;
        this.context = context;
        this.navController = navController;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        VitalRecyclerAdapterLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.vital_recycler_adapter_layout, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        VitalDataNew vitalDataNew = clinicVitalDataList.get(position);
        holder.binding.textVitalName.setText(vitalDataNew.getName());
        holder.binding.textVitalReading.setText(vitalDataNew.getValue());
        holder.binding.imgVital.setImageDrawable(vitalDataNew.getImage());
        Constant.istabselected="1";
        if (vitalDataNew.getType().equals("Height")){
            height=vitalDataNew.getValue();
        } else if (vitalDataNew.getType().equals("hr")){
            if (vitalDataNew.getValue().equals("--")){

            }else {
                AppConstant.getdataHRtextColor(Double.parseDouble(vitalDataNew.getValue()), holder.binding.textVitalReading);
            }
        }else if (vitalDataNew.getType().equals("Temp")){
            if (vitalDataNew.getValue().equals("--")){

            }else {
                AppConstant.getTemperatureColorInFahrenheit(Double.parseDouble(vitalDataNew.getValue().replace("°F","")), holder.binding.textVitalReading);
            }
        }else if (vitalDataNew.getType().equals("Glucose")){
            if (vitalDataNew.getValue().equals("--")){

            }else {
                AppConstant.getdataGlucosetextColor(Double.parseDouble(vitalDataNew.getValue()), holder.binding.textVitalReading);
            }
        }else if (vitalDataNew.getType().equals("BP")){
            if (vitalDataNew.getValue().equals("--")){

            }else {
                String[] values = vitalDataNew.getValue().split("/");
                AppConstant.getdataBPtextColor(Double.parseDouble(values[0]),Double.parseDouble(values[1]), holder.binding.textVitalReading);
            }
        }else if (vitalDataNew.getType().equals("HC")){
            if (vitalDataNew.getValue().equals("--")){

            }else {
                AppConstant.getHeadCircumferenceColor(Double.parseDouble(vitalDataNew.getValue()), holder.binding.textVitalReading);
            }
        }else if (vitalDataNew.getType().equals("BMI")){
            if (vitalDataNew.getValue().equals("--")){

            }else {
                dynamictextforBMI(vitalDataNew.getValue(), holder.binding.textVitalReading);
            }
        }else if (vitalDataNew.getType().equals("Weight")){
            weight=vitalDataNew.getValue();
        }
        holder.binding.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(vitalDataNew.getType().equals("BMI")){
                    if (vitalDataNew.getValue().equals("--")){

                    }else {
                        if (TextUtils.isEmpty(vitalDataNew.getType())){
                            if (!TextUtils.isEmpty(height) && !TextUtils.isEmpty(weight)){
                                float bmi = (float) convertToKg(weight) / ((float) convertFtInToMeters(height) * (float) convertFtInToMeters(height));
                                Bundle args = new Bundle();
                                args.putString(KEY_HEIGHT, height);
                                args.putString(KEY_WEIGHT, weight);
                                args.putString(KEY_BMI, String.format("%.1f",bmi));
                                BMIGraphActivity bmiGraphActivity = new BMIGraphActivity();
                                bmiGraphActivity.setArguments(args);
                                bmiGraphActivity.show(fragmentManager, "BMIGraph Fragment");
                            }
                        }else {
                            if (!TextUtils.isEmpty(height) && !TextUtils.isEmpty(weight)){
                                Bundle args = new Bundle();
                                args.putString(KEY_HEIGHT, height);
                                args.putString(KEY_WEIGHT, weight);
                                args.putString(KEY_BMI, vitalDataNew.getValue());
                                BMIGraphActivity bmiGraphActivity = new BMIGraphActivity();
                                bmiGraphActivity.setArguments(args);
                                bmiGraphActivity.show(fragmentManager, "BMIGraph Fragment");
                            }else {
                          /*  Bundle args = new Bundle();
                            args.putString(KEY_HEIGHT, height);
                            args.putString(KEY_WEIGHT, weight);
                            args.putString(KEY_BMI, vitalDataNew.getValue());
                            BMIGraphActivity bmiGraphActivity = new BMIGraphActivity();
                            bmiGraphActivity.setArguments(args);
                            bmiGraphActivity.show(fragmentManager, "BMIGraph Fragment");*/
                            }
                        }
                    }



                }else {
                    Constant.istype=vitalDataNew.getType();
                    navController.navigate(R.id.action_IOT_MainPage_Fragment_to_vitalgraph);
                }
            }
        });
        holder.binding.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(vitalDataNew.getType().equals("BMI")){
                    if (vitalDataNew.getValue().equals("--")){

                    }else {
                        if (TextUtils.isEmpty(vitalDataNew.getType())){
                            if (!TextUtils.isEmpty(height) && !TextUtils.isEmpty(weight)){
                                float bmi = (float) convertToKg(weight) / ((float) convertFtInToMeters(height) * (float) convertFtInToMeters(height));
                                Bundle args = new Bundle();
                                args.putString(KEY_HEIGHT, height);
                                args.putString(KEY_WEIGHT, weight);
                                args.putString(KEY_BMI, String.format("%.1f",bmi));
                                BMIGraphActivity bmiGraphActivity = new BMIGraphActivity();
                                bmiGraphActivity.setArguments(args);
                                bmiGraphActivity.show(fragmentManager, "BMIGraph Fragment");
                            }
                        }else {
                            if (!TextUtils.isEmpty(height) && !TextUtils.isEmpty(weight)){
                                Bundle args = new Bundle();
                                args.putString(KEY_HEIGHT, height);
                                args.putString(KEY_WEIGHT, weight);
                                args.putString(KEY_BMI, vitalDataNew.getValue());
                                BMIGraphActivity bmiGraphActivity = new BMIGraphActivity();
                                bmiGraphActivity.setArguments(args);
                                bmiGraphActivity.show(fragmentManager, "BMIGraph Fragment");
                            }else {
                          /*  Bundle args = new Bundle();
                            args.putString(KEY_HEIGHT, height);
                            args.putString(KEY_WEIGHT, weight);
                            args.putString(KEY_BMI, vitalDataNew.getValue());
                            BMIGraphActivity bmiGraphActivity = new BMIGraphActivity();
                            bmiGraphActivity.setArguments(args);
                            bmiGraphActivity.show(fragmentManager, "BMIGraph Fragment");*/
                            }
                        }
                    }


                }else {
                    Constant.istype=vitalDataNew.getType();
                    navController.navigate(R.id.action_IOT_MainPage_Fragment_to_vitalgraph);
                }
            }
        });



    }
    public void dynamictextforBMI(String value,TextView textView){
        SpannableStringBuilder spannableBuilder = new SpannableStringBuilder();
        float bmiValue;
        try {
            bmiValue = Float.parseFloat(value);
        } catch (NumberFormatException e) {
            bmiValue = 0.0f; // Default value if parsing fails
        }
        if(bmiValue<18.5){
            textView.setTextColor(Color.parseColor("#34c85a"));
        }else if (bmiValue>=18.5 && bmiValue<=25){
            textView.setTextColor(Color.parseColor("#ffcc00"));
        }else if (bmiValue>25 && bmiValue<=30){
            textView.setTextColor(Color.parseColor("#ff9501"));
        }else {
            textView.setTextColor(Color.parseColor("#ff3b2f"));
        }

        /*  if(bmiValue<18.5){
            textView.setTextColor(Color.parseColor("#32CD32"));
        }else if (bmiValue>=18.5 && bmiValue<=25){
            textView.setTextColor(Color.parseColor("#FFD700"));
        }else if (bmiValue>25 && bmiValue<=30){
            textView.setTextColor(Color.parseColor("#ff9501"));
        }else {
            textView.setTextColor(Color.parseColor("#FF4500"));
        }*/


        textView.setText(String.valueOf(bmiValue));

    }

    @Override
    public int getItemCount() {
        return clinicVitalDataList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        VitalRecyclerAdapterLayoutBinding binding = null;
        MyViewHolder(VitalRecyclerAdapterLayoutBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;

        }
    }
}
