package com.cybermed.cdoc_patient.me;


import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.BaseFragment;
import com.cybermed.cdoc_patient.main.FragmentMainActivity;

public class ComingSoonFragment extends BaseFragment{
    private FragmentMainActivity mFragMain;
    private MeFragment mMeFragment;
    private View view;



    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_coming_soon, null);
        return view;
    }

    @Override
    protected void initLayout(View view) {

        mFragMain = (FragmentMainActivity) getActivity();
        mMeFragment = (MeFragment) getParentFragment();

        String title = getArguments().getString("FRAGTITLE");


        Toolbar toolbar = view.findViewById(R.id.toolbar);
        mFragMain.setSupportActionBar(toolbar);
        mFragMain.getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.icon_back_row));
        toolbar.setTitleTextColor(Color.WHITE);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(title);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMeFragment.openUserActivityFragment();
            }
        });
    }



}
