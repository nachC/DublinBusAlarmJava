package com.example.dublinbusalarm.ui;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.example.dublinbusalarm.receivers.AlarmReceiver;
import com.example.dublinbusalarm.services.LocationService;
import com.example.dublinbusalarm.R;
import com.example.dublinbusalarm.models.Route;
import com.example.dublinbusalarm.models.Stop;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

import static java.lang.String.valueOf;

/* TODO:
 *   - check on logic for when stop is reached (onLocationChanged())
 **/

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener {

    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location stopLocation;
    private Marker currentMarker;

    private Route route; // object will contain bus route info
    private List<Stop> stops; // object will contain stops info for the selected Route

    private boolean stopReached = false; // flag for when the selected stop is reached
    private boolean stopSelected = false; // flag for when a stop is selected

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        // get the intent info from the activity we come from (RoutesActivity)
        Intent intent = getIntent();
        route = intent.getParcelableExtra("route");
        assert route != null;
        stops = route.getStops();
        currentMarker = null;

        // this is here for debugging
        //startAlarm();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // location object for the stop selected
        // This instance will hold the Lat and Lang for the selected stop through the onMarkerClick method
        stopLocation = new Location("stopLocation");

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // check if the user has selected a stop
                if(isStopSelected()) {
                    // get and store the distance from the user to the stop selected
                    float distanceToStop = location.distanceTo(stopLocation);
                    //Log.d(TAG + " distanceToStop", valueOf(distanceToStop));
                    if(distanceToStop < 50f && !isStopReached()) {
                        // we are 50 meters or less from the stop and we haven't reached the stop
                        Log.d(TAG, "reached stop -> firing alarm");
                        setStopReached(true);
                        setStopSelected(false);
                        startAlarm();
                    }
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        } else {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }

        // adding markers for each stop and drawing the lines between all the stops
        LatLng firstMarker = new LatLng(stops.get(0).getLat(), stops.get(0).getLng());
        for(int i=0 ; i<stops.size() ; i++) {
            mMap.addMarker(new MarkerOptions().position(new LatLng(stops.get(i).getLat(), stops.get(i).getLng())));
            if (i != stops.size()-1) {
                googleMap.addPolyline(new PolylineOptions()
                    .add(new LatLng(stops.get(i).getLat(), stops.get(i).getLng()))
                    .add(new LatLng(stops.get(i+1).getLat(), stops.get(i+1).getLng())));
            }
        }
        // set the camera position at launch to the first stop
        CameraPosition cameraPosition = new CameraPosition.Builder().target(firstMarker).zoom(12).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mMap.setOnMarkerClickListener(this);
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        //Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        //Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (currentMarker != null) {
            // set the current selected marker to the default marker icon (not selected anymore)
            currentMarker.setIcon(BitmapDescriptorFactory.defaultMarker());
            if (!marker.equals(currentMarker)) {
                setStopReached(false);
            }
        }
        // save selected marker as current to work with it
        currentMarker = marker;
        // set the coordinates of the Location object using the marker's location
        stopLocation.setLatitude(currentMarker.getPosition().latitude);
        stopLocation.setLongitude(currentMarker.getPosition().longitude);

        currentMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

        setStopSelected(true);
        startLocationService();
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }

    // method to start the alarm once the selected stop is reached
    public void startAlarm() {
        Log.d("MapsActivity", "startAlarm executed");
        Intent alarmIntent = new Intent(MapsActivity.this, AlarmReceiver.class);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, alarmIntent, 0);
        AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(System.currentTimeMillis()+500, pendingIntent);
        alarmManager.setAlarmClock(alarmClockInfo, pendingIntent);
    }

    // method to start the Location Foreground Service when a stop (marker) is selected in onMarkerClick()
    public void startLocationService() {
        Log.d(TAG, "startLocationService called.");
        Intent serviceIntent = new Intent(this, LocationService.class);
        MapsActivity.this.startForegroundService(serviceIntent);
    }

    public boolean isStopSelected() {
        return stopSelected;
    }

    public void setStopSelected(boolean stopSelected) {
        this.stopSelected = stopSelected;
    }

    public boolean isStopReached() {
        return stopReached;
    }

    public void setStopReached(boolean stopReached) {
        this.stopReached = stopReached;
    }
}