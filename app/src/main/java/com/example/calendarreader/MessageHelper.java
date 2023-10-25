package com.example.calendarreader;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MessageHelper {

    public static JSONArray getFirstNMessagesAsJson(Context context, int n) {
        JSONArray messagesArray = new JSONArray();

        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = Telephony.Sms.CONTENT_URI;
        String[] projection = {
                Telephony.Sms._ID,
                Telephony.Sms.ADDRESS,
                Telephony.Sms.BODY,
                Telephony.Sms.DATE,
                Telephony.Sms.TYPE
        };

        // Sort by date in descending order
        String sortOrder = Telephony.Sms.DATE + " DESC";

        Cursor cursor = contentResolver.query(uri, projection, null, null, sortOrder);

        if (cursor != null) {
            int count = 0;
            while (cursor.moveToNext() && count < n) {
                JSONObject messageObject = new JSONObject();

                try {
                    messageObject.put("id", cursor.getLong(cursor.getColumnIndex(Telephony.Sms._ID)));
                    messageObject.put("address", cursor.getString(cursor.getColumnIndex(Telephony.Sms.ADDRESS)));
                    messageObject.put("body", cursor.getString(cursor.getColumnIndex(Telephony.Sms.BODY)));
                    messageObject.put("date", cursor.getLong(cursor.getColumnIndex(Telephony.Sms.DATE)));
                    messageObject.put("type", cursor.getInt(cursor.getColumnIndex(Telephony.Sms.TYPE))); // 1 for received, 2 for sent

                    messagesArray.put(messageObject);
                    count++;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            cursor.close();
        }

        return messagesArray;
    }
    public static String getFirstMessageAsJsonString(Context context) {
        JSONArray messageArray = getFirstNMessagesAsJson(context, 1);
        if (messageArray.length() == 0) {
            return "{}"; // No events found
        }

        try {
            JSONObject firstMessage = messageArray.getJSONObject(0);
            JSONObject simplifiedEvent = new JSONObject();

            simplifiedEvent.put("内容", firstMessage.getString("body"));
            simplifiedEvent.put("时间", firstMessage.getLong("Date"));
            if(firstMessage.getInt("type") == 1){
                simplifiedEvent.put("接受地址", firstMessage.getLong("address"));
            }
            else{
                simplifiedEvent.put("发送地址", firstMessage.getLong("address"));
            }
            return simplifiedEvent.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
