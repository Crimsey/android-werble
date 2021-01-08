package com.example.android_werble;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.SearchView;
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

public class EventParticipatingListActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        AdapterEvent.OnNoteListener,
        SearchView.OnQueryTextListener
{

    private static final String TAG = "EventActivity";

    RecyclerView recyclerView;
    List<Event> eventList;

    //variables for sidebar
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Context context;

    Call<Data<Event>> call;
    Call<Message> messageCall;

    ApiService service;
    TokenManager tokenManager;

    AdapterEvent adapterEvent;
    SearchView searchEvent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        recyclerView = (RecyclerView) findViewById(R.id.eventsRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        searchEvent = findViewById(R.id.searchEvent);
        searchEvent.setOnQueryTextListener(this);
        context=this;


        ButterKnife.bind(this);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));

        if (tokenManager.getToken() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);

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
        if (getIntent().hasExtra("range")){
            Bundle b = getIntent().getExtras();
            String range = b.getString("range");
            call = service.getLocalEvents(Integer.parseInt(range));
        }
        else
        {
            call = service.getLocalEvents(10);
        }


        call.enqueue(new Callback<Data<Event>>() {

            @Override
            public void onResponse(Call<Data<Event>> call, Response<Data<Event>> response) {
                Log.w(TAG,"GETLOCALEVENTS");

                Log.w(TAG, "onResponse: " + response);

                if (response.isSuccessful()) {
                    eventList = response.body().getData();
                    adapterEvent = new AdapterEvent(eventList, recyclerView, EventParticipatingListActivity.this::onNoteClick,context);
                    recyclerView.setAdapter(adapterEvent);

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

    /*@OnClick(R.id.yours)
    void getUserEvents() {


        call = service.getUserEvents();
        call.enqueue(new Callback<Data<Event>>() {

            @Override
            public void onResponse(Call<Data<Event>> call, Response<Data<Event>> response) {
                Log.w(TAG,"GETUSEREVENTS");
                Log.w(TAG, "onResponse: " + response);

                if (response.isSuccessful()) {
                    eventList = response.body().getData();
                    recyclerView.setAdapter(adapterEvent);

                }
            }

            @Override
            public void onFailure(Call<Data<Event>> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });

    }*/

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
    void gotoMap() {
        Bundle b = getIntent().getExtras();
        Intent i = getIntent();
        String range;
        if (i.hasExtra("range")){
            range = b.getString("range");
        }else {
            range="10";
        }
        Toast.makeText(this, "Going to map", Toast.LENGTH_LONG).show();
        i = new Intent(this, MyLocationActivity.class);
        i.putExtra("range",range);
        startActivity(i);
        finish();
        Log.w(TAG, "Going to map");
    }

    void gotoSettings() {
        Bundle b = getIntent().getExtras();
        Intent i = getIntent();
        String range;
        if (i.hasExtra("range")){
            range = b.getString("range");
        }else {
            range="10";
        }
        Toast.makeText(this, "Going to settings", Toast.LENGTH_LONG).show();
        i = new Intent(this, SettingsActivity.class);
        i.putExtra("range",range);
        startActivity(i);
        finish();
        Log.w(TAG, "Going to settings");
    }


    void gotoProfile() {
        Bundle b = getIntent().getExtras();
        Intent i = getIntent();
        String range;
        if (i.hasExtra("range")){
            range = b.getString("range");
        }else {
            range="10";
        }
        Toast.makeText(this, "Going to profile", Toast.LENGTH_LONG).show();
        i = new Intent(this, UserActivity.class);
        i.putExtra("range",range);
        startActivity(i);
        finish();
        Log.w(TAG, "Going to profile");
    }

    void gotoParticipating() {
        Bundle b = getIntent().getExtras();
        Intent i = getIntent();
        String range;
        if (i.hasExtra("range")){
            range = b.getString("range");
        }else {
            range="10";
        }
        Toast.makeText(this, "Going to participating events", Toast.LENGTH_LONG).show();
        i = new Intent(this, EventParticipatingListActivity.class);
        i.putExtra("range",range);
        startActivity(i);
        finish();
        Log.w(TAG, "Going to participating events");
    }

    void gotoOwnedEvents() {
        Bundle b = getIntent().getExtras();
        Intent i = getIntent();
        String range;
        if (i.hasExtra("range")){
            range = b.getString("range");
        }else {
            range="10";
        }
        Toast.makeText(this, "Going to owned events", Toast.LENGTH_LONG).show();
        i = new Intent(this, EventOwnedListActivity.class);
        i.putExtra("range",range);
        startActivity(i);
        finish();
        Log.w(TAG, "Going to owned events");
    }

    void gotoLocalEvents() {
        Bundle b = getIntent().getExtras();
        Intent i = getIntent();
        String range;
        if (i.hasExtra("range")){
            range = b.getString("range");
        }else {
            range="10";
        }
        Toast.makeText(this, "Going to local events", Toast.LENGTH_LONG).show();
        i = new Intent(this, EventLocalListActivity.class);
        i.putExtra("range",range);
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
                    Intent i = new Intent(EventParticipatingListActivity.this, LoginActivity.class);
                    i.putExtra("logoutMessage", message);
                    Log.w(TAG, "MESS: " + message);

                    tokenManager.deleteToken();
                    startActivity(new Intent(EventParticipatingListActivity.this, LoginActivity.class));
                    finish();
                    Toast.makeText(EventParticipatingListActivity.this,"Successful logout",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Message> messageCall, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });
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


        call = service.getLocalEvents(10);
        call.enqueue(new Callback<Data<Event>>() {


            @Override
            public void onResponse(Call<Data<Event>> call, Response<Data<Event>> response) {
                Log.w(TAG, "onResponse: " + response);

                //List<Event> event = response.body().getData();
                if (response.isSuccessful()) {
                    eventList = response.body().getData();
                    //recyclerView.setAdapter(new AdapterEvent(eventList, recyclerView,EventActivity.this::onNoteClick));

                    Intent intent = new Intent(EventParticipatingListActivity.this, EventSingleActivity.class);
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

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        //if(newText.length()==0){
        //adapterEvent.getFilter().filter(newText);
        //recyclerView.
        //}else{
        adapterEvent.getFilter().filter(newText);
        //}
        adapterEvent.notifyDataSetChanged();
        return true;
    }
}