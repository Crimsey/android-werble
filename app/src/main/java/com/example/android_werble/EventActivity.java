package com.example.android_werble;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android_werble.entities.Data;
import com.example.android_werble.entities.Event;
import com.example.android_werble.entities.Message;
import com.example.android_werble.network.ApiService;
import com.example.android_werble.network.RetrofitBuilder;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        AdapterEvent.OnNoteListener {

    private static final String TAG = "EventActivity";

    RecyclerView recyclerView;
    List<Event> eventList;

    //variables for sidebar
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    Call<Data<Event>> call;
    Call<Message> messageCall;

    ApiService service;
    TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        //eventListView = findViewById(R.id.eventList);
        //eventListView.setTextFilterEnabled(true);
        recyclerView = (RecyclerView) findViewById(R.id.eventsRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        ButterKnife.bind(this);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));

        if (tokenManager.getToken() == null) {
            startActivity(new Intent(EventActivity.this, LoginActivity.class));
            finish();
        }

        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);
        //service = RetrofitBuilder.createService(ApiService.class);
        Log.w(TAG, "LAST LINE" + tokenManager.getToken().getAccessToken());

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


        getLocalEvents();
    }

    @OnClick(R.id.join)
    void getLocalEvents() {


        call = service.getLocalEvents();
        call.enqueue(new Callback<Data<Event>>() {

            @Override
            public void onResponse(Call<Data<Event>> call, Response<Data<Event>> response) {
                Log.w(TAG,"GETLOCALEVENTS");

                Log.w(TAG, "onResponse: " + response);

                if (response.isSuccessful()) {
                    eventList = response.body().getData();
                    recyclerView.setAdapter(new AdapterEvent(eventList, recyclerView, EventActivity.this::onNoteClick));

                }
                /*if (response.isSuccessful()) {
                    List<Event> eventList = response.body().getData();
                    String content = "";
                    for (Event event : eventList) {

                        content += "Id :" + event.getEventId().toString() + "\n" +
                                "name: " + event.getName() + "\n";//.getData().get(i).getEventId() + "\n";
                    }*/
                //title.setText(content);
                //title.setText(response.body().getData().get(0).getName());
                //String titleEvent = response.body().getData().get(0).getName();
                //title.setText(titleEvent);
                //Log.w(TAG, "getEvents" + response);
                //}
                /*else {
                    tokenManager.deleteToken();
                    startActivity(new Intent(EventActivity.this, LoginActivity.class));
                    finish();
                }*/
            }

            @Override
            public void onFailure(Call<Data<Event>> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });

    }

    @OnClick(R.id.yours)
    void getUserEvents() {


        call = service.getUserEvents();
        call.enqueue(new Callback<Data<Event>>() {

            @Override
            public void onResponse(Call<Data<Event>> call, Response<Data<Event>> response) {
                Log.w(TAG,"GETUSEREVENTS");
                Log.w(TAG, "onResponse: " + response);

                if (response.isSuccessful()) {
                    eventList = response.body().getData();
                    recyclerView.setAdapter(new AdapterEvent(eventList, recyclerView, EventActivity.this::onNoteClick));

                }
            }

            @Override
            public void onFailure(Call<Data<Event>> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });

    }

    //@OnClick(R.id.profileSidebar)
    void gotoProfile() {
        Toast.makeText(EventActivity.this, "YOUR PROFILE", Toast.LENGTH_LONG).show();
        startActivity(new Intent(EventActivity.this, UserActivity.class));
        finish();
        Log.w(TAG, "USERACTIVITY");
    }

    //@OnClick(R.id.mapSidebar)
    void gotoMap() {
        Toast.makeText(EventActivity.this, "MAP", Toast.LENGTH_LONG).show();
        //startActivity(new Intent(EventActivity.this, MapActivity.class));
        startActivity(new Intent(EventActivity.this, MyLocationActivity.class));
        //startActivity(new Intent(EventActivity.this,MyLocationLayerActivity.class));

        finish();
        Log.w(TAG, "GOINGTOMAP");
    }

    @OnClick(R.id.CreateEventButton)
    void gotoCreateEvent() {
        Toast.makeText(EventActivity.this, "CREATING", Toast.LENGTH_LONG).show();
        startActivity(new Intent(EventActivity.this, CreateEventActivity.class));
        finish();
        Log.w(TAG, "CREATE EVENT");
    }

    void gotoSettings() {
        Toast.makeText(EventActivity.this, "SETTINGS", Toast.LENGTH_LONG).show();
        startActivity(new Intent(EventActivity.this, SettingsActivity.class));
        finish();
        Log.w(TAG, "SETTINGS");
    }

    /*@OnClick(R.id.join)
    void joinSingleEvent(){
        Bundle b = getIntent().getExtras();
        String event_id = b.getString("event_id");
        callJoin = service.joinEvent(Integer.parseInt(event_id),"1");
        callJoin.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                Log.w(TAG, "You have joined!: " + response);
                Toast.makeText(SingleEventActivity.this,"JOINING EVENT",Toast.LENGTH_LONG).show();

                finish();
                startActivity(getIntent());
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());

            }
        });*/


    //@OnClick(R.id.logoutButton)
    void logout() {
        messageCall = service.logout();

        messageCall.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> messageCall, Response<Message> response) {
                Log.w(TAG, "MESSresponse: " + response);

                if (response.isSuccessful()) {
                    String message = response.body().getMessage();
                    Intent i = new Intent(EventActivity.this, LoginActivity.class);
                    i.putExtra("logoutMessage", message);
                    Log.w(TAG, "MESS: " + message);

                    tokenManager.deleteToken();
                    startActivity(new Intent(EventActivity.this, LoginActivity.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Message> messageCall, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Log.w(TAG, "SIDEBAR");
        Toast.makeText(EventActivity.this, "TOST", Toast.LENGTH_LONG).show();
        switch (item.getTitle().toString()) {
            case "Logout":
                logout();
                break;
            case "Your profile":
                gotoProfile();
                break;
            //case "Your events":
            case "Map":
                gotoMap();
                break;
            //case "Create event": gotoCreateEvent(); break;
            case "Settings":
                gotoSettings();
                break;

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

    @Override
    public void onNoteClick(int position) {
        Log.d(TAG, "onNoteClick: clicked.");


        call = service.getLocalEvents();
        call.enqueue(new Callback<Data<Event>>() {


            @Override
            public void onResponse(Call<Data<Event>> call, Response<Data<Event>> response) {
                Log.w(TAG, "onResponse: " + response);

                List<Event> event = response.body().getData();
                if (response.isSuccessful()) {
                    eventList = response.body().getData();
                    //recyclerView.setAdapter(new AdapterEvent(eventList, recyclerView,EventActivity.this::onNoteClick));

                    Intent intent = new Intent(EventActivity.this, SingleEventActivity.class);
                    intent.putExtra("event_id", String.valueOf(position));
                    //intent.putExtra("event_id", String.valueOf(event.get(position)));
                    startActivity(intent);
                }


            }

            @Override
            public void onFailure(Call<Data<Event>> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
}