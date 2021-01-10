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

public class UserActivity extends NavigationActivity {

    private static final String TAG = "UserActivity";

    TextView firstname,lastname,login,email,birthdate,description,createdat;

    Call<User> call;


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

        firstname = findViewById(R.id.firstname);
        lastname = findViewById(R.id.lastname);
        email = findViewById(R.id.email);
        birthdate = findViewById(R.id.birthdate);
        description = findViewById(R.id.description);
        login = findViewById(R.id.login);
        createdat = findViewById(R.id.createdat);

        getUser();
    }

    void getUser() {

        call = service.user();
        call.enqueue(new Callback<User>() {

            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.w(TAG, "onResponse: " + response.body().getFirstName());

                if (response.isSuccessful()) {
                    User user = response.body();


                    if (user.getFirstName()==null ){
                        firstname.setText("FIRST NAME");
                    }else  {firstname.setText(user.getFirstName().toString());}

                    if (user.getLastName()==null){
                        lastname.setText("LAST NAME");
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
                    }else  {createdat.setText("CREATED AT:  "+user.getCreatedAt().substring(0,10));}


                    }
                }


            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
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
        if (messageCall != null) {
            messageCall.cancel();
            messageCall = null;
        }
    }
}