package com.example.dublinbusalarm.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.dublinbusalarm.R;
import com.example.dublinbusalarm.models.Route;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RoutesActivity extends AppCompatActivity {

    Route route;
    ListView routesListView;

    private static final String TAG = "RoutesActivity";
    private static final int REQUEST_CHECK_SETTINGS = 214;
    private static final int REQUEST_ENABLE_GPS = 516;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);

        routesListView = findViewById(R.id.routesListView);

        final SettingsClient settingsClient;
        final LocationSettingsRequest locationSettingsRequest;

        // check if user has location enabled
        // if not, show dialog to turn on location or send to settings
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY));
        builder.setAlwaysShow(true);
        locationSettingsRequest = builder.build();

        settingsClient = LocationServices.getSettingsClient(RoutesActivity.this);
        settingsClient
                .checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener(locationSettingsResponse -> {
                    //Success Perform Task Here
                })
                .addOnFailureListener(e -> {
                    int statusCode = ((ApiException) e).getStatusCode();
                    switch (statusCode) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException rae = (ResolvableApiException) e;
                                rae.startResolutionForResult(RoutesActivity.this, REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException sie) {
                                Log.e("GPS","Unable to execute request.");
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            Log.e("GPS","Location settings are inadequate, and cannot be fixed here. Fix in Settings.");
                    }
                })
                .addOnCanceledListener(() -> Log.e("GPS","checkLocationSettings -> onCanceled"));

        // get the intent with the bus route information
        Intent intent = getIntent();
        route = (Route) intent.getSerializableExtra("route");

        // HashMap to use on the click listener:
        // key: (String) origin to destination
        // value: (Route.Trip) trip
        Map<String, Route.Trip> availableTrips = new HashMap<>();

        // array list necessary for displaying the list view
        // contains the trip's "origin to destination" string
        ArrayList<String> originToDestination = new ArrayList<>();

        // as I'm not allowed to have duplicate keys (not allowed in hashmap)
        // "i" will be appended to the key string to not have duplicate keys ("i" will increase per trip)
        int i = 1;

        // populate both availableTrips and originToDestination
        // elements in originToDestination are the same as the keys for availableTrips
        for (Route.Trip trip : route.getTrips()) {
            String key = trip.getOrigin() + " to " + trip.getDestination();
            key = availableTrips.containsKey(key) ? key + " " + i++ : key;
            availableTrips.put(key, trip);
            originToDestination.add(key);
        }

        // set listView Header using TextView
        TextView textView = new TextView(RoutesActivity.this);
        textView.setClickable(true);
        textView.setText(R.string.header_text);
        textView.setAllCaps(true);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setTextSize(24);
        textView.setBackgroundColor(ContextCompat.getColor(RoutesActivity.this, R.color.colorSecondary));
        textView.setTextColor(ContextCompat.getColor(RoutesActivity.this, R.color.colorYellowAccent));
        routesListView.addHeaderView(textView);

        // display the list view with the trips
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>
                (this, android.R.layout.simple_list_item_1, originToDestination);
        routesListView.setAdapter(arrayAdapter);

        // we set the listener for the list view
        // clicking an item (route) will start the maps activity with the route information
        routesListView.setOnItemClickListener((parent, view, position, id) -> {
            // start Google Maps Activity
            Intent mapIntent = new Intent(getApplicationContext(), MapsActivity.class);
            mapIntent.putExtra("trip", availableTrips.get(parent.getItemAtPosition(position).toString()));
            startActivity(mapIntent);
        });
    }

    // handles the action of opening the Settings view to turn on Gps
    private void openGpsEnableSetting() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, REQUEST_ENABLE_GPS);
    }

    // handles the result of opening Settings
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CHECK_SETTINGS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    //Success Perform Task Here
                    break;
                case Activity.RESULT_CANCELED:
                    Log.e("GPS","User denied to access location");
                    openGpsEnableSetting();
                    break;
            }
        } else if (requestCode == REQUEST_ENABLE_GPS) {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (!isGpsEnabled) {
                openGpsEnableSetting();
            }
        }
    }
}
