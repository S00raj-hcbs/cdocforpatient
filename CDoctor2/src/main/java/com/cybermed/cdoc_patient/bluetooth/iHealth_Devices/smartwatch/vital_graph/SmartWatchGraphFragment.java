package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch.vital_graph;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.Navigation;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.annotation.Factory;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch.swUtil.ReqSaveSWData;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch.vital_graph.utility.TYPE;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch.vital_graph.utility.Utility;
import com.cybermed.cdoc_patient.common.BaseFragment;
import com.cybermed.cdoc_patient.databinding.SmartWatchGraphFragBinding;
import com.cybermed.cdoc_patient.doctor.docDetail.model.BaseResponseModel;
import com.cybermed.cdoc_patient.webapi.IResponseReceiver;
import com.cybermed.cdoc_patient.webapi.manager.HomeApiManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cybermed.cdoc_patient.util.AppConstant.BLOOD_OXYGEN;
import static com.cybermed.cdoc_patient.util.AppConstant.BLOOD_PRESSURE;
import static com.cybermed.cdoc_patient.util.AppConstant.CALORIES;
import static com.cybermed.cdoc_patient.util.AppConstant.DISTANCE;
import static com.cybermed.cdoc_patient.util.AppConstant.HEART_RATE;
import static com.cybermed.cdoc_patient.util.AppConstant.SMART_MAC;
import static com.cybermed.cdoc_patient.util.AppConstant.SMART_WATCH;
import static com.cybermed.cdoc_patient.util.AppConstant.STEPS;
import static com.cybermed.cdoc_patient.util.AppConstant.TEMP;


public class SmartWatchGraphFragment extends BaseFragment {

    private HashMap<String, List<Map<String, String>>> hashMap = new HashMap<>();
    SmartWatchGraphFragBinding binding;
    String swDataJson, smartMac, graphType, dataJson;

    public static final String GRAPH_TYPE = "graph_type";
    public static final String HASHMAP = "hashmap";
    Context context;

    @Factory
    public static SmartWatchGraphFragment newInstance(String mac, String graph_type, HashMap<String, List<Map<String, String>>> hashmap) {
        SmartWatchGraphFragment fragment = new SmartWatchGraphFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putString(SMART_MAC, mac);
        args.putString(GRAPH_TYPE, graph_type);
        args.putSerializable(HASHMAP, hashmap);


        fragment.setArguments(args);

        return fragment;
    }


    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.smart_watch_graph_frag, container, false);
        binding.toolBar.txtTittle.setText(getString(R.string.smart_watch));
        context=getActivity();
        return binding.getRoot();
    }

    @Override
    protected void initLayout(View view) {
        binding.graphDivider.topLayout.setVisibility(View.GONE);
        if (getArguments() != null) {
            smartMac = getArguments().getString(SMART_MAC);
            graphType = getArguments().getString(GRAPH_TYPE);
            hashMap = (HashMap<String, List<Map<String, String>>>) getArguments().getSerializable(HASHMAP);

        }
        if (smartMac != null) {
            getDate();
        }
        if (hashMap != null) {
            Utility.setDataToUi(hashMap, graphType, binding);
        }
        initToolBar(graphType);
        binding.graphDivider.weekBtn.setOnClickListener(v -> Utility.updateGraph(binding, TYPE.WEEKLY, graphType));
        binding.graphDivider.monthBtn.setOnClickListener(v -> Utility.updateGraph(binding, TYPE.MONTHLY, graphType));
        binding.graphDivider.yearBtn.setOnClickListener(v -> Utility.updateGraph(binding, TYPE.YEARLY, graphType));
        binding.toolBar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hashMap == null) {
                    Navigation.findNavController(v).navigate(R.id.action_watchGraph_to_IOTFragment);
                } else
                    Navigation.findNavController(v).navigate(R.id.action_watchGraph_to_watchItemList, getArguments());
            }
        });
    }

    public void initToolBar(String input) {
        if (input.equals(STEPS)) {
            binding.toolBar.txtTittle.setText(R.string.steps_count);
        } else if (input.equals(DISTANCE)) {
            binding.toolBar.txtTittle.setText(R.string.distance);
        } else if (input.equals(CALORIES)) {
            binding.toolBar.txtTittle.setText(R.string.calories);
        } else if (input.equals(BLOOD_PRESSURE)) {
            binding.toolBar.txtTittle.setText(R.string.iot_blood_pressure);
        } else if (input.equals(BLOOD_OXYGEN)) {
            binding.toolBar.txtTittle.setText(R.string.blood_oxygen);
        } else if (input.equals(TEMP)) {
            binding.toolBar.txtTittle.setText(R.string.temperature);
        } else if (input.equals(HEART_RATE)) {
            binding.toolBar.txtTittle.setText(R.string.heart_rate);
        } else if (input.equals(SMART_WATCH)) {
            binding.toolBar.txtTittle.setText(R.string.smart_watch);
        }

    }

    public String getDate() {
        HomeApiManager apiManager = new HomeApiManager(new IResponseReceiver() {
            @Override
            public void onSuccess(Object data) {

                ArrayList<ReqSaveSWData> resSmartGetData = (ArrayList<ReqSaveSWData>)
                        ((BaseResponseModel<List<ReqSaveSWData>>) data).getObject();
                swDataJson = resSmartGetData.get(0).getValue();
                // convertJsonToMap(swDataJson);
                Utility.initializeData(swDataJson);
                Utility.decorateGraph(binding);
                Utility.updateGraph(binding, TYPE.WEEKLY, graphType);

            }

            @Override
            public void onFailure(@NonNull String errorResponse) {

                Toast.makeText(getContext(), "failure", Toast.LENGTH_LONG).show();
            }
        }, context);

        apiManager.getSWData(SMART_WATCH, smartMac, "1");
        return swDataJson;
    }

}