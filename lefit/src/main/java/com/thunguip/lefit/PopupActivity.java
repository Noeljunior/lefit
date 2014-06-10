package com.thunguip.lefit;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Calendar;

public class PopupActivity extends Activity {
    public static final String MESSAGE = "com.thunguip.lefit.PopupActivity.MESSAGE";

    private int sboffset;

    /* Values */
    private String[] phrases;
    private String[] messages;
    private int selectedmessage;

    /* Result message */
    private PopupEntryParcel popupentry;

    /* States */
    private boolean showMessage;

    /* Widgets */
    private SeekBar seekbar;
    private TextView tvtitle;
    private TextView tvphrase;
    private TextView tvmessage;
    private ImageView ivphraselogo;
    private ImageButton ibmore;
    private ImageButton ibless;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Get the message */
        popupentry = getIntent().getExtras().getParcelable(MESSAGE);
        if (popupentry == null) {
            finish();
        }

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup);


        /* Set the time when this popup was started */
        popupentry.dateinit = Preferences.TimeHelper.getTodayDateTime();


        /* Initialize this new popup */
        seekbar = (SeekBar) findViewById(R.id.seekbar);
        tvphrase = (TextView) findViewById(R.id.phrase);
        tvmessage = (TextView) findViewById(R.id.message);
        tvtitle = (TextView) findViewById(R.id.title);
        ivphraselogo = (ImageView) findViewById(R.id.phraselogo);
        ibmore = (ImageButton) findViewById(R.id.ibmore);
        ibless = (ImageButton) findViewById(R.id.ibless);


        // Get and set title
        tvtitle.setText(getResources().getStringArray(R.array.titles)[popupentry.title]);

        // Prepare seekbar
        seekbar.setMax(popupentry.phrasemax - popupentry.phrasemin);
        sboffset = popupentry.phrasemin;

        // Get phrases
        TypedArray ids = getResources().obtainTypedArray(R.array.phrases);
        int phraseid = ids.getResourceId(popupentry.phraseset, -1);
        ids.recycle();
        phrases = getResources().getStringArray(phraseid);
        // And set the default
        setPhrase(popupentry.phraseanswer);


        /* END Initialize this new popup*/

        if (popupentry.messagehide == PopupEntryParcel.POPUP_HIDE_HIDEN) {
            hideMessage();
        } else {
            // Get messages
            ids = getResources().obtainTypedArray(R.array.messages);
            int messageids = ids.getResourceId(popupentry.messageset, -1);
            ids.recycle();
            ids = getResources().obtainTypedArray(messageids);
            messageids = ids.getResourceId(popupentry.messagesubset, -1);
            ids.recycle();
            messages = getResources().getStringArray(messageids);
            selectedmessage = popupentry.messagedef;
            // And set the default
            tvmessage.setText(messages[selectedmessage]);
        }

        if (popupentry.showpostpone == PopupEntryParcel.POPUP_SHOWPOSTPONE_FALSE) {
            hidePostpone();
        }


        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setPhrase(progress + sboffset);

                if (progress == seekBar.getMax() && (progress + sboffset) < (phrases.length - 1))
                    ibmore.setImageResource(R.drawable.ic_more);
                else
                    ibmore.setImageResource(R.drawable.ic_next);

                if (progress == 0 && sboffset > 0)
                    ibless.setImageResource(R.drawable.ic_less);
                else
                    ibless.setImageResource(R.drawable.ic_previous);
            }
        });
    }

    /* -- ME -- */
    public void incrementSeek(View view) {
        if (seekbar.getProgress() == seekbar.getMax() &&  (seekbar.getProgress() + sboffset) < (phrases.length - 1)) {
            seekbar.setMax(seekbar.getMax() + 1);

            // STATS
            popupentry.phrasehitmore++;
        }

        seekbar.setProgress(seekbar.getProgress() + 1);
    }
    public void decrementSeek(View view) {
        if (seekbar.getProgress() == 0 && sboffset > 0) {
            sboffset--;
            seekbar.setMax(seekbar.getMax() + 1);

            // STATS
            popupentry.phrasehitless++;
        }

        seekbar.setProgress(seekbar.getProgress() - 1);
    }

    private void setPhrase(int id) {
        if (id < 0 || id >= phrases.length)
            return;

        // sets the textview
        tvphrase.setText(phrases[id]);

        // sets the seekbar
        if ((seekbar.getProgress() + sboffset) != id)
            seekbar.setProgress(id - sboffset);

        TypedArray ids;
        if (popupentry.title == 0) ids = getResources().obtainTypedArray(R.array.firstphraseicons);
        else                       ids = getResources().obtainTypedArray(R.array.phraseicons);

        int phraseid = ids.getResourceId(id, -1);
        ids.recycle();

        if (id >= 0)
            ivphraselogo.setImageResource(phraseid);

        // STATS
        popupentry.phraseanswer = id;
    }

    public void nextMessage(View view) {
        selectedmessage = (selectedmessage + 1) % messages.length;
        tvmessage.setText(messages[selectedmessage]);

        // STATS
        popupentry.messagemore++;
    }



    public void hideMessageClick(View view) {
        hideMessage();

        new Preferences(this).setShowDaillyMessage(false);

        // STATS
        popupentry.messagehide = PopupEntryParcel.POPUP_HIDE_TRUE;
    }

    public void hideMessage() {
        RelativeLayout messagelayout = (RelativeLayout) findViewById(R.id.messagelayout);
        messagelayout.setVisibility(RelativeLayout.GONE);
    }

    public void hidePostpone() {
        findViewById(R.id.ibpostpone).setVisibility(View.GONE);
        findViewById(R.id.separator2).setVisibility(View.GONE);
    }


    public void actionCancel(View view) {

        // STATS
        popupentry.action = PopupEntryParcel.POPUP_ACTION_CANCEL;

        finish();
    }

    public void actionPostpone(View view) {
        // STATS
        popupentry.action = PopupEntryParcel.POPUP_ACTION_POSTPONE;

        finish();
    }

    public void actionSubmit(View view) {

        // STATS
        popupentry.action = PopupEntryParcel.POPUP_ACTION_SUBMIT;

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        popupentry.dateaction = Preferences.TimeHelper.getTodayDateTime();
        MainService.sendIntent(this, MainService.ADDANSWERTODB, popupentry);
    }


}
