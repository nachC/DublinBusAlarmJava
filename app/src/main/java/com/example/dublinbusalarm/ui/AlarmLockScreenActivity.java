package com.example.dublinbusalarm.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.example.dublinbusalarm.R;
import com.example.dublinbusalarm.receivers.AlarmReceiver;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;
import static android.provider.AlarmClock.ACTION_DISMISS_ALARM;

public class AlarmLockScreenActivity extends AppCompatActivity {

    private static final String NOTIFICATION_DISMISS = "dismiss";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.activity_alarm_lock_screen);
    }

    public void stopAlarmLockScreen(View view) {
        Intent dismissIntent = new Intent(AlarmLockScreenActivity.this, AlarmReceiver.class);
        dismissIntent.setAction(ACTION_DISMISS_ALARM);
        dismissIntent.putExtra(EXTRA_NOTIFICATION_ID, NOTIFICATION_DISMISS);
        sendBroadcast(dismissIntent);
    }
}