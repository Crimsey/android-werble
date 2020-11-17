package com.example.android_werble;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.android_werble.entities.Event;
import com.example.android_werble.entities.EventResponse;
import com.example.android_werble.network.ApiService;
import com.example.android_werble.network.RetrofitBuilder;

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
    Call<EventResponse> call;

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

        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);
    }

    @OnClick(R.id.EventButton)
    void getPosts(){
        call = service.events();
        call.enqueue(new Callback<EventResponse>() {
            @Override
            public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
                Log.w(TAG, "onResponse: " + response);

                if (response.isSuccessful()) {
                    title.setText(response.body().getData().get(0).getName());
                } else {
                    tokenManager.deleteToken();
                    startActivity(new Intent(EventActivity.this, LoginActivity.class));
                    finish();

                }
            }

            @Override
            public void onFailure(Call<EventResponse> call, Throwable t) {

            }
        });

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