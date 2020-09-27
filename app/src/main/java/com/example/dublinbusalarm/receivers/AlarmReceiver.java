package com.example.dublinbusalarm.receivers;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.dublinbusalarm.R;
import com.example.dublinbusalarm.services.LocationService;
import com.example.dublinbusalarm.services.RingtonePlayService;
import com.example.dublinbusalarm.ui.MainActivity;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;
import static android.provider.AlarmClock.ACTION_DISMISS_ALARM;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiver";
    private static final String NOTIFICATION_TITLE = "Arrived to Stop"; // holds the title of the alarm notification
    private static final String NOTIFICATION_CONTEXT = "Tap to dismiss alarm"; // holds more info below the title of the notification
    private static final String NOTIFICATION_DISMISS = "dismiss"; // holds the text to show as the notification's button
    private static final String ALARM_CHANNEL_ID = "alarm_channel";
    private static final int NOTIFICATION_ID = 0;

    NotificationManagerCompat notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmReceiver", "called");
        // Here we check if the alarm receiver was invoked to dismiss the ongoing alarm
        // or to set an alarm.
        // First we check if the user clicked the DISMISS button on the notification
        if (intent.hasExtra(EXTRA_NOTIFICATION_ID) && intent.getStringExtra(EXTRA_NOTIFICATION_ID).equals(NOTIFICATION_DISMISS)) {
            // user dismissed the alarm
            Log.d(TAG, "dismiss clicked in notification");

            // stop location service with serviceIntent
            Intent serviceIntent = new Intent(context, LocationService.class);
            context.stopService(serviceIntent);

            // stop ringtone service with stopRingtoneIntent
            Intent stopRingtoneIntent = new Intent(context, RingtonePlayService.class);
            context.stopService(stopRingtoneIntent);

            // cancel the ongoing notification
            notificationManager = NotificationManagerCompat.from(context);
            notificationManager.cancel(NOTIFICATION_ID);

            // clear all activities and go back to the MainActivity
            Intent backToMainActivityIntent = new Intent(context, MainActivity.class);
            backToMainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(backToMainActivityIntent);
        } else {
            // user is setting an alarm
            Log.d(TAG, "intent to initiate alarm");
            // start ringtone service
            Intent startRingtoneIntent = new Intent(context, RingtonePlayService.class);
            context.startService(startRingtoneIntent);
            createNotification(context);
        }
    }

    // method to create the alarm notification
    public void createNotification(Context context) {
        // create intent to call AlarmReceiver and provide action to dismiss the alarm
        Intent dismissIntent = new Intent(context, AlarmReceiver.class);
        dismissIntent.setAction(ACTION_DISMISS_ALARM);
        dismissIntent.putExtra(EXTRA_NOTIFICATION_ID, NOTIFICATION_DISMISS);
        PendingIntent dismissPendingIntent =
                PendingIntent.getBroadcast(context, 0, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, ALARM_CHANNEL_ID)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setContentTitle(NOTIFICATION_TITLE)
                .setContentText(NOTIFICATION_CONTEXT)
                .setContentIntent(dismissPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .addAction(R.drawable.ic_launcher_background, NOTIFICATION_DISMISS, dismissPendingIntent);

        notificationManager = NotificationManagerCompat.from(context);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}

