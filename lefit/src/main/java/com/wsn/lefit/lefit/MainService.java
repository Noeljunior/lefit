package com.wsn.lefit.lefit;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;


public class MainService extends IntentService {
    private int number;

    public static final String DOCHECK = "com.wsn.lefit.lefit.extra.DOCHECK";
    public static final String DOSETALARM = "com.wsn.lefit.lefit.extra.DOSETALARM";
    public static final String DOUNSETALARM = "com.wsn.lefit.lefit.extra.DOUNSETALARM";

    public static final String SWITCH = "com.wsn.lefit.lefit.mainservice.SWITCH";
    public static final String MESSENGER = "com.wsn.lefit.lefit.mainservice.MESSENGER";

    public static final String GETLVITEMS = "com.wsn.lefit.lefit.mainservice.SWITCH.GETLVITEMS";

    public MainService() {
        super("MainService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //number = 0;
        //Toast.makeText(this, "Service created: " + number, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent.getStringExtra(SWITCH) == null) {
            // Unknown intent
            return;
        }

        switch (intent.getStringExtra(SWITCH)) {
            case GETLVITEMS:
                sendLvItems((Messenger) intent.getExtras().get(MESSENGER));
                break;

            default:
                return;
        }


        //Toast.makeText(this, "New intent received: " + intent.getStringExtra(DOCHECK), Toast.LENGTH_SHORT).show();
        /*if (intent != null) {
            final String action = intent.getAction();
            //if (CHECK.equals(action)) {
                final String param1 = intent.getStringExtra(DOCHECK);
                handleActionCheck(param1);
            //}
        }*/

        /*final String cenas = intent.getStringExtra(DOCHECK);

        Handler mHandler = new Handler(getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "New intent received: " + cenas, Toast.LENGTH_SHORT).show();
            }
        });*/


        /*if (intent.getStringExtra(DOCHECK) != null) {
            // If it is to check what to do
            checkIfSendsANotification();
        }
        else if (intent.getStringExtra(DOSETALARM) != null) {
            // If it is to set the alamarm

            // TODO check if there is already an alarmer and stop it

            // Setting up new alarmer
            new Alarmer().setAlarm(MainService.this, System.currentTimeMillis(), 1000 * 10 * 1);

        }
        else if (intent.getStringExtra(DOUNSETALARM) != null) {
            // If it is to unset the alamarm

            // Unset the alarm, if any
            new Alarmer().cancelAlarm(MainService.this);

        }
        else {
            // Dunno what to do :/

        }*/





    }

    private void sendLvItems(Messenger messenger) {
        Message msg = Message.obtain();
        Bundle data = new Bundle();

        //data.putString(GETLVITEMS, "TESTING");


        LvItemParcel item = new LvItemParcel(LvItemParcel.Type.SEPRATOR, "Separator test");

        data.putParcelable(GETLVITEMS, item);

        msg.setData(data);
        try {
            messenger.send(msg);
        } catch (RemoteException e) {
            Log.i("ERROR", "Error send lv items back");
        }
    }



    private void checkIfSendsANotification() {
        /* Go check if its time to send notification */


        /* So, its time to send notification */

        /* Pick a message to send */
        MessageParcel m = new MessageParcel("Como é o seu estilo de vida?",
                new String[] {"Sou sedentário", "Sou activo mas não pratico exercício", "Faço algum exercicio", "Faço muito"},
                new int[] {R.drawable.ic_phraseicon_0, R.drawable.ic_phraseicon_1, R.drawable.ic_phraseicon_2, R.drawable.ic_phraseicon_3},
                new String[] {"A vida é bela", "Faça exercicio fisico!", "Lamba-me o escroto"},
                2, 0,
                1);

        sendNotification(m);
    }


    private void sendNotification(MessageParcel m) {
        NotificationCompat.Builder mBuilder;
        mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat)
                .setContentTitle("Como foi o seu dia?")
                .setContentText("Toque para registar como foi o seu dia.")
                .addAction (R.drawable.ic_navigation_cancel,
                        "Dispensar", null)
                .addAction (R.drawable.ic_device_access_time,
                        "Adiar", null);

        Intent resultIntent = new Intent(this, PopupActivity.class);

        resultIntent.putExtra("msg", m);


        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(PopupActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        /* TODO: do something with mId : 0  */
        mNotificationManager.notify(0, mBuilder.build());
    }
}
