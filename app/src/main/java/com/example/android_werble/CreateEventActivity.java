package com.example.android_werble;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

public class CreateEventActivity extends AppCompatActivity {

    private static final String TAG = "CreateEventActivity";

    @BindView(R.id.eventName)
    TextInputLayout eventName;
    @BindView(R.id.eventLocation)
    TextInputLayout eventLocation;
    @BindView(R.id.eventDescription)
    TextInputLayout eventDescription;
    @BindView(R.id.eventDatetime)
    TextInputLayout eventDatetime;

    ApiService service;
    Call<AccessToken> call;
    AwesomeValidation validator;
    TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createevent);

        Log.w(TAG,"My tu w ogóle wchodzimy?");
        ButterKnife.bind(this);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));

        if (tokenManager.getToken() == null) {
            startActivity(new Intent(CreateEventActivity.this, LoginActivity.class));
            finish();
        }

        service = RetrofitBuilder.createServiceWithAuth(ApiService.class,tokenManager);
        validator = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);




        setupRules();
    }

    @OnClick(R.id.CreateEventButton)
    void createEvent() {
        String name = eventName.getEditText().getText().toString();
        String location = eventLocation.getEditText().getText().toString();
        String description = eventDescription.getEditText().getText().toString();
        String datetime = eventDatetime.getEditText().getText().toString();

        eventName.setError(null);
        eventLocation.setError(null);
        eventDescription.setError(null);
        eventDatetime.setError(null);

        validator.clear();

        if (validator.validate()) {
            call = service.createEvent(name, location, description, datetime);
            call.enqueue(new Callback<AccessToken>() {
                @Override
                public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                    if (response.isSuccessful()) {
                        Log.e(TAG, "onResponse: " + response.body());
                        Toast.makeText(CreateEventActivity.this,"Created event!",Toast.LENGTH_LONG).show();
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

    }


    private void handleErrors(ResponseBody response) {

        ApiError apiError = Utils.converErrors(response);
        if (apiError.getErrors() != null) {
            Log.w("no errors", "apiError.getErrors()"+apiError.getErrors());

            for (Map.Entry<String, List<String>> error : apiError.getErrors().entrySet()) {
                if (error.getKey().equals("name")) {
                    eventName.setError(error.getValue().get(0));
                }
                if (error.getKey().equals("location")) {
                    eventLocation.setError(error.getValue().get(0));
                }
                if (error.getKey().equals("description")) {
                    eventDescription.setError(error.getValue().get(0));
                }
                if (error.getKey().equals("datetime")) {
                    eventDatetime.setError(error.getValue().get(0));
                }
            }
        } else {
            Log.e("no errors", "weird");
        }
    }

    public void setupRules() {
        validator.addValidation(this, R.id.eventName, RegexTemplate.NOT_EMPTY, R.string.err_event_name);
        validator.addValidation(this, R.id.eventLocation, RegexTemplate.NOT_EMPTY, R.string.err_event_location);
        validator.addValidation(this, R.id.eventDatetime, RegexTemplate.NOT_EMPTY, R.string.err_event_datetime);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (call != null) {
            call.cancel();
            call = null;
        }
    }
}

