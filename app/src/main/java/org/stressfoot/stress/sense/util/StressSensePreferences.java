package org.stressfoot.stress.sense.util;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class StressSensePreferences {
    private static StressSensePreferences instance;
    private Context context;
    private final SharedPreferences.Editor editor;

    public StressSensePreferences(Context context) {
        this.context = context;
        editor = context.getSharedPreferences("StressSense", MODE_PRIVATE).edit();
    }

    public static StressSensePreferences getInstance(Context context){
        if (instance == null){
            instance = new StressSensePreferences(context);
        }
        return instance;
    }

    public void setIpAddress(String ipAddress){
        editor.putString(Constants.PREF_KEY_IP, ipAddress);
        editor.commit();
    }

    public void setMacAddress(String macAddress){
        editor.putString(Constants.PREF_KEY_MAC, macAddress);
        editor.commit();
    }

    public void setTimeInterval(int time){
        editor.putInt(Constants.PREF_TIME_INTERVAL, time);
        editor.commit();
    }

    public void setSitTimeInterval(int time){
        editor.putInt(Constants.PREF_SIT_TIME_INTERVAL, time);
        editor.commit();
    }

    public void setReminderMessage(String message){
        editor.putString(Constants.PREF_REMINDER_MSG,message);
        editor.commit();
    }

    public void setSitReminderMessage(String message){
        editor.putString(Constants.PREF_SIT_REMINDER_MSG,message);
        editor.commit();
    }

    public void setIftttHelp(boolean flag){
        editor.putBoolean(Constants.PREF_IFTTT_HELP, flag);
        editor.commit();
    }

    public String getIpAddress(){
        SharedPreferences prefs = context.getSharedPreferences("StressSense", MODE_PRIVATE);
        String ipAddress =  prefs.getString(Constants.PREF_KEY_IP, Constants.IP_ADDRESS);
        return ipAddress;
    }

    public String getMacAddress(){
        SharedPreferences prefs = context.getSharedPreferences("StressSense", MODE_PRIVATE);
        String macAddress = prefs.getString(Constants.PREF_KEY_MAC, Constants.MW_MAC_ADDRESS);
        return macAddress;
    }

    public int getTimeInterval(){
        SharedPreferences prefs = context.getSharedPreferences("StressSense", MODE_PRIVATE);
        int time =  prefs.getInt(Constants.PREF_TIME_INTERVAL, 1);
        return time;
    }

    public int getSitTimeInterval(){
        SharedPreferences prefs = context.getSharedPreferences("StressSense", MODE_PRIVATE);
        int time =  prefs.getInt(Constants.PREF_SIT_TIME_INTERVAL, 1);
        return time;
    }

    public String getReminderMessage(){
        SharedPreferences prefs = context.getSharedPreferences("StressSense", MODE_PRIVATE);
        String message = prefs.getString(Constants.PREF_REMINDER_MSG, "");
        return message;
    }

    public String getSitReminderMessage(){
        SharedPreferences prefs = context.getSharedPreferences("StressSense", MODE_PRIVATE);
        String message = prefs.getString(Constants.PREF_SIT_REMINDER_MSG, "");
        return message;
    }

    public boolean getIftttHelp(){
        SharedPreferences prefs = context.getSharedPreferences("StressSense", MODE_PRIVATE);
        boolean state =  prefs.getBoolean(Constants.PREF_IFTTT_HELP, true);
        return state;
    }

    public void clearPreferences() {
        editor.clear().commit();
    }
}