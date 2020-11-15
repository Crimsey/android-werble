package com.example.android_werble;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.android_werble.network.ApiService;
import com.example.android_werble.network.RetrofitBuilder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EventActivity extends AppCompatActivity {

    @BindView(R.id.event_title)
    TextView title;
    ApiService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        ButterKnife.bind(this);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs"),MODE_PRIVATE);
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class,TokenManager);
    }

    @OnClick(R.id.PostButton)
    void getPosts(){

    }
    }