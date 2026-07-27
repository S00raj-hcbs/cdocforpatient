package com.cybermed.cdoc_patient.payment;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cdfortis.datainterface.soap.OnPostExecute;
import com.cdfortis.datainterface.soap.UserInfo;
import com.cdfortis.datainterface.soap.VectorCreditCard;
import com.cdfortis.datainterface.soap.WebService;
import com.cdfortis.datainterface.soap.WebServiceID;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.annotation.Factory;
import com.cybermed.cdoc_patient.common.BaseFragment;
import com.cybermed.cdoc_patient.common.CDoctor2Application;
import com.cybermed.cdoc_patient.databinding.ActivityCreditCardListBinding;
import com.cybermed.cdoc_patient.me.MeFragment;
import com.cybermed.cdoc_patient.util.ErrorMessage;
import com.cybermed.cdoc_patient.webapi.IResponseReceiver;
import com.cybermed.cdoc_patient.webapi.manager.HomeApiManager;
import com.cybermed.cdoc_patient.webapi.model.request.DeleteSquareCard;
import com.cybermed.cdoc_patient.webapi.model.response.ErrorResponse;
import com.cybermed.cdoc_patient.webapi.model.response.SquareCard;
import com.google.gson.Gson;

import org.ksoap2.serialization.SoapObject;

import java.util.List;

import sqip.Card;
import sqip.CardDetails;
import sqip.CardEntry;

import static com.cybermed.cdoc_patient.me.MeFragment.USERINFOKEY;


/**
 * Created by qinwe on 2017/5/3.
 */

public class PaymentCreditCardFrag extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener
        , View.OnClickListener {
    public static final int DEFAULT_CARD_ENTRY_REQUEST_CODE = 10101;
    private CreditCardAdapter adapter;
    private String userId;



    Context context;
    ActivityCreditCardListBinding mBinding;

    @Factory
    public static PaymentCreditCardFrag newInstance(UserInfo userInfo) {
        PaymentCreditCardFrag fragment = new PaymentCreditCardFrag();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putSerializable(USERINFOKEY, userInfo);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.activity_credit_card_list, container, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            WindowCompat.setDecorFitsSystemWindows(requireActivity().getWindow(), true);

            ViewCompat.setOnApplyWindowInsetsListener(mBinding.getRoot(), (v, insets) -> {
                return WindowInsetsCompat.CONSUMED;
            });
        }
        context = getActivity();
        return mBinding.getRoot();
    }

    @Override
    protected void initLayout(View view) {
        initToolBar();
        //initAddPayment(); // TODO double check if it working well
        // loadView = findViewById(R.id.loadView);
        userId = CDoctor2Application.getLoginInfo().getAccount();
        CreditCardAdapter.ICreditCardCallback deleteCardCallback = new CreditCardAdapter.ICreditCardCallback() {
            @Override
            public void delete(SquareCard squareCard) {
                HomeApiManager homeApiManager = new HomeApiManager(new IResponseReceiver() {
                    @Override
                    public void onSuccess(Object data) {
                        ErrorMessage.alertDialog(context, "Success", "Delete Success", null);
                        getCreditCardList();
                    }

                    @Override
                    public void onFailure(@NonNull String errorResponse) {
                        ErrorMessage.alertDialog(context, "Error", "Error happen on server", null);
                    }
                }, context);
                homeApiManager.deleteCard(new DeleteSquareCard(CDoctor2Application.getLoginInfo().getAccount(), squareCard.getId()));
            }

            @Override
            public void itemSelect(SquareCard squareCard) {
                final SquareCard creditCard = squareCard;


            }
        };
        adapter = new CreditCardAdapter(context, true, false, deleteCardCallback);

        mBinding.listView.setLayoutManager(new LinearLayoutManager(context));
        mBinding.listView.setAdapter(adapter);
        mBinding.addPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initAddPayment();
            }
        });
        mBinding.layEmptyView.btnAddEmptyCreditCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initAddPayment();
            }
        });
        loadData();
    }

    private void initAddPayment() {
        CardEntry.startCardEntryActivity(getActivity(), true, DEFAULT_CARD_ENTRY_REQUEST_CODE);
    }

    private void initToolBar() {
       mBinding.toolbar.txtTittle.setText( getString(R.string.doc_pay_heading));
        //removed addbtn to initAddPayment()
        mBinding.toolbar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((MeFragment) getParentFragment() != null)) {
                    ((MeFragment) getParentFragment()).openUserActivityFragment();
                }
            }
        });
        mBinding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });

    }




    private void loadData() {
        getCreditCardList();
    }


    /**
     * get credit card list
     */
    private void getCreditCardList() {
        showProgress();
        HomeApiManager apiManager = new HomeApiManager(new IResponseReceiver() {
            @Override
            public void onSuccess(Object data) {
                List<SquareCard> cards = (List<SquareCard>) data;
                adapter.appendList(cards);
                if (cards.size() == 0) {
                    mBinding.layEmptyView.linearEmptyView.setVisibility(View.VISIBLE);
                    checkOldCards(userId);
                } else {
                    mBinding.layEmptyView.linearEmptyView.setVisibility(View.GONE);
                    adapter.appendList(cards);
                }

                hideProgress();
                mBinding.swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(@NonNull String errorResponse) {
                hideProgress();
                mBinding.swipeRefreshLayout.setRefreshing(false);
                ErrorResponse error = new Gson().fromJson(errorResponse, ErrorResponse.class);
                ErrorMessage.alertDialog(context, "Error", error.getError(), null);
            }
        }, context);
        apiManager.getCreditCardList(CDoctor2Application.getLoginInfo().getAccount());
    }

    private void checkOldCards(String userId) {
        OnPostExecute ope = result -> {
            VectorCreditCard creditCardInfo = new VectorCreditCard((SoapObject) result);
            if (creditCardInfo.size() != 0) {
                ErrorMessage.alertDialog(context, "Notice", "We have updated our payment gateway system. Please re-enter your credit card information again.", null);
            }
        };
        WebService.webServiceAsyncTask(WebServiceID.get_CCInfo, ope, userId);
    }

    @Override
    public void onRefresh() {
        loadData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static final int PAYMENT_APPT_RETURN = 1001;
    public static final int PAYMENT_WAITING_RETURN = 1101;


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("intentResult", String.valueOf(resultCode));

        CardEntry.handleActivityResult(data, result -> {
            if (result.isSuccess()) {
                CardDetails cardResult = result.getSuccessValue();
                Card card = cardResult.getCard();
                String nonce = cardResult.getNonce();
            } else if (result.isCanceled()) {
                Toast.makeText(getActivity(),
                        "Cancelled",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });
        getCreditCardList();
    }

    @Override
    public void onClick(View v) {
    }


}
