package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealth_Blood_pressure;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.ViewPagerAdapter;
import com.cybermed.cdoc_patient.databinding.BloodPresureInfoBinding;
import com.cybermed.cdoc_patient.databinding.DoctorDialogFragmentBinding;
import com.cybermed.cdoc_patient.widget.SliderViewPager;
import com.google.android.material.tabs.TabLayout;

import static com.cybermed.cdoc_patient.util.AppConstant.KEY_MAX_BP;
import static com.cybermed.cdoc_patient.util.AppConstant.KEY_MIN_BP;

public class BPInfoFragment extends DialogFragment {

    BloodPresureInfoBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.blood_presure_info, container, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {

            Window window = requireDialog().getWindow();
            if (window != null) {
                WindowCompat.setDecorFitsSystemWindows(window, true);
                window.setStatusBarColor(Color.TRANSPARENT);
                window.setNavigationBarColor(Color.TRANSPARENT);

                WindowInsetsControllerCompat controller =
                        WindowCompat.getInsetsController(window, window.getDecorView());
                controller.setAppearanceLightStatusBars(true); // Use dark icons on light bg
                controller.setAppearanceLightNavigationBars(true);
            }

            // Apply insets manually to root view
            ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
        binding.toolbar.txtTittle.setText(getString(R.string.bp_info));
        binding.toolbar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

            }
        });
        initLayout();
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog);
    }



    protected void initLayout() {
        Bundle data = getArguments();
        if (data != null) {
            String minVal = data.getString(KEY_MIN_BP);
            String maxVal = data.getString(KEY_MAX_BP);
            setPagerAdapter(minVal, maxVal);
        }
    }

    private void setPagerAdapter(String minVal, String maxVal) {
        SliderViewPager sliderViewPager = binding.viewPager.findViewById(R.id.slider_view_pager);
        TabLayout tabLayout = binding.viewPager.findViewById(R.id.tab_layout);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        Bundle data = new Bundle();
        data.putString(KEY_MIN_BP, minVal);
        data.putString(KEY_MAX_BP, maxVal);
        Fragment fragment1 = new BloodPresureInfoFrag();
        fragment1.setArguments(data);
        adapter.addFragment(fragment1, "");
        Fragment fragment2 = new BloodPresureFrag();
        fragment2.setArguments(data);
        adapter.addFragment(fragment2, "");
        sliderViewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(sliderViewPager);
        sliderViewPager.setAnimation(2);
    }


}
