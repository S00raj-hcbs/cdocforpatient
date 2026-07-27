package com.cybermed.cdoc_patient.doctor.docDetail;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cdfortis.datainterface.soap.VectorProviderReviews;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.CDoctor2Application;
import com.cybermed.cdoc_patient.common.base.BaseMVVMFragment;
import com.cybermed.cdoc_patient.common.videoui.Constant;
import com.cybermed.cdoc_patient.databinding.DialogueForConfirmBinding;
import com.cybermed.cdoc_patient.databinding.DialogueForProviderAddressBinding;
import com.cybermed.cdoc_patient.databinding.FragDoctorProfileBinding;
import com.cybermed.cdoc_patient.doctor.ProviderReviewAdapter;
import com.cybermed.cdoc_patient.doctor.docDetail.model.ProviderLocationListModel;
import com.cybermed.cdoc_patient.webapi.IResponseReceiver;
import com.cybermed.cdoc_patient.webapi.manager.HomeApiManager;
import org.jsoup.helper.StringUtil;
import org.ksoap2.serialization.SoapObject;

import static com.cybermed.cdoc_patient.util.AppConstant.FREE_PROVIDER;
import java.util.List;
/**
 * doctor profile page
 */
public class DoctorProfileFrag extends BaseMVVMFragment<DocBookingVm> implements View.OnClickListener {
    /**
     * review list
     */
    private VectorProviderReviews providerReviewsList;
    FragDoctorProfileBinding binding;
    /**
     * review adapter
     */
    ProviderReviewAdapter adapter;
    /**
     * show minimum review list
     */
    VectorProviderReviews minReviewList;
    HomeApiManager apiManager;
     Dialog dialog;
    ProviderLocationAdapter providerLocationAdapter;

    @Override
    protected DocBookingVm createViewModel() {
        return new ViewModelProvider(getActivity()).get(DocBookingVm.class);
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.frag_doctor_profile;
    }

    @Override
    public void onViewModelCreated(View view, DocBookingVm viewModel) {
        binding = (FragDoctorProfileBinding) getDataBinding();
        binding.nextAvailableHeading.setText(getString(R.string.next_available)+" : ");
        initilizeDialogue();
        viewModel.resetValues();
        setClickListners();
        getProviderReview();
    }

