package com.cybermed.cdoc_patient.bluetooth.IC;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.BaseFragment;
import com.cybermed.cdoc_patient.databinding.FragmentIChoiceOxiResultBinding;

import androidx.databinding.DataBindingUtil;

public class iChoiceOxiResultFragment extends BaseFragment implements View.OnClickListener{

    private static final String OXI_DATA = "ichoice_oxi_data";
    private static final String HR_DATA = "ichoice_hr_data";

    private String mOxi;
    private String mHR;
    FragmentIChoiceOxiResultBinding binding;


    public static iChoiceOxiResultFragment newInstance(String oxi, String hr) {
        iChoiceOxiResultFragment fragment = new iChoiceOxiResultFragment();
        Bundle args = new Bundle();
        args.putString(OXI_DATA, oxi);
        args.putString(HR_DATA, hr);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mOxi = getArguments().getString(OXI_DATA);
            mHR = getArguments().getString(HR_DATA);

        }
    }



    public iChoiceOxiResultFragment() {
        // Required empty public constructor
    }

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_i_choice_oxi_result,container,false);
        return binding.getRoot();
    }

    @Override
    protected void initLayout(View view) {
        initView();

    }
    public void initView(){
        binding.txtBo.setText(mOxi + "%");
        binding.txtHr.setText(mHR + " bpm");
        binding.btnFinish.setOnClickListener(this);
    }



//    public void displayData(String bph, String bpl){
//        mBP.setText("Blood Pressure: " + bph + ":" + bpl);
//    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_finish:
                getActivity().finish();
                break;
        }
    }
}