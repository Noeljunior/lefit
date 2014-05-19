package com.wsn.lefit.lefit;

import android.support.v4.app.FragmentActivity;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;



public class PopupActivity extends Activity {

    private Message message;
    private int test;

    /* States */
    private boolean showMessage;

    /* Widgets */
    private SeekBar seekbar;
    private TextView tvtitle;
    private TextView tvphrase;
    private TextView tvmessage;
    private ImageView ivphraselogo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        /* Get the message */
        message = getIntent().getExtras().getParcelable("msg");
        if (message == null) {
            Log.w("myApp", "NULL!!!!!");
            // TODO abort the creation of this activity
        }

        // TODO use support lib
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup);

        /* Initialize this new popup */
        seekbar = (SeekBar) findViewById(R.id.seekbar);
        tvphrase = (TextView) findViewById(R.id.phrase);
        tvmessage = (TextView) findViewById(R.id.message);
        tvtitle = (TextView) findViewById(R.id.title);
        ivphraselogo = (ImageView) findViewById(R.id.phraselogo);

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setPhrase(progress);
            }
        });

        tvtitle.setText(message.getTitle());

        seekbar.setMax(message.countPhrases() - 1);

        setPhrase(message.getDphrase());

        tvmessage.setText(message.getMessage(message.getDmessage()));
        /* END Initialize this new popup*/

        Log.w("------", "onCreate ! ! ! ! ! ! ! ! ! ! ! " + test++);
    }

    /* -- ME -- */
    public void incrementSeek(View view) {
        seekbar.setProgress(seekbar.getProgress() + 1);
    }
    public void decrementSeek(View view) {
        seekbar.setProgress(seekbar.getProgress() - 1);
    }

    private void setPhrase(int id) {

        // sets the textview
        tvphrase.setText(message.getPhrase(id));

        // sets the seekbar
        if (seekbar.getProgress() != id)
            seekbar.setProgress(id);

        // sets the image
        ivphraselogo.setImageResource(message.getLogo(id));
    }

    public void hideMessage(View view) {
        RelativeLayout messagelayout = (RelativeLayout) findViewById(R.id.messagelayout);
        messagelayout.setVisibility(RelativeLayout.GONE);
    }


}
