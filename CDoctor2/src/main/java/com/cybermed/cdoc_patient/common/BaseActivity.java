package com.cybermed.cdoc_patient.common;

import static android.os.Build.VERSION.SDK_INT;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.cdfortis.datainterface.soap.WebService;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.videoui.Constant;
import com.cybermed.cdoc_patient.login.LoginInfo;
import com.cybermed.cdoc_patient.util.ErrorMessage;
import com.cybermed.cdoc_patient.view.CdocProgressBar;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.stemoscope.stemolib.bus.BusManager;
import com.stemoscope.stemolib.event.BlueToothStatusEvent;
import com.stemoscope.stemolib.event.BluetoothRssiEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jsoup.Jsoup;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.cybermed.cdoc_patient.util.AppConstant.RECEIVE_LOGOUT;
import static com.cybermed.cdoc_patient.util.AppConstant.SIGNALR_LOGOUT;


/**
 * Created by qinwe on 2017/5/2.
 */

public class BaseActivity extends AppCompatActivity {

    private CommonAsyncTaskActivity.SetStatusResult setStatusResult;
    private AsyncTask setPatientDeviceStatusTask;
    private AsyncTask mupdateUserDefaultStateTask;
    private AsyncTask setPatientOnlineRoomTask;
    private SetPatientOnlineRoom setPatientOnlineRoom;
    private CountDownTimer cdTimer;

    //等待模式（等待被呼叫，响铃中，通话筒）
    public static final int MODE_WAIT_FOR_CALL = 0;
    public static final int MODE_RINGING = 1;
    public static final int MODE_IN_THE_CALL = 2;
    //在线状态(离线，在线，忙碌)
    public static final int STATUS_OFF_LINE = 0;
    public static final int STATUS_ON_LINE = 1;
    public static final int STATUS_BUSY = 2;
    //运行模式(测试，生产)
    public static final int MODE_TEST = 1;
    public static final int MODE_PRODUCTION = 0;
    //呼叫类型（呼出，呼入）
    public static final int CALL_TYPE_OUT_GOING = 1;
    public static final int CALL_TYPE_IN_COMING = 0;

    //    public static int timerCount = 14400000;
//    public static final int PHARMACY_TIMER = 60000;
    public static int timerCount = 10000;
    public static final int PHARMACY_TIMER = 5000;
    public static final int NORMAL_MODE = 0;
    public static final int PHARMACY_MODE = 1;

    public static final int DOCTOR_FREE = 0;
    public static final int DOCTOR_PAYMENT = 1;
    public static final int DOCTOR_PAYPAL = 3;
    public static final int DOCTOR_INSURANCE = 2;

    public static final int PERMISSION_lOCATION = 1010;
    public static final int PERMISSION_CAMERA_MIC = 1000;
    public static final int PERMISSION_Storage_MIC = 1020;
    public static final int PERMISSION_Storage = 1030;
    public static final int MY_REQUEST_CODE = 9000;

    private String currentVersion;

    private boolean tabletMode;
    public String checkChangeState;

    private int TIMEOUTMODE;
    BroadcastReceiver receiver;
    private CdocProgressBar mLoader;

    public interface SetPatientOnlineRoom {
        void setPatientOnlineRoomResult(int result);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        tabletMode = preferences.getBoolean("tabletmode", false);

        SharedPreferences timeout_preferences = getSharedPreferences("timeout_mode", MODE_PRIVATE);
        TIMEOUTMODE = timeout_preferences.getInt("pharmacy_mode", NORMAL_MODE);
        if (!EventBus.getDefault().hasSubscriberForEvent(BluetoothRssiEvent.class)) {
            EventBus.getDefault().register(this);
        }
        //receiveLogoutStatus();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
            EdgeToEdge.enable((BaseActivity) this);
            ViewCompat.setOnApplyWindowInsetsListener(getWindow().getDecorView(), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                //v.setBackgroundColor(ContextCompat.getColor(BaseActivity.this,R.color.color_00acbb));
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                WindowInsetsControllerCompat controller =
                        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
                controller.setAppearanceLightStatusBars(true); // Use dark icons on light bg
                controller.setAppearanceLightNavigationBars(true);
                return insets;
            });
        }
    }

    public boolean getTabletMode() {
        return tabletMode;
    }

    public CDoctor2Application getCDocApplication() {
        return (CDoctor2Application) getApplication();
    }


    public LoginInfo getLoginInfo2() {
        return getCDocApplication().getLoginInfo();
    }

    public void resetLogoutTimer() {
        if (TIMEOUTMODE == PHARMACY_MODE) {
            if (cdTimer != null) {
                cdTimer.cancel();
            }
            logOutTimer();
        }
    }

    public void stopLogoutTimer() {
        if (TIMEOUTMODE == PHARMACY_MODE) {
            if (cdTimer != null) {
                cdTimer.cancel();
            }
            if (isAppIsInBackground(this)) {
                logOutTimer();
            }
        }
    }


    private void logOutTimer() {
        cdTimer = new CountDownTimer(PHARMACY_TIMER, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                // startService(new Intent(BaseActivity.this, LogoutService.class));
            }
        }.start();
    }

    public void closeSoftware() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
