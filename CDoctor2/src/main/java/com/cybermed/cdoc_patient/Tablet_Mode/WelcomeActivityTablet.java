package com.cybermed.cdoc_patient.Tablet_Mode;

import static com.cdfortis.datainterface.soap.WebServiceID.developer_debug_log;
import static com.cdfortis.datainterface.soap.WebServiceID.get_PatientDemographic_Android;
import static com.cdfortis.datainterface.soap.WebServiceID.get_cybermed_code_from_mac_address;
import static com.cdfortis.datainterface.soap.WebServiceID.get_patient_IoT_mode;
import static com.cdfortis.datainterface.soap.WebServiceID.get_patient_user_id_by_mac_address;
import static com.cdfortis.datainterface.soap.WebServiceID.setPatientOnlineStatus;
import static com.cdfortis.datainterface.soap.WebServiceID.set_patient_IoT_device_log;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealthScannedDeviceFragment.CYBERMED_CODE_REGEX;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.cdfortis.datainterface.soap.UserInfo;
import com.cdfortis.datainterface.soap.WebService;
import com.cdfortis.datainterface.soap.model.Patient_Info;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.Tablet_Mode.EventBusMessage.AsyncThread;
import com.cybermed.cdoc_patient.Tablet_Mode.EventBusMessage.MainThread;
import com.cybermed.cdoc_patient.common.CDoctor2Application;
import com.cybermed.cdoc_patient.common.CommonAsyncTaskActivity;
import com.cybermed.cdoc_patient.doctor.VideoCallManager;
import com.cybermed.cdoc_patient.login.ResponseAppId;
import com.cybermed.cdoc_patient.main.FragmentMainActivity;
import com.cybermed.cdoc_patient.webapi.IResponseReceiver;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.ksoap2.serialization.SoapObject;

