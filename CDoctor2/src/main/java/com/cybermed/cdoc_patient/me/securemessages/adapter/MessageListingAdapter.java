package com.cybermed.cdoc_patient.me.securemessages.adapter;

import static com.cybermed.cdoc_patient.util.AppConstant.DATE_TIME_FORMAT;
import static com.cybermed.cdoc_patient.util.AppConstant.SERVER_DATE_FORMAT;
import static com.cybermed.cdoc_patient.util.AppConstant.TIME_FORMAT;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.databinding.AdapterImmunizationBinding;
import com.cybermed.cdoc_patient.databinding.MessageCustomUiBinding;
import com.cybermed.cdoc_patient.me.securemessages.model.ReceivedMessagesItem;
import com.cybermed.cdoc_patient.util.DateUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MessageListingAdapter extends RecyclerView.Adapter<MessageListingAdapter.MyViewHolder> {

    List<ReceivedMessagesItem> messageLists;

    public MessageListingAdapter(List<ReceivedMessagesItem> messageLists) {
        this.messageLists = messageLists;
    }


    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MessageCustomUiBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),R.layout.message_custom_ui, parent, false);
        return new MyViewHolder(binding);
    }


    @Override
    public int getItemCount() {
        return messageLists.size();
    }

    // stores and recycles views as they are scrolled off screen
    static class MyViewHolder extends RecyclerView.ViewHolder {


        MessageCustomUiBinding binding = null;
        MyViewHolder(MessageCustomUiBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }


    }


    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding.txtMessage.setText(messageLists.get(position).getMsgBody());
        holder.binding.txtTittle.setText(messageLists.get(position).getMsgSubject());/*
        String date = DateUtil.formatedDate(messageLists.get(position).getMsgSendDate(),
                "yyyy-MM-dd'T'HH:mm:ss", "MM/dd/yyy, HH:mm a");*/
        String date = DateUtil.formatedDate(messageLists.get(position).getMsgSendDate(),
                SERVER_DATE_FORMAT, DATE_TIME_FORMAT);
        String currentDate = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.US).format(Calendar.getInstance().getTime());
        if (date.equals(currentDate)) {
            String time = DateUtil.formatedDate(messageLists.get(0).getMsgSendDate(),
                    SERVER_DATE_FORMAT, TIME_FORMAT);
            holder.binding.txtDate.setText(time);
        } else {
            holder.binding.txtDate.setText(date);
        }
        holder.binding.txtDate.setText(date);
    }


}
