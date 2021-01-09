package com.example.android_werble;

import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_werble.entities.Data;
import com.example.android_werble.entities.Event;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventOwnedListActivity extends MyListActivity{

    private static final String TAG = "EventOwnedActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        globalRange.setText("");
        getOwnedEvents();
    }

    void getOwnedEvents() {
        call = service.getOwnedEvents();
        call.enqueue(new Callback<Data<Event>>() {

            @Override
            public void onResponse(Call<Data<Event>> call, Response<Data<Event>> response) {
                Log.w(TAG, "onResponse: " + response);

                if (response.isSuccessful()) {
                    eventList = response.body().getData();
                    adapterEvent = new AdapterEvent(eventList, context);
                    recyclerView.setAdapter(adapterEvent);
                }
            }

            @Override
            public void onFailure(Call<Data<Event>> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
}