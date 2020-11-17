package com.example.android_werble.network;

import com.example.android_werble.entities.AccessToken;
import com.example.android_werble.entities.EventResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    @POST("register") //http://domain.com/api/
    @FormUrlEncoded
    Call<AccessToken> register(@Field("login") String login,
                               @Field("email") String email,
                               @Field("password") String password,
                               @Field("password_confirmation") String password_confirmation);

    @POST("login")
    @FormUrlEncoded
    Call<AccessToken> login(@Field("login") String login,
                            @Field("password") String password);

    @POST("refresh")
    @FormUrlEncoded
    Call <AccessToken> refresh(@Field("refresh_token") String refreshToken);

    @GET("events")
    Call<EventResponse> events();
}
