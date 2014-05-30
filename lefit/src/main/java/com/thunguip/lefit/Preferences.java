package com.thunguip.lefit;


import android.content.Context;
import android.content.SharedPreferences;
import android.media.RingtoneManager;

import java.util.Calendar;

public class Preferences {
    /* Prefrences File */
    public static final String  PREFS_NAME                  = "MainPrefs";
    public static final int     PREFS_MODE                  = Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS;
    /* Preferences Keys */
    public static final String  PREFS_STARTDATE             = "MAINPREFES.PREFS_STARTDATE";
    public static final String  PREFS_FIRENOTIFICATIONS     = "MAINPREFES.PREFS_FIRENOTIFICATIONS";
    public static final String  PREFS_NOTIFICATIONTIME      = "MAINPREFES.PREFS_NOTIFICATIONTIME";
    public static final String  PREFS_NOTIFICATIONSOUND     = "MAINPREFES.PREFS_NOTIFICATIONSOUND";
    public static final String  PREFS_NOTIFICATIONVIBRATE   = "MAINPREFES.PREFS_NOTIFICATIONVIBRATE";
    public static final String  PREFS_SHOWDAILLYMESSAGE     = "MAINPREFES.PREFS_SHOWDAILLYMESSAGE";

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
    private SharedPreferences settings;

    public Preferences(Context context) {
        this.context = context;
        this.settings = context.getSharedPreferences(PREFS_NAME, PREFS_MODE);
    }




    /* Getters */

    public long getStartDate() {
        if (false) { /* TODO go check the preferences */

            return settings.getLong(PREFS_STARTDATE, TimeHelper.getTodayDate());
        }
        else { /* TODO return today's date */
            return TimeHelper.getByDate(2014, 4, 20);/*TimeHelper.getTodayDate();*/
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


    /* Setters */
    private void setStartDate(long l) {
        commitPrefLong(PREFS_STARTDATE, l);
    }
    public void setFireNotifications(boolean b) {
        commitPrefBoolean(PREFS_FIRENOTIFICATIONS, b);
    }
    public void setNotificationTime(long l) {
        commitPrefLong(PREFS_NOTIFICATIONTIME, l);
    }
    public void setNotificationSound(String s) {
        commitPrefString(PREFS_NOTIFICATIONSOUND, s);
    }
    public void setNotificationVibrate(boolean b) {
        commitPrefBoolean(PREFS_NOTIFICATIONVIBRATE, b);
    }
    public void setShowDaillyMessage(boolean b) {
        commitPrefBoolean(PREFS_SHOWDAILLYMESSAGE, b);
    }



    /* Helpers */

    /* Preferences Helpers */
    private void commitPrefBoolean(String key, boolean b) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, b);
        editor.commit();
    }
    private void commitPrefString(String key, String s) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, s);
        editor.commit();
    }
    private void commitPrefInt(String key, int i) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, i);
        editor.commit();
    }
    private void commitPrefLong(String key, long l) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(key, l);
        editor.commit();
    }

    /* TimeDate Helper */
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
