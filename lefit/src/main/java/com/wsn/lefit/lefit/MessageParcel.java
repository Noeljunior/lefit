package com.wsn.lefit.lefit;

import android.os.Parcel;
import android.os.Parcelable;


public class MessageParcel implements Parcelable {
    public int title;

    public int phraseset;
    public int minphrase;
    public int maxphrase;
    public int defphrase;

    public int messageset;
    public int messagesubset;
    public int defmessage;
    public int showmessage;


    public MessageParcel(int title, int phraseset, int minphrase, int maxphrase, int defphrase, int messageset, int messagesubset, int defmessage, int showmessage) {
        this.title = title;
        this.phraseset = phraseset;
        this.minphrase = minphrase;
        this.maxphrase = maxphrase;
        this.defphrase = defphrase;
        this.messageset = messageset;
        this.messagesubset = messagesubset;
        this.defmessage = defmessage;
        this.showmessage = showmessage;
    }

    /* PARCELABLE SPECIFIC */
    public MessageParcel(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(title);
        dest.writeInt(phraseset);
        dest.writeInt(minphrase);
        dest.writeInt(maxphrase);
        dest.writeInt(defphrase);

        dest.writeInt(messageset);
        dest.writeInt(messagesubset);
        dest.writeInt(defmessage);
        dest.writeInt(showmessage);
    }


    private void readFromParcel(Parcel in) {
        this.title = in.readInt();
        this.phraseset = in.readInt();
        this.minphrase = in.readInt();
        this.maxphrase = in.readInt();
        this.defphrase = in.readInt();
        this.messageset = in.readInt();
        this.messagesubset = in.readInt();
        this.defmessage = in.readInt();
        this.showmessage = in.readInt();
    }

    public static final Parcelable.Creator CREATOR =
            new Parcelable.Creator() {
                public MessageParcel createFromParcel(Parcel in) {
                    return new MessageParcel(in);
                }
                public MessageParcel[] newArray(int size) {
                    return new MessageParcel[size];
                }
            };


}
