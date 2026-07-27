package com.cybermed.cdoc_patient.bluetooth.Miscellaneous;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.cybermed.cdoc_patient.R;
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

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class TaiDocGlucometerActivity extends AppCompatActivity implements BluetoothAdapter.LeScanCallback, View.OnClickListener {
    private static final String TAG = TaiDocGlucometerActivity.class.getSimpleName();

    private BluetoothAdapter mAdapter;

    private AndroidBluetoothConnection mConnection;
    private static final boolean DEBUG = true;
    private AbstractMeter mTaiDocMeter = null;

    private String mMacAddress;
    private String mBtTransferType;
    private boolean mBLEMode;
    protected WifiManager.WifiLock lock = null;

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

    private FrameLayout fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taidoc_glucometer);

        fragmentContainer = findViewById(R.id.fragment_container);
        findViewById(R.id.btn_quit).setOnClickListener(TaiDocGlucometerActivity.this);

        mAdapter = BluetoothUtil.getBluetoothAdapter();
        mAdapter.startLeScan(this);

    }


    public void openFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_right, R.animator.enter_from_right, R.animator.exit_to_right);
        transaction.add(R.id.fragment_container, fragment, fragment.getClass().getSimpleName()).commit();
    }


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
                        mAdapter.stopLeScan(TaiDocGlucometerActivity.this);


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
            Log.d("workflowdebug", "disconnection2");
            mAdapter.startLeScan(TaiDocGlucometerActivity.this);
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
                    Log.d("workflowdebug", String.valueOf(bgValue));


                    Log.d(TAG, "glucose values: " + "glucose level：" + bgValue);
//                    sendPatVital("Glucose", String.valueOf(bgValue), mMacAddress);


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
                    Log.d("workflowdebug", "done");
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


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.cancelDiscovery();
            mAdapter = null;
        }
        disconnectMeter();
        Log.i(TAG, "onDestroy() , service stopped...");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_quit:
                finish();
                break;
        }
    }
}
