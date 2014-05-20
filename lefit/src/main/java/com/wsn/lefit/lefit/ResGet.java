package com.wsn.lefit.lefit;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;

/**
 * Created by liam on 5/20/14.
 */
public class ResGet {
    private static final int phrases = R.array.phrases;

    public static String getPhraseString(Context context, int type, int index) {
        String out = "";

        TypedArray taphrases = context.getResources().obtainTypedArray(phrases);


        taphrases.getResourceId(type, 0);

        return out;
    }


}
