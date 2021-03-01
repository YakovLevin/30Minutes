package com.jacob.a30minutesschedule;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class CustomAdapter extends ArrayAdapter<Task>{
    private static final  String TAG = "Pers";

    private Context mContext;
    private int mResource;
    ImageView checkBox;



    public CustomAdapter(@NonNull Context context, int resource , ArrayList<Task> tasks) {
        super(context, resource , tasks);
        mResource = resource;
        mContext = context;
    }

    @Nullable
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


         LayoutInflater inflater = LayoutInflater.from(mContext);
         convertView = inflater.inflate(mResource , parent , false);

        TextView time  = (TextView) convertView.findViewById(R.id.time);
        TextView context = (TextView) convertView.findViewById(R.id.context);
         checkBox = (ImageView) convertView.findViewById(R.id.checkbox);




        if(getItem(position).isChecked())
            checkBox.setBackgroundResource(R.drawable.dot);



        time.setText(getItem(position).getTime()+"");
        context.setText(getItem(position).getTask());


        return convertView;
    }




    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }



}

