package com.example.android_werble;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.example.android_werble.entities.AccessToken;
import com.example.android_werble.entities.ApiError;
import com.example.android_werble.entities.User;
import com.example.android_werble.network.ApiService;
import com.example.android_werble.network.RetrofitBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

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

public class SettingsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "SettingsActivity";


    @BindView(R.id.userFirstName2)
    TextInputEditText userFirstName;
    @BindView(R.id.userLastName2)
    TextInputEditText userLastName;
    @BindView(R.id.userBirthDate2)
    TextInputEditText userBirthDate;
    @BindView(R.id.userDescription2)
    TextInputEditText userDescription;

    Button calbutton;

    ApiService service;
    Call<User> call;
    Call<AccessToken> callAccessToken;
    AwesomeValidation validator;
    TokenManager tokenManager;
    String user_id,firstName,lastName,birthDate,description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Log.w(TAG,"My tu w ogóle wchodzimy?");
        ButterKnife.bind(this);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));

        if (tokenManager.getToken() == null) {
            startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
            finish();
        }

        service = RetrofitBuilder.createServiceWithAuth(ApiService.class,tokenManager);
        validator = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);

        call = service.user();

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.w(TAG,"CHECK");
                if (response.isSuccessful()) {
                    Log.e(TAG, "onResponse: " + response.body().getFirstName());

                    User user = response.body();
                        user_id = user.getUserId().toString();


                    if (user.getFirstName()!=null){
                       firstName = user.getFirstName().toString();
                       userFirstName.setText(firstName);
                    }
                    if (user.getLastName()!=null){
                        lastName = user.getLastName().toString();
                        userLastName.setText(lastName);
                    }
                    if (user.getBirthDate()!=null){
                        birthDate = user.getBirthDate().toString();
                        userBirthDate.setText(birthDate);
                    }
                    if (user.getDescription()!=null){
                        description = user.getDescription().toString();
                        userDescription.setText(description);
                    }

                } else {
                    handleErrors(response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());

            }
        });

        userBirthDate = findViewById(R.id.userBirthDate2);
        userBirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog(userBirthDate);
            }
        });

        calbutton = findViewById(R.id.calbutton);
        calbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog(userBirthDate);
            }
        });


        setupRules();
    }

    private void showDateDialog(TextInputEditText userBirthDate) {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                userBirthDate.setText(simpleDateFormat.format(calendar.getTime()));

            }
        };

        new DatePickerDialog(SettingsActivity.this,R.style.datepicker, dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    @OnClick(R.id.SettingsButton)
    void editUser() {
        String firstName = userFirstName.getText().toString();
        String lastName = userLastName.getText().toString();
        String birthDate = userBirthDate.getText().toString();
        String description = userDescription.getText().toString();

        userFirstName.setError(null);
        userLastName.setError(null);
        userBirthDate.setError(null);
        userDescription.setError(null);

        validator.clear();

        if (validator.validate()) {

            callAccessToken = service.userEdit(firstName, lastName, birthDate, description);//,password);

            callAccessToken.enqueue(new Callback<AccessToken>() {
                @Override
                public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                    Log.w(TAG,"CHECK2");
                    if (response.isSuccessful()) {
                        Log.e(TAG, "onResponse: " + response);



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

        gotoProfile();

    }

    private void handleErrors(ResponseBody response) {

        ApiError apiError = Utils.converErrors(response);
        if (apiError.getErrors() != null) {
            Log.w("no errors", "apiError.getErrors()"+apiError.getErrors());

            for (Map.Entry<String, List<String>> error : apiError.getErrors().entrySet()) {
                if (error.getKey().equals("first_name")) {
                    userFirstName.setError(error.getValue().get(0));
                }
                if (error.getKey().equals("last_name")) {
                    userLastName.setError(error.getValue().get(0));
                }
                if (error.getKey().equals("birth_date")) {
                    userBirthDate.setError(error.getValue().get(0));
                }
                if (error.getKey().equals("description")) {
                    userDescription.setError(error.getValue().get(0));
                }
            }
        } else {
            Log.e("no errors", "weird");
        }
    }

    public void setupRules() {
        validator.addValidation(this, R.id.eventEditName, RegexTemplate.NOT_EMPTY, R.string.err_event_name);
        validator.addValidation(this, R.id.eventEditDescription, RegexTemplate.NOT_EMPTY, R.string.err_event_name);
        validator.addValidation(this, R.id.userBirthDate, RegexTemplate.NOT_EMPTY, R.string.err_event_name);

    }

    //@OnClick(R.id.profileSidebar)
    void gotoProfile() {
        Toast.makeText(SettingsActivity.this,"TUTAJ",Toast.LENGTH_LONG).show();
        startActivity(new Intent(SettingsActivity.this, UserActivity.class));
        finish();
        Log.w(TAG,"USERACTIVITY");
    }

    //@OnClick(R.id.mapSidebar)
    void gotoMap() {
        Toast.makeText(this,"MAP",Toast.LENGTH_LONG).show();
        //startActivity(new Intent(EventActivity.this, MapActivity.class));
        startActivity(new Intent(this,MyLocationActivity.class));
        //startActivity(new Intent(EventActivity.this,MyLocationLayerActivity.class));

        finish();
        Log.w(TAG,"GOINGTOMAP");
    }

    //@OnClick(R.id.createEventSidebar)
    void gotoCreateEvent() {
        Toast.makeText(this,"CREATING",Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, EventCreateActivity.class));
        finish();
        Log.w(TAG,"CREATE EVENT");
    }

    void gotoSettings() {
        Toast.makeText(this,"SETTINGS",Toast.LENGTH_LONG).show();
    /*    startActivity(new Intent(this, SettingsActivity.class));
        finish();*/
        Log.w(TAG,"SETTINGS");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.w(TAG,"SIDEBAR");
        //Toast.makeText(this,"TOST",Toast.LENGTH_LONG).show();
        switch (item.getTitle().toString()) {
            //case "Logout": logout(); break;
            case "Your profile": gotoProfile(); break;
            //case "Your events":
            case "Map": gotoMap(); break;
            case "Create event": gotoCreateEvent(); break;
            case "Settings": gotoSettings(); break;

        }
        return false;    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

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


