package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.ViewModel.QRCodeViewModel;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.BlueToothScanFragment;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.OnBackPressedListener;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.STATE;
import com.cybermed.cdoc_patient.common.CDoctor2Application;
import com.cybermed.cdoc_patient.common.videoui.Constant;
import com.cybermed.cdoc_patient.main.FragmentMainActivity;
import com.cybermed.cdoc_patient.main.HomeFragment;
import com.cybermed.cdoc_patient.ws.WS;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.stemoscope.stemolib.interfaces.VerificationCallBack;
import com.stemoscope.stemolib.register.RegisterUtils;

import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.STATE.CONNECTED;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.IOTDeviceSetUpFragment.set_up_device_name;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.IOTDeviceSetUpFragment.set_up_device_type;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.Stemoscope.StemoscopeFragment.STEMOSCOPE_DEBUG_TAG;

public class IOTActivity_MainPage extends BluetoothBaseFragment {
    Activity context;
    NavController navController;
    NavHostFragment mNavHostFragment;
    View view;

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.getContentView(inflater, container, savedInstanceState);
        context = getActivity();
        view = inflater.inflate(R.layout.activity_iot, null);
        return view;
    }

    @Override
    protected void initLayout(View view) {
        //setContentView(R.layout.activity_iot);

        NavHostFragment navHost = (NavHostFragment) getChildFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHost.getNavController();

        NavInflater navInflater = navController.getNavInflater();
        NavGraph graph = navInflater.inflate(R.navigation.nav_graph);

        if (CDoctor2Application.getTabletMode()) { //tabletMode
            graph.setStartDestination(R.id.tabletMainFragment);
        } else {
            graph.setStartDestination(R.id.IOT_MainPage_Fragment);
        }
        checkPermissions();
        checkCameraPermissions();
        QRCodeViewModel qrCodeViewModel = new ViewModelProvider(getActivity()).get(QRCodeViewModel.class);

        qrCodeViewModel.getQRCodeDataObs().observe(getActivity(), qrCodeData -> {
            WS.registerBluetoothDevice(set_up_device_type, qrCodeData.getQrCode(), set_up_device_name, result -> {
                if (result.toString().equals("1")) {
                    RegisterUtils.getInstance().stemo_register_sn(qrCodeData.getQrCode(), CDoctor2Application.getLoginInfo().getAccount(),
                            (VerificationCallBack) i -> {
                                Log.d(STEMOSCOPE_DEBUG_TAG, "register result: " + i);
                                switch (i) {
                                    default:
                                        getActivity().runOnUiThread(() -> {
                                            Toast.makeText(getActivity(), "Register successfully. Please tap on stemoscope to start", Toast.LENGTH_LONG).show();
                                            Navigation.findNavController(qrCodeData.getView()).navigate(R.id.action_QRCodeScannerFragment_to_IOT_MainPage_Fragment);
                                        });
                                        break;
                                }
                            });
                } else {
                    Toast.makeText(getActivity(), "Register Failed", Toast.LENGTH_LONG).show();
                }
            });
        });

        navController.setGraph(graph);
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                getPermission();

            }
        } else {
            int coarse_permission = PermissionChecker.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION);
            if (coarse_permission != PermissionChecker.PERMISSION_GRANTED) {
                getPermission();

            }
        }
    }
    private void checkCameraPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                getCameraPermission();
            }
        } else {
            int camera_permission = PermissionChecker.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
            if (camera_permission!= PackageManager.PERMISSION_GRANTED ) {

                getCameraPermission();
            }
        }
    }
    private void getCameraPermission() {

        //location permission
        PermissionListener dialogPermissionListener =
                DialogOnDeniedPermissionListener.Builder
                        .withContext(getActivity())
                        .withTitle("Camera permission")
                        .withMessage("Camera permission is needed to check vitals")
                        .withButtonText(android.R.string.ok)
                        .build();

        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.CAMERA)
                .withListener(dialogPermissionListener)
                .check();
    }
    private void getPermission() {

        //location permission
        PermissionListener dialogPermissionListener =
                DialogOnDeniedPermissionListener.Builder
                        .withContext(getActivity())
                        .withTitle("Location permission")
                        .withMessage("Location permission is needed to check vitals")
                        .withButtonText(android.R.string.ok)
                        .build();

        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(dialogPermissionListener)
                .check();
    }

    /**
     * back press
     */
    public void backPress() {
        STATE state = BG1_Subject.getValue();

        if (state == CONNECTED)
            return;

        Fragment currentFragment = getCurrentFragment();
        if (currentFragment instanceof OnBackPressedListener) {
            ((OnBackPressedListener) currentFragment).onBackPressed();
        } else if (currentFragment instanceof BlueToothScanFragment) {
            if (!((BlueToothScanFragment) currentFragment).isConnecting()) {
                ((HomeFragment) getParentFragment()).openMainActivity();
            }
        } else if (!(currentFragment instanceof IOT_MainPage_Fragment)) {
            if (mNavHostFragment.getChildFragmentManager().getFragments().size() > 1) {
                navController.popBackStack();
            } else {
                navController.popBackStack();
                if(CDoctor2Application.getTabletMode()) {
                    navController.navigate(R.id.tabletMainFragment);
                }else{
                    navController.navigate(R.id.IOT_MainPage_Fragment);
                }
            }

        } else {
            if (Constant.isvitalnot.equals("1")){
                Constant.isvitalnot="";
                navController.popBackStack();
                navController.navigate(R.id.VitalMonitorFragment);
            }else {
                Constant.ishomefragment="MainFragment";
                if ((FragmentMainActivity) getActivity() != null)
                    ((FragmentMainActivity) getActivity()).setHomeNavigation();
           }
        }
    }

    public void refresh() {
        if (navController != null) {
            if (getCurrentFragment() instanceof IOT_MainPage_Fragment) {
                if (Constant.isvitalnot.equals("1")){
                    ((IOT_MainPage_Fragment) getCurrentFragment()).onRefresh();
                } if (Constant.isvitalrecord.equals("2")) {
                    ((IOT_MainPage_Fragment) getCurrentFragment()).Vitalrecord();
                }

            }
        }
    }

    Fragment getCurrentFragment() {
        mNavHostFragment = (NavHostFragment) ((HomeFragment) getParentFragment()).curFragment.getChildFragmentManager().getFragments().get(0);
        return mNavHostFragment.getChildFragmentManager().getFragments().get(0);
    }

}
