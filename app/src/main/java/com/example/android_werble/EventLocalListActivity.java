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
import android.widget.TextView;
import android.widget.Toast;

import com.example.android_werble.entities.Data;
import com.example.android_werble.entities.Event;
import com.example.android_werble.entities.Message;
import com.example.android_werble.network.ApiService;
import com.example.android_werble.network.RetrofitBuilder;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventLocalListActivity extends NavigationActivity implements
        SearchView.OnQueryTextListener
         {

    private static final String TAG = "EventActivity";

    RecyclerView recyclerView;
    List <Event> eventList;
    Call<Data<Event>> call;
    AdapterEvent adapterEvent;
    SearchView searchEvent;

    @BindView(R.id.globalRange)
    TextView globalRange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        recyclerView = (RecyclerView) findViewById(R.id.eventsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        searchEvent = findViewById(R.id.searchEvent);
        searchEvent.setOnQueryTextListener(this);
        context=this;

        ButterKnife.bind(this);

        getLocalEvents();
    }

    void getLocalEvents() {
        call = service.getLocalEvents(MyApplication.getGlobalRangeVariable());
            globalRange.setText("RANGE OF EXPLORING: "+MyApplication.getGlobalRangeVariable());
        call.enqueue(new Callback<Data<Event>>() {
            @Override
            public void onResponse(Call<Data<Event>> call, Response<Data<Event>> response) {
                Log.w(TAG, "onResponse: " + response);
                if (response.isSuccessful()) {
                    eventList = response.body().getData();
                    adapterEvent = new AdapterEvent(eventList,context);
                    recyclerView.setAdapter(adapterEvent);
                }
            }
            @Override
            public void onFailure(Call<Data<Event>> call, Throwable t) {
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

             @Override
             public boolean onQueryTextSubmit(String query) {
                 return false;
             }

             @Override
             public boolean onQueryTextChange(String newText) {
                 adapterEvent.getFilter().filter(newText);
                 adapterEvent.notifyDataSetChanged();
                 return true;
             }
         }