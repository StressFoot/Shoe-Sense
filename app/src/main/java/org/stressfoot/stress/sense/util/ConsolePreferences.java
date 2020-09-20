package org.stressfoot.stress.sense.util;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class ConsolePreferences {
    private static ConsolePreferences instance;
    private Context context;
    private final SharedPreferences.Editor editor;

    public ConsolePreferences(Context context) {
        this.context = context;
        editor = context.getSharedPreferences("Console", MODE_PRIVATE).edit();
    }

    public static ConsolePreferences getInstance(Context context){
        if (instance == null){
            instance = new ConsolePreferences(context);
        }
        return instance;
    }

    public void setNotificationState(boolean flag){
        editor.putBoolean(Constants.PREF_NOTIF_STATE, flag);
        editor.commit();
    }

    public void setSitNotificationState(boolean flag){
        editor.putBoolean(Constants.PREF_SIT_NOTIF_STATE, flag);
        editor.commit();
    }

    public void setMusicState(boolean flag){
        editor.putBoolean(Constants.PREF_MUSIC_STATE, flag);
        editor.commit();
    }

    public void setSitMusicState(boolean flag){
        editor.putBoolean(Constants.PREF_SIT_MUSIC_STATE, flag);
        editor.commit();
    }

    public void setIftttState(boolean flag){
        editor.putBoolean(Constants.PREF_IFTTT_STATE, flag);
        editor.commit();
    }

    public void setSitIftttState(boolean flag){
        editor.putBoolean(Constants.PREF_SIT_IFTTT_STATE, flag);
        editor.commit();
    }

    public void setReminderState(boolean flag){
        editor.putBoolean(Constants.PREF_REMIND_STATE, flag);
        editor.commit();
    }

    public void setSitReminderState(boolean flag){
        editor.putBoolean(Constants.PREF_SIT_REMIND_STATE, flag);
        editor.commit();
    }

    public void setStressLevel(String level){
        editor.putString(Constants.PREF_STRESS_LVL, level);
        editor.commit();
    }

    public void setStressParam(String param){
        editor.putString(Constants.PREF_STRESS_PARAM, param);
        editor.commit();
    }

    public void setPostureLevel(String level){
        editor.putString(Constants.PREF_POSTURE_LVL, level);
        editor.commit();
    }

    public void setPostureParam(String param){
        editor.putString(Constants.PREF_POSTURE_PARAM, param);
        editor.commit();
    }

    public void setButtonState(boolean flag){
        editor.putBoolean(Constants.PREF_BUTTON_STATE, flag);
        editor.commit();
    }

    public void setStressState(boolean flag){
        editor.putBoolean(Constants.PREF_STRESS_STATE, flag);
        editor.commit();
    }

    public void setPostureState(boolean flag){
        editor.putBoolean(Constants.PREF_POS_STATE, flag);
        editor.commit();
    }

    public void setSessionState(boolean flag){
        editor.putBoolean(Constants.PREF_SESSION_STATE, flag);
        editor.commit();
    }

    public boolean getNotificationState(){
        SharedPreferences prefs = context.getSharedPreferences("Console", MODE_PRIVATE);
            boolean state =  prefs.getBoolean(Constants.PREF_NOTIF_STATE, false);
        return state;
    }

    public boolean getSitNotificationState(){
        SharedPreferences prefs = context.getSharedPreferences("Console", MODE_PRIVATE);
        boolean state =  prefs.getBoolean(Constants.PREF_SIT_NOTIF_STATE, false);
        return state;
    }

    public boolean getMusicState(){
        SharedPreferences prefs = context.getSharedPreferences("Console", MODE_PRIVATE);
            boolean state =  prefs.getBoolean(Constants.PREF_MUSIC_STATE, false);
        return state;
    }

    public boolean getSitMusicState(){
        SharedPreferences prefs = context.getSharedPreferences("Console", MODE_PRIVATE);
        boolean state =  prefs.getBoolean(Constants.PREF_SIT_MUSIC_STATE, false);
        return state;
    }

    public boolean getIftttState(){
        SharedPreferences prefs = context.getSharedPreferences("Console", MODE_PRIVATE);
        boolean state =  prefs.getBoolean(Constants.PREF_IFTTT_STATE, false);
        return state;
    }

    public boolean getSitIftttState(){
        SharedPreferences prefs = context.getSharedPreferences("Console", MODE_PRIVATE);
        boolean state =  prefs.getBoolean(Constants.PREF_SIT_IFTTT_STATE, false);
        return state;
    }

    public boolean getReminderState(){
        SharedPreferences prefs = context.getSharedPreferences("Console", MODE_PRIVATE);
        boolean state =  prefs.getBoolean(Constants.PREF_REMIND_STATE, false);
        return state;
    }

    public boolean getSitReminderState(){
        SharedPreferences prefs = context.getSharedPreferences("Console", MODE_PRIVATE);
        boolean state =  prefs.getBoolean(Constants.PREF_SIT_REMIND_STATE, false);
        return state;
    }

    public String getStressParam(){
        SharedPreferences prefs = context.getSharedPreferences("Console", MODE_PRIVATE);
        String param =  prefs.getString(Constants.PREF_STRESS_PARAM, ">=");
        return param;
    }

    public String getStressLevel(){
        SharedPreferences prefs = context.getSharedPreferences("Console", MODE_PRIVATE);
        String level =  prefs.getString(Constants.PREF_STRESS_LVL, "10%");
        return level;
    }

    public String getPostureParam(){
        SharedPreferences prefs = context.getSharedPreferences("Console", MODE_PRIVATE);
        String param =  prefs.getString(Constants.PREF_POSTURE_PARAM, ">=");
        return param;
    }

    public String getPostureLevel(){
        SharedPreferences prefs = context.getSharedPreferences("Console", MODE_PRIVATE);
        String level =  prefs.getString(Constants.PREF_POSTURE_LVL, "10%");
        return level;
    }

    public boolean getButtonState(){
        SharedPreferences prefs = context.getSharedPreferences("Console", MODE_PRIVATE);
        boolean state =  prefs.getBoolean(Constants.PREF_BUTTON_STATE, false);
        return state;
    }

    public boolean getStressState(){
        SharedPreferences prefs = context.getSharedPreferences("Console", MODE_PRIVATE);
        boolean state =  prefs.getBoolean(Constants.PREF_STRESS_STATE, false);
        return state;
    }

    public boolean getPostureState(){
        SharedPreferences prefs = context.getSharedPreferences("Console", MODE_PRIVATE);
        boolean state =  prefs.getBoolean(Constants.PREF_POS_STATE, false);
        return state;
    }

    public boolean getSessionState(){
        SharedPreferences prefs = context.getSharedPreferences("Console", MODE_PRIVATE);
        boolean state =  prefs.getBoolean(Constants.PREF_SESSION_STATE, false);
        return state;
    }

    public void clearPreferences() {
        editor.clear().commit();
    }
}