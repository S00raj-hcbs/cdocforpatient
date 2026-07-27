package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices;

import static com.cdfortis.datainterface.soap.WebService.WSInstance;
import static com.cdfortis.datainterface.soap.WebServiceID.get_patient_health_records_v2;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealthGraph.IOTGraph.MEASUREMENT1;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealthGraph.IOTGraph.MEASUREMENT2;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealthGraph.IOTGraph.TIMESTAMP;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealthGraph.IOTGraph.VALUE;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch.vital_graph.SmartWatchGraphFragment.GRAPH_TYPE;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch.vital_graph.SmartWatchGraphFragment.HASHMAP;
import static com.cybermed.cdoc_patient.common.CDoctor2Application.getAndSetDeviceVector;
import static com.cybermed.cdoc_patient.util.AppConstant.APPLE_HEALTH;
import static com.cybermed.cdoc_patient.util.AppConstant.SMART_MAC;
import static com.cybermed.cdoc_patient.util.AppConstant.SMART_WATCH;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cdfortis.datainterface.data.MonitorRefreshCompletedMessage;
import com.cdfortis.datainterface.data.MostRecentMonitorData;
import com.cdfortis.datainterface.data.RequestMonitorRefresh;
import com.cdfortis.datainterface.soap.UserInfo;
import com.cdfortis.datainterface.soap.model.Monitor_BO;
import com.cdfortis.datainterface.soap.model.Monitor_BP;
import com.cdfortis.datainterface.soap.model.Monitor_Glucose;
import com.cdfortis.datainterface.soap.model.Monitor_HR;
import com.cdfortis.datainterface.soap.model.Monitor_STEMO;
import com.cdfortis.datainterface.soap.model.Monitor_Weight;
import com.cdfortis.datainterface.soap.model.SoapObjectVector;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.adapter.IOTSettingAdapter;
import com.cybermed.cdoc_patient.common.BaseFragment;
import com.cybermed.cdoc_patient.common.CDoctor2Application;
import com.cybermed.cdoc_patient.databinding.FragmentDeviceListBinding;
import com.cybermed.cdoc_patient.me.vitalcheck.VitalMonitorFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.ksoap2.serialization.SoapObject;

