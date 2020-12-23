package com.example.android_werble;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.example.android_werble.entities.AccessToken;
import com.example.android_werble.entities.ApiError;
import com.example.android_werble.network.ApiService;
import com.example.android_werble.network.RetrofitBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
    @BindView(R.id.eventDatetime2)
    TextInputEditText eventDatetime;
    //EditText eventDatetime;

    ApiService service;
    Call<AccessToken> call;
    AwesomeValidation validator;
    TokenManager tokenManager;

    private TextView DisplayDate;



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

        eventDatetime = findViewById(R.id.eventDatetime2);
        eventDatetime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimeDialog(eventDatetime);
            }
        });



    }

    private void showDateTimeDialog(final TextInputEditText eventDatetime) {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        calendar.set(Calendar.MINUTE,minute);

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:00");

                        eventDatetime.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                };

                new TimePickerDialog(CreateEventActivity.this,R.style.datepicker,timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false).show();
            }
        };

        new DatePickerDialog(CreateEventActivity.this,R.style.datepicker, dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
    }


    @OnClick(R.id.CreateEventButton)
    void createEventwithMarker() {
        String name = eventName.getEditText().getText().toString();
        String location = eventLocation.getEditText().getText().toString();
        String description = eventDescription.getEditText().getText().toString();
        //String datetime = eventDatetime.getEditText().getText().toString();
        String datetime = eventDatetime.getText().toString();

        eventName.setError(null);
        eventLocation.setError(null);
        eventDescription.setError(null);
        eventDatetime.setError(null);

        validator.clear();

        if (validator.validate()) {
           Bundle b = getIntent().getExtras();
           String latitude = b.getString("lat");
           String longitude = b.getString("lon");

           Log.w(TAG,b.getString("lat"));
           Log.w(TAG,b.getString("lon"));

            call = service.createEventwithMarker(name, location, description, datetime,longitude,latitude);
            //call = service.createEvent(name, location, description, datetime);
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

        gotoEvent();
    }

    void gotoEvent() {
        Toast.makeText(CreateEventActivity.this,"CREATING",Toast.LENGTH_LONG).show();
        startActivity(new Intent(CreateEventActivity.this, EventActivity.class));
        finish();
        Log.w(TAG,"CREATE EVENT");
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
                /*if (error.getKey().equals("datetime")) {
                    eventDatetime.setError(error.getValue().get(0));
                }*/
            }
        } else {
            Log.e("no errors", "weird");
        }
    }

    public void setupRules() {
        validator.addValidation(this, R.id.eventName, RegexTemplate.NOT_EMPTY, R.string.err_event_name);
        validator.addValidation(this, R.id.eventLocation, RegexTemplate.NOT_EMPTY, R.string.err_event_location);
        //validator.addValidation(this, R.id.eventDatetime, RegexTemplate.NOT_EMPTY, R.string.err_event_datetime);

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

