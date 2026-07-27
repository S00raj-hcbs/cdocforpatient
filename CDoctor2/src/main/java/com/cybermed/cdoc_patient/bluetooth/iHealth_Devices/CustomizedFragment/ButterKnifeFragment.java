package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment;

import android.view.View;

import androidx.fragment.app.Fragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ButterKnifeFragment extends Fragment {
    private Unbinder unbinder = null;

    protected void bindView(View view) {
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null)
            unbinder.unbind();
    }
}
