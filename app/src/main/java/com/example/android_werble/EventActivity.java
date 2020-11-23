package com.example.android_werble;

import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.android_werble.entities.AccessToken;
import com.example.android_werble.entities.Data;
import com.example.android_werble.entities.Event;
import com.example.android_werble.network.ApiService;
import com.example.android_werble.network.RetrofitBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventActivity extends AppCompatActivity {

    private static final  String TAG ="EventActivity";

    Toolbar toolbar;
    RecyclerView recyclerView;

    //@BindView(R.id.event_title)
    //TextView title;
    //TextView eventBody;

    ApiService service;
    TokenManager tokenManager;
    Call<Data<Event>> call;

    EventAdapter eventAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerview);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        eventAdapter = new EventAdapter();

        ButterKnife.bind(this);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs",MODE_PRIVATE));

        if (tokenManager.getToken() == null){
            startActivity(new Intent(EventActivity.this, LoginActivity.class));
            finish();
        }

        //service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);
        service = RetrofitBuilder.createService(ApiService.class);
        Log.w(TAG,"LAST LINE"+tokenManager.getToken().getAccessToken());
    }

    void getEvents(){
        call = service.getAllEvents();
        call.enqueue(new Callback<Data<Event>>() {
            @Override
            public void onResponse(Call<Data<Event>> call, Response<Data<Event>> response) {

                if (response.isSuccessful()){
                    List<Data<Event>> events = (List<Data<Event>>) response.body();
                    eventAdapter.setData(events);
                    recyclerView.setAdapter(eventAdapter);

                }
            }

            @Override
            public void onFailure(Call<Data<Event>> call, Throwable t) {

            }
        });


    }


    //@OnClick(R.id.EventButton)
    /*void getEvents(){
        call = service.getAllEvents();
        call.enqueue(new Callback<Data<Event>>() {

            @Override
            public void onResponse(Call<Data<Event>> call, Response<Data<Event>> response) {
                Log.w(TAG, "onResponse: " + response);

                if (response.isSuccessful()) {
                    /*List<Data<Event>> eventList = Collections.singletonList(response.body());
                    String content ="";
                    for (Data<Event> currentEvent : eventList){

                        content+="Id :" + currentEvent.getData().get(0) + "\n";
                    }
                    title.setText(content);*/

                    //title.setText(response.body().getData().get(0).getName());
                    //eventBody.setText(response.body().getData().get().getEventId());
                    //String titleEvent = response.body().getData().get(0).getName();
                    //title.setText(titleEvent);
                    //Log.w(TAG,"getEvents"+response);
               /* } else {
                    tokenManager.deleteToken();
                    startActivity(new Intent(EventActivity.this, LoginActivity.class));
                    finish();

                }
            }

            @Override
            public void onFailure(Call<Data<Event>> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage() );
            }
        });

    }*/

    //@OnClick(R.id.logoutButton)
    void logout(){
        String message="SUCCESSFULY LOGOUT!";
        Intent i = new Intent(this,LoginActivity.class);
        i.putExtra("logoutMessage",message);
        tokenManager.deleteToken();
        startActivity(new Intent(EventActivity.this, LoginActivity.class));
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (call != null){
            call.cancel();
            call = null;
        }
    }
}