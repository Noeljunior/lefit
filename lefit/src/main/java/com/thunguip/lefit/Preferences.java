package com.thunguip.lefit;


import android.content.Context;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Preferences {
    public static final long SECOND = 1000L;
    public static final long MINUTE = SECOND * 60L;
    public static final long HOUR   = MINUTE * 60L;

    public static final int PERSONSTYLE_NOTDEFINED  = -1;
    public static final int PERSONSTYLE_SEDENTARY   =  0;
    public static final int PERSONSTYLE_ACTIVE      =  1;
    public static final int PERSONSTYLE_MODERATE    =  2;
    public static final int PERSONSTYLE_INTENSE     =  3;

    public static final int VERSIONINGVERIFICATION  =  1;

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
    public static final String  PREFS_PERSONSTYLE           = "MAINPREFES.PREFS_PERSONSTYLE";

    /* DEFAULTS */
    public static final boolean     DEF_fireNotifications       = true;
    public static final long        DEF_notificationTime        = TimeHelper.getByTimeCal(20, 0);
    public static final String      DEF_notificationSound       = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString();
    public static final boolean     DEF_notificationVibrate     = true;
    public static final boolean     DEF_showDaillyMessage       = true;

    public static final long    notificationInterval            = /*5 * 60 * 1000;*/ TimeHelper.getByTime(24, 0); /* TODO set to 24h */
    public static final long    postponeDelay                   =  /*1 * 60 * 1000;*/ TimeHelper.getByTime(1, 0); /* TODO set to 1h */
    public static final long    notificationCleanGap            = postponeDelay * 2;

    public static final int     DEF_personStyle                 = PERSONSTYLE_NOTDEFINED;

    private Context context;
    private SharedPreferences settings;

    public Preferences(Context context) {
        this.context = context;
        this.settings = context.getSharedPreferences(PREFS_NAME, PREFS_MODE);
    }




    /* Getters */
    public long getStartDate() {
        if (settings.contains(PREFS_STARTDATE)) {
            return settings.getLong(PREFS_STARTDATE, TimeHelper.getTodayDate());
        }
        else { /* return the default */
            return TimeHelper.getTodayDate();
        }
    }

    public boolean isFireNotifications() {
        if (settings.contains(PREFS_FIRENOTIFICATIONS)) {
            return settings.getBoolean(PREFS_FIRENOTIFICATIONS, DEF_fireNotifications);
        }
        else { /* return the default */
            return setFireNotifications(DEF_fireNotifications);
        }
    }

    public long getNotificationTime() {
        if (settings.contains(PREFS_NOTIFICATIONTIME)) {
            return settings.getLong(PREFS_NOTIFICATIONTIME, DEF_notificationTime);
        }
        else { /* return the default */
            return setNotificationTime(DEF_notificationTime);
        }
    }

    public String getNotificationSound() {
        if (settings.contains(PREFS_NOTIFICATIONSOUND)) {
            return settings.getString(PREFS_NOTIFICATIONSOUND, DEF_notificationSound);
        }
        else { /* return the default */
            return setNotificationSound(DEF_notificationSound);
        }
    }

    public boolean isNotificationVibrate() {
        if (settings.contains(PREFS_NOTIFICATIONVIBRATE)) {
            return settings.getBoolean(PREFS_NOTIFICATIONVIBRATE, DEF_notificationVibrate);
        }
        else { /* return the default */
            return setNotificationVibrate(DEF_notificationVibrate);
        }
    }

    public boolean isShowDaillyMessage() {
        if (settings.contains(PREFS_SHOWDAILLYMESSAGE)) {
            return settings.getBoolean(PREFS_SHOWDAILLYMESSAGE, DEF_showDaillyMessage);
        }
        else { /* return the default */
            return setShowDaillyMessage(DEF_showDaillyMessage);
        }
    }

    public int getPersonStyle() {
        if (settings.contains(PREFS_PERSONSTYLE)) {
            return settings.getInt(PREFS_PERSONSTYLE, DEF_personStyle);
        }
        else { /* return the default */
            return setPersonStyle(DEF_personStyle);
        }
    }

    public long getNotificationInterval() {
        return notificationInterval;
    }

    public long getPostponeDelay() {
        return postponeDelay;
    }

    public long getNotificationCleanGap() {
        return notificationCleanGap;
    }


    /* Setters */
    public long setStartDate(long l) {
        commitPrefLong(PREFS_STARTDATE, l);
        return l;
    }
    public boolean setFireNotifications(boolean b) {
        commitPrefBoolean(PREFS_FIRENOTIFICATIONS, b);
        return b;
    }
    public long setNotificationTime(long l) {
        commitPrefLong(PREFS_NOTIFICATIONTIME, l);
        return l;
    }
    public String setNotificationSound(String s) {
        commitPrefString(PREFS_NOTIFICATIONSOUND, s);
        return s;
    }
    public boolean setNotificationVibrate(boolean b) {
        commitPrefBoolean(PREFS_NOTIFICATIONVIBRATE, b);
        return b;
    }
    public boolean setShowDaillyMessage(boolean b) {
        commitPrefBoolean(PREFS_SHOWDAILLYMESSAGE, b);
        return b;
    }

    public int setPersonStyle(int i) {
        commitPrefInt(PREFS_PERSONSTYLE, i);
        return i;
    }



    public void reset() {
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.commit();

        if (MainService.isDebuggable(context))
            Toast.makeText(context, "Preferences were resetted", Toast.LENGTH_SHORT).show();
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
            return hour * HOUR + minute * MINUTE;
        }

        public static long getByTime(int hour, int minute, int second) {
            return getByTime(hour, minute) + second * SECOND;
        }

        public static long getByTimeCal(int hour, int minute) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(0);
            cal.set(Calendar.HOUR_OF_DAY, hour);
            cal.set(Calendar.MINUTE, minute);
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

        public static long getTodayTime() {
            Calendar now = Calendar.getInstance();
            Calendar nowClean = Calendar.getInstance();
            nowClean.setTimeInMillis(0);

            nowClean.set(Calendar.HOUR_OF_DAY, now.get(Calendar.HOUR_OF_DAY));
            nowClean.set(Calendar.MINUTE, now.get(Calendar.MINUTE));

            return nowClean.getTimeInMillis();
        }

        public static long getTodayDateTime() {
            Calendar now = Calendar.getInstance();

            now.set(Calendar.MILLISECOND, 0);

            return now.getTimeInMillis();
        }

        public static long getYesterdayDate() {
            Calendar yesterday = Calendar.getInstance();
            yesterday.setTimeInMillis(getTodayDate());
            yesterday.add(Calendar.DAY_OF_MONTH, -1);
            return yesterday.getTimeInMillis();
        }


        public static long getNextByTime (Calendar time) {
            Calendar now = Calendar.getInstance();
            now.set(Calendar.SECOND, 0);
            now.set(Calendar.MILLISECOND, 0);

            Calendar target = Calendar.getInstance();
            target.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
            target.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
            target.set(Calendar.SECOND, 0);
            target.set(Calendar.MILLISECOND, 0);

            if (target.getTimeInMillis() <= now.getTimeInMillis()) {
                target.add(Calendar.DAY_OF_MONTH, 1);
            }

            return target.getTimeInMillis();
        }

        public static long getNextByTime(long time) {
            Calendar timecal = Calendar.getInstance();
            timecal.setTimeInMillis(time);
            return getNextByTime(timecal);
        }

        public static String toString(Calendar cal) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss, dd/MM/yyyy");
            return dateFormat.format(cal.getTime());
        }

        public static String toString(long l) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(l);
            return toString(cal);
        }

        public static boolean isSameDay(Calendar a, Calendar b) {
            return (a.get(Calendar.YEAR) == b.get(Calendar.YEAR)) &&
                    (a.get(Calendar.MONTH) == b.get(Calendar.MONTH)) &&
                    (a.get(Calendar.DAY_OF_MONTH) == b.get(Calendar.DAY_OF_MONTH));
        }

        public static boolean isSameDay(long a, long b) {
            Calendar ca = Calendar.getInstance();
            Calendar cb = Calendar.getInstance();
            ca.setTimeInMillis(a);
            cb.setTimeInMillis(b);
            return isSameDay(ca, cb);
        }
    }
}
