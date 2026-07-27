package com.cdfortis.datainterface.soap.model;

import com.cdfortis.datainterface.JsonSerializable;

import org.json.JSONObject;

/**
 * Created by qinwe on 2017/5/5.
 */

public class ProviderReviewsInfo implements JsonSerializable {
    public String rating_score;
    public String comment;
    public String review_date;

    @Override
    public void deserialize(JSONObject jsonObject) {
        rating_score = jsonObject.optString("rating_score","");
        comment = jsonObject.optString("comment","");
        review_date = jsonObject.optString("review_date","");

    }

    @Override
    public void serialize(JSONObject jsonObject) {

    }

    public void deserialize(ProviderReviews providerReviews){
        rating_score = providerReviews.getProperty(0).toString();
        comment = providerReviews.getProperty(1).toString();
        review_date = providerReviews.getProperty(2).toString();

    }

    public String getRating_score() {
        return rating_score;
    }

    public void setRating_score(String rating_score) {
        this.rating_score = rating_score;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getReview_date() {
        return review_date;
    }

    public void setReview_date(String review_date) {
        this.review_date = review_date;
    }

}
