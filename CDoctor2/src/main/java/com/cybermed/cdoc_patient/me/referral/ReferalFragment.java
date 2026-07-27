package com.cybermed.cdoc_patient.me.referral;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cdfortis.datainterface.soap.OnPostExecute;
import com.cdfortis.datainterface.soap.UserInfo;
import com.cdfortis.datainterface.soap.WebService;
import com.cdfortis.datainterface.soap.WebServiceID;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.annotation.Factory;
import com.cybermed.cdoc_patient.main.HomeFragment;
import com.cybermed.cdoc_patient.me.MeFragment;
import com.cybermed.cdoc_patient.me.PatientPortalBaseFragment;
import com.cybermed.cdoc_patient.me.WebViewDialog;
import com.cybermed.cdoc_patient.me.manager.ProfileApiManager;
import com.cybermed.cdoc_patient.me.referral.adapter.ReferralAdapter;
import com.cybermed.cdoc_patient.me.referral.model.ReferralData;
import com.cybermed.cdoc_patient.me.referral.model.ResponseReferral;
import com.cybermed.cdoc_patient.webapi.IResponseReceiver;

import org.ksoap2.serialization.SoapObject;

import java.util.List;

import static com.cybermed.cdoc_patient.me.MeFragment.USERINFOKEY;
import static com.cybermed.cdoc_patient.util.AppConstant.IS_FROM_HEALTH_RECORD;


public class ReferalFragment extends PatientPortalBaseFragment implements IResponseReceiver,
        ReferralAdapter.ItemClickListner, MeFragment.OnInnerFragmentStatusChange,HomeFragment.OnInnerFragmentStatusChange {
    ProfileApiManager labManager;
    UserInfo userInfo;
    Context context;

    @Factory
    public static ReferalFragment newInstance(UserInfo userInfo, boolean isFromHealthRecord) {
        ReferalFragment fragment = new ReferalFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putSerializable(USERINFOKEY, userInfo);
        args.putBoolean(IS_FROM_HEALTH_RECORD, isFromHealthRecord);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    protected void setTitle() {
        getToolbarTitle().setText(getString(R.string.referral));
        getToolBarBack(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getArguments() != null && getArguments().getBoolean(IS_FROM_HEALTH_RECORD)) {
                    if (getParentFragment() != null)
                        ((HomeFragment) getParentFragment()).openHealthRecordFragment();
                }else
                if (((MeFragment) getParentFragment() != null)) {
                    ((MeFragment) getParentFragment()).openUserActivityFragment();
                }
            }
        });

    }

    @Override
    protected void onCreateView() {
        context = getActivity();
        callApi();
    }

    private void callApi() {
        Bundle args = getArguments();
        if (args != null) {
            userInfo = (UserInfo) args.getSerializable(USERINFOKEY);
            labManager = new ProfileApiManager(this, context);
            if (userInfo != null) {
                labManager.getReferralList(userInfo.getEmail());
                showProgress();
            }
        }
    }


    @Override
    public void onSuccess(Object data) {
        ResponseReferral responseReferral = (ResponseReferral) data;
        hideProgress();
        if (data != null && responseReferral.getReferral().size() > 0) {
            hideEmptyLayout();
            setRecyclerView(responseReferral.getReferral());
        } else {
            showEmptyLayout(getString(R.string.no_referral_found),
                    ContextCompat.getDrawable(getActivity(), R.drawable.no_referal),getString(R.string.refferal_desc));
        }
    }

    @Override
    public void onFailure(@NonNull String errorResponse) {
        hideProgress();
        showEmptyLayout(getString(R.string.no_referral_found),
                ContextCompat.getDrawable(getActivity(), R.drawable.no_referal),getString(R.string.refferal_desc));
    }


    /**
     * set recycler view
     *
     * @param data medication list data
     */
    void setRecyclerView(List<ReferralData> data) {
        getRecyclerView().setLayoutManager(new LinearLayoutManager(getActivity()));
        ReferralAdapter recyclerViewAdapter = new ReferralAdapter(data, this);
        getRecyclerView().setAdapter(recyclerViewAdapter);
    }

    @Override
    protected void refreshUI() {
        callApi();
    }

    @Override
    public void pdfClick(ReferralData labReportData) {
        OnPostExecute ope = result -> {
            hideProgress();
            if (result != null && !result.toString().equals("anyType{}")) {
                String pdf = ((SoapObject) result).getProperty(0).toString();
                if (!pdf.equals("anyType{}")) {
                    WebViewDialog dialogFragment = new WebViewDialog(getActivity(), R.layout.dialog_webview);
                    dialogFragment.setDisplayUrl(pdf, getString(R.string.referral));
                    dialogFragment.show();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.referral_report_unavailable), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), getString(R.string.referral_report_unavailable), Toast.LENGTH_SHORT).show();

            }
        };
        WebService.webServiceAsyncTask(WebServiceID.get_patient_referral, ope, userInfo.getService_code(), labReportData.getReferalId());
        showProgress();
    }


    @Override
    public void onMyResume() {
        callApi();
    }

    @Override
    public void onMyStop() {

    }
}
