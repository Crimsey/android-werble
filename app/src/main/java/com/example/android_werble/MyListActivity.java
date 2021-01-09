package com.example.android_werble;

import android.os.Bundle;
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

public class MyListActivity extends NavigationActivity
implements SearchView.OnQueryTextListener
{
    protected RecyclerView recyclerView;
    protected List <Event> eventList;
    protected Call<Data<Event>> call;
    protected AdapterEvent adapterEvent;
    protected SearchView searchEvent;

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

        ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (call != null) {
            call.cancel();
            call = null;
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

