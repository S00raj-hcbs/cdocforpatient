package com.cybermed.cdoc_patient.appointment;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.annimon.stream.Stream;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.doctor.docDetail.model.ResApptList;
import com.cybermed.cdoc_patient.util.AppConstant;
import com.cybermed.cdoc_patient.util.DateUtil;

import org.jsoup.helper.StringUtil;
import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.List;

import static com.cybermed.cdoc_patient.common.BaseFragment.FUTUREAPPT;
import static com.cybermed.cdoc_patient.common.BaseFragment.PASTAPPT;
import com.cybermed.cdoc_patient.databinding.CallLogHeaderBinding;

public class MyApptRecyclerViewAdapter extends RecyclerView.Adapter<MyApptRecyclerViewAdapter.ViewHolder> {

    private final Context context;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private final List<ResApptList> displayedList; //the displayedList we display to user
    private final List<ResApptList> patientHistoryList; //the full displayedList

    private static final int VIEW_ITEM = 2;
    private static final int VIEW_HEADER = 1;
    boolean isFilter;
    // data is passed into the constructor
    MyApptRecyclerViewAdapter(Context context) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        displayedList = new ArrayList<>();
        patientHistoryList = new ArrayList<>();
    }

    public void appendList(List<ResApptList> list, String myApptTab,String is_filter) {
        // patientHistoryList.clear();
        patientHistoryList.addAll(list);
        //this.displayedList.clear();
        this.displayedList.addAll(list);
        this.isFilter=is_filter.equals("1")?true:false;
        // appTab = myApptTab;
        notifyDataSetChanged();
    }

    public List<ResApptList> getDisplayedList() {
        return displayedList;
    }

    public void clearList() {
        displayedList.clear();
    }


    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       /* View view = mInflater.inflate(R.layout.adapter_appointment_log, parent, false);
        return new ViewHolder(view);*/
        if (mInflater == null) {
            mInflater = LayoutInflater.from(parent.getContext());
        }
        View view;
        CallLogHeaderBinding callLogHeaderBinding;

        if (viewType == VIEW_ITEM) {
            view = mInflater.inflate(R.layout.adapter_appointment_log, parent, false);
            return new ViewHolder(view);
        } else{
            callLogHeaderBinding =
                    DataBindingUtil.inflate(mInflater, R.layout.call_log_header, parent, false);
            return new ViewHolder(callLogHeaderBinding);
        }


    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        ResApptList patAppt = displayedList.get(position);
        if (holder.getItemViewType() == VIEW_ITEM) {
            Glide.with(context).asBitmap()
                    .load(Base64.decode(patAppt.getProviderImage(), Base64.DEFAULT))
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_doc)
                            .dontAnimate())
                    .into(holder.imgDoctor);
            holder.txtName.setText(patAppt.getProviderFirstName() + " " + patAppt.getProviderLastName());
            if (patAppt.getApptType().equals(FUTUREAPPT)) {
                holder.relativePastAppt.setVisibility(View.GONE);
                holder.relativeUpAppt.setVisibility(View.VISIBLE);
                // holder.imgVideoCall.setVisibility(View.GONE);
            } else {
                holder.imgCancel.setVisibility(View.GONE);
                //  holder.imgVideoCall.setVisibility(View.VISIBLE);
                holder.relativePastAppt.setVisibility(View.VISIBLE);
                holder.relativeUpAppt.setVisibility(View.VISIBLE);
            }
            if ("1".equals(patAppt.getProvider_online_status())) {
                holder.imgStatusPro.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_green_online));
            } else if ("0".equals(patAppt.getProvider_online_status())) {
                holder.imgStatusPro.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_grey_busy));
            } else if ("2".equals(patAppt.getProvider_online_status())) {
                holder.imgStatusPro.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_red_offline));
            } else {
                holder.imgStatusPro.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_green_online));
            }
            if (!TextUtils.isEmpty(patAppt.getTalkTime()) && !patAppt.getTalkTime().equals("0") && !patAppt.getTalkTime().contains("-")) {
                String stringTalkTime = (!patAppt.getTalkTime().contains("min") ? (patAppt.getTalkTime() + context.getString(R.string.appt_mins)) : patAppt.getTalkTime());
                holder.txtTalkTime.setText(stringTalkTime);
                holder.linearDuration.setVisibility(View.VISIBLE);
                holder.txtDateUp.setVisibility(View.GONE);
            } else {
                holder.linearDuration.setVisibility(View.GONE);
                holder.txtDate.setVisibility(View.GONE);
                holder.txtDateUp.setVisibility(View.VISIBLE);
            }
            String charge = patAppt.getApptStatus() != null && patAppt.getApptStatus().equals("3") ? ("$" + patAppt.getAmountPaid()) : "";
            if (!TextUtils.isEmpty(charge)) {
                if (patAppt.getAmountPaid().startsWith("0.0")) {
                    holder.txtCharge.setText("$0.0");
                } else holder.txtCharge.setText(charge);
                holder.txtCharge.setVisibility(View.VISIBLE);
                holder.labelTxtCharge.setVisibility(View.VISIBLE);
            } else {
                holder.txtCharge.setVisibility(View.GONE);
                holder.labelTxtCharge.setVisibility(View.GONE);
            }

            holder.txtSpecialty.setText(StringUtil.isBlank(patAppt.getProviderSpecialties())?context.getString(R.string.speciality_not_available):patAppt.getProviderSpecialties());

            String[] apptStatusArray = context.getResources().getStringArray(R.array.appointment_status);
            String apptType = context.getString(R.string.video_appointment);
            String appointmentStatus = apptStatusArray[3];
            if (patAppt.getApptStatus() != null && !patAppt.getApptStatus().equals(" ") && !patAppt.getApptStatus().equals("")) {
                switch (Integer.parseInt(patAppt.getApptStatus())) {
                    case 0:
                    case 6:
                        appointmentStatus = apptStatusArray[5];
                        apptType = context.getString(R.string.video_appointment);
                        holder.status.setTextColor(ContextCompat.getColor(context, R.color.orange_0_2));
                        holder.txtScheduled.setTextColor(ContextCompat.getColor(context, R.color.orange_0_2));
                        holder.imgCancel.setVisibility(View.GONE);
                        if (DateUtil.checkSameDay(patAppt.getApptDate())) {
                            holder.rel_video_call.setVisibility(View.VISIBLE);
                            holder.txtScheduled.setVisibility(View.VISIBLE);
                            holder.rel_Reschedule.setVisibility(View.GONE);
                            holder.txtDateUp.setVisibility(View.VISIBLE);
                        }
                        else if (patAppt.getApptType().equals(PASTAPPT)) {
                            holder.imgCancel.setVisibility(View.GONE);
                            holder.status.setVisibility(View.VISIBLE);
                            holder.rel_video_call.setVisibility(View.GONE);
                            holder.txtScheduled.setVisibility(View.GONE);
                            holder.lineView.setVisibility(View.GONE);
                            holder.rel_Reschedule.setVisibility(View.VISIBLE);
                        } else if (patAppt.getApptType().equals(FUTUREAPPT)){
                            holder.imgCancel.setVisibility(View.GONE);
                            holder.rel_cancel2.setVisibility(View.VISIBLE);
                            holder.rel_video_call.setVisibility(View.GONE);
                            holder.status.setVisibility(View.GONE);
                            holder.txtScheduled.setVisibility(View.VISIBLE);
                            holder.rel_Reschedule.setVisibility(View.VISIBLE);
                            holder.relativePastAppt.setVisibility(View.VISIBLE);

                            holder.lineView.setVisibility(View.GONE);
                        }

                        if (patAppt.getApptType().equals(PASTAPPT)) {
                            if (Integer.parseInt(patAppt.getApptStatus())==0||Integer.parseInt(patAppt.getApptStatus())==6){
                                holder.status.setText(R.string.appointment_has_been_scheduled);
                                holder.rel_cancel2.setVisibility(View.VISIBLE);
                            }
                        }else {
                            holder.status.setText(appointmentStatus);
                        }
                        holder.txtScheduled.setText(appointmentStatus);
                        break;
                    case 3:
                    case 2:
                        appointmentStatus = apptStatusArray[1];
                        apptType = context.getString(R.string.video_appointment);
                        holder.status.setTextColor(ContextCompat.getColor(context, R.color.green_2));
                        holder.txtScheduled.setTextColor(ContextCompat.getColor(context, R.color.green_2));
                        holder.status.setText(appointmentStatus);
                        holder.txtScheduled.setText(appointmentStatus);
                        setView(holder,patAppt);
                        break;
                    case 1:
                        appointmentStatus = apptStatusArray[4];
                        apptType = context.getString(R.string.video_appointment);
                        holder.status.setTextColor(ContextCompat.getColor(context, R.color.red_2_1));
                        holder.txtScheduled.setTextColor(ContextCompat.getColor(context, R.color.red_2_1));
                        holder.status.setText(appointmentStatus);
                        holder.txtScheduled.setText(appointmentStatus);
                        setView(holder,patAppt);
                        break;
                    case 10:
                        appointmentStatus = apptStatusArray[6];
                        apptType = context.getString(R.string.clinic_appointment);
                        holder.status.setTextColor(ContextCompat.getColor(context, R.color.orange_0_2));
                        holder.txtScheduled.setTextColor(ContextCompat.getColor(context, R.color.orange_0_2));
                        holder.status.setText(appointmentStatus);
                        holder.txtScheduled.setText(appointmentStatus);
                        setView(holder,patAppt);
                        break;
                    case 11:
                        appointmentStatus = apptStatusArray[5];
                        apptType = context.getString(R.string.clinic_appointment);
                        holder.status.setTextColor(ContextCompat.getColor(context, R.color.orange_0_2));
                        holder.txtScheduled.setTextColor(ContextCompat.getColor(context, R.color.orange_0_2));
                        holder.status.setText(appointmentStatus);
                        holder.txtScheduled.setText(appointmentStatus);
                        setView(holder,patAppt);
                        break;
                    default:
                        apptType = context.getString(R.string.video_appointment);
                        appointmentStatus = apptStatusArray[3];
                        holder.status.setText(appointmentStatus);
                        holder.txtScheduled.setText(appointmentStatus);
                        break;
                }
            }

            String aTime = patAppt.getApptDate();
            String date = DateUtil.formatedDate(aTime, "MM/dd/yyyy hh:mm:ss aa", AppConstant.DATE_TIME_FORMAT);
            holder.txtDate.setText(date+" | "+apptType);
            holder.txtDateUp.setText(date+" | "+apptType);
            // holder.txtApptType.setText(apptType);
            //  holder.pastApptType.setText(apptType);
        }else {

            if (position == 0) {
                holder.headerBinding.viewDescriptor.setVisibility(View.GONE);
            } else{
                holder.headerBinding.viewDescriptor.setVisibility(View.GONE);

            }
            if (isFilter){
                holder.headerBinding.txtFilter.setVisibility(View.GONE);
                holder.headerBinding.txtHeader.setVisibility(View.GONE);
            }else {
                holder.headerBinding.txtHeader.setText(patAppt.getStartTime());
                holder.headerBinding.txtHeader.setVisibility(View.VISIBLE);
                holder.headerBinding.txtFilter.setVisibility(View.GONE);
            }
            holder.headerBinding.viewDescriptor.setVisibility(View.GONE);
            //holder.headerBinding.txtHeader.setText(patAppt.getStartTime());
        }


    }

    private void setView(ViewHolder holder,ResApptList patAppt) {
        holder.imgCancel.setVisibility(View.GONE);
        holder.txtScheduled.setVisibility(View.VISIBLE);
        holder.lineView.setVisibility(View.GONE);
        holder.rel_video_call.setVisibility(View.GONE);
        holder.relativePastAppt.setVisibility(View.VISIBLE);
        holder.status.setVisibility(View.GONE);
        holder.rel_Reschedule.setVisibility(View.VISIBLE);
        if (patAppt.getApptType().equals(PASTAPPT)) {
            if (Integer.parseInt(patAppt.getApptStatus())==1){
                holder.status.setText(R.string.your_appointment_is_cancelled);
            }
            else if (Integer.parseInt(patAppt.getApptStatus())==0||Integer.parseInt(patAppt.getApptStatus())==6||Integer.parseInt(patAppt.getApptStatus())==11){
                holder.status.setText(R.string.appointment_has_been_scheduled);
                holder.rel_cancel2.setVisibility(View.VISIBLE);
            }
            else if (Integer.parseInt(patAppt.getApptStatus())==10){
                holder.rel_cancel2.setVisibility(View.GONE);
                if (patAppt.getApptType().equals(PASTAPPT)) {
                    holder.txtScheduled.setVisibility(View.GONE);
                    holder.status.setVisibility(View.VISIBLE);
                    holder.status.setText(R.string.appointment_requested);
                }

            }else {
                holder.status.setText(R.string.appointment_has_been_scheduled);
                holder.rel_cancel2.setVisibility(View.VISIBLE);
            }
        }
        else if (patAppt.getApptType().equals(FUTUREAPPT)) {
            if (patAppt.getApptStatus() != null && !patAppt.getApptStatus().equals(" ") && !patAppt.getApptStatus().equals("")) {
                if (Integer.parseInt(patAppt.getApptStatus())==2||Integer.parseInt(patAppt.getApptStatus())==3||Integer.parseInt(patAppt.getApptStatus())==1){
                    holder.rel_video_call.setVisibility(View.GONE);
                    holder.rel_Reschedule.setVisibility(View.VISIBLE);
                    holder.rel_cancel2.setVisibility(View.GONE);
                }else if (Integer.parseInt(patAppt.getApptStatus())==10){
                    // holder.status.setText("You've requested an appointment!");
                    holder.rel_video_call.setVisibility(View.GONE);
                    holder.rel_Reschedule.setVisibility(View.VISIBLE);
                    holder.rel_cancel2.setVisibility(View.GONE);
                }else {
                    holder.rel_video_call.setVisibility(View.VISIBLE);
                    holder.btnVideoCall.setVisibility(View.GONE);
                    holder.rel_Reschedule.setVisibility(View.GONE);
                    holder.rel_cancel2.setVisibility(View.GONE);
                }



                //holder.txtApptType.setVisibility(View.VISIBLE);
                //  holder.txtApptTypeFuture.setVisibility(View.GONE);
            }

        } else {
            //  holder.linearPast.setVisibility(View.VISIBLE);
        }
        if (Integer.parseInt(patAppt.getApptStatus())==2||Integer.parseInt(patAppt.getApptStatus())==3){
            holder.rel_Reschedule.setVisibility(View.GONE);
            holder.status.setVisibility(View.VISIBLE);
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.green_2));
            holder.txtScheduled.setVisibility(View.GONE);
            holder.txtDate.setVisibility(View.VISIBLE);
            holder.status.setText(R.string.appointment_has_been_completed);
        }

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return displayedList.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtDate, txtName, txtTalkTime, txtSpecialty, txtCharge, labelTxtCharge;
        AppCompatTextView txtDateUp, txtScheduled, txtApptType;
        RelativeLayout relativePastAppt,btnVideoCall,rel_cancel2;
        RelativeLayout relativeUpAppt,rel_cancel;
        ImageView imgDoctor, imgCancel,imgVideoCall,imgStatusPro,imgChat;
        TextView status, pastApptType;
        Button btnScheduled;
        LinearLayout rel_video_call;
        LinearLayout linearDuration,rel_Reschedule;
        View lineView;
        CallLogHeaderBinding headerBinding;
        ViewHolder(View itemView) {
            super(itemView);
            labelTxtCharge = itemView.findViewById(R.id.label_txtCharge);
            linearDuration = itemView.findViewById(R.id.linear_duration);
            relativeUpAppt = itemView.findViewById(R.id.relative_upcomingappt);
            relativePastAppt = itemView.findViewById(R.id.relative_past);
            txtScheduled = itemView.findViewById(R.id.txtScheduled);
            txtDateUp = itemView.findViewById(R.id.txtDateUp);
            imgCancel = itemView.findViewById(R.id.img_cancel);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtName = itemView.findViewById(R.id.txtName);
            txtTalkTime = itemView.findViewById(R.id.txtTalkTime);
            txtSpecialty = itemView.findViewById(R.id.txtSpecialty);
            imgDoctor = itemView.findViewById(R.id.imgAvatar);
            imgStatusPro = itemView.findViewById(R.id.imgStatusPro);
            status = itemView.findViewById(R.id.btn_status);
            txtCharge = itemView.findViewById(R.id.txtCharge);
            btnVideoCall = itemView.findViewById(R.id.btn_videocall);
            rel_cancel = itemView.findViewById(R.id.rel_cancel);
            rel_video_call = itemView.findViewById(R.id.rel_video_call);
            btnScheduled = itemView.findViewById(R.id.btnScheduled);
            rel_cancel2 = itemView.findViewById(R.id.rel_cancel2);
            rel_Reschedule = itemView.findViewById(R.id.rel_Reschedule);
            txtApptType = itemView.findViewById(R.id.txtApptType);
            pastApptType = itemView.findViewById(R.id.txtPastApptType);
            lineView = itemView.findViewById(R.id.view);
            imgChat = itemView.findViewById(R.id.imgChat);
            imgVideoCall=itemView.findViewById(R.id.img_video_call);
            imgVideoCall.setOnClickListener(v -> mClickListener.videoAppt(displayedList.get(getAdapterPosition())));
            //itemView.setOnClickListener(this);
            imgCancel.setOnClickListener(v -> mClickListener.onItemClick(displayedList.get(getAdapterPosition()), true));
            btnScheduled.setOnClickListener(v -> mClickListener.onBtnScheduled(displayedList.get(getAdapterPosition())));
            btnVideoCall.setOnClickListener(v -> mClickListener.videoAppt(displayedList.get(getAdapterPosition())));
            imgChat.setOnClickListener(v -> mClickListener.chatAppt(displayedList.get(getAdapterPosition())));
            rel_cancel.setOnClickListener(v -> mClickListener.onItemClick(displayedList.get(getAdapterPosition()), true));
            rel_cancel2.setOnClickListener(v -> mClickListener.onItemClick(displayedList.get(getAdapterPosition()), true));

        }
        ViewHolder(CallLogHeaderBinding binding) {
            super(binding.getRoot());
            this.headerBinding = binding;
            headerBinding.txtFilter.setVisibility(View.GONE);

            headerBinding.txtFilter.setOnClickListener(v -> mClickListener.filterReset());
        }
        @Override
        public void onClick(View view) {
            //if (mClickListener != null)
            // mClickListener.onItemClick(displayedList.get(getAdapterPosition()), false);
        }
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }
    @Override
    public int getItemViewType(int position) {
        if (displayedList.get(position) != null &&
                displayedList.get(position).getViewType() == 2) {
            return VIEW_ITEM;
        } else
            return VIEW_HEADER;


    }
    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(ResApptList patientAppointment, boolean isFromDel);

        void onBtnScheduled(ResApptList patientAppointment);
        void chatAppt(ResApptList patientAppointment);
        void videoAppt(ResApptList patientAppointment);
        void filterReset();
    }

    public void filterList(ApptStatus apptStatus, LocalDate startDate, LocalDate endDate) {
        displayedList.clear();

        Stream.of(patientHistoryList).filter(patientAppointment -> statusMatch(apptStatus, patientAppointment) && timeMatch(startDate, endDate, patientAppointment))
                .forEach(patientAppointment -> displayedList.add(patientAppointment));

        notifyDataSetChanged();
    }

    private boolean statusMatch(ApptStatus apptStatus, ResApptList patientAppointment) {
        if (apptStatus == ApptStatus.All) {
            return true;
        }
        if (apptStatus.getCode().equals(patientAppointment.getApptStatus())) {
            return true;
        }
        return false;
    }

    private boolean timeMatch(LocalDate startDate, LocalDate endDate, ResApptList patientAppointment) {
        LocalDate date = DateUtil.stringToLocalDate(patientAppointment.getApptDate());
        return startDate.compareTo(date) <= 0 && date.compareTo(endDate) <= 0;
    }
}
