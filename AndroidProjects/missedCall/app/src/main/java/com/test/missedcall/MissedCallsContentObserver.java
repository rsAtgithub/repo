package com.test.missedcall;

import android.database.ContentObserver;
import android.database.Cursor;
import android.media.AudioManager;
import android.provider.CallLog;
import android.util.Log;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MissedCallsContentObserver extends ContentObserver{

    private  android.content.ContentResolver cr;
    public MissedCallsContentObserver(android.content.ContentResolver contResolver)
    {
        super(null);
        cr = contResolver;
    }

    @Override
    public void onChange(boolean selfChange)
    {
        Cursor cursor = cr.query(
                CallLog.Calls.CONTENT_URI,
                null,
                CallLog.Calls.TYPE +  " = ? AND " + CallLog.Calls.NEW + " = ?",
                new String[] { Integer.toString(CallLog.Calls.MISSED_TYPE), "1" },
                CallLog.Calls.DATE + " DESC ");

        //this is the number of missed calls
        //for your case you may need to track this number
        //so that you can figure out when it changes
        int missedCalls = cursor.getCount();
        Log.i(MainActivity.debugTag, "No of missed calls found: " + String.valueOf(missedCalls));

        int ind=cursor.getColumnIndex(android.provider.CallLog.Calls.NUMBER);
        int date = cursor.getColumnIndex(CallLog.Calls.DATE);
        //String m = cursor.getString(ind);
        //MainActivity.missedCallList.clear();
        boolean modeChanged = false;
        while (cursor.moveToNext()){
            String phNumber = cursor.getString(ind);
            Date callDayTime = new Date(Long.valueOf(cursor.getString(date)));
            Log.i(MainActivity.debugTag, "NUmber:" + phNumber +  " date: " + callDayTime);
            //MainActivity.missedCallList.add(phNumber);
            int oldRingerMode = MainActivity.am.getRingerMode();
            if (oldRingerMode == AudioManager.RINGER_MODE_NORMAL) {
                // We don't want to continue if ringer is already NORMAL.
                break;
            }
            for (int i = 0; i < MainActivity.phoneTrack.length; i++){
                if(!MainActivity.phoneTrack[i].equals("")) {
                    if (phNumber.contains(MainActivity.phoneTrack[i])) {
                        Log.i(MainActivity.debugTag, "Entry: " + i + " is found in " + phNumber);
                        Log.i(MainActivity.debugTag, "Setting Ringer mode NORMAL");
                        MainActivity.am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                        TimerTask timerExipireEvent = new TimerTask() {
                            @Override
                            public void run() {
                                MainActivity.am.setRingerMode(oldRingerMode);
                                Log.i(MainActivity.debugTag,"Reverting old ringer mode");
                            }
                        };
                        Log.i(MainActivity.debugTag, "Timer started for: " + MainActivity.timeToRevert + " ms");
                        new Timer().schedule(timerExipireEvent, MainActivity.timeToRevert);
                        modeChanged = true;
                        break;
                    }
                }
            }
            if (modeChanged){
                Log.i(MainActivity.debugTag, "Mode was already changed, skipping further scanning");
                break;
            }
        }

        //myToast.tMessage(this.getC, cursor.getString(ind));

        cursor.close();
    }
}
