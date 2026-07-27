package com.cybermed.cdoc_patient.video;

import android.os.Bundle;

import com.cybermed.cdoc_patient.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class VideoConsultation extends AppCompatActivity {
    RecyclerView apptRecyclerView;
    private RecyclerView.LayoutManager RecyclerViewLayoutManager;
  //  private VideoConsultationAdapter videoConsultationAdapter;
    private LinearLayoutManager HorizontalLayout;
    RecyclerView availibilityRecyclerView;
   // private AvailibilitySlotAdapter availibilitySlotAdapter;
    private RecyclerView.LayoutManager RecyclerViewLayoutManagerAvailibiltySlot;
    private LinearLayoutManager HorizontalLayoutAvailibiltySlot;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doc_booking_frag);
        initAppointmentTime();
       // getAvailibiltySlot();
    }

    private void initAppointmentTime(){
       // apptRecyclerView = findViewById(R.id.rv_apptdate);
        RecyclerViewLayoutManager = new LinearLayoutManager(getApplicationContext());
        apptRecyclerView.setLayoutManager(RecyclerViewLayoutManager);
        Calendar cal = Calendar.getInstance();
        ArrayList<String> apptDate = new ArrayList<String>();
        ArrayList<String> apptDay = new ArrayList<String>();
        for (int i=0; i < 7; i++) {
            cal.add(Calendar.DATE, i > 0 ? 1 : 0);
            String date = new SimpleDateFormat("d", Locale.US).format(cal.getTime());
            String day = new SimpleDateFormat("E", Locale.US).format(cal.getTime());
            apptDate.add(date);
            apptDay.add(day);
            cal = (Calendar)cal.clone();
        }
//        videoConsultationAdapter = new VideoConsultationAdapter(apptDate,apptDay);
//        HorizontalLayout = new LinearLayoutManager(VideoConsultation.this, LinearLayoutManager.HORIZONTAL, false);
//        apptRecyclerView.setLayoutManager(HorizontalLayout);
//        apptRecyclerView.setAdapter(videoConsultationAdapter);

    }

//    private void getAvailibiltySlot(){
//        availibilityRecyclerView = findViewById(R.id.rv_availslot);
//        RecyclerViewLayoutManagerAvailibiltySlot = new LinearLayoutManager(getApplicationContext());
//        availibilityRecyclerView.setLayoutManager(RecyclerViewLayoutManagerAvailibiltySlot);
//        Calendar cal = Calendar.getInstance();
//        ArrayList<String> availTime = new ArrayList<String>();
//        for (int i=0; i < 4; i++) {
//            cal.add(Calendar.MINUTE, i > 0 ? 15 : 0);
//            String time = new SimpleDateFormat("hh:mm aa", Locale.US).format(cal.getTime());
//            availTime.add(time);
//            cal = (Calendar)cal.clone();
//        }
//        availibilitySlotAdapter = new AvailibilitySlotAdapter(availTime);
//        HorizontalLayoutAvailibiltySlot = new LinearLayoutManager(VideoConsultation.this, LinearLayoutManager.HORIZONTAL, false);
//        availibilityRecyclerView.setLayoutManager(HorizontalLayoutAvailibiltySlot);
//        availibilityRecyclerView.setAdapter(availibilitySlotAdapter);
//
//
//    }
}
