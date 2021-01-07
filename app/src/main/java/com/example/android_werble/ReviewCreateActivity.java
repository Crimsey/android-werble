package com.example.android_werble;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.example.android_werble.entities.AccessToken;
import com.example.android_werble.entities.Message;
import com.example.android_werble.network.ApiService;
import com.example.android_werble.network.RetrofitBuilder;
import com.google.android.material.textfield.TextInputLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewCreateActivity extends AppCompatActivity {

    private static final String TAG = "ReviewCreateActivity";

    ApiService service;
    Call<Message> callReview;
    TokenManager tokenManager;
    AwesomeValidation validator;

    RatingBar rating;
    //TextView content;
    @BindView(R.id.reviewContent)
    TextInputLayout content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviewcreate);

        rating = findViewById(R.id.rating);
        content = findViewById(R.id.reviewContent);

        ButterKnife.bind(this);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));

        if (tokenManager.getToken() == null) {
            startActivity(new Intent(ReviewCreateActivity.this, LoginActivity.class));
            finish();
        }

        service = RetrofitBuilder.createServiceWithAuth(ApiService.class,tokenManager);
        validator = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);
        setupRules();
    }

    @OnClick(R.id.CreateReviewButton)
    void createReview(){

        Log.w(TAG,"Going to Review");

        Bundle b = getIntent().getExtras();
        String event_id = b.getString("event_id");

        String ratingString = String.valueOf(rating.getRating());
        String contentString = content.getEditText().getText().toString();

        if (rating.getRating()==0){
            Toast.makeText(ReviewCreateActivity.this, "Rating cannot be zero", Toast.LENGTH_LONG).show();
        }else {
            content.setError(null);
            validator.clear();
            if (validator.validate()) {
                callReview = service.createReview(contentString, ratingString, event_id);
                callReview.enqueue(new Callback<Message>() {
                    @Override
                    public void onResponse(Call<Message> call, Response<Message> response) {
                        Log.w(TAG, "You have created review!: " + response);
                        Toast.makeText(ReviewCreateActivity.this, "CREATING REVIEW", Toast.LENGTH_LONG).show();
                        gotoReviewList();
                        //finish();
                        //startActivity(getIntent());
                    }

                    @Override
                    public void onFailure(Call<Message> call, Throwable t) {
                        Log.w(TAG, "onFailure: " + t.getMessage());

                    }
                });
            }
        }

    }

    public void setupRules() {
        validator.addValidation(this, R.id.reviewContent, RegexTemplate.NOT_EMPTY, R.string.err_reviewcontent);
    }

    void gotoReviewList() {
        Bundle b = getIntent().getExtras();
        String event_id = b.getString("event_id");
        String variable = b.getString("variable");
        String event_participant_id = b.getString("event_participant_id");


        Intent intent = new Intent(ReviewCreateActivity.this, ReviewListActivity.class);
        intent.putExtra("event_id",event_id);
        intent.putExtra("variable",variable);
        intent.putExtra("event_participant_id",event_participant_id);

        startActivity(intent);
        finish();
    }
}
