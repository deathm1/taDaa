package com.koshurTech.tadaa;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

public class loading {

    Activity activity;
    AlertDialog alertDialog;
    public loading(Activity mAct){
        activity = mAct;
    }

    public void startLoadingAnimation(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.loading_layout,null));

        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.show();
    }

    public void dismissDialog(){
        alertDialog.dismiss();
    }
}
