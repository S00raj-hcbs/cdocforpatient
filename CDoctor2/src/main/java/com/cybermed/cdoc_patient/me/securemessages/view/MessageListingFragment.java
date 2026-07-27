package com.cybermed.cdoc_patient.me.securemessages.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cdfortis.datainterface.soap.UserInfo;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.annotation.Factory;
import com.cybermed.cdoc_patient.common.BaseFragment;
import com.cybermed.cdoc_patient.databinding.FragSecureMessageBinding;
import com.cybermed.cdoc_patient.me.securemessages.adapter.MessageListingAdapter;
import com.cybermed.cdoc_patient.me.securemessages.model.ReceivedMessagesItem;

import java.util.ArrayList;
import java.util.Collections;

import static com.cybermed.cdoc_patient.common.videoui.Constant.SHOW_MESSAGE;
import static com.cybermed.cdoc_patient.me.MeFragment.USERINFOKEY;


public class MessageListingFragment extends BaseFragment {

    FragSecureMessageBinding binding;

    @Factory
    public static MessageListingFragment newInstance(UserInfo userInfo) {
        MessageListingFragment fragment = new MessageListingFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putSerializable(USERINFOKEY, userInfo);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.frag_secure_message,container,false);
        binding.recyclerView.setBackgroundColor(getResources().getColor(R.color.color_ecf6fb));
        return binding.getRoot();
    }

    @Override
    protected void initLayout(View view) {
        binding.linTabs.setVisibility(View.GONE);
        binding.newMsgBtn2.setVisibility(View.GONE);
      /*  binding.floatingActionButton.setVisibility(View.GONE);*/
        Bundle arg = getArguments();
        if (arg != null) {
            ArrayList<ReceivedMessagesItem> data = arg.getParcelableArrayList(SHOW_MESSAGE);
            Collections.reverse(data);
            setRecyclerView(data);
        }
        binding.refreshLayout.setOnRefreshListener(() -> {
            binding.refreshLayout.setRefreshing(false);
        });

    }


    void setRecyclerView(ArrayList<ReceivedMessagesItem> data) {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        MessageListingAdapter messageAdapter = new MessageListingAdapter(data);
        binding.recyclerView.setAdapter(messageAdapter);
    }


}
