package com.thunguip.lefit;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.widget.Toast;

import java.util.ArrayList;

public class StorageDB extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "lefit";

    public static final int TYPE_POPUP = 1;
    public static final int SENT_TRUE = 1;
    public static final int SENT_FALSE = 0;
    /* TODO add an ID to each day */
    /* TODO add how was popup action fired [notification | mainactivity] */

    private Context context;

    public static abstract class UniqTable implements BaseColumns {
        public static final String TABLE_NAME = "uniqtable";
        private static final String INT_TYPE = " INTEGER";
        private static final String COMMA_SEP = ",";

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
        this.context = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(UniqTable.SQL_CREATE_ENTRIES);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(UniqTable.SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void resetDataBase() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(UniqTable.SQL_DELETE_ENTRIES);
        onCreate(db);

        if (MainService.isDebuggable(context))
            Toast.makeText(context, "Database was deleted", Toast.LENGTH_SHORT).show();
    }

    /* - - - Own methods - - - */
    public long addEntry(PopupEntryParcel entry) {
        SQLiteDatabase db = getWritableDatabase();
        long res = db.insert(UniqTable.TABLE_NAME, null, entry.getContentValues());
        db.close();
        return res;
    }

    /* * * * DATABASES QUERIES * * * */
    public PopupEntryParcel[] getAnsweredPopupEntries() {
        ArrayList<PopupEntryParcel> results = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                UniqTable.COLUMN_NAME_VAL2,
                UniqTable.COLUMN_NAME_VAL5,
                UniqTable.COLUMN_NAME_VAL13
        };

        String where = UniqTable.COLUMN_NAME_TYPE + " = " + TYPE_POPUP + " AND " +
                UniqTable.COLUMN_NAME_VAL12 + " = " + PopupEntryParcel.POPUP_ACTION_SUBMIT;

        String order = UniqTable.COLUMN_NAME_VAL13 + " ASC";

        Cursor cursor = db.query(
                UniqTable.TABLE_NAME,       // The table to query
                projection,                 // The columns to return
                where,                      // The columns for the WHERE clause
                null,                       // The values for the WHERE clause
                null,                       // don't group the rows
                null,                       // don't filter by row groups
                order                       // The sort order
        );

        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            PopupEntryParcel pe = new PopupEntryParcel();

            pe.phraseset = cursor.getInt(cursor.getColumnIndexOrThrow(UniqTable.COLUMN_NAME_VAL2));
            pe.phraseanswer = cursor.getInt(cursor.getColumnIndexOrThrow(UniqTable.COLUMN_NAME_VAL5));
            pe.daterefer = cursor.getLong(cursor.getColumnIndexOrThrow(UniqTable.COLUMN_NAME_VAL13));

            results.add(pe);

            cursor.moveToNext();
        }

        db.close();

        return results.toArray(new PopupEntryParcel[results.size()]);
    }

    public PopupEntryParcel[] getAnsweredPopupEntries(long today, long yesterday) {
        ArrayList<PopupEntryParcel> results = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                UniqTable.COLUMN_NAME_VAL2,
                UniqTable.COLUMN_NAME_VAL5,
                UniqTable.COLUMN_NAME_VAL13
        };

        String where = UniqTable.COLUMN_NAME_TYPE + " = " + TYPE_POPUP + " AND " +
                UniqTable.COLUMN_NAME_VAL12 + " = " + PopupEntryParcel.POPUP_ACTION_SUBMIT;

        String order = UniqTable.COLUMN_NAME_VAL15 + " ASC";

        Cursor cursor = db.query(
                UniqTable.TABLE_NAME,       // The table to query
                projection,                 // The columns to return
                where,                      // The columns for the WHERE clause
                null,                       // The values for the WHERE clause
                null,                       // don't group the rows
                null,                       // don't filter by row groups
                order                       // The sort order
        );

        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            PopupEntryParcel pe = new PopupEntryParcel();

            pe.phraseset = cursor.getInt(cursor.getColumnIndexOrThrow(UniqTable.COLUMN_NAME_VAL2));
            pe.phraseanswer = cursor.getInt(cursor.getColumnIndexOrThrow(UniqTable.COLUMN_NAME_VAL5));
            pe.daterefer = cursor.getLong(cursor.getColumnIndexOrThrow(UniqTable.COLUMN_NAME_VAL13));

            results.add(pe);

            cursor.moveToNext();
        }

        db.close();

        return results.toArray(new PopupEntryParcel[results.size()]);
    }


}
