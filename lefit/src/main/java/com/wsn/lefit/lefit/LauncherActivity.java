package com.wsn.lefit.lefit;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class LauncherActivity extends Activity {
    private LvCalendarAdaptor lvcalendaradaptor;

    //public static final


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        lvcalendaradaptor = new LvCalendarAdaptor(this);


        /* TODO delete this dummy listview populator */

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d 'de' MMM");
        cal.set(Calendar.MONTH, 4);
        cal.set(Calendar.DAY_OF_MONTH, 0);


        for (int i = 0; i < 100; i++) {
            if (((i+1) % 8) == 0) {
                lvcalendaradaptor.addSeparator("Semana " + i/8);
                continue;
            }
            cal.add(Calendar.DAY_OF_MONTH, 1);
            if (Math.random() <= 0.2) {
                lvcalendaradaptor.addItemUnfilled(R.drawable.ic_questionmark, "Clique para preencher", dateFormat.format(cal.getTime()));
                continue;
            }

            int sel = (int) (Math.random() * 10.99);
            switch (sel) {
                default:
                case 0:
                    lvcalendaradaptor.addItemFilled(R.drawable.ic_phraseicon_0, getResources().getStringArray(R.array.phrases0)[0], dateFormat.format(cal.getTime()));
                    break;
                case 1:
                    lvcalendaradaptor.addItemFilled(R.drawable.ic_phraseicon_1, getResources().getStringArray(R.array.phrases0)[1], dateFormat.format(cal.getTime()));
                    break;
                case 2:
                    lvcalendaradaptor.addItemFilled(R.drawable.ic_phraseicon_2, getResources().getStringArray(R.array.phrases0)[2], dateFormat.format(cal.getTime()));
                    break;
                case 3:
                    lvcalendaradaptor.addItemFilled(R.drawable.ic_phraseicon_3, getResources().getStringArray(R.array.phrases0)[3], dateFormat.format(cal.getTime()));
                    break;
                case 4:
                    lvcalendaradaptor.addItemFilled(R.drawable.ic_phraseicon_4, getResources().getStringArray(R.array.phrases0)[4], dateFormat.format(cal.getTime()));
                    break;
                case 5:
                    lvcalendaradaptor.addItemFilled(R.drawable.ic_phraseicon_5, getResources().getStringArray(R.array.phrases0)[5], dateFormat.format(cal.getTime()));
                    break;
                case 6:
                    lvcalendaradaptor.addItemFilled(R.drawable.ic_phraseicon_6, getResources().getStringArray(R.array.phrases0)[6], dateFormat.format(cal.getTime()));
                    break;
                case 7:
                    lvcalendaradaptor.addItemFilled(R.drawable.ic_phraseicon_7, getResources().getStringArray(R.array.phrases0)[7], dateFormat.format(cal.getTime()));
                    break;
                case 8:
                    lvcalendaradaptor.addItemFilled(R.drawable.ic_phraseicon_8, getResources().getStringArray(R.array.phrases0)[8], dateFormat.format(cal.getTime()));
                    break;
                case 9:
                    lvcalendaradaptor.addItemFilled(R.drawable.ic_phraseicon_9, getResources().getStringArray(R.array.phrases0)[9], dateFormat.format(cal.getTime()));
                    break;
                case 10:
                    lvcalendaradaptor.addItemFilled(R.drawable.ic_phraseicon_10, getResources().getStringArray(R.array.phrases0)[10], dateFormat.format(cal.getTime()));
                    break;
            }

        }

        /* TODO delete until here */

        ListView list=(ListView)findViewById(R.id.lvcalendar);
        list.setAdapter(lvcalendaradaptor);


    }

    public void testBehave(View view) {
        Intent intent = new Intent(this, MainService.class);
        intent.putExtra(MainService.MESSENGER, new Messenger(handler));
        intent.putExtra(MainService.SWITCH, MainService.GETLVITEMS);
        startService(intent);
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle data = msg.getData();


            LvItemParcel item = data.getParcelable(MainService.GETLVITEMS);

            ArrayList<LvItemParcel> lvar = new ArrayList<>();
            lvar.add(item);

            lvcalendaradaptor.setItems(lvar);

        }
    };








    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.launcher, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;

            case R.id.menunotifications:
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
