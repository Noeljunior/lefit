package com.thunguip.lefit;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
    


    public void WriteItem(View view) {
        //StorageDB db = new StorageDB(this);

        //db.addDailyRow(69, "hell yeah");

        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Som de notificação");
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
        this.startActivityForResult(intent, 5);

    }

    public void ReadItems(View view) {
        StorageDB db = new StorageDB(this);

        //db.readDaily();

    }



    public void setalarmService(View view) {
        /*Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 5);

        AlarmerManager.setAlarm(this, MainService.ALARMID_REPEATED, cal.getTimeInMillis());*/

        Intent intent = new Intent(this, MainService.class);
        intent.putExtra(MainService.SWITCH, MainService.ENABLENOTIFICATIONS);
        startService(intent);

        Log.d("DebugActivity", "SETTING ALLARM OK");

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
