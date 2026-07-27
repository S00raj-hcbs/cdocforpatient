package com.cybermed.cdoc_patient.webapi;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestApiCall<S> {
    private static Retrofit sRetrofit;
    public final static String ServerAddr = "https://api.cybermedehr.com/";
    public final static String TEST_SERVER_ADDR = "https://testehrapi.cybermedehr.com/";

/*    public static Retrofit getInterface() {
        if (AuthManager.getToken() != null) {
            AuthenticationInterceptor interceptor =
                    new AuthenticationInterceptor(AuthManager.getToken());

            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.level(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

            httpClient.followRedirects(false);
            httpClient.connectTimeout(2, TimeUnit.MINUTES);
            httpClient.readTimeout(2, TimeUnit.MINUTES);
            httpClient.writeTimeout(2, TimeUnit.MINUTES);
            httpClient.retryOnConnectionFailure(false);
            if (!httpClient.interceptors().contains(interceptor)) {
                httpClient.addInterceptor(interceptor);
                httpClient.addInterceptor(loggingInterceptor);
                Retrofit.Builder builder = getRetroBuilder();
                builder.client(httpClient.build());
                sRetrofit = builder.build();
            }
        }
        return sRetrofit;
    }*/
public static Retrofit getInterface() {
    OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
            .followRedirects(false)
            .connectTimeout(2, TimeUnit.MINUTES)
            .readTimeout(2, TimeUnit.MINUTES)
            .writeTimeout(2, TimeUnit.MINUTES)
            .retryOnConnectionFailure(false);

    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
    loggingInterceptor.level(HttpLoggingInterceptor.Level.BODY);
    httpClient.addInterceptor(loggingInterceptor);

    if (AuthManager.getToken() != null) {
        AuthenticationInterceptor interceptor =
                new AuthenticationInterceptor(AuthManager.getToken());
        httpClient.addInterceptor(interceptor);
    }

    Retrofit.Builder builder = getRetroBuilder();
    builder.client(httpClient.build());
    sRetrofit = builder.build();
    return sRetrofit;
}

    public static Retrofit.Builder getRetroBuilder() {
        return new Retrofit.Builder()
                .baseUrl(ServerAddr)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create());

    }

    /**
     * Get api retrofit object
     *
     * @return Instance of api service
     */
    public static <S> S getApiService(Class<S> serviceClass) {
        return getInterface().create(serviceClass);
    }

    public static <S> S createNonAuthService(Class<S> serviceClass) {
        sRetrofit = getRetroBuilder().build();
        return sRetrofit.create(serviceClass);
    }
}
