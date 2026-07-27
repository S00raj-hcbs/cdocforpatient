package com.cybermed.cdoc_patient.me.document;

import static com.cybermed.cdoc_patient.me.MeFragment.USERINFOKEY;
import static com.cybermed.cdoc_patient.util.AppConstant.IS_FROM_HEALTH_RECORD;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cdfortis.datainterface.soap.UserInfo;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.annotation.Factory;
import com.cybermed.cdoc_patient.common.BaseFragment;
import com.cybermed.cdoc_patient.databinding.FragmentDocumentUiBinding;
import com.cybermed.cdoc_patient.main.HomeFragment;
import com.cybermed.cdoc_patient.me.MeFragment;
import com.cybermed.cdoc_patient.me.WebViewDialog;
import com.cybermed.cdoc_patient.me.document.adapter.DocumentRecycleViewAdapter;
import com.cybermed.cdoc_patient.me.document.model.ResponseDocument;
import com.cybermed.cdoc_patient.me.document.model.doc_model;
import com.cybermed.cdoc_patient.me.manager.ProfileApiManager;
import com.cybermed.cdoc_patient.webapi.IResponseReceiver;

import java.util.List;


public class DocumentFragment extends BaseFragment implements MeFragment.OnInnerFragmentStatusChange, HomeFragment.OnInnerFragmentStatusChange, DocumentRecycleViewAdapter.ItemClickListner {

    Activity context;
    FragmentDocumentUiBinding binding;
    UserInfo userInfo;

    @Factory
    public static DocumentFragment newInstance(UserInfo userInfo, boolean isFromHealthRecord) {
        DocumentFragment fragment = new DocumentFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putSerializable(USERINFOKEY, userInfo);
        args.putBoolean(IS_FROM_HEALTH_RECORD, isFromHealthRecord);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_document_ui, container, false);
        return binding.getRoot();
    }

    @Override
    protected void initLayout(View view) {
        context = getActivity();
        //setRecyclerView();
        callApi();
        clickListner();

        /*RecyclerView.Adapter adapter = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // Inflate your item layout XML here
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_document, parent, false);
                return new RecyclerView.ViewHolder(itemView) {};
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                // You can perform any additional configuration or customization here if needed
            }

            @Override
            public int getItemCount() {
                // Return the number of items in your design-time list
                return 20; // Replace with the desired number of items
            }
        };
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(adapter);*/
    }

    protected void showEmptyLayout(String text, Drawable drawable , String desc) {
        binding.swipeRefreshLayout.setVisibility(View.GONE);
        //binding.emptyText.setCompoundDrawablesRelativeWithIntrinsicBounds(null, drawable, null, null);
        binding.emptyLayout.setVisibility(View.VISIBLE);
       // binding.emptyText.setText(text);
    }

    protected void hideEmptyLayout() {
        binding.swipeRefreshLayout.setVisibility(View.VISIBLE);
        binding.emptyLayout.setVisibility(View.GONE);
    }

    private void clickListner() {
        binding.toolBar.txtTittle.setText(R.string.my_documents);
        binding.toolBar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getArguments() != null && getArguments().getBoolean(IS_FROM_HEALTH_RECORD)) {
                    if (getParentFragment() != null)
                        ((HomeFragment) getParentFragment()).openMainFragment();
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

    /**
     * set recycler view
     *
     * @param data medication list data
     */
    void setRecyclerView(List<doc_model> data) {
        getRecyclerView().setLayoutManager(new LinearLayoutManager(getActivity()));
        DocumentRecycleViewAdapter recyclerViewAdapter = new DocumentRecycleViewAdapter(data, this);
        getRecyclerView().setAdapter(recyclerViewAdapter);
    }


    public RecyclerView getRecyclerView() {
        return binding.recyclerView;
    }
    //***************api call and success failure**************************************
    private void callApi() {

        Bundle args = getArguments();
        if (args != null) {
            UserInfo userInfo = (UserInfo) args.getSerializable(USERINFOKEY);
            if (userInfo != null) {
                ProfileApiManager deviceDocumentManager = new ProfileApiManager(new IResponseReceiver() {
                    @Override
                    public void onSuccess(Object data) {
                        hideProgress();
                        ResponseDocument responseDocument = (ResponseDocument) data;
                        hideProgress();
                        if (data != null && responseDocument.getData().size() > 0) {
                            hideEmptyLayout();
                            setRecyclerView(responseDocument.getData());
                        } else {
                            showEmptyLayout(getString(R.string.no_lab_report_found), ContextCompat.getDrawable(getActivity(),
                                    R.drawable.no_lab_report), getString(R.string.lab_desc));
                        }

                        binding.swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(@NonNull String errorResponse) {
                        hideProgress();
                        binding.swipeRefreshLayout.setRefreshing(false);
                        // binding.emptyLayout.setVisibility(View.VISIBLE);
                    }
                }, context);
                deviceDocumentManager.getDocumentList(userInfo.getEmail());
                showProgress();
            }
        }

        /*Bundle args = getArguments();
        if (args != null) {
            UserInfo userInfo = (UserInfo) args.getSerializable(USERINFOKEY);
            if (userInfo != null) {
                showProgress();
                ProfileApiManager ImmunizationManager = new ProfileApiManager(this, context);
                ImmunizationManager.getImmunizationList(userInfo.getService_code(), userInfo.getEmail());
                ProfileApiManager DocumentManager = new ProfileApiManager(new IResponseReceiver() {
                    @Override
                    public void onSuccess(Object data) {
                        hideProgress();
                        ResponseVital responseVital = (ResponseVital) data;
                        binding.swipeRefreshLayout.setRefreshing(false);
                        if (data != null && responseVital.getClinicVitaldata().size() > 0) {
                            binding.emptyLayout.setVisibility(View.GONE);
                            setList(responseVital.getClinicVitaldata());
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
                }, context);
                DocumentManager.getClinicVitalList(userInfo.getEmail());
            }
        }*/
    }
/*    void setRecyclerView() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        immunizationRecyclerViewAdapter = new ImmunizationRecyclerViewAdapter(new ArrayList<>(), context);
        binding.recyclerView.setAdapter(immunizationRecyclerViewAdapter);
    }
    void setList(List<ImmunizationData> data) {
        if (immunizationRecyclerViewAdapter != null)
            immunizationRecyclerViewAdapter.setList(data);
    }*/

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

    @Override
    public void pdfClick(doc_model labReportData) {
        String pdf = labReportData.getBuffer();
        if (!pdf.equals("anyType{}")) {
            WebViewDialog dialogFragment = new WebViewDialog(getActivity(), R.layout.dialog_webview);
            dialogFragment.setDisplayUrl(pdf, "My Documents");
            dialogFragment.show();
        }else {
            Toast.makeText(getActivity(), "Document not available, please try again later", Toast.LENGTH_SHORT).show();
        }
    }


}
