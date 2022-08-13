package com.example.theftprotection.ui.main;

import android.Manifest;
import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import com.example.theftprotection.FrontUI;
import com.example.theftprotection.deviceadmin.MyAdmin;
import com.example.theftprotection.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;


public class SettingFrag extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

    private ComponentName mAdminName;
    Context context;
    private DevicePolicyManager mDPM;
    FirebaseAuth mAuth;
    Preference uninstall,logout,change_number;
    SwitchPreference emailalert,soundalert,smsalert,detect_breakin;
    ListPreference listPreference;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    private void removeADMIN(){
        mDPM.removeActiveAdmin(mAdminName);
    }

    public SettingFrag(Context context) {
        this.context=context;
        this.mDPM = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        this.mAdminName = new ComponentName(context, MyAdmin.class);
        this.mAuth = FirebaseAuth.getInstance();
        this.sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.editor = sharedpreferences.edit();
    }

    public SettingFrag(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        try{
            addPreferencesFromResource(R.xml.preference);
        }catch (ClassCastException e){
            Log.e("Exception",e.getLocalizedMessage());
        }

        uninstall= findPreference("Uninstall");
        logout= findPreference("Logout");
        change_number= findPreference("change_number");
        emailalert= findPreference("Email Alert");
        soundalert= findPreference("Sound Alert");
        listPreference =findPreference("max_unlock_attempt");
        smsalert= findPreference("SMS alert");
        detect_breakin= findPreference("break-in");



        assert change_number != null;
        change_number.setSummary(sharedpreferences.getString("emergency_number",""));
        change_number.setOnPreferenceClickListener(preference -> {
            shownumber();
            return false;
        });


        assert uninstall != null;
        uninstall.setOnPreferenceClickListener(preference -> {
            removeADMIN();
            Intent intent = new Intent(Intent.ACTION_DELETE);
            intent.setData(Uri.parse("package:com.example.theftprotection"));
            startActivity(intent);
            return false;
        });

        assert logout != null;
        logout.setOnPreferenceClickListener(preference -> {
            new AlertDialog.Builder(context)
                    .setTitle("Logging Out")
                    .setMessage("Are you sure?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {

                        mAuth.signOut();
                        removeADMIN();
                        Intent i= new Intent(context, FrontUI.class);
                        startActivity(i);
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
            return false;
        });

        assert emailalert != null;
        emailalert.setOnPreferenceChangeListener((preference, newValue) -> {
            this.editor.putString("email",newValue.toString());
            Log.e("",newValue.toString());
            this.editor.apply();
            return true;
        });

        assert soundalert != null;
        soundalert.setOnPreferenceChangeListener((preference, newValue) -> {

            this.editor.putString("sound",newValue.toString());
            Log.e("",newValue.toString());
            this.editor.apply();
            return true;
        });

        listPreference.setOnPreferenceChangeListener((preference, newValue) -> {

            this.editor.putString("max_unlock_attempt",newValue.toString());
            Log.e("",newValue.toString());
            this.editor.apply();
            return true;
        });



        assert smsalert != null;
        smsalert.setOnPreferenceChangeListener((preference, newValue) -> {

            if(newValue.toString().equals("true")) requestSmsPermission();
            if(!sharedpreferences.getBoolean("emergency_number_is_set",false)) shownumber();
            this.editor.putString("sms alert",newValue.toString());
            Log.e("",newValue.toString());
            this.editor.apply();
            return true;
        });



        assert detect_breakin != null;
        detect_breakin.setOnPreferenceChangeListener((preference, newValue) -> {

            this.editor.putString("detect_breakin",newValue.toString());
            Log.e("",newValue.toString());
            this.editor.apply();
            return true;
        });



    }


    private static final int PERMISSION_SEND_SMS = 123;
    private void requestSmsPermission() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            // request permission (see result in onRequestPermissionsResult() method)
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.SEND_SMS},
                    PERMISSION_SEND_SMS);
        }
    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

    }


    @Override
    public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {

        return false;
    }

    private void shownumber()
    {
     /* final Dialog d = new Dialog(context);
        d.setContentView(R.layout.numberdialog);
        d.setCancelable(false);
        Button b1 = d.findViewById(R.id.ok);
        EditText text = (EditText) d.findViewById(R.id.number);
        b1.setOnClickListener(v -> {
            if (text.equals("") || text.length() < 13) {
                Toast.makeText(context,"Enter a right mobile number",Toast.LENGTH_LONG).show();
            }else{
                editor.putBoolean("emergency_number_is_set",true);
                editor.putString("emergency_number",text.getText().toString());
                editor.apply();
                change_number.setSummary(sharedpreferences.getString("emergency_number",""));
                d.dismiss();
            }
        });
        d.show();*/


        AlertDialog.Builder builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
        builder.setTitle(R.string.enter_emergency_phone_number);
        builder.setMessage("Please enter number which is not in this mobile phone");
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.numberdialog, (ViewGroup) getView(), false);
        final EditText input = (EditText) viewInflated.findViewById(R.id.input);
        builder.setView(viewInflated);
        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            if (input.equals("") || input.length() < 13) {
                  Snackbar.make(getView(),"Enter Correct number",Snackbar.LENGTH_LONG).show();
            }else{
                editor.putBoolean("emergency_number_is_set",true);
                editor.putString("emergency_number",input.getText().toString());
                editor.apply();
                change_number.setSummary(sharedpreferences.getString("emergency_number",""));
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());
        builder.show();
    }



}
