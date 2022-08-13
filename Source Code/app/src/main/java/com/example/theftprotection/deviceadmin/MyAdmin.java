package com.example.theftprotection.deviceadmin;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.example.theftprotection.utils.Capture_Service;
import com.example.theftprotection.utils.RingtonePlay;
import com.example.theftprotection.utils.send_SMS;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

public class MyAdmin extends DeviceAdminReceiver {

    String name;
    String email;
    Uri photoUrl;
    String providerId;
    String uid;
    int failed=-1;

    @Override
    public void onEnabled(@NonNull Context context, @NonNull Intent intent) {
        super.onEnabled(context, intent);
        getProviderData();
    }


    @Override
    public void onDisabled(@NonNull Context context, @NonNull Intent intent) {
        super.onDisabled(context, intent);
    }

    @Override
    public void onPasswordFailed(Context context, Intent intent) {
        Log.d("Hello", "onPasswordFailed");

        SharedPreferences sharedPreferences =  PreferenceManager.getDefaultSharedPreferences(context);
        int max=Integer.parseInt(sharedPreferences.getString("max_unlock_attempt","3"));

        Log.e("MAX",String.valueOf(max));
        this.failed= Integer.parseInt(sharedPreferences.getString("failed","0"));

        Log.e("Failed",String.valueOf(this.failed));
        getProviderData();
        this.failed+=1;

        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("failed",String.valueOf(this.failed));
        editor.apply();
        Log.e("Failed_update",sharedPreferences.getString("failed","0"));

        Log.e("condition",String.valueOf(this.failed==max));

        if(this.failed==max)
            context.startService(new Intent(context, Capture_Service.class));

        if(sharedPreferences.getString("sound", "false").equals("true") &&this.failed==max )
            context.startService(new Intent(context, RingtonePlay.class));

        if(sharedPreferences.getString("sms alert", "false").equals("true") &&this.failed==max )
            context.startService(new Intent(context, send_SMS.class));

        Log.d("", "onPasswordFailed");
    }

    @Override
    public void onPasswordSucceeded(Context context, Intent intent) {
        Log.d("Hello", "onPasswordSucceeded");

        SharedPreferences sharedPreferences =  PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor=sharedPreferences.edit();


        if(Integer.parseInt(sharedPreferences.getString("failed","0")) > 0)
            context.startService(new Intent(context, Capture_Service.class));


        editor.putString("failed","0");
        editor.apply();

        Intent stopIntent = new Intent(context, RingtonePlay.class);
        context.stopService(stopIntent);

    }


    private void getProviderData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                this.providerId = profile.getProviderId();
                this.uid = profile.getUid();
                this.name = profile.getDisplayName();
                this.email = profile.getEmail();
                 this.photoUrl = profile.getPhotoUrl();
            }
        }
    }



}
