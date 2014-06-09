package com.thunguip.lefit;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class BackgroundService extends IntentService {
    public static final String CNAME = "BackgroundService";

    public static final String SWITCH               = "com.thunguip.lefit.backgroundservice.SWITCH";
    public static final String UPLOADITEMS          = "com.thunguip.lefit.backgroundservice.SWITCH.UPLOADITEMS";

    private Preferences preferences;

    public BackgroundService() {
        super(CNAME);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        preferences = new Preferences(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent.getStringExtra(SWITCH) == null) {
            // Unknown intent
            return;
        }

        Log.d(CNAME, "New Intent: " + intent.getStringExtra(SWITCH));

        switch (intent.getStringExtra(SWITCH)) {
            case UPLOADITEMS:       /* Upload items to the form */
                uploadItems();
                return;

            default:
                /* Unknown SWITCH */
        }
    }

    private void uploadItems() {
        NetworkInfo activeNetwork = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        boolean thereAreItems = new StorageDB(this).countUnsetItems() > 0;

        if (thereAreItems && !isConnected) { /* There are items to send but there is NO connection */
            Log.d(CNAME, "Upload items ABORTED due to not connection");
            MainService.setInternetChangeBroadcastReceiver(this, true);
        }
        else if (!thereAreItems) { /* There are no items to send, unsetting internetchange broadcast receiver */
            Log.d(CNAME, "Upload items ABORTED due to no items to send");
            MainService.setInternetChangeBroadcastReceiver(this, false);
        }

        int MAX_ERRORS = 3;

        StorageDB        database   = new StorageDB(this);
        ArrayList<List>  items      = database.getAllUnsent();
        final HttpClient httpclient = new DefaultHttpClient();
        String           deviceid   = Preferences.getAndroidId(this);
        String           userid     = preferences.getUserID();

        Log.d(CNAME, "Will send " + items.size() + " items.");

        int errorcount = 0;
        for (List item : items) {
            item.add(new BasicNameValuePair(Form.DEVICEID, deviceid));
            item.add(new BasicNameValuePair(Form.USERID,   userid));

            try {
                HttpPost httppost = new HttpPost(Form.URL);
                httppost.setEntity(new UrlEncodedFormEntity(item));
                HttpResponse response = httpclient.execute(httppost);

                if (response.getStatusLine().getStatusCode() == 200) {
                    database.markAsSent(getId(item));

                    Log.d(CNAME, "Item sent and maked as sent: " + itemToString(item));
                }
                else {
                    Log.d(CNAME, "HTTP ERROR CODE: " + response.getStatusLine().getStatusCode());
                    errorcount++;
                }

            }
            catch (IOException e) {
                Log.d(CNAME, "NO CONNECTION");
                errorcount++;
            }

            if (errorcount >= MAX_ERRORS)
                break;
        }

        if (errorcount > 0){
            /* Set an alarm to check later */
            AlarmerManager.setRelativeAlarm(this,
                    MainService.ALARMID_POSTPONEUPLOAD,
                    SystemClock.elapsedRealtime() + preferences.getUploadDelay());

            MainService.setInternetChangeBroadcastReceiver(this, false);

            Log.d(CNAME, "Some erros occured: " + errorcount + " errors");
        }
        else {
            Log.d(CNAME, "All items were uploaded");
            MainService.setInternetChangeBroadcastReceiver(this, false);
        }
    }




    /* Static Methods */
    public static void sendIntent(Context context, String switchitent) {
        Intent intent = new Intent(context, BackgroundService.class);
        intent.putExtra(SWITCH, switchitent);
        context.startService(intent);
    }

    private static long getId(List<BasicNameValuePair> item) {
        for (BasicNameValuePair subitem : item) {
            if (subitem.getName().equals(Form.ID))
                return Long.parseLong(subitem.getValue());
        }
        return 0;
    }

    private static String itemToString(List<BasicNameValuePair> item) {
        String out = "";
        for (BasicNameValuePair subitem : item)
            out = out + subitem.getName() + "=" + subitem.getValue() + "; ";
        return out;
    }


    /* Static Inner Classes */
    public static class Form {
        private static final String URL = "https://docs.google.com/forms/d/1uwWgnavpUBcN8yVZ4p_7NOoJNVmiIqPOSLfuoL-njoI/formResponse";

        public static final String DBVERSION   = "entry.1523456143";
        public static final String DEVICEID    = "entry.1696053651";
        public static final String USERID      = "entry.1109179678";
        public static final String TYPE        = "entry.677334655";
        public static final String ID          = "entry.1827167259";
        public static final String VAL1        = "entry.1934645259";
        public static final String VAL2        = "entry.398983787";
        public static final String VAL3        = "entry.1220964455";
        public static final String VAL4        = "entry.1701495023";
        public static final String VAL5        = "entry.828452764";
        public static final String VAL6        = "entry.1535871734";
        public static final String VAL7        = "entry.330838149";
        public static final String VAL8        = "entry.712222950";
        public static final String VAL9        = "entry.421910484";
        public static final String VAL10       = "entry.96937508";
        public static final String VAL11       = "entry.446565661";
        public static final String VAL12       = "entry.715610532";
        public static final String VAL13       = "entry.1645755123";
        public static final String VAL14       = "entry.1746984713";
        public static final String VAL15       = "entry.1445171993";
    }
}
