package com.example.theftprotection.ui.main;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.theftprotection.BuildConfig;
import com.example.theftprotection.R;
import com.example.theftprotection.pics.pic;
import com.example.theftprotection.pics.picadapter;
import com.example.theftprotection.ui.main.adapter.imgtouch;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class PicFrag extends Fragment {

    String s="";
    ArrayList<pic> ls;
    picadapter mAdapter;
    RecyclerView recyclerView;
    Context context;

    TabLayout tabLayout;
    public PicFrag(Context context) {
        this.context=context;
    }

    public PicFrag(){}

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private ArrayList<pic> get(){
        File[] allFiles=getpics();
        ls= new ArrayList<>();
        if(allFiles!=null){

            for (File allFile : allFiles) {
                ls.add(new pic(allFile.getName(), allFile));
            }
        }

       Collections.sort(ls, (o1, o2) -> {
            String s1 = o1.title.substring(0, o1.title.lastIndexOf("."));
            String s2 = o2.title.substring(0, o2.title.lastIndexOf("."));
            Date d1=new Date(s1);
            Date d2=new Date(s2);
            return d2.compareTo(d1);
        });

        return ls;
    }



    private File[] getpics(){
        File[] allFiles;
        File folder = new File(Environment.getExternalStorageDirectory().toString() + "/theftprotection/images/");
        allFiles=folder.listFiles();
        if(folder.exists() && allFiles!=null) {
                allFiles= folder.listFiles();
        }
       if (allFiles == null) {
            Log.e("", "null");
        } else {
            Log.e("", "not null");
        }
        return allFiles;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.picfrag, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new picadapter(get(),getActivity());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());



       recyclerView.addOnItemTouchListener(new imgtouch(getActivity(), (view, position) -> {
           s=Environment.getExternalStorageDirectory()+"/theftprotection/images/";
           TextView t=view.findViewById(R.id.textView_ImgName);
           Log.e("","");
           s+=t.getText().toString();
           Intent galleryIntent = new Intent(Intent.ACTION_VIEW, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
           galleryIntent.setDataAndType(FileProvider.getUriForFile(requireActivity(), BuildConfig.APPLICATION_ID + ".fileprovider",new File(s)), "image/*");
           galleryIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
           galleryIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
           startActivity(galleryIntent);

       }));

        tabLayout= rootView.findViewById(R.id.tabLayout);
        return rootView;
    }








}
