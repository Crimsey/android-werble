package com.example.android_werble;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_werble.entities.ApiError;
import com.example.android_werble.entities.Data;
import com.example.android_werble.entities.Event;
import com.example.android_werble.entities.EventParticipant;
import com.example.android_werble.entities.EventReview;
import com.example.android_werble.entities.EventType;
import com.example.android_werble.entities.Message;
import com.example.android_werble.entities.User;
import com.example.android_werble.network.ApiService;
import com.example.android_werble.network.RetrofitBuilder;

import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventSingleActivity extends AppCompatActivity {

    private static final String TAG = "EventSingleActivity";
    RecyclerView recyclerView;
    List<EventParticipant> eventParticipantList;
    List<EventReview> eventReviewList;
    List<EventType> eventTypeList;


    Call<Message> callJoin;
    ApiService service;
    Call<Event> call;
    Call<User> callUser;
    Call<Data<EventParticipant>> callParticipant;
    Call<Data<EventReview>> callReview;
    Call<Data<EventType>> callType;

    //Call<AccessToken> callAccessToken;
    //AwesomeValidation validator;
    TokenManager tokenManager;


    TextView name,location,zip_code,street_name,house_number,description,startdatetime,enddatetime,status,type,distance;

    Button addReview,seeReviews;
    Button joinSingleEvent;
    Button editSingleEvent;
    Button leaveSingleEvent;

    int ended=0;
    String latitude,longitude;
    String participantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventsingle);

        recyclerView = (RecyclerView) findViewById(R.id.participantRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        name = findViewById(R.id.singleEventName);
        location = findViewById(R.id.singleEventLocation);
        zip_code = findViewById(R.id.singleEventZipcode);
        street_name = findViewById(R.id.singleEventStreetName);
        house_number = findViewById(R.id.singleEventHouseNumber);
        description = findViewById(R.id.singleEventDescription);
        startdatetime = findViewById(R.id.singleEventStartDatetime);
        enddatetime = findViewById(R.id.singleEventEndDatetime);

        status = findViewById(R.id.singleEventStatus);
        type = findViewById(R.id.singleEventType);
        distance = findViewById(R.id.singleEventDistance);


        addReview = findViewById(R.id.addReview);
        seeReviews = findViewById(R.id.seeReviews);
        joinSingleEvent = findViewById(R.id.joinSingleEvent);
        editSingleEvent = findViewById(R.id.editSingleEvent);
        leaveSingleEvent = findViewById(R.id.leaveSingleEvent);

        Log.w(TAG, "My tu w ogóle wchodzimy?");
        ButterKnife.bind(this);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));

        if (tokenManager.getToken() == null) {
            startActivity(new Intent(EventSingleActivity.this, LoginActivity.class));
            finish();
        }


        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);
        //validator = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);

        Bundle b = getIntent().getExtras();
        String event_id = b.getString("event_id");

        call = service.getSingleEvent(Integer.parseInt(event_id));

        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {

                Log.w(TAG, "CHECK");
                if (response.isSuccessful()) {
                    Log.e(TAG, "onResponse: " + response.body());

                    Event event = response.body();
                    latitude = event.getLatitude().toString();
                    longitude = event.getLongitude().toString();

                    if (TextUtils.isEmpty(event.getName())
                    ){
                        name.setText("Name: ");
                    }else{name.setText(event.getName());}

                    if (event.getLocation()==null){
                        location.setText("Location: ");
                    }else {location.setText("Location: "+event.getLocation());}

                    if (event.getZipCode()==null){
                        zip_code.setText("Zipcode: ");
                    }else {zip_code.setText("Zipcode: "+event.getZipCode());}

                    if (event.getStreetName()==null){
                        street_name.setText("Street: ");
                    }else {street_name.setText("Street: "+event.getStreetName());}

                    if (event.getHouseNumber()==null){
                        house_number.setText("House number: ");
                    }else {house_number.setText("House number: "+event.getHouseNumber());}

                    if (event.getDescription()==null){
                        description.setText("Description: ");
                    }else {description.setText("Description: "+event.getDescription());}

                    if (event.getStartDatetime()==null){
                        startdatetime.setText("Begin: ");
                    }else {startdatetime.setText("Begin: "+event.getStartDatetime());}

                    if (event.getEndDatetime()==null){
                        enddatetime.setText("End: ");
                    }else {enddatetime.setText("End: "+event.getEndDatetime());}

                    if (event.getDistance()==null){
                        distance.setText("Distance: ");
                    }else {distance.setText("Distance: "+event.getDistance().toString()+"km");}

                    callType = service.getEventTypes();
                    callType.enqueue(new Callback<Data<EventType>>() {
                        @Override
                        public void onResponse(Call<Data<EventType>> call, Response<Data<EventType>> response) {
                            if (response.isSuccessful()) {
                                eventTypeList = response.body().getData();
                                if (event.getEventTypeId() == null) {
                                    type.setText("Type: ");
                                } else {
                                    String typeString = String.valueOf(eventTypeList.get(event.getEventTypeId() - 1));
                                    type.setText("Type: " +typeString);
                                }
                            } else {
                                type.setText("Type: ");

                            }
                            StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
                            SpannableString spannableType = new SpannableString(type.getText());
                            spannableType.setSpan(boldSpan,0,4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            type.setText(spannableType);

                        }

                        @Override
                        public void onFailure(Call<Data<EventType>> call, Throwable t) {

                        }
                    });

                    if (event.getIsActive()==null){
                        status.setText("Status: ");
                    }else {
                        switch (event.getIsActive()) {
                            case 0:
                                status.setText("Status: Ended");
                                ended++;
                                break;
                            case 1:
                                status.setText("Status: Started");
                                break;
                            case 2:
                                status.setText("Status: Not started yet");
                                break;
                        }
                    }
                    StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);

                    SpannableString spannableLocation = new SpannableString(location.getText());
                    SpannableString spannableZipCode = new SpannableString(zip_code.getText());
                    SpannableString spannableStreet = new SpannableString(street_name.getText());
                    SpannableString spannableHouseNum = new SpannableString(house_number.getText());
                    SpannableString spannableDescription = new SpannableString(description.getText());
                    SpannableString spannableStartDatetime = new SpannableString(startdatetime.getText());
                    SpannableString spannableEndDatetime = new SpannableString(enddatetime.getText());
                    SpannableString spannableDistance = new SpannableString(distance.getText());
                    SpannableString spannableStatus = new SpannableString(status.getText());

                    spannableLocation.setSpan(boldSpan,0,8, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                    spannableZipCode.setSpan(boldSpan,0,7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannableStreet.setSpan(boldSpan,0,6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannableHouseNum.setSpan(boldSpan,0,12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannableDescription.setSpan(boldSpan,0,11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannableStartDatetime.setSpan(boldSpan,0,6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannableEndDatetime.setSpan(boldSpan,0,4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannableDistance.setSpan(boldSpan,0,8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannableStatus.setSpan(boldSpan,0,6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    location.setText(spannableLocation);
                    zip_code.setText(spannableZipCode);
                    street_name.setText(spannableStreet);
                    house_number.setText(spannableHouseNum);
                    description.setText(spannableDescription);
                    startdatetime.setText(spannableStartDatetime);
                    enddatetime.setText(spannableEndDatetime);
                    distance.setText(spannableDistance);
                    status.setText(spannableStatus);

                    callUser = service.user();
                    callUser.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            if (response.isSuccessful()){
                                Log.w(TAG, "onResponse: " +response.body());
                                User user = response.body();
                                if (event.getEventCreatorId() == user.getUserId()){
                                    editSingleEvent.setClickable(true);
                                    editSingleEvent.setVisibility(View.VISIBLE);
                                }else{
                                    editSingleEvent.setClickable(false);
                                    editSingleEvent.setVisibility(View.INVISIBLE);
                                }
                            }
                        }
                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            Log.w(TAG, "onFailure: " + t.getMessage());
                        }
                    });
                } else {
                    handleErrors(response.errorBody());
                }
            }
            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });

        callUser = service.user();
        callUser.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {

                    User user = response.body();
                    Integer user_id = user.getUserId();
                    callParticipant = service.getEventParticipant(Integer.parseInt(event_id));
                    callParticipant.enqueue(new Callback<Data<EventParticipant>>() {

                        @Override
                        public void onResponse(Call<Data<EventParticipant>> call, Response<Data<EventParticipant>> response) {
                            Log.w(TAG, "PARTICIPANT" + response);

                            if (response.isSuccessful()) {
                                eventParticipantList = response.body().getData();
                                recyclerView.setAdapter(new AdapterParticipant(eventParticipantList, recyclerView));

                                int help=0;
                                for (EventParticipant eventParticipant : eventParticipantList) {
                                    if (eventParticipant.getUserId() == user_id){
                                        participantId =  eventParticipant.getEventParticipantId().toString();
                                        help++;
                                    }

                                }
                                addReview.setClickable(false);
                                seeReviews.setClickable(false);
                                addReview.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blankblue)));
                                seeReviews.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blankblue)));
                                leaveSingleEvent.setVisibility(View.GONE);

                                if (help>0){ //participant
                                    joinSingleEvent.setVisibility(View.GONE);
                                    leaveSingleEvent.setVisibility(View.VISIBLE);
                                }
                                if (ended==1){//ended
                                    seeReviews.setClickable(true);
                                    seeReviews.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));
                                    joinSingleEvent.setClickable(false);
                                    joinSingleEvent.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blank)));
                                }
                                if (help>0 && ended==1)//participant+ended
                                {
                                    addReview.setClickable(true);
                                    addReview.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));

                                    Bundle b = getIntent().getExtras();
                                    String event_id = b.getString("event_id");
                                    callReview = service.getEventReview(Integer.parseInt(event_id));
                                    callReview.enqueue(new Callback<Data<EventReview>>() {
                                        @Override
                                        public void onResponse(Call<Data<EventReview>> call, Response<Data<EventReview>> response) {
                                            if (response.isSuccessful()){
                                                eventReviewList = response.body().getData();
                                                for (EventReview eventReview : eventReviewList){
                                                    if (eventReview.getEventParticipantId().toString().equals(participantId)){
                                                        addReview.setClickable(false);
                                                        addReview.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blankblue)));
                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Data<EventReview>> call, Throwable t) {

                                        }
                                    });
                                }

                            } else {
                                Log.w(TAG, "POST-PARTICIPANT");
                            }
                        }

                        @Override
                        public void onFailure(Call<Data<EventParticipant>> call, Throwable t) {
                            Log.w(TAG, "onFailure: " + t.getMessage());

                        }
                    });

                }
            }


            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });

        //setupRules();

    }
        private void handleErrors (ResponseBody response){

            ApiError apiError = Utils.converErrors(response);
            if (apiError.getErrors() != null) {
                Log.w("no errors", "apiError.getErrors()" + apiError.getErrors());

                for (Map.Entry<String, List<String>> error : apiError.getErrors().entrySet()) {
                    /*if (error.getKey().equals("first_name")) {
                        userFirstName.setError(error.getValue().get(0));
                    }
                    if (error.getKey().equals("last_name")) {
                        userLastName.setError(error.getValue().get(0));
                    }
                    if (error.getKey().equals("birth_date")) {
                        userBirthDate.setError(error.getValue().get(0));
                    }
                    if (error.getKey().equals("description")) {
                        userDescription.setError(error.getValue().get(0));
                    }*/
                /*if (error.getKey().equals("password")) {
                    userPassword.setError(error.getValue().get(0));
                }*/
                }
            } else {
                Log.e("no errors", "weird");
            }
        }

        @OnClick(R.id.joinSingleEvent)
        void joinSingleEvent(){
            Bundle b = getIntent().getExtras();
            String event_id = b.getString("event_id");

            callJoin = service.joinEvent(Integer.parseInt(event_id));
            callJoin.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                Log.w(TAG, "You have joined!: " + response);
                Toast.makeText(EventSingleActivity.this,"JOINING EVENT",Toast.LENGTH_LONG).show();
                finish();
                startActivity(getIntent());

            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());

            }
        });

        }

    @OnClick(R.id.leaveSingleEvent)
    void leaveSingleEvent(){
        Bundle b = getIntent().getExtras();
        String event_id = b.getString("event_id");

        callJoin = service.leaveEvent(Integer.parseInt(event_id));
        callJoin.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                Log.w(TAG, "You have left event!: " + response);
                Toast.makeText(EventSingleActivity.this,"LEAVING EVENT",Toast.LENGTH_LONG).show();
                finish();
                startActivity(getIntent());
            }
            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());

            }
        });

    }

    @OnClick(R.id.addReview)
    void gotoReview(){
        Bundle b = getIntent().getExtras();
        String event_id = b.getString("event_id");

        Intent intent = new Intent(EventSingleActivity.this, ReviewCreateActivity.class);
        intent.putExtra("event_id",event_id);
        intent.putExtra("event_participant_id",participantId);


        startActivity(intent);
        finish();

    }

    @OnClick(R.id.editSingleEvent)
    void gotoEditSingleEvent(){
        Bundle b = getIntent().getExtras();
        String event_id = b.getString("event_id");

        Intent intent = new Intent(EventSingleActivity.this, EventEditActivity.class);
        intent.putExtra("event_id",event_id);
        intent.putExtra("lat",latitude);
        intent.putExtra("lon",longitude);
        intent.putExtra("event_participant_id",participantId);

        startActivity(intent);
        finish();

    }

    @OnClick(R.id.seeReviews)
    void listReviews(){
        Bundle b = getIntent().getExtras();
        String event_id = b.getString("event_id");

        Intent intent = new Intent(EventSingleActivity.this, ReviewListActivity.class);
        intent.putExtra("event_id",event_id);
        intent.putExtra("event_participant_id",participantId);

        startActivity(intent);
        finish();
    }

        @OnClick(R.id.returntomaporlist)
        void gotoMap() {
            switch (MyApplication.getManaging()){
                case 2: startActivity(new Intent(this, MyLocationActivity.class)); break;
                case 3: startActivity(new Intent(this, EventOwnedListActivity.class)); break;
                case 4: startActivity(new Intent(this, EventParticipatingListActivity.class)); break;
                default: startActivity(new Intent(this, EventLocalListActivity.class)); break;
            }
            finish();

        }


    /*public void setupRules() {
        validator.addValidation(this, R.id.userFirstName, RegexTemplate.NOT_EMPTY, R.string.err_event_name);
        validator.addValidation(this, R.id.userLastName, RegexTemplate.NOT_EMPTY, R.string.err_event_name);
        validator.addValidation(this, R.id.userBirthDate, RegexTemplate.NOT_EMPTY, R.string.err_event_name);
        //validator.addValidation(this, R.id.userDescription, RegexTemplate.NOT_EMPTY, R.string.err_event_name);

    }*/


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (call != null) {
            call.cancel();
            call = null;
        }
    }

    }

