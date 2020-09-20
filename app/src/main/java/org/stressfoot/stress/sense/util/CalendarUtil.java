package org.stressfoot.stress.sense.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class CalendarUtil {
    private static ArrayList<String> nameOfEvent = new ArrayList<String>();
    private static ArrayList<String> startDates = new ArrayList<String>();
    private static ArrayList<String> endDates = new ArrayList<String>();
    private static ArrayList<String> descriptions = new ArrayList<String>();

    public static String readCalendarEvent(Context context, long currentEpoch, long timeInterval) {
        Cursor cursor = context.getContentResolver()
                .query(
                        Uri.parse("content://com.android.calendar/events"),
                        new String[]{"calendar_id", "title", "description",
                                "dtstart", "dtend", "eventLocation"}, null,
                        null, null);
        cursor.moveToFirst();
        // fetching calendars name
        String CNames[] = new String[cursor.getCount()];

        // fetching calendars id
        nameOfEvent.clear();
        startDates.clear();
        endDates.clear();
        descriptions.clear();
        for (int i = 0; i < CNames.length; i++) {

            long startEpoch = currentEpoch - timeInterval;
            try {
                if (isCurrentEvent(Long.parseLong(cursor.getString(3)), Long.parseLong(cursor.getString(4)), startEpoch, currentEpoch)) {

                    nameOfEvent.add(cursor.getString(1));
                    startDates.add(getDate(Long.parseLong(cursor.getString(3))));
                    endDates.add(getDate(Long.parseLong(cursor.getString(4))));
                    descriptions.add(cursor.getString(2));
                }
            }
            catch (Exception e){}
            CNames[i] = cursor.getString(1);
            cursor.moveToNext();

        }
        cursor.close();
        if (nameOfEvent.isEmpty()){
            return "free";
        }
        return (TextUtils.join("/", nameOfEvent));
    }

    private static String getDate(long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "dd/MM/yyyy hh:mm:ss a");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    private static boolean isCurrentEvent(long startTime, long endTime, long startEpoch, long currentEpoch){
        return ((currentEpoch > startTime && currentEpoch <= endTime) || (startEpoch >= startTime && startEpoch < endTime) || (startEpoch < startTime && currentEpoch > endTime));
    }
}