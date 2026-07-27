package com.cybermed.cdoc_patient.webapi;

import androidx.annotation.NonNull;

public interface IResponseReceiver<T> {
    /**
     * Call when api success
     *
     * @param data Response data
     */

    void onSuccess(T data);

    /**
     * Call when api get failure
     *
     * @param errorResponse Server error response
     */
    void onFailure(@NonNull String errorResponse);
}
