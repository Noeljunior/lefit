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
    private enum Type {
        ITEM_FILLED(0),
        ITEM_UNFILLED(1),
        SEPRATOR(2);

        private final int id;
        Type(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    private ArrayList<Item> items = new ArrayList<Item>();


    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater = null;


    public LvCalendarAdaptor(Activity a) {
        activity = a;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        /* TODO delete this dummy data set */
        for (int i = 0; i < 50; i++) {

            if ((i % 10) == 0) {
                items.add(new Item(Type.SEPRATOR, "Separator at " + i));
            }
            else if ((i % 3) == 0) {
                items.add(new Item(Type.ITEM_UNFILLED, R.drawable.ic_questionmark, "Clique para preencher", ""));
            }
            else {
                items.add(new Item(Type.ITEM_FILLED, R.drawable.ic_icon_0, "Descrição da performance deste dia", "Seg, 19 de Maio"));
            }

        }

    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Item getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType().getId();
    }

    @Override
    public int getViewTypeCount() {
        return Type.values().length;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi;

        switch (items.get(position).getType()) {
            default:
            case ITEM_FILLED:
                vi = buildItem(convertView, items.get(position).getLogo(), items.get(position).description, items.get(position).getDate());
                break;
            case ITEM_UNFILLED:
                vi = buildItemUnfilled(convertView, position);
                break;
            case SEPRATOR:
                vi = buildSeparator(convertView, items.get(position).getDescription());
                break;
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

    private View buildItemUnfilled(View convertView, int position) {
        View vi = convertView;
        if(convertView == null)
            vi = inflater.inflate(R.layout.lv_item_unfilled_calendar, null);

        TextView tvdescription = (TextView) vi.findViewById(R.id.tvdescription); // title
        ImageView ivlogo = (ImageView) vi.findViewById(R.id.ivlogo); // thumb image


        // Setting all values in listview
        tvdescription.setText(items.get(position).getDescription());
        ivlogo.setImageResource(items.get(position).getLogo());


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


    public class Item {
        private Type type;

        private int logo;
        private String description;
        private String date;

        private Item(Type type, int logo, String description, String date) {
            this.type = type;
            this.logo = logo;
            this.description = description;
            this.date = date;
        }

        private Item(Type type, String description) {
            this.type = type;
            this.description = description;
        }

        public Type getType() {
            return type;
        }

        public int getLogo() {
            return logo;
        }

        public String getDescription() {
            return description;
        }

        public String getDate() {
            return date;
        }
    }

}
