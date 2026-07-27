package com.cybermed.cdoc_patient.me;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.BaseFragment;
import com.cybermed.cdoc_patient.main.FragmentMainActivity;

import java.util.ArrayList;

public class AboutActivity extends BaseFragment{

    private ListView listView;
    private AboutAdapter adapter;
    private ArrayList<String> infoList;
    private CountDownTimer cdTimer;

    private FragmentManager fragmentManager;
    private FragmentTransaction ft;
    private Fragment curFragment;
    private MeFragment meFragment;
    private View view;
    private CountDownTimer tabletCountDownTimer;
    private int tabletCountDownInt = 0;

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        view = inflater.inflate(R.layout.fragment_about,null);
        return view;
    }

    @Override
    protected void initLayout(View view) {
        meFragment = (MeFragment)getParentFragment();

        initToolBar();
        initListView();
        initVersionName();
    }



    private void switchTabletMode(){

        PackageManager pm = getActivity().getApplicationContext().getPackageManager();
        ComponentName compName = new ComponentName(getActivity().getPackageName(), getActivity().getPackageName() + ".login.WelcomeActivity");
        pm.setComponentEnabledSetting(compName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        ComponentName compName2 = new ComponentName(getActivity().getPackageName(), getActivity().getPackageName() + ".login.WelcomeActivityTablet");
        pm.setComponentEnabledSetting(compName2, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    private void initListView(){
        infoList = new ArrayList<>();
        listView = (ListView) view.findViewById(R.id.listView);
        adapter = new AboutAdapter(getActivity());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Tablet Debug", String.valueOf(position));
                switch(position) {
                    case 0:
                        tabletCountDownInt++;

                        if(tabletCountDownInt==10){
                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("tabletmode", true);
                            editor.commit();
                            switchTabletMode();
                            recreate();
                        } else {
                            if(tabletCountDownInt > 5){
                                Toast.makeText(getActivity(),"Press " + String.valueOf(10 - tabletCountDownInt) + " more times to enter Tablet Mode",Toast.LENGTH_SHORT).show();
                            }
                            if(tabletCountDownTimer != null) {
                                tabletCountDownTimer.cancel();
                            }
                        }

                        tabletCountDownTimer = new CountDownTimer(5000, 1000) {

                            public void onTick(long millisUntilFinished) {
                                //mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
                            }

                            public void onFinish() {
                                tabletCountDownInt = 0;
                            }
                        }.start();

                        break;
                }
            }
        });

    }

    private void initVersionName(){
        try {
            String versionName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
            infoList.add(versionName);
            adapter.appendList(infoList);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initToolBar(){
        Toolbar toolbar = initFragToolBar(view,getString(R.string.about_heading));

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meFragment.openMeActivity(false);
            }
        });
    }

    private void recreate(){
        final FragmentMainActivity fragMain = (FragmentMainActivity) getActivity();
        fragMain.logOut();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

}
