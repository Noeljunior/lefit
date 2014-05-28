package com.thunguip.lefit;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class MainService extends IntentService {
    /* Intent receivers */
    public static final String SWITCH = "com.thunguip.lefit.mainservice.SWITCH";
    public static final String MESSENGER = "com.thunguip.lefit.mainservice.MESSENGER";

    public static final String DEBUG = "com.thunguip.lefit.mainservice.SWITCH.DEBUG";
    public static final String GETLVITEMS = "com.thunguip.lefit.mainservice.SWITCH.GETLVITEMS";
    public static final String ADDPOPUPENTRY = "com.thunguip.lefit.mainservice.SWITCH.ADDPOPUPENTRY";
    public static final String INVOKEPOPUP = "com.thunguip.lefit.mainservice.SWITCH.INVOKEPOPUP";
    public static final String FIRENOTIFICATION = "com.thunguip.lefit.mainservice.SWITCH.FIRENOTIFICATION";

    /* Broadcasts senders */
    public static final String BROADCAST = "com.thunguip.lefit.mainservice.BROADCAST";

    public static final String BC_SWITCH = "com.thunguip.lefit.mainservice.BROADCAST.BC_SWITCH";
    public static final String BC_UPDATEITEMS = "com.thunguip.lefit.mainservice.BROADCAST.BC_UPDATEITEMS";

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
        initCalendar.set(Calendar.DAY_OF_MONTH, 20);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent.getStringExtra(SWITCH) == null) {
            // Unknown intent
            return;
        }

        Log.d("MainService", "New Intent: " + intent.getStringExtra(SWITCH));

        switch (intent.getStringExtra(SWITCH)) {
            case GETLVITEMS:
                sendLvItems((Messenger) intent.getExtras().get(MESSENGER));
                return;

            case ADDPOPUPENTRY:
                addPopupEntryToDB((PopupEntryParcel) intent.getParcelableExtra(ADDPOPUPENTRY));
                return;
            case INVOKEPOPUP:
                openNewPopup(intent.getLongExtra(INVOKEPOPUP, -1));
                return;
            case FIRENOTIFICATION:
                fireNotification();
                return;

            case DEBUG:

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

        //data.putParcelableArray(GETLVITEMS, getDummyItems(10));
        data.putParcelableArray(GETLVITEMS, loadItemsFromDB());

        msg.setData(data);
        try {
            messenger.send(msg);
        } catch (RemoteException e) {
            Log.i("ERROR", "Error send lv items back");
        }
    }

    private void addPopupEntryToDB(PopupEntryParcel pep) {
        StorageDB db = new StorageDB(this);
        db.addEntry(pep);

        if (pep.action == PopupEntryParcel.POPUP_ACTION_SUBMIT)
            sendBroadcast(BC_UPDATEITEMS);
    }

    private void openNewPopup(long refer) {
        /* TODO know what to send to popup */

        Intent intent = new Intent(this, PopupActivity.class);

        MessageParcel m = new MessageParcel(1,
                2, 2, 5, 4,
                1, 0, 0, 1,
                newZeroedNowCalendar().getTimeInMillis(), refer);

        intent.putExtra(PopupActivity.MESSAGE, m);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(intent);
    }

    private void fireNotification() {
        MessageParcel m = new MessageParcel(1,
                2, 2, 5, 4,
                1, 0, 0, 1,
                newZeroedNowCalendar().getTimeInMillis(), 0);



        sendNotificationByMessage(m, 0, "Como foi o seu dia?", "Toque para registar como foi o seu dia.");
    }






    private LvItemParcel[] loadItemsFromDB() {
        ArrayList<LvItemParcel> items = new ArrayList<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d 'de' MMM");

        Calendar today = Calendar.getInstance();
        Calendar iterator = (Calendar) today.clone();

        StorageDB database = new StorageDB(this);
        PopupEntryParcel[] entries = database.getAnsweredPopupEntries();
        PopupEntryParcel pep;

        while (iterator.getTimeInMillis() >= initCalendar.getTimeInMillis()) {
            if ((pep = PopupEntryParcel.findByDay(entries, iterator)) != null) { // Answered day
                // Get logo drwable id
                TypedArray ids = getResources().obtainTypedArray(R.array.phraseicons);
                int logoid = ids.getResourceId(pep.phraseanswer, -1);
                ids.recycle();
                // Get phrase string
                ids = getResources().obtainTypedArray(R.array.phrases);
                int phraseid = ids.getResourceId(pep.phraseset, -1);
                ids.recycle();
                String phrase = getResources().getStringArray(phraseid)[pep.phraseanswer];

                items.add(new LvItemParcel(LvItemParcel.Type.ITEM_FILLED,
                        logoid,
                        phrase,
                        dateFormat.format(iterator.getTime())));
            }
            else { // Unanswered day
                items.add(new LvItemParcel(LvItemParcel.Type.ITEM_UNFILLED,
                        R.drawable.ic_questionmark, "Clique para preencher",
                        dateFormat.format(iterator.getTime()),
                        newZeroedCalendarByCalendar(iterator).getTimeInMillis()));
            }
            iterator.add(Calendar.DAY_OF_MONTH, -1);

            if (iterator.getTimeInMillis() >= initCalendar.getTimeInMillis() && iterator.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                SimpleDateFormat sepdateFormat = new SimpleDateFormat("d 'de' MMMM");
                Calendar cloned = (Calendar) iterator.clone();
                String text = " a " + sepdateFormat.format(cloned.getTime());
                cloned.add(Calendar.DAY_OF_MONTH, -6);
                text = "De " + sepdateFormat.format(cloned.getTime()) + text;

                items.add(new LvItemParcel(LvItemParcel.Type.SEPRATOR, text));
            }
        }

        return items.toArray(new LvItemParcel[items.size()]);
    }


    public static boolean isSameDay(Calendar a, Calendar b) {
        return (a.get(Calendar.YEAR) == b.get(Calendar.YEAR)) &&
                (a.get(Calendar.MONTH) == b.get(Calendar.MONTH)) &&
                (a.get(Calendar.DAY_OF_MONTH) == b.get(Calendar.DAY_OF_MONTH));
    }

    public static Calendar newCalendarByDate(int day, int month, int year) {
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(0);
        now.set(Calendar.YEAR, year);
        now.set(Calendar.MONTH, month);
        now.set(Calendar.DAY_OF_MONTH, day);
        return now;
    }

    public static Calendar newZeroedCalendarByCalendar(Calendar cal) {
        return newCalendarByDate(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR));
    }

    public static Calendar newZeroedNowCalendar() {
        return newZeroedCalendarByCalendar(Calendar.getInstance());
    }

    public static Calendar newCalendarByMillis(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        return cal;
    }

    public static String getStringByCalendar(Calendar cal) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d 'de' MMM");
        return dateFormat.format(cal.getTime());
    }

    public static String getStringByMillis(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        return getStringByCalendar(cal);
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



    private void sendNotificationByMessage(MessageParcel m, int mid, String title, String description) {
        NotificationCompat.Builder mBuilder;
        mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat)
                .setContentTitle(title)
                .setContentText(description)
                .addAction (R.drawable.ic_navigation_cancel,
                        "Dispensar", null)
                .addAction (R.drawable.ic_device_access_time,
                        "Adiar", null);

        Intent resultIntent = new Intent(this, PopupActivity.class);

        resultIntent.putExtra(PopupActivity.MESSAGE, m);


        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(PopupActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        /* TODO: do something with mId : 0  */
        mNotificationManager.notify(mid, mBuilder.build());
    }



}
