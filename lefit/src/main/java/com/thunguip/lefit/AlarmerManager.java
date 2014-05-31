package com.thunguip.lefit;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;


public class AlarmerManager extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();

        /*Intent si = new Intent(context, MainService.class);
        si.putExtra(MainService.SWITCH, MainService.ALARM);
        si.putExtra(MainService.ALARM, intent.getIntExtra(MainService.ALARM, -1));
        context.startService(si);*/
        MainService.sendIntent(context, MainService.ALARM, intent.getIntExtra(MainService.ALARM, -1));
        Log.d("AlarmerManager", "ALARMFIRED | ID: " + intent.getIntExtra(MainService.ALARM, -1));

        wl.release();
    }

    public static void setAlarm(Context context, int id, long trigger){
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, AlarmerManager.class);
        i.putExtra(MainService.ALARM, id);
        PendingIntent pi = PendingIntent.getBroadcast(context, id, i, PendingIntent.FLAG_CANCEL_CURRENT);
        am.set(AlarmManager.RTC_WAKEUP, trigger, pi);
    }

    public static void setRepeatingAlarm(Context context, int id, long trigger, long interval){
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, AlarmerManager.class);
        i.putExtra(MainService.ALARM, id);
        PendingIntent pi = PendingIntent.getBroadcast(context, id, i, PendingIntent.FLAG_CANCEL_CURRENT);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, trigger, interval, pi);
    }

    public static void setRelativeAlarm(Context context, int id, long trigger){
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, AlarmerManager.class);
        i.putExtra(MainService.ALARM, id);
        PendingIntent pi = PendingIntent.getBroadcast(context, id, i, PendingIntent.FLAG_CANCEL_CURRENT);
        am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, trigger, pi);
    }

    public static void setRelativeRepeatingAlarm(Context context, int id, long trigger, long interval){
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, AlarmerManager.class);
        i.putExtra(MainService.ALARM, id);
        PendingIntent pi = PendingIntent.getBroadcast(context, id, i, PendingIntent.FLAG_CANCEL_CURRENT);
        am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, trigger, interval, pi);
    }

    public static void cancelAlarm(Context context, int id) {
        Intent intent = new Intent(context, AlarmerManager.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
}
