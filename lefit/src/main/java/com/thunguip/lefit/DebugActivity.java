package com.thunguip.lefit;

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

        /*MessageParcel m = new MessageParcel("Como é o seu estilo de vida?",
                new String[] {"Sou sedentário", "Sou activo mas não pratico exercício", "Faço algum exercicio", "Faço muito"},
                new int[] {R.drawable.ic_phraseicon_0, R.drawable.ic_phraseicon_1, R.drawable.ic_phraseicon_2, R.drawable.ic_phraseicon_3},
                new String[] {"A vida é bela", "Faça exercicio fisico!", "Lamba-me o escroto"},
                2, 0,
                1);*/

        MessageParcel m = new MessageParcel(1,
                2, 3, 4, 3,
                1, 0, 0, 1,
                0, 0);

        intent.putExtra("msg", m);

        startActivity(intent);

    }

    /*public void checkService(View view) {
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
    }*/

    public void showNotification(View view) {
        Intent intent = new Intent(this, MainService.class);
        intent.putExtra(MainService.SWITCH, MainService.FIRENOTIFICATION);
        startService(intent);
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


    public void WriteItem(View view) {
        StorageDB db = new StorageDB(this);

        //db.addDailyRow(69, "hell yeah");



    }

    public void ReadItems(View view) {
        StorageDB db = new StorageDB(this);

        //db.readDaily();

    }





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
