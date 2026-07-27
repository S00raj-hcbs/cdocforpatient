package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices;


import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cdfortis.datainterface.soap.WebService;
import com.cdfortis.datainterface.soap.model.IoT_Device;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BTAdapter.ScannedDeviceAdapter;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.BlueToothService;
import com.cybermed.cdoc_patient.common.BaseFragment;
import com.cybermed.cdoc_patient.databinding.FragmentPulseOxiInstructionBinding;
import com.cybermed.cdoc_patient.databinding.FragmentScanDeviceBinding;
import com.ihealth.communication.manager.iHealthDevicesCallback;
import com.ihealth.communication.manager.iHealthDevicesManager;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static com.cdfortis.datainterface.soap.WebServiceID.get_cybermed_code_from_mac_address;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment.fixMac;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.IOTDeviceSetUpFragment.set_up_device_name;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.IOTDeviceSetUpFragment.set_up_device_type;


public class iHealthScannedDeviceFragment extends BaseFragment {


    private ScannedDeviceAdapter scannedDeviceAdapter;

    FragmentScanDeviceBinding binding;

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_scan_device, container, false);
        return binding.getRoot();
    }

    @Override
    protected void initLayout(View view) {
        initContent();

    }

    public void initContent() {
        scannedDeviceAdapter = new ScannedDeviceAdapter();

        binding.scannedDevicesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.scannedDevicesRecyclerView.setAdapter(scannedDeviceAdapter);
        initView();
    }

    public void initView() {
        // binding.titleFindDevice.setText("Finding " + getArguments().getString(IOTDeviceSetUpFragment.SCAN_DEVICE_KEY, getResources().getString(R.string.iot_unknown_device)));

        getLifecycle().addObserver(new MyIHealthCallBack());

        //binding.iotSettingToolbar.setNavigationIcon(R.drawable.icon_back_row);
        //binding.iotSettingToolbar.setNavigationOnClickListener(v -> getActivity().onBackPressed());
        binding.layToolbar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_iHealthScannedDeviceFragment_to_IOTDeviceSetUpFragment);
            }
        });
        binding.layToolbar.txtTittle.setText(getString(R.string.search_device));
        binding.btnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_iHealthScannedDeviceFragment_to_IOTDeviceSetUpFragment);
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.unbind();
    }

    public static final String UNRECOGNIZED = "Unrecognized";
    public static final String CYBERMED_CODE_REGEX = "\\d{2}-\\d{2}-\\d{4}";


    private class MyIHealthCallBack extends iHealthDevicesCallback implements LifecycleObserver {
        private final CompositeDisposable disposables = new CompositeDisposable();
        int registerID = 0;

        private CountDownTimer countDownTimer = new CountDownTimer(30000, 13000) {
            @Override
            public void onTick(long millisUntilFinished) {
                iHealthDevicesManager.getInstance().startDiscovery(
                        BlueToothService.Companion.getDiscoveryTypeEnum(set_up_device_name));
            }

            @Override
            public void onFinish() {
                /*Do Something After 10 minutes Timeout*/
                binding.relativeProgress.setVisibility(View.GONE);
                binding.searchingText.setText(getString(R.string.no_device_found));
                binding.searchingText.setCompoundDrawablesRelativeWithIntrinsicBounds(null, ContextCompat.getDrawable(getActivity(), R.drawable.search_oops), null, null);
                binding.txtLabel.setText(getString(R.string.device_is_near));
                binding.btnStartMeasure.setVisibility(View.VISIBLE);
                binding.btnStartMeasure.setText(getString(R.string.try_again));
                binding.btnStartMeasure.setOnClickListener(v -> {
                    tryAgain();
                    countDownTimer.start();
                });
            }
        };


        @Override
        public void onScanDevice(String mac, String deviceType, int rssi) {
            disposables.add(Single.just(new IoT_Device(set_up_device_type, fixMac(mac)))
                    .subscribeOn(Schedulers.io())
                    .map(device -> {
                        device.setCybermed_code(WebService.getInstance().RxCallingWebservice(get_cybermed_code_from_mac_address, device.getDevice_macAddress()).toString());
                        return device;
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            /*Success*/device -> {
                                binding.realtiveSearching.setVisibility(View.GONE);
                                binding.scannedDevicesRecyclerView.setVisibility(View.VISIBLE);
                                if (!device.getCybermed_code().matches(CYBERMED_CODE_REGEX)) {
                                    device.setCybermed_code(UNRECOGNIZED);
                                }

                                if (!scannedDeviceAdapter.contains(device)) {
                                    scannedDeviceAdapter.addDevice(device);
                                }
                            },
                            error -> {
                                Toast.makeText(iHealthScannedDeviceFragment.this.getContext(),
                                        "Please check your network connection", Toast.LENGTH_SHORT).show();
                            })
            );
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        public void registerCallBack() {
            registerID = iHealthDevicesManager.getInstance().registerClientCallback(this);
            countDownTimer.start();
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        public void unregisterCallBack() {
            disposables.clear();
            countDownTimer.cancel();
            iHealthDevicesManager.getInstance().unRegisterClientCallback(registerID);
        }
    }

    private void tryAgain() {
        binding.relativeProgress.setVisibility(View.VISIBLE);
        binding.searchingText.setText(getString(R.string.searching_for_device));
        binding.searchingText.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
        binding.txtLabel.setText(getString(R.string.device_is_near));
        binding.btnStartMeasure.setVisibility(View.GONE);

    }
}