package com.cybermed.cdoc_patient.bluetooth.IC;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealth_Scale.Measurement.iHealthScaleFragment;

import androidx.appcompat.app.AppCompatActivity;

public class ScaleActivity extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_scale);
        findViewById(R.id.ichoice_scale_image).setOnClickListener(this);
        findViewById(R.id.ihealth_scale_image).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ihealth_scale_image:
                Intent ihealth_intent = new Intent(this, iHealthScaleFragment.class);
                startActivity(ihealth_intent);
                finish();
                break;
            case R.id.ichoice_scale_image:
                Intent ichoice_intent = new Intent(this, iChoiceScaleFragment.class);
                startActivity(ichoice_intent);
                finish();
                break;
        }
    }
}
