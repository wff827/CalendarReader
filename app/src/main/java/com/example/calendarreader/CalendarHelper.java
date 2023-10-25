package com.example.calendarreader;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class CalendarHelper {
    @SuppressLint("Range")
    public static JSONArray getFirstNEventsAsJson(Context context, int n) {
        JSONArray eventsArray = new JSONArray();

        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = CalendarContract.Events.CONTENT_URI;
        String[] projection = {
                CalendarContract.Events._ID,
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DESCRIPTION,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.DTEND
        };

        // Sort by start time in ascending order
        String sortOrder = CalendarContract.Events.DTSTART + " ASC";

        Cursor cursor = contentResolver.query(uri, projection, null, null, sortOrder);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                JSONObject eventObject = new JSONObject();

                try {
                    eventObject.put("id", cursor.getLong(cursor.getColumnIndex(CalendarContract.Events._ID)));
                    eventObject.put("title", cursor.getString(cursor.getColumnIndex(CalendarContract.Events.TITLE)));
                    eventObject.put("description", cursor.getString(cursor.getColumnIndex(CalendarContract.Events.DESCRIPTION)));
                    eventObject.put("start_time", cursor.getLong(cursor.getColumnIndex(CalendarContract.Events.DTSTART)));
                    eventObject.put("end_time", cursor.getLong(cursor.getColumnIndex(CalendarContract.Events.DTEND)));

                    eventsArray.put(eventObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            cursor.close();
        }
        // If less than 'n' events, add mock data
        while (eventsArray.length() < n) {
            try {
                JSONObject mockEvent = new JSONObject();
                mockEvent.put("id", 0);
                mockEvent.put("title", "Mock Event");
                mockEvent.put("description", "This is a mock event description.");
                mockEvent.put("start_time", System.currentTimeMillis());
                mockEvent.put("end_time", System.currentTimeMillis() + 3600000); // +1 hour

                eventsArray.put(mockEvent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return eventsArray;
    }

    public static String getFirstEventAsJsonString(Context context) {
        JSONArray eventsArray = getFirstNEventsAsJson(context, 1);
        if (eventsArray.length() == 0) {
            return "{}"; // No events found
        }

        try {
            JSONObject firstEvent = eventsArray.getJSONObject(0);
            JSONObject simplifiedEvent = new JSONObject();

            simplifiedEvent.put("标题", firstEvent.getString("title"));
            simplifiedEvent.put("事件描述", firstEvent.getString("description"));
            simplifiedEvent.put("开始时间", firstEvent.getLong("startTime"));
            simplifiedEvent.put("结束时间", firstEvent.getLong("endTime"));

            return simplifiedEvent.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static long addEventToCalendar(Context context, String title, String description, long startTime, long endTime, String location) {
        ContentResolver contentResolver = context.getContentResolver();

        ContentValues contentValues = new ContentValues();
        contentValues.put(CalendarContract.Events.CALENDAR_ID, 1); // 注意: 在大多数Android设备上, 主日历的ID通常是1，但是最好动态检索此ID
        contentValues.put(CalendarContract.Events.TITLE, title);
        contentValues.put(CalendarContract.Events.DESCRIPTION, description);
        contentValues.put(CalendarContract.Events.DTSTART, startTime);
        contentValues.put(CalendarContract.Events.DTEND, endTime);
        contentValues.put(CalendarContract.Events.EVENT_TIMEZONE, "UTC"); // 修改这里以适应其他时区
        contentValues.put(CalendarContract.Events.EVENT_LOCATION, location);

        Uri uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, contentValues);

        // 返回新创建的事件的ID。如果插入失败，返回-1
        long eventId = -1;
        if (uri != null) {
            eventId = Long.parseLong(uri.getLastPathSegment());
        }

        return eventId;
    }
}

