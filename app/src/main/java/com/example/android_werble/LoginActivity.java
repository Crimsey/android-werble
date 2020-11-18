package com.example.android_werble;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.example.android_werble.entities.AccessToken;
import com.example.android_werble.entities.ApiError;
import com.example.android_werble.entities.Event;
import com.example.android_werble.network.ApiService;
import com.example.android_werble.network.RetrofitBuilder;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    @BindView(R.id.Login)
    TextInputLayout Login;
    @BindView(R.id.Password)
    TextInputLayout Password;

    ApiService service;
    TokenManager tokenManager;
    AwesomeValidation validator;
    Call<AccessToken> call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        service = RetrofitBuilder.createService(ApiService.class);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        validator = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);
        setupRules();

        if (tokenManager.getToken().getAccessToken() != null){
            startActivity(new Intent(LoginActivity.this, EventActivity.class));
            finish();
        }
    }

    @OnClick(R.id.LoginButton)
    void login() {

        String login = Login.getEditText().getText().toString();
        String password = Password.getEditText().getText().toString();

        Login.setError(null);
        Password.setError(null);

        validator.clear();

        if (validator.validate()) {

            call = service.login(login, password);
            call.enqueue(new Callback<AccessToken>() {
                @Override
                public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {

                    Log.w(TAG, "onResponse " + response);

                    if (response.isSuccessful()) {
                        tokenManager.saveToken(response.body());
                        Log.w(TAG,"SPRAWDZAMY TOKEN:"+tokenManager.getToken().getAccessToken());
                        Log.w(TAG,"RES BODY: :"+response.body().getAccessToken());

                        startActivity(new Intent(LoginActivity.this, EventActivity.class));
                        finish();
                    } else {
                        if (response.code() == 422) {
                            handleErrors(response.errorBody());
                        }
                        if (response.code() == 401) {
                            ApiError apiError = Utils.converErrors(response.errorBody());
                            Toast.makeText(LoginActivity.this, apiError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<AccessToken> call, Throwable t) {
                    Log.w(TAG, "onFailure: " + t.getMessage());
                }
            });
        }
    }

    @OnClick(R.id.go_to_register)
    void goToRegister(){
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }

    private void handleErrors(ResponseBody response){

        ApiError apiError = Utils.converErrors(response);

        for (Map.Entry<String, List<String>> error : apiError.getErrors().entrySet()){
            if (error.getKey().equals("login")){
                Login.setError(error.getValue().get(0));
            }
            if (error.getKey().equals("password")){
                Password.setError(error.getValue().get(0));
            }
        }
    }

    public void setupRules(){
        validator.addValidation(this,R.id.Login, RegexTemplate.NOT_EMPTY,R.string.err_login);
        validator.addValidation(this,R.id.Password,RegexTemplate.NOT_EMPTY,R.string.err_password);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(call != null){
            call.cancel();
            call = null;
        }
    }
}