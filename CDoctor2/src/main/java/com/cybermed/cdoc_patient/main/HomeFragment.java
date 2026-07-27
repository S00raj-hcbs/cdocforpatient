package com.cybermed.cdoc_patient.main;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.appointment.AppointmentFragment;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.IOTActivity_MainPage;
import com.cybermed.cdoc_patient.common.BaseFragment;
import com.cybermed.cdoc_patient.common.CDoctor2Application;
import com.cybermed.cdoc_patient.common.videoui.Constant;
import com.cybermed.cdoc_patient.doctor.DoctorListFragment;
import com.cybermed.cdoc_patient.doctor.docDetail.DoctorBaseFrag;
import com.cybermed.cdoc_patient.healthRecord.HealthRecordFragment;
import com.cybermed.cdoc_patient.me.MeFragment;
import com.cybermed.cdoc_patient.me.SettingsFragment;
import com.cybermed.cdoc_patient.me.UserFragment;
import com.cybermed.cdoc_patient.me.allergies.AllergiesFragment;
import com.cybermed.cdoc_patient.me.document.DocumentFragment;
import com.cybermed.cdoc_patient.me.immunizations.ImmunizationFragment;
import com.cybermed.cdoc_patient.me.labereport.LabFragment;
import com.cybermed.cdoc_patient.me.medication.MedicationFragment;
import com.cybermed.cdoc_patient.me.referral.ReferalFragment;
import com.cybermed.cdoc_patient.me.vitalcheck.VitalCheckFragment;
import com.cybermed.cdoc_patient.me.vitalcheck.VitalMonitorFragment;

import static com.cybermed.cdoc_patient.common.videoui.Constant.ishomesnot;
import static com.cybermed.cdoc_patient.util.AppConstant.DOC_INFO;
import static com.cybermed.cdoc_patient.util.AppConstant.FROM_SEARCH;
import static com.cybermed.cdoc_patient.util.AppConstant.KEY_APPTID;
import static com.cybermed.cdoc_patient.util.AppConstant.KEY_APPT_TYPE;
import static com.cybermed.cdoc_patient.util.AppConstant.KEY_IS_CHIEF_COMPLAIN;
import static com.cybermed.cdoc_patient.util.AppConstant.KEY_IS_CHIEF_NOTES;
import static com.cybermed.cdoc_patient.util.AppConstant.KEY_IS_RESCHEDULE;
import static com.cybermed.cdoc_patient.util.AppConstant.KEY_PAGE;
import static com.cybermed.cdoc_patient.util.AppConstant.KEY_PAGE_TYPE;
import static com.cybermed.cdoc_patient.util.AppConstant.SELECTED_TAB;

/**
 * Created by qinwe on 2017/6/13.
 */

