package com.thunguip.lefit;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class StorageDB extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "lefit";

    private static final String INT_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";

    private static final int TYPE_POPUP = 1;
    private static final int SENT_TRUE = 1;
    private static final int SENT_FALSE = 0;

    public static abstract class UniqEntry implements BaseColumns {
        public static final String TABLE_NAME = "uniqtable";

        public static final String COLUMN_NAME_TYPE  = "type";
        public static final String COLUMN_NAME_SENT  = "sent";
        public static final String COLUMN_NAME_VAL1  = "val1";
        public static final String COLUMN_NAME_VAL2  = "val2";
        public static final String COLUMN_NAME_VAL3  = "val3";
        public static final String COLUMN_NAME_VAL4  = "val4";
        public static final String COLUMN_NAME_VAL5  = "val5";
        public static final String COLUMN_NAME_VAL6  = "val6";
        public static final String COLUMN_NAME_VAL7  = "val7";
        public static final String COLUMN_NAME_VAL8  = "val8";
        public static final String COLUMN_NAME_VAL9  = "val9";
        public static final String COLUMN_NAME_VAL10 = "val10";
        public static final String COLUMN_NAME_VAL11 = "val11";
        public static final String COLUMN_NAME_VAL12 = "val12";
        public static final String COLUMN_NAME_VAL13 = "val13";
        public static final String COLUMN_NAME_VAL14 = "val14";
        public static final String COLUMN_NAME_VAL15 = "val15";

        private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + INT_TYPE + " PRIMARY KEY " + COMMA_SEP +
                        COLUMN_NAME_TYPE  + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_SENT  + INT_TYPE + COMMA_SEP +

                        COLUMN_NAME_VAL1  + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_VAL2  + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_VAL3  + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_VAL4  + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_VAL5  + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_VAL6  + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_VAL7  + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_VAL8  + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_VAL9  + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_VAL10 + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_VAL11 + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_VAL12 + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_VAL13 + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_VAL14 + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_VAL15 + INT_TYPE + " )";

        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public StorageDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(UniqEntry.SQL_CREATE_ENTRIES);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(UniqEntry.SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    /* - - - OWN CLASSES - - - */
    public class PopupEntry {
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
        public int messagehide;
        public int messagemore;
        public int action;
        public int daterefer;
        public int dateinit;
        public int dateaction;

        public PopupEntry(int _type, int title, int phraseset, int phrasemin, int phrasemax, int phraseanswer, int phrasehitmore, int phrasehitless, int messageset, int messagesubset, int messagehide, int messagemore, int action, int daterefer, int dateinit, int dateaction) {
            this._type = _type;
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
            values.put(UniqEntry.COLUMN_NAME_TYPE , _type);
            values.put(UniqEntry.COLUMN_NAME_SENT , SENT_FALSE);
            values.put(UniqEntry.COLUMN_NAME_VAL1 , title);
            values.put(UniqEntry.COLUMN_NAME_VAL2 , phraseset);
            values.put(UniqEntry.COLUMN_NAME_VAL3 , phrasemin);
            values.put(UniqEntry.COLUMN_NAME_VAL4 , phrasemax);
            values.put(UniqEntry.COLUMN_NAME_VAL5 , phraseanswer);
            values.put(UniqEntry.COLUMN_NAME_VAL6 , phrasehitmore);
            values.put(UniqEntry.COLUMN_NAME_VAL7 , phrasehitless);
            values.put(UniqEntry.COLUMN_NAME_VAL8 , messageset);
            values.put(UniqEntry.COLUMN_NAME_VAL9 , messagesubset);
            values.put(UniqEntry.COLUMN_NAME_VAL10, messagehide);
            values.put(UniqEntry.COLUMN_NAME_VAL11, messagemore);
            values.put(UniqEntry.COLUMN_NAME_VAL12, action);
            values.put(UniqEntry.COLUMN_NAME_VAL13, daterefer);
            values.put(UniqEntry.COLUMN_NAME_VAL14, dateinit);
            values.put(UniqEntry.COLUMN_NAME_VAL15, dateaction);
            return values;
        }
    }

    /* - - - Own methods - - - */

    public long addPopup(PopupEntry entry) {
        SQLiteDatabase db = getWritableDatabase();

        return db.insert(UniqEntry.TABLE_NAME, null, entry.getContentValues());
    }


    public PopupEntry[] getUnansweredPopupEntries() {


        return null;
    }

    public void readDaily() {
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                UniqEntry._ID/*,
                UniqEntry.COLUMN_NAME_ANSWERID,
                UniqEntry.COLUMN_NAME_DATE,*/
        };


        Cursor c = db.query(
                UniqEntry.TABLE_NAME,                    // The table to query
                projection,                               // The columns to return
                null,                                     // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // The sort order
        );



        c.moveToFirst();
        while (c.isAfterLast() == false) {

            Log.d("StorageDB", "SQLITEM: [" +
                    c.getLong(c.getColumnIndexOrThrow(UniqEntry._ID)) + "] [" /*+
                    c.getLong(c.getColumnIndexOrThrow(UniqEntry.COLUMN_NAME_ANSWERID)) + "] [" +
                    c.getString(c.getColumnIndexOrThrow(UniqEntry.COLUMN_NAME_DATE))+ "]"*/
            );

            c.moveToNext();
        }

    }

}
