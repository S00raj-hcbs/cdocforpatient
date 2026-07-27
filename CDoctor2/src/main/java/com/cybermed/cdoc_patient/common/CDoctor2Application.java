package com.cybermed.cdoc_patient.common;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.multidex.MultiDex;

import com.cdfortis.datainterface.soap.UserInfo;
import com.cdfortis.datainterface.soap.WebService;
import com.cdfortis.datainterface.soap.model.IoT_Device;
import com.cdfortis.datainterface.soap.model.SoapObjectVector;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch.swUtil.BleManager;
import com.cybermed.cdoc_patient.login.LoginInfo;
import com.cybermed.cdoc_patient.notification.MyNotificationReceivedHandler;
import com.cybermed.cdoc_patient.payment.CardEntryBackgroundHandler;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.onesignal.Continue;
import com.onesignal.OneSignal;
import com.onesignal.debug.LogLevel;
import com.stemoscope.stemolib.Manager;

import org.ksoap2.serialization.SoapObject;

import java.util.Locale;
import java.util.Vector;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import sqip.CardEntry;

import static com.cdfortis.datainterface.soap.WebServiceID.get_cybermed_code_from_mac_address;
import static com.cdfortis.datainterface.soap.WebServiceID.get_patient_IoT_device_list_V2;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealthScannedDeviceFragment.CYBERMED_CODE_REGEX;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealthScannedDeviceFragment.UNRECOGNIZED;

//import com.cybermed.cdoc.notification.GetUiIntentService;
////import com.cybermed.cdoc.notification.GetUiPushService;


/**
 * Created by Ldj on 2016/4/18.
 */
public class CDoctor2Application extends Application {
    private static final String AP_PATH = "com.cybermed.cdoc_patient";

    private static final String TAG = "CDoctor2Application";
    private DisplayMetrics displayMetrics;

    private static SharedPreferences preferences;

    private static LoginInfo loginInfo;
    private boolean isForeground = false;
    private boolean isLogin = false;
    private String videoCallRoomNumber;
    public static CDoctor2Application application;
    private static final String ONESIGNAL_APP_ID = "f614514d-b080-460d-a5e0-47ce4fb21497";
    private static final int SLEEP_TIME_TO_MIMIC_ASYNC_OPERATION = 2000;


    public static void setTabletMode(boolean tabletMode) {
        CDoctor2Application.tabletMode = tabletMode;
    }

