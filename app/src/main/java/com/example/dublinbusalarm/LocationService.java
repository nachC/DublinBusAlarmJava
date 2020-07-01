package com.example.dublinbusalarm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class LocationService extends Service {

    private static final String TAG = "LocationService";
    private FusedLocationProviderClient fusedLocationClient;
    private static final String LOCATION_SERVICE_CHANNEL_ID = "location_service_channel";

    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.location_service_channel_name);
            String description = getString(R.string.location_service_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel locationServiceChannel = new NotificationChannel(LOCATION_SERVICE_CHANNEL_ID, name, importance);
            locationServiceChannel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(locationServiceChannel);

            Notification notification = new NotificationCompat.Builder(this, LOCATION_SERVICE_CHANNEL_ID)
                    .setContentTitle("Title")
                    .setContentText("Text").build();

            startForeground(1, notification);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "onStartCommand started");
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
