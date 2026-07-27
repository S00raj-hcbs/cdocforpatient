package com.cybermed.cdoc_patient.bluetooth.IC;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.BaseFragment;
import com.cybermed.cdoc_patient.databinding.FragmentIChoiceScaleResultBinding;

import androidx.databinding.DataBindingUtil;

public class iChoiceScaleResultFragment extends BaseFragment implements View.OnClickListener {

    private static final String SCALE_DATA = "ichoice_scale_data";

    private String mScale;


    // TODO: Rename and change types of parameters
    private TextView mOxiTxt;
    FragmentIChoiceScaleResultBinding binding;


    public static iChoiceScaleResultFragment newInstance(String weight) {
        iChoiceScaleResultFragment fragment = new iChoiceScaleResultFragment();
        Bundle args = new Bundle();
        args.putString(SCALE_DATA, weight);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mScale = getArguments().getString(SCALE_DATA);

        }
    }



    public iChoiceScaleResultFragment() {
        // Required empty public constructor
    }

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_i_choice_scale_result, container, false);
        return binding.getRoot();
    }

    @Override
    protected void initLayout(View view) {
        initView();
    }
    public void initView(){
        binding.txtGlucose.setText(mScale + " lb");
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


}