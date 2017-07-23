package com.places.distance.domain;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.places.distance.Constants;


/**
 * Receiver to be notified when the boot section is complete. If the service was running when the
 * device shut down, it should restart the service
 * Created by Aurelian Cotuna
 */

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "BootReceiver";


    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences mSharedPreferences =
                context.getSharedPreferences(Constants.SP_TRACKING_STATUS, Context.MODE_PRIVATE);

        if (mSharedPreferences.getBoolean(Constants.SP_TRACKING_RUNNING, false)){
            Log.i(TAG, "Starting service from boot receiver");
            context.startService(new Intent(context, TrackingService.class));

        }
    }
}
