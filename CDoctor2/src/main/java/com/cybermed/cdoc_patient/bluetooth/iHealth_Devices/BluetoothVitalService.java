package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices;

import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.STATE.RESULT;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.cdfortis.datainterface.soap.WebService;
import com.choicemmed.blelibrary.base.DeviceType;
import com.choicemmed.blelibrary.cmd.P10bCmdListener;
import com.choicemmed.bp1blelibrary.Device.Bp1Device;
import com.choicemmed.bp1blelibrary.cmd.invoker.Bp1Invoker;
import com.choicemmed.bp1blelibrary.cmd.listener.Bp1Listener;
import com.choicemmed.c208blelibrary.Device.C208Device;
import com.choicemmed.c208blelibrary.cmd.invoker.C208Invoker;
import com.choicemmed.c208blelibrary.cmd.listener.C208BindDeviceListener;
import com.choicemmed.c208blelibrary.cmd.listener.C208CommandListener;
import com.choicemmed.c208blelibrary.cmd.listener.C208ConnectDeviceListener;
import com.choicemmed.c208blelibrary.cmd.listener.C208DisconnectCommandListener;
import com.choicemmed.s1blelibrary.Device.S1Device;
import com.choicemmed.s1blelibrary.cmd.invoker.S1Invoker;
import com.choicemmed.s1blelibrary.cmd.listener.S1Listener;
import com.cybermed.cdoc_patient.bluetooth.Miscellaneous.PCLinkLibraryDemoConstant;
import com.cybermed.cdoc_patient.common.CDoctor2Application;

import com.cybermed.cdoc_patient.webapi.IResponseReceiver;
import com.cybermed.cdoc_patient.webapi.manager.SendIOTDataManager;
import com.taidoc.pclinklibrary.android.bluetooth.util.BluetoothUtil;
import com.taidoc.pclinklibrary.connection.AndroidBluetoothConnection;
import com.taidoc.pclinklibrary.connection.util.ConnectionManager;
import com.taidoc.pclinklibrary.constant.PCLinkLibraryConstant;
import com.taidoc.pclinklibrary.constant.PCLinkLibraryEnum;
import com.taidoc.pclinklibrary.exceptions.CommunicationTimeoutException;
import com.taidoc.pclinklibrary.exceptions.ExceedRetryTimesException;
import com.taidoc.pclinklibrary.exceptions.NotConnectSerialPortException;
import com.taidoc.pclinklibrary.exceptions.NotSupportMeterException;
import com.taidoc.pclinklibrary.meter.AbstractMeter;
import com.taidoc.pclinklibrary.meter.record.AbstractRecord;
import com.taidoc.pclinklibrary.meter.record.BloodGlucoseRecord;
import com.taidoc.pclinklibrary.meter.util.MeterManager;

