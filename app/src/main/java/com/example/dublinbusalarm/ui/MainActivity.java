package com.example.dublinbusalarm.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
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

import com.example.dublinbusalarm.R;
import com.example.dublinbusalarm.firebase.FirebaseCall;
import com.example.dublinbusalarm.models.Route;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // UI elements
    EditText inputLineEditText;
    TextView permissionText;
    Button searchBtn, permissionBtn;
    ProgressBar progressBar;

    // dialog used to show what data is saved to the DB by the app
    private AlertDialog alertDialog;

    private static final String TAG = "MainActivity"; // tag for logging
    private static final String CHANNEL_ID = "alarm_channel"; // identifier for the channel that handles the alarms
    private static final int FINE_LOCATION = 1; // helper flag for FINE LOCATION request code

    // Reference to Firebase Database. Used to get and write data.
    private DatabaseReference databaseRef;
    // Helper class to make calls to save and write data to Firebase DB
    private FirebaseCall firebaseCall;

    // Variables to hold the route data coming from Firebase DB
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

        // get a reference to the database
        databaseRef = FirebaseDatabase.getInstance().getReference();
        // instantiation of FirebaseCall class to make calls
        firebaseCall = new FirebaseCall();

        createNotificationChannel();
        enableApp();
    }

    // method to fetch necessary route data from DB given a route id (user input)
    public void fetchData(String routeid) {
        databaseRef.child("routes").child(routeid).addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.INVISIBLE);
                // get route data from db
                route = firebaseCall.getRouteData(dataSnapshot);
                // if route is null ->_ routeid is non-existent, return
                if (route == null) {
                    Toast.makeText(MainActivity.this, "No results found. Please check bus line.", Toast.LENGTH_LONG).show();
                    return;
                }
                // create intent for RoutesActivity and add the route instance to access from RoutesActivity
                Intent intent = new Intent(getApplicationContext(), RoutesActivity.class);
                intent.putExtra("route", route);
                startActivity(intent);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.d(TAG, "Failed to read value.", error.toException());
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
                    fetchData(userInput.toLowerCase());
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

    // method to show a AlertDialog with information
    // regarding what data is saved to the DB
    public void showDataSavedDialog(View view) {
        alertDialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Data we save")
                .setMessage("When the alarm triggers, we save some data to our database. \n" +
                        "We save exactly four things: \n" +
                        "- The coordinates of the stop you selected. \n" +
                        "- Your coordinates at the moment you select a stop \n" +
                        "- The time it took for the bus to reach the selected stop \n" +
                        "- The date the trip was made")
                // A null listener allows the button to dismiss the dialog and take no further action.
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if we come from the SETTINGS screen and user provided location permission
        // then we enable the app
        if (requestCode == 0) {
            enableApp();
        }
    }
}

