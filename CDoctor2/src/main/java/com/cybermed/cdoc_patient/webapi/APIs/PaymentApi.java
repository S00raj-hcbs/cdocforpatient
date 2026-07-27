package com.cybermed.cdoc_patient.webapi.APIs;

import com.cybermed.cdoc_patient.webapi.model.request.AddSquareCard;
import com.cybermed.cdoc_patient.webapi.model.request.ApptPayment;
import com.cybermed.cdoc_patient.webapi.model.request.DeleteSquareCard;
import com.cybermed.cdoc_patient.webapi.model.response.SquareCard;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PaymentApi {

    @GET("hub/payment/get_patient_square_card_list")
    Call<List<SquareCard>> getSquareCards(@Query("portal_user_id") String userId);

    @POST("/hub/payment/add_square_card")
    Call<SquareCard> addSquareCards(@Body AddSquareCard squareCard);

    @HTTP(method = "DELETE", path = "/hub/payment/delete_square_card", hasBody = true)
    Call<Void> deleteSquareCards(@Body DeleteSquareCard deleteSquareCard);

    @POST("/hub/payment/set_appt_payment_card")
    Call<Void> setApptPaymentCard(@Body ApptPayment apptPayment);
}
