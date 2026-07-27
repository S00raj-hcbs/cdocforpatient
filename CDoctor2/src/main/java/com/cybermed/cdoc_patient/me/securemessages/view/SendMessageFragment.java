package com.cybermed.cdoc_patient.me.securemessages.view;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.cdfortis.datainterface.soap.UserInfo;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.annotation.Factory;
import com.cybermed.cdoc_patient.common.BaseFragment;
import com.cybermed.cdoc_patient.databinding.FragSendMessageBinding;
import com.cybermed.cdoc_patient.me.manager.IReponseMessageCall;
import com.cybermed.cdoc_patient.me.manager.SecureMessagesManager;
import com.cybermed.cdoc_patient.me.securemessages.ISecureCallback;
import com.cybermed.cdoc_patient.me.securemessages.adapter.SearchAdapter;
import com.cybermed.cdoc_patient.me.securemessages.model.ProvidersItem;
import com.cybermed.cdoc_patient.me.securemessages.model.RequestSendMessage;
import com.cybermed.cdoc_patient.me.securemessages.model.ResponseProvidersList;

import androidx.databinding.DataBindingUtil;

import static com.cybermed.cdoc_patient.me.MeFragment.USERINFOKEY;


public class SendMessageFragment extends BaseFragment implements IReponseMessageCall {
    SecureMessagesManager manager;
    SearchAdapter<ProvidersItem> adapter;
    ISecureCallback iSecureMessage;
    String providerId;
    UserInfo userInfo;
    FragSendMessageBinding binding;


    @Factory
    public static SendMessageFragment newInstance(UserInfo userInfo) {
        SendMessageFragment fragment = new SendMessageFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putSerializable(USERINFOKEY, userInfo);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_send_message, container, false);
        return binding.getRoot();
    }

    @Override
    protected void initLayout(View view) {
        Bundle args = getArguments();
        if (args != null) {
            userInfo = (UserInfo) args.getSerializable(USERINFOKEY);
        }
        callProviderListApi();

        clickListners();
    }


    private void clickListners() {
        int maxLength = 300;
        Log.e("patient_id",""+userInfo.getEmail());

        binding.edtBody.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int currentLength = s.length();
                binding.txtedtlength.setText("(" + currentLength + "/" + maxLength + ")");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString()) && !TextUtils.isEmpty(providerId)) {
                    binding.btnSend.setEnabled(true);
                } else {
                    binding.btnSend.setEnabled(false);
                }
            }
        });
        binding.txvReceipnt.setOnClickListener(v -> {
            if (adapter != null) {
                Dialog dialog = showSearchDialog(adapter, () -> {

                });
                adapter.setItemSelectedListener((item, position) -> {
                    binding.txvReceipnt.setText(item.getProviderName());
                    providerId = item.getProviderId();
                    Log.e("provider",""+providerId);
                    dialog.cancel();

                });
            }
        });
        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestSendMessage secureMessage = new RequestSendMessage();
                secureMessage.setMsgBody(binding.edtBody.getText().toString());
                secureMessage.setMsgSubject(binding.edtSubject.getText().toString());
                secureMessage.setPatientId(userInfo.getEmail());
                secureMessage.setProviderId(providerId);
                manager.sendMessage(secureMessage);
            }
        });
    }

    private void callProviderListApi() {
        manager = new SecureMessagesManager(this);
        manager.getProviderList(userInfo.getEmail());
        showProgress();
    }


    @Override
    public void onSendMessageSuccess(Object data) {
        hideProgress();
        Toast.makeText(getActivity(), getString(R.string.send_message_result), Toast.LENGTH_SHORT).show();
        iSecureMessage.sendMessageCallBack();
    }

    @Override
    public void onFailure(@NonNull String errorResponse) {
        hideProgress();
        Toast.makeText(getActivity(), getString(R.string.send_message_fail_result), Toast.LENGTH_SHORT).show();
        iSecureMessage.sendMessageCallBack();
    }

    @Override
    public void onProviderListSuccess(Object data) {
        hideProgress();
        ResponseProvidersList providersList = (ResponseProvidersList) data;
        adapter = new SearchAdapter<>(providersList.getProviders(), true);
    }

    public void setListner(ISecureCallback iSecureMessage) {
        this.iSecureMessage = iSecureMessage;
    }

}