import java.net.NetworkInterface;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class BluetoothVitalService extends Service implements S1Listener, Bp1Listener, C208BindDeviceListener, C208CommandListener,
        C208ConnectDeviceListener, C208DisconnectCommandListener, P10bCmdListener, BluetoothAdapter.LeScanCallback {
    private static final String TAG = BluetoothVitalService.class.getSimpleName();
    private AsyncTask sendPatVitalTask;

    private int bindingFailedIndicator = 0;

    private S1Invoker scaleInvoker;
    private Bp1Invoker bp1Invoker;
    private C208Invoker oxiInvoker;

    private long scanPeriod = 5000;
    private boolean setHandler = false;

    private static final int MSG_BINDS1 = 0;
    private static final int MSG_DISCONNECTS1 = 1;
    private static final int scaleBind = 2;
    private static final int bpBind = 3;
    private static final int oxiBind = 0;

    private BluetoothAdapter mAdapter;

    private AndroidBluetoothConnection mConnection;
    private static final boolean DEBUG = true;
    private AbstractMeter mTaiDocMeter = null;

    private String mMacAddress;
    private String mBtTransferType;
    private boolean mBLEMode;
    protected WifiManager.WifiLock lock = null;

    // Message types sent from the meterCommuHandler Handler
    public static final int MESSAGE_STATE_CONNECTING = 1;
    public static final int MESSAGE_STATE_CONNECT_FAIL = 2;
    public static final int MESSAGE_STATE_CONNECT_DONE = 3;
    public static final int MESSAGE_STATE_CONNECT_NONE = 4;
    public static final int MESSAGE_STATE_CONNECT_METER_SUCCESS = 5;
    public static final int MESSAGE_STATE_CHECK_METER_INFORMATION = 6;
    public static final int MESSAGE_STATE_CHECK_METER_BT_DISTENCE = 7;
    public static final int MESSAGE_STATE_CHECK_METER_BT_DISTENCE_FAIL = 8;
    public static final int MESSAGE_STATE_NOT_SUPPORT_METER = 9;
    public static final int MESSAGE_STATE_NOT_CONNECT_SERIAL_PORT = 10;
    public static final int MESSAGE_STATE_SCANED_DEVICE = 11;


    private static final int HANDLER_SCAN = 101;
    private static final int HANDLER_CONNECTED = 102;
    private static final int HANDLER_DISCONNECT = 103;
    private static final int HANDLER_USER_STATUE = 104;


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            setHandler = false;
            switch (msg.what) {
                case MSG_BINDS1:
//                    developerDebugLog("Bluetooth Debug - binding IOT devices");
                    bindS1();
                    bindBpl();
                    bindOxi();
//                    bindEcg();
                    break;
                case MSG_DISCONNECTS1:
//                    developerDebugLog("Bluetooth Debug - disconnecting IOT devices");
                    disconnectS1();
                    disconnectBpl();
                    disconnectOxi();
                    break;
                default:
                    break;
            }
        }
    };

    private void sendMsg_BindAllDevices(long delayMillis) {
        if (!setHandler) {
            setHandler = true;
            Log.d(TAG, "bindingagain");
            mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_BINDS1), delayMillis);
        }
    }

    private void sendMsg_DisconnectS1() {
        mHandler.obtainMessage(MSG_DISCONNECTS1).sendToTarget();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int bluetoothState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (bluetoothState) {
                    case BluetoothAdapter.STATE_ON:
                        //Bluethooth is on, now you can perform your tasks
                        Log.d(TAG, "isEnabled?" + String.valueOf(mBluetoothAdapter.isEnabled()));
                        sendMsg_BindAllDevices(scanPeriod);
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        mBluetoothAdapter.enable();
                        Log.d(TAG, "isEnabled?" + String.valueOf(mBluetoothAdapter.isEnabled()));
                }
            }
        }
    };

    public BluetoothVitalService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"onStartCommand() - start_sticky");
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        WifiManager manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        lock = manager.createWifiLock("com.elsdoerfer.wifilock");
        lock.acquire();
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(mReceiver, filter,Context.RECEIVER_EXPORTED);
        }else {
            registerReceiver(mReceiver, filter);
        }


        initInvoker();

        bindS1();
        bindBpl();
        bindOxi();

        mAdapter = BluetoothUtil.getBluetoothAdapter();
        mAdapter.startLeScan(this);

    }

    //////////iChoice Devices: Oximeter, Scale, Blood Pressure Monitor///////////

    private void initInvoker() {
        scaleInvoker = new S1Invoker(BluetoothVitalService.this, this);
        bp1Invoker = new Bp1Invoker(BluetoothVitalService.this, this);
        oxiInvoker = new C208Invoker(BluetoothVitalService.this);
//        p10bCmdInvoker = new P10bCmd(BluetoothVitalService.this, this);
    }


    private void disconnectS1() {
        Log.d(TAG, "disconnecting scale");
        scaleInvoker.disconnectDevice();
    }

    private void disconnectBpl() {
        Log.d(TAG, "disconnecting bp monitor");
        bp1Invoker.disconnectDevice();
    }

    private void disconnectOxi() {
        Log.d(TAG, "disconnecting oxi");
        oxiInvoker.disconnectDevice(this);
    }

    private void disconnectEcg() {
        Log.d(TAG, "disconnecting ecg");
//        p10bCmdInvoker.onDisconnected();
    }

    private void bindS1() {
        Log.d(TAG, "binding scale");
        scaleInvoker.bindDevice();
    }

    private void bindBpl() {
        Log.d(TAG, "binding bp monitor");
        bp1Invoker.bindDevice();
    }

    private void bindOxi() {
        Log.d(TAG, "binding oximeter");
        oxiInvoker.bindDevice(this);
    }

