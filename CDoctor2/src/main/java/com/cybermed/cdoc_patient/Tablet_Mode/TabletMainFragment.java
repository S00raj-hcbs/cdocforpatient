package com.cybermed.cdoc_patient.Tablet_Mode;

import static com.cdfortis.datainterface.soap.WebServiceID.count_online_provider;
import static com.cdfortis.datainterface.soap.WebServiceID.get_PatientDemographic_Android;
import static com.cdfortis.datainterface.soap.WebServiceID.get_patient_user_id_by_mac_address;
import static com.cdfortis.datainterface.soap.WebServiceID.random_choose_online_provider;
import static com.cybermed.cdoc_patient.Tablet_Mode.WelcomeActivityTablet.getWIFIMacAddr;
import static com.cybermed.cdoc_patient.Tablet_Mode.WelcomeActivityTablet.validate;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment.BP;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment.GLUCOMETER_DEVICE_TYPE;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment.IHEALTH_MAC_ADDR;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment.OXIMETER;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment.SCALE;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment.STEMOSCOPE;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment.STEMOSCOPE_NAME;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealthGraph.IOTGraph.BP_DEVICE_TYPE;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealthGraph.IOTGraph.MEASUREMENT1;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealthGraph.IOTGraph.MEASUREMENT2;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealthGraph.IOTGraph.PO_DEVICE_TYPE;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealthGraph.IOTGraph.SCALE_DEVICE_TYPE;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealthGraph.IOTGraph.TIMESTAMP;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealthGraph.IOTGraph.VALUE;
import static com.cybermed.cdoc_patient.common.BaseActivity.DOCTOR_FREE;
import static com.cybermed.cdoc_patient.main.FragmentMainActivity.MY_CAMERA_AUDIO_REQUEST_CODE;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.navigation.Navigation;

import com.annimon.stream.Stream;
import com.cdfortis.datainterface.soap.OnPostExecute;
import com.cdfortis.datainterface.soap.UserInfo;
import com.cdfortis.datainterface.soap.WebService;
import com.cdfortis.datainterface.soap.model.DocInfo;
import com.cdfortis.datainterface.soap.model.IoT_Device;
import com.cdfortis.datainterface.soap.model.Patient_Info;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.ButterKnifeFragment;
import com.cybermed.cdoc_patient.common.CDoctor2Application;
import com.cybermed.cdoc_patient.databinding.TabletMainFragBinding;
import com.cybermed.cdoc_patient.doctor.VideoCallActivity;
import com.cybermed.cdoc_patient.family.FamilyFragment;
import com.cybermed.cdoc_patient.util.ErrorMessage;

import org.ksoap2.serialization.SoapObject;

