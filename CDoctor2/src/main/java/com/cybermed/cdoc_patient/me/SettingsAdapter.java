package com.cybermed.cdoc_patient.me;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cybermed.cdoc_patient.BuildConfig;
import com.cybermed.cdoc_patient.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by joshu on 6/6/2017.
 */

public class SettingsAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<String> list;

    public SettingsAdapter(Context context) {
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

        SettingsAdapter.ViewHolder holder = new SettingsAdapter.ViewHolder();
        holder.dropDownImg = (ImageView) convertView.findViewById(R.id.dropDownImg);
        holder.titleText = (TextView) convertView.findViewById(R.id.infoLabel);
        holder.infoText = (TextView) convertView.findViewById(R.id.infoTxt);


        switch (position) {
            case 0:
                //holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_payment_black_24dp));
                holder.titleText.setText(context.getString(R.string.about_language));
                String currentLang = Locale.getDefault().getLanguage();
                if (currentLang.equals("en")) {
                    holder.infoText.setText(context.getString(R.string.about_english));
                } else if (currentLang.equals("zh")) {
                    holder.infoText.setText(context.getString(R.string.about_chinese));
                }
                break;
            case 1:
                holder.titleText.setText(context.getString(R.string.about_state));
                /*Ready to change*/
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                String defaultState = prefs.getString("state_key", "");
                /*Ready to change*/
                if(defaultState.equals("")){
                    defaultState = "All";
                }
                holder.infoText.setText(defaultState);
                break;
            case 2:
                holder.dropDownImg.setVisibility(View.GONE);
                holder.titleText.setText(context.getString(R.string.about_pharmacy));
                holder.infoText.setText(context.getString(R.string.about_pharmacy_activate));
                break;

        }
        return convertView;
    }

    @Override
    public int getCount() {
        if(BuildConfig.FLAVOR.equals("cybermedi8")){
            return 2;
        } else {
            return 3;
        }
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