//    private void bindEcg() {
//        Log.d(TAG, "binding ecg");
//        p10bCmdInvoker.startLeScan();
//    }

    @Override
    public void onBindDeviceSuccess(S1Device scaleDevice) {
        Log.d(TAG, "binding scale success");
//        developerDebugLog("Bluetooth Debug - binding SCALE success");
    }

    @Override
    public void onBindDeviceSuccess(Bp1Device bplDevice) {
        Log.d(TAG, "binding bp monitor success");
//        developerDebugLog("Bluetooth Debug - binding BP success");
    }

    @Override
    public void onBindDeviceSuccess(C208Device oxiDevice) {
        Log.d(TAG, "binding oximeter success");
//        developerDebugLog("Bluetooth Debug - binding OXI success");

    }

    @Override
    public void onBindDeviceFail(String failMessage) {
        Log.d(TAG, "binding failed");
//        Log.d(TAG, "binding failed, re-binding now");
//        bindingFailedIndicator++;
//        if(bindingFailedIndicator == 3) {
//            bindingFailedIndicator = 0;
//            sendMsg_BindAllDevices(scanPeriod);
//        }
    }

    @Override
    public void onDataResponse(double weight, String macAddress) {
        Log.d(TAG, "scale values: " + String.format("%.1f", weight) + "kg");
//        developerDebugLog("Bluetooth Debug - retrieved scale data");
        sendPatVital("weight", String.format("%.1f", weight), macAddress);
    }

    @Override
    public void onDataResponse(int bph, int bpl, int hr, String macAddress) {
        Log.d(TAG, "blood pressure monitor values: " + "bph：" + bph + "；bpl：" + bpl + "；heart rate：" + hr);
//        developerDebugLog("Bluetooth Debug - retrieved bp data");
        sendPatVital("BP", bph + ":" + bpl, macAddress);
    }

    @Override
    public void onDataResponse(int spo, int pr, String macAddress) {
        Log.d(TAG, "oximeter values: " + "oxygen level：" + spo + "；pulse rate：" + pr);
//        developerDebugLog("Bluetooth Debug - retrieved oxi data");
        sendPatVital("BO", String.valueOf(spo), macAddress);
        sendPatVital("HR", String.valueOf(pr), macAddress);

    }

    @Override
    public boolean onEcgDataResponse(String paramString) {
        Log.d(TAG, "ecg values: " + paramString);
        return false;
    }

    @Override
    public void onError(String message) {
        Log.e(TAG, message);
        Log.d(TAG,"onerror" + message);
//        sendMsg_DisconnectS1();

        sendMsg_BindAllDevices(scanPeriod);
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.disable();
        Log.d(TAG, String.valueOf(mBluetoothAdapter.isEnabled()));
        mBluetoothAdapter.enable();
    }

    @Override
    public void onConnectedDeviceSuccess() {
        Log.e(TAG, "connected to device");
    }

    @Override
    public void onConnectedDeviceFail(String failMessage) {
        Log.d(TAG,"onconnecteddebvicefail");
        Log.e(TAG, "failed to connect to device " + failMessage);
    }


    @Override
    public void onDisconnected() {
        Log.e(TAG, "diconnecting");
//        sendMsg_DisconnectS1();
    }

    @Override
    public void onStateChanged(int bleState, int state) {
        Log.e(TAG, "bleState" + bleState + "state" + state);
        if (state == S1Invoker.BLUETOOTH_FREE && state == C208Invoker.BLUETOOTH_FREE && state == Bp1Invoker.BLUETOOTH_FREE) {
            sendMsg_BindAllDevices(scanPeriod);
        }
    }

    @Override
    public void onScanTimeout(String message) {

    }

    @Override
    public void onScanTimeout(com.choicemmed.bp1blelibrary.base.DeviceType deviceType) {

    }

    @Override
    public void setUnitSuccess() {
        Log.e(TAG, "unitsuccess");
    }

    @Override
    public void setUnitError(String msg) {
        Log.e(TAG, "uniterror");
    }


    //////FOR ECG DEVICE
    @Override
    public void onFoundDevice(DeviceType paramDeviceType, String paramString1, String paramString2) {

    }

    @Override
    public void onScanTimeout(DeviceType paramDeviceType) {

        sendMsg_BindAllDevices(scanPeriod);
    }

    @Override
    public void onError(DeviceType paramDeviceType, String paramString) {

        sendMsg_BindAllDevices(scanPeriod);
    }

    @Override
    public void onDisconnected(DeviceType paramDeviceType) {

        sendMsg_BindAllDevices(scanPeriod);
    }

    @Override
    public void onLoadBegin() {

    }

    @Override
    public void onRecordInfoResponse(String paramString1, String paramString2, int paramInt) {

    }

    @Override
    public void onLoadEnd(boolean paramBoolean, String paramString) {

    }


    /////////

    private void sendPatVital(String type, final String value, final String device_mac_address){
        sendPatVitalAsyncTask(type,value,device_mac_address);

    }
    String timestamp;
    private void sendPatVitalAsyncTask(final String type, final String value, final String device_mac_address) {
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
            df.setTimeZone(TimeZone.getTimeZone("GMT"));
            timestamp = df.format(Calendar.getInstance().getTime());
            Log.d(TAG, "timestamp: " + timestamp);

        SendIOTDataManager sendIOTDataManager=  new SendIOTDataManager(new IResponseReceiver() {
            @Override
            public void onSuccess(Object data) {
                RESULT.setIndex("1");
            }

            @Override
            public void onFailure(@NonNull String errorResponse) {

            }
        });
        sendIOTDataManager.callGetEnableLog(type, value, timestamp, device_mac_address, getMacAddr(), null, null, null);


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

    @Override
    public void onDestroy() {
//        lock.release();

        disconnectS1();
        disconnectBpl();
        disconnectOxi();
        disconnectMeter();
        mAdapter.stopLeScan(BluetoothVitalService.this);
        mHandler.removeCallbacksAndMessages(null);

        Log.i(TAG, "onCreate() , service stopped...");
    }

    /////////////TaiDoc: Blood Pressure Monitor//////////////////

    @Override
    public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
//        Log.d("workflowdebug", "device2");
        final String str = bytes2HexString(scanRecord).replace("-", "").toLowerCase();
        new Thread() {
            @Override
            public void run() {
                try {
//                    Log.d("workflowdebug", str);
                    /* 54 */
                    if (str.contains("180a180f1808030705010204001903")) {
                        Log.d("workflowdebug", (device.getAddress() != null) ? device.getAddress() : "N/A");
                        mAdapter.stopLeScan(BluetoothVitalService.this);


                        mMacAddress = device.getAddress();
                        mBtTransferType = PCLinkLibraryDemoConstant.BT_TRANSFER_TYPE_TWO;
                        mBLEMode = true;
                        mTaiDocMeter = null;


                        Log.d("workflowdebug", "connectble");
                        if ("".equals(mMacAddress)) {
                            // 如果是用listen且meter支援ble的話則進入
                            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                                setupAndroidBluetoothConnection();
                                connectMeter();
                            }
                        } else if ("".equals(mBtTransferType)) {
                        } else if (mTaiDocMeter == null) {
                            setupAndroidBluetoothConnection();
                            connectMeter();
                        }

//                        Intent intent = new Intent(FullScreenActivity.this, PCLinkLibraryCommuTestActivity.class);
//                        startActivity(intent);


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    public static String bytes2HexString(byte[] a) {
        int len = a.length;
        byte[] b = new byte[len];
        for (int k = 0; k < len; k++) {
            b[k] = a[(a.length - 1 - k)];
        }
        String ret = "";
        for (int i = 0; i < len; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret = ret + hex.toUpperCase();
        }
        return ret;
    }


    private AndroidBluetoothConnection.LeConnectedListener mLeConnectedListener = new AndroidBluetoothConnection.LeConnectedListener() {

        @Override
        public void onConnectionTimeout() {
//            dimissProcessDialog();
        }

        @Override
        public void onConnectionStateChange_Disconnect(BluetoothGatt gatt,
                                                       int status, int newState) {
            Log.d("workflowdebug","disconnection2");
            mAdapter.startLeScan(BluetoothVitalService.this);
//            dimissProcessDialog();
        }

        @SuppressLint("NewApi")
        @Override
        public void onDescriptorWrite_Complete(BluetoothGatt gatt,
                                               BluetoothGattDescriptor descriptor, int status) {
            mConnection.LeConnected(gatt.getDevice());
        }

        @Override
        public void onCharacteristicChanged_Notify(BluetoothGatt gatt,
                                                   BluetoothGattCharacteristic characteristic) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();

                    try {
                        Log.d("workflowconnection", "mconnection1");
                        mTaiDocMeter = MeterManager.detectConnectedMeter(mConnection);
                    } catch (Exception e) {
                        if (PCLinkLibraryDemoConstant.PL2303_TRANSFER_TYPE.equals(mBtTransferType)) {
                            meterCommuHandler.sendEmptyMessage(MESSAGE_STATE_NOT_CONNECT_SERIAL_PORT);
                        } else {
                            meterCommuHandler.sendEmptyMessage(MESSAGE_STATE_NOT_SUPPORT_METER);
                        }
                    }



                    AbstractRecord record = mTaiDocMeter.getStorageDataRecord(0,
                            PCLinkLibraryEnum.User.CurrentUser);
                    SimpleDateFormat formatterDate = new SimpleDateFormat("yyyy/MM/dd hh:mm aa");

                    String measurementDate = formatterDate
                            .format(((BloodGlucoseRecord) record).getMeasureTime());
                    int bgValue = ((BloodGlucoseRecord) record).getGlucoseValue();
                    Log.d("workflowdebug",String.valueOf(bgValue));



                    Log.d(TAG, "glucose values: " + "glucose level：" + bgValue);
                    sendPatVital("Glucose", String.valueOf(bgValue), mMacAddress);
//                    BluetoothVitalService.this.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            dimissProcessDialog();
//                            if (mTaiDocMeter == null) {
//                                //throw new NotSupportMeterException();
//                                meterCommuHandler.sendEmptyMessage(MESSAGE_STATE_NOT_SUPPORT_METER);
//                            }
//                        }
//                    });

                    Looper.loop();
                }
            }).start();
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            // TODO Auto-generated method stub

        }
    };

    // Handlers
    // The Handler that gets information back from the android bluetooth connection
    private final Handler mBTConnectionHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case PCLinkLibraryConstant.MESSAGE_STATE_CHANGE:
                        if (DEBUG) {
                            Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                        } /* end of if */
                        switch (msg.arg1) {
                            case AndroidBluetoothConnection.STATE_CONNECTED_BY_LISTEN_MODE:
                                try {
                                    Log.d("workflowconnection", "mconnection2");
                                    mTaiDocMeter = MeterManager.detectConnectedMeter(mConnection);
                                } catch (Exception e) {
                                    throw new NotSupportMeterException();
                                }
//                                dimissProcessDialog();
                                if (mTaiDocMeter == null) {
                                    throw new NotSupportMeterException();
                                }/* end of if */
                                break;
                            case AndroidBluetoothConnection.STATE_CONNECTING:
                                // 暫無需特別處理的事項
                                break;
                            case AndroidBluetoothConnection.STATE_SCANED_DEVICE:
                                meterCommuHandler.sendEmptyMessage(MESSAGE_STATE_SCANED_DEVICE);
                                break;
                            case AndroidBluetoothConnection.STATE_LISTEN:
                                // 暫無需特別處理的事項
                                break;
                            case AndroidBluetoothConnection.STATE_NONE:
                                // 暫無需特別處理的事項
                                break;
                        } /* end of switch */
                        break;
                    case PCLinkLibraryConstant.MESSAGE_TOAST:
                        // 暫無需特別處理的事項
                        break;
                    default:
                        break;
                } /* end of switch */
            } catch (NotSupportMeterException e) {
                Log.e(TAG, "not support meter", e);
            } /* end of try-catch */
        }
    };

    private final Handler meterCommuHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CONNECTING:
