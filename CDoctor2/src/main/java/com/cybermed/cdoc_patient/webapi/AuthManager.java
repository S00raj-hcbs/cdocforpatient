package com.cybermed.cdoc_patient.webapi;

import android.content.Context;

import com.cybermed.cdoc_patient.util.ErrorMessage;
import com.cybermed.cdoc_patient.webapi.APIs.AuthApi;
import com.cybermed.cdoc_patient.webapi.model.request.ApiCredentials;
import com.cybermed.cdoc_patient.webapi.model.response.ApiToken;
import com.cybermed.cdoc_patient.webapi.model.response.ErrorResponse;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthManager {

    private final static String APP_ID = "cdoc-app";

    private final static String API_KEY = "31d7c1cd-5310-44de-ba0d-a3d980acf7a3";

   // private final static String APP_ID = "test";

   // private final static String API_KEY = "password";
    private static String token;

    private static long token_acquire_time = 0;

    private static int token_expire_time = 0;

    public static ApiCredentials createKeyBasedCredential() {

        return new ApiCredentials("api_key", APP_ID, API_KEY);
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        AuthManager.token = token;
    }

    public static void setToken_acquire_time(long token_acquire_time) {
        AuthManager.token_acquire_time = token_acquire_time;
    }

    public static void setToken_expire_time(int token_expire_time) {
        AuthManager.token_expire_time = token_expire_time;
    }

    public static boolean isTokenExpire() {
        long expireMillis = token_expire_time * 1000;
        //Give 10 seconds for running delay
        return System.currentTimeMillis() - token_acquire_time > expireMillis - 10 * 1000;
    }

    public static void acquireNewTokenAsync(Context context, Runnable onAcquireSuccess) {
        AuthApi authApi = RestApiCall.createNonAuthService(AuthApi.class);

        ApiCredentials credentials = AuthManager.createKeyBasedCredential();
        Call<ApiToken> apiTokenCall = authApi.getToken(credentials.getGrant_type(), credentials.getApp_id(), credentials.getApi_key());

        apiTokenCall.enqueue(new Callback<ApiToken>() {
            @Override
            public void onResponse(Call<ApiToken> call, Response<ApiToken> response) {
                if (!response.isSuccessful()) {
                    if (response.code() == 500) {
                        if (context != null)
                            ErrorMessage.alertDialog(context, "Server Error", "Error happened on server", null);
                    } else {
                        try {
                            String errorBody = response.errorBody().string();
                            ErrorResponse error = new Gson().fromJson(errorBody, ErrorResponse.class);
                            if (context != null)
                                ErrorMessage.alertDialog(context, "Error", error.getError(), null);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    ApiToken tokenInfo = response.body();
                    setToken(tokenInfo.getToken_type() + " " + tokenInfo.getAccess_token());
                    setToken_acquire_time(System.currentTimeMillis());
                    setToken_expire_time(tokenInfo.getExpires_in());
                    if (onAcquireSuccess != null) {
                        onAcquireSuccess.run();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiToken> call, Throwable t) {
                if (context != null)
                    ErrorMessage.alertDialog(context, "Server Error", "Cannot connect to API server", null);
            }
        });
    }

    public static boolean acquireNewTokenSync() {
        AuthApi authApi = RestApiCall.createNonAuthService(AuthApi.class);

        ApiCredentials credentials = AuthManager.createKeyBasedCredential();
        Call<ApiToken> apiTokenCall = authApi.getToken(credentials.getGrant_type(), credentials.getApp_id(), credentials.getApi_key());

        try {
            Response<ApiToken> response = apiTokenCall.execute();
            if (response.isSuccessful()) {
                ApiToken tokenInfo = response.body();
                setToken(tokenInfo.getToken_type() + " " + tokenInfo.getAccess_token());
                setToken_acquire_time(System.currentTimeMillis());
                setToken_expire_time(tokenInfo.getExpires_in());
                return true;
            } else {
                if (response.code() == 500) {

                }
                String errorBody = response.errorBody().string();
                ErrorResponse error = new Gson().fromJson(errorBody, ErrorResponse.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean getOrCheckTokenSync() {
        if (AuthManager.isTokenExpire()) {
            return AuthManager.acquireNewTokenSync();
        }
        return true;
    }

    public static void getOrCheckTokenAsync(Context context, Runnable runnable) {
        if (AuthManager.isTokenExpire()) {
            AuthManager.acquireNewTokenAsync(context, runnable);
        } else {
            runnable.run();
        }
    }
}
