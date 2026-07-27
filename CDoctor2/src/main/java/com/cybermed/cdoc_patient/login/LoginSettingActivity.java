package com.cybermed.cdoc_patient.login;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.BaseActivity;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LoginSettingActivity extends BaseActivity implements View.OnClickListener, MaterialSpinner.OnItemSelectedListener {

    private LinearLayout aboutLayout, contactLayout;
    private MaterialSpinner langSpinner;
    private List<String> langList;
    private TextView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_setting);

        aboutLayout = findViewById(R.id.about_us_layout);
        contactLayout = findViewById(R.id.contact_us_layout);
        langSpinner = findViewById(R.id.languageSpinner);
        backBtn = findViewById(R.id.login_back);

        String[] langArray = getResources().getStringArray(R.array.lang_select);
        langList = new ArrayList<>();
        for(String lang : langArray){ langList.add(lang);}
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String lang = preferences.getString("LANG", "");
        if(lang.isEmpty()) {
            lang = Locale.getDefault().getLanguage();
        }
        langSpinner.setItems(langArray);
        int position = lang.equals("en") ? 0 :
                        lang.equals("zh") ? 1 :
                        lang.equals("es") ? 2 : 0;
        langSpinner.setSelectedIndex(position);



        aboutLayout.setOnClickListener(this);
        contactLayout.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        langSpinner.setOnItemSelectedListener(this);
    }

    public void setLangRecreate(String langval) {
        Configuration config = getBaseContext().getResources().getConfiguration();
        Locale locale = new Locale(langval);
        Locale.setDefault(locale);
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        restart();
    }

    private void restart(){
        Intent mStartActivity = new Intent(LoginSettingActivity.this, WelcomeActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(LoginSettingActivity.this, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) getSystemService(LoginSettingActivity.this.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.about_us_layout:
                Intent aboutIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.cybermedcorp.com/Abouts/about_us.html"));
                startActivity(aboutIntent);
                break;
            case R.id.contact_us_layout:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.cybermedcorp.com/#contact"));
                startActivity(browserIntent);
                break;
            case R.id.login_back:
                finish();
                break;

        }
    }


    @Override
    public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
        Log.d("languagedeubg","" + item.toString());
        langSpinner.setSelectedIndex(position);
        switch (position) {
            case 0:
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "en").commit();
                setLangRecreate("en");
                return;
            case 1:
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "zh").commit();
                setLangRecreate("zh");
                return;
            case 2:
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "es").commit();
                setLangRecreate("es");
                return;
        }
    }
}
