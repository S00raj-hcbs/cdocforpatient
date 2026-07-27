package com.cybermed.cdoc_patient.me.labereport;

import static com.cybermed.cdoc_patient.me.MeFragment.USERINFOKEY;
import static com.cybermed.cdoc_patient.util.AppConstant.IS_FROM_HEALTH_RECORD;

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
import com.cybermed.cdoc_patient.me.labereport.adapter.LabRecyclerViewAdapter;
import com.cybermed.cdoc_patient.me.labereport.model.LabReportData;
import com.cybermed.cdoc_patient.me.labereport.model.ResponseLabReport;
import com.cybermed.cdoc_patient.me.manager.ProfileApiManager;
import com.cybermed.cdoc_patient.webapi.IResponseReceiver;

import org.ksoap2.serialization.SoapObject;

import java.util.List;


public class LabFragment extends PatientPortalBaseFragment implements IResponseReceiver,
        LabRecyclerViewAdapter.ItemClickListner, MeFragment.OnInnerFragmentStatusChange, HomeFragment.OnInnerFragmentStatusChange {
    ProfileApiManager labManager;
    UserInfo userInfo;
    Context context;

    @Factory
    public static LabFragment newInstance(UserInfo userInfo, boolean isFromHealthRecord) {
        LabFragment fragment = new LabFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putSerializable(USERINFOKEY, userInfo);
        args.putBoolean(IS_FROM_HEALTH_RECORD, isFromHealthRecord);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    protected void setTitle() {
        getToolbarTitle().setText(getString(R.string.Lab_Report));
        getToolBarBack(new View.OnClickListener() {
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
            if (userInfo != null) {
                labManager = new ProfileApiManager(this, context);
                labManager.getLabReportList(userInfo.getService_code(), userInfo.getEmail());
                showProgress();
            }
        }
    }


    @Override
    public void onSuccess(Object data) {
        ResponseLabReport responseLabReport = (ResponseLabReport) data;
        hideProgress();
        if (data != null && responseLabReport.getResults().size() > 0) {
            hideEmptyLayout();
            setRecyclerView(responseLabReport.getResults());
        } else {
            showEmptyLayout(getString(R.string.no_lab_report_found), ContextCompat.getDrawable(getActivity(),
                    R.drawable.no_lab_report), getString(R.string.lab_desc));
        }
    }

    @Override
    public void onFailure(@NonNull String errorResponse) {
        hideProgress();
        showEmptyLayout(getString(R.string.no_lab_report_found), ContextCompat.getDrawable(getActivity(),
                R.drawable.no_lab_report), getString(R.string.lab_desc));
    }


    /**
     * set recycler view
     *
     * @param data medication list data
     */
    void setRecyclerView(List<LabReportData> data) {
        getRecyclerView().setLayoutManager(new LinearLayoutManager(getActivity()));
        LabRecyclerViewAdapter recyclerViewAdapter = new LabRecyclerViewAdapter(data, this);
        getRecyclerView().setAdapter(recyclerViewAdapter);
    }

    @Override
    protected void refreshUI() {
        callApi();
    }

    @Override
    public void pdfClick(LabReportData labReportData) {
        showPdfDialog(labReportData.getOrderId());
    }

    private void showPdfDialog(String orderId) {
        OnPostExecute ope = result -> {
            hideProgress();
            if (result != null && !result.toString().equals("anyType{}")) {
                String pdf = ((SoapObject) result).getProperty(0).toString();
                if (!pdf.equals("anyType{}")) {
                    WebViewDialog dialogFragment = new WebViewDialog(getActivity(), R.layout.dialog_webview);
                    dialogFragment.setDisplayUrl(pdf, getString(R.string.Lab_Report));
                    dialogFragment.show();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.lab_report_fail_result), Toast.LENGTH_SHORT).show();
                }
            } else
                Toast.makeText(getActivity(), getString(R.string.lab_report_fail_result), Toast.LENGTH_SHORT).show();

        };
        WebService.webServiceAsyncTask(WebServiceID.get_patient_lab_report, ope, userInfo.getService_code(), orderId);
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
