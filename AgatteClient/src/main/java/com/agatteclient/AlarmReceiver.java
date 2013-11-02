package com.agatteclient;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.os.ResultReceiver;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Broadcast Receiver that is fired when an alarm expire.
 *
 * Created by Rémi Pannequin on 01/11/13.
 */
public class AlarmReceiver extends BroadcastReceiver {

    private class PunchResultReceiver extends ResultReceiver {

        public PunchResultReceiver() {
            super(new Handler(Looper.getMainLooper()));
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            //TODO: display notification based on result
            AgatteResponse rsp = AgatteResponse.fromBundle(resultData);

        }
    }


    //Manage a collection of alarm
    Map<PunchAlarmTime, PendingIntent> alarms;

    public AlarmReceiver() {
        this.alarms = new HashMap<PunchAlarmTime, PendingIntent>();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();

        final Intent i = new Intent(context, PunchService.class);
        i.setAction(PunchService.DO_PUNCH);
        i.putExtra(PunchService.RESULT_RECEIVER, new PunchResultReceiver());
        context.startService(i);

        wl.release();

    }

    public void AddAlarm(Context context) {
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        //TODO: add to the collection of alarms
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * 10, pi); // Millisec * Second * Minute

    }

    public Collection<Date> getAlarms() {
        //TODO


        return null;
    }

    public void CancelAlarm(Context context)
    {
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }



}
