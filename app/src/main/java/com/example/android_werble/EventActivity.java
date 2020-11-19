package com.example.android_werble;

import androidx.appcompat.app.AppCompatActivity;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventActivity extends AppCompatActivity {

    private static final  String TAG ="EventActivity";

    @BindView(R.id.event_title)
    TextView title;

    ApiService service;
    TokenManager tokenManager;
    Call<Data<Event>> call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

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

    @OnClick(R.id.EventButton)
    void getEvents(){
        call = service.events();
        call.enqueue(new Callback<Data<Event>>() {

            @Override
            public void onResponse(Call<Data<Event>> call, Response<Data<Event>> response) {
                Log.w(TAG, "onResponse: " + response);

                if (response.isSuccessful()) {
                    title.setText(response.body().getData().get(0).getName());
                    //String titleEvent = response.body().getData().get(0).getName();
                    //title.setText(titleEvent);
                    Log.w(TAG,"getEvents"+response);
                } else {
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

    }

    @OnClick(R.id.logoutButton)
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