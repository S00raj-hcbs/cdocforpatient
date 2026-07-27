package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch.swUtil.BleManager;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch.swUtil.BleService;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch.swUtil.RxBus;
import com.cybermed.cdoc_patient.common.BaseFragment;
import com.jstyle.blesdk1963.Util.BleData;
import com.jstyle.blesdk1963.Util.BleSDK;
import com.jstyle.blesdk1963.callback.DataListener1963;
import com.jstyle.blesdk1963.constant.DeviceKey;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class SmartWatchBaseFragment extends BaseFragment implements DataListener1963 {

    private Disposable subscription;



    protected void subscribe() {
        subscription = RxBus.getInstance().toObservable(BleData.class).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<BleData>() {
            @Override
            public void accept(BleData bleData) throws Exception {
                String action = bleData.getAction();
                if (action.equals(BleService.ACTION_DATA_AVAILABLE)) {
                    byte[] value = bleData.getValue();
                    BleSDK.DataParsingWithData(value, SmartWatchBaseFragment.this);
                }else if(action.equals(BleService.ACTION_GATT_onDescriptorWrite)){
                    connected();
                }else if(action.equals(BleService.ACTION_GATT_DISCONNECTED)){
                    disconnected();
                }

            }
        });

    }

    public void connected() {

    }

    private void unSubscribe(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    public void unSubscribe(){
        unSubscribe(subscription);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unSubscribe(subscription);
    }

    @Override
    public void dataCallback(Map<String, Object> maps) {

    }

    @Override
    public void dataCallback(byte[] value) {

    }


    protected void sendValue(byte[] value) {
        if (!BleManager.getInstance().isConnected()) {
            Toast.makeText(getActivity(),"Please Connect Device",Toast.LENGTH_LONG).show();
            return;
        }
        if (value == null) return;

        BleManager.getInstance().writeValue(value);

    }

    protected String getDataType(Map<String, Object> maps) {
        return (String) maps.get(DeviceKey.DataType);
    }

    protected boolean getEnd(Map<String, Object> maps) {
        return (boolean) maps.get(DeviceKey.End);
    }

    protected Map<String, String> getData(Map<String, Object> maps) {
        return (Map<String, String>) maps.get(DeviceKey.Data);
    }

    protected void offerData(byte[] value) {
        BleManager.getInstance().offerValue(value);
    }

    protected void offerData() {

        BleManager.getInstance().writeValue();
    }


    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "02:00:00:00:00:00";
    }

    public void disconnected(){

    }

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        subscribe();
        return null;
    }

    @Override
    protected void initLayout(View view) {

    }
}
