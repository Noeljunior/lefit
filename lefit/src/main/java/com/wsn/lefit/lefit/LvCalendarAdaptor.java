package com.wsn.lefit.lefit;


import android.app.Activity;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class LvCalendarAdaptor extends BaseAdapter {
    private ArrayList<LvItemParcel> items = new ArrayList<LvItemParcel>();


    private static LayoutInflater inflater = null;


    public LvCalendarAdaptor(Activity activity) {
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public ArrayList<LvItemParcel> getItems() {
        return items;
    }

    public void setItems(ArrayList<LvItemParcel> items) {
        this.items = items;
    }

    public void addItem(LvItemParcel item) {
        items.add(item);
    }

    public void addItemFilled(int logo, String description, String date) {
        items.add(0, new LvItemParcel(LvItemParcel.Type.ITEM_FILLED, logo, description, date));
    }
    public void addItemUnfilled(int logo, String description, String date) {
        items.add(0, new LvItemParcel(LvItemParcel.Type.ITEM_UNFILLED, logo, description, date));
    }
    public void addSeparator(String description) {
        items.add(0, new LvItemParcel(LvItemParcel.Type.SEPRATOR, description));
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public LvItemParcel getItem(int position) {
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
        return LvItemParcel.Type.values().length;
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


}
