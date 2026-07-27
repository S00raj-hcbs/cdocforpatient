package com.cybermed.cdoc_patient.login.signup;

import android.app.DatePickerDialog;
import android.text.InputType;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.base.BaseMVVMFragment;
import com.cybermed.cdoc_patient.databinding.FragmentSignup2Binding;
import com.cybermed.cdoc_patient.login.viewmodel.SignUpVM;

import java.util.Date;

/**
 * Created by joshu on 9/11/2017.
 */

public class SignUp2Fragment extends BaseMVVMFragment<SignUpVM> {

    private String mGender;
    private int selectMonth = 0, selectDay = 1, selectYear = 1975;
    FragmentSignup2Binding mBinding;


    @Override
    protected SignUpVM createViewModel() {
        return new ViewModelProvider(getActivity()).get(SignUpVM.class);
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.fragment_signup2;
    }

    @Override
    public void onViewModelCreated(View view, SignUpVM viewModel) {
        mBinding =(FragmentSignup2Binding)getDataBinding();
        initViews();
    }


    /**
     * init views
     */
    private void initViews() {
        mBinding.edtBirth.setInputType(InputType.TYPE_NULL);
        mBinding.nextSignUp.setOnClickListener(v -> nextPage());
        mBinding.edtBirth.setOnClickListener(v -> showDate(selectYear, selectMonth, selectDay));
        mBinding.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.tvMale:
                    mGender = "M";
                    break;
                case R.id.tvFemale:
                    mGender = "F";
                    break;
                case R.id.tvOther:
                    mGender = "U";

            }
            viewModel.getGender().setValue(mGender);
        });
    }


    /**
     * open dob dialog
     *
     * @param year        selcted year
     * @param monthOfYear selected month
     * @param dayOfMonth  selected day
     */
    void showDate(int year, int monthOfYear, int dayOfMonth) {
        DatePickerDialog datePicker = new android.app.DatePickerDialog(
                getActivity(), (view, year1, month, dayOfMonth1) -> {
            String tempMonth, tempDay;
            if ((month + 1) < 10)
                tempMonth = "0" + (month + 1);
            else
                tempMonth = (month + 1) + "";

            if (dayOfMonth1 < 10)
                tempDay = "0" + dayOfMonth1;
            else
                tempDay = dayOfMonth1 + "";

            selectDay = dayOfMonth1;
            selectMonth = month;
            selectYear = year1;
            viewModel.getDob().setValue(year1 + "-" + tempMonth + "-" + tempDay);
            mBinding.edtBirth.setText(String.format("%s/%s/%d", tempMonth, tempDay, year1));
        }, year, monthOfYear, dayOfMonth);
        datePicker.getDatePicker().setMaxDate(new Date().getTime());
        datePicker.show();

    }

    public void nextPage() {
        viewModel.getMoveNext().setValue(true);
    }




}

