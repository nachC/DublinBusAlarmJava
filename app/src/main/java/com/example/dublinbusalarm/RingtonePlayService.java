package com.example.dublinbusalarm;

import android.app.Service;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class RingtonePlayService extends Service {

    private Ringtone ringtone;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("RingtoneService", "onStartCommand. Playing ringtone");
        ringtone = RingtoneManager.getRingtone(getApplicationContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
        ringtone.play();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i("Ringtone service", "onDestroy");
        ringtone.stop();
    }
}