//                    mProcessDialog = ProgressDialog.show(FullScreenActivity.this, null,
//                            "Start connecting meter and get result, please wait a while", true);
//                    mProcessDialog.setCancelable(false);
                    break;
                case MESSAGE_STATE_SCANED_DEVICE:
                    // 取得Bluetooth Device資訊
                    final BluetoothDevice device = BluetoothUtil.getPairedDevice(mConnection.getConnectedDeviceAddress());
                    // Attempt to connect to the device
                    mConnection.LeConnect(getApplicationContext(), device);
                    // 在mLeConnectedListener會收
                    break;
                case MESSAGE_STATE_CONNECT_DONE:
                    Log.d("workflowdebug","done");
//                    dimissProcessDialog();
                    break;
                case MESSAGE_STATE_CONNECT_FAIL:
//                    dimissProcessDialog();
                    break;
                case MESSAGE_STATE_CONNECT_NONE:
//                    dimissProcessDialog();
                    if (PCLinkLibraryDemoConstant.PL2303_TRANSFER_TYPE.equals(mBtTransferType)) {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(PCLinkLibraryDemoConstant.FromPL2303, true);
                    } else {
                    }
                    break;
                case MESSAGE_STATE_CONNECT_METER_SUCCESS:
                    break;
                case MESSAGE_STATE_CHECK_METER_BT_DISTENCE:
