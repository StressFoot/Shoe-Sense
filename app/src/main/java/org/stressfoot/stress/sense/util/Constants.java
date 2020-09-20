package org.stressfoot.stress.sense.util;

import android.os.Environment;

public class Constants {
    public static final String MW_MAC_ADDRESS = "F2:CC:13:83:AB:A9";
    public static final String IP_ADDRESS = "stressfootmodelv2.herokuapp.com";

    public static final String IMAGE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/" + "StressShoe_temp.png";
    public static final String SAVED_IMAGE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/" + "StressShoe_Saved_" + System.currentTimeMillis() + ".png";
    public static final String IMAGE_PATH_BASE = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/stressShoe/";
    public static final String DATA_PATH_BASE = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/stressShoe/";


    public static final String[] levelsList = new String[]{"10%", "20%", "30%", "40%", "50%", "60%", "70%", "80%", "90%"};
    public static final String[] conditionList = new String[]{">=", "<="};

    public static final String PREF_KEY_IP = "PREF_KEY_IP";
    public static final String PREF_KEY_MAC = "PREF_KEY_MAC";

    public static final String PREF_NOTIF_STATE = "PREF_NOTIF_STATE";
    public static final String PREF_MUSIC_STATE = "PREF_MUSIC_STATE";
    public static final String PREF_IFTTT_STATE = "PREF_IFTTT_STATE";
    public static final String PREF_REMIND_STATE = "PREF_REMIND_STATE";

    public static final String PREF_SIT_NOTIF_STATE = "PREF_SIT_NOTIF_STATE";
    public static final String PREF_SIT_MUSIC_STATE = "PREF_SIT_MUSIC_STATE";
    public static final String PREF_SIT_IFTTT_STATE = "PREF_SIT_IFTTT_STATE";
    public static final String PREF_SIT_REMIND_STATE = "PREF_SIT_REMIND_STATE";

    public static final String PREF_STRESS_LVL = "PREF_STRESS_LVL";
    public static final String PREF_STRESS_PARAM = "PREF_STRESS_PARAM";
    public static final String PREF_POSTURE_LVL = "PREF_POSTURE_LVL";
    public static final String PREF_POSTURE_PARAM = "PREF_POSTURE_PARAM";
    public static final String PREF_BUTTON_STATE = "PREF_BUTTON_STATE";
    public static final String PREF_STRESS_STATE = "PREF_STRESS_STATE";
    public static final String PREF_POS_STATE = "PREF_POS_STATE";
    public static final String PREF_SESSION_STATE = "PREF_SESSION_STATE";
    public static final String PREF_TIME_INTERVAL = "PREF_TIME_INTERVAL";
    public static final String PREF_SIT_TIME_INTERVAL = "PREF_SIT_TIME_INTERVAL";
    public static final String PREF_REMINDER_MSG = "PREF_REMINDER_MSG";
    public static final String PREF_SIT_REMINDER_MSG = "PREF_SIT_REMINDER_MSG";
    public static final String PREF_IFTTT_HELP = "PREF_IFTTT_HELP";

    public static final String HOURLY_STRESS = "HOURLY_STRESS";
    public static final String COLOUR_VALUE = "COLOUR_VALUE";
    public static final String ACCELEROMETER_AVG = "ACCELEROMETER_AVG";

    public static final String ACTION_BLE_CONNECTED = "org.ahlab.stress.sense.ACTION_BLE_CONNECTED";
    public static final String ACTION_BLE_DISCONNECTED = "org.ahlab.stress.sense.ACTION_BLE_DISCONNECTED";
    public static final String ACTION_RECEIVED_RESPONSE = "org.ahlab.stress.sense.ACTION_RECEIVED_RESPONSE";
    public static final String ACTION_HOURLY_STRESS_DATA = "org.ahlab.stress.sense.ACTION_HOURLY_STRESS_DATA";
    public static final String ACTION_UPDATE_STRESS_TIMES = "org.ahlab.stress.sense.ACTION_UPDATE_STRESS_TIMES";
    public static final String ACTION_UPDATE_STRESS_DATA = "ACTION_UPDATE_STRESS_DATA";
    public static final String ACTION_TERMINATE_BROADCASTS = "ACTION_TERMINATE_BROADCASTS";
    public static final String ACTION_START_ACCELEROMETER = "ACTION_START_ACCELEROMETER";
    public static final String ACTION_STOP_ACCELEROMETER = "ACTION_STOP_ACCELEROMETER";
    public static final String ACTION_UPDATE_ACCELEROMETER_READING = "ACTION_UPDATE_ACCELEROMETER_READING";
    public static final String ACTION_UPDATE_SITTING_STATE = "ACTION_UPDATE_SITTING_STATE";
    public static final String ACTION_UPDATE_GRAPH = "ACTION_UPDATE_GRAPH";
    public static final String ACTION_ALERT_USER = "ACTION_ALERT_USER";

    public static final String ACTION_CALIBRATE = "ACTION_CALIBRATE";
    public static final String ACTION_CALIBRATION_COMPLETE = "ACTION_CALIBRATION_COMPLETE";
    public static final String ACTIVE_AXIS = "ACTIVE_AXIS";
    public static final String ACTION_HOURLY_LOG = "ACTION_HOURLY_LOG";
}
