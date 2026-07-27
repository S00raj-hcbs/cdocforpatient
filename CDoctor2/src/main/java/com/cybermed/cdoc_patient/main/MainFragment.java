package com.cybermed.cdoc_patient.main;

import static com.cybermed.cdoc_patient.common.BaseActivity.CALL_TYPE_OUT_GOING;
import static com.cybermed.cdoc_patient.common.BaseActivity.DOCTOR_FREE;
import static com.cybermed.cdoc_patient.common.BaseActivity.PERMISSION_lOCATION;
import static com.cybermed.cdoc_patient.common.videoui.Constant.ishomesnot;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cdfortis.datainterface.soap.OnPostExecute;
import com.cdfortis.datainterface.soap.WebService;
import com.cdfortis.datainterface.soap.WebServiceID;
import com.cdfortis.datainterface.soap.model.DocInfo;
import com.cdfortis.datainterface.soap.model.SoapObjectVector;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.appointment.MyApptFragment;
import com.cybermed.cdoc_patient.common.CDoctor2Application;
import com.cybermed.cdoc_patient.common.base.BaseMVVMFragment;
import com.cybermed.cdoc_patient.databinding.DialogueForConfirmBinding;
import com.cybermed.cdoc_patient.databinding.FragmentHomeBinding;
import com.cybermed.cdoc_patient.doctor.VideoCallActivity;
import com.cybermed.cdoc_patient.doctor.docDetail.model.BaseResponseModel;
import com.cybermed.cdoc_patient.doctor.docDetail.model.ResApptList;
import com.cybermed.cdoc_patient.login.viewmodel.BaseResponse;
import com.cybermed.cdoc_patient.main.Adapter.MenuAdapter;
import com.cybermed.cdoc_patient.maps.MapsActivity;
import com.cybermed.cdoc_patient.util.AppConstant;
import com.cybermed.cdoc_patient.util.ErrorMessage;
import com.cybermed.cdoc_patient.view.MedicalDisclaimerDialog;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * Created by qinwe on 2017/5/2.
 */

public class MainFragment extends BaseMVVMFragment<MainActVm> implements View.OnClickListener {
    /**
     * home fragment instance
     */
    private HomeFragment mHomeFragment;
    /**
     * main fragment instance
     */
    private FragmentMainActivity mFragMain;
    /**
     * binding
     */
    private int currentPage = 0;
    FragmentHomeBinding mBinding;
    Context mContext;
    String[] titleArray;
    MenuAdapter adapter;
    String permission = Manifest.permission.CALL_PHONE;
    int grant = PackageManager.PERMISSION_GRANTED;
    boolean permisiion=false;
    Handler handler;
    public static final int REQUEST_ENABLE_BT = 9010;
    Runnable runnable;
    @Override
    protected MainActVm createViewModel() {
        return new ViewModelProvider(getActivity()).get(MainActVm.class);
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.fragment_home;
    }

    @Override
    public void onViewModelCreated(View view, MainActVm viewModel) {
        mBinding = (FragmentHomeBinding) getDataBinding();
        mContext = getContext();
        viewModel.isErrorOccurred().observe(this, aBoolean -> {
            if (aBoolean) {
                hideProgress();
               /* if (mBinding.viewWelcome.getVisibility() != View.VISIBLE &&
                        mBinding.viewAppt.scrollView.getVisibility() != View.VISIBLE) {
                    mBinding.viewWelcome.setVisibility(View.VISIBLE);
                }*/
                mBinding.pullToRefresh.setRefreshing(false);
                //Toast.makeText(getActivity(), "Some error occurred, please refresh your screen", Toast.LENGTH_LONG).show();
            }
        });
        getPatientApptHistory();
        ishomesnot="";
        initView();
      //  getCdocSupport();
        setProfile();
        setRecyclerView();
         handler = new Handler();
         runnable = new Runnable() {
            @Override
            public void run() {
                if (currentPage == adapter.getItemCount()) {
                    currentPage = 0;
                }
                mBinding.viewAppt.layoutHomeViewpager.viewPager.setCurrentItem(currentPage++, true);
                handler.postDelayed(this, 3000); // Change page every 3 seconds
            }
        };
        handler.postDelayed(runnable, 3000);
    }


