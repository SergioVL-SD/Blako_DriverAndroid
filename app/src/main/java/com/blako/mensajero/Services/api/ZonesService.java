package com.blako.mensajero.Services.api;

import com.blako.mensajero.models.inbound.ZonesResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface ZonesService {
    @GET
    Call<ZonesResponse> zones(@Url String url);
}
