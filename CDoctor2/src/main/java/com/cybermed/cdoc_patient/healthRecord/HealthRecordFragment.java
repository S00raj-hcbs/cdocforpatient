package com.cybermed.cdoc_patient.healthRecord;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.BaseFragment;
import com.cybermed.cdoc_patient.databinding.ActivityHealthRecordsBinding;
import com.cybermed.cdoc_patient.main.FragmentMainActivity;
import com.cybermed.cdoc_patient.main.HomeFragment;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;


public class HealthRecordFragment extends BaseFragment {
    ActivityHealthRecordsBinding binding;
    HealthRecordAdapter adapter;
    String[] titleArray;
    View view;
    Context context;

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_health_records, container, false);
        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((FragmentMainActivity) getActivity() != null)
                    ((FragmentMainActivity) getActivity()).setHomeNavigation();
            }
        });
        return binding.getRoot();
    }

    @Override
    protected void initLayout(View view) {
        context = getActivity();
        setRecyclerView();
    }

    void setRecyclerView() {
        //binding.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        binding.recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        ArrayList<String> data = new ArrayList<>();
        titleArray = getResources().getStringArray(R.array.title);
        Collections.addAll(data, titleArray);
        adapter = new HealthRecordAdapter(data, new HealthRecordAdapter.ItemClickListner() {
            @Override
            public void itemClick(String item) {
                int index = Arrays.asList(titleArray).indexOf(item);
                switch (index) {
                  /*  case "Vital Check":
                        ((HomeFragment) getParentFragment()).openVitalFragment();
                       // ((HomeFragment) getParentFragment()).openVitalcheckFragment();
                        break;*/
                    case /*"Lab Records"*/0:
                        ((HomeFragment) getParentFragment()).openLabReportFragment();
                        break;
                    case /*"Immunizations"*/1:
                        ((HomeFragment) getParentFragment()).openImmunizationFragment();
                        break;
                    case /*"Allergies"*/2:
                        ((HomeFragment) getParentFragment()).openAllergiesFragment();
                        break;
                    case /*"Medications"*/3:
                        ((HomeFragment) getParentFragment()).openMedicationFragment();
                        break;
                    case /*"Referral"*/4:
                        ((HomeFragment) getParentFragment()).openReferalFragment();
                        break;
                }
            }
        });
        binding.recyclerView.setAdapter(adapter);
    }
}
