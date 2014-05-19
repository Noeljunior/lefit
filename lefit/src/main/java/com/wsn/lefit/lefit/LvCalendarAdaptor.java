package com.wsn.lefit.lefit;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class LvCalendarAdaptor extends BaseAdapter {
    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater = null;


    public LvCalendarAdaptor(Activity a) {
        activity = a;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public int getCount() {
        return 50;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi;


        if (position % 4 == 0) {
            vi = buildSeparator(convertView, "Separador " + position / 4);
        }
        else {
            vi = buildItem(convertView, R.drawable.ic_icon_0, "Descrição da performance deste dia", "Seg, 19 de Maio");
        }

        return vi;
    }


    private View buildItem(View convertView, int logo, String description, String date) {
        View vi = convertView;
        if(convertView == null)
            vi = inflater.inflate(R.layout.lv_item_calendar, null);

        TextView tvdescription = (TextView) vi.findViewById(R.id.tvdescription); // title
        TextView tvdate = (TextView) vi.findViewById(R.id.tvdate); // artist name
        ImageView ivlogo = (ImageView) vi.findViewById(R.id.ivlogo); // thumb image


        // Setting all values in listview
        tvdescription.setText(description);
        tvdate.setText(date);
        ivlogo.setImageResource(logo);


        return vi;
    }

    private View buildSeparator(View convertView, String text) {
        View vi = convertView;
        if(convertView == null)
            vi = inflater.inflate(R.layout.lv_item_separator, null);

        TextView tvtext = (TextView) vi.findViewById(R.id.tvtext);

        tvtext.setText(text);

        return vi;
    }

}
