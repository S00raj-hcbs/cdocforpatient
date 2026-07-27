package com.cybermed.cdoc_patient.maps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cdfortis.datainterface.soap.VectorFavoritePharmacy;
import com.cdfortis.datainterface.soap.model.FavoritePharmacy;
import com.cybermed.cdoc_patient.R;

import java.util.List;

/**
 * Created by joshu on 6/6/2017.
 */

public class MapPharmacyAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private VectorFavoritePharmacy list;


    public MapPharmacyAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        list = new VectorFavoritePharmacy();


    }
    private class ViewHolder {
        TextView timeTxt;
        TextView callTypeTxt;
        TextView providerTxt;
        TextView talkMinTxt;
        TextView charge_type;
        TextView charge_amount;


    }

    public void appendList(List<FavoritePharmacy> list) {
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
        convertView = inflater.inflate(R.layout.adapter_call_log, null);

        MapPharmacyAdapter.ViewHolder holder = new MapPharmacyAdapter.ViewHolder();
        holder.timeTxt = (TextView) convertView.findViewById(R.id.timeTxt);
        holder.callTypeTxt = (TextView) convertView.findViewById(R.id.callType);
        holder.providerTxt = (TextView) convertView.findViewById(R.id.providerName);
        holder.talkMinTxt = (TextView) convertView.findViewById(R.id.talkMin);
        holder.charge_amount = (TextView) convertView.findViewById(R.id.chargeAmount);
        holder.charge_type = (TextView) convertView.findViewById(R.id.chargeType);

        FavoritePharmacy favoritePharmacy = list.get(position);

        holder.timeTxt.setText(favoritePharmacy.pharmacy_name);
        holder.callTypeTxt.setText(favoritePharmacy.city);

        return convertView;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

}
