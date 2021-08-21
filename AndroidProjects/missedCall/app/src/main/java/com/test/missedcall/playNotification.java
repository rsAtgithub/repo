package com.test.missedcall;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

public class playNotification {
    public static void run(android.content.Context context){
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(context, notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
