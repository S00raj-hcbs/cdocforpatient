package com.cybermed.cdoc_patient.main;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;

import androidx.core.app.ActivityCompat;
import android.util.Log;

import com.cdfortis.datainterface.soap.WebService;
import com.cybermed.cdoc_patient.common.CDoctor2Application;
import com.cybermed.cdoc_patient.login.LoginInfo;

import java.net.NetworkInterface;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class TabletDataService extends Service {
    private static Timer timer = new Timer();
    private Context context;
    private LoginInfo loginInfo;

    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public void onCreate() {
        super.onCreate();
        context = this;

        loginInfo = ((CDoctor2Application) getApplication()).getLoginInfo();


        //Battery
        IntentFilter intentfilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(broadcastreceiver, intentfilter,Context.RECEIVER_EXPORTED);
        }else {
            context.registerReceiver(broadcastreceiver, intentfilter);
        }

        //Location
        getTabletLocation();

        //Version Number
        try {
            String currentVersion = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            setDeviceAppVersion(loginInfo, currentVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

//        startService();

    }

    private void startService() {

         //timer.scheduleAtFixedRate(new mainTask(), 0, 1800000);
         //timer.scheduleAtFixedRate(new mainTask(), 0, 10000);
        myThread.start();

    }

    private class mainTask extends TimerTask {
        public synchronized void run() {
            if(!myThread.isAlive()){
                myThread.start();
            }
        }
    }

    Thread myThread = new Thread(new Runnable() {
        @Override
        public void run() {
            Log.d("threaddebug", "runningthread");
            // Do Stuff
//            developerDebugLog("WakeLock - Refresh Dim WakeLock");
//            PowerManager mgr = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//            PowerManager.WakeLock wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "cdoc:tabletwakelock");
//            wakeLock.acquire(1800000);

            //Location
            getTabletLocation();

            //Version Number
            try {
                String currentVersion = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                setDeviceAppVersion(loginInfo, currentVersion);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

        }
    });


    private void getTabletLocation() {
        try{
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener, Looper.getMainLooper());
            if (location != null) {
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                String tabletLocation = latitude + "," + longitude;
                SetTabletLocation(loginInfo,tabletLocation);
            }
        }catch (Exception e){
        }

    }

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            String tabletLocation = latitude + "," + longitude;
            SetTabletLocation(loginInfo,tabletLocation);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };


//    private boolean isMyServiceRunning(Class<?> serviceClass) {
//        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//            if (serviceClass.getName().equals(service.service.getClassName())) {
//                return true;
//            }
//        }
//        return false;
//    }


    private BroadcastReceiver broadcastreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int deviceStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

            int rawlevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int level = -1;
            if (rawlevel >= 0 && scale > 0) {
                level = (rawlevel * 100) / scale;
            }

            if (deviceStatus == BatteryManager.BATTERY_STATUS_CHARGING) {
                setBatteryLevel(loginInfo,"Charging", String.valueOf(level));
                Log.d("Battery Level", "Battery Status = Charging " + level + "%");

            }

            if (deviceStatus == BatteryManager.BATTERY_STATUS_DISCHARGING) {
                setBatteryLevel(loginInfo,"Discharging", String.valueOf(level));

                Log.d("Battery Level", "Battery Status = Discharging " + level + "%");

            }

            if (deviceStatus == BatteryManager.BATTERY_STATUS_FULL) {
                setBatteryLevel(loginInfo,"Full", String.valueOf(level));

                Log.d("Battery Level", "Battery Status = Battery Full " + level + "%");

            }

            if (deviceStatus == BatteryManager.BATTERY_STATUS_UNKNOWN) {
                setBatteryLevel(loginInfo,"Unknown", String.valueOf(level));

                Log.d("Battery Level", "Battery Status = Unknown " + level + "%");
            }


            if (deviceStatus == BatteryManager.BATTERY_STATUS_NOT_CHARGING) {
                setBatteryLevel(loginInfo,"Not Charging", String.valueOf(level));

                Log.d("Battery Level", "Battery Status = Not Charging " + level + "%");

            }

        }
    };

    public static AsyncTask developerDebugLog(final String user_id, final String message) {
        return new AsyncTask<Void, Void, Integer>() {
            Exception e;
            private String timestamp;

            @Override
            protected void onPreExecute() {
                DateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa", Locale.US);
                timestamp = df.format(Calendar.getInstance().getTime());
            }

            @Override
            protected Integer doInBackground(Void... params) {
                try {
                    return WebService.getInstance().DeveloperDebugLog(user_id, message, timestamp);
                } catch (Exception e) {
                    this.e = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                if (e == null) {
                }
            }
        }.execute();
    }

    private static AsyncTask setBatteryLevel(final LoginInfo loginInfo, final String batteryStatus, final String batteryLevel) {
        return new AsyncTask<Void, Void, Integer>() {
            Exception e;
            String patientName;

            @Override
            protected void onPreExecute() {
                patientName = loginInfo.getUserInfo().getFirstName() + " " + loginInfo.getUserInfo().getMi() + " " + loginInfo.getUserInfo().getLastname();
            }

            @Override
            protected Integer doInBackground(Void... params) {
                try {
                    return WebService.getInstance().setBatteryLevel(loginInfo.getAccount(), patientName,
                            "Tablet", getMacAddr(), batteryStatus, batteryLevel);
                } catch (Exception e) {
                    this.e = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                if (e == null) {
                    if (integer == 1) {
                    }
                } else {
                }

            }
        }.execute();
    }

    private static AsyncTask setDeviceAppVersion(final LoginInfo loginInfo, final String appVersion) {
        return new AsyncTask<Void, Void, Integer>() {
            Exception e;
            String patientName;

            @Override
            protected void onPreExecute() {
                patientName = loginInfo.getUserInfo().getFirstName() + " " + loginInfo.getUserInfo().getMi() + " " + loginInfo.getUserInfo().getLastname();
            }

            @Override
            protected Integer doInBackground(Void... params) {
                try {
                    return WebService.getInstance().SetDeviceAppVersion(loginInfo.getAccount(), patientName,
                            "Tablet", getMacAddr(), appVersion);
                } catch (Exception e) {
                    this.e = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                if (e == null) {
                    if (integer == 1) {
                    }
                } else {
                }

            }
        }.execute();
    }


    private static AsyncTask SetTabletLocation(final LoginInfo loginInfo, final String location) {
        return new AsyncTask<Void, Void, Integer>() {
            Exception e;
            String patientName;

            @Override
            protected void onPreExecute() {
                patientName = loginInfo.getUserInfo().getFirstName() + " " + loginInfo.getUserInfo().getMi() + " " + loginInfo.getUserInfo().getLastname();
            }

            @Override
            protected Integer doInBackground(Void... params) {
                try {
                    return WebService.getInstance().SetTabletLocation(loginInfo.getAccount(), patientName,
                            "Tablet", getMacAddr(), location);
                } catch (Exception e) {
                    this.e = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                if (e == null) {
                    if (integer == 1) {
                    }
                } else {
                }

            }
        }.execute();
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


}
