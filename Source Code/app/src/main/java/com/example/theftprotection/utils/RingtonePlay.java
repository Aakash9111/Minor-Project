package com.example.theftprotection.utils;

import android.app.Service;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;

public class RingtonePlay extends Service
{
    private Ringtone r;

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        r=RingtoneManager.getRingtone(this, notification);
        r.play();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy()
    {
        r.stop();
    }

}