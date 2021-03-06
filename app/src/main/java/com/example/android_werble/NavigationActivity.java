package com.example.android_werble;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.android_werble.entities.Data;
import com.example.android_werble.entities.Event;
import com.example.android_werble.entities.Message;
import com.example.android_werble.network.ApiService;
import com.example.android_werble.network.RetrofitBuilder;
import com.google.android.material.navigation.NavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class NavigationActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "NavigationActivity";
    protected Toolbar toolbar;
    protected DrawerLayout drawerLayout;
    protected NavigationView navigationView;
    Call<Message> messageCall;
    ApiService service;
    TokenManager tokenManager;
    Context context;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setupDrawer();
    }

    void setupDrawer(){
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                null,
                R.string.openNavDrawer,
                R.string.closeNavDrawer
        );

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        if (tokenManager.getToken() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getTitle().toString()) {
            case "Map":
                gotoMap();
                break;
            case "Your profile":
                gotoProfile();
                break;
            case "Local events":
                gotoLocalEvents();
                break;
            case "Owned events":
                gotoOwnedEvents();
                break;
            case "Participating":
                gotoParticipating();
                break;
            case "Settings":
                gotoSettings();
                break;
            case "Logout":
                logout();
                break;
        }
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    void gotoMap() {
        Toast.makeText(this, "Going to map", Toast.LENGTH_LONG).show();
        Intent i = new Intent(this, MyLocationActivity.class);
        startActivity(i);
        finish();
        Log.w(TAG, "Going to map");
    }

    void gotoSettings() {
        Toast.makeText(this, "Going to settings", Toast.LENGTH_LONG).show();
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
        finish();
        Log.w(TAG, "Going to settings");
    }


    void gotoProfile() {
        Toast.makeText(this, "Going to profile", Toast.LENGTH_LONG).show();
        Intent i = new Intent(this, UserActivity.class);
        startActivity(i);
        finish();
        Log.w(TAG, "Going to profile");
    }

    void gotoParticipating() {
        Toast.makeText(this, "Going to participating events", Toast.LENGTH_LONG).show();
        Intent i = new Intent(this, EventParticipatingListActivity.class);
        startActivity(i);
        finish();
        Log.w(TAG, "Going to participating events");
    }

    void gotoOwnedEvents() {
        Toast.makeText(this, "Going to owned events", Toast.LENGTH_LONG).show();
        Intent i = new Intent(this, EventOwnedListActivity.class);
        startActivity(i);
        finish();
        Log.w(TAG, "Going to owned events");
    }

    void gotoLocalEvents() {
        Toast.makeText(this, "Going to local events", Toast.LENGTH_LONG).show();
        Intent i = new Intent(this, EventLocalListActivity.class);
        startActivity(i);
        finish();
        Log.w(TAG, "Going to local events");
    }

    void logout() {
        messageCall = service.logout();

        messageCall.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> messageCall, Response<Message> response) {
                Log.w(TAG, "MESSresponse: " + response);

                if (response.isSuccessful()) {
                    String message = response.body().getMessage();
                    Intent i = new Intent(context, LoginActivity.class);
                    i.putExtra("logoutMessage", message);
                    Log.w(TAG, "MESS: " + message);

                    tokenManager.deleteToken();
                    startActivity(new Intent(context, LoginActivity.class));
                    finish();
                    Toast.makeText(context,"Successful logout",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Message> messageCall, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (messageCall != null) {
            messageCall.cancel();
            messageCall = null;
        }
    }
}
