package com.example.android_werble;

import com.example.android_werble.entities.AccessToken;
import com.example.android_werble.entities.Data;
import com.example.android_werble.entities.Event;
import com.example.android_werble.entities.Message;
import com.example.android_werble.network.ApiService;
import com.example.android_werble.network.RetrofitBuilder;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import com.google.appengine.api.search.GeoPoint;



/**
 * This demo shows how GMS Location can be used to check for changes to the users location.  The
 * "My Location" button uses GMS Location to set the blue dot representing the users location.
 * Permission for {@link android.Manifest.permission#ACCESS_FINE_LOCATION} is requested at run
 * time. If the permission has not been granted, the Activity is finished with an error message.
 */
public class MyLocationActivity extends AppCompatActivity
        implements
        OnMyLocationButtonClickListener,
        OnMyLocationClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,
        NavigationView.OnNavigationItemSelectedListener,
        GoogleMap.OnMarkerClickListener
{

    private static final String TAG = "MyLocationActivity";

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean permissionDenied = false;

    private GoogleMap map;

    private FusedLocationProviderClient mFusedLocationClient;

    List<Event> eventList;

    Call<Data<Event>> call;
    Call<Message> callAccessToken;
    ApiService service;
    String longitude;
    String latitude;
    TokenManager tokenManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        if (tokenManager.getToken() == null) {
            startActivity(new Intent(MyLocationActivity.this, LoginActivity.class));
            finish();
        }
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class,tokenManager);


        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }



    private void getLastKnownLocation() {
        Log.d(TAG, " getLastKnownLocation: called.");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()){
                    Location location = task.getResult();
                    //GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                    LatLng latLng  = new LatLng(location.getLatitude(), location.getLongitude());
                    System.out.println("WSPÓŁRZĘDNE"+latLng);

                    longitude = String.valueOf(location.getLongitude());

                    Log.e(TAG, "location.getLongitude(): " + location.getLongitude());

                    latitude = String.valueOf(location.getLatitude());
                    Log.e(TAG, "location.getLatitude(): " + location.getLatitude());

                    callAccessToken = service.userPosition(longitude,latitude);

                    callAccessToken.enqueue(new Callback<Message>() {
                        @Override
                        public void onResponse(Call<Message> call, Response<Message> response) {
                            Log.w(TAG,"CHECK2");
                            if (response.isSuccessful()) {
                                Log.e(TAG, "onResponse: " + response.body().getMessage());

                            } else {
                                Log.e(TAG, "ELSE: " + response);
                                //handleErrors(response.errorBody());
                            }
                        }

                        @Override
                        public void onFailure(Call<Message> call, Throwable t) {
                            Log.e(TAG, "onFailure: " + t.getMessage());
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMyLocationButtonClickListener(this);
        map.setOnMyLocationClickListener(this);
        enableMyLocation();
        map.setOnMarkerClickListener((GoogleMap.OnMarkerClickListener) this);



        call = service.getLocalEvents();
        call.enqueue(new Callback<Data<Event>>() {

            @Override
            public void onResponse(Call<Data<Event>> call, Response<Data<Event>> response) {
                Log.w(TAG, "onResponseLOCALEVENTS: " + response);
                if (response.isSuccessful()){
                    eventList = response.body().getData();
                    Log.w(TAG,"ADDING MARKERS1");

                    for (Event event : eventList){
                        Double lat = event.getLatitude();
                        Double lon = event.getLongitude();

                        LatLng position = new LatLng(lat,lon); //event position
                        MarkerOptions markerOptions = new MarkerOptions();//creating marker
                        markerOptions.position(position);//add position to marker
                        markerOptions.title(event.getName());//add title to marker
                        //markerOptions.
                        googleMap.addMarker(markerOptions).setTag(event.getEventId());//display marker on map
                        Log.w(TAG,"ADDING MARKERS2");
                    }

                }

            }

            @Override
            public void onFailure(Call<Data<Event>> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title(latLng.latitude + " : " + latLng.longitude);
                        googleMap.addMarker(markerOptions);

                        //ask if certain??
                        Log.w(TAG,"goting to create event");

                        Intent intent = new Intent(MyLocationActivity.this, CreateEventActivity.class);
                        intent.putExtra("lat",Double.toString(latLng.latitude));
                        intent.putExtra("lon",Double.toString(latLng.longitude));

                        startActivity(intent);
                        finish();

                    }
                });

            }
        });
    }

    /** Called when the user clicks a marker. */
    @Override
    public boolean onMarkerClick(final Marker marker) {

        // Retrieve the data from the marker.
        Integer clickCount = (Integer) marker.getTag();

        // Check if a click count was set, then display the click count.
        /*if (clickCount != null) {
            clickCount = clickCount + 1;
            marker.setTag(clickCount);
            Toast.makeText(this,
                    marker.getTitle() +
                            " has been clicked " + clickCount + " times.",
                    Toast.LENGTH_SHORT).show();
        }*/
        if (clickCount !=null) {
            clickCount++;
            //if (clickCount == 2) {
                Toast.makeText(this,
                        marker.getTitle() +
                                " has been clicked " + clickCount + " times.",
                        Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MyLocationActivity.this, SingleEventActivity.class);
                intent.putExtra("event_id", String.valueOf(marker.getTag()));

                startActivity(intent);
                finish();
                Log.w(TAG,"SINGLE EVENT ACTIVITY");
            //}
        }
        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
        //return true;
    }


    //void getLocalEvents() {

