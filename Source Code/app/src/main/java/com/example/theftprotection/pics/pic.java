package com.example.theftprotection.pics;


import java.io.File;

public class pic {
    public final String title;
    public final File img;



    public pic(String title, File img) {
        this.title = title;
        this.img = img;

    }

    public String getTitle(){return  this.title;}
    public File getimg(){return  this.img;}



}