package com.example.sourabh.seafood;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Panwar on 05/06/17.
 */

public class Detection_Adapter extends BaseAdapter {

    ArrayList<String> Data = new ArrayList<String>();
    Context CONT ;
    private static LayoutInflater inflater=null;


    public Detection_Adapter(ArrayList<String> data, Context context)
    {
        Data = data;
        CONT = context;
        inflater = (LayoutInflater)context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }


    @Override
    public int getCount() {

        return Data.size();
    }

    @Override
    public Object getItem(int position) {
        return Data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View root = inflater.inflate(R.layout.detection_item,null);
        TextView item = (TextView)root.findViewById(R.id.text_detection);
        item.setText(Data.get(position));
        return root;
    }
}
