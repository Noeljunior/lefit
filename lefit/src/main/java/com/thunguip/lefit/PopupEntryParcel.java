package com.thunguip.lefit;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.Calendar;

public class PopupEntryParcel implements Parcelable {
    public static final int POPUP_HIDE_HIDEN = -1;
    public static final int POPUP_HIDE_FALSE = 0;
    public static final int POPUP_HIDE_TRUE = 1;
    public static final int POPUP_ACTION_IGNORE = 0;
    public static final int POPUP_ACTION_CANCEL = 1;
    public static final int POPUP_ACTION_POSTPONE = 2;
    public static final int POPUP_ACTION_SUBMIT = 3;

    public int _id;
    public int _type;
    public int _sent;
    public int title;
    public int phraseset;
    public int phrasemin;
    public int phrasemax;
    public int phraseanswer;
    public int phrasehitmore;
    public int phrasehitless;
    public int messageset;
    public int messagesubset;
    public int messagedef;
    public int messagehide;
    public int messagemore;
    public int showpostpone;
    public int action;
    public long daterefer;
    public long dateinit;
    public long dateaction;

    public PopupEntryParcel() {
    }

    public PopupEntryParcel(int title, int phraseset, int phrasemin, int phrasemax, int phraseanswer, int phrasehitmore, int phrasehitless, int messageset, int messagesubset, int messagehide, int messagemore, int action, long daterefer, long dateinit, long dateaction) {
        this._type = StorageDB.TYPE_POPUP;
        this.title = title;
        this.phraseset = phraseset;
        this.phrasemin = phrasemin;
        this.phrasemax = phrasemax;
        this.phraseanswer = phraseanswer;
        this.phrasehitmore = phrasehitmore;
        this.phrasehitless = phrasehitless;
        this.messageset = messageset;
        this.messagesubset = messagesubset;
        this.messagehide = messagehide;
        this.messagemore = messagemore;
        this.action = action;
        this.daterefer = daterefer;
        this.dateinit = dateinit;
        this.dateaction = dateaction;
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(StorageDB.UniqTable.COLUMN_NAME_TYPE , _type);
        values.put(StorageDB.UniqTable.COLUMN_NAME_SENT , StorageDB.SENT_FALSE);
        values.put(StorageDB.UniqTable.COLUMN_NAME_VAL1 , title);
        values.put(StorageDB.UniqTable.COLUMN_NAME_VAL2 , phraseset);
        values.put(StorageDB.UniqTable.COLUMN_NAME_VAL3 , phrasemin);
        values.put(StorageDB.UniqTable.COLUMN_NAME_VAL4 , phrasemax);
        values.put(StorageDB.UniqTable.COLUMN_NAME_VAL5 , phraseanswer);
        values.put(StorageDB.UniqTable.COLUMN_NAME_VAL6 , phrasehitmore);
        values.put(StorageDB.UniqTable.COLUMN_NAME_VAL7 , phrasehitless);
        values.put(StorageDB.UniqTable.COLUMN_NAME_VAL8 , messageset);
        values.put(StorageDB.UniqTable.COLUMN_NAME_VAL9 , messagesubset);
        values.put(StorageDB.UniqTable.COLUMN_NAME_VAL10, messagehide);
        values.put(StorageDB.UniqTable.COLUMN_NAME_VAL11, messagemore);
        values.put(StorageDB.UniqTable.COLUMN_NAME_VAL12, action);
        values.put(StorageDB.UniqTable.COLUMN_NAME_VAL13, daterefer);
        values.put(StorageDB.UniqTable.COLUMN_NAME_VAL14, dateinit);
        values.put(StorageDB.UniqTable.COLUMN_NAME_VAL15, dateaction);
        return values;
    }

    public Calendar getDateRefer() {
        Calendar entryDate = Calendar.getInstance();
        entryDate.setTimeInMillis(daterefer);
        return entryDate;
    }

    public static PopupEntryParcel findByDay(PopupEntryParcel[] peps, Calendar cal) {
        for (PopupEntryParcel pep : peps) {
            if (Preferences.TimeHelper.isSameDay(pep.getDateRefer(), cal))
                return pep;
        }
        return null;
    }

    /* Parcelable */
    public PopupEntryParcel(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(_id);
        dest.writeInt(_type);
        dest.writeInt(_sent);
        dest.writeInt(title);
        dest.writeInt(phraseset);
        dest.writeInt(phrasemin);
        dest.writeInt(phrasemax);
        dest.writeInt(phraseanswer);
        dest.writeInt(phrasehitmore);
        dest.writeInt(phrasehitless);
        dest.writeInt(messageset);
        dest.writeInt(messagesubset);
        dest.writeInt(messagedef);
        dest.writeInt(messagehide);
        dest.writeInt(messagemore);
        dest.writeInt(showpostpone);
        dest.writeInt(action);
        dest.writeLong(daterefer);
        dest.writeLong(dateinit);
        dest.writeLong(dateaction);
    }

    private void readFromParcel(Parcel in) {
        _id = in.readInt();
        _type = in.readInt();
        _sent = in.readInt();
        title = in.readInt();
        phraseset = in.readInt();
        phrasemin = in.readInt();
        phrasemax = in.readInt();
        phraseanswer = in.readInt();
        phrasehitmore = in.readInt();
        phrasehitless = in.readInt();
        messageset = in.readInt();
        messagesubset = in.readInt();
        messagedef = in.readInt();
        messagehide = in.readInt();
        messagemore = in.readInt();
        showpostpone = in.readInt();
        action = in.readInt();
        daterefer = in.readLong();
        dateinit = in.readLong();
        dateaction = in.readLong();
    }

    public static final Parcelable.Creator CREATOR =
            new Parcelable.Creator() {
                public PopupEntryParcel createFromParcel(Parcel in) {
                    return new PopupEntryParcel(in);
                }
                public PopupEntryParcel[] newArray(int size) {
                    return new PopupEntryParcel[size];
                }
            };


    @Override
    public String toString() {
        return "[" + _id +" : " + _type + " | " + _sent + "] " + "{" +
                title + "; " +
                phraseset + "; " +
                phrasemin + "; " +
                phrasemax + "; " +
                phraseanswer + "; " +
                phrasehitmore + "; " +
                phrasehitless + "; " +
                messageset + "; " +
                messagesubset + "; " +
                messagehide + "; " +
                messagemore + "; " +
                action + "; " +
                Preferences.TimeHelper.toString(daterefer) + "; " +
                Preferences.TimeHelper.toString(dateinit) + "; " +
                Preferences.TimeHelper.toString(dateaction) +
                "}";
    }
}