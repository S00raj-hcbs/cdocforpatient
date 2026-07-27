package com.cybermed.cdoc_patient.me.vitalcheck;

import static com.cybermed.cdoc_patient.common.CDoctor2Application.getAndSetDeviceVector;
import static com.cybermed.cdoc_patient.common.videoui.Constant.istabselected;
import static com.cybermed.cdoc_patient.me.MeFragment.USERINFOKEY;
import static com.cybermed.cdoc_patient.util.AppConstant.DATE_FORMAT2;
import static com.cybermed.cdoc_patient.util.AppConstant.IS_FROM_HEALTH_RECORD;
import static com.cybermed.cdoc_patient.util.AppConstant.IShome;
import static com.cybermed.cdoc_patient.util.AppConstant.KEY_BMI;
import static com.cybermed.cdoc_patient.util.AppConstant.KEY_HEIGHT;
import static com.cybermed.cdoc_patient.util.AppConstant.KEY_WEIGHT;
import static com.cybermed.cdoc_patient.util.AppConstant.SERVER_DATE_FORMAT;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.health.connect.client.HealthConnectClient;
import androidx.health.connect.client.PermissionController;
import androidx.health.connect.client.permission.HealthPermission;
import androidx.health.connect.client.records.BloodGlucoseRecord;
import androidx.health.connect.client.records.BloodPressureRecord;
import androidx.health.connect.client.records.BodyTemperatureRecord;
import androidx.health.connect.client.records.DistanceRecord;
import androidx.health.connect.client.records.ExerciseSessionRecord;
import androidx.health.connect.client.records.HeartRateRecord;
import androidx.health.connect.client.records.HeightRecord;
import androidx.health.connect.client.records.OxygenSaturationRecord;
import androidx.health.connect.client.records.RespiratoryRateRecord;
import androidx.health.connect.client.records.StepsRecord;
import androidx.health.connect.client.records.WeightRecord;
import androidx.health.connect.client.time.TimeRangeFilter;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cdfortis.datainterface.soap.UserInfo;
import com.cdfortis.datainterface.soap.model.IoT_Device;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.annotation.Factory;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.IOTDeviceListFragment;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealthGraph.GraphData;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch.vital_graph.utility.Custom;
import com.cybermed.cdoc_patient.common.BaseFragment;
import com.cybermed.cdoc_patient.common.CDoctor2Application;
import com.cybermed.cdoc_patient.common.videoui.Constant;
import com.cybermed.cdoc_patient.databinding.FragmentNewVitalCheckUiBinding;
import com.cybermed.cdoc_patient.main.FragmentMainActivity;
import com.cybermed.cdoc_patient.me.manager.ProfileApiManager;
import com.cybermed.cdoc_patient.me.vitalcheck.adapter.VitalHCRecycleViewAdapter;
import com.cybermed.cdoc_patient.me.vitalcheck.adapter.VitalRecycleViewAdapter;
import com.cybermed.cdoc_patient.me.vitalcheck.model.ClinicVitaldata;
import com.cybermed.cdoc_patient.me.vitalcheck.model.ReqSaveVitalData;
import com.cybermed.cdoc_patient.me.vitalcheck.model.ReqVitalData;
import com.cybermed.cdoc_patient.me.vitalcheck.model.ResponseVital;
import com.cybermed.cdoc_patient.me.vitalcheck.model.VitalData;
import com.cybermed.cdoc_patient.me.vitalcheck.model.VitalDataBP;
import com.cybermed.cdoc_patient.me.vitalcheck.model.VitalDataNew;
import com.cybermed.cdoc_patient.util.AppUtiltiy;
import com.cybermed.cdoc_patient.util.DateUtil;
import com.cybermed.cdoc_patient.util.ErrorMessage;
import com.cybermed.cdoc_patient.view.MedicalDisclaimerDialog;
import com.cybermed.cdoc_patient.webapi.IResponseReceiver;
import com.cybermed.cdoc_patient.webapi.manager.HomeApiManager;
import com.google.gson.Gson;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import kotlin.Unit;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.jvm.functions.Function1;

public class VitalMonitorFragment extends BaseFragment /*implements HomeFragment.OnInnerFragmentStatusChange*/{

    Activity context;
    FragmentNewVitalCheckUiBinding binding;

    private ArrayList<VitalDataBP> bp_dataVector;
    String[] bph_dataPoints;
    String[] bpl_dataPoints;
    String[] hr_datapoints;

    public static final int REQUEST_ENABLE_BT = 9010;
    private String[] weight_datapoints;
    private String[] bo_datapoints;
    private String[] glucose_datapoints;
    private String[] bp_timestamp, hr_timestamp, weight_timestamp, bo_timestamp, glucose_timestamp;
    ArrayList<VitalData> hr_dataVector;
    private ArrayList<VitalData> weight_dataVector;
    private ArrayList<VitalData> bo_dataVector;
    private ArrayList<VitalData> glucose_dataVector;

    List<VitalDataNew> clinicVitalDataList2;

    private List<BloodGlucoseRecord> glucoseList = new ArrayList<>();
    private List<BloodPressureRecord> bpList = new ArrayList<>();
    private List<HeartRateRecord> hrList = new ArrayList<>();
    private List<OxygenSaturationRecord> spo2List = new ArrayList<>();
    private List<RespiratoryRateRecord> rrList = new ArrayList<>();
    private List<BodyTemperatureRecord> tempList = new ArrayList<>();
    private List<HeightRecord> heightList = new ArrayList<>();
    private List<WeightRecord> weightList = new ArrayList<>();
    private int pendingCallbacks = 0;

    private static final int PERMISSIONS_REQUEST_CODE = 1001;

    private static final String TAG = "HealthConnectDemo";

    private HealthConnectClient healthConnectClient=null;

    private Executor executor;



    public static final int IDX_BP_Diastolic        = 0;
    public static final int IDX_HRV                 = 1;
    public static final int IDX_RESP_RATE           = 2;
    public static final int IDX_GLUCOSE             = 3;
    public static final int IDX_TEMP                = 4;
    public static final int IDX_BP_Systolic         = 5;
    public static final int IDX_HR                  = 6;
    public static final int IDX_SPO2                = 7;
    public static final int IDX_RestingHR           = 8;
    public static final int IDX_CALORIES            = 9;
    public static final int IDX_STEP                = 10;
    public static final int IDX_DISTANCE            = 11;
    public static final int IDX_EXERCISE            = 12;
    public static final int IDX_VO2                 = 13;
    public static final int IDX_BMI                 = 14;
    public static final int IDX_HEIGHT              = 15;
    public static final int IDX_WEIGHT              = 16;
    public static final int IDX_BODY_FAT            = 17;
    public static final int IDX_BMR                 = 18;
    public static final int IDX_LeanBodyMass        = 19;

    private Set<String> requiredPermissions = new HashSet<>();
    ActivityResultLauncher<Set<String>> permissionLauncher;
    List<VitalDataNew> clinicVitalDataList;
    public static final String BP_DEVICE_TYPE = "IChoice_BP", PO_DEVICE_TYPE = "IChoice_Oximeter", GLUCOMETER_DEVICE_TYPE = "IChoice_Glucose", SCALE_DEVICE_TYPE = "IChoice_Scale";
    public static boolean isDeviceTablet;
    private final int[] colors = new int[]{Color.parseColor("#fe0000"),
            Color.parseColor("#750e72"),
            Color.parseColor("#53BD8B"),
            Color.parseColor("#F2727A"),
            Color.parseColor("#F79452"),
            Color.parseColor("#DDA827")};

    GraphData graphData;
    Vector<IoT_Device> ioT_devices;
    Custom custom;
    ArrayList<String> device_value;


    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_vital_check_ui, container, false);
        return binding.getRoot();
    }

    @Override
    protected void initLayout(View view) {
        context = getActivity();
        //callApi();
        clickListner();
        initVal();
      //  GraphUI.decorateGraph3(binding);
        pendingCallbacks = 8;

        glucoseList.clear();
        bpList.clear();
        hrList.clear();
        spo2List.clear();
        rrList.clear();
        tempList.clear();
        heightList.clear();
        weightList.clear();

       /* executor = Executors.newSingleThreadExecutor();

        permissionLauncher=
                registerForActivityResult(
                        PermissionController.createRequestPermissionResultContract(),
                        isGranted -> {
                            // isGranted is a Set<String> of granted permissions
                            Set<String> granted = new HashSet<>(isGranted);
                            boolean allGranted = requiredPermissions.stream()
                                    .allMatch(granted::contains);

                            if (allGranted) {

                            } else {
                                // Show which ones failed
                                Set<String> denied = requiredPermissions.stream()
                                        .filter(perm -> !granted.contains(perm))
                                        .collect(Collectors.toSet());
                                Toast.makeText(requireActivity(), "Denied: " + denied.toString(), Toast.LENGTH_LONG).show();
                                // Optionally, prompt to retry or direct to settings
                                promptRetryPermissions();
                            }
                        }
                );*/

        /*checkHealthConnect(requireActivity());*/
    }


    /*public void checkHealthConnect(Context context) {
        int status = HealthConnectClient.getSdkStatus(context);

        switch (status) {

            case HealthConnectClient.SDK_AVAILABLE:
                // Everything OK
                if(healthConnectClient==null){
                    healthConnectClient = HealthConnectClient.getOrCreate(requireActivity());  // Ensure client is initialized
                }
                setupHealthPermissions();
                break;
            case HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED:
                // Ask user to update Health Connect
                openHealthConnectInstallPage(requireActivity());
                break;

            case HealthConnectClient.SDK_UNAVAILABLE:
            default:
                Toast.makeText(context, "Health Connect not supported on this device", Toast.LENGTH_LONG).show();
                break;
        }
    }*/



    /*public void openHealthConnectInstallPage(Context context) {
        Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.healthdata");

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);

        // If context is not Activity → REQUIRED
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        // Try opening Play Store app
        intent.setPackage("com.android.vending");

        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // Fallback to browser
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
            if (!(context instanceof Activity)) {
                browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(browserIntent);
        }
    }*/



    private void setupHealthPermissions() {
        // Define permissions (use HealthPermission for correct strings)

       /* requiredPermissions.clear();
        requiredPermissions.add(HealthPermission.getReadPermission(JvmClassMappingKt.getKotlinClass(BloodGlucoseRecord.class)));
        requiredPermissions.add(HealthPermission.getReadPermission(JvmClassMappingKt.getKotlinClass(BloodPressureRecord.class)));
        requiredPermissions.add(HealthPermission.getReadPermission(JvmClassMappingKt.getKotlinClass(HeartRateRecord.class)));
        requiredPermissions.add(HealthPermission.getReadPermission(JvmClassMappingKt.getKotlinClass(WeightRecord.class)));*/
        /*requiredPermissions.add(HealthPermission.getReadPermission(JvmClassMappingKt.getKotlinClass(StepsRecord.class)));*/
        /*requiredPermissions.add(HealthPermission.getReadPermission(JvmClassMappingKt.getKotlinClass(HeightRecord.class)));*/
        /*requiredPermissions.add(HealthPermission.getReadPermission(JvmClassMappingKt.getKotlinClass(BodyTemperatureRecord.class)));*/
        /*requiredPermissions.add(HealthPermission.getReadPermission(JvmClassMappingKt.getKotlinClass(RespiratoryRateRecord.class)));*/
        /*requiredPermissions.add(HealthPermission.getReadPermission(JvmClassMappingKt.getKotlinClass(OxygenSaturationRecord.class)));*/
        /*requiredPermissions.add(HealthPermission.getReadPermission(JvmClassMappingKt.getKotlinClass(HeartRateVariabilityRmssdRecord.class)));*/
        /*requiredPermissions.add(HealthPermission.getReadPermission(JvmClassMappingKt.getKotlinClass(Vo2MaxRecord.class)));*/
        /*requiredPermissions.add(HealthPermission.getReadPermission(JvmClassMappingKt.getKotlinClass(ExerciseSessionRecord.class)));*/
        /*requiredPermissions.add(HealthPermission.getReadPermission(JvmClassMappingKt.getKotlinClass(BasalMetabolicRateRecord.class)));*/
        /*requiredPermissions.add(HealthPermission.getReadPermission(JvmClassMappingKt.getKotlinClass(DistanceRecord.class)));*/
       /* requiredPermissions.add(HealthPermission.getReadPermission(JvmClassMappingKt.getKotlinClass(BodyFatRecord.class)));
        requiredPermissions.add(HealthPermission.getReadPermission(JvmClassMappingKt.getKotlinClass(TotalCaloriesBurnedRecord.class)));
        requiredPermissions.add(HealthPermission.getReadPermission(JvmClassMappingKt.getKotlinClass(RestingHeartRateRecord.class)));
        requiredPermissions.add(HealthPermission.getReadPermission(JvmClassMappingKt.getKotlinClass(LeanBodyMassRecord.class)));*/

        // Create the SPECIALIZED launcher for Health Connect

        // Check current status and request if needed
        /*checkAndRequestPermissions(permissionLauncher);*/
    }



/*
    private void promptRetryPermissions() {
        if(healthConnectClient==null){
            healthConnectClient = HealthConnectClient.getOrCreate(requireActivity());  // Ensure client is initialized
        }
        // Button or dialog to retry: Call setupHealthPermissions() again
        new AlertDialog.Builder(requireActivity())
                .setMessage("Permissions needed for health data. Retry?")
                .setPositiveButton("Retry", (dialog, which) -> setupHealthPermissions())
                .setNegativeButton("Settings", (dialog, which) -> {
                    // Direct to Health Connect settings

                    // Open Health Connect main settings (recommended way)
                    try {
                        Intent intent = new Intent(HealthConnectClient.getHealthConnectSettingsAction());
                        // or: new Intent(Settings.ACTION_HEALTH_CONNECT_SETTINGS);  // same thing
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        // Very old device or Health Connect not installed → fallback to Play Store
                        Toast.makeText(requireActivity(), "Health Connect not available", Toast.LENGTH_LONG).show();
                        */