    /**
     * initialize view
     */
    void initView() {
        mFragMain = (FragmentMainActivity) getActivity();
        mHomeFragment = (HomeFragment) getParentFragment();
        mBinding.pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPatientApptHistory();
            }
        });
        //set tab view

        //mBinding.slidmenu.setViewPager(mBinding.layoutHomeViewpager.viewPager);
        mBinding.slidmenu.setViewPager(mBinding.viewAppt.layoutHomeViewpager.viewPager);

        mBinding.viewAppt.layoutHomeViewpager.tabLayout.setupWithViewPager(mBinding.viewAppt.layoutHomeViewpager.viewPager);
       // mBinding.viewAppt.layoutHomeViewpager.tabLayout.setupWithViewPager(mBinding.viewAppt.layoutHomeViewpager.viewPager);
        String versionName="";
        int versionCode = 1;
        try {
            PackageInfo packageInfo = requireActivity().getPackageManager().getPackageInfo(requireActivity().getPackageName(), 0);
             versionName = packageInfo.versionName; // Version name
             versionCode = 1; // Version code

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        mBinding.layoutSlidemenu.labelText.setText("VER "+versionName+" ("+versionCode+")");
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        boolean currentView = mBinding.slidmenu.isMenuShow();
        switch (v.getId()) {
            case R.id.btn_checkvital:
                mHomeFragment.openVitalcheckFragment();
                break;
            case R.id.card_vitalmonitoring:
                //mHomeFragment.openHomeVitalcheckFragment();
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
                                mHomeFragment.openVitalFragment();
                            }
                        },
                        ()->{

                        }
                );


                break;
            case R.id.card_document:
                mHomeFragment.openDocumentFragment();
                break;
            case R.id.card_Myappoinments:
                mHomeFragment.openApptFragment(FUTUREAPPT, false);
                break;
            case R.id.card_search_doctors:
                mHomeFragment.openDoctorList(true, false);
                break;
            case R.id.card_find_pharmacy:
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSION_lOCATION);
                    return;
                }
                Intent intent2 = new Intent(getActivity(), MapsActivity.class);
                intent2.putExtra("user_id", mFragMain.getLoginInfo2().getAccount());
                startActivity(intent2);
                break;
            case R.id.card_health_records:
                mHomeFragment.openHealthRecordFragment();
                break;
            case R.id.btn_see_provider:
                mHomeFragment.openDoctorList(false, false);
                break;
            case R.id.txtSearch:
                mHomeFragment.openDoctorList(true, false);
                break;
            case R.id.findPharmacy:
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSION_lOCATION);
                    return;
                }

                Intent intent = new Intent(getActivity(), MapsActivity.class);
                intent.putExtra("user_id", mFragMain.getLoginInfo2().getAccount());
                startActivity(intent);
                break;
            case R.id.txtViewPast:
                mHomeFragment.openApptFragment(PASTAPPT, false);
                break;
            case R.id.txtViewUp:
                mHomeFragment.openApptFragment(FUTUREAPPT, false);
                break;
            case R.id.imgUser:
                mHomeFragment.meFragment();
                break;

            case R.id.img_view:
                if (currentView) {
                    mBinding.slidmenu.hideMenu();
                    mBinding.imgView.setVisibility(View.GONE);
                }
                break;
            case R.id.btn_contact_us:
                if (currentView) {
                    mBinding.slidmenu.hideMenu();
                    mBinding.imgView.setVisibility(View.GONE);
                } else {
                    mBinding.slidmenu.showMenu();
                    mBinding.imgView.setVisibility(View.VISIBLE);
                }
                /*final Dialog dialog = new Dialog(getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(R.layout.dialog_cdoc_support);
                Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
                Button btn_call = dialog.findViewById(R.id.btn_call);

                btn_call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        viewModel.getCdocStatusAsync("132", "cdoc");
                        dialog.dismiss();
                    }
                });
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();*/


                break;

        }
    }

    /**
     * get patient app list
     */
    private void getPatientApptHistory() {
        showProgress();
        viewModel.getPatientApptList().observe(this, o -> {
            hideProgress();
            mBinding.pullToRefresh.setRefreshing(false);
            ArrayList<BaseResponseModel<ArrayList<ResApptList>>> list = o;
            if (list.size() > 0) {
                if (mBinding.viewAppt.scrollView.getVisibility() != View.VISIBLE) {
                    //mBinding.viewWelcome.setVisibility(View.GONE);
                    mBinding.viewAppt.scrollView.setVisibility(View.VISIBLE);
                }
                setList(list.get(0).getObject(), FUTUREAPPT);
               // setList(list.get(1).getObject(), PASTAPPT);
            } else {
                /*if (mBinding.viewWelcome.getVisibility() != View.VISIBLE) {
                    mBinding.viewAppt.scrollView.setVisibility(View.GONE);
                    mBinding.viewWelcome.setVisibility(View.VISIBLE);
                }*/
            }

            reconnectDroppedVideo();
        });
        viewModel.getPatientApptHistory();
    }

    /**
     * @param patientAppointments past/future list
     * @param myApptTab           future/past view
     */
    private void setList(List<ResApptList> patientAppointments, String myApptTab) {
        List<ResApptList> patientHistoryList = new ArrayList<>();
        if (patientAppointments.size() > 0) {
            patientHistoryList.add(patientAppointments.get(0));
            /*if (patientAppointments.size() > 1) {
                patientHistoryList.add(patientAppointments.get(1));
            }*/
            initFrag(patientHistoryList, myApptTab);
        }
    }

    /**
     * @param patientHistoryList past/future list
     * @param myApptTab          future/past view
     */
    void initFrag(List<ResApptList> patientHistoryList, String myApptTab) {
        if (myApptTab.equals(FUTUREAPPT)) {
            viewModel.getApptModelLiveData().getValue().setFutureViewListSize(patientHistoryList.size());
            MyApptFragment apptUpcomingFrag = MyApptFragment.newInstance(FUTUREAPPT, patientHistoryList);
            launchFrag(R.id.container_upcoming, apptUpcomingFrag);
        } else {
            viewModel.getApptModelLiveData().getValue().setPastViewListSize(patientHistoryList.size());
            MyApptFragment apptUpcomingFrag = MyApptFragment.newInstance(PASTAPPT, patientHistoryList);
            launchFrag(R.id.container_past, apptUpcomingFrag);
        }
    }

    /**
     * set fragment into home container
     *
     * @param container      base container
     * @param myApptFragment Appointment fragment
     */
    public void launchFrag(int container, MyApptFragment myApptFragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(container, myApptFragment).commit();
    }

    /**
     * cdoc support call
     */
    private void getCdocSupport() {
        viewModel.getCdocSupport().observe(this, o -> {
            BaseResponse response = (BaseResponse) o;
            if (response != null) {
                if (response.getIntegerVal() == 1) {
                    //start video call
                    Intent intent = new Intent(getActivity(), VideoCallActivity.class);
                    intent.putExtra("type", CALL_TYPE_OUT_GOING);
                    intent.putExtra("orgCode", "cdoc");
                    intent.putExtra("providerId", "132");
                    intent.putExtra("docName", "SUPPORT CDOC");
                    intent.putExtra("paymentType", DOCTOR_FREE);
                    intent.putExtra("isskipped", true);
                    startActivityForResult(intent, AppConstant.REQUEST_HOME_VIDEOCALL);
                } else {
                    String state = response.getIntegerVal() == 2 ? getString(R.string.busy_state) : getString(R.string.offline_state);
                    ErrorMessage.alertDialog(mContext, getString(R.string.unable_to_call_title),
                            getString(R.string.try_again_later_content, state), null);
                }
            }
        });
    }


    /**
     * all online provider call as cdoc support call
     */
    private void getAllProviderAsCdocSupport() {

        OnPostExecute ope = result -> {
            List<DocInfo> providerlist = new ArrayList<>(new SoapObjectVector<>(DocInfo.class, (SoapObject) result));
         if (providerlist.size()<1){
             String state =  getString(R.string.offline_state);
             ErrorMessage.alertDialog(mContext, getString(R.string.unable_to_call_title),
                     getString(R.string.try_again_later_content, state), null);
         }else {

             Log.e("result",""+providerlist.get(0).first_name);
            for (int i=0;i<providerlist.size();i++){
                Intent intent = new Intent(getActivity(), VideoCallActivity.class);
                intent.putExtra("type", CALL_TYPE_OUT_GOING);
                intent.putExtra("orgCode", providerlist.get(i).org_code);
                intent.putExtra("providerId", providerlist.get(i).provider_code);
                intent.putExtra("docName", providerlist.get(i).first_name+" "+providerlist.get(i).last_name);
                intent.putExtra("paymentType", DOCTOR_FREE);
                intent.putExtra("isskipped", true);
                startActivityForResult(intent, AppConstant.REQUEST_HOME_VIDEOCALL);
            }
         }
        };
            WebService.webServiceAsyncTask(WebServiceID.get_all_online_support_providers, ope);
    }

    /**
     * reconnect dropped video call
     */
    void reconnectDroppedVideo() {
        SharedPreferences preferences = mContext.getSharedPreferences("VIDEOSHAREPREF", Context.MODE_PRIVATE);
        String roomNumber = preferences.getString("ROOM_NUMBER", "");
        String guestId = preferences.getString("ROOM_GUEST_ID", "");
        if (!TextUtils.isEmpty(roomNumber)) {
            viewModel.getActiveGuestsCount(roomNumber, guestId);
        } else hideProgress();
    }

    @Override
    public void refreshFragment(boolean isRefresh) {
        if (isRefresh) {
            getPatientApptHistory();
            String titleText = getString(R.string.main_page_title) + CDoctor2Application.getLoginInfo().getUserInfo().getFirstName()
                    + " " + CDoctor2Application.getLoginInfo().getUserInfo().getLastname();
            if (CDoctor2Application.getLoginInfo().isAuthRep()) {
                titleText += "(Rep)";
            }
            mBinding.toolbar.title.setText(titleText);
        }
    }

    private void setProfile() {
        String titleText = CDoctor2Application.getLoginInfo().getUserInfo().getFirstName() + " " + CDoctor2Application.getLoginInfo().getUserInfo().getLastname();
        if (mBinding.layoutSlidemenu.name != null && titleText != null)
            mBinding.layoutSlidemenu.name.setText(titleText);
        mBinding.layoutSlidemenu.email.setText(CDoctor2Application.getLoginInfo().getAccount());
        if (CDoctor2Application.getLoginInfo().getUserInfo().getSex().equals("M")) {
            mBinding.layoutSlidemenu.patientImage.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_doc));
        } else {
            mBinding.layoutSlidemenu.patientImage.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.user_girl));
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            new Object();
        } else {
            refreshFragment(true);
        }
    }

    void setRecyclerView() {
        mBinding.layoutSlidemenu.recyclerviewMenus.setLayoutManager(new LinearLayoutManager(mContext));
        ArrayList<String> data = new ArrayList<>();
        titleArray = getResources().getStringArray(R.array.title_menus);
        Collections.addAll(data, titleArray);
        adapter = new MenuAdapter(mContext,data, new MenuAdapter.ItemClickListner() {
            @Override
            public void itemClick(String item) {
                boolean currentView = mBinding.slidmenu.isMenuShow();
                int index = Arrays.asList(titleArray).indexOf(item);

                switch (index) {
                    case /*"Home"*/0:
                        if (currentView) {
                            mBinding.slidmenu.hideMenu();
                            mBinding.imgView.setVisibility(View.GONE);
                        }
                        refreshFragment(true);
                        break;
                    /*case "Health Records":
                        if (currentView) {
                            mBinding.slidmenu.hideMenu();
                            mBinding.imgView.setVisibility(View.GONE);
                        }
                        mHomeFragment.openHealthRecordFragment();
                        break;*/
                    case /*"Support"*/1:
                        showViewAlertDialog();
                  /*      final Dialog dialog = new Dialog(getContext());
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.setContentView(R.layout.dialog_cdoc_support);
                        Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
                        Button btn_call = dialog.findViewById(R.id.btn_call);

                        btn_call.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                               // viewModel.getCdocStatusAsync("132", "cdoc");
                                getAllProviderAsCdocSupport();
                                dialog.dismiss();
                                if (currentView) {
                                    mBinding.slidmenu.hideMenu();
                                    mBinding.imgView.setVisibility(View.GONE);
                                }
                            }
                        });
                        btn_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                if (currentView) {
                                    mBinding.slidmenu.hideMenu();
                                    mBinding.imgView.setVisibility(View.GONE);
                                }
                                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), permission)) {
                                    // Permission has been denied, but not permanently
                                    // Show an explanation to the user or prompt them to grant the permission again
                                    permisiion=true;
                                    requestPermissions(new String[]{permission}, 1);
                                } else {
                                    makePhoneCall();
                                }
                            }
                        });
                        dialog.show();*/
                        break;
                    case /*"Settings"*/2:
                        if (currentView) {
                            mBinding.slidmenu.hideMenu();
                            mBinding.imgView.setVisibility(View.GONE);
                        }
                        /*SettingsFragment settingsActivity = new SettingsFragment();
                        mHomeFragment.replaceFragment(settingsActivity);*/
                        ((HomeFragment) getParentFragment()).openSettingFragment();
                        break;
                    case /*"Sign Out"*/3:
                        if (currentView) {
                            mBinding.slidmenu.hideMenu();
                            mBinding.imgView.setVisibility(View.GONE);
                        }
                        mFragMain.showLogOutDialog();
                        break;
                }
            }
        });
        mBinding.layoutSlidemenu.recyclerviewMenus.setAdapter(adapter);
    }

    private void showViewAlertDialog() {

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        DialogueForConfirmBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.dialogue_for_confirm, null, false);
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        binding.labelAppt.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen._5sdp));
        binding.txtMessage.setVisibility(View.GONE);
        binding.linTitle.setVisibility(View.VISIBLE);
        binding.btnCancel.setText(R.string.cancel);
        binding.btnConfirm.setText(R.string.video_call);
        binding.btnSchudle.setText(R.string.phone_call);
        boolean currentView = mBinding.slidmenu.isMenuShow();

        binding.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getAllProviderAsCdocSupport();
                dialog.dismiss();
                if (currentView) {
                    mBinding.slidmenu.hideMenu();
                    mBinding.imgView.setVisibility(View.GONE);
                }
            }
        });
        binding.btnSchudle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (currentView) {
                    mBinding.slidmenu.hideMenu();
                    mBinding.imgView.setVisibility(View.GONE);
                }
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), permission)) {
                    // Permission has been denied, but not permanently
                    // Show an explanation to the user or prompt them to grant the permission again
                    permisiion=true;
                    requestPermissions(new String[]{permission}, 1);
                } else {
                    makePhoneCall();
                }
            }
        });
        binding.btnCancel.setOnClickListener(v -> {
                    dialog.dismiss();
                }
        );
        dialog.show();
    }


    private void makePhoneCall() {
        String phoneNumber = "+1(732)800-0020"; // Replace with the desired phone number
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(intent);
        } else {
            Toast.makeText(requireContext(), R.string.permission_denied_to_make_phone_call_please_go_to_app_setting_and_enable_call_permission, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_lOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                intent.putExtra("user_id", mFragMain.getLoginInfo2().getAccount());
                startActivity(intent);
            } else {
                toastShortInfo(getString(R.string.pharmacy_map));
            }
        }
        else if (requestCode == REQUEST_ENABLE_BT) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mHomeFragment.openVitalFragment();
            } else {
                toastShortInfo(getString(R.string.no_permission_bluetooth));
            }
        }
        else if (requestCode == 1 ) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (permisiion==true){
                    makePhoneCall();
                }
            }
            else {
                // Permission denied
                Toast.makeText(requireContext(), "Permission denied to make phone calls", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable); // Stop auto-scroll when activity is destroyed
    }
}
