package com.cybermed.cdoc_patient.me.vitalcheck;

import static com.cybermed.cdoc_patient.me.MeFragment.USERINFOKEY;
import static com.cybermed.cdoc_patient.util.AppConstant.DATE_TIME_FORMAT3;
import static com.cybermed.cdoc_patient.util.AppConstant.DATE_TIME_FORMAT4;
import static com.cybermed.cdoc_patient.util.AppConstant.DATE_TIME_FORMAT5;
import static com.cybermed.cdoc_patient.util.AppConstant.IS_FROM_HEALTH_RECORD;
import static com.cybermed.cdoc_patient.util.AppConstant.IShome;
import static com.cybermed.cdoc_patient.util.AppConstant.SERVER_DATE_FORMAT;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.cdfortis.datainterface.soap.UserInfo;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.annotation.Factory;

import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealthGraph.GraphData;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealthGraph.GraphUI;

import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch.vital_graph.utility.Custom;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch.vital_graph.utility.Custom_Line;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch.vital_graph.utility.Custom_LineBP;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch.vital_graph.utility.TYPE;
import com.cybermed.cdoc_patient.common.BaseFragment;

import com.cybermed.cdoc_patient.databinding.FragmentVitalCheckUiBinding;

import com.cybermed.cdoc_patient.main.HomeFragment;
import com.cybermed.cdoc_patient.me.MeFragment;
import com.cybermed.cdoc_patient.me.manager.ProfileApiManager;
import com.cybermed.cdoc_patient.me.vitalcheck.adapter.ClinicVitalRecycleViewAdapter;
import com.cybermed.cdoc_patient.me.vitalcheck.model.ClinicVitaldata;
import com.cybermed.cdoc_patient.me.vitalcheck.model.ResponseVital;
import com.cybermed.cdoc_patient.me.vitalcheck.model.VitalData;
import com.cybermed.cdoc_patient.me.vitalcheck.model.VitalDataBP;
import com.cybermed.cdoc_patient.util.AppUtiltiy;
import com.cybermed.cdoc_patient.util.DateUtil;
import com.cybermed.cdoc_patient.webapi.IResponseReceiver;
import com.github.mikephil.charting.animation.Easing;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class VitalCheckFragment extends BaseFragment implements
        MeFragment.OnInnerFragmentStatusChange,HomeFragment.OnInnerFragmentStatusChange{
    Activity context;
    FragmentVitalCheckUiBinding binding;
    ClinicVitalRecycleViewAdapter  clinicVitalRecycleViewAdapter;
    private ArrayList<VitalDataBP> bp_dataVector;
    String[] bph_dataPoints;
    String[] bpl_dataPoints;
    String[] hr_datapoints;
    private String[] weight_datapoints;
    private String[] bo_datapoints;
    private String[] glucose_datapoints;
    private String[] bp_timestamp, hr_timestamp, weight_timestamp, bo_timestamp, glucose_timestamp;
    ArrayList<VitalData> hr_dataVector;
    private ArrayList<VitalData> weight_dataVector;
    private ArrayList<VitalData> bo_dataVector;
    private ArrayList<VitalData> glucose_dataVector;

    public static final String BP_DEVICE_TYPE = "IChoice_BP", PO_DEVICE_TYPE = "IChoice_Oximeter", GLUCOMETER_DEVICE_TYPE = "IChoice_Glucose", SCALE_DEVICE_TYPE = "IChoice_Scale";
    public static boolean isDeviceTablet;
    private final int[] colors = new int[]{Color.parseColor("#fe0000"),
            Color.parseColor("#750e72"),
            Color.parseColor("#53BD8B"),
            Color.parseColor("#F2727A"),
            Color.parseColor("#F79452"),
            Color.parseColor("#DDA827")};

    GraphData graphData;

    Custom custom;
    ArrayList<String> device_value;

    @Factory
    public static VitalCheckFragment newInstance(UserInfo userInfo, boolean isFromHealthRecord, boolean ishome) {
        VitalCheckFragment fragment = new VitalCheckFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putSerializable(USERINFOKEY, userInfo);
        args.putBoolean(IS_FROM_HEALTH_RECORD, isFromHealthRecord);
        args.putBoolean(IShome, ishome);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_vital_check_ui, container, false);
        return binding.getRoot();
    }

    @Override
    protected void initLayout(View view) {
        context = getActivity();
        setRecyclerView();
        callApi();
        clickListner();
        initVal();
        GraphUI.decorateGraph2(binding);
    }

    /**
     * initialize value
     */
    private void initVal() {
        device_value = new ArrayList<>();
        graphData = new GraphData();
        bo_dataVector = new ArrayList<VitalData>();
        glucose_dataVector = new ArrayList<VitalData>();
        weight_dataVector = new ArrayList<VitalData>();
        hr_dataVector = new ArrayList<VitalData>();
        bp_dataVector = new ArrayList<VitalDataBP>();
        isDeviceTablet = AppUtiltiy.isDeviceTablet(getActivity());
    }

    private void clickListner() {
        binding.toolBar.txtTittle.setText("Vitals");
        binding.toolBar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getArguments() != null && getArguments().getBoolean(IS_FROM_HEALTH_RECORD)&& !getArguments().getBoolean(IShome)) {
                    if (getParentFragment() != null)
                        ((HomeFragment) getParentFragment()).openHealthRecordFragment();
                }else   if (getArguments() != null && getArguments().getBoolean(IS_FROM_HEALTH_RECORD)&& getArguments().getBoolean(IShome)) {
                    if (getParentFragment() != null)
                        //((HomeFragment) getParentFragment()).openMainActivity();
                        ((HomeFragment) getParentFragment()).openHomeVitalcheckFragment();
                } else {
                    if (((MeFragment) getParentFragment() != null)) {
                        ((MeFragment) getParentFragment()).openUserActivityFragment();
                    }
                }
            }
        });
        binding.tvClinicVital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.tvClinicVital.setBackground(getResources().getDrawable(R.drawable.selected_tab_bg));
                binding.tvDeviceVital.setBackground(getResources().getDrawable(R.drawable.unselected_tab_bg));
                binding.tvClinicVital.setTextColor(Color.parseColor("#FFFFFF"));
                binding.tvDeviceVital.setTextColor(Color.parseColor("#515055"));
                binding.relativeClinicvital.setVisibility(View.VISIBLE);
                binding.scrollview.setVisibility(View.GONE);
            }
        });
        binding.tvDeviceVital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.tvDeviceVital.setBackground(getResources().getDrawable(R.drawable.selected_tab_bg));
                binding.tvClinicVital.setBackground(getResources().getDrawable(R.drawable.unselected_tab_bg));
                binding.tvDeviceVital.setTextColor(Color.parseColor("#FFFFFF"));
                binding.tvClinicVital.setTextColor(Color.parseColor("#515055"));
                binding.relativeClinicvital.setVisibility(View.GONE);
                binding.scrollview.setVisibility(View.VISIBLE);
            }
        });
        binding.tvClinicVital.setBackground(getResources().getDrawable(R.drawable.unselected_tab_bg));
        binding.tvDeviceVital.setBackground(getResources().getDrawable(R.drawable.selected_tab_bg));
        binding.tvClinicVital.setTextColor(Color.parseColor("#515055"));
        binding.tvDeviceVital.setTextColor(Color.parseColor("#FFFFFF"));
        binding.relativeClinicvital.setVisibility(View.GONE);
        binding.scrollview.setVisibility(View.VISIBLE);
        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                callApi();
            }
        });

        binding.dayBtn.setOnClickListener(v -> updateGraph(TYPE.Daily));
        binding.sixmonthBtn.setOnClickListener(v -> updateGraph(TYPE.SIX_MONTH));
        binding.weekBtn.setOnClickListener(v -> updateGraph(TYPE.WEEKLY));
        binding.monthBtn.setOnClickListener(v -> updateGraph(TYPE.MONTHLY));
        binding.yearBtn.setOnClickListener(v -> updateGraph(TYPE.YEARLY));
    }
    @Override
    public void refreshFragment(boolean isRefresh) {
        super.refreshFragment(isRefresh);
    }

    @Override
    public void onMyResume() {
        callApi();
    }

    @Override
    public void onMyStop() {
    }
    void setRecyclerView() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        clinicVitalRecycleViewAdapter = new ClinicVitalRecycleViewAdapter(new ArrayList<>(), context);
        binding.recyclerView.setAdapter(clinicVitalRecycleViewAdapter);
    }

    //***************api call and success failure**************************************
    private void callApi() {
        Bundle args = getArguments();
        if (args != null) {
            UserInfo userInfo = (UserInfo) args.getSerializable(USERINFOKEY);
            if (userInfo != null) {
                showProgress();
                ProfileApiManager ClinicVitalManager = new ProfileApiManager(new IResponseReceiver() {
                    @Override
                    public void onSuccess(Object data) {
                        hideProgress();
                        ResponseVital responseVital = (ResponseVital) data;
                        binding.swipeRefreshLayout.setRefreshing(false);
                        if (data != null && responseVital.getClinicVitaldata().size() > 0) {
                            binding.emptyLayout.setVisibility(View.GONE);
                            Collections.reverse(responseVital.getClinicVitaldata());
                            setList(responseVital.getClinicVitaldata());
                        } else {
                            binding.emptyLayout.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull String errorResponse) {
                        hideProgress();
                        binding.swipeRefreshLayout.setRefreshing(false);
                        binding.emptyLayout.setVisibility(View.VISIBLE);
                    }
                }, context);
                ClinicVitalManager.getClinicVitalList(userInfo.getEmail());

                ProfileApiManager deviceVitalManager = new ProfileApiManager(new IResponseReceiver() {
                    @Override
                    public void onSuccess(Object data) {
                        hideProgress();
                        JsonObject responseVital = (JsonObject) data;
                        bp_dataVector=new ArrayList<>();
                        hr_dataVector=new ArrayList<>();
                        weight_dataVector=new ArrayList<>();
                        glucose_dataVector=new ArrayList<>();
                        bo_dataVector=new ArrayList<>();
                        binding.swipeRefreshLayout.setRefreshing(false);
                        if (data != null && responseVital.getAsJsonArray("data").size() > 0) {
                            try {
                                JSONArray jsonArray=new JSONArray(String.valueOf(responseVital.getAsJsonArray("data")));
                                for (int i=0;i<jsonArray.length();i++){
                                    if (jsonArray.getJSONObject(i).getString("device_type").equalsIgnoreCase(BP_DEVICE_TYPE)){
                                        if (jsonArray.getJSONObject(i).getJSONArray("vital_records").length()!=0){
                                            JSONArray jVital_record=jsonArray.getJSONObject(i).getJSONArray("vital_records");
                                            for (int j=0;j<jVital_record.length();j++){
                                                VitalDataBP vitalDataBP=new VitalDataBP();
                                                VitalData vitalDatahr=new VitalData();
                                                String value= jVital_record.getJSONObject(j).getString("value");
                                                String[] values = value.split(":");
                                                vitalDataBP.setBphigh(values[0]);
                                                vitalDataBP.setBplow(values[1]);
                                                vitalDatahr.setValue(values[2]);
                                                vitalDataBP.setEntry_date(DateUtil.formatedDate(jVital_record.getJSONObject(j).getString("entry_date"), SERVER_DATE_FORMAT, DATE_TIME_FORMAT3));
                                                vitalDatahr.setEntry_date(DateUtil.formatedDate(jVital_record.getJSONObject(j).getString("entry_date"), SERVER_DATE_FORMAT, DATE_TIME_FORMAT3));
                                                bp_dataVector.add(vitalDataBP);
                                                hr_dataVector.add(vitalDatahr);
                                            }
                                        }
                                    }
                                    else if (jsonArray.getJSONObject(i).getString("device_type").equalsIgnoreCase(SCALE_DEVICE_TYPE)){
                                        if (jsonArray.getJSONObject(i).getJSONArray("vital_records").length()!=0){
                                            JSONArray jVital_record=jsonArray.getJSONObject(i).getJSONArray("vital_records");
                                            for (int j=0;j<jVital_record.length();j++){
                                                VitalData vitalDataWeight=new VitalData();
                                                String value= jVital_record.getJSONObject(j).getString("value");
                                                vitalDataWeight.setValue(value);
                                                vitalDataWeight.setEntry_date(DateUtil.formatedDate(jVital_record.getJSONObject(j).getString("entry_date"), SERVER_DATE_FORMAT, DATE_TIME_FORMAT3));
                                                weight_dataVector.add(vitalDataWeight);
                                            }
                                        }
                                    }
                                    else if (jsonArray.getJSONObject(i).getString("device_type").equalsIgnoreCase(GLUCOMETER_DEVICE_TYPE)){
                                        if (jsonArray.getJSONObject(i).getJSONArray("vital_records").length()!=0){
                                            JSONArray jVital_record=jsonArray.getJSONObject(i).getJSONArray("vital_records");
                                            for (int j=0;j<jVital_record.length();j++){
                                                VitalData vitalDataGlucose=new VitalData();
                                                String value= jVital_record.getJSONObject(j).getString("value");
                                                vitalDataGlucose.setValue(value);
                                                vitalDataGlucose.setEntry_date(DateUtil.formatedDate(jVital_record.getJSONObject(j).getString("entry_date"), SERVER_DATE_FORMAT, DATE_TIME_FORMAT3));
                                                glucose_dataVector.add(vitalDataGlucose);
                                            }
                                        }
                                    }
                                    else if (jsonArray.getJSONObject(i).getString("device_type").equalsIgnoreCase(PO_DEVICE_TYPE)){
                                        if (jsonArray.getJSONObject(i).getJSONArray("vital_records").length()!=0){
                                            JSONArray jVital_record=jsonArray.getJSONObject(i).getJSONArray("vital_records");
                                            for (int j=0;j<jVital_record.length();j++){
                                                VitalData vitalDataBO=new VitalData();
                                                String value= jVital_record.getJSONObject(j).getString("value");
                                                vitalDataBO.setValue(value);
                                                vitalDataBO.setEntry_date(DateUtil.formatedDate(jVital_record.getJSONObject(j).getString("entry_date"), SERVER_DATE_FORMAT, DATE_TIME_FORMAT3));
                                                bo_dataVector.add(vitalDataBO);
                                            }
                                        }
                                    }
                                }
                                if (weight_dataVector.size()>0){
                                    init_weight_graph(weight_dataVector);
                                }
                                if (bo_dataVector.size()>0){
                                    init_bo_graph(bo_dataVector);
                                }
                                if (hr_dataVector.size()>0){
                                    init_HR_graph(hr_dataVector);
                                }
                                if (glucose_dataVector.size()>0){
                                    init_glucose_graph(glucose_dataVector);
                                }
                                if (bp_dataVector.size()>0){
                                    init_Blood_Pressure_Graph(bp_dataVector);
                                }
                                updateGraph(TYPE.Daily);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else {
                            updateGraph(TYPE.Daily);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull String errorResponse) {
                        hideProgress();
                        binding.swipeRefreshLayout.setRefreshing(false);
                       // binding.emptyLayout.setVisibility(View.VISIBLE);
                        updateGraph(TYPE.Daily);
                    }
                }, context);
                deviceVitalManager.getDeviceVitalList(userInfo.getEmail());
            }

        }
    }
    void setList(List<ClinicVitaldata> data) {
        if (clinicVitalRecycleViewAdapter != null)
            clinicVitalRecycleViewAdapter.setList(data);
    }
    public void init_Blood_Pressure_Graph(ArrayList<VitalDataBP> vector) {
        if (vector != null) {
            bph_dataPoints = soapObject_TO_Array(vector, "bphigh");
            bpl_dataPoints = soapObject_TO_Array(vector, "bplow");
        }
    }

    private int getScreenWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }
    public void init_HR_graph(ArrayList<VitalData> vector) {
        if (vector != null) {
            hr_timestamp = new String[vector.size()];
            String[] data = new String[vector.size()];

            hr_datapoints = vectorToArray2(vector,hr_timestamp);

            /*if (hr_datapoints.length>0){
                Double max=0.0;
                Double min= Double.parseDouble(vector.get(0).getValue());
                for (int i=0;i<vector.size();i++){
                    Double value= Double.parseDouble(vector.get(i).getValue());
                    if (value>max){
                        max=value;
                    }
                    if (value<min){
                        min=value;
                    }
                }
             *//*   Custom_Line customLine = graphData.getdefaultLineData(hr_datapoints, vector.size(), new int[]{colors[1]}, hr_timestamp);

                Log.e("max",""+max);
                Log.e("min",""+min);
                GraphUI.updateVitalData(customLine, binding.heartRateChart);
                binding.heartRateChart.getAxisLeft().setAxisMaximum((int)(max+20));
                binding.heartRateChart.getAxisLeft().setAxisMinimum((int)(min-20));
                binding.heartRateChart.getAxisLeft().setStartAtZero(false);
                binding.heartRateChart.animateX(1000, Easing.EaseInCubic);*//*

            }*/
        }
    }
    public void init_weight_graph(ArrayList<VitalData> vector) {
        if (vector != null) {
            weight_timestamp = new String[vector.size()];
            String[] data = new String[vector.size()];
            weight_datapoints = vectorToArray2(vector,weight_timestamp);
        }
    }
    public void init_bo_graph(ArrayList<VitalData> vector) {
        if (vector != null) {
            bo_timestamp = new String[vector.size()];
            String[] data = new String[vector.size()];
            bo_datapoints = vectorToArray2(vector,bo_timestamp);
        }
    }
    public void init_glucose_graph(ArrayList<VitalData> vector) {
        if (vector != null) {
            glucose_timestamp = new String[vector.size()];
            String[] data = new String[vector.size()];
            glucose_datapoints = vectorToArray2(vector,glucose_timestamp);
        }
    }
    public void updateGraph(String type) {
        binding.heartRateChart.clear();
        binding.boChart.clear();
        binding.weightChart.clear();
        binding.glucoseChart.clear();
        binding.bloodPressureChart.clear();
        switch (type) {
            case TYPE.Daily: {
                GraphUI.setAppearance2(binding.dayBtn, binding.monthBtn, binding.yearBtn, binding.sixmonthBtn, binding.weekBtn);
                SimpleDateFormat format2 = new SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH);

                Calendar calendar = Calendar.getInstance();
                String formattedDate = format2.format(calendar.getTime());
                binding.textWeight.setText(formattedDate);
                binding.textBo.setText(formattedDate);
                binding.textBp.setText(formattedDate);
                binding.textGlucose.setText(formattedDate);
                binding.textHr.setText(formattedDate);

                if (hr_dataVector.size()>0){
                    if (hr_datapoints.length>0){
                        Custom_Line customLine = graphData.getDailyLineData(hr_datapoints, hr_dataVector.size(), new int[]{colors[1]}, hr_timestamp,binding.heartRateChart);
                        GraphUI.updateDailyData(customLine, binding.heartRateChart);
                        binding.heartRateChart.getAxisLeft().setStartAtZero(false);
                        binding.heartRateChart.animateX(1000, Easing.EaseInCubic);
                    }
                }
                if (glucose_dataVector.size()>0){
                    if (glucose_datapoints.length>0){
                        Custom_Line customLine = graphData.getDailyLineData(glucose_datapoints, glucose_dataVector.size(), new int[]{colors[1]}, glucose_timestamp,binding.glucoseChart);
                        GraphUI.updateDailyData(customLine, binding.glucoseChart);
                        binding.glucoseChart.getAxisLeft().setStartAtZero(false);
                        binding.glucoseChart.animateX(1000, Easing.EaseInCubic);
                    }
                }

                if (bo_dataVector.size()>0){
                    if (bo_datapoints.length>0){
                        Custom_Line customLine = graphData.getDailyLineData(bo_datapoints, bo_dataVector.size(), new int[]{colors[1]}, bo_timestamp,binding.boChart);
                        GraphUI.updateDailyData(customLine, binding.boChart);
                        binding.boChart.getAxisLeft().setStartAtZero(false);
                        binding.boChart.animateX(1000, Easing.EaseInCubic);
                    }
                }

                if (weight_dataVector.size()>0){
                    if (weight_datapoints.length>0){
                        Custom_Line customLine = graphData.getDailyLineData(weight_datapoints, weight_dataVector.size(), new int[]{colors[1]}, weight_timestamp,binding.weightChart);
                        GraphUI.updateDailyData(customLine, binding.weightChart);
                        binding.weightChart.getAxisLeft().setStartAtZero(false);
                        binding.weightChart.animateX(1000, Easing.EaseInCubic);
                    }
                }

                if (bp_dataVector.size()>0){
                    if (bpl_dataPoints.length>0){
                        Custom_LineBP customLine = graphData.getdefaultBPData2(bph_dataPoints,bpl_dataPoints, bp_dataVector, new int[]{colors[1], colors[0]}, bp_timestamp,"daily",binding.bloodPressureChart);
                        GraphUI.updateWeekData2(customLine, binding.bloodPressureChart);
                        binding.bloodPressureChart.getAxisLeft().setStartAtZero(false);
                        binding.bloodPressureChart.animateX(1000, Easing.EaseInCubic);
                        binding.bloodPressureChart.invalidate();
                    }
                }
                break;
            }

            case TYPE.WEEKLY: {
                GraphUI.setAppearance2(binding.weekBtn, binding.monthBtn, binding.yearBtn, binding.sixmonthBtn, binding.dayBtn);
                SimpleDateFormat format3 = new SimpleDateFormat("EE dd", Locale.ENGLISH);
                List<Date> daysl=getAllDaysOfTheCurrentWeek();
                String formattedDate = format3.format(daysl.get(0));
                String formattedDate2 = format3.format(daysl.get(daysl.size()-1));


                binding.textWeight.setText(formattedDate+" - "+formattedDate2);
                binding.textBo.setText(formattedDate+" - "+formattedDate2);
                binding.textBp.setText(formattedDate+" - "+formattedDate2);
                binding.textGlucose.setText(formattedDate+" - "+formattedDate2);
                binding.textHr.setText(formattedDate+" - "+formattedDate2);
              if (hr_dataVector.size()>0){
                  List<VitalData> hr_data=new ArrayList<>();
                  Map<String, Double> dateToSum = new HashMap<>();
                  Map<String, Integer> dateToCount = new HashMap<>();

                  for (int l=0;l<hr_dataVector.size();l++){
                      VitalData vitalData=new VitalData();
                      vitalData.setValue(hr_dataVector.get(l).getValue());
                      vitalData.setEntry_date(DateUtil.formatedDate(hr_dataVector.get(l).getEntry_date(),DATE_TIME_FORMAT3,DATE_TIME_FORMAT4));
                      hr_data.add(vitalData);
                  }

                  for (VitalData entry : hr_data) {
                      String date = entry.getEntry_date();
                      double value = Double.parseDouble(entry.getValue());

                      if (dateToSum.equals(date)) {
                          dateToSum.put(date, dateToSum.get(date) + value);
                          dateToCount.put(date, dateToCount.get(date) + 1);
                      } else {
                          dateToSum.put(date, value);
                          dateToCount.put(date, 1);
                      }
                  }

                  Map<String, Double> dateToAverage = new HashMap<>();
                  for (Map.Entry<String, Double> entry : dateToSum.entrySet()) {
                      String date = entry.getKey();
                      double sum = entry.getValue();
                      int count = dateToCount.get(date);
                      double average = sum / count;
                      dateToAverage.put(date, average);
                  }
                  List<VitalData> newHr_data = new ArrayList<>();

                  for (Map.Entry<String, Double> entry : dateToAverage.entrySet()) {
                      String date = entry.getKey();
                      String average = String.valueOf(entry.getValue());
                      VitalData vitalData=new VitalData();
                      vitalData.setValue(average);
                      vitalData.setEntry_date(date);
                      newHr_data.add(vitalData);
                  }
                  if (newHr_data.size()>0){
                      String[] newhr_datapoints=new String[newHr_data.size()];
                      String[] newhr_timestamp=new String[newHr_data.size()];

                      for (int i=0;i<newHr_data.size();i++){
                          newhr_datapoints[i]=newHr_data.get(i).getValue();
                          newhr_timestamp[i]=DateUtil.formatedDate(newHr_data.get(i).getEntry_date(),DATE_TIME_FORMAT4,DATE_TIME_FORMAT3);
                      }
                      Custom_Line customLine = graphData.getWeekLineData2(newhr_datapoints, newHr_data.size(), new int[]{colors[1]}, newhr_timestamp,binding.heartRateChart);
                      GraphUI.updateWeekData(customLine, binding.heartRateChart);
                      binding.heartRateChart.getAxisLeft().setStartAtZero(false);
                      binding.heartRateChart.animateX(1000, Easing.EaseInCubic);
                  }
              }

                if (glucose_dataVector.size()>0){
                    List<VitalData> glocose_data=new ArrayList<>();
                    Map<String, Double> dateToSum = new HashMap<>();
                    Map<String, Integer> dateToCount = new HashMap<>();

                    for (int l=0;l<glucose_dataVector.size();l++){
                        VitalData vitalData=new VitalData();
                        vitalData.setValue(glucose_dataVector.get(l).getValue());
                        vitalData.setEntry_date(DateUtil.formatedDate(glucose_dataVector.get(l).getEntry_date(),DATE_TIME_FORMAT3,DATE_TIME_FORMAT4));
                        glocose_data.add(vitalData);
                    }

                    for (VitalData entry : glocose_data) {
                        String date = entry.getEntry_date();
                        double value = Double.parseDouble(entry.getValue());

                        if (dateToSum.equals(date)) {
                            dateToSum.put(date, dateToSum.get(date) + value);
                            dateToCount.put(date, dateToCount.get(date) + 1);
                        } else {
                            dateToSum.put(date, value);
                            dateToCount.put(date, 1);
                        }
                    }

                    Map<String, Double> dateToAverage = new HashMap<>();
                    for (Map.Entry<String, Double> entry : dateToSum.entrySet()) {
                        String date = entry.getKey();
                        double sum = entry.getValue();
                        int count = dateToCount.get(date);
                        double average = sum / count;
                        dateToAverage.put(date, average);
                    }
                    List<VitalData> newGl_data = new ArrayList<>();

                    for (Map.Entry<String, Double> entry : dateToAverage.entrySet()) {
                        String date = entry.getKey();
                        String average = String.valueOf(entry.getValue());
                        VitalData vitalData=new VitalData();
                        vitalData.setValue(average);
                        vitalData.setEntry_date(date);
                        newGl_data.add(vitalData);
                    }
                    if (newGl_data.size()>0){
                        String[] newgl_datapoints=new String[newGl_data.size()];
                        String[] newgl_timestamp=new String[newGl_data.size()];

                        for (int i=0;i<newGl_data.size();i++){
                            newgl_datapoints[i]=newGl_data.get(i).getValue();
                            newgl_timestamp[i]=DateUtil.formatedDate(newGl_data.get(i).getEntry_date(),DATE_TIME_FORMAT4,DATE_TIME_FORMAT3);
                        }
                        Custom_Line customLine = graphData.getWeekLineData2(newgl_datapoints, newGl_data.size(), new int[]{colors[1]}, newgl_timestamp,binding.glucoseChart);
                        GraphUI.updateWeekData(customLine, binding.glucoseChart);
                        binding.glucoseChart.getAxisLeft().setStartAtZero(false);
                        binding.glucoseChart.animateX(1000, Easing.EaseInCubic);
                    }
                }


                /*if (bo_dataVector.size()>0){
                    if (bo_datapoints.length>0){
                        Double max=0.0;
                        Double min= Double.parseDouble(bo_datapoints[0]);
                        for (int i=0;i<bo_datapoints.length;i++){
                            Double value= Double.parseDouble(bo_datapoints[i]);
                            if (value>max){
                                max=value;
                            }
                            if (value<min){
                                min=value;
                            }
                        }

                        Custom_Line customLine = graphData.getWeekLineData(bo_datapoints, bo_dataVector.size(), new int[]{colors[1]}, bo_timestamp);
                        GraphUI.updateWeekData(customLine, binding.boChart);
                        binding.boChart.getAxisLeft().setStartAtZero(false);
                        binding.boChart.getAxisLeft().setAxisMaximum((float)(max+20));
                        binding.boChart.getAxisLeft().setAxisMinimum((float)(min-20));
                        binding.boChart.animateX(1000, Easing.EaseInCubic);
                    }
                }
*/


                if (weight_dataVector.size()>0){
                    List<VitalData> weight_data=new ArrayList<>();
                    Map<String, Double> dateToSum = new HashMap<>();
                    Map<String, Integer> dateToCount = new HashMap<>();

                    for (int l=0;l<weight_dataVector.size();l++){
                        VitalData vitalData=new VitalData();
                        vitalData.setValue(weight_dataVector.get(l).getValue());
                        vitalData.setEntry_date(DateUtil.formatedDate(weight_dataVector.get(l).getEntry_date(),DATE_TIME_FORMAT3,DATE_TIME_FORMAT4));
                        weight_data.add(vitalData);
                    }

                    for (VitalData entry : weight_data) {
                        String date = entry.getEntry_date();
                        double value = Double.parseDouble(entry.getValue());

                        if (dateToSum.equals(date)) {
                            dateToSum.put(date, dateToSum.get(date) + value);
                            dateToCount.put(date, dateToCount.get(date) + 1);
                        } else {
                            dateToSum.put(date, value);
                            dateToCount.put(date, 1);
                        }
                    }

                    Map<String, Double> dateToAverage = new HashMap<>();
                    for (Map.Entry<String, Double> entry : dateToSum.entrySet()) {
                        String date = entry.getKey();
                        double sum = entry.getValue();
                        int count = dateToCount.get(date);
                        double average = sum / count;
                        dateToAverage.put(date, average);
                    }
                    List<VitalData> newweiht_data = new ArrayList<>();

                    for (Map.Entry<String, Double> entry : dateToAverage.entrySet()) {
                        String date = entry.getKey();
                        String average = String.valueOf(entry.getValue());
                        VitalData vitalData=new VitalData();
                        vitalData.setValue(average);
                        vitalData.setEntry_date(date);
                        newweiht_data.add(vitalData);
                    }
                    if (newweiht_data.size()>0){
                        String[] newweight_datapoints=new String[newweiht_data.size()];
                        String[] newweight_timestamp=new String[newweiht_data.size()];

                        for (int i=0;i<newweiht_data.size();i++){
                            newweight_datapoints[i]=newweiht_data.get(i).getValue();
                            newweight_timestamp[i]=DateUtil.formatedDate(newweiht_data.get(i).getEntry_date(),DATE_TIME_FORMAT4,DATE_TIME_FORMAT3);
                        }
                        Custom_Line customLine = graphData.getWeekLineData2(newweight_datapoints, newweiht_data.size(), new int[]{colors[1]}, newweight_timestamp,binding.weightChart);
                        GraphUI.updateWeekData(customLine, binding.weightChart);
                        binding.weightChart.getAxisLeft().setStartAtZero(false);
                        binding.weightChart.animateX(1000, Easing.EaseInCubic);
                    }
                }
               /* if (bp_dataVector.size()>0){
                    if (bpl_dataPoints.length>0){
                        Double max=0.0;
                        Double min= Double.parseDouble(bp_dataVector.get(0).getBplow());
                        for (int i=0;i<bp_dataVector.size();i++){
                            Double value= Double.parseDouble(bp_dataVector.get(i).getBphigh());
                            Double value2= Double.parseDouble(bp_dataVector.get(i).getBplow());
                            if (value>max){
                                max=value;
                            }
                            if (value2<min){
                                min=value2;
                            }
                        }
                        Custom_LineBP customLine = graphData.getdefaultBPData2(bph_dataPoints,bpl_dataPoints, bp_dataVector, new int[]{colors[1], colors[0]}, hr_timestamp,"weekly");
                        GraphUI.updateWeekData2(customLine, binding.bloodPressureChart);
                        binding.bloodPressureChart.getAxisLeft().setStartAtZero(false);
                        binding.bloodPressureChart.getAxisLeft().setAxisMaximum((float)(max+20));
                        binding.bloodPressureChart.getAxisLeft().setAxisMinimum((float)(min-20));
                        binding.bloodPressureChart.animateX(1000, Easing.EaseInCubic);

                    }
                }*/
                if (bp_dataVector.size()>0){
                    List<VitalDataBP> bp_data=new ArrayList<>();
                   /* Map<String, Double> dateToSum = new HashMap<>();
                    Map<String, Integer> dateToCount = new HashMap<>();*/
                    Map<String, List<Double>> dateTohigh = new HashMap<>();
                    Map<String, List<Double>> dateTolow = new HashMap<>();
                    List<Date> days=getAllDaysOfTheCurrentWeek();
                    SimpleDateFormat format2 = new SimpleDateFormat("EEE MMM dd yyyy", Locale.ENGLISH);
                    for (int m=0;m<days.size();m++){
                        for (int l=0;l<bp_dataVector.size();l++){
                            String days1=format2.format(days.get(m));
                            String days2=DateUtil.formatedDate(bp_dataVector.get(l).getEntry_date(),DATE_TIME_FORMAT3,DATE_TIME_FORMAT5);
                            if (days1.equalsIgnoreCase(days2)){
                                VitalDataBP vitalData=new VitalDataBP();
                                vitalData.setBplow(bp_dataVector.get(l).getBplow());
                                vitalData.setBphigh(bp_dataVector.get(l).getBphigh());
                                vitalData.setEntry_date(DateUtil.formatedDate(bp_dataVector.get(l).getEntry_date(),DATE_TIME_FORMAT3,DATE_TIME_FORMAT4));
                                bp_data.add(vitalData);
                            }
                        }
                    }

                    for (VitalDataBP entry : bp_data) {
                        String date = entry.getEntry_date();
                        double value = Double.parseDouble(entry.getBphigh());
                        double value2 = Double.parseDouble(entry.getBplow());

                        if (dateTohigh.containsKey(date)) {
                            dateTohigh.get(date).add(value);
                            dateTolow.get(date).add(value2);
                        } else {
                            List<Double> systolicList = new ArrayList<>();
                            systolicList.add(value);
                            dateTohigh.put(date, systolicList);

                            List<Double> diastolicList = new ArrayList<>();
                            diastolicList.add(value2);
                            dateTolow.put(date, diastolicList);
                        }
                    }

                    Map<String, Double> dateToAverageSystolic = new HashMap<>();
                    Map<String, Double> dateToAverageDiastolic = new HashMap<>();

                    for (Map.Entry<String, List<Double>> entry : dateTohigh.entrySet()) {
                        String date = entry.getKey();
                        List<Double> systolicValues = entry.getValue();
                        List<Double> diastolicValues = dateTolow.get(date);

                       /* double averageSystolic = 0.0;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            averageSystolic = systolicValues.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                        }
                        double averageDiastolic = 0.0;
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            averageDiastolic = diastolicValues.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                        }

                        dateToAverageSystolic.put(date, averageSystolic);
                        dateToAverageDiastolic.put(date, averageDiastolic);*/
                        double sumSystolic = 0.0;
                        for (Double systolic : systolicValues) {
                            sumSystolic += systolic;
                        }

                        double sumDiastolic = 0.0;
                        for (Double diastolic : diastolicValues) {
                            sumDiastolic += diastolic;
                        }

                        double averageSystolic = systolicValues.size() > 0 ? sumSystolic / systolicValues.size() : 0.0;
                        double averageDiastolic = diastolicValues.size() > 0 ? sumDiastolic / diastolicValues.size() : 0.0;

                        dateToAverageSystolic.put(date, averageSystolic);
                        dateToAverageDiastolic.put(date, averageDiastolic);
                    }


                    List<VitalDataBP> newbp_data = new ArrayList<>();
                    ArrayList<VitalDataBP> newbp_data2 = new ArrayList<>();

                    for (String date : dateToAverageSystolic.keySet()) {
                        double averageSystolic = dateToAverageSystolic.get(date);
                        double averageDiastolic = dateToAverageDiastolic.get(date);
                        VitalDataBP vitalData=new VitalDataBP();
                        vitalData.setBplow(String.valueOf(averageDiastolic));
                        vitalData.setBphigh(String.valueOf(averageSystolic));
                        vitalData.setEntry_date(date);
                        newbp_data.add(vitalData);
                        newbp_data2.add(vitalData);
                    }
                    if (newbp_data.size()>0){
                        String[] newbp_timestamp=new String[newbp_data.size()];
                        String[] newbph_dataPoints=new String[newbp_data.size()];
                        String[] newbpl_dataPoints=new String[newbp_data.size()];
                        for (int i=0;i<newbp_data.size();i++){
                            newbph_dataPoints[i]=newbp_data.get(i).getBphigh();
                            newbpl_dataPoints[i]=newbp_data.get(i).getBplow();
                            newbp_timestamp[i]=newbp_data.get(i).getEntry_date();
                        }

                        Custom_LineBP customLine = graphData.getdefaultBPData2(newbph_dataPoints,newbpl_dataPoints, newbp_data2, new int[]{colors[1], colors[0]}, newbp_timestamp,"weekly",binding.bloodPressureChart);
                        GraphUI.updateWeekData2(customLine, binding.bloodPressureChart);
                        binding.bloodPressureChart.getAxisLeft().setStartAtZero(false);
                        binding.bloodPressureChart.animateX(1000, Easing.EaseInCubic);
                    }
                }

                if (bo_dataVector.size()>0){
                    List<VitalData> bo_data=new ArrayList<>();
                    Map<String, Double> dateToSum = new HashMap<>();
                    Map<String, Integer> dateToCount = new HashMap<>();

                    for (int l=0;l<bo_dataVector.size();l++){
                        VitalData vitalData=new VitalData();
                        vitalData.setValue(bo_dataVector.get(l).getValue());
                        vitalData.setEntry_date(DateUtil.formatedDate(bo_dataVector.get(l).getEntry_date(),DATE_TIME_FORMAT3,DATE_TIME_FORMAT4));
                        bo_data.add(vitalData);
                    }

                    for (VitalData entry : bo_data) {
                        String date = entry.getEntry_date();
                        double value = Double.parseDouble(entry.getValue());

                        if (dateToSum.equals(date)) {
                            dateToSum.put(date, dateToSum.get(date) + value);
                            dateToCount.put(date, dateToCount.get(date) + 1);
                        } else {
                            dateToSum.put(date, value);
                            dateToCount.put(date, 1);
                        }
                    }

                    Map<String, Double> dateToAverage = new HashMap<>();
                    for (Map.Entry<String, Double> entry : dateToSum.entrySet()) {
                        String date = entry.getKey();
                        double sum = entry.getValue();
                        int count = dateToCount.get(date);
                        double average = sum / count;
                        dateToAverage.put(date, average);
                    }
                    List<VitalData> newbo_data = new ArrayList<>();

                    for (Map.Entry<String, Double> entry : dateToAverage.entrySet()) {
                        String date = entry.getKey();
                        String average = String.valueOf(entry.getValue());
                        VitalData vitalData=new VitalData();
                        vitalData.setValue(average);
                        vitalData.setEntry_date(date);
                        newbo_data.add(vitalData);
                    }
                    if (newbo_data.size()>0){
                        String[] newbo_datapoints=new String[newbo_data.size()];
                        String[] newbo_timestamp=new String[newbo_data.size()];

                        for (int i=0;i<newbo_data.size();i++){
                            newbo_datapoints[i]=newbo_data.get(i).getValue();
                            newbo_timestamp[i]=DateUtil.formatedDate(newbo_data.get(i).getEntry_date(),DATE_TIME_FORMAT4,DATE_TIME_FORMAT3);
                        }

                        Custom_Line customLine = graphData.getWeekLineData2(newbo_datapoints, newbo_data.size(), new int[]{colors[1]}, newbo_timestamp,binding.boChart);
                        GraphUI.updateWeekData(customLine,binding.boChart);
                        binding.boChart.getAxisLeft().setStartAtZero(false);
                        binding.boChart.animateX(1000, Easing.EaseInCubic);
                    }
                }
                break;
            }
           case TYPE.MONTHLY: {
                GraphUI.setAppearance2(binding.monthBtn, binding.yearBtn, binding.weekBtn, binding.sixmonthBtn, binding.dayBtn);
            /*    switch (value) {
                    case PO_DEVICE_TYPE:
                        Custom_Line customLine = graphData.getMonthLineData(hr_datapoints, hr_dataVector.size(), new int[]{colors[1]}, hr_timestamp);
                        GraphUI.updateMonthData(binding.heartRateChart, customLine);
                        custom = graphData.getMonthData(bo_datapoints, bo_dataVector.size(), new int[]{colors[3]}, bo_timestamp);
                        GraphUI.updateMonthData(binding.bloodOxygenChart, custom);
                        break;
                    case GLUCOMETER_DEVICE_TYPE:
                        customLine = graphData.getMonthLineData(glucose_datapoints, glucose_dataVector.size(), new int[]{colors[4]}, glucose_timestamp);
                        GraphUI.updateMonthData(binding.GlucoseChart, customLine);
                        break;
                    case BP_DEVICE_TYPE:
                        custom = graphData.getMonthBPData(bph_dataPoints, bpl_dataPoints, bp_dataVector, new int[]{colors[5], colors[0]}, bp_timestamp);
                        GraphUI.updateMonthData(binding.bloodPressureChart, custom);
                        break;
                    case SCALE_DEVICE_TYPE:
                        custom = graphData.getMonthData(weight_datapoints, weight_dataVector.size(), new int[]{colors[2]}, weight_timestamp);
                        GraphUI.updateMonthData(binding.WeightChart, custom);
                        break;
                    case "":
                        customLine = graphData.getMonthLineData(hr_datapoints, hr_dataVector.size(), new int[]{colors[1]}, hr_timestamp);
                        GraphUI.updateMonthData(binding.heartRateChart, customLine);
                        custom = graphData.getMonthData(bo_datapoints, bo_dataVector.size(), new int[]{colors[3]}, bo_timestamp);
                        GraphUI.updateMonthData(binding.bloodOxygenChart, custom);
                        customLine = graphData.getMonthLineData(glucose_datapoints, glucose_dataVector.size(), new int[]{colors[4]}, glucose_timestamp);
                        GraphUI.updateMonthData(binding.GlucoseChart, customLine);
                        custom = graphData.getMonthBPData(bph_dataPoints, bpl_dataPoints, bp_dataVector, new int[]{colors[5], colors[0]}, bp_timestamp);
                        GraphUI.updateMonthData(binding.bloodPressureChart, custom);
                }*/
               List<Date> daysl=getAllDaysOfTheCurrentMonth();
               SimpleDateFormat format3 = new SimpleDateFormat("dd MMM", Locale.ENGLISH);
               SimpleDateFormat format4 = new SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH);
               String formattedDate = format3.format(daysl.get(0));
               String formattedDate2 = format4.format(daysl.get(daysl.size()-1));


               binding.textWeight.setText(formattedDate+" - "+formattedDate2);
               binding.textBo.setText(formattedDate+" - "+formattedDate2);
               binding.textBp.setText(formattedDate+" - "+formattedDate2);
               binding.textGlucose.setText(formattedDate+" - "+formattedDate2);
               binding.textHr.setText(formattedDate+" - "+formattedDate2);
               if (hr_dataVector.size()>0){
                   List<VitalData> hr_data=new ArrayList<>();
                   Map<String, Double> dateToSum = new HashMap<>();
                   Map<String, Integer> dateToCount = new HashMap<>();

                   for (int l=0;l<hr_dataVector.size();l++){
                       VitalData vitalData=new VitalData();
                       vitalData.setValue(hr_dataVector.get(l).getValue());
                       vitalData.setEntry_date(DateUtil.formatedDate(hr_dataVector.get(l).getEntry_date(),DATE_TIME_FORMAT3,DATE_TIME_FORMAT4));
                       hr_data.add(vitalData);
                   }

                   for (VitalData entry : hr_data) {
                       String date = entry.getEntry_date();
                       double value = Double.parseDouble(entry.getValue());

                       if (dateToSum.equals(date)) {
                           dateToSum.put(date, dateToSum.get(date) + value);
                           dateToCount.put(date, dateToCount.get(date) + 1);
                       } else {
                           dateToSum.put(date, value);
                           dateToCount.put(date, 1);
                       }
                   }

                   Map<String, Double> dateToAverage = new HashMap<>();
                   for (Map.Entry<String, Double> entry : dateToSum.entrySet()) {
                       String date = entry.getKey();
                       double sum = entry.getValue();
                       int count = dateToCount.get(date);
                       double average = sum / count;
                       dateToAverage.put(date, average);
                   }
                   List<VitalData> newHr_data = new ArrayList<>();

                   for (Map.Entry<String, Double> entry : dateToAverage.entrySet()) {
                       String date = entry.getKey();
                       String average = String.valueOf(entry.getValue());
                       VitalData vitalData=new VitalData();
                       vitalData.setValue(average);
                       vitalData.setEntry_date(date);
                       newHr_data.add(vitalData);
                   }
                   if (newHr_data.size()>0){
                       String[] newhr_datapoints=new String[newHr_data.size()];
                       String[] newhr_timestamp=new String[newHr_data.size()];

                       for (int i=0;i<newHr_data.size();i++){
                           newhr_datapoints[i]=newHr_data.get(i).getValue();
                           newhr_timestamp[i]=DateUtil.formatedDate(newHr_data.get(i).getEntry_date(),DATE_TIME_FORMAT4,DATE_TIME_FORMAT3);
                       }
                       Custom_Line customLine = graphData.getMonthLineData2(newhr_datapoints, newHr_data.size(), new int[]{colors[1]}, newhr_timestamp,binding.heartRateChart);
                       GraphUI.updateMonthData2(binding.heartRateChart,customLine);
                       binding.heartRateChart.getAxisLeft().setStartAtZero(false);
                       binding.heartRateChart.animateX(1000, Easing.EaseInCubic);
                   }
               }

               if (glucose_dataVector.size()>0){
                   List<VitalData> glocose_data=new ArrayList<>();
                   Map<String, Double> dateToSum = new HashMap<>();
                   Map<String, Integer> dateToCount = new HashMap<>();

                   for (int l=0;l<glucose_dataVector.size();l++){
                       VitalData vitalData=new VitalData();
                       vitalData.setValue(glucose_dataVector.get(l).getValue());
                       vitalData.setEntry_date(DateUtil.formatedDate(glucose_dataVector.get(l).getEntry_date(),DATE_TIME_FORMAT3,DATE_TIME_FORMAT4));
                       glocose_data.add(vitalData);
                   }

                   for (VitalData entry : glocose_data) {
                       String date = entry.getEntry_date();
                       double value = Double.parseDouble(entry.getValue());

                       if (dateToSum.equals(date)) {
                           dateToSum.put(date, dateToSum.get(date) + value);
                           dateToCount.put(date, dateToCount.get(date) + 1);
                       } else {
                           dateToSum.put(date, value);
                           dateToCount.put(date, 1);
                       }
                   }

                   Map<String, Double> dateToAverage = new HashMap<>();
                   for (Map.Entry<String, Double> entry : dateToSum.entrySet()) {
                       String date = entry.getKey();
                       double sum = entry.getValue();
                       int count = dateToCount.get(date);
                       double average = sum / count;
                       dateToAverage.put(date, average);
                   }
                   List<VitalData> newGl_data = new ArrayList<>();

                   for (Map.Entry<String, Double> entry : dateToAverage.entrySet()) {
                       String date = entry.getKey();
                       String average = String.valueOf(entry.getValue());
                       VitalData vitalData=new VitalData();
                       vitalData.setValue(average);
                       vitalData.setEntry_date(date);
                       newGl_data.add(vitalData);
                   }
                   if (newGl_data.size()>0){
                       String[] newgl_datapoints=new String[newGl_data.size()];
                       String[] newgl_timestamp=new String[newGl_data.size()];

                       for (int i=0;i<newGl_data.size();i++){
                           newgl_datapoints[i]=newGl_data.get(i).getValue();
                           newgl_timestamp[i]=DateUtil.formatedDate(newGl_data.get(i).getEntry_date(),DATE_TIME_FORMAT4,DATE_TIME_FORMAT3);
                       }
                       Custom_Line customLine = graphData.getMonthLineData2(newgl_datapoints, newGl_data.size(), new int[]{colors[1]}, newgl_timestamp, binding.glucoseChart);
                       GraphUI.updateMonthData2(binding.glucoseChart,customLine);
                       binding.glucoseChart.getAxisLeft().setStartAtZero(false);
                       binding.glucoseChart.animateX(1000, Easing.EaseInCubic);
                   }
               }

               /*if (bo_dataVector.size()>0){
                   if (bo_datapoints.length>0){
                       Double max=0.0;
                       Double min= Double.parseDouble(bo_datapoints[0]);
                       for (int i=0;i<bo_datapoints.length;i++){
                           Double value= Double.parseDouble(bo_datapoints[i]);
                           if (value>max){
                               max=value;
                           }
                           if (value<min){
                               min=value;
                           }
                       }
                       Custom_Line customLine = graphData.getMonthLineData(bo_datapoints, bo_dataVector.size(), new int[]{colors[1]}, bo_timestamp);
                       GraphUI.updateMonthData2(binding.boChart,customLine);
                       binding.boChart.getAxisLeft().setStartAtZero(false);
                       binding.boChart.getAxisLeft().setAxisMaximum((float)(max+20));
                       binding.boChart.getAxisLeft().setAxisMinimum((float)(min-20));
                       binding.boChart.animateX(1000, Easing.EaseInCubic);
                   }
               }*/
               if (weight_dataVector.size()>0){
                   List<VitalData> weight_data=new ArrayList<>();
                   Map<String, Double> dateToSum = new HashMap<>();
                   Map<String, Integer> dateToCount = new HashMap<>();

                   for (int l=0;l<weight_dataVector.size();l++){
                       VitalData vitalData=new VitalData();
                       vitalData.setValue(weight_dataVector.get(l).getValue());
                       vitalData.setEntry_date(DateUtil.formatedDate(weight_dataVector.get(l).getEntry_date(),DATE_TIME_FORMAT3,DATE_TIME_FORMAT4));
                       weight_data.add(vitalData);
                   }

                   for (VitalData entry : weight_data) {
                       String date = entry.getEntry_date();
                       double value = Double.parseDouble(entry.getValue());

                       if (dateToSum.equals(date)) {
                           dateToSum.put(date, dateToSum.get(date) + value);
                           dateToCount.put(date, dateToCount.get(date) + 1);
                       } else {
                           dateToSum.put(date, value);
                           dateToCount.put(date, 1);
                       }
                   }

                   Map<String, Double> dateToAverage = new HashMap<>();
                   for (Map.Entry<String, Double> entry : dateToSum.entrySet()) {
                       String date = entry.getKey();
                       double sum = entry.getValue();
                       int count = dateToCount.get(date);
                       double average = sum / count;
                       dateToAverage.put(date, average);
                   }
                   List<VitalData> newweiht_data = new ArrayList<>();

                   for (Map.Entry<String, Double> entry : dateToAverage.entrySet()) {
                       String date = entry.getKey();
                       String average = String.valueOf(entry.getValue());
                       VitalData vitalData=new VitalData();
                       vitalData.setValue(average);
                       vitalData.setEntry_date(date);
                       newweiht_data.add(vitalData);
                   }
                   if (newweiht_data.size()>0){
                       String[] newweight_datapoints=new String[newweiht_data.size()];
                       String[] newweight_timestamp=new String[newweiht_data.size()];

                       for (int i=0;i<newweiht_data.size();i++){
                           newweight_datapoints[i]=newweiht_data.get(i).getValue();
                           newweight_timestamp[i]=DateUtil.formatedDate(newweiht_data.get(i).getEntry_date(),DATE_TIME_FORMAT4,DATE_TIME_FORMAT3);
                       }

                       Custom_Line customLine = graphData.getMonthLineData2(newweight_datapoints, newweiht_data.size(), new int[]{colors[1]}, newweight_timestamp,binding.weightChart);
                       GraphUI.updateMonthData2( binding.weightChart,customLine);
                       binding.weightChart.getAxisLeft().setStartAtZero(false);
                       binding.weightChart.animateX(1000, Easing.EaseInCubic);
                   }
               }


             /*  if (bp_dataVector.size()>0){
                   if (bpl_dataPoints.length>0){
                       Double max=0.0;
                       Double min= Double.parseDouble(bp_dataVector.get(0).getBplow());
                       for (int i=0;i<bp_dataVector.size();i++){
                           Double value= Double.parseDouble(bp_dataVector.get(i).getBphigh());
                           Double value2= Double.parseDouble(bp_dataVector.get(i).getBplow());
                           if (value>max){
                               max=value;
                           }
                           if (value2<min){
                               min=value2;
                           }
                       }
                       Custom_LineBP customLine = graphData.getdefaultBPData2(bph_dataPoints,bpl_dataPoints, bp_dataVector, new int[]{colors[1], colors[0]}, hr_timestamp,"monthly");
                       GraphUI.updateWeekData2(customLine, binding.bloodPressureChart);
                       binding.bloodPressureChart.getAxisLeft().setStartAtZero(false);
                       binding.bloodPressureChart.getAxisLeft().setAxisMaximum((float)(max+20));
                       binding.bloodPressureChart.getAxisLeft().setAxisMinimum((float)(min-20));
                       binding.bloodPressureChart.animateX(1000, Easing.EaseInCubic);

                   }
               }*/
               if (bp_dataVector.size()>0){
                   List<VitalDataBP> bp_data=new ArrayList<>();
                   /* Map<String, Double> dateToSum = new HashMap<>();
                    Map<String, Integer> dateToCount = new HashMap<>();*/
                   Map<String, List<Double>> dateTohigh = new HashMap<>();
                   Map<String, List<Double>> dateTolow = new HashMap<>();
                   /*for (int l=0;l<bp_dataVector.size();l++){
                       VitalDataBP vitalData=new VitalDataBP();
                       vitalData.setBplow(bp_dataVector.get(l).getBplow());
                       vitalData.setBphigh(bp_dataVector.get(l).getBphigh());
                       vitalData.setEntry_date(DateUtil.formatedDate(bp_dataVector.get(l).getEntry_date(),DATE_TIME_FORMAT3,DATE_TIME_FORMAT4));
                       bp_data.add(vitalData);
                   }*/

                   List<Date> days=getAllDaysOfTheCurrentMonth();
                   SimpleDateFormat format2 = new SimpleDateFormat("EEE MMM dd yyyy", Locale.ENGLISH);
                   for (int m=0;m<days.size();m++){
                       for (int l=0;l<bp_dataVector.size();l++){
                           String days1=format2.format(days.get(m));
                           String days2=DateUtil.formatedDate(bp_dataVector.get(l).getEntry_date(),DATE_TIME_FORMAT3,DATE_TIME_FORMAT5);
                           if (days1.equals(days2)){
                               VitalDataBP vitalData=new VitalDataBP();
                               vitalData.setBplow(bp_dataVector.get(l).getBplow());
                               vitalData.setBphigh(bp_dataVector.get(l).getBphigh());
                               vitalData.setEntry_date(DateUtil.formatedDate(bp_dataVector.get(l).getEntry_date(),DATE_TIME_FORMAT3,DATE_TIME_FORMAT4));
                               bp_data.add(vitalData);
                           }
                       }
                   }
                   for (VitalDataBP entry : bp_data) {
                       String date = entry.getEntry_date();
                       double value = Double.parseDouble(entry.getBphigh());
                       double value2 = Double.parseDouble(entry.getBplow());
                       if (dateTohigh.containsKey(date)) {
                           dateTohigh.get(date).add(value);
                           dateTolow.get(date).add(value2);
                       } else {
                           List<Double> systolicList = new ArrayList<>();
                           systolicList.add(value);
                           dateTohigh.put(date, systolicList);

                           List<Double> diastolicList = new ArrayList<>();
                           diastolicList.add(value2);
                           dateTolow.put(date, diastolicList);
                       }
                   }

                   Map<String, Double> dateToAverageSystolic = new HashMap<>();
                   Map<String, Double> dateToAverageDiastolic = new HashMap<>();
                   for (Map.Entry<String, List<Double>> entry : dateTohigh.entrySet()) {
                       String date = entry.getKey();
                       List<Double> systolicValues = entry.getValue();
                       List<Double> diastolicValues = dateTolow.get(date);

                       double sumSystolic = 0.0;
                       for (Double systolic : systolicValues) {
                           sumSystolic += systolic;
                       }

                       double sumDiastolic = 0.0;
                       for (Double diastolic : diastolicValues) {
                           sumDiastolic += diastolic;
                       }

                       double averageSystolic = systolicValues.size() > 0 ? sumSystolic / systolicValues.size() : 0.0;
                       double averageDiastolic = diastolicValues.size() > 0 ? sumDiastolic / diastolicValues.size() : 0.0;

                       dateToAverageSystolic.put(date, averageSystolic);
                       dateToAverageDiastolic.put(date, averageDiastolic);
                   }

                   List<VitalDataBP> newbp_data = new ArrayList<>();
                   ArrayList<VitalDataBP> newbp_data2 = new ArrayList<>();

                   for (String date : dateToAverageSystolic.keySet()) {
                       double averageSystolic = dateToAverageSystolic.get(date);
                       double averageDiastolic = dateToAverageDiastolic.get(date);
                       VitalDataBP vitalData=new VitalDataBP();
                       vitalData.setBplow(String.valueOf(averageDiastolic));
                       vitalData.setBphigh(String.valueOf(averageSystolic));
                       vitalData.setEntry_date(date);
                       newbp_data.add(vitalData);
                       newbp_data2.add(vitalData);
                   }
                   if (newbp_data.size()>0){
                       String[] newbp_timestamp=new String[newbp_data.size()];
                       String[] newbph_dataPoints=new String[newbp_data.size()];
                       String[] newbpl_dataPoints=new String[newbp_data.size()];
                       for (int i=0;i<newbp_data.size();i++){
                           newbph_dataPoints[i]=newbp_data.get(i).getBphigh();
                           newbpl_dataPoints[i]=newbp_data.get(i).getBplow();
                           newbp_timestamp[i]=newbp_data.get(i).getEntry_date();
                       }

                       Custom_LineBP customLine = graphData.getdefaultBPData2(newbph_dataPoints,newbpl_dataPoints, newbp_data2, new int[]{colors[1], colors[0]}, newbp_timestamp,"monthly",binding.bloodPressureChart);
                       GraphUI.updateWeekData2(customLine, binding.bloodPressureChart);
                       binding.bloodPressureChart.getAxisLeft().setStartAtZero(false);
                       binding.bloodPressureChart.animateX(1000, Easing.EaseInCubic);
                       binding.bloodPressureChart.invalidate();
                   }
               }

               if (bo_dataVector.size()>0){
                   List<VitalData> bo_data=new ArrayList<>();
                   Map<String, Double> dateToSum = new HashMap<>();
                   Map<String, Integer> dateToCount = new HashMap<>();

                   for (int l=0;l<bo_dataVector.size();l++){
                       VitalData vitalData=new VitalData();
                       vitalData.setValue(bo_dataVector.get(l).getValue());
                       vitalData.setEntry_date(DateUtil.formatedDate(bo_dataVector.get(l).getEntry_date(),DATE_TIME_FORMAT3,DATE_TIME_FORMAT4));
                       bo_data.add(vitalData);
                   }

                   for (VitalData entry : bo_data) {
                       String date = entry.getEntry_date();
                       double value = Double.parseDouble(entry.getValue());

                       if (dateToSum.equals(date)) {
                           dateToSum.put(date, dateToSum.get(date) + value);
                           dateToCount.put(date, dateToCount.get(date) + 1);
                       } else {
                           dateToSum.put(date, value);
                           dateToCount.put(date, 1);
                       }
                   }

                   Map<String, Double> dateToAverage = new HashMap<>();
                   for (Map.Entry<String, Double> entry : dateToSum.entrySet()) {
                       String date = entry.getKey();
                       double sum = entry.getValue();
                       int count = dateToCount.get(date);
                       double average = sum / count;
                       dateToAverage.put(date, average);
                   }
                   List<VitalData> newbo_data = new ArrayList<>();

                   for (Map.Entry<String, Double> entry : dateToAverage.entrySet()) {
                       String date = entry.getKey();
                       String average = String.valueOf(entry.getValue());
                       VitalData vitalData=new VitalData();
                       vitalData.setValue(average);
                       vitalData.setEntry_date(date);
                       newbo_data.add(vitalData);
                   }
                   if (newbo_data.size()>0){
                       String[] newbo_datapoints=new String[newbo_data.size()];
                       String[] newbo_timestamp=new String[newbo_data.size()];

                       for (int i=0;i<newbo_data.size();i++){
                           newbo_datapoints[i]=newbo_data.get(i).getValue();
                           newbo_timestamp[i]=DateUtil.formatedDate(newbo_data.get(i).getEntry_date(),DATE_TIME_FORMAT4,DATE_TIME_FORMAT3);
                       }

                       Custom_Line customLine = graphData.getMonthLineData2(newbo_datapoints, newbo_data.size(), new int[]{colors[1]}, newbo_timestamp,binding.boChart);
                       GraphUI.updateMonthData2(binding.boChart,customLine);
                       binding.boChart.getAxisLeft().setStartAtZero(false);
                       binding.boChart.animateX(1000, Easing.EaseInCubic);
                   }
               }
                break;
            }

            case TYPE.SIX_MONTH: {

                GraphUI.setAppearance2(binding.sixmonthBtn, binding.monthBtn, binding.weekBtn, binding.yearBtn, binding.dayBtn);
                List<String> daysl=getSixMonthsExceptCurrent();
                String formattedDate = daysl.get(0);
                String formattedDate2 = daysl.get(daysl.size()-1);

                binding.textWeight.setText(formattedDate+" - "+formattedDate2);
                binding.textBo.setText(formattedDate+" - "+formattedDate2);
                binding.textBp.setText(formattedDate+" - "+formattedDate2);
                binding.textGlucose.setText(formattedDate+" - "+formattedDate2);
                binding.textHr.setText(formattedDate+" - "+formattedDate2);
                if (hr_dataVector.size()>0){
                    List<VitalData> hr_data=new ArrayList<>();
                    List<String> days=getSixMonthsExceptCurrent();

                    Map<String, Double> monthSumMap = new HashMap<>();
                    Map<String, Integer> monthCountMap = new HashMap<>();

                    for (int m=0;m<days.size();m++){
                        for (int l=0;l<hr_dataVector.size();l++){
                            String days1=days.get(m);
                            String days2=DateUtil.formatedDate(hr_dataVector.get(l).getEntry_date(),DATE_TIME_FORMAT3,"MMM yyyy");
                            if (days1.equals(days2)){
                                VitalData vitalData=new VitalData();
                                vitalData.setValue(hr_dataVector.get(l).getValue());
                                vitalData.setEntry_date(DateUtil.formatedDate(hr_dataVector.get(l).getEntry_date(),DATE_TIME_FORMAT3,DATE_TIME_FORMAT4));
                                hr_data.add(vitalData);
                            }
                        }
                    }

                    for (VitalData entry : hr_data) {
                        String monthYear = DateUtil.formatedDate(entry.getEntry_date(),DATE_TIME_FORMAT4,"MMM yyyy");
                        double value = Double.parseDouble(entry.getValue());
                        double sum = 0.0;
                        if (monthSumMap.containsKey(monthYear)) {
                            sum = monthSumMap.get(monthYear);
                        }
                        sum += value;
                        monthSumMap.put(monthYear, sum);

                        int count = 0;
                        if (monthCountMap.containsKey(monthYear)) {
                            count = monthCountMap.get(monthYear);
                        }
                        count += 1;
                        monthCountMap.put(monthYear, count);
                    }

                    List<VitalData> newhr_data = new ArrayList<>();
                    for (Map.Entry<String, Double> entry : monthSumMap.entrySet()) {
                        String monthYear = entry.getKey();
                        double sum = entry.getValue();
                        int count = monthCountMap.get(monthYear); // Avoid division by zero
                        if(count==0){
                            count=1;
                        }
                        double average = sum / count;
                        VitalData vitalData=new VitalData();
                        vitalData.setEntry_date(monthYear);
                        vitalData.setValue(String.format("%.1f", average));
                        newhr_data.add(vitalData);
                    }

                    if (newhr_data.size()>0){
                        SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy", Locale.ENGLISH);
                        Comparator<VitalData> monthComparator = new Comparator<VitalData>() {
                            @Override
                            public int compare(VitalData o1, VitalData o2) {
                                // Compare the months of the dates
                                try {
                                    Date date1 = sdf.parse(o1.getEntry_date());
                                    Date date2 = sdf.parse(o2.getEntry_date());
                                    return date1.compareTo(date2);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    return 0;
                                }
                            }
                        };
                        Collections.sort(newhr_data,monthComparator);
                        String[] newhr_datapoints=new String[newhr_data.size()];
                        String[] newhr_timestamp=new String[newhr_data.size()];

                        for (int i=0;i<newhr_data.size();i++){
                            newhr_datapoints[i]=newhr_data.get(i).getValue();
                            newhr_timestamp[i]=newhr_data.get(i).getEntry_date();
                        }

                        Custom_Line customLine = graphData.getYearLineData2(newhr_datapoints, newhr_data.size(), new int[]{colors[1]}, newhr_timestamp,"sixmonth",binding.heartRateChart);
                        GraphUI.updateYearData(customLine,binding.heartRateChart);
                        binding.heartRateChart.getAxisLeft().setStartAtZero(false);
                        binding.heartRateChart.animateX(1000, Easing.EaseInCubic);
                    }
                }

                if (glucose_dataVector.size()>0){
                    List<VitalData> gl_data=new ArrayList<>();
                    List<String> days=getSixMonthsExceptCurrent();

                    Map<String, Double> monthSumMap = new HashMap<>();
                    Map<String, Integer> monthCountMap = new HashMap<>();

                    for (int m=0;m<days.size();m++){
                        for (int l=0;l<glucose_dataVector.size();l++){
                            String days1=days.get(m);
                            String days2=DateUtil.formatedDate(glucose_dataVector.get(l).getEntry_date(),DATE_TIME_FORMAT3,"MMM yyyy");
                            if (days1.equals(days2)){
                                VitalData vitalData=new VitalData();
                                vitalData.setValue(glucose_dataVector.get(l).getValue());
                                vitalData.setEntry_date(DateUtil.formatedDate(glucose_dataVector.get(l).getEntry_date(),DATE_TIME_FORMAT3,DATE_TIME_FORMAT4));
                                gl_data.add(vitalData);
                            }
                        }
                    }

                    for (VitalData entry : gl_data) {
                        String monthYear = DateUtil.formatedDate(entry.getEntry_date(),DATE_TIME_FORMAT4,"MMM yyyy");
                        double value = Double.parseDouble(entry.getValue());
                        double sum = 0.0;
                        if (monthSumMap.containsKey(monthYear)) {
                            sum = monthSumMap.get(monthYear);
                        }
                        sum += value;
                        monthSumMap.put(monthYear, sum);

                        int count = 0;
                        if (monthCountMap.containsKey(monthYear)) {
                            count = monthCountMap.get(monthYear);
                        }
                        count += 1;
                        monthCountMap.put(monthYear, count);
                    }

                    List<VitalData> newgl_data = new ArrayList<>();
                    for (Map.Entry<String, Double> entry : monthSumMap.entrySet()) {
                        String monthYear = entry.getKey();
                        double sum = entry.getValue();
                        int count = monthCountMap.get(monthYear); // Avoid division by zero
                        if(count==0){
                            count=1;
                        }
                        double average = sum / count;
                        VitalData vitalData=new VitalData();
                        vitalData.setEntry_date(monthYear);
                        vitalData.setValue(String.format("%.1f", average));
                        newgl_data.add(vitalData);
                    }

                    if (newgl_data.size()>0){
                        SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy", Locale.ENGLISH);
                        Comparator<VitalData> monthComparator = new Comparator<VitalData>() {
                            @Override
                            public int compare(VitalData o1, VitalData o2) {
                                // Compare the months of the dates
                                try {
                                    Date date1 = sdf.parse(o1.getEntry_date());
                                    Date date2 = sdf.parse(o2.getEntry_date());
                                    return date1.compareTo(date2);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    return 0;
                                }
                            }
                        };
                        Collections.sort(newgl_data,monthComparator);
                        String[] newgl_datapoints=new String[newgl_data.size()];
                        String[] newgl_timestamp=new String[newgl_data.size()];

                        for (int i=0;i<newgl_data.size();i++){
                            newgl_datapoints[i]=newgl_data.get(i).getValue();
                            newgl_timestamp[i]=newgl_data.get(i).getEntry_date();
                        }

                        Custom_Line customLine = graphData.getYearLineData2(newgl_datapoints, newgl_data.size(), new int[]{colors[1]}, newgl_timestamp,"sixmonth",binding.glucoseChart);
                        GraphUI.updateYearData(customLine,binding.glucoseChart);
                        binding.glucoseChart.getAxisLeft().setStartAtZero(false);
                        binding.glucoseChart.animateX(1000, Easing.EaseInCubic);
                    }
                }

                if (weight_dataVector.size()>0){
                    List<VitalData> weight_data=new ArrayList<>();
                    List<String> days=getSixMonthsExceptCurrent();

                    Map<String, Double> monthSumMap = new HashMap<>();
                    Map<String, Integer> monthCountMap = new HashMap<>();

                    for (int m=0;m<days.size();m++){
                        for (int l=0;l<weight_dataVector.size();l++){
                            String days1=days.get(m);
                            String days2=DateUtil.formatedDate(weight_dataVector.get(l).getEntry_date(),DATE_TIME_FORMAT3,"MMM yyyy");
                            if (days1.equals(days2)){
                                VitalData vitalData=new VitalData();
                                vitalData.setValue(weight_dataVector.get(l).getValue());
                                vitalData.setEntry_date(DateUtil.formatedDate(weight_dataVector.get(l).getEntry_date(),DATE_TIME_FORMAT3,DATE_TIME_FORMAT4));
                                weight_data.add(vitalData);
                            }
                        }
                    }

                    for (VitalData entry : weight_data) {
                        String monthYear = DateUtil.formatedDate(entry.getEntry_date(),DATE_TIME_FORMAT4,"MMM yyyy");
                        double value = Double.parseDouble(entry.getValue());
                        double sum = 0.0;
                        if (monthSumMap.containsKey(monthYear)) {
                            sum = monthSumMap.get(monthYear);
                        }
                        sum += value;
                        monthSumMap.put(monthYear, sum);
                        int count = 0;
                        if (monthCountMap.containsKey(monthYear)) {
                            count = monthCountMap.get(monthYear);
                        }
                        count += 1;
                        monthCountMap.put(monthYear, count);
                    }

                    List<VitalData> newweiht_data = new ArrayList<>();
                    for (Map.Entry<String, Double> entry : monthSumMap.entrySet()) {
                        String monthYear = entry.getKey();
                        double sum = entry.getValue();
                        int count = monthCountMap.get(monthYear); // Avoid division by zero
                        if(count==0){
                            count=1;
                        }
                        double average = sum / count;
                        VitalData vitalData=new VitalData();
                        vitalData.setEntry_date(monthYear);
                        vitalData.setValue(String.format("%.1f", average));
                        newweiht_data.add(vitalData);
                    }

                    if (newweiht_data.size()>0){
                        SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy", Locale.ENGLISH);
                        Comparator<VitalData> monthComparator = new Comparator<VitalData>() {
                            @Override
                            public int compare(VitalData o1, VitalData o2) {
                                // Compare the months of the dates
                                try {
                                    Date date1 = sdf.parse(o1.getEntry_date());
                                    Date date2 = sdf.parse(o2.getEntry_date());
                                    return date1.compareTo(date2);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    return 0;
                                }
                            }
                        };
                        Collections.sort(newweiht_data,monthComparator);
                        String[] newweight_datapoints=new String[newweiht_data.size()];
                        String[] newweight_timestamp=new String[newweiht_data.size()];
                        for (int i=0;i<newweiht_data.size();i++){
                            newweight_datapoints[i]=newweiht_data.get(i).getValue();
                            newweight_timestamp[i]=newweiht_data.get(i).getEntry_date();
                        }

                        Custom_Line customLine = graphData.getYearLineData2(newweight_datapoints, newweiht_data.size(), new int[]{colors[1]}, newweight_timestamp,"sixmonth",binding.weightChart);
                        GraphUI.updateYearData(customLine,binding.weightChart);
                        binding.weightChart.getAxisLeft().setStartAtZero(false);
                        binding.weightChart.animateX(1000, Easing.EaseInCubic);
                    }
                }

                if (bp_dataVector.size()>0){
                    List<VitalDataBP> bp_data=new ArrayList<>();
                   /* Map<String, Double> dateToSum = new HashMap<>();
                    Map<String, Integer> dateToCount = new HashMap<>();*/
                    Map<String, List<Double>> dateTohigh = new HashMap<>();
                    Map<String, List<Double>> dateTolow = new HashMap<>();
                    List<String> days=getSixMonthsExceptCurrent();
                    for (int m=0;m<days.size();m++){
                        for (int l=0;l<bp_dataVector.size();l++){
                            String days1=days.get(m);
                            String days2=DateUtil.formatedDate(bp_dataVector.get(l).getEntry_date(),DATE_TIME_FORMAT3,"MMM yyyy");
                            if (days1.equals(days2)){
                                VitalDataBP vitalData=new VitalDataBP();
                                vitalData.setBplow(bp_dataVector.get(l).getBplow());
                                vitalData.setBphigh(bp_dataVector.get(l).getBphigh());
                                vitalData.setEntry_date(DateUtil.formatedDate(bp_dataVector.get(l).getEntry_date(),DATE_TIME_FORMAT3,DATE_TIME_FORMAT4));
                                bp_data.add(vitalData);
                            }
                        }
                    }

                    for (VitalDataBP entry : bp_data) {
                        String monthYear = DateUtil.formatedDate(entry.getEntry_date(),DATE_TIME_FORMAT4,"MMM yyyy");
                        double value = Double.parseDouble(entry.getBphigh());
                        double value2 = Double.parseDouble(entry.getBplow());
                        if (dateTohigh.containsKey(monthYear)) {
                            dateTohigh.get(monthYear).add(value);
                            dateTolow.get(monthYear).add(value2);
                        } else {
                            List<Double> systolicList = new ArrayList<>();
                            systolicList.add(value);
                            dateTohigh.put(monthYear, systolicList);
                            List<Double> diastolicList = new ArrayList<>();
                            diastolicList.add(value2);
                            dateTolow.put(monthYear, diastolicList);
                        }
                    }
                    Map<String, Double> dateToAverageSystolic = new HashMap<>();
                    Map<String, Double> dateToAverageDiastolic = new HashMap<>();

                    for (Map.Entry<String, List<Double>> entry : dateTohigh.entrySet()) {
                        String date = entry.getKey();
                        List<Double> systolicValues = entry.getValue();
                        List<Double> diastolicValues = dateTolow.get(date);
                        double sumSystolic = 0.0;
                        for (Double systolic : systolicValues) {
                            sumSystolic += systolic;
                        }

                        double sumDiastolic = 0.0;
                        for (Double diastolic : diastolicValues) {
                            sumDiastolic += diastolic;
                        }
                        double averageSystolic = systolicValues.size() > 0 ? sumSystolic / systolicValues.size() : 0.0;
                        double averageDiastolic = diastolicValues.size() > 0 ? sumDiastolic / diastolicValues.size() : 0.0;
                        dateToAverageSystolic.put(date, averageSystolic);
                        dateToAverageDiastolic.put(date, averageDiastolic);
                    }

                    List<VitalDataBP> newbp_data = new ArrayList<>();
                    ArrayList<VitalDataBP> newbp_data2 = new ArrayList<>();
                    for (String date : dateToAverageSystolic.keySet()) {
                        double averageSystolic = dateToAverageSystolic.get(date);
                        double averageDiastolic = dateToAverageDiastolic.get(date);
                        VitalDataBP vitalData=new VitalDataBP();
                        vitalData.setBplow(String.format("%.1f", averageDiastolic));
                        vitalData.setBphigh(String.format("%.1f", averageSystolic));
                        vitalData.setEntry_date(date);
                        newbp_data.add(vitalData);
                        newbp_data2.add(vitalData);
                    }
                    if (newbp_data.size()>0){
                        String[] newbp_timestamp=new String[newbp_data.size()];
                        String[] newbph_dataPoints=new String[newbp_data.size()];
                        String[] newbpl_dataPoints=new String[newbp_data.size()];
                        for (int i=0;i<newbp_data.size();i++){
                            newbph_dataPoints[i]=newbp_data.get(i).getBphigh();
                            newbpl_dataPoints[i]=newbp_data.get(i).getBplow();
                            newbp_timestamp[i]=newbp_data.get(i).getEntry_date();
                        }

                        Custom_LineBP customLine = graphData.getdefaultBPData2(newbph_dataPoints,newbpl_dataPoints, newbp_data2, new int[]{colors[1], colors[0]}, newbp_timestamp,"sixmonth",binding.bloodPressureChart);
                        GraphUI.updateWeekData2(customLine, binding.bloodPressureChart);
                        binding.bloodPressureChart.getAxisLeft().setStartAtZero(false);
                        binding.bloodPressureChart.animateX(1000, Easing.EaseInCubic);
                        binding.bloodPressureChart.invalidate();
                    }
                }

                if (bo_dataVector.size()>0){
                    List<VitalData> bo_data=new ArrayList<>();
                    List<String> days=getSixMonthsExceptCurrent();

                    Map<String, Double> monthSumMap = new HashMap<>();
                    Map<String, Integer> monthCountMap = new HashMap<>();

                    for (int m=0;m<days.size();m++){
                        for (int l=0;l<bo_dataVector.size();l++){
                            String days1=days.get(m);
                            String days2=DateUtil.formatedDate(bo_dataVector.get(l).getEntry_date(),DATE_TIME_FORMAT3,"MMM yyyy");
                            if (days1.equals(days2)){
                                VitalData vitalData=new VitalData();
                                vitalData.setValue(bo_dataVector.get(l).getValue());
                                vitalData.setEntry_date(DateUtil.formatedDate(bo_dataVector.get(l).getEntry_date(),DATE_TIME_FORMAT3,DATE_TIME_FORMAT4));
                                bo_data.add(vitalData);
                            }
                        }
                    }

                    for (VitalData entry : bo_data) {
                        String monthYear = DateUtil.formatedDate(entry.getEntry_date(),DATE_TIME_FORMAT4,"MMM yyyy");
                        double value = Double.parseDouble(entry.getValue());
                        double sum = 0.0;
                        if (monthSumMap.containsKey(monthYear)) {
                            sum = monthSumMap.get(monthYear);
                        }
                        sum += value;
                        monthSumMap.put(monthYear, sum);
                        int count = 0;
                        if (monthCountMap.containsKey(monthYear)) {
                            count = monthCountMap.get(monthYear);
                        }
                        count += 1;
                        monthCountMap.put(monthYear, count);
                    }

                    List<VitalData> newbo_data = new ArrayList<>();
                    for (Map.Entry<String, Double> entry : monthSumMap.entrySet()) {
                        String monthYear = entry.getKey();
                        double sum = entry.getValue();
                        int count = monthCountMap.get(monthYear); // Avoid division by zero
                        if(count==0){
                            count=1;
                        }
                        double average = sum / count;
                        VitalData vitalData=new VitalData();
                        vitalData.setEntry_date(monthYear);
                        vitalData.setValue(String.format("%.1f", average));
                        newbo_data.add(vitalData);
                    }

                    if (newbo_data.size()>0){
                        SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy", Locale.ENGLISH);
                        Comparator<VitalData> monthComparator = new Comparator<VitalData>() {
                            @Override
                            public int compare(VitalData o1, VitalData o2) {
                                // Compare the months of the dates
                                try {
                                    Date date1 = sdf.parse(o1.getEntry_date());
                                    Date date2 = sdf.parse(o2.getEntry_date());
                                    return date1.compareTo(date2);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    return 0;
                                }
                            }
                        };
                        Collections.sort(newbo_data,monthComparator);
                        String[] newbo_datapoints=new String[newbo_data.size()];
                        String[] newbo_timestamp=new String[newbo_data.size()];

                        for (int i=0;i<newbo_data.size();i++){
                            newbo_datapoints[i]=newbo_data.get(i).getValue();
                            newbo_timestamp[i]=newbo_data.get(i).getEntry_date();
                        }
                        Custom_Line customLine = graphData.getYearLineData2(newbo_datapoints, newbo_data.size(), new int[]{colors[1]}, newbo_timestamp,"sixmonth",binding.boChart);
                        GraphUI.updateYearData(customLine,binding.boChart);
                        binding.boChart.getAxisLeft().setStartAtZero(false);
                        binding.boChart.animateX(1000, Easing.EaseInCubic);
                    }
                }
                break;
            }

             case TYPE.YEARLY: {
                GraphUI.setAppearance2(binding.yearBtn, binding.monthBtn, binding.weekBtn, binding.sixmonthBtn, binding.dayBtn);
                 List<String> daysl=getAllMonthsOfCurrentYear();
                 String formattedDate = daysl.get(0);
                 String formattedDate2 = daysl.get(daysl.size()-1);

                 binding.textWeight.setText(formattedDate+" - "+formattedDate2);
                 binding.textBo.setText(formattedDate+" - "+formattedDate2);
                 binding.textBp.setText(formattedDate+" - "+formattedDate2);
                 binding.textGlucose.setText(formattedDate+" - "+formattedDate2);
                 binding.textHr.setText(formattedDate+" - "+formattedDate2);

                 if (hr_dataVector.size()>0){
                     List<VitalData> hr_data=new ArrayList<>();
                     List<String> days=getAllMonthsOfCurrentYear();
                     Map<String, Double> monthSumMap = new HashMap<>();
                     Map<String, Integer> monthCountMap = new HashMap<>();
                     for (int m=0;m<days.size();m++){
                         for (int l=0;l<hr_dataVector.size();l++){
                             String days1=days.get(m);
                             String days2=DateUtil.formatedDate(hr_dataVector.get(l).getEntry_date(),DATE_TIME_FORMAT3,"MMM yyyy");
                             if (days1.equals(days2)){
                                 VitalData vitalData=new VitalData();
                                 vitalData.setValue(hr_dataVector.get(l).getValue());
                                 vitalData.setEntry_date(DateUtil.formatedDate(hr_dataVector.get(l).getEntry_date(),DATE_TIME_FORMAT3,DATE_TIME_FORMAT4));
                                 hr_data.add(vitalData);
                             }
                         }
                     }
                     for (VitalData entry : hr_data) {
                         String monthYear = DateUtil.formatedDate(entry.getEntry_date(),DATE_TIME_FORMAT4,"MMM yyyy");
                         double value = Double.parseDouble(entry.getValue());
                         double sum = 0.0;
                         if (monthSumMap.containsKey(monthYear)) {
                             sum = monthSumMap.get(monthYear);
                         }
                         sum += value;
                         monthSumMap.put(monthYear, sum);
                         int count = 0;
                         if (monthCountMap.containsKey(monthYear)) {
                             count = monthCountMap.get(monthYear);
                         }
                         count += 1;
                         monthCountMap.put(monthYear, count);
                     }

                     List<VitalData> newhr_data = new ArrayList<>();
                     for (Map.Entry<String, Double> entry : monthSumMap.entrySet()) {
                         String monthYear = entry.getKey();
                         double sum = entry.getValue();
                         int count = monthCountMap.get(monthYear); // Avoid division by zero
                         if(count==0){
                             count=1;
                         }
                         double average = sum / count;
                         VitalData vitalData=new VitalData();
                         vitalData.setEntry_date(monthYear);
                         vitalData.setValue(String.format("%.1f", average));
                         newhr_data.add(vitalData);
                     }

                     if (newhr_data.size()>0){
                         SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy", Locale.ENGLISH);
                         Comparator<VitalData> monthComparator = new Comparator<VitalData>() {
                             @Override
                             public int compare(VitalData o1, VitalData o2) {
                                 // Compare the months of the dates
                                 try {
                                     Date date1 = sdf.parse(o1.getEntry_date());
                                     Date date2 = sdf.parse(o2.getEntry_date());
                                     return date1.compareTo(date2);
                                 } catch (ParseException e) {
                                     e.printStackTrace();
                                     return 0;
                                 }
                             }
                         };

                         // Sort the list using the custom comparator
                         Collections.sort(newhr_data, monthComparator);
                         String[] newhr_datapoints=new String[newhr_data.size()];
                         String[] newhr_timestamp=new String[newhr_data.size()];

                         for (int i=0;i<newhr_data.size();i++){
                             newhr_datapoints[i]=newhr_data.get(i).getValue();
                             newhr_timestamp[i]=newhr_data.get(i).getEntry_date();
                         }

                         Custom_Line customLine = graphData.getYearLineData2(newhr_datapoints, newhr_data.size(), new int[]{colors[1]}, newhr_timestamp,"yearly",binding.heartRateChart);
                         GraphUI.updateYearData(customLine,binding.heartRateChart);
                         binding.heartRateChart.getAxisLeft().setStartAtZero(false);
                         binding.heartRateChart.animateX(1000, Easing.EaseInCubic);
                     }
                 }

                 if (glucose_dataVector.size()>0){
                     List<VitalData> gl_data=new ArrayList<>();
                     List<String> days=getAllMonthsOfCurrentYear();
                     Map<String, Double> monthSumMap = new HashMap<>();
                     Map<String, Integer> monthCountMap = new HashMap<>();

                     for (int m=0;m<days.size();m++){
                         for (int l=0;l<glucose_dataVector.size();l++){
                             String days1=days.get(m);
                             String days2=DateUtil.formatedDate(glucose_dataVector.get(l).getEntry_date(),DATE_TIME_FORMAT3,"MMM yyyy");
                             if (days1.equals(days2)){
                                 VitalData vitalData=new VitalData();
                                 vitalData.setValue(glucose_dataVector.get(l).getValue());
                                 vitalData.setEntry_date(DateUtil.formatedDate(glucose_dataVector.get(l).getEntry_date(),DATE_TIME_FORMAT3,DATE_TIME_FORMAT4));
                                 gl_data.add(vitalData);
                             }
                         }
                     }

                     for (VitalData entry : gl_data) {
                         String monthYear = DateUtil.formatedDate(entry.getEntry_date(),DATE_TIME_FORMAT4,"MMM yyyy");
                         double value = Double.parseDouble(entry.getValue());
                         double sum = 0.0;
                         if (monthSumMap.containsKey(monthYear)) {
                             sum = monthSumMap.get(monthYear);
                         }
                         sum += value;
                         monthSumMap.put(monthYear, sum);

                         int count = 0;
                         if (monthCountMap.containsKey(monthYear)) {
                             count = monthCountMap.get(monthYear);
                         }
                         count += 1;
                         monthCountMap.put(monthYear, count);
                     }

                     List<VitalData> newgl_data = new ArrayList<>();
                     for (Map.Entry<String, Double> entry : monthSumMap.entrySet()) {
                         String monthYear = entry.getKey();
                         double sum = entry.getValue();
                         int count = monthCountMap.get(monthYear); // Avoid division by zero
                         if(count==0){
                             count=1;
                         }
                         double average = sum / count;
                         VitalData vitalData=new VitalData();
                         vitalData.setEntry_date(monthYear);
                         vitalData.setValue(String.format("%.1f", average));
                         newgl_data.add(vitalData);
                     }

                     if (newgl_data.size()>0){
                         SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy", Locale.ENGLISH);
                         Comparator<VitalData> monthComparator = new Comparator<VitalData>() {
                             @Override
                             public int compare(VitalData o1, VitalData o2) {
                                 // Compare the months of the dates
                                 try {
                                     Date date1 = sdf.parse(o1.getEntry_date());
                                     Date date2 = sdf.parse(o2.getEntry_date());
                                     return date1.compareTo(date2);
                                 } catch (ParseException e) {
                                     e.printStackTrace();
                                     return 0;
                                 }


                             }
                         };

                         // Sort the list using the custom comparator
                         Collections.sort(newgl_data, monthComparator);
                         String[] newgl_datapoints=new String[newgl_data.size()];
                         String[] newgl_timestamp=new String[newgl_data.size()];

                         for (int i=0;i<newgl_data.size();i++){
                             newgl_datapoints[i]=newgl_data.get(i).getValue();
                             newgl_timestamp[i]=newgl_data.get(i).getEntry_date();
                         }

                         Custom_Line customLine = graphData.getYearLineData2(newgl_datapoints, newgl_data.size(), new int[]{colors[1]}, newgl_timestamp,"yearly",binding.glucoseChart);
                         GraphUI.updateYearData(customLine,binding.glucoseChart);
                         binding.glucoseChart.getAxisLeft().setStartAtZero(false);
                         binding.glucoseChart.animateX(1000, Easing.EaseInCubic);
                     }
                 }

                 if (weight_dataVector.size()>0){
                     List<VitalData> weight_data=new ArrayList<>();
                     List<String> days=getAllMonthsOfCurrentYear();

                     Map<String, Double> monthSumMap = new HashMap<>();
                     Map<String, Integer> monthCountMap = new HashMap<>();

                     for (int m=0;m<days.size();m++){
                         for (int l=0;l<weight_dataVector.size();l++){
                             String days1=days.get(m);
                             String days2=DateUtil.formatedDate(weight_dataVector.get(l).getEntry_date(),DATE_TIME_FORMAT3,"MMM yyyy");
                             if (days1.equals(days2)){
                                 VitalData vitalData=new VitalData();
                                 vitalData.setValue(weight_dataVector.get(l).getValue());
                                 vitalData.setEntry_date(DateUtil.formatedDate(weight_dataVector.get(l).getEntry_date(),DATE_TIME_FORMAT3,DATE_TIME_FORMAT4));
                                 weight_data.add(vitalData);
                             }
                         }
                     }

                         for (VitalData entry : weight_data) {
                             String monthYear = DateUtil.formatedDate(entry.getEntry_date(),DATE_TIME_FORMAT4,"MMM yyyy");
                             double value = Double.parseDouble(entry.getValue());
                             double sum = 0.0;
                             if (monthSumMap.containsKey(monthYear)) {
                                 sum = monthSumMap.get(monthYear);
                             }
                             sum += value;
                             monthSumMap.put(monthYear, sum);

                             int count = 0;
                             if (monthCountMap.containsKey(monthYear)) {
                                 count = monthCountMap.get(monthYear);
                             }
                             count += 1;
                             monthCountMap.put(monthYear, count);
                         }

                     List<VitalData> newweiht_data = new ArrayList<>();
                     for (Map.Entry<String, Double> entry : monthSumMap.entrySet()) {
                         String monthYear = entry.getKey();
                         double sum = entry.getValue();
                         int count = monthCountMap.get(monthYear); // Avoid division by zero
                         if(count==0){
                             count=1;
                         }
                         double average = sum / count;
                         VitalData vitalData=new VitalData();
                         vitalData.setEntry_date(monthYear);
                         vitalData.setValue(String.format("%.1f", average));
                         newweiht_data.add(vitalData);
                     }

                     if (newweiht_data.size()>0){
                         SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy", Locale.ENGLISH);
                         Comparator<VitalData> monthComparator = new Comparator<VitalData>() {
                             @Override
                             public int compare(VitalData o1, VitalData o2) {
                                 // Compare the months of the dates
                                 try {
                                     Date date1 = sdf.parse(o1.getEntry_date());
                                     Date date2 = sdf.parse(o2.getEntry_date());
                                     return date1.compareTo(date2);
                                 } catch (ParseException e) {
                                     e.printStackTrace();
                                     return 0;
                                 }
                             }
                         };

                         // Sort the list using the custom comparator
                         Collections.sort(newweiht_data, monthComparator);
                         String[] newweight_datapoints=new String[newweiht_data.size()];
                         String[] newweight_timestamp=new String[newweiht_data.size()];

                         for (int i=0;i<newweiht_data.size();i++){
                             newweight_datapoints[i]=newweiht_data.get(i).getValue();
                             newweight_timestamp[i]=newweiht_data.get(i).getEntry_date();
                         }

                         Custom_Line customLine = graphData.getYearLineData2(newweight_datapoints, newweiht_data.size(), new int[]{colors[1]}, newweight_timestamp,"yearly",binding.weightChart);
                         GraphUI.updateYearData(customLine,binding.weightChart);
                         binding.weightChart.getAxisLeft().setStartAtZero(false);
                         binding.weightChart.animateX(1000, Easing.EaseInCubic);
                     }
                 }
                 if (bp_dataVector.size()>0){
                     List<VitalDataBP> bp_data=new ArrayList<>();
                   /* Map<String, Double> dateToSum = new HashMap<>();
                    Map<String, Integer> dateToCount = new HashMap<>();*/
                     Map<String, List<Double>> dateTohigh = new HashMap<>();
                     Map<String, List<Double>> dateTolow = new HashMap<>();
                     List<String> days=getAllMonthsOfCurrentYear();
                     for (int m=0;m<days.size();m++){
                         for (int l=0;l<bp_dataVector.size();l++){
                             String days1=days.get(m);
                             String days2=DateUtil.formatedDate(bp_dataVector.get(l).getEntry_date(),DATE_TIME_FORMAT3,"MMM yyyy");
                             if (days1.equals(days2)){
                                 VitalDataBP vitalData=new VitalDataBP();
                                 vitalData.setBplow(bp_dataVector.get(l).getBplow());
                                 vitalData.setBphigh(bp_dataVector.get(l).getBphigh());
                                 vitalData.setEntry_date(DateUtil.formatedDate(bp_dataVector.get(l).getEntry_date(),DATE_TIME_FORMAT3,DATE_TIME_FORMAT4));
                                 bp_data.add(vitalData);
                             }
                         }
                     }

                     for (VitalDataBP entry : bp_data) {
                         String monthYear = DateUtil.formatedDate(entry.getEntry_date(),DATE_TIME_FORMAT4,"MMM yyyy");
                         double value = Double.parseDouble(entry.getBphigh());
                         double value2 = Double.parseDouble(entry.getBplow());
                         if (dateTohigh.containsKey(monthYear)) {
                             dateTohigh.get(monthYear).add(value);
                             dateTolow.get(monthYear).add(value2);
                         } else {
                             List<Double> systolicList = new ArrayList<>();
                             systolicList.add(value);
                             dateTohigh.put(monthYear, systolicList);
                             List<Double> diastolicList = new ArrayList<>();
                             diastolicList.add(value2);
                             dateTolow.put(monthYear, diastolicList);
                         }
                     }
                     Map<String, Double> dateToAverageSystolic = new HashMap<>();
                     Map<String, Double> dateToAverageDiastolic = new HashMap<>();

                     for (Map.Entry<String, List<Double>> entry : dateTohigh.entrySet()) {
                         String date = entry.getKey();
                         List<Double> systolicValues = entry.getValue();
                         List<Double> diastolicValues = dateTolow.get(date);
                         double sumSystolic = 0.0;
                         for (Double systolic : systolicValues) {
                             sumSystolic += systolic;
                         }
                         double sumDiastolic = 0.0;
                         for (Double diastolic : diastolicValues) {
                             sumDiastolic += diastolic;
                         }

                         double averageSystolic = systolicValues.size() > 0 ? sumSystolic / systolicValues.size() : 0.0;
                         double averageDiastolic = diastolicValues.size() > 0 ? sumDiastolic / diastolicValues.size() : 0.0;

                         dateToAverageSystolic.put(date, averageSystolic);
                         dateToAverageDiastolic.put(date, averageDiastolic);
                     }

                     List<VitalDataBP> newbp_data = new ArrayList<>();
                     ArrayList<VitalDataBP> newbp_data2 = new ArrayList<>();

                     for (String date : dateToAverageSystolic.keySet()) {
                         double averageSystolic = dateToAverageSystolic.get(date);
                         double averageDiastolic = dateToAverageDiastolic.get(date);
                         VitalDataBP vitalData=new VitalDataBP();
                         vitalData.setBplow(String.format("%.1f", averageDiastolic));
                         vitalData.setBphigh(String.format("%.1f", averageSystolic));
                         vitalData.setEntry_date(date);
                         newbp_data.add(vitalData);
                         newbp_data2.add(vitalData);
                     }
                     if (newbp_data.size()>0){
                         String[] newbp_timestamp=new String[newbp_data.size()];
                         String[] newbph_dataPoints=new String[newbp_data.size()];
                         String[] newbpl_dataPoints=new String[newbp_data.size()];
                         for (int i=0;i<newbp_data.size();i++){
                             newbph_dataPoints[i]=newbp_data.get(i).getBphigh();
                             newbpl_dataPoints[i]=newbp_data.get(i).getBplow();
                             newbp_timestamp[i]=newbp_data.get(i).getEntry_date();
                         }
                         Custom_LineBP customLine = graphData.getdefaultBPData2(newbph_dataPoints,newbpl_dataPoints, newbp_data2, new int[]{colors[1], colors[0]}, newbp_timestamp,"yearly",binding.bloodPressureChart);
                         GraphUI.updateWeekData2(customLine, binding.bloodPressureChart);

                         binding.bloodPressureChart.getAxisLeft().setStartAtZero(false);
                         binding.bloodPressureChart.animateX(1000, Easing.EaseInCubic);
                         binding.bloodPressureChart.invalidate();
                     }
                 }

                 if (bo_dataVector.size()>0){
                     List<VitalData> bo_data=new ArrayList<>();
                     List<String> days=getAllMonthsOfCurrentYear();

                     Map<String, Double> monthSumMap = new HashMap<>();
                     Map<String, Integer> monthCountMap = new HashMap<>();

                     for (int m=0;m<days.size();m++){
                         for (int l=0;l<bo_dataVector.size();l++){
                             String days1=days.get(m);
                             String days2=DateUtil.formatedDate(bo_dataVector.get(l).getEntry_date(),DATE_TIME_FORMAT3,"MMM yyyy");
                             if (days1.equals(days2)){
                                 VitalData vitalData=new VitalData();
                                 vitalData.setValue(bo_dataVector.get(l).getValue());
                                 vitalData.setEntry_date(DateUtil.formatedDate(bo_dataVector.get(l).getEntry_date(),DATE_TIME_FORMAT3,DATE_TIME_FORMAT4));
                                 bo_data.add(vitalData);
                             }
                         }
                     }

                     for (VitalData entry : bo_data) {
                         String monthYear = DateUtil.formatedDate(entry.getEntry_date(),DATE_TIME_FORMAT4,"MMM yyyy");
                         double value = Double.parseDouble(entry.getValue());

                         double sum = 0.0;
                         if (monthSumMap.containsKey(monthYear)) {
                             sum = monthSumMap.get(monthYear);
                         }
                         sum += value;
                         monthSumMap.put(monthYear, sum);

                         int count = 0;
                         if (monthCountMap.containsKey(monthYear)) {
                             count = monthCountMap.get(monthYear);
                         }
                         count += 1;
                         monthCountMap.put(monthYear, count);
                     }

                     List<VitalData> newbo_data = new ArrayList<>();
                     for (Map.Entry<String, Double> entry : monthSumMap.entrySet()) {
                         String monthYear = entry.getKey();
                         double sum = entry.getValue();
                         int count = monthCountMap.get(monthYear); // Avoid division by zero
                         if(count==0){
                             count=1;
                         }
                         double average = sum / count;
                         VitalData vitalData=new VitalData();
                         vitalData.setEntry_date(monthYear);
                         vitalData.setValue(String.format("%.1f", average));
                         newbo_data.add(vitalData);
                     }

                     if (newbo_data.size()>0){
                         SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy", Locale.ENGLISH);
                         Comparator<VitalData> monthComparator = new Comparator<VitalData>() {
                             @Override
                             public int compare(VitalData o1, VitalData o2) {
                                 // Compare the months of the dates
                                 try {
                                     Date date1 = sdf.parse(o1.getEntry_date());
                                     Date date2 = sdf.parse(o2.getEntry_date());
                                     return date1.compareTo(date2);
                                 } catch (ParseException e) {
                                     e.printStackTrace();
                                     return 0;
                                 }


                             }
                         };

                         // Sort the list using the custom comparator
                         Collections.sort(newbo_data, monthComparator);

                         String[] newbo_datapoints=new String[newbo_data.size()];
                         String[] newbo_timestamp=new String[newbo_data.size()];

                         for (int i=0;i<newbo_data.size();i++){
                             newbo_datapoints[i]=newbo_data.get(i).getValue();
                             newbo_timestamp[i]=newbo_data.get(i).getEntry_date();
                         }

                         Custom_Line customLine = graphData.getYearLineData2(newbo_datapoints, newbo_data.size(), new int[]{colors[1]}, newbo_timestamp,"yearly",binding.boChart);
                         GraphUI.updateYearData(customLine,binding.boChart);
                         binding.boChart.getAxisLeft().setStartAtZero(false);
                         binding.boChart.animateX(1000, Easing.EaseInCubic);
                     }
                 }
                break;
            }
        }
        binding.heartRateChart.invalidate();
        binding.boChart.invalidate();
        binding.weightChart.invalidate();
        binding.glucoseChart.invalidate();
        binding.bloodPressureChart.invalidate();
     //   hideProgress();
    }

    public String[] vectorToArray(int index, VitalData soapObjectData, String[] data, String[] timeStamp_value) {
        data[index] = Double.toString(Double.parseDouble(soapObjectData.getValue()));
        timeStamp_value[index] = soapObjectData.getEntry_date();

        return data;
    }
    public String[] vectorToArray2( ArrayList<VitalData> soapObjectData, String[] timeStamp_value) {
        String[] data = new String[soapObjectData.size()];
        for (int i = 0; i < soapObjectData.size(); i++) {
            data[i] = (soapObjectData.get(i).getValue());
            timeStamp_value[i] = soapObjectData.get(i).getEntry_date();
            }
        return data;
    }
    private String[] soapObject_TO_Array(ArrayList<VitalDataBP> monitor_bp, String BP) {
        bp_timestamp = new String[monitor_bp.size()];
        String[] data = new String[monitor_bp.size()];
        for (int i = 0; i < monitor_bp.size(); i++) {
            switch (BP) {
                case "bphigh":
                    data[i] = (monitor_bp.get(i).getBphigh());
                    break;
                case "bplow":
                    data[i] = (monitor_bp.get(i).getBplow());
                    break;
            }
            bp_timestamp[i] = monitor_bp.get(i).getEntry_date();
        }
        return data;
    }

    private String[] soapObject_TO_Array2(ArrayList<VitalDataBP> monitor_bp, String BP) {
        bp_timestamp = new String[monitor_bp.size()];
        String[] data = new String[monitor_bp.size()];
        for (int i = 0; i < monitor_bp.size(); i++) {
            switch (BP) {
                case "bphigh":
                    data[i] = (monitor_bp.get(i).getBphigh());
                    break;
                case "bplow":
                    data[i] = (monitor_bp.get(i).getBplow());
                    break;
            }
            bp_timestamp[i] = monitor_bp.get(i).getEntry_date();
        }
        return data;
    }

    private static List<Date> getAllDaysOfTheCurrentWeek() {
        List<Date> datesWeek = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // Set to the first day of the week

        // Generate a list of dates for the week
        for (int i = 0; i < 7; i++) {
            datesWeek.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_WEEK, 1); // Move to the next day
        }

        return datesWeek;
    }

    private static List<Date> getAllDaysOfTheCurrentMonth() {
        List<Date> datesMonth = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);

        // Set the calendar to the first day of the current month
        calendar.set(year, month, 1);

        // Generate a list of dates for the month
        while (calendar.get(Calendar.MONTH) == month) {
            datesMonth.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1); // Move to the next day
        }

        return datesMonth;
    }

    public static List<Date> getDaysOfCurrentWeekExceptToday() {
        List<Date> daysOfCurrentWeekExceptToday = new ArrayList<>();

        // Get the current date
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        // Set the calendar to the start of the week
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());

        // Add the days of the current week except today
        for (int i = 0; i < 7; i++) {
            Date day = calendar.getTime();
            if (!day.equals(currentDate)) {
                daysOfCurrentWeekExceptToday.add(day);
            }
            calendar.add(Calendar.DAY_OF_WEEK, 1);
        }

        return daysOfCurrentWeekExceptToday;
    }

    public static List<String> getAllMonthsOfCurrentYear() {
        List<String> monthsOfYear = new ArrayList<>();

        // Get the current year
        /*Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);

        // Populate the list with all months of the current year
        SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy");
        for (int monthNumber = Calendar.JANUARY; monthNumber <= Calendar.DECEMBER; monthNumber++) {
            calendar.set(currentYear, monthNumber, 1);
            String monthName = sdf.format(calendar.getTime());
            monthsOfYear.add(monthName);
        }*/

        // Get the current year and month
        Calendar currentCalendar = Calendar.getInstance();
        int currentYear = currentCalendar.get(Calendar.YEAR);
        int currentMonth = currentCalendar.get(Calendar.MONTH);
        // Calculate six months excluding the current month
        SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy");
        for (int i = 1; i <= 12; i++) {
            int targetMonth = (currentMonth - i + 12) % 12; // Handle negative values
            int targetYear = currentYear;

            if (targetMonth > currentMonth) {
                targetYear--; // If the target month is in the previous year
            }

            Calendar calendar = Calendar.getInstance();
            calendar.set(targetYear, targetMonth, 1);
            String monthYear = sdf.format(calendar.getTime());
            monthsOfYear.add(monthYear);
        }
        Comparator<String> monthComparator = new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                // Compare the months of the dates
                try {
                    Date date1 = sdf.parse(o1);
                    Date date2 = sdf.parse(o2);
                    return date1.compareTo(date2);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }


            }
        };

        // Sort the list using the custom comparator
        Collections.sort(monthsOfYear, monthComparator);
        return monthsOfYear;
    }

    public static List<String> getSixMonthsExceptCurrent() {
        List<String> sixMonths = new ArrayList<>();

        // Get the current year and month
        Calendar currentCalendar = Calendar.getInstance();
        int currentYear = currentCalendar.get(Calendar.YEAR);
        int currentMonth = currentCalendar.get(Calendar.MONTH)+1;
        // Calculate six months excluding the current month
        SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy");
        for (int i = 1; i <= 6; i++) {
            int targetMonth = (currentMonth - i + 12) % 12; // Handle negative values
            int targetYear = currentYear;

            if (targetMonth > currentMonth) {
                targetYear--; // If the target month is in the previous year
            }

            Calendar calendar = Calendar.getInstance();
            calendar.set(targetYear, targetMonth, 1);
            String monthYear = sdf.format(calendar.getTime());
            sixMonths.add(monthYear);
        }
        Collections.reverse(sixMonths);

        Comparator<String> monthComparator = new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                // Compare the months of the dates
                try {
                    Date date1 = sdf.parse(o1);
                    Date date2 = sdf.parse(o2);
                    return date1.compareTo(date2);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }


            }
        };

        // Sort the list using the custom comparator
        Collections.sort(sixMonths, monthComparator);
        return sixMonths;
    }

    private static boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }
}
