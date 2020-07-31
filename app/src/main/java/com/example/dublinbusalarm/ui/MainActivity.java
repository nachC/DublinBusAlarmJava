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
import com.example.dublinbusalarm.api.DublinBusApi;
import com.example.dublinbusalarm.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    EditText inputLineEditText;
    TextView permissionText;
    Button searchBtn, permissionBtn;
    ProgressBar progressBar;
    DublinBusApi dublinBusApi;

    private static final String BASE_URL = "https://data.smartdublin.ie/"; //base url for making API calls
    private static final String OPERATOR = "bac"; //this specifies that we're requesting info about the "bus" service (and not "rail" service, for example)
    private static final String CHANNEL_ID = "alarm_channel"; //identifier for the channel that handles the alarms
    private static final int FINE_LOCATION = 1; //helper flag for FINE LOCATION request code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissionText = findViewById(R.id.permissionText);
        inputLineEditText = findViewById(R.id.inputLineEditText);
        searchBtn = findViewById(R.id.searchBtn);
        permissionBtn = findViewById(R.id.permissionBtn);

        // create retrofit instance to handle the api calls using the BASE_URL and Gson Converter
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        dublinBusApi = retrofit.create(DublinBusApi.class);

        createNotificationChannel();
        enableApp();
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
                Toast.makeText(this, "Permission was not granted", Toast.LENGTH_SHORT).show();
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
    public void searchLine(View view) {
        // check if fine-location access is already available
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // if it's available -> continue with normal app flow
            String userInput = inputLineEditText.getText().toString();
            if(userInput.isEmpty()) {
                inputLineEditText.setError("Enter a bus line");
                //Toast.makeText(MainActivity.this, "Enter a bus line", Toast.LENGTH_SHORT).show();
            } else {
                // validate input
                if (userInput.matches("[a-zA-Z0-9 *]+$")) {
                    // Hide the keyboard after clicking search
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(inputLineEditText.getWindowToken(), 0);

                    // Show progress bar
                    progressBar = findViewById(R.id.progressBar);
                    progressBar.setVisibility(View.VISIBLE);
                    // Make API call
                    Call<BusRoute> call = dublinBusApi.getBusRouteList(userInput, OPERATOR);
                    call.enqueue(new Callback<BusRoute>() {
                        @Override
                        public void onResponse(Call<BusRoute> call, Response<BusRoute> response) {
                            progressBar.setVisibility(View.INVISIBLE);
                            assert response.body() != null;
                            if (!response.body().getErrorCode().equals("0")) {
                                Log.e("Error", "No results found");
                            } else {
                                // on a successful response set the necessary fields in a new instance of BusRoute
                                BusRoute busRoute = new BusRoute();
                                busRoute.setErrorCode(response.body().getErrorCode());
                                busRoute.setRouteName(response.body().getRouteName());
                                busRoute.setRoutes(response.body().getRoutes());

                                // create intent for RoutesActivity and add the object created above to access from RoutesActivity
                                Intent intent = new Intent(getApplicationContext(), RoutesActivity.class);
                                intent.putExtra("busRoute", busRoute);
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onFailure(Call<BusRoute> call, Throwable t) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Log.e("MainActivity", "onFailure something went wrong: " + t.getMessage());
                            Toast.makeText(MainActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, "Use only letters and numbers", Toast.LENGTH_SHORT).show();
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

