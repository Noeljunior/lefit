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

        /* If there is no PersonStyle defined, then the popup is always the same */
        if (preferences.getPersonStyle() == Preferences.PERSONSTYLE_NOTDEFINED) {
            /* The person did not answer his first time */

            mp.title = 0;
            mp.phraseset = 0;
            mp.minphrase = 0;

            TypedArray ids = context.getResources().obtainTypedArray(R.array.phrases);
            int phraseid = ids.getResourceId(mp.phraseset, -1);
            ids.recycle();

            mp.maxphrase = context.getResources().getStringArray(phraseid).length - 1;
            mp.defphrase = 1;

            mp.showmessage = 0;

            mp.referdate = Preferences.TimeHelper.getTodayDate();

            return mp;
        }

        /* Select referDate and title*/
        if (clickedcontext == CONTEXT_LVITEM) {
            /* The person just clicked on an item, so */
            mp.title = 3;
            mp.referdate = refer;
        }
        else if (Preferences.TimeHelper.getTodayTime() < preferences.getNotificationTime()) {
            /* It may be refering to the day before today */
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(Preferences.TimeHelper.getTodayDate());
            cal.add(Calendar.DAY_OF_MONTH, -1);

            mp.title = 2;
            mp.referdate = cal.getTimeInMillis();

            long gap = (preferences.getNotificationTime() - preferences.getNotificationCleanGap() - preferences.getPostponeDelay());

            /*Log.d("Decision", "TIME: " + Preferences.TimeHelper.toString(preferences.getNotificationTime()) +
                    "; GAP: " + Preferences.TimeHelper.toString(preferences.getNotificationCleanGap()) +
                    "; PDELAY: " + Preferences.TimeHelper.toString(preferences.getPostponeDelay()) +
                    "; SUM: " + Preferences.TimeHelper.toString(gap) +
                    "; NOW: " + Preferences.TimeHelper.toString(Preferences.TimeHelper.getTodayTime()));*/

            if (Preferences.TimeHelper.getTodayTime() < gap) {
                mp.showpostpone = 1;
            }
            else {
                mp.showpostpone = 0;
            }
        }
        else {
            /* It may be refering to today */
            mp.title = 1;
            mp.referdate = Preferences.TimeHelper.getTodayDate();
            mp.showpostpone = 1;
        }

        /* Select the message set */
        if (preferences.getPersonStyle() == Preferences.PERSONSTYLE_SEDENTARY ||
                preferences.getPersonStyle() == Preferences.PERSONSTYLE_ACTIVE) {
            /* Profile one */
            mp.phraseset = 1;
        }
        else if (preferences.getPersonStyle() == Preferences.PERSONSTYLE_MODERATE ||
                preferences.getPersonStyle() == Preferences.PERSONSTYLE_INTENSE) {
            /* Profile two */
            mp.phraseset = 2;
        }

        /* TODO Set the limits of phrases: by formula */
        mp.minphrase = 1;
        mp.maxphrase = 2;

        /* Select the message set */
        if (preferences.isShowDaillyMessage()) {
            /* If the user wants to see dailly message */
            mp.showmessage = 1;
            mp.messageset = 0;
            mp.defmessage = 0;

            // TODO get which messagesubset to show: count how many time did the person answered
            mp.messagesubset = 0;
        }




        return mp;
    }

    public NotificationCompat.Builder getNotificationBuilderByContext() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.drawable.ic_stat);

        /* Result Action */
        Intent resultIntent = new Intent(context, MainService.class);
        resultIntent.putExtra(MainService.SWITCH, MainService.NOTIFICATIONACTION);
        resultIntent.putExtra(MainService.NOTIFICATIONACTION, MainService.NOTIFCATION_OPNEN);
        PendingIntent resultPendingIntent = PendingIntent.getService(context, MainService.NOTIFID_PIRESULT, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        /* Postpone Action */
        Intent postponeIntent = new Intent(context, MainService.class);
        postponeIntent.putExtra(MainService.SWITCH, MainService.NOTIFICATIONACTION);
        postponeIntent.putExtra(MainService.NOTIFICATIONACTION, MainService.NOTIFCATION_POSTPONE);
        PendingIntent postponePendingIntent = PendingIntent.getService(context, MainService.NOTIFID_PIPOSTPONE, postponeIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        /* Setting title and description */
        int id;

        if (preferences.getPersonStyle() == Preferences.PERSONSTYLE_NOTDEFINED) {
            id = 0;
        }
        else if (Preferences.TimeHelper.getTodayTime() < preferences.getNotificationTime()) {
            id = 2;

            long gap = (preferences.getNotificationTime() - preferences.getNotificationCleanGap() - preferences.getPostponeDelay());

            if (Preferences.TimeHelper.getTodayTime() < gap) {
                mBuilder.addAction(R.drawable.ic_device_access_time, "Lembrar mais tarde", postponePendingIntent);
            }

        }
        else {
            id = 1;
            mBuilder.addAction(R.drawable.ic_device_access_time, "Lembrar mais tarde", postponePendingIntent);
        }


        String title = context.getResources().getStringArray(R.array.titles)[id];
        String description = context.getResources().getStringArray(R.array.notifdesc)[id];

        if (preferences.isNotificationVibrate()) {
            mBuilder.setSound(Uri.parse(preferences.getNotificationSound()));
            mBuilder.setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS);
        }

        mBuilder.setContentTitle(title);
        mBuilder.setContentText(description);
        mBuilder.setContentIntent(resultPendingIntent);

        return mBuilder;
    }
}
