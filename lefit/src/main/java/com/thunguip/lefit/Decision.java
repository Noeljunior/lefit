package com.thunguip.lefit;


import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Calendar;

public class Decision {
    private Context context;
    private Preferences preferences;

    public static final int CONTEXT_NOTIFICATION            = 0;
    public static final int CONTEXT_POSTPONEDNOTIFICATION   = 1;
    public static final int CONTEXT_LVITEM                  = 2;

    public Decision(Context context) {
        this.context = context;

        preferences = new Preferences(context);
    }

    public MessageParcel getMessageParcelByContext(int clickedcontext, long refer) {
        MessageParcel mp = new MessageParcel();

        /* Select which title */
        if (isFirstTime()) { /* The first time */
            mp.title = 0;
        }
        else if (isClickedOnLvItem(clickedcontext)) { /* Clicked on specific LvItem */
            mp.title = 3;
        }
        else if (isReferingToYesterday()) { /* It is about yesterday */
            mp.title = 2;
        }
        else { /* It is about today */
            mp.title = 1;
        }

        /* Select which phrase set, min, max and default */
        if (whichProfile() == -1) { /* The first time */
            mp.phraseset = 0;
            mp.minphrase = 0;

            TypedArray ids = context.getResources().obtainTypedArray(R.array.phrases);
            int phraseid = ids.getResourceId(mp.phraseset, -1);
            ids.recycle();

            mp.maxphrase = context.getResources().getStringArray(phraseid).length - 1;
            mp.defphrase = 1;
        }
        else { /* Not the first time, get the prase set based on formula */
            if (whichProfile() == 0)
                mp.phraseset = 1;
            else
                mp.phraseset = 2;

            // TODO get min, max and default
        }

        /* Select dailly message */
        if (isFirstTime() || (preferences.isShowDaillyMessage() == false)) {
            /* Do not show if it is the first time or if the user ask to hide those messages */
            mp.showmessage = 0;
        }
        else { /* Else, show the messages */
            mp.showmessage = 1;

            // TODO get message set/subset based on how many days does the person answered
            // TODO get message set/subset based on how many times did the person see the message
        }

        /* Select if this popup can be postponed or not */
        if (!isClickedOnLvItem(clickedcontext) && canPostpone()) {
            mp.showpostpone = 1;
        }
        else { /* It was clicked on LvItem or it is too closed to next day notification,
                    else cannot be postponed */
            mp.showpostpone = 0;
        }

        /* Get refer date */
        if (isClickedOnLvItem(clickedcontext)) {
            mp.referdate = refer;
        }
        else if (isReferingToYesterday()) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(Preferences.TimeHelper.getTodayDate());
            cal.add(Calendar.DAY_OF_MONTH, -1);
            mp.referdate = cal.getTimeInMillis();
        }
        else {
            mp.referdate = Preferences.TimeHelper.getTodayDate();
        }

        return mp;
    }

    public NotificationCompat.Builder getNotificationBuilderByContext(boolean fireAlarms) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.drawable.ic_stat);

        /* Result Action */
        Intent resultIntent = new Intent(context, MainService.class);
        resultIntent.putExtra(MainService.SWITCH, MainService.NOTIFICATIONACTION);
        resultIntent.putExtra(MainService.NOTIFICATIONACTION, MainService.NOTIFCATION_OPNEN);
        PendingIntent resultPendingIntent = PendingIntent.getService(context, MainService.NOTIFID_PIRESULT, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);


        /* Select title and description */
        int strid;
        if (isFirstTime()) { /* The first time */
            strid = 0;
        }
        else if (isReferingToYesterday()) { /* It is about yesterday */
            strid = 2;
        }
        else { /* It is about today */
            strid = 1;
        }
        mBuilder.setContentTitle(context.getResources().getStringArray(R.array.titles)[strid]);
        mBuilder.setContentText(context.getResources().getStringArray(R.array.notifdesc)[strid]);
        mBuilder.setTicker(context.getResources().getStringArray(R.array.titles)[strid]);

        /* Select if can be postponed */
        if (canPostpone()) {
            Intent postponeIntent = new Intent(context, MainService.class);
            postponeIntent.putExtra(MainService.SWITCH, MainService.NOTIFICATIONACTION);
            postponeIntent.putExtra(MainService.NOTIFICATIONACTION, MainService.NOTIFCATION_POSTPONE);
            PendingIntent postponePendingIntent = PendingIntent.getService(context, MainService.NOTIFID_PIPOSTPONE, postponeIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            mBuilder.addAction(R.drawable.ic_device_access_time, context.getResources().getString(R.string.notifpostpone), postponePendingIntent);
        }

        /* Set the notifications user preferences */
        if (preferences.isNotificationVibrate()) {
            mBuilder.setSound(Uri.parse(preferences.getNotificationSound()));
            mBuilder.setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS);
        }

        /* Select if it is to update or to launch new */
        if (!fireAlarms) {
            mBuilder.setOnlyAlertOnce(true);
            mBuilder.setTicker(null);
        }


        return mBuilder;
    }


    public boolean isFirstTime() {
        return preferences.getPersonStyle() == Preferences.PERSONSTYLE_NOTDEFINED;
    }
    public boolean isClickedOnLvItem(int clickedcontext) {
        return clickedcontext == CONTEXT_LVITEM;
    }
    public boolean isReferingToYesterday() {
        return Preferences.TimeHelper.getTodayTime() < preferences.getNotificationTime();
    }
    public boolean canPostpone() {
        return (Preferences.TimeHelper.getTodayTime() <
                (preferences.getNotificationTime() - preferences.getNotificationCleanGap() - preferences.getPostponeDelay())) ||
                (Preferences.TimeHelper.getTodayTime() >= preferences.getNotificationTime());
    }
    public int whichProfile() {
        if (preferences.getPersonStyle() == Preferences.PERSONSTYLE_SEDENTARY ||
                preferences.getPersonStyle() == Preferences.PERSONSTYLE_ACTIVE) {
            /* Profile one */
            return 0;
        }
        else if (preferences.getPersonStyle() == Preferences.PERSONSTYLE_MODERATE ||
                preferences.getPersonStyle() == Preferences.PERSONSTYLE_INTENSE) {
            /* Profile two */
            return 1;
        }
        else if (preferences.getPersonStyle() == Preferences.PERSONSTYLE_NOTDEFINED) {
            return -1;
        }
        return -1;
    }



}