    private static boolean tabletMode;

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);
        MultiDex.install(this);
        BleManager.init(this);
        if (getCurProcessName().contains("remote")) {
            return;
        }
        application = this;
        //RestApiCall.init();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        //WebService.getInstance().switchToQaSite();
        //WebService.getInstance().switchToLocal();
        String IMEI = getDeviceIMEI();

        try {
            Class.forName("android.os.AsyncTask");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        loginInfo = new LoginInfo(this);
        preferences = getSharedPreferences(AP_PATH, MODE_PRIVATE);

        RxJavaPlugins.setErrorHandler(t -> {
        });

        initialize();


        SharedPreferences preferences2 = PreferenceManager.getDefaultSharedPreferences(this);
        tabletMode = preferences2.getBoolean("tabletmode", false);
        OneSignal.getDebug().setLogLevel(LogLevel.VERBOSE);
        OneSignal.initWithContext(this,ONESIGNAL_APP_ID);
        initOneSignal();

        CardEntryBackgroundHandler cardHandler =
                new CardEntryBackgroundHandler();
        CardEntry.setCardNonceBackgroundHandler(cardHandler);

 /*       OneSignal.getNotifications().addForegroundLifecycleListener(new INotificationLifecycleListener() {
            @Override
            public void onWillDisplay(@NonNull INotificationWillDisplayEvent event) {
                Log.v("datas", "INotificationLifecycleListener.onWillDisplay fired" +
                        " with event: " + event);

                IDisplayableNotification notification = event.getNotification();
                JSONObject data = notification.getAdditionalData();
                Log.e("data",""+data);
                Log.e("data",""+notification.getBody());
                MyNotificationReceivedHandler notificationReceivedHandler = new MyNotificationReceivedHandler(application);
                Log.d("OneSignalDebug", "received notification");
                Log.d("TIMEANALYSIS", "4. Recieved Onesignal " + Calendar.getInstance().getTime().toString());
                Log.d("TIMEANALYSIS", "4. Recieved Onesignal " + notification.getRawPayload());
                Log.d("TIMEANALYSIS", "4. Recieved Onesignal " + notification);
                //old code
                //JSONObject data = notification.payload.additionalData;
                //JSONObject data = notification.getAdditionalData();
                // OneSignal.clearOneSignalNotifications();
                OneSignal.getNotifications().clearAllNotifications();
                try {
                    // old code
          *//*  if (notification.payload.body.contains("HeartBeat Check")) {
                setDeviceStatus();
            }*//*
                    if (Objects.requireNonNull(notification.getBody()).contains("HeartBeat Check")) {
                        notificationReceivedHandler.setDeviceStatus();
                    } *//*else if (notification.payload.body.contains("Payment")) {
                try {
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CDOC_CHANNEL_ID)
                            .setSmallIcon(R.drawable.cdoc_icon)
                            .setContentTitle("Charge Notification")
                            .setContentText(notification.payload.body)
                            .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                            .setPriority(NotificationCompat.PRIORITY_MAX);

                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel mChannel = new NotificationChannel(CDOC_CHANNEL_ID, CDOC_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);

                        notificationManager.createNotificationChannel(mChannel);
                    }

                    notificationManager.notify(PAYMENT_NOTIFICATION, mBuilder.build());
                } catch (Exception e) {
                }
            }*//*else if (notification.getBody().contains("Payment")) {
                        try {
                            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(application, CDOC_CHANNEL_ID)
                                    .setSmallIcon(R.drawable.cdoc_icon)
                                    .setContentTitle("Charge Notification")
                                    .setContentText(notification.getBody())
                                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                                    .setPriority(NotificationCompat.PRIORITY_MAX);

                            NotificationManager notificationManager = (NotificationManager) application.getSystemService(NOTIFICATION_SERVICE);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                NotificationChannel mChannel = new NotificationChannel(CDOC_CHANNEL_ID, CDOC_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);

                                notificationManager.createNotificationChannel(mChannel);
                            }

                            notificationManager.notify(notificationReceivedHandler.PAYMENT_NOTIFICATION, mBuilder.build());
                        } catch (Exception e) {
                        }
                    }
//            else if (data != null && data.getString("subject") != null &&
//                    data.getString("subject").equals("Logout_All")) {
//                Intent intent = new Intent(SIGNALR_LOGOUT);
//                //LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
//            }
                    else {
                        if (data != null) {
                            //Reboot push
                            try {
                                //Reboot Message
                                if (data.has("admin_action")){
                                    String admin_action = data.getString("admin_action");
                                    if (admin_action != null && admin_action.equals("reboot_device")) {
                                        notificationReceivedHandler.restartApp();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            //Normal push
                            try {
                                notificationReceivedHandler.roomnumber = data.getString("roomnumber");
                                notificationReceivedHandler.onlinestatus = data.getString("onlinestatus");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (notificationReceivedHandler.onlinestatus != null && notificationReceivedHandler.onlinestatus.equals("1")) {
                                // message = notification.payload.body;
                                notificationReceivedHandler.message = notification.getBody();
                                String[] messageString = notificationReceivedHandler.message.split(" ");
                                notificationReceivedHandler.callerType = messageString[0].toLowerCase().trim();
                                notificationReceivedHandler.callerName = messageString[1] + " " + messageString[2];

                                notificationReceivedHandler.startActivity();
                            } else {
                                if (VideoCallActivity.getInstance() != null) {
                                    VideoCallActivity.getInstance().clearNotification();
                                    VideoCallActivity.getInstance().cancelIncomingCallTimer();
                                    VideoCallActivity.getInstance().finish();
                                }
                            }
                        }
                    }
                } catch (Exception jsonException) {

                }

            }
        });*/

       /* OneSignal.getNotifications().addClickListener(new INotificationClickListener() {
            @Override
            public void onClick(@NonNull INotificationClickEvent iNotificationClickEvent) {
                INotification notification = iNotificationClickEvent.getNotification();
                JSONObject data = notification.getAdditionalData();
                if (!application.isLogin()){
                    return;
                }
                MyNotificationReceivedHandler notificationReceivedHandler = new MyNotificationReceivedHandler(application);
                OneSignal.getNotifications().clearAllNotifications();

                try {
                    if (Objects.requireNonNull(notification.getBody()).contains("HeartBeat Check")) {
                        notificationReceivedHandler.setDeviceStatus();
                    } else if (notification.getBody().contains("Payment")) {
                        try {
                            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(application, CDOC_CHANNEL_ID)
                                    .setSmallIcon(R.drawable.cdoc_icon)
                                    .setContentTitle("Charge Notification")
                                    .setContentText(notification.getBody())
                                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                                    .setPriority(NotificationCompat.PRIORITY_MAX);

                            NotificationManager notificationManager = (NotificationManager) application.getSystemService(NOTIFICATION_SERVICE);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                NotificationChannel mChannel = new NotificationChannel(CDOC_CHANNEL_ID, CDOC_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);

                                notificationManager.createNotificationChannel(mChannel);
                            }

                            notificationManager.notify(notificationReceivedHandler.PAYMENT_NOTIFICATION, mBuilder.build());
                        } catch (Exception e) {
                        }
                    }
                    else {
                        if (data != null) {
                            //Reboot push
                            try {
                                //Reboot Message
                                if (data.has("admin_action")){
                                    String admin_action = data.getString("admin_action");
                                    if (admin_action != null && admin_action.equals("reboot_device")) {
                                        notificationReceivedHandler.restartApp();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            //Normal push
                            try {
                                notificationReceivedHandler.roomnumber = data.getString("roomnumber");
                                notificationReceivedHandler.onlinestatus = data.getString("onlinestatus");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (notificationReceivedHandler.onlinestatus != null && notificationReceivedHandler.onlinestatus.equals("1")) {
                                // message = notification.payload.body;
                                notificationReceivedHandler.message = notification.getBody();
                                String[] messageString = notificationReceivedHandler.message.split(" ");
                                notificationReceivedHandler.callerType = messageString[0].toLowerCase().trim();
                                notificationReceivedHandler.callerName = messageString[1] + " " + messageString[2];

                                notificationReceivedHandler.startActivity();
                            } else {
                                if (VideoCallActivity.getInstance() != null) {
                                    VideoCallActivity.getInstance().clearNotification();
                                    VideoCallActivity.getInstance().cancelIncomingCallTimer();
                                    VideoCallActivity.getInstance().finish();
                                }
                            }
                        }
                    }
                } catch (Exception jsonException) {

                }
            }
        });*/


       /* OneSignal.getNotifications().addClickListener(new INotificationClickListener() {
            @Override
            public void onClick(@NonNull INotificationClickEvent iNotificationClickEvent) {
                INotification notification = iNotificationClickEvent.getNotification();
                JSONObject data = notification.getAdditionalData();
                if (!application.isLogin()){
                    return;
                }
                OneSignal.getNotifications().clearAllNotifications();
           //     Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);
                if (data != null) {
                    try {
                       String docName = data.getString("docName");
                       String orgCode = data.getString("orgCode");
                       String   providerId = data.getString("providerId");
                        Intent intent = new Intent(application,VideoCallActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                        intent.putExtra("type",0);
                        intent.putExtra("docName",docName);
                        intent.putExtra("orgCode",orgCode);
                        intent.putExtra("providerId",providerId);
                        application.startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });*/


    }

    public static boolean getTabletMode() {
        return tabletMode;
    }

    private void initOneSignal() {
        Log.d("Onesignal", "initonesignal");
        // OLD CODE
//        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.DEBUG, OneSignal.LOG_LEVEL.DEBUG);
        /*OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .disableGmsMissingPrompt(true)
                //.setNotificationOpenedHandler(new MyNotificationOpenHandler(this))
                .setNotificationReceivedHandler(new MyNotificationReceivedHandler(this))
                .init();*/
        //new MyNotificationReceivedHandler(this);
        new MyNotificationReceivedHandler(this);


        // OneSignal Initialization

        OneSignal.setDisableGMSMissingPrompt(true);
        // requestPermission will show the native Android notification permission prompt.
        // NOTE: It's recommended to use a OneSignal In-App Message to prompt instead.
       // OneSignal.getNotifications().requestPermission(true, Continue.none());
        // String UUID = OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId()
       //getOneSignalResult();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        @SuppressLint({"NewApi", "LocalSuppress"}) CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            OneSignal.getNotifications().requestPermission(true, Continue.none());
        }, executor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            future.join(); // Waits for the task to complete
        }
        executor.shutdown();
        getOneSignalResult();
    }

    private void getOneSignalResult() {
        // OLD CODE
       /* OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                Log.e("onesignal", "userid:" + userId + ";registrationId:" + registrationId);
                if (userId != null) {
                    loginInfo.setOneSignalUserId(userId);
                    loginInfo.save();
                }
            }
        });*/

      /*  String userID = OneSignal.getUser().getPushSubscription().getId();
       // Log.e("userid",userID);
        //   Log.e("subid",OneSignal.getUser().getOnesignalId());
        //  Log.e("exterid",OneSignal.getUser().getExternalId());
        loginInfo.setOneSignalUserId(userID);
        loginInfo.save();*/
        /*new Handler(Looper.getMainLooper()).postDelayed(() -> {
            String userID = OneSignal.getUser().getPushSubscription().getId();
            if (userID != null) {
                Log.d("OneSignal", "Push Token (Retry): " + userID);
                loginInfo.setOneSignalUserId(userID);
                loginInfo.save();
            } else {
                Log.e("OneSignal", "Token still not available!");
            }
        }, 3000);*/
        String userID = OneSignal.getUser().getPushSubscription().getId();
        if(!userID.isEmpty()){
            loginInfo.setOneSignalUserId(userID);
            loginInfo.save();
        }else {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                String pushToken = OneSignal.getUser().getPushSubscription().getId();
                if (!pushToken.isEmpty()) {
                    Log.d("OneSignal", "Push Token (Retry): " + pushToken);
                    loginInfo.setOneSignalUserId(pushToken);
                    loginInfo.save();
                } else {
                    getOneSignalResult();
                }
            }, 3000);
        }


    }

    //系统启动就设置语言
    protected void initLanguage() {
        PreferenceUtil.init(this);
        String lastLanguage = PreferenceUtil.getString("language", "default");
        switchLanguage(lastLanguage);

    }

    public static LoginInfo getLoginInfo() {
        return loginInfo;
    }

    public void shutDown() {
        Intent intent = new Intent(this, PushActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * 修改语言本地化
     */
    public void switchLanguage(String language) {
        //设置应用语言类型
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        if (language.equals("en")) {
            config.locale = Locale.US;
        } else if (language.equals("zh")) {
            config.locale = Locale.CHINA;
        } else if (language.equals("es"))
            config.locale = new Locale("es", "ES");
        else if (language.equals("sys")) {
            config.locale = Locale.getDefault();
        }
        resources.updateConfiguration(config, dm);
        //保存设置语言的类型
    }

    private void initialize() {
        if (TextUtils.isEmpty(loginInfo.getIMEIID())) {
            //loginInfo.setDeviceId(DeviceIdUtil.getDeviceId(this));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                        != PackageManager.PERMISSION_GRANTED) {

                } else {
                    loginInfo.setIMEIID(getDeviceIMEI());
                }
            } else {
                loginInfo.setIMEIID(getDeviceIMEI());
            }
            loginInfo.save();

        }

        displayMetrics = this.getApplicationContext().getResources().getDisplayMetrics();

        //register stemoscope manager
        Manager.newInstance().stemo_initialize(this);

        initLanguage();
    }


    public String getDeviceIMEI() {
        String deviceUniqueIdentifier = null;
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if (null != tm) {
            try {
                deviceUniqueIdentifier = tm.getDeviceId();
            } catch (SecurityException se) {
                return "";
            }
        }
        if (null == deviceUniqueIdentifier || 0 == deviceUniqueIdentifier.length()) {
            deviceUniqueIdentifier = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return deviceUniqueIdentifier;
    }

    public void setDeviceImei() {
        loginInfo.setIMEIID(getDeviceIMEI());
        loginInfo.save();
        Log.d("IMEIDEBUG", "IMEI2: " + loginInfo.getIMEIID());
    }

    public void processUserLogin2(String account, String pwd, UserInfo userInfo) {
        Log.e("account",""+account);
        Log.e("pwd",""+pwd);
        Log.e("userInfo",""+userInfo);
        loginInfo.setAccount(account);
        loginInfo.setPwd(pwd);
        loginInfo.setUserInfo(userInfo); // update userinfo
        loginInfo.save();
    }

    public void setAuthRep(String originalAccount, boolean isAuthRep) {
        loginInfo.setAuthRep(isAuthRep);
        loginInfo.setOriginalAccount(originalAccount);
        loginInfo.saveAuthRep();
    }

    public void processSetPwd(String pwd) {
        loginInfo.setPwd(pwd);
    }


    private String getCurProcessName() {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (mActivityManager != null) {
            for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                    .getRunningAppProcesses()) {
                if (appProcess.pid == pid) {

                    return appProcess.processName;
                }
            }
        }
        return null;
    }


    public boolean isLogin() {
        return isLogin;
    }


    public static boolean isLoggedIn() {
        return preferences.getBoolean("isloggedin", false);
    }


    public void setLogin(boolean login) {
        isLogin = login;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isloggedin", login);
        editor.commit();
    }

    private static final CompositeDisposable disposables = new CompositeDisposable();


    public static void getAndSetDeviceVector() {
        if (disposables.size() != 0)
            disposables.clear();

        disposables.add(Single.just(CDoctor2Application.getLoginInfo().getAccount())
                .subscribeOn(Schedulers.io())
                .map(account -> WebService.getInstance().RxCallingWebservice(get_patient_IoT_device_list_V2, account))
                .filter(result -> result instanceof SoapObject)
                .flattenAsObservable(result -> new SoapObjectVector<>(IoT_Device.class, (SoapObject) result))
                .flatMap(iot_device -> Observable.just(iot_device)
                        .subscribeOn(Schedulers.io())
                        .map(device -> {
                            String cybermed_code = WebService.getInstance().RxCallingWebservice(get_cybermed_code_from_mac_address, device.device_macAddress).toString();
                            if (!cybermed_code.matches(CYBERMED_CODE_REGEX))
                                cybermed_code = UNRECOGNIZED;
                            device.setCybermed_code(cybermed_code);
                            return device;
                        }))
                .collectInto(new Vector<IoT_Device>(), Vector::add)
                .subscribe(
                        vector -> CDoctor2Application.getLoginInfo().getUserInfo().setIoT_devices_obs(vector),
                        error -> {
                            CDoctor2Application.getLoginInfo().getUserInfo().setIoT_devices_obs(null);
                        })
        );
    }

    public void setAgoraAppId(String appId) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("AgoraAppId", appId);
        editor.commit();
    }

    public String getAgoraAppId() {
        return preferences.getString("AgoraAppId", null);
    }


}
