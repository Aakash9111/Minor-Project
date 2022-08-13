package com.example.theftprotection.utils;


import android.os.Build;

public class Device{

    public String getdevicemodel(){
        return "\t"+"MODEL: " + Build.MODEL+"\t";
    }

}