//
//    public static boolean isEmailAddress(String email) {
//        if (email == null || TextUtils.isEmpty(email))
//            return false;
//
//        Pattern p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
//        Matcher m = p.matcher(email);
//        return m.matches();
//    }
//
//    public static boolean isEmailValid(String email) {
//        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
//        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
//        Matcher matcher = pattern.matcher(email);
//        return matcher.matches();
//    }
//
//    public static boolean isPhoneNum(String phone) {
//        if (TextUtils.isEmpty(phone))
//            return false;
//        if (phone.matches("[123456789]{1}\\d{2}-\\d{3}-\\d{4}")) {
//            return true;
//        } else
//            return false;
//    }

    /**
     * 持续时间较短的Toast
     */
    public void toastShortInfo(String value) {
        Toast.makeText(this, value, Toast.LENGTH_SHORT).show();
    }

    /**
     * 持续时间较长的Toast
     */
    public void toastLongInfo(String value) {
        Toast.makeText(this, value, Toast.LENGTH_LONG).show();
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
//        getAppClient().setLanguage(config.locale.getLanguage());
        //保存设置语言的类型
        PreferenceUtil.commitString("language", language);
    }

    public String formatPhoneNum(String phone) {
        if (phone == null || phone.length() < 10 || phone.length() > 11)
            return phone;

        if (phone.length() == 10)
            return phone.substring(0, 3) + "-" + phone.substring(3, 6) + "-" + phone.substring(6);
        else
            return phone.substring(0, 3) + "-" + phone.substring(3, 7) + "-" + phone.substring(7);
    }

    public String formatBirth(String birth) {
        if (TextUtils.isEmpty(birth))
            return "";
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = format.parse(birth);
            SimpleDateFormat format1 = new SimpleDateFormat("MM/dd/yyyy");
            return format1.format(date);
        } catch (Exception e) {
            return "";
        }
    }

    public String getFormatTime(String time) {
        Locale locale = getResources().getConfiguration().locale;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = dateFormat.parse(time);
            SimpleDateFormat format;

            if (locale.equals(Locale.CHINA))
                format = new SimpleDateFormat("yyyy年MM月dd日 ahh:mm");
            else
                format = new SimpleDateFormat("EEE. MMM dd, yyyy hh:mm a", Locale.US);
            String str = format.format(date);
            return str;
        } catch (Exception e) {
            return time;
        }
    }

    public String getFormatTime2(String time) {
        Locale locale = getResources().getConfiguration().locale;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date date = dateFormat.parse(time);
            SimpleDateFormat format;

            if (locale.equals(Locale.CHINA))
                format = new SimpleDateFormat("hh:mm a EEE, MM dd, yyyy");
            else
                format = new SimpleDateFormat("hh:mm a EEE. MMM dd, yyyy", Locale.US);
            String str = format.format(date);
            return str;
        } catch (Exception e) {
            return time;
        }
    }

    public String getFormatTime3(String birth) {
        SimpleDateFormat format1 = null;
        if (TextUtils.isEmpty(birth))
            return "--";
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = format.parse(birth);
            if (getResources().getConfiguration().locale.equals(Locale.CHINA))
                format = new SimpleDateFormat("EEE，yyyy年MM月dd日 ahh:mm");
            else
                format1 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            return format1.format(date);
        } catch (Exception e) {
            return "--";
        }
    }

    public String getFormatTime4(String time) {
        Locale locale = getResources().getConfiguration().locale;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date date = dateFormat.parse(time);
            SimpleDateFormat format;
            String str;

            if (locale.equals(Locale.CHINA)) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                format = new SimpleDateFormat("yyyy年MM月dd日 ahh:mm");
                str = getWeek(calendar.get(Calendar.DAY_OF_WEEK)) + "，" + format.format(date);

            } else {
                format = new SimpleDateFormat("hh:mm a EEE. MMM dd, yyyy", Locale.US);
                str = format.format(date);
            }
            return str;
        } catch (Exception e) {
            return time;
        }
    }

    private String getWeek(int i) {
        String week = null;
        switch (i) {
            case 1:
                week = "星期天";
                break;
            case 2:
                week = "星期一";
                break;
            case 3:
                week = "星期二";
                break;
            case 4:
                week = "星期三";
                break;
            case 5:
                week = "星期四";
                break;
            case 6:
                week = "星期五";
                break;
            case 7:
                week = "星期六";
                break;
        }
        return week;
    }

    // 判定是否需要隐藏
    public boolean isHideInput(View v, MotionEvent ev) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (ev.getX() > left && ev.getX() < right && ev.getY() > top
                    && ev.getY() < bottom) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    // 隐藏软键盘
    public void HideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(token,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    public void showSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
        }
    }

    //Check if app is in background -> for timing out app to auto logout user
    public boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }
        Log.d("timer", String.valueOf(isInBackground));
        return isInBackground;
    }


    public AsyncTask RateDoctorAsyncTask(final String org_code, final String provider_code, final String user_id, final String rating, final String comment) {

        return new AsyncTask<Void, Void, Integer>() {
            Exception e;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Integer doInBackground(Void... params) {

                try {
                    return WebService.getInstance().SetProviderReview(org_code, provider_code, user_id, rating, comment);

                } catch (Exception e) {
                    this.e = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);

            }
        }.execute();

    }

    public void getSetPatientOnlineRoomResult(String userId, int onLineStatus,
                                              String onLineRoom, CommonAsyncTaskActivity.SetPatientOnlineRoom setPatientOnlineRoom) {
        this.setPatientOnlineRoom = setPatientOnlineRoom;
        if (setPatientOnlineRoomTask == null) {
            setPatientOnlineRoomTask = setPatientOnlineRoomAsyncTask(userId, onLineStatus, onLineRoom);
        }
    }

    //设置用户在线房间
    private AsyncTask setPatientOnlineRoomAsyncTask(final String uerId, final int onlineStatus,
                                                    final String onlineRoom) {
        return new AsyncTask<Void, Void, Integer>() {
            Exception e;

            @Override
            protected Integer doInBackground(Void... params) {
                try {
                    return WebService.getInstance().setPatientOnlineRoom(uerId, onlineStatus, onlineRoom);
                } catch (Exception e) {
                    this.e = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                setPatientOnlineRoomTask = null;
                if (e == null) {
                    setPatientOnlineRoom.setPatientOnlineRoomResult(integer);
                } else {
                    toastShortInfo(e.getMessage());
                }
            }
        }.execute();
    }

    public void checkDataConnectionAndVersion(final int type) {

        //Log.d("dataType",networkType);

        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        boolean wifiProblem = false;
        if (!isConnected) {
            wifiProblem = true;
            ErrorMessage.alertDialog(this, getString(R.string.data_connection_unstable_titile),
                    getString(R.string.data_connection_unstable_msg), null);
        }
        if (!wifiProblem) {
            //Check whether app connects to CDoc servers (through helloworld webservice)
            HelloWorld();
        }

        if (type != 2) {
            //Check Current Version With PlayStore Version
            Log.d("versionCheck", "Checking");

            try {
                currentVersion = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                Log.d("versionCheck", currentVersion);
               // new GetVersionCode().execute();
                checkforappupdate();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

private  void  checkforappupdate(){
    AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(this);

// Returns an intent object that you use to check for an update.
    Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

// Checks that the platform will allow the specified type of update.
    appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
        if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                // This example applies an immediate update. To apply a flexible update
                // instead, pass in AppUpdateType.FLEXIBLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
            // Request the update.
            try {
                appUpdateManager.startUpdateFlowForResult(
                        // Pass the intent that is returned by 'getAppUpdateInfo()'.
                        appUpdateInfo,
                        // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                        AppUpdateType.IMMEDIATE,
                        // The current activity making the update request.
                        this,
                        // Include a request code to later monitor this update request.
                        MY_REQUEST_CODE);
            } catch (IntentSender.SendIntentException e) {
                throw new RuntimeException(e);
            }
        }
    });
}
    private AsyncTask HelloWorld() {
        return new AsyncTask<Void, Void, String>() {
            Exception e;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... params) {
                try {
                    return WebService.getInstance().HelloWorld();
                } catch (Exception e) {
                    this.e = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.d("helloworld", s);
                if (!s.equals("Hello World")) {
                    if (BaseActivity.this != null && !BaseActivity.this.isFinishing()) {
                        ErrorMessage.alertDialog(BaseActivity.this, getString(R.string.connection_error_title),
                                getString(R.string.connection_error_msg), null);
                    }
                }

            }
        }.execute();
    }

    private class GetVersionCode extends AsyncTask<Void, String, String> {
        @Override
        protected String doInBackground(Void... voids) {

            String newVersion = null;
            try {
                newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName() + "&hl=it")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select("div[itemprop=softwareVersion]")
                        .first()
                        .ownText();
                return newVersion;
            } catch (Exception e) {
                return newVersion;
            }
        }

        @Override
        protected void onPostExecute(String onlineVersion) {
            super.onPostExecute(onlineVersion);
            if (onlineVersion != null && !onlineVersion.isEmpty()) {
                String[] curr = currentVersion.split("\\.");
                String[] online = onlineVersion.split("\\.");
                int currInt = versionToInt(curr);
                int onlineInt = versionToInt(online);

                if (currInt < onlineInt) {
                    final AlertDialog alertDialog = new AlertDialog.Builder(BaseActivity.this).create();
                    alertDialog.setTitle(getString(R.string.force_update_title));
                    alertDialog.setMessage(getString(R.string.force_update_msg));
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.btn_ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                    try {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                    } catch (android.content.ActivityNotFoundException anfe) {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                    }
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            }
            Log.d("versionCheck1", "Current version " + currentVersion + "playstore version " + onlineVersion);
        }
    }

    private int versionToInt(String[] version) {
        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < version.length; i++) {
            strBuilder.append(version[i]);
        }
        String newString = strBuilder.toString();
        return Integer.valueOf(newString);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("BaseActivity", "onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("BaseActivity", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusManager.getInstance().unregister(this);
        Log.e("BaseActivity", "onDestroy");
    }

    public void shortToast(String content) {
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }

    public void longToast(String content) {
        Toast.makeText(this, content, Toast.LENGTH_LONG).show();
    }


    //udpate
    public void updateUserDefaultState(String userId, String default_State) {
        if (mupdateUserDefaultStateTask == null) {
            mupdateUserDefaultStateTask = updateUserDefaultStateAsyncTask(userId, default_State);
        }
    }

    private AsyncTask updateUserDefaultStateAsyncTask(final String userId, final String default_State) {
        return new AsyncTask<Void, Void, Integer>() {
            Exception e;

            @Override
            protected Integer doInBackground(Void... params) {
                try {
                    return WebService.getInstance().UpdateDefaultState(userId, default_State);
                } catch (Exception e) {
                    this.e = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Integer retValue) {

                mupdateUserDefaultStateTask = null;
                if (e == null) {
                } else {
                    toastShortInfo(e.getMessage());
                }
            }
        }.execute();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveConnectedStatus(BlueToothStatusEvent event) {
        String cStatus = event.getStatus() == 1 ? "Connected" : "Disconnected";
        if (event.getStatus() == 1) {
            Constant.IS_STEMO_CONNECTED = true;
        } else
            Constant.IS_STEMO_CONNECTED = false;
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    private void receiveLogoutStatus() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Intent sendIntent = new Intent(SIGNALR_LOGOUT);
                LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(sendIntent);
            }
        };

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(receiver, new IntentFilter(RECEIVE_LOGOUT));
    }

    public void showProgress() {
        if (mLoader == null) {
            mLoader = new CdocProgressBar(this);
            mLoader.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            // mLoader.setCancelable(false);
        }

        if (mLoader != null && !mLoader.isShowing()) {
            mLoader.show();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideProgress();
            }
        }, 20000);
    }

    public void hideProgress() {
        if (!isFinishing() && mLoader != null && mLoader.isShowing())
            mLoader.dismiss();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                Log.e("Update Failed","Update flow failed! Result code: " + resultCode);

                // If the update is cancelled or fails,
                // you can request to start the update again.
            }
        }
        if (requestCode == PERMISSION_Storage) {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {

                } else {

                }
            }
        }
    }
}

