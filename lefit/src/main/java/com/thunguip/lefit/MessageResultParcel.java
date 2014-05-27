package com.thunguip.lefit;


import android.os.Parcel;
import android.os.Parcelable;

public class MessageResultParcel implements Parcelable {
    public static final int HIDE_HIDEN = -1;
    public static final int HIDE_FALSE = 0;
    public static final int HIDE_TRUE = 1;
    public static final int ACTION_IGNORE = 0;
    public static final int ACTION_CANCEL = 1;
    public static final int ACTION_POSTPONE = 2;
    public static final int ACTION_SUBMIT = 3;



    // DATA SET
    public int title;

    public int phrasesset;
    public int phrasesmin;
    public int phrasesmax;
    public int phrasesanswered;
    public int phraseshitsmore;
    public int phraseshitsless;

    public int messageset;
    public int messagesubset;
    public int messagehithide;
    public int messagehitmore;

    public int action;

    public int daterefer;
    public int dateinit;
    public int dateaction;



    public MessageResultParcel() {
    }

    /* PARCELABLE SPECIFIC */
    public MessageResultParcel(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(title);

        dest.writeInt(phrasesset);
        dest.writeInt(phrasesmin);
        dest.writeInt(phrasesmax);
        dest.writeInt(phrasesanswered);
        dest.writeInt(phraseshitsmore);
        dest.writeInt(phraseshitsless);

        dest.writeInt(messageset);
        dest.writeInt(messagesubset);
        dest.writeInt(messagehithide);
        dest.writeInt(messagehitmore);

        dest.writeInt(action);

        dest.writeInt(daterefer);
        dest.writeInt(dateinit);
        dest.writeInt(dateaction);
    }


    private void readFromParcel(Parcel in) {
        this.title = in.readInt();

        this.phrasesset = in.readInt();
        this.phrasesmin = in.readInt();
        this.phrasesmax = in.readInt();
        this.phrasesanswered = in.readInt();
        this.phraseshitsmore = in.readInt();
        this.phraseshitsless = in.readInt();

        this.messageset = in.readInt();
        this.messagesubset = in.readInt();
        this.messagehithide = in.readInt();
        this.messagehitmore = in.readInt();

        this.action = in.readInt();

        this.daterefer = in.readInt();
        this.dateinit = in.readInt();
        this.dateaction = in.readInt();

    }

    public static final Parcelable.Creator CREATOR =
            new Parcelable.Creator() {
                public MessageResultParcel createFromParcel(Parcel in) {
                    return new MessageResultParcel(in);
                }
                public MessageResultParcel[] newArray(int size) {
                    return new MessageResultParcel[size];
                }
            };

}
