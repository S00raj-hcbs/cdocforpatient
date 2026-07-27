package com.cybermed.cdoc_patient.me.securemessages.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.videoui.Constant;
import com.cybermed.cdoc_patient.databinding.AdapterImmunizationBinding;
import com.cybermed.cdoc_patient.me.securemessages.model.ReceivedMessagesItem;
import com.cybermed.cdoc_patient.util.DateUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.cybermed.cdoc_patient.util.AppConstant.DATE_TIME_FORMAT;
import static com.cybermed.cdoc_patient.util.AppConstant.SERVER_DATE_FORMAT;
import static com.cybermed.cdoc_patient.util.AppConstant.TIME_FORMAT;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    Map<String, List<ReceivedMessagesItem>> messageLists;
    ItemClickListner itemClickListner;
    Context mContext;

    public MessageAdapter(Map<String, List<ReceivedMessagesItem>> messageLists,
                          ItemClickListner itemClickListner, Context context) {
        mContext=context;
        this.messageLists = messageLists;
        this.itemClickListner = itemClickListner;
    }


    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterImmunizationBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.adapter_immunization, parent, false);
        return new MyViewHolder(binding);
    }


    @Override
    public int getItemCount() {
        return messageLists.size();
    }

    // stores and recycles views as they are scrolled off screen
    class MyViewHolder extends RecyclerView.ViewHolder {


        AdapterImmunizationBinding binding = null;

        MyViewHolder(AdapterImmunizationBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;

        }


    }


    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        List<ReceivedMessagesItem> messagesItem = messageLists.get(messageLists.keySet().toArray()[position]);
        /*if (!messagesItem.isEmpty() && messagesItem.size()>1){

        }*/
        /*if (messagesItem == null || messagesItem.isEmpty()) return;
        sortDate(messagesItem);*/
        sortDate(messagesItem);
        if (messagesItem != null && !messagesItem.isEmpty()) {
            holder.binding.txtReceiverName.setText(Constant.isSelected.equals("0")?messagesItem.get(0).getMsgFrom():messagesItem.get(0).getMsgTo());
            holder.binding.txtMessage.setText(messagesItem.get(messagesItem.size()-1).getMsgBody());
            String date = DateUtil.formatedDate(messagesItem.get(messagesItem.size()-1).getMsgSendDate(),
                    SERVER_DATE_FORMAT, DATE_TIME_FORMAT);
            String currentDate = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.US).format(Calendar.getInstance().getTime());
            if (date.equals(currentDate)) {
                String time = DateUtil.formatedDate(messagesItem.get(0).getMsgSendDate(),
                        SERVER_DATE_FORMAT, TIME_FORMAT);
                holder.binding.txtDate.setText(time);
            } else {
                holder.binding.txtDate.setText(date);
            }
            holder.binding.txtDate.setText(date);
            holder.binding.txtDate.setTextColor(ContextCompat.getColor(mContext, R.color.azure));

            if (messagesItem.size() > 1) {
                holder.binding.txtNum.setVisibility(View.VISIBLE);
                holder.binding.txtNum.setText(String.valueOf(messagesItem.size()));
                if (messagesItem.size() < 10) {
                    holder.binding.txtNum.setBackground(ContextCompat.getDrawable(mContext,R.drawable.bg_blue_circle));
                } else {
                    holder.binding.txtNum.setBackground(ContextCompat.getDrawable(mContext,R.drawable.ic_message_num));
                }
            } else {
                holder.binding.txtNum.setVisibility(View.GONE);
                holder.binding.txtMessage.setMaxLines(50);
            }

            holder.binding.relative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (messagesItem != null && messagesItem.size() > 1)
                        itemClickListner.itemClick(messagesItem);
                }
            });
        }

    }

    public void sortDate(List<ReceivedMessagesItem> messagesItem) {
        Collections.sort(messagesItem, new Comparator<ReceivedMessagesItem>() {
            public int compare(ReceivedMessagesItem o1, ReceivedMessagesItem o2) {
                if (o1.getMsgSendDate() == null || o2.getMsgSendDate() == null)
                    return 0;
                return o1.getMsgSendDate().compareTo(o2.getMsgSendDate());
            }
        });
    }

    public interface ItemClickListner {
        void itemClick(List<ReceivedMessagesItem> messagesItem);
    }

}