/*
        call = service.getLocalEvents();
        call.enqueue(new Callback<Data<Event>>() {

            @Override
            public void onResponse(Call<Data<Event>> call, Response<Data<Event>> response) {
                Log.w(TAG, "onResponse: " + response);
                if (response.isSuccessful()){
                    eventList = response.body().getData();
                }

            }

            @Override
            public void onFailure(Call<Data<Event>> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });
*/
    //}

       /*(map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

            }
        })
    }

    //FLOATING BUTTON PACK IT UP
    private void addMapMarker(){
        /*Marker marker = map.addMarker(new MarkerOptions()
                .position(new LatLng())
                .title("TEST")
                .snippet("event-test")
        );*/


    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (map != null) {
                map.setMyLocationEnabled(true);
                Log.w(TAG,"setMyLocationEnabled(true)");
            }
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
            Log.w(TAG,"setMyLocationEnabled(false)");
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        getLastKnownLocation();

        call = service.getLocalEvents();
        call.enqueue(new Callback<Data<Event>>() {

            @Override
            public void onResponse(Call<Data<Event>> call, Response<Data<Event>> response) {
                Log.w(TAG, "onResponseLOCALEVENTS: " + response);
                if (response.isSuccessful()){
                    eventList = response.body().getData();
                    Log.w(TAG,"ADDING MARKERS1");

                    for (Event event : eventList){
                        Double lat = event.getLatitude();
                        Double lon = event.getLongitude();

                        LatLng position = new LatLng(lat,lon); //event position
                        MarkerOptions markerOptions = new MarkerOptions();//creating marker
                        markerOptions.position(position);//add position to marker
                        markerOptions.title(event.getName());//add title to marker
                        //markerOptions.
                        map.addMarker(markerOptions).setTag(event.getEventId());//display marker on map
                        Log.w(TAG,"ADDING MARKERS2");
                    }

                }

            }

            @Override
            public void onFailure(Call<Data<Event>> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });

        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Permission was denied. Display an error message
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true;
            Log.w(TAG,"onRequestPermissionsResult");

        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            permissionDenied = false;
        }
        Log.w(TAG,"onResumeFragments");

    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        Log.w(TAG,"showMissingPermissionError");

        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    void gotoEvent(){
        //Toast.makeText(MapLocat.this,"TUTAJ",Toast.LENGTH_LONG).show();
        startActivity(new Intent(MyLocationActivity.this, EventActivity.class));
        finish();
    }

    void gotoProfile() {
        Toast.makeText(MyLocationActivity.this,"TUTAJ",Toast.LENGTH_LONG).show();
        startActivity(new Intent(MyLocationActivity.this, UserActivity.class));
        finish();
        Log.w(TAG,"USERACTIVITY");
    }

    void gotoSettings() {
        Toast.makeText(MyLocationActivity.this,"SETTINGS",Toast.LENGTH_LONG).show();
        startActivity(new Intent(MyLocationActivity.this, SettingsActivity.class));
        finish();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Log.w(TAG,"SIDEBAR");
        Toast.makeText(MyLocationActivity.this,"TOST",Toast.LENGTH_LONG).show();
        switch (item.getTitle().toString()) {
            //case "Logout": logout(); break;
            case "Your profile": gotoProfile(); break;
            case "Your events": gotoEvent(); break;
            //case "Map": gotoMap(); break;
            //case "Create event": gotoCreateEvent(); break;
            case "Settings": gotoSettings(); break;

        }
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


}
