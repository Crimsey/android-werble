package com.example.android_werble;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.example.android_werble.entities.EventReview;
import com.example.android_werble.entities.Message;
import com.example.android_werble.network.ApiService;
import com.example.android_werble.network.RetrofitBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewEditActivity extends AppCompatActivity implements ViewDialog.ViewDialogListener {

    private static final String TAG = "EventEditActivity";

    @BindView(R.id.ReviewEditContent2)
    TextInputEditText reviewEditContent;
    @BindView(R.id.reviewRatingEdit)
    RatingBar reviewRatingEdit;


    Button editReview,backToSingleEvent;
    ApiService service;
    Call<Message> callMessage;
    Call<EventReview> callEventReview;
    AwesomeValidation validator;
    TokenManager tokenManager;

    String content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviewedit);

        ButterKnife.bind(this);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));

        if (tokenManager.getToken() == null) {
            startActivity(new Intent(ReviewEditActivity.this, LoginActivity.class));
            finish();
        }
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class,tokenManager);
        validator = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);
        setupRules();

        Bundle b = getIntent().getExtras();
        String event_id = b.getString("event_id");

        callEventReview = service.getSingleReview(Integer.parseInt(event_id));
        callEventReview.enqueue(new Callback<EventReview>() {
            @Override
            public void onResponse(Call<EventReview> call, Response<EventReview> response) {
                if (response.isSuccessful()){
                    EventReview eventReview = response.body();
                    content = eventReview.getContent();
                    reviewEditContent.setText(content);
                    System.out.println("reviewEditContent"+reviewEditContent);

                    reviewRatingEdit.setRating(eventReview.getRating());

                }
            }

            @Override
            public void onFailure(Call<EventReview> call, Throwable t) {

            }
        });
    }

    @OnClick(R.id.EditReviewButton)
    void editReview(){
        String content = reviewEditContent.getText().toString();
        Integer rating = (int) reviewRatingEdit.getRating();

        if (reviewRatingEdit.getRating()==0){
            Toast.makeText(ReviewEditActivity.this, "Rating cannot be zero", Toast.LENGTH_LONG).show();
        }else {
        reviewEditContent.setError(null);
        validator.clear();
        if (validator.validate()){
            Bundle b = getIntent().getExtras();
            String event_id = b.getString("event_id");
            callMessage = service.editReview(Integer.parseInt(event_id),rating,content);
            callMessage.enqueue(new Callback<Message>() {
                @Override
                public void onResponse(Call<Message> call, Response<Message> response) {
                    Log.e(TAG, "onResponse: " + response);
                    gotoReviewList();
                }

                @Override
                public void onFailure(Call<Message> call, Throwable t) {
                    Log.e(TAG, "onFailure: " + t.getMessage());
                }
            });
        }
        }
    }

    @OnClick(R.id.deleteReview)
    void deleteReview(){
        ViewDialog alert = new ViewDialog();
        alert.showDialog(this);
    }

    @OnClick(R.id.BackFromEditReview)
    void gotoReviewList() {
        Bundle b = getIntent().getExtras();
        String event_id = b.getString("event_id");
        String event_participant_id = b.getString("event_participant_id");


        Intent intent = new Intent(ReviewEditActivity.this, ReviewListActivity.class);
        intent.putExtra("event_id",event_id);
        intent.putExtra("event_participant_id",event_participant_id);

        startActivity(intent);
        finish();
    }

    private void setupRules() {
        validator.addValidation(this, R.id.ReviewEditContent, "{1,280}", R.string.err_reviewcontent);
    }

    @Override
    public void onDeleteClick() {
        Bundle b = getIntent().getExtras();
        String event_participant_id = b.getString("event_participant_id");
        Integer event_participant_idInteger = Integer.parseInt(event_participant_id);
        callMessage = service.deleteReview(event_participant_idInteger);
        callMessage.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if (response.isSuccessful()) {
                    Log.e(TAG, "onResponse: " + response);

                }
            }


        @Override
        public void onFailure(Call<Message> call, Throwable t) {
            Log.e(TAG, "onFailure: " + t.getMessage());

        }
    });
    gotoReviewList();
    }

}
