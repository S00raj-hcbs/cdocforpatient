package com.cybermed.cdoc_patient.bluetooth.IC;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealth_Pulse_Oximeter.iHealthPulseOxiFragment;

import androidx.appcompat.app.AppCompatActivity;

public class OxiActivity extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_oxi);
        findViewById(R.id.ichoice_oxi_image).setOnClickListener(this);
        findViewById(R.id.ihealth_oxi_image).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ihealth_oxi_image:
                Intent ihealth_intent = new Intent(this, iHealthPulseOxiFragment.class);
                startActivity(ihealth_intent);
                finish();
                break;
            case R.id.ichoice_oxi_image:
                Intent ichoice_intent = new Intent(this, iChoicePulseOxiFragment.class);
                startActivity(ichoice_intent);
                finish();
                break;
        }
    }
}