public class HomeFragment extends BaseFragment {
    private MainFragment mainFragment;
    private IOTActivity_MainPage tabletMainFragment;
    private DoctorListFragment doctorListFragment;
    private AppointmentFragment ApptFragment;
    private DoctorBaseFrag doctorBaseFrag;
    private FragmentManager fragmentManager;
    private FragmentTransaction ft;
    public Fragment curFragment;
    private FragmentMainActivity fragMain;
    private static HomeFragment mainInstance = null;
    private boolean tabletMode;
    private MeFragment meFragment;
    UserFragment userFragment;
    MainActVm viewModel;
    IOTActivity_MainPage vitalFragment;
    HealthRecordFragment healthRecordFragment;
    SettingsFragment settingsFragment;


    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base, null);
        return view;
    }

    @Override
    protected void initLayout(View view) {
        viewModel = new ViewModelProvider(getActivity()).get(MainActVm.class);
        mainFragment = new MainFragment();
        meFragment = new MeFragment();
        userFragment = new UserFragment();
        doctorBaseFrag = new DoctorBaseFrag();
        doctorListFragment = new DoctorListFragment();
        ApptFragment = new AppointmentFragment();
        fragMain = (FragmentMainActivity) getActivity();
        vitalFragment = new IOTActivity_MainPage();
        healthRecordFragment = new HealthRecordFragment();
        settingsFragment = new SettingsFragment();

        mainInstance = this;

        fragmentManager = getChildFragmentManager();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        tabletMode = preferences.getBoolean("tabletmode", false);
        if (tabletMode) {
            fragMain.hideBottomBar();
            tabletMainFragment = new IOTActivity_MainPage();
            replaceFragment(tabletMainFragment);
        } else {
            ishomesnot="";
            Constant.ishomefragment="MainFragment";
            replaceFragment(mainFragment);
        }

    }



    /**
     * @param providerCode      provider code
     * @param keySearchPage     decide profile open from doctor search page/appt list /homepage
     * @param isVideoClinicAppt true book appt for video/ false for clinic
     * @param isOpenProfile     true open profile page /false booking page
     */
    public void openDocDetail(String providerCode, String keySearchPage,
                              boolean isVideoClinicAppt, boolean isOpenProfile) {
        Bundle bundle = new Bundle();
        bundle.putString(DOC_INFO, providerCode);
        bundle.putString(KEY_PAGE, keySearchPage);
        bundle.putBoolean(KEY_APPT_TYPE, isVideoClinicAppt);
        bundle.putBoolean(KEY_PAGE_TYPE, isOpenProfile);
        doctorBaseFrag.setArguments(bundle);
        replaceFragment(doctorBaseFrag);
        Fragment existedFragment = fragmentManager.findFragmentByTag(doctorBaseFrag.getClass().getSimpleName());
        if (existedFragment != null) {
            doctorBaseFrag.refreshFragment(true);
        }

    }
    /**
     * @param providerCode      provider code
     * @param keySearchPage     decide profile open from doctor search page/appt list /homepage
     * @param isVideoClinicAppt true book appt for video/ false for clinic
     * @param isOpenProfile     true open profile page /false booking page
     */
    public void openDocBook(String providerCode, String keySearchPage,
                              boolean isVideoClinicAppt, boolean isOpenProfile, boolean is_reschedule,String ApptId,String Reson,String notes) {
        Bundle bundle = new Bundle();
        bundle.putString(DOC_INFO, providerCode);
        bundle.putString(KEY_PAGE, keySearchPage);
        bundle.putBoolean(KEY_APPT_TYPE, isVideoClinicAppt);
        bundle.putBoolean(KEY_PAGE_TYPE, isOpenProfile);
        bundle.putBoolean(KEY_IS_RESCHEDULE, is_reschedule);
        bundle.putString(KEY_IS_CHIEF_COMPLAIN, Reson);
        bundle.putString(KEY_IS_CHIEF_NOTES, notes);
        bundle.putString(KEY_APPTID, ApptId);
        doctorBaseFrag.setArguments(bundle);
        replaceFragment(doctorBaseFrag);
        Fragment existedFragment = fragmentManager.findFragmentByTag(doctorBaseFrag.getClass().getSimpleName());
        if (existedFragment != null) {
            doctorBaseFrag.refreshFragment(true);
        }

    }
    public void openDoctorList(boolean fromSeach, boolean isSetFragment) {
        fragMain.checkOutState();
        Bundle bundle = new Bundle();
        bundle.putBoolean(FROM_SEARCH, fromSeach);
        doctorListFragment.setArguments(bundle);
        if (isSetFragment) {
            setFragment(doctorListFragment);
        } else
            replaceFragment(doctorListFragment);
    }

    public void openApptFragment(String selectedTab, boolean isSetFragment) {
        Bundle data = new Bundle();
        data.putString(SELECTED_TAB, selectedTab);
        ApptFragment.setArguments(data);
        if (isSetFragment) {
            setFragment(ApptFragment);
        } else
            replaceFragment(ApptFragment);
        ApptFragment.fragmentOpened(MAINFRAGMENTTYPE);
        ApptFragment.refreshFragment(true);
    }

    /**
     * back press of Me page, appointment
     */
    public void openMainFragment() {
        Constant.ishomefragment="MainFragment";
        replaceFragment(mainFragment);
        mainFragment.refreshFragment(true);
    }

    /**
     * open vital device list
     */
    public void openVitalFragment() {
        Constant.isvitalrecord="1";
        replaceFragment(vitalFragment);
        vitalFragment.refresh();

    }

    /**
     * open health record list
     */
    public void openHealthRecordFragment() {
        replaceFragment(healthRecordFragment);
    }
    /**
     * open setting
     */
    public void openSettingFragment() {
        SettingsFragment settingsFragment1 = SettingsFragment.newInstance( true);
        replaceFragment(settingsFragment1);
    }
    /**
     * back press from doctor list
     */
    public void openMainActivity() {
       /* Constant.ishomefragment="MainFragment";
        setFragment(mainFragment);
        mainFragment.refreshFragment(true);*/
        Constant.ishomefragment="MainFragment";
        replaceFragment(mainFragment);
        mainFragment.refreshFragment(true);
    }

    /**
     * open medication fragment
     */
    public void openMedicationFragment() {
        MedicationFragment medicationFragment = MedicationFragment.newInstance(CDoctor2Application.getLoginInfo().getUserInfo(), true);
        replaceFragment(medicationFragment);

    }

    public void openImmunizationFragment() {
        ImmunizationFragment immunizationFragment = ImmunizationFragment.newInstance(CDoctor2Application.getLoginInfo().getUserInfo(), true);
        replaceFragment(immunizationFragment);

    }

    public void openAllergiesFragment() {
        AllergiesFragment allergiesFragment = AllergiesFragment.newInstance(CDoctor2Application.getLoginInfo().getUserInfo(), true);
        replaceFragment(allergiesFragment);

    } public void openVitalcheckFragment() {
        VitalCheckFragment vitalCheckFragment = VitalCheckFragment.newInstance(CDoctor2Application.getLoginInfo().getUserInfo(), true,false);
        replaceFragment(vitalCheckFragment);

    }
    public void openHomeVitalcheckFragment() {
        VitalMonitorFragment vitalMonitorFragment = VitalMonitorFragment.newInstance(CDoctor2Application.getLoginInfo().getUserInfo(), true,true);
        replaceFragment(vitalMonitorFragment);
    }
    public void openHomeVitalcheckFragment2() {
        VitalCheckFragment vitalCheckFragment = VitalCheckFragment.newInstance(CDoctor2Application.getLoginInfo().getUserInfo(), true,true);
        replaceFragment(vitalCheckFragment);

    }
    public void openDocumentFragment() {
        DocumentFragment documentFragment = DocumentFragment.newInstance(CDoctor2Application.getLoginInfo().getUserInfo(), true);
        replaceFragment(documentFragment);

    }
    /**
     * open medication fragment
     */
    public void openReferalFragment() {
        ReferalFragment referalFragment = ReferalFragment.newInstance(CDoctor2Application.getLoginInfo().getUserInfo(),true);
        replaceFragment(referalFragment);

    }

    /**
     * open lab report fragment
     */
    public void openLabReportFragment() {
        LabFragment labFragment = LabFragment.newInstance(CDoctor2Application.getLoginInfo().getUserInfo(), true);
        replaceFragment(labFragment);
    }

    public void openTabletMainFragment() {
        replaceFragment(tabletMainFragment);
    }

    public void meFragment() {
        replaceFragment(meFragment);
    }

    public static HomeFragment getInstance() {
        return mainInstance;
    }

    public DoctorListFragment getDoctorListFragment() {
        return doctorListFragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public void setFragment(Fragment fragment) {
        ft = fragmentManager.beginTransaction();
        if (fragment != curFragment) {
            if (curFragment != null) {
                ft.remove(curFragment);
            }

            Fragment existedFragment = fragmentManager.findFragmentByTag(fragment.getClass().getSimpleName());

            if (existedFragment != null) {
                ft.show(existedFragment);
                curFragment = existedFragment;
                Constant.ishomefragment=existedFragment.getTag();
            } else {
                ft.add(R.id.home_container, fragment, fragment.getClass().getSimpleName());
                Constant.ishomefragment=fragment.getClass().getSimpleName();
                curFragment = fragment;
            }

            ft.commitAllowingStateLoss();
        }
    }

    public void replaceFragment(Fragment fragment) {
        ft = fragmentManager.beginTransaction();
        if (fragment != curFragment) {
            Log.e("s","s");
            if (curFragment != null) {
                ft.hide(curFragment);
            }

            Fragment existedFragment = fragmentManager.findFragmentByTag(fragment.getClass().getSimpleName());

            if (existedFragment != null) {
                Log.e("existedFragment",""+existedFragment);
                Log.e("existedFragment",""+existedFragment.getTag());
                Constant.ishomefragment=existedFragment.getTag();
                ft.show(existedFragment);
                if (existedFragment instanceof MeFragment.OnInnerFragmentStatusChange) {
                    ((HomeFragment.OnInnerFragmentStatusChange) existedFragment).onMyResume();
                }
                curFragment = existedFragment;
            } else {
                ft.add(R.id.home_container, fragment, fragment.getClass().getSimpleName());
                Constant.ishomefragment=fragment.getClass().getSimpleName();
                curFragment = fragment;
            }

            ft.commitAllowingStateLoss();
        }
    }

    public interface OnInnerFragmentStatusChange {
        void onMyResume();

        void onMyStop();
    }

}
