package com.cybermed.cdoc_patient.login.onBoarding;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.viewpager.widget.ViewPager;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.PreferenceUtil;
import com.cybermed.cdoc_patient.login.LoginActivity;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import static com.cybermed.cdoc_patient.util.AppConstant.isFirstLaunch;


public class OnBoardingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
            EdgeToEdge.enable((OnBoardingActivity) this);
            ViewCompat.setOnApplyWindowInsetsListener(getWindow().getDecorView(), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                //v.setBackgroundColor(ContextCompat.getColor(BaseActivity.this,R.color.color_00acbb));
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {

            WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
            EdgeToEdge.enable(this);
            // Optional: dark icons
            WindowInsetsControllerCompat controller =
                    WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());

            if (controller != null) {
                controller.setAppearanceLightStatusBars(true); // dark text/icons
            }

            ViewCompat.setOnApplyWindowInsetsListener(getWindow().getDecorView(), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                //v.setBackgroundColor(ContextCompat.getColor(BaseActivity.this,R.color.color_00acbb));
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
        PreferenceUtil.commitBoolean(isFirstLaunch, true);
        setPagerAdapter();
    }

    private void setPagerAdapter() {
        ViewPager sliderViewPager = findViewById(R.id.view_pager);
        final int padding = getResources().getDimensionPixelOffset(R.dimen.dimen_28dp);
        final int margin = getResources().getDimensionPixelOffset(R.dimen.dimen_24dp);
        sliderViewPager.setPadding(padding, 0, padding, 0);
        sliderViewPager.setPageMargin(margin);
        sliderViewPager.setClipToPadding(false);
        TextView skipBtn = findViewById(R.id.skipBtn);
        skipBtn.setBackground(null);
        LinearLayout getStartedBtn = findViewById(R.id.getStartedBtn);
        sliderViewPager.setOffscreenPageLimit(5);
        sliderViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 4) {
                    skipBtn.setVisibility(View.GONE);
                    getStartedBtn.setVisibility(View.VISIBLE);
                } else if (skipBtn.getVisibility() == View.GONE) {
                    skipBtn.setVisibility(View.VISIBLE);
                    getStartedBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        TabLayout tabLayout = findViewById(R.id.tab_layout);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getImages());

        sliderViewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(sliderViewPager);
    }

    public void moveToLogin(View v) {
        finish();
        startActivity(new Intent(OnBoardingActivity.this, LoginActivity.class));
    }

    private ArrayList<Drawable> getImages() {
        ArrayList<Drawable> onBoardImages = new ArrayList<>();
        onBoardImages.add(ContextCompat.getDrawable(this, R.drawable.medical_disclaimer_onboarding_new));
        onBoardImages.add(ContextCompat.getDrawable(this, R.drawable.device_info_onbording_new));
        onBoardImages.add(ContextCompat.getDrawable(this, R.drawable.on_boarding_1));
        onBoardImages.add(ContextCompat.getDrawable(this, R.drawable.on_boarding_2));
        onBoardImages.add(ContextCompat.getDrawable(this, R.drawable.on_boarding_3));
        return onBoardImages;
    }

}