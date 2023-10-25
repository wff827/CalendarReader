package com.example.calendarreader;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;


public class ClipboardHelper {

    /**
     * Get the current text content from the clipboard.
     *
     * @param context the application context.
     * @return the clipboard content as a string, or null if the clipboard is empty or the content is not text.
     */
    public static String getClipboardContent(Context context) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

        if (clipboardManager != null && clipboardManager.hasPrimaryClip()) {
            ClipData.Item item = clipboardManager.getPrimaryClip().getItemAt(0);
            CharSequence charSequence = item.getText();
            return charSequence != null ? charSequence.toString() : null;
        }
        return null;
    }

    /**
     * Set a text content to the clipboard.
     *
     * @param context the application context.
     * @param text the text to be placed into the clipboard.
     */
    public static void setClipboardContent(Context context, String text) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboardManager != null) {
            ClipData clip = ClipData.newPlainText("Clipboard Content", text);
            clipboardManager.setPrimaryClip(clip);
        }
    }
}



