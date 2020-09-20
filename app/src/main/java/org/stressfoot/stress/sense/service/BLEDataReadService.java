package org.stressfoot.stress.sense.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.stressfoot.stress.sense.MainActivity;
import org.stressfoot.stress.sense.R;
import org.stressfoot.stress.sense.tasks.GETRequestHandler;
import org.stressfoot.stress.sense.tasks.POSTRequestHandler;
import org.stressfoot.stress.sense.util.ConsolePreferences;
import org.stressfoot.stress.sense.util.Constants;
import org.stressfoot.stress.sense.util.CalendarUtil;
import org.stressfoot.stress.sense.util.StressSensePreferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class BLEDataReadService extends Service {
    private final String CHANNEL_ID = "StressSense";
    private final String CHANNEL_NAME = "Stress Sense";

    private Handler handler;
    private ArrayList<String> statesList = new ArrayList<>();
    private ArrayList<String> stressList = new ArrayList<>();
    private ArrayList<String> sitStateList = new ArrayList<>();
    private ArrayList<String> sitList = new ArrayList<>();

    private ArrayList<String> timesList = new ArrayList<>();
    private ArrayList<String> eventsList = new ArrayList<>();
    private ArrayList<Float> stressValueList = new ArrayList<>();
    private ArrayList<Float> sitValueList = new ArrayList<>();

    private StressSensePreferences stressSensePreferences;
    private ConsolePreferences consolePreferences;
    private long timeInterval;
    private long sitTimeInterval;

    private NotificationManager manager;
    private PendingIntent contentIntent;

    private RequestQueue requestQueue;

    private final String TAG = BLEDataReadService.class.getSimpleName();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        handler = new Handler();

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registerReceiver(sensorDataReceiver, getIntentFilters());
        consolePreferences = ConsolePreferences.getInstance(getApplicationContext());
        stressSensePreferences = StressSensePreferences.getInstance(getApplicationContext());
        timeInterval = stressSensePreferences.getTimeInterval() * 5*60*1000;
        sitTimeInterval = stressSensePreferences.getSitTimeInterval() * 5*60*1000;

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        String currentTime = sdf.format(new Date());
        timesList.add(currentTime);

        broadcastHourlyStress();
        checkStressIntervention();
        checkSittingIntervention();

        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = createNotification("Stress Sense is running", NotificationManager.IMPORTANCE_DEFAULT, NotificationCompat.PRIORITY_DEFAULT);
        startForeground(1, notification);

        return START_STICKY;
        //return super.onStartCommand(intent, flags, startId);
    }

    private final BroadcastReceiver sensorDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(Constants.ACTION_UPDATE_ACCELEROMETER_READING)){
                double reading = intent.getDoubleExtra(Constants.ACCELEROMETER_AVG, 0.0);
                Log.i("accelerometer", reading + "");
                GETRequestHandler requestHandler = new GETRequestHandler(getApplicationContext(), Double.toString(reading), getRequestQueue());
                requestHandler.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            else if (action.equals(Constants.ACTION_RECEIVED_RESPONSE)){
                String response = intent.getStringExtra(Intent.EXTRA_TEXT);
                Log.i("stress_value", response);
                statesList.add(response);
                stressList.add(response);
            }
            else if (action.equals(Constants.ACTION_UPDATE_SITTING_STATE)){
                String state = intent.getStringExtra(Intent.EXTRA_TEXT);
                Log.i("POSTURE STATUS", state);
                sitStateList.add(state);
                sitList.add(state);
            }
            else if (action.equals(Constants.ACTION_ALERT_USER)){
                Notification notification = createNotification("Accelerometer disconnected", NotificationManager.IMPORTANCE_HIGH, NotificationCompat.PRIORITY_HIGH);
                manager.notify(789, notification);
            }
        }
    };

    private void broadcastHourlyStress(){
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                handlePostRequest(false);

                handler.postDelayed(this, 3600*1000);
            }
        }, 3600*1000);
    }

    private void handlePostRequest(boolean backupEnabled){

        float stressRatio = getStressRatio(new ArrayList<String>(statesList));
        handleStressData(stressRatio);
        statesList.clear();

        float sitRatio = getSitRatio(new ArrayList<String>(sitStateList));
        handleSitData(sitRatio);
        sitStateList.clear();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        String currentTime = sdf.format(new Date());
        timesList.add(currentTime);

        eventsList.add(CalendarUtil.readCalendarEvent(getApplicationContext(), System.currentTimeMillis(), 3600*1000));

        String times = TextUtils.join(",", timesList);
        String stress_values =  TextUtils.join(",", stressValueList);
        String sitValues = TextUtils.join(",", sitValueList);
        String events = TextUtils.join(",", eventsList);

        Log.e("**********calendar", events);

        POSTRequestHandler requestHandler = new POSTRequestHandler(getApplicationContext(), times, sitValues, stress_values, events, backupEnabled, getRequestQueue());
        requestHandler.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void checkStressIntervention(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                float ratio = getStressRatio(new ArrayList<String>(stressList));
                stressList.clear();
                if (checkStressTrigger(ratio)){
                    handleSpotifyEvent(consolePreferences.getMusicState());
                    handleDoNotDisturb(consolePreferences.getStressState(), consolePreferences.getNotificationState(), NotificationManager.INTERRUPTION_FILTER_PRIORITY);
                    handleReminderMessage(consolePreferences.getReminderState(), stressSensePreferences.getReminderMessage(), 123);
                    handleIftttIntervention(consolePreferences.getIftttState());
                }
                else handleDoNotDisturb(consolePreferences.getStressState(), consolePreferences.getNotificationState(), NotificationManager.INTERRUPTION_FILTER_ALL);

                timeInterval = stressSensePreferences.getTimeInterval()* 5*60*1000;

                handler.postDelayed(this, timeInterval);
            }
        }, timeInterval);
    }

    private void checkSittingIntervention(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                float ratio = getSitRatio(new ArrayList<String>(sitList));
                sitList.clear();
                if (checkSitTrigger(ratio)){
                    handleSpotifyEvent(consolePreferences.getSitMusicState());
                    handleDoNotDisturb(consolePreferences.getPostureState(), consolePreferences.getSitNotificationState(), NotificationManager.INTERRUPTION_FILTER_PRIORITY);
                    handleReminderMessage(consolePreferences.getSitReminderState(), stressSensePreferences.getSitReminderMessage(), 567);
                    handleIftttIntervention(consolePreferences.getSitIftttState());
                }
                else handleDoNotDisturb(consolePreferences.getPostureState(), consolePreferences.getSitNotificationState(), NotificationManager.INTERRUPTION_FILTER_ALL);

                sitTimeInterval = stressSensePreferences.getSitTimeInterval() * 5*60*1000;

                handler.postDelayed(this, sitTimeInterval);
            }
        }, sitTimeInterval);
    }

    private float getStressRatio(ArrayList<String> states){
        if (states.isEmpty()) return 0f;
        int stressCount = Collections.frequency(states, "[\"S\"]");
        float ratio = (stressCount / (states.size() *1f)) * 100f;
        Log.w("array", states.toString());
        return ratio;
    }

    private void handleStressData(float ratio){
        if (consolePreferences.getStressState()){
            stressValueList.add(ratio);
            Log.w("====", "S "+ratio);
        }
        else {
            stressValueList.add(0f);
        }
    }

    private boolean checkStressTrigger(float ratio){
        if (!consolePreferences.getStressState()){
            return false;
        }
        String param = consolePreferences.getStressParam();
        float stressValue = Float.parseFloat(consolePreferences.getStressLevel().substring(0,2)); //remove "%" sign
        if (param.equals(">=")){
            return ratio >= stressValue;
        }
        return ratio <= stressValue;
    }

    private float getSitRatio(ArrayList<String> states){
        if (states.isEmpty()) return 0f;
        int stressCount = Collections.frequency(states, "sit");
        float ratio = (stressCount / (states.size() *1f)) * 100f;
        Log.w("array", states.toString() + "    " + ratio);
        return ratio;
    }

    private void handleSitData(float ratio){
        if (consolePreferences.getPostureState()){
            sitValueList.add(ratio);
        }
        else {
            sitValueList.add(0f);
        }
    }

    private boolean checkSitTrigger(float ratio){
        if (!consolePreferences.getPostureState()){
            return false;
        }
        String param = consolePreferences.getPostureParam();
        float sitValue = Float.parseFloat(consolePreferences.getPostureLevel().substring(0,2)); //remove "%" sign
        if (param.equals(">=")){
            return ratio >= sitValue;
        }
        return ratio <= sitValue;
    }

    private void handleSpotifyEvent(boolean musicState)
    {
        if (musicState) {
            wakeScreen();

            Intent playSpotify = new Intent("com.spotify.mobile.android.ui.widget.NEXT");
            playSpotify.setPackage("com.spotify.music");
            sendBroadcast(playSpotify);
        }
    }

    private void handleDoNotDisturb(boolean modeState, boolean notifState, int priorityFilter){
        if (modeState && notifState){
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.setInterruptionFilter(priorityFilter);
        }
    }

    private void handleReminderMessage(boolean remindState, String message, int id){
        if (remindState){
            Notification notification = createNotification(message, NotificationManager.IMPORTANCE_HIGH, NotificationCompat.PRIORITY_HIGH);
            wakeScreen();
            manager.notify(id, notification);
        }
    }

    private void handleIftttIntervention(boolean iftttState){
        if (iftttState){
            Notification notification = createNotification("IFTTT Intervention Triggered", NotificationManager.IMPORTANCE_LOW, NotificationCompat.PRIORITY_LOW);
            manager.cancel(345);
            manager.notify(345, notification);
        }
    }

    private Notification createNotification(String message, int importance, int priority){
        Notification notification;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel chan = new NotificationChannel(CHANNEL_ID+importance, CHANNEL_NAME, importance);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            assert manager != null;
            manager.createNotificationChannel(chan);

            Notification.Builder builder = new Notification.Builder(this, CHANNEL_ID+importance)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(message)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true);

            notification = builder.build();
        } else {

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(message)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(contentIntent)
                    .setPriority(priority)
                    .setAutoCancel(true);

            notification = builder.build();
        }
        return notification;
    }

    private void wakeScreen()
    {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isInteractive();
        if (!isScreenOn){
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            PowerManager.WakeLock  wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP |
                    PowerManager.ON_AFTER_RELEASE, "appname::WakeLock");

            wakeLock.acquire();
            wakeLock.release();
        }
    }

    private RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return requestQueue;
    }

    private IntentFilter getIntentFilters()
    {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_RECEIVED_RESPONSE);
        intentFilter.addAction(Constants.ACTION_UPDATE_ACCELEROMETER_READING);
        intentFilter.addAction(Constants.ACTION_UPDATE_SITTING_STATE);
        intentFilter.addAction(Constants.ACTION_ALERT_USER);

        return intentFilter;
    }

    private void broadcastUpdate( final String action, ArrayList<String> message )
    {
        final Intent intent = new Intent( action );
        intent.putStringArrayListExtra(Constants.HOURLY_STRESS, message);
        sendBroadcast( intent );
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(sensorDataReceiver);
        handler.removeCallbacksAndMessages(null);
        handlePostRequest(true);
        super.onDestroy();
    }
}
