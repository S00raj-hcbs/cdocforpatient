package com.cybermed.cdoc_patient.login;

import static android.os.Build.VERSION.SDK_INT;
import static com.cdfortis.datainterface.soap.WebServiceID.developer_debug_log;
import static com.cybermed.cdoc_patient.util.AppConstant.isFirstLaunch;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.cdfortis.datainterface.soap.WebService;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.IOTActivity_MainPage;
import com.cybermed.cdoc_patient.common.CDoctor2Application;
import com.cybermed.cdoc_patient.common.CommonAsyncTaskActivity;
import com.cybermed.cdoc_patient.common.PreferenceUtil;
import com.cybermed.cdoc_patient.doctor.VideoCallManager;
import com.cybermed.cdoc_patient.login.onBoarding.OnBoardingActivity;
import com.cybermed.cdoc_patient.main.FragmentMainActivity;
import com.cybermed.cdoc_patient.util.ErrorMessage;
//import com.cybermed.cdoc.util.VersionCheck;
import com.cybermed.cdoc_patient.webapi.IResponseReceiver;

import java.util.concurrent.TimeUnit;

public class WelcomeActivity extends CommonAsyncTaskActivity {
    private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 999;
    private String defaultState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //WebService.getInstance().switchToQaSite();
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
//        if (RootDetection.checkRootedDevice()) {
//            Toast.makeText(this, getString(R.string.device_rooted), Toast.LENGTH_SHORT).show();
//            finish();
//        }

        setContentView(R.layout.activity_splash_screen);
        ImageView imageView = findViewById(R.id.logo);
        Glide.with(this)
                .asGif()
                .listener(new RequestListener<GifDrawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (resource instanceof GifDrawable) {
                            ((GifDrawable) resource).setLoopCount(1);
                        }
                        return false;
                    }
                })
                .load(R.raw.splash_gif1)
                .into(imageView);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        defaultState = getLoginInfo2().getUserInfo().getDefault_state();
        if (defaultState != null && defaultState.equals("All")) {
            defaultState = "";
        }
        callGetAppId();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable e) {
                String[] errorArr = ErrorMessage.produceUnhandleExceptionError(e, getApplicationContext());

                int endIndex = errorArr[1].length();
                if (endIndex > 230)
                    endIndex = 230;

                WebService.webServiceAsyncTask(developer_debug_log, errorArr[0], errorArr[1].substring(0, endIndex), errorArr[2]);
                try {
                    developer_debug_log.getAsyncTask().get(20, TimeUnit.SECONDS);
                } catch (Exception ex) {
                    new Object();
                }

                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory(Intent.CATEGORY_HOME);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
                System.exit(1);
            }
        });

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("tabletmode", false);
        editor.commit();
        checkDataConnectionAndVersion(1);
        //new VersionCheck(this, this::loadData).check();
        loadData();
    }

    private void loadData() {

        if (SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.CALL_PHONE,
                                Manifest.permission.POST_NOTIFICATIONS},
                        PERMISSIONS_REQUEST_READ_PHONE_STATE);
                return;
            }
        }else {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.CALL_PHONE},
                        PERMISSIONS_REQUEST_READ_PHONE_STATE);
                return;
            }
        }

        transitionToLogin();
    }

    private void transitionToLogin() {

        new Handler(Looper.myLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean IOTMode = getLoginInfo2().getUserInfo().getMobile_mode().equalsIgnoreCase("remote_monitoring");

                Intent intent = null;

                if (CDoctor2Application.isLoggedIn() && IOTMode) {
                    intent = new Intent(WelcomeActivity.this, IOTActivity_MainPage.class);
                } else if (CDoctor2Application.isLoggedIn()) {
                    intent = new Intent(WelcomeActivity.this, FragmentMainActivity.class);
                } else {
                    if (!PreferenceUtil.getBoolean(isFirstLaunch, false)) {
                        intent = new Intent(WelcomeActivity.this, OnBoardingActivity.class);
                    } else {
                        intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                    }
                }
                startActivity(intent);
                finish();


            }
        }, 2500);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {

        if (grantResults.length == 0)
            return;

        if (requestCode == PERMISSIONS_REQUEST_READ_PHONE_STATE
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCDocApplication().setDeviceImei();
            transitionToLogin();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

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



