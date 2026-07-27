package com.cybermed.cdoc_patient.bluetooth.IC;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.choicemmed.s1blelibrary.Device.S1Device;
import com.choicemmed.s1blelibrary.cmd.invoker.S1Invoker;
import com.choicemmed.s1blelibrary.cmd.listener.S1Listener;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothMeasuringFragment;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothTimeoutFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class iChoiceScaleFragment extends BluetoothBaseFragment implements S1Listener, View.OnClickListener {
    private static final String TAG = iChoiceScaleFragment.class.getSimpleName();

    private FrameLayout fragmentContainer;
    private S1Invoker S1Invoker;
    private CountDownTimer countDownTimer;

   /* @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_i_choice_scale);

        fragmentContainer = findViewById(R.id.fragment_container);
        S1Invoker = new S1Invoker(iChoiceScaleActivity.this, this);
        findViewById(R.id.btn_quit).setOnClickListener(this);

        startScanScale();
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
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_right, R.animator.enter_from_right, R.animator.exit_to_right);
        transaction.add(R.id.fragment_container, fragment, fragment.getClass().getSimpleName()).commit();
    }


    private void startScanScale() {
        if (S1Invoker != null) {
            Log.d(TAG, "binding scale monitor");
            S1Invoker.bindDevice();
        }
    }

    private void stopScanScale() {
        if (S1Invoker != null) {
            Log.d(TAG, "stop binding scale monitor");
            S1Invoker.stopDeviceScan();
        }
    }

    private void disconnectScale() {
        Log.d(TAG, "disconnecting scale monitor");
        if (S1Invoker != null)
            S1Invoker.disconnectDevice();
    }

    @Override
    public void onBindDeviceSuccess(S1Device s1Device) {
        cancelTimeOut();
    }

    @Override
    public void onBindDeviceFail(String failMessage) {
        Log.e(TAG, "failed to connect to device " + failMessage);
    }

    @Override
    public void onDataResponse(double d, String macaddress) {
        SendVitalData("weight",String.format("%.1f", d),macaddress);
        double measurelbs = kg2lbs(d);
        iChoiceScaleResultFragment fragment = iChoiceScaleResultFragment.newInstance(String.format("%.1f", measurelbs));
        cancelTimeOut();
        openFragment(fragment);
    }

    public static double kg2lbs(double w) {
        return 2.20462 * w;
    }

    @Override
    public void onConnectedDeviceSuccess() {
        Log.d(TAG,"device connected");
        cancelTimeOut();
        openFragment(new BluetoothMeasuringFragment());

    }

    @Override
    public void onConnectedDeviceFail(String failMessage) {

    }

//    @Override
//    public void onDataResponse(int paramInt1, int paramInt2) {
//
//    }

//    @Override
//    public void onDataResponse(int bph, int bpl, int hr, String macAddress) {
//        Log.d(TAG, "blood pressure monitor values: " + "bph：" + bph + "；bpl：" + bpl + "；heart rate：" + hr);
//        iChoiceBPResultFragment fragment = iChoiceBPResultFragment.newInstance(String.valueOf(bph), String.valueOf(bpl));
//
//        openFragment(fragment);
////        developerDebugLog("Bluetooth Debug - retrieved bp data");
////        sendPatVital("BP", bph + ":" + bpl, macAddress);
//    }

    @Override
    public void onScanTimeout(String message) {
        Log.d(TAG, "scale scan timeout" + message);
        if (S1Invoker != null)
            startScanScale();

    }

//    @Override
//    public void onDataResponse(int paramInt1, int paramInt2, String macAddress) {
//        iChoiceOxiResultFragment fragment = iChoiceOxiResultFragment.newInstance(String.valueOf(paramInt1), String.valueOf(paramInt2));
//        openFragment(fragment);
//    }

    @Override
    public void onError(String paramString) {

    }

    @Override
    public void onDisconnected() {
        Log.e(TAG, "disconnected");

        if (S1Invoker != null)
            startScanScale();
    }

    @Override
    public void onStateChanged(int bleState, int state) {

    }

    @Override
    public void setUnitSuccess() {

    }

    @Override
    public void setUnitError(String msg) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(countDownTimer!=null){
            countDownTimer.cancel();
            countDownTimer = null;
        }
        if (S1Invoker != null) {
            stopScanScale();
            disconnectScale();
            S1Invoker = null;
        }
        Log.i(TAG, "onDestroy() , service stopped...");
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
