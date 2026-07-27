package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.Navigation;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch.swUtil.BleManager;
import com.cybermed.cdoc_patient.databinding.ActivityWatchItemBinding;
import com.cybermed.cdoc_patient.webapi.ICallBack;
import com.jstyle.blesdk1963.Util.BleSDK;
import com.jstyle.blesdk1963.constant.BleConst;
import com.jstyle.blesdk1963.constant.DeviceKey;
import com.jstyle.blesdk1963.model.MyDeviceTime;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch.vital_graph.SmartWatchGraphFragment.GRAPH_TYPE;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch.vital_graph.SmartWatchGraphFragment.HASHMAP;
import static com.cybermed.cdoc_patient.util.AppConstant.BLOOD_OXYGEN;
import static com.cybermed.cdoc_patient.util.AppConstant.BLOOD_PRESSURE;
import static com.cybermed.cdoc_patient.util.AppConstant.CALORIES;
import static com.cybermed.cdoc_patient.util.AppConstant.DISTANCE;
import static com.cybermed.cdoc_patient.util.AppConstant.HEART_RATE;
import static com.cybermed.cdoc_patient.util.AppConstant.SMART_BO;
import static com.cybermed.cdoc_patient.util.AppConstant.SMART_DAILY;
import static com.cybermed.cdoc_patient.util.AppConstant.SMART_HRV;
import static com.cybermed.cdoc_patient.util.AppConstant.SMART_MAC;
import static com.cybermed.cdoc_patient.util.AppConstant.SMART_TEMP;
import static com.cybermed.cdoc_patient.util.AppConstant.STEPS;
import static com.cybermed.cdoc_patient.util.AppConstant.TEMP;

