package com.cybermed.cdoc_patient.payment;

import com.cybermed.cdoc_patient.common.CDoctor2Application;
import com.cybermed.cdoc_patient.webapi.APIs.PaymentApi;
import com.cybermed.cdoc_patient.webapi.AuthManager;
import com.cybermed.cdoc_patient.webapi.RestApiCall;
import com.cybermed.cdoc_patient.webapi.model.request.AddSquareCard;
import com.cybermed.cdoc_patient.webapi.model.response.ErrorResponse;
import com.cybermed.cdoc_patient.webapi.model.response.SquareCard;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import sqip.CardDetails;
import sqip.CardEntryActivityCommand;
import sqip.CardNonceBackgroundHandler;

public class CardEntryBackgroundHandler implements CardNonceBackgroundHandler {

    @NotNull
    @Override
    public CardEntryActivityCommand handleEnteredCardInBackground(@NotNull CardDetails cardDetails) {

        if(AuthManager.getOrCheckTokenSync()) {
            PaymentApi paymentApi = RestApiCall.getApiService(PaymentApi.class);

            AddSquareCard squareCard = new AddSquareCard(CDoctor2Application.getLoginInfo().getAccount(), cardDetails.getNonce());
            Call<SquareCard> addSquareCardCall = paymentApi.addSquareCards(squareCard);

            try {
                Response<SquareCard> response = addSquareCardCall.execute();
                if (response.isSuccessful()) {
                    return new CardEntryActivityCommand.Finish();
                } else {
                    if (response.code() == 500) {
                        return new CardEntryActivityCommand.ShowError("Server Error");
                    }
                    String errorBody = response.errorBody().string();
                    ErrorResponse error = new Gson().fromJson(errorBody, ErrorResponse.class);
                    return new CardEntryActivityCommand.ShowError(error.getError());
                }
            } catch (IOException e) {
                return new CardEntryActivityCommand.ShowError("Server Error");
            }
        } else {
            return new CardEntryActivityCommand.ShowError("Server Error");
        }
    }
}
