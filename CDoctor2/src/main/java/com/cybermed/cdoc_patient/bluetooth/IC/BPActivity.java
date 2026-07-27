package com.cybermed.cdoc_patient.bluetooth.IC;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.cybermed.cdoc_patient.R;

public class BPActivity extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_bp);
        findViewById(R.id.ichoice_scale_image).setOnClickListener(this);
        findViewById(R.id.ihealth_bp_image).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ihealth_bp_image:
                Intent ihealth_intent = new Intent(this, iHealthBP550BTFragment.class);
                startActivity(ihealth_intent);
                finish();
                break;
            case R.id.ichoice_scale_image:
                Intent ichoice_intent = new Intent(this, iChoiceBPFragment.class);
                startActivity(ichoice_intent);
                finish();
                break;
        }
    }
}