import java.net.NetworkInterface;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Maybe;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class WelcomeActivityTablet extends CommonAsyncTaskActivity {
    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION_PHARM = 12;
    private Unbinder unbinder;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkDataConnectionAndVersion(2);

        unbinder = ButterKnife.bind(this);

        //Removes bug where app opened from playstore will restart on push notification received
        //App store creates duplicate instance of the app
        if (!isTaskRoot()
                && getIntent().hasCategory(Intent.CATEGORY_LAUNCHER)
                && Intent.ACTION_MAIN.equals(getIntent().getAction())) {
            finish();
            return;
        }

        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        //some random getCode. Not because i dont want to break;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSION_ACCESS_COARSE_LOCATION_PHARM);
        }


        setContentView(R.layout.activity_splash_screen);


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        editor.putBoolean("tabletmode", true);
        editor.apply();

        CDoctor2Application.setTabletMode(true);
        callGetAppId();
        turnOnWIFI();
        logVersionNumberInDatabase();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        initLang();
    }


    public void turnOnWIFI() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled())
            wifiManager.setWifiEnabled(true);
    }

    // Called in a separate thread
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void LoadData(AsyncThread messageEvent) {

        while (true) {
            //10:7b:44:bd:24:47
            //String device_mac_address = "a8:34:6a:9d:3b:84";
            String device_mac_address = getWIFIMacAddr();
            //Toast.makeText(this, "Mac Address: " + device_mac_address, Toast.LENGTH_LONG).show();
            Object user_id = WebService.getInstance().callingWebservice(get_patient_user_id_by_mac_address, device_mac_address);
            if (validate(user_id.toString())) {
                Object obj = WebService.getInstance().callingWebservice(get_PatientDemographic_Android, user_id.toString());
                Patient_Info patientInfo = new Patient_Info((SoapObject) obj);

                getCDocApplication().setLogin(true);
                UserInfo userInfo = new UserInfo();
                userInfo.deserialize(patientInfo);
                getCDocApplication().processUserLogin2(user_id.toString(), "", userInfo);

                WebService.webServiceAsyncTask(set_patient_IoT_device_log, getLoginInfo2().getAccount(), "Tablet", device_mac_address, getLoginInfo2().getOneSignalUserId());

                String iotModeString = WebService.getInstance().callingWebservice(get_patient_IoT_mode, user_id.toString()).toString();
                int iotMode = Integer.valueOf(iotModeString);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("iotmode", iotMode);
                editor.apply();

                String oneSignalId = "OS^" + getLoginInfo2().getOneSignalUserId();
                String online_status_result = WebService.getInstance().callingWebservice(setPatientOnlineStatus, user_id.toString(), "1", oneSignalId).toString();
                if (online_status_result.equals("1")) {
                    break;
                }

            } else {
                try {
                    Thread.sleep(1000);
                    continue;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        EventBus.getDefault().post(new MainThread());
    }


    // Called in a separate thread
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void NextPage(MainThread messageEvent) {

        Intent intent = new Intent(WelcomeActivityTablet.this, FragmentMainActivity.class);
        startActivity(intent);
        finish();
       /* FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.appt_contianer, new IOTActivity_MainPage()).commit();*/
    }

    public static String getWIFIMacAddr() {
        //BC:76:5E:74:CB:3B
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
        return "";
    }

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (isHideInput(view, ev)) {
                HideSoftInput(view.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().post(new AsyncThread());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }

    private static final String VERSION_NAME_KEY = "VERSION_NAME_KEY";
    private static final String VERSION_ERROR_LOG = "VERSION_ERROR_LOG";

    void logVersionNumberInDatabase() {
        try {
            String temp = sharedPreferences.getString(VERSION_NAME_KEY, "");
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            if (temp.equals(versionName)) {
                return;
            }

            DateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa", Locale.US);

            //String mac = "a8:34:6a:9d:3b:84";
            String mac = getWIFIMacAddr();
            //Toast.makeText(this, "Mac Address: " + mac, Toast.LENGTH_LONG).show();
            final Disposable disposal = Maybe.fromCallable(() -> {
                String timestamp = df.format(Calendar.getInstance().getTime());
                boolean has_written_error = sharedPreferences.getBoolean(VERSION_ERROR_LOG, false);
                boolean isWifiEnabled = ((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE)).isWifiEnabled();
                String cybermed_code = WebService.getInstance().RxCallingWebservice(get_cybermed_code_from_mac_address, mac).toString();
                if (!cybermed_code.matches(CYBERMED_CODE_REGEX)) {
                    if (!has_written_error) {
                        WebService.getInstance().RxCallingWebservice(developer_debug_log, mac, "Error, cannot find CyberMed_Code", timestamp);
                        editor.putBoolean(VERSION_ERROR_LOG, true).commit();
                    }
                    throw new Exception("Wrong CyberMed_code format");
                }
                return WebService.getInstance().RxCallingWebservice(developer_debug_log, cybermed_code, versionName + " Wifi_On : " + isWifiEnabled + " TableMode : " + getTabletMode(), timestamp);
            }).subscribeOn(Schedulers.io())
                    .retryWhen(errors ->
                            errors.delay(5, TimeUnit.SECONDS)
                    ).subscribe(success -> {
                        if (success.toString().equals("1")) {
                            editor.putBoolean(VERSION_ERROR_LOG, false);
                            editor.putString(VERSION_NAME_KEY, versionName).commit();
                        }
                    });
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initLang() {

        if (!sharedPreferences.getBoolean("firstTime", false)) {
            //First time run
            String currentLanguage = getResources().getConfiguration().locale.getLanguage();
            Configuration config = getBaseContext().getResources().getConfiguration();
            Locale locale = new Locale(currentLanguage);
            Locale.setDefault(locale);
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

            // mark first time has runned.
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("firstTime", true);
            editor.commit();
        } else {
            //not first time run
            Configuration config = getBaseContext().getResources().getConfiguration();
            String lang = sharedPreferences.getString("LANG", "");
            if (!"".equals(lang) && !config.locale.getLanguage().equals(lang)) {
                Locale locale = new Locale(lang);
                Locale.setDefault(locale);
                config.locale = locale;
                getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
            }
        }
    }

    /**
     * get app id for agora video call
     */
    private void callGetAppId() {
        VideoCallManager callManager = new VideoCallManager(new IResponseReceiver() {
            @Override
            public void onSuccess(Object data) {
                CDoctor2Application.application.setAgoraAppId(((ResponseAppId) data).getAppId());
            }

            @Override
            public void onFailure(@NonNull String errorResponse) {
                Log.e("Agora App Id", errorResponse);
            }
        }, this);
        callManager.getAppId();
    }
}

