package com.example.android_werble;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
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
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
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
    @BindView(R.id.userPassword2)
    TextInputEditText userPassword;

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

        /*userFirstName = findViewById(R.id.userFirstName);
        userLastName = findViewById(R.id.userLastName);
        userBirthDate = findViewById(R.id.userBirthDate);
        userDescription = findViewById(R.id.userDescription);
        userPassword = findViewById(R.id.userPassword);*/
        /*Call<User> call = service.userEdit(firstName, lastName, birthDate, description,password);

        User user = response.body();*/

        call = service.user();

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.w(TAG,"CHECK");
                if (response.isSuccessful()) {
                    Log.e(TAG, "onResponse: " + response.body().getFirstName());

                    User user = response.body();
                    user_id = user.getUserId().toString();
                    firstName = user.getFirstName().toString();
                    lastName = user.getLastName().toString();
                    birthDate = user.getBirthDate().toString();
                    description = user.getDescription().toString();
                    //userFirstName.setPlaceholderText(user.getFirstName().toString());
                    //if (userFirstName.getText().equals("")){

                    //}else {
                        userFirstName.setText(firstName);
                    //}

                    userLastName.setText(lastName);
                    userBirthDate.setText(birthDate);
                    userDescription.setText(description);
                    //userPassword.setText(user.get);
                    //userFirstName.setText(firstName);

                } else {
                    handleErrors(response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());

            }
        });


        /*TextInputEditText edit = (TextInputEditText) findViewById(R.id.userFirstName);
        edit.setText(user.getFirstName().toString());
*/

        setupRules();
    }

    @OnClick(R.id.SettingsButton)
    void editUser() {
        String firstName = userFirstName.getText().toString();
        String lastName = userLastName.getText().toString();
        String birthDate = userBirthDate.getText().toString();
        String description = userDescription.getText().toString();
        //String password = userPassword.getText().toString();

        userFirstName.setError(null);
        userLastName.setError(null);
        userBirthDate.setError(null);
        userDescription.setError(null);
       // userPassword.setError(null);

        validator.clear();

        if (validator.validate()) {
            callAccessToken = service.userEdit(user_id,firstName, lastName, birthDate, description);//,password);

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
                /*if (error.getKey().equals("password")) {
                    userPassword.setError(error.getValue().get(0));
                }*/
            }
        } else {
            Log.e("no errors", "weird");
        }
    }

    public void setupRules() {
        validator.addValidation(this, R.id.userFirstName, RegexTemplate.NOT_EMPTY, R.string.err_event_name);
        validator.addValidation(this, R.id.userLastName, RegexTemplate.NOT_EMPTY, R.string.err_event_name);
        validator.addValidation(this, R.id.userBirthDate, RegexTemplate.NOT_EMPTY, R.string.err_event_name);
        validator.addValidation(this, R.id.userDescription, RegexTemplate.NOT_EMPTY, R.string.err_event_name);
        //validator.addValidation(this, R.id.userPassword, RegexTemplate.NOT_EMPTY, R.string.err_event_name);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (call != null) {
            call.cancel();
            call = null;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
