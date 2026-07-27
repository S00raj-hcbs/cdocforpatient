package com.cybermed.cdoc_patient.bluetooth.IC;


import android.os.Bundle;

import androidx.databinding.DataBindingUtil;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.BaseFragment;
import com.cybermed.cdoc_patient.databinding.FragmentIChoiceBpResultBinding;

public class iChoiceBPResultFragment extends BaseFragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TEXT = "text";
    private static final String BPH_DATA = "ichoice_bph_data";
    private static final String BPL_DATA = "ichoice_bpl_data";

    private String mBPH;
    private String mBPL;


    // TODO: Rename and change types of parameters
    FragmentIChoiceBpResultBinding binding;


    public static iChoiceBPResultFragment newInstance(String bph, String bpl) {
        iChoiceBPResultFragment fragment = new iChoiceBPResultFragment();
        Bundle args = new Bundle();
        args.putString(BPH_DATA, bph);
        args.putString(BPL_DATA, bpl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mBPH = getArguments().getString(BPH_DATA);
            mBPL = getArguments().getString(BPL_DATA);

        }
    }


    public iChoiceBPResultFragment() {
        // Required empty public constructor
    }

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_i_choice_bp_result, container, false);
        return binding.getRoot();
    }

    @Override
    protected void initLayout(View view) {
        initView();
    }
    public void initView(){
       binding.txtBp.setText(mBPH + " / " + mBPL + " mmHg");
       binding.btnFinish.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_finish:
                getActivity().finish();
                break;
        }
    }

//    public void displayData(String bph, String bpl){
//        mBP.setText("Blood Pressure: " + bph + ":" + bpl);
//    }


}