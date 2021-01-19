package com.example.android_werble;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.example.android_werble.entities.ApiError;
import com.example.android_werble.entities.Data;
import com.example.android_werble.entities.EventType;
import com.example.android_werble.entities.Message;
import com.example.android_werble.network.ApiService;
import com.example.android_werble.network.RetrofitBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
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


public class EventCreateActivity extends AppCompatActivity {

    private static final String TAG = "CreateEventActivity";

    @BindView(R.id.eventName)
    TextInputLayout eventName;
    @BindView(R.id.eventLocation)
    TextInputLayout eventLocation;
    @BindView(R.id.eventDescription)
    TextInputLayout eventDescription;
    @BindView(R.id.eventStartDatetime2)
    TextInputEditText eventStartDatetime;
    @BindView(R.id.eventEndDatetime2)
    TextInputEditText eventEndDatetime;
    @BindView(R.id.eventZipcode)
    TextInputLayout eventZipcode;
    @BindView(R.id.eventHouseNum)
    TextInputLayout eventHouseNum;
    @BindView(R.id.eventStreet)
    TextInputLayout eventStreet;
    @BindView(R.id.eventType)
    Spinner eventType;


    Button calclockbutton;


    ApiService service;
    Call<Message> call;
    Call<Data<EventType>> callEventType;

    AwesomeValidation validator;
    TokenManager tokenManager;
    List<EventType> eventTypeList;

