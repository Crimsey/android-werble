package com.example.android_werble;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.example.android_werble.entities.AccessToken;
import com.example.android_werble.entities.ApiError;
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

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    @BindView(R.id.Login)
    TextInputLayout Login;
    @BindView(R.id.Email)
    TextInputLayout Email;
    @BindView(R.id.Password)
    TextInputLayout Password;
    @BindView(R.id.PasswordConfirmation)
    TextInputLayout PasswordConfirmation;
    ApiService service;
    Call<AccessToken> call;
    AwesomeValidation validator;
    TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);

        service = RetrofitBuilder.createService(ApiService.class);
        validator = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs",MODE_PRIVATE));
        setupRules();

        if (tokenManager.getToken().getAccessToken() != null){
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        }
    }

    @OnClick(R.id.RegistrationButton)
    void register(){

        String name = Login.getEditText().getText().toString();
        String email = Email.getEditText().getText().toString();
        String password = Password.getEditText().getText().toString();
        String password_confirmation = PasswordConfirmation.getEditText().getText().toString();


        Login.setError(null);
        Email.setError(null);
        Password.setError(null);
        PasswordConfirmation.setError(null);
        validator.clear();
        if(validator.validate()) {
            if (password_confirmation.equals(password)) {
                call = service.register(name, email, password, password_confirmation);
                call.enqueue(new Callback<AccessToken>() {
                    @Override
                    public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {

                        Log.e(TAG, "onResponse: " + response);
                        if (response.isSuccessful()) {
                            Log.e(TAG, "onResponse: " + response.body());
                            tokenManager.saveToken(response.body());
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();
                            Toast.makeText(RegisterActivity.this, "Successful registration", Toast.LENGTH_LONG).show();
                        } else {
                            handleErrors(response.errorBody());
                        }
                    }
                    @Override
                    public void onFailure(Call<AccessToken> call, Throwable t) {
                        Log.e(TAG, "onFailure: " + t.getMessage());
                    }
                });
            }
            else {
                Toast.makeText(RegisterActivity.this,"Passwords doesn't match!",Toast.LENGTH_LONG).show();
            }
        }
    }
    public void setupRules(){
        validator.addValidation(this,R.id.Login, "[a-zA-Z0-9]{4,30}",R.string.err_login);
        validator.addValidation(this,R.id.Email, Patterns.EMAIL_ADDRESS,R.string.err_email);
        validator.addValidation(this,R.id.Password,"[a-zA-Z0-9]{8,64}",R.string.err_password);
    }

    @OnClick(R.id.go_to_login)
    void goToLogin(){
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        Toast.makeText(RegisterActivity.this,"Login",Toast.LENGTH_LONG).show();
    }

    private void handleErrors(ResponseBody response){

        ApiError apiError = Utils.converErrors(response);
        if (apiError.getErrors() != null) {
            for (Map.Entry<String, List<String>> error : apiError.getErrors().entrySet()) {
                if (error.getKey().equals("login")) {
                    Login.setError(error.getValue().get(0));
                }
                if (error.getKey().equals("email")) {
                    Email.setError(error.getValue().get(0));
                }
                if (error.getKey().equals("password")) {
                    Password.setError(error.getValue().get(0));
                }
            }
        }
         else {
            Log.e("no errors","No errors occured");
         }
        }




    @Override
    protected void onDestroy(){
        super.onDestroy();
        if (call != null) {
            call.cancel();
            call = null;
        }
    }
}