public class WatchItemList extends SmartWatchBaseFragment implements View.OnClickListener {
    int ModeStart = 0;
    String mac = null;
    WatchItemHelper helper;
    ActivityWatchItemBinding binding;
    HashMap<String, List<Map<String, String>>> hashMap;
    boolean tryOnce;

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.getContentView(inflater, container, savedInstanceState);
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_watch_item, container, false);
        return binding.getRoot();
    }

    @Override
    protected void initLayout(View view) {
        init();
        initCLickListner();
        binding.toolbar.setNavigationOnClickListener(v -> {
            if (getParentFragment() != null) {
                backToMainIOt();
            }
        });
    }

    private void backToMainIOt() {
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_watchItemList_to_IOT_MainPage_Fragment);
    }

    private void initCLickListner() {
        binding.linearCalories.setOnClickListener(this::onClick);
        binding.linearDistance.setOnClickListener(this::onClick);
        binding.linearSteps.setOnClickListener(this::onClick);
        binding.layoutDaily.linearCalories.setOnClickListener(this::onClick);
        binding.layoutDaily.linearDistance.setOnClickListener(this::onClick);
        binding.layoutDaily.linearSteps.setOnClickListener(this::onClick);
        binding.layoutHrv.linearBp.setOnClickListener(this::onClick);
        binding.layoutHrv.linearHeartrate.setOnClickListener(this::onClick);
        binding.layoutTemp.linearTemp.setOnClickListener(this::onClick);
        binding.layoutBloodpressure.linearBloodOxygen.setOnClickListener(this::onClick);
    }

    private void init() {
        helper = new WatchItemHelper(getContext(), binding, new ICallBack() {
            @Override
            public void showProgress() {
                WatchItemList.this.showProgress();
            }

            @Override
            public void hideProgress() {
                WatchItemList.this.hideProgress();
                binding.txtLoader.setVisibility(View.GONE);
            }

            @Override
            public void unsubscribe() {
                binding.txtLoader.setVisibility(View.GONE);
                hideProgress();
            }

            @Override
            public void connectDevice() {
                WatchItemList.this.connectDevice();
            }

            @Override
            public void apiMapValues(HashMap<String, List<Map<String, String>>> hashMap) {
                WatchItemList.this.hashMap = hashMap;
            }
        });
        Bundle data = getArguments();
        if (data != null) {
            mac = data.getString(SMART_MAC);
            helper.getData(mac);
        }
        binding.imgConnect.setOnClickListener(v -> {
            binding.imgConnect.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.bg_blue_trans));
            connectDevice();
        });


    }

    private void connectDevice() {
        if (BleManager.getInstance().isConnected()) {
            getDataFromSW();
        } else if (!TextUtils.isEmpty(mac)) {
            binding.txtLoader.setVisibility(View.VISIBLE);
            BleManager.getInstance().connectDevice(mac);
        }

    }

    /**
     * get data from smart watch
     */
    private void getDataFromSW() {
        setTime();
        binding.txtLoader.setVisibility(View.VISIBLE);
        helper.clearData();
        new Handler(Looper.myLooper()).postDelayed(() -> getTotalData(ModeStart), 300);
    }

    private void setTime() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        MyDeviceTime setTime = new MyDeviceTime();
        setTime.setYear(year);
        setTime.setMonth(month);
        setTime.setDay(day);
        setTime.setHour(hour);
        setTime.setMinute(min);
        setTime.setSecond(second);
        sendValue(BleSDK.SetDeviceTime(setTime));
    }

    //******************************** Smart Watch CallbAck *****************************************
    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        String dataType = getDataType(maps);

        switch (dataType) {
            case BleConst.GetTotalActivityData:
                helper.createList((List<Map<String, String>>) maps.get(DeviceKey.Data), SMART_DAILY,
                        (Boolean) maps.get(DeviceKey.End));
                if ((Boolean) maps.get(DeviceKey.End)) {
                    getHrvData(ModeStart);
                }
                break;
            case BleConst.Blood_oxygen:
                helper.createList((List<Map<String, String>>) maps.get(DeviceKey.Data), SMART_BO,
                        (Boolean) maps.get(DeviceKey.End));
                if ((Boolean) maps.get(DeviceKey.End)) {
                    getTempData(ModeStart);
                }
                break;
            case BleConst.GetHRVData:
                helper.createList((List<Map<String, String>>) maps.get(DeviceKey.Data), SMART_HRV,
                        (Boolean) maps.get(DeviceKey.End));
                if ((Boolean) maps.get(DeviceKey.End)) {
                    getHeartHistoryData(ModeStart);
                }
                break;
            case BleConst.Temperature_history:
                helper.createList((List<Map<String, String>>) maps.get(DeviceKey.Data), SMART_TEMP,
                        (Boolean) maps.get(DeviceKey.End));
                break;

        }
    }

    private void getTotalData(int mode) {
        sendValue(BleSDK.GetTotalActivityDataWithMode(mode));
    }

    private void getTempData(int mode) {
        sendValue(BleSDK.GetTemperature_historyDataWithMode(mode));
    }


    private void getHeartHistoryData(int mode) {
        sendValue(BleSDK.GetBloodOxygen(mode));
    }

    private void getHrvData(int mode) {
        sendValue(BleSDK.GetHRVDataWithMode(mode));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getDataFromSW();
    }

    @Override
    public void connected() {
        super.connected();
        hideProgress();
        getDataFromSW();
    }

    @Override
    public void disconnected() {
        super.disconnected();
        hideProgress();
        if (!tryOnce) {
            tryOnce = true;
            connectDevice();
        } else {
            Toast.makeText(getActivity(), getString(R.string.error_while_measuring), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        String type = "";
        switch (v.getId()) {
            case R.id.linear_steps:
                type = STEPS;
                break;
            case R.id.linear_calories:
                type = CALORIES;
                break;
            case R.id.linear_distance:
                type = DISTANCE;
                break;
            case R.id.linear_bp:
                type = BLOOD_PRESSURE;
                break;
            case R.id.linear_temp:
                type = TEMP;
                break;
            case R.id.linear_heartrate:
                type = HEART_RATE;
                break;
            case R.id.linear_blood_oxygen:
                type = BLOOD_OXYGEN;
                break;

        }
        if (mac != null && hashMap != null) {
            Bundle args = new Bundle();
            args.putString(SMART_MAC, mac);
            args.putString(GRAPH_TYPE, type);
            args.putSerializable(HASHMAP, hashMap);
            Navigation.findNavController(v).navigate(R.id.action_watchItemList_to_watchgraph, args);

        }
    }
}