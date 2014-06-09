package com.thunguip.lefit;

import android.content.Context;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class UploaderDB {
    private static final String URL_FORM = "https://docs.google.com/forms/d/1uwWgnavpUBcN8yVZ4p_7NOoJNVmiIqPOSLfuoL-njoI/formResponse";

    public static final String FORM_DBVERSION   = "entry.1523456143";
    public static final String FORM_DEVICEID    = "entry.1696053651";
    public static final String FORM_USERID      = "entry.1109179678";
    public static final String FORM_TYPE        = "entry.677334655";
    public static final String FORM_ID          = "entry.1827167259";
    public static final String FORM_VAL1        = "entry.1934645259";
    public static final String FORM_VAL2        = "entry.398983787";
    public static final String FORM_VAL3        = "entry.1220964455";
    public static final String FORM_VAL4        = "entry.1701495023";
    public static final String FORM_VAL5        = "entry.828452764";
    public static final String FORM_VAL6        = "entry.1535871734";
    public static final String FORM_VAL7        = "entry.330838149";
    public static final String FORM_VAL8        = "entry.712222950";
    public static final String FORM_VAL9        = "entry.421910484";
    public static final String FORM_VAL10       = "entry.96937508";
    public static final String FORM_VAL11       = "entry.446565661";
    public static final String FORM_VAL12       = "entry.715610532";
    public static final String FORM_VAL13       = "entry.1645755123";
    public static final String FORM_VAL14       = "entry.1746984713";
    public static final String FORM_VAL15       = "entry.1445171993";

    private static final int MAX_ERRORS = 3;


    private Context context;


    public UploaderDB(Context context) {
        this.context = context;
    }

    public void sendAllUnsent() {

        /* Submit form POST */
        new Thread(new Runnable() { public void run() {

            StorageDB database = new StorageDB(context);
            ArrayList<List> items = database.getAllUnsent();
            final HttpClient httpclient = new DefaultHttpClient();


            Log.d("UploaderDB", "Will send " + items.size() + " items.");

            int errorcount = 0;
            for (List item : items) {
                // TODO get device ID and user ID
                item.add(new BasicNameValuePair(UploaderDB.FORM_DEVICEID,  Preferences.getAndroidId()));
                item.add(new BasicNameValuePair(UploaderDB.FORM_USERID,    new Preferences(context).getUserID()));

                try {
                    HttpPost httppost = new HttpPost(URL_FORM);
                    httppost.setEntity(new UrlEncodedFormEntity(item));
                    HttpResponse response = httpclient.execute(httppost);

                    if (response.getStatusLine().getStatusCode() == 200) {
                        database.markAsSent(getId(item));

                        Log.d("UploaderDB", "Item sent and maked as sent: " + itemToString(item));
                    }
                    else {
                        Log.d("UploaderDB", "HTTP ERROR CODE: " + response.getStatusLine().getStatusCode());
                        errorcount++;
                    }

                }
                catch (IOException e) {
                    Log.d("UploaderDB", "NO CONNECTION");
                    errorcount++;
                }

                if (errorcount >= MAX_ERRORS)
                    break;
            }

            if (errorcount > 0){
                MainService.sendIntent(context, MainService.CHECKINTERNETSTATE);
                Log.d("UploaderDB", "Some erros occured: " + errorcount + " errors");
            }
            else {
                Log.d("UploaderDB", "All items were uploaded");
                MainService.sendIntent(context, MainService.CHECKINTERNETSTATE);
            }

        } }).start();
    }

    private long getId(List<BasicNameValuePair> item) {
        for (BasicNameValuePair subitem : item) {
            if (subitem.getName().equals(FORM_ID))
                return Long.parseLong(subitem.getValue());
        }
        return 0;
    }

    private String itemToString(List item) {
        String out = "";

        for (Object subitem : item) {
            BasicNameValuePair bnp = (BasicNameValuePair) subitem;

            out = out + bnp.getName() + "=" + bnp.getValue() + "; ";

        }

        return out;
    }










}
