package com.thunguip.lefit;


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
    public static final int CONTEXT_LVITEM                  = 1;

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

        /* Get refer date */
        if (isClickedOnLvItem(clickedcontext)) {
            mp.referdate = refer;
        }
        else if (isReferingToYesterday()) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(Preferences.TimeHelper.getTodayDate());
            cal.add(Calendar.DAY_OF_MONTH, -1);
            refer = mp.referdate = cal.getTimeInMillis();
        }
        else {
            refer = mp.referdate = Preferences.TimeHelper.getTodayDate();
        }

        /* Select which phrase set, min, max and default */
        int profile = whichProfile();
        mp.phraseset = profile + 1;

        Log.d("Decision","PROFILE: " + profile);

        PopupEntryParcel peps[] = new StorageDB(context).getAnsweredPopupEntries();

        TypedArray ids = context.getResources().obtainTypedArray(R.array.phrases);
        int phraseid = ids.getResourceId(mp.phraseset, -1);
        ids.recycle();
        int phrasecount = context.getResources().getStringArray(phraseid).length;

        int countuntilnow = 0;

        for (PopupEntryParcel pep : peps) {
            if (pep.daterefer < refer)
                countuntilnow++;
            else
                break;
        }

        switch (countuntilnow) {
            case 0:
                mp.minphrase = 0;
                mp.defphrase = 1;
                mp.maxphrase = phrasecount -1;
                break;
            case 1:
                switch (preferences.getPersonStyle()) {
                    case Preferences.PERSONSTYLE_SEDENTARY:
                        mp.minphrase = 0;
                        mp.defphrase = 1;
                        mp.maxphrase = 3;
                        break;
                    case Preferences.PERSONSTYLE_ACTIVE:
                        mp.minphrase = 2;
                        mp.defphrase = 3;
                        mp.maxphrase = 5;
                        break;
                    case Preferences.PERSONSTYLE_MODERATE:
                        mp.minphrase = 4;
                        mp.defphrase = 5;
                        mp.maxphrase = 7;
                        break;
                    case Preferences.PERSONSTYLE_INTENSE:
                        mp.minphrase = 6;
                        mp.defphrase = 7;
                        mp.maxphrase = 9;
                        break;
                }
                break;
            default:
                int x = peps[countuntilnow - 1].phraseanswer;
                mp.minphrase = x - 1;
                mp.defphrase = x;
                mp.maxphrase = x + 2;

                if (mp.minphrase < 0) {
                    mp.maxphrase = mp.maxphrase - mp.minphrase;
                    mp.minphrase = 0;
                }
                else if (mp.maxphrase >= phrasecount) {
                    mp.minphrase = mp.minphrase - (mp.maxphrase - phrasecount + 1);
                    mp.maxphrase = phrasecount - 1;
                }

                break;
        }

        /* Select dailly message */
        if (isFirstTime() || (preferences.isShowDaillyMessage() == false)) {
            /* Do not show if it is the first time or if the user ask to hide those messages */
            mp.showmessage = 0;
        }
        else { /* Else, show the messages */
            mp.showmessage = 1;
            mp.messageset = 0;

            // get message set/subset based on how many times did the person see the message?

            /* Based on how many answeres already done */
            int totalansered = (new StorageDB(context).getAnsweredPopupEntries()).length;


            ids = context.getResources().obtainTypedArray(R.array.messages);
            int messageids = ids.getResourceId(mp.messageset, -1);
            ids.recycle();
            ids = context.getResources().obtainTypedArray(messageids);
            int msgcount = ids.length();
            ids.recycle();


            mp.messagesubset = totalansered - 1;

            mp.messagesubset = mp.messagesubset < 0 ? 0 : mp.messagesubset;
            mp.messagesubset = mp.messagesubset >= msgcount ? mp.messagesubset % msgcount : mp.messagesubset;
        }

        /* Select if this popup can be postponed or not */
        if (!isClickedOnLvItem(clickedcontext) && canPostpone()) {
            mp.showpostpone = 1;
        }
        else { /* It was clicked on LvItem or it is too closed to next day notification,
                    else cannot be postponed */
            mp.showpostpone = 0;
        }

        return mp;
    }

    public NotificationCompat.Builder getNotificationBuilderByContext(boolean fireAlarms) {
        /* Is, by context, needed to fire notification? */
        PopupEntryParcel[] peps = new StorageDB(context).getAnsweredPopupEntries();

        long checktime = Preferences.TimeHelper.getTodayDate();
        if (Preferences.TimeHelper.getTodayTime() < preferences.getNotificationTime()) {
            checktime = Preferences.TimeHelper.getYesterdayDate();
        }

        for (PopupEntryParcel pep : peps) {
            if (Preferences.TimeHelper.isSameDay(pep.daterefer, checktime)) {
                /* Abort notification */
                return null;
            }
        }


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