//                    ProgressDialog baCmdDialog = new ProgressDialog(
//                            FullScreenActivity.this);
//                    baCmdDialog.setCancelable(false);
//                    baCmdDialog.setMessage("send ba command");
//                    baCmdDialog.setButton(DialogInterface.BUTTON_POSITIVE, "cancel",
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    // Use either finish() or return() to either close the activity
//                                    // or just
//                                    // the dialog
//                                    dialog.dismiss();
//                                    return;
//                                }
//                            });
//                    baCmdDialog.show();
                    break;
                case MESSAGE_STATE_CHECK_METER_BT_DISTENCE_FAIL:
                    break;
                case MESSAGE_STATE_NOT_SUPPORT_METER:
//                    dimissProcessDialog();
                    break;
                case MESSAGE_STATE_NOT_CONNECT_SERIAL_PORT:
                    break;
            } /* end of switch */
        }
    };


    private void updatePairedList() {
        Map<String, String> addrs = new HashMap<String, String>();
        String addrKey = PCLinkLibraryDemoConstant.BLE_PAIRED_METER_ADDR_ + String.valueOf(0);
        addrs.put(addrKey, mMacAddress);
        mConnection.updatePairedList(addrs, 1);
    }

    /**
     * Connect Meter
     */
    private void connectMeter() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                try {
                    meterCommuHandler.sendEmptyMessage(MESSAGE_STATE_CONNECTING);
                    if (mBLEMode) {
                        updatePairedList();
                        mConnection.setLeConnectedListener(mLeConnectedListener);

                        if (mConnection.getState() == AndroidBluetoothConnection.STATE_NONE) {
                            // Start the Android Bluetooth connection services to listen mode
                            mConnection.LeListen();

                            if (DEBUG) {
                                Log.i(TAG, "into listen mode");
                            }
                        }

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (mConnection.getState() == AndroidBluetoothConnection.STATE_LISTEN) {
                                    if (mLeConnectedListener != null) {
                                        mLeConnectedListener.onConnectionTimeout();
                                    }
                                }
                            }
                        }, 10000);
                    } else {
                        // Only if the state is STATE_NONE, do we know that we haven't started
                        // already
                        if (mConnection.getState() == AndroidBluetoothConnection.STATE_NONE) {
                            // Start the Android Bluetooth connection services to listen mode
                            mConnection.listen();

                            if (DEBUG) {
                                Log.i(TAG, "into listen mode");
                            }
                        }
                    }


                } catch (CommunicationTimeoutException e) {
                    Log.e(TAG, e.getMessage(), e);
                    meterCommuHandler.sendEmptyMessage(MESSAGE_STATE_CONNECT_FAIL);
                } catch (NotSupportMeterException e) {
                    Log.e(TAG, "not support meter", e);
                    meterCommuHandler.sendEmptyMessage(MESSAGE_STATE_NOT_SUPPORT_METER);
                } catch (NotConnectSerialPortException e) {
                    meterCommuHandler.sendEmptyMessage(MESSAGE_STATE_NOT_CONNECT_SERIAL_PORT);
                } catch (ExceedRetryTimesException e) {
                    if (PCLinkLibraryDemoConstant.PL2303_TRANSFER_TYPE.equals(mBtTransferType)) {
                        meterCommuHandler.sendEmptyMessage(MESSAGE_STATE_NOT_CONNECT_SERIAL_PORT);
                    } else {
                        meterCommuHandler.sendEmptyMessage(MESSAGE_STATE_NOT_SUPPORT_METER);
                    }
                } finally {
                    if (PCLinkLibraryDemoConstant.BT_TRANSFER_TYPE_ONE.equals(mBtTransferType) ||
                            PCLinkLibraryDemoConstant.PL2303_TRANSFER_TYPE.equals(mBtTransferType)) {
                        meterCommuHandler.sendEmptyMessage(MESSAGE_STATE_CONNECT_DONE);
                    }
                }
                Looper.loop();
            }
        }).start();
    }

    /**
     * // 關閉Process dialog
     */
