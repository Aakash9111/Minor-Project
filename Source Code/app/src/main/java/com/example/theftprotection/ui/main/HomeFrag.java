package com.example.theftprotection.ui.main;

import static android.content.Context.POWER_SERVICE;
import static android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS;

import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.example.theftprotection.Main_Activity;
import com.example.theftprotection.deviceadmin.MyAdmin;
import com.example.theftprotection.R;
import com.example.theftprotection.fgservice;

import java.util.Arrays;
import java.util.Objects;

public class HomeFrag extends Fragment{

    private Context context;
    private ComponentName mAdminName;
    private DevicePolicyManager mDPM;
    private static final int REQUEST_CODE = 0;
    SwitchCompat switchCompat;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;


    public HomeFrag(Context context) {
        this.context=context;
        this.sharedpreferences= PreferenceManager.getDefaultSharedPreferences(context);
        this.editor = sharedpreferences.edit();
    }
    public HomeFrag(){}

    @Override
    public void onResume() {
        super.onResume();
        setswitch();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context=context;

    }

    private boolean checkADMIN(){
        return mDPM.isAdminActive(mAdminName);
    }

    public void setswitch(){
        if(checkADMIN()){
            switchCompat.setChecked(true);
            switchCompat.setText(R.string.activated);
            ContextCompat.startForegroundService(context,new Intent(context, fgservice.class));
        }else {
            switchCompat.setChecked(false);
            switchCompat.setText(R.string.deactivated);
            requireContext().stopService(new Intent(getContext(),fgservice.class));
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = context.getPackageName();
            PowerManager pm = (PowerManager) context.getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
        }


        switchCompat.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked){
                getADMIN();
            }else {
               removeADMIN();
            }
            setswitch();
        });

    }


/*
    public void showattempt()
    {
        final Dialog d = new Dialog(context,android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        d.setContentView(R.layout.cdialog);
        d.setCancelable(false);
        Button b1 = d.findViewById(R.id.buttonOk);
        final NumberPicker np = d.findViewById(R.id.numberpick);
        np.setMaxValue(5);
        np.setMinValue(1);
        Log.e("", Arrays.toString(np.getDisplayedValues()));
        b1.setOnClickListener(v -> {

            editor.putString("max_unlock_attempt", String.valueOf(np.getValue()));
            editor.putBoolean("max_unlock_attempt_is_set",true);
            Log.e("",String.valueOf(np.getValue()));
            editor.apply();
            d.dismiss();
            startActivity(new Intent(getContext(),Main_Activity.class));
            requireActivity().finish();
        });
        d.show();
    }



*/



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==0) switchCompat.setChecked(false);
        Log.e("ADMIN RIGHTS",String.valueOf(resultCode));
    }

    private void removeADMIN(){
        mDPM.removeActiveAdmin(mAdminName);
    }

    private  void getADMIN(){
        try
        {
            // Initiate DevicePolicyManager.
            mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            // Set DeviceAdminDemo Receiver for active the component with different option
            mAdminName = new ComponentName(context, MyAdmin.class);
            if (!mDPM.isAdminActive(mAdminName)) {
                // try to become active
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Click on Activate button to secure your application.");
                startActivityForResult(intent, REQUEST_CODE);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.homefrag, container, false);
        switchCompat= view.findViewById(R.id.switch2);
        mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        mAdminName = new ComponentName(context, MyAdmin.class);
        switchCompat.setChecked(checkADMIN());
        setswitch();

        return view;
    }

}
