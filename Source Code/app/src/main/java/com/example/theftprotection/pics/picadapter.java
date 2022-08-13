package com.example.theftprotection.pics;


import android.annotation.SuppressLint;
import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.theftprotection.R;

import java.util.ArrayList;

public class picadapter extends RecyclerView.Adapter<picadapter.MyViewHolder> implements Filterable{

    private final ArrayList<pic> noteList;
    final Context context;


    public picadapter(ArrayList<pic> noteList, Context context) {
        this.noteList = noteList;
        this.context = context;
    }



    final Filter f=new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<pic> temp=new ArrayList<>();
            ArrayList<pic> newList = new ArrayList<>();
            for (pic element : temp) {
                if (!newList.contains(element)) {
                    newList.add(element);
                }
            }
            FilterResults fr=new FilterResults();
            fr.values=newList;
            return fr;
        }

        @SuppressWarnings("unchecked")
        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            noteList.clear();
            noteList.addAll((ArrayList<pic>)results.values);
            notifyDataSetChanged();
        }
    };



    @Override
    public Filter getFilter() {
        return f;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public final TextView title;
        public final ImageView img;

        @SuppressLint("NotifyDataSetChanged")
        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.textView_ImgName);
            img = view.findViewById(R.id.imageView_flag);
            view.setOnLongClickListener(v -> false);
        }


    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.itm, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        pic picture = noteList.get(position);
        holder.title.setText(picture.getTitle());
        Bitmap bm = BitmapFactory.decodeFile(picture.getimg().getAbsolutePath());
        holder.img.setImageBitmap(bm);
    }


    @Override
    public int getItemCount() {
        return noteList.size();
    }
}