//    private void dimissProcessDialog() {
//        if (mProcessDialog != null) {
//            mProcessDialog.dismiss();
//            mProcessDialog = null;
//        } /* end of if */
//    }

    /**
     * 關閉Meter
     */
    private void disconnectMeter() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                try {
                    if (mTaiDocMeter != null) {
                        mTaiDocMeter.turnOffMeterOrBluetooth(0);
                    }

                    if (mBLEMode) {
                        mConnection.setLeConnectedListener(null);
                        mConnection.LeDisconnect();
                    } else {
                        mConnection.disconnect();
                        mConnection.LeDisconnect();
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                } finally {
                }/* end of try-catch-finally */
                Looper.loop();
            }
        }).start();
    }

    /**
     * 初始化 Android Bluetooth Connection
     */
    private void setupAndroidBluetoothConnection() {
        if (mConnection == null) {
            Log.d(TAG, "setupAndroidBluetoothConnection()");
            // 這裡一定要用一個try-catch, 因為在4.3以前是無法用ble的,會造成runtime error
            try {
                mConnection = ConnectionManager.createAndroidBluetoothConnection(mBTConnectionHandler);
                mConnection.canScanV3KNV(false);
            } catch (Exception ee) {
            }

        } /* end of if */
    }



    private AsyncTask developerDebugLog(final String message) {
        return new AsyncTask<Void, Void, Integer>() {
            Exception e;
            private String timestamp;

            @Override
            protected void onPreExecute() {
                DateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
                timestamp = df.format(Calendar.getInstance().getTime());
            }

            @Override
            protected Integer doInBackground(Void... params) {
                try {
                    return WebService.getInstance().DeveloperDebugLog(((CDoctor2Application) getApplication()).getLoginInfo().getAccount()
                            ,message,timestamp);
                } catch (Exception e) {
                    this.e = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                if (e == null) {
                    if (integer == 1) {
                    }
                } else {
                }

            }
        }.execute();
    }


//
//    @Override
//    protected void onStop() {
//        super.onStop();
//
//        disconnectMeter();
//        dimissProcessDialog();
//    }
//
//    @Override
//    protected void onResume() {
//        // TODO Auto-generated method stub
//        super.onResume();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//    }

}
