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
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.example.android_werble.entities.ApiError;
import com.example.android_werble.entities.Data;
import com.example.android_werble.entities.Event;
import com.example.android_werble.entities.EventType;
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

public class EventEditActivity extends AppCompatActivity implements ViewDialog.ViewDialogListener{

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
    @BindView(R.id.eventEditZipcode2)
    TextInputEditText eventEditZipcode;
    @BindView(R.id.eventEditHouseNum2)
    TextInputEditText eventEditHouseNum;
    @BindView(R.id.eventEditStreet2)
    TextInputEditText eventEditStreet;

    Button editMarker,edit,back;

    ApiService service;
    Call<Event> call;
    Call<Data<EventType>> callEventType;
    Call<Message> callMessage;
    AwesomeValidation validator;
    TokenManager tokenManager;

    String name,description,datetime,location,zip_code,house_number,street_name;//,type;
    Integer type;
    Integer typeId;

    String latitude,longitude;
    List<EventType> eventTypeList;

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


        Bundle b = getIntent().getExtras();
        String event_id = b.getString("event_id");

        call = service.getSingleEvent(Integer.parseInt(event_id));
        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                if (response.isSuccessful()) {
                    Log.e(TAG, "onResponse: " + response.body());
                    Event event = response.body();

                    callEventType = service.getEventTypes();
                    callEventType.enqueue(new Callback<Data<EventType>>() {
                    @Override
                    public void onResponse(Call<Data<EventType>> call, Response<Data<EventType>> response) {
                        if (response.isSuccessful())
                {
                    eventTypeList = response.body().getData();
                    ArrayAdapter eventTypeAdapter =  new ArrayAdapter(EventEditActivity.this, R.layout.arraytype, eventTypeList);
                    eventTypeAdapter.setDropDownViewResource(R.layout.arraytype);
                    eventType.setAdapter(eventTypeAdapter);

                    assert event != null;
                    eventType.setSelection(event.getEventTypeId()-1);

                }

            }

            @Override
            public void onFailure(Call<Data<EventType>> call, Throwable t) {

            }
        });





                    //Event event = response.body();
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
                    /*if (event.getEventTypeId()!=null){
                        type = event.getEventTypeId();//.toString();
                        eventType.setSelection(type-1);
                    }*/
                    if (event.getLocation()!=null){
                        location = event.getLocation();//.toString();
                        eventEditLocation.setText(location);
                    }
                    if (event.getZipCode()!=null){
                        zip_code = event.getZipCode();//.toString();
                        eventEditZipcode.setText(zip_code);
                    }
                    if (event.getHouseNumber()!=null){
                        house_number = event.getHouseNumber();//.toString();
                        eventEditHouseNum.setText(house_number);
                    }
                    if (event.getStreetName()!=null){
                        street_name = event.getStreetName();//.toString();
                        eventEditStreet.setText(street_name);
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

        //eventEditDatetime = findViewById(R.id.eventEditDatetime2);
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
        String variable = b.getString("variable");


        Intent intent = new Intent(EventEditActivity.this, EditMarkerActivity.class);
        intent.putExtra("event_id",event_id);
        intent.putExtra("name",eventEditName.getText().toString());
        intent.putExtra("location",eventEditLocation.getText().toString());
        intent.putExtra("description",eventEditDescription.getText().toString());
        intent.putExtra("datetime",eventEditDatetime.getText().toString());
        intent.putExtra("event_type_id",String.valueOf(eventType.getSelectedItemId()));
        intent.putExtra("variable",variable);
        intent.putExtra("zip_code",eventEditZipcode.getText().toString());
        intent.putExtra("street_name",eventEditStreet.getText().toString());
        intent.putExtra("house_number",eventEditHouseNum.getText().toString());
        intent.putExtra("variable",variable);

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
        String zipCode = eventEditZipcode.getText().toString();
        String streetName = eventEditStreet.getText().toString();
        String houseNumber = eventEditHouseNum.getText().toString();
        //type = eventType.getText().toString();

        eventEditName.setError(null);
        eventEditDescription.setError(null);
        eventEditDatetime.setError(null);
        eventEditLocation.setError(null);

        validator.clear();

        if (validator.validate()) {
            Bundle b = getIntent().getExtras();

            String event_id = b.getString("event_id");
            String latitude = b.getString("lat");
            String longitude = b.getString("lon");

            callMessage = service.editEvent(Integer.parseInt(event_id),name,location,description,datetime,longitude,latitude,typeId,zipCode,streetName,houseNumber);
            callMessage.enqueue(new Callback<Message>() {

                @Override
                public void onResponse(Call<Message> call, Response<Message> response) {
                    if (response.isSuccessful()){
                        Log.e(TAG, "onResponse: " + response);

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
        gotoEvent();
    }

    @OnClick(R.id.backToEventSingle)
    void gotoEvent() {
        Bundle b = getIntent().getExtras();
        String event_id = b.getString("event_id");
        String variable = b.getString("variable");
        System.out.println("variable: " + variable);

        Intent intent = new Intent(EventEditActivity.this,EventSingleActivity.class);
        intent.putExtra("event_id",event_id);
        intent.putExtra("lat",latitude);
        intent.putExtra("lon",longitude);
        intent.putExtra("variable",variable);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.deleteEvent)
    void deleteEvent() {
        ViewDialog alert = new ViewDialog();
        alert.showDialog(this);
    }

    @Override
    public void onDeleteClick() {
        Bundle b = getIntent().getExtras();
        String event_id = b.getString("event_id");

        Integer event_participant_idInteger = Integer.parseInt(event_id);
        callMessage = service.deleteEvent(event_participant_idInteger);
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
        gotoEventList();
    }


    private void handleErrors(ResponseBody response) {

        ApiError apiError = Utils.converErrors(response);
        if (apiError.getErrors() != null) {
            Log.w("no errors", "apiError.getErrors()"+apiError.getErrors());

            for (Map.Entry<String, List<String>> error : apiError.getErrors().entrySet()) {
                if (error.getKey().equals("name")) {
                    eventEditName.setError(error.getValue().get(0));
                }
                if (error.getKey().equals("location")) {
                    eventEditLocation.setError(error.getValue().get(0));
                }
                if (error.getKey().equals("description")) {
                    eventEditDescription.setError(error.getValue().get(0));
                }
            }
        } else {
            Log.e("no errors", "weird");
        }
    }

    private void setupRules() {
        validator.addValidation(this, R.id.eventName, RegexTemplate.NOT_EMPTY, R.string.err_event_name);
        validator.addValidation(this, R.id.eventLocation, RegexTemplate.NOT_EMPTY, R.string.err_event_location);
        validator.addValidation(this, R.id.eventDatetime, RegexTemplate.NOT_EMPTY, R.string.err_event_datetime);
    }

    void gotoEventList() {
        Bundle b = getIntent().getExtras();
        String event_id = b.getString("event_id");
        String variable = b.getString("variable");
        String range = b.getString("range");

        if (variable.equals(1)) {
            Intent intent = new Intent(EventEditActivity.this, EventLocalListActivity.class);
            intent.putExtra("event_id", event_id);
            intent.putExtra("name", eventEditName.getText().toString());
            intent.putExtra("location", eventEditLocation.getText().toString());
            intent.putExtra("description", eventEditDescription.getText().toString());
            intent.putExtra("datetime", eventEditDatetime.getText().toString());
            intent.putExtra("event_type_id", String.valueOf(eventType.getSelectedItemId()));
            intent.putExtra("variable", variable);
            intent.putExtra("zip_code", eventEditZipcode.getText().toString());
            intent.putExtra("street_name", eventEditStreet.getText().toString());
            intent.putExtra("house_number", eventEditHouseNum.getText().toString());
            intent.putExtra("range", range);

            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(EventEditActivity.this, MyLocationActivity.class);
            intent.putExtra("event_id", event_id);
            intent.putExtra("name", eventEditName.getText().toString());
            intent.putExtra("location", eventEditLocation.getText().toString());
            intent.putExtra("description", eventEditDescription.getText().toString());
            intent.putExtra("datetime", eventEditDatetime.getText().toString());
            intent.putExtra("event_type_id", String.valueOf(eventType.getSelectedItemId()));
            intent.putExtra("variable", variable);
            intent.putExtra("zip_code", eventEditZipcode.getText().toString());
            intent.putExtra("street_name", eventEditStreet.getText().toString());
            intent.putExtra("house_number", eventEditHouseNum.getText().toString());
            intent.putExtra("range", range);

            startActivity(intent);
            finish();
        }
        
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
