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
    public static enum Type {
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


    private static LayoutInflater inflater = null;


    public LvCalendarAdaptor(Activity activity) {
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public void addItemFilled(int logo, String description, String date) {
        items.add(0, new Item(Type.ITEM_FILLED, logo, description, date));
    }
    public void addItemUnfilled(int logo, String description, String date) {
        items.add(0, new Item(Type.ITEM_UNFILLED, logo, description, date));
    }
    public void addSeparator(String description) {
        items.add(0, new Item(Type.SEPRATOR, description));
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
        return items.get(position).type.getId();
    }

    @Override
    public int getViewTypeCount() {
        return Type.values().length;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi;

        switch (items.get(position).type) {
            default:
            case ITEM_FILLED:
                vi = buildItemFilled(convertView, position);
                break;
            case ITEM_UNFILLED:
                vi = buildItemUnfilled(convertView, position);
                break;
            case SEPRATOR:
                vi = buildSeparator(convertView, position);
                break;
        }
        return vi;
    }


    private View buildItemFilled(View convertView, int position) {
        ItemHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.lv_item_calendar, null);

            holder = new ItemHolder();
            holder.logo = (ImageView) convertView.findViewById(R.id.ivlogo);
            holder.description = (TextView) convertView.findViewById(R.id.tvdescription);
            holder.date = (TextView) convertView.findViewById(R.id.tvdate);
            convertView.setTag(holder);

            convertView.setEnabled(false);
            convertView.setOnClickListener(null);
        }
        else {
            holder = (ItemHolder) convertView.getTag();
        }

        holder.logo.setImageResource(items.get(position).logo);
        holder.description.setText(items.get(position).description);
        holder.date.setText(items.get(position).date);

        return convertView;
    }

    private View buildItemUnfilled(View convertView, int position) {
        ItemHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.lv_item_unfilled_calendar, null);

            holder = new ItemHolder();
            holder.logo = (ImageView) convertView.findViewById(R.id.ivlogo);
            holder.description = (TextView) convertView.findViewById(R.id.tvdescription);
            holder.date = (TextView) convertView.findViewById(R.id.tvdate);
            convertView.setTag(holder);
        }
        else {
            holder = (ItemHolder) convertView.getTag();
        }

        holder.logo.setImageResource(items.get(position).logo);
        holder.description.setText(items.get(position).description);
        holder.date.setText(items.get(position).date);

        return convertView;
    }

    private View buildSeparator(View convertView, int position) {
        ItemHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.lv_item_separator, null);

            holder = new ItemHolder();
            holder.description = (TextView) convertView.findViewById(R.id.tvtext);
            convertView.setTag(holder);

            convertView.setEnabled(false);
            convertView.setOnClickListener(null);
        }
        else {
            holder = (ItemHolder) convertView.getTag();
        }

        holder.description.setText(items.get(position).description);

        return convertView;
    }

    public static class ItemHolder {
        public ImageView logo;
        public TextView description;
        public TextView date;
    }

    public class Item {
        public Type type;
        public int logo;
        public String description;
        public String date;

        public Item(Type type, int logo, String description, String date) {
            this.type = type;
            this.logo = logo;
            this.description = description;
            this.date = date;
        }

        public Item(Type type, String description) {
            this.type = type;
            this.description = description;
        }

        /*public Type getType() {
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
        }*/
    }

}
