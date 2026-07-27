package com.cybermed.cdoc_patient.common.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.cybermed.cdoc_patient.BR;
import com.cybermed.cdoc_patient.common.BaseFragment;

public  abstract class BaseMVVMFragment<T extends BaseViewModel> extends BaseFragment {

    protected T viewModel;
    private ViewDataBinding dataBinding;
    /**
     * Override this method to create view model specific to fragment
     *
     * @return ViewModel
     */
    protected abstract T createViewModel();

    /**
     * Override this method to provide fragment xml view
     *
     * @return layoutId, returns the fragment layout id
     */
    public abstract int getFragmentLayout();
    /**
     * @return dataBinding, data binding object for this view
     */
    public ViewDataBinding getDataBinding() {
        return dataBinding;
    }

    /**
     * Called after view model is created
     *
     * @param view      , View attached to this fragment
     * @param viewModel , view model attached to this fragment
     */
    public abstract void onViewModelCreated(View view, T viewModel);

    protected final View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dataBinding = DataBindingUtil.inflate(inflater,
                getFragmentLayout(), container, false);
        viewModel = createViewModel();
        dataBinding.setLifecycleOwner(this);
        dataBinding.setVariable(BR.vm, viewModel);
        return dataBinding.getRoot();
    }
    /**
     * Super class method, called when view is created
     * Load your view model here.
     *
     * @param view : inflated {@link View}
     */
    @Override
    protected void initLayout(View view) {
        onViewModelCreated(view, viewModel);
    }

}
