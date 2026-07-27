package com.cybermed.cdoc_patient.me;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.cdfortis.datainterface.soap.OnPostExecute;
import com.cdfortis.datainterface.soap.UserInfo;
import com.cdfortis.datainterface.soap.WebService;
import com.cdfortis.datainterface.soap.model.SoapObjectVector;
import com.cdfortis.datainterface.soap.model.VisitRecord;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.adapter.OrgAdapter;
import com.cybermed.cdoc_patient.common.BaseFragment;
import com.cybermed.cdoc_patient.common.CDoctor2Application;
import com.cybermed.cdoc_patient.databinding.FragmentPatientPortalBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.ksoap2.serialization.SoapObject;

import java.util.Vector;

import static com.cdfortis.datainterface.soap.WebServiceID.get_patient_visit_record;


public abstract class PatientPortalBaseFragment extends BaseFragment {

    protected static final String MEDICATION = "1";
    protected static final String IMMUNIZATION = "2";
    protected static final String LABREPORT = "3";
    protected static final String REFERRALS = "4";


    FragmentPatientPortalBinding binding;
    Context context;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.unbind();
    }

    UserInfo userInfo;
    Vector<VisitRecord> visitRecords;

    View.OnClickListener onClickListener;


    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_patient_portal, container, false);
        context = getActivity();
        return binding.getRoot();
    }

    @Override
    protected void initLayout(View view) {
        userInfo = CDoctor2Application.getLoginInfo().getUserInfo();
        initView();
        initToolBar();
        binding.refreshLayout.setOnRefreshListener(() -> {
            refreshUI();
            binding.refreshLayout.setRefreshing(false);
        });
        clickListener();
    }


    private void initToolBar() {
        binding.toolbar.backBtn.setOnClickListener(onClickListener);

    }

    private void clickListener() {
        binding.refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshUI();
            }
        });
    }


    private void initView() {
        setTitle();
        onCreateView();
    }

    private void getVisitRecord() {
        if (userInfo == null)
            return;

        OnPostExecute ope = result -> {
            visitRecords = new SoapObjectVector<>(VisitRecord.class, (SoapObject) result);

            int index = visitRecords.indexOf(new VisitRecord(userInfo.getService_code(), ""));

            // if (index != -1)
            //populateRecyclerView(visitRecords.get(index));

        };

        WebService.webServiceAsyncTask(get_patient_visit_record, ope, userInfo.getEmail());
    }

    protected abstract void setTitle();

    protected abstract void onCreateView();

    // protected abstract void populateRecyclerView(VisitRecord visitRecord);

    protected abstract void refreshUI();

    protected void showEmptyLayout(String text, Drawable drawable ,String desc) {
        binding.refreshLayout.setVisibility(View.GONE);
        binding.emptyText.setCompoundDrawablesRelativeWithIntrinsicBounds(null, drawable, null, null);
        binding.emptyLayout.setVisibility(View.VISIBLE);
        binding.emptyText.setText(text);
        binding.emptyDesc.setText(desc);
    }

    protected void hideEmptyLayout() {
        binding.refreshLayout.setVisibility(View.VISIBLE);
        binding.emptyLayout.setVisibility(View.GONE);
    }

    private void populateSpinner() {

        if (userInfo == null) {
            return;
        }

        OnPostExecute ope = result -> {
            visitRecords = new SoapObjectVector<>(VisitRecord.class, (SoapObject) result);

            if (visitRecords.size() > 0) {
                OrgAdapter orgAdapter = new OrgAdapter(getActivity(), visitRecords);
                //orgSpinner.setAdapter(orgAdapter);
            }

            setOrgSpinnerOnItemChanged();
        };

        WebService.webServiceAsyncTask(get_patient_visit_record, ope, userInfo.getEmail());
    }

    /*Since populate spinner is not being used, this is not used as well*/
    private void setOrgSpinnerOnItemChanged() {
//        orgSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                VisitRecord visitRecord = visitRecords.get(position);
//                //populateRecyclerView(visitRecord);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
    }

    public RecyclerView getRecyclerView() {
        return binding.recyclerView;
    }

    public FloatingActionButton getFloatingActionButton() {
        return binding.floatingActionButton;
    }

    public TextView getToolbarTitle() {
        return binding.toolbar.txtTittle;
    }

    public void getToolBarBack(View.OnClickListener onClickListener) {
        this.onClickListener=onClickListener;
    }
}
