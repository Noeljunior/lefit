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


    private static LayoutInflater inflater = null;


    public LvCalendarAdaptor(Activity activity) {
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        /* TODO delete this dummy data set */
        for (int i = 0; i < 50; i++) {

            if (((i+1) % 7) == 0) {
                items.add(new Item(Type.SEPRATOR, "Semana " + (int) i/7));
            }
            if (Math.random() <= 0.2) {
                items.add(new Item(Type.ITEM_UNFILLED, R.drawable.ic_questionmark, "Clique para preencher", "Seg, 20 de Maio"));
            }

            int sel = (int) (Math.random() * 3.99);
            switch (sel) {
                default:
                case 0:
                    items.add(new Item(Type.ITEM_FILLED, R.drawable.ic_icon_0, "Fui sedentário", "Seg, 10 de Maio"));
                    break;
                case 1:
                    items.add(new Item(Type.ITEM_FILLED, R.drawable.ic_icon_1, "Fui activo", "Ter, 11 de Maio"));
                    break;
                case 2:
                    items.add(new Item(Type.ITEM_FILLED, R.drawable.ic_icon_2, "Pratiquei algum exercício", "Qua, 12 de Maio"));
                    break;
                case 3:
                    items.add(new Item(Type.ITEM_FILLED, R.drawable.ic_icon_3, "Pratiquei muito exercício", "Qui, 13 de Maio"));
                    break;
            }
        }

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

        holder.logo.setImageResource(items.get(position).getLogo());
        holder.description.setText(items.get(position).getDescription());
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

        holder.logo.setImageResource(items.get(position).getLogo());
        holder.description.setText(items.get(position).getDescription());
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

        holder.description.setText(items.get(position).getDescription());

        return convertView;
    }

    public static class ItemHolder {
        public ImageView logo;
        public TextView description;
        public TextView date;
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
