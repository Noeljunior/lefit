package com.thunguip.lefit;


import android.app.AlarmManager;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.Log;

import java.util.Calendar;

public class Decision {
    private Context context;
    private Preferences preferences;

    public static final int CONTEXT_NOTIFICATION = 0;
    public static final int CONTEXT_POSTPONEDNOTIFICATION = 1;
    public static final int CONTEXT_LVITEM = 2;


    public Decision(Context context) {
        this.context = context;

        preferences = new Preferences(context);
    }

    public MessageParcel getNotificationTodaysMessage(int clickedcontext, long refer) {
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

            Log.d("Decision", "TIME: " + Preferences.TimeHelper.toString(preferences.getNotificationTime()) +
                    "; GAP: " + Preferences.TimeHelper.toString(preferences.getNotificationCleanGap()) +
                    "; PDELAY: " + Preferences.TimeHelper.toString(preferences.getPostponeDelay()) +
                    "; SUM: " + Preferences.TimeHelper.toString(gap) +
                    "; NOW: " + Preferences.TimeHelper.toString(Preferences.TimeHelper.getTodayTime()));

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
}
