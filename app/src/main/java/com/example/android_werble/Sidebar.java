package com.example.android_werble;

import android.annotation.SuppressLint;
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

import com.google.android.material.navigation.NavigationView;

public class Sidebar extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "SidebarActivity";

    //variables for sidebar
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

   // public Sidebar(EventActivity eventActivity) {
   // }

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.menu.nav_menu);

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
    }

    void gotoProfile() {
        Toast.makeText(this,"TUTAJ",Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, UserActivity.class));
        finish();
        Log.w(TAG,"USERACTIVITY");
    }

    void gotoMap() {
        Toast.makeText(this,"MAP",Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, MapActivity.class));
        finish();
        Log.w(TAG,"GOINGTOMAP");
    }

    void gotoCreateEvent() {
        Toast.makeText(this,"CREATING",Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, CreateEventActivity.class));
        finish();
        Log.w(TAG,"CREATE EVENT");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.w(TAG,"SIDEBAR");
        Toast.makeText(this,"TOST",Toast.LENGTH_LONG).show();
        switch (item.getTitle().toString()) {
            //case "Logout": logout(); break;
            case "Your profile": gotoProfile(); break;
            //case "Your events":
            case "Map": gotoMap(); break;
            case "Create event": gotoCreateEvent(); break;
        }
        return false;    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
