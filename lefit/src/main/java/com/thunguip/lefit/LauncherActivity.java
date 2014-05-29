package com.thunguip.lefit;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;


public class LauncherActivity extends ActionBarActivity {
    private static final int ACTRES_SELECTSOUND = 1;


    private boolean shownotificationsmenus;
    private boolean showdaillymessage;

    private LvCalendarAdaptor lvcalendaradaptor;

    private ListView listView;
    //public static final


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        lvcalendaradaptor = new LvCalendarAdaptor(this);


        registerReceiver(setLvItemsReceiver, new IntentFilter(MainService.BROADCAST));


        listView = (ListView)findViewById(R.id.lvcalendar);
        listView.setAdapter(lvcalendaradaptor);
        listView.setOnItemClickListener(ListViewOnItemClickedListner);

        requestLvItems();

        // TODO get saved results
        shownotificationsmenus = true;
        showdaillymessage = true;
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

            Log.d("MainActivity", "position: " + position + " :: id: " + id + " :: " + MainService.getStringByMillis(clickedItem.referdate));


            Intent intent = new Intent(v.getContext(), MainService.class);
            intent.putExtra(MainService.SWITCH, MainService.INVOKEPOPUPBYLVITEM);
            intent.putExtra(MainService.INVOKEPOPUPBYLVITEM, clickedItem.referdate);
            startService(intent);
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
        if (resultCode == Activity.RESULT_OK && requestCode == ACTRES_SELECTSOUND)
        {
            Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

            if (uri != null)
            {
                //this.chosenRingtone = uri.toString();

                Log.d("LauncherActivity", "Ringtone selected: " + uri);
            }
            else
            {
                Log.d("LauncherActivity", "Ringtone selected: NULL");
                //this.chosenRingtone = null;
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

        menunotifications.setChecked(shownotificationsmenus);
        menunotifications.setIcon(shownotificationsmenus ? R.drawable.ic_action_bellon : R.drawable.ic_action_belloff);
        menusound.setVisible(shownotificationsmenus);
        menuvibrate.setVisible(shownotificationsmenus);
        menutime.setVisible(shownotificationsmenus);

        // Dailly message
        MenuItem menumessage =  menu.findItem(R.id.menumessage);
        menumessage.setChecked(showdaillymessage);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;

            case R.id.menumessage:
                showdaillymessage = !item.isChecked();
                this.invalidateOptionsMenu();

                return true;

            case R.id.menunotifications:
                shownotificationsmenus = !item.isChecked();
                this.invalidateOptionsMenu();

                if (shownotificationsmenus) {
                    Intent intent = new Intent(this, MainService.class);
                    intent.putExtra(MainService.SWITCH, MainService.ENABLENOTIFICATIONS);
                    startService(intent);
                }
                else {
                    Intent intent = new Intent(this, MainService.class);
                    intent.putExtra(MainService.SWITCH, MainService.DISABLENOTIFICATIONS);
                    startService(intent);
                }

                return true;
            case R.id.menusound:
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Som de notificação");
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);

                this.startActivityForResult(intent, ACTRES_SELECTSOUND);

                return true;
            case R.id.menuvibrate:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);

                return true;
            case R.id.menutime:
                DialogFragment newFragment = new GettimeFragment();
                newFragment.show(getFragmentManager(), "timePicker");
                return true;



            case R.id.menudebug:
                startActivity(new Intent(this, DebugActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
