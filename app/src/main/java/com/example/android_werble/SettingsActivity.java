package com.example.android_werble;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.example.android_werble.entities.AccessToken;
import com.example.android_werble.entities.ApiError;
import com.example.android_werble.entities.Message;
import com.example.android_werble.entities.User;
import com.example.android_werble.network.ApiService;
import com.example.android_werble.network.RetrofitBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends NavigationActivity implements
        ViewDialog.ViewDialogListener{

    private static final String TAG = "SettingsActivity";

    @BindView(R.id.userFirstName2)
    TextInputEditText userFirstName;
    @BindView(R.id.userLastName2)
    TextInputEditText userLastName;
    @BindView(R.id.userBirthDate2)
    TextInputEditText userBirthDate;
    @BindView(R.id.userDescription2)
    TextInputEditText userDescription;
    @BindView(R.id.userEmail2)
    TextInputEditText userEmail;
    @BindView(R.id.userPassword2)
    TextInputEditText userPassword;

    Button calbutton,deactivateProfile;

    Call<User> call;
    AwesomeValidation validator,validatorEmail;
    String user_id,firstName,lastName,birthDate,description,email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Log.w(TAG,"My tu w ogóle wchodzimy?");
        ButterKnife.bind(this);
        //tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        validator = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);
        validatorEmail = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);

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
                    /*if (user.getEmail()!=null){
                        email = user.getEmail();
                        userEmail.setText(email);
                    }*/

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
        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();

        userFirstName.setError(null);
        userLastName.setError(null);
        userBirthDate.setError(null);
        userDescription.setError(null);
        //userEmail.setError(null);
        //userPassword.setError(null);
        validator.clear();
        System.out.println("firstName "+firstName);
        System.out.println("lastName "+lastName);
        System.out.println("birthDate "+birthDate);
        System.out.println("description "+description);
        System.out.println("email "+email);

        if (validator.validate()) {

            System.out.println("firstName "+firstName);
            System.out.println("lastName "+lastName);
            System.out.println("birthDate "+birthDate);
            System.out.println("description "+description);
            System.out.println("email "+email);


            messageCall = service.userEdit(firstName, lastName, birthDate, description);//, email);//,password);

            messageCall.enqueue(new Callback<Message>() {
                @Override
                public void onResponse(Call<Message> call, Response<Message> response) {
                    Log.w(TAG, "CHECK2");
                    if (response.isSuccessful()) {
                        Log.e(TAG, "onResponse: " + response);


                    } else {
                        handleErrors(response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<Message> call, Throwable t) {
                    Log.e(TAG, "onFailure: " + t.getMessage());

                }
            });
        }else {
            Log.e(TAG, "VALIDATOR: " + validator.validate());

        }
        userPassword.setError(null);
        validator.clear();
            if (!password.isEmpty()) {
                if (validator.validate()) {
                    messageCall = service.userEditPassword(password);
                    messageCall.enqueue(new Callback<Message>() {
                        @Override
                        public void onResponse(Call<Message> call, Response<Message> response) {
                            if (response.isSuccessful()) {
                                Log.e(TAG, "onResponse: " + response);
                                gotoProfile();

                            }
                            else {
                                Log.e(TAG,"Error!");
                            }
                        }

                        @Override
                        public void onFailure(Call<Message> call, Throwable t) {
                            Log.e(TAG, "onFailure: " + t.getMessage());
                        }
                    });
                }
            }
            userEmail.setError(null);
            validatorEmail.clear();
            if (!email.isEmpty()){
                if (validatorEmail.validate()){
                    messageCall = service.userEditEmail(email);
                    messageCall.enqueue(new Callback<Message>() {
                        @Override
                        public void onResponse(Call<Message> call, Response<Message> response) {
                            if (response.isSuccessful()) {
                                Log.e(TAG, "onResponse: " + response);
                                gotoProfile();

                            }
                            else {
                                Log.e(TAG,"Error!");
                            }
                        }

                        @Override
                        public void onFailure(Call<Message> call, Throwable t) {
                            Log.e(TAG, "onFailure: " + t.getMessage());
                        }
                    });
                }
            }
            /*else {
                gotoProfile();
            }*/
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
                if (error.getKey().equals("email")) {
                    userEmail.setError(error.getValue().get(0));
                }
            }
        } else {
            Log.e("no errors", "No errors occured");
        }
    }

    public void setupRules() {
        //if (!userEmail.getText().toString().isEmpty()){
            //System.out.println("MAIL");
        validatorEmail.addValidation(this,R.id.userEmail, Patterns.EMAIL_ADDRESS,R.string.err_email);//}
        validator.addValidation(this,R.id.userPassword,"[a-zA-Z0-9]{8,64}|^\\s*$",R.string.err_password);
        validator.addValidation(this,R.id.userFirstName,"[a-zA-Z]{1,30}|^\\s*$",R.string.err_firstname);
        validator.addValidation(this,R.id.userLastName,"[a-zA-Z]{1,40}|^\\s*$",R.string.err_lastname);
        validator.addValidation(this, R.id.userDescription, "[a-zA-Z0-9]{1,200}|^\\s*$", R.string.err_event_description);
    }

    @OnClick(R.id.deactivateProfile)
    void DeactivateProfile(){
        ViewDialog alert = new ViewDialog();
        alert.showDialog(this);
    }


    @Override
    public void onDeleteClick() {
        messageCall = service.deactivateProfile();
        messageCall.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if (response.isSuccessful()){
                    Log.e(TAG, "onResponse: " + response);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());

            }
        });
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


