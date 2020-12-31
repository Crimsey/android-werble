package com.example.android_werble;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.example.android_werble.entities.AccessToken;
import com.example.android_werble.entities.ApiError;
import com.example.android_werble.entities.Data;
import com.example.android_werble.entities.Event;
import com.example.android_werble.entities.EventParticipant;
import com.example.android_werble.entities.Message;
import com.example.android_werble.entities.User;
import com.example.android_werble.network.ApiService;
import com.example.android_werble.network.RetrofitBuilder;
import com.google.android.material.navigation.NavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventSingleActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "EventSingleActivity";
    RecyclerView recyclerView;
    List<EventParticipant> eventParticipantList;

    Call<Message> callJoin;
    ApiService service;
    Call<Event> call;
    Call<User> callUser;
    Call<Data<EventParticipant>> callParticipant;
    Call<Message> callReview;

    Call<AccessToken> callAccessToken;
    AwesomeValidation validator;
    TokenManager tokenManager;


    TextView name,location,zip_code,street_name,house_number,description,datetime,status,type;

    Button addReview,seeReviews;
    Button joinSingleEvent;
    Button editSingleEvent;

    int yet=0,ended=0,participant=0;
    String latitude,longitude;


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
        datetime = findViewById(R.id.singleEventDatetime);
        status = findViewById(R.id.singleEventStatus);
        type = findViewById(R.id.singleEventType);

        addReview = findViewById(R.id.addReview);
        seeReviews = findViewById(R.id.seeReviews);
        joinSingleEvent = findViewById(R.id.joinSingleEvent);
        editSingleEvent = findViewById(R.id.editSingleEvent);

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
                        name.setText("no name :(");
                    }else{name.setText(event.getName());}

                    if (event.getLocation()==null){
                        location.setText("no location :(");
                    }else {location.setText(event.getLocation());}

                    if (event.getZipCode()==null){
                        zip_code.setText("no zipcode :(");
                    }else {zip_code.setText(event.getZipCode());}

                    if (event.getStreetName()==null){
                        street_name.setText("no street name :(");
                    }else {street_name.setText(event.getStreetName());}

                    if (event.getHouseNumber()==null){
                        house_number.setText("no house number :(");
                    }else {house_number.setText(event.getHouseNumber());}

                    if (event.getDescription()==null){
                        street_name.setText("no description :(");
                    }else {street_name.setText(event.getDescription());}

                    if (event.getDatetime()==null){
                        datetime.setText("no datetime :(");
                    }else {datetime.setText(event.getDatetime());}

                    if (event.getEventTypeId()==null){
                        type.setText("no type :(");
                    }else {type.setText(getResources().getStringArray(R.array.types)[event.getEventTypeId()-1]);}

                    if (event.getEventStatusId()==null){
                        status.setText("no status :(");
                    }else {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:00");
                        Date currentTime = new Date();


                    try {
                        Date eventDate = simpleDateFormat.parse(event.getDatetime());

                        //currentTime = simpleDateFormat.parse(String.valueOf(currentTime));
                        if (currentTime.after(eventDate)){
                            event.setEventStatusId(2);
                        }else{
                            event.setEventStatusId(1);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                        System.out.println("event.getEventStatusId(): "+event.getEventStatusId());


                        switch (event.getEventStatusId()) {
                            case 1:
                                status.setText("Not started yet");
                                yet++;
                                /*seeReviews.setClickable(false);
                                seeReviews.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blankblue)));

                                addReview.setClickable(false);
                                addReview.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blankblue)));

                                joinSingleEvent.setClickable(true);
                                joinSingleEvent.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.teal_700)));*/
                            break;

                            /*case 2:
                                status.setText("Trwa");
                                seeReviews.setClickable(false);
                                seeReviews.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blankblue)));

                                addReview.setClickable(false);
                                addReview.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blankblue)));

                                joinSingleEvent.setClickable(true);
                                joinSingleEvent.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.teal_700)));
                            break;*/

                            case 2:
                                status.setText("Ended");
                                ended++;
                                /*seeReviews.setClickable(true);
                                seeReviews.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));

                                addReview.setClickable(true);
                                addReview.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));

                                joinSingleEvent.setClickable(true);
                                joinSingleEvent.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blank)));*/
                            break;
                        }
                    }


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
                                    System.out.println("I AM CREATOR");
                                }else{
                                    editSingleEvent.setClickable(true);
                                    editSingleEvent.setVisibility(View.INVISIBLE);
                                    System.out.println("I AM NOT CREATOR");

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

        Log.w(TAG, "PRE-PARTICIPANT");

        callUser = service.user();
        callUser.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {

                    User user = response.body();
                    Integer user_id = user.getUserId();
                    String login = user.getLogin();
                    callParticipant = service.getEventParticipant(Integer.parseInt(event_id));
                    callParticipant.enqueue(new Callback<Data<EventParticipant>>() {

                        @Override
                        public void onResponse(Call<Data<EventParticipant>> call, Response<Data<EventParticipant>> response) {
                            Log.w(TAG, "PARTICIPANT" + response);

                            if (response.isSuccessful()) {
                                eventParticipantList = response.body().getData();
                                recyclerView.setAdapter(new AdapterParticipant(eventParticipantList, recyclerView));
                                Log.w(TAG, "PARTICIPANT2");

                                //hide buttons if USER ISNT PARTICIPANT
                                //AND IN FUTURE IF EVENT ISNT'T DONE!!!!!!!!!!1
                                int help=0,help2=0;
                                for (EventParticipant eventParticipant : eventParticipantList) {
                                    if (eventParticipant.getUserId() == user_id){
                                        help++;
                                        System.out.println("help"+help);
                                    }
                                    /*if (eventParticipant.getLogin().equals(login)){
                                        help2++;
                                    }*/
                                }

                                addReview.setClickable(false);
                                seeReviews.setClickable(false);
                                addReview.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blankblue)));
                                seeReviews.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blankblue)));

                                if (help>0){ //participant
                                    System.out.println("help>0");
                                    addReview.setClickable(true);
                                    joinSingleEvent.setClickable(false);
                                    joinSingleEvent.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blank)));
                                    Log.w(TAG,"help>0 = "+help);


                                }
                                if (ended==1){//ended
                                    System.out.println("ended==1");
                                    seeReviews.setClickable(true);
                                    seeReviews.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));
                                }

                                if (help>0 && ended==1)//participant+ended
                                {
                                    System.out.println("help>0 && ended==1");
                                    addReview.setClickable(true);
                                    addReview.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));

                                }






                                /*else {addReview.setClickable(false);
                                       addReview.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blankblue)));
                                        Log.w(TAG,"help=0 = "+help);

                                    seeReviews.setClickable(false);
                                    seeReviews.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blankblue)));


                                    joinSingleEvent.setClickable(true);
                                    joinSingleEvent.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.teal_700)));

                                }*/

                                /*if (help2>0){
                                    addReview.setClickable(false);
                                    addReview.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blankblue)));
                                    Log.w(TAG,"help2>0 = "+help2);

                                }else {
                                    addReview.setClickable(true);
                                    addReview.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));
                                    Log.w(TAG,"help2= "+help2);

                                }*/


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
        callJoin = service.joinEvent(Integer.parseInt(event_id),"1");
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

    @OnClick(R.id.addReview)
    void gotoReview(){
        //startActivity(new Intent(EventSingleActivity.this,ReviewCreateActivity.class));
        //finish();
        //Log.w(TAG,"Going to Review");

        Bundle b = getIntent().getExtras();
        String event_id = b.getString("event_id");

        Intent intent = new Intent(EventSingleActivity.this, ReviewCreateActivity.class);
        intent.putExtra("event_id",event_id);

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

        startActivity(intent);
        finish();

    }

    @OnClick(R.id.seeReviews)
    void listReviews(){
        Bundle b = getIntent().getExtras();
        String event_id = b.getString("event_id");

        Intent intent = new Intent(EventSingleActivity.this, ReviewListActivity.class);
        intent.putExtra("event_id",event_id);

        startActivity(intent);
        finish();
    }

        @OnClick(R.id.returntomap)
        void gotoMap() {
            //Toast.makeText(EventActivity.this,"MAP",Toast.LENGTH_LONG).show();
            startActivity(new Intent(EventSingleActivity.this,MyLocationActivity.class));
            finish();
            Log.w(TAG,"Returning to map");
        }


    /*public void setupRules() {
        validator.addValidation(this, R.id.userFirstName, RegexTemplate.NOT_EMPTY, R.string.err_event_name);
        validator.addValidation(this, R.id.userLastName, RegexTemplate.NOT_EMPTY, R.string.err_event_name);
        validator.addValidation(this, R.id.userBirthDate, RegexTemplate.NOT_EMPTY, R.string.err_event_name);
        //validator.addValidation(this, R.id.userDescription, RegexTemplate.NOT_EMPTY, R.string.err_event_name);

    }*/

        @Override
        public boolean onNavigationItemSelected (@NonNull MenuItem item){
            return false;
        }

        @Override
        public void onPointerCaptureChanged ( boolean hasCapture){

        }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (call != null) {
            call.cancel();
            call = null;
        }
    }

    }

