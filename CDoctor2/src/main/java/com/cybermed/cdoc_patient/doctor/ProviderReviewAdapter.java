package com.cybermed.cdoc_patient.doctor;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.recyclerview.widget.RecyclerView;

import com.cdfortis.datainterface.soap.VectorProviderReviews;
import com.cdfortis.datainterface.soap.model.ProviderReviews;
import com.cdfortis.datainterface.soap.model.ProviderReviewsInfo;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.util.DateUtil;

import org.jsoup.helper.StringUtil;

/**
 * Created by joshu on 7/11/2017.
 */

public class ProviderReviewAdapter extends RecyclerView.Adapter<ProviderReviewAdapter.ViewHolder> {
    private VectorProviderReviews list;


    public ProviderReviewAdapter(Context context, VectorProviderReviews providerReviews) {
        list = providerReviews;

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView comment;
        TextView userRating;
        AppCompatRatingBar ratingBar;
        TextView date;

        public ViewHolder(View v) {
            super(v);
            comment = v.findViewById(R.id.commentTxt);
            userRating = v.findViewById(R.id.userRating);
            date = v.findViewById(R.id.dateTxt);
            ratingBar = v.findViewById(R.id.ratingBar);
        }

    }


    public void setList(VectorProviderReviews list) {
        //this.list.clear();
        this.list = new VectorProviderReviews();
        this.list.addAll(list);
        //Collections.reverse(this.list);
        Log.d("providerreview", String.valueOf(list.size()));
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_review_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProviderReviews providerReviews = list.get(position);
        final ProviderReviewsInfo providerReviewsInfo = new ProviderReviewsInfo();
        providerReviewsInfo.deserialize(providerReviews);
        if (!TextUtils.isEmpty(providerReviewsInfo.comment)) {
            holder.comment.setVisibility(View.VISIBLE);
            holder.comment.setText(providerReviewsInfo.comment);
        } else holder.comment.setVisibility(View.GONE);
        //holder.userRating.setText(providerReviewsInfo.rating_score);

        holder.ratingBar.setRating(StringUtil.isBlank(providerReviewsInfo.rating_score)?0.0f:Float.parseFloat(providerReviewsInfo.rating_score));

        String date = DateUtil.formatedDate(providerReviewsInfo.review_date, "MM/dd/yyy hh:mm:ss a", "MMMM dd, yyy");
        String time = DateUtil.formatedDate(providerReviewsInfo.review_date, "MM/dd/yyy hh:mm:ss a", "hh:mm a");
        holder.date.setText(date + " | " + time);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }


}
