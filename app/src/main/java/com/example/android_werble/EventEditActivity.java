package com.example.android_werble;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.example.android_werble.entities.ApiError;
import com.example.android_werble.entities.Event;
import com.example.android_werble.entities.Message;
import com.example.android_werble.network.ApiService;
import com.example.android_werble.network.RetrofitBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventEditActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "EventEditActivity";

    @BindView(R.id.eventEditName2)
    TextInputEditText eventEditName;
    @BindView(R.id.eventEditDescription2)
    TextInputEditText eventEditDescription;
    @BindView(R.id.eventEditDatetime2)
    TextInputEditText eventEditDatetime;
    @BindView(R.id.eventEditLocation2)
    TextInputEditText eventEditLocation;
    @BindView(R.id.eventEditType)
    Spinner eventType;

    Button editMarker,edit,back;

    ApiService service;
    Call<Event> call;
    Call<Message> callMessage;
    AwesomeValidation validator;
    TokenManager tokenManager;

    String name,description,datetime,location;//,type;
    Integer type;
    Integer typeId;

    String latitude,longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventedit);

        ButterKnife.bind(this);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));

        if (tokenManager.getToken() == null) {
            startActivity(new Intent(EventEditActivity.this, LoginActivity.class));
            finish();
        }

        service = RetrofitBuilder.createServiceWithAuth(ApiService.class,tokenManager);
        validator = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);

        eventType = findViewById(R.id.eventEditType);
        ArrayAdapter eventTypeAdapter =  ArrayAdapter.createFromResource(EventEditActivity.this,R.array.types,R.layout.arraytype);
        eventTypeAdapter.setDropDownViewResource(R.layout.arraytype);
        eventType.setAdapter(eventTypeAdapter);



        Bundle b = getIntent().getExtras();
        String event_id = b.getString("event_id");

        call = service.getSingleEvent(Integer.parseInt(event_id));
        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                if (response.isSuccessful()) {
                    Log.e(TAG, "onResponse: " + response.body());

                    Event event = response.body();
                    //user_id = user.getUserId().toString();
                    event.getEventTypeId();

                    System.out.println();
                    latitude = event.getLatitude().toString();
                    longitude = event.getLongitude().toString();

                    if (event.getName()!=null){
                        name = event.getName();
                        eventEditName.setText(name);
                    }
                    if (event.getDescription()!=null){
                        description = event.getDescription();
                        eventEditDescription.setText(description);
                    }
                    if (event.getDatetime()!=null){
                        datetime = event.getDatetime();
                        eventEditDatetime.setText(datetime);
                    }
                    if (event.getDatetime()!=null){
                        datetime = event.getDatetime();
                        eventEditDatetime.setText(datetime);
                    }
                    if (event.getEventTypeId()!=null){
                        type = event.getEventTypeId();//.toString();
                        eventType.setSelection(type-1);
                    }
                    if (event.getLocation()!=null){
                        location = event.getLocation();//.toString();
                        eventEditLocation.setText(location);
                    }


                } else {
                    handleErrors(response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());

            }
        });

        eventType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                typeId = ++position;
                //type = parent.getItemAtPosition(position).toString();
                Log.w(TAG,"type: "+type);
                System.out.println("TYPE: "+type);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        eventEditDatetime = findViewById(R.id.eventEditDatetime2);
        eventEditDatetime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimeDialog(eventEditDatetime);
            }
        });

        setupRules();
    }

    private void showDateTimeDialog(final TextInputEditText eventDatetime) {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        calendar.set(Calendar.MINUTE,minute);

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:00");

                        Date currentTime = new Date();
                        System.out.println("currentTime: "+currentTime);
                        System.out.println("choosenTime: "+calendar.getTime());
                        if (currentTime.before(calendar.getTime())){
                            eventDatetime.setText(simpleDateFormat.format(calendar.getTime()));

                        }
                        else{
                            System.out.println("DATE NOT GREATER");
                            Toast.makeText(EventEditActivity.this,"Your date must be greater than todays date",Toast.LENGTH_LONG).show();
                        }


                    }
                };

                new TimePickerDialog(EventEditActivity.this,R.style.datepicker,timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false).show();
            }
        };

        new DatePickerDialog(EventEditActivity.this,R.style.datepicker, dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    @OnClick(R.id.editMarker)
    void editMarker(){
        System.out.println("EDITINGMARKER");

        Bundle b = getIntent().getExtras();
        String event_id = b.getString("event_id");


        Intent intent = new Intent(EventEditActivity.this, EditMarkerActivity.class);
        intent.putExtra("event_id",event_id);
        intent.putExtra("name",eventEditName.getText().toString());
        intent.putExtra("location",eventEditLocation.getText().toString());
        intent.putExtra("description",eventEditDescription.getText().toString());
        intent.putExtra("datetime",eventEditDatetime.getText().toString());
        //intent.putExtra("lat",latitude);
        //intent.putExtra("lon",longitude);
        intent.putExtra("event_type_id",String.valueOf(eventType.getSelectedItemId()));

        startActivity(intent);
        finish();

        //gotoEditEvent();
    }

    private void gotoEditEvent() {
        startActivity(new Intent(EventEditActivity.this, EventEditActivity.class));
        finish();
    }

    @OnClick(R.id.editButton)
    void editEvent(){
        String name = eventEditName.getText().toString();
        String description = eventEditDescription.getText().toString();
        String datetime = eventEditDatetime.getText().toString();
        String location = eventEditLocation.getText().toString();
        //type = eventType.getText().toString();

        eventEditName.setError(null);
        eventEditDescription.setError(null);
        eventEditDatetime.setError(null);
        eventEditLocation.setError(null);
        //userDescription.setError(null);

        validator.clear();

        if (validator.validate()) {
            Bundle b = getIntent().getExtras();

            String event_id = b.getString("event_id");

            //if () { button "change marker" not clicked
                String latitude = b.getString("lat");
                String longitude = b.getString("lon");

            //}
            System.out.println("event_id: "+event_id);
            System.out.println("name: "+name);
            System.out.println("location: "+location);
            System.out.println("description: "+description);
            System.out.println("datetime: "+datetime);
            System.out.println("longitude: "+longitude);
            System.out.println("latitude: "+latitude);
            System.out.println("typeId: "+typeId);
            callMessage = service.editEvent(Integer.parseInt(event_id),name,location,description,datetime,longitude,latitude,typeId);
            System.out.println("callMessage: "+callMessage);


            callMessage.enqueue(new Callback<Message>() {

                @Override
                public void onResponse(Call<Message> call, Response<Message> response) {
                    System.out.println("ANOTHER");
                    Log.e(TAG, "onResponse: " + response);
                    Log.e(TAG, "onResponse.body: " + response.body());

                    if (response.isSuccessful()){
                        Log.e(TAG, "onResponse: " + response);
                        System.out.println("DURING CALL");

                    } else {
                    handleErrors(response.errorBody());
                }
                }

                @Override
                public void onFailure(Call<Message> call, Throwable t) {
                    Log.e(TAG, "onFailure: " + t.getMessage());

                }
            });
        }
        System.out.println("AFTER CALL");

        gotoEvent();
    }

    private void gotoEvent() {
        Bundle b = getIntent().getExtras();
        String event_id = b.getString("event_id");

        Intent intent = new Intent(EventEditActivity.this,EventSingleActivity.class);
        intent.putExtra("event_id",event_id);
        intent.putExtra("lat",latitude);
        intent.putExtra("lon",longitude);
        startActivity(intent);

        finish();
    }


    private void handleErrors(ResponseBody response) {

        ApiError apiError = Utils.converErrors(response);
        if (apiError.getErrors() != null) {
            Log.w("no errors", "apiError.getErrors()"+apiError.getErrors());

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
            }
        } else {
            Log.e("no errors", "weird");
        }
    }

    private void setupRules() {
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.w(TAG,"SIDEBAR");
        //Toast.makeText(this,"TOST",Toast.LENGTH_LONG).show();
        switch (item.getTitle().toString()) {
            //case "Logout": logout(); break;
            /*case "Your profile": gotoProfile(); break;
            //case "Your events":
            case "Map": gotoMap(); break;
            case "Create event": gotoCreateEvent(); break;
            case "Settings": gotoSettings(); break;
*/
        }
        return false;    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

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
