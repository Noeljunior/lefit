package com.wsn.lefit.lefit;

import android.app.ActionBar;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.MenuItem;
import android.view.View;


public class DebugActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        ActionBar actionBar = getActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
    }





    public void showDialog(View view) {
        Intent intent = new Intent(this, PopupActivity.class);

        Message m = new Message("Como é o seu estilo de vida?",
                new String[] {"Sou sedentário", "Sou activo mas não pratico exercício", "Faço algum exercicio", "Faço muito"},
                new int[] {R.drawable.ic_phraseicon_0, R.drawable.ic_phraseicon_1, R.drawable.ic_phraseicon_2, R.drawable.ic_phraseicon_3},
                new String[] {"A vida é bela", "Faça exercicio fisico!", "Lamba-me o escroto"},
                2, 0,
                1);

        intent.putExtra("msg", m);

        startActivity(intent);

    }

    public void checkService(View view) {
        Intent mServiceIntent = new Intent(this, MainService.class);

        mServiceIntent.putExtra(MainService.DOCHECK, "");
        this.startService(mServiceIntent);
    }
    public void setalarmService(View view) {
        Intent mServiceIntent = new Intent(this, MainService.class);

        mServiceIntent.putExtra(MainService.DOSETALARM, "");
        this.startService(mServiceIntent);
    }
    public void unsetalarmService(View view) {
        Intent mServiceIntent = new Intent(this, MainService.class);

        mServiceIntent.putExtra(MainService.DOUNSETALARM, "");
        this.startService(mServiceIntent);
    }

    public void showNotification(View view) {
        NotificationCompat.Builder mBuilder;
        mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat)
                .setContentTitle("Como foi o seu dia?")
                .setContentText("Toque para registar como foi o seu dia.")
                .addAction (R.drawable.ic_navigation_cancel,
                        "Dispensar", null)
                .addAction (R.drawable.ic_device_access_time,
                        "Adiar", null);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, PopupActivity.class);


        Message m = new Message("Como é o seu estilo de vida?",
                new String[] {"Sou sedentário", "Sou activo mas não pratico exercício", "Faço algum exercicio", "Faço muito"},
                new int[] {R.drawable.ic_phraseicon_0, R.drawable.ic_phraseicon_1, R.drawable.ic_phraseicon_2, R.drawable.ic_phraseicon_3},
                new String[] {"A vida é bela", "Faça exercicio fisico!", "Lamba-me o escroto"},
                2, 0,
                1);

        resultIntent.putExtra("msg", m);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(PopupActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(0, mBuilder.build());
    }
    
    
    

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.debug, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
