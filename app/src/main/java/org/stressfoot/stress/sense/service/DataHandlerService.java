package org.stressfoot.stress.sense.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import org.stressfoot.stress.sense.util.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/*************NOT IN USE*************/
public class DataHandlerService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registerReceiver(dataReceiver, getIntentFilters());
        return super.onStartCommand(intent, flags, startId);
    }

    private final BroadcastReceiver dataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(Constants.ACTION_HOURLY_STRESS_DATA)){
                ArrayList<String> statesList = intent.getStringArrayListExtra(Constants.HOURLY_STRESS);
                handleStressData(statesList);
            }
        }
    };

    private void handleStressData(ArrayList<String> states){
        int stressCount = Collections.frequency(states, "S");
        int relaxCount = Collections.frequency(states, "R");
        if (stressCount>=relaxCount){
            broadcastUpdate(Constants.ACTION_UPDATE_STRESS_DATA, Color.RED);
            Log.w("====", "S");
        }
        else {
            broadcastUpdate(Constants.ACTION_UPDATE_STRESS_DATA, Color.GREEN);
            Log.w("====", "R");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        broadcastUpdate(Constants. ACTION_UPDATE_STRESS_TIMES, sdf.format(new Date()));
    }

    private IntentFilter getIntentFilters()
    {
        final IntentFilter intentFilter = new IntentFilter(Constants.ACTION_HOURLY_STRESS_DATA);

        return intentFilter;
    }

    private void broadcastUpdate( final String action, int message )
    {
        final Intent intent = new Intent( action );
        intent.putExtra(Constants.COLOUR_VALUE, message);
        sendBroadcast( intent );
    }

    private void broadcastUpdate( final String action, String message )
    {
        final Intent intent = new Intent( action );
        intent.putExtra(Intent.EXTRA_TEXT, message);
        sendBroadcast( intent );
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(dataReceiver);
        super.onDestroy();
    }
}
