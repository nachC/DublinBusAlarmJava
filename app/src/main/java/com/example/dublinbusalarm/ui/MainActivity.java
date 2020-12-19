package com.example.dublinbusalarm.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dublinbusalarm.models.BusRoute;
import com.example.dublinbusalarm.R;
import com.example.dublinbusalarm.models.Route;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static java.lang.Double.parseDouble;
import static java.lang.Float.parseFloat;

public class MainActivity extends AppCompatActivity {

    EditText inputLineEditText;
    TextView permissionText;
    Button searchBtn, permissionBtn;
    ProgressBar progressBar;

    private static final String TAG = "MainActivity"; // tag for logging
    private static final String CHANNEL_ID = "alarm_channel"; // identifier for the channel that handles the alarms
    private static final int FINE_LOCATION = 1; // helper flag for FINE LOCATION request code

    private DatabaseReference databaseRef;

    /** Variables to hold the route data coming from Firebase DB */
    Route route;
    Route.Trip trip;
    ArrayList<ArrayList<String>> shape;
    ArrayList<Route.Trip.Stop> stops;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissionText = findViewById(R.id.permissionText);
        inputLineEditText = findViewById(R.id.inputLineEditText);
        searchBtn = findViewById(R.id.searchBtn);
        permissionBtn = findViewById(R.id.permissionBtn);

        databaseRef = FirebaseDatabase.getInstance().getReference();

        createNotificationChannel();
        enableApp();
    }

    public void fetchData(String routeID) {
        Log.d(TAG, "fetching data...");
        route = new Route();
        databaseRef.child(routeID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.INVISIBLE);
                // if routeid is non-existent, return
                if (dataSnapshot.getValue() == null) {
                    Log.d(TAG, "No results found");
                    // alert user message (advise to double check bus line entered)
                    Toast.makeText(MainActivity.this, "No results found. Please check bus line.", Toast.LENGTH_LONG).show();
                    return;
                }
                new Thread ( new Runnable() {
                    @Override
                    public void run() {
                        // This code will run in another thread. Usually as soon as start() gets called!
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        for (DataSnapshot tripData : dataSnapshot.getChildren()) {

                            trip = new Route.Trip();
                            shape = new ArrayList<>();
                            stops = new ArrayList<>();

                            for ( DataSnapshot shapeData : tripData.child("shape").getChildren()) {
                                // shapeData is [lat, lng] for shape point
                                shape.add((ArrayList<String>) shapeData.getValue());
                            }
                            //Double d = parseDouble(shape.get(0).get(0)) + parseDouble(shape.get(0).get(1));
                            for ( DataSnapshot stopData: tripData.child("stopSequence").getChildren()) {
                                stops.add(stopData.getValue(Route.Trip.Stop.class));
                            }
                            /** set all trip instance properties */
                            trip.setDestination(tripData.child("destination").getValue().toString());
                            trip.setOrigin(tripData.child("origin").getValue().toString());
                            trip.setShapePoints(shape);
                            trip.setStops(stops);
                            route.setTrips(trip);
                        }
                        //Log.d(TAG, route.getTrips().size() + "");
                        // create intent for RoutesActivity and add the object created above to access from RoutesActivity
                        Intent intent = new Intent(getApplicationContext(), RoutesActivity.class);
                        intent.putExtra("route", route);
                        Log.d(TAG, route.getTrips().size() + "");
                        startActivity(intent);
                    }
                }).start();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    // this method enables the main functionality of the app
    // here we check for location permission, without it, the app can't work
    public void enableApp() {
        // check if fine-location access is already available
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // if it's available -> enable the app's functionality
            searchBtn.setEnabled(true);
            permissionText.setVisibility(View.INVISIBLE);
            permissionBtn.setVisibility(View.INVISIBLE);
        } else {
            // fine-location access not granted
            searchBtn.setEnabled(false);
            permissionText.setVisibility(View.VISIBLE);
            // request permission for access
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == FINE_LOCATION) {
            // received permission for fine-location
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                searchBtn.setEnabled(true);
                permissionText.setVisibility(View.INVISIBLE);
                permissionBtn.setVisibility(View.INVISIBLE);
                Toast.makeText(this, "Permission granted. You can search now", Toast.LENGTH_LONG).show();
            } else {
                //Toast.makeText(this, "Permission was not granted", Toast.LENGTH_SHORT).show();
                permissionBtn.setVisibility(View.VISIBLE);
                if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    permissionBtn.setText(R.string.settings_button);
                } else {
                    permissionBtn.setText(R.string.allow_button);
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.alarm_channel_name);
            String description = getString(R.string.alarm_channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel alarmChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            alarmChannel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(alarmChannel);
        }
    }

    // method handling the event of finding the bus line and firing the intent to show the routes in the RoutesActivity
    public void search(View view) {
        // check if fine-location access is already available
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // if it's available -> continue with normal app flow
            String userInput = inputLineEditText.getText().toString();
            if(userInput.isEmpty()) {
                inputLineEditText.setError("Enter a bus line");
            } else {
                // validate input (only allow letters and numbers)
                if (userInput.matches("[a-zA-Z0-9 *]+$")) {
                    // Hide the keyboard after clicking search
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(inputLineEditText.getWindowToken(), 0);
                    // Show progress bar
                    progressBar = findViewById(R.id.progressBar);
                    progressBar.setVisibility(View.VISIBLE);
                    // fetch route data from db
                    fetchData(userInput);
                } else {
                    Toast.makeText(MainActivity.this, "Please, use only letters and numbers", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            // fine-location access not granted
            permissionText.setVisibility(View.VISIBLE);
            // request permission for access
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION);
        }

    }

    // method called when clicking the permissionBtn
    public void openPermissionSettings(View view) {
        // check if the button is for routing the user to the "settings" menu
        if (permissionBtn.getText().equals("settings")) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            // here we route the user to the Settings menu to set the permissions manually
            // we'll handle the result with the onActivityResult method
            startActivityForResult(intent, 0);
        } else {
            enableApp();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if we come from the SETTINGS screen and user provided location permission -> enableApp
        if (requestCode == 0) {
            enableApp();
        }
    }
}

