package com.example.android_werble;

import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.android_werble.entities.Data;
import com.example.android_werble.entities.Event;

import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventParticipatingListActivity extends MyListActivity
{
    private static final String TAG = "EventPartActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventsTitle.setText("PARTICIPATING");
        globalRange.setText("");
        getParticipatingEvents();
    }

    void getParticipatingEvents() {
        call = service.getParticipatingEvents();

        call.enqueue(new Callback<Data<Event>>() {
            @Override
            public void onResponse(Call<Data<Event>> call, Response<Data<Event>> response) {
                Log.w(TAG,"GETLOCALEVENTS");
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