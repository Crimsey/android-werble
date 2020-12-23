package com.example.android_werble.network;

import com.example.android_werble.entities.AccessToken;
import com.example.android_werble.entities.Data;
import com.example.android_werble.entities.Event;
import com.example.android_werble.entities.EventParticipant;
import com.example.android_werble.entities.Message;
import com.example.android_werble.entities.User;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

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


    @POST("user/events/create")
    @FormUrlEncoded
    Call<AccessToken> createEventwithMarker(@Field("name")String name,
                                  @Field("location") String location,
                                  @Field("description") String description,
                                  @Field("datetime") String datetime,
                                  @Field("longitude") String longitude,
                                  @Field("latitude") String latitude);

    @GET("user/events")
    Call<Data<Event>> getUserEvents();

    @GET("user/events/local")
    Call<Data<Event>> getLocalEvents();

    @GET("user")
    Call<User> user();

    @PUT("user/profile/edit")
    @FormUrlEncoded
    Call<AccessToken> userEdit(
                        @Field("first_name") String first_name,
                        @Field("last_name") String last_name,
                        @Field("birth_date") String birth_date,
                        @Field("description") String description);
    @PUT("user/position")
    @FormUrlEncoded
    Call<Message> userPosition(
                        @Field("longitude") String longitude,
                        @Field("latitude") String latitude);

    @GET("user/event/{id}")
    Call<Event> getSingleEvent(
                        @Path("id") Integer event_id);

    @GET("user/events/{id}/participants")
    Call<Data<EventParticipant>> getEventParticipant(@Path("id") Integer event_id);

    @POST("user/event/{id}/join")
    @FormUrlEncoded
    Call<Message> joinEvent(
                    @Path("id") Integer event_id,
                    @Field("participant_status_id") String participant_status_id);

    @POST("user/events/review/create")
    @FormUrlEncoded
    Call<Message> createReview(
                        @Field("content") String content,
                        @Field("rating") String rating,
                        @Field("event_id") String event_id);

}
