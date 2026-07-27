package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealthGraph.IOTGraph;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.BaseFragment;
import com.cybermed.cdoc_patient.main.HomeFragment;

/**
 * Created by qinwe on 2017/6/13.
 */

public class IOTContainer extends BaseFragment {
    private FragmentManager fragmentManager;
    private FragmentTransaction ft;
    private Fragment curFragment;

    public static String USERINFOKEY = "userInfoKey";
    HomeFragment homeFragment;
    IOTActivity_MainPage vitalFragment;
    IOTGraph IOTGraph;

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base, null);
        return view;
    }

    @Override
    protected void initLayout(View view) {

        homeFragment = (HomeFragment) getParentFragment();
        fragmentManager = getChildFragmentManager();
        openVitalFragment();
    }

    /**
     * open vital device list
     */
    public void openVitalFragment() {
        vitalFragment = new IOTActivity_MainPage();
        setFragment(vitalFragment);
        // mainFragment.refreshFragment(true);
    }


    public void backPress() {
        if (curFragment.getChildFragmentManager().getFragments().size() >= 1) {
            openVitalFragment();
        } else if (curFragment instanceof IOTActivity_MainPage) {
            homeFragment.openMainActivity();
        }
    }

    public void replaceFragment(Fragment fragment) {
        //switchFragment();
        ft = fragmentManager.beginTransaction();
        if (fragment != curFragment) {
            if (curFragment != null) {
                ft.hide(curFragment);
                if (curFragment instanceof OnInnerFragmentStatusChange) {
                    ((OnInnerFragmentStatusChange) curFragment).onMyStop();
                }
            }

            Fragment existedFragment = fragmentManager.findFragmentByTag(fragment.getClass().getSimpleName());

            if (existedFragment != null) {
                ft.show(existedFragment);
                if (existedFragment instanceof OnInnerFragmentStatusChange) {
                    ((OnInnerFragmentStatusChange) existedFragment).onMyResume();
                }
                curFragment = existedFragment;
            } else {
                ft.add(R.id.home_container, fragment, fragment.getClass().getSimpleName());
                curFragment = fragment;
            }

            ft.commitAllowingStateLoss();
        }
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
            } else {
                ft.add(R.id.home_container, fragment, fragment.getClass().getSimpleName());
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
