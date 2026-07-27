package com.cybermed.cdoc_patient.webapi;

import androidx.annotation.NonNull;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * A wrapper layer over the retrofit callback, written for distinguishing the success
 * and failure responses.
 *
 * @param <T> the class type of the success response expected.
 */
public class ResponseWrapper<T> implements Callback<T> {

    private final IResponseReceiver<T> mResponseCallback;

    /**
     * Creates an instance without the error mapper,
     * in case of all errors we would get the default response.
     *
     * @param responseCallback implementation of the response callback.
     */
    public ResponseWrapper(IResponseReceiver<T> responseCallback) {
        mResponseCallback = responseCallback;
    }


    @Override
    public void onResponse(Call<T> call, retrofit2.Response<T> response) {
        if (response.isSuccessful()) {
            mResponseCallback.onSuccess(response.body());
        } else {
            String errorBodyPayload = null;
            try {
                if (response.errorBody() != null)
                    errorBodyPayload = response.errorBody().string();
                if (errorBodyPayload != null) {
                    mResponseCallback.onFailure(errorBodyPayload);
                } else {
                    mResponseCallback.onFailure("Internal Error");
                }
            } catch (IOException e) {
                e.printStackTrace();
                mResponseCallback.onFailure("Internal Error");
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onFailure(@NonNull Call<T> call, @NonNull Throwable throwable) {

        mResponseCallback.onFailure(throwable.getMessage());
    }


}
