package org.stressfoot.stress.sense.service;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;

import org.stressfoot.stress.sense.util.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/*************NOT IN USE*************/
public class TestDataReadService extends Service {
    File file;
    ArrayList<String> statesList = new ArrayList<>();
    Handler handler;
    int hourLBound;
    int hourUBound;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        handler = new Handler();
        File sdcard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        file = new File(sdcard, "stress.txt");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        readData();
        return super.onStartCommand(intent, flags, startId);
    }

    private void readData(){
        hourLBound = 0;
        hourUBound = 36000;

        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                int i = 0;
                try {
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String line;

                    while ((line = br.readLine()) != null) {
                        if (i >= hourLBound && i < hourUBound) {
                            statesList.add(line);
                        }
                        i++;
                    }
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (hourUBound <= i) {
                    broadcastUpdate(Constants.ACTION_HOURLY_STRESS_DATA, new ArrayList<String>(statesList));
                }
                statesList.clear();
                hourLBound += 36000;
                hourUBound += 36000;
                handler.postDelayed(this, 30000);
            }
        }, 30000);

        }

    private void broadcastUpdate( final String action, ArrayList<String> message )
    {
        final Intent intent = new Intent( action );
        intent.putStringArrayListExtra(Constants.HOURLY_STRESS, message);
        sendBroadcast( intent );
    }
}
