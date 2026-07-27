package com.cybermed.cdoc_patient.me.securemessages.view;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cdfortis.datainterface.soap.UserInfo;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.annotation.Factory;
import com.cybermed.cdoc_patient.common.BaseFragment;
import com.cybermed.cdoc_patient.common.videoui.Constant;
import com.cybermed.cdoc_patient.databinding.FragSecureMessageBinding;
import com.cybermed.cdoc_patient.me.MeFragment;
import com.cybermed.cdoc_patient.me.manager.SecureMessagesManager;
import com.cybermed.cdoc_patient.me.securemessages.ISecureCallback;
import com.cybermed.cdoc_patient.me.securemessages.adapter.MessageAdapter;
import com.cybermed.cdoc_patient.me.securemessages.model.ReceivedMessagesItem;
import com.cybermed.cdoc_patient.me.securemessages.model.ResponseReceivedMessage;
import com.cybermed.cdoc_patient.util.AppUtiltiy;
import com.cybermed.cdoc_patient.webapi.IResponseReceiver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import static com.cybermed.cdoc_patient.common.videoui.Constant.SHOW_MESSAGE;
import static com.cybermed.cdoc_patient.me.MeFragment.USERINFOKEY;


public class SecureMessageFragment extends BaseFragment implements IResponseReceiver<ResponseReceivedMessage>,
        MessageAdapter.ItemClickListner , MeFragment.OnInnerFragmentStatusChange{
    ISecureCallback iSecureMessage;
    UserInfo userInfo;
    SecureMessagesManager medicationManager;
    FragSecureMessageBinding binding;


    @Factory
    public static SecureMessageFragment newInstance(UserInfo userInfo) {
        SecureMessageFragment fragment = new SecureMessageFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putSerializable(USERINFOKEY, userInfo);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_secure_message, container, false);
        return binding.getRoot();
    }

    @Override
    protected void initLayout(View view) {
        binding.linTabs.setVisibility(View.VISIBLE);
        Bundle args = getArguments();
        if (args != null) {
            userInfo = (UserInfo) args.getSerializable(USERINFOKEY);
        }
        medicationManager = new SecureMessagesManager(this);
        if (Constant.isSelected.equals("0")){

            binding.tvInbox.setBackground(getResources().getDrawable(R.drawable.selected_tab_bg));
            binding.tvSend.setBackground(getResources().getDrawable(R.drawable.unselected_tab_bg));
            binding.tvInbox.setTextColor(Color.parseColor("#FFFFFF"));
            binding.tvSend.setTextColor(Color.parseColor("#515055"));
            callApi();
        }else {
            binding.tvSend.setBackground(getResources().getDrawable(R.drawable.selected_tab_bg));
            binding.tvInbox.setBackground(getResources().getDrawable(R.drawable.unselected_tab_bg));
            binding.tvSend.setTextColor(Color.parseColor("#FFFFFF"));
            binding.tvInbox.setTextColor(Color.parseColor("#515055"));
            callApi2();
        }
        binding.tvInbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.tvInbox.setBackground(getResources().getDrawable(R.drawable.selected_tab_bg));
                binding.tvSend.setBackground(getResources().getDrawable(R.drawable.unselected_tab_bg));
                binding.tvInbox.setTextColor(Color.parseColor("#FFFFFF"));
                binding.tvSend.setTextColor(Color.parseColor("#515055"));
               /* binding.relativeClinicvital.setVisibility(View.VISIBLE);
                binding.scrollview.setVisibility(View.GONE);*/
                Constant.isSelected="0";
                callApi();
            }
        });
        binding.tvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.tvSend.setBackground(getResources().getDrawable(R.drawable.selected_tab_bg));
                binding.tvInbox.setBackground(getResources().getDrawable(R.drawable.unselected_tab_bg));
                binding.tvSend.setTextColor(Color.parseColor("#FFFFFF"));
                binding.tvInbox.setTextColor(Color.parseColor("#515055"));
            /*    binding.relativeClinicvital.setVisibility(View.GONE);
                binding.scrollview.setVisibility(View.VISIBLE);*/
                Constant.isSelected="1";
                callApi2();
            }
        });

        binding.refreshLayout.setOnRefreshListener(() -> {
            /*callApi();*/
            if (Constant.isSelected.equals("0")){
                callApi();
            }else {
                callApi2();
            }
        });

        // binding.floatingActionButton.setOnClickListener(v -> iSecureMessage.sercureMessageCallBack());
    }


    public void callApi() {
        binding.tvInbox.setBackground(getResources().getDrawable(R.drawable.selected_tab_bg));
        binding.tvSend.setBackground(getResources().getDrawable(R.drawable.unselected_tab_bg));
        binding.tvInbox.setTextColor(Color.parseColor("#FFFFFF"));
        binding.tvSend.setTextColor(Color.parseColor("#515055"));
        medicationManager.getMessageList(userInfo.getEmail());
        showProgress();
    }


    public void callApi2() {
        medicationManager.getSentMessageList(userInfo.getEmail());
        showProgress();
    }


    @Override
    public void onSuccess(ResponseReceivedMessage data) {
        if (binding.refreshLayout.isRefreshing()) {
            binding.refreshLayout.setRefreshing(false);
        }
        hideProgress();
        if (data != null && data.getReceivedMessages() != null && data.getReceivedMessages().size() > 0) {
            showEmptyView(false);
            setRecyclerView(data.getReceivedMessages());
        } else {
            showEmptyView(true);
        }
    }

    @Override
    public void onFailure(@NonNull String errorResponse) {
        if (binding.refreshLayout.isRefreshing()) {
            binding.refreshLayout.setRefreshing(false);
        }
        showEmptyView(true);
        hideProgress();
    }

    void showEmptyView(boolean show) {
        if (show) {
            binding.noMsgView.setVisibility(View.VISIBLE);
            binding.refreshLayout.setVisibility(View.VISIBLE);
            binding.recyclerView.setVisibility(View.GONE);
            binding.newMsgBtn2.setVisibility(View.GONE);
            ((SecureMessageParent) getParentFragment()).addButton.setVisibility(View.VISIBLE);
        } else {
            binding.noMsgView.setVisibility(View.GONE);
            binding.refreshLayout.setVisibility(View.VISIBLE);
            binding.newMsgBtn2.setVisibility(View.VISIBLE);
            ((SecureMessageParent) getParentFragment()).addButton.setVisibility(View.VISIBLE);
        }
        binding.newMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SecureMessageParent) getParentFragment()).addSendMessage();
            }
        });
        binding.newMsgBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SecureMessageParent) getParentFragment()).addSendMessage();
            }
        });
    }

    /**
     * set recycler view
     *
     * @param data medication list data
     */
    void setRecyclerView(List<ReceivedMessagesItem> data) {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        HashMap<String, List<ReceivedMessagesItem>> hashMap = new HashMap<>();
        HashMap<String, List<ReceivedMessagesItem>> hashMap2 = new HashMap<>();

        for (ReceivedMessagesItem item : data) {

            if (Constant.isSelected.equals("0")&&!item.getMsgFrom().equalsIgnoreCase(userInfo.getEmail())){
                if (!hashMap.containsKey(item.getMsgFrom())) {
                    List<ReceivedMessagesItem> messagesItems = new ArrayList<>();
                    messagesItems.add(item);
                    hashMap.put(item.getMsgFrom(), messagesItems);
                } else {
                    List<ReceivedMessagesItem> messagesItems = hashMap.get(item.getMsgFrom());
                    messagesItems.add(item);
                    hashMap.put(item.getMsgFrom(), messagesItems);
                }
                SortedMap<String, List<ReceivedMessagesItem>> sortedMap2 = new TreeMap<>(new AppUtiltiy.MessageListingComprator(hashMap));
                sortedMap2.putAll(hashMap);
               /* SortedMap<String, List<ReceivedMessagesItem>> sortedMap =
                        new TreeMap<>((s1, s2) -> {
                            List<ReceivedMessagesItem> list1 = hashMap.get(s1);
                            List<ReceivedMessagesItem> list2 = hashMap.get(s2);
                            if (list1 == null || list1.isEmpty()) return 1;
                            if (list2 == null || list2.isEmpty()) return -1;

                            String date1 = list1.get(0).getMsgSendDate(); // newest message (already sorted)
                            String date2 = list2.get(0).getMsgSendDate();
                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.getDefault());
                                Date d1 = sdf.parse(date1);
                                Date d2 = sdf.parse(date2);
                                return d2.compareTo(d1); // newest sender first
                            } catch (Exception e) {
                                return 0;
                            }
                        });

                sortedMap.putAll(hashMap);*/
                /*SortedMap<String, List<ReceivedMessagesItem>> sortedMap = new TreeMap<>(new AppUtiltiy.MessageListingComprator(hashMap));
                sortedMap.putAll(hashMap);*/
                /*SortedMap<String, List<ReceivedMessagesItem>> sortedMap = new TreeMap<>(new AppUtiltiy.MessageListingComprator(sortedMap2));
                sortedMap.putAll(sortedMap2);*/
                MessageAdapter messageAdapter = new MessageAdapter(sortedMap2, this, getActivity());
                binding.recyclerView.setAdapter(messageAdapter);
            }else if (Constant.isSelected.equals("1")){
                if (!hashMap2.containsKey(item.getMsgTo())) {
                    List<ReceivedMessagesItem> messagesItems = new ArrayList<>();
                    messagesItems.add(item);
                    hashMap2.put(item.getMsgTo(), messagesItems);
                } else {
                    List<ReceivedMessagesItem> messagesItems = hashMap2.get(item.getMsgTo());
                    messagesItems.add(item);
                    hashMap2.put(item.getMsgTo(), messagesItems);
                }
                /*SortedMap<String, List<ReceivedMessagesItem>> sortedMap =
                        new TreeMap<>((s1, s2) -> {
                            List<ReceivedMessagesItem> list1 = hashMap2.get(s1);
                            List<ReceivedMessagesItem> list2 = hashMap2.get(s2);
                            if (list1 == null || list1.isEmpty()) return 1;
                            if (list2 == null || list2.isEmpty()) return -1;

                            String date1 = list1.get(0).getMsgSendDate(); // newest message (already sorted)
                            String date2 = list2.get(0).getMsgSendDate();
                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.getDefault());
                                Date d1 = sdf.parse(date1);
                                Date d2 = sdf.parse(date2);
                                return d2.compareTo(d1); // newest sender first
                            } catch (Exception e) {
                                return 0;
                            }
                        });

                sortedMap.putAll(hashMap2);*/
                SortedMap<String, List<ReceivedMessagesItem>> sortedMap = new TreeMap<>(new AppUtiltiy.MessageListingComprator(hashMap2));
                sortedMap.putAll(hashMap2);
                MessageAdapter messageAdapter = new MessageAdapter(sortedMap, this, getActivity());
                binding.recyclerView.setAdapter(messageAdapter);
            }
        }
        /*SortedMap<String, List<ReceivedMessagesItem>> sortedMap = new TreeMap<>(new AppUtiltiy.MessageListingComprator(hashMap));
        sortedMap.putAll(hashMap);*/

    }


    public void setListner(ISecureCallback iSecureMessage) {
        this.iSecureMessage = iSecureMessage;
    }

    @Override
    public void itemClick(List<ReceivedMessagesItem> messagesItem) {
        //if (Constant.isSelected.equals("0")){
            binding.newMsgBtn2.setVisibility(View.GONE);
            binding.newMsgBtn.setVisibility(View.GONE);
            ((SecureMessageParent) getParentFragment()).addButton.setVisibility(View.GONE);
            iSecureMessage.setUserTittle(Constant.isSelected.equals("0")?messagesItem.get(0).getMsgFrom():messagesItem.get(0).getMsgTo());
            MessageListingFragment newFragment = MessageListingFragment.newInstance((UserInfo) getArguments().getSerializable(USERINFOKEY));
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(SHOW_MESSAGE, (ArrayList<? extends Parcelable>) messagesItem);
            newFragment.setArguments(bundle);
            ft.add(R.id.parent, newFragment).commit();
       // }
    }


    @Override
    public void onMyResume() {
        callApi();
    }

    @Override
    public void onMyStop() {

    }
}
