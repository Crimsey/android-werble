package com.example.android_werble;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.android_werble.entities.AccessToken;
import com.example.android_werble.entities.Data;
import com.example.android_werble.entities.Event;
import com.example.android_werble.entities.Message;
import com.example.android_werble.network.ApiService;
import com.example.android_werble.network.RetrofitBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventActivity extends AppCompatActivity {

    private static final String TAG = "EventActivity";

    @BindView(R.id.event_title)
    TextView title;

    Call<Data<Event>> call;
    Call<Message> messageCall;

    ApiService service;
    TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        ButterKnife.bind(this);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));

        if (tokenManager.getToken() == null) {
            startActivity(new Intent(EventActivity.this, LoginActivity.class));
            finish();
        }

        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);
        //service = RetrofitBuilder.createService(ApiService.class);
        Log.w(TAG, "LAST LINE" + tokenManager.getToken().getAccessToken());
    }

    @OnClick(R.id.EventButton)
    void getEvents() {

        call = service.events();
        call.enqueue(new Callback<Data<Event>>() {

            @Override
            public void onResponse(Call<Data<Event>> call, Response<Data<Event>> response) {
                Log.w(TAG, "onResponse: " + response);

                if (response.isSuccessful()) {
                    List<Event> eventList = response.body().getData();
                    String content = "";
                    for (Event event : eventList) {

                        content += "Id :" + event.getEventId().toString() + "\n" +
                                "name: " + event.getName() + "\n";//.getData().get(i).getEventId() + "\n";
                        //Log.w(TAG, "curr: " + event.getData() + "\n");


                    }
                    title.setText(content);
                    //List<Data<Event>> eventList = Collections.singletonList(response.body());
                    /*String content ="";
                    int i=0;
                    for (Data<Event> currentEvent : eventList){

                        content+="Id :" + currentEvent;//.getData().get(i).getEventId() + "\n";
                        Log.w(TAG, "curr: " + currentEvent.getData() + "\n");
                        i++;

                    }
                    title.setText(content);
*/
                    //title.setText(response.body().getData().get(0).getName());
                    //String titleEvent = response.body().getData().get(0).getName();
                    //title.setText(titleEvent);
                    Log.w(TAG, "getEvents" + response);
                } /*else {
                    tokenManager.deleteToken();
                    startActivity(new Intent(EventActivity.this, LoginActivity.class));
                    finish();
                }*/
            }

            @Override
            public void onFailure(Call<Data<Event>> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });

    }

    @OnClick(R.id.deleteToken)
    void deletetoken(){
        tokenManager.deleteToken();
        finish();
    }

    @OnClick(R.id.logoutButton)
    void logout() {
        messageCall = service.logout();

        messageCall.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> messageCall, Response<Message> response) {
                Log.w(TAG, "MESSresponse: " + response);

                if (response.isSuccessful()) {
                    String message = response.body().getMessage();
                    Intent i = new Intent(EventActivity.this, LoginActivity.class);
                    i.putExtra("logoutMessage", message);
                    Log.w(TAG, "MESS: " + message);

                    tokenManager.deleteToken();
                    startActivity(new Intent(EventActivity.this, LoginActivity.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Message> messageCall, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (call != null) {
            call.cancel();
            call = null;
        }
        if (messageCall != null) {
            messageCall.cancel();
            messageCall = null;
        }
    }
}