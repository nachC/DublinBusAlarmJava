package com.example.dublinbusalarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.provider.AlarmClock;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;
import static android.provider.AlarmClock.ACTION_DISMISS_ALARM;

/* TODO:
*   - what to do after alarm notification gets dismissed. Send back to MainActivity?
**/

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiver";
    NotificationManagerCompat notificationManager;
    private static final int NOTIFICATION_ID = 0;
    private static final String ALARM_CHANNEL_ID = "alarm_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmReceiver", "called");
        if (intent.hasExtra(EXTRA_NOTIFICATION_ID) && intent.getStringExtra(EXTRA_NOTIFICATION_ID).equals("dismiss")) {
            Log.d(TAG, "dismiss clicked in notification");

            Intent serviceIntent = new Intent(context, LocationService.class);
            context.stopService(serviceIntent);

            Intent stopRingtoneIntent = new Intent(context, RingtonePlayService.class);
            context.stopService(stopRingtoneIntent);
            notificationManager = NotificationManagerCompat.from(context);
            notificationManager.cancel(NOTIFICATION_ID);

            //Intent backToMainActivityIntent = new Intent(context, MainActivity.class);
            //backToMainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //context.startActivity(backToMainActivityIntent);
        } else {
            Log.d(TAG, "intent to initiate alarm");
            Intent startRingtoneIntent = new Intent(context, RingtonePlayService.class);
            context.startService(startRingtoneIntent);
            createNotification(context);
        }
    }

    public void createNotification(Context context) {
        Intent dismissIntent = new Intent(context, AlarmReceiver.class);
        dismissIntent.setAction(ACTION_DISMISS_ALARM);
        dismissIntent.putExtra(EXTRA_NOTIFICATION_ID, "dismiss");
        PendingIntent dismissPendingIntent =
                PendingIntent.getBroadcast(context, 0, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, ALARM_CHANNEL_ID)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setContentTitle("Arrived to stop")
                .setContentText("Time to get out!")
                .setContentIntent(dismissPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .addAction(R.drawable.ic_launcher_background, "dismiss", dismissPendingIntent);

        notificationManager = NotificationManagerCompat.from(context);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}

