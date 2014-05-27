package com.thunguip.lefit;

import android.os.Parcel;
import android.os.Parcelable;


public class LvItemParcel implements Parcelable {
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

    public Type type;
    public int logo;
    public String description;
    public String date;

    private static final long serialVersionUID = 2L;

    public LvItemParcel(Type type, int logo, String description, String date) {
        this.type = type;
        this.logo = logo;
        this.description = description;
        this.date = date;
    }

    public LvItemParcel(Type type, String description) {
        this.type = type;
        this.logo = 0;
        this.description = description;
        this.date = "";
    }

    /* Parcelable */
    public LvItemParcel(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type.getId());
        dest.writeInt(logo);
        dest.writeString(description);
        dest.writeString(date);
    }

    private void readFromParcel(Parcel in) {
        type = Type.values()[in.readInt()];
        logo = in.readInt();
        description = in.readString();
        date = in.readString();
    }

    public static final Parcelable.Creator CREATOR =
            new Parcelable.Creator() {
                public LvItemParcel createFromParcel(Parcel in) {
                    return new LvItemParcel(in);
                }
                public LvItemParcel[] newArray(int size) {
                   return new LvItemParcel[size];
                }
            };


}

