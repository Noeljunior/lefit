package com.thunguip.lefit;


import android.content.Context;
import android.media.RingtoneManager;

import java.util.Calendar;

public class Preferences {
    /* DEFAULTS */
    public static final boolean     DEF_fireNotifications       = true;
    public static final long        DEF_notificationTime        = TimeHelper.getByTime(20, 0);  /* TODO set to 20h */
    public static final String      DEF_notificationSound       = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString();
    public static final boolean     DEF_notificationVibrate     = true;
    public static final boolean     DEF_showDaillyMessage       = true;

    public static final long    notificationInterval            = TimeHelper.getByTimeSeconds(0, 5, 0); /* TODO set to 24h */
    public static final long    postponeDelay                   = TimeHelper.getByTimeSeconds(0, 1, 0); /* TODO set to 1h */
    public static final long    notificationCleanGap            = postponeDelay * 2;

    private Context context;

    public Preferences(Context context) {
        this.context = context;
    }


    public long getNotificationTime() {
        if (false) { /* TODO go check the preferences */

            return 0;
        }
        else { /* return the defaul */

            /* DUMMY */
            Calendar cal = Calendar.getInstance();
            //cal.setTimeInMillis(0);
            cal.set(Calendar.HOUR, Calendar.getInstance().get(Calendar.HOUR));
            cal.set(Calendar.MINUTE, Calendar.getInstance().get(Calendar.MINUTE));
            cal.set(Calendar.SECOND, Calendar.getInstance().get(Calendar.SECOND));
            cal.add(Calendar.SECOND, 15);
            return cal.getTimeInMillis();
            /* DUMMY */

            //return DEF_notificationTime;
        }
    }

    /* Getters */

    public long getNotificationInterval() {
        if (false) { /* TODO go check the preferences */

            return 0;
        }
        else { /* return the defaul */
            return notificationInterval;
        }
    }

    public long getPostponeDelay() {
        if (false) { /* TODO go check the preferences */

            return 0;
        }
        else { /* return the defaul */
            return postponeDelay;
        }
    }

    public long getNotificationCleanGap() {
        if (false) { /* TODO go check the preferences */

            return 0;
        }
        else { /* return the defaul */
            return notificationCleanGap;
        }
    }

    public boolean isFireNotifications() {
        if (false) { /* TODO go check the preferences */

            return true;
        }
        else { /* return the defaul */
            return DEF_fireNotifications;
        }
    }

    public String getNotificationSound() {
        if (false) { /* TODO go check the preferences */

            return "";
        }
        else { /* return the defaul */
            return DEF_notificationSound;
        }
    }

    public boolean isNotificationVibrate() {
        if (false) { /* TODO go check the preferences */

            return true;
        }
        else { /* return the defaul */
            return DEF_notificationVibrate;
        }
    }

    public boolean isShowDaillyMessage() {
        if (false) { /* TODO go check the preferences */

            return true;
        }
        else { /* return the defaul */
            return DEF_showDaillyMessage;
        }
    }

    public long getStartDate() {
        if (false) { /* TODO go check the preferences */

            return 0;
        }
        else { /* TODO return today's date */
            return TimeHelper.getByDate(2014, 4, 20);/*TimeHelper.getTodayDate();*/
        }
    }

    /* Setters */



    /* Helpers */

    public abstract static class TimeHelper {
        public static long getNow() {
            Calendar cal = Calendar.getInstance();
            //cal.set(Calendar.MILLISECOND, 0);
            return cal.getTimeInMillis();
        }

        public static long getByTime(int hour, int minute) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(0);
            cal.add(Calendar.HOUR, hour);
            cal.add(Calendar.MINUTE, minute);

            return cal.getTimeInMillis();
        }

        public static long getByTimeSeconds(int hour, int minute, int second) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(0);
            cal.add(Calendar.HOUR, hour);
            cal.add(Calendar.MINUTE, minute);
            cal.add(Calendar.SECOND, second);

            return cal.getTimeInMillis();
        }

        public static long getByDate(int year, int month, int day) {
            Calendar now = Calendar.getInstance();
            now.setTimeInMillis(0);

            now.set(Calendar.YEAR, year);
            now.set(Calendar.MONTH, month);
            now.set(Calendar.DAY_OF_MONTH, day);

            return now.getTimeInMillis();
        }

        public static long getTodayDate() {
            Calendar now = Calendar.getInstance();
            Calendar nowClean = Calendar.getInstance();
            nowClean.setTimeInMillis(0);

            nowClean.set(Calendar.YEAR, now.get(Calendar.YEAR));
            nowClean.set(Calendar.MONTH, now.get(Calendar.MONTH));
            nowClean.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH));

            return nowClean.getTimeInMillis();
        }
    }
}
