package com.cybermed.cdoc_patient.doctor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cdfortis.datainterface.soap.model.FamilyInfo;
import com.cybermed.cdoc_patient.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joshu on 6/6/2017.
 */

public class SearchPatientAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<FamilyInfo> list;

    public SearchPatientAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        list = new ArrayList<>();
    }

    private class ViewHolder {
        TextView nameText;
        TextView relationshipTxt;
        ImageView iconOnlineStatus;

    }

    public void appendList(List<FamilyInfo> list) {
        if(list != null) {
            this.list.clear();
            this.list.addAll(list);
            notifyDataSetChanged();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.adapter_patient_search_list, null);

        SearchPatientAdapter.ViewHolder holder = new SearchPatientAdapter.ViewHolder();
        holder.nameText =  convertView.findViewById(R.id.txt_name);
       // holder.relationshipTxt = convertView.findViewById(R.id.txt_relationship);
        holder.iconOnlineStatus = convertView.findViewById(R.id.icon_online_status);

        FamilyInfo familyInfo = list.get(position);

        holder.nameText.setText(familyInfo.first_name + " " + familyInfo.last_name);
        holder.relationshipTxt.setText(context.getString(R.string.family_member_relation, familyInfo.relationshipToPatient));


        if(familyInfo.onlineStatus.equals("1")){
            holder.iconOnlineStatus.setImageResource(R.drawable.icon_online);
        } else if (familyInfo.onlineStatus.equals("2")){
            holder.iconOnlineStatus.setImageResource(R.drawable.icon_busy);
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public FamilyInfo getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


}
