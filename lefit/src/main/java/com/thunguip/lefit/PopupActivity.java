package com.thunguip.lefit;

import android.app.Activity;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class PopupActivity extends Activity {
    public static final String MESSAGE = "com.thunguip.lefit.PopupActivity.MESSAGE";

    public static final String KEY_PEP          = "KEY_PEP";
    public static final String KEY_OFFSET       = "KEY_OFFSET";
    public static final String KEY_SEEKBARMIN   = "KEY_SEEKBARMIN";
    public static final String KEY_SEEKBARMAX   = "KEY_SEEKBARMAX";
    public static final String KEY_MSGSTATE     = "KEY_MSGSTATE";



    /* Values */
    private String[]    phrases;
    private String[]    messages;


    /* Result message */
    private PopupEntryParcel popupentry;

    /* States */
    private int         sboffset;
    private int         seekbarmax;
    private int         seekbarmin;
    private int         selectedmessage;


    /* Widgets */
    private SeekBar     seekbar;
    private TextView    tvtitle;
    private TextView    tvphrase;
    private TextView    tvmessage;
    private ImageView   ivphraselogo;
    private ImageButton ibmore;
    private ImageButton ibless;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup);

        /* Initialize this new popup */
        seekbar         = (SeekBar)     findViewById(R.id.seekbar);
        tvphrase        = (TextView)    findViewById(R.id.phrase);
        tvmessage       = (TextView)    findViewById(R.id.message);
        tvtitle         = (TextView)    findViewById(R.id.title);
        ivphraselogo    = (ImageView)   findViewById(R.id.phraselogo);
        ibmore          = (ImageButton) findViewById(R.id.ibmore);
        ibless          = (ImageButton) findViewById(R.id.ibless);


        Log.d("PopupActivity", savedInstanceState == null ? "NULL_SAVED" : "NOTNULL_SAVED" + "; ");


        /* Get the message */
        if ((popupentry = initPopup(savedInstanceState)) == null) {
            finish();
            return;
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

        setPhrase(popupentry.phraseanswer);
        if (seekbar.getProgress() == seekbar.getMax() && (seekbar.getProgress() + sboffset) < (phrases.length - 1))
            ibmore.setImageResource(R.drawable.ic_more);
        else
            ibmore.setImageResource(R.drawable.ic_next);

        if (seekbar.getProgress() == 0 && sboffset > 0)
            ibless.setImageResource(R.drawable.ic_less);
        else
            ibless.setImageResource(R.drawable.ic_previous);
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putParcelable(KEY_PEP,        popupentry);

        savedInstanceState.putInt       (KEY_OFFSET,     sboffset);

        savedInstanceState.putInt       (KEY_SEEKBARMAX, seekbarmax);
        savedInstanceState.putInt       (KEY_SEEKBARMIN, seekbarmin);

        savedInstanceState.putInt       (KEY_MSGSTATE,   selectedmessage);
    }

    /* -- ME -- */


    private PopupEntryParcel initPopup(Bundle savedInstanceState) {

        PopupEntryParcel pep;

        if (savedInstanceState == null) {
            /* New popup */
            pep = getIntent().getExtras().getParcelable(MESSAGE);

            /* Set the time when this popup was started */
            pep.dateinit = Preferences.TimeHelper.getTodayDateTime();

            /* Set initial offset */
            sboffset = pep.phrasemin;

            /* Set the initial ohrase max and min */
            seekbarmin = pep.phrasemin;
            seekbarmax = pep.phrasemax;

            selectedmessage = pep.messagedef;

        }
        else {
            /* Restoring popup */
            pep         = savedInstanceState.getParcelable(KEY_PEP);

            sboffset    = savedInstanceState.getInt       (KEY_OFFSET);

            seekbarmin  = savedInstanceState.getInt       (KEY_SEEKBARMIN);
            seekbarmax  = savedInstanceState.getInt       (KEY_SEEKBARMAX);

            selectedmessage  = savedInstanceState.getInt  (KEY_MSGSTATE);
        }


        // Get and set title
        tvtitle.setText(getResources().getStringArray(R.array.titles)[pep.title]);

        // Prepare seekbar
        seekbar.setMax(seekbarmax - seekbarmin);


        // Get phrases
        TypedArray ids = getResources().obtainTypedArray(R.array.phrases);
        int phraseid = ids.getResourceId(pep.phraseset, -1);
        ids.recycle();
        phrases = getResources().getStringArray(phraseid);


        /* END Initialize this new popup*/

        if (pep.messagehide != PopupEntryParcel.POPUP_HIDE_FALSE) {
            hideMessage();
        } else {
            // Get messages
            ids = getResources().obtainTypedArray(R.array.messages);
            int messageids = ids.getResourceId(pep.messageset, -1);
            ids.recycle();
            ids = getResources().obtainTypedArray(messageids);
            messageids = ids.getResourceId(pep.messagesubset, -1);
            ids.recycle();
            messages = getResources().getStringArray(messageids);
            // And set the default
            tvmessage.setText(messages[selectedmessage]);
        }

        if (pep.showpostpone == PopupEntryParcel.POPUP_SHOWPOSTPONE_FALSE) {
            hidePostpone();
        }

        return pep;
    }




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
