package com.thunguip.lefit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;


public class LauncherActivity extends ActionBarActivity {
    private static final int ACTRES_SELECTSOUND = 1000;

    private Preferences preferences;

    private LvCalendarAdaptor lvcalendaradaptor;
    private ListView listView;
    //public static final


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        lvcalendaradaptor = new LvCalendarAdaptor(this);


        listView = (ListView)findViewById(R.id.lvcalendar);
        listView.setAdapter(lvcalendaradaptor);
        listView.setOnItemClickListener(ListViewOnItemClickedListner);

        requestLvItems();

        preferences = new Preferences(this);

        MainService.sendIntent(this, MainService.CHECKALARMSETTINGS);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(setLvItemsReceiver, new IntentFilter(MainService.BROADCAST));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(setLvItemsReceiver);
    }

    public void getItems(View view) {
        requestLvItems();
    }


    public void testBehave(View view) {
        requestLvItems();
    }


    public void requestLvItems() {
        Intent intent = new Intent(this, MainService.class);
        intent.putExtra(MainService.MESSENGER, new Messenger(handler));
        intent.putExtra(MainService.SWITCH, MainService.GETLVITEMS);
        startService(intent);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle data = msg.getData();

            if (data.getString(MainService.SWITCH) == null) {
                // Unknown intent
                return;
            }

            switch (data.getString(MainService.SWITCH)) {
                case MainService.GETLVITEMS:
                    LvItemParcel item[] = (LvItemParcel[]) data.getParcelableArray(MainService.GETLVITEMS);

                    ArrayList<LvItemParcel> lvar = new ArrayList<>(Arrays.asList(item));

                    lvcalendaradaptor.setItems(lvar);
                    lvcalendaradaptor.notifyDataSetChanged();
                    return;

                default:
                    return;
            }


        }
    };

    AdapterView.OnItemClickListener ListViewOnItemClickedListner = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            LvItemParcel clickedItem = (LvItemParcel) listView.getAdapter().getItem(position);

            MainService.sendIntent(v.getContext(), MainService.INVOKEPOPUPBYLVITEM, clickedItem.referdate);

            /*Intent intent = new Intent(v.getContext(), MainService.class);
            intent.putExtra(MainService.SWITCH, MainService.INVOKEPOPUPBYLVITEM);
            intent.putExtra(MainService.INVOKEPOPUPBYLVITEM, clickedItem.referdate);
            startService(intent);*/
        }
    };

    public void testBroadcast(View view) {
        /*Intent intent = new Intent(this, MainService.class);
        intent.putExtra(MainService.SWITCH, MainService.DEBUG);
        startService(intent);*/

        StorageDB db = new StorageDB(this);

        Calendar initCalendar = Calendar.getInstance();
        initCalendar.set(Calendar.YEAR, 2014);
        initCalendar.set(Calendar.MONTH, 4);
        initCalendar.set(Calendar.DAY_OF_MONTH, 25);

        //int title,
        //int phraseset, int phrasemin, int phrasemax, int phraseanswer, int phrasehitmore, int phrasehitless,
        // int messageset, int messagesubset, int messagehide, int messagemore,
        // int action,
        // int daterefer, int dateinit, int dateaction
        db.addEntry(new PopupEntryParcel(1,
                2, 2, 8, 4, 0, 0,
                0, 0, 0, 0,
                PopupEntryParcel.POPUP_ACTION_SUBMIT,
                initCalendar.getTimeInMillis(), initCalendar.getTimeInMillis(), initCalendar.getTimeInMillis()));
    }

    private BroadcastReceiver setLvItemsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("LauncherActivity", "NEW BROADCAST RECEIVED");
            if (intent.getStringExtra(MainService.BC_SWITCH) == null) {
                // Unkown intent
                Log.d("LauncherActivity", "NULL BROADCAST INTENT");
                return;
            }

            switch (intent.getStringExtra(MainService.BC_SWITCH)) {
                case MainService.BC_UPDATEITEMS:
                    requestLvItems();
                    return;

                default:
                    return;
            }

        }
    };




    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent)
    {
        if (resultCode == Activity.RESULT_OK && requestCode == ACTRES_SELECTSOUND) {
            Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

            if (uri != null) {
                preferences.setNotificationSound(uri.toString());

                Log.d("LauncherActivity", "Ringtone selected: " + uri);
            }
            else {
                preferences.setNotificationSound(uri.toString());

                Log.d("LauncherActivity", "Ringtone selected: NULL");
            }
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.launcher, menu);

        // Notifications
        MenuItem menunotifications =  menu.findItem(R.id.menunotifications);
        MenuItem menusound = menu.findItem(R.id.menusound);
        MenuItem menuvibrate = menu.findItem(R.id.menuvibrate);
        MenuItem menutime = menu.findItem(R.id.menutime);

        menunotifications.setChecked(preferences.isFireNotifications());
        menunotifications.setIcon(preferences.isFireNotifications() ? R.drawable.ic_action_bellon : R.drawable.ic_action_belloff);
        menusound.setVisible(preferences.isFireNotifications());
        menuvibrate.setVisible(preferences.isFireNotifications());
        menuvibrate.setChecked(preferences.isNotificationVibrate());
        menutime.setVisible(preferences.isFireNotifications());

        // Dailly message
        MenuItem menumessage =  menu.findItem(R.id.menumessage);
        menumessage.setChecked(preferences.isShowDaillyMessage());


        if (( 0 != ( getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE ) )) {
            MenuItem menudebug =  menu.findItem(R.id.menudebug);
            menudebug.setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;

            case R.id.menumessage:
                preferences.setShowDaillyMessage(!item.isChecked());

                this.invalidateOptionsMenu();
                return true;

            case R.id.menunotifications:
                preferences.setFireNotifications(!item.isChecked());



                /*if (preferences.isFireNotifications()) {
                    Intent intent = new Intent(this, MainService.class);
                    intent.putExtra(MainService.SWITCH, MainService.ENABLENOTIFICATIONS);
                    startService(intent);
                }
                else {
                    Intent intent = new Intent(this, MainService.class);
                    intent.putExtra(MainService.SWITCH, MainService.DISABLENOTIFICATIONS);
                    startService(intent);
                }*/

                MainService.sendIntent(this, MainService.CHECKALARMSETTINGS);

                this.invalidateOptionsMenu();
                return true;
            case R.id.menusound:
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Som de notificação");
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(preferences.getNotificationSound()));

                startActivityForResult(intent, ACTRES_SELECTSOUND);

                return true;
            case R.id.menuvibrate:
                preferences.setNotificationVibrate(!item.isChecked());

                this.invalidateOptionsMenu();
                return true;
            case R.id.menutime:
                DialogFragment newFragment = new DialogTimePicker();

                newFragment.show(getFragmentManager(), "timePicker");
                return true;



            case R.id.menudebug:
                startActivity(new Intent(this, DebugActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* TODO solve this pretty shit time picker dialog thing */
    @SuppressLint("ValidFragment")
    public class DialogTimePicker extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(preferences.getNotificationTime());

            return new TimePickerDialog(getActivity(), TimePickerDialog.THEME_HOLO_DARK, this,
                    c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),
                    DateFormat.is24HourFormat(getActivity()));
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Log.d("InnerTimePicker", "TIME: " + hourOfDay + ":" + minute);

            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(0);
            c.set(Calendar.HOUR_OF_DAY, hourOfDay);
            c.set(Calendar.MINUTE, minute);

            preferences.setNotificationTime(c.getTimeInMillis());
            //preferences.setNotificationTime(Preferences.TimeHelper.getByTime(hourOfDay, minute));

            MainService.sendIntent(getActivity(), MainService.CHECKALARMSETTINGS);

        }
    }


}
