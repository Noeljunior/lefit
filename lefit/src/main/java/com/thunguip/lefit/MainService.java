package com.thunguip.lefit;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class MainService extends IntentService {
    /* Intent receivers */
    public static final String SWITCH               = "com.thunguip.lefit.mainservice.SWITCH";
    public static final String MESSENGER            = "com.thunguip.lefit.mainservice.MESSENGER";
    public static final String DEBUG                = "com.thunguip.lefit.mainservice.SWITCH.DEBUG";

    public static final String GETLVITEMS           = "com.thunguip.lefit.mainservice.SWITCH.GETLVITEMS";
    public static final String ADDANSWERTODB        = "com.thunguip.lefit.mainservice.SWITCH.ADDANSWERTODB";

    public static final String ALARM                = "com.thunguip.lefit.mainservice.SWITCH.ALARM";

    public static final String NOTIFICATIONACTION   = "com.thunguip.lefit.mainservice.SWITCH.NOTIFICATIONACTION";
    public static final String NOTIFCATION_POSTPONE = "com.thunguip.lefit.mainservice.SWITCH.NOTIFICATIONACTION.NOTIFCATION_POSTPONE";
    public static final String NOTIFCATION_OPNEN    = "com.thunguip.lefit.mainservice.SWITCH.NOTIFICATIONACTION.NOTIFCATION_OPNEN";

    public static final String INVOKEPOPUPBYLVITEM  = "com.thunguip.lefit.mainservice.SWITCH.INVOKEPOPUPBYLVITEM";

    public static final String CHECKALARMSETTINGS   = "com.thunguip.lefit.mainservice.SWITCH.CHECKALARMSETTINGS";


    /* Broadcasts senders */
    public static final String BROADCAST        = "com.thunguip.lefit.mainservice.BROADCAST";

    public static final String BC_SWITCH        = "com.thunguip.lefit.mainservice.BROADCAST.BC_SWITCH";
    public static final String BC_UPDATEITEMS   = "com.thunguip.lefit.mainservice.BROADCAST.BC_UPDATEITEMS";

    /* Alarm Identifiers */
    public static final int ALARMID_REPEATED    = 1;
    public static final int ALARMID_POSTPONED   = 2;
    public static final int ALARMID_CLEAN       = 3;
    public static final int ALARMID_RENOTIF     = 4;

    /* Notification Identifiers */
    public static final int NOTIFID_MAIN        = 1;
    public static final int NOTIFID_PIRESULT    = 100;
    public static final int NOTIFID_PIPOSTPONE  = 101;

    /* Preferences */
    private Preferences preferences;

    public MainService() {
        super("MainService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        preferences = new Preferences(this);
    }

    private void sendBroadcast(String bswitch) {
        Intent i = new Intent(BROADCAST);
        i.putExtra(BC_SWITCH, bswitch);
        sendBroadcast(i);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent.getStringExtra(SWITCH) == null) {
            // Unknown intent
            return;
        }

        Log.d("MainService", "New Intent: " + intent.getStringExtra(SWITCH));

        switch (intent.getStringExtra(SWITCH)) {
            /* Request and receive data */
            case GETLVITEMS:
                sendLvItems((Messenger) intent.getExtras().get(GETLVITEMS));
                return;
            case ADDANSWERTODB:
                addAnswerToDB((PopupEntryParcel) intent.getParcelableExtra(ADDANSWERTODB));
                return;

            /* Alarm */
            case ALARM:
                handleAlarm(intent.getIntExtra(ALARM, -1));
                return;

            /* Notification */
            case NOTIFICATIONACTION:
                handleNotificationAction(intent.getStringExtra(NOTIFICATIONACTION));
                return;

            /* Popup invokers */
            case INVOKEPOPUPBYLVITEM:
                openPopupByRefer(intent.getLongExtra(INVOKEPOPUPBYLVITEM, -1));
                return;

            case CHECKALARMSETTINGS:
                checkAlarmSettings();
                return;

            case DEBUG:

                return;
            default:
                /* Unknown SWITCH */
        }
    }

    /* Alarmer Switch */
    private void handleAlarm(int as) {
        switch (as) {
            case ALARMID_REPEATED:
                Log.d("MainService", "HANDLEALARM: ALARMID_REPEATED");
                /* TODO set notification's title and descritions based on something */
                /*String title = "Como foi o seu dia?";
                String description = "Toque para registar como foi o seu dia.";


                sendNotificationByMessage(NOTIFID_MAIN, title, description);*/
                sendNotification(new Decision(this).getNotificationBuilderByContext(true), NOTIFID_MAIN);
                return;
            case ALARMID_POSTPONED:
                Log.d("MainService", "HANDLEALARM: ALARMID_POSTPONED");
                /* TODO get last postponed  */

                /* TODO check if refer date is older then yerterday  */


                /*sendNotificationByMessage(NOTIFID_MAIN,
                        "Como foi o seu dia ontem?",
                        "Toque para registar como foi o seu dia de ontem.");*/
                sendNotification(new Decision(this).getNotificationBuilderByContext(true), NOTIFID_MAIN);
                return;
            case ALARMID_CLEAN:
                Log.d("MainService", "HANDLEALARM: ALARMID_CLEAN");
                /* Unset alarm */
                AlarmerManager.cancelAlarm(this, ALARMID_POSTPONED);
                /* Remove notification, if any */
                removeNotificationByID(NOTIFID_MAIN);
                return;

            case ALARMID_RENOTIF:
                sendNotification(new Decision(this).getNotificationBuilderByContext(false), NOTIFID_MAIN);
                return;

            default:
                Log.d("MainService", "HANDLEALARM: UNKOWN " + as);
                /* UNKOWN ALARM ID */
                return;
        }
    }

    /* Notification Switch */
    private void handleNotificationAction(String action) {
        Log.d("MainService", "HANDLENOTIFICATION: " + action);
        switch (action) {
            case NOTIFCATION_OPNEN:
                openPopupByNotification();

                return;
            case NOTIFCATION_POSTPONE:
                removeNotificationByID(NOTIFID_MAIN);

                setPostponeNotification();
                return;

            default:
                /* Unknown action */
                return;
        }

    }


    /* orders methods */
    private void sendLvItems(Messenger messenger) {
        Message msg = Message.obtain();
        Bundle data = new Bundle();
        data.putString(SWITCH, GETLVITEMS);

        data.putParcelableArray(GETLVITEMS, loadItemsFromDB());

        msg.setData(data);
        try {
            messenger.send(msg);
        } catch (RemoteException e) {
            Log.i("ERROR", "Error send lv items back");
        }
    }

    private void addAnswerToDB(PopupEntryParcel pep) {
        Log.d("MainService", "NewPEP: " + pep.toString());

        switch (pep.action) {
            case PopupEntryParcel.POPUP_ACTION_SUBMIT:
                sendBroadcast(BC_UPDATEITEMS);


                /* Remove notifications if already answered */
                if (Preferences.TimeHelper.getTodayTime() < preferences.getNotificationTime()) {
                    Calendar yesterday = Calendar.getInstance();
                    yesterday.setTimeInMillis(Preferences.TimeHelper.getTodayDate());
                    yesterday.add(Calendar.DAY_OF_MONTH, -1);

                    if (Preferences.TimeHelper.isSameDay(pep.daterefer, yesterday.getTimeInMillis())) {
                        MainService.sendIntent(this, MainService.ALARM, MainService.ALARMID_CLEAN);
                    }

                } else if (Preferences.TimeHelper.isSameDay(pep.daterefer, Preferences.TimeHelper.getTodayDate())) {
                    MainService.sendIntent(this, MainService.ALARM, MainService.ALARMID_CLEAN);
                }

                /* Set the StartDate */
                if (pep.title == 0) {
                    /* The person just answered the first time, so set the start date by now */
                    preferences.setStartDate(Preferences.TimeHelper.getTodayDate());

                    preferences.setPersonStyle(pep.phraseanswer);
                }
                break;

            case PopupEntryParcel.POPUP_ACTION_CANCEL:
                /* Remove notifications if already answered */
                if (Preferences.TimeHelper.getTodayTime() < preferences.getNotificationTime()) {
                    Calendar yesterday = Calendar.getInstance();
                    yesterday.setTimeInMillis(Preferences.TimeHelper.getTodayDate());
                    yesterday.add(Calendar.DAY_OF_MONTH, -1);

                    if (Preferences.TimeHelper.isSameDay(pep.daterefer, yesterday.getTimeInMillis())) {
                        MainService.sendIntent(this, MainService.ALARM, MainService.ALARMID_CLEAN);
                    }

                } else if (Preferences.TimeHelper.isSameDay(pep.daterefer, Preferences.TimeHelper.getTodayDate())) {
                    MainService.sendIntent(this, MainService.ALARM, MainService.ALARMID_CLEAN);
                }
                break;

            case PopupEntryParcel.POPUP_ACTION_POSTPONE:
                removeNotificationByID(NOTIFID_MAIN);
                setPostponeNotification();
                break;


            default:
        }

        StorageDB db = new StorageDB(this);
        db.addEntry(pep);

    }

    private void openPopupByRefer(long refer) {
        Intent intent = new Intent(this, PopupActivity.class);

        MessageParcel m = new Decision(this).getMessageParcelByContext(Decision.CONTEXT_LVITEM, refer);

        intent.putExtra(PopupActivity.MESSAGE, m);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(intent);
    }

    private void openPopupByNotification() {
        Intent intent = new Intent(this, PopupActivity.class);

        MessageParcel m = new Decision(this).getMessageParcelByContext(Decision.CONTEXT_NOTIFICATION, 0);

        intent.putExtra(PopupActivity.MESSAGE, m);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

        startActivity(intent);
    }

    private void setPostponeNotification() {
        AlarmerManager.setRelativeAlarm(this,
                MainService.ALARMID_POSTPONED,
                SystemClock.elapsedRealtime() + preferences.getPostponeDelay());
    }

    private void checkAlarmSettings() {
        if (preferences.isFireNotifications()) {
            /* Enable or updatate notifications alarms */
            AlarmerManager.setRepeatingAlarm(this,
                    ALARMID_REPEATED,
                    Preferences.TimeHelper.getNextByTime(preferences.getNotificationTime()),
                    preferences.getNotificationInterval());

            AlarmerManager.setRepeatingAlarm(this,
                    ALARMID_CLEAN,
                    Preferences.TimeHelper.getNextByTime(
                            preferences.getNotificationTime() - preferences.getNotificationCleanGap()),
                    preferences.getNotificationInterval()
            );

            /* Set alarm at boot */
            ComponentName receiver = new ComponentName(this, BootReceiver.class);
            PackageManager pm = getPackageManager();
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);



            /*Log.d("MainService", "CHECKALARMSETTINGS | TIME: " +
                    Preferences.TimeHelper.toString(Preferences.TimeHelper.getNextByTime(preferences.getNotificationTime()))
                    + "(" + Preferences.TimeHelper.getNextByTime(preferences.getNotificationTime()) + ")");
            Log.d("MainService", "CHECKALARMSETTINGS | INTERVAL: " + Preferences.TimeHelper.toString(preferences.getNotificationInterval())
                    + "(" + preferences.getNotificationInterval() + ")");

            Log.d("MainService", "CHECKALARMSETTINGS | POSTDELAY: " + Preferences.TimeHelper.toString(preferences.getPostponeDelay())
                    + "(" + preferences.getPostponeDelay() + ")");

            Log.d("MainService", "CHECKALARMSETTINGS | CLEANGAP: " + Preferences.TimeHelper.toString(Preferences.TimeHelper.getNextByTime(
                    preferences.getNotificationTime() - preferences.getNotificationCleanGap()))
                    + "(" + Preferences.TimeHelper.getNextByTime(
                    preferences.getNotificationTime() - preferences.getNotificationCleanGap()) + ")");

            Log.d("MainService", "CHECKALARMSETTINGS | CLEANGAP: " + Preferences.TimeHelper.toString(preferences.getNotificationCleanGap()) +
                    "(" + preferences.getNotificationCleanGap() + ")");
            Log.d("MainService", "TEST " + Preferences.TimeHelper.getByTime(0, 0) + " :: " + Preferences.TimeHelper.toString(0));*/
        }
        else {
            /* Disable notifications alarms */
            AlarmerManager.cancelAlarm(this, ALARMID_REPEATED);
            AlarmerManager.cancelAlarm(this, ALARMID_POSTPONED);
            AlarmerManager.cancelAlarm(this, ALARMID_CLEAN);

            removeNotificationByID(NOTIFID_MAIN);

            /* Do not set alarm at boot */
            ComponentName receiver = new ComponentName(this, BootReceiver.class);
            PackageManager pm = getPackageManager();
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);

            Log.d("MainService", "CHECKALARMSETTINGS: Alarm unset");
        }


        Preferences.TimeHelper.getNextByTime(preferences.getNotificationTime());
    }







    private LvItemParcel[] loadItemsFromDB() {
        ArrayList<LvItemParcel> items = new ArrayList<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d 'de' MMM");

        Calendar iterator = Calendar.getInstance();
        iterator.setTimeInMillis(Preferences.TimeHelper.getTodayDate());

        StorageDB database = new StorageDB(this);
        PopupEntryParcel[] entries = database.getAnsweredPopupEntries();
        PopupEntryParcel pep;

        long startDate;

        if (preferences.getPersonStyle() == Preferences.PERSONSTYLE_NOTDEFINED) {
            startDate = Preferences.TimeHelper.getTodayDate();
        }
        else {
            startDate = preferences.getStartDate();
        }

        while (iterator.getTimeInMillis() >= startDate) {
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
                        iterator.getTimeInMillis()));
            }
            iterator.add(Calendar.DAY_OF_MONTH, -1);

            if (iterator.getTimeInMillis() >= startDate && iterator.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
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


    private void sendNotification(NotificationCompat.Builder mBuilder, int mid) {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(mid, mBuilder.build());
    }

    public void removeNotificationByID(int id) {
        ((NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE)).cancel(id);
    }


    /* STATIC METHODS */
    public static void sendIntent(Context context, String switchitent) {
        Intent intent = new Intent(context, MainService.class);
        intent.putExtra(MainService.SWITCH, switchitent);
        context.startService(intent);
    }

    public static void sendIntent(Context context, String switchitent, long extra) {
        Intent intent = new Intent(context, MainService.class);
        intent.putExtra(MainService.SWITCH, switchitent);
        intent.putExtra(switchitent, extra);
        context.startService(intent);
    }

    public static void sendIntent(Context context, String switchitent, int extra) {
        Intent intent = new Intent(context, MainService.class);
        intent.putExtra(MainService.SWITCH, switchitent);
        intent.putExtra(switchitent, extra);
        context.startService(intent);
    }

    public static void sendIntent(Context context, String switchitent, Parcelable extra) {
        Intent intent = new Intent(context, MainService.class);
        intent.putExtra(MainService.SWITCH, switchitent);
        intent.putExtra(switchitent, extra);
        context.startService(intent);
    }

    public static boolean isDebuggable(Context context) {
        return ( 0 != ( context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE ) );
    }

}
