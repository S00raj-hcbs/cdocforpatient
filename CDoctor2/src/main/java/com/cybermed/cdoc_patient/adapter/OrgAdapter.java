package com.cybermed.cdoc_patient.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cdfortis.datainterface.soap.model.VisitRecord;
import com.cybermed.cdoc_patient.R;

import java.util.Vector;

public class OrgAdapter extends BaseAdapter {

    Context context;
    Vector<VisitRecord> visitRecords;


    public OrgAdapter(Context context, Vector<VisitRecord> visitRecords) {
        this.context = context;
        this.visitRecords = visitRecords;
    }

    @Override
    public int getCount() {
        return visitRecords.size();
    }

    @Override
    public Object getItem(int position) {
        return visitRecords.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view;
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.spinner_organization_code, null);
        } else {
            view = convertView;
        }

        ((TextView) view.findViewById(R.id.organization)).setText(visitRecords.get(position).org_name);

        return view;
    }
}
