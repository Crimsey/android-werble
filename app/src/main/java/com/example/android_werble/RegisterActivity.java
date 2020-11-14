package com.example.android_werble;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
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
    //@BindView(R.id.PasswordConfirmation)
    //TextInputLayout PasswordConfirmation;

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
    }

    @OnClick(R.id.RegistrationButton)
    void register(){

        String name = Login.getEditText().getText().toString();
        String email = Email.getEditText().getText().toString();
        String password = Password.getEditText().getText().toString();
        //String password_confirmation = PasswordConfirmation().getText().toString();
        Login.setError(null);
        Email.setError(null);
        Password.setError(null);

        validator.clear();

        if(validator.validate()) {

            call = service.register(name, email, password);//,password_confirmation);
            call.enqueue(new Callback<AccessToken>() {
                @Override
                public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {

                    Log.w(TAG, "onResponse: " + response);

                    if (response.isSuccessful()) {
                        Log.w(TAG, "onResponse: " + response.body());

                        tokenManager.saveToken(response.body());
                    } else {
                        handleErrors(response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<AccessToken> call, Throwable t) {

                }
            });
        }
    }

    private void handleErrors(ResponseBody response){

        ApiError apiError = Utils.converErrors(response);

        for (Map.Entry<String, List<String>> error : apiError.getErrors().entrySet()){
            if (error.getKey().equals("login")){
                Login.setError(error.getValue().get(0));
            }
            if (error.getKey().equals("email")){
                Login.setError(error.getValue().get(0));
            }
            if (error.getKey().equals("password")){
                Login.setError(error.getValue().get(0));
            }
        }
    }

    public void setupRules(){
        validator.addValidation(this,R.id.Login, RegexTemplate.NOT_EMPTY,R.string.err_login);
        validator.addValidation(this,R.id.Email, Patterns.EMAIL_ADDRESS,R.string.err_email);
        validator.addValidation(this,R.id.Password,"[a-zA-Z0-9]{8,}",R.string.err_password);

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