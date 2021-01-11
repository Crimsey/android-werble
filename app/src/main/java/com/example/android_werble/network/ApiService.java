package com.example.android_werble.network;

import com.example.android_werble.entities.AccessToken;
import com.example.android_werble.entities.Data;
import com.example.android_werble.entities.Event;
import com.example.android_werble.entities.EventParticipant;
import com.example.android_werble.entities.EventReview;
import com.example.android_werble.entities.EventType;
import com.example.android_werble.entities.Message;
import com.example.android_werble.entities.User;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

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
    Call<Message> createEventwithMarker(@Field("name") String name,
                                            @Field("location") String location,
                                            @Field("description") String description,
                                            @Field("start_datetime") String startDatetime,
                                            @Field("end_datetime") String endDatetime,
                                            @Field("longitude") String longitude,
                                            @Field("latitude") String latitude,
                                            @Field("event_type_id") Integer typeId,
                                            @Field("zip_code") String zipCode,
                                            @Field("street_name") String streetName,
                                            @Field("house_number") String houseNumber);

    @GET("user/events")
    Call<Data<Event>> getUserEvents();

    @GET("user/events/local")
    Call<Data<Event>> getLocalEvents(@Query("distance") Integer distance);

    @GET("user/events/owned")
    Call<Data<Event>> getOwnedEvents();

    @GET("user/events/participating")
    Call<Data<Event>> getParticipatingEvents();

    @GET("user")
    Call<User> user();

    @PUT("user/profile/edit")
    @FormUrlEncoded
    Call<Message> userEdit(
                        @Field("first_name") String first_name,
                        @Field("last_name") String last_name,
                        @Field("birth_date") String birth_date,
                        @Field("description") String description,
                        @Field("email") String email);

    @PUT("user/profile/editpassword")
    @FormUrlEncoded
    Call<Message> userEditPassword(
            @Field("password") String password);

    @PUT("user/position")
    @FormUrlEncoded
    Call<Message> userPosition(
                        @Field("longitude") String longitude,
                        @Field("latitude") String latitude);

    @GET("user/events/{id}")
    Call<Event> getSingleEvent(
                        @Path("id") Integer event_id);

    @GET("user/events/{id}/participants")
    Call<Data<EventParticipant>> getEventParticipant(@Path("id") Integer event_id);

    @GET("user/events/{id}/reviews")
    Call<Data<EventReview>> getEventReview(@Path("id") Integer event_id);

    @GET("user/types")
    Call<Data<EventType>> getEventTypes();

    @POST("user/events/{id}/join")
    Call<Message> joinEvent(
                    @Path("id") Integer event_id
                    );

    @DELETE("user/events/{id}/leave")
    Call<Message> leaveEvent(
            @Path("id") Integer event_id);

    @POST("user/events/review/create")
    @FormUrlEncoded
    Call<Message> createReview(
                        @Field("content") String content,
                        @Field("rating") String rating,
                        @Field("event_id") String event_id);

    @PUT("user/events/{id}/edit")
    @FormUrlEncoded
    Call<Message> editEvent(
                        @Path("id") Integer event_id,
                        @Field("name") String name,
                        @Field("location") String location,
                        @Field("description") String description,
                        @Field("start_datetime") String startDatetime,
                        @Field("end_datetime") String endDatetime,
                        @Field("longitude") String longitude,
                        @Field("latitude") String latitude,
                        @Field("event_type_id") Integer typeId,
                        @Field("zip_code") String zipCode,
                        @Field("street_name") String streetName,
                        @Field("house_number") String houseNumber);

    @PUT("user/events/{id}/review/edit")
    @FormUrlEncoded
    Call<Message> editReview(
            @Path("id") Integer event_id,
            @Field("rating") Integer rating,
            @Field("content") String content);

    @GET("user/events/{id}/review")
    Call<EventReview> getSingleReview(
            @Path("id") Integer event_id);

    @DELETE("user/events/review/{id}/softdelete")
    Call<Message> deleteReview(
            @Path("id") Integer event_participant_id
    );

    @DELETE("user/events/{id}/softdelete")
    Call<Message> deleteEvent(
            @Path("id") Integer event_id
    );

    @DELETE("user/profile/deactivate")
    Call<Message> deactivateProfile();


}
