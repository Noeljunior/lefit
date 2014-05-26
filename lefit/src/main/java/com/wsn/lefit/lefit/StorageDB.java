package com.wsn.lefit.lefit;


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

    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";


    public static abstract class DailyEntry implements BaseColumns {
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



        private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + INT_TYPE + " PRIMARY KEY " + COMMA_SEP +
                        COLUMN_NAME_TYPE + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_SENT + INT_TYPE + COMMA_SEP +

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
                        COLUMN_NAME_VAL14 + INT_TYPE + " )";

        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public StorageDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DailyEntry.SQL_CREATE_ENTRIES);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(DailyEntry.SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    /* - - - Own methods - - - */


    public long addDailyRow(int answer, String date) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DailyEntry.COLUMN_NAME_ANSWERID, answer);
        values.put(DailyEntry.COLUMN_NAME_DATE, date);

        return db.insert(DailyEntry.TABLE_NAME, null, values);
    }

    public void readDaily() {
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                DailyEntry._ID,
                DailyEntry.COLUMN_NAME_ANSWERID,
                DailyEntry.COLUMN_NAME_DATE,
        };


        Cursor c = db.query(
                DailyEntry.TABLE_NAME,                    // The table to query
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
                    c.getLong(c.getColumnIndexOrThrow(DailyEntry._ID)) + "] [" +
                    c.getLong(c.getColumnIndexOrThrow(DailyEntry.COLUMN_NAME_ANSWERID)) + "] [" +
                    c.getString(c.getColumnIndexOrThrow(DailyEntry.COLUMN_NAME_DATE))+ "]"
            );

            c.moveToNext();
        }

    }

}
