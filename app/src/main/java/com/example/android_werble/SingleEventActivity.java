package com.example.android_werble;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
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

import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SingleEventActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "SingleEventActivity";
    RecyclerView recyclerView;
    List<EventParticipant> eventParticipantList;

    Call<Message> callJoin;
    ApiService service;
    Call<Event> call;
    Call<Data<EventParticipant>> callParticipant;

    Call<AccessToken> callAccessToken;
    AwesomeValidation validator;
    TokenManager tokenManager;

    //String event_id;
    TextView name,location,zip_code,street_name,house_number,description,datetime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singleevent);

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

        Log.w(TAG, "My tu w ogóle wchodzimy?");
        ButterKnife.bind(this);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));

        if (tokenManager.getToken() == null) {
            startActivity(new Intent(SingleEventActivity.this, LoginActivity.class));
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
                    //Data2<Event> eventData2 = response.body();
                    //event_id = event.getEventId().toString();
                    //List<Event> eventList = response.body().getData2();


                    //Event[] e = eventList.toArray(new Event[eventList.size()]);
                    //Event event = e[0];


                    //Log.w(TAG,"PÓŁNOCNICA"+ String.valueOf(event));

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

        callParticipant = service.getEventParticipant(Integer.parseInt(event_id));
        callParticipant.enqueue(new Callback<Data<EventParticipant>>() {

            @Override
            public void onResponse(Call<Data<EventParticipant>> call, Response<Data<EventParticipant>> response) {
                Log.w(TAG, "PARTICIPANT" + response);

                if (response.isSuccessful()){
                    eventParticipantList = response.body().getData();
                    recyclerView.setAdapter(new AdapterParticipant(eventParticipantList, recyclerView));
                    Log.w(TAG, "PARTICIPANT2");

                }
             else {
                Log.w(TAG, "POST-PARTICIPANT");
            }
            }

            @Override
            public void onFailure(Call<Data<EventParticipant>> call, Throwable t) {
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
                Toast.makeText(SingleEventActivity.this,"JOINING EVENT",Toast.LENGTH_LONG).show();

                finish();
                startActivity(getIntent());
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());

            }
        });

        }

        @OnClick(R.id.returntomap)
        void gotoMap() {
            //Toast.makeText(EventActivity.this,"MAP",Toast.LENGTH_LONG).show();
            startActivity(new Intent(SingleEventActivity.this,MyLocationActivity.class));
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

