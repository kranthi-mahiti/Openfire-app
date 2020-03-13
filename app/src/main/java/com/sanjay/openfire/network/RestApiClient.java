package com.sanjay.openfire.network;

import com.chuckerteam.chucker.api.ChuckerInterceptor;
import com.sanjay.openfire.app.MyApplication;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestApiClient {
    private static Retrofit retrofit = null;
    static OkHttpClient.Builder client = new OkHttpClient.Builder();

    public static Retrofit getApiClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        client.addInterceptor(loggingInterceptor);
        client.addInterceptor(new ChuckerInterceptor(MyApplication.getContext()));
        if (retrofit == null) {

            retrofit = new Retrofit.Builder()
                    .baseUrl("BASE_URL")
                    .client(client.build())
                    .addConverterFactory(GsonConverterFactory.create())
//                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
