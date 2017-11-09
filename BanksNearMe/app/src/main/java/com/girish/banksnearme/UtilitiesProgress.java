package com.girish.banksnearme;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by girish on 10/10/2015.
 */
public class UtilitiesProgress {

    private static ProgressDialog mProgressDialog = null;

    @SuppressLint("NewApi")
    public static void displayProgressDialog(Context context,String message, Boolean backButtonCancelable){
        if(mProgressDialog == null && context != null){
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage(message);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.show();
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setCancelable(backButtonCancelable);
        }
    }

    public static void cancelProgressDialog(){
        if(mProgressDialog != null){
            mProgressDialog.dismiss();
            mProgressDialog.cancel();
            mProgressDialog = null;
        }
    }

}
