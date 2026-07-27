package com.cybermed.cdoc_patient.me.vitalcheck;

import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch.vital_graph.utility.TYPE.WEEKLY;
import static com.cybermed.cdoc_patient.util.AppConstant.DATE_TIME_FORMAT3;
import static com.cybermed.cdoc_patient.util.AppConstant.DATE_TIME_FORMAT4;
import static com.cybermed.cdoc_patient.util.AppConstant.DATE_TIME_FORMAT5;
import static com.cybermed.cdoc_patient.util.AppConstant.SERVER_DATE_FORMAT;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cdfortis.datainterface.soap.UserInfo;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.IOTActivity_MainPage;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealthGraph.GraphData;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealthGraph.GraphUI;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch.vital_graph.utility.Custom;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch.vital_graph.utility.Custom_Line;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch.vital_graph.utility.Custom_LineBP;
import com.cybermed.cdoc_patient.common.BaseFragment;
import com.cybermed.cdoc_patient.common.CDoctor2Application;
import com.cybermed.cdoc_patient.common.videoui.Constant;
import com.cybermed.cdoc_patient.databinding.AdapterClinicVitalBinding;
import com.cybermed.cdoc_patient.databinding.GraphAdapterLayoutBinding;
import com.cybermed.cdoc_patient.databinding.VitalCheckFregmentNewLayoutBinding;
import com.cybermed.cdoc_patient.me.manager.ProfileApiManager;
import com.cybermed.cdoc_patient.me.vitalcheck.adapter.ClinicVitalRecycleViewAdapter;
import com.cybermed.cdoc_patient.me.vitalcheck.model.ClinicVitaldata;
import com.cybermed.cdoc_patient.me.vitalcheck.model.ResponseVital;
import com.cybermed.cdoc_patient.me.vitalcheck.model.VitalData;
import com.cybermed.cdoc_patient.me.vitalcheck.model.VitalDataBP;
import com.cybermed.cdoc_patient.me.vitalcheck.model.VitalDataNew;
import com.cybermed.cdoc_patient.util.AppUtiltiy;
import com.cybermed.cdoc_patient.util.DateUtil;
import com.cybermed.cdoc_patient.webapi.IResponseReceiver;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
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


import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.databinding.FragmentVitalCheckUiBinding;

public class VitalChartFragment extends BaseFragment {
    Activity context;
    VitalCheckFregmentNewLayoutBinding binding;
    ClinicVitalRecycleViewAdapter clinicVitalRecycleViewAdapter;
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
    List<VitalDataNew> clinicVitalDataList;

    public static final String BP_DEVICE_TYPE = "IChoice_BP", PO_DEVICE_TYPE = "IChoice_Oximeter", GLUCOMETER_DEVICE_TYPE = "IChoice_Glucose", SCALE_DEVICE_TYPE = "IChoice_Scale";
    public static boolean isDeviceTablet;
    private final int[] colors = new int[]{Color.parseColor("#fe0000"),
            Color.parseColor("#750e72"),
            Color.parseColor("#53BD8B"),
            Color.parseColor("#F2727A"),
            Color.parseColor("#F79452"),
            Color.parseColor("#DDA827")};
    ArrayList<String> arrayGraphNameList=new ArrayList<>();
    GraphRecycleViewAdapter graphRecycleViewAdapter;
    GraphData graphData;

