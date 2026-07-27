package com.cybermed.cdoc_patient.me;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cybermed.cdoc_patient.common.BaseFragment;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.main.HomeFragment;

/**
 * Created by qinwe on 2017/6/13.
 */

public class MeFragment extends BaseFragment {
    private UserFragment userFragment;
    private FragmentManager fragmentManager;
    private FragmentTransaction ft;
    private Fragment curFragment;
    private static MeFragment userInstance = null;
    public static String USERINFOKEY = "userInfoKey";
    HomeFragment homeFragment;

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base, null);
        return view;
    }

    @Override
    protected void initLayout(View view) {

        userFragment = new UserFragment();
        userInstance = this;
        homeFragment = (HomeFragment) getParentFragment();
        fragmentManager = getChildFragmentManager();
        replaceFragment(userFragment);
    }


    public void openMeActivity(boolean refreshVitals) {
        replaceFragment(userFragment);
        userFragment.refreshFragment(refreshVitals);
    }

    public void openSetting(boolean refreshVitals) {
        SettingsFragment settingsFragment1 = SettingsFragment.newInstance( false);
        replaceFragment(settingsFragment1);
    }
    public void openUserActivityFragment() {
        replaceFragment(userFragment);
    }

    public static MeFragment getInstance() {
        return userInstance;
    }

    public void openHomeFrag() {
        homeFragment.openMainFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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


    public interface OnInnerFragmentStatusChange {
        void onMyResume();

        void onMyStop();
    }
}
