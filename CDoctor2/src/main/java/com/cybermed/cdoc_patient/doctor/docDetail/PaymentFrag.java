package com.cybermed.cdoc_patient.doctor.docDetail;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cdfortis.datainterface.soap.OnPostExecute;
import com.cdfortis.datainterface.soap.VectorCreditCard;
import com.cdfortis.datainterface.soap.WebService;
import com.cdfortis.datainterface.soap.WebServiceID;
import com.cdfortis.datainterface.soap.model.SoapObjectVector;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.CDoctor2Application;
import com.cybermed.cdoc_patient.common.base.BaseMVVMFragment;
import com.cybermed.cdoc_patient.common.videoui.Constant;
import com.cybermed.cdoc_patient.databinding.DialogueForConfirmBinding;
import com.cybermed.cdoc_patient.databinding.FragPaymentBinding;
import com.cybermed.cdoc_patient.doctor.InsuranceAdapter;
import com.cybermed.cdoc_patient.doctor.docDetail.model.InsuranceModel;
import com.cybermed.cdoc_patient.payment.CreditCardAdapter;
import com.cybermed.cdoc_patient.util.ErrorMessage;
import com.cybermed.cdoc_patient.view.MyAlertDialog;
import com.cybermed.cdoc_patient.webapi.APIs.PaymentApi;
import com.cybermed.cdoc_patient.webapi.AuthManager;
import com.cybermed.cdoc_patient.webapi.IResponseReceiver;
import com.cybermed.cdoc_patient.webapi.RestApiCall;
import com.cybermed.cdoc_patient.webapi.manager.HomeApiManager;
import com.cybermed.cdoc_patient.webapi.model.request.DeleteSquareCard;
import com.cybermed.cdoc_patient.webapi.model.response.ErrorResponse;
import com.cybermed.cdoc_patient.webapi.model.response.SquareCard;
import com.google.gson.Gson;

import org.ksoap2.serialization.SoapObject;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sqip.Card;
import sqip.CardDetails;
import sqip.CardEntry;

/**
 * payment page
 */
public class PaymentFrag extends BaseMVVMFragment<DocBookingVm> {
    FragPaymentBinding binding;
    public static final int DEFAULT_CARD_ENTRY_REQUEST_CODE = 10101;
    Context mContext;
    CreditCardAdapter adapter;
    InsuranceAdapter insuranceAdapter;
    private int RecyclerViewItemPosition;

    @Override
    protected DocBookingVm createViewModel() {
        return new ViewModelProvider(requireActivity()).get(DocBookingVm.class);
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.frag_payment;
    }