    Custom custom;
    ArrayList<String> device_value;


    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.vital_check_fregment_new_layout, container, false);
        return binding.getRoot();
    }

    @Override
    protected void initLayout(View view) {
        context = getActivity();

        callApi();
        clickListner();
        initVal();
        arrayGraphNameList.add("Blood Pressure");
        arrayGraphNameList.add("Heart Rate");
        arrayGraphNameList.add("Height");
        arrayGraphNameList.add("Weight");
        arrayGraphNameList.add("Glucose");
        arrayGraphNameList.add("Body Mass Index");
        arrayGraphNameList.add("Temperature");
        arrayGraphNameList.add("Head Circumference");
        arrayGraphNameList.add("Peak Flow");
        arrayGraphNameList.add("Hemoglobin");
        binding.graphView.setLayoutManager(new GridLayoutManager(context,2, RecyclerView.VERTICAL,false));
        graphRecycleViewAdapter = new GraphRecycleViewAdapter(arrayGraphNameList, context);
        binding.graphView.setAdapter(graphRecycleViewAdapter);

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
                ((IOTActivity_MainPage) getParentFragment().getParentFragment()).backPress();

            }
        });


    }

    @Override
    public void refreshFragment(boolean isRefresh) {
        super.refreshFragment(isRefresh);
    }



    //***************api call and success failure**************************************
    private void callApi() {
        /*Bundle args = getArguments();
        if (args != null) {*/
        //UserInfo userInfo = (UserInfo) args.getSerializable(USERINFOKEY);
        clinicVitalDataList=new ArrayList<>();
        UserInfo userInfo = CDoctor2Application.getLoginInfo().getUserInfo();
        if (userInfo != null) {
            showProgress();
            ProfileApiManager ClinicVitalManager = new ProfileApiManager(new IResponseReceiver() {
                @Override
                public void onSuccess(Object data) {
                    hideProgress();
                    ResponseVital responseVital = (ResponseVital) data;


                    if (data != null && responseVital.getClinicVitaldata().size() > 0) {
                        if (!Constant.istype.equals("Weight")){
                            ArrayList<VitalData> arr_Vector = new ArrayList<VitalData>();
                            for (int i = 0; i < responseVital.getClinicVitaldata().size(); i++) {

                                if (Constant.istype.equals("Height")) {
                                    if (!TextUtils.isEmpty(responseVital.getClinicVitaldata().get(i).getHeight())) {
                                        VitalData vitalDataHeight = new VitalData();
                                        vitalDataHeight.setEntry_date(DateUtil.formatedDate(responseVital.getClinicVitaldata().get(i).getVitalDate(), SERVER_DATE_FORMAT, DATE_TIME_FORMAT3));
                                        vitalDataHeight.setValue(String.valueOf(convertFtInToInches(responseVital.getClinicVitaldata().get(i).getHeight())));
                                        arr_Vector.add(vitalDataHeight);
                                    }
                                }  else if (Constant.istype.equals("Temp")) {
                                    if (!TextUtils.isEmpty(responseVital.getClinicVitaldata().get(i).getTemp())) {
                                        VitalData vitalDataGlucose = new VitalData();
                                        vitalDataGlucose.setEntry_date(DateUtil.formatedDate(responseVital.getClinicVitaldata().get(i).getVitalDate(), SERVER_DATE_FORMAT, DATE_TIME_FORMAT3));
                                        vitalDataGlucose.setValue(responseVital.getClinicVitaldata().get(i).getTemp().replace("°F", ""));
                                        arr_Vector.add(vitalDataGlucose);
                                    }

                                } else if (Constant.istype.equals("HGB")) {
                                    if (!TextUtils.isEmpty(responseVital.getClinicVitaldata().get(i).getHGB())) {
                                        VitalData vitalDataGlucose = new VitalData();
                                        vitalDataGlucose.setEntry_date(DateUtil.formatedDate(responseVital.getClinicVitaldata().get(i).getVitalDate(), SERVER_DATE_FORMAT, DATE_TIME_FORMAT3));
                                        vitalDataGlucose.setValue(responseVital.getClinicVitaldata().get(i).getHGB());
                                        arr_Vector.add(vitalDataGlucose);
                                    }

                                }else if (Constant.istype.equals("HC")) {
                                    if (!TextUtils.isEmpty(responseVital.getClinicVitaldata().get(i).getHC())) {
                                        VitalData vitalDataGlucose = new VitalData();
                                        vitalDataGlucose.setEntry_date(DateUtil.formatedDate(responseVital.getClinicVitaldata().get(i).getVitalDate(), SERVER_DATE_FORMAT, DATE_TIME_FORMAT3));
                                        vitalDataGlucose.setValue(responseVital.getClinicVitaldata().get(i).getHC());
                                        arr_Vector.add(vitalDataGlucose);
                                    }

                                }else if (Constant.istype.equals("Peak_Flow")) {
                                    if (!TextUtils.isEmpty(responseVital.getClinicVitaldata().get(i).getPeak_Flow())) {
                                        VitalData vitalDataGlucose = new VitalData();
                                        vitalDataGlucose.setEntry_date(DateUtil.formatedDate(responseVital.getClinicVitaldata().get(i).getVitalDate(), SERVER_DATE_FORMAT, DATE_TIME_FORMAT3));
                                        vitalDataGlucose.setValue(responseVital.getClinicVitaldata().get(i).getPeak_Flow());
                                        arr_Vector.add(vitalDataGlucose);
                                    }

                                }
                            }
                            if (arr_Vector.size() > 0) {
                                weight_dataVector.addAll(arr_Vector);
                                init_weight_graph(weight_dataVector);
                            }
                          //  updateGraph(TYPE.WEEKLY,);
                        }





                        Collections.reverse(responseVital.getClinicVitaldata());
                        setList(responseVital.getClinicVitaldata());
                    } else {

                    }
                }

                @Override
                public void onFailure(@NonNull String errorResponse) {
                    hideProgress();

                }
            }, context);
            ClinicVitalManager.getClinicVitalList(userInfo.getEmail());
            if (Constant.istype.equals("BP")||Constant.istype.equals("hr")||Constant.istype.equals("Glucose")||Constant.istype.equals("Weight")) {
                ProfileApiManager deviceVitalManager = new ProfileApiManager(new IResponseReceiver() {
                    @Override
                    public void onSuccess(Object data) {
                        hideProgress();
                        JsonObject responseVital = (JsonObject) data;
                        bp_dataVector = new ArrayList<>();
                        hr_dataVector = new ArrayList<>();
                        weight_dataVector = new ArrayList<>();
                        glucose_dataVector = new ArrayList<>();
                        bo_dataVector = new ArrayList<>();

                        if (data != null && responseVital.getAsJsonArray("data").size() > 0) {
                            try {
                                JSONArray jsonArray = new JSONArray(String.valueOf(responseVital.getAsJsonArray("data")));
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    if (jsonArray.getJSONObject(i).getString("device_type").equalsIgnoreCase(BP_DEVICE_TYPE)) {
                                        if (jsonArray.getJSONObject(i).getJSONArray("vital_records").length() != 0) {
                                            JSONArray jVital_record = jsonArray.getJSONObject(i).getJSONArray("vital_records");
                                            for (int j = 0; j < jVital_record.length(); j++) {
                                                VitalDataBP vitalDataBP = new VitalDataBP();
                                                VitalData vitalDatahr = new VitalData();
                                                String value = jVital_record.getJSONObject(j).getString("value");
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
                                    } else if (jsonArray.getJSONObject(i).getString("device_type").equalsIgnoreCase(SCALE_DEVICE_TYPE)) {
                                        if (jsonArray.getJSONObject(i).getJSONArray("vital_records").length() != 0) {
                                            JSONArray jVital_record = jsonArray.getJSONObject(i).getJSONArray("vital_records");
                                            for (int j = 0; j < jVital_record.length(); j++) {
                                                VitalData vitalDataWeight = new VitalData();
                                                String value = jVital_record.getJSONObject(j).getString("value");
                                                vitalDataWeight.setValue(value);
                                                vitalDataWeight.setEntry_date(DateUtil.formatedDate(jVital_record.getJSONObject(j).getString("entry_date"), SERVER_DATE_FORMAT, DATE_TIME_FORMAT3));
                                                weight_dataVector.add(vitalDataWeight);
                                            }
                                        }
                                    } else if (jsonArray.getJSONObject(i).getString("device_type").equalsIgnoreCase(GLUCOMETER_DEVICE_TYPE)) {
                                        if (jsonArray.getJSONObject(i).getJSONArray("vital_records").length() != 0) {
                                            JSONArray jVital_record = jsonArray.getJSONObject(i).getJSONArray("vital_records");
                                            for (int j = 0; j < jVital_record.length(); j++) {
                                                VitalData vitalDataGlucose = new VitalData();
                                                String value = jVital_record.getJSONObject(j).getString("value");
                                                vitalDataGlucose.setValue(value);
                                                vitalDataGlucose.setEntry_date(DateUtil.formatedDate(jVital_record.getJSONObject(j).getString("entry_date"), SERVER_DATE_FORMAT, DATE_TIME_FORMAT3));
                                                glucose_dataVector.add(vitalDataGlucose);
                                            }
                                        }
                                    } else if (jsonArray.getJSONObject(i).getString("device_type").equalsIgnoreCase(PO_DEVICE_TYPE)) {
                                        if (jsonArray.getJSONObject(i).getJSONArray("vital_records").length() != 0) {
                                            JSONArray jVital_record = jsonArray.getJSONObject(i).getJSONArray("vital_records");
                                            for (int j = 0; j < jVital_record.length(); j++) {
                                                VitalData vitalDataBO = new VitalData();
                                                String value = jVital_record.getJSONObject(j).getString("value");
                                                vitalDataBO.setValue(value);
                                                vitalDataBO.setEntry_date(DateUtil.formatedDate(jVital_record.getJSONObject(j).getString("entry_date"), SERVER_DATE_FORMAT, DATE_TIME_FORMAT3));
                                                bo_dataVector.add(vitalDataBO);
                                            }
                                        }
                                    }
                                }
                                if (weight_dataVector.size() > 0) {
                                    init_weight_graph(weight_dataVector);
                                }
                                if (bo_dataVector.size() > 0) {
                                    init_bo_graph(bo_dataVector);
                                }
                                if (hr_dataVector.size() > 0) {
                                    init_HR_graph(hr_dataVector);
                                }
                                if (glucose_dataVector.size() > 0) {
                                    init_glucose_graph(glucose_dataVector);
                                }
                                if (bp_dataVector.size() > 0) {
                                    init_Blood_Pressure_Graph(bp_dataVector);
                                }
                               // updateGraph(TYPE.Daily);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            //updateGraph(TYPE.Daily);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull String errorResponse) {
                        hideProgress();

                        // binding.emptyLayout.setVisibility(View.VISIBLE);
                     //   updateGraph(TYPE.Daily);
                    }
                }, context);
                deviceVitalManager.getDeviceVitalList(userInfo.getEmail());
            }
            else if (Constant.istype.isEmpty()){
                ProfileApiManager deviceVitalManager = new ProfileApiManager(new IResponseReceiver() {
                    @Override
                    public void onSuccess(Object data) {
                        hideProgress();
                        JsonObject responseVital = (JsonObject) data;
                        bp_dataVector = new ArrayList<>();
                        hr_dataVector = new ArrayList<>();
                        weight_dataVector = new ArrayList<>();
                        glucose_dataVector = new ArrayList<>();
                        bo_dataVector = new ArrayList<>();

                        if (data != null && responseVital.getAsJsonArray("data").size() > 0) {
                            try {
                                JSONArray jsonArray = new JSONArray(String.valueOf(responseVital.getAsJsonArray("data")));
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    if (jsonArray.getJSONObject(i).getString("device_type").equalsIgnoreCase(BP_DEVICE_TYPE)) {
                                        if (jsonArray.getJSONObject(i).getJSONArray("vital_records").length() != 0) {
                                            JSONArray jVital_record = jsonArray.getJSONObject(i).getJSONArray("vital_records");
                                            for (int j = 0; j < jVital_record.length(); j++) {
                                                VitalDataBP vitalDataBP = new VitalDataBP();
                                                VitalData vitalDatahr = new VitalData();
                                                String value = jVital_record.getJSONObject(j).getString("value");
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
                                    } else if (jsonArray.getJSONObject(i).getString("device_type").equalsIgnoreCase(SCALE_DEVICE_TYPE)) {
                                        if (jsonArray.getJSONObject(i).getJSONArray("vital_records").length() != 0) {
                                            JSONArray jVital_record = jsonArray.getJSONObject(i).getJSONArray("vital_records");
                                            for (int j = 0; j < jVital_record.length(); j++) {
                                                VitalData vitalDataWeight = new VitalData();
                                                String value = jVital_record.getJSONObject(j).getString("value");
                                                vitalDataWeight.setValue(value);
                                                vitalDataWeight.setEntry_date(DateUtil.formatedDate(jVital_record.getJSONObject(j).getString("entry_date"), SERVER_DATE_FORMAT, DATE_TIME_FORMAT3));
                                                weight_dataVector.add(vitalDataWeight);
                                            }
                                        }
                                    } else if (jsonArray.getJSONObject(i).getString("device_type").equalsIgnoreCase(GLUCOMETER_DEVICE_TYPE)) {
                                        if (jsonArray.getJSONObject(i).getJSONArray("vital_records").length() != 0) {
                                            JSONArray jVital_record = jsonArray.getJSONObject(i).getJSONArray("vital_records");
                                            for (int j = 0; j < jVital_record.length(); j++) {
                                                VitalData vitalDataGlucose = new VitalData();
                                                String value = jVital_record.getJSONObject(j).getString("value");
                                                vitalDataGlucose.setValue(value);
                                                vitalDataGlucose.setEntry_date(DateUtil.formatedDate(jVital_record.getJSONObject(j).getString("entry_date"), SERVER_DATE_FORMAT, DATE_TIME_FORMAT3));
                                                glucose_dataVector.add(vitalDataGlucose);
                                            }
                                        }
                                    } else if (jsonArray.getJSONObject(i).getString("device_type").equalsIgnoreCase(PO_DEVICE_TYPE)) {
                                        if (jsonArray.getJSONObject(i).getJSONArray("vital_records").length() != 0) {
                                            JSONArray jVital_record = jsonArray.getJSONObject(i).getJSONArray("vital_records");
                                            for (int j = 0; j < jVital_record.length(); j++) {
                                                VitalData vitalDataBO = new VitalData();
                                                String value = jVital_record.getJSONObject(j).getString("value");
                                                vitalDataBO.setValue(value);
                                                vitalDataBO.setEntry_date(DateUtil.formatedDate(jVital_record.getJSONObject(j).getString("entry_date"), SERVER_DATE_FORMAT, DATE_TIME_FORMAT3));
                                                bo_dataVector.add(vitalDataBO);
                                            }
                                        }
                                    }
                                }
                                if (weight_dataVector.size() > 0) {
                                    init_weight_graph(weight_dataVector);
                                }
                                if (bo_dataVector.size() > 0) {
                                    init_bo_graph(bo_dataVector);
                                }
                                if (hr_dataVector.size() > 0) {
                                    init_HR_graph(hr_dataVector);
                                }
                                if (glucose_dataVector.size() > 0) {
                                    init_glucose_graph(glucose_dataVector);
                                }
                                if (bp_dataVector.size() > 0) {
                                    init_Blood_Pressure_Graph(bp_dataVector);
                                }
                              //  updateGraph(TYPE.Daily);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            //updateGraph(TYPE.Daily);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull String errorResponse) {
                        hideProgress();
                        // binding.emptyLayout.setVisibility(View.VISIBLE);
                        //updateGraph(TYPE.Daily);
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

            hr_datapoints = vectorToArray2(vector, hr_timestamp);


        }
    }

    public void init_weight_graph(ArrayList<VitalData> vector) {
        if (vector != null) {
            weight_timestamp = new String[vector.size()];
            String[] data = new String[vector.size()];
            weight_datapoints = vectorToArray2(vector, weight_timestamp);
        }
    }

    public void init_bo_graph(ArrayList<VitalData> vector) {
        if (vector != null) {
            bo_timestamp = new String[vector.size()];
            String[] data = new String[vector.size()];
            bo_datapoints = vectorToArray2(vector, bo_timestamp);
        }
    }

    public void init_glucose_graph(ArrayList<VitalData> vector) {
        if (vector != null) {
            glucose_timestamp = new String[vector.size()];
            String[] data = new String[vector.size()];
            glucose_datapoints = vectorToArray2(vector, glucose_timestamp);
        }
    }


    public String[] vectorToArray(int index, VitalData soapObjectData, String[] data, String[] timeStamp_value) {
        data[index] = Double.toString(Double.parseDouble(soapObjectData.getValue()));
        timeStamp_value[index] = soapObjectData.getEntry_date();

        return data;
    }

    public String[] vectorToArray2(ArrayList<VitalData> soapObjectData, String[] timeStamp_value) {
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
        int currentMonth = currentCalendar.get(Calendar.MONTH) + 1;
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

    public static double convertToLbs(String weightString) {
        // Check if the input contains both pounds and ounces
        if (weightString.contains("lbs")) {

            // Split the string by space to separate pounds and ounces
            String[] parts = weightString.split(" ");
            double pounds = Double.parseDouble(parts[0].replace("lbs", "")); // First part is pounds
            double ounces = Double.parseDouble(parts[1].replace("oz", ""));  // Second part is ounces

            // Convert ounces to pounds (1 lb = 16 oz) and sum with pounds
            double totalPounds = pounds + (ounces / 16);

            // Return total weight in pounds
            return totalPounds;

        } else {
            // If the input contains only pounds
            String poundsPart = weightString.replace("lbs", "").trim();

            // Convert the string to a double (already in pounds)
            double pounds = Double.parseDouble(poundsPart);

            // Return weight in pounds
            return pounds;

        }
    }

    public double convertFtInToInches(String heightFtIn) {
        // Check if the input contains "ft" (for feet and inches case)
        if (heightFtIn.contains("ft")) {
            // Split the string by space to separate feet and inches
            String[] parts = heightFtIn.split(" ");

            // Extract feet and inches
            int feet = Integer.parseInt(parts[0].replace("ft", "")); // First part is feet
            int inches = Integer.parseInt(parts[1].replace("in", "")); // Second part is inches

            // Convert feet to inches and sum with inches
            int totalInches = (feet * 12) + inches;

            // Return total height in inches
            return totalInches;

        } else {
            // If the input only contains inches
            String inchesPart = heightFtIn.replace("in", "").trim();

            // Convert the string directly to an integer (it's already in inches)
            int inches = Integer.parseInt(inchesPart);

            // Return height in inches
            return inches;

        }
    }

    public class GraphRecycleViewAdapter extends RecyclerView.Adapter<GraphRecycleViewAdapter.MyViewHolder>{

        List<String> clinicVitalDataList;
        Context context;


        public GraphRecycleViewAdapter(List<String> clinicVitaldata, Context context) {
            this.clinicVitalDataList = clinicVitaldata;
            this.context = context;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            GraphAdapterLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.graph_adapter_layout, parent, false);
            return new MyViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            String CategoryName = clinicVitalDataList.get(position);
            holder.binding.tvCategoryUnit.setText(CategoryName);
            GraphUI.decorateGraph3(holder.binding);
            updateGraph(WEEKLY, holder.binding.categoryChart,CategoryName);
        }

        @Override
        public int getItemCount() {
            return clinicVitalDataList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            GraphAdapterLayoutBinding binding = null;
            MyViewHolder(GraphAdapterLayoutBinding itemView) {
                super(itemView.getRoot());
                this.binding = itemView;

            }
        }

    }

    public void updateGraph(String type, LineChart lineChart,String name) {
        lineChart.clear();
        switch (type) {
            case WEEKLY: {

                if (hr_dataVector.size() > 0 && name.equals("Heart Rate")) {
                    List<VitalData> hr_data = new ArrayList<>();
                    Map<String, Double> dateToSum = new HashMap<>();
                    Map<String, Integer> dateToCount = new HashMap<>();

                    for (int l = 0; l < hr_dataVector.size(); l++) {
                        VitalData vitalData = new VitalData();
                        vitalData.setValue(hr_dataVector.get(l).getValue());
                        vitalData.setEntry_date(DateUtil.formatedDate(hr_dataVector.get(l).getEntry_date(), DATE_TIME_FORMAT3, DATE_TIME_FORMAT4));
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
                        VitalData vitalData = new VitalData();
                        vitalData.setValue(average);
                        vitalData.setEntry_date(date);
                        newHr_data.add(vitalData);
                    }
                    if (newHr_data.size() > 0) {
                        String[] newhr_datapoints = new String[newHr_data.size()];
                        String[] newhr_timestamp = new String[newHr_data.size()];

                        for (int i = 0; i < newHr_data.size(); i++) {
                            newhr_datapoints[i] = newHr_data.get(i).getValue();
                            newhr_timestamp[i] = DateUtil.formatedDate(newHr_data.get(i).getEntry_date(), DATE_TIME_FORMAT4, DATE_TIME_FORMAT3);
                        }
                        Custom_Line customLine = graphData.getWeekLineData2(newhr_datapoints, newHr_data.size(), new int[]{colors[1]}, newhr_timestamp, lineChart);
                        GraphUI.updateWeekData(customLine, lineChart);
                        lineChart.getAxisLeft().setStartAtZero(false);
                        lineChart.animateX(1000, Easing.EaseInCubic);
                    }
                }

                if (glucose_dataVector.size() > 0&& name.equals("Glucose")) {
                    List<VitalData> glocose_data = new ArrayList<>();
                    Map<String, Double> dateToSum = new HashMap<>();
                    Map<String, Integer> dateToCount = new HashMap<>();

                    for (int l = 0; l < glucose_dataVector.size(); l++) {
                        VitalData vitalData = new VitalData();
                        vitalData.setValue(glucose_dataVector.get(l).getValue());
                        vitalData.setEntry_date(DateUtil.formatedDate(glucose_dataVector.get(l).getEntry_date(), DATE_TIME_FORMAT3, DATE_TIME_FORMAT4));
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
                        VitalData vitalData = new VitalData();
                        vitalData.setValue(average);
                        vitalData.setEntry_date(date);
                        newGl_data.add(vitalData);
                    }
                    if (newGl_data.size() > 0) {
                        String[] newgl_datapoints = new String[newGl_data.size()];
                        String[] newgl_timestamp = new String[newGl_data.size()];

                        for (int i = 0; i < newGl_data.size(); i++) {
                            newgl_datapoints[i] = newGl_data.get(i).getValue();
                            newgl_timestamp[i] = DateUtil.formatedDate(newGl_data.get(i).getEntry_date(), DATE_TIME_FORMAT4, DATE_TIME_FORMAT3);
                        }
                        Custom_Line customLine = graphData.getWeekLineData2(newgl_datapoints, newGl_data.size(), new int[]{colors[1]}, newgl_timestamp, lineChart);
                        GraphUI.updateWeekData(customLine, lineChart);
                        lineChart.getAxisLeft().setStartAtZero(false);
                        lineChart.animateX(1000, Easing.EaseInCubic);
                    }
                }



                if (weight_dataVector.size() > 0 && name.equals("Weight")) {
                    List<VitalData> weight_data = new ArrayList<>();
                    Map<String, Double> dateToSum = new HashMap<>();
                    Map<String, Integer> dateToCount = new HashMap<>();

                    for (int l = 0; l < weight_dataVector.size(); l++) {
                        VitalData vitalData = new VitalData();
                        vitalData.setValue(weight_dataVector.get(l).getValue());
                        vitalData.setEntry_date(DateUtil.formatedDate(weight_dataVector.get(l).getEntry_date(), DATE_TIME_FORMAT3, DATE_TIME_FORMAT4));
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
                        VitalData vitalData = new VitalData();
                        vitalData.setValue(average);
                        vitalData.setEntry_date(date);
                        newweiht_data.add(vitalData);
                    }
                    if (newweiht_data.size() > 0) {
                        String[] newweight_datapoints = new String[newweiht_data.size()];
                        String[] newweight_timestamp = new String[newweiht_data.size()];

                        for (int i = 0; i < newweiht_data.size(); i++) {
                            newweight_datapoints[i] = newweiht_data.get(i).getValue();
                            newweight_timestamp[i] = DateUtil.formatedDate(newweiht_data.get(i).getEntry_date(), DATE_TIME_FORMAT4, DATE_TIME_FORMAT3);
                        }
                        Custom_Line customLine = graphData.getWeekLineData2(newweight_datapoints, newweiht_data.size(), new int[]{colors[1]}, newweight_timestamp, lineChart);
                        GraphUI.updateWeekData(customLine, lineChart);
                        lineChart.getAxisLeft().setStartAtZero(false);
                        lineChart.animateX(1000, Easing.EaseInCubic);
                    }
                }

                if (bp_dataVector.size() > 0 && name.equals("Blood Pressure")) {
                    List<VitalDataBP> bp_data = new ArrayList<>();
                   /* Map<String, Double> dateToSum = new HashMap<>();
                    Map<String, Integer> dateToCount = new HashMap<>();*/
                    Map<String, List<Double>> dateTohigh = new HashMap<>();
                    Map<String, List<Double>> dateTolow = new HashMap<>();
                    List<Date> days = getAllDaysOfTheCurrentWeek();
                    SimpleDateFormat format2 = new SimpleDateFormat("EEE MMM dd yyyy", Locale.ENGLISH);
                    for (int m = 0; m < days.size(); m++) {
                        for (int l = 0; l < bp_dataVector.size(); l++) {
                            String days1 = format2.format(days.get(m));
                            String days2 = DateUtil.formatedDate(bp_dataVector.get(l).getEntry_date(), DATE_TIME_FORMAT3, DATE_TIME_FORMAT5);
                            if (days1.equalsIgnoreCase(days2)) {
                                VitalDataBP vitalData = new VitalDataBP();
                                vitalData.setBplow(bp_dataVector.get(l).getBplow());
                                vitalData.setBphigh(bp_dataVector.get(l).getBphigh());
                                vitalData.setEntry_date(DateUtil.formatedDate(bp_dataVector.get(l).getEntry_date(), DATE_TIME_FORMAT3, DATE_TIME_FORMAT4));
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
                        VitalDataBP vitalData = new VitalDataBP();
                        vitalData.setBplow(String.valueOf(averageDiastolic));
                        vitalData.setBphigh(String.valueOf(averageSystolic));
                        vitalData.setEntry_date(date);
                        newbp_data.add(vitalData);
                        newbp_data2.add(vitalData);
                    }
                    if (newbp_data.size() > 0) {
                        String[] newbp_timestamp = new String[newbp_data.size()];
                        String[] newbph_dataPoints = new String[newbp_data.size()];
                        String[] newbpl_dataPoints = new String[newbp_data.size()];
                        for (int i = 0; i < newbp_data.size(); i++) {
                            newbph_dataPoints[i] = newbp_data.get(i).getBphigh();
                            newbpl_dataPoints[i] = newbp_data.get(i).getBplow();
                            newbp_timestamp[i] = newbp_data.get(i).getEntry_date();
                        }

                        Custom_LineBP customLine = graphData.getdefaultBPData2(newbph_dataPoints, newbpl_dataPoints, newbp_data2, new int[]{colors[1], colors[0]}, newbp_timestamp, "weekly", lineChart);
                        GraphUI.updateWeekData2(customLine, lineChart);
                        lineChart.getAxisLeft().setStartAtZero(false);
                        lineChart.animateX(1000, Easing.EaseInCubic);
                    }
                }

                if (bo_dataVector.size() > 0 ) {
                    List<VitalData> bo_data = new ArrayList<>();
                    Map<String, Double> dateToSum = new HashMap<>();
                    Map<String, Integer> dateToCount = new HashMap<>();

                    for (int l = 0; l < bo_dataVector.size(); l++) {
                        VitalData vitalData = new VitalData();
                        vitalData.setValue(bo_dataVector.get(l).getValue());
                        vitalData.setEntry_date(DateUtil.formatedDate(bo_dataVector.get(l).getEntry_date(), DATE_TIME_FORMAT3, DATE_TIME_FORMAT4));
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
                        VitalData vitalData = new VitalData();
                        vitalData.setValue(average);
                        vitalData.setEntry_date(date);
                        newbo_data.add(vitalData);
                    }
                    if (newbo_data.size() > 0) {
                        String[] newbo_datapoints = new String[newbo_data.size()];
                        String[] newbo_timestamp = new String[newbo_data.size()];

                        for (int i = 0; i < newbo_data.size(); i++) {
                            newbo_datapoints[i] = newbo_data.get(i).getValue();
                            newbo_timestamp[i] = DateUtil.formatedDate(newbo_data.get(i).getEntry_date(), DATE_TIME_FORMAT4, DATE_TIME_FORMAT3);
                        }

                        Custom_Line customLine = graphData.getWeekLineData2(newbo_datapoints, newbo_data.size(), new int[]{colors[1]}, newbo_timestamp, lineChart);
                        GraphUI.updateWeekData(customLine, lineChart);
                        lineChart.getAxisLeft().setStartAtZero(false);
                        lineChart.animateX(1000, Easing.EaseInCubic);
                    }
                }
                break;
            }
        }
        lineChart.invalidate();
        //   hideProgress();
    }

}
