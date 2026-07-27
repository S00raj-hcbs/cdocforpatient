package com.cybermed.cdoc_patient.doctor;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.core.content.ContextCompat;

import com.cdfortis.datainterface.soap.model.DocInfo;
import com.cybermed.cdoc_patient.R;

import java.util.List;
import java.util.Vector;

/**
 * Created by qinwe on 2017/5/3.
 */

public class DoctorAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private Vector<DocInfo> list;
    String onLineStatus;
    String providerCode;

    public DoctorAdapter(Context context, boolean isShowFoucs) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        list = new Vector<>();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void refreshData(List<DocInfo> list) {
        this.list.clear();
        //Tablet Mode
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void appendList(List<DocInfo> list) {
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void setOnlineStatus(String onlineStatus, String providerCode) {
        this.onLineStatus = onlineStatus;
        this.providerCode = providerCode;
        for (DocInfo docInfo : list) {
            if (providerCode.equals(docInfo.provider_code)) {
                if (!docInfo.getOnline_status().equals(onlineStatus)) {
                    notifyDataSetChanged();
                    break;
                } else {
                    break;
                }
            }
        }

    }

    private class ViewHolder {
        ImageView imgAvatar, imgStar;
        LinearLayout linearBgRating;
        ImageView imgStatus, favoriteIcon;
        RelativeLayout rel_rating;

        AppCompatRatingBar ratingBar;
        // RatingBar starRating;
        TextView txtName, txtDpmt, txtLanguage, ratingNumber;
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
        return position;
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        View v;
        if (convertView == null) {
            v = inflater.inflate(R.layout.adapter_doctor_list, null);

            ViewHolder holder = new ViewHolder();
            holder.imgAvatar = v.findViewById(R.id.imgAvatar);
            holder.imgStatus = v.findViewById(R.id.imgStatus);
            holder.txtName = v.findViewById(R.id.txtName);
            holder.txtDpmt = v.findViewById(R.id.txtSpecialty);
            holder.txtLanguage = v.findViewById(R.id.txtDpmt);
            holder.favoriteIcon = v.findViewById(R.id.favoriteIcon);
            holder.imgStar = v.findViewById(R.id.imgstar);
            holder.ratingNumber = v.findViewById(R.id.ratingNumber);
            holder.ratingBar = v.findViewById(R.id.ratingBar);
            holder.linearBgRating = v.findViewById(R.id.linear_rating);
            holder.rel_rating = v.findViewById(R.id.rel_rating);
            v.setTag(holder);
        } else
            v = convertView;

        final ViewHolder holder = (ViewHolder) v.getTag();
        DocInfo docInfo = list.get(position);
        holder.txtName.setText(docInfo.getName_prefix() + docInfo.getFirst_name()
                + " " + docInfo.getMi() + " " + docInfo.getLast_name() + docInfo.getName_suffix());
        if (!TextUtils.isEmpty(docInfo.getLanguages())) {
            holder.txtLanguage.setText(context.getString(R.string.doclist_filter_language) + " "+docInfo.getLanguages());

        } else {
            holder.txtLanguage.setText(context.getString(R.string.doclist_filter_language) + " "+"English");

        }
        if (!TextUtils.isEmpty(docInfo.getLanguages())) {
            holder.txtDpmt.setText(context.getString(R.string.doclist_filter_specialty) + " "+docInfo.getSpecialties());

        } else {
            holder.txtDpmt.setText(context.getString(R.string.doc_profile_specialty_not_listed));

        }
        if (!TextUtils.isEmpty(docInfo.getReview_score())){
            holder.ratingBar.setRating(docInfo.getReview_score().equals("0.0")?0.0f:Float.parseFloat(docInfo.getReview_score()));
            holder.rel_rating.setVisibility(View.VISIBLE);
        }else {
            holder.ratingBar.setRating(docInfo.getReview_score().equals("0.0")?0.0f:Float.parseFloat(docInfo.getReview_score()));
            holder.rel_rating.setVisibility(View.GONE);
        }

       /* if (!TextUtils.isEmpty(docInfo.getReview_score())) {
            String rating = String.format("%.1f", Float.parseFloat(docInfo.getReview_score()));
            if (rating.equals("0.0")) {
                holder.ratingNumber.setVisibility(View.GONE);
                holder.imgStar.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.star_grey));
                holder.linearBgRating.setBackgroundColor(ContextCompat.getColor(context,R.color.color_f1f1f1));
            } else {
                holder.ratingNumber.setText(rating);
                holder.ratingNumber.setVisibility(View.VISIBLE);
                holder.imgStar.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.star));
                holder.linearBgRating.setBackgroundColor(ContextCompat.getColor(context,R.color.color_ecf6fb));
            }
        } else {
            holder.ratingNumber.setVisibility(View.GONE);
            holder.imgStar.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.star_grey));
            holder.linearBgRating.setBackgroundColor(ContextCompat.getColor(context,R.color.color_f1f1f1));
        }*/
        if (!TextUtils.isEmpty(onLineStatus) && providerCode.equals(docInfo.getProvider_code())) {
            docInfo.setOnline_status(onLineStatus);
        }
        if ("1".equals(docInfo.getOnline_status())) {
            holder.imgStatus.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_green_online));
        } else if ("0".equals(docInfo.getOnline_status())) {
            holder.imgStatus.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_grey_busy));
        } else if ("2".equals(docInfo.getOnline_status())) {
            holder.imgStatus.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_red_offline));
        } else {
            holder.imgStatus.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_green_online));
        }


//        Glide.with(context)
//                .load("http://www.mycdoc.com/img/imghandler/providerphotohandler.ashx?provider=" + docInfo.provider_code + "&org_code=" + docInfo.org_code)
//                //.override(100, 100)
//                .apply(new RequestOptions()
//                        .centerCrop()
//                        .placeholder(R.drawable.ic_doc)
//                        .dontAnimate())
//                .into(holder.imgAvatar);


        if (!docInfo.favorite.equals("1")) {
            holder.favoriteIcon.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_fav_heart_unselect));
            //holder.favoriteIcon.setVisibility(View.INVISIBLE);
        } else {
            holder.favoriteIcon.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_fav_heart_select));
            //holder.favoriteIcon.setVisibility(View.VISIBLE);
        }

        return v;
    }


}

