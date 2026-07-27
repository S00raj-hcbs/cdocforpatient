package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealth_Glucometer.BG1;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.navigation.Navigation;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.ButterKnifeFragment;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.OnBackPressedListener;
import com.cybermed.cdoc_patient.databinding.FragmentBg5ResultBinding;

import org.jetbrains.annotations.NotNull;

import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment.mainID;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.STATE.RESULT;

public class iHealthBG1ResultFragment extends ButterKnifeFragment  implements OnBackPressedListener {

    FragmentBg5ResultBinding binding;


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_bg5_result, container, false);


        String mData = RESULT.getValue();

        if(binding.txtGlucose != null /*&& mInstruction != null*/){
            binding.txtGlucose.setText(getResources().getString(R.string.iot_glucose) + mData + " mg/dL");
//            mInstruction.setText(getResources().getString(R.string.iot_remove_discard_strip));
        }
        binding.btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackToMain();
            }
        });
        return binding.getRoot();
    }

    private void goBackToMain(){


        int ID =mainID(BluetoothBaseFragment.GLUCOSE_BG1);
        Navigation.findNavController(getView()).navigate(ID);
    }

    @Override
    public void onBackPressed() {
        goBackToMain();
    }
}