import java.util.Vector;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class TabletMainFragment extends ButterKnifeFragment implements View.OnClickListener {
    private String user_id;
    TabletMainFragBinding binding;
    Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.tablet_main_frag, container, false);
        context = getActivity();
        ImageView docList = binding.getRoot().findViewById(R.id.docList);
        //pharmacy
        user_id = ((CDoctor2Application) getActivity().getApplication()).getLoginInfo().getAccount();


        if (user_id != null && user_id.equals("lovingcarepharmacy@cybermedcorp.com")) {
            docList.setImageResource(R.drawable.button_callpharmacy);
        }

        getDevices().observe(getViewLifecycleOwner(), ioT_devices -> {
            //if there is device, set the layout to visible
            setUi(ioT_devices);
        });

        CDoctor2Application.getAndSetDeviceVector();

        initVersionName();
        OnlineProviderCount(user_id);

        initClickListners();
        setOrgName();
        return binding.getRoot();
    }

    private void initClickListners() {
        binding.docList.setOnClickListener(this);
        binding.pulseOximeter.setOnClickListener(this);
        binding.scale.setOnClickListener(this);
        binding.bloodPressure.setOnClickListener(this);
        binding.stemoscope.setOnClickListener(this);
        binding.glucometer.setOnClickListener(this);
        binding.imgPulse.setOnClickListener(this);
        binding.imggluco.setOnClickListener(this);
        binding.imgBp.setOnClickListener(this);
        binding.imgsStemo.setOnClickListener(this);
        binding.imgscale.setOnClickListener(this);
    }

    private void setUi(Vector<IoT_Device> ioT_devices) {
        if (ioT_devices != null && ioT_devices.size() > 0) {
            for (IoT_Device device : ioT_devices) {
                String device_type = device.device_type;
                if (device_type.contains(PO_DEVICE_TYPE) || device_type.contains(BP_DEVICE_TYPE) ||
                        device_type.contains(SCALE_DEVICE_TYPE) || device_type.contains(STEMOSCOPE_NAME) || device_type.contains(GLUCOMETER_DEVICE_TYPE)) {
                    binding.devicesConstraintLayout.setVisibility(View.VISIBLE);
                    switch (device_type) {
                        case BP_DEVICE_TYPE:
                            setEnabled(binding.imgBp, binding.txtBp, R.drawable.tablet_ic_bp);
                            break;
                        case PO_DEVICE_TYPE:
                            setEnabled(binding.imgPulse, binding.txtPulse, R.drawable.tablet_ic_pulse);
                            break;
                        case SCALE_DEVICE_TYPE:
                            setEnabled(binding.imgscale, binding.txtScale, R.drawable.tablet_ic_weight);
                            break;
                        case STEMOSCOPE_NAME:
                            setEnabled(binding.imgsStemo, binding.txtStemo, R.drawable.tablet_ic_stemo);
                            break;
                        case GLUCOMETER_DEVICE_TYPE:
                            setEnabled(binding.imggluco, binding.txtGluco, R.drawable.tablet_ic_gluco);
                            break;
                    }
                }

            }
        }
        // otherwise gone
        //binding.devicesConstraintLayout.setVisibility(View.GONE);

    }

    private LiveData<Vector<IoT_Device>> getDevices() {
        return CDoctor2Application.getLoginInfo().getUserInfo().getIoT_devices_obs();
    }

    Disposable disposable;


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        // disposable.dispose();
    }

    private String deviceMacFromVector(String device_type) {
        IoT_Device ioT_device = Stream.of(getDevices().getValue()).filter(device -> device.device_type.contains(device_type)).findFirst()
                .orElse(null);

        if (ioT_device != null)
            return ioT_device.device_macAddress;
        else
            return null;
    }

    private void showErrorMessage(String title, String message, boolean isError) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_cdoc_support);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
        btn_cancel.setText("Cancel");
        Button btn_call = dialog.findViewById(R.id.btn_call);
        TextView txtTittle = dialog.findViewById(R.id.txt_title);
        TextView txtMessage = dialog.findViewById(R.id.txt_message);
        txtTittle.setText(title);
        txtMessage.setText(message);
        if (isError) {
            btn_call.setVisibility(View.GONE);
        } else btn_call.setVisibility(View.VISIBLE);
        btn_call.setOnClickListener(view -> {
            get_user_id_before_call();
            dialog.dismiss();
        });
        btn_cancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }


    private void initVersionName() {
        try {
            String versionName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
            binding.systemVersion.setText(getString(R.string.tablet_version_number) + versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    //if want to add monitor, add the following statement
    //Navigation.findNavController(getView()).navigate(R.id.action_tabletMainFragment_to_monitorFragment);


    private CDoctor2Application getCDocApplication() {
        return (CDoctor2Application) getActivity().getApplication();
    }

    private void get_user_id_before_call() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Activity a = getActivity();

            if (a.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || a.checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                    a.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || a.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_CAMERA_AUDIO_REQUEST_CODE);
                return;
            }
        }

        Object o = Single.just(getWIFIMacAddr())
                .subscribeOn(Schedulers.io())
                .map(mac -> WebService.getInstance().RxCallingWebservice(get_patient_user_id_by_mac_address, mac))
                .filter(userID -> validate(userID.toString()))
                .map(userID -> new Pair<>(userID, WebService.getInstance().RxCallingWebservice(get_PatientDemographic_Android, userID.toString())))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        pair -> {
                            Patient_Info patientInfo = new Patient_Info((SoapObject) pair.second);

                            getCDocApplication().setLogin(true);
                            UserInfo userInfo = new UserInfo();
                            userInfo.deserialize(patientInfo);
                            userInfo.setDevices(CDoctor2Application.getLoginInfo().getUserInfo().getIoT_devices_obs());
                            getCDocApplication().processUserLogin2(userInfo.getEmail(), "", userInfo);

                            AutoChooseProvider(pair.first.toString());
                        },
                        error -> {
                            // Webservice call failed
                            Toast.makeText(TabletMainFragment.this.getContext(), "Unknown error has occurred. Please check internet service.", Toast.LENGTH_SHORT).show();
                        },
                        //completed
                        () -> {
                            // Error on Filter
                            ErrorMessage.alertDialog(context, "User Not Found",
                                    "The user cannot be found on this tablet", new ErrorMessage.OkBtnCallBack() {
                                        @Override
                                        public void callback() {
                                            getActivity().finish();
                                        }
                                    });
                        });
    }

    private void startVideoConsult(String org_code, String provider_id, String docName) {
        Intent intent = new Intent(getActivity(), VideoCallActivity.class);
        intent.putExtra("orgCode", org_code);
        intent.putExtra("providerId", provider_id);
        intent.putExtra("docName", docName);
        intent.putExtra("type", 1);
        intent.putExtra("isskipped", true);
        intent.putExtra("paymentType", DOCTOR_FREE);
        startActivity(intent);
    }


    private void AutoChooseProvider(final String user_id) {

        OnPostExecute ope = result -> {

            DocInfo docInfo = new DocInfo((SoapObject) result);

            if (docInfo != null && docInfo.getOrg_code() != null) {
                String docName = docInfo.getName_prefix() + docInfo.getFirst_name() + " " + docInfo.getMi() + " " + docInfo.getLast_name() + docInfo.getName_suffix();
                startVideoConsult(docInfo.org_code, docInfo.provider_code, docName);
            } else {
                ErrorMessage.alertDialog(context, null,
                        getString(R.string.tablet_main_no_providers), null);
            }
        };

        WebService.webServiceAsyncTask(random_choose_online_provider, ope, user_id);

    }

    private final CompositeDisposable disposables = new CompositeDisposable();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disposables.clear();
    }

    private void OnlineProviderCount(final String user_id) {

        binding.onlineDocCountTxt.setText(getString(R.string.current_online_providers) + " 0");

        Disposable d = Observable.just(user_id)
                .map(id ->
                        WebService.getInstance().RxCallingWebservice(count_online_provider, id))
                .repeatWhen(x -> x.delay(5, TimeUnit.SECONDS))
                .retryWhen(x -> x.delay(5, TimeUnit.SECONDS))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    int integer = Integer.valueOf(result.toString());
                    if (binding.onlineDocCountTxt == null)
                        return;

                    if (integer >= 0) {
                        binding.onlineDocCountTxt.setText(getString(R.string.current_online_providers) + " " + integer);
                    } else {
                        binding.onlineDocCountTxt.setText(getString(R.string.current_online_providers) + " 0");
                    }
                });

        disposables.add(d);

    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        bundle.putString(TIMESTAMP, Long.toString(System.currentTimeMillis() / 1000));
        bundle.putString(MEASUREMENT1, "");
        bundle.putString(MEASUREMENT2, "");
        String mac;

        switch (v.getId()) {
            case R.id.docList:
                showErrorMessage("Call Doctor",
                        "Are you sure you want to call?", false);
                //get_user_id_before_call();
                break;
            case R.id.btn_call_family:
                bundle.putBoolean("TYPE", false);
                FamilyFragment famFrag = new FamilyFragment();
                famFrag.setArguments(bundle);
                break;
            case R.id.img_pulse:
            case R.id.pulse_oximeter:
                mac = deviceMacFromVector(OXIMETER);
                if (mac == null) {
                    showErrorMessage("This device is not registered",
                            "Please contact customer support to register your device", true);
                    return;
                }
                bundle.putString(VALUE, PO_DEVICE_TYPE);
                bundle.putString(IHEALTH_MAC_ADDR, mac);
                Navigation.findNavController(v).navigate(R.id.action_tabletMainFragment_to_IOtGraph, bundle);
                break;
            case R.id.imgscale:
            case R.id.scale:
                mac = deviceMacFromVector(SCALE);
                if (mac == null) {
                    showErrorMessage("This device is not registered",
                            "Please contact customer support to register your device", true);
                    return;
                }
                bundle.putString(VALUE, SCALE_DEVICE_TYPE);
                bundle.putString(IHEALTH_MAC_ADDR, mac);
                Navigation.findNavController(v).navigate(R.id.action_tabletMainFragment_to_IOtGraph, bundle);
                break;
            case R.id.imgBp:
            case R.id.blood_pressure:
                mac = deviceMacFromVector(BP);
                if (mac == null) {
                    showErrorMessage("This device is not registered",
                            "Please contact customer support to register your device", true);
                    return;
                }

                bundle.putString(IHEALTH_MAC_ADDR, mac);
                bundle.putString(VALUE, BP_DEVICE_TYPE);

                Navigation.findNavController(v).navigate(R.id.action_tabletMainFragment_to_IOtGraph, bundle);

                break;
            case R.id.imggluco:
            case R.id.glucometer:
                mac = deviceMacFromVector(GLUCOMETER_DEVICE_TYPE);
                if (mac == null) {
                    showErrorMessage("This device is not registered",
                            "Please contact customer support to register your device", true);
                    return;
                }
                bundle.putString(VALUE, GLUCOMETER_DEVICE_TYPE);
                bundle.putString(IHEALTH_MAC_ADDR, mac);
                Navigation.findNavController(v).navigate(R.id.action_tabletMainFragment_to_IOtGraph, bundle);
                break;
            case R.id.imgsStemo:
            case R.id.stemoscope:
                mac = deviceMacFromVector(STEMOSCOPE);
                if (mac == null) {
                    showErrorMessage("This device is not registered",
                            "Please contact customer support to register your device", true);
                    return;
                }
                bundle.putString(IHEALTH_MAC_ADDR, mac);
                Navigation.findNavController(v).navigate(R.id.action_tabletMainFragment_to_stemoscopeFragment, bundle);
                break;
        }
    }

    void setEnabled(Button btn, TextView txt, int drawable) {
        btn.setBackground(ContextCompat.getDrawable(context, drawable));
        txt.setTextColor(ContextCompat.getColor(context, R.color.dark_slate_blue));
    }

    void setOrgName() {
        String docName = CDoctor2Application.getLoginInfo().getUserInfo().getService_code();
        if (!TextUtils.isEmpty(docName)) {
            if (docName.contains("LovingCare")) {
                binding.txtCompanyName.setText("Pharmacy");
            } else if (docName.equalsIgnoreCase("hnympc")) {
                binding.txtCompanyName.setText("Heritage Medical");
            } else if (docName.equalsIgnoreCase("heightsmedical")) {
                binding.txtCompanyName.setText("Heights Medical");
            } else if (docName.equalsIgnoreCase("sky")) {
                binding.txtCompanyName.setText("Skylands Medical Group");
            } else if (docName.equalsIgnoreCase("pansy")) {
                binding.txtCompanyName.setText("GoodCare Healthcare Service");
            } else {
                binding.txtCompanyName.setText("CyberMed Health Inc.");
            }
        }
    }
}
