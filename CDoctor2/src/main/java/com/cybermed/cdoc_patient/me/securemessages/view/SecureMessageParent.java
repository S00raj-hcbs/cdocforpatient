package com.cybermed.cdoc_patient.me.securemessages.view;

import static com.cybermed.cdoc_patient.me.MeFragment.USERINFOKEY;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.cdfortis.datainterface.soap.UserInfo;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.annotation.Factory;
import com.cybermed.cdoc_patient.common.BaseFragment;
import com.cybermed.cdoc_patient.common.videoui.Constant;
import com.cybermed.cdoc_patient.databinding.FragSecureBinding;
import com.cybermed.cdoc_patient.main.FragmentMainActivity;
import com.cybermed.cdoc_patient.me.MeFragment;
import com.cybermed.cdoc_patient.me.securemessages.ISecureCallback;

public class SecureMessageParent extends BaseFragment implements ISecureCallback {

    FragmentMainActivity fragMain;
    SecureMessageFragment secureMessageFragment;
    FragSecureBinding binding;
    MeFragment meFragment;
    public ImageView addButton;
    Context mContext;

    @Factory
    public static SecureMessageParent newInstance(UserInfo userInfo) {
        SecureMessageParent fragment = new SecureMessageParent();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putSerializable(USERINFOKEY, userInfo);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_secure, container, false);
        return binding.getRoot();
    }

    @Override
    protected void initLayout(View view) {
        meFragment = (MeFragment) getParentFragment();
        mContext = getActivity();
        initToolBar();
        addSecureMessageFrag();

    }

    private void initToolBar() {
        addButton = binding.toolBar.icImg2;
        addButton.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_add_plus));
        binding.toolBar.backBtn.setOnClickListener(v -> {

            Fragment f = getChildFragmentManager().findFragmentById(R.id.parent);
            if (f instanceof MessageListingFragment || f instanceof SendMessageFragment) {
                addSecureMessageFrag();
            } else{
                Constant.isSelected="0";
                meFragment.openUserActivityFragment();
            }

        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSendMessage();
                //secureMessageFragment.callApi();
            }
        });

    }

    void addSecureMessageFrag() {
        addButton.setVisibility(View.VISIBLE);
        binding.toolBar.txtTittle.setText(R.string.secure_message);
        secureMessageFragment = SecureMessageFragment.newInstance((UserInfo) getArguments().getSerializable(USERINFOKEY));
        secureMessageFragment.setListner(this);
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.add(R.id.parent, secureMessageFragment).commit();
    }

    @Override
    public void sercureMessageCallBack() {
        addSendMessage();
    }

    void addSendMessage() {
        addButton.setVisibility(View.GONE);
        binding.toolBar.txtTittle.setText(R.string.send_message);
        SendMessageFragment newFragment = SendMessageFragment.newInstance((UserInfo) getArguments().getSerializable(USERINFOKEY));
        newFragment.setListner(this);
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.add(R.id.parent, newFragment).commit();
    }

    @Override
    public void sendMessageCallBack() {
        addSecureMessageFrag();
    }

    @Override
    public void setUserTittle(String userNameTittle) {
        binding.toolBar.txtTittle.setText(userNameTittle);
    }


}
