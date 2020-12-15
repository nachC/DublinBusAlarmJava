package com.example.dublinbusalarm.ui;

import androidx.annotation.NonNull;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.dublinbusalarm.models.BusRoute;
import com.example.dublinbusalarm.R;
import com.example.dublinbusalarm.models.Route;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class RoutesActivity extends AppCompatActivity {
    /*

    BusRoute busRoute;
    ListView routesListView;
    List<Route> routes;

    private SettingsClient settingsClient;
    private LocationSettingsRequest locationSettingsRequest;
    private static final int REQUEST_CHECK_SETTINGS = 214;
    private static final int REQUEST_ENABLE_GPS = 516;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);

        routesListView = findViewById(R.id.routesListView);

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
        busRoute = intent.getParcelableExtra("busRoute");

        // populate the routes object with the routes for this bus route
        assert busRoute != null;
        routes = busRoute.getRoutes();

        // we'll use this predicate to filter the routes to get only the latest ones
        Predicate<Route> byDateUpdated = route -> route.getLastUpdated().matches("(.*)2020(.*)");

        // hashmap to use on the click listener:
        // key: (String) route origin -> destination
        // value: (Route) route
        Map<String, Route> updatedRoutes = new HashMap<>();

        // array list necessary for displaying the list view
        // contains the route's "origin -> destination" string
        ArrayList<String> originToDestination = new ArrayList<>();

        for (Route route : routes) {
            if (byDateUpdated.test(route)) {
                updatedRoutes.put(route.getOrigin() + " to " + route.getDestination(), route);
                originToDestination.add(route.getOrigin() + " to " + route.getDestination());
            }
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

        // display the list view with the routes
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>
                (this, android.R.layout.simple_list_item_1, originToDestination);
        routesListView.setAdapter(arrayAdapter);

        // we set the listener for the list view
        // clicking an item (route) will start the maps activity with the route information
        routesListView.setOnItemClickListener((parent, view, position, id) -> {
            // start Google Maps Activity
            Intent mapIntent = new Intent(getApplicationContext(), MapsActivity.class);
            mapIntent.putExtra("route", updatedRoutes.get(parent.getItemAtPosition(position).toString()));
            startActivity(mapIntent);
        });
    }

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

    private void openGpsEnableSetting() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, REQUEST_ENABLE_GPS);
    }

     */
}
