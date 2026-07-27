package com.cybermed.cdoc_patient.login.signup;

import android.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.base.BaseMVVMFragment;
import com.cybermed.cdoc_patient.databinding.FragmentSignup3Binding;
import com.cybermed.cdoc_patient.doctor.doctorFilter.SpinnerAdapter;
import com.cybermed.cdoc_patient.doctor.doctorFilter.SpinnerModel;
import com.cybermed.cdoc_patient.login.viewmodel.SignUpVM;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joshu on 9/11/2017.
 */

public class SignUp3Fragment extends BaseMVVMFragment<SignUpVM> implements View.OnClickListener {
    private List<SpinnerModel> stateList;
    FragmentSignup3Binding mBinding;
    private AlertDialog dialog;


    @Override
    protected SignUpVM createViewModel() {
        return new ViewModelProvider(getActivity()).get(SignUpVM.class);
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.fragment_signup3;
    }

    @Override
    public void onViewModelCreated(View view, SignUpVM viewModel) {
        mBinding = (FragmentSignup3Binding) getDataBinding();
        initViews();
    }


    private void initViews() {
        mBinding.editState.setInputType(InputType.TYPE_NULL);
        mBinding.editState.setOnClickListener(this);
        mBinding.getStartedBtn.setOnClickListener(this);
        //initliaze state list
        String[] stateArray = getResources().getStringArray(R.array.state);
        stateList = new ArrayList<>();
        for (String state : stateArray)
            stateList.add(new SpinnerModel(state, false));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editState:
                showStateOptionDialog();
                break;
            case R.id.getStartedBtn:
                viewModel.getComplete().setValue(true);
                break;
        }
    }

    private void showStateOptionDialog() {
//        PickerViewUtil.alertBottomWheelOption(getActivity(), stateList,
//                (view, postion) -> {
//                    mBinding.editState.setText(stateList.get(postion));
//                    viewModel.getState().setValue(stateList.get(postion));
//                });


        LayoutInflater layoutInflaterAndroid = getLayoutInflater();
        View mView = layoutInflaterAndroid.inflate(R.layout.custom_spinner_drop_down_list_view, null);
        RecyclerView filters = mView.findViewById(R.id.listItems);
        TextView title = mView.findViewById(R.id.title);
        ImageView imgClose=mView.findViewById(R.id.closeBtn);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        title.setText(R.string.select_state_title);

        //in this case there is no okay button
        mView.findViewById(R.id.okayBtn).setVisibility(View.GONE);

        SpinnerAdapter adapter = new SpinnerAdapter(stateList, SpinnerAdapter.Source.SignUp);
        adapter.setFragment(this);
        filters.setAdapter(adapter);
        filters.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        filters.setHasFixedSize(true);


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setView(mView);
        dialog = alertDialogBuilder.create();
        dialog.show();
    }

    public void setState(String state) {
        mBinding.editState.setText(state);
        viewModel.getState().setValue(state);
        dialog.dismiss();
    }
}
