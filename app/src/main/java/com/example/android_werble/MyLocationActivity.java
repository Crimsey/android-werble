package com.example.android_werble;

import com.example.android_werble.entities.Data;
import com.example.android_werble.entities.Event;
import com.example.android_werble.entities.Message;
import com.example.android_werble.network.ApiService;
import com.example.android_werble.network.RetrofitBuilder;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import android.os.Handler;


import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * This demo shows how GMS Location can be used to check for changes to the users location.  The
 * "My Location" button uses GMS Location to set the blue dot representing the users location.
 * Permission for {@link android.Manifest.permission#ACCESS_FINE_LOCATION} is requested at run
 * time. If the permission has not been granted, the Activity is finished with an error message.
 */
public class MyLocationActivity extends NavigationActivity
        implements
        OnMyLocationButtonClickListener,
        OnMyLocationClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,
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
    //Call<Message> callAccessToken;
    //ApiService service;
    String longitude;
    String latitude;
    TokenManager tokenManager;
    @BindView(R.id.seekBar)
    SeekBar seekBar;
    @BindView(R.id.rangeText)
    TextView rangeText;
    Integer range;

    Circle mapCircle=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ButterKnife.bind(this);

        seekBar.refreshDrawableState();
        rangeText.setText(seekBar.getProgress()+"km");

        range = seekBar.getProgress();
        MyApplication.setGlobalRangeVariable(range);


        System.out.println("seekBar.getProgress()"+seekBar.getProgress());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            //Circle mapCircle=null;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                range = seekBar.getProgress();
                rangeText.setText(seekBar.getProgress()+"km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                range = seekBar.getProgress();
                rangeText.setText(seekBar.getProgress()+"km");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                range = seekBar.getProgress();
                rangeText.setText(seekBar.getProgress()+"km");

                MyApplication.setGlobalRangeVariable(range);


                call = service.getLocalEvents(range);
                call.enqueue(new Callback<Data<Event>>() {

                    @Override
                    public void onResponse(Call<Data<Event>> call, Response<Data<Event>> response) {
                        Log.w(TAG, "onResponseLOCALEVENTS: " + response);
                        if (response.isSuccessful()){
                            eventList = response.body().getData();
                            Log.w(TAG,"ADDING MARKERS1");
                            map.clear();

                            if (mapCircle!=null) {
                                mapCircle.remove();
                            }
                            mapCircle = map.addCircle(new CircleOptions()
                                    .center(new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude)))
                                    .radius(range*1000)
                                    .strokeWidth(0f)
                                    .fillColor(0x500084d3));

                            for (Event event : eventList){
                                if (event.getLatitude() != null && event.getLongitude() != null ) {
                                    Double lat = event.getLatitude();
                                    Double lon = event.getLongitude();

                                    LatLng position = new LatLng(lat, lon); //event position
                                    MarkerOptions markerOptions = new MarkerOptions();//creating marker
                                    markerOptions.position(position);//add position to marker
                                    markerOptions.title("Name: "+event.getName()+" Distance:"+event.getDistance().toString()+"km");//add title to marker
                                    //markerOptions.


                                    map.addMarker(markerOptions).setTag(event.getEventId());//display marker on map

                                    Log.w(TAG, "ADDING MARKERS2");
                                }
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<Data<Event>> call, Throwable t) {
                        Log.w(TAG, "onFailure: " + t.getMessage());
                    }
                });
            }
        });
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
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    //GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    System.out.println("WSPÓŁRZĘDNE" + latLng);

                    longitude = String.valueOf(location.getLongitude());

                    Log.e(TAG, "location.getLongitude(): " + location.getLongitude());

                    latitude = String.valueOf(location.getLatitude());
                    Log.e(TAG, "location.getLatitude(): " + location.getLatitude());

                    Integer range = seekBar.getProgress();
                    rangeText.setText(range.toString() + "km");

                    messageCall = service.userPosition(longitude, latitude);
                    messageCall.enqueue(new Callback<Message>() {
                        @Override
                        public void onResponse(Call<Message> call, Response<Message> response) {
                            Log.w(TAG, "CHECK2");
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

                    //Location location = task.getResult();
                    //LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,12));

                    if (mapCircle!=null) {
                        mapCircle.remove();
                    }
                    mapCircle = map.addCircle(new CircleOptions()
                            .center(new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude)))
                            .radius(range*1000)
                            .strokeWidth(0f)
                            .fillColor(0x500084d3));
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

        getLastKnownLocation();

        Integer range = seekBar.getProgress();
        rangeText.setText(range.toString()+"km");

        messageCall = service.userPosition(longitude,latitude);


        call = service.getLocalEvents(range);
        call.enqueue(new Callback<Data<Event>>() {

            @Override
            public void onResponse(Call<Data<Event>> call, Response<Data<Event>> response) {
                Log.w(TAG, "onResponseLOCALEVENTS: " + response);
                if (response.isSuccessful()){
                    eventList = response.body().getData();
                    Log.w(TAG,"ADDING MARKERS1");

                    for (Event event : eventList){
                        if (event.getLatitude() != null && event.getLongitude() != null ) {
                            Double lat = event.getLatitude();

                            Double lon = event.getLongitude();

                            LatLng position = new LatLng(lat, lon); //event position
                            MarkerOptions markerOptions = new MarkerOptions();//creating marker
                            markerOptions.position(position);//add position to marker
                            markerOptions.title("Name: "+event.getName()+" Distance:"+event.getDistance().toString());//add title to marker
                            //markerOptions.
                            googleMap.addMarker(markerOptions).setTag(event.getEventId());//display marker on map
                            Log.w(TAG, "ADDING MARKERS2");
                        }
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

                map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        //markerOptions.title(latLng.latitude + " : " + latLng.longitude);
                        googleMap.addMarker(markerOptions);

                        //ask if certain??
                        Log.w(TAG,"goting to create event");

                        Intent intent = new Intent(MyLocationActivity.this, EventCreateActivity.class);
                        intent.putExtra("lat",Double.toString(latLng.latitude));
                        intent.putExtra("lon",Double.toString(latLng.longitude));
                        intent.putExtra("range",String.valueOf(seekBar.getProgress()));
                        intent.putExtra("variable","2");

                        startActivity(intent);
                        finish();

                    }
                });

            }
        });


    }


    /** Called when the user clicks a marker. */
    @SuppressLint("PotentialBehaviorOverride")
    @Override
    public boolean onMarkerClick(final Marker marker) {

        // Retrieve the data from the marker.
        Integer clickCount = (Integer) marker.getTag();

        marker.showInfoWindow();

        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker arg0) {
                System.out.println("setOnInfoWindowClickListener onInfoWindowClick");
                int variable = 2;
                Intent intent = new Intent(MyLocationActivity.this, EventSingleActivity.class);
                intent.putExtra("event_id", String.valueOf(marker.getTag()));
                intent.putExtra("variable", String.valueOf(variable));

                startActivity(intent);
                finish();
                Log.w(TAG, "SINGLE EVENT ACTIVITY");
            }
        });

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
        //return true;
    }

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

        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:" + location, Toast.LENGTH_LONG).show();
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

}
