package com.cybermed.cdoc_patient.signalr;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.cybermed.cdoc_patient.common.CDoctor2Application;

import microsoft.aspnet.signalr.client.Action;
import microsoft.aspnet.signalr.client.ConnectionState;
import microsoft.aspnet.signalr.client.ErrorCallback;
import microsoft.aspnet.signalr.client.LogLevel;
import microsoft.aspnet.signalr.client.Logger;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;

import static com.cybermed.cdoc_patient.common.BaseActivity.STATUS_OFF_LINE;
import static com.cybermed.cdoc_patient.common.CDoctor2Application.application;
import static com.cybermed.cdoc_patient.util.AppConstant.ONLINE_STATUS;
import static com.cybermed.cdoc_patient.util.AppConstant.PROVIDER_CODE;
import static com.cybermed.cdoc_patient.util.AppConstant.RECEIVE_LOGOUT;
import static com.cybermed.cdoc_patient.util.AppConstant.SIGNALR_ONLINE;

import androidx.core.app.ActivityCompat;


public class SignalRService extends Service {
    private final HubConnection mHubConnection;
    private HubProxy mHubProxy;
    private Handler mHandler; // to display Toast message
    private final LocalBinder mBinder = new LocalBinder();
    public Boolean is_service_connected = false;
    boolean logoutAllDevices,isReconnecting;
     int status=1;

    public SignalRService() {
        Logger logger = new Logger() {
            @Override
            public void log(String message, LogLevel level) {

                 //Log.d("Log Message : ", message);
            }
        };

        //provider code is hard code for now
        QueryStringObj qso = new QueryStringObj("patient", CDoctor2Application.getLoginInfo().getAccount(),
                CDoctor2Application.getLoginInfo().getUserInfo().getService_code(),
                CDoctor2Application.getLoginInfo().getOneSignalUserId());

        //change to www.cdoconline.com after it's released
        //  https://qacdoc.cybermedehr.com/signalr
       // mHubConnection = new HubConnection("https://www.cdoconline.com/signalr", qso.toString(), true, logger);
        mHubConnection = new HubConnection("https://login.cybermedehr.com/signalr", qso.toString(), true, logger);


    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("service", "Inside oncreate  - service");
        mHandler = new Handler(Looper.getMainLooper());
        startSignalR();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("service", "service start  - service");
        int result = super.onStartCommand(intent, flags, startId);
        if (intent != null) {
             status = intent.getIntExtra("status", 1);
             logoutAllDevices = intent.getBooleanExtra("logoutAllDevices", false);
            if (status == STATUS_OFF_LINE) {
                setDeviceStatus();
                setStatus(status, logoutAllDevices);
            }
        }


        return result;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("Unbounding", "SignalRservice Service unbound");
        return super.onUnbind(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Return the communication channel to the service.
        Log.d("service", "onBind  - service");
        return (IBinder) mBinder;
    }


    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public SignalRService getService() {
            // Return this instance of SignalRService so clients can call public methods
            return SignalRService.this;
        }
    }

    /**
     * method for clients (activities)
     */
    public void setStatus(int status, boolean logoutAllDevices) {
        Log.d("Inside : ", "getIncommingcht - service - Method");

        mHubProxy.invoke("Patient_setOnlineStatus", CDoctor2Application.getLoginInfo().getUserInfo().getService_code(),
                CDoctor2Application.getLoginInfo().getAccount(), CDoctor2Application.getLoginInfo().getOneSignalUserId(),
                String.valueOf(status), logoutAllDevices).onError(new ErrorCallback() {
            @Override
            public void onError(Throwable throwable) {
                System.out.println("error signalr");
            }
        }).done(new microsoft.aspnet.signalr.client.Action<Void>() {
            @Override
            public void run(Void aVoid) throws Exception {
                if (status == STATUS_OFF_LINE) {
                    isReconnecting=true;
                    mHubConnection.stop();
                    stopSelf();
                }
            }
        });

    }
    /**
     * method for DeviceStatus (activities)
     */
    public void setDeviceStatus() {
        Log.d("Inside : ", "DeviceStatus - service - Method");
        boolean is_camera_enabled = ActivityCompat.checkSelfPermission(application, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean is_microphone_enabled = ActivityCompat.checkSelfPermission(application, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
        mHubProxy.invoke("Patient_setDeviceStatus", CDoctor2Application.getLoginInfo().getUserInfo().getService_code(),
                CDoctor2Application.getLoginInfo().getAccount(), CDoctor2Application.getLoginInfo().getOneSignalUserId(),
                is_camera_enabled, is_microphone_enabled).onError(new ErrorCallback() {
            @Override
            public void onError(Throwable throwable) {
                Log.e("SignalR", "Error while calling Patient_setDeviceStatus", throwable);
            }
        }).done(new microsoft.aspnet.signalr.client.Action<Void>() {
            @Override
            public void run(Void aVoid) throws Exception {
                Log.d("SignalR", "Patient_setDeviceStatus call succeeded.");
            }
        });

    }

    /**
     * start signalr
     */
    private void startSignalR() {
       // mHubProxy = mHubConnection.createHubProxy("SignalRHub_CDocHub");
        mHubProxy = mHubConnection.createHubProxy("signalr_hub");
        /*mHubProxy.on("on_patient_device_status_changed", (set_to_online_status) -> {
            Log.d("signalr", set_to_online_status );
        }, String.class);*/

        mHubProxy.on("on_provider_online_status_changed", (org_code, provider_code, online_status) -> {
            Log.d("signalr", org_code + ", " + provider_code + ", " + online_status);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {

                @Override
                public void run() {
                    Intent intent = new Intent(SIGNALR_ONLINE);
                    intent.putExtra(ONLINE_STATUS, online_status);
                    intent.putExtra(PROVIDER_CODE, provider_code);
                    sendBroadcast(intent);
                }
            });

        }, String.class, String.class, String.class);

        mHubProxy.on("on_patient_logout_all", (set_to_online_status) -> {
            Log.d("signalr+logout", set_to_online_status);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {

                @Override
                public void run() {
                    Intent intent = new Intent(RECEIVE_LOGOUT);
                    //LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                }
            });

        }, String.class);


        mHubConnection.error(new ErrorCallback() {
            @Override
            public void onError(Throwable error) {
                error.printStackTrace();
                reconnectingSignalR();
            }
        });


        // Subscribe to the connected event
        mHubConnection.connected(new Runnable() {

            @Override
            public void run() {
                Log.d("signalr", "CONNECTED");
                //setDeviceStatus();
                is_service_connected = true;
                setDeviceStatus();
                setStatus(status, logoutAllDevices);

            }
        });

        // Subscribe to the closed event
        mHubConnection.closed(new Runnable() {

            @Override
            public void run() {
                System.out.println("DISCONNECTED");
            }
        });

        // Start the connection
        mHubConnection.start().done(new Action<Void>() {
            @Override
            public void run(Void aVoid) throws Exception {
                System.out.println("DONE CONNECTING");
            }
        });


    }

    /**
     * reconnecting connection
     */
    private void reconnectingSignalR() {
        Log.d("signalr", "reconnecting");
        if (mHubConnection.getState() == ConnectionState.Disconnected && !isReconnecting) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mHubConnection.start();
                }
            }, 1000);

        }
    }


}