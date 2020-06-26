package com.example.dublinbusalarm;


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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    EditText inputLineEditText;
    TextView permissionText;
    ProgressBar progressBar;
    DublinBusApi dublinBusApi;
    Button searchBtn, permissionBtn;

    private static final String BASE_URL = "https://data.smartdublin.ie/";
    private static final String OPERATOR = "bac";
    private static final String CHANNEL_ID = "notify_channel";
    private static final int FINE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissionText = findViewById(R.id.permissionText);
        inputLineEditText = findViewById(R.id.inputLineEditText);
        searchBtn = findViewById(R.id.searchBtn);
        permissionBtn = findViewById(R.id.permissionBtn);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        dublinBusApi = retrofit.create(DublinBusApi.class);

        createNotificationChannel();
        enableApp();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            enableApp();
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void searchLine(View view) {
        // check if fine-location access is already available
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // if it's available -> enable the app's functionality
            String userInput = inputLineEditText.getText().toString();
            if(userInput.isEmpty()) {
                inputLineEditText.setError("Enter a bus line");
                Toast.makeText(MainActivity.this, "Enter a bus line", Toast.LENGTH_SHORT).show();
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
                            if (!response.body().getErrorCode().equals("0")) {
                                Log.e("Error", "No results found");
                            } else {
                                BusRoute busRoute = new BusRoute();
                                busRoute.setErrorCode(response.body().getErrorCode());
                                busRoute.setRouteName(response.body().getRouteName());
                                busRoute.setRoutes(response.body().getRoutes());

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

    public void openPermissionSettings(View view) {
        if (permissionBtn.getText().equals("settings")) {
            // add text with link to settings to add permission manually
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivityForResult(intent, 0);
            //startActivity(intent);
        } else {
            enableApp();
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
                    permissionBtn.setText("settings");
                } else {
                    permissionBtn.setText("allow");
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}

