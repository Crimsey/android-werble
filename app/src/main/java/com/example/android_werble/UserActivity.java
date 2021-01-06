package com.example.android_werble;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
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

import com.example.android_werble.entities.Message;
import com.example.android_werble.entities.User;
import com.example.android_werble.network.ApiService;
import com.example.android_werble.network.RetrofitBuilder;
import com.google.android.material.navigation.NavigationView;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "UserActivity";

    //variables for sidebar
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

   // @BindView(R.id.firstname)
    TextView firstname,lastname,login,email,birthdate,description,createdat;

    Call<User> call;
    Call<Message> messageCall;

    ApiService service;
    TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
        }
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
        createdat = findViewById(R.id.createdat);

        //implementation of sidebar
        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.openNavDrawer,
                R.string.closeNavDrawer
        );

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

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


                    if (user.getFirstName()==null ){
                        firstname.setText("YOUR_FIRST_NAME");
                    }else  {firstname.setText(user.getFirstName().toString());}

                    if (user.getLastName()==null){
                        lastname.setText("YOUR_LAST_NAME");
                    }else  {lastname.setText(user.getLastName().toString());}

                    if (user.getBirthDate()==null){
                        birthdate.setText("BIRTHDATE:  ");
                    }else  {birthdate.setText("BIRTHDATE:  "+user.getBirthDate().toString());}

                    if (user.getDescription()==null){
                        description.setText("DESCRIPTION:  ");
                    }else  {description.setText("DESCRIPTION:  "+user.getDescription().toString());}

                    if (user.getLogin()==null){
                        login.setText("LOGIN:  ");
                    }else  {login.setText("LOGIN:  "+user.getLogin());}

                    if (user.getEmail()==null){
                        email.setText("EMAIL:  ");
                    }else  {email.setText("EMAIL:  "+user.getEmail());}
                    if (user.getCreatedAt()==null){
                        createdat.setText("CREATED AT:  ");
                    }else  {createdat.setText("CREATED AT:  "+user.getCreatedAt());}


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
        startActivity(new Intent(UserActivity.this, EventListActivity.class));
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