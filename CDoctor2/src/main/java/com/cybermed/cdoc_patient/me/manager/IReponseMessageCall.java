package com.cybermed.cdoc_patient.me.manager;

import androidx.annotation.NonNull;

public interface IReponseMessageCall<T> {
    /**
     * Call when api success
     *
     * @param data Response data
     */

    void onSendMessageSuccess(T data);

    /**
     * Call when api get failure
     *
     * @param errorResponse Server error response
     */
    void onFailure(@NonNull String errorResponse);

    void onProviderListSuccess(T data);
}
