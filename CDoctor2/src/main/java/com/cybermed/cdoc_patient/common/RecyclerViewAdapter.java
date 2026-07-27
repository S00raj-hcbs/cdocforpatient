package com.cybermed.cdoc_patient.common;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cybermed.cdoc_patient.R;

import java.util.List;

/**
 * Created by Juned on 3/27/2017.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyView> {

    private List<String> list;
    private List<Integer> availabilityList;
    private List<Integer> maxApptList;
    Context mContext;
    int selectedPosition=-1;

    public class MyView extends RecyclerView.ViewHolder {

        public TextView textView;
        public RelativeLayout rel_text_bg;
        //public CardView cardView;

        public MyView(View view) {
            super(view);

            textView = (TextView) view.findViewById(R.id.textview1);
            rel_text_bg = (RelativeLayout) view.findViewById(R.id.rel_text_bg);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedPosition=getAdapterPosition();
                    notifyDataSetChanged();
                }
            });
            // cardView = (CardView) view.findViewById(R.id.cardview);

        }
    }

    public void setApptAvailableList(List<Integer> availabilityList) {
        this.availabilityList.clear();
        this.availabilityList.addAll(availabilityList);
    }

    public void setMaxApptList(List<Integer> maxApptList) {
        this.maxApptList.clear();
        this.maxApptList.addAll(maxApptList);
    }

    public void refreshRecyclerView(List<String> horizontalList) {
        this.list.clear();
        this.list.addAll(horizontalList);
        Log.d("MAXAPPTDEBUG", "NOTIFY");
        selectedPosition=-1;
        notifyDataSetChanged();
    }
    public void clearList() {
        this.list.clear();
        notifyDataSetChanged();
    }


    public RecyclerViewAdapter(List<String> horizontalList, List<Integer> availabilityList, List<Integer> maxApptList, Context context) {
        this.list = horizontalList;
        this.availabilityList = availabilityList;
        this.maxApptList = maxApptList;
        mContext = context;
    }

    @Override
    public MyView onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_horizontal, parent, false);

        return new MyView(itemView);
    }

    @Override
    public void onBindViewHolder(final MyView holder, final int position) {

        holder.textView.setText(list.get(position));
        int bgColor, textColor;

        if (availabilityList != null && availabilityList.size() > 0 && maxApptList.get(position) > availabilityList.get(position)&& position==selectedPosition) {
            //If available. Bug, if original color not set, it will randomly become gray
            bgColor = ContextCompat.getColor(mContext, R.color.dark_slate_blue);
            textColor = ContextCompat.getColor(mContext, R.color.white_0_2);

        } else if (availabilityList != null && availabilityList.size() > 0 && maxApptList.get(position) <= availabilityList.get(position)) {
            bgColor = ContextCompat.getColor(mContext, R.color.gray_0_1);
            textColor = ContextCompat.getColor(mContext, R.color.white_0_2);
        } else {
            bgColor = ContextCompat.getColor(mContext, R.color.white_0_2);
            textColor = ContextCompat.getColor(mContext, R.color.dark_slate_blue);
        }
        holder.rel_text_bg.setBackgroundColor(bgColor);
        holder.textView.setTextColor(textColor);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }



}
