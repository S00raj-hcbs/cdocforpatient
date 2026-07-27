package com.cybermed.cdoc_patient.me.calllog.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.databinding.AdapterCallLogBinding;
import com.cybermed.cdoc_patient.databinding.CallLogHeaderBinding;
import com.cybermed.cdoc_patient.me.calllog.model.ResCallLog;
import com.cybermed.cdoc_patient.util.DateUtil;

import java.util.ArrayList;

import static com.cybermed.cdoc_patient.util.AppConstant.SERVER_DATE_FORMAT;
import static com.cybermed.cdoc_patient.util.AppConstant.TIME_FORMAT;

public class CallLogRecyclerViewAdapter extends RecyclerView.Adapter<CallLogRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private ArrayList<ResCallLog> list;
    private int lastVisibleItem, totalItemCount;
    private int visibleThreshold = 1;
    private static final int VIEW_ITEM = 2;
    private static final int VIEW_HEADER = 1;
    boolean isFilter;

    // data is passed into the constructor
    public CallLogRecyclerViewAdapter(Context context) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        list = new ArrayList<ResCallLog>();
    }

    public void appendList(ArrayList<ResCallLog> list, boolean isFilter) {
        this.list.clear();
        this.list.addAll(list);
        this.isFilter = isFilter;
        notifyDataSetChanged();
    }

    void clearList() {
        list.clear();
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //View view = mInflater.inflate(R.layout.adapter_call_log, parent, false);


        if (mInflater == null) {
            mInflater = LayoutInflater.from(parent.getContext());
        }
        AdapterCallLogBinding adapterCallLogBinding;
        CallLogHeaderBinding callLogHeaderBinding;

        if (viewType == VIEW_ITEM) {
            adapterCallLogBinding =
                    DataBindingUtil.inflate(mInflater, R.layout.adapter_call_log, parent, false);
            return new ViewHolder(adapterCallLogBinding);
        } else callLogHeaderBinding =
                DataBindingUtil.inflate(mInflater, R.layout.call_log_header, parent, false);
        return new ViewHolder(callLogHeaderBinding);

    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ResCallLog callLog = list.get(position);

        //Get start time
        if (holder.getItemViewType() == VIEW_ITEM) {
            holder.binding.timeTxt.setText(DateUtil.formatedDate(callLog.getStartTime(), SERVER_DATE_FORMAT, TIME_FORMAT));

            // temporary add charge amount to call type
            String  amountString = (TextUtils.isEmpty(callLog.getChargeAmount()) || callLog.getChargeAmount().equals("0")) ? context.getString(R.string.no_charges) : (context.getString(R.string.call_logs_charge_amount) +
                    callLog.getChargeAmount().substring(0, callLog.getChargeAmount().indexOf(".") + 3));
            //
            holder.binding.chargeType.setText(amountString);
            holder.binding.callType.setText(callLog.getCallType());
            holder.binding.providerName.setText(callLog.getProviderName());
            if (callLog.getCallType().contains("Outgoing")) {
                holder.binding.callImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.out_call));
            } else {
                holder.binding.callImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_incoming));
            }
            //Set mins and seconds of session
            try {
                int min = Integer.valueOf(callLog.getTalkMin());
                holder.binding.talkMin.setText(context.getString(R.string.call_logs_duration_time, min));
            } catch (NumberFormatException e) {
                holder.binding.talkMin.setText(context.getString(R.string.not_answered));
            }
            if (!TextUtils.isEmpty(callLog.getTalkMin())){
                try {
                    int min = Integer.parseInt(callLog.getTalkMin());
                    if (min > 60)
                        holder.binding.talkMin.setText(context.getString(R.string.call_logs_duration_time_hrs, min / 60, min % 60));
                    else if (min >1 && min < 60)
                        holder.binding.talkMin.setText(context.getString(R.string.call_logs_duration_time, min));
                    else if (min == 1)
                        holder.binding.talkMin.setText(context.getString(R.string.call_logs_duration_time2, min));
                    else
                        holder.binding.talkMin.setText(context.getString(R.string.not_answered));
                } catch (NumberFormatException e) {
                    holder.binding.talkMin.setText(context.getString(R.string.not_answered));
                }
            }else {
                holder.binding.talkMin.setText(context.getString(R.string.not_answered));
            }
        } else if (holder.getItemViewType() == VIEW_HEADER) {
            if (position == 0) {
                holder.headerBinding.viewDescriptor.setVisibility(View.GONE);
            } else holder.headerBinding.viewDescriptor.setVisibility(View.VISIBLE);
            if (isFilter) {
                holder.headerBinding.txtFilter.setVisibility(View.VISIBLE);
            } else {
                holder.headerBinding.txtFilter.setVisibility(View.GONE);
            }
            holder.headerBinding.txtHeader.setText(callLog.getStartTime());
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return list.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CallLogHeaderBinding headerBinding;
        AdapterCallLogBinding binding;

        ViewHolder(CallLogHeaderBinding binding) {
            super(binding.getRoot());
            this.headerBinding = binding;
            headerBinding.txtFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickListener != null)
                        mClickListener.filterReset();
                }
            });
        }

        ViewHolder(AdapterCallLogBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemClick(view, list.get(getAdapterPosition()));
        }
    }

    // convenience method for getting data at click position
//    String getItem(int id) {
//        return list.get(id);
//    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, ResCallLog callLog);

        void filterReset();
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position) != null &&
                list.get(position).getViewType() == 2) {
            return VIEW_ITEM;
        } else
            return VIEW_HEADER;


    }
}