    @Override
    public void onViewModelCreated(View view, DocBookingVm viewModel) {
        binding = (FragPaymentBinding) getDataBinding();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
        WindowCompat.setDecorFitsSystemWindows(requireActivity().getWindow(), true);

        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            return WindowInsetsCompat.CONSUMED;
        });
        }
        mContext = requireActivity();
        insuranceAdapter = new InsuranceAdapter(mContext);
        binding.insuranceRCView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        binding.insuranceRCView.setAdapter(insuranceAdapter);
        getInsuranceList();
        initAddPayment();
        onClickListener();
    }

    /**
     * on click event
     */
    private void onClickListener() {
        binding.toolBar.txtTittle.setText("Choose Payment Method");
        binding.btnAddCreditCard.setOnClickListener(v -> CardEntry.startCardEntryActivity(requireActivity(), true, DEFAULT_CARD_ENTRY_REQUEST_CODE));
     /*   binding.btnAddCreditCard.setOnClickListener(v ->{
            ((DoctorBaseFrag) getParentFragment()).openPaymentCreditCardFrag();
        });*/
        binding.linearEmptyView.btnAddEmptyCreditCard.setOnClickListener(v -> CardEntry.startCardEntryActivity(requireActivity(), true, DEFAULT_CARD_ENTRY_REQUEST_CODE));
        binding.btnConfirmPayment.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(viewModel.getDocInfo().getValue().getCardId())) {
                confirmPayment();
            } else {
                Toast.makeText(mContext, getString(R.string.select_any_card), Toast.LENGTH_LONG).show();
            }
        });
        binding.toolBar.backBtn.setOnClickListener(v -> {
            viewModel.resetValues();
            if (viewModel.getPageFrom().getValue() == DoctorBaseFrag.FROM_PROFILE || viewModel.getDocInfo().getValue().getWaitingRoom() == 0) {
                ((DoctorBaseFrag) getParentFragment()).openDocProfile();
            } else {
                ((DoctorBaseFrag) getParentFragment()).openDocBookingFrag();
            }
        });

    }

    /**
     * initiliaze payment
     */
    private void initAddPayment() {
        // TODO merge to initView{}
        // TODO merge to initView{}
        CreditCardAdapter.ICreditCardCallback iCreditCardCallback = new CreditCardAdapter.ICreditCardCallback() {
            @Override
            public void delete(SquareCard squareCard) {
                Runnable deleteCard = () -> {
                    PaymentApi paymentApi = RestApiCall.getApiService(PaymentApi.class);
                    Call<Void> deleteResponse = paymentApi.deleteSquareCards(new DeleteSquareCard(CDoctor2Application.getLoginInfo().getAccount(), squareCard.getId()));

                    deleteResponse.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (!response.isSuccessful()) {
                                if (response.code() == 500) {
                                    ErrorMessage.alertDialog(mContext, "Server Error", "Error happen on server", null);
                                } else {
                                    try {
                                        String errorBody = response.errorBody().string();
                                        ErrorResponse error = new Gson().fromJson(errorBody, ErrorResponse.class);
                                        ErrorMessage.alertDialog(mContext, "Error", error.getError(), null);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                ErrorMessage.alertDialog(mContext, "Success", "Delete Success", null);
                                getCreditCardList();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            ErrorMessage.alertDialog(mContext, "Error", "Error happen on server", null);
                        }
                    });
                };

                AuthManager.getOrCheckTokenAsync(mContext, deleteCard);
            }

            @Override
            public void itemSelect(SquareCard squareCard) {
                viewModel.getDocInfo().getValue().setCardId(squareCard.getId());
            }
        };

        adapter = new CreditCardAdapter(mContext, true, true, iCreditCardCallback);
        binding.paymentRCView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.paymentRCView.setAdapter(adapter);
        getCreditCardList();
    }

    /**
     * get credit card list
     */
    private void getCreditCardList() {
        showProgress();
        HomeApiManager apiManager = new HomeApiManager(new IResponseReceiver<>() {
            @Override
            public void onSuccess(Object data) {
                hideProgress();
                List<SquareCard> cards = (List<SquareCard>) data;
                if (cards == null || (cards != null && cards.size() == 0)) {
                    binding.viewCardlist.setVisibility(View.GONE);
                    binding.linCard.setVisibility(View.GONE);
                    binding.linearEmptyView.linearEmptyView.setVisibility(View.VISIBLE);
                    checkOldCards();
                } else {
                    binding.viewCardlist.setVisibility(View.VISIBLE);
                    binding.linCard.setVisibility(View.VISIBLE);
                    binding.linearEmptyView.linearEmptyView.setVisibility(View.GONE);
                    adapter.appendList(cards);
                }
            }

            @Override
            public void onFailure(@NonNull String errorResponse) {
                hideProgress();
                ErrorResponse error = new Gson().fromJson(errorResponse, ErrorResponse.class);
                ErrorMessage.alertDialog(mContext, "Error", error.getError(), null);
            }
        },mContext);
        apiManager.getCreditCardList(CDoctor2Application.getLoginInfo().getAccount());
    }


    private void checkOldCards() {
        OnPostExecute ope = result -> {
            VectorCreditCard creditCardInfo = new VectorCreditCard((SoapObject) result);
            if (creditCardInfo.size() != 0) {
                ErrorMessage.alertDialog(mContext, "Notice", "We have updated our payment gateway system. Please re-enter your credit card information again.", null);
            }
        };
        WebService.webServiceAsyncTask(WebServiceID.get_CCInfo, ope, CDoctor2Application.getLoginInfo().getAccount());
    }



    /**
     * Insurance list
     */

    private void getInsuranceList() {


        OnPostExecute ope = result -> {
            Vector<InsuranceModel> insuranceModels = new SoapObjectVector<>(InsuranceModel.class, (SoapObject) result);
            Log.e("result",""+insuranceModels);
            if (insuranceModels.size()==0){
                binding.linInsurance.setVisibility(View.GONE);
            }else {
                binding.linInsurance.setVisibility(View.VISIBLE);
                insuranceAdapter.appendList(insuranceModels);

                binding.insuranceRCView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                    GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {

                        @Override
                        public boolean onSingleTapUp(MotionEvent motionEvent) {
                            return true;
                        }

                    });

                    @Override
                    public boolean onInterceptTouchEvent(RecyclerView Recyclerview, MotionEvent motionEvent) {
                       View ChildView = Recyclerview.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
                        if (ChildView != null && gestureDetector.onTouchEvent(motionEvent)) {
                            RecyclerViewItemPosition = Recyclerview.getChildAdapterPosition(ChildView);
                            InsuranceModel insuranceModel = insuranceAdapter.getItem(RecyclerViewItemPosition);

                            viewModel.getPageFrom().setValue(DoctorBaseFrag.FROM_PAYMENT);
                            viewModel.getDocInfo().getValue().setPaymentType(DoctorBaseFrag.Insurance);
                            showAlertDialog();
                            //((DoctorBaseFrag) getParentFragment()).openConfirmAppointment();
                            /*if (apptAvail.get(RecyclerViewItemPosition) < apptMax.get(RecyclerViewItemPosition)) {
                                Calendar cal = Calendar.getInstance(Locale.US);
                                cal.add(Calendar.DAY_OF_MONTH, viewModel.getApptDateIncrement().getValue());
                                String time = apptTime.get(RecyclerViewItemPosition);
                                String dateTime = mDayDateModel.getDate() + " " + time;

                            }*/
                        }
                        return false;
                    }

                    @Override
                    public void onTouchEvent(RecyclerView Recyclerview, MotionEvent motionEvent) {
                    }

                    @Override
                    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
                    }
                });
            }

        };
        WebService.webServiceAsyncTask(WebServiceID.Get_Patient_Insurances, ope, CDoctor2Application.getLoginInfo().getAccount());


    }

    /**
     * confirm payment
     */
    void confirmPayment() {
        if (viewModel.getDocInfo().getValue().getCardId() != null) {
            MyAlertDialog dialog = new MyAlertDialog(getActivity());
            dialog.show();
            dialog.setTitle(getString(R.string.btn_confirm));
            dialog.setDialogContent(getString(R.string.confirm_payment_dialog));
            dialog.setRightClickListener(getString(R.string.btn_ok), view -> {
                dialog.dismiss();
                viewModel.getPageFrom().setValue(DoctorBaseFrag.FROM_PAYMENT);
                viewModel.getDocInfo().getValue().setPaymentType(DoctorBaseFrag.DOCTOR_PAYMENT);
                showAlertDialog();
               // ((DoctorBaseFrag) getParentFragment()).openConfirmAppointment();
            });
            dialog.setLeftClickListener(getString(R.string.btn_cancel), view -> dialog.dismiss());

        } else {
            ErrorMessage.alertDialog(mContext, null, "Please select card.", null);

        }
    }
    /**
     * Dialogue for quick detail or schedule now.
     */
    private void showAlertDialog() {

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        DialogueForConfirmBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.dialogue_for_confirm, null, false);
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        binding.labelAppt.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen._5sdp));
        if(viewModel.getDocInfo().getValue().isIs_reschedule()){
            binding.txtMessage.setText(R.string.add_details_reschedule_dialogue_message);
            binding.btnSchudle.setText(R.string.reschedule_appointment);
            binding.btnConfirm.setText("Modify Details");
        }else {
            binding.txtMessage.setText(R.string.add_details_dialogue_message);
        }

       // binding.txtMessage.setText(R.string.add_details_dialogue_message);
        binding.btnCancel.setText(R.string.cancel);


        binding.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                // moveToHome();
                dialog.dismiss();
                ((DoctorBaseFrag) getParentFragment()).openConfirmAppointment();
            }
        });
        binding.btnSchudle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Constant.isSchedule = true;
                ((DoctorBaseFrag) getParentFragment()).openConfirmAppointment();
                // moveToHome();
            }
        });
        binding.btnCancel.setOnClickListener(v -> {
                    dialog.dismiss();
                }
        );
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("intentResult", String.valueOf(resultCode));
        if (resultCode == 6) {
            getCreditCardList();
        } else if (resultCode == 7) {
            getCreditCardList();
        }

        CardEntry.handleActivityResult(data, result -> {
            if (result.isSuccess()) {
                CardDetails cardResult = result.getSuccessValue();
                Card card = cardResult.getCard();
                String nonce = cardResult.getNonce();
            } else if (result.isCanceled()) {
                Toast.makeText(mContext,
                        "Cancelled",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });
        getCreditCardList();
    }
}
