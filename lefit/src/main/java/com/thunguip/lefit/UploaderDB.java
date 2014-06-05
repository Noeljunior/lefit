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


    private Context context;


    public UploaderDB(Context context) {
        this.context = context;
    }

    public void sendAllUnsent() {
        ArrayList<List> items = new StorageDB(context).getAllUnsent();

        final ArrayList<HttpPost> postclients = new ArrayList<>();

        Log.d("UploaderDB", "Printing all items");

        for (List item : items) {
            Log.d("UploaderDB", itemToString(item, 0));


            item.add(new BasicNameValuePair(UploaderDB.FORM_DEVICEID,  "TESTDEVICEENV"));
            item.add(new BasicNameValuePair(UploaderDB.FORM_USERID,    "TESTUSERENV"));

            try {
                HttpPost httppost = new HttpPost(URL_FORM);
                httppost.setEntity(new UrlEncodedFormEntity(item));
                postclients.add(httppost);

                //new DefaultHttpClient().execute(httppost);
            }
            catch (IOException e) {}


        }


        /* Submit form POST */
        new Thread(new Runnable() {
            public void run() {
                final HttpClient httpclient = new DefaultHttpClient();
                for (HttpPost httpPost : postclients) {

                    try {
                        HttpResponse response = httpclient.execute(httpPost);
                        Log.d("UploaderDB", "JUSTUPLOADEDoutise with code: " + response.getStatusLine().getStatusCode());

                        // TODO mark as sent on the local database
                    }
                    catch (IOException e) { }
                }
            }
        }).start();
    }

    private String itemToString(List item, int how) {
        String out = "";

        for (Object subitem : item) {
            BasicNameValuePair bnp = (BasicNameValuePair) subitem;

            out = out + bnp.getName() + "=" + bnp.getValue() + "; ";

        }

        return out;
    }

    public void postItem() {


    }










}
