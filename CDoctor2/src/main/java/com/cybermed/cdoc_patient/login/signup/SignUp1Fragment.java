package com.cybermed.cdoc_patient.login.signup;

import android.graphics.Typeface;
import android.text.method.PasswordTransformationMethod;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.base.BaseMVVMFragment;
import com.cybermed.cdoc_patient.databinding.FragmentSignup1Binding;
import com.cybermed.cdoc_patient.login.viewmodel.SignUpVM;

/**
 * Created by joshu on 9/11/2017.
 */

public class SignUp1Fragment extends BaseMVVMFragment<SignUpVM> {
    FragmentSignup1Binding mBinding;


    @Override
    protected SignUpVM createViewModel() {
        return new ViewModelProvider(getActivity()).get(SignUpVM.class);
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.fragment_signup1;
    }

    @Override
    public void onViewModelCreated(View view, SignUpVM viewModel) {
        mBinding = (FragmentSignup1Binding) getDataBinding();
        initViews();
    }

    /**
     * init views
     */
    private void initViews() {
        mBinding.editPwd.setTypeface(Typeface.DEFAULT);
        mBinding.editPwd.setTransformationMethod(new PasswordTransformationMethod());
        mBinding.editConfirmPassword.setTypeface(Typeface.DEFAULT);
        mBinding.editConfirmPassword.setTransformationMethod(new PasswordTransformationMethod());
        mBinding.nextSignUp.setOnClickListener(v -> nextPage());
        mBinding.passwordReveal.setOnClickListener(view -> {
            if (view.getTag() == "0") {
                view.setTag("1");
                mBinding.editPwd.setTransformationMethod(null);
                mBinding.editPwd.setSelection(mBinding.editPwd.length());
                viewModel.getPasswordView().setValue(true);
            } else {
                view.setTag("0");
                mBinding.editPwd.setTransformationMethod(new PasswordTransformationMethod());
                mBinding.editPwd.setSelection(mBinding.editPwd.length());
                viewModel.getPasswordView().setValue(false);
            }
        });
    }

    public void nextPage() {
        viewModel.getMoveNext().setValue(true);
    }


}
