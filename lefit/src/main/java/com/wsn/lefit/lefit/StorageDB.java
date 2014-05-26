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
        public static final String TABLE_NAME = "dailyreg";

        public static final String COLUMN_NAME_ANSWERID = "answer";
        public static final String COLUMN_NAME_DATE = "date";

        private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + INT_TYPE + " PRIMARY KEY " + COMMA_SEP +
                        COLUMN_NAME_ANSWERID + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_DATE + TEXT_TYPE+
                        " )";

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
