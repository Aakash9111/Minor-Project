package com.example.theftprotection.utils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import androidx.annotation.Nullable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class send_SMS extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        LocationTrackClass l= new LocationTrackClass(getBaseContext());
        StringBuilder msg;
        String latitude=Double.toString(l.getLatitude());
        String longitude=Double.toString(l.getLongitude());
        msg = new StringBuilder("Your device" + new Device().getdevicemodel()+"is been accessed by Someone at "+latitude+" and "+longitude+" visit below link to track your device");

        try {
            URL url= new URL("https://www.google.com/maps/search/?api=1&query="+l.getLatitude()+","+l.getLongitude());
            msg.append(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        try {
            SmsManager sms = SmsManager.getDefault();
            ArrayList<String> parts = sms.divideMessage(msg.toString());
            sms.sendMultipartTextMessage("+916265471199", null, parts, null, null);
            Log.e("in service","SMS SENT");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
