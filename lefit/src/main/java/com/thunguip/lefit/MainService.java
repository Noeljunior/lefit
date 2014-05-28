package com.thunguip.lefit;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
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
    public static final String ALARM = "com.thunguip.lefit.mainservice.SWITCH.ALARM";
    public static final String REMOVETODAYNOTIF = "com.thunguip.lefit.mainservice.SWITCH.REMOVETODAYNOTIF";
    public static final String POSTPONE = "com.thunguip.lefit.mainservice.SWITCH.POSTPONE";

    /* Broadcasts senders */
    public static final String BROADCAST = "com.thunguip.lefit.mainservice.BROADCAST";

    public static final String BC_SWITCH = "com.thunguip.lefit.mainservice.BROADCAST.BC_SWITCH";
    public static final String BC_UPDATEITEMS = "com.thunguip.lefit.mainservice.BROADCAST.BC_UPDATEITEMS";

    /* Init date */
    Calendar initCalendar;
    Calendar alarmTimeCalendar;
    Calendar intervalCalendar;

    /* Alarm Identifiers */
    public static final int ALARM_SENDNOTIFICATION = 1;

    /* Notification Identifiers */
    public static final int NOTIF_TODAY = 1;

    /* Notifications settings */
    private Uri notificationSound;
    private boolean isToVibrate;
    private long fireAlarm;
    private long intervalAlarm;

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

        alarmTimeCalendar ;
        intervalCalendar ;

        /* TODO get saved notification sound and vibrate state */
        notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        isToVibrate = true;
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

            case ALARM:
                handleAlarm(intent.getIntExtra(ALARM, -1));
                return;
            case REMOVETODAYNOTIF:
                ((NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE)).cancel(NOTIF_TODAY);
                return;
            case POSTPONE:
                postponePopup();
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

    private void handleAlarm(int as) {
        switch (as) {
            case ALARM_SENDNOTIFICATION:
                /* TODO set notification's title and descritions based on something */
                String title = "Como foi o seu dia?";
                String description = "Toque para registar como foi o seu dia.";


                sendNotificationByMessage(getTodaysMessageParcel(),
                        NOTIF_TODAY, title, description);
                return;

            default:
                /* UNKOWN ALARM ID */
                return;
        }
    }

    private void postponePopup() {
        ((NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE)).cancel(NOTIF_TODAY);

        Calendar cal = Calendar.getInstance();

        AlarmerManager.setAlarm(this,
                MainService.ALARM_SENDNOTIFICATION,
                cal.getTimeInMillis() + newCalendarByTime(0, 1, 0).getTimeInMillis());
    }


    private void setRepeatAlarmer() {

    }

    private MessageParcel getTodaysMessageParcel() {
        MessageParcel mp = new MessageParcel();

        /* TODO select which title */
        mp.title = 1;

        /* TODO select which phrase set, min, max and default */
        mp.phraseset = 1;
        mp.minphrase = 2;
        mp.maxphrase = 5;
        mp.defphrase = 4;

        /* TODO select which message set, message subset, default message and if it is to show */
        mp.messageset = 1;
        mp.messagesubset = 0;
        mp.defmessage = 0;
        mp.showmessage = 1;

        /* Set the refer */
        mp.referdate = newZeroedNowCalendar().getTimeInMillis();

        return mp;
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





    private void sendNotificationByMessage(MessageParcel m, int mid, String title, String description) {
        /* Actions callbacks */
        Intent cancelIntent = new Intent(this, MainService.class);
        cancelIntent.putExtra(MainService.SWITCH, MainService.REMOVETODAYNOTIF);
        PendingIntent cancelPendingIntent = PendingIntent.getService(this, 0, cancelIntent, 0);

        Intent postponeIntent = new Intent(this, MainService.class);
        postponeIntent.putExtra(MainService.SWITCH, MainService.POSTPONE);
        PendingIntent postponePendingIntent = PendingIntent.getService(this, 1, postponeIntent, 0);


        NotificationCompat.Builder mBuilder;
        mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat)
                .setSound(notificationSound)
                .setContentTitle(title)
                .setContentText(description)
                /*.addAction (R.drawable.ic_navigation_cancel, "Dispensar", cancelPendingIntent)*/
                .addAction (R.drawable.ic_device_access_time, "Adiar", postponePendingIntent);

        if (isToVibrate) {
            mBuilder.setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS);
        }

        Intent resultIntent = new Intent(this, PopupActivity.class);

        resultIntent.putExtra(PopupActivity.MESSAGE, m);


        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(PopupActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(mid, mBuilder.build());
    }



    /* STATIC METHODS */

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

    public static Calendar newCalendarByTime(int hours, int minutes, int seconds) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);

        cal.add(Calendar.HOUR, hours);
        cal.add(Calendar.MINUTE, minutes);
        cal.add(Calendar.SECOND, seconds);

        return cal;
    }

}
