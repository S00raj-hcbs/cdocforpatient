package com.cybermed.cdoc_patient.me;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cybermed.cdoc_patient.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joshu on 6/6/2017.
 */

public class AboutAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<String> list;

    public AboutAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        list = new ArrayList<String>();


    }
    private class ViewHolder {
        ImageView dropDownImg;
        TextView titleText;
        TextView infoText;

    }

    public void appendList(List<String> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        //User user = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        //if (convertView == null) {
        convertView = inflater.inflate(R.layout.adapter_about, null);

        AboutAdapter.ViewHolder holder = new AboutAdapter.ViewHolder();
        holder.dropDownImg = (ImageView) convertView.findViewById(R.id.dropDownImg);
        holder.titleText = (TextView) convertView.findViewById(R.id.infoLabel);
        holder.infoText = (TextView) convertView.findViewById(R.id.infoTxt);


        switch(position){
            case 0:
                //holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_account_circle_black_24dp));
                holder.dropDownImg.setVisibility(View.GONE);
                holder.infoText.setText(list.get(0));
                break;

        }
        return convertView;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


}
