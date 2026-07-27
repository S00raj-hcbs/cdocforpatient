package com.cybermed.cdoc_patient.main;

import static com.cybermed.cdoc_patient.common.BaseFragment.FUTUREAPPT;
import static com.cybermed.cdoc_patient.common.videoui.Constant.ishomesnot;
import static com.cybermed.cdoc_patient.common.videoui.ConstantApp.KEY_ENABLE;
import static com.cybermed.cdoc_patient.doctor.docDetail.PaymentFrag.DEFAULT_CARD_ENTRY_REQUEST_CODE;
import static com.cybermed.cdoc_patient.doctor.searchDoctor.CalendarHelper.CALENDARHELPER_PERMISSION_REQUEST_CODE;
import static com.cybermed.cdoc_patient.util.AppConstant.REQUEST_IMAGE_SELECTION;
import static com.cybermed.cdoc_patient.util.PermissionUtil.notificationCheck;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.cdfortis.datainterface.soap.OnPostExecute;
import com.cdfortis.datainterface.soap.UserInfo;
import com.cdfortis.datainterface.soap.model.Patient_Demographic;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.appointment.AppointmentFragment;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.IOTActivity_MainPage;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch.swUtil.BleManager;
import com.cybermed.cdoc_patient.common.CDoctor2Application;
import com.cybermed.cdoc_patient.common.CommonAsyncTaskActivity;
import com.cybermed.cdoc_patient.common.StateAbbr;
import com.cybermed.cdoc_patient.common.videoui.Constant;
import com.cybermed.cdoc_patient.databinding.ActivityFragmainBinding;
import com.cybermed.cdoc_patient.doctor.DoctorListFragment;
import com.cybermed.cdoc_patient.doctor.VideoCallActivity;
import com.cybermed.cdoc_patient.doctor.docDetail.ConfirmAppointmentFragment;
import com.cybermed.cdoc_patient.doctor.docDetail.DoctorBaseFrag;
import com.cybermed.cdoc_patient.doctor.docDetail.PaymentFrag;
import com.cybermed.cdoc_patient.login.LoginActivity;
import com.cybermed.cdoc_patient.login.viewmodel.BaseResponse;
import com.cybermed.cdoc_patient.me.MeFragment;
import com.cybermed.cdoc_patient.me.UserFragment;
import com.cybermed.cdoc_patient.me.vitalcheck.VitalMonitorFragment;
import com.cybermed.cdoc_patient.payment.PaymentCreditCardFrag;
import com.cybermed.cdoc_patient.signalr.SignalRService;
import com.cybermed.cdoc_patient.util.PermissionUtil;
import com.cybermed.cdoc_patient.view.MedicalDisclaimerDialog;
import com.cybermed.cdoc_patient.view.MyAlertDialog;
import com.cybermed.cdoc_patient.webapi.IResponseReceiver;
import com.cybermed.cdoc_patient.webapi.manager.HomeApiManager;
import com.cybermed.cdoc_patient.webapi.model.response.TriageConfigResponse;
import com.cybermed.cdoc_patient.ws.WS;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ihealth.communication.manager.iHealthDevicesManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

//import com.cybermed.cdoc.notification.GetUiPushService;

