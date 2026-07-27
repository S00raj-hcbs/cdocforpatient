package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealthGraph;


import static com.cdfortis.datainterface.soap.WebService.WSInstance;
import static com.cdfortis.datainterface.soap.WebServiceID.get_patient_health_records_v2;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment.IHEALTH_MAC_ADDR;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment.STEMOSCOPE;
import static com.cybermed.cdoc_patient.util.AppConstant.IOT_GRAPH_IOT;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.Navigation;

import com.cdfortis.datainterface.annotation.DataField;
import com.cdfortis.datainterface.soap.UserInfo;
import com.cdfortis.datainterface.soap.model.Monitor_BO;
import com.cdfortis.datainterface.soap.model.Monitor_Glucose;
import com.cdfortis.datainterface.soap.model.Monitor_HR;
import com.cdfortis.datainterface.soap.model.Monitor_Weight;
import com.cdfortis.datainterface.soap.model.SoapObjectData;
import com.cdfortis.datainterface.soap.model.SoapObjectVector;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.annotation.Factory;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch.vital_graph.utility.Custom;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch.vital_graph.utility.Custom_Line;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch.vital_graph.utility.TYPE;
import com.cybermed.cdoc_patient.common.BaseFragment;
import com.cybermed.cdoc_patient.common.CDoctor2Application;
import com.cybermed.cdoc_patient.databinding.GraphHistoryBinding;
import com.cybermed.cdoc_patient.me.MeFragment;
import com.cybermed.cdoc_patient.util.AppConstant;
import com.cybermed.cdoc_patient.util.AppUtiltiy;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.ksoap2.serialization.SoapObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class IOTGraph extends BaseFragment {
    GraphHistoryBinding binding;
    UserInfo userInfo;
    private Vector<Bp_data> bp_dataVector;

    String[] bph_dataPoints;
    String[] bpl_dataPoints;
    String[] hr_datapoints;
    private String[] weight_datapoints;
    private String[] bo_datapoints;
    private String[] glucose_datapoints;
    private String[] bp_timestamp, hr_timestamp, weight_timestamp, bo_timestamp, glucose_timestamp;
    private static final int BPL = 1;
    private static final int BPH = 2;
    Vector<Monitor_HR> hr_dataVector;
    private Vector<Monitor_Weight> weight_dataVector;
    private Vector<Monitor_BO> bo_dataVector;
    private Vector<Monitor_Glucose> glucose_dataVector;
    public static final String VALUE = "Value";
    public static final String TIMESTAMP = "TIMESTAMP";
    public static final String MEASUREMENT1 = "MEASUREMENT1";
    public static final String MEASUREMENT2 = "MEASUREMENT2";


    String value = "";
    String measuremt1 = "";
    String meaurement_time = "";
    String measurement2 = "";
    private static final String HR = "1";
    private static final String WEIGHT = "2";
    private static final String BP = "3";
    public static final String BLOOD_OXYGEN = "5";
    public static final String GLUCOSE = "6";
    public static final String BP_DEVICE_TYPE = "IChoice_BP", PO_DEVICE_TYPE = "IChoice_Oximeter", GLUCOMETER_DEVICE_TYPE = "IChoice_Glucose", SCALE_DEVICE_TYPE = "IChoice_Scale";
    public static boolean isDeviceTablet;
    private final int[] colors = new int[]{Color.parseColor("#6870B5"),
            Color.parseColor("#8969AE"),
            Color.parseColor("#53BD8B"),
            Color.parseColor("#F7C758"),
            Color.parseColor("#F79452"),
            Color.parseColor("#DDA827")};


    GraphData graphData;
    Custom custom;
    Disposable refresh_disposable;
    ArrayList<String> device_value;
    Context mContext;

    @Factory
    public static IOTGraph newInstance(String type, String time_Stamp, String measurement_1, String measurement_2) {
        IOTGraph fragment = new IOTGraph();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putString(VALUE, type);
        args.putString(TIMESTAMP, time_Stamp);
        args.putString(MEASUREMENT1, measurement_1);
        args.putString(MEASUREMENT2, measurement_2);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.graph_history, container, false);

        return binding.getRoot();
    }

    @Override
    protected void initLayout(View view) {
        showProgress();
        getIntentValues();
        userInfo = CDoctor2Application.getLoginInfo().getUserInfo();
        mContext = getActivity();
        initVal();
        GraphUI.decorateGraph(binding);

       //show graph icon for tablet mode only
        if (CDoctor2Application.getTabletMode() && !TextUtils.isEmpty(value)) {
            binding.btnMeasure.setVisibility(View.VISIBLE);
            binding.toolBar.icImg2.setVisibility(View.VISIBLE);
            binding.toolBar.icImg2.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.graph_icon));
            binding.toolBar.icImg2.setBackground(ContextCompat.getDrawable(mContext, R.drawable.circle_white));
        } else {
            binding.toolBar.icImg2.setVisibility(View.GONE);
            binding.btnMeasure.setVisibility(View.GONE);
        }
        //show current reading ui incase of measurment only
        if (TextUtils.isEmpty(value)) {
            binding.graphInfo.topLayout.setVisibility(View.GONE);
            checkDeviceType(PO_DEVICE_TYPE);
            checkDeviceType(GLUCOMETER_DEVICE_TYPE);
            checkDeviceType(BP_DEVICE_TYPE);
            checkDeviceType(SCALE_DEVICE_TYPE);
            GraphUI.updateUIData(PO_DEVICE_TYPE, meaurement_time, measuremt1, measurement2, binding);
            GraphUI.updateUIData(GLUCOMETER_DEVICE_TYPE, meaurement_time, measuremt1, measurement2, binding);
            GraphUI.updateUIData(BP_DEVICE_TYPE, meaurement_time, measuremt1, measurement2, binding);
            GraphUI.updateUIData(SCALE_DEVICE_TYPE, meaurement_time, measuremt1, measurement2, binding);
        } else {
            if (!CDoctor2Application.getTabletMode()) {
                binding.graphInfo.topLayout.setVisibility(View.VISIBLE);
            }
            checkDeviceType(value);
            GraphUI.updateUIData(value, meaurement_time, measuremt1, measurement2, binding);
        }

        binding.refreshLayout.setOnRefreshListener(() -> {
            EventBus.getDefault().post(new RequestBPRefresh());
        });
        binding.graphInfo.allBtn.setVisibility(View.VISIBLE);
        clickListener();
        initToolBar(value);
        binding.btnMeasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                if (getArguments() != null) {
                    args.putString(IHEALTH_MAC_ADDR, getArguments().getString(IHEALTH_MAC_ADDR));
                    if (!TextUtils.isEmpty(value)) {
                        if (BP_DEVICE_TYPE.equalsIgnoreCase(value)) {//args.putString(IHEALTH_MAC_ADDR, "F0:C7:7F:4C:B8:F7");
                            Navigation.findNavController(view).navigate(R.id.action_tabletMainFragment_to_iHealthBp3LFragment, args);
                        } else if (PO_DEVICE_TYPE.equalsIgnoreCase(value)) {//args.putString(IHEALTH_MAC_ADDR, "94:E3:6D:56:D9:0C");
                            Navigation.findNavController(view).navigate(R.id.action_tabletMainFragment_to_iHealthPulseOxiFragment, args);
                        } else if (SCALE_DEVICE_TYPE.equalsIgnoreCase(value)) {
                            Navigation.findNavController(view).navigate(R.id.action_tabletMainFragment_to_iHealthScaleFragment, args);
                        } else if (GLUCOMETER_DEVICE_TYPE.equalsIgnoreCase(value)) {//args.putString(IHEALTH_MAC_ADDR, "34:81:F4:3E:DA:DD");
                            Navigation.findNavController(view).navigate(R.id.action_tabletMainFragment_to_iHealthBG5Fragment, args);
                        } else if (STEMOSCOPE.equalsIgnoreCase(value)) {//args.putString(IHEALTH_MAC_ADDR, args.getString(IHEALTH_MAC_ADDR));
                            Navigation.findNavController(view).navigate(R.id.action_tabletMainFragment_to_stemoscopeFragment, args);
                        }
                    }
                }

            }
        });

    }

    /**
     * initialize value
     */
    private void initVal() {
        device_value = new ArrayList<>();
        graphData = new GraphData();
        bo_dataVector = new Vector<>();
        glucose_dataVector = new Vector<>();
        weight_dataVector = new Vector<>();
        hr_dataVector = new Vector<>();
        bp_dataVector = new Vector<>();
        hr_dataVector = new Vector<>();
        isDeviceTablet = AppUtiltiy.isDeviceTablet(getActivity());
    }

    /**
     * get argument values
     */
    private void getIntentValues() {
        if (getArguments() != null) {
            value = getArguments().getString(VALUE);
            meaurement_time = getArguments().getString(TIMESTAMP);
            measuremt1 = getArguments().getString(MEASUREMENT1);
            measurement2 = getArguments().getString(MEASUREMENT2);
        }
    }

    /**
     * setup toolbar
     *
     * @param input name on bar
     */
    public void initToolBar(String input) {
        if (input.equalsIgnoreCase(PO_DEVICE_TYPE)) {
            binding.toolBar.txtTittle.setText(R.string.pulse_oxi_history);
        } else if (input.equalsIgnoreCase(GLUCOMETER_DEVICE_TYPE)) {
            binding.toolBar.txtTittle.setText(R.string.glucose_history);
        } else if (input.equalsIgnoreCase(BP_DEVICE_TYPE)) {
            binding.toolBar.txtTittle.setText(R.string.blood_pressure_history);
        } else if (input.equalsIgnoreCase(SCALE_DEVICE_TYPE)) {
            binding.toolBar.txtTittle.setText(R.string.weight_scale_history);
        } else {
            binding.toolBar.txtTittle.setText(R.string.health_records);
        }
        //click of graph image
        binding.toolBar.icImg2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString(IOT_GRAPH_IOT, value);
                bundle.putString(TIMESTAMP, Long.toString(System.currentTimeMillis() / 1000));
                bundle.putString(MEASUREMENT1, "");
                bundle.putString(MEASUREMENT2, "");
                bundle.putString(VALUE, "");
                Navigation.findNavController(view).navigate(R.id.action_iotgraph_to_iotgraph, bundle);
            }
        });
        binding.toolBar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CDoctor2Application.getTabletMode()) {
                    //back handle of tablet mode
                    Navigation.findNavController(v).navigate(R.id.action_iotgraph_to_TabletMainFragment);
                } else if (getArguments() != null && !TextUtils.isEmpty(getArguments().getString(AppConstant.IOT_GRAPH_IOT))) {
                    //back handle of all graph page
                    Bundle bundle = new Bundle();
                    bundle.putString(TIMESTAMP, Long.toString(System.currentTimeMillis() / 1000));
                    bundle.putString(MEASUREMENT1, "");
                    bundle.putString(MEASUREMENT2, "");
                    bundle.putString(VALUE, getArguments().getString(AppConstant.IOT_GRAPH_IOT));
                    Navigation.findNavController(v).navigate(R.id.action_iotgraph_to_iotgraph, bundle);
                } else if (getArguments() != null && getArguments().getBoolean(AppConstant.FROM_USER, false)) {
                    //back handle from user profile page
                    if (((MeFragment) getParentFragment() != null)) {
                        ((MeFragment) getParentFragment()).openUserActivityFragment();
                    }

                } else if (value != null)
                    //back handle from Iot device list page
                    Navigation.findNavController(v).navigate(R.id.action_iotgraph_to_IOT_MainPage_Fragment);
            }
        });
    }

    private void clickListener() {
        binding.graphInfo.allBtn.setOnClickListener(v -> updateGraph(TYPE.All));
        binding.graphInfo.weekBtn.setOnClickListener(v -> updateGraph(TYPE.WEEKLY));
        binding.graphInfo.monthBtn.setOnClickListener(v -> updateGraph(TYPE.MONTHLY));
        binding.graphInfo.yearBtn.setOnClickListener(v -> updateGraph(TYPE.YEARLY));
    }

    public void checkDeviceType(String input) {
        if (input.equalsIgnoreCase(PO_DEVICE_TYPE)) {
            device_value.add(0, HR);
            device_value.add(1, BLOOD_OXYGEN);
        } else if (input.equalsIgnoreCase(GLUCOMETER_DEVICE_TYPE)) {
            device_value.add(0, GLUCOSE);
        } else if (input.equalsIgnoreCase(BP_DEVICE_TYPE)) {
            device_value.add(0, BP);
        } else if (input.equalsIgnoreCase(SCALE_DEVICE_TYPE)) {
            device_value.add(0, WEIGHT);
        }
    }

    public void updateGraph(String type) {
        binding.heartRateChart.clear();
        binding.bloodOxygenChart.clear();
        binding.WeightChart.clear();
        binding.GlucoseChart.clear();
        binding.bloodPressureChart.clear();
        switch (type) {
            case TYPE.WEEKLY:
                GraphUI.setAppearance(binding.graphInfo.weekBtn,binding.graphInfo.allBtn, binding.graphInfo.monthBtn, binding.graphInfo.yearBtn);
                if (PO_DEVICE_TYPE.equalsIgnoreCase(value)) {
                    Custom_Line customLine = graphData.getWeekLineDataold(hr_datapoints, hr_dataVector.size(), new int[]{colors[1]}, hr_timestamp);
                    GraphUI.updateWeekDataNew(customLine, binding.heartRateChart);
                    custom = graphData.getWeekData(bo_datapoints, bo_dataVector.size(), new int[]{colors[3]}, bo_timestamp);
                    GraphUI.updateWeekData2(custom, binding.bloodOxygenChart);
                } else if (GLUCOMETER_DEVICE_TYPE.equalsIgnoreCase(value)) {
                    Custom_Line customLine;
                    customLine = graphData.getWeekLineDataold(glucose_datapoints, glucose_dataVector.size(), new int[]{colors[4]}, glucose_timestamp);
                    GraphUI.updateWeekDataNew(customLine, binding.GlucoseChart);
                } else if (BP_DEVICE_TYPE.equalsIgnoreCase(value)) {
                    custom = graphData.getWeekBPData(bph_dataPoints, bpl_dataPoints, bp_dataVector, bp_timestamp);
                    GraphUI.updateWeekData2(custom, binding.bloodPressureChart);
                } else if (SCALE_DEVICE_TYPE.equalsIgnoreCase(value)) {
                    custom = graphData.getWeekData(weight_datapoints, weight_dataVector.size(), new int[]{colors[2]}, weight_timestamp);
                    GraphUI.updateWeekData2(custom, binding.WeightChart);
                } else if ("".equalsIgnoreCase(value)) {
                    Custom_Line customLine;
                    customLine = graphData.getWeekLineDataold(hr_datapoints, hr_dataVector.size(), new int[]{colors[1]}, hr_timestamp);
                    GraphUI.updateWeekDataNew(customLine, binding.heartRateChart);
                    custom = graphData.getWeekData(bo_datapoints, bo_dataVector.size(), new int[]{colors[3]}, bo_timestamp);
                    GraphUI.updateWeekData2(custom, binding.bloodOxygenChart);
                    customLine = graphData.getWeekLineDataold(glucose_datapoints, glucose_dataVector.size(), new int[]{colors[4]}, glucose_timestamp);
                    GraphUI.updateWeekDataNew(customLine, binding.GlucoseChart);
                    custom = graphData.getWeekBPData(bph_dataPoints, bpl_dataPoints, bp_dataVector, bp_timestamp);
                    GraphUI.updateWeekData2(custom, binding.bloodPressureChart);
                    custom = graphData.getWeekData(weight_datapoints, weight_dataVector.size(), new int[]{colors[2]}, weight_timestamp);
                    GraphUI.updateWeekData2(custom, binding.WeightChart);
                }


                break;
            case TYPE.All:
                GraphUI.setAppearance(binding.graphInfo.allBtn,binding.graphInfo.weekBtn, binding.graphInfo.monthBtn, binding.graphInfo.yearBtn);
                if (PO_DEVICE_TYPE.equalsIgnoreCase(value)) {
                    Custom_Line customLine = graphData.getAllDataLine(hr_datapoints, hr_dataVector.size(), new int[]{colors[1]}, hr_timestamp);
                    GraphUI.updateWeekDataNew(customLine, binding.heartRateChart);
                    custom = graphData.getAllData(bo_datapoints, bo_dataVector.size(), new int[]{colors[3]}, bo_timestamp);
                    GraphUI.updateWeekData2(custom, binding.bloodOxygenChart);
                } else if (GLUCOMETER_DEVICE_TYPE.equalsIgnoreCase(value)) {
                    Custom_Line customLine;
                    customLine = graphData.getAllDataLine(glucose_datapoints, glucose_dataVector.size(), new int[]{colors[4]}, glucose_timestamp);
                    GraphUI.updateWeekDataNew(customLine, binding.GlucoseChart);
                } else if (BP_DEVICE_TYPE.equalsIgnoreCase(value)) {
                    custom = graphData.getAllBPData(bph_dataPoints, bpl_dataPoints, bp_dataVector, bp_timestamp);
                    GraphUI.updateWeekData2(custom, binding.bloodPressureChart);
                } else if (SCALE_DEVICE_TYPE.equalsIgnoreCase(value)) {
                    custom = graphData.getAllData(weight_datapoints, weight_dataVector.size(), new int[]{colors[2]}, weight_timestamp);
                    GraphUI.updateWeekData2(custom, binding.WeightChart);
                } else if ("".equalsIgnoreCase(value)) {
                    Custom_Line customLine;
                    customLine = graphData.getAllDataLine(hr_datapoints, hr_dataVector.size(), new int[]{colors[1]}, hr_timestamp);
                    GraphUI.updateWeekDataNew(customLine, binding.heartRateChart);
                    custom = graphData.getAllData(bo_datapoints, bo_dataVector.size(), new int[]{colors[3]}, bo_timestamp);
                    GraphUI.updateWeekData2(custom, binding.bloodOxygenChart);
                    customLine = graphData.getAllDataLine(glucose_datapoints, glucose_dataVector.size(), new int[]{colors[4]}, glucose_timestamp);
                    GraphUI.updateWeekDataNew(customLine, binding.GlucoseChart);
                    custom = graphData.getAllBPData(bph_dataPoints, bpl_dataPoints, bp_dataVector, bp_timestamp);
                    GraphUI.updateWeekData2(custom, binding.bloodPressureChart);
                    custom = graphData.getAllData(weight_datapoints, weight_dataVector.size(), new int[]{colors[2]}, weight_timestamp);
                    GraphUI.updateWeekData2(custom, binding.WeightChart);
                }
                break;
            case TYPE.MONTHLY:
                GraphUI.setAppearance(binding.graphInfo.monthBtn,binding.graphInfo.allBtn, binding.graphInfo.yearBtn, binding.graphInfo.weekBtn);
                if (PO_DEVICE_TYPE.equalsIgnoreCase(value)) {
                    Custom_Line customLine = graphData.getMonthLineDataold(hr_datapoints, hr_dataVector.size(), new int[]{colors[1]}, hr_timestamp);
                    GraphUI.updateMonthDataNew(binding.heartRateChart, customLine);
                    custom = graphData.getMonthData(bo_datapoints, bo_dataVector.size(), new int[]{colors[3]}, bo_timestamp);
                    GraphUI.updateMonthData2(binding.bloodOxygenChart, custom);
                } else if (GLUCOMETER_DEVICE_TYPE.equalsIgnoreCase(value)) {
                    Custom_Line customLine;
                    customLine = graphData.getMonthLineDataold(glucose_datapoints, glucose_dataVector.size(), new int[]{colors[4]}, glucose_timestamp);
                    GraphUI.updateMonthDataNew(binding.GlucoseChart, customLine);
                } else if (BP_DEVICE_TYPE.equalsIgnoreCase(value)) {
                    custom = graphData.getMonthBPData(bph_dataPoints, bpl_dataPoints, bp_dataVector, new int[]{colors[5], colors[0]}, bp_timestamp);
                    GraphUI.updateMonthData2(binding.bloodPressureChart, custom);
                } else if (SCALE_DEVICE_TYPE.equalsIgnoreCase(value)) {
                    custom = graphData.getMonthData(weight_datapoints, weight_dataVector.size(), new int[]{colors[2]}, weight_timestamp);
                    GraphUI.updateMonthData2(binding.WeightChart, custom);
                } else if ("".equalsIgnoreCase(value)) {
                    Custom_Line customLine;
                    customLine = graphData.getMonthLineDataold(hr_datapoints, hr_dataVector.size(), new int[]{colors[1]}, hr_timestamp);
                    GraphUI.updateMonthDataNew(binding.heartRateChart, customLine);
                    custom = graphData.getMonthData(bo_datapoints, bo_dataVector.size(), new int[]{colors[3]}, bo_timestamp);
                    GraphUI.updateMonthData2(binding.bloodOxygenChart, custom);
                    customLine = graphData.getMonthLineDataold(glucose_datapoints, glucose_dataVector.size(), new int[]{colors[4]}, glucose_timestamp);
                    GraphUI.updateMonthDataNew(binding.GlucoseChart, customLine);
                    custom = graphData.getMonthBPData(bph_dataPoints, bpl_dataPoints, bp_dataVector, new int[]{colors[5], colors[0]}, bp_timestamp);
                    GraphUI.updateMonthData2(binding.bloodPressureChart, custom);
                    custom = graphData.getMonthData(weight_datapoints, weight_dataVector.size(), new int[]{colors[2]}, weight_timestamp);
                    GraphUI.updateMonthData2(binding.WeightChart, custom);
                }
                break;

            case TYPE.YEARLY:
                GraphUI.setAppearance(binding.graphInfo.yearBtn,binding.graphInfo.allBtn, binding.graphInfo.monthBtn, binding.graphInfo.weekBtn);
                if (PO_DEVICE_TYPE.equalsIgnoreCase(value)) {
                    Custom_Line customLine = graphData.getYearLineDataold(hr_datapoints, hr_dataVector.size(), new int[]{colors[1]}, hr_timestamp);
                    GraphUI.updateYearDataNew(customLine, binding.heartRateChart);
                    custom = graphData.getYearData(bo_datapoints, bo_dataVector.size(), new int[]{colors[3]}, bo_timestamp);
                    GraphUI.updateYearData2(custom, binding.bloodOxygenChart);
                } else if (GLUCOMETER_DEVICE_TYPE.equalsIgnoreCase(value)) {
                    Custom_Line customLine;
                    customLine = graphData.getYearLineDataold(glucose_datapoints, glucose_dataVector.size(), new int[]{colors[4]}, glucose_timestamp);
                    GraphUI.updateYearDataNew(customLine, binding.GlucoseChart);
                } else if (BP_DEVICE_TYPE.equalsIgnoreCase(value)) {
                    custom = graphData.getYearBPData(bph_dataPoints, bpl_dataPoints, bp_dataVector, new int[]{colors[5], colors[0]}, bp_timestamp);
                    GraphUI.updateYearData2(custom, binding.bloodPressureChart);
                } else if (SCALE_DEVICE_TYPE.equalsIgnoreCase(value)) {
                    custom = graphData.getYearData(weight_datapoints, weight_dataVector.size(), new int[]{colors[2]}, weight_timestamp);
                    GraphUI.updateYearData2(custom, binding.WeightChart);
                } else if ("".equalsIgnoreCase(value)) {
                    Custom_Line customLine;
                    customLine = graphData.getYearLineDataold(hr_datapoints, hr_dataVector.size(), new int[]{colors[1]}, hr_timestamp);
                    GraphUI.updateYearDataNew(customLine, binding.heartRateChart);
                    custom = graphData.getYearData(bo_datapoints, bo_dataVector.size(), new int[]{colors[3]}, bo_timestamp);
                    GraphUI.updateYearData2(custom, binding.bloodOxygenChart);
                    customLine = graphData.getYearLineDataold(glucose_datapoints, glucose_dataVector.size(), new int[]{colors[4]}, glucose_timestamp);
                    GraphUI.updateYearDataNew(customLine, binding.GlucoseChart);
                    custom = graphData.getYearBPData(bph_dataPoints, bpl_dataPoints, bp_dataVector, new int[]{colors[5], colors[0]}, bp_timestamp);
                    GraphUI.updateYearData2(custom, binding.bloodPressureChart);
                    custom = graphData.getYearData(weight_datapoints, weight_dataVector.size(), new int[]{colors[2]}, weight_timestamp);
                    GraphUI.updateYearData2(custom, binding.WeightChart);
                }

                break;

        }
        hideProgress();
    }


    // Called in a separate thread
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshData(RequestBPRefresh messageEvent) {

        if (refresh_disposable != null) {
            refresh_disposable.dispose();
        }

        final String user_account = userInfo.getEmail();
       // final String user_account = "pmajoka777@gmail.com";

        for (int i = 0; i < device_value.size(); i++) {
            refresh_disposable = Observable.just(device_value.get(i))
                    .flatMap(record_type ->
                            Observable.fromCallable(() ->
                                    WSInstance().RxCallingWebservice(get_patient_health_records_v2, record_type, "", user_account))
                                    .subscribeOn(Schedulers.io())
                                    .map(soapObject -> {
                                        if (HR.equalsIgnoreCase(record_type)) {
                                            hr_dataVector = new SoapObjectVector<>(Monitor_HR.class, (SoapObject) soapObject);
                                            if (hr_dataVector == null) {
                                                hr_dataVector = new Vector<>();
                                            }
                                        } else if (WEIGHT.equalsIgnoreCase(record_type)) {
                                            weight_dataVector = new SoapObjectVector<>(Monitor_Weight.class, (SoapObject) soapObject);

                                            if (weight_dataVector == null) {
                                                weight_dataVector = new Vector<>();
                                            }
                                        } else if (BLOOD_OXYGEN.equalsIgnoreCase(record_type)) {
                                            bo_dataVector = new SoapObjectVector<>(Monitor_BO.class, (SoapObject) soapObject);

                                            if (bo_dataVector == null) {
                                                bo_dataVector = new Vector<>();
                                            }
                                        } else if (BP.equalsIgnoreCase(record_type)) {
                                            bp_dataVector = new SoapObjectVector<>(Bp_data.class, (SoapObject) soapObject);
                                            if (bp_dataVector == null) {
                                                bp_dataVector = new Vector<>();
                                            }
                                        } else if (GLUCOSE.equalsIgnoreCase(record_type)) {
                                            glucose_dataVector = new SoapObjectVector<>(Monitor_Glucose.class, (SoapObject) soapObject);
                                            if (glucose_dataVector == null) {
                                                glucose_dataVector = new Vector<>();
                                            }
                                        } else {
                                            throw new Exception("Invalid Patient Health Records");
                                        }
                                        return true;
                                    })
                    ).subscribe(result -> {
                        binding.refreshLayout.setRefreshing(false);

                    }, error -> {
                        binding.refreshLayout.setRefreshing(false);
                        hideProgress();
                        Toast.makeText(getContext(), getString(R.string.refresh_error), Toast.LENGTH_SHORT).show();
                    }, /*Complete*/() -> {
                        binding.refreshLayout.setRefreshing(false);

                        EventBus.getDefault().post(new BPRefreshCompleteMessage());
                    });
        }
    }


    // Called in a separate thread
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshPage(BPRefreshCompleteMessage messageEvent) {
        binding.refreshLayout.setRefreshing(false);
        if (PO_DEVICE_TYPE.equalsIgnoreCase(value)) {
            EventBus.getDefault().post(new MostRecentPOData(hr_dataVector, bo_dataVector));
            init_HR_graph(hr_dataVector);
            init_bo_graph(bo_dataVector);
        } else if (GLUCOMETER_DEVICE_TYPE.equalsIgnoreCase(value)) {
            EventBus.getDefault().post(new MostRecentBpData<>(glucose_dataVector));
            init_glucose_graph(glucose_dataVector);
        } else if (BP_DEVICE_TYPE.equalsIgnoreCase(value)) {
            EventBus.getDefault().post(new MostRecentBpData<>(bp_dataVector));
            init_Blood_Pressure_Graph(bp_dataVector);
        } else if (SCALE_DEVICE_TYPE.equalsIgnoreCase(value)) {
            EventBus.getDefault().post(new MostRecentBpData<>(weight_dataVector));
            init_weight_graph(weight_dataVector);
        } else if ("".equalsIgnoreCase(value)) {
            EventBus.getDefault().post(new MostRecentPOData(hr_dataVector, bo_dataVector));
            init_HR_graph(hr_dataVector);
            init_bo_graph(bo_dataVector);
            EventBus.getDefault().post(new MostRecentBpData<>(glucose_dataVector));
            init_glucose_graph(glucose_dataVector);
            EventBus.getDefault().post(new MostRecentBpData<>(bp_dataVector));
            init_Blood_Pressure_Graph(bp_dataVector);
            EventBus.getDefault().post(new MostRecentBpData<>(weight_dataVector));
            init_weight_graph(weight_dataVector);
        }


        updateGraph(TYPE.All);
        //updateGraph(TYPE.WEEKLY);

    }

    public void init_Blood_Pressure_Graph(Vector<Bp_data> vector) {
        if (vector != null) {
            bph_dataPoints = soapObject_TO_Array(vector, BPH);
            bpl_dataPoints = soapObject_TO_Array(vector, BPL);
        }

    }

    public void init_HR_graph(Vector<Monitor_HR> vector) {
        if (vector != null) {
            hr_timestamp = new String[vector.size()];
            String[] data = new String[vector.size()];
            for (int i = 0; i < vector.size(); i++) {
                hr_datapoints = vectorToArray(i, vector.get(i), data, hr_timestamp);
            }
        }

    }

    public void init_weight_graph(Vector<Monitor_Weight> vector) {
        if (vector != null) {
            weight_timestamp = new String[vector.size()];
            String[] data = new String[vector.size()];
            for (int i = 0; i < vector.size(); i++) {
                weight_datapoints = vectorToArray(i, vector.get(i), data, weight_timestamp);
            }
        }
    }

    public void init_bo_graph(Vector<Monitor_BO> vector) {
        if (vector != null) {
            bo_timestamp = new String[vector.size()];
            String[] data = new String[vector.size()];
            for (int i = 0; i < vector.size(); i++) {
                bo_datapoints = vectorToArray(i, vector.get(i), data, bo_timestamp);
            }
        }
    }

    public void init_glucose_graph(Vector<Monitor_Glucose> vector) {
        if (vector != null) {
            glucose_timestamp = new String[vector.size()];
            String[] data = new String[vector.size()];
            for (int i = 0; i < vector.size(); i++) {
                glucose_datapoints = vectorToArray(i, vector.get(i), data, glucose_timestamp);
            }
        }
    }

    public String[] vectorToArray(int index, SoapObjectData soapObjectData, String[] data, String[] timeStamp_value) {


        Field[] fields = soapObjectData.getClass().getDeclaredFields();
        Long time = null;
        Double values = null;
        String latestDate = "";
        try {
            for (Field field : fields) {
                /*if no annotation*/
                if (field.getAnnotation(DataField.class) == null) {
                    continue;
                }
                if (field.getName().contains("stamp")) {
                    time = Long.valueOf(field.get(soapObjectData).toString());
                    Date date = new Date(time * 1000);
                    timeStamp_value[index] = String.valueOf(date);
                } else {
                    values = Double.valueOf(field.get(soapObjectData).toString());
                    data[index] = Double.toString(values);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    private String[] soapObject_TO_Array(Vector<Bp_data> monitor_bp, int BP) {
        bp_timestamp = new String[monitor_bp.size()];
        String latestDate = "";
        String[] data = new String[monitor_bp.size()];
        for (int i = 0; i < monitor_bp.size(); i++) {
            switch (BP) {
                case BPH:
                    data[i] = (monitor_bp.get(i).BPH);
                    break;
                case BPL:
                    data[i] = (monitor_bp.get(i).BPL);
                    break;
            }
            long time = Long.parseLong(monitor_bp.get(i).BP_timestamp);
            Date date = new Date(time * 1000);
            bp_timestamp[i] = String.valueOf(date);


        }

        return data;
    }


    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().post(new RequestBPRefresh());

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}