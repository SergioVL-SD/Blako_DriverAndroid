package com.blako.mensajero.Services.api;

import com.blako.mensajero.BuildConfig;
import com.blako.mensajero.Utils.LogUtils;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class ZonesServiceImplementation {
    final static String LOG_SOURCE = ZonesServiceImplementation.class.getName();

    public static ZonesService create()
    {
        LogUtils.debug(LOG_SOURCE, "create()");

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.API_ZONES)
                .addConverterFactory(MoshiConverterFactory.create())
                .client(client)
                .build();

        return retrofit.create(ZonesService.class);
    }
}