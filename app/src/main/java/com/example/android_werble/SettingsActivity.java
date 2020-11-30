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


    @BindView(R.id.userFirstName)
    TextInputLayout userFirstName;
    @BindView(R.id.userLastName)
    TextInputLayout userLastName;
    @BindView(R.id.userBirthDate)
    TextInputLayout userBirthDate;
    @BindView(R.id.userDescription)
    TextInputLayout userDescription;
    @BindView(R.id.userPassword)
    TextInputLayout userPassword;

    ApiService service;
    Call<User> call;
    AwesomeValidation validator;
    TokenManager tokenManager;

    /*String firstName = userFirstName.getEditText().getText().toString();
    String lastName = userLastName.getEditText().getText().toString();
    String birthDate = userBirthDate.getEditText().getText().toString();
    String description = userDescription.getEditText().getText().toString();
    String password = userPassword.getEditText().getText().toString();*/
    //String firstName,lastName,birthDate,description,password;

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
                    String firstName = user.getFirstName().toString();
                    userFirstName.setPlaceholderText(user.getFirstName().toString());
                    userFirstName.();
                    //userFirstName.setText(firstName);
                    /*firstName =user.getFirstName().toString();
                    lastName = user.getLastName().toString();
                    birthDate = user.getBirthDate().toString();
                    description = user.getDescription().toString();
                    password = user.getDescription().toString();
*/
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
        String firstName = userFirstName.getEditText().getText().toString();
        String lastName = userLastName.getEditText().getText().toString();
        String birthDate = userBirthDate.getEditText().getText().toString();
        String description = userDescription.getEditText().getText().toString();
        String password = userPassword.getEditText().getText().toString();

        userFirstName.setError(null);
        userLastName.setError(null);
        userBirthDate.setError(null);
        userDescription.setError(null);
        userPassword.setError(null);

        validator.clear();

        if (validator.validate()) {
            call = service.userEdit(firstName, lastName, birthDate, description,password);

            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    Log.w(TAG,"CHECK");
                    if (response.isSuccessful()) {
                        Log.e(TAG, "onResponse: " + response);

                        User user = response.body();
                        //firstName.(); user.getFirstName().toString();

                    } else {
                        handleErrors(response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
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
                if (error.getKey().equals("password")) {
                    userPassword.setError(error.getValue().get(0));
                }
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
        validator.addValidation(this, R.id.userPassword, RegexTemplate.NOT_EMPTY, R.string.err_event_name);

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