import java.util.Vector;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class IOTDeviceListFragment extends BaseFragment {

    private UserInfo userInfo;


    private static final String HR = "1";
    private static final String WEIGHT = "2";
    private static final String BLOOD_PRESSURE = "3";
    public static final String BLOOD_OXYGEN = "5";
    public static final String GLUCOSE = "6";
    public static final String STEMOSCOPE = "8";

    private Vector<Monitor_BO> monitor_boVector;
    private Vector<Monitor_BP> monitor_bpVector;
    private Vector<Monitor_Glucose> monitor_glucoseVector;
    private Vector<Monitor_Weight> monitor_weightVector;
    private Vector<Monitor_HR> monitor_hrVector;
    private Vector<Monitor_STEMO> stemoscope_Vector;


    IOTSettingAdapter iotSettingAdapter;
    FragmentDeviceListBinding binding;
    String mMac, mDeviceName;
    int mStatus;

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_device_list, container, false);
        return binding.getRoot();
    }

    @Override
    protected void initLayout(View view) {

        userInfo = CDoctor2Application.getLoginInfo().getUserInfo();
        initContent();
    }

    public void initContent() {
        iotSettingAdapter = new IOTSettingAdapter(getActivity(), (type, timeStamp, measurement1, measurement2) -> {
            if (type.equals(SMART_WATCH)) {
                Bundle args = new Bundle();
                args.putString(SMART_MAC, measurement1);
                args.putString(GRAPH_TYPE, SMART_WATCH);
                args.putSerializable(HASHMAP, null);
                Navigation.findNavController(binding.getRoot()).navigate(R.id.action_IOT_MainPage_Fragment_to_watchgraph, args);
            } else if (type.equals(APPLE_HEALTH)) {
               /* Bundle args = new Bundle();
                args.putString(SMART_MAC, measurement1);
                args.putString(GRAPH_TYPE, SMART_WATCH);
                args.putSerializable(HASHMAP, null);*//*
                Navigation.findNavController(binding.getRoot()).navigate(R.id.action_IOT_MainPage_Fragment_to_watchgraph, args);*/

                /*if (getParentFragment() instanceof VitalMonitorFragment) {
                    ((VitalMonitorFragment) getParentFragment()).showMainVitalScreen();
                }*/
            } else {
                Bundle args = new Bundle();
                args.putString(VALUE, type);
                args.putString(TIMESTAMP, timeStamp);
                args.putString(MEASUREMENT1, measurement1);
                args.putString(MEASUREMENT2, measurement2);
                Navigation.findNavController(binding.getRoot()).navigate(R.id.action_IOT_MainPage_Fragment_to_iotgraph, args);
            }
        },getParentFragmentManager());


        binding.iotDeviceSettingList.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.iotDeviceSettingList.setAdapter(iotSettingAdapter);
        CDoctor2Application.getLoginInfo().getUserInfo().getIoT_devices_obs().observe(getViewLifecycleOwner(), ioT_devices -> {
            iotSettingAdapter.setIoT_deviceVector(ioT_devices);
        });
        binding.iotSettingSwipeRefresh.setOnRefreshListener(() -> {
            refreshData();
            EventBus.getDefault().post(new RequestMonitorRefresh());
        });

    }


    Disposable refresh_disposable;

    // Called in a separate thread
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshData(RequestMonitorRefresh messageEvent) {
        if (refresh_disposable != null) {
            refresh_disposable.dispose();
        }

        final String user_account = userInfo.getEmail();

        refresh_disposable = Observable.just(HR, WEIGHT, BLOOD_OXYGEN, BLOOD_PRESSURE, GLUCOSE, STEMOSCOPE)
                .flatMap(record_type ->
                        Observable.fromCallable(() ->
                                WSInstance().RxCallingWebservice(get_patient_health_records_v2, record_type, "", user_account))
                                .subscribeOn(Schedulers.io())
                                .map(soapObject -> {
                                    switch (record_type) {
                                        case HR:
                                            monitor_hrVector = new SoapObjectVector<>(Monitor_HR.class, (SoapObject) soapObject);
                                            break;
                                        case WEIGHT:
                                            monitor_weightVector = new SoapObjectVector<>(Monitor_Weight.class, (SoapObject) soapObject);
                                            break;
                                        case BLOOD_OXYGEN:
                                            monitor_boVector = new SoapObjectVector<>(Monitor_BO.class, (SoapObject) soapObject);
                                            break;
                                        case BLOOD_PRESSURE:
                                            monitor_bpVector = new SoapObjectVector<>(Monitor_BP.class, (SoapObject) soapObject);
                                            break;
                                        case GLUCOSE:
                                            monitor_glucoseVector = new SoapObjectVector<>(Monitor_Glucose.class, (SoapObject) soapObject);
                                            break;
                                        case STEMOSCOPE:
                                            stemoscope_Vector = new SoapObjectVector<>(Monitor_STEMO.class, (SoapObject) soapObject);
                                            break;
                                        default:
                                            throw new Exception("Invalid Patient Health Records");
                                    }
                                    return true;
                                })
                ).subscribe(result -> {
                }, error -> {
                    Toast.makeText(getContext(), getString(R.string.refresh_error), Toast.LENGTH_SHORT).show();
                }, /*Complete*/() -> {
                    EventBus.getDefault().post(new MonitorRefreshCompletedMessage());
                });
    }

    // Called in a separate thread
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshPage(MonitorRefreshCompletedMessage messageEvent) {
        EventBus.getDefault().post(new MostRecentMonitorData(monitor_hrVector, monitor_weightVector, monitor_boVector, monitor_bpVector, monitor_glucoseVector, stemoscope_Vector));
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateMostRecentData(MostRecentMonitorData mrd) {
        //updating the adapter's data
        iotSettingAdapter.updateData(mrd);
    }


    @Override
    public void onStart() {
        super.onStart();
        //registering event bus for the fragment
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        //unregistering event bus for the fragment
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().post(new RequestMonitorRefresh());
        refreshData();
    }

    private void refreshData() {
        getAndSetDeviceVector();
        binding.iotSettingSwipeRefresh.setRefreshing(false);
    }



}
