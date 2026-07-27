package com.cybermed.cdoc_patient.main;

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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.doctor.docDetail.model.ResApptList;
import com.cybermed.cdoc_patient.util.AppConstant;
import com.cybermed.cdoc_patient.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

import static com.cybermed.cdoc_patient.common.BaseFragment.FUTUREAPPT;

import org.jsoup.helper.StringUtil;

public class HomeApptAdapter extends RecyclerView.Adapter<HomeApptAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private final List<ResApptList> displayedList; //the displayedList we display to user
    private final List<ResApptList> patientHistoryList; //the full displayedList
    private String appTab;

    // data is passed into the constructor
    public HomeApptAdapter(Context context) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        displayedList = new ArrayList<>();
        patientHistoryList = new ArrayList<>();
    }

    public void appendList(List<ResApptList> list, String myApptTab) {
        patientHistoryList.clear();
        patientHistoryList.addAll(list);
        this.displayedList.clear();
        this.displayedList.addAll(
                list);
        appTab = myApptTab;

    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.home_appt_adapter, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        ResApptList patAppt = displayedList.get(position);

        Glide.with(context).asBitmap()
                .load(Base64.decode(patAppt.getProviderImage(), Base64.DEFAULT))
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_doc)
                        .dontAnimate())
                .into(holder.imgDoctor);
        if ("1".equals(patAppt.getProvider_online_status())) {
            holder.imgStatusPro.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_green_online));
        } else if ("0".equals(patAppt.getProvider_online_status())) {
            holder.imgStatusPro.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_grey_busy));
        } else if ("2".equals(patAppt.getProvider_online_status())) {
            holder.imgStatusPro.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_red_offline));
        } else {
            holder.imgStatusPro.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_green_online));
        }
        holder.txtName.setText(patAppt.getProviderFirstName() + " " + patAppt.getProviderLastName());
        if (appTab.equals(FUTUREAPPT)) {
            holder.imgVideoCall.setVisibility(View.GONE);
           // holder.txtApptTypeFuture.setVisibility(View.VISIBLE);
           // holder.txtApptType.setVisibility(View.GONE);
        } else {
            holder.imgVideoCall.setVisibility(View.VISIBLE);
           // holder.txtApptTypeFuture.setVisibility(View.GONE);
            holder.txtApptType.setVisibility(View.VISIBLE);
        }

        holder.txtSpecialty.setText(context.getString(R.string.speciality)+(StringUtil.isBlank(patAppt.getProviderSpecialties())?context.getString(R.string.speciality_not_available):patAppt.getProviderSpecialties()));
        if (!TextUtils.isEmpty(patAppt.getProviderLanguages())) {
            holder.view.setVisibility(View.GONE);
            holder.txtlang.setVisibility(View.GONE);
            holder.txtlang.setText(patAppt.getProviderLanguages());
        } else {
            holder.view.setVisibility(View.GONE);
            holder.txtlang.setVisibility(View.GONE);
        }
        String aTime = patAppt.getApptDate();
        String date = DateUtil.formatedDate(aTime, "MM/dd/yyyy hh:mm:ss aa", AppConstant.DATE_TIME_FORMAT);


        String[] apptStatusArray = context.getResources().getStringArray(R.array.appointment_status);
        String appointmentStatus = apptStatusArray[3];
        String apptType = context.getString(R.string.video_appointment);
        if (patAppt.getApptStatus() != null && !patAppt.getApptStatus().equals(" ") && !patAppt.getApptStatus().equals("")) {
            switch (Integer.parseInt(patAppt.getApptStatus())) {
                case 0:
                case 6:
                    holder.txtApptStatus.setText(R.string.appointment_has_been_scheduled);
                    holder.txtApptStatus.setTextColor(ContextCompat.getColor(context, R.color.colorScheduled));
                    apptType = context.getString(R.string.video_appointment);
                    if (DateUtil.checkSameDay(patAppt.getApptDate())) {
                        appointmentStatus = context.getString(R.string.video_call);
                        holder.rel_schedule.setVisibility(View.GONE);
                        if (appTab.equals(FUTUREAPPT)) {
                           // holder.btnVideoCall.setVisibility(View.VISIBLE);
                            holder.rel_video_call.setVisibility(View.VISIBLE);
                            holder.txtApptType.setVisibility(View.GONE);
                           // holder.txtApptTypeFuture.setVisibility(View.VISIBLE);
                        }
                    } else {
                      //  holder.btnVideoCall.setVisibility(View.GONE);
                        holder.rel_video_call.setVisibility(View.VISIBLE);
                        holder.btnVideoCall.setVisibility(View.GONE);
                        if (appTab.equals(FUTUREAPPT)) {
                            holder.rel_video_call.setVisibility(View.GONE);
                            holder.rel_schedule.setVisibility(View.VISIBLE);
                            holder.txtApptType.setVisibility(View.GONE);
                          //  holder.txtApptTypeFuture.setVisibility(View.VISIBLE);
                            appointmentStatus = context.getString(R.string.waiting_room);
                        } else {
                            appointmentStatus = apptStatusArray[5];
                           // holder.linearPast.setVisibility(View.VISIBLE);
                        }
                    }
                    break;
                case 3:
                case 2:
                    holder.txtApptStatus.setText(R.string.appointment_has_been_completed);
                    appointmentStatus = apptStatusArray[1];
                    apptType = context.getString(R.string.video_appointment);
                    holder.txtApptStatus.setTextColor(ContextCompat.getColor(context, R.color.colorCompleted));
                    setUi(holder,patAppt);
                    break;
                case 1:
                    holder.txtApptStatus.setText(R.string.your_appointment_is_cancelled);
                    appointmentStatus = apptStatusArray[4];
                    apptType = context.getString(R.string.video_appointment);
                    holder.txtApptStatus.setTextColor(ContextCompat.getColor(context, R.color.colorCancelled));
                    setUi(holder,patAppt);
                    break;
                case 10:
                    holder.txtApptStatus.setText(R.string.you_ve_requested_an_appointment);
                    appointmentStatus = apptStatusArray[6];
                    apptType = context.getString(R.string.clinic_appointment);
                    holder.txtApptStatus.setTextColor(ContextCompat.getColor(context, R.color.colorScheduled));
                    setUi(holder,patAppt);
                    break;
                case 11:
                    holder.txtApptStatus.setText(R.string.appointment_has_been_scheduled);
                    appointmentStatus = apptStatusArray[5];
                    apptType = context.getString(R.string.clinic_appointment);
                    holder.txtApptStatus.setTextColor(ContextCompat.getColor(context, R.color.colorScheduled));
                    setUi(holder,patAppt);
                    break;
                default:
                    appointmentStatus = apptStatusArray[3];
                    setUi(holder,patAppt);
                    holder.txtApptStatus.setText(R.string.appointment_has_been_scheduled);
                    holder.txtApptStatus.setTextColor(ContextCompat.getColor(context, R.color.colorScheduled));
                    /*appointmentStatus = apptStatusArray[5];
                    apptType = context.getString(R.string.clinic_appointment);
                    holder.txtApptStatus.setTextColor(ContextCompat.getColor(context, R.color.orange_0_2));
                    setUi(holder);*/
                    break;
            }
        }
        //holder.btnVideoCall.setText(appointmentStatus);
       // holder.txtApptStatus.setText(appointmentStatus);
        holder.txtDate.setText(date+" | "+apptType);
        //holder.txtApptType.setText(" | "+apptType);
     //   holder.txtApptTypeFuture.setText(apptType);

    }

    private void setUi(ViewHolder holder, ResApptList patAppt2 ) {
        if (appTab.equals(FUTUREAPPT)) {
            if (patAppt2.getApptStatus() != null && !patAppt2.getApptStatus().equals(" ") && !patAppt2.getApptStatus().equals("")) {
                if (Integer.parseInt(patAppt2.getApptStatus())==2||Integer.parseInt(patAppt2.getApptStatus())==3||Integer.parseInt(patAppt2.getApptStatus())==1){
                    holder.rel_video_call.setVisibility(View.GONE);
                    holder.rel_schedule.setVisibility(View.VISIBLE);
                    holder.rel_cancel2.setVisibility(View.GONE);
                }else if (Integer.parseInt(patAppt2.getApptStatus())==10){
                    holder.rel_video_call.setVisibility(View.GONE);
                    holder.rel_schedule.setVisibility(View.VISIBLE);
                    holder.rel_cancel2.setVisibility(View.GONE);
                }else {
                    holder.rel_video_call.setVisibility(View.VISIBLE);
                    holder.btnVideoCall.setVisibility(View.GONE);
                    holder.rel_schedule.setVisibility(View.GONE);
                    holder.rel_cancel2.setVisibility(View.GONE);
                }


                holder.txtApptStatus.setVisibility(View.VISIBLE);
                //holder.txtApptType.setVisibility(View.VISIBLE);
                //  holder.txtApptTypeFuture.setVisibility(View.GONE);
            }

        } else {
          //  holder.linearPast.setVisibility(View.VISIBLE);
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return displayedList.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtDate, txtName, txtlang, txtSpecialty, txtRating, txtApptType,txtApptTypeFuture,txtApptStatus;
        ImageView imgDoctor, imgFavUp, imgFavPast,imgVideoCall,imgStatusPro,imgChat;
        Button  btnClinicAppt, btnVideoAppt,btnWaiting;
        LinearLayout linearPast,rel_video_call,rel_schedule;
        RelativeLayout btnVideoCall,rel_cancel,rel_cancel2;
        View view;

        ViewHolder(View itemView) {
            super(itemView);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtName = itemView.findViewById(R.id.txtName);
            txtlang = itemView.findViewById(R.id.txtlang);
            txtSpecialty = itemView.findViewById(R.id.txtSpecialty);
            imgDoctor = itemView.findViewById(R.id.imgAvatar);
            imgStatusPro = itemView.findViewById(R.id.imgStatusPro);
            imgChat = itemView.findViewById(R.id.imgChat);
            btnVideoCall = itemView.findViewById(R.id.btn_videocall);
            rel_video_call = itemView.findViewById(R.id.rel_video_call);
            txtApptStatus = itemView.findViewById(R.id.txtStatus);
            btnWaiting = itemView.findViewById(R.id.btnWaitRoom);
           // btnClinicAppt = itemView.findViewById(R.id.btn_clinicappt);
           // btnVideoAppt = itemView.findViewById(R.id.btn_videoappt);
            txtRating = itemView.findViewById(R.id.txt_rating);
            imgFavPast = itemView.findViewById(R.id.img_rating_past);
            imgVideoCall = itemView.findViewById(R.id.img_video);
          //  linearPast = itemView.findViewById(R.id.linear_past);
            txtApptType = itemView.findViewById(R.id.txtApptType);
            txtApptTypeFuture=itemView.findViewById(R.id.txtApptTypeBottom);
            rel_cancel=itemView.findViewById(R.id.rel_cancel);
            rel_schedule=itemView.findViewById(R.id.rel_schedule);
            rel_cancel2=itemView.findViewById(R.id.rel_cancel2);
            view = itemView.findViewById(R.id.view);
            imgVideoCall.setOnClickListener(v -> mClickListener.videoAppt(displayedList.get(getAdapterPosition())));
            btnVideoCall.setOnClickListener(v -> mClickListener.videoAppt(displayedList.get(getAdapterPosition())));
            btnWaiting.setOnClickListener(v -> mClickListener.ReSchedualAppt(displayedList.get(getAdapterPosition())));
            imgChat.setOnClickListener(v -> mClickListener.chatAppt(displayedList.get(getAdapterPosition())));
           // btnVideoAppt.setOnClickListener(v -> mClickListener.makeAppt(displayedList.get(getAdapterPosition()), true));
          //  btnClinicAppt.setOnClickListener(v -> mClickListener.makeAppt(displayedList.get(getAdapterPosition()), false));
            rel_cancel.setOnClickListener(v -> mClickListener.onItemCancel(displayedList.get(getAdapterPosition()), true));
            rel_cancel2.setOnClickListener(v -> mClickListener.onItemCancel(displayedList.get(getAdapterPosition()), true));
            //rel_cancel.setVisibility(View.GONE);

        }


    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void makeAppt(ResApptList patientAppointment, boolean isVideoClinicAppt);

        void waitingAppt(ResApptList patientAppointment);
        void ReSchedualAppt(ResApptList patientAppointment);
        void chatAppt(ResApptList patientAppointment);

        void videoAppt(ResApptList patientAppointment);
        void onItemCancel(ResApptList patientAppointment, boolean isFrom);
       // void onItemClick(PatientAppointment patientAppointment, boolean isFrom);
    }


}
