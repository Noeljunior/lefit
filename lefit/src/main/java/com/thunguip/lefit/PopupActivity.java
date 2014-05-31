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

    private MessageParcel message;
    private int sboffset;

    /* Values */
    private String[] phrases;
    private String[] messages;
    private int selectedmessage;

    private MessageResultParcel msgresult;

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

        //setFinishOnTouchOutside(true);

        /* Get the message */
        message = getIntent().getExtras().getParcelable(MESSAGE);
        if (message == null) {
            Log.w("myApp", "NULL!!!!!");
            // TODO abort the creation of this activity
            finish();
        }

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup);

        // Start new result message
        msgresult = new MessageResultParcel();

        msgresult.title = message.title;
        msgresult.phrasesset = message.phraseset;
        msgresult.phrasesmax = message.maxphrase;
        msgresult.phrasesmin = message.minphrase;
        msgresult.messageset = message.messageset;
        msgresult.messagesubset = message.messagesubset;
        msgresult.action = PopupEntryParcel.POPUP_ACTION_IGNORE;
        msgresult.messagehithide = message.showmessage == 1 ? PopupEntryParcel.POPUP_HIDE_FALSE : PopupEntryParcel.POPUP_HIDE_HIDEN;
        msgresult.daterefer = message.referdate;
        msgresult.dateinit = Preferences.TimeHelper.getTodayDateTime();


        Log.d("PopupActivity", "REFERDATE: " + msgresult.daterefer);

        /* Initialize this new popup */
        seekbar = (SeekBar) findViewById(R.id.seekbar);
        tvphrase = (TextView) findViewById(R.id.phrase);
        tvmessage = (TextView) findViewById(R.id.message);
        tvtitle = (TextView) findViewById(R.id.title);
        ivphraselogo = (ImageView) findViewById(R.id.phraselogo);
        ibmore = (ImageButton) findViewById(R.id.ibmore);
        ibless = (ImageButton) findViewById(R.id.ibless);


        // Get and set title
        tvtitle.setText(getResources().getStringArray(R.array.titles)[message.title]);

        // Prepare seekbar
        seekbar.setMax(message.maxphrase - message.minphrase);
        sboffset = message.minphrase;

        // Get phrases
        TypedArray ids = getResources().obtainTypedArray(R.array.phrases);
        int phraseid = ids.getResourceId(message.phraseset, -1);
        ids.recycle();
        phrases = getResources().getStringArray(phraseid);
        // And set the default
        setPhrase(message.defphrase);

        // Get messages
        ids = getResources().obtainTypedArray(R.array.messages);
        int messageids = ids.getResourceId(message.messageset, -1);
        ids.recycle();
        ids = getResources().obtainTypedArray(messageids);
        messageids = ids.getResourceId(message.messagesubset, -1);
        ids.recycle();
        messages = getResources().getStringArray(messageids);
        selectedmessage = message.defmessage;
        // And set the default
        tvmessage.setText(messages[selectedmessage]);


        /* END Initialize this new popup*/

        if (message.showmessage == 0) {
            hideMessage();
        }

        if (message.showpostpone == 0) {
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
                    ibmore.setImageResource(R.drawable.ic_hitmore);
                else
                    ibmore.setImageResource(R.drawable.ic_navigation_next_item);

                if (progress == 0 && sboffset > 0)
                    ibless.setImageResource(R.drawable.ic_hitless);
                else
                    ibless.setImageResource(R.drawable.ic_navigation_previous_item);
            }
        });
    }

    /* -- ME -- */
    public void incrementSeek(View view) {
        if (seekbar.getProgress() == seekbar.getMax() &&  (seekbar.getProgress() + sboffset) < (phrases.length - 1)) {
            seekbar.setMax(seekbar.getMax() + 1);

            // STATS
            msgresult.phraseshitsmore++;
        }

        seekbar.setProgress(seekbar.getProgress() + 1);
    }
    public void decrementSeek(View view) {
        if (seekbar.getProgress() == 0 && sboffset > 0) {
            sboffset--;
            seekbar.setMax(seekbar.getMax() + 1);

            // STATS
            msgresult.phraseshitsless++;
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

        TypedArray ids = getResources().obtainTypedArray(R.array.phraseicons);
        int phraseid = ids.getResourceId(id, -1);
        ids.recycle();

        if (id >= 0)
            ivphraselogo.setImageResource(phraseid);

        // STATS
        msgresult.phrasesanswered = id;
    }

    public void nextMessage(View view) {
        selectedmessage = (selectedmessage + 1) % messages.length;
        tvmessage.setText(messages[selectedmessage]);

        // STATS
        msgresult.messagehitmore++;
    }



    public void hideMessageClick(View view) {
        hideMessage();

        new Preferences(this).setShowDaillyMessage(false);

        // STATS
        msgresult.messagehithide = PopupEntryParcel.POPUP_HIDE_TRUE;
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
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(message.referdate);

        // STATS
        msgresult.action = PopupEntryParcel.POPUP_ACTION_CANCEL;

        finish();
    }

    public void actionPostpone(View view) {
        // STATS
        msgresult.action = PopupEntryParcel.POPUP_ACTION_POSTPONE;

        finish();
    }

    public void actionSubmit(View view) {

        // STATS
        msgresult.action = PopupEntryParcel.POPUP_ACTION_SUBMIT;

        finish();
    }

    /*private void closeAndSendData() {

        finish();
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();

        PopupEntryParcel pep = new PopupEntryParcel(msgresult.title,
                msgresult.phrasesset, msgresult.phrasesmin, msgresult.phrasesmax, msgresult.phrasesanswered, msgresult.phraseshitsmore, msgresult.phraseshitsless,
                msgresult.messageset, msgresult.messagesubset, msgresult.messagehithide, msgresult.messagehitmore,
                msgresult.action,
                msgresult.daterefer, msgresult.dateinit, Preferences.TimeHelper.getTodayDateTime());

        MainService.sendIntent(this, MainService.ADDANSWERTODB, pep);
    }


}
