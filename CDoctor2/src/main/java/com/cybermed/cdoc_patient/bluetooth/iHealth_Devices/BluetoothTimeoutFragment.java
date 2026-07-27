package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.BaseFragment;
import com.cybermed.cdoc_patient.databinding.FragmentIhealthTimeoutBinding;

import androidx.databinding.DataBindingUtil;

public class BluetoothTimeoutFragment extends BaseFragment implements View.OnClickListener {
    FragmentIhealthTimeoutBinding binding;


    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_ihealth_timeout, container, false);
        return binding.getRoot();
    }

    @Override
    protected void initLayout(View view) {
        initView();
    }

    public void initView(){
        binding.btnTryAgain.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_try_again) {
            toMainPage();
        }
    }

    public void toMainPage() {
      /*  int ID = ((BluetoothBaseActivity) getActivity()).mainID(BluetoothBaseActivity.TIME_OUT);

        Navigation.findNavController(getView()).navigate(ID);*/
    }

}
