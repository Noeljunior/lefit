package com.thunguip.lefit;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.Calendar;

public class DebugActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        ActionBar actionBar = getActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setTintResource(R.color.actionbar_bg);
    }





    public void showDialog(View view) {
        MainService.sendIntent(this, MainService.INVOKEPOPUPBYLVITEM, Preferences.TimeHelper.getTodayDate());
    }

    public void clearAllDb(View view) {
        new StorageDB(this).resetDataBase();

    }

    public void setStartDate(View view) {
        DialogFragment newFragment = new DialogDatePicker();

        newFragment.show(getFragmentManager(), "datePicker");


    }


    public void resetPreferences(View view) {
        new Preferences(this).reset();
    }

    public void showNotification(View view) {
        MainService.sendIntent(this, MainService.ALARM, 1);
    }
    


    public void updateNotif(View view) {
        MainService.sendIntent(this, MainService.ALARM, MainService.ALARMID_RENOTIF);

    }

    public void sendItems(View view) {
        BackgroundService.sendIntent(this, BackgroundService.UPLOADITEMS);

    }




    public void openTimepicker(View view) {

        final TimePicker timePicker = new TimePicker(this);
        timePicker.setIs24HourView(true);
        timePicker.setCurrentHour(20);
        timePicker.setCurrentMinute(15);

        new AlertDialog.Builder(this)
                .setTitle("Hora da notificação")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("Picker", timePicker.getCurrentHour() + ":"
                                + timePicker.getCurrentMinute());
                    }
                })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                Log.d("Picker", "Cancelled!");
                            }
                        }).setView(timePicker).show();

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






    public class DialogDatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(new Preferences(getActivity()).getStartDate());

            return new DatePickerDialog(getActivity(), DatePickerDialog.THEME_HOLO_DARK,this,
                    c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Log.d("InnerDatePicker", "DATE: " + dayOfMonth + "/" + monthOfYear + "/" + year);

            new Preferences(getActivity()).setStartDate(Preferences.TimeHelper.getByDate(year, monthOfYear, dayOfMonth));
        }
    }
}
