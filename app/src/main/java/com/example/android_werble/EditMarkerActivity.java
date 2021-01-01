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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
import android.location.Geocoder;
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
public class EditMarkerActivity extends AppCompatActivity
        implements
        OnMyLocationButtonClickListener,
        OnMyLocationClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,
        NavigationView.OnNavigationItemSelectedListener,
        GoogleMap.OnMarkerClickListener
{

    private static final String TAG = "EditMarkerActivity";

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


    //private Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        if (tokenManager.getToken() == null) {
            startActivity(new Intent(EditMarkerActivity.this, LoginActivity.class));
            finish();
        }
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class,tokenManager);


        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        System.out.println("OUTSIDE FLOATING BUTTON");

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        /*MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title(latLng.latitude + " : " + latLng.longitude);
                        googleMap.addMarker(markerOptions);

                        //ask if certain??
                        Log.w(TAG,"goting to create event");*/
                        System.out.println("INSIDE FLOATING BUTTON");

                        Intent intent = new Intent(EditMarkerActivity.this, EventEditActivity.class);
                        intent.putExtra("lat",Double.toString(latLng.latitude));
                        intent.putExtra("lon",Double.toString(latLng.longitude));

                        startActivity(intent);
                        finish();

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

        System.out.println("OUTSIDE FLOATING BUTTON");

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        /*MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title(latLng.latitude + " : " + latLng.longitude);
                        googleMap.addMarker(markerOptions);

                        //ask if certain??
                        Log.w(TAG,"goting to create event");*/
                        System.out.println("INSIDE FLOATING BUTTON");

                        Intent intent = new Intent(EditMarkerActivity.this, EventEditActivity.class);
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

        Integer clickCount = (Integer) marker.getTag();


        if (clickCount !=null) {
            clickCount++;

            Intent intent = new Intent(EditMarkerActivity.this, EventSingleActivity.class);
            intent.putExtra("event_id", String.valueOf(marker.getTag()));

            startActivity(intent);
            finish();
            Log.w(TAG,"SINGLE EVENT ACTIVITY");

        }
        return false;
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

        call = service.getLocalEvents();
        call.enqueue(new Callback<Data<Event>>() {

            @Override
            public void onResponse(Call<Data<Event>> call, Response<Data<Event>> response) {
                Log.w(TAG, "onResponseLOCALEVENTS: " + response);
                if (response.isSuccessful()) {
                    eventList = response.body().getData();
                    Log.w(TAG, "ADDING MARKERS1");

                    Bundle b = getIntent().getExtras();
                    String event_id = b.getString("event_id");
                    Integer event_idInt = Integer.parseInt(event_id);

                    for (Event event : eventList) {
                        if (event.getEventId() == event_idInt) {
                            if (event.getLatitude() != null && event.getLongitude() != null) {
                                Double lat = event.getLatitude();

                                Double lon = event.getLongitude();

                                LatLng position = new LatLng(lat, lon); //event position
                                MarkerOptions markerOptions = new MarkerOptions();//creating marker
                                markerOptions.position(position);//add position to marker
                                markerOptions.title(event.getName());//add title to marker
                                markerOptions.draggable(true);
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                                //markerOptions.
                                map.addMarker(markerOptions).setTag(event.getEventId());
                            }
                        }
                    }

                    map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                        @Override
                        public void onMarkerDragStart(Marker arg0) {
                            Log.d("System out", "onMarkerDragStart..."+arg0.getPosition().latitude+"..."+arg0.getPosition().longitude);
                        }

                        @SuppressWarnings("unchecked")
                        @Override
                        public void onMarkerDragEnd(Marker arg0) {
                            // TODO Auto-generated method stub
                            Log.d("System out", "onMarkerDragEnd..."+arg0.getPosition().latitude+"..."+arg0.getPosition().longitude);

                            map.animateCamera(CameraUpdateFactory.newLatLng(arg0.getPosition()));

                            System.out.println("event_idInt"+event_idInt);
                            System.out.println("arg0.getPosition().longitude"+arg0.getPosition().longitude);
                            System.out.println("arg0.getPosition().latitude"+arg0.getPosition().latitude);

                            /*callAccessToken = service.editEventLongLat( event_idInt,
                                                                        String.valueOf(arg0.getPosition().longitude),
                                                                        String.valueOf(arg0.getPosition().latitude));*/
                            Bundle b = getIntent().getExtras();

                            //String event_id = b.getString("event_id");
                            String name = b.getString("name");
                            String location = b.getString("location");
                            String description = b.getString("description");
                            String datetime = b.getString("datetime");
                            //String lat = b.getString("lat");
                            //String lon = b.getString("lon");
                            String event_type_id = b.getString("event_type_id");

                            System.out.println("event_idInt"+event_idInt);
                            System.out.println("name"+name);
                            System.out.println("location"+location);
                            System.out.println("description"+description);
                            System.out.println("event_idInt"+event_idInt);
                            System.out.println("event_idInt"+event_idInt);


                            callAccessToken = service.editEvent(
                                    event_idInt,
                                    name,
                                    location,
                                    description,
                                    datetime,
                                    String.valueOf(arg0.getPosition().latitude),
                                    String.valueOf(arg0.getPosition().longitude),
                                    Integer.parseInt(event_type_id));

                            callAccessToken.enqueue(new Callback<Message>() {
                                @Override
                                public void onResponse(Call<Message> call, Response<Message> response) {
                                    if (response.isSuccessful()){
                                        Log.e(TAG, "onResponse!!!: " + response.body());
                                    }
                                    else{
                                        Log.e(TAG, "notSuccessful: " + response.body());
                                    }
                                }

                                @Override
                                public void onFailure(Call<Message> call, Throwable t) {
                                    Log.e(TAG, "onFailure: " + t.getMessage());
                                }
                            });

                            System.out.println("BEFORE");
                            //floating button to approve
                            FloatingActionButton fab = findViewById(R.id.fab);
                            fab.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    System.out.println("INSIDE");

                                    //map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                     //   @Override
                                     //   public void onMapClick(LatLng latLng) {

                                            Intent intent = new Intent(EditMarkerActivity.this, EventEditActivity.class);
                                            intent.putExtra("event_id",String.valueOf(event_idInt));
                                            intent.putExtra("lat",String.valueOf(arg0.getPosition().latitude));
                                            intent.putExtra("lon",String.valueOf(arg0.getPosition().longitude));

                                            intent.putExtra("name", name);
                                            intent.putExtra("location",location);
                                            intent.putExtra("description",description);
                                            intent.putExtra("datetime",datetime);
                                            intent.putExtra("event_type_id",event_type_id);

                                            startActivity(intent);
                                            finish();

                                        //}
                                    //});

                                }
                            });
                        }

                        @Override
                        public void onMarkerDrag(Marker arg0) {
                            // TODO Auto-generated method stub
                            Log.i("System out", "onMarkerDrag...");
                            LatLng latLng = arg0.getPosition();

                        }
                    });

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
        startActivity(new Intent(EditMarkerActivity.this, EventActivity.class));
        finish();
    }

    void gotoProfile() {
        Toast.makeText(EditMarkerActivity.this,"TUTAJ",Toast.LENGTH_LONG).show();
        startActivity(new Intent(EditMarkerActivity.this, UserActivity.class));
        finish();
        Log.w(TAG,"USERACTIVITY");
    }

    void gotoSettings() {
        Toast.makeText(EditMarkerActivity.this,"SETTINGS",Toast.LENGTH_LONG).show();
        startActivity(new Intent(EditMarkerActivity.this, SettingsActivity.class));
        finish();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Log.w(TAG,"SIDEBAR");
        Toast.makeText(EditMarkerActivity.this,"TOST",Toast.LENGTH_LONG).show();
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
