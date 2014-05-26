package com.wsn.lefit.lefit;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.BaseColumns;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class MainService extends IntentService {
    /* Intent receivers */
    public static final String SWITCH = "com.wsn.lefit.lefit.mainservice.SWITCH";
    public static final String MESSENGER = "com.wsn.lefit.lefit.mainservice.MESSENGER";

    public static final String DEBUG = "com.wsn.lefit.lefit.mainservice.SWITCH.DEBUG";
    public static final String GETLVITEMS = "com.wsn.lefit.lefit.mainservice.SWITCH.GETLVITEMS";

    /* Broadcasts senders */
    public static final String BROADCAST = "com.wsn.lefit.lefit.mainservice.BROADCAST";

    public static final String BC_SWITCH = "com.wsn.lefit.lefit.mainservice.BROADCAST.BC_SWITCH";
    public static final String BC_UPDATEITEMS = "com.wsn.lefit.lefit.mainservice.BROADCAST.BC_UPDATEITEMS";

    /* Init date */
    Calendar initCalendar;

    public MainService() {
        super("MainService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        /* TODO get and set the init day */
        initCalendar = Calendar.getInstance();
        initCalendar.set(Calendar.YEAR, 2014);
        initCalendar.set(Calendar.MONTH, 4);
        initCalendar.set(Calendar.DAY_OF_MONTH, 0);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent.getStringExtra(SWITCH) == null) {
            // Unknown intent
            return;
        }

        switch (intent.getStringExtra(SWITCH)) {
            case GETLVITEMS:
                sendLvItems((Messenger) intent.getExtras().get(MESSENGER));
                return;

            case DEBUG:
                debugIntent(intent);
                return;
            default:
                return;
        }


    }

    private void sendBroadcast(String bswitch) {
        Intent i = new Intent(BROADCAST);
        i.putExtra(BC_SWITCH, bswitch);
        sendBroadcast(i);
    }




    private void sendLvItems(Messenger messenger) {
        Message msg = Message.obtain();
        Bundle data = new Bundle();
        data.putString(SWITCH, GETLVITEMS);

        data.putParcelableArray(GETLVITEMS, getDummyItems(10));

        msg.setData(data);
        try {
            messenger.send(msg);
        } catch (RemoteException e) {
            Log.i("ERROR", "Error send lv items back");
        }
    }
















    private LvItemParcel[] getDummyItems(int n) {
        ArrayList<LvItemParcel> items = new ArrayList<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d 'de' MMM");

        Calendar today = Calendar.getInstance();
        Calendar iterator = (Calendar) initCalendar.clone();

        //if (iterator.getTimeInMillis() <= today.getTimeInMillis());

        int i = 1;

        while (iterator.getTimeInMillis() <= today.getTimeInMillis()) {
            Log.d("MainService", "DATE: " + dateFormat.format(iterator.getTime()));
            if (iterator.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                items.add(new LvItemParcel(LvItemParcel.Type.SEPRATOR, "Semana " + i++));
            }
            iterator.add(Calendar.DAY_OF_MONTH, 1);
            if (Math.random() <= 0.2) {
                items.add(new LvItemParcel(LvItemParcel.Type.ITEM_UNFILLED, R.drawable.ic_questionmark, "Clique para preencher", dateFormat.format(iterator.getTime())));
                continue;
            }

            int drawable;
            String phrase;

            int sel = (int) (Math.random() * 10.99);
            switch (sel) {
                default:
                case  0: drawable = R.drawable.ic_phraseicon_0;  phrase = getResources().getStringArray(R.array.phrases0)[0];  break;
                case  1: drawable = R.drawable.ic_phraseicon_1;  phrase = getResources().getStringArray(R.array.phrases0)[1];  break;
                case  2: drawable = R.drawable.ic_phraseicon_2;  phrase = getResources().getStringArray(R.array.phrases0)[2];  break;
                case  3: drawable = R.drawable.ic_phraseicon_3;  phrase = getResources().getStringArray(R.array.phrases0)[3];  break;
                case  4: drawable = R.drawable.ic_phraseicon_4;  phrase = getResources().getStringArray(R.array.phrases0)[4];  break;
                case  5: drawable = R.drawable.ic_phraseicon_5;  phrase = getResources().getStringArray(R.array.phrases0)[5];  break;
                case  6: drawable = R.drawable.ic_phraseicon_6;  phrase = getResources().getStringArray(R.array.phrases0)[6];  break;
                case  7: drawable = R.drawable.ic_phraseicon_7;  phrase = getResources().getStringArray(R.array.phrases0)[7];  break;
                case  8: drawable = R.drawable.ic_phraseicon_8;  phrase = getResources().getStringArray(R.array.phrases0)[8];  break;
                case  9: drawable = R.drawable.ic_phraseicon_9;  phrase = getResources().getStringArray(R.array.phrases0)[9];  break;
                case 10: drawable = R.drawable.ic_phraseicon_10; phrase = getResources().getStringArray(R.array.phrases0)[10]; break;
            }
            items.add(new LvItemParcel(LvItemParcel.Type.ITEM_FILLED, drawable, phrase, dateFormat.format(iterator.getTime())));
        }

        return items.toArray(new LvItemParcel[items.size()]);
    }


    private void debugIntent(Intent it) {
        Intent i = new Intent(BROADCAST);

        i.putExtra(BC_SWITCH, BC_UPDATEITEMS);

        sendBroadcast(i);
    }



    private void checkIfSendsANotification() {
        /* Go check if its time to send notification */


        /* So, its time to send notification */

        /* Pick a message to send */
        /*MessageParcel m = new MessageParcel("Como é o seu estilo de vida?",
                new String[] {"Sou sedentário", "Sou activo mas não pratico exercício", "Faço algum exercicio", "Faço muito"},
                new int[] {R.drawable.ic_phraseicon_0, R.drawable.ic_phraseicon_1, R.drawable.ic_phraseicon_2, R.drawable.ic_phraseicon_3},
                new String[] {"A vida é bela", "Faça exercicio fisico!", "Lamba-me o escroto"},
                2, 0,
                1);*/

        //sendNotification(m);
    }


    private void sendNotification(MessageParcel m) {
        NotificationCompat.Builder mBuilder;
        mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat)
                .setContentTitle("Como foi o seu dia?")
                .setContentText("Toque para registar como foi o seu dia.")
                .addAction (R.drawable.ic_navigation_cancel,
                        "Dispensar", null)
                .addAction (R.drawable.ic_device_access_time,
                        "Adiar", null);

        Intent resultIntent = new Intent(this, PopupActivity.class);

        resultIntent.putExtra("msg", m);


        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(PopupActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        /* TODO: do something with mId : 0  */
        mNotificationManager.notify(0, mBuilder.build());
    }



    /*private class StorageDB extends SQLiteOpenHelper {
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "";

        public static final String SQL_CREATE_ENTRIES = "";


        public static abstract class StorageEntry implements BaseColumns {
            public static final String TABLE_NAME = "entry";
            public static final String COLUMN_NAME_ENTRY_ID = "entryid";
            public static final String COLUMN_NAME_TITLE = "title";
            public static final String COLUMN_NAME_SUBTITLE = "subtitle";
        }

        public StorageDB(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }

    }*/

}