    String type;
    Integer typeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventcreate);


        Log.w(TAG, "My tu w ogóle wchodzimy?");
        ButterKnife.bind(this);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));

        if (tokenManager.getToken() == null) {
            startActivity(new Intent(EventCreateActivity.this, LoginActivity.class));
            finish();
        }

        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);
        validator = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);
        setupRules();

        //eventType = findViewById(R.id.eventType);
        ArrayAdapter eventTypeAdapter = ArrayAdapter.createFromResource(EventCreateActivity.this, R.array.types, R.layout.arraytype);
        eventTypeAdapter.setDropDownViewResource(R.layout.arraytype);
        eventType.setAdapter(eventTypeAdapter);

        eventType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                typeId = ++position;
                //type = parent.getItemAtPosition(position).toString();
                Log.w(TAG, "type: " + type);
                System.out.println("TYPE: " + type);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        eventStartDatetime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimeDialog(eventStartDatetime);
            }
        });

        eventEndDatetime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimeDialog(eventEndDatetime);
            }
        });


        callEventType = service.getEventTypes();
        callEventType.enqueue(new Callback<Data<EventType>>() {
            @Override
            public void onResponse(Call<Data<EventType>> call, Response<Data<EventType>> response) {
                if (response.isSuccessful()) {
                    eventTypeList = response.body().getData();
                    ArrayAdapter eventTypeAdapter = new ArrayAdapter(EventCreateActivity.this, R.layout.arraytype, eventTypeList);
                    eventTypeAdapter.setDropDownViewResource(R.layout.arraytype);
                    eventType.setAdapter(eventTypeAdapter);
                }
            }

            @Override
            public void onFailure(Call<Data<EventType>> call, Throwable t) {

            }
        });

    }

    private void showDateTimeDialog(final TextInputEditText eventDatetime) {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:00");
                        Date currentTime = new Date();
                        if (currentTime.before(calendar.getTime())) {
                            eventDatetime.setText(simpleDateFormat.format(calendar.getTime()));
                        } else {
                            Toast.makeText(EventCreateActivity.this, "Your date must be greater than todays date",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                };
                new TimePickerDialog(EventCreateActivity.this, R.style.datepicker, timeSetListener,
                        calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
            }
        };
        new DatePickerDialog(EventCreateActivity.this, R.style.datepicker, dateSetListener,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }


    @OnClick(R.id.CreateEventButton)
    void createEventwithMarker() {
        String name = eventName.getEditText().getText().toString();
        String location = eventLocation.getEditText().getText().toString();
        String description = eventDescription.getEditText().getText().toString();
        String startDatetime = eventStartDatetime.getText().toString();
        String endDatetime = eventEndDatetime.getText().toString();
        String zipCode = eventZipcode.getEditText().getText().toString();
        String streetName = eventStreet.getEditText().getText().toString();
        String houseNumber = eventHouseNum.getEditText().getText().toString();

        eventName.setError(null);
        eventLocation.setError(null);
        eventDescription.setError(null);
        eventStartDatetime.setError(null);
        eventEndDatetime.setError(null);
        eventDescription.setError(null);
        eventZipcode.setError(null);
        eventStreet.setError(null);
        eventHouseNum.setError(null);

        validator.clear();
        if (validator.validate()) {
            Date startDate, endDate;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd h:m:00");
            try {
                startDate = sdf.parse(startDatetime);
                endDate = sdf.parse(endDatetime);
                if (startDate.before(endDate)) {
                    Bundle b = getIntent().getExtras();
                    String latitude = b.getString("lat");
                    String longitude = b.getString("lon");
                    call = service.createEventwithMarker(name, location, description, startDatetime,
                           endDatetime, longitude, latitude, typeId, zipCode, streetName, houseNumber);
                    call.enqueue(new Callback<Message>() {
                        @Override
                        public void onResponse(Call<Message> call, Response<Message> response) {
                            if (response.isSuccessful()) {
                                Log.e(TAG, "onResponse: " + response.body());
                                Toast.makeText(EventCreateActivity.this, "Created event!",
                                        Toast.LENGTH_LONG).show();
                                gotoEvent();
                            } else {
                                handleErrors(response.errorBody());
                            }
                        }
                        @Override
                        public void onFailure(Call<Message> call, Throwable t) {
                            Log.e(TAG, "onFailure: " + t.getMessage());
                        }
                    });
                } else {
                    Toast.makeText(this, "Begin datetime have to be before end datetime",
                            Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                System.out.println("Error occurred " + e.getMessage());
            }
        }
    }




    void gotoEvent() {
        Toast.makeText(EventCreateActivity.this,"CREATING",Toast.LENGTH_LONG).show();
        switch (MyApplication.getManaging()){
            case 2: startActivity(new Intent(this, MyLocationActivity.class)); break;
            case 3: startActivity(new Intent(this, EventOwnedListActivity.class)); break;
            case 4: startActivity(new Intent(this, EventParticipatingListActivity.class)); break;
            default: startActivity(new Intent(this, EventLocalListActivity.class)); break;
        }
        finish();
    }

    @OnClick(R.id.backToMap)
    void back(){
        startActivity(new Intent(EventCreateActivity.this, MyLocationActivity.class));
        finish();
    }

    private void handleErrors(ResponseBody response) {

        ApiError apiError = Utils.converErrors(response);
        if (apiError.getErrors() != null) {
            Log.w("no errors", "apiError.getErrors()"+apiError.getErrors());

            for (Map.Entry<String, List<String>> error : apiError.getErrors().entrySet()) {
                if (error.getKey().equals("name")) {
                    eventName.setError(error.getValue().get(0));
                }
                if (error.getKey().equals("location")) {
                    eventLocation.setError(error.getValue().get(0));
                }
                if (error.getKey().equals("description")) {
                    eventDescription.setError(error.getValue().get(0));
                }
            }
        } else {
            Log.e("no errors", "No errors occured");
        }
    }

    public void setupRules() {
        validator.addValidation(this, R.id.eventName, "[a-zA-Z0-9 ]{3,50}", R.string.err_event_name);
        validator.addValidation(this, R.id.eventLocation, "[a-zA-Z0-9 ]{3,100}", R.string.err_event_location);
        validator.addValidation(this, R.id.eventStartDatetime, RegexTemplate.NOT_EMPTY, R.string.err_event_datetime);
        validator.addValidation(this, R.id.eventEndDatetime, RegexTemplate.NOT_EMPTY, R.string.err_event_datetime);

        validator.addValidation(this, R.id.eventZipcode, "^[0-9]{2}-[0-9]{3}$|^\\s*$", R.string.err_event_zipcode);
        validator.addValidation(this, R.id.eventDescription, ".{1,200}|^\\s*$", R.string.err_event_description);
        validator.addValidation(this, R.id.eventHouseNum, "[a-zA-Z0-9 ]{1,10}|^\\s*$", R.string.err_event_housenumber);
        validator.addValidation(this, R.id.eventStreet, "[a-zA-Z0-9 ]{1,50}|^\\s*$", R.string.err_event_street);
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

