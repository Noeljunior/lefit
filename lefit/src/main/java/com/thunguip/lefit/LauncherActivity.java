package com.thunguip.lefit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
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

import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;


public class LauncherActivity extends ActionBarActivity {
    private static final int ACTRES_SELECTSOUND = 1000;

    private static final String KEY_LVPOSTION = "KEY_LVPOSTION";
    private static final String KEY_LVOFFSET  = "KEY_LVOFFSET";

    private Preferences preferences;

    private LvCalendarAdaptor lvcalendaradaptor;
    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);


        lvcalendaradaptor = new LvCalendarAdaptor(this);
        listView = (ListView)findViewById(R.id.lvcalendar);
        listView.setAdapter(lvcalendaradaptor);
        listView.setOnItemClickListener(ListViewOnItemClickedListner);

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setTintResource(R.color.actionbar_bg);

        requestLvItems();

        preferences = new Preferences(this);

        MainService.sendIntent(this, MainService.CHECKALARMSETTINGS);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(setLvItemsReceiver, new IntentFilter(MainService.BROADCAST));
        requestLvItems();
        this.invalidateOptionsMenu();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(setLvItemsReceiver);
        }
        catch (java.lang.IllegalArgumentException e) {
            /* Already unregistred */
            Log.d("LauncherActivity","EXCEPTION: Already Unregistered");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(setLvItemsReceiver);
        }
        catch (java.lang.IllegalArgumentException e) {
            /* Already unregistred */
            Log.d("LauncherActivity","EXCEPTION: Already Unregistered");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        View v = listView.getChildAt(0);

        savedInstanceState.putInt       (KEY_LVPOSTION, listView.getFirstVisiblePosition());
        savedInstanceState.putInt       (KEY_LVOFFSET, (v == null) ? 0 : v.getTop());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        listView.setSelectionFromTop(savedInstanceState.getInt(KEY_LVPOSTION),
                savedInstanceState.getInt(KEY_LVOFFSET));

    }

    public void requestLvItems() {
        MainService.sendIntent(this, MainService.GETLVITEMS, new Messenger(handler));
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
        }
    };



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
                preferences.setNotificationSound("");

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

                MainService.sendIntent(this, MainService.CHECKALARMSETTINGS);

                this.invalidateOptionsMenu();
                return true;
            case R.id.menusound:
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Som de notificação");
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
                        preferences.getNotificationSound().equals("") ? null : Uri.parse(preferences.getNotificationSound()));

                startActivityForResult(intent, ACTRES_SELECTSOUND);

                return true;
            case R.id.menuvibrate:
                preferences.setNotificationVibrate(!item.isChecked());

                this.invalidateOptionsMenu();
                return true;
            case R.id.menutime:
                /*DialogFragment newFragment = new DialogTimePicker();

                newFragment.show(getFragmentManager(), "timePicker");*/

                openTimePicker();
                return true;



            case R.id.menudebug:
                startActivity(new Intent(this, DebugActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private void openTimePicker() {
        // TODO change this theme to LeFitBlue

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(preferences.getNotificationTime());

        final TimePicker timePicker = new TimePicker(this);
        //timePicker.setScrollBarStyle(R.style.LeFitTimePicker);
        timePicker.setIs24HourView(DateFormat.is24HourFormat(this));
        timePicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
        timePicker.setCurrentMinute(c.get(Calendar.MINUTE));
        final Context thiscontext = this;

        //ContextThemeWrapper ctw = new ContextThemeWrapper(this, R.style.AlertDialogStyle);

        new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT)
                .setTitle("Hora da notificação")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("LauncherActivity", "TimePicker OK: " + timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute());

                        Calendar c = Calendar.getInstance();
                        c.setTimeInMillis(0);
                        c.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
                        c.set(Calendar.MINUTE, timePicker.getCurrentMinute());

                        preferences.setNotificationTime(c.getTimeInMillis());

                        MainService.sendIntent(thiscontext, MainService.CHECKALARMSETTINGS);

                    }
                })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("LauncherActivity", "TimePicker CANCEL: " + timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute());
                            }
                        })
                .setView(timePicker)



                .show();
    }

}
