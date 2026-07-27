package com.cybermed.cdoc_patient.bluetooth.IC;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.choicemmed.c208blelibrary.Device.C208Device;
import com.choicemmed.c208blelibrary.cmd.invoker.C208Invoker;
import com.choicemmed.c208blelibrary.cmd.listener.C208BindDeviceListener;
import com.choicemmed.c208blelibrary.cmd.listener.C208CommandListener;
import com.choicemmed.c208blelibrary.cmd.listener.C208ConnectDeviceListener;
import com.choicemmed.c208blelibrary.cmd.listener.C208DisconnectCommandListener;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothMeasuringFragment;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothTimeoutFragment;

public class iChoicePulseOxiFragment extends BluetoothBaseFragment implements C208BindDeviceListener, C208CommandListener,
        C208ConnectDeviceListener, C208DisconnectCommandListener, View.OnClickListener {
    private static final String TAG = iChoicePulseOxiFragment.class.getSimpleName();

    private FrameLayout fragmentContainer;
    private C208Invoker c208Invoker;
    private CountDownTimer countDownTimer;

   /* @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_i_choice_pulse_oxi);
        findViewById(R.id.btn_quit).setOnClickListener(this);

        fragmentContainer = findViewById(R.id.fragment_container);
        c208Invoker = new C208Invoker(iChoicePulseOxiActivity.this);
        findViewById(R.id.btn_quit).setOnClickListener(this);
        startScanOxi();
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


    private void startScanOxi() {
        if (c208Invoker != null) {
            Log.d(TAG, "binding oxi monitor");
            c208Invoker.bindDevice(iChoicePulseOxiFragment.this);
        }
    }

    private void stopScanOxi() {
        if (c208Invoker != null) {
            Log.d(TAG, "stop binding oxi monitor");
            c208Invoker.stopDeviceScan(iChoicePulseOxiFragment.this);
        }
    }

    private void disconnectOxi() {
        Log.d(TAG, "disconnecting oxi monitor");
        if (c208Invoker != null)
            c208Invoker.disconnectDevice(iChoicePulseOxiFragment.this);
    }

    @Override
    public void onBindDeviceSuccess(C208Device paramC208Device) {
        Log.d(TAG, "binding oxi monitor success");
        cancelTimeOut();
        openFragment(new BluetoothMeasuringFragment());
    }

    @Override
    public void onBindDeviceFail(String failMessage) {
        Log.e(TAG, "failed to connect to device " + failMessage);
    }

    @Override
    public void onConnectedDeviceSuccess() {
        Log.d(TAG,"device connected");
        cancelTimeOut();

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
        Log.d(TAG, "oxi scan timeout" + message);
        if (c208Invoker != null)
            startScanOxi();

    }

    @Override
    public void onDataResponse(int paramInt1, int paramInt2, String macAddress) {
        //send_patient_vital_data.setDisableNullRestriction(true);
        SendVitalData("BO",String.valueOf(paramInt1),macAddress);
        SendVitalData("HR",String.valueOf(paramInt2),macAddress);
        iChoiceOxiResultFragment fragment = iChoiceOxiResultFragment.newInstance(String.valueOf(paramInt1), String.valueOf(paramInt2));
        cancelTimeOut();
        openFragment(fragment);
//        quitButton.setVisibility(View.GONE);
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
        if (c208Invoker != null) {
            stopScanOxi();
            disconnectOxi();
            c208Invoker = null;
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
