package com.example.android_werble;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.android_werble.entities.AccessToken;
import com.example.android_werble.entities.Event;
//import com.example.android_werble.entities.EventResponse;
import com.example.android_werble.entities.UserResponse;
import com.example.android_werble.network.ApiService;
import com.example.android_werble.network.RetrofitBuilder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private static final  String TAG ="HomeActivity";
    private static String accessToken;

    @BindView(R.id.titleHome)
    TextView titleHome;

    ApiService service;
    TokenManager tokenManager;
    Call<UserResponse> call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs",MODE_PRIVATE));

        if (tokenManager.getToken() == null){
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        }

        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);
    }

    @OnClick(R.id.HomeButton)
    void getUsers(){
        call = service.user();
        System.out.println("TUTAJ:"+tokenManager.getToken().getAccessToken());
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                Log.w(TAG, "onResponse: " + response);

                if (response.isSuccessful()) {
                    titleHome.setText(response.body().getData().get(0).getLogin());
                } else {
                    tokenManager.deleteToken();
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                    finish();

                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {

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