/*openHealthConnectInPlayStore();*//*

                    }
                })
                .show();
    }
*/

   /* private void checkAndRequestPermissions(ActivityResultLauncher<Set<String>> launcher) {
        HealthConnectHelper.INSTANCE.checkPermissionsAsync(
                healthConnectClient,
                new Function1<Set<String>, Unit>() {
                    @Override
                    public Unit invoke(Set<String> granted) {
                        // granted is seen as Set<String> here — contains() is available
                        Set<String> needed = new HashSet<>();
                        for (String perm : requiredPermissions) {
                            if (!granted.contains(perm)) {
                                needed.add(perm);
                            }
                        }

                        if (!needed.isEmpty()) {
                            launcher.launch(needed);
                        } else {
                            *//*loadAllHealthData();*//*
                        }
                        return Unit.INSTANCE;
                    }
                }
        );
        //  GraphUI.decorateGraph3(binding);
    }
*/

    @Factory
    public static VitalMonitorFragment newInstance(UserInfo userInfo, boolean isFromHealthRecord, boolean ishome) {
        VitalMonitorFragment fragment = new VitalMonitorFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putSerializable(USERINFOKEY, userInfo);
        args.putBoolean(IS_FROM_HEALTH_RECORD, isFromHealthRecord);
        args.putBoolean(IShome, ishome);
        fragment.setArguments(args);
        return fragment;
    }
    private final ActivityResultLauncher<Set<String>> requestPermissions =
            registerForActivityResult(
                    PermissionController.createRequestPermissionResultContract(),
                    grantedPermissions -> {

                        if (grantedPermissions.containsAll(requiredPermissions)) {

                            // Permission granted
                           /* emptyLayout.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);

                            loadHealthData();*/
                            if (healthConnectClient == null) {
                                Log.e("HealthConnect", "Client is NULL");
                                VitalDataNew vitalDataNew = new VitalDataNew();
                                vitalDataNew.setName(getString(R.string.step));
                                vitalDataNew.setType("STEP");
                                vitalDataNew.setValue("--");
                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_stepcount));
                                setVital(IDX_STEP,vitalDataNew);
                                vitalDataNew = new VitalDataNew();
                                vitalDataNew.setName(getString(R.string.glucose));
                                vitalDataNew.setType("Glucose");
                                vitalDataNew.setValue("--");
                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_bloodglucose));
                                setVital(IDX_GLUCOSE,vitalDataNew);
                                vitalDataNew = new VitalDataNew();
                                vitalDataNew.setName(getString(R.string.blood_pressure_sys));
                                vitalDataNew.setType("BP Sys");
                                vitalDataNew.setValue("--");
                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_bloodpressuresystolic));
                                setVital(IDX_BP_Systolic,vitalDataNew);
                                vitalDataNew = new VitalDataNew();
                                vitalDataNew.setName(getString(R.string.blood_pressure_dia));
                                vitalDataNew.setType("BP Dia");
                                vitalDataNew.setValue("--");
                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_bloodpressurediastolic));
                                setVital(IDX_BP_Diastolic,vitalDataNew);
                                vitalDataNew = new VitalDataNew();
                                vitalDataNew.setName(getString(R.string.heart_rate));
                                vitalDataNew.setType("HR");
                                vitalDataNew.setValue("--");
                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_heartrate));
                                setVital(IDX_HR,vitalDataNew);
                                vitalDataNew = new VitalDataNew();
                                vitalDataNew.setName(getString(R.string.weight));
                                vitalDataNew.setType("Weight");
                                vitalDataNew.setValue("--");
                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_bodymass));
                                setVital(IDX_WEIGHT,vitalDataNew);
                                vitalDataNew = new VitalDataNew();
                                vitalDataNew.setName(getString(R.string.vitals_height));
                                vitalDataNew.setType("Height");
                                vitalDataNew.setValue("--");
                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_height));
                                setVital(IDX_HEIGHT,vitalDataNew);
                                vitalDataNew = new VitalDataNew();
                                vitalDataNew.setName(getString(R.string.temperature));
                                vitalDataNew.setType("Temp");
                                vitalDataNew.setValue("--");
                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_bodytemperature));
                                setVital(IDX_TEMP,vitalDataNew);
                                vitalDataNew = new VitalDataNew();
                                vitalDataNew.setName(getString(R.string.respiratory_rate));
                                vitalDataNew.setType("Respiratory_Rate");
                                vitalDataNew.setValue("--");
                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_respiratoryrate));
                                setVital(IDX_RESP_RATE,vitalDataNew);
                                vitalDataNew = new VitalDataNew();
                                vitalDataNew.setName(getString(R.string.oxygen_saturation));
                                vitalDataNew.setType("SpO2");
                                vitalDataNew.setValue("--");
                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_oxygensaturation));
                                setVital(IDX_SPO2,vitalDataNew);
                                vitalDataNew = new VitalDataNew();
                                vitalDataNew.setName(getString(R.string.heart_rate_variability_rms));
                                vitalDataNew.setType("HRV");
                                vitalDataNew.setValue("--");
                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_heartrate));
                                setVital(IDX_HRV,vitalDataNew);
                                vitalDataNew = new VitalDataNew();
                                vitalDataNew.setName(getString(R.string.vo2_max));
                                vitalDataNew.setType("VO2");
                                vitalDataNew.setValue("--");
                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.pulse_vc));
                                setVital(IDX_VO2,vitalDataNew);
                                vitalDataNew = new VitalDataNew();
                                vitalDataNew.setName(getString(R.string.resting_heart_rate));
                                vitalDataNew.setType("resting_hr");
                                vitalDataNew.setValue("--");
                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_heartrate));
                                setVital(IDX_RestingHR,vitalDataNew);
                                vitalDataNew = new VitalDataNew();
                                vitalDataNew.setName(getString(R.string.exercise));
                                vitalDataNew.setType("Exercise");
                                vitalDataNew.setValue("--");
                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_exercisetime));
                                setVital(IDX_EXERCISE,vitalDataNew);
                                vitalDataNew = new VitalDataNew();
                                vitalDataNew.setName(getString(R.string.basal_metabolic_rate));
                                vitalDataNew.setType("BMR");
                                vitalDataNew.setValue("--");
                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_basalenergyburned));
                                setVital(IDX_BMR,vitalDataNew);
                                vitalDataNew = new VitalDataNew();
                                vitalDataNew.setName(getString(R.string.calories));
                                vitalDataNew.setType("CALORIES");
                                vitalDataNew.setValue("--");
                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_totalenergyburned));
                                setVital(IDX_CALORIES,vitalDataNew);
                                vitalDataNew = new VitalDataNew();
                                vitalDataNew.setName(getString(R.string.distance));
                                vitalDataNew.setType("distance");
                                vitalDataNew.setValue("--");
                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_distancewalkingrunning));
                                setVital(IDX_DISTANCE,vitalDataNew);
                                vitalDataNew = new VitalDataNew();
                                vitalDataNew.setName(getString(R.string.body_fat));
                                vitalDataNew.setType("body_fat");
                                vitalDataNew.setValue("--");
                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_bodyfatpercentage));
                                setVital(IDX_BODY_FAT,vitalDataNew);
                                vitalDataNew = new VitalDataNew();
                                vitalDataNew.setName(getString(R.string.bmi));
                                vitalDataNew.setType("bmi");
                                vitalDataNew.setValue("--");
                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_bodymassindex));
                                setVital(IDX_BMI,vitalDataNew);
                                vitalDataNew = new VitalDataNew();
                                vitalDataNew.setName(getString(R.string.lean_body_mass));
                                vitalDataNew.setType("lean_body_mass");
                                vitalDataNew.setValue("--");
                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_leanbodymass));
                                setVital(IDX_LeanBodyMass,vitalDataNew);
                            }
                            else {
                                /*Instant start2 = Instant.now().minus(Duration.ofDays(30));*/
                                showProgressBar();
                                Instant start = Instant.now().minus(Duration.ofDays(30));
                                Instant end = Instant.now();
                                clinicVitalDataList2 = new ArrayList<>();
                                clinicVitalDataList2 =
                                        new ArrayList<>(Collections.nCopies(20, null));
                                ZoneId zone = ZoneId.systemDefault();

                                Instant start2 = LocalDate.now()
                                        .atStartOfDay(zone)
                                        .toInstant();
                                Instant end2 = Instant.now();
                                VitalDataNew vitalDataNew = new VitalDataNew();
                                vitalDataNew.setName(getString(R.string.step));
                                vitalDataNew.setType("STEP");
                                vitalDataNew.setValue("--");
                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_stepcount));

                                HealthConnectHelper.readBodyGlucoseByTimeRangeJava(
                                        healthConnectClient,
                                        start,
                                        end,
                                        records -> {
                                            if (!records.isEmpty()){

                                                Gson gson = new Gson();
                                                VitalDataNew vitalDataNew2 = new VitalDataNew();
                                                vitalDataNew2.setName(getString(R.string.glucose));
                                                vitalDataNew2.setType("Glucose");
                                                vitalDataNew2.setValue(TextUtils.isEmpty(String.valueOf(records.get(records.size()-1).getLevel().getMilligramsPerDeciliter())) ? "--" : String.format("%d", Math.round(
                                                        (float) records.get(records.size()-1)
                                                                .getLevel()
                                                                .getMilligramsPerDeciliter()
                                                )/*records.get(records.size()-1).getLevel().getMilligramsPerDeciliter()*/));
                                                vitalDataNew2.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_bloodglucose));
                                                setVital(IDX_GLUCOSE,vitalDataNew2);
                                            }else {
                                                VitalDataNew vitalDataNew2 = new VitalDataNew();
                                                vitalDataNew2.setName(getString(R.string.glucose));
                                                vitalDataNew2.setType("Glucose");
                                                vitalDataNew2.setValue("--");
                                                vitalDataNew2.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_bloodglucose));
                                                setVital(IDX_GLUCOSE,vitalDataNew2);
                                            }
                                            glucoseList = records;

                                            onVitalCallbackDone(context);
                                            return null;
                                        }
                                );
                                HealthConnectHelper.readBloodPressureByTimeRangeJava(
                                        healthConnectClient,
                                        start,
                                        end,
                                        records -> {
                                            if (!records.isEmpty()) {

                                                Gson gson = new Gson();
                                                String json = gson.toJson(records);
                                                /*Log.e("BP", json);*/
                                                VitalDataNew vitalDataNew2 = new VitalDataNew();

                                                double systolic= TextUtils.isEmpty(String.valueOf(records.get(records.size()-1).getSystolic().toString()))?0.0:records.get(records.size()-1).getSystolic().getMillimetersOfMercury();
                                                double Diastolic= TextUtils.isEmpty(String.valueOf(records.get(records.size()-1).getDiastolic().toString()))?0.0:records.get(records.size()-1).getDiastolic().getMillimetersOfMercury();

                                                vitalDataNew2 = new VitalDataNew();
                                                vitalDataNew2.setName(getString(R.string.blood_pressure_sys));
                                                vitalDataNew2.setType("BP Sys");
                                                vitalDataNew2.setValue(systolic==0.0?"--":String.valueOf(systolic));
                                                vitalDataNew2.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_bloodpressuresystolic));
                                                setVital(IDX_BP_Systolic,vitalDataNew2);
                                                vitalDataNew2 = new VitalDataNew();
                                                vitalDataNew2.setName(getString(R.string.blood_pressure_dia));
                                                vitalDataNew2.setType("BP Dia");
                                                vitalDataNew2.setValue(Diastolic==0.0?"--":String.valueOf(Diastolic));
                                                vitalDataNew2.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_bloodpressurediastolic));
                                                setVital(IDX_BP_Diastolic,vitalDataNew2);



                                            } else {

                                                VitalDataNew vitalDataNew2 = new VitalDataNew();
                                                vitalDataNew2 = new VitalDataNew();
                                                vitalDataNew2.setName(getString(R.string.blood_pressure_sys));
                                                vitalDataNew2.setType("BP Sys");
                                                vitalDataNew2.setValue("--");
                                                vitalDataNew2.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_bloodpressuresystolic));
                                                setVital(IDX_BP_Systolic,vitalDataNew2);
                                                vitalDataNew2 = new VitalDataNew();
                                                vitalDataNew2.setName(getString(R.string.blood_pressure_dia));
                                                vitalDataNew2.setType("BP Dia");
                                                vitalDataNew2.setValue("--");
                                                vitalDataNew2.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_bloodpressurediastolic));
                                                setVital(IDX_BP_Diastolic,vitalDataNew2);
                                            }
                                            bpList = records;

                                            onVitalCallbackDone(context);
                                            return null;
                                        }
                                );
                                HealthConnectHelper.readHeartRateByTimeRangeJava(
                                        healthConnectClient,
                                        start,
                                        end,
                                        records -> {
                                            if (!records.isEmpty()) {

                                                Gson gson = new Gson();
                                                String json = gson.toJson(records);
                                                Log.e("jsonhr",json.toString());
                                                VitalDataNew vitalDataNew2 = new VitalDataNew();
                                                vitalDataNew2.setName(getString(R.string.heart_rate));
                                                vitalDataNew2.setType("HR");
                                                vitalDataNew2.setValue(TextUtils.isEmpty(String.valueOf(records.get(records.size()-1).getSamples().toString())) ? "--" : String.valueOf(records.get(records.size()-1).getSamples().toString()+" bpm"));
                                                vitalDataNew2.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_heartrate));
                                                setVital(IDX_HR,vitalDataNew2);

                                            } else {
                                                VitalDataNew vitalDataNew2 = new VitalDataNew();
                                                vitalDataNew2.setName(getString(R.string.heart_rate));
                                                vitalDataNew2.setType("HR");
                                                vitalDataNew2.setValue("--");
                                                vitalDataNew2.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_heartrate));
                                                setVital(IDX_HR,vitalDataNew2);
                                            }
                                            hrList = records;

                                            onVitalCallbackDone(context);
                                            return null;
                                        }
                                );

                                HealthConnectHelper.readHeightRangeJava(
                                        healthConnectClient,
                                        start,
                                        end,
                                        records -> {
                                            if (!records.isEmpty()) {
                                                Gson gson = new Gson();
                                                String json = gson.toJson(records);
                                                VitalDataNew vitalDataNew2 = new VitalDataNew();
                                                vitalDataNew2.setName(getString(R.string.vitals_height));
                                                vitalDataNew2.setType("Height");
                                                vitalDataNew2.setValue(TextUtils.isEmpty(String.valueOf(records.get(records.size()-1).getHeight().getMeters())) ? "--" : String.valueOf(records.get(records.size()-1).getHeight().getMeters())+" m");
                                                vitalDataNew2.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_height));
                                                setVital(IDX_HEIGHT,vitalDataNew2);
                              /*  VitalDataNew vitalDataNew = new VitalDataNew();
                                vitalDataNew.setName(getString(R.string.vitals_height));
                                vitalDataNew.setType("Height");
                                double inchDouble = records.get(records.size() - 1).getHeight().getInches();


                                int totalInches = (int) (inchDouble);
                                int feet = totalInches / 12;
                                int inches = totalInches % 12;
                                String heightValue;
                                if (feet == 0 && inches == 0) {
                                    heightValue = "--";
                                } else {
                                    heightValue = feet + " Ft " + inches + " In";
                                }

                                vitalDataNew.setValue(heightValue);
                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_height));

                                setVital(IDX_HEIGHT, vitalDataNew);*/
                                            } else {
                                                VitalDataNew vitalDataNew2 = new VitalDataNew();
                                                vitalDataNew2.setName(getString(R.string.vitals_height));
                                                vitalDataNew2.setType("Height");
                                                vitalDataNew2.setValue("--");
                                                vitalDataNew2.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_height));
                                                setVital(IDX_HEIGHT,vitalDataNew2);
                                            }
                                            heightList = records;

                                            onVitalCallbackDone(context);
                                            return null;
                                        }
                                );

                                HealthConnectHelper.readWeightRangeJava(
                                        healthConnectClient,
                                        start,
                                        end,
                                        records -> {
                                            if (!records.isEmpty()) {

                                                Gson gson = new Gson();
                                                String json = gson.toJson(records);
                                                VitalDataNew vitalDataNew2 = new VitalDataNew();
                                                vitalDataNew2.setName(getString(R.string.weight));
                                                vitalDataNew2.setType("Weight");
                                                vitalDataNew2.setValue(TextUtils.isEmpty(String.valueOf(records.get(records.size()-1).getWeight().getKilograms())) ? "--" : String.format("%.1f kg",records.get(records.size()-1).getWeight().getKilograms()));
                                                vitalDataNew2.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_bodymass));
                                                setVital(IDX_WEIGHT,vitalDataNew2);
                              /*  if (!heightList.isEmpty()){
                                    double bmi = records.get(records.size()-1).getWeight().getKilograms() / (heightList.get(heightList.size()-1).getHeight().getMeters() * heightList.get(heightList.size()-1).getHeight().getMeters());
                                    VitalDataNew vitalDataNew2 = new VitalDataNew();
                                    vitalDataNew2.setName(getString(R.string.bmi));
                                    vitalDataNew2.setType("bmi");
                                    vitalDataNew2.setValue(String.format("%.1f",bmi));
                                    vitalDataNew2.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_bodymassindex));
                                    setVital(IDX_BMI,vitalDataNew2);
                                }
*/
                                            } else {
                                                VitalDataNew vitalDataNew2 = new VitalDataNew();
                                                vitalDataNew2.setName(getString(R.string.weight));
                                                vitalDataNew2.setType("Weight");
                                                vitalDataNew2.setValue("--");
                                                vitalDataNew2.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_bodymass));
                                                setVital(IDX_WEIGHT,vitalDataNew2);
                                            }
                                            weightList = records;

                                            onVitalCallbackDone(context);
                                            return null;
                                        }
                                );
                                 vitalDataNew = new VitalDataNew();
                                vitalDataNew.setName(getString(R.string.temperature));
                                vitalDataNew.setType("Temp");
                                vitalDataNew.setValue("--");
                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_bodytemperature));
                                setVital(IDX_TEMP,vitalDataNew);

                               /* HealthConnectHelper.readTemperatureRecordJava(
                                        healthConnectClient,
                                        start,
                                        end,
                                        records -> {
                                            if (!records.isEmpty()) {
                                                Gson gson = new Gson();
                                                String json = gson.toJson(records);
                                                VitalDataNew vitalDataNew = new VitalDataNew();
                                                vitalDataNew.setName(getString(R.string.temperature));
                                                vitalDataNew.setType("Temp");
                                                vitalDataNew.setValue(TextUtils.isEmpty(String.valueOf(records.get(records.size()-1).getTemperature().getFahrenheit())) ? "--" : String.format("%.1f °F",records.get(records.size()-1).getTemperature().getFahrenheit()));
                                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_bodytemperature));
                                                setVital(IDX_TEMP,vitalDataNew);

                                            } else {
                                                VitalDataNew vitalDataNew = new VitalDataNew();
                                                vitalDataNew.setName(getString(R.string.temperature));
                                                vitalDataNew.setType("Temp");
                                                vitalDataNew.setValue("--");
                                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_bodytemperature));
                                                setVital(IDX_TEMP,vitalDataNew);
                                            }
                                            tempList = records;

                                            onVitalCallbackDone(context);
                                            return null;
                                        }
                                );*/
               /* HealthConnectHelper.readRespiratoryRateRecordJava(
                        healthConnectClient,
                        start,
                        end,
                        records -> {
                            if (!records.isEmpty()) {
                                Gson gson = new Gson();
                                String json = gson.toJson(records);
                                VitalDataNew vitalDataNew = new VitalDataNew();
                                vitalDataNew.setName(getString(R.string.respiratory_rate));
                                vitalDataNew.setType("Respiratory_Rate");
                                vitalDataNew.setValue(TextUtils.isEmpty(String.valueOf(records.get(records.size()-1).getRate())) ? "--" : String.format("%.1f breaths/minute",records.get(records.size()-1).getRate()));
                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_respiratoryrate));
                                setVital(IDX_RESP_RATE,vitalDataNew);

                            } else {
                                VitalDataNew vitalDataNew = new VitalDataNew();
                                vitalDataNew.setName(getString(R.string.respiratory_rate));
                                vitalDataNew.setType("Respiratory_Rate");
                                vitalDataNew.setValue("--");
                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_respiratoryrate));
                                setVital(IDX_RESP_RATE,vitalDataNew);
                            }
                             rrList = records;

                            onVitalCallbackDone(context);
                            return null;
                        }
                );*/
                                 vitalDataNew = new VitalDataNew();
                                vitalDataNew.setName(getString(R.string.oxygen_saturation));
                                vitalDataNew.setType("SpO2");
                                vitalDataNew.setValue("--");
                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_oxygensaturation));
                                setVital(IDX_SPO2,vitalDataNew);

                               /* HealthConnectHelper.readOxygenSaturationRecordJava(
                                        healthConnectClient,
                                        start,
                                        end,
                                        records -> {
                                            if (!records.isEmpty()) {
                                                Gson gson = new Gson();
                                                String json = gson.toJson(records);
                                                VitalDataNew vitalDataNew = new VitalDataNew();
                                                vitalDataNew.setName(getString(R.string.oxygen_saturation));
                                                vitalDataNew.setType("SpO2");
                                                vitalDataNew.setValue(TextUtils.isEmpty(String.valueOf(records.get(records.size()-1).getPercentage().getValue())) ? "--" : String.format("%.2f %",records.get(records.size()-1).getPercentage().getValue()));
                                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_oxygensaturation));
                                                setVital(IDX_SPO2,vitalDataNew);

                                            } else {
                                                VitalDataNew vitalDataNew = new VitalDataNew();
                                                vitalDataNew.setName(getString(R.string.oxygen_saturation));
                                                vitalDataNew.setType("SpO2");
                                                vitalDataNew.setValue("--");
                                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_oxygensaturation));
                                                setVital(IDX_SPO2,vitalDataNew);
                                            }
                                            spo2List = records;

                                            onVitalCallbackDone(context);
                                            return null;
                                        }
                                );*/
              /*  HealthConnectHelper.readHeartRateVariabilityRmssdRecordJava(
                        healthConnectClient,
                        start,
                        end,
                        records -> {
                            if (!records.isEmpty()) {

                                Gson gson = new Gson();
                                String json = gson.toJson(records);
                                VitalDataNew vitalDataNew = new VitalDataNew();
                                vitalDataNew.setName(getString(R.string.heart_rate_variability_rms));
                                vitalDataNew.setType("HRV");
                                vitalDataNew.setValue(TextUtils.isEmpty(String.valueOf(records.get(records.size()-1).getHeartRateVariabilityMillis())) ? "--" : String.valueOf(records.get(records.size()-1).getHeartRateVariabilityMillis())+" ms");
                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_heartrate));
                                setVital(IDX_HRV,vitalDataNew);
                            } else {
                                VitalDataNew vitalDataNew = new VitalDataNew();
                                vitalDataNew.setName(getString(R.string.heart_rate_variability_rms));
                                vitalDataNew.setType("HRV");
                                vitalDataNew.setValue("--");
                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_heartrate));
                                setVital(IDX_HRV,vitalDataNew);
                            }

                            return null;
                        }
                );*/
               /* HealthConnectHelper.readVo2MaxRecordJava(
                        healthConnectClient,
                        start,
                        end,
                        records -> {
                            if (!records.isEmpty()) {

                                Gson gson = new Gson();
                                String json = gson.toJson(records);
                                VitalDataNew vitalDataNew = new VitalDataNew();
                                vitalDataNew.setName(getString(R.string.vo2_max));
                                vitalDataNew.setType("VO2");
                                vitalDataNew.setValue(TextUtils.isEmpty(String.valueOf(records.get(records.size()-1).getVo2MillilitersPerMinuteKilogram())) ? "--" : String.valueOf(records.get(records.size()-1).getVo2MillilitersPerMinuteKilogram())+" ml/kg/m");
                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.pulse_vc));
                                setVital(IDX_VO2,vitalDataNew);
                            } else {
                                VitalDataNew vitalDataNew = new VitalDataNew();
                                vitalDataNew.setName(getString(R.string.vo2_max));
                                vitalDataNew.setType("VO2");
                                vitalDataNew.setValue("--");
                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.pulse_vc));
                                setVital(IDX_VO2,vitalDataNew);
                            }

                            return null;
                        }
                );*/
                                 vitalDataNew = new VitalDataNew();
                                vitalDataNew.setName(getString(R.string.exercise));
                                vitalDataNew.setType("Exercise");
                                vitalDataNew.setValue("--");
                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_exercisetime));
                                setVital(IDX_EXERCISE,vitalDataNew);
                                /*HealthConnectHelper.readExerciseSessionRecordJava(
                                        healthConnectClient,
                                        start,
                                        end,
                                        records -> {
                                            if (!records.isEmpty()) {

                                                Gson gson = new Gson();
                                                String json = gson.toJson(records);
                                                ExerciseSessionRecord record = records.get(records.size() - 1);

                                                VitalDataNew vitalDataNew = new VitalDataNew();

                                                int exerciseType = record.getExerciseType();

                                                vitalDataNew.setName(getString(R.string.exercise)+" ("+getExerciseName(exerciseType)+")");
                                                vitalDataNew.setType("Exercise");
                                                vitalDataNew.setValue(ExerciseTimeUtil.getExerciseTime(record)); // or duration

                                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_exercisetime));

                                                setVital(IDX_EXERCISE,vitalDataNew);


                                            } else {
                                                VitalDataNew vitalDataNew = new VitalDataNew();
                                                vitalDataNew.setName(getString(R.string.exercise));
                                                vitalDataNew.setType("Exercise");
                                                vitalDataNew.setValue("--");
                                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_exercisetime));
                                                setVital(IDX_EXERCISE,vitalDataNew);
                                            }

                                            return null;
                                        }
                                );*/
                /*HealthConnectHelper.readBasalMetabolicRateRecordJava(
                        healthConnectClient,
                        start,
                        end,
                        records -> {
                            if (!records.isEmpty()) {

                                Gson gson = new Gson();
                                String json = gson.toJson(records);
                                VitalDataNew vitalDataNew = new VitalDataNew();
                                vitalDataNew.setName(getString(R.string.basal_metabolic_rate));
                                vitalDataNew.setType("BMR");
                                vitalDataNew.setValue(TextUtils.isEmpty(String.valueOf(records.get(records.size()-1).getBasalMetabolicRate().getKilocaloriesPerDay())) ? "--" : String.valueOf(records.get(records.size()-1).getBasalMetabolicRate().getKilocaloriesPerDay()+" kcal/day"));
                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_basalenergyburned));
                                setVital(IDX_BMR,vitalDataNew);
                            } else {
                                VitalDataNew vitalDataNew = new VitalDataNew();
                                vitalDataNew.setName(getString(R.string.basal_metabolic_rate));
                                vitalDataNew.setType("BMR");
                                vitalDataNew.setValue("--");
                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_basalenergyburned));
                                setVital(IDX_BMR,vitalDataNew);
                            }

                            return null;
                        }
                );*/

               /* HealthConnectHelper.readTotalCaloriesBurnedRecordJava(
                        healthConnectClient,
                        start,
                        end,
                        records -> {
                            if (!records.isEmpty()) {

                                Gson gson = new Gson();
                                String json = gson.toJson(records);
                                VitalDataNew vitalDataNew = new VitalDataNew();
                                vitalDataNew.setName(getString(R.string.calories));
                                vitalDataNew.setType("CALORIES");
                                vitalDataNew.setValue(TextUtils.isEmpty(String.valueOf(records.get(records.size()-1).getEnergy().getKilocalories())) ? "--" : String.format("%.1f",records.get(records.size()-1).getEnergy().getKilocalories())+" kcal");
                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_totalenergyburned));
                                setVital(IDX_CALORIES,vitalDataNew);
                            } else {
                                VitalDataNew vitalDataNew = new VitalDataNew();
                                vitalDataNew.setName(getString(R.string.calories));
                                vitalDataNew.setType("CALORIES");
                                vitalDataNew.setValue("--");
                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_totalenergyburned));
                                setVital(IDX_CALORIES,vitalDataNew);
                            }

                            return null;
                        }
                );*/
                                 vitalDataNew = new VitalDataNew();
                                vitalDataNew.setName(getString(R.string.distance));
                                vitalDataNew.setType("distance");
                                vitalDataNew.setValue("--");
                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_distancewalkingrunning));
                                setVital(IDX_DISTANCE,vitalDataNew);

                                /*HealthConnectHelper.readDistanceRecordJava(
                                        healthConnectClient,
                                        start,
                                        end,
                                        records -> {
                                            if (!records.isEmpty()) {

                                                Gson gson = new Gson();
                                                String json = gson.toJson(records);
                                                VitalDataNew vitalDataNew = new VitalDataNew();
                                                vitalDataNew.setName(getString(R.string.distance));
                                                vitalDataNew.setType("distance");
                                                vitalDataNew.setValue(TextUtils.isEmpty(String.valueOf(records.get(records.size()-1).getDistance().getKilometers())) ? "--" : String.format("%.2f",records.get(records.size()-1).getDistance().getKilometers())+" km");
                                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_distancewalkingrunning));
                                                setVital(IDX_DISTANCE,vitalDataNew);
                                            } else {
                                                VitalDataNew vitalDataNew = new VitalDataNew();
                                                vitalDataNew.setName(getString(R.string.distance));
                                                vitalDataNew.setType("distance");
                                                vitalDataNew.setValue("--");
                                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_distancewalkingrunning));
                                                setVital(IDX_DISTANCE,vitalDataNew);
                                            }

                                            return null;
                                        }
                                );*/
               /* HealthConnectHelper.readBodyFatRecordJava(
                        healthConnectClient,
                        start,
                        end,
                        records -> {
                            if (!records.isEmpty()) {

                                Gson gson = new Gson();
                                String json = gson.toJson(records);
                                VitalDataNew vitalDataNew = new VitalDataNew();
                                vitalDataNew.setName(getString(R.string.body_fat));
                                vitalDataNew.setType("body_fat");
                                vitalDataNew.setValue(TextUtils.isEmpty(String.valueOf(records.get(records.size()-1).getPercentage().getValue())) ? "--" : String.valueOf(records.get(records.size()-1).getPercentage().getValue())+" bfp");
                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_bodyfatpercentage));
                                setVital(IDX_BODY_FAT,vitalDataNew);
                            } else {
                                VitalDataNew vitalDataNew = new VitalDataNew();
                                vitalDataNew.setName(getString(R.string.body_fat));
                                vitalDataNew.setType("body_fat");
                                vitalDataNew.setValue("--");
                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_bodyfatpercentage));
                                setVital(IDX_BODY_FAT,vitalDataNew);
                            }

                            return null;
                        }
                );*/
              /*  HealthConnectHelper.readRestingHeartRateRecordJava(
                        healthConnectClient,
                        start,
                        end,
                        records -> {
                            if (!records.isEmpty()) {
                                Gson gson = new Gson();
                                String json = gson.toJson(records);
                                VitalDataNew vitalDataNew = new VitalDataNew();
                                vitalDataNew.setName(getString(R.string.resting_heart_rate));
                                vitalDataNew.setType("resting_hr");
                                vitalDataNew.setValue(TextUtils.isEmpty(String.valueOf(records.get(records.size()-1).getBeatsPerMinute())) ? "--" : String.valueOf(records.get(records.size()-1).getBeatsPerMinute())+" bpm");
                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_heartrate));
                                setVital(IDX_RestingHR,vitalDataNew);
                            } else {
                                VitalDataNew vitalDataNew = new VitalDataNew();
                                vitalDataNew.setName(getString(R.string.resting_heart_rate));
                                vitalDataNew.setType("resting_hr");
                                vitalDataNew.setValue("--");
                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_heartrate));
                                setVital(IDX_RestingHR,vitalDataNew);
                            }

                            return null;
                        }
                );*/
             /*   HealthConnectHelper.readLeanBodyMassRecordJava(
                        healthConnectClient,
                        start,
                        end,
                        records -> {
                            if (!records.isEmpty()) {

                                Gson gson = new Gson();
                                String json = gson.toJson(records);
                                VitalDataNew vitalDataNew = new VitalDataNew();
                                vitalDataNew.setName(getString(R.string.lean_body_mass));
                                vitalDataNew.setType("lean_body_mass");
                                vitalDataNew.setValue(TextUtils.isEmpty(String.valueOf(records.get(records.size()-1).getMass().getPounds())) ? "--" : String.valueOf(records.get(records.size()-1).getMass().getPounds()) +" lbs");
                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_leanbodymass));
                                setVital(IDX_LeanBodyMass,vitalDataNew);
                            } else {
                                VitalDataNew vitalDataNew = new VitalDataNew();
                                vitalDataNew.setName(getString(R.string.lean_body_mass));
                                vitalDataNew.setType("lean_body_mass");
                                vitalDataNew.setValue("--");
                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_leanbodymass));
                                setVital(IDX_LeanBodyMass,vitalDataNew);

                            }

                            return null;
                        }
                );*/
                                 vitalDataNew = new VitalDataNew();
                                vitalDataNew.setName(getString(R.string.bmi));
                                vitalDataNew.setType("bmi");
                                vitalDataNew.setValue("--");
                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_bodymassindex));
                                setVital(IDX_BMI,vitalDataNew);
                                vitalDataNew = new VitalDataNew();
                                vitalDataNew.setName(getString(R.string.step));
                                vitalDataNew.setType("STEP");
                                vitalDataNew.setValue("--");
                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_stepcount));
                                setVital(IDX_STEP,vitalDataNew);
                                vitalDataNew = new VitalDataNew();
                                vitalDataNew.setName(getString(R.string.respiratory_rate));
                                vitalDataNew.setType("Respiratory_Rate");
                                vitalDataNew.setValue("--");
                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_respiratoryrate));
                                setVital(IDX_RESP_RATE,vitalDataNew);
                                vitalDataNew = new VitalDataNew();
                                vitalDataNew.setName(getString(R.string.heart_rate_variability_rms));
                                vitalDataNew.setType("HRV");
                                vitalDataNew.setValue("--");
                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_heartrate));
                                setVital(IDX_HRV,vitalDataNew);
                                vitalDataNew = new VitalDataNew();
                                vitalDataNew.setName(getString(R.string.vo2_max));
                                vitalDataNew.setType("VO2");
                                vitalDataNew.setValue("--");
                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.pulse_vc));
                                setVital(IDX_VO2,vitalDataNew);
                                vitalDataNew = new VitalDataNew();
                                vitalDataNew.setName(getString(R.string.exercise));
                                vitalDataNew.setType("Exercise");
                                vitalDataNew.setValue("--");
                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_exercisetime));
                                setVital(IDX_EXERCISE,vitalDataNew);
                                vitalDataNew = new VitalDataNew();
                                vitalDataNew.setName(getString(R.string.basal_metabolic_rate));
                                vitalDataNew.setType("BMR");
                                vitalDataNew.setValue("--");
                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_basalenergyburned));
                                setVital(IDX_BMR,vitalDataNew);
                                vitalDataNew = new VitalDataNew();
                                vitalDataNew.setName(getString(R.string.calories));
                                vitalDataNew.setType("CALORIES");
                                vitalDataNew.setValue("--");
                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_totalenergyburned));
                                setVital(IDX_CALORIES,vitalDataNew);
                                vitalDataNew = new VitalDataNew();
                                vitalDataNew.setName(getString(R.string.distance));
                                vitalDataNew.setType("distance");
                                vitalDataNew.setValue("--");
                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_distancewalkingrunning));
                                setVital(IDX_DISTANCE,vitalDataNew);
                                vitalDataNew = new VitalDataNew();
                                vitalDataNew.setName(getString(R.string.body_fat));
                                vitalDataNew.setType("body_fat");
                                vitalDataNew.setValue("--");
                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_bodyfatpercentage));
                                setVital(IDX_BODY_FAT,vitalDataNew);
                                vitalDataNew = new VitalDataNew();
                                vitalDataNew.setName(getString(R.string.resting_heart_rate));
                                vitalDataNew.setType("resting_hr");
                                vitalDataNew.setValue("--");
                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_heartrate));
                                setVital(IDX_RestingHR,vitalDataNew);
                                vitalDataNew = new VitalDataNew();
                                vitalDataNew.setName(getString(R.string.lean_body_mass));
                                vitalDataNew.setType("lean_body_mass");
                                vitalDataNew.setValue("--");
                                vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.ic_hc_leanbodymass));
                                setVital(IDX_LeanBodyMass,vitalDataNew);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        binding.progress.setVisibility(View.GONE);
                                        List<VitalDataNew> clinicVitalDataListVital = new ArrayList<>();
                                        List<VitalDataNew> clinicVitalDataListActivity = new ArrayList<>();
                                        List<VitalDataNew> clinicVitalDataListBodyMeasurement = new ArrayList<>();
                                        for (int i=0;i<clinicVitalDataList2.size();i++){
                                            if (i<=8){
                                                clinicVitalDataListVital.add(clinicVitalDataList2.get(i));
                                            }
                                            else if (i<14){
                                                clinicVitalDataListActivity.add(clinicVitalDataList2.get(i));
                                            }else {
                                                clinicVitalDataListBodyMeasurement.add(clinicVitalDataList2.get(i));
                                            }
                                        }

                                        binding.rvHealthConnectVital.setLayoutManager(new GridLayoutManager(context, 1, RecyclerView.VERTICAL, false));
                                        VitalHCRecycleViewAdapter vitalRecycleViewAdapter = new VitalHCRecycleViewAdapter(clinicVitalDataListVital, context, Navigation.findNavController(binding.getRoot()), getParentFragmentManager());
                                        binding.rvHealthConnectVital.setAdapter(vitalRecycleViewAdapter);
                                        binding.rvHealthConnectActivity.setLayoutManager(new GridLayoutManager(context, 1, RecyclerView.VERTICAL, false));
                                        VitalHCRecycleViewAdapter vitalRecycleViewAdapter2 = new VitalHCRecycleViewAdapter(clinicVitalDataListActivity, context, Navigation.findNavController(binding.getRoot()), getParentFragmentManager());
                                        binding.rvHealthConnectActivity.setAdapter(vitalRecycleViewAdapter2);
                                        binding.rvHealthConnectBodyMeasurement.setLayoutManager(new GridLayoutManager(context, 1, RecyclerView.VERTICAL, false));
                                        VitalHCRecycleViewAdapter vitalRecycleViewAdapter3 = new VitalHCRecycleViewAdapter(clinicVitalDataListBodyMeasurement, context, Navigation.findNavController(binding.getRoot()), getParentFragmentManager());
                                        binding.rvHealthConnectBodyMeasurement.setAdapter(vitalRecycleViewAdapter3);
                                        binding.textErrorHealthconnect.setVisibility(View.GONE);
                        /*syncAllVitals(context,
                                glucoseList,
                                bpList,
                                hrList,
                                spo2List,
                                rrList,
                                tempList,
                                heightList,
                                weightList);*/
                                    }
                                }, 1500);
                            }

                        } else {

                            binding.scrollViewHealthConnect.setVisibility(View.GONE);
                            binding.textErrorHealthconnect.setVisibility(View.VISIBLE);
                            binding.addRefreshBtn.setVisibility(View.GONE);
                            // Permission denied
                           /* emptyLayout.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);

                            txtMessage.setText(
                                    "Health Connect permission not granted."
                            );*/
                        }
                    });

    private void initVal() {
        device_value = new ArrayList<>();
       /* graphData = new GraphData();
        bo_dataVector = new ArrayList<VitalData>();
        glucose_dataVector = new ArrayList<VitalData>();
        weight_dataVector = new ArrayList<VitalData>();
        hr_dataVector = new ArrayList<VitalData>();
        bp_dataVector = new ArrayList<VitalDataBP>();*/
        isDeviceTablet = AppUtiltiy.isDeviceTablet(getActivity());
        /*BluetoothManager bluetoothManager = requireActivity().getSystemService(BluetoothManager.class);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

        if (!bluetoothAdapter.isEnabled()) {
            // Bluetooth is disabled
            // Request enabling Bluetooth through system settings
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                requestPermissions(
                        new String[]{Manifest.permission.BLUETOOTH_CONNECT,
                                Manifest.permission.BLUETOOTH_SCAN,},
                        REQUEST_ENABLE_BT);
            } else {
                // Request Bluetooth permission for pre-Android 12
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }*/

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Do nothing to disable back button
                Constant.istabselected="";
                backPress();
            }
        };

        // Add the callback to the activity's OnBackPressedDispatcher
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);


        binding.linClinicVital.setOnClickListener(v -> {
            binding.viewDevice.setVisibility(View.GONE);
            binding.viewOffice.setVisibility(View.VISIBLE);
          //  binding.scrollview.setVisibility(View.VISIBLE);
            binding.addDeviceBtn.setVisibility(View.VISIBLE);
            binding.relVitalOffice.setVisibility(View.VISIBLE);
            binding.viewGoogleFit.setVisibility(View.GONE);
            binding.constraintDevice.setVisibility(View.GONE);
            binding.scrollViewHealthConnect.setVisibility(View.GONE);
            binding.addRefreshBtn.setVisibility(View.GONE);
            binding.textOfficeVisit.setTextColor(ContextCompat.getColor(context,R.color.white_0_2));
            binding.textHealthConnect.setTextColor(ContextCompat.getColor(context,R.color.black_2_1));
            binding.textMedicalDevice.setTextColor(ContextCompat.getColor(context,R.color.black_2_1));
            binding.textErrorHealthconnect.setVisibility(View.GONE);
            if (binding.viewOffice.getVisibility() == View.VISIBLE) {
                Constant.istabselected = "1";
                Constant.istype = "";
            } else if (binding.viewDevice.getVisibility() == View.VISIBLE) {
                Constant.istabselected = "2";
                Constant.istype = "";
            } else {
                Constant.istabselected = "3";
                Constant.istype = "";
            }
        });
        binding.linDeviceVital.setOnClickListener(v -> {
            binding.viewDevice.setVisibility(View.VISIBLE);
            binding.viewOffice.setVisibility(View.GONE);
            binding.constraintDevice.setVisibility(View.VISIBLE);
            binding.scrollview.setVisibility(View.GONE);
            binding.relVitalOffice.setVisibility(View.GONE);
            binding.viewGoogleFit.setVisibility(View.GONE);
            binding.addDeviceBtn.setVisibility(View.GONE);
            binding.scrollViewHealthConnect.setVisibility(View.GONE);
            binding.addRefreshBtn.setVisibility(View.GONE);
            binding.textHealthConnect.setTextColor(ContextCompat.getColor(context,R.color.black_2_1));
            binding.textOfficeVisit.setTextColor(ContextCompat.getColor(context,R.color.black_2_1));
            binding.textMedicalDevice.setTextColor(ContextCompat.getColor(context,R.color.white_0_2));
            binding.textErrorHealthconnect.setVisibility(View.GONE);
            if (binding.viewOffice.getVisibility() == View.VISIBLE) {
                Constant.istabselected = "1";
                Constant.istype = "";
            } else if (binding.viewDevice.getVisibility() == View.VISIBLE) {
                Constant.istabselected = "2";
                Constant.istype = "";
            } else {
                Constant.istabselected = "3";
                Constant.istype = "";
            }
        });

       /* binding.linGoogleFitVital.setOnClickListener(v -> {
            binding.viewDevice.setVisibility(View.GONE);
            binding.viewGoogleFit.setVisibility(View.VISIBLE);
            binding.viewOffice.setVisibility(View.GONE);
            binding.constraintDevice.setVisibility(View.GONE);
            binding.scrollview.setVisibility(View.GONE);
            binding.relVitalOffice.setVisibility(View.GONE);
            binding.scrollViewHealthConnect.setVisibility(View.VISIBLE);
            binding.addRefreshBtn.setVisibility(View.VISIBLE);
            binding.addDeviceBtn.setVisibility(View.GONE);
            binding.textOfficeVisit.setTextColor(ContextCompat.getColor(context,R.color.black_2_1));
            binding.textHealthConnect.setTextColor(ContextCompat.getColor(context,R.color.white_0_2));
            binding.textMedicalDevice.setTextColor(ContextCompat.getColor(context,R.color.black_2_1));
            binding.textErrorHealthconnect.setVisibility(View.GONE);
            if (binding.viewGoogleFit.getVisibility() == View.VISIBLE) {
                Constant.istabselected = "3";
                Constant.istype = "";
            } else if (binding.viewOffice.getVisibility() == View.VISIBLE) {
                Constant.istabselected = "1";
                Constant.istype = "";
            } else {
                Constant.istabselected = "2";
                Constant.istype = "";
            }
            TimeRangeFilter last7Days = TimeRangeFilter.between(
                    Instant.now().minusSeconds(30 * 24 * 60 * 60),
                    Instant.now()
            );



            requestPermissions.launch(requiredPermissions);


        });*/


        binding.addRefreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* binding.linGoogleFitVital.performClick();*/
                binding.progress.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        syncAllVitals(context,
                                glucoseList,
                                bpList,
                                hrList,
                                spo2List,
                                rrList,
                                tempList,
                                heightList,
                                weightList);
                        binding.progress.setVisibility(View.GONE);
                    }
                }, 1500);
            }
        });

        if (TextUtils.isEmpty(Constant.istabselected)){
            binding.viewDevice.setVisibility(View.GONE);
            binding.viewOffice.setVisibility(View.VISIBLE);
          //  binding.constraintDevice.setVisibility(View.GONE);
        //    binding.scrollview.setVisibility(View.VISIBLE);
            binding.addDeviceBtn.setVisibility(View.VISIBLE);
            binding.relVitalOffice.setVisibility(View.VISIBLE);
            binding.viewGoogleFit.setVisibility(View.GONE);
            binding.constraintDevice.setVisibility(View.GONE);
            binding.scrollViewHealthConnect.setVisibility(View.GONE);
            binding.addRefreshBtn.setVisibility(View.GONE);
            binding.textMedicalDevice.setTextColor(ContextCompat.getColor(context,R.color.black_2_1));
            binding.textOfficeVisit.setTextColor(ContextCompat.getColor(context,R.color.white_0_2));
            binding.textHealthConnect.setTextColor(ContextCompat.getColor(context,R.color.black_2_1));
            binding.textErrorHealthconnect.setVisibility(View.GONE);
        }else if (Constant.istabselected.equals("1")){
            binding.viewDevice.setVisibility(View.GONE);
            binding.viewOffice.setVisibility(View.VISIBLE);
            //binding.constraintDevice.setVisibility(View.GONE);
          //  binding.scrollview.setVisibility(View.VISIBLE);
            binding.addDeviceBtn.setVisibility(View.VISIBLE);
            binding.relVitalOffice.setVisibility(View.VISIBLE);
            binding.viewGoogleFit.setVisibility(View.GONE);
            binding.constraintDevice.setVisibility(View.GONE);
            binding.scrollViewHealthConnect.setVisibility(View.GONE);
            binding.addRefreshBtn.setVisibility(View.GONE);
            binding.textMedicalDevice.setTextColor(ContextCompat.getColor(context,R.color.black_2_1));
            binding.textOfficeVisit.setTextColor(ContextCompat.getColor(context,R.color.white_0_2));
            binding.textHealthConnect.setTextColor(ContextCompat.getColor(context,R.color.black_2_1));
            binding.textErrorHealthconnect.setVisibility(View.GONE);
        }else if (istabselected.equals("2")){
            binding.viewDevice.setVisibility(View.VISIBLE);
            binding.viewOffice.setVisibility(View.GONE);
            binding.constraintDevice.setVisibility(View.VISIBLE);
            binding.scrollview.setVisibility(View.GONE);
            binding.relVitalOffice.setVisibility(View.GONE);
            binding.viewGoogleFit.setVisibility(View.GONE);
            binding.addDeviceBtn.setVisibility(View.GONE);
            binding.scrollViewHealthConnect.setVisibility(View.GONE);
            binding.addRefreshBtn.setVisibility(View.GONE);
            binding.textErrorHealthconnect.setVisibility(View.GONE);
            binding.textOfficeVisit.setTextColor(ContextCompat.getColor(context,R.color.black_2_1));
            binding.textMedicalDevice.setTextColor(ContextCompat.getColor(context,R.color.white_0_2));
            binding.textHealthConnect.setTextColor(ContextCompat.getColor(context,R.color.black_2_1));
        }
        else {
            binding.viewDevice.setVisibility(View.GONE);
            binding.viewGoogleFit.setVisibility(View.VISIBLE);
            binding.viewOffice.setVisibility(View.GONE);
            binding.constraintDevice.setVisibility(View.GONE);
            binding.scrollview.setVisibility(View.GONE);
            binding.relVitalOffice.setVisibility(View.GONE);
            binding.scrollViewHealthConnect.setVisibility(View.VISIBLE);
            binding.addRefreshBtn.setVisibility(View.VISIBLE);
            binding.addDeviceBtn.setVisibility(View.GONE);
            binding.textErrorHealthconnect.setVisibility(View.GONE);
            binding.textOfficeVisit.setTextColor(ContextCompat.getColor(context,R.color.black_2_1));
            binding.textHealthConnect.setTextColor(ContextCompat.getColor(context,R.color.white_0_2));
            binding.textMedicalDevice.setTextColor(ContextCompat.getColor(context,R.color.black_2_1));
        }

        initEvent();
    }



    private String getExerciseName(int type) {
        switch (type) {

            case ExerciseSessionRecord.EXERCISE_TYPE_WALKING:
                return "Walking";

            case ExerciseSessionRecord.EXERCISE_TYPE_RUNNING:
                return "Running";

            case ExerciseSessionRecord.EXERCISE_TYPE_BIKING:
                return "Biking";

            case ExerciseSessionRecord.EXERCISE_TYPE_BIKING_STATIONARY:
                return "Cycling";

            case ExerciseSessionRecord.EXERCISE_TYPE_YOGA:
                return "Yoga";

            case ExerciseSessionRecord.EXERCISE_TYPE_STRENGTH_TRAINING:
                return "Strength Training";

            default:
                return "Workout";
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }
    private void clickListner() {
        binding.toolBar.txtTittle.setText(getString(R.string.vitals));
        binding.toolBar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* if (getArguments() != null && getArguments().getBoolean(IS_FROM_HEALTH_RECORD)&& !getArguments().getBoolean(IShome)) {
                    if (getParentFragment() != null)
                        ((HomeFragment) getParentFragment()).openHealthRecordFragment();
                }else   if (getArguments() != null && getArguments().getBoolean(IS_FROM_HEALTH_RECORD)&& getArguments().getBoolean(IShome)) {
                    if (getParentFragment() != null)
                        ((HomeFragment) getParentFragment()).openMainActivity();
                }*/

                backPress();
            }
        });
        binding.toolBar.icImg2.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.file_vc));
        binding.toolBar.icImg2.setPadding(20,35,20,35);
        binding.toolBar.icImg2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // ((HomeFragment) getParentFragment()).openHomeVitalcheckFragment2();
                if (binding.viewOffice.getVisibility() == View.VISIBLE) {
                    Constant.istabselected = "1";
                }else if (binding.viewDevice.getVisibility() == View.VISIBLE) {
                    Constant.istabselected = "2";
                } else {
                    Constant.istabselected = "3";
                }
                Constant.istype="";
                Navigation.findNavController(binding.getRoot()).navigate(R.id.action_IOT_MainPage_Fragment_to_vitalgraph);
            }
        });
        binding.addDeviceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MedicalDisclaimerDialog.show(
                        requireActivity(),
                        "Device Information",
                        "CDOC Patient requires compatible external medical, wellness, or wearable devices for certain features and health measurements, including blood pressure monitors, glucometers, pulse oximeters, weight scales, and supported wearable devices.\n" +
                                "\n" +
                                "Health data displayed in the application is obtained from connected compatible devices. These device-dependent features do not function independently without the appropriate external hardware.\n" +
                                "\n" +
                                "Please ensure that a supported device is properly connected before using these features."
                        /*"Certain CDOC features require compatible external medical or wellness devices, such as Bluetooth blood pressure monitors, glucometers, pulse oximeters, weight scales, or supported wearable devices.\n" +
                                "\n" +
                                "These device-connected features do not function independently without the appropriate external hardware"*/

                        /*.\n" +
                                "\n" +
                                "CDoc does not automatically collect health data from external devices. Health data is imported only after you choose to connect a compatible device and grant permission."*/,
                        "Continue",
                        true,
                        () -> {
                            BluetoothManager bluetoothManager = requireActivity().getSystemService(BluetoothManager.class);
                            BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

                            if (!bluetoothAdapter.isEnabled()) {
                                // Bluetooth is disabled
                                // Request enabling Bluetooth through system settings
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                    requestPermissions(
                                            new String[]{Manifest.permission.BLUETOOTH_CONNECT,
                                                    Manifest.permission.BLUETOOTH_SCAN,},
                                            REQUEST_ENABLE_BT);
                                } else {
                                    // Request Bluetooth permission for pre-Android 12
                                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                                }
                            }else {
                                Constant.isvitalnot="1";
                                if (binding.viewOffice.getVisibility()==View.VISIBLE){
                                    Constant.istabselected="1";
                                }
                                else if (binding.viewDevice.getVisibility()==View.VISIBLE){
                                    Constant.istabselected="2";
                                }else {
                                    Constant.istabselected="3";
                                }
                                Navigation.findNavController(binding.getRoot()).navigate(R.id.action_IOT_MainPage_Fragment_to_IOTDeviceSetUpFragment);
                                //  ((HomeFragment) getParentFragment()).openVitalFragment();
                            }
                        },
                        ()->{

                        }
                );

                //((HomeFragment) getParentFragment()).openVitalFragment();
            }
        });

    }


    private void callApi() {
        /*Bundle args = getArguments();
        if (args != null) {*/
        clinicVitalDataList=new ArrayList<>();
            UserInfo userInfo = CDoctor2Application.getLoginInfo().getUserInfo();

            if (userInfo != null) {
                showProgress();
                ProfileApiManager ClinicVitalManager = new ProfileApiManager(new IResponseReceiver() {
                    @Override
                    public void onSuccess(Object data) {
                        hideProgress();
                        ResponseVital responseVital = (ResponseVital) data;
                        binding.swipeRefreshLayout.setRefreshing(false);
                        if (data != null && responseVital.getClinicVitaldata().size() > 0) {
                           /* binding.emptyLayout.setVisibility(View.GONE);
                            setList(responseVital.getClinicVitaldata());*/
                            ClinicVitaldata clinicVitaldata = responseVital.getClinicVitaldata().get(responseVital.getClinicVitaldata().size()-1);

                            VitalDataNew vitalDataNew=new VitalDataNew();

                            vitalDataNew.setName(getString(R.string.temperature_fever));
                            vitalDataNew.setType("Temp");
                            vitalDataNew.setValue(TextUtils.isEmpty(clinicVitaldata.getTemp())?"--":clinicVitaldata.getTemp());
                            vitalDataNew.setImage(ContextCompat.getDrawable(context,R.drawable.temperatureicon));
                            clinicVitalDataList.add(vitalDataNew);

                            binding.txtTemp.setText(TextUtils.isEmpty(clinicVitaldata.getTemp())?"--":clinicVitaldata.getTemp());
                            binding.relTemp.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (binding.viewOffice.getVisibility()==View.VISIBLE){
                                        Constant.istabselected="1";
                                        Constant.istype="Temp";
                                    }
                                    Navigation.findNavController(binding.getRoot()).navigate(R.id.action_IOT_MainPage_Fragment_to_vitalgraph);
                                }
                            });

                            vitalDataNew=new VitalDataNew();
                            vitalDataNew.setName(getString(R.string.bp));
                            vitalDataNew.setType("BP");
                            vitalDataNew.setValue(TextUtils.isEmpty(clinicVitaldata.getBP())?"--":clinicVitaldata.getBP());
                            vitalDataNew.setImage(ContextCompat.getDrawable(context,R.drawable.bp_vc));
                            clinicVitalDataList.add(vitalDataNew);

                            binding.txtBp.setText(TextUtils.isEmpty(clinicVitaldata.getBP())?"--":clinicVitaldata.getBP());
                            binding.cardBp.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (binding.viewOffice.getVisibility()==View.VISIBLE){
                                        Constant.istabselected="1";
                                        Constant.istype="BP";
                                    }
                                    Navigation.findNavController(binding.getRoot()).navigate(R.id.action_IOT_MainPage_Fragment_to_vitalgraph);
                                }
                            });

                            vitalDataNew=new VitalDataNew();
                            vitalDataNew.setName(getString(R.string.vitals_height));
                            vitalDataNew.setType("Height");
                            vitalDataNew.setValue(TextUtils.isEmpty(clinicVitaldata.getHeight())?"--":clinicVitaldata.getHeight());
                            vitalDataNew.setImage(ContextCompat.getDrawable(context,R.drawable.height_vc2));
                            clinicVitalDataList.add(vitalDataNew);
                            binding.txtHeight.setText(TextUtils.isEmpty(clinicVitaldata.getHeight())?"--":clinicVitaldata.getHeight());
                            binding.cardHeight.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (binding.viewOffice.getVisibility()==View.VISIBLE){
                                        Constant.istabselected="1";
                                        Constant.istype="Height";
                                    }
                                    Navigation.findNavController(binding.getRoot()).navigate(R.id.action_IOT_MainPage_Fragment_to_vitalgraph);
                                }
                            });

                            vitalDataNew=new VitalDataNew();
                            vitalDataNew.setName(getString(R.string.weight));
                            vitalDataNew.setType("Weight");
                            vitalDataNew.setValue(TextUtils.isEmpty(clinicVitaldata.getWeight())?"--":clinicVitaldata.getWeight());
                            vitalDataNew.setImage(ContextCompat.getDrawable(context,R.drawable.weight_vc));
                            clinicVitalDataList.add(vitalDataNew);

                            binding.txtWeight.setText(TextUtils.isEmpty(clinicVitaldata.getWeight())?"--":clinicVitaldata.getWeight());
                            binding.cardWeight.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (binding.viewOffice.getVisibility()==View.VISIBLE){
                                        Constant.istabselected="1";
                                        Constant.istype="Weight";
                                    }
                                    Navigation.findNavController(binding.getRoot()).navigate(R.id.action_IOT_MainPage_Fragment_to_vitalgraph);
                                }
                            });

                            if (clinicVitaldata.getBMI()==null||clinicVitaldata.getBMI().equals("null")){
                                if (!TextUtils.isEmpty(clinicVitaldata.getHeight()) && !TextUtils.isEmpty(clinicVitaldata.getWeight())){

                                    float bmi = (float) convertToKg(clinicVitaldata.getWeight()) / ((float) convertFtInToMeters(clinicVitaldata.getHeight()) * (float) convertFtInToMeters(clinicVitaldata.getHeight()));
                                    binding.txtBmi.setText(String.format("%.1f",bmi));
                                    dynamictextforBMI();
                                    binding.buttonBMI.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Bundle args = new Bundle();
                                            args.putString(KEY_HEIGHT, clinicVitaldata.getHeight());
                                            args.putString(KEY_WEIGHT, clinicVitaldata.getWeight());
                                            args.putString(KEY_BMI, String.format("%.1f",bmi));
                                            BMIGraphActivity bmiGraphActivity = new BMIGraphActivity();
                                            bmiGraphActivity.setArguments(args);
                                            bmiGraphActivity.show(getParentFragmentManager(), "BMIGraph Fragment");
                                        }
                                    });
                                    binding.cardBmi.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Bundle args = new Bundle();
                                            args.putString(KEY_HEIGHT, clinicVitaldata.getHeight());
                                            args.putString(KEY_WEIGHT, clinicVitaldata.getWeight());
                                            args.putString(KEY_BMI, String.format("%.1f",bmi));
                                            BMIGraphActivity bmiGraphActivity = new BMIGraphActivity();
                                            bmiGraphActivity.setArguments(args);
                                            bmiGraphActivity.show(getParentFragmentManager(), "BMIGraph Fragment");
                                        }
                                    });
                                }else {
                                    binding.txtBmi.setText("--");
                                    binding.textBMIvalue.setText("");
                                }
                            }else {
                                if (!TextUtils.isEmpty(clinicVitaldata.getHeight()) && !TextUtils.isEmpty(clinicVitaldata.getWeight())){

                                    float bmi = (float) convertToKg(clinicVitaldata.getWeight()) / ((float) convertFtInToMeters(clinicVitaldata.getHeight()) * (float) convertFtInToMeters(clinicVitaldata.getHeight()));
                                    binding.txtBmi.setText(clinicVitaldata.getBMI());
                                    dynamictextforBMI();
                                    binding.buttonBMI.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Bundle args = new Bundle();
                                            args.putString(KEY_HEIGHT, clinicVitaldata.getHeight());
                                            args.putString(KEY_WEIGHT, clinicVitaldata.getWeight());
                                            args.putString(KEY_BMI, clinicVitaldata.getBMI());
                                            BMIGraphActivity bmiGraphActivity = new BMIGraphActivity();
                                            bmiGraphActivity.setArguments(args);
                                            bmiGraphActivity.show(getParentFragmentManager(), "BMIGraph Fragment");
                                        }
                                    });
                                    binding.cardBmi.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Bundle args = new Bundle();
                                            args.putString(KEY_HEIGHT, clinicVitaldata.getHeight());
                                            args.putString(KEY_WEIGHT, clinicVitaldata.getWeight());
                                            args.putString(KEY_BMI, clinicVitaldata.getBMI());
                                            BMIGraphActivity bmiGraphActivity = new BMIGraphActivity();
                                            bmiGraphActivity.setArguments(args);
                                            bmiGraphActivity.show(getParentFragmentManager(), "BMIGraph Fragment");
                                        }
                                    });
                                }else {
                                    float bmi = Float.parseFloat(clinicVitaldata.getBMI());
                                    binding.txtBmi.setText(String.format("%.1f",bmi));
                                    binding.textBMIvalue.setText(String.format("%.1f",bmi));
                                }

                                dynamictextforBMI();


                            }
                            vitalDataNew=new VitalDataNew();
                            vitalDataNew.setName(getString(R.string.bmi));
                            vitalDataNew.setType("BMI");
                            vitalDataNew.setValue(TextUtils.isEmpty(binding.txtBmi.getText().toString().trim())?"--":binding.txtBmi.getText().toString().trim());
                            vitalDataNew.setImage(ContextCompat.getDrawable(context,R.drawable.bmi_vc));
                            clinicVitalDataList.add(vitalDataNew);



                            vitalDataNew=new VitalDataNew();
                            vitalDataNew.setName(getString(R.string.pulse));
                            vitalDataNew.setType("hr");
                            vitalDataNew.setValue(TextUtils.isEmpty(clinicVitaldata.getPulse())?"--":clinicVitaldata.getPulse());
                            vitalDataNew.setImage(ContextCompat.getDrawable(context,R.drawable.pulse_vc));
                            clinicVitalDataList.add(vitalDataNew);
                            binding.txtPulse.setText(TextUtils.isEmpty(clinicVitaldata.getPulse())?"--":clinicVitaldata.getPulse());
                            binding.cardPulse.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (binding.viewOffice.getVisibility()==View.VISIBLE){
                                        Constant.istabselected="1";
                                        Constant.istype="hr";
                                    }
                                    Navigation.findNavController(binding.getRoot()).navigate(R.id.action_IOT_MainPage_Fragment_to_vitalgraph);
                                }
                            });


                            vitalDataNew=new VitalDataNew();
                            vitalDataNew.setName(getString(R.string.glucose));
                            vitalDataNew.setType("Glucose");
                            vitalDataNew.setValue(TextUtils.isEmpty(clinicVitaldata.getGlucose())?"--":clinicVitaldata.getGlucose());
                            vitalDataNew.setImage(ContextCompat.getDrawable(context,R.drawable.glucometer_vc));
                            clinicVitalDataList.add(vitalDataNew);

                            binding.txtGlucose.setText(TextUtils.isEmpty(clinicVitaldata.getGlucose())?"--":clinicVitaldata.getGlucose());
                            binding.cardGlucose.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (binding.viewOffice.getVisibility()==View.VISIBLE){
                                        Constant.istabselected="1";
                                        Constant.istype="Glucose";
                                    }
                                    Navigation.findNavController(binding.getRoot()).navigate(R.id.action_IOT_MainPage_Fragment_to_vitalgraph);
                                }
                            });

                            vitalDataNew=new VitalDataNew();
                            vitalDataNew.setName(getString(R.string.peak_flow_2));
                            vitalDataNew.setType("Peak_Flow");
                            vitalDataNew.setValue(TextUtils.isEmpty(clinicVitaldata.getPeak_Flow())?"--":clinicVitaldata.getPeak_Flow());
                            vitalDataNew.setImage(ContextCompat.getDrawable(context,R.drawable.peak_flow_vc2));
                            clinicVitalDataList.add(vitalDataNew);

                            binding.txtPf.setText(TextUtils.isEmpty(clinicVitaldata.getPeak_Flow())?"--":clinicVitaldata.getPeak_Flow());
                            binding.cardPF.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (binding.viewOffice.getVisibility()==View.VISIBLE){
                                        Constant.istabselected="1";
                                        Constant.istype="Peak_Flow";
                                    }
                                    Navigation.findNavController(binding.getRoot()).navigate(R.id.action_IOT_MainPage_Fragment_to_vitalgraph);
                                }
                            });

                            vitalDataNew=new VitalDataNew();
                            vitalDataNew.setName(getString(R.string.hc));
                            vitalDataNew.setType("HC");
                            vitalDataNew.setValue(TextUtils.isEmpty(clinicVitaldata.getHC())?"--":clinicVitaldata.getHC());
                            vitalDataNew.setImage(ContextCompat.getDrawable(context,R.drawable.hc_vc2));
                            clinicVitalDataList.add(vitalDataNew);
                            binding.txtHc.setText(TextUtils.isEmpty(clinicVitaldata.getHC())?"--":clinicVitaldata.getHC());
                            binding.cardHc.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (binding.viewOffice.getVisibility()==View.VISIBLE){
                                        Constant.istabselected="1";
                                        Constant.istype="HC";
                                    }
                                    Navigation.findNavController(binding.getRoot()).navigate(R.id.action_IOT_MainPage_Fragment_to_vitalgraph);
                                }
                            });

                            vitalDataNew=new VitalDataNew();
                            vitalDataNew.setName(getString(R.string.hgb));
                            vitalDataNew.setType("HGB");
                            vitalDataNew.setValue(TextUtils.isEmpty(clinicVitaldata.getHGB())?"--":clinicVitaldata.getHGB());
                            vitalDataNew.setImage(ContextCompat.getDrawable(context,R.drawable.hgb_vc));
                            clinicVitalDataList.add(vitalDataNew);

                            binding.txtPf.setText(TextUtils.isEmpty(clinicVitaldata.getHGB())?"--":clinicVitaldata.getHGB());
                            binding.cardHgb.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (binding.viewOffice.getVisibility()==View.VISIBLE){
                                        Constant.istabselected="1";
                                        Constant.istype="HGB";
                                    }
                                    Navigation.findNavController(binding.getRoot()).navigate(R.id.action_IOT_MainPage_Fragment_to_vitalgraph);
                                }
                            });

                            binding.rvVital.setLayoutManager(new GridLayoutManager(context,2, RecyclerView.VERTICAL,false));
                            VitalRecycleViewAdapter vitalRecycleViewAdapter = new VitalRecycleViewAdapter(clinicVitalDataList, context,Navigation.findNavController(binding.getRoot()),getParentFragmentManager());
                            binding.rvVital.setAdapter(vitalRecycleViewAdapter);
                            binding.txtDate.setText(DateUtil.formatedDate(clinicVitaldata.getVitalDate(), SERVER_DATE_FORMAT, DATE_FORMAT2));
                        } else {
                            binding.txtTemp.setText("--");
                            binding.txtHeight.setText("--");
                            binding.txtWeight.setText("--");
                            binding.txtHc.setText("--");
                            binding.txtBmi.setText("--");
                            binding.txtBp.setText("--");
                            binding.txtPulse.setText("--");
                            binding.txtHgb.setText("--");
                            binding.txtPf.setText("--");
                            binding.txtGlucose.setText("--");
                            binding.txtDate.setText("");

                            VitalDataNew vitalDataNew = new VitalDataNew();
                            vitalDataNew.setName(getString(R.string.temperature_fever));
                            vitalDataNew.setValue("--");
                            vitalDataNew.setType("Temp");
                            vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.temperatureicon));
                            clinicVitalDataList.add(vitalDataNew);

                            vitalDataNew = new VitalDataNew();
                            vitalDataNew.setName(getString(R.string.bp));
                            vitalDataNew.setValue("--");
                            vitalDataNew.setType("BP");
                            vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.bp_vc));
                            clinicVitalDataList.add(vitalDataNew);

                            vitalDataNew = new VitalDataNew();
                            vitalDataNew.setName(getString(R.string.vitals_height));
                            vitalDataNew.setValue("--");
                            vitalDataNew.setType("Height");
                            vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.height_vc2));
                            clinicVitalDataList.add(vitalDataNew);

                            vitalDataNew = new VitalDataNew();
                            vitalDataNew.setName(getString(R.string.weight));
                            vitalDataNew.setValue("--");
                            vitalDataNew.setType("Weight");
                            vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.weight_vc));
                            clinicVitalDataList.add(vitalDataNew);

                            vitalDataNew = new VitalDataNew();
                            vitalDataNew.setName(getString(R.string.bmi));
                            vitalDataNew.setValue("--");
                            vitalDataNew.setType("BMI");
                            vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.bmi_vc));
                            clinicVitalDataList.add(vitalDataNew);

                            vitalDataNew = new VitalDataNew();
                            vitalDataNew.setName(getString(R.string.pulse));
                            vitalDataNew.setValue("--");
                            vitalDataNew.setType("hr");
                            vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.pulse_vc));
                            clinicVitalDataList.add(vitalDataNew);

                            vitalDataNew = new VitalDataNew();
                            vitalDataNew.setName(getString(R.string.glucose));
                            vitalDataNew.setValue("--");
                            vitalDataNew.setType("Glucose");
                            vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.glucometer_vc));
                            clinicVitalDataList.add(vitalDataNew);

                            vitalDataNew = new VitalDataNew();
                            vitalDataNew.setName(getString(R.string.peak_flow_2));
                            vitalDataNew.setValue("--");
                            vitalDataNew.setType("Peak_Flow");
                            vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.peak_flow_vc2));
                            clinicVitalDataList.add(vitalDataNew);

                            vitalDataNew = new VitalDataNew();
                            vitalDataNew.setName(getString(R.string.hc));
                            vitalDataNew.setValue("--");
                            vitalDataNew.setType("HC");
                            vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.hc_vc2));
                            clinicVitalDataList.add(vitalDataNew);

                            vitalDataNew = new VitalDataNew();
                            vitalDataNew.setName(getString(R.string.hgb));
                            vitalDataNew.setValue("--");
                            vitalDataNew.setType("HGB");
                            vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.hgb_vc));
                            clinicVitalDataList.add(vitalDataNew);



                            binding.rvVital.setLayoutManager(new GridLayoutManager(context,2, RecyclerView.VERTICAL,false));
                            VitalRecycleViewAdapter vitalRecycleViewAdapter = new VitalRecycleViewAdapter(clinicVitalDataList, context,Navigation.findNavController(binding.getRoot()),getParentFragmentManager());
                            binding.rvVital.setAdapter(vitalRecycleViewAdapter);
                            //binding.emptyLayout.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull String errorResponse) {
                        hideProgress();
                        binding.swipeRefreshLayout.setRefreshing(false);
                        //binding.emptyLayout.setVisibility(View.VISIBLE);
                        binding.txtTemp.setText("--");
                        binding.txtBp.setText("--");
                        binding.txtHeight.setText("--");
                        binding.txtWeight.setText("--");
                        binding.txtBmi.setText("--");
                        binding.txtPulse.setText("--");
                        binding.txtGlucose.setText("--");
                        binding.txtPf.setText("--");
                        binding.txtHc.setText("--");
                        binding.txtHgb.setText("--");

                        VitalDataNew vitalDataNew = new VitalDataNew();
                        vitalDataNew.setName(getString(R.string.temperature_fever));
                        vitalDataNew.setValue("--");
                        vitalDataNew.setType("Temp");
                        vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.temperatureicon));
                        clinicVitalDataList.add(vitalDataNew);

                        vitalDataNew = new VitalDataNew();
                        vitalDataNew.setName(getString(R.string.bp));
                        vitalDataNew.setValue("--");
                        vitalDataNew.setType("BP");
                        vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.bp_vc));
                        clinicVitalDataList.add(vitalDataNew);

                        vitalDataNew = new VitalDataNew();
                        vitalDataNew.setName(getString(R.string.vitals_height));
                        vitalDataNew.setValue("--");
                        vitalDataNew.setType("Height");
                        vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.height_vc2));
                        clinicVitalDataList.add(vitalDataNew);

                        vitalDataNew = new VitalDataNew();
                        vitalDataNew.setName(getString(R.string.weight));
                        vitalDataNew.setValue("--");
                        vitalDataNew.setType("Weight");
                        vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.weight_vc));
                        clinicVitalDataList.add(vitalDataNew);

                        vitalDataNew = new VitalDataNew();
                        vitalDataNew.setName(getString(R.string.bmi));
                        vitalDataNew.setValue("--");
                        vitalDataNew.setType("BMI");
                        vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.bmi_vc));
                        clinicVitalDataList.add(vitalDataNew);

                        vitalDataNew = new VitalDataNew();
                        vitalDataNew.setName(getString(R.string.pulse));
                        vitalDataNew.setValue("--");
                        vitalDataNew.setType("hr");
                        vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.pulse_vc));
                        clinicVitalDataList.add(vitalDataNew);

                        vitalDataNew = new VitalDataNew();
                        vitalDataNew.setName(getString(R.string.glucose));
                        vitalDataNew.setValue("--");
                        vitalDataNew.setType("Glucose");
                        vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.glucometer_vc));
                        clinicVitalDataList.add(vitalDataNew);

                        vitalDataNew = new VitalDataNew();
                        vitalDataNew.setName(getString(R.string.peak_flow_2));
                        vitalDataNew.setValue("--");
                        vitalDataNew.setType("Peak_Flow");
                        vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.peak_flow_vc2));
                        clinicVitalDataList.add(vitalDataNew);

                        vitalDataNew = new VitalDataNew();
                        vitalDataNew.setName(getString(R.string.hc));
                        vitalDataNew.setValue("--");
                        vitalDataNew.setType("HC");
                        vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.hc_vc2));
                        clinicVitalDataList.add(vitalDataNew);

                        vitalDataNew = new VitalDataNew();
                        vitalDataNew.setName(getString(R.string.hgb));
                        vitalDataNew.setValue("--");
                        vitalDataNew.setType("HGB");
                        vitalDataNew.setImage(ContextCompat.getDrawable(context, R.drawable.hgb_vc));
                        clinicVitalDataList.add(vitalDataNew);

                        binding.rvVital.setLayoutManager(new GridLayoutManager(context, 2, RecyclerView.VERTICAL, false));
                        VitalRecycleViewAdapter vitalRecycleViewAdapter = new VitalRecycleViewAdapter(clinicVitalDataList, context, Navigation.findNavController(binding.getRoot()), getParentFragmentManager());
                        binding.rvVital.setAdapter(vitalRecycleViewAdapter);
                    }
                }, context);
                ClinicVitalManager.getClinicVitalList(userInfo.getEmail());

            }

        //}
    }




    @Override
    public void onResume() {
        super.onResume();
        callApi();
    }
    /*  @Override
    public void onMyResume() {
        callApi();
    }*/

    @Override
    public void refreshFragment(boolean isRefresh) {
        super.refreshFragment(isRefresh);
    }


   /* @Override
    public void onMyStop() {

    }*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constant.isvitalnot="1";
                if (binding.viewOffice.getVisibility()==View.VISIBLE){
                    Constant.istabselected="1";
                }else {
                    Constant.istabselected="2";
                }
                Navigation.findNavController(binding.getRoot()).navigate(R.id.action_IOT_MainPage_Fragment_to_IOTDeviceSetUpFragment);
                //((HomeFragment) getParentFragment()).openVitalFragment();
            } else {
                toastShortInfo(getString(R.string.no_permission_bluetooth));
            }
        }

    }

    public void backPress() {

        Constant.isvitalnot="";
        Constant.isvitalrecord="";
        Constant.ishomefragment="MainFragment";
        if ((FragmentMainActivity) getActivity() != null)
            ((FragmentMainActivity) getActivity()).setHomeNavigation();
    }

    public void dynamictextforBMI(){
        SpannableStringBuilder spannableBuilder = new SpannableStringBuilder();
        float bmiValue;
        try {
            bmiValue = Float.parseFloat(binding.txtBmi.getText().toString());
        } catch (NumberFormatException e) {
            bmiValue = 0.0f; // Default value if parsing fails
        }
        if(bmiValue<18.5){
            spannableBuilder.append(getString(R.string.bmi) +String.format(" = %.1f kg/m² (", bmiValue));
            SpannableString greenText = new SpannableString(getString(R.string.under_weight));
            greenText.setSpan(new ForegroundColorSpan(Color.parseColor("#34c85a")), 0, greenText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableBuilder.append(greenText);
            spannableBuilder.append(")");
        }else if (bmiValue>=18.5 && bmiValue<=25){
            spannableBuilder.append(getString(R.string.bmi) +String.format(" = %.1f kg/m² (", bmiValue));
            SpannableString greenText = new SpannableString(getString(R.string.normal_weight));
            greenText.setSpan(new ForegroundColorSpan(Color.parseColor("#ffcc00")), 0, greenText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableBuilder.append(greenText);
            spannableBuilder.append(")");
        }else if (bmiValue>25 && bmiValue<=30){
            spannableBuilder.append(getString(R.string.bmi) + String.format(" = %.1f kg/m² (", bmiValue));
            SpannableString greenText = new SpannableString(getString(R.string.overweight));
            greenText.setSpan(new ForegroundColorSpan(Color.parseColor("#ff9501")), 0, greenText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableBuilder.append(greenText);
            spannableBuilder.append(")");
        }else {
            spannableBuilder.append(getString(R.string.bmi) + String.format(" = %.1f kg/m² (", bmiValue));
            SpannableString greenText = new SpannableString(getString(R.string.obesity));
            greenText.setSpan(new ForegroundColorSpan(Color.parseColor("#ff3b2f")), 0, greenText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableBuilder.append(greenText);
            spannableBuilder.append(")");
        }


        binding.textBMIvalue.setText(spannableBuilder);

    }

    public static double convertToKg(String weightString) {
        // Convert ounces to pounds
        if (weightString.contains("lbs") && weightString.contains("oz")){

            String[] parts = weightString.split(" ");
            double pounds = Double.parseDouble(parts[0].replace("lbs","")); // First part is pounds
            double ounces = Double.parseDouble(parts[1].replace("oz","")); // Third part is ounces
            double totalPounds = pounds + (ounces / 16);

            // Convert pounds to kilograms
            return totalPounds * 0.453592;
        }else {

            String parts = weightString.replace("lbs","");
            double pounds = Double.parseDouble(parts); // First part is pounds

            // Convert pounds to kilograms
            return pounds * 0.453592;
        }
    }
    public static double convertFtInToMeters(String heightFtIn) {
        // Split the string by space
        if (heightFtIn.contains("ft")){
            String[] parts = heightFtIn.split(" ");

            // Extract feet and inches
            int feet = Integer.parseInt(parts[0].replace("ft","")); // First part is feet
            int inches = Integer.parseInt(parts[1].replace("in","")); // Third part is inches

            // Convert feet to meters and inches to meters
            double metersFromFeet = feet * 0.3048;
            double metersFromInches = inches * 0.0254;

            // Return total height in meters
            return metersFromFeet + metersFromInches;
        }else {
            String parts = heightFtIn.replace("in","");

            // Extract feet and inches
            int feet = Integer.parseInt(parts); // First part is feet

            // Convert feet to meters and inches to meters
            double metersFromFeet = feet * 0.3048;

            // Return total height in meters
            return metersFromFeet;
        }

    }

    public void initEvent() {
        showProgressBar();
        getAndSetDeviceVector();
        binding.rpmAddDeviceBtn.setOnClickListener(v -> {
            addDevice();
        });
        CDoctor2Application.getLoginInfo().getUserInfo().getIoT_devices_obs().observe(getViewLifecycleOwner(), ioT_devices -> {
            if (ioT_devices != null && ioT_devices.size() != 0) {
                if (this.ioT_devices == null) {
                    this.ioT_devices = ioT_devices;
                    showHideNoDeviceView(false);
                    getChildFragmentManager().beginTransaction()
                            .replace(R.id.pager, new IOTDeviceListFragment()).commit();
                }

            } else showHideNoDeviceView(true);
        });
        CDoctor2Application.getLoginInfo().getUserInfo().getIoT_devices_error().observe(getViewLifecycleOwner(), ioT_devices -> {
            showHideNoDeviceView(true);
        });


    }


    void showProgressBar() {
        binding.progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPause() {
        super.onPause();
        //disposable.dispose();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.unbind();
    }
    /**
     * @param showNoDeviceView true: show no device view
     *                         false : hide no device view
     */
    void showHideNoDeviceView(boolean showNoDeviceView) {
        binding.progress.setVisibility(View.GONE);
        if (showNoDeviceView) {
            binding.pager.setVisibility(View.GONE);
            binding.textInform.setVisibility(View.GONE);
            binding.topView.setVisibility(View.GONE);
            binding.noDeviceView.setVisibility(View.VISIBLE);
        } else {
            binding.pager.setVisibility(View.VISIBLE);
            binding.textInform.setVisibility(View.VISIBLE);
            binding.topView.setVisibility(View.VISIBLE);
            binding.noDeviceView.setVisibility(View.GONE);
        }
        binding.addDeviceBtns.setOnClickListener(v -> addDevice());

    }
    /**
     * add new device
     */
    void addDevice() {
        /*Navigation.findNavController(binding.getRoot()).navigate(R.id.action_IOT_MainPage_Fragment_to_IOTDeviceSetUpFragment);*/

        MedicalDisclaimerDialog.show(
                requireActivity(),
                "Device Information",
                "CDOC requires compatible external medical, wellness, or wearable devices for certain features and health measurements, including blood pressure monitors, glucometers, pulse oximeters, weight scales, and supported wearable devices.\n" +
                        "\n" +
                        "Health data displayed in the application is obtained from connected compatible devices. These device-dependent features do not function independently without the appropriate external hardware.\n" +
                        "\n" +
                        "Please ensure that a supported device is properly connected before using these features."
                        /*"Certain CDOC features require compatible external medical or wellness devices, such as Bluetooth blood pressure monitors, glucometers, pulse oximeters, weight scales, or supported wearable devices.\n" +
                                "\n" +
                                "These device-connected features do not function independently without the appropriate external hardware"*/

                        /*.\n" +
                                "\n" +
                                "CDoc does not automatically collect health data from external devices. Health data is imported only after you choose to connect a compatible device and grant permission."*/,
                "Continue",
                true,
                () -> {
                    BluetoothManager bluetoothManager = requireActivity().getSystemService(BluetoothManager.class);
                    BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

                    if (!bluetoothAdapter.isEnabled()) {
                        // Bluetooth is disabled
                        // Request enabling Bluetooth through system settings
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            requestPermissions(
                                    new String[]{Manifest.permission.BLUETOOTH_CONNECT,
                                            Manifest.permission.BLUETOOTH_SCAN,},
                                    REQUEST_ENABLE_BT);
                        } else {
                            // Request Bluetooth permission for pre-Android 12
                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                        }
                    }else {
                        Constant.isvitalnot="1";
                        if (binding.viewOffice.getVisibility()==View.VISIBLE){
                            Constant.istabselected="1";
                        }
                        else if (binding.viewDevice.getVisibility()==View.VISIBLE){
                            Constant.istabselected="2";
                        }else {
                            Constant.istabselected="3";
                        }
                        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_IOT_MainPage_Fragment_to_IOTDeviceSetUpFragment);
                        // ((HomeFragment) getParentFragment()).openVitalFragment();
                    }
                },
                ()->{

                }
        );

    }

    public void onRefresh() {
        getAndSetDeviceVector();
    }

    /*public void showMainVitalScreen() {
        binding.linGoogleFitVital.performClick();
    }*/

    private void setVital(int index, VitalDataNew vital) {
        clinicVitalDataList2.set(index, vital);
    }


    private void syncAllVitals(Context context,
                               List<BloodGlucoseRecord> glucoseList,
                               List<BloodPressureRecord> bpList,
                               List<HeartRateRecord> hrList,
                               List<OxygenSaturationRecord> spo2List,
                               List<RespiratoryRateRecord> rrList,
                               List<BodyTemperatureRecord> tempList,
                               List<HeightRecord> heightList,
                               List<WeightRecord> weightList) {

        /*SharedPreferences pref = context.getSharedPreferences("health_sync", MODE_PRIVATE);*/
        /*String deviceId = getDeviceId(context);*/
        List<ReqVitalData> apiList = new ArrayList<>();

        if (!glucoseList.isEmpty()) {
            BloodGlucoseRecord r = glucoseList.get(glucoseList.size() - 1);
            long ts = r.getMetadata().getLastModifiedTime().getEpochSecond();

          /* if (ts > pref.getLong(VitalSyncKeys.GLUCOSE, 0)) {
               apiList.add(makeVital("bloodGlucose",
                       String.valueOf(Math.round(
                               (float) r.getLevel().getMilligramsPerDeciliter())), ts));
               pref.edit().putLong(VitalSyncKeys.GLUCOSE, ts).apply();
           }*/
            apiList.add(makeVital("bloodGlucose",
                    String.valueOf(Math.round(
                            (float) r.getLevel().getMilligramsPerDeciliter())), ts));
        }


        syncBpAndHrMerged(bpList, hrList, /*pref,*/ apiList);

        if (!spo2List.isEmpty()) {
            OxygenSaturationRecord r = spo2List.get(spo2List.size() - 1);
            long ts = r.getMetadata().getLastModifiedTime().getEpochSecond();

           /*if (ts > pref.getLong(VitalSyncKeys.SPO2, 0)) {
               apiList.add(makeVital("oxygenSaturation",
                       String.valueOf(r.getPercentage().getValue()), ts));
               pref.edit().putLong(VitalSyncKeys.SPO2, ts).apply();
           }*/
            apiList.add(makeVital("oxygenSaturation",
                    String.valueOf(r.getPercentage().getValue()), ts));
        }

        if (!rrList.isEmpty()) {
            RespiratoryRateRecord r = rrList.get(rrList.size() - 1);
            long ts = r.getMetadata().getLastModifiedTime().getEpochSecond();

          /* if (ts > pref.getLong(VitalSyncKeys.RR, 0)) {
               apiList.add(makeVital("respiratoryRate",
                       String.valueOf(r.getRate()), ts));
               pref.edit().putLong(VitalSyncKeys.RR, ts).apply();
           }*/
            apiList.add(makeVital("respiratoryRate",
                    String.valueOf(r.getRate()), ts));
        }

        if (!tempList.isEmpty()) {
            BodyTemperatureRecord r = tempList.get(tempList.size() - 1);
            long ts = r.getMetadata().getLastModifiedTime().getEpochSecond();

         /*  if (ts > pref.getLong(VitalSyncKeys.TEMP, 0)) {
               apiList.add(makeVital("bodyTemperature",
                       String.valueOf(r.getTemperature().getCelsius()), ts));
               pref.edit().putLong(VitalSyncKeys.TEMP, ts).apply();
           }*/
            apiList.add(makeVital("bodyTemperature",
                    String.valueOf(r.getTemperature().getCelsius()), ts));
        }

        if (!heightList.isEmpty()) {
            HeightRecord r = heightList.get(heightList.size() - 1);
            long ts = r.getMetadata().getLastModifiedTime().getEpochSecond();

        /*   if (ts > pref.getLong(VitalSyncKeys.HEIGHT, 0)) {
               apiList.add(makeVital("height",
                       String.valueOf(r.getHeight().getMeters()), ts));
               pref.edit().putLong(VitalSyncKeys.HEIGHT, ts).apply();
           }*/
            apiList.add(makeVital("height",
                    String.valueOf(r.getHeight().getMeters()), ts));
        }

        if (!weightList.isEmpty()) {
            WeightRecord r = weightList.get(weightList.size() - 1);
            long ts = r.getMetadata().getLastModifiedTime().getEpochSecond();

         /*  if (ts > pref.getLong(VitalSyncKeys.WEIGHT, 0)) {
               apiList.add(makeVital("bodyMass",
                       String.valueOf(r.getWeight().getKilograms()), ts));
               pref.edit().putLong(VitalSyncKeys.WEIGHT, ts).apply();
           }*/
            apiList.add(makeVital("bodyMass",
                    String.valueOf(r.getWeight().getKilograms()), ts));

            double h = heightList.isEmpty() ? 0 :
                    heightList.get(heightList.size() - 1).getHeight().getMeters();

          /* if (h > 0 && ts > pref.getLong(VitalSyncKeys.BMI, 0)) {
               double bmi = r.getWeight().getKilograms() / (h * h);
               apiList.add(makeVital("bodyMassIndex", String.valueOf(bmi), ts));
               pref.edit().putLong(VitalSyncKeys.BMI, ts).apply();
           }*/
            if (h > 0){
                double bmi = r.getWeight().getKilograms() / (h * h);
                apiList.add(makeVital("bodyMassIndex", String.format("%.1f",bmi), ts));
            }

        }

     /*  if (!apiList.isEmpty()) {
           sendVitalsBatch(context, apiList);
       } else {
           Log.e("SYNC", "No new vitals to upload");
           toastLongInfo("Vitals synced successfully");
       }*/
        sendVitalsBatch(context, apiList);
    }



    private void syncBpAndHrMerged(
            List<BloodPressureRecord> bpList,
            List<HeartRateRecord> hrList,
            /* SharedPreferences pref,*/
            List<ReqVitalData> apiList
    ) {
        final int TIME_WINDOW_SECONDS = 5;

        BloodPressureRecord bp = null;
        HeartRateRecord hr = null;

        if (bpList != null && !bpList.isEmpty()) {
            bp = bpList.get(bpList.size() - 1);
        }

        if (hrList != null && !hrList.isEmpty()
                && !hrList.get(hrList.size() - 1).getSamples().isEmpty()) {
            hr = hrList.get(hrList.size() - 1);
        }

        Long bpTs = bp != null
                ? bp.getMetadata().getLastModifiedTime().getEpochSecond()
                : null;

        Long hrTs = hr != null
                ? hr.getMetadata().getLastModifiedTime().getEpochSecond()
                : null;

        // long lastSynced = pref.getLong(VitalSyncKeys.BP_HR, 0);

        String sys = "", dia = "", heartRate = "";
        long measureTime = 0;

        // ---------- CASE 1: BP + HR ----------
        if (bpTs != null && hrTs != null
                && Math.abs(bpTs - hrTs) <= TIME_WINDOW_SECONDS
            /*&& Math.max(bpTs, hrTs) > lastSynced*/) {

           /* sys = String.valueOf(Integer.parseInt(String.valueOf(bp.getSystolic().getMillimetersOfMercury())));
            dia = String.valueOf(Integer.parseInt(String.valueOf(bp.getDiastolic().getMillimetersOfMercury())));*/
            int sysStr  = (int) bp.getSystolic().getMillimetersOfMercury();
            int diaStr = (int) bp.getDiastolic().getMillimetersOfMercury();
            sys = String.valueOf(sysStr);
            dia = String.valueOf(diaStr);
            heartRate = String.valueOf(hr.getSamples().get(0).getBeatsPerMinute());
            measureTime = Math.max(bpTs, hrTs);
        }
        // ---------- CASE 2: Only BP ----------
        else if (bpTs != null /*&& bpTs > lastSynced*/) {
            int sysStr  = (int) bp.getSystolic().getMillimetersOfMercury();
            int diaStr = (int) bp.getDiastolic().getMillimetersOfMercury();
            sys = String.valueOf(sysStr);
            dia = String.valueOf(diaStr);
            heartRate = "";
            measureTime = bpTs;
        }
        // ---------- CASE 3: Only HR ----------
        else if (hrTs != null /*&& hrTs > lastSynced*/) {

            sys = "";
            dia = "";
            heartRate = String.valueOf(hr.getSamples().get(0).getBeatsPerMinute());
            measureTime = hrTs;
        } else {
            return; // nothing new to sync
        }

        String value = sys + ":" + dia + ":" + heartRate;

        apiList.add(makeVital(
                "BP",
                value,
                measureTime
        ));

        /* pref.edit().putLong(VitalSyncKeys.BP_HR, measureTime).apply();*/
    }

    public class VitalSyncKeys {
        public static final String GLUCOSE = "last_sync_glucose";
        public static final String BP = "last_sync_bp";
        public static final String HR = "last_sync_hr";
        public static final String BP_HR = "last_sync_bp_hr";
        public static final String SPO2 = "last_sync_spo2";
        public static final String RR = "last_sync_rr";
        public static final String TEMP = "last_sync_temp";
        public static final String HEIGHT = "last_sync_height";
        public static final String WEIGHT = "last_sync_weight";
        public static final String BMI = "last_sync_bmi";
    }

    private ReqVitalData makeVital(String type, String value, long ts) {
        ReqVitalData v = new ReqVitalData();
        v.setmType(type);
        v.setmValue(value);
        v.setmTimestamp(String.valueOf(ts));
        return v;
    }


    private void sendVitalsBatch(Context context, List<ReqVitalData> list) {

        if (!list.isEmpty()){
            showProgress();
            HomeApiManager apiManager = new HomeApiManager(new IResponseReceiver() {
                @Override
                public void onSuccess(Object data) {
                    hideProgress();
                    /*Log.e("API", "Vitals synced successfully");*/
                    ErrorMessage.alertDialog(context, "Success",
                            "Vitals synced successfully", null);
                }

                @Override
                public void onFailure(@NonNull String errorResponse) {
                    hideProgress();
                    Log.e("API", "Sync failed: " + errorResponse);
                    toastShortInfo("Sync failed.");
                    ErrorMessage.alertDialog(context, "Failed",
                            "Vitals sync failed.", null);
                }
            }, context);
            ReqSaveVitalData reqSaveVitalData=new ReqSaveVitalData();
            reqSaveVitalData.setmVitalRecord(list);
            reqSaveVitalData.setUser_id(CDoctor2Application.getLoginInfo().getUserInfo().getEmail());
            reqSaveVitalData.setDevice_type("apple_health");

            Gson gson = new Gson();
            Log.e("API_PARAMS", gson.toJson(reqSaveVitalData));
            apiManager.saveVitalData(reqSaveVitalData);
        }else {
            ErrorMessage.alertDialog(context, "Success",
                    "Vitals synced successfully", null);
        }


    }

    private synchronized void onVitalCallbackDone(Context context) {
        pendingCallbacks--;

        if (pendingCallbacks == 0) {
            Log.e("glucoseList", "All vitals loaded, calling syncAllVitals()"+glucoseList);
            Log.e("SYNC", "All vitals loaded, calling syncAllVitals()"+bpList);
            Log.e("SYNC", "All vitals loaded, calling syncAllVitals()"+hrList);
            Log.e("SYNC", "All vitals loaded, calling syncAllVitals()"+spo2List);
            Log.e("SYNC", "All vitals loaded, calling syncAllVitals()"+rrList);
            Log.e("SYNC", "All vitals loaded, calling syncAllVitals()"+tempList);
            Log.e("SYNC", "All vitals loaded, calling syncAllVitals()"+heightList);
            Log.e("SYNC", "All vitals loaded, calling syncAllVitals()"+weightList);

        }
    }
}
