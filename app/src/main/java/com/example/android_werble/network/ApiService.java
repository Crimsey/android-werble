package com.example.android_werble.network;

import com.example.android_werble.entities.AccessToken;
import com.example.android_werble.entities.Data;
import com.example.android_werble.entities.Event;
import com.example.android_werble.entities.Message;
import com.example.android_werble.entities.User;

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

    @POST("logout")
    Call<Message>   logout();

    @POST("refresh")
    @FormUrlEncoded
    Call <AccessToken> refresh(@Field("refresh_token") String refreshToken);

    /*@POST("user/events/create")
    @FormUrlEncoded
    Call<AccessToken> createEvent(@Field(""));*/

    @GET("user/events")
    //@GET("events")
    Call<Data<Event>> events();

    @GET("user")
    Call<User> user();


}
