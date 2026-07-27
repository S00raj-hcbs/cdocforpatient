package com.cybermed.cdoc_patient.me.immunizations;

import static com.cybermed.cdoc_patient.me.MeFragment.USERINFOKEY;
import static com.cybermed.cdoc_patient.util.AppConstant.IS_FROM_HEALTH_RECORD;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cdfortis.datainterface.soap.UserInfo;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.annotation.Factory;
import com.cybermed.cdoc_patient.common.BaseFragment;
import com.cybermed.cdoc_patient.databinding.FragmentImmunizationUiBinding;
import com.cybermed.cdoc_patient.main.HomeFragment;
import com.cybermed.cdoc_patient.me.MeFragment;
import com.cybermed.cdoc_patient.me.immunizations.Model.ImmunizationData;
import com.cybermed.cdoc_patient.me.immunizations.Model.ResponseImmunization;
import com.cybermed.cdoc_patient.me.immunizations.adapter.ImmunizationRecyclerViewAdapter;
import com.cybermed.cdoc_patient.me.manager.ProfileApiManager;
import com.cybermed.cdoc_patient.webapi.IResponseReceiver;

import java.util.ArrayList;
import java.util.List;

public class ImmunizationFragment extends BaseFragment implements IResponseReceiver,
        MeFragment.OnInnerFragmentStatusChange,HomeFragment.OnInnerFragmentStatusChange{

    Activity context;
    FragmentImmunizationUiBinding binding;

    ImmunizationRecyclerViewAdapter immunizationRecyclerViewAdapter;

    @Factory
    public static ImmunizationFragment newInstance(UserInfo userInfo, boolean isFromHealthRecord) {
        ImmunizationFragment fragment = new ImmunizationFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putSerializable(USERINFOKEY, userInfo);
        args.putBoolean(IS_FROM_HEALTH_RECORD, isFromHealthRecord);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_immunization_ui, container, false);
        return binding.getRoot();
    }

    @Override
    protected void initLayout(View view) {
        context = getActivity();
        setRecyclerView();
        callApi();
        clickListner();
    }

    private void clickListner() {
        binding.toolBar.txtTittle.setText(R.string.immunization2);
        binding.toolBar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getArguments() != null && getArguments().getBoolean(IS_FROM_HEALTH_RECORD)) {
                    if (getParentFragment() != null)
                        ((HomeFragment) getParentFragment()).openHealthRecordFragment();
                } else {
                    if (((MeFragment) getParentFragment() != null)) {
                        ((MeFragment) getParentFragment()).openUserActivityFragment();
                    }
                }
            }
        });
        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                callApi();
            }
        });
    }

    void setRecyclerView() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        immunizationRecyclerViewAdapter = new ImmunizationRecyclerViewAdapter(new ArrayList<>(), context);
        binding.recyclerView.setAdapter(immunizationRecyclerViewAdapter);
    }

    //***************api call and success failure**************************************
    private void callApi() {
        Bundle args = getArguments();
        if (args != null) {
            UserInfo userInfo = (UserInfo) args.getSerializable(USERINFOKEY);
            if (userInfo != null) {
                ProfileApiManager ImmunizationManager = new ProfileApiManager(this, context);
                ImmunizationManager.getImmunizationList(userInfo.getService_code(), userInfo.getEmail());
                showProgress();
            }
        }
    }

    @Override
    public void refreshFragment(boolean isRefresh) {
        super.refreshFragment(isRefresh);

    }

    @Override
    public void onMyResume() {
        callApi();
    }

    @Override
    public void onMyStop() {

    }

    void setList(List<ImmunizationData> data) {
        if (immunizationRecyclerViewAdapter != null)
            immunizationRecyclerViewAdapter.setList(data);
    }
    @Override
    public void onSuccess(Object data) {
        hideProgress();
        ResponseImmunization responseImmunization = (ResponseImmunization) data;

        binding.swipeRefreshLayout.setRefreshing(false);
        if (data != null && responseImmunization.getImmunizations().size() > 0) {
            binding.emptyLayout.setVisibility(View.GONE);
            setList(responseImmunization.getImmunizations());
        } else {
            binding.emptyLayout.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void onFailure(@NonNull String errorResponse) {
        hideProgress();
        binding.swipeRefreshLayout.setRefreshing(false);
        binding.emptyLayout.setVisibility(View.VISIBLE);
    }
}
