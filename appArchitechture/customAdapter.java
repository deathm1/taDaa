package com.koshurTech.tadaa;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class customAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> maintitle;

    public customAdapter(Activity context, ArrayList<String> maintitle) {
        super(context, R.layout.my_lists, maintitle);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.maintitle=maintitle;

    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.my_lists_layout, null,true);

        TextView titleText = (TextView) rowView.findViewById(R.id.listName);

        titleText.setText(maintitle.get(position));


        return rowView;

    };
}