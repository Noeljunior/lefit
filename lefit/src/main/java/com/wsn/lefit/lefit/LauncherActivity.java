package com.wsn.lefit.lefit;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;


public class LauncherActivity extends Activity {
    private LvCalendarAdaptor lvcalendaradaptor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        lvcalendaradaptor = new LvCalendarAdaptor(this);


        ListView list=(ListView)findViewById(R.id.lvcalendar);
        list.setAdapter(lvcalendaradaptor);


    }

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
