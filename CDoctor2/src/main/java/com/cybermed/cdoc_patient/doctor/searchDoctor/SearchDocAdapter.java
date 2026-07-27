package com.cybermed.cdoc_patient.doctor.searchDoctor;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cybermed.cdoc_patient.R;

import java.util.ArrayList;
import java.util.List;

/**
 * search doc adapter
 */
public class SearchDocAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<ResponseDocInfo> list;
    String onLineStatus;
    String providerCode;

    public SearchDocAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        list = new ArrayList<>();
    }

    public void refreshData(List<ResponseDocInfo> list) {
        this.list.clear();
        //Tablet Mode
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void appendList(List<ResponseDocInfo> list) {
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void setOnlineStatus(String onlineStatus, String providerCode) {
        this.onLineStatus = onlineStatus;
        this.providerCode = providerCode;
        for (ResponseDocInfo docInfo : list) {
            if (providerCode.equals(docInfo.getProviderCode())) {
                if (!docInfo.getOnlineStatus().equals(onlineStatus)) {
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
        // RatingBar starRating;
        TextView txtName, txtDpmt, txtLanguage, ratingNumber;
        RelativeLayout rel_rating;

        AppCompatRatingBar ratingBar;
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

            SearchDocAdapter.ViewHolder holder = new SearchDocAdapter.ViewHolder();
            holder.imgAvatar = v.findViewById(R.id.imgAvatar);
            holder.imgStatus = v.findViewById(R.id.imgStatus);
            holder.txtName = v.findViewById(R.id.txtName);
            holder.txtDpmt = v.findViewById(R.id.txtSpecialty);
            holder.txtLanguage = v.findViewById(R.id.txtDpmt);
            holder.favoriteIcon = v.findViewById(R.id.favoriteIcon);
            holder.imgStar = v.findViewById(R.id.imgstar);
            holder.ratingNumber = v.findViewById(R.id.ratingNumber);
            holder.linearBgRating = v.findViewById(R.id.linear_rating);
            holder.rel_rating = v.findViewById(R.id.rel_rating);
            holder.ratingBar = v.findViewById(R.id.ratingBar);
            v.setTag(holder);
        } else
            v = convertView;

        final SearchDocAdapter.ViewHolder holder = (SearchDocAdapter.ViewHolder) v.getTag();
        ResponseDocInfo docInfo = list.get(position);
        holder.txtName.setText(docInfo.getFirstName()
                + " " + docInfo.getLastName());
        if (!TextUtils.isEmpty(docInfo.getLanguages())) {
            holder.txtLanguage.setText(context.getString(R.string.doclist_filter_language) + " "+docInfo.getLanguages());

        } else {
            holder.txtLanguage.setText(context.getString(R.string.unspecified_language));

        }
        if (!TextUtils.isEmpty(docInfo.getLanguages())) {
            holder.txtDpmt.setText(context.getString(R.string.doclist_filter_specialty) + " "+ docInfo.getSpecialties());

        } else {
            holder.txtDpmt.setText(context.getString(R.string.doc_profile_specialty_not_listed));

        }

        if (!TextUtils.isEmpty(docInfo.getReviewScore())){
            holder.ratingBar.setRating(docInfo.getReviewScore().equals("0.0")?0.0f:Float.parseFloat(docInfo.getReviewScore()));
            holder.rel_rating.setVisibility(View.VISIBLE);
        }else {
            holder.ratingBar.setRating(docInfo.getReviewScore().equals("0.0")?0.0f:Float.parseFloat(docInfo.getReviewScore()));
            holder.rel_rating.setVisibility(View.GONE);
        }
        /*if (!TextUtils.isEmpty(docInfo.getReviewScore())) {
            String rating = String.format("%.1f", Float.parseFloat(docInfo.getReviewScore()));
            if (rating.equals("0.0")) {
                holder.ratingNumber.setVisibility(View.GONE);
                holder.imgStar.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.star_grey));
                holder.linearBgRating.setBackgroundColor(context.getResources().getColor(R.color.color_f1f1f1));
            } else {
                holder.ratingNumber.setText(rating);
                holder.ratingNumber.setVisibility(View.VISIBLE);
                holder.imgStar.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.star));
                holder.linearBgRating.setBackgroundColor(context.getResources().getColor(R.color.color_ecf6fb));
            }
        } else {
            holder.ratingNumber.setVisibility(View.GONE);
            holder.imgStar.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.star_grey));
            holder.linearBgRating.setBackgroundColor(context.getResources().getColor(R.color.color_f1f1f1));
        }*/
        if (!TextUtils.isEmpty(onLineStatus) && providerCode.equals(docInfo.getProviderCode())) {
            docInfo.setOnlineStatus(onLineStatus);
        }
        if ("1".equals(docInfo.getOnlineStatus())) {
            holder.imgStatus.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_green_online));
        } else if ("0".equals(docInfo.getOnlineStatus())) {
            holder.imgStatus.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_grey_busy));
        } else if ("2".equals(docInfo.getOnlineStatus())) {
            holder.imgStatus.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_red_offline));
        } else {
            holder.imgStatus.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_green_online));
        }

        Glide.with(context).asBitmap()
                .load(Base64.decode(docInfo.getProfileImage(), Base64.DEFAULT))
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_doc)
                        .dontAnimate())
                .into(holder.imgAvatar);


        if (!docInfo.getFavorite().equals("1")) {
            holder.favoriteIcon.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_fav_heart_unselect));
            //holder.favoriteIcon.setVisibility(View.INVISIBLE);
        } else {
            holder.favoriteIcon.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_fav_heart_select));
            //holder.favoriteIcon.setVisibility(View.VISIBLE);
        }

        return v;
    }


    public void clearList() {
        list.clear();
        notifyDataSetChanged();
    }
}