public class FragmentMainActivity extends CommonAsyncTaskActivity implements LocationListener,
        BottomNavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private Fragment curFragment;
    public HomeFragment homefragment;
    private MeFragment meFragment;
    private FragmentManager fragmentManager;
    private EditText mEditPwd;
    private String user_id;
    private AlertDialog alertDialog;
    private boolean tabletMode;
    public static Boolean isVisible = false;
    boolean rejoinCallFromAudioIssue = false;
    Intent signalrIntent;
    boolean isLogoutInProcess;
    MainActVm viewModel;
    String tabletVerificationPassword;
    public ActivityFragmainBinding binding;

    //location update variables
    public static final int MY_CAMERA_AUDIO_REQUEST_CODE = 100;
    private String defaultState;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    UserFragment userFragment;
    String check="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_fragmain);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_fragmain);
        viewModel = new ViewModelProvider(this).get(MainActVm.class);
        checkDataConnectionAndVersion(0);
        registerObserver();
        init();
        SharedPreferences prefs =
                getSharedPreferences("app_prefs1", MODE_PRIVATE);

        boolean isDisclaimerShown =
                prefs.getBoolean("disclaimer_shown", false);
        boolean hardwareShown =
                prefs.getBoolean("hardware_disclaimer_shown", false);

        if (!isDisclaimerShown) {
            String title="Medical Disclaimer";
            /*String description= "The app does not provide medical diagnosis, treatment, or emergency medical services.\n" +
                    "\n" +
                    "Users are reminded to consult a licensed healthcare professional for medical advice, diagnosis, or treatment.";*/
            String description= "This application is intended for health monitoring and informational purposes only.\n" +
                    "\n" +
                    "The app does not provide medical diagnosis, treatment, emergency medical services, or medical recommendations.\n" +
                    "\n" +
                    "Health information displayed in the app is collected from compatible connected devices and should not be relied upon as a substitute for professional medical advice, diagnosis, or treatment.\n" +
                    "\n" +
                    "Always consult a licensed healthcare professional regarding any medical condition or healthcare decision.";
            /*\n" +
                    "\n" +
                    "Health and vital information displayed in CDoc may be provided by external devices that users voluntarily connect. CDoc does not automatically collect data from such devices.";
*/

            MedicalDisclaimerDialog.show(this,title,description,"I Understand",false,()->{
                prefs.edit()
                        .putBoolean("disclaimer_shown", true)
                        .apply();

                // AFTER medical → show hardware
                if (!hardwareShown) {

                    MedicalDisclaimerDialog.show(
                            this,
                            "Device Information",
                            "CDOC Patient requires compatible external medical, wellness, or wearable devices for certain features and health measurements, including blood pressure monitors, glucometers, pulse oximeters, weight scales, and supported wearable devices.\n" +
                                    "\n" +
                                    "Health data displayed in the application is obtained from connected compatible devices. These device-dependent features do not function independently without the appropriate external hardware.\n" +
                                    "\n" +
                                    "Please ensure that a supported device is properly connected before using these features.",
                            /*.\n" +
                                    "\n" +
                                    "CDoc does not automatically collect health data from external devices. Health data is imported only after you choose to connect a compatible device and grant permission.",*/
                            "Continue",
                            false,
                            () -> prefs.edit()
                                    .putBoolean("hardware_disclaimer_shown", true)
                                    .apply(),
                            ()->{}
                    );
                }
            },()->{

            });

        }

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Do nothing to disable back button
                Log.e("Constant.ishomefragment",""+Constant.ishomefragment);
                if (Constant.ishomefragment.equals("MainFragment")){
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }else {
                    if (homefragment.curFragment instanceof AppointmentFragment || homefragment.curFragment instanceof MeFragment) {
                        binding.bottomBar.setSelectedItemId(R.id.home);
                    } else if (homefragment.curFragment instanceof DoctorListFragment || homefragment.curFragment instanceof DoctorBaseFrag) {
                        HomeFragment.getInstance().openMainActivity();
                        binding.bottomBar.setSelectedItemId(R.id.home);
                    } else if (homefragment.curFragment instanceof IOTActivity_MainPage) {
                        ((IOTActivity_MainPage) homefragment.curFragment).backPress();
                    }else if (homefragment.curFragment instanceof VitalMonitorFragment) {
                        HomeFragment.getInstance().openMainActivity();
                    } else if (curFragment.getChildFragmentManager().getFragments().size() > 1) {
                        binding.bottomBar.setSelectedItemId(R.id.home);
                    } /*else if (homefragment.curFragment instanceof MainFragment) {
                    FragmentMainActivity.this.finish();
                }*/else {


                    }
                }

            }
        };

        // Add the callback to the activity's OnBackPressedDispatcher
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    /**
     * initialize fragments and call imp service
     */
    void init() {
        getTriageConfig();
        binding.bottomBar.setOnNavigationItemSelectedListener(this);
        meFragment = new MeFragment();
        homefragment = new HomeFragment();
        userFragment = new UserFragment();
        fragmentManager = getSupportFragmentManager();
        Bundle data = getIntent().getExtras();
        if (data != null) {
            rejoinCallFromAudioIssue = data.getBoolean("callRejoin");
        }
        Bundle bundle = new Bundle();
        homefragment.setArguments(bundle);
        replaceFragment(homefragment);
        initTabletMode();
        WS.setPatientDeviceStatus(STATUS_ON_LINE);
        startService();
    }

    /**
     * listen observer for api calls
     */
    private void registerObserver() {
        viewModel.getApiResponse().observe(this, liveAction -> {
            switch (liveAction.getLiveActionEvent()) {
                case LOGIN_INFO:
                    BaseResponse response = (BaseResponse) liveAction.getLiveActionValue();
                    if (response.getIntegerVal() == 1) {
                        getCDocApplication().setLogin(true);
                        Log.e("isLogin", "isLogin == true");
                        getUserInfo(user_id);
                    } else if (response.getIntegerVal() == -1) {
                        toastShortInfo(getString(R.string.login_failed));
                    }
                    break;
                case GET_USER_INFO:
                    Patient_Demographic patientInfo = (Patient_Demographic) liveAction.getLiveActionValue();
                    if (patientInfo != null) {
                        UserInfo userInfo = new UserInfo();
                        userInfo.deserialize(patientInfo);
                        getCDocApplication().processUserLogin2(user_id, tabletVerificationPassword, userInfo);
                        if (homefragment != null)
                            homefragment.meFragment();
                        alertDialog.dismiss();
                    }
                    break;
                case ACTIVE_GUEST_COUNT:
                    BaseResponse respons = (BaseResponse) liveAction.getLiveActionValue();
                    hideProgress();
                    if (respons.getIntegerVal() > 0) {
                        if (rejoinCallFromAudioIssue) {
                            Intent intent = new Intent(FragmentMainActivity.this, VideoCallActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.putExtra("type", 2);
                            intent.putExtra("docName", "");
                            Bundle data = getIntent().getExtras();
                            if (data != null) {
                                intent.putExtra("orgCode", data.getString("orgCode"));
                                intent.putExtra("providerId", data.getString("providerId"));
                            }
                            startActivity(intent);
                        } else dialogRejoin();

                    } else {
                        removeDroppedCallInfo();
                    }
                    break;

            }
        });
    }

    void getTriageConfig(){
        new HomeApiManager(new IResponseReceiver<TriageConfigResponse>() {
            @Override
            public void onSuccess(TriageConfigResponse data) {
                if(data.getTriageConfiguration()!=null && data.getTriageConfiguration().equals("1")){
                    CDoctor2Application.getLoginInfo().setTriageConfig("true");
                }else
                  CDoctor2Application.getLoginInfo().setTriageConfig("false");
            }

            @Override
            public void onFailure(@NonNull String errorResponse) {

            }
        }, this).getTriageConfig(CDoctor2Application.getLoginInfo().getUserInfo().getService_code());
    }
    //****************************************Interact With child fragments ******************************************************


    public void toDoctorList() {
        ishomesnot="appointment";
        homefragment.openDoctorList(false, false);
    }


    private void replaceFragment(Fragment fragment) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        if (fragment != curFragment) {
            if (curFragment != null) {
                ft.hide(curFragment);
            }
            curFragment = fragment;
            if (!fragment.isAdded()) {
                Log.d("fragmentTest", "not added");
                if (!tabletMode) {
                    ft.add(R.id.content, fragment);
                } else {
                    //TabletMode has a bug where onresume is called multiple times
                    //replace (rather than add) fixes this issue.
                    ft.replace(R.id.content, fragment);
                }
            } else {
                ft.show(fragment);
                if (fragment instanceof innerFragsLifecycleSimulator) {
                    ((innerFragsLifecycleSimulator) fragment).onMyResume();
                }
            }
            ft.commitAllowingStateLoss();
        }
    }

    public void reloadDoctorList() {
        if (HomeFragment.getInstance().getDoctorListFragment().getView() != null)
            HomeFragment.getInstance().getDoctorListFragment().reloadWithNewState();
    }

    public void openMeFrag() {
        if (tabletMode) {
            if (getLoginInfo2().getPwd().equals("")) {
                accessMeTablet();
            } else {
                homefragment.meFragment();
                // meFragment();
            }
        } else {
            homefragment.meFragment();
            //meFragment();
        }
    }

    @Override
    public void onClick(View v) {
        if (homefragment.curFragment instanceof MainFragment) {
            ((MainFragment) homefragment.curFragment).onClick(v);
        }
    }


    interface innerFragsLifecycleSimulator {
        void onMyResume();
    }

    /**
     * start signalr service
     */
    private void startService() {
        signalrIntent = new Intent(this, SignalRService.class);
        startService(signalrIntent);
    }


    //************************************** Rejoin Dropped Call **********************************************
    private void dialogRejoin() {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    Intent intent = new Intent(FragmentMainActivity.this, VideoCallActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("type", 2);
                    intent.putExtra("docName", "");
                    startActivity(intent);
                    //Yes button clicked
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    removeDroppedCallInfo();
                    //No button clicked
                    break;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(FragmentMainActivity.this);
        builder.setMessage(getString(R.string.home_rejoin_call)).setPositiveButton(getString(R.string.btn_join), dialogClickListener)
                .setNegativeButton(getString(R.string.btn_no), dialogClickListener).show();
    }

    private void removeDroppedCallInfo() {
        setPatientOnlineRoom(getLoginInfo2().getAccount(), 1);
        SharedPreferences preferences = getSharedPreferences("VIDEOSHAREPREF", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("ROOM_NUMBER", "");
        editor.putString("ROOM_GUEST_ID", "");
        editor.apply();
    }


    private void setPatientOnlineRoom(String userId, int status) {
        getSetPatientOnlineRoomResult(userId, status, "", result -> {
        });
    }

    //*****************************************logout procedure**********************************************************************
    // receive logout status from signalr

    public void showLogOutDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_logout);
        Button btn_ok = dialog.findViewById(R.id.btn_ok);
        Button btn_Cancel = dialog.findViewById(R.id.btn_cancel);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showProgress();
                signalrIntent.putExtra("status", STATUS_OFF_LINE);
                startService(signalrIntent);
                logOut();
            }
        });
        btn_Cancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    public void logOut() {
        OnPostExecute ope = result -> {
            hideProgress();
            Log.e("fragMainAct", "ondestro");
            PreferenceManager.getDefaultSharedPreferences(
                    this).edit().putBoolean(KEY_ENABLE, false).commit();
            isLogoutInProcess = false;
            getCDocApplication().setLogin(false);
            if (BleManager.getInstance().isConnected()) BleManager.getInstance().disconnectDevice();
            finish();
            Intent mStartActivity = new Intent(FragmentMainActivity.this, LoginActivity.class);
            startActivity(mStartActivity);
        };

        WS.setPatientDeviceStatus(STATUS_OFF_LINE, ope);
        hideProgress();
    }

    //****************************************lifecycle & overrides method********************************************
    Fragment getCurrentFrag() {
        return getSupportFragmentManager().findFragmentById(R.id.content);
    }

    public void setHomeNavigation() {
        Constant.istabselected="";
        Constant.ishomefragment="MainFragment";
        binding.bottomBar.setSelectedItemId(R.id.home);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                if (!tabletMode) {
                    if (!(homefragment.curFragment instanceof MainFragment)) {
                        ishomesnot="";
                        HomeFragment.getInstance().openMainFragment();
                    }
                }

                break;
            case R.id.my_appointments:
                homefragment.openApptFragment(FUTUREAPPT, false);
                break;
            case R.id.health_record:
                homefragment.openHealthRecordFragment();
                break;

        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Log.e("Constant.ishomefragment",""+Constant.ishomefragment);
        if (Constant.ishomefragment.equals("MainFragment")){
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }else {
            if (homefragment != null && homefragment.curFragment instanceof MainFragment) {
                ishomesnot = "";
                Log.e("curFragment2", "" + curFragment);
                Log.e("curFragment2", "" + curFragment.getChildFragmentManager().getFragments());

                super.onBackPressed();
            } else if (homefragment.curFragment instanceof AppointmentFragment || homefragment.curFragment instanceof MeFragment) {
                binding.bottomBar.setSelectedItemId(R.id.home);
            } else if (homefragment.curFragment instanceof DoctorListFragment || homefragment.curFragment instanceof DoctorBaseFrag) {
                HomeFragment.getInstance().openMainActivity();
                binding.bottomBar.setSelectedItemId(R.id.home);
            } else if (homefragment.curFragment instanceof IOTActivity_MainPage) {
                ((IOTActivity_MainPage) homefragment.curFragment).backPress();
            }/*else if (homefragment.curFragment instanceof VitalCheckFragment) {
            HomeFragment.getInstance().openHomeVitalcheckFragment();
        }*/ else if (homefragment.curFragment instanceof VitalMonitorFragment) {
                HomeFragment.getInstance().openMainActivity();
            } else if (curFragment.getChildFragmentManager().getFragments().size() > 1) {
                binding.bottomBar.setSelectedItemId(R.id.home);
            } else {
                Log.e("curFragment2", "" + curFragment);
                Log.e("curFragment2", "" + curFragment.getChildFragmentManager().getFragments());
                super.onBackPressed();
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        notificationCheck(this);
        isVisible = true;
        resetLogoutTimer();
    }


    @Override
    public void onPause() {
        super.onPause();
        stopLogoutTimer();
        isVisible = false;
    }

    @Override
    public void onStop() {
        super.onStop();
        isVisible = false;
        stopLogoutTimer();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //Hide Keyboard
        View view = getCurrentFocus();
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (isHideInput(view, ev)) {
                HideSoftInput(view.getWindowToken());
            }
        }
        resetLogoutTimer();
        return super.dispatchTouchEvent(ev);
    }


    //*********************************Show Change Location Dialog*******************************************
    public void checkOutState() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        /*Ready to change*/
        //defaultState = prefs.getString("state_key", "");
        defaultState = getLoginInfo2().getUserInfo().getDefault_state();
        /*Ready to change*/
        //Compared location is the location out of default state
        String compareLocation = prefs.getString("compare_location", "");
        if (!getTabletMode()) {
            compareLocations();
        }

    }

    private void compareLocations() {
        //Find current location
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_CAMERA_AUDIO_REQUEST_CODE);
            }
            return;
        }
        // Create the location request to start receiving updates
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        /* 10 secs */
        long UPDATE_INTERVAL = 10 * 1000;
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        /* 2 sec */
        long FASTEST_INTERVAL = 2000;
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onLocationChanged(locationResult.getLastLocation());
            }
        };

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

        // Define the criteria how to select the locatioin provider -> use
        // default
        //Location location = locationManager.getLastKnownLocation(provider);

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            Geocoder geoCoder = new Geocoder(this, Locale.getDefault());

            try {
                List<Address> address = geoCoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                String stateName="";
                if (address != null && !address.isEmpty()) {
                    Address address2 = address.get(0);
                    stateName = address2.getAdminArea();
                }
         //      String stateName = address.get(0).getAdminArea();
                final String currState = StateAbbr.valueOfName(stateName).getAbbreviation();

                if (!(TextUtils.isEmpty(currState)) && !currState.equals("All")
                        && !currState.equals(checkChangeState) && !currState.equals(defaultState)) {
                    MyAlertDialog dialog = new MyAlertDialog(this);
                    dialog.show();
                    dialog.setDialogTitle(getString(R.string.main_location) + " " + (!defaultState.isEmpty() ? defaultState : getString(R.string.all_states)));
                    dialog.setDialogContent(getString(R.string.main_location_message_1) + " " + currState + " " + getString(R.string.main_location_message_2) + " "
                            + currState + " " + getString(R.string.main_location_message_3));

                    dialog.setRightClickListener(getString(R.string.btn_yes), view -> {
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(FragmentMainActivity.this);
                        SharedPreferences.Editor editor = prefs.edit();
                        getLoginInfo2().getUserInfo().setDefault_state(currState);
                        editor.apply();
                        checkChangeState = currState;
                        updateUserDefaultState(user_id, currState);
                        HomeFragment.getInstance().getDoctorListFragment().reloadWithNewState();

                    });
                    dialog.setLeftClickListener(getString(R.string.btn_no), view -> checkChangeState = currState);
                }

            } catch (Exception e) {
                Log.d("LOCATIONDEBUG", "location " + e.getClass());
                e.printStackTrace();
              //  Toast.makeText(this, "Update location failed!", Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d("LOCATIONDEBUG", "location null");
        }
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 22222 && resultCode == RESULT_OK) {
            getLoginInfo2().load();
        } else if (requestCode == DEFAULT_CARD_ENTRY_REQUEST_CODE) {
            if (homefragment != null) {
                if (homefragment.curFragment != null && (homefragment.curFragment instanceof DoctorBaseFrag ||
                        homefragment.curFragment instanceof MeFragment)) {
                    for (Fragment fragment : homefragment.curFragment.getChildFragmentManager().getFragments()) {
                        if (fragment instanceof PaymentFrag || fragment instanceof PaymentCreditCardFrag) {
                            fragment.onActivityResult(requestCode, resultCode, data);
                            break;
                        }
                    }
                }
            }
        } else if (requestCode == REQUEST_IMAGE_SELECTION) {
            if (homefragment != null) {
                if (homefragment.curFragment != null && homefragment.curFragment instanceof DoctorBaseFrag) {
                    for (Fragment fragment : homefragment.curFragment.getChildFragmentManager().getFragments()) {
                        if (fragment instanceof ConfirmAppointmentFragment) {
                            fragment.onActivityResult(requestCode, resultCode, data);
                            break;
                        }
                    }
                }
            }
        } else {
            if (homefragment != null) {
                if (homefragment.curFragment != null && homefragment.curFragment instanceof MainFragment) {
                    homefragment.curFragment.onActivityResult(requestCode, resultCode, data);
                }
            }
            if (homefragment != null) {
                if (homefragment.curFragment != null && homefragment.curFragment instanceof AppointmentFragment) {
                    homefragment.curFragment.onActivityResult(requestCode, resultCode, data);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CAMERA_MIC || requestCode == CALENDARHELPER_PERMISSION_REQUEST_CODE ||
                requestCode == MY_CAMERA_AUDIO_REQUEST_CODE) {
            if (homefragment != null) {
                if (homefragment.curFragment != null && homefragment.curFragment instanceof DoctorBaseFrag) {
                    for (Fragment fragment : homefragment.curFragment.getChildFragmentManager().getFragments()) {
                        if (fragment instanceof ConfirmAppointmentFragment) {
                            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
                            break;
                        }
                    }
                }
            }

        }
    }
    //*********************************************Tablet Mode**************************************************************

    /**
     * initliaze tablet mode
     */
    private void initTabletMode() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        tabletMode = preferences.getBoolean("tabletmode", false);
        user_id = getLoginInfo2().getAccount();
        if (tabletMode) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //Disable DOZE and AppStandby Mode
                Intent intent = new Intent();
                String packageName = getPackageName();
                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                    intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + packageName));
                    startActivity(intent);
                }
                PermissionUtil.checkCameraAudioPermission(this, null);
            }

            WifiManager manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiManager.WifiLock lock = manager.createWifiLock("MyWifiLock");
            lock.acquire();
            startService(new Intent(this, TabletDataService.class));
        }
    }

    private void accessMeTablet() {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.dialog_tablet_verfication, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);

        TextView textView = (TextView) promptsView.findViewById(R.id.user_id);
        textView.setText(user_id);
        mEditPwd = (EditText) promptsView.findViewById(R.id.password);
        mEditPwd.setTypeface(Typeface.DEFAULT);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(getString(R.string.dialog_enter),
                        (dialog, id) -> {

                        })
                .setNegativeButton(getString(R.string.dialog_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();


        alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyPassword()) {
                    tabletVerificationPassword = mEditPwd.getText().toString();
                    userLogin(user_id, tabletVerificationPassword);
                }
            }
        });
    }


    private boolean verifyPassword() {
        String password = mEditPwd.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mEditPwd.setError(getString(R.string.regist_error_password));
            mEditPwd.requestFocus();
            return false;
        }
        return true;
    }

    /**
     * tablet mode user login
     *
     * @param email    useremail
     * @param passWord password
     */
    private void userLogin(String email, String passWord) {
        viewModel.getUserLoginTask(email, passWord, getLoginInfo2().getOneSignalUserId());
    }

    /**
     * tablet mode get user info
     *
     * @param userId user id
     */
    private void getUserInfo(String userId) {
        viewModel.getUserInfoAsyncTask(userId);
    }

    public void hideBottomBar() {
        binding.bottomBar.setVisibility(View.GONE);
    }
}
