/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cybermed.cdoc_patient.R;

import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch.swUtil.BleManager;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch.swUtil.BleService;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch.swUtil.RxBus;
import com.cybermed.cdoc_patient.databinding.ActivityScanBinding;
import com.cybermed.cdoc_patient.databinding.AdapterMedicationBinding;
import com.cybermed.cdoc_patient.databinding.AdapterScannedDeviceBinding;
import com.cybermed.cdoc_patient.databinding.FragmentScanDeviceBinding;
import com.cybermed.cdoc_patient.util.AppConstant;
import com.cybermed.cdoc_patient.ws.WS;
import com.jstyle.blesdk1963.Util.BleData;
import com.jstyle.blesdk1963.Util.BleSDK;
import com.jstyle.blesdk1963.Util.ResolveData;
import com.jstyle.blesdk1963.constant.BleConst;
import com.jstyle.blesdk1963.constant.DeviceKey;
import com.jstyle.blesdk1963.model.ExtendedBluetoothDevice;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.cybermed.cdoc_patient.util.AppConstant.SMART_MAC;
import static com.cybermed.cdoc_patient.util.AppConstant.SMART_WATCH;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
public class DeviceScanFragment extends SmartWatchBaseFragment {

    private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    private static final int REQUEST_ENABLE_BT = 1;
    private ProgressDialog progressDialog;
    private Disposable subscription;
    private String address;
    private boolean moveToDeviceData;
    //ActivityScanBinding binding;
    FragmentScanDeviceBinding binding;
    Activity activity;

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.getContentView(inflater, container, savedInstanceState);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_scan_device, container, false);

        return binding.getRoot();
    }

    @Override
    protected void initLayout(View view) {
        // binding = FragmentScanDeviceBinding.inflate(getLayoutInflater());
        activity = getActivity();
        subscribe();
        //setContentView(binding.getRoot());

        Bundle data = getArguments();
        if (data != null) {
            boolean val = data.getBoolean(AppConstant.KEY_SMARTW, false);
            if (val) {
                moveToDeviceData = true;
            }
        }
        mHandler = new Handler();
        if (!activity.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(activity, "Ble not supported", Toast.LENGTH_SHORT)
                    .show();
            //finish();
            Navigation.findNavController(view).navigate(R.id.action_watchItemScan_to_device_Setup);
        }
        final BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(activity, R.string.error_bluetooth_not_supported,
                    Toast.LENGTH_SHORT).show();
            //finish();
            Navigation.findNavController(view).navigate(R.id.action_watchItemScan_to_device_Setup);
        }
        scanLeDevice(true);
        binding.btnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_watchItemScan_to_device_Setup);
            }
        });
        binding.layToolbar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_watchItemScan_to_device_Setup);
            }
        });
        binding.layToolbar.txtTittle.setText("Smart Watch");
    }


    public static String assetsPath = "J1668_06_V011_3_20180326.zip";

    //public static String assetsPath = "J1638_10_ONE_V015_4_20180309.zip";
    private void copyFileFromAssets(String filePath) {
        File file = new File(activity.getFilesDir().getAbsolutePath());
        try {
            if (!file.exists()) {
                file.mkdirs();
            }
            File fileOTA = new File(file, "ota");
            if (!fileOTA.exists()) {
                fileOTA.mkdir();
            }
            File fileMusic = new File(fileOTA, assetsPath);
            if (fileMusic.exists()) {
                return;
            } else {
                fileMusic.createNewFile();
            }
            AssetManager assetManager = activity.getAssets();
            InputStream inputStream = assetManager.open(assetsPath);
            OutputStream outputStream = new FileOutputStream(fileMusic);
            byte[] buffer = new byte[1024];
            while (inputStream.read(buffer) > 0) {
                outputStream.write(buffer);
            }
            outputStream.flush();
            inputStream.close();
            outputStream.close();


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return true;
    }


    @Override
    public void onResume() {
        super.onResume();

        // Ensures Bluetooth is enabled on the device. If Bluetooth is not
        // currently enabled,
        // fire an intent to display a dialog asking the user to grant
        // permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        // Initializes list view adapter.
        mLeDeviceListAdapter = new LeDeviceListAdapter();

        setListAdapter(mLeDeviceListAdapter);
        Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
        List<ExtendedBluetoothDevice> list = new ArrayList<>();
        for (BluetoothDevice device : devices) {
            if (isSmartWatchDetected(device.getName())) {
                list.add(new ExtendedBluetoothDevice(device));
                break;
            }
        }
        mLeDeviceListAdapter.addBondDevice(list);
        scanLeDevice(true);
    }

    private void connectDevice(String address) {
        subscription = RxBus.getInstance().toObservable(BleData.class)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<BleData>() {
                    @Override
                    public void accept(BleData bleData) throws Exception {
                        String action = bleData.getAction();
                        if (action.equals(BleService.ACTION_GATT_onDescriptorWrite)) {
                            sendValue(BleSDK.GetDeviceMacAddress());
                        } else if (action.equals(BleService.ACTION_GATT_DISCONNECTED)) {
                            dissMissDialog();
                        }
                    }
                });
        if (TextUtils.isEmpty(address)) {
            Log.i("DeviceScan", "onCreate: address null ");
            return;
        }
        Log.i("DeviceScan", "onCreate: ");
        BleManager.getInstance().connectDevice(address);
        showConnectDialog();
    }


    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        Log.e("info", maps.toString());
        String dataType = getDataType(maps);
        Map<String, String> data = getData(maps);
        switch (dataType) {
            case BleConst.GetDeviceMacAddress:
                String mac = data.get(DeviceKey.MacAddress);
                WS.registerBluetoothDevice(SMART_WATCH, mac, "Ble_1963ui", result -> {
                    if (result.toString().equals("1")) {
                        dissMissDialog();
                        Toast.makeText(activity, "Device Register Successfully", Toast.LENGTH_LONG).show();
                        WatchItemList fragment = new WatchItemList();
                        Bundle args = new Bundle();
                        args.putString(SMART_MAC, mac);
                        fragment.setArguments(args);
                        if (moveToDeviceData) {
                            unSubscribe();

                            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_watchItemScan_to_DeviceList, args);
                        } else {

                            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_watchItemScan_to_IOT_MainPage_Fragment, args);
                        }
                    } else
                        Toast.makeText(activity, "Please try again.", Toast.LENGTH_LONG).show();

                });
                break;

        }
    }

    private void showConnectDialog() {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Connecting");
        if (!progressDialog.isShowing()) progressDialog.show();

    }

    private void dissMissDialog() {
        if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
    }

    private void setListAdapter(LeDeviceListAdapter mLeDeviceListAdapter) {
        binding.scannedDevicesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.scannedDevicesRecyclerView.setAdapter(mLeDeviceListAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT
                && resultCode == Activity.RESULT_CANCELED) {
            //finish();
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_watchItemScan_to_device_Setup);
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        scanLeDevice(false);
        extendedBluetoothDevices.clear();
        mLeDeviceListAdapter.clear();


    }


    private void scanLeDevice(final boolean enable) {

        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    mScanning = false;
                    activity.invalidateOptionsMenu();
                }
            }, 12000);
            mBluetoothAdapter.startLeScan(mLeScanCallback);
            //	mBluetoothAdapter.startLeScan(serviceUuids, mLeScanCallback);
            mScanning = true;
        } else {
            if (!mScanning) return;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mHandler.removeCallbacksAndMessages(null);
            mScanning = false;
        }
        activity.invalidateOptionsMenu();
    }

    int filterRssi = -100;
    private List<ExtendedBluetoothDevice> extendedBluetoothDevices = new ArrayList<>();

    private ExtendedBluetoothDevice findDevice(final BluetoothDevice device) {
        for (final ExtendedBluetoothDevice mDevice : extendedBluetoothDevices) {
            if (mDevice.matches(device)) return mDevice;
        }
        return null;
    }

    public void addDevice(BluetoothDevice device, String name, int rssi) {
        ExtendedBluetoothDevice bluetoothDevice = findDevice(device);
        if (bluetoothDevice == null) {
            extendedBluetoothDevices.add(new ExtendedBluetoothDevice(device, name, rssi));
        } else {
            bluetoothDevice.rssi = rssi;
        }
    }


    public boolean isSmartWatchDetected(String deviceName) {
        if (deviceName != null && (deviceName.startsWith("1963YH") || deviceName.startsWith("Smart"))) {
            binding.realtiveSearching.setVisibility(View.GONE);
            binding.scannedDevicesRecyclerView.setVisibility(View.VISIBLE);
            return true;
        } else {
            return false;
        }
    }

    private class LeDeviceListAdapter extends RecyclerView.Adapter<LeDeviceListAdapter.MyViewHolder> {
        private List<ExtendedBluetoothDevice> deviceList;
        private LayoutInflater mInflator;
        int filterRssi;

        public void setDeviceList(List<ExtendedBluetoothDevice> deviceList) {
            this.deviceList = deviceList;
            notifyDataSetChanged();
            //getFilter().filter(filterName);
        }

        public void setFilterRssi(int rssi) {
            this.filterRssi = rssi;
        }

        public LeDeviceListAdapter() {
            //super();
            deviceList = new ArrayList<>();
            mInflator = DeviceScanFragment.this.getLayoutInflater();
        }

        public void addBondDevice(List<ExtendedBluetoothDevice> list) {
            deviceList.addAll(list);
            notifyDataSetChanged();

        }

        public void addDevice(BluetoothDevice device, String name, int rssi) {
            ExtendedBluetoothDevice bluetoothDevice = findDevice(device);
            if (bluetoothDevice == null) {
                if (isSmartWatchDetected(name))
                    deviceList.add(new ExtendedBluetoothDevice(device, name, rssi));
            } else {
                bluetoothDevice.rssi = rssi;
            }
        }

        private ExtendedBluetoothDevice findDevice(final BluetoothDevice device) {
            for (final ExtendedBluetoothDevice mDevice : deviceList) {
                if (mDevice.matches(device)) return mDevice;
            }
            return null;
        }

        public BluetoothDevice getDevice(int position) {
            return deviceList.get(position).device;
        }

        public String getName(int position) {
            return deviceList.get(position).name;
        }

        public void clear() {
            deviceList.clear();
        }


        // inflates the row layout from xml when needed
        @Override
        @NonNull
        public LeDeviceListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            AdapterScannedDeviceBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.adapter_scanned_device, parent, false);
            return new LeDeviceListAdapter.MyViewHolder(binding);
        }


        @Override
        public int getItemCount() {
            return deviceList == null ? 0 : deviceList.size();
        }

        // stores and recycles views as they are scrolled off screen
        class MyViewHolder extends RecyclerView.ViewHolder {

            AdapterScannedDeviceBinding binding = null;

            MyViewHolder(AdapterScannedDeviceBinding itemView) {
                super(itemView.getRoot());
                this.binding = itemView;


            }


        }


        // binds the data to the TextView in each row


        @Override
        public void onBindViewHolder(@NonNull LeDeviceListAdapter.MyViewHolder holder, int position) {

            ExtendedBluetoothDevice extendedBluetoothDevice = deviceList.get(position);
            BluetoothDevice device = extendedBluetoothDevice.device;
            final String deviceName = extendedBluetoothDevice.name;

            if (deviceName != null && deviceName.length() > 0 && deviceName.startsWith("Smart Watch")) {
                //holder.binding.cardSmartwatch.setVisibility(View.VISIBLE);
                holder.binding.deviceType.setText(deviceName);
                holder.binding.deviceIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.smart_watch_ic));
               /* else
                    holder.binding.deviceType.setText(getString(R.string.unknown_device));*/

                holder.binding.deviceCybermedCode.setText(device.getAddress());
                holder.binding.rootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                        final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
                        if (device == null)
                            return;
                        String name = mLeDeviceListAdapter.getName(position);
                        if (mScanning) {
                            scanLeDevice(false);
                        }

                        connectDevice(device.getAddress());
                    }
                });


            }
        }


        @Override
        public long getItemId(int i) {
            return i;
        }


    }


    // Device scan callback.

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi,
                             final byte[] scanRecord) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String deviceName = device.getName();
                    if (TextUtils.isEmpty(deviceName)) {
                        deviceName = ResolveData.decodeDeviceName(scanRecord);
                    }
                    if (TextUtils.isEmpty(deviceName))
                        deviceName = "Unknown device";

                    addDevice(device, deviceName, rssi);

                    if (isSmartWatchDetected(deviceName)) {
                        deviceName = "Smart Watch";
                        if (rssi > filterRssi) {
                            mLeDeviceListAdapter.addDevice(device, deviceName, rssi);
                            mLeDeviceListAdapter.notifyDataSetChanged();
                        }
                    }

                }
            });
        }
    };


}