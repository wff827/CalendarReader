package com.example.calendarreader;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CALENDAR_PERMISSION = 100;
    private static final int REQUEST_SMS_PERMISSION = 101;

    private TextView eventsTextView;
    private TextView messagesTextView;

    private EditText clipboardInputEditText;
    private TextView clipboardTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clipboardTextView = findViewById(R.id.clipboardTextView);
        displayClipboardContent();
        eventsTextView = findViewById(R.id.eventsTextView);
        messagesTextView = findViewById(R.id.messagesTextView);

        requestCalendarPermission();
        requestSmsPermission();
    }

    private void requestCalendarPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALENDAR}, REQUEST_CALENDAR_PERMISSION);
        } else {
            displayEvents();
        }
    }

    private void requestSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, REQUEST_SMS_PERMISSION);
        } else {
            displayMessages();
        }
    }

    private void displayEvents() {
        JSONArray events = CalendarHelper.getFirstNEventsAsJson(this, 5);
        StringBuilder displayText = new StringBuilder();
        for (int i = 0; i < events.length(); i++) {
            try {
                JSONObject event = events.getJSONObject(i);
                displayText.append(event.getString("title")).append("\n");
                displayText.append(event.getString("description")).append("\n\n");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        eventsTextView.setText(displayText.toString());
    }

    private void displayMessages() {
        JSONArray messages = MessageHelper.getFirstNMessagesAsJson(this, 5);
        StringBuilder displayText = new StringBuilder();
        for (int i = 0; i < messages.length(); i++) {
            try {
                JSONObject message = messages.getJSONObject(i);
                displayText.append(message.getString("address")).append("\n");
                displayText.append(message.getString("body")).append("\n\n");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        messagesTextView.setText(displayText.toString());
    }

    private void displayClipboardContent() {
        String clipboardContent = ClipboardHelper.getClipboardContent(this);
        if (clipboardContent != null && !clipboardContent.trim().isEmpty()) {
            clipboardTextView.setText(clipboardContent);
        } else {
            clipboardTextView.setText("Clipboard is empty.");
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        switch (requestCode) {
            case REQUEST_CALENDAR_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    displayEvents();
                } else {
                    eventsTextView.setText(R.string.permission_denied);
                }
                break;
            case REQUEST_SMS_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    displayMessages();
                } else {
                    messagesTextView.setText(R.string.permission_denied);
                }
                break;
            default:
                break;
        }
    }
}
