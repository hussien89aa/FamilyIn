package com.hussienalrubaye.familyfinder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


/**
 * Created by hussienalrubaye on 9/17/15.
 */
public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (intent.getAction().equalsIgnoreCase("android.intent.action.BOOT_COMPLETED")) {

            Intent intentone = new Intent(context.getApplicationContext(), MainActivity.class);
            intentone.putExtra("IsBoot", "Yes");
            intentone.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intentone);

        }
    }



}
