package com.cybermed.cdoc_patient.doctor;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cdfortis.datainterface.soap.model.FamilyInfo;
import com.cybermed.cdoc_patient.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joshu on 6/6/2017.
 */

public class FamilyMemberAdapter extends RecyclerView.Adapter<FamilyMemberAdapter.MyViewHolder> {
    private ArrayList<FamilyInfo> list = new ArrayList<>();
    private ItemClickListener mClickListener;


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView nameText;
        TextView relationshipTxt;
        ImageView iconOnlineStatus;

        public MyViewHolder(View v) {
            super(v);
            nameText = v.findViewById(R.id.txt_name);
            //relationshipTxt = v.findViewById(R.id.txt_relationship);
            // iconOnlineStatus = v.findViewById(R.id.icon_online_status);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public FamilyMemberAdapter() {
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_patient_search_list, parent, false);

        MyViewHolder vh = new MyViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        FamilyInfo searchPatient = list.get(position);
        String name;
        if (!TextUtils.isEmpty(searchPatient.email)) {
            name = searchPatient.first_name + " " + searchPatient.last_name + "(" + searchPatient.email + ")";
        } else name = searchPatient.first_name + " " + searchPatient.last_name;
        holder.nameText.setText(name);

//        if (!TextUtils.isEmpty(searchPatient.relationshipToPatient)) {
//            holder.relationshipTxt.setVisibility(View.VISIBLE);
//            holder.relationshipTxt.setText(searchPatient.relationshipToPatient);
//        } else holder.relationshipTxt.setVisibility(View.GONE);
////        if (!TextUtils.isEmpty(searchPatient.onlineStatus)) {
////            if (searchPatient.onlineStatus.equals("1")) {
////                holder.iconOnlineStatus.setImageResource(R.drawable.icon_online);
////            } else if (searchPatient.onlineStatus.equals("2")) {
////                holder.iconOnlineStatus.setImageResource(R.drawable.icon_busy);
////            } else {
////                holder.iconOnlineStatus.setImageResource(R.drawable.icon_offline);
////            }
////        }
//        holder.iconOnlineStatus.setVisibility(View.GONE);

    }


    public FamilyInfo getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void clearList() {
        this.list.clear();
        notifyDataSetChanged();
    }

    public void appendList(List<FamilyInfo> list) {
        this.list.clear();
        this.list.addAll(list);
        if (mClickListener != null) {
            if (list != null && list.size() > 0)
                mClickListener.setView(true);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);

        void setView(boolean hasList);
    }

}
