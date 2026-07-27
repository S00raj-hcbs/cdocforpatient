package com.cybermed.cdoc_patient.bluetooth.IC;

import androidx.fragment.app.*;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.choicemmed.bp1blelibrary.Device.Bp1Device;
import com.choicemmed.bp1blelibrary.base.DeviceType;
import com.choicemmed.bp1blelibrary.cmd.invoker.Bp1Invoker;
import com.choicemmed.bp1blelibrary.cmd.listener.Bp1Listener;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothMeasuringFragment;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothTimeoutFragment;

public class iChoiceBPFragment extends BluetoothBaseFragment implements Bp1Listener, View.OnClickListener {
    private static final String TAG = iChoiceBPFragment.class.getSimpleName();

    private FrameLayout fragmentContainer;
    private Bp1Invoker bp1Invoker;
    private CountDownTimer countDownTimer;
/*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_i_choice_bp);
        Log.i(TAG, "onCreate() , service created...");

        fragmentContainer = findViewById(R.id.fragment_container);
        bp1Invoker = new Bp1Invoker(iChoiceBPActivity.this, this);
        findViewById(R.id.btn_quit).setOnClickListener(this);

        startScanBpl();

        timeOutCounter();

    }*/

    private void timeOutCounter(){
        countDownTimer = new CountDownTimer(15000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                openFragment(new BluetoothTimeoutFragment());
            }

        }.start();
    }

    private void cancelTimeOut(){
        if(countDownTimer!=null){
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    public void openFragment(Fragment fragment) {
        try {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_right, R.animator.enter_from_right, R.animator.exit_to_right);
            transaction.add(R.id.fragment_container, fragment, fragment.getClass().getSimpleName()).commit();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    private void startScanBpl() {
        if (bp1Invoker != null) {
            Log.d(TAG, "binding bp monitor");
            bp1Invoker.bindDevice();
        }
    }

    private void stopScanBp1() {
        if (bp1Invoker != null) {
            Log.d(TAG, "stop binding bp monitor");
            bp1Invoker.stopDeviceScan();
        }
    }

    private void disconnectBpl() {
        Log.d(TAG, "disconnecting bp monitor");
        if (bp1Invoker != null)
            bp1Invoker.disconnectDevice();
    }

    @Override
    public void onBindDeviceSuccess(Bp1Device bplDevice) {
        Log.d(TAG, "binding bp monitor success");
        cancelTimeOut();
//        developerDebugLog("Bluetooth Debug - binding BP success");
    }

    @Override
    public void onBindDeviceFail(String failMessage) {
        Log.e(TAG, "failed to connect to device " + failMessage);
    }

    @Override
    public void onConnectedDeviceSuccess() {
        Log.d(TAG,"device connected");
        cancelTimeOut();
        openFragment(new BluetoothMeasuringFragment());
//        stopScanBp1();
    }

    @Override
    public void onConnectedDeviceFail(String failMessage) {

    }

    @Override
    public void onDataResponse(int bph, int bpl, int hr, String macAddress) {
        cancelTimeOut();
        SendVitalData("BP", bph + ":" + bpl + ":" + hr, macAddress);
        iChoiceBPResultFragment fragment = iChoiceBPResultFragment.newInstance(String.valueOf(bph), String.valueOf(bpl));
        openFragment(fragment);
    }

    @Override
    public void onScanTimeout(DeviceType deviceType) {
        Log.d(TAG, "bp1 scan timeout" + deviceType.toString());
        if (bp1Invoker != null)
            startScanBpl();

    }

    @Override
    public void onError(String paramString) {

    }

    @Override
    public void onDisconnected() {
        Log.e(TAG, "disconnected");
    }

    @Override
    public void onStateChanged(int bleState, int state) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(countDownTimer!=null){
            countDownTimer.cancel();
            countDownTimer = null;
        }
        if (bp1Invoker != null) {
            bp1Invoker.disconnectDevice();
            stopScanBp1();
            bp1Invoker.close();
            bp1Invoker = null;
        }
        Log.i(TAG, "onDestroy() , service stopped...");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume() , service resumed...");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_quit:
                getActivity().finish();
                break;
        }
    }
}