    /**
     * set on click listners
     */
    private void setClickListners() {
        binding.btnClinicappt.setOnClickListener(this);
        binding.seeAllReviewsBtn.setOnClickListener(this);
        binding.favBtn.setOnClickListener(this);
        binding.backBtn.setOnClickListener(this);
        binding.btnConsult.setOnClickListener(this);
        binding.btnVideoConslt.setOnClickListener(this);
        binding.btnWaiting.setOnClickListener(this);
        binding.backBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fav_btn:
                if (binding.favBtn.getTag().equals("1")) {
                    viewModel.markProviderAsFavorite(CDoctor2Application.getLoginInfo().getAccount(),
                            viewModel.getDocInfo().getValue().getOrgCode(), viewModel.getDocInfo().getValue().getProviderCode(), 0);
                } else {
                    viewModel.markProviderAsFavorite(CDoctor2Application.getLoginInfo().getAccount(),
                            viewModel.getDocInfo().getValue().getOrgCode(), viewModel.getDocInfo().getValue().getProviderCode(), 1);
                }
                break;
            case R.id.see_all_reviews_btn:
                if (v.getTag().equals("0")) {
                    v.setTag("1");
                    adapter.setList(providerReviewsList);
                    binding.seeAllReviewsBtn.setText(getString(R.string.view_less));
                } else {
                    v.setTag("0");
                    adapter.setList(minReviewList);
                    binding.seeAllReviewsBtn.setText(getString(R.string.view_all));
                }
                break;
            case R.id.btn_clinicappt:
                viewModel.getDocInfo().getValue().setVideoAppoitnmentType(false);
                showAlertDialog();

                break;
            case R.id.btnVideoConslt:
                viewModel.getDocInfo().getValue().setVideoAppoitnmentType(true);
                viewModel.getPageFrom().setValue(DoctorBaseFrag.FROM_PROFILE);
                ((DoctorBaseFrag) getParentFragment()).openDocBookingFrag();
                break;
            case R.id.btn_consult:
                DocHelper.getAuthRep(getActivity(), () -> callNow());
                break;
            case R.id.btn_waiting:
               // showViewAlertDialog();
                callWattingNow();
                break;
            case R.id.back_btn:
                ((DoctorBaseFrag) getParentFragment()).handleBack();
                break;

        }
    }

    /**
     * get provider  review list
     */
    @SuppressLint("DefaultLocale")
    private void getProviderReview() {
        viewModel.getProviderReview();
        viewModel.getProvideReviewList().observe(this, result -> {
            if (result != null) {
                minReviewList = new VectorProviderReviews();

                providerReviewsList = new VectorProviderReviews((SoapObject) result);
                if (providerReviewsList.size() > 3) {
                    for (int i = 0; i < 3; i++) {
                        minReviewList.add(providerReviewsList.get(i));
                    }
                    adapter = new ProviderReviewAdapter(getContext(), minReviewList);
                    binding.seeAllReviewsBtn.setVisibility(View.VISIBLE);
                } else {
                    adapter = new ProviderReviewAdapter(getContext(), providerReviewsList);
                }

                binding.reviewList.setLayoutManager(new LinearLayoutManager(getActivity()));
                binding.reviewList.setAdapter(adapter);
                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(binding.reviewList.getContext(), DividerItemDecoration.VERTICAL);
                Drawable dividerDrawable = ContextCompat.getDrawable(binding.reviewList.getContext(), R.drawable.view_line_bg);
                if (dividerDrawable != null) {
                    dividerItemDecoration.setDrawable(dividerDrawable);
                }
                binding.reviewList.addItemDecoration(dividerItemDecoration);
                binding.titleReview.setText(String.format("%s%d)", getString(R.string.pat_review), providerReviewsList.size()));

            }
        });
    }

    /**
     * consult now option
     */
    private void callNow() {
        viewModel.getPageFrom().setValue(DoctorBaseFrag.FROM_PROFILE);
        viewModel.getDocInfo().getValue().setWaitingRoom(0);
        //Doctors that are free does not require payment method
        if (viewModel.getDocInfo().getValue().getPayingMode() == FREE_PROVIDER) {
            viewModel.getDocInfo().getValue().setPaymentType(DoctorBaseFrag.DOCTOR_FREE);
            showViewAlertDialog();
           // ((DoctorBaseFrag) getParentFragment()).openConfirmAppointment();
        } else {
            //paid doctor
            if ((viewModel.getDocInfo().getValue().getInitialCharge().equals("0")|| StringUtil.isBlank(viewModel.getDocInfo().getValue().getInitialCharge().trim())) && (viewModel.getDocInfo().getValue().getIncrementalCharge().equals("0")||StringUtil.isBlank(viewModel.getDocInfo().getValue().getIncrementalCharge().trim()))){
              //  ((DoctorBaseFrag) getParentFragment()).openConfirmAppointment();
                showViewAlertDialog();
            }else {
                ((DoctorBaseFrag) getParentFragment()).openPaymentFrag();
            }
         //   ((DoctorBaseFrag) getParentFragment()).openPaymentFrag();
        }
    }

    private void callWattingNow() {
        viewModel.getPageFrom().setValue(DoctorBaseFrag.FROM_PROFILE);
        if (viewModel.getDocInfo().getValue().getPayingMode() == FREE_PROVIDER) {
            viewModel.getDocInfo().getValue().setPaymentType(DoctorBaseFrag.DOCTOR_FREE);
           // ((DoctorBaseFrag) getParentFragment()).openConfirmAppointment();
            showViewAlertDialog();
        } else {
            //paid booking appointment
            if ((viewModel.getDocInfo().getValue().getInitialCharge().equals("0")|| StringUtil.isBlank(viewModel.getDocInfo().getValue().getInitialCharge().trim())) && (viewModel.getDocInfo().getValue().getIncrementalCharge().equals("0")||StringUtil.isBlank(viewModel.getDocInfo().getValue().getIncrementalCharge().trim()))){
                //((DoctorBaseFrag) getParentFragment()).openConfirmAppointment();
                showViewAlertDialog();
            }else {
                ((DoctorBaseFrag) getParentFragment()).openPaymentFrag();
            }
            // ((DoctorBaseFrag) getParentFragment()).openPaymentFrag();
        }
    }



    private void showViewAlertDialog() {

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        DialogueForConfirmBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.dialogue_for_confirm, null, false);
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        binding.labelAppt.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen._5sdp));
        binding.txtMessage.setText(R.string.add_details_dialogue_message);
        binding.btnCancel.setText(R.string.cancel);
        binding.btnSchudle.setText(viewModel.getDocInfo().getValue().getOnlineStatus().equals("1")?R.string.consult_now:R.string.join_waiting_room);


        binding.btnConfirm.setOnClickListener(v -> {
            dialog.dismiss();
            Constant.isSchedule = false;
            viewModel.getDocInfo().getValue().setVideoAppoitnmentType(true);
            ((DoctorBaseFrag) getParentFragment()).openConfirmAppointment();
            // moveToHome();
        });
        binding.btnSchudle.setOnClickListener(v -> {
            dialog.dismiss();
            Constant.isSchedule = true;
            viewModel.getDocInfo().getValue().setVideoAppoitnmentType(true);
            ((DoctorBaseFrag) getParentFragment()).openConfirmAppointment();
            // moveToHome();
        });
        binding.btnCancel.setOnClickListener(v -> {
                    dialog.dismiss();
                }
        );
        dialog.show();
    }


    @Override
    public void refreshFragment(boolean isRefresh) {
        super.refreshFragment(isRefresh);
    }

    /**
     * Dialogue for Clinic Address Select
     */
    private void initilizeDialogue() {
        final String[] address1 = {""};
        final String[] address2 = {""};
        final String[] city = {""};
        final String[] state = {""};
        final String[] zip = {""};
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        DialogueForProviderAddressBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.dialogue_for_provider_address, null, false);
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        ProviderLocationAdapter.IProviderLocationCallback iProviderLocationCallback = listModel -> {
            address1[0] =!TextUtils.isEmpty(listModel.getFacility_addr1())?listModel.getFacility_addr1():"";
            address2[0] =!TextUtils.isEmpty(listModel.getFacility_addr2())?listModel.getFacility_addr2():"";
            city[0] =!TextUtils.isEmpty(listModel.getFacility_city())?listModel.getFacility_city():"";
            state[0] =!TextUtils.isEmpty(listModel.getFacility_state())?listModel.getFacility_state():"";
            zip[0] =!TextUtils.isEmpty(listModel.getFacility_zip())?listModel.getFacility_zip():"";
        };

        providerLocationAdapter = new ProviderLocationAdapter(requireContext(), iProviderLocationCallback);
        binding.rcAddressList.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rcAddressList.setAdapter(providerLocationAdapter);


        binding.btnChooseAddress.setOnClickListener(v -> {
            dialog.dismiss();
            // moveToHome();
            /*Objects.requireNonNull(viewModel.getDocInfo().getValue()).setAddr1(address1[0].trim());
            viewModel.getDocInfo().getValue().setAddr2(address2[0].trim());
            viewModel.getDocInfo().getValue().setCity(city[0].trim());
            viewModel.getDocInfo().getValue().setState(state[0].trim());
            viewModel.getDocInfo().getValue().setZip(zip[0].trim());*/
            viewModel.getDocInfo().getValue().setVideoAppoitnmentType(false);
            viewModel.getPageFrom().setValue(DoctorBaseFrag.FROM_PROFILE);
            ((DoctorBaseFrag) getParentFragment()).openDocBookingFrag();
        });
        binding.imageCancel.setOnClickListener(v -> {
                    dialog.dismiss();
                }
        );
        ProviderLocationList();
    }

    private void showAlertDialog() {
        dialog.show();
    }


    /**
     * get doc info
     */
    public void ProviderLocationList() {
        showProgress();
        HomeApiManager apiManager = new HomeApiManager(new IResponseReceiver() {
            @Override
            public void onSuccess(Object data) {
                hideProgress();
                if (isAdded() && isVisible()) {
                    List<ProviderLocationListModel> Locationlist = (List<ProviderLocationListModel>) data;
                    if (Locationlist != null) {
                        if (Locationlist.isEmpty()) {
                            providerLocationAdapter.clearList();
                        } else {
                            providerLocationAdapter.clearList();
                            providerLocationAdapter.appendList(Locationlist);
                        }
                    } else {
                        providerLocationAdapter.clearList();
                    }
                }


            }

            @Override
            public void onFailure(@NonNull String errorResponse) {
                hideProgress();
            }
        }, requireContext());
        apiManager.getProviderClinic(viewModel.getDocInfo().getValue().getOrgCode());

    }

}