package com.example.android_werble;

import android.content.Intent;
import android.media.Rating;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_werble.entities.Data;
import com.example.android_werble.entities.EventReview;
import com.example.android_werble.network.ApiService;
import com.example.android_werble.network.RetrofitBuilder;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "EventSingleActivity";
    RecyclerView recyclerView;
    List<EventReview> eventReviewsList;

    Call<Data<EventReview>> callReview;
    ApiService service;

    TokenManager tokenManager;

    Button back;

    TextView rating;
    TextView content;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        recyclerView = (RecyclerView) findViewById(R.id.reviewRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        rating = findViewById(R.id.reviewRating);
        content = findViewById(R.id.reviewContent);
        back = findViewById(R.id.back);

        ButterKnife.bind(this);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));

        if (tokenManager.getToken() == null) {
            startActivity(new Intent(ReviewListActivity.this, LoginActivity.class));
            finish();
        }


        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);
        Bundle b = getIntent().getExtras();
        String event_id = b.getString("event_id");

        callReview = service.getEventReview(Integer.parseInt(event_id));
        callReview.enqueue(new Callback<Data<EventReview>>() {
            @Override
            public void onResponse(Call<Data<EventReview>> call, Response<Data<EventReview>> response) {
                Log.w(TAG, "onResponse: " + response);

                if (response.isSuccessful()){
                    eventReviewsList = response.body().getData();
                    recyclerView.setAdapter(new AdapterReview(eventReviewsList, recyclerView));

                }
            }

            @Override
            public void onFailure(Call<Data<EventReview>> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());

            }
        });
    }

    @OnClick(R.id.back)
    void backtoSingleEvent(){

        Bundle b = getIntent().getExtras();
        String event_id = b.getString("event_id");

        Intent intent = new Intent(ReviewListActivity.this, EventSingleActivity.class);
        intent.putExtra("event_id",event_id);

        startActivity(intent);
        finish();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}
