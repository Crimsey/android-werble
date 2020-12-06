package com.example.android_werble;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android_werble.entities.AccessToken;
import com.example.android_werble.entities.Data;
import com.example.android_werble.entities.Event;
import com.example.android_werble.entities.Message;
import com.example.android_werble.entities.User;
import com.example.android_werble.network.ApiService;
import com.example.android_werble.network.RetrofitBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "UserActivity";


   // @BindView(R.id.firstname)
    TextView firstname,lastname,login,email,birthdate,description;

    Call<User> call;
    Call<Message> messageCall;

    ApiService service;
    TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        ButterKnife.bind(this);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));

        if (tokenManager.getToken() == null) {
            startActivity(new Intent(UserActivity.this, LoginActivity.class));
            finish();
        }

        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);
        Log.w(TAG, "LAST LINE" + tokenManager.getToken().getAccessToken());

        firstname = findViewById(R.id.firstname);
        lastname = findViewById(R.id.lastname);
        email = findViewById(R.id.email);
        birthdate = findViewById(R.id.birthdate);
        description = findViewById(R.id.description);
        login = findViewById(R.id.login);

        getUser();
    }

    //@OnClick(R.id.EventButton)
    void getUser() {

        call = service.user();
        call.enqueue(new Callback<User>() {

            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.w(TAG, "onResponse: " + response.body().getFirstName());

                if (response.isSuccessful()) {
                    User user = response.body();


                    if (user.getFirstName().toString()==null){
                        firstname.setText("nofirstname :(");
                    }else  {firstname.setText(user.getFirstName().toString());}

                    if (user.getLastName()==null){
                        lastname.setText("nolastname :(");
                    }else  {lastname.setText(user.getLastName().toString());}

                    if (user.getBirthDate()==null){
                        birthdate.setText("nobirthdate :(");
                    }else  {birthdate.setText("Birthdate: "+user.getBirthDate().toString());}

                    if (user.getDescription()==null){
                        description.setText("nodescription :(");
                    }else  {description.setText("Description: "+user.getDescription().toString());}

                    email.setText(user.getEmail());
                    login.setText(user.getLogin());
                      /*  content += "Id :" + user.getUserId().toString() + "\n" +
                                "login: " + user.getLogin() + "\n" //.getData().get(i).getEventId() + "\n";
                                + "email: " + user.getEmail() + "\n"
                                + "first_name: " + user.getFirstName() + "\n"
                                + "last_name: " + user.getLastName() + "\n"
                                + "birth_date: " + user.getBirthDate() + "\n"
                                + "description: " + user.getDescription() + "\n";*/
                    //userTitle.setText(content);
                    }
                }


            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });

    }


    //@OnClick(R.id.logoutButton)
    void logout() {
        messageCall = service.logout();

        messageCall.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> messageCall, Response<Message> response) {
                Log.w(TAG, "MESSresponse: " + response);

                if (response.isSuccessful()) {
                    String message = response.body().getMessage();
                    Intent i = new Intent(UserActivity.this, LoginActivity.class);
                    i.putExtra("logoutMessage", message);
                    Log.w(TAG, "MESS: " + message);

                    tokenManager.deleteToken();
                    startActivity(new Intent(UserActivity.this, LoginActivity.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Message> messageCall, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    void gotoProfile() {
        Toast.makeText(UserActivity.this,"TUTAJ",Toast.LENGTH_LONG).show();
        startActivity(new Intent(UserActivity.this, UserActivity.class));
        finish();
        Log.w(TAG,"USERACTIVITY");
    }

    void gotoEvent() {
        startActivity(new Intent(UserActivity.this, EventActivity.class));
        finish();
    }

    void gotoSettings() {
        Toast.makeText(UserActivity.this,"SETTINGS",Toast.LENGTH_LONG).show();
        startActivity(new Intent(UserActivity.this, SettingsActivity.class));
        finish();
        Log.w(TAG,"SETTINGS");
    }

    void gotoMap() {
        Toast.makeText(UserActivity.this,"MAP",Toast.LENGTH_LONG).show();
        startActivity(new Intent(UserActivity.this,MyLocationActivity.class));
        finish();
        Log.w(TAG,"GOINGTOMAP");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.w(TAG,"SIDEBAR");
        Toast.makeText(UserActivity.this,"TOST",Toast.LENGTH_LONG).show();
        switch (item.getTitle().toString()) {
            case "Logout": logout(); break;
            case "Your profile": gotoProfile(); break;
            case "Your events": gotoEvent(); break;
            case "Map": gotoMap(); break;
            case "Settings": gotoSettings(); break;


        }
        return false;
    }

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
        if (messageCall != null) {
            messageCall.cancel();
            messageCall = null;
        }
